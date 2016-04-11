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

package org.apache.commons.math4.complex;

import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.IntegerSequence;
import org.apache.commons.math4.util.IntegerSequence.Range;

/**
 * Static implementations of common {@link Complex} utilities functions.
 */
public class ComplexUtils {

    /**
     * Utility class.
     */
    private ComplexUtils() {}

    /**
     * Creates a complex number from the given polar representation.
     * <p>
     * If either {@code r} or {@code theta} is NaN, or {@code theta} is
     * infinite, {@link Complex#NaN} is returned.
     * <p>
     * If {@code r} is infinite and {@code theta} is finite, infinite or NaN
     * values may be returned in parts of the result, following the rules for
     * double arithmetic.
     *
     * <pre>
     * Examples:
     * {@code
     * polar2Complex(INFINITY, \(\pi\)) = INFINITY + INFINITY i
     * polar2Complex(INFINITY, 0) = INFINITY + NaN i
     * polar2Complex(INFINITY, \(-\frac{\pi}{4}\)) = INFINITY - INFINITY i
     * polar2Complex(INFINITY, \(5\frac{\pi}{4}\)) = -INFINITY - INFINITY i }
     * </pre>
     *
     * @param r the modulus of the complex number to create
     * @param theta the argument of the complex number to create
     * @return {@code Complex}
     * @throws MathIllegalArgumentException  if {@code r} is negative.
     * @since 1.1
     */
    public static Complex polar2Complex(double r, double theta) throws MathIllegalArgumentException {
        if (r < 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.NEGATIVE_COMPLEX_MODULE, r);
        }
        return new Complex(r * FastMath.cos(theta), r * FastMath.sin(theta));
    }

    /**
     * Creates {@code Complex[]} array given {@code double[]} arrays of r and
     * theta.
     *
     * @param r {@code double[]} of moduli
     * @param theta {@code double[]} of arguments
     * @return {@code Complex[]}
     * @throws MathIllegalArgumentException
     *             if {@code r} is negative.
     * @since 4.0
     */
    public static Complex[] polar2Complex(double[] r, double[] theta) throws MathIllegalArgumentException {
        final int length = r.length;
        final Complex[] c = new Complex[length];
        for (int x = 0; x < length; x++) {
            if (r[x] < 0) {
                throw new MathIllegalArgumentException(LocalizedFormats.NEGATIVE_COMPLEX_MODULE, r[x]);
            }
            c[x] = new Complex(r[x] * FastMath.cos(theta[x]), r[x] * FastMath.sin(theta[x]));
        }
        return c;
    }

    /**
     * Creates {@code Complex[][]} array given {@code double[][]} arrays of r
     * and theta.
     *
     * @param r {@code double[]} of moduli
     * @param theta {@code double[]} of arguments
     * @return {@code Complex[][]}
     * @throws MathIllegalArgumentException
     *             if {@code r} is negative.
     * @since 4.0
     */
    public static Complex[][] polar2Complex(double[][] r, double[][] theta) throws MathIllegalArgumentException {
        final int length = r.length;
        final Complex[][] c = new Complex[length][];
        for (int x = 0; x < length; x++) {
            c[x] = polar2Complex(r[x], theta[x]);
        }
        return c;
    }

    /**
     * Creates {@code Complex[][][]} array given {@code double[][][]} arrays of
     * r and theta.
     *
     * @param r array of moduli
     * @param theta array of arguments
     * @return {@code Complex}
     * @throws MathIllegalArgumentException  if {@code r} is negative.
     * @since 4.0
     */
    public static Complex[][][] polar2Complex(double[][][] r, double[][][] theta) throws MathIllegalArgumentException {
        final int length = r.length;
        final Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < length; x++) {
            c[x] = polar2Complex(r[x], theta[x]);
        }
        return c;
    }

    /**
     * Returns double from array {@code real[]} at entry {@code index} as a
     * {@code Complex}.
     *
     * @param real array of real numbers
     * @param index location in the array
     * @return {@code Complex}.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromRealArray(double[] real, int index) {
        return new Complex(real[index]);
    }

    /**
     * Returns float from array {@code real[]} at entry {@code index} as a
     * {@code Complex}.
     *
     * @param real array of real numbers
     * @param index location in the array
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex extractComplexFromRealArray(float[] real, int index) {
        return new Complex(real[index]);
    }

    /**
     * Returns double from array {@code imaginary[]} at entry {@code index} as a
     * {@code Complex}.
     *
     * @param imaginary array of imaginary numbers
     * @param index location in the array
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex extractComplexFromImaginaryArray(double[] imaginary, int index) {
        return new Complex(0, imaginary[index]);
    }

    /**
     * Returns float from array {@code imaginary[]} at entry {@code index} as a
     * {@code Complex}.
     *
     * @param imaginary array of imaginary numbers
     * @param index location in the array
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex extractComplexFromImaginaryArray(float[] imaginary, int index) {
        return new Complex(0, imaginary[index]);
    }

    /**
     * Returns real component of Complex from array {@code Complex[]} at entry
     * {@code index} as a {@code double}.
     *
     * @param complex array of complex numbers
     * @param index location in the array
     * @return {@code double}.
     *
     * @since 4.0
     */
    public static double extractRealFromComplexArray(Complex[] complex, int index) {
        return complex[index].getReal();
    }

    /**
     * Returns real component of array {@code Complex[]} at entry {@code index}
     * as a {@code float}.
     *
     * @param complex array of complex numbers
     * @param index location in the array
     * @return {@code float}.
     *
     * @since 4.0
     */
    public static float extractRealFloatFromComplexArray(Complex[] complex, int index) {
        return (float) complex[index].getReal();
    }

    /**
     * Returns imaginary component of Complex from array {@code Complex[]} at
     * entry {@code index} as a {@code double}.
     *
     * @param complex array of complex numbers
     * @param index location in the array
     * @return {@code double}.
     *
     * @since 4.0
     */
    public static double extractImaginaryFromComplexArray(Complex[] complex, int index) {
        return complex[index].getImaginary();
    }

    /**
     * Returns imaginary component of array {@code Complex[]} at entry
     * {@code index} as a {@code float}.
     *
     * @param complex array of complex numbers
     * @param index location in the array
     * @return {@code float}.
     *
     * @since 4.0
     */
    public static float extractImaginaryFloatFromComplexArray(Complex[] complex, int index) {
        return (float) complex[index].getImaginary();
    }

    /**
     * Returns a Complex object from interleaved {@code double[]} array at entry
     * {@code index}.
     *
     * @param d array of interleaved complex numbers alternating real and imaginary values
     * @param index location in the array This is the location by complex number, e.g. index number 5 in the array will return {@code new Complex(d[10], d[11])}
     * @return {@code Complex}.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromInterleavedArray(double[] d, int index) {
        return new Complex(d[index * 2], d[index * 2 + 1]);
    }

    /**
     * Returns a Complex object from interleaved {@code float[]} array at entry
     * {@code index}.
     *
     * @param f float array of interleaved complex numbers alternating real and imaginary values
     * @param index location in the array This is the location by complex number, e.g. index number 5 in the {@code float[]} array will return new {@code Complex(d[10], d[11])}
     * @return {@code Complex}.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromInterleavedArray(float[] f, int index) {
        return new Complex(f[index * 2], f[index * 2 + 1]);
    }

    /**
     * Returns values of Complex object from array {@code Complex[]} at entry
     * {@code index} as a size 2 {@code double} of the form {real, imag}.
     *
     * @param complex array of complex numbers
     * @param index location in the array
     * @return size 2 array.
     *
     * @since 4.0
     */
    public static double[] extractInterleavedFromComplexArray(Complex[] complex, int index) {
        return new double[] { complex[index].getReal(), complex[index].getImaginary() };
    }

    /**
     * Returns Complex object from array {@code Complex[]} at entry
     * {@code index} as a size 2 {@code float} of the form {real, imag}.
     *
     * @param complex {@code Complex} array
     * @param index location in the array
     * @return size 2 {@code float[]}.
     *
     * @since 4.0
     */
    public static float[] extractInterleavedFloatFromComplexArray(Complex[] complex, int index) {
        return new float[] { (float) complex[index].getReal(), (float) complex[index].getImaginary() };
    }

    /**
     * Converts a {@code double[]} array to a {@code Complex[]} array for the
     * range {@code start} - {@code end}.
     *
     * @param real array of real numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code float[]} array to a {@code Complex[]} array for the
     * range {@code start} - {@code end}.
     *
     * @param real array of real numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code double[]} array to a {@code Complex[]} array for the
     * range {@code start} - {@code end} by {@code increment}.
     *
     * @param real array of numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code float[]} array to a {@code Complex[]} array for the
     * range {@code start} - {@code end} by {@code increment}.
     *
     * @param real array of numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code double[]} array to a {@code Complex[]} array for the
     * {@code IntegerSequence} range.
     *
     * @param real array of numbers to be converted to their {@code Complex} equivalent
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real, Range range) {
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code float[]} array to a {@code Complex[]} array for the
     * {@code IntegerSequence} range.
     *
     * @param real array of numbers to be converted to their {@code Complex} equivalent
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real, Range range) {
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code double[]} array to a {@code Complex[]} array.
     *
     * @param real array of numbers to be converted to their {@code Complex} equivalent
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real) {
        int index = 0;
        final Complex c[] = new Complex[real.length];
        for (double d : real) {
            c[index] = new Complex(d);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code float[]} array to a {@code Complex[]} array.
     *
     * @param real array of numbers to be converted to their {@code Complex} equivalent
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real) {
        int index = 0;
        final Complex c[] = new Complex[real.length];
        for (float d : real) {
            c[index] = new Complex(d);
            index++;
        }
        return c;
    }

    /**
     * Converts a 2D real {@code double[][]} array to a 2D {@code Complex[][]}
     * array.
     *
     * @param d 2D array
     * @return 2D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][] real2Complex(double[][] d) {
        final int width = d.length;
        final Complex[][] c = new Complex[width][];
        for (int n = 0; n < width; n++) {
            c[n] = ComplexUtils.real2Complex(d[n]);
        }
        return c;
    }

    /**
     * Converts a 3D real {@code double[][][]} array to a {@code Complex [][][]}
     * array.
     *
     * @param d 3D complex interleaved array
     * @return 3D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][][] real2Complex(double[][][] d) {
        final int width = d.length;
        final Complex[][][] c = new Complex[width][][];
        for (int x = 0; x < width; x++) {
            c[x] = ComplexUtils.real2Complex(d[x]);
        }
        return c;
    }

    /**
     * Converts a {@code Complex[]} array to a {@code double[]} array for the
     * range {@code start} - {@code end}.
     *
     * @param c {@code Complex} array
     * @param start start index
     * @param end end index
     * @return array of the real component
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final double d[] = new double[range.size()];
        for (Integer i : range) {
            d[index] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    /**
     * Converts a {@code Complex[]} array to a {@code float[]} array for the
     * range {@code start} - {@code end}.
     *
     * @param c {@code Complex} array
     * @param start start index
     * @param end end index
     * @return {@code float[]} array of the real component
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final float f[] = new float[range.size()];
        for (Integer i : range) {
            f[index] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    /**
     * Converts a {@code Complex[]} array to a {@code double[]} array for the
     * range {@code start} - {@code end} by {@code increment}.
     *
     * @param c {@code Complex} array
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return array of the real component
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final double d[] = new double[range.size()];
        for (Integer i : range) {
            d[index] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    /**
     * Converts a {@code Complex[]} array to a {@code float[]} array for the
     * range {@code start} - {@code end} by {@code increment}.
     *
     * @param c {@code Complex} array
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return {@code float[]} array of the real component
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final float f[] = new float[range.size()];
        for (Integer i : range) {
            f[index] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    /**
     * Converts a {@code Complex[]} array to a {@code double[]} array for the
     * {@code IntegerSequence} range.
     *
     * @param c {@code Complex} array
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return array of the real component
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c, Range range) {
        int index = 0;
        final double d[] = new double[range.size()];
        for (Integer i : range) {
            d[index] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    /**
     * Converts a {@code Complex[]} array to a {@code float[]} array for the
     * {@code IntegerSequence} range.
     *
     * @param c {@code Complex} array
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return {@code float[]} array of the real component
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c, Range range) {
        int index = 0;
        final float f[] = new float[range.size()];
        for (Integer i : range) {
            f[index] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    /**
     * Converts real component of {@code Complex[]} array to a {@code double[]}
     * array.
     *
     * @param c {@code Complex} array
     * @return array of the real component
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c) {
        int index = 0;
        final double d[] = new double[c.length];
        for (Complex cc : c) {
            d[index] = cc.getReal();
            index++;
        }
        return d;
    }

    /**
     * Converts real component of {@code Complex[]} array to a {@code float[]}
     * array.
     *
     * @param c {@code Complex} array
     * @return {@code float[]} array of the real component
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c) {
        int index = 0;
        final float f[] = new float[c.length];
        for (Complex cc : c) {
            f[index] = (float) cc.getReal();
            index++;
        }
        return f;
    }

    /**
     * Converts real component of a 2D {@code Complex[][]} array to a 2D
     * {@code double[][]} array.
     *
     * @param c 2D {@code Complex} array
     * @return {@code double[][]} of real component
     * @since 4.0
     */
    public static double[][] complex2Real(Complex[][] c) {
        final int length = c.length;
        double[][] d = new double[length][];
        for (int n = 0; n < length; n++) {
            d[n] = complex2Real(c[n]);
        }
        return d;
    }

    /**
     * Converts real component of a 2D {@code Complex[][]} array to a 2D
     * {@code float[][]} array.
     *
     * @param c 2D {@code Complex} array
     * @return {@code float[][]} of real component
     * @since 4.0
     */
    public static float[][] complex2RealFloat(Complex[][] c) {
        final int length = c.length;
        float[][] f = new float[length][];
        for (int n = 0; n < length; n++) {
            f[n] = complex2RealFloat(c[n]);
        }
        return f;
    }

    /**
     * Converts real component of a 3D {@code Complex[][][]} array to a 3D
     * {@code double[][][]} array.
     *
     * @param c 3D complex interleaved array
     * @return array of real component
     *
     * @since 4.0
     */
    public static double[][][] complex2Real(Complex[][][] c) {
        final int length = c.length;
        double[][][] d = new double[length][][];
        for (int n = 0; n < length; n++) {
            d[n] = complex2Real(c[n]);
        }
        return d;
    }

    /**
     * Converts real component of a 3D {@code Complex[][][]} array to a 3D
     * {@code float[][][]} array.
     *
     * @param c 3D {@code Complex} array
     * @return {@code float[][][]} of real component
     * @since 4.0
     */
    public static float[][][] complex2RealFloat(Complex[][][] c) {
        final int length = c.length;
        float[][][] f = new float[length][][];
        for (int n = 0; n < length; n++) {
            f[n] = complex2RealFloat(c[n]);
        }
        return f;
    }

    /**
     * Converts a {@code double[]} array to an imaginary {@code Complex[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param imaginary array of imaginary numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(double[] imaginary, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code float[]} array to an imaginary {@code Complex[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param imaginary array of imaginary numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(float[] imaginary, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code double[]} array to an imaginary {@code Complex[]} array
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param imaginary array of numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(double[] imaginary, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code float[]} array to an imaginary {@code Complex[]} array
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param imaginary array of numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(float[] imaginary, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code double[]} array to an imaginary {@code Complex[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param imaginary array of numbers to be converted to their {@code Complex} equivalent
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(double[] imaginary, Range range) {
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code float[]} array to an imaginary {@code Complex[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param imaginary array of numbers to be converted to their {@code Complex} equivalent
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(float[] imaginary, Range range) {
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code double[]} array to an imaginary {@code Complex[]}
     * array.
     *
     * @param imaginary array of numbers to be converted to their {@code Complex} equivalent
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(double[] imaginary) {
        int index = 0;
        final Complex c[] = new Complex[imaginary.length];
        for (double d : imaginary) {
            c[index] = new Complex(0, d);
            index++;
        }
        return c;
    }

    /**
     * Converts a {@code float[]} array to an imaginary {@code Complex[]} array.
     *
     * @param imaginary array of numbers to be converted to their {@code Complex} equivalent
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(float[] imaginary) {
        int index = 0;
        final Complex c[] = new Complex[imaginary.length];
        for (float d : imaginary) {
            c[index] = new Complex(0, d);
            index++;
        }
        return c;
    }

    /**
     * Converts a 2D imaginary array {@code double[][]} to a 2D
     * {@code Complex[][]} array.
     *
     * @param d 2D array
     * @return 2D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][] imaginary2Complex(double[][] d) {
        int width = d.length;
        int height = d[0].length;
        Complex[][] c = new Complex[width][height];
        for (int n = 0; n < width; n++) {
            c[n] = ComplexUtils.imaginary2Complex(d[n]);
        }
        return c;
    }

    /**
     * Converts a 3D imaginary array {@code double[][][]} to a {@code Complex[]}
     * array.
     *
     * @param d 3D complex imaginary array
     * @return 3D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][][] imaginary2Complex(double[][][] d) {
        int width = d.length;
        int height = d[0].length;
        int depth = d[0].length;
        Complex[][][] c = new Complex[width][height][depth];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                c[x][y] = ComplexUtils.imaginary2Complex(d[x][y]);
            }
        }
        return c;
    }

    /**
     * Converts imaginary part of {@code Complex[]} array to a {@code double[]}
     * array for the range {@code start} - {@code end}.
     *
     * @param c {@code Complex} array.
     * @param start start index
     * @param end end index
     * @return array of the imaginary component
     *
     * @since 4.0
     */
    public static double[] complex2Imaginary(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final double d[] = new double[range.size()];
        for (Integer i : range) {
            d[index] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    /**
     * Converts imaginary part of a {@code Complex[]} array to a {@code float[]}
     * array for the range {@code start} - {@code end}.
     *
     * @param c Complex array
     * @param start start index
     * @param end end index
     * @return {@code float[]} array of the imaginary component
     *
     * @since 4.0
     */
    public static float[] complex2ImaginaryFloat(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final float f[] = new float[range.size()];
        for (Integer i : range) {
            f[index] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    /**
     * Converts imaginary part of a {@code Complex[]} array to a
     * {@code double[]} array for the range {@code start} - {@code end} by
     * {@code increment}.
     *
     * @param c {@code Complex} array.
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return array of the imaginary component
     *
     * @since 4.0
     */
    public static double[] complex2Imaginary(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final double d[] = new double[range.size()];
        for (Integer i : range) {
            d[index] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    /**
     * Converts imaginary part of a {@code Complex[]} array to a {@code float[]}
     * array for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param c {@code Complex} array.
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return {@code float[]} array of the imaginary component
     *
     * @since 4.0
     */
    public static float[] complex2ImaginaryFloat(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final float f[] = new float[range.size()];
        for (Integer i : range) {
            f[index] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    /**
     * Converts imaginary part of a {@code Complex[]} array to a
     * {@code double[]} array for the {@code IntegerSequence} range.
     *
     * @param c {@code Complex} array.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return array of the imaginary component
     *
     * @since 4.0
     */
    public static double[] complex2Imaginary(Complex[] c, Range range) {
        int index = 0;
        final double d[] = new double[range.size()];
        for (Integer i : range) {
            d[index] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    /**
     * Converts imaginary part of a {@code Complex[]} array to a {@code float[]}
     * array for the {@code IntegerSequence} range.
     *
     * @param c {@code Complex} array.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return {@code float[]} array of the imaginary component
     *
     * @since 4.0
     */
    public static float[] complex2ImaginaryFloat(Complex[] c, Range range) {
        int index = 0;
        final float f[] = new float[range.size()];
        for (Integer i : range) {
            f[index] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    /**
     * Converts imaginary part of a {@code Complex[]} array to a
     * {@code double[]} array.
     *
     * @param c {@code Complex} array.
     * @return array of the imaginary component
     *
     * @since 4.0
     */
    public static double[] complex2Imaginary(Complex[] c) {
        int index = 0;
        final double d[] = new double[c.length];
        for (Complex cc : c) {
            d[index] = cc.getImaginary();
            index++;
        }
        return d;
    }

    /**
     * Converts imaginary component of a {@code Complex[]} array to a
     * {@code float[]} array.
     *
     * @param c {@code Complex} array.
     * @return {@code float[]} array of the imaginary component
     *
     * @since 4.0
     */
    public static float[] complex2ImaginaryFloat(Complex[] c) {
        int index = 0;
        final float f[] = new float[c.length];
        for (Complex cc : c) {
            f[index] = (float) cc.getImaginary();
            index++;
        }
        return f;
    }

    /**
     * Converts imaginary component of a 2D {@code Complex[][]} array to a 2D
     * {@code double[][]} array.
     *
     * @param c 2D {@code Complex} array
     * @return {@code double[][]} of imaginary component
     * @since 4.0
     */
    public static double[][] complex2Imaginary(Complex[][] c) {
        final int length = c.length;
        double[][] d = new double[length][];
        for (int n = 0; n < length; n++) {
            d[n] = complex2Imaginary(c[n]);
        }
        return d;
    }

    /**
     * Converts imaginary component of a 2D {@code Complex[][]} array to a 2D
     * {@code float[][]} array.
     *
     * @param c 2D {@code Complex} array
     * @return {@code float[][]} of imaginary component
     * @since 4.0
     */
    public static float[][] complex2ImaginaryFloat(Complex[][] c) {
        final int length = c.length;
        float[][] f = new float[length][];
        for (int n = 0; n < length; n++) {
            f[n] = complex2ImaginaryFloat(c[n]);
        }
        return f;
    }

    /**
     * Converts imaginary component of a 3D {@code Complex[][][]} array to a 3D
     * {@code double[][][]} array.
     *
     * @param c 3D complex interleaved array
     * @return 3D {@code Complex} array
     *
     * @since 4.0
     */
    public static double[][][] complex2Imaginary(Complex[][][] c) {
        final int length = c.length;
        double[][][] d = new double[length][][];
        for (int n = 0; n < length; n++) {
            d[n] = complex2Imaginary(c[n]);
        }
        return d;
    }

    /**
     * Converts imaginary component of a 3D {@code Complex[][][]} array to a 3D
     * {@code float[][][]} array.
     *
     * @param c 3D {@code Complex} array
     * @return {@code float[][][]} of imaginary component
     * @since 4.0
     */
    public static float[][][] complex2ImaginaryFloat(Complex[][][] c) {
        final int length = c.length;
        float[][][] f = new float[length][][];
        for (int n = 0; n < length; n++) {
            f[n] = complex2ImaginaryFloat(c[n]);
        }
        return f;
    }

    // INTERLEAVED METHODS

    /**
     * Converts a complex interleaved {@code double[]} array to a
     * {@code Complex[]} array for the range {@code start} - {@code end}.
     *
     * @param interleaved array of numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a complex interleaved {@code float[]} array to a
     * {@code Complex[]} array for the range {@code start} - {@code end}.
     *
     * @param interleaved float array of numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a complex interleaved {@code double[]} array to a
     * {@code Complex[]} array for the range {@code start} - {@code end} by
     * {@code increment}.
     *
     * @param interleaved array of numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a complex interleaved {@code float[]} array to a
     * {@code Complex[]} array for the range {@code start} - {@code end} by
     * {@code increment}.
     *
     * @param interleaved float array of numbers to be converted to their {@code Complex} equivalent
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a complex interleaved {@code double[]} array to a
     * {@code Complex[]} array for the {@code IntegerSequence} range.
     *
     * @param interleaved array of numbers to be converted to their {@code Complex} equivalent
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved, Range range) {
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a complex interleaved {@code float[]} array to a
     * {@code Complex[]} array for the {@code IntegerSequence} range.
     *
     * @param interleaved float array of numbers to be converted to their {@code Complex} equivalent
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved, Range range) {
        int index = 0;
        final Complex c[] = new Complex[range.size()];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    /**
     * Converts a complex interleaved {@code double[]} array to a
     * {@code Complex[]} array
     *
     * @param interleaved array of numbers to be converted to their {@code Complex} equivalent
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved) {
        final int length = interleaved.length / 2;
        final Complex c[] = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = new Complex(interleaved[n * 2], interleaved[n * 2 + 1]);
        }
        return c;
    }

    /**
     * Converts a complex interleaved {@code float[]} array to a
     * {@code Complex[]} array
     *
     * @param interleaved float[] array of numbers to be converted to their {@code Complex} equivalent
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved) {
        final int length = interleaved.length / 2;
        final Complex c[] = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = new Complex(interleaved[n * 2], interleaved[n * 2 + 1]);
        }
        return c;
    }

    /**
     * Converts a {@code Complex[]} array to an interleaved complex
     * {@code double[]} array for the range {@code start} - {@code end}.
     *
     * @param c Complex array
     * @param start start index
     * @param end end index
     * @return complex interleaved array alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final double d[] = new double[range.size() * 2];
        for (Integer i : range) {
            int real = index * 2;
            int imag = index * 2 + 1;
            d[real] = c[i].getReal();
            d[imag] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    /**
     * Converts a {@code Complex[]} array to an interleaved complex
     * {@code float[]} array for the range {@code start} - {@code end}.
     *
     * @param c Complex array
     * @param start start index
     * @param end end index
     * @return complex interleaved {@code float[]} alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range(start, end);
        int index = 0;
        final float f[] = new float[range.size() * 2];
        for (Integer i : range) {
            int real = index * 2;
            int imag = index * 2 + 1;
            f[real] = (float) c[i].getReal();
            f[imag] = (float) c[i].getImaginary();
            index++;
        }
        return f;
    }

    /**
     * Converts a {@code Complex[]} array to an interleaved complex
     * {@code double[]} array for the range {@code start} - {@code end} by
     * {@code increment}.
     *
     * @param c Complex array
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return complex interleaved array alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final double d[] = new double[range.size() * 2];
        for (Integer i : range) {
            int real = index * 2;
            int imag = index * 2 + 1;
            d[real] = c[i].getReal();
            d[imag] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    /**
     * Converts a {@code Complex[]} array to an interleaved complex
     * {@code float[]} array for the range {@code start} - {@code end} by
     * {@code increment}.
     *
     * @param c Complex array
     * @param start start index
     * @param end end index
     * @param increment range increment
     * @return complex interleaved {@code float[]} alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range(start, end, increment);
        int index = 0;
        final float f[] = new float[range.size() * 2];
        for (Integer i : range) {
            int real = index * 2;
            int imag = index * 2 + 1;
            f[real] = (float) c[i].getReal();
            f[imag] = (float) c[i].getImaginary();
            index++;
        }
        return f;
    }

    /**
     * Converts a {@code Complex[]} array to an interleaved complex
     * {@code double[]} array for the {@code IntegerSequence} range.
     *
     * @param c Complex array
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return complex interleaved array alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c, Range range) {
        int index = 0;
        final double d[] = new double[range.size() * 2];
        for (Integer i : range) {
            int real = index * 2;
            int imag = index * 2 + 1;
            d[real] = c[i].getReal();
            d[imag] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    /**
     * Converts a {@code Complex[]} array to an interleaved complex
     * {@code float[]} array for the {@code IntegerSequence} range.
     *
     * @param c Complex array
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()}
     * @return complex interleaved {@code float[]} alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c, Range range) {
        int index = 0;
        final float f[] = new float[range.size() * 2];
        for (Integer i : range) {
            int real = index * 2;
            int imag = index * 2 + 1;
            f[real] = (float) c[i].getReal();
            f[imag] = (float) c[i].getImaginary();
            index++;
        }
        return f;
    }

    /**
     * Converts a {@code Complex[]} array to an interleaved complex
     * {@code double[]} array
     *
     * @param c Complex array
     * @return complex interleaved array alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c) {
        int index = 0;
        final double d[] = new double[c.length * 2];
        for (Complex cc : c) {
            int real = index * 2;
            int imag = index * 2 + 1;
            d[real] = cc.getReal();
            d[imag] = cc.getImaginary();
            index++;
        }
        return d;
    }

    /**
     * Converts a {@code Complex[]} array to an interleaved complex
     * {@code float[]} array
     *
     * @param c Complex array
     * @return complex interleaved {@code float[]} alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c) {
        int index = 0;
        final float f[] = new float[c.length * 2];
        for (Complex cc : c) {
            int real = index * 2;
            int imag = index * 2 + 1;
            f[real] = (float) cc.getReal();
            f[imag] = (float) cc.getImaginary();
            index++;
        }
        return f;
    }

    /**
     * Converts a 2D {@code Complex[][]} array to an interleaved complex
     * {@code double[][]} array.
     *
     * @param c 2D Complex array
     * @param interleavedDim Depth level of the array to interleave
     * @return complex interleaved array alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static double[][] complex2Interleaved(Complex[][] c, int interleavedDim) {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = c.length;
        final int height = c[0].length;
        double[][] d;
        if (interleavedDim == 0) {
            d = new double[2 * width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    d[x * 2][y] = c[x][y].getReal();
                    d[x * 2 + 1][y] = c[x][y].getImaginary();
                }
            }
        } else {
            d = new double[width][2 * height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    d[x][y * 2] = c[x][y].getReal();
                    d[x][y * 2 + 1] = c[x][y].getImaginary();
                }
            }
        }
        return d;
    }

    /**
     * Converts a 2D {@code Complex[][]} array to an interleaved complex
     * {@code double[][]} array. The second depth level of the array is assumed
     * to be interleaved.
     *
     * @param c 2D Complex array
     * @return complex interleaved array alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static double[][] complex2Interleaved(Complex[][] c) {
        return complex2Interleaved(c, 1);
    }

    /**
     * Converts a 3D {@code Complex[][][]} array to an interleaved complex
     * {@code double[][][]} array.
     *
     * @param c 3D Complex array
     * @param interleavedDim Depth level of the array to interleave
     * @return complex interleaved array alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static double[][][] complex2Interleaved(Complex[][][] c, int interleavedDim) {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        int width = c.length;
        int height = c[0].length;
        int depth = c[0][0].length;
        double[][][] d;
        if (interleavedDim == 0) {
            d = new double[2 * width][height][depth];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        d[x * 2][y][z] = c[x][y][z].getReal();
                        d[x * 2 + 1][y][z] = c[x][y][z].getImaginary();
                    }
                }
            }
        } else if (interleavedDim == 1) {
            d = new double[width][2 * height][depth];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        d[x][y * 2][z] = c[x][y][z].getReal();
                        d[x][y * 2 + 1][z] = c[x][y][z].getImaginary();
                    }
                }
            }
        } else {
            d = new double[width][height][2 * depth];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        d[x][y][z * 2] = c[x][y][z].getReal();
                        d[x][y][z * 2 + 1] = c[x][y][z].getImaginary();
                    }
                }
            }
        }
        return d;
    }

    /**
     * Converts a 3D {@code Complex[][][]} array to an interleaved complex
     * {@code double[][][]} array. The third depth level of the array is
     * interleaved.
     *
     * @param c 3D Complex array
     * @return complex interleaved array alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static double[][][] complex2Interleaved(Complex[][][] c) {
        return complex2Interleaved(c, 2);
    }

    /**
     * Converts a 2D {@code Complex[][]} array to an interleaved complex
     * {@code float[][]} array.
     *
     * @param c 2D Complex array
     * @param interleavedDim Depth level of the array to interleave
     * @return complex interleaved {@code float[][]} alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static float[][] complex2InterleavedFloat(Complex[][] c, int interleavedDim) {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = c.length;
        final int height = c[0].length;
        float[][] d;
        if (interleavedDim == 0) {
            d = new float[2 * width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    d[x * 2][y] = (float) c[x][y].getReal();
                    d[x * 2 + 1][y] = (float) c[x][y].getImaginary();
                }
            }
        } else {
            d = new float[width][2 * height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    d[x][y * 2] = (float) c[x][y].getReal();
                    d[x][y * 2 + 1] = (float) c[x][y].getImaginary();
                }
            }
        }
        return d;
    }

    /**
     * Converts a 2D {@code Complex[][]} array to an interleaved complex
     * {@code float[][]} array. The second depth level of the array is assumed
     * to be interleaved.
     *
     * @param c 2D Complex array
     *
     * @return complex interleaved {@code float[][]} alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static float[][] complex2InterleavedFloat(Complex[][] c) {
        return complex2InterleavedFloat(c, 1);
    }

    /**
     * Converts a 3D {@code Complex[][][]} array to an interleaved complex
     * {@code float[][][]} array.
     *
     * @param c 3D Complex array
     * @param interleavedDim Depth level of the array to interleave
     * @return complex interleaved {@code float[][][]} alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static float[][][] complex2InterleavedFloat(Complex[][][] c, int interleavedDim) {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        final int width = c.length;
        final int height = c[0].length;
        final int depth = c[0][0].length;
        float[][][] d;
        if (interleavedDim == 0) {
            d = new float[2 * width][height][depth];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        d[x * 2][y][z] = (float) c[x][y][z].getReal();
                        d[x * 2 + 1][y][z] = (float) c[x][y][z].getImaginary();
                    }
                }
            }
        } else if (interleavedDim == 1) {
            d = new float[width][2 * height][depth];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        d[x][y * 2][z] = (float) c[x][y][z].getReal();
                        d[x][y * 2 + 1][z] = (float) c[x][y][z].getImaginary();
                    }
                }
            }
        } else {
            d = new float[width][height][2 * depth];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        d[x][y][z * 2] = (float) c[x][y][z].getReal();
                        d[x][y][z * 2 + 1] = (float) c[x][y][z].getImaginary();
                    }
                }
            }
        }
        return d;
    }

    /**
     * Converts a 3D {@code Complex[][][]} array to an interleaved complex
     * {@code float[][][]} array. The third depth level of the array is
     * interleaved.
     *
     * @param c 2D Complex array
     *
     * @return complex interleaved {@code float[][][]} alternating real and
     *         imaginary values
     *
     * @since 4.0
     */
    public static float[][][] complex2InterleavedFloat(Complex[][][] c) {
        return complex2InterleavedFloat(c, 2);
    }

    /**
     * Converts a 2D interleaved complex {@code double[][]} array to a
     * {@code Complex[][]} array.
     *
     * @param d 2D complex interleaved array
     * @param interleavedDim Depth level of the array to interleave
     * @return 2D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][] interleaved2Complex(double[][] d, int interleavedDim) {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = d.length;
        final int height = d[0].length;
        Complex[][] c;
        if (interleavedDim == 0) {
            c = new Complex[width / 2][height];
            for (int x = 0; x < width / 2; x++) {
                for (int y = 0; y < height; y++) {
                    c[x][y] = new Complex(d[x * 2][y], d[x * 2 + 1][y]);
                }
            }
        } else {
            c = new Complex[width][height / 2];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height / 2; y++) {
                    c[x][y] = new Complex(d[x][y * 2], d[x][y * 2 + 1]);
                }
            }
        }
        return c;
    }

    /**
     * Converts a 2D interleaved complex {@code double[][]} array to a
     * {@code Complex[][]} array. The second depth level of the array is assumed
     * to be interleaved.
     *
     * @param d 2D complex interleaved array
     * @return 2D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][] interleaved2Complex(double[][] d) {
        return interleaved2Complex(d, 1);
    }

    /**
     * Converts a 3D interleaved complex {@code double[][][]} array to a
     * {@code Complex[][][]} array.
     *
     * @param d 3D complex interleaved array
     * @param interleavedDim Depth level of the array to interleave
     * @return 3D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][][] interleaved2Complex(double[][][] d, int interleavedDim) {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        final int width = d.length;
        final int height = d[0].length;
        final int depth = d[0][0].length;
        Complex[][][] c;
        if (interleavedDim == 0) {
            c = new Complex[width / 2][height][depth];
            for (int x = 0; x < width / 2; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        c[x][y][z] = new Complex(d[x * 2][y][z], d[x * 2 + 1][y][z]);
                    }
                }
            }
        } else if (interleavedDim == 1) {
            c = new Complex[width][height / 2][depth];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height / 2; y++) {
                    for (int z = 0; z < depth; z++) {
                        c[x][y][z] = new Complex(d[x][y * 2][z], d[x][y * 2 + 1][z]);
                    }
                }
            }
        } else {
            c = new Complex[width][height][depth / 2];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth / 2; z++) {
                        c[x][y][z] = new Complex(d[x][y][z * 2], d[x][y][z * 2 + 1]);
                    }
                }
            }
        }
        return c;
    }

    /**
     * Converts a 3D interleaved complex {@code double[][][]} array to a
     * {@code Complex[][][]} array. The third depth level is assumed to be
     * interleaved.
     *
     * @param d 3D complex interleaved array
     * @return 3D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][][] interleaved2Complex(double[][][] d) {
        return interleaved2Complex(d, 2);
    }

    /**
     * Converts a 2D interleaved complex {@code float[][]} array to a
     * {@code Complex[][]} array.
     *
     * @param d 2D complex interleaved float array
     * @param interleavedDim Depth level of the array to interleave
     * @return 2D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][] interleaved2Complex(float[][] d, int interleavedDim) {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = d.length;
        final int height = d[0].length;
        Complex[][] c;
        if (interleavedDim == 0) {
            c = new Complex[width / 2][height];
            for (int x = 0; x < width / 2; x++) {
                for (int y = 0; y < height; y++) {
                    c[x][y] = new Complex(d[x * 2][y], d[x * 2 + 1][y]);
                }
            }
        } else {
            c = new Complex[width][height / 2];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height / 2; y++) {
                    c[x][y] = new Complex(d[x][y * 2], d[x][y * 2 + 1]);
                }
            }
        }
        return c;
    }

    /**
     * Converts a 2D interleaved complex {@code float[][]} array to a
     * {@code Complex[][]} array. The second depth level of the array is assumed
     * to be interleaved.
     *
     * @param d 2D complex interleaved float array
     * @return 2D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][] interleaved2Complex(float[][] d) {
        return interleaved2Complex(d, 1);
    }

    /**
     * Converts a 3D interleaved complex {@code float[][][]} array to a
     * {@code Complex[][][]} array.
     *
     * @param d 3D complex interleaved float array
     * @param interleavedDim Depth level of the array to interleave
     * @return 3D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][][] interleaved2Complex(float[][][] d, int interleavedDim) {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        final int width = d.length;
        final int height = d[0].length;
        final int depth = d[0][0].length;
        Complex[][][] c;
        if (interleavedDim == 0) {
            c = new Complex[width / 2][height][depth];
            for (int x = 0; x < width/2; x ++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        c[x][y][z] = new Complex(d[x * 2][y][z], d[x * 2 + 1][y][z]);
                    }
                }
            }
        } else if (interleavedDim == 1) {
            c = new Complex[width][height / 2][depth];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height/2; y ++) {
                    for (int z = 0; z < depth; z++) {
                        c[x][y][z] = new Complex(d[x][y * 2][z], d[x][y * 2 + 1][z]);
                    }
                }
            }
        } else {
            c = new Complex[width][height][depth / 2];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth/2; z++) {
                        c[x][y][z] = new Complex(d[x][y][z * 2], d[x][y][z * 2 + 1]);
                    }
                }
            }
        }
        return c;
    }

    /**
     * Converts a 3D interleaved complex {@code float[][][]} array to a
     * {@code Complex[]} array. The third depth level of the array is assumed to
     * be interleaved.
     *
     * @param d 3D complex interleaved float array
     * @return 3D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][][] interleaved2Complex(float[][][] d) {
        return interleaved2Complex(d, 2);
    }

    // SPLIT METHODS

    /**
     * Converts a split complex array {@code double[] r, double[] i} to a
     * {@code Complex[]} array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] split2Complex(double[] real, double[] imag) {
        final int length = real.length;
        final Complex[] c = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = new Complex(real[n], imag[n]);
        }
        return c;
    }

    /**
     * Converts a 2D split complex array {@code double[][] r, double[][] i} to a
     * 2D {@code Complex[][]} array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return 2D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][] split2Complex(double[][] real, double[][] imag) {
        final int length = real.length;
        Complex[][] c = new Complex[length][];
        for (int x = 0; x < length; x++) {
            c[x] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    /**
     * Converts a 3D split complex array {@code double[][][] r, double[][][] i}
     * to a 3D {@code Complex[][][]} array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return 3D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][][] split2Complex(double[][][] real, double[][][] imag) {
        final int length = real.length;
        Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < length; x++) {
            c[x] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    /**
     * Converts a split complex array {@code float[] r, float[] i} to a
     * {@code Complex[]} array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[] split2Complex(float[] real, float[] imag) {
        final int length = real.length;
        final Complex[] c = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = new Complex(real[n], imag[n]);
        }
        return c;
    }

    /**
     * Converts a 2D split complex array {@code float[][] r, float[][] i} to a
     * 2D {@code Complex[][]} array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return 2D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][] split2Complex(float[][] real, float[][] imag) {
        final int length = real.length;
        Complex[][] c = new Complex[length][];
        for (int x = 0; x < length; x++) {
            c[x] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    /**
     * Converts a 3D split complex array {@code float[][][] r, float[][][] i} to
     * a 3D {@code Complex[][][]} array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return 3D {@code Complex} array
     *
     * @since 4.0
     */
    public static Complex[][][] split2Complex(float[][][] real, float[][][] imag) {
        final int length = real.length;
        Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < length; x++) {
            c[x] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    // MISC

    /**
     * Initializes a {@code Complex[]} array to zero, to avoid
     * NullPointerExceptions.
     *
     * @param c Complex array
     * @return c
     *
     * @since 4.0
     */
    public static Complex[] initialize(Complex[] c) {
        final int length = c.length;
        for (int x = 0; x < length; x++) {
            c[x] = Complex.ZERO;
        }
        return c;
    }

    /**
     * Initializes a {@code Complex[][]} array to zero, to avoid
     * NullPointerExceptions.
     *
     * @param c {@code Complex} array
     * @return c
     *
     * @since 4.0
     */
    public static Complex[][] initialize(Complex[][] c) {
        final int length = c.length;
        for (int x = 0; x < length; x++) {
            c[x] = initialize(c[x]);
        }
        return c;
    }

    /**
     * Initializes a {@code Complex[][][]} array to zero, to avoid
     * NullPointerExceptions.
     *
     * @param c {@code Complex} array
     * @return c
     *
     * @since 4.0
     */
    public static Complex[][][] initialize(Complex[][][] c) {
        final int length = c.length;
        for (int x = 0; x < length; x++) {
            c[x] = initialize(c[x]);
        }
        return c;
    }

    /**
     * Returns {@code double[]} containing absolute values (magnitudes) of a
     * {@code Complex[]} array.
     *
     * @param c {@code Complex} array
     * @return {@code double[]}
     *
     * @since 4.0
     */
    public static double[] abs(Complex[] c) {
        final int length = c.length;
        final double[] d = new double[length];
        for (int x = 0; x < length; x++) {
            d[x] = c[x].abs();
        }
        return d;
    }

    /**
     * Returns {@code double[]} containing arguments (phase angles) of a
     * {@code Complex[]} array.
     *
     * @param c {@code Complex} array
     * @return {@code double[]} array
     *
     * @since 4.0
     */
    public static double[] arg(Complex[] c) {
        final int length = c.length;
        final double[] d = new double[length];
        for (int x = 0; x < length; x++) {
            d[x] = c[x].getArgument();
        }
        return d;
    }

}
