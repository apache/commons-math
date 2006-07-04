/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.transform;

import java.io.Serializable;
import org.apache.commons.math.analysis.*;
import org.apache.commons.math.complex.*;
import org.apache.commons.math.MathException;

/**
 * Implements the <a href="http://mathworld.wolfram.com/FastFourierTransform.html">
 * Fast Fourier Transform</a> for transformation of one-dimensional data sets.
 * For reference, see <b>Applied Numerical Linear Algebra</b>, ISBN 0898713897,
 * chapter 6.
 * <p>
 * There are several conventions for the definition of FFT and inverse FFT,
 * mainly on different coefficient and exponent. Here the equations are listed
 * in the comments of the corresponding methods.
 * <p>
 * We require the length of data set to be power of 2, this greatly simplifies
 * and speeds up the code. Users can pad the data with zeros to meet this
 * requirement. There are other flavors of FFT, for reference, see S. Winograd,
 * <i>On computing the discrete Fourier transform</i>, Mathematics of Computation,
 * 32 (1978), 175 - 199.
 *
 * @version $Revision$ $Date$
 */
public class FastFourierTransformer implements Serializable {

    /** serializable version identifier */
    static final long serialVersionUID = 5138259215438106000L;

    /** array of the roots of unity */
    private Complex omega[] = new Complex[0];

    /**
     * |omegaCount| is the length of lasted computed omega[]. omegaCount
     * is positive for forward transform and negative for inverse transform.
     */
    private int omegaCount = 0;

    /**
     * Construct a default transformer.
     */
    FastFourierTransformer() {
        super();
    }

