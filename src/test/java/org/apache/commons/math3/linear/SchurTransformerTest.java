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

package org.apache.commons.math3.linear;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.Test;
import org.junit.Assert;

public class SchurTransformerTest {

    private double[][] testSquare5 = {
            { 5, 4, 3, 2, 1 },
            { 1, 4, 0, 3, 3 },
            { 2, 0, 3, 0, 0 },
            { 3, 2, 1, 2, 5 },
            { 4, 2, 1, 4, 1 }
    };

    private double[][] testSquare3 = {
            {  2, -1, 1 },
            { -1,  2, 1 },
            {  1, -1, 2 }
    };

    // from http://eigen.tuxfamily.org/dox/classEigen_1_1RealSchur.html
    private double[][] testRandom = {
            {  0.680, -0.3300, -0.2700, -0.717, -0.687,  0.0259 },
            { -0.211,  0.5360,  0.0268,  0.214, -0.198,  0.6780 },
            {  0.566, -0.4440,  0.9040, -0.967, -0.740,  0.2250 },
            {  0.597,  0.1080,  0.8320, -0.514, -0.782, -0.4080 },
            {  0.823, -0.0452,  0.2710, -0.726,  0.998,  0.2750 },
            { -0.605,  0.2580,  0.4350,  0.608, -0.563,  0.0486 }
    };

    @Test
    public void testNonSquare() {
        try {
            new SchurTransformer(MatrixUtils.createRealMatrix(new double[3][2]));
            Assert.fail("an exception should have been thrown");
        } catch (NonSquareMatrixException ime) {
            // expected behavior
        }
    }

    @Test
    public void testAEqualPTPt() {
        checkAEqualPTPt(MatrixUtils.createRealMatrix(testSquare5));
        checkAEqualPTPt(MatrixUtils.createRealMatrix(testSquare3));
        checkAEqualPTPt(MatrixUtils.createRealMatrix(testRandom));
   }

    @Test
    public void testPOrthogonal() {
        checkOrthogonal(new SchurTransformer(MatrixUtils.createRealMatrix(testSquare5)).getP());
        checkOrthogonal(new SchurTransformer(MatrixUtils.createRealMatrix(testSquare3)).getP());
        checkOrthogonal(new SchurTransformer(MatrixUtils.createRealMatrix(testRandom)).getP());        
    }

    @Test
    public void testPTOrthogonal() {
        checkOrthogonal(new SchurTransformer(MatrixUtils.createRealMatrix(testSquare5)).getPT());
        checkOrthogonal(new SchurTransformer(MatrixUtils.createRealMatrix(testSquare3)).getPT());
        checkOrthogonal(new SchurTransformer(MatrixUtils.createRealMatrix(testRandom)).getPT());
    }

    @Test
    public void testSchurForm() {
        checkSchurForm(new SchurTransformer(MatrixUtils.createRealMatrix(testSquare5)).getT());
        checkSchurForm(new SchurTransformer(MatrixUtils.createRealMatrix(testSquare3)).getT());
        checkSchurForm(new SchurTransformer(MatrixUtils.createRealMatrix(testRandom)).getT());
    }

    @Test
    public void testRandomData() {
        for (int run = 0; run < 100; run++) {
            Random r = new Random(System.currentTimeMillis());

            // matrix size
            int size = r.nextInt(20) + 4;

            double[][] data = new double[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    data[i][j] = r.nextInt(100);
                }
            }

            RealMatrix m = MatrixUtils.createRealMatrix(data);
            RealMatrix s = checkAEqualPTPt(m);
            checkSchurForm(s);
        }
    }

    @Test
    public void testRandomDataNormalDistribution() {
        for (int run = 0; run < 100; run++) {
            Random r = new Random(System.currentTimeMillis());
            NormalDistribution dist = new NormalDistribution(0.0, r.nextDouble() * 5);

            // matrix size
            int size = r.nextInt(20) + 4;

            double[][] data = new double[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    data[i][j] = dist.sample();
                }
            }

            RealMatrix m = MatrixUtils.createRealMatrix(data);
            RealMatrix s = checkAEqualPTPt(m);
            checkSchurForm(s);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Test helpers
    ///////////////////////////////////////////////////////////////////////////

    private RealMatrix checkAEqualPTPt(RealMatrix matrix) {
        SchurTransformer transformer = new SchurTransformer(matrix);
        RealMatrix p  = transformer.getP();
        RealMatrix t  = transformer.getT();
        RealMatrix pT = transformer.getPT();
        
        RealMatrix result = p.multiply(t).multiply(pT);

        double norm = result.subtract(matrix).getNorm();
        Assert.assertEquals(0, norm, 1.0e-10);
        
        return t;
    }

    private void checkOrthogonal(RealMatrix m) {
        RealMatrix mTm = m.transpose().multiply(m);
        RealMatrix id  = MatrixUtils.createRealIdentityMatrix(mTm.getRowDimension());
        Assert.assertEquals(0, mTm.subtract(id).getNorm(), 1.0e-14);
    }

    private void checkSchurForm(final RealMatrix m) {
        final int rows = m.getRowDimension();
        final int cols = m.getColumnDimension();
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                if (i > j + 1) {
                    Assert.assertEquals(0, m.getEntry(i, j), 1.0e-16);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void checkMatricesValues(double[][] matrix, double[][] pRef, double[][] hRef) {

        SchurTransformer transformer =
            new SchurTransformer(MatrixUtils.createRealMatrix(matrix));

        // check values against known references
        RealMatrix p = transformer.getP();
        Assert.assertEquals(0, p.subtract(MatrixUtils.createRealMatrix(pRef)).getNorm(), 1.0e-14);

        RealMatrix t = transformer.getT();
        Assert.assertEquals(0, t.subtract(MatrixUtils.createRealMatrix(hRef)).getNorm(), 1.0e-14);

        // check the same cached instance is returned the second time
        Assert.assertTrue(p == transformer.getP());
        Assert.assertTrue(t == transformer.getT());
    }
}
