/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.util;

import java.io.File;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import org.apache.commons.math.exception.MathInternalError;

/**
 * Utility class for saving and loading tabulated data used by
 * {@link FastMath}.
 *
 * @version $Id$
 */
class FastMathResources {
    /**
     * Resource directory. Assuming that this class and the resource files
     * are located in the same package as "FastMath".
     */
    private static final String RES_DIR = "data/" +
        FastMath.class.getPackage().getName().replace('.', '/') + "/";
    /** File resource prefix. */
    private static final String RES_PREFIX = RES_DIR + "FastMath__";
    /** Resource basename for "EXP_INT_TABLE_A" and "EXP_INT_TABLE_B". */
    private static final String EXP_INT = "exp_int";
    /** Resource basename for "EXP_FRAC_TABLE_A" and "EXP_FRAC_TABLE_B". */
    private static final String EXP_FRAC = "exp_frac";
    /** Resource basename for "LN_MANT". */
    private static final String LN_MANT = "ln_mant";
    /** Number of bytes in a "double". */
    private static final int BYTES_IN_DOUBLE = Double.SIZE / Byte.SIZE;

    /**
     * Class contains only static methods.
     */
    private FastMathResources() {}

    /**
     * Compute and save all the resources.
     */
    static void createAll() {
        // Create resource directory.
        final File resDir = new File(RES_DIR);
        if (resDir.exists()) {
            if (!resDir.isDirectory()) {
                throw new MathInternalError();
            }
        } else {
            try {
                resDir.mkdirs();
            } catch (SecurityException e) {
                throw new MathInternalError(e);
            }
        }

        // "EXP_INT" tables.
        final double[] expIntA = new double[FastMath.EXP_INT_TABLE_LEN];
        final double[] expIntB = new double[FastMath.EXP_INT_TABLE_LEN];

        final double tmp[] = new double[2];
        final double recip[] = new double[2];

        for (int i = 0; i < FastMath.EXP_INT_TABLE_MAX_INDEX; i++) {
            FastMathCalc.expint(i, tmp);
            expIntA[i + FastMath.EXP_INT_TABLE_MAX_INDEX] = tmp[0];
            expIntB[i + FastMath.EXP_INT_TABLE_MAX_INDEX] = tmp[1];

            if (i != 0) {
                // Negative integer powers.
                FastMathCalc.splitReciprocal(tmp, recip);
                expIntA[FastMath.EXP_INT_TABLE_MAX_INDEX - i] = recip[0];
                expIntB[FastMath.EXP_INT_TABLE_MAX_INDEX - i] = recip[1];
            }
        }

        saveTable2d(EXP_INT, new double[][] { expIntA, expIntB });

        // "EXP_FRAC" tables.
        final double[] expFracA = new double[FastMath.EXP_FRAC_TABLE_LEN];
        final double[] expFracB = new double[FastMath.EXP_FRAC_TABLE_LEN];

        for (int i = 0; i < FastMath.EXP_FRAC_TABLE_LEN; i++) {
            FastMathCalc.slowexp(i / 1024d, tmp); // TWO_POWER_10
            expFracA[i] = tmp[0];
            expFracB[i] = tmp[1];
        }

        saveTable2d(EXP_FRAC, new double[][] { expFracA, expFracB });

        // "LN_MANT" table.
        final double[][] lnMant = new double[FastMath.LN_MANT_LEN][];

        for (int i = 0; i < FastMath.LN_MANT_LEN; i++) {
            final double d = Double.longBitsToDouble((((long) i) << 42) |
                                                     0x3ff0000000000000L);
            lnMant[i] = FastMathCalc.slowLog(d);
        }

        saveTable2d(LN_MANT, transpose(lnMant));
    }

    /**
     * Load "EXP_INT" tables.
     * "EXP_INT_TABLE_A" is at index 0.
     * "EXP_INT_TABLE_B" is at index 1.
     *
     * @return the retrieved data.
     */
    static double[][] loadExpInt() {
        return loadTable2d(EXP_INT, 2, FastMath.EXP_INT_TABLE_LEN);
    }