    /**
     * Transform the given real data set.
     * <p>
     * The formula is $ y_n = \Sigma_{k=0}^{N-1} e^{-2 \pi i nk/N} x_k $
     *
     * @param f the real data array to be transformed
     * @return the complex transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] transform(double f[]) throws MathException,
        IllegalArgumentException {

        return fft(f, false);
    }

    /**
     * Transform the given real function, sampled on the given interval.
     * <p>
     * The formula is $ y_n = \Sigma_{k=0}^{N-1} e^{-2 \pi i nk/N} x_k $
     *
     * @param f the function to be sampled and transformed
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the number of sample points
     * @return the complex transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] transform(
        UnivariateRealFunction f, double min, double max, int n)
        throws MathException, IllegalArgumentException {

        double data[] = sample(f, min, max, n);
        return fft(data, false);
    }

    /**
     * Transform the given complex data set.
     * <p>
     * The formula is $ y_n = \Sigma_{k=0}^{N-1} e^{-2 \pi i nk/N} x_k $
     *
     * @param f the complex data array to be transformed
     * @return the complex transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] transform(Complex f[]) throws MathException,
        IllegalArgumentException {

        computeOmega(f.length);
        return fft(f);
    }

    /**
     * Transform the given real data set.
     * <p>
     * The formula is $y_n = (1/\sqrt{N}) \Sigma_{k=0}^{N-1} e^{-2 \pi i nk/N} x_k$
     *
     * @param f the real data array to be transformed
     * @return the complex transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] transform2(double f[]) throws MathException,
        IllegalArgumentException {

        double scaling_coefficient = 1.0 / Math.sqrt(f.length);
        return scaleArray(fft(f, false), scaling_coefficient);
    }

    /**
     * Transform the given real function, sampled on the given interval.
     * <p>
     * The formula is $y_n = (1/\sqrt{N}) \Sigma_{k=0}^{N-1} e^{-2 \pi i nk/N} x_k$
     *
     * @param f the function to be sampled and transformed
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the number of sample points
     * @return the complex transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] transform2(
        UnivariateRealFunction f, double min, double max, int n)
        throws MathException, IllegalArgumentException {

        double data[] = sample(f, min, max, n);
        double scaling_coefficient = 1.0 / Math.sqrt(n);
        return scaleArray(fft(data, false), scaling_coefficient);
    }

    /**
     * Transform the given complex data set.
     * <p>
     * The formula is $y_n = (1/\sqrt{N}) \Sigma_{k=0}^{N-1} e^{-2 \pi i nk/N} x_k$
     *
     * @param f the complex data array to be transformed
     * @return the complex transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] transform2(Complex f[]) throws MathException,
        IllegalArgumentException {

        computeOmega(f.length);
        double scaling_coefficient = 1.0 / Math.sqrt(f.length);
        return scaleArray(fft(f), scaling_coefficient);
    }

    /**
     * Inversely transform the given real data set.
     * <p>
     * The formula is $ x_k = (1/N) \Sigma_{n=0}^{N-1} e^{2 \pi i nk/N} y_n $
     *
     * @param f the real data array to be inversely transformed
     * @return the complex inversely transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] inversetransform(double f[]) throws MathException,
        IllegalArgumentException {

        double scaling_coefficient = 1.0 / f.length;
        return scaleArray(fft(f, true), scaling_coefficient);
    }

    /**
     * Inversely transform the given real function, sampled on the given interval.
     * <p>
     * The formula is $ x_k = (1/N) \Sigma_{n=0}^{N-1} e^{2 \pi i nk/N} y_n $
     *
     * @param f the function to be sampled and inversely transformed
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the number of sample points
     * @return the complex inversely transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] inversetransform(
        UnivariateRealFunction f, double min, double max, int n)
        throws MathException, IllegalArgumentException {

        double data[] = sample(f, min, max, n);
        double scaling_coefficient = 1.0 / n;
        return scaleArray(fft(data, true), scaling_coefficient);
    }

    /**
     * Inversely transform the given complex data set.
     * <p>
     * The formula is $ x_k = (1/N) \Sigma_{n=0}^{N-1} e^{2 \pi i nk/N} y_n $
     *
     * @param f the complex data array to be inversely transformed
     * @return the complex inversely transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] inversetransform(Complex f[]) throws MathException,
        IllegalArgumentException {

        computeOmega(-f.length);    // pass negative argument
        double scaling_coefficient = 1.0 / f.length;
        return scaleArray(fft(f), scaling_coefficient);
    }

    /**
     * Inversely transform the given real data set.
     * <p>
     * The formula is $x_k = (1/\sqrt{N}) \Sigma_{n=0}^{N-1} e^{2 \pi i nk/N} y_n$
     *
     * @param f the real data array to be inversely transformed
     * @return the complex inversely transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] inversetransform2(double f[]) throws MathException,
        IllegalArgumentException {

        double scaling_coefficient = 1.0 / Math.sqrt(f.length);
        return scaleArray(fft(f, true), scaling_coefficient);
    }

    /**
     * Inversely transform the given real function, sampled on the given interval.
     * <p>
     * The formula is $x_k = (1/\sqrt{N}) \Sigma_{n=0}^{N-1} e^{2 \pi i nk/N} y_n$
     *
     * @param f the function to be sampled and inversely transformed
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the number of sample points
     * @return the complex inversely transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] inversetransform2(
        UnivariateRealFunction f, double min, double max, int n)
        throws MathException, IllegalArgumentException {

        double data[] = sample(f, min, max, n);
        double scaling_coefficient = 1.0 / Math.sqrt(n);
        return scaleArray(fft(data, true), scaling_coefficient);
    }

    /**
     * Inversely transform the given complex data set.
     * <p>
     * The formula is $x_k = (1/\sqrt{N}) \Sigma_{n=0}^{N-1} e^{2 \pi i nk/N} y_n$
     *
     * @param f the complex data array to be inversely transformed
     * @return the complex inversely transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public Complex[] inversetransform2(Complex f[]) throws MathException,
        IllegalArgumentException {

        computeOmega(-f.length);    // pass negative argument
        double scaling_coefficient = 1.0 / Math.sqrt(f.length);
        return scaleArray(fft(f), scaling_coefficient);
    }

    /**
     * Perform the base-4 Cooley-Tukey FFT algorithm (including inverse).
     *
     * @param f the real data array to be transformed
     * @param isInverse the indicator of forward or inverse transform
     * @return the complex transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    protected Complex[] fft(double f[], boolean isInverse) throws
        MathException, IllegalArgumentException {

        verifyDataSet(f);
        Complex F[] = new Complex[f.length];
        if (f.length == 1) {
            F[0] = new Complex(f[0], 0.0);
            return F;
        }

        // Rather than the naive real to complex conversion, pack 2N
        // real numbers into N complex numbers for better performance.
        int N = f.length >> 1;
        Complex c[] = new Complex[N];
        for (int i = 0; i < N; i++) {
            c[i] = new Complex(f[2*i], f[2*i+1]);
        }
        computeOmega(isInverse ? -N : N);
        Complex z[] = fft(c);

        // reconstruct the FFT result for the original array
        computeOmega(isInverse ? -2*N : 2*N);
        F[0] = new Complex(2 * (z[0].getReal() + z[0].getImaginary()), 0.0);
        F[N] = new Complex(2 * (z[0].getReal() - z[0].getImaginary()), 0.0);
        for (int i = 1; i < N; i++) {
            Complex A = z[N-i].conjugate();
            Complex B = z[i].add(A);
            Complex C = z[i].subtract(A);
            Complex D = omega[i].multiply(Complex.I);
            F[i] = B.subtract(C.multiply(D));
            F[2*N-i] = F[i].conjugate();
        }

        return scaleArray(F, 0.5);
    }

    /**
     * Perform the base-4 Cooley-Tukey FFT algorithm (including inverse).
     *
     * @param data the complex data array to be transformed
     * @return the complex transformed array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    protected Complex[] fft(Complex data[]) throws MathException,
        IllegalArgumentException {

        int i, j, k, m, N = data.length;
        Complex A, B, C, D, E, F, z, f[] = new Complex[N];

        // initial simple cases
        verifyDataSet(data);
        if (N == 1) {
            f[0] = data[0];
            return f;
        }
        if (N == 2) {
            f[0] = data[0].add(data[1]);
            f[1] = data[0].subtract(data[1]);
            return f;
        }

        // permute original data array in bit-reversal order
        j = 0;
        for (i = 0; i < N; i++) {
            f[i] = data[j];
            k = N >> 1;
            while (j >= k && k > 0) {
                j -= k; k >>= 1;
            }
            j += k;
        }

        // the bottom base-4 round
        for (i = 0; i < N; i += 4) {
            A = f[i].add(f[i+1]);
            B = f[i+2].add(f[i+3]);
            C = f[i].subtract(f[i+1]);
            D = f[i+2].subtract(f[i+3]);
            E = C.add(D.multiply(Complex.I));
            F = C.subtract(D.multiply(Complex.I));
            f[i] = A.add(B);
            f[i+2] = A.subtract(B);
            // omegaCount indicates forward or inverse transform
            f[i+1] = omegaCount < 0 ? E : F;
            f[i+3] = omegaCount > 0 ? E : F;
        }

        // iterations from bottom to top take O(N*logN) time
        for (i = 4; i < N; i <<= 1) {
            m = N / (i<<1);
            for (j = 0; j < N; j += i<<1) {
                for (k = 0; k < i; k++) {
                    z = f[i+j+k].multiply(omega[k*m]);
                    f[i+j+k] = f[j+k].subtract(z);
                    f[j+k] = f[j+k].add(z);
                }
            }
        }
        return f;
    }

    /**
     * Calculate the n-th roots of unity.
     * <p>
     * The computed omega[] = { 1, w, w^2, ... w^(n-1) } where
     * w = exp(-2 \pi i / n), i = sqrt(-1). Note n is positive for
     * forward transform and negative for inverse transform.
     * 
     * @param n the integer passed in
     * @throws IllegalArgumentException if n = 0
     */
    protected void computeOmega(int n) throws IllegalArgumentException {
        if (n == 0) {
            throw new IllegalArgumentException
                ("Cannot compute 0-th root of unity, indefinite result.");
        }
        // avoid repetitive calculations
        if (n == omegaCount) { return; }
        if (n + omegaCount == 0) {
            for (int i = 0; i < Math.abs(omegaCount); i++) {
                omega[i] = omega[i].conjugate();
            }
            omegaCount = n;
            return;
        }
        // calculate everything from scratch
        omega = new Complex[Math.abs(n)];
        double t = 2.0 * Math.PI / n;
        double cost = Math.cos(t);
        double sint = Math.sin(t);
        omega[0] = new Complex(1.0, 0.0);
        for (int i = 1; i < Math.abs(n); i++) {
            omega[i] = new Complex(
                omega[i-1].getReal() * cost + omega[i-1].getImaginary() * sint,
                omega[i-1].getImaginary() * cost - omega[i-1].getReal() * sint);
        }
        omegaCount = n;
    }

