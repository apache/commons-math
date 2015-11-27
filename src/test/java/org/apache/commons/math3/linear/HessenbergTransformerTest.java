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

public class HessenbergTransformerTest {

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

    // from http://eigen.tuxfamily.org/dox/classEigen_1_1HessenbergDecomposition.html

    private double[][] testRandom = {
            {  0.680,  0.823, -0.4440, -0.2700 },
            { -0.211, -0.605,  0.1080,  0.0268 },
            {  0.566, -0.330, -0.0452,  0.9040 },
            {  0.597,  0.536,  0.2580,  0.8320 }
    };

    @Test
    public void testNonSquare() {
        try {
            new HessenbergTransformer(MatrixUtils.createRealMatrix(new double[3][2]));
            Assert.fail("an exception should have been thrown");
        } catch (NonSquareMatrixException ime) {
            // expected behavior
        }
    }

    @Test
    public void testAEqualPHPt() {
        checkAEqualPHPt(MatrixUtils.createRealMatrix(testSquare5));
        checkAEqualPHPt(MatrixUtils.createRealMatrix(testSquare3));
        checkAEqualPHPt(MatrixUtils.createRealMatrix(testRandom));
   }

    @Test
    public void testPOrthogonal() {
        checkOrthogonal(new HessenbergTransformer(MatrixUtils.createRealMatrix(testSquare5)).getP());
        checkOrthogonal(new HessenbergTransformer(MatrixUtils.createRealMatrix(testSquare3)).getP());
    }

    @Test
    public void testPTOrthogonal() {
        checkOrthogonal(new HessenbergTransformer(MatrixUtils.createRealMatrix(testSquare5)).getPT());
        checkOrthogonal(new HessenbergTransformer(MatrixUtils.createRealMatrix(testSquare3)).getPT());
    }

    @Test
    public void testHessenbergForm() {
        checkHessenbergForm(new HessenbergTransformer(MatrixUtils.createRealMatrix(testSquare5)).getH());
        checkHessenbergForm(new HessenbergTransformer(MatrixUtils.createRealMatrix(testSquare3)).getH());
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
            RealMatrix h = checkAEqualPHPt(m);
            checkHessenbergForm(h);
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
            RealMatrix h = checkAEqualPHPt(m);
            checkHessenbergForm(h);
        }
    }

    @Test
    public void testMatricesValues5() {
        checkMatricesValues(testSquare5,
                            new double[][] {
                                { 1.0,  0.0,                0.0,                0.0,                0.0               },
                                { 0.0, -0.182574185835055,  0.784218758628863,  0.395029040913988, -0.442289115981669 },
                                { 0.0, -0.365148371670111, -0.337950625265477, -0.374110794088820, -0.782621974707823 },
                                { 0.0, -0.547722557505166,  0.402941130124223, -0.626468266309003,  0.381019628053472 },
                                { 0.0, -0.730296743340221, -0.329285224617644,  0.558149336547665,  0.216118545309225 }
                            },
                            new double[][] {
                                {  5.0,              -3.65148371670111,  2.59962019434982, -0.237003414680848, -3.13886458663398  },
                                { -5.47722557505166,  6.9,              -2.29164066120599,  0.207283564429169,  0.703858369151728 },
                                {  0.0,              -4.21386600008432,  2.30555659846067,  2.74935928725112,   0.857569835914113 },
                                {  0.0,               0.0,               2.86406180891882, -1.11582249161595,   0.817995267184158 },
                                {  0.0,               0.0,               0.0,               0.683518597386085,  1.91026589315528  }
                            });
    }

    @Test
    public void testMatricesValues3() {
        checkMatricesValues(testSquare3,
                            new double[][] {
                                {  1.0,  0.0,               0.0               },
                                {  0.0, -0.707106781186547, 0.707106781186547 },
                                {  0.0,  0.707106781186547, 0.707106781186548 },
                            },
                            new double[][] {
                                {  2.0,              1.41421356237309,  0.0 },
                                {  1.41421356237310, 2.0,              -1.0 },
                                {  0.0,              1.0,               2.0 },
                            });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Test helpers
    ///////////////////////////////////////////////////////////////////////////

    private RealMatrix checkAEqualPHPt(RealMatrix matrix) {
        HessenbergTransformer transformer = new HessenbergTransformer(matrix);
        RealMatrix p  = transformer.getP();
        RealMatrix pT = transformer.getPT();
        RealMatrix h  = transformer.getH();

        RealMatrix result = p.multiply(h).multiply(pT);
        double norm = result.subtract(matrix).getNorm();
        Assert.assertEquals(0, norm, 1.0e-10);

        for (int i = 0; i < matrix.getRowDimension(); ++i) {
            for (int j = 0; j < matrix.getColumnDimension(); ++j) {
                if (i > j + 1) {
                    Assert.assertEquals(matrix.getEntry(i, j), result.getEntry(i, j), 1.0e-12);
                }
            }
        }

        return transformer.getH();
    }

    private void checkOrthogonal(RealMatrix m) {
        RealMatrix mTm = m.transpose().multiply(m);
        RealMatrix id  = MatrixUtils.createRealIdentityMatrix(mTm.getRowDimension());
        Assert.assertEquals(0, mTm.subtract(id).getNorm(), 1.0e-14);
    }

    private void checkHessenbergForm(RealMatrix m) {
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

    private void checkMatricesValues(double[][] matrix, double[][] pRef, double[][] hRef) {
        HessenbergTransformer transformer =
            new HessenbergTransformer(MatrixUtils.createRealMatrix(matrix));

        // check values against known references
        RealMatrix p = transformer.getP();
        Assert.assertEquals(0, p.subtract(MatrixUtils.createRealMatrix(pRef)).getNorm(), 1.0e-14);

        RealMatrix h = transformer.getH();
        Assert.assertEquals(0, h.subtract(MatrixUtils.createRealMatrix(hRef)).getNorm(), 1.0e-14);

        // check the same cached instance is returned the second time
        Assert.assertTrue(p == transformer.getP());
        Assert.assertTrue(h == transformer.getH());
    }
}
