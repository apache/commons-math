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

import org.apache.commons.math.fraction.BigFraction;
import org.apache.commons.math.linear.DefaultFieldMatrixPreservingVisitor;
import org.apache.commons.math.linear.FieldMatrix;
import org.apache.commons.math.linear.FieldMatrixImpl;
import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;
import org.apache.commons.math.linear.decomposition.FieldDecompositionSolver;
import org.apache.commons.math.linear.decomposition.FieldLUDecompositionImpl;

/**
 * This class transforms state history between multistep (with or without
 * derivatives) and Nordsieck forms.
 * <p>
 * {@link MultistepIntegrator multistep integrators} use state and state
 * derivative history from several previous steps to compute the current state.
 * All states are separated by a fixed step size h from each other. Since these
 * methods are based on polynomial interpolation, the information from the
 * previous states may be represented in another equivalent way: using the state
 * higher order derivatives at current step only. This class transforms state
 * history between these equivalent forms.
 * </p>
 * <p>
 * The general multistep form for a dimension n state history at step k is
 * composed of q-p previous states followed by s-r previous scaled derivatives
 * with n = (q-p) + (s-r):
 * <pre>
 *   y<sub>k-p</sub>, y<sub>k-(p+1)</sub> ... y<sub>k-(q-1)</sub>
 *   h y'<sub>k-r</sub>, h y'<sub>k-(r+1)</sub> ... h y'<sub>k-(s-1)</sub>
 * </pre>
 * As an example, the {@link
 * org.apache.commons.math.ode.nonstiff.AdamsBashforthIntegrator Adams-Bashforth}
 * integrator uses p=1, q=2, r=1, s=n. The {@link
 * org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator Adams-Moulton}
 * integrator uses p=1, q=2, r=0, s=n-1. The {@link
 * org.apache.commons.math.ode.stiff.BDFIntegrator BDF} integrator uses p=1, q=n,
 * r=0, s=1.
 * </p>
 * <p>
 * The Nordsieck form for a dimension n state history at step k is composed of the
 * current state followed by n-1 current scaled derivatives:
 * <pre>
 * y<sub>k</sub>
 * h y'<sub>k</sub>, h<sup>2</sup>/2 y''<sub>k</sub> ... h<sup>n-1</sup>/(n-1)! yn-1<sub>k</sub>
 * </pre>
 * Where y'<sub>k</sub>, y''<sub>k</sub> ... yn-1<sub>k</sub> are respectively the
 * first, second, ... (n-1)<sup>th</sup> derivatives of the state at current step
 * and h is the fixed step size.
 * </p>
 * <p>
 * In Nordsieck form, the state history can be converted from step size h to step
 * size h' by scaling each component by 1, h'/h, (h'/h)<sup>2</sup> ...
 * (h'/h)<sup>n-1</sup>.
 * </p>
 * <p>
 * The transform between general multistep and Nordsieck forms is exact for
 * polynomials.
 * </p>
 * <p>
 * Instances of this class are guaranteed to be immutable.
 * </p>
 * @see org.apache.commons.math.ode.MultistepIntegrator
 * @see org.apache.commons.math.ode.nonstiff.AdamsBashforthIntegrator
 * @see org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator
 * @see org.apache.commons.math.ode.stiff.BDFIntegrator
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class NordsieckTransformer implements Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 2216907099394084076L;

    /** Nordsieck to Multistep matrix. */
    private final RealMatrix nordsieckToMultistep;
                                           
    /** Multistep to Nordsieck matrix. */
    private final RealMatrix multistepToNordsieck;

    /**
     * Build a transformer for a specified order.
     * <p>States considered are y<sub>k-p</sub>, y<sub>k-(p+1)</sub> ...
     * y<sub>k-(q-1)</sub> and scaled derivatives considered are
     * h y'<sub>k-r</sub>, h y'<sub>k-(r+1)</sub> ... h y'<sub>k-(s-1)</sub><\p>
     * @param p start state index offset in multistep form
     * @param q end state index offset in multistep form
     * @param r start state derivative index offset in multistep form
     * @param s end state derivative index offset in multistep form
     * @exception InvalidMatrixException if the selected indices ranges define a
     * non-invertible transform (this typically happens when p == q)
     */
    public NordsieckTransformer(final int p, final int q, final int r, final int s)
        throws InvalidMatrixException {

        // from Nordsieck to multistep
        final FieldMatrix<BigFraction> bigNtoM = buildNordsieckToMultistep(p, q, r, s);
        Convertor convertor = new Convertor();
        bigNtoM.walkInOptimizedOrder(convertor);
        nordsieckToMultistep = convertor.getConvertedMatrix();

        // from multistep to Nordsieck
        final FieldDecompositionSolver<BigFraction> solver =
            new FieldLUDecompositionImpl<BigFraction>(bigNtoM).getSolver();
        final FieldMatrix<BigFraction> bigMtoN = solver.getInverse();
        convertor = new Convertor();
        bigMtoN.walkInOptimizedOrder(convertor);
        multistepToNordsieck = convertor.getConvertedMatrix();

    }

    /**
     * Build the transform from Nordsieck to multistep.
     * <p>States considered are y<sub>k-p</sub>, y<sub>k-(p+1)</sub> ...
     * y<sub>k-(q-1)</sub> and scaled derivatives considered are
     * h y'<sub>k-r</sub>, h y'<sub>k-(r+1)</sub> ... h y'<sub>k-(s-1)</sub>
     * @param p start state index offset in multistep form
     * @param q end state index offset in multistep form
     * @param r start state derivative index offset in multistep form
     * @param s end state derivative index offset in multistep form
     * @return transform from Nordsieck to multistep
     */
    public static FieldMatrix<BigFraction> buildNordsieckToMultistep(final int p, final int q,
                                                                     final int r, final int s) {

        final int n = (q - p) + (s - r);
        final BigFraction[][] array = new BigFraction[n][n];

        int i = 0;
        for (int l = p; l < q; ++l) {
            // handle previous state y<sub>(k-l)</sub>
            // the following expressions are direct applications of Taylor series
            // y<sub>k-1</sub>: [ 1  -1   1  -1   1 ...]
            // y<sub>k-2</sub>: [ 1  -2   4  -8  16 ...]
            // y<sub>k-3</sub>: [ 1  -3   9 -27  81 ...]
            // y<sub>k-4</sub>: [ 1  -4  16 -64 256 ...]
            final BigFraction[] row = array[i++];
            final BigInteger factor = BigInteger.valueOf(-l);
            BigInteger al = BigInteger.ONE;
            for (int j = 0; j < n; ++j) {
                row[j] = new BigFraction(al, BigInteger.ONE);
                al = al.multiply(factor);
            }
        }

        for (int l = r; l < s; ++l) {
            // handle previous state scaled derivative h y'<sub>(k-l)</sub>
            // the following expressions are direct applications of Taylor series
            // h y'<sub>k-1</sub>: [ 0  1  -2   3   -4     5 ...]
            // h y'<sub>k-2</sub>: [ 0  1  -4  12  -32    80 ...]
            // h y'<sub>k-3</sub>: [ 0  1  -6  27 -108   405 ...]
            // h y'<sub>k-4</sub>: [ 0  1  -8  48 -256  1280 ...]
            final BigFraction[] row = array[i++];
            final BigInteger factor = BigInteger.valueOf(-l);
            row[0] = BigFraction.ZERO;
            BigInteger al = BigInteger.ONE;
            for (int j = 1; j < n; ++j) {
                row[j] = new BigFraction(al.multiply(BigInteger.valueOf(j)), BigInteger.ONE);
                al = al.multiply(factor);
            }
        }

        return new FieldMatrixImpl<BigFraction>(array, false);

    }

    /** Convertor for {@link FieldMatrix}/{@link BigFraction}. */
    private static class Convertor extends DefaultFieldMatrixPreservingVisitor<BigFraction> {

        /** Converted array. */
        private double[][] data;

        /** Simple constructor. */
        public Convertor() {
            super(BigFraction.ZERO);
        }

        /** {@inheritDoc} */
        @Override
        public void start(int rows, int columns,
                          int startRow, int endRow, int startColumn, int endColumn) {
            data = new double[rows][columns];
        }

        /** {@inheritDoc} */
        @Override
        public void visit(int row, int column, BigFraction value) {
            data[row][column] = value.doubleValue();
        }

        /** Get the converted matrix.
         * @return converted matrix
         */
        RealMatrix getConvertedMatrix() {
            return new RealMatrixImpl(data, false);
        }

    }

    /**
     * Transform a scalar state history from multistep form to Nordsieck form.
     * <p>
     * The input state history must be in multistep form with element 0 for
     * y<sub>k-p</sub>, element 1 for y<sub>k-(p+1)</sub> ... element q-p-1 for
     * y<sub>k-(q-1)</sub>, element q-p for h y'<sub>k-r</sub>, element q-p+1
     * for h y'<sub>k-(r+1)</sub> ... element n-1 for h y'<sub>k-(s-1)</sub>.
     * The output state history will be in Nordsieck form with element 0 for
     * y<sub>k</sub>, element 1 for h y'<sub>k</sub>, element 2 for
     * h<sup>2</sup>/2 y''<sub>k</sub> ... element n-1 for
     * h<sup>n-1</sup>/(n-1)! yn-1<sub>k</sub>.
     * </p>
     * @param multistepHistory scalar state history in multistep form
     * @return scalar state history in Nordsieck form
     */
    public double[] multistepToNordsieck(final double[] multistepHistory) {
        return multistepToNordsieck.operate(multistepHistory);
    }

    /**
     * Transform a vectorial state history from multistep form to Nordsieck form.
     * <p>
     * The input state history must be in multistep form with row 0 for
     * y<sub>k-p</sub>, row 1 for y<sub>k-(p+1)</sub> ... row q-p-1 for
     * y<sub>k-(q-1)</sub>, row q-p for h y'<sub>k-r</sub>, row q-p+1
     * for h y'<sub>k-(r+1)</sub> ... row n-1 for h y'<sub>k-(s-1)</sub>.
     * The output state history will be in Nordsieck form with row 0 for
     * y<sub>k</sub>, row 1 for h y'<sub>k</sub>, row 2 for
     * h<sup>2</sup>/2 y''<sub>k</sub> ... row n-1 for
     * h<sup>n-1</sup>/(n-1)! yn-1<sub>k</sub>.
     * </p>
     * @param multistepHistory vectorial state history in multistep form
     * @return vectorial state history in Nordsieck form
     */
    public RealMatrix multistepToNordsieck(final RealMatrix multistepHistory) {
        return multistepToNordsieck.multiply(multistepHistory);
    }

    /**
     * Transform a scalar state history from Nordsieck form to multistep form.
     * <p>
     * The input state history must be in Nordsieck form with element 0 for
     * y<sub>k</sub>, element 1 for h y'<sub>k</sub>, element 2 for
     * h<sup>2</sup>/2 y''<sub>k</sub> ... element n-1 for
     * h<sup>n-1</sup>/(n-1)! yn-1<sub>k</sub>.
     * The output state history will be in multistep form with element 0 for
     * y<sub>k-p</sub>, element 1 for y<sub>k-(p+1)</sub> ... element q-p-1 for
     * y<sub>k-(q-1)</sub>, element q-p for h y'<sub>k-r</sub>, element q-p+1
     * for h y'<sub>k-(r+1)</sub> ... element n-1 for h y'<sub>k-(s-1)</sub>.
     * </p>
     * @param nordsieckHistory scalar state history in Nordsieck form
     * @return scalar state history in multistep form
     */
    public double[] nordsieckToMultistep(final double[] nordsieckHistory) {
        return nordsieckToMultistep.operate(nordsieckHistory);
    }

    /**
     * Transform a vectorial state history from Nordsieck form to multistep form.
     * <p>
     * The input state history must be in Nordsieck form with row 0 for
     * y<sub>k</sub>, row 1 for h y'<sub>k</sub>, row 2 for
     * h<sup>2</sup>/2 y''<sub>k</sub> ... row n-1 for
     * h<sup>n-1</sup>/(n-1)! yn-1<sub>k</sub>.
     * The output state history will be in multistep form with row 0 for
     * y<sub>k-p</sub>, row 1 for y<sub>k-(p+1)</sub> ... row q-p-1 for
     * y<sub>k-(q-1)</sub>, row q-p for h y'<sub>k-r</sub>, row q-p+1
     * for h y'<sub>k-(r+1)</sub> ... row n-1 for h y'<sub>k-(s-1)</sub>.
     * </p>
     * @param nordsieckHistory vectorial state history in Nordsieck form
     * @return vectorial state history in multistep form
     */
    public RealMatrix nordsieckToMultistep(final RealMatrix nordsieckHistory) {
        return nordsieckToMultistep.multiply(nordsieckHistory);
    }

}
