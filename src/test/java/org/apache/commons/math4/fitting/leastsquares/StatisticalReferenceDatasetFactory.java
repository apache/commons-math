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
package org.apache.commons.math4.fitting.leastsquares;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.math4.util.FastMath;

/**
 * A factory to create instances of {@link StatisticalReferenceDataset} from
 * available resources.
 */
public class StatisticalReferenceDatasetFactory {

    private StatisticalReferenceDatasetFactory() {
        // Do nothing
    }

    /**
     * Creates a new buffered reader from the specified resource name.
     *
     * @param name the name of the resource
     * @return a buffered reader
     * @throws IOException if an I/O error occured
     */
    public static BufferedReader createBufferedReaderFromResource(final String name)
        throws IOException {
        final InputStream resourceAsStream;
        resourceAsStream = StatisticalReferenceDatasetFactory.class
            .getResourceAsStream(name);
        if (resourceAsStream == null) {
            throw new IOException("could not find resource " + name);
        }
        return new BufferedReader(new InputStreamReader(resourceAsStream));
    }

    public static StatisticalReferenceDataset createKirby2()
        throws IOException {
        final BufferedReader in = createBufferedReaderFromResource("Kirby2.dat");
        StatisticalReferenceDataset dataset = null;
        try {
            dataset = new StatisticalReferenceDataset(in) {

                @Override
                public double getModelValue(final double x, final double[] a) {
                    final double p = a[0] + x * (a[1] + x * a[2]);
                    final double q = 1.0 + x * (a[3] + x * a[4]);
                    return p / q;
                }

                @Override
                public double[] getModelDerivatives(final double x,
                                                    final double[] a) {
                    final double[] dy = new double[5];
                    final double p = a[0] + x * (a[1] + x * a[2]);
                    final double q = 1.0 + x * (a[3] + x * a[4]);
                    dy[0] = 1.0 / q;
                    dy[1] = x / q;
                    dy[2] = x * dy[1];
                    dy[3] = -x * p / (q * q);
                    dy[4] = x * dy[3];
                    return dy;
                }
            };
        } finally {
            in.close();
        }
        return dataset;
    }

    public static StatisticalReferenceDataset createHahn1()
        throws IOException {
        final BufferedReader in = createBufferedReaderFromResource("Hahn1.dat");
        StatisticalReferenceDataset dataset = null;
        try {
            dataset = new StatisticalReferenceDataset(in) {

                @Override
                public double getModelValue(final double x, final double[] a) {
                    final double p = a[0] + x * (a[1] + x * (a[2] + x * a[3]));
                    final double q = 1.0 + x * (a[4] + x * (a[5] + x * a[6]));
                    return p / q;
                }

                @Override
                public double[] getModelDerivatives(final double x,
                                                    final double[] a) {
                    final double[] dy = new double[7];
                    final double p = a[0] + x * (a[1] + x * (a[2] + x * a[3]));
                    final double q = 1.0 + x * (a[4] + x * (a[5] + x * a[6]));
                    dy[0] = 1.0 / q;
                    dy[1] = x * dy[0];
                    dy[2] = x * dy[1];
                    dy[3] = x * dy[2];
                    dy[4] = -x * p / (q * q);
                    dy[5] = x * dy[4];
                    dy[6] = x * dy[5];
                    return dy;
                }
            };
        } finally {
            in.close();
        }
        return dataset;
    }

    public static StatisticalReferenceDataset createMGH17()
        throws IOException {
        final BufferedReader in = createBufferedReaderFromResource("MGH17.dat");
        StatisticalReferenceDataset dataset = null;
        try {
            dataset = new StatisticalReferenceDataset(in) {

                @Override
                public double getModelValue(final double x, final double[] a) {
                    return a[0] + a[1] * FastMath.exp(-a[3] * x) + a[2] *
                           FastMath.exp(-a[4] * x);
                }

                @Override
                public double[] getModelDerivatives(final double x,
                                                    final double[] a) {
                    final double[] dy = new double[5];
                    dy[0] = 1.0;
                    dy[1] = FastMath.exp(-x * a[3]);
                    dy[2] = FastMath.exp(-x * a[4]);
                    dy[3] = -x * a[1] * dy[1];
                    dy[4] = -x * a[2] * dy[2];
                    return dy;
                }
            };
        } finally {
            in.close();
        }
        return dataset;
    }

    public static StatisticalReferenceDataset createLanczos1()
        throws IOException {
        final BufferedReader in =
            createBufferedReaderFromResource("Lanczos1.dat");
        StatisticalReferenceDataset dataset = null;
        try {
            dataset = new StatisticalReferenceDataset(in) {

                @Override
                public double getModelValue(final double x, final double[] a) {
                    System.out.println(a[0]+", "+a[1]+", "+a[2]+", "+a[3]+", "+a[4]+", "+a[5]);
                    return a[0] * FastMath.exp(-a[3] * x) +
                           a[1] * FastMath.exp(-a[4] * x) +
                           a[2] * FastMath.exp(-a[5] * x);
                }

                @Override
                public double[] getModelDerivatives(final double x,
                    final double[] a) {
                    final double[] dy = new double[6];
                    dy[0] = FastMath.exp(-x * a[3]);
                    dy[1] = FastMath.exp(-x * a[4]);
                    dy[2] = FastMath.exp(-x * a[5]);
                    dy[3] = -x * a[0] * dy[0];
                    dy[4] = -x * a[1] * dy[1];
                    dy[5] = -x * a[2] * dy[2];
                    return dy;
                }
            };
        } finally {
            in.close();
        }
        return dataset;
    }

    /**
     * Returns an array with all available reference datasets.
     *
     * @return the array of datasets
     * @throws IOException if an I/O error occurs
     */
    public StatisticalReferenceDataset[] createAll()
        throws IOException {
        return new StatisticalReferenceDataset[] {
            createKirby2(), createMGH17()
        };
    }
}
