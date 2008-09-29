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

package org.apache.commons.math.linear;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TriDiagonalTransformerTest extends TestCase {

    private double[][] testSquare5 = {
            { 1, 2, 3, 1, 1 },
            { 2, 1, 1, 3, 1 },
            { 3, 1, 1, 1, 2 },
            { 1, 3, 1, 2, 1 },
            { 1, 1, 2, 1, 3 }
    };

    private double[][] testSquare3 = {
            { 1, 3, 4 },
            { 3, 2, 2 },
            { 4, 2, 0 }
    };

    public TriDiagonalTransformerTest(String name) {
        super(name);
    }

    public void testNonSquare() {
        try {
            new TriDiagonalTransformer(new RealMatrixImpl(new double[3][2], false));
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException ime) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    public void testAEqualQTQt() {
        checkAEqualQTQt(new RealMatrixImpl(testSquare5, false));
        checkAEqualQTQt(new RealMatrixImpl(testSquare3, false));
    }

    private void checkAEqualQTQt(RealMatrix matrix) {
        TriDiagonalTransformer transformer = new TriDiagonalTransformer(matrix);
        RealMatrix q  = transformer.getQ();
        RealMatrix qT = transformer.getQT();
        RealMatrix t  = transformer.getT();
        double norm = q.multiply(t).multiply(qT).subtract(matrix).getNorm();
        assertEquals(0, norm, 4.0e-15);
    }

    public void testQOrthogonal() {
        checkOrthogonal(new TriDiagonalTransformer(new RealMatrixImpl(testSquare5, false)).getQ());
        checkOrthogonal(new TriDiagonalTransformer(new RealMatrixImpl(testSquare3, false)).getQ());
    }

    public void testQTOrthogonal() {
        checkOrthogonal(new TriDiagonalTransformer(new RealMatrixImpl(testSquare5, false)).getQT());
        checkOrthogonal(new TriDiagonalTransformer(new RealMatrixImpl(testSquare3, false)).getQT());
    }

    private void checkOrthogonal(RealMatrix m) {
        RealMatrix mTm = m.transpose().multiply(m);
        RealMatrix id  = MatrixUtils.createRealIdentityMatrix(mTm.getRowDimension());
        assertEquals(0, mTm.subtract(id).getNorm(), 1.0e-15);        
    }

    public void testTTriDiagonal() {
        checkTriDiagonal(new TriDiagonalTransformer(new RealMatrixImpl(testSquare5, false)).getT());
        checkTriDiagonal(new TriDiagonalTransformer(new RealMatrixImpl(testSquare3, false)).getT());
    }

    private void checkTriDiagonal(RealMatrix m) {
        final int rows = m.getRowDimension();
        final int cols = m.getColumnDimension();
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                if ((i < j - 1) || (i > j + 1)) {
                    assertEquals(0, m.getEntry(i, j), 1.0e-16);
                }                    
            }
        }
    }

    public void testMatricesValues5() {
        checkMatricesValues(testSquare5,
                            new double[][] {
                                { 1.0,  0.0,                 0.0,                  0.0,                   0.0 },
                                { 0.0, -0.5163977794943222,  0.016748280772542083, 0.839800693771262,     0.16669620021405473 },
                                { 0.0, -0.7745966692414833, -0.4354553000860955,  -0.44989322880603355,  -0.08930153582895772 },
                                { 0.0, -0.2581988897471611,  0.6364346693566014,  -0.30263204032131164,   0.6608313651342882 },
                                { 0.0, -0.2581988897471611,  0.6364346693566009,  -0.027289660803112598, -0.7263191580755246 }
                            },
                            new double[] { 1, 4.4, 1.433099579242636, -0.89537362758743, 2.062274048344794 },
                            new double[] { -Math.sqrt(15), -3.0832882879592476, 0.6082710842351517, 1.1786086405912128 });
    }

    public void testMatricesValues3() {
        checkMatricesValues(testSquare3,
                            new double[][] {
                                {  1.0,  0.0,  0.0 },
                                {  0.0, -0.6,  0.8 },
                                {  0.0, -0.8, -0.6 },
                            },
                            new double[] { 1, 2.64, -0.64 },
                            new double[] { -5, -1.52 });
    }

    private void checkMatricesValues(double[][] matrix, double[][] qRef,
                                     double[] mainDiagnonal,
                                     double[] secondaryDiagonal) {
        TriDiagonalTransformer transformer =
            new TriDiagonalTransformer(new RealMatrixImpl(matrix, false));

        // check values against known references
        RealMatrix q = transformer.getQ();
        assertEquals(0, q.subtract(new RealMatrixImpl(qRef, false)).getNorm(), 1.0e-14);

        RealMatrix t = transformer.getT();
        double[][] tData = new double[mainDiagnonal.length][mainDiagnonal.length];
        for (int i = 0; i < mainDiagnonal.length; ++i) {
            tData[i][i] = mainDiagnonal[i];
            if (i > 0) {
                tData[i][i - 1] = secondaryDiagonal[i - 1];
            }
            if (i < secondaryDiagonal.length) {
                tData[i][i + 1] = secondaryDiagonal[i];
            }
        }
        assertEquals(0, t.subtract(new RealMatrixImpl(tData, false)).getNorm(), 1.0e-14);

        // check the same cached instance is returned the second time
        assertTrue(q == transformer.getQ());
        assertTrue(t == transformer.getT());
        
    }

    public static Test suite() {
        return new TestSuite(TriDiagonalTransformerTest.class);
    }

}