    /**
     * Load "EXP_FRAC" tables.
     * "EXP_FRAC_TABLE_A" is at index 0.
     * "EXP_FRAC_TABLE_B" is at index 1.
     *
     * @return the retrieved data.
     */
    static double[][] loadExpFrac() {
        return loadTable2d(EXP_FRAC, 2, FastMath.EXP_FRAC_TABLE_LEN);
    }

    /**
     * Load "LN_MANT".
     *
     * @return the retrieved data.
     */
    static double[][] loadLnMant() {
        return transpose(loadTable2d(LN_MANT, 2, FastMath.LN_MANT_LEN));
    }

    /**
     * @param name Basename of the resource.
     * @return an output stream.
     * @throws FileNotFoundException if the file cannot be opened.
     */
    private static DataOutputStream out(String name)
        throws FileNotFoundException {
        final String fullName = RES_PREFIX + name;
        return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fullName)));
    }

    /**
     * @param name Basename of the resource.
     * @param data Data to be stored.
     */
    private static void saveTable1d(String name,
                                    double[] data) {
        final int len = data.length;

        try {
            final DataOutputStream out = out(name);

            for (int i = 0; i < len; i++) {
                out.writeDouble(data[i]);
            }

            out.close();
        } catch (IOException e) {
            throw new MathInternalError(e);
        }
    }

    /**
     * @param name Basename of the resource.
     * @param data Data to be stored.
     */
    private static void saveTable2d(String name,
                                    double[][] data) {
        final int len = data.length;
        final int rowLen = data[0].length;

        try {
            final DataOutputStream out = out(name);

            for (int i = 0; i < len; i++) {
                for (int j = 0; j < rowLen; j++) {
                    out.writeDouble(data[i][j]);
                }
            }

            out.close();
        } catch (IOException e) {
            throw new MathInternalError(e);
        }
    }

    /**
     * @param name Basename of the resource.
     * @return an input stream.
     * @throws FileNotFoundException if the resource cannot be accessed.
     */
    private static DataInputStream in(String name)
        throws FileNotFoundException {
        final String fullName = "/" + RES_PREFIX + name;
        final InputStream in = FastMathResources.class.getResourceAsStream(fullName);
        return new DataInputStream(new BufferedInputStream(in));
    }

    /**
     * @param name Basename of the resource.
     * @param len Size of the data.
     * @return the retrieved data.
     */
    private static double[] loadTable1d(String name,
                                        int len) {
        try {
            final DataInputStream in = in(name);

            final double[] data = new double[len];
            for (int i = 0; i < len; i++) {
                data[i] = in.readDouble();
            }

            in.close();
            return data;
        } catch (IOException e) {
            throw new MathInternalError(e);
        }
    }

    /**
     * @param name Basename of the resource.
     * @param len Size of the table.
     * @param rowLen Size of each row of the table.
     * @return the retrieved data.
     */
    private static double[][] loadTable2d(String name,
                                          int len,
                                          int rowLen) {
        try {
            final DataInputStream in = in(name);
            final byte[] b = new byte[BYTES_IN_DOUBLE * rowLen];
            final double[][] data = new double[len][rowLen];
            final ByteBuffer bBuf = ByteBuffer.wrap(b);

            for (int i = 0; i < len; i++) {
                in.readFully(b);
                final DoubleBuffer dBuf = bBuf.asDoubleBuffer();
                for (int j = 0; j < rowLen; j++) {
                    data[i][j] = dBuf.get();
                }
            }

            in.close();
            return data;
        } catch (IOException e) {
            throw new MathInternalError(e);
        }
    }

    /**
     * Transposes a two-dimensional array: The number of rows becomes the
     * number of columns and vice-versa.
     * The array must be rectangular (same number of colums in each row).
     *
     * @param data Array to be transposed.
     * @return the transposed array.
     */
    private static double[][] transpose(double[][] data) {
        final int rowLen = data.length;
        final int len = data[0].length;
        final double[][] tData = new double[len][rowLen];

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < rowLen; j++) {
                tData[i][j] = data[j][i];
            }
        }

        return tData;
    }
}
