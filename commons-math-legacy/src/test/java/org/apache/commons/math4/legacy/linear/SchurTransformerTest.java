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

package org.apache.commons.math4.legacy.linear;

import java.util.Random;

import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.rng.simple.RandomSource;
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
            ContinuousDistribution.Sampler dist
                = NormalDistribution.of(0.0, r.nextDouble() * 5).createSampler(RandomSource.WELL_512_A.create(64925784252L));

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

    @Test
    public void testMath848() {
        double[][] data = {
                { 0.1849449280, -0.0646971046,  0.0774755812, -0.0969651755, -0.0692648806,  0.3282344352, -0.0177423074,  0.2063136340},
                {-0.0742700134, -0.0289063030, -0.0017269460, -0.0375550146, -0.0487737922, -0.2616837868, -0.0821201295, -0.2530000167},
                { 0.2549910127,  0.0995733692, -0.0009718388,  0.0149282808,  0.1791878897, -0.0823182816,  0.0582629256,  0.3219545182},
                {-0.0694747557, -0.1880649148, -0.2740630911,  0.0720096468, -0.1800836914, -0.3518996425,  0.2486747833,  0.6257938167},
                { 0.0536360918, -0.1339297778,  0.2241579764, -0.0195327484, -0.0054103808,  0.0347564518,  0.5120802482, -0.0329902864},
                {-0.5933332356, -0.2488721082,  0.2357173629,  0.0177285473,  0.0856630593, -0.3567126300, -0.1600668126, -0.1010899621},
                {-0.0514349819, -0.0854319435,  0.1125050061,  0.0063453560, -0.2250000688, -0.2209343090,  0.1964623477, -0.1512329924},
                { 0.0197395947, -0.1997170581, -0.1425959019, -0.2749477910, -0.0969467073,  0.0603688520, -0.2826905192,  0.1794315473}};
        RealMatrix m = MatrixUtils.createRealMatrix(data);
        RealMatrix s = checkAEqualPTPt(m);
        checkSchurForm(s);
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
        Assert.assertEquals(0, norm, 1.0e-9);

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
        Assert.assertSame(p, transformer.getP());
        Assert.assertSame(t, transformer.getT());
    }
}