    /**
     * Sample the given univariate real function on the given interval.
     * <p>
     * The interval is divided equally into N sections and sample points
     * are taken from min to max-(max-min)/N. Usually f(x) is periodic
     * such that f(min) = f(max) (note max is not sampled), but we don't
     * require that.
     *
     * @param f the function to be sampled
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the number of sample points
     * @return the samples array
     * @throws MathException if any math-related errors occur
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public static double[] sample(
        UnivariateRealFunction f, double min, double max, int n)
        throws MathException, IllegalArgumentException {

        if (n <= 0) {
            throw new IllegalArgumentException("Number of samples not positive.");
        }
        verifyInterval(min, max);

        double s[] = new double[n];
        double h = (max - min) / n;
        for (int i = 0; i < n; i++) {
            s[i] = f.value(min + i * h);
        }
        return s;
    }

    /**
     * Multiply every component in the given real array by the
     * given real number. The change is made in place.
     *
     * @param f the real array to be scaled
     * @param d the real scaling coefficient
     * @return a reference to the scaled array
     */
    public static double[] scaleArray(double f[], double d) {
        for (int i = 0; i < f.length; i++) {
            f[i] *= d;
        }
        return f;
    }

    /**
     * Multiply every component in the given complex array by the
     * given real number. The change is made in place.
     *
     * @param f the complex array to be scaled
     * @param d the real scaling coefficient
     * @return a reference to the scaled array
     */
    public static Complex[] scaleArray(Complex f[], double d) {
        for (int i = 0; i < f.length; i++) {
            f[i] = new Complex(d * f[i].getReal(), d * f[i].getImaginary());
        }
        return f;
    }

