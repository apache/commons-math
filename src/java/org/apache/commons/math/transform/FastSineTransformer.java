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
package org.apache.commons.math.transform;

import org.apache.commons.math.analysis.*;
import org.apache.commons.math.complex.*;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;

/**
 * Implements the <a href="http://documents.wolfram.com/v5/Add-onsLinks/
 * StandardPackages/LinearAlgebra/FourierTrig.html">Fast Sine Transform</a>
 * for transformation of one-dimensional data sets. For reference, see
 * <b>Fast Fourier Transforms</b>, ISBN 0849371635, chapter 3.
 * <p>
 * FST is its own inverse, up to a multiplier depending on conventions.
 * The equations are listed in the comments of the corresponding methods.</p>
 * <p>
 * Similar to FFT, we also require the length of data set to be power of 2.
 * In addition, the first element must be 0 and it's enforced in function
 * transformation after sampling.</p>
 *
 * @version $Revision$ $Date$
 * @since 1.2
 */
public class FastSineTransformer implements RealTransformer {

    /** serializable version identifier */
    private static final long serialVersionUID = -7557024407476823001L;

    /**
     * Construct a default transformer.
     */
    public FastSineTransformer() {
        super();
    }

    /**
     * Transform the given real data set.
     * <p>
     * The formula is F<sub>n</sub> = &sum;<sub>k=0</sub><sup>N-1</sup> f<sub>k</sub> sin(&pi; nk/N)
     * </p>
     * 
     * @param f the real data array to be transformed
     * @return the real transformed array
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double[] transform(double f[])
        throws IllegalArgumentException {
        return fst(f);
    }

    /**
     * Transform the given real function, sampled on the given interval.
     * <p>
     * The formula is F<sub>n</sub> = &sum;<sub>k=0</sub><sup>N-1</sup> f<sub>k</sub> sin(&pi; nk/N)
     * </p>
     * 
     * @param f the function to be sampled and transformed
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the number of sample points
     * @return the real transformed array
     * @throws FunctionEvaluationException if function cannot be evaluated
     * at some point
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double[] transform(UnivariateRealFunction f,
                              double min, double max, int n)
        throws FunctionEvaluationException, IllegalArgumentException {

        double data[] = FastFourierTransformer.sample(f, min, max, n);
        data[0] = 0.0;
        return fst(data);
    }

    /**
     * Transform the given real data set.
     * <p>
     * The formula is F<sub>n</sub> = &radic;(2/N) &sum;<sub>k=0</sub><sup>N-1</sup> f<sub>k</sub> sin(&pi; nk/N)
     * </p>
     * 
     * @param f the real data array to be transformed
     * @return the real transformed array
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double[] transform2(double f[]) throws IllegalArgumentException {

        double scaling_coefficient = Math.sqrt(2.0 / f.length);
        return FastFourierTransformer.scaleArray(fst(f), scaling_coefficient);
    }

    /**
     * Transform the given real function, sampled on the given interval.
     * <p>
     * The formula is F<sub>n</sub> = &radic;(2/N) &sum;<sub>k=0</sub><sup>N-1</sup> f<sub>k</sub> sin(&pi; nk/N)
     * </p>
     * 
     * @param f the function to be sampled and transformed
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the number of sample points
     * @return the real transformed array
     * @throws FunctionEvaluationException if function cannot be evaluated
     * at some point
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double[] transform2(
        UnivariateRealFunction f, double min, double max, int n)
        throws FunctionEvaluationException, IllegalArgumentException {

        double data[] = FastFourierTransformer.sample(f, min, max, n);
        data[0] = 0.0;
        double scaling_coefficient = Math.sqrt(2.0 / n);
        return FastFourierTransformer.scaleArray(fst(data), scaling_coefficient);
    }

    /**
     * Inversely transform the given real data set.
     * <p>
     * The formula is f<sub>k</sub> = (2/N) &sum;<sub>n=0</sub><sup>N-1</sup> F<sub>n</sub> sin(&pi; nk/N)
     * </p>
     * 
     * @param f the real data array to be inversely transformed
     * @return the real inversely transformed array
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double[] inversetransform(double f[]) throws IllegalArgumentException {

        double scaling_coefficient = 2.0 / f.length;
        return FastFourierTransformer.scaleArray(fst(f), scaling_coefficient);
    }

    /**
     * Inversely transform the given real function, sampled on the given interval.
     * <p>
     * The formula is f<sub>k</sub> = (2/N) &sum;<sub>n=0</sub><sup>N-1</sup> F<sub>n</sub> sin(&pi; nk/N)
     * </p>
     * 
     * @param f the function to be sampled and inversely transformed
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the number of sample points
     * @return the real inversely transformed array
     * @throws FunctionEvaluationException if function cannot be evaluated
     * at some point
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double[] inversetransform(UnivariateRealFunction f, double min, double max, int n)
        throws FunctionEvaluationException, IllegalArgumentException {

        double data[] = FastFourierTransformer.sample(f, min, max, n);
        data[0] = 0.0;
        double scaling_coefficient = 2.0 / n;
        return FastFourierTransformer.scaleArray(fst(data), scaling_coefficient);
    }

    /**
     * Inversely transform the given real data set.
     * <p>
     * The formula is f<sub>k</sub> = &radic;(2/N) &sum;<sub>n=0</sub><sup>N-1</sup> F<sub>n</sub> sin(&pi; nk/N)
     * </p>
     * 
     * @param f the real data array to be inversely transformed
     * @return the real inversely transformed array
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double[] inversetransform2(double f[]) throws IllegalArgumentException {

        return transform2(f);
    }

    /**
     * Inversely transform the given real function, sampled on the given interval.
     * <p>
     * The formula is f<sub>k</sub> = &radic;(2/N) &sum;<sub>n=0</sub><sup>N-1</sup> F<sub>n</sub> sin(&pi; nk/N)
     * </p>
     * 
     * @param f the function to be sampled and inversely transformed
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the number of sample points
     * @return the real inversely transformed array
     * @throws FunctionEvaluationException if function cannot be evaluated
     * at some point
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double[] inversetransform2(UnivariateRealFunction f, double min, double max, int n)
        throws FunctionEvaluationException, IllegalArgumentException {

        return transform2(f, min, max, n);
    }

    /**
     * Perform the FST algorithm (including inverse).
     *
     * @param f the real data array to be transformed
     * @return the real transformed array
     * @throws IllegalArgumentException if any parameters are invalid
     */
    protected double[] fst(double f[]) throws IllegalArgumentException {

        double A, B, x[], F[] = new double[f.length];

        FastFourierTransformer.verifyDataSet(f);
        if (f[0] != 0.0) {
            throw MathRuntimeException.createIllegalArgumentException("first element is not 0: {0}",
                                                                      new Object[] { f[0] });
        }
        int N = f.length;
        if (N == 1) {       // trivial case
            F[0] = 0.0;
            return F;
        }

        // construct a new array and perform FFT on it
        x = new double[N];
        x[0] = 0.0;
        x[N >> 1] = 2.0 * f[N >> 1];
        for (int i = 1; i < (N >> 1); i++) {
            A = Math.sin(i * Math.PI / N) * (f[i] + f[N-i]);
            B = 0.5 * (f[i] - f[N-i]);
            x[i] = A + B;
            x[N-i] = A - B;
        }
        FastFourierTransformer transformer = new FastFourierTransformer();
        Complex y[] = transformer.transform(x);

        // reconstruct the FST result for the original array
        F[0] = 0.0;
        F[1] = 0.5 * y[0].getReal();
        for (int i = 1; i < (N >> 1); i++) {
            F[2*i] = -y[i].getImaginary();
            F[2*i+1] = y[i].getReal() + F[2*i-1];
        }

        return F;
    }
}
