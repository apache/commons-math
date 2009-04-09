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

package org.apache.commons.math.ode;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

import org.apache.commons.math.fraction.BigFraction;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;

/**
 * This class transforms state history between multistep (with or without
 * derivatives) and Nordsieck forms.
 * <p>
 * {@link MultistepIntegrator multistep integrators} use state history
 * from several previous steps to compute the current state. They may also use
 * the first derivative of current state. All states are separated by a fixed
 * step size h from each other. Since these methods are based on polynomial
 * interpolation, the information from the previous state may be represented
 * in another equivalent way: using the state higher order derivatives at
 * current step rather. This class transforms state history between these three
 * equivalent forms.
 * <p>
 * <p>
 * The supported forms for a dimension n history are:
 * <ul>
 *   <li>multistep without derivatives:<br/>
 *     <pre>
 *       y<sub>k</sub>, y<sub>k-1</sub> ... y<sub>k-(n-2), y<sub>k-(n-1)</sub>
 *     </pre>
 *   </li>
 *   <li>multistep with first derivative at current step:<br/>
 *     <pre>
 *       y<sub>k</sub>, y'<sub>k</sub>, y<sub>k-1</sub> ... y<sub>k-(n-2)</sub>
 *     </pre>
 *   </li>
 *   <li>Nordsieck:
 *     <pre>
 *       y<sub>k</sub>, h y'<sub>k</sub>, h<sup>2</sup>/2 y''<sub>k</sub> ... h<sup>n-1</sup>/(n-1)! yn-1<sub>k</sub>
 *     </pre>
 *   </li>
 * </ul> 
 * In these expressions, y<sub>k</sub> is the state at the current step. For each p,
 * y<sub>k-p</sub> is the state at the p<sup>th</sup> previous step. y'<sub>k</sub>,
 * y''<sub>k</sub> ... yn-1<sub>k</sub> are respectively the first, second, ...
 * (n-1)<sup>th</sup> derivatives of the state at current step and h is the fixed
 * step size.
 * </p>
 * <p>
 * The transforms are exact for polynomials.
 * </p>
 * <p>
 * In Nordsieck form, the state history can be converted from step size h to step
 * size h' by rescaling each component by 1, h'/h, (h'/h)<sup>2</sup> ...
 * (h'/h)<sup>n-1</sup>.
 * </p>
 * <p>
 * Instances of this class are guaranteed to be immutable.
 * </p>
 * @see org.apache.commons.math.ode.MultistepIntegrator
 * @see org.apache.commons.math.ode.nonstiff.AdamsBashforthIntegrator
 * @see org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class NordsieckTransformer implements Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -2707468304560314664L;

    /** Nordsieck to Multistep  without derivatives matrix. */
    private final RealMatrix matNtoMWD;
                                           
    /** Multistep without derivatives to Nordsieck matrix. */
    private final RealMatrix matMWDtoN;

    /** Nordsieck to Multistep matrix. */
    private final RealMatrix matNtoM;
                                           
    /** Multistep to Nordsieck matrix. */
    private final RealMatrix matMtoN;

    /**
     * Build a transformer for a specified order.
     * @param n dimension of the history
     */
    public NordsieckTransformer(final int n) {

        // from Nordsieck to multistep without derivatives
        final BigInteger[][] bigNtoMWD = buildNordsieckToMultistepWithoutDerivatives(n);
        double[][] dataNtoMWD = new double[n][n];
        for (int i = 0; i < n; ++i) {
            double[]     dRow = dataNtoMWD[i];
            BigInteger[] bRow = bigNtoMWD[i];
            for (int j = 0; j < n; ++j) {
                dRow[j] = bRow[j].doubleValue();
            }
        }
        matNtoMWD = new RealMatrixImpl(dataNtoMWD, false);

        // from multistep without derivatives to Nordsieck
        final BigFraction[][] bigToN = buildMultistepWithoutDerivativesToNordsieck(n);
        double[][] dataMWDtoN = new double[n][n];
        for (int i = 0; i < n; ++i) {
            double[]     dRow = dataMWDtoN[i];
            BigFraction[] bRow = bigToN[i];
            for (int j = 0; j < n; ++j) {
                dRow[j] = bRow[j].doubleValue();
            }
        }
        matMWDtoN = new RealMatrixImpl(dataMWDtoN, false);

        // from Nordsieck to multistep
        final BigInteger[][] bigNtoM = buildNordsieckToMultistep(n);
        double[][] dataNtoM = new double[n][n];
        for (int i = 0; i < n; ++i) {
            double[]     dRow = dataNtoM[i];
            BigInteger[] bRow = bigNtoM[i];
            for (int j = 0; j < n; ++j) {
                dRow[j] = bRow[j].doubleValue();
            }
        }
        matNtoM = new RealMatrixImpl(dataNtoM, false);

        // from multistep to Nordsieck
        convertMWDtNtoMtN(bigToN);
        double[][] dataMtoN = new double[n][n];
        for (int i = 0; i < n; ++i) {
            double[]     dRow = dataMtoN[i];
            BigFraction[] bRow = bigToN[i];
            for (int j = 0; j < n; ++j) {
                dRow[j] = bRow[j].doubleValue();
            }
        }
        matMtoN = new RealMatrixImpl(dataMtoN, false);

    }

    /**
     * Build the transform from Nordsieck to multistep without derivatives.
     * @param n dimension of the history
     * @return transform from Nordsieck to multistep without derivatives
     */
    public static BigInteger[][] buildNordsieckToMultistepWithoutDerivatives(final int n) {

        final BigInteger[][] array = new BigInteger[n][n];

        // row 0: [1 0 0 0 ... 0 ]
        array[0][0] = BigInteger.ONE;
        Arrays.fill(array[0], 1, n, BigInteger.ZERO);

        // the following expressions are direct applications of Taylor series
        // rows 1 to n-1: aij = (-i)^j
        // [ 1  -1   1  -1   1 ...]
        // [ 1  -2   4  -8  16 ...]
        // [ 1  -3   9 -27  81 ...]
        // [ 1  -4  16 -64 256 ...]
        for (int i = 1; i < n; ++i) {
            final BigInteger[] row  = array[i];
            final BigInteger factor = BigInteger.valueOf(-i);
            BigInteger aj = BigInteger.ONE;
            for (int j = 0; j < n; ++j) {
                row[j] = aj;
                aj = aj.multiply(factor);
            }
        }

        return array;

    }

    /**
     * Build the transform from multistep without derivatives to Nordsieck.
     * @param n dimension of the history
     * @return transform from multistep without derivatives to Nordsieck
     */
    public static BigFraction[][] buildMultistepWithoutDerivativesToNordsieck(final int n) {

        final BigInteger[][] iArray = new BigInteger[n][n];

        // row 0: [1 0 0 0 ... 0 ]
        iArray[0][0] = BigInteger.ONE;
        Arrays.fill(iArray[0], 1, n, BigInteger.ZERO);

        // We use recursive definitions of triangular integer series for each column.
        // For example column 0 of matrices of increasing dimensions are:
        //  1/0! for dimension 1
        //  1/1!,  1/1! for dimension 2
        //  2/2!,  3/2!,  1/2! for dimension 3
        //  6/3!, 11/3!,  6/3!,  1/3! for dimension 4
        // 24/4!, 50/4!, 35/4!, 10/4!, 1/4! for dimension 5
        // The numerators are the Stirling numbers of the first kind, (A008275 in
        // Sloane's encyclopedia http://www.research.att.com/~njas/sequences/A008275)
        // with a multiplicative factor of +/-1 (which we will write +/-binomial(n-1, 0)).
        // In the same way, column 1 is A049444 with a multiplicative factor of
        // +/-binomial(n-1, 1) and column 2 is A123319 with a multiplicative factor of
        // +/-binomial(n-1, 2). The next columns are defined by similar definitions but
        // are not identified in Sloane's encyclopedia.
        // Another interesting observation is that for each dimension k, the last column
        // (except the initial 0) is a copy of the first column of the dimension k-1 matrix,
        // possibly with an opposite sign (i.e. these columns are also linked to Stirling
        // numbers of the first kind).
        for (int i = 1; i < n; ++i) {

            final BigInteger bigI = BigInteger.valueOf(i);

            // row i
            BigInteger[] rowK   = iArray[i];
            BigInteger[] rowKm1 = iArray[i - 1];
            for (int j = 0; j < i; ++j) {
                rowK[j] = BigInteger.ONE;
            }
            rowK[i] = rowKm1[0];

            // rows i-1 to 1
            for (int k = i - 1; k > 0; --k) {

                // select rows
                rowK   = rowKm1;
                rowKm1 = iArray[k - 1];

                // apply recursive defining formula
                for (int j = 0; j < i; ++j) {
                    rowK[j] = rowK[j].multiply(bigI).add(rowKm1[j]);
                }

                // initialize new last column
                rowK[i] = rowKm1[0];

            }
            rowKm1[0] = rowKm1[0].multiply(bigI);

        }

        // apply column specific factors
        final BigInteger factorial = iArray[0][0];
        final BigFraction[][] fArray = new BigFraction[n][n];
        for (int i = 0; i < n; ++i) {
            final BigFraction[] fRow = fArray[i];
            final BigInteger[]  iRow = iArray[i];
            BigInteger binomial = BigInteger.ONE;
            for (int j = 0; j < n; ++j) {
                fRow[j] = new BigFraction(binomial.multiply(iRow[j]), factorial);
                binomial = binomial.negate().multiply(BigInteger.valueOf(n - j - 1)).divide(BigInteger.valueOf(j + 1));
            }
        }

        return fArray;

    }

    /**
     * Build the transform from Nordsieck to multistep.
     * @param n dimension of the history
     * @return transform from Nordsieck to multistep
     */
    public static BigInteger[][] buildNordsieckToMultistep(final int n) {

        final BigInteger[][] array = new BigInteger[n][n];

        // row 0: [1 0 0 0 ... 0 ]
        array[0][0] = BigInteger.ONE;
        Arrays.fill(array[0], 1, n, BigInteger.ZERO);

        if (n > 1) {

            // row 1: [0 1 0 0 ... 0 ]
            array[1][0] = BigInteger.ZERO;
            array[1][1] = BigInteger.ONE;
            Arrays.fill(array[1], 2, n, BigInteger.ZERO);

            // the following expressions are direct applications of Taylor series
            // rows 2 to n-1: aij = (1-i)^j
            // [ 1  -1   1  -1   1 ...]
            // [ 1  -2   4  -8  16 ...]
            // [ 1  -3   9 -27  81 ...]
            // [ 1  -4  16 -64 256 ...]
            for (int i = 2; i < n; ++i) {
                final BigInteger[] row  = array[i];
                final BigInteger factor = BigInteger.valueOf(1 - i);
                BigInteger aj = BigInteger.ONE;
                for (int j = 0; j < n; ++j) {
                    row[j] = aj;
                    aj = aj.multiply(factor);
                }
            }

        }

        return array;

    }

    /**
     * Build the transform from multistep to Nordsieck.
     * @param n dimension of the history
     * @return transform from multistep to Nordsieck
     */
    public static BigFraction[][] buildMultistepToNordsieck(final int n) {
        final BigFraction[][] array = buildMultistepWithoutDerivativesToNordsieck(n);
        convertMWDtNtoMtN(array);
        return array;
    }

    /**
     * Convert a transform from multistep without derivatives to Nordsieck to
     * multistep to Nordsieck.
     * @param work array, contains tansform from multistep without derivatives
     * to Nordsieck on input, will be overwritten with tansform from multistep
     * to Nordsieck on output
     */
    private static void convertMWDtNtoMtN(BigFraction[][] array) {

        final int n = array.length;
        if (n == 1) {
            return;
        }

        // the second row of the matrix without derivatives represents the linear equation:
        // hy' = a0 yk + a1 yk-1 + ... + a(n-1) yk-(n-1)
        // we solve it with respect to the oldest state yk-(n-1) and get
        // yk-(n-1) = -a0/a(n-1) yk + 1/a(n-1) hy' - a1/a(n-1) yk-1 - ...
        final BigFraction[] secondRow = array[1];
        final BigFraction[] solved    = new BigFraction[n];
        final BigFraction f = secondRow[n - 1].reciprocal().negate();
        solved[0] = secondRow[0].multiply(f);
        solved[1] = f.negate();
        for (int j = 2; j < n; ++j) {
            solved[j] = secondRow[j - 1].multiply(f);
        }

        // update the matrix so it expects hy' in second element
        // rather than yk-(n-1) in last elements when post-multiplied
        for (int i = 0; i < n; ++i) {
            final BigFraction[] rowI = array[i];
            final BigFraction last = rowI[n - 1];
            for (int j = n - 1; j > 1; --j) {
                rowI[j] = rowI[j - 1].add(last.multiply(solved[j]));
            }
            rowI[1] = last.multiply(solved[1]);
            rowI[0] = rowI[0].add(last.multiply(solved[0]));
        }

    }

    /**
     * Transform a scalar state history from multistep form to Nordsieck form.
     * <p>
     * The input state history must be in multistep form with element 0 for
     * current state, element 1 for current state scaled first derivative, element
     * 2 for previous state ... element n-1 for (n-2)<sup>th</sup> previous state.
     * The output state history will be in Nordsieck form with element 0 for
     * current state, element 1 for current state scaled first derivative, element
     * 2 for current state scaled second derivative ... element n-1 for current state
     * scaled (n-1)<sup>th</sup> derivative.
     * </p>
     * @param multistepHistory scalar state history in multistep form
     * @return scalar state history in Nordsieck form
     */
    public double[] multistepToNordsieck(final double[] multistepHistory) {
        return matMtoN.operate(multistepHistory);
    }

    /**
     * Transform a vectorial state history from multistep form to Nordsieck form.
     * <p>
     * The input state history must be in multistep form with row 0 for
     * current state, row 1 for current state scaled first derivative, row
     * 2 for previous state ... row n-1 for (n-2)<sup>th</sup> previous state.
     * The output state history will be in Nordsieck form with row 0 for
     * current state, row 1 for current state scaled first derivative, row
     * 2 for current state scaled second derivative ... row n-1 for current state
     * scaled (n-1)<sup>th</sup> derivative.
     * </p>
     * @param multistepHistory vectorial state history in multistep form
     * @return vectorial state history in Nordsieck form
     */
    public RealMatrix multistepToNordsieck(final RealMatrix multistepHistory) {
        return matMtoN.multiply(multistepHistory);
    }

    /**
     * Transform a scalar state history from Nordsieck form to multistep form.
     * <p>
     * The input state history must be in Nordsieck form with element 0 for
     * current state, element 1 for current state scaled first derivative, element
     * 2 for current state scaled second derivative ... element n-1 for current state
     * scaled (n-1)<sup>th</sup> derivative.
     * The output state history will be in multistep form with element 0 for
     * current state, element 1 for current state scaled first derivative, element
     * 2 for previous state ... element n-1 for (n-2)<sup>th</sup> previous state.
     * </p>
     * @param nordsieckHistory scalar state history in Nordsieck form
     * @return scalar state history in multistep form
     */
    public double[] nordsieckToMultistep(final double[] nordsieckHistory) {
        return matNtoM.operate(nordsieckHistory);
    }

    /**
     * Transform a vectorial state history from Nordsieck form to multistep form.
     * <p>
     * The input state history must be in Nordsieck form with row 0 for
     * current state, row 1 for current state scaled first derivative, row
     * 2 for current state scaled second derivative ... row n-1 for current state
     * scaled (n-1)<sup>th</sup> derivative.
     * The output state history will be in multistep form with row 0 for
     * current state, row 1 for current state scaled first derivative, row
     * 2 for previous state ... row n-1 for (n-2)<sup>th</sup> previous state.
     * </p>
     * @param nordsieckHistory vectorial state history in Nordsieck form
     * @return vectorial state history in multistep form
     */
    public RealMatrix nordsieckToMultistep(final RealMatrix nordsieckHistory) {
        return matNtoM.multiply(nordsieckHistory);
    }

    /**
     * Transform a scalar state history from multistep without derivatives form
     * to Nordsieck form.
     * <p>
     * The input state history must be in multistep without derivatives form with
     * element 0 for current state, element 1 for previous state ... element n-1
     * for (n-1)<sup>th</sup> previous state.
     * The output state history will be in Nordsieck form with element 0 for
     * current state, element 1 for current state scaled first derivative, element
     * 2 for current state scaled second derivative ... element n-1 for current state
     * scaled (n-1)<sup>th</sup> derivative.
     * </p>
     * @param mwdHistory scalar state history in multistep without derivatives form
     * @return scalar state history in Nordsieck form
     */
    public double[] multistepWithoutDerivativesToNordsieck(final double[] mwdHistory) {
        return matMWDtoN.operate(mwdHistory);
    }

    /**
     * Transform a vectorial state history from multistep without derivatives form
     * to Nordsieck form.
     * <p>
     * The input state history must be in multistep without derivatives form with
     * row 0 for current state, row 1 for previous state ... row n-1
     * for (n-1)<sup>th</sup> previous state.
     * The output state history will be in Nordsieck form with row 0 for
     * current state, row 1 for current state scaled first derivative, row
     * 2 for current state scaled second derivative ... row n-1 for current state
     * scaled (n-1)<sup>th</sup> derivative.
     * </p>
     * @param mwdHistory vectorial state history in multistep without derivatives form
     * @return vectorial state history in Nordsieck form
     */
    public RealMatrix multistepWithoutDerivativesToNordsieck(final RealMatrix mwdHistory) {
        return matMWDtoN.multiply(mwdHistory);
    }

    /**
     * Transform a scalar state history from Nordsieck form to multistep without
     * derivatives form.
     * <p>
     * The input state history must be in Nordsieck form with element 0 for
     * current state, element 1 for current state scaled first derivative, element
     * 2 for current state scaled second derivative ... element n-1 for current state
     * scaled (n-1)<sup>th</sup> derivative.
     * The output state history will be in multistep without derivatives form with
     * element 0 for current state, element 1 for previous state ... element n-1
     * for (n-1)<sup>th</sup> previous state.
     * </p>
     * @param nordsieckHistory scalar state history in Nordsieck form
     * @return scalar state history in multistep without derivatives form
     */
    public double[] nordsieckToMultistepWithoutDerivatives(final double[] nordsieckHistory) {
        return matNtoMWD.operate(nordsieckHistory);
    }

    /**
     * Transform a vectorial state history from Nordsieck form to multistep without
     * derivatives form.
     * <p>
     * The input state history must be in Nordsieck form with row 0 for
     * current state, row 1 for current state scaled first derivative, row
     * 2 for current state scaled second derivative ... row n-1 for current state
     * scaled (n-1)<sup>th</sup> derivative.
     * The output state history will be in multistep without derivatives form with
     * row 0 for current state, row 1 for previous state ... row n-1
     * for (n-1)<sup>th</sup> previous state.
     * </p>
     * @param nordsieckHistory vectorial state history in Nordsieck form
     * @return vectorial state history in multistep without derivatives form
     */
    public RealMatrix nordsieckToMultistepWithoutDerivatives(final RealMatrix nordsieckHistory) {
        return matNtoMWD.multiply(nordsieckHistory);
    }

}