    /**
     * Returns true if the argument is power of 2.
     * 
     * @param n the number to test
     * @return true if the argument is power of 2
     */
    public static boolean isPowerOf2(long n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }

    /**
     * Verifies that the data set has length of power of 2.
     * 
     * @param d the data array
     * @throws IllegalArgumentException if array length is not power of 2
     */
    public static void verifyDataSet(double d[]) throws IllegalArgumentException {
        if (!isPowerOf2(d.length)) {
            throw new IllegalArgumentException
                ("Number of samples not power of 2, consider padding for fix.");
        }       
    }

    /**
     * Verifies that the data set has length of power of 2.
     * 
     * @param o the data array
     * @throws IllegalArgumentException if array length is not power of 2
     */
    public static void verifyDataSet(Object o[]) throws IllegalArgumentException {
        if (!isPowerOf2(o.length)) {
            throw new IllegalArgumentException
                ("Number of samples not power of 2, consider padding for fix.");
        }       
    }

    /**
     * Verifies that the endpoints specify an interval.
     * 
     * @param lower lower endpoint
     * @param upper upper endpoint
     * @throws IllegalArgumentException if not interval
     */
    public static void verifyInterval(double lower, double upper) throws
        IllegalArgumentException {

        if (lower >= upper) {
            throw new IllegalArgumentException
                ("Endpoints do not specify an interval: [" + lower +
                ", " + upper + "]");
        }       
    }
}
