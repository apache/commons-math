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

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.math4.legacy.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.apache.commons.numbers.core.Precision;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EigenDecompositionTest {

    private double[] refValues;
    private RealMatrix matrix;

    @Test
    public void testDimension1() {
        RealMatrix matrix =
            MatrixUtils.createRealMatrix(new double[][] { { 1.5 } });
        EigenDecomposition ed;
        ed = new EigenDecomposition(matrix);
        Assert.assertEquals(1.5, ed.getRealEigenvalue(0), 1.0e-15);
    }

    @Test
    public void testDimension2() {
        RealMatrix matrix =
            MatrixUtils.createRealMatrix(new double[][] {
                    { 59.0, 12.0 },
                    { 12.0, 66.0 }
            });
        EigenDecomposition ed;
        ed = new EigenDecomposition(matrix);
        Assert.assertEquals(75.0, ed.getRealEigenvalue(0), 1.0e-15);
        Assert.assertEquals(50.0, ed.getRealEigenvalue(1), 1.0e-15);
    }

    @Test
    public void testDimension3() {
        RealMatrix matrix =
            MatrixUtils.createRealMatrix(new double[][] {
                                   {  39632.0, -4824.0, -16560.0 },
                                   {  -4824.0,  8693.0,   7920.0 },
                                   { -16560.0,  7920.0,  17300.0 }
                               });
        EigenDecomposition ed;
        ed = new EigenDecomposition(matrix);
        Assert.assertEquals(50000.0, ed.getRealEigenvalue(0), 3.0e-11);
        Assert.assertEquals(12500.0, ed.getRealEigenvalue(1), 3.0e-11);
        Assert.assertEquals( 3125.0, ed.getRealEigenvalue(2), 3.0e-11);
    }

    @Test
    public void testDimension3MultipleRoot() {
        RealMatrix matrix =
            MatrixUtils.createRealMatrix(new double[][] {
                    {  5,   10,   15 },
                    { 10,   20,   30 },
                    { 15,   30,   45 }
            });
        EigenDecomposition ed;
        ed = new EigenDecomposition(matrix);
        Assert.assertEquals(70.0, ed.getRealEigenvalue(0), 3.0e-11);
        Assert.assertEquals(0.0,  ed.getRealEigenvalue(1), 3.0e-11);
        Assert.assertEquals(0.0,  ed.getRealEigenvalue(2), 3.0e-11);
    }

    @Test
    public void testDimension4WithSplit() {
        RealMatrix matrix =
            MatrixUtils.createRealMatrix(new double[][] {
                                   {  0.784, -0.288,  0.000,  0.000 },
                                   { -0.288,  0.616,  0.000,  0.000 },
                                   {  0.000,  0.000,  0.164, -0.048 },
                                   {  0.000,  0.000, -0.048,  0.136 }
                               });
        EigenDecomposition ed;
        ed = new EigenDecomposition(matrix);
        Assert.assertEquals(1.0, ed.getRealEigenvalue(0), 1.0e-15);
        Assert.assertEquals(0.4, ed.getRealEigenvalue(1), 1.0e-15);
        Assert.assertEquals(0.2, ed.getRealEigenvalue(2), 1.0e-15);
        Assert.assertEquals(0.1, ed.getRealEigenvalue(3), 1.0e-15);
    }

    @Test
    public void testDimension4WithoutSplit() {
        RealMatrix matrix =
            MatrixUtils.createRealMatrix(new double[][] {
                                   {  0.5608, -0.2016,  0.1152, -0.2976 },
                                   { -0.2016,  0.4432, -0.2304,  0.1152 },
                                   {  0.1152, -0.2304,  0.3088, -0.1344 },
                                   { -0.2976,  0.1152, -0.1344,  0.3872 }
                               });
        EigenDecomposition ed;
        ed = new EigenDecomposition(matrix);
        Assert.assertEquals(1.0, ed.getRealEigenvalue(0), 1.0e-15);
        Assert.assertEquals(0.4, ed.getRealEigenvalue(1), 1.0e-15);
        Assert.assertEquals(0.2, ed.getRealEigenvalue(2), 1.0e-15);
        Assert.assertEquals(0.1, ed.getRealEigenvalue(3), 1.0e-15);
    }

    // the following test triggered an ArrayIndexOutOfBoundsException in commons-math 2.0
    @Test
    public void testMath308() {

        double[] mainTridiagonal = {
            22.330154644539597, 46.65485522478641, 17.393672330044705, 54.46687435351116, 80.17800767709437
        };
        double[] secondaryTridiagonal = {
            13.04450406501361, -5.977590941539671, 2.9040909856707517, 7.1570352792841225
        };

        // the reference values have been computed using routine DSTEMR
        // from the fortran library LAPACK version 3.2.1
        double[] refEigenValues = {
            82.044413207204002, 53.456697699894512, 52.536278520113882, 18.847969733754262, 14.138204224043099
        };
        RealVector[] refEigenVectors = {
            new ArrayRealVector(new double[] { -0.000462690386766, -0.002118073109055,  0.011530080757413,  0.252322434584915,  0.967572088232592 }),
            new ArrayRealVector(new double[] {  0.314647769490148,  0.750806415553905, -0.167700312025760, -0.537092972407375,  0.143854968127780 }),
            new ArrayRealVector(new double[] {  0.222368839324646,  0.514921891363332, -0.021377019336614,  0.801196801016305, -0.207446991247740 }),
            new ArrayRealVector(new double[] { -0.713933751051495,  0.190582113553930, -0.671410443368332,  0.056056055955050, -0.006541576993581 }),
            new ArrayRealVector(new double[] { -0.584677060845929,  0.367177264979103,  0.721453187784497, -0.052971054621812,  0.005740715188257 })
        };

        EigenDecomposition decomposition;
        decomposition = new EigenDecomposition(mainTridiagonal, secondaryTridiagonal);

        double[] eigenValues = decomposition.getRealEigenvalues();
        for (int i = 0; i < refEigenValues.length; ++i) {
            Assert.assertEquals(refEigenValues[i], eigenValues[i], 1.0e-5);
            Assert.assertEquals(0, refEigenVectors[i].subtract(decomposition.getEigenvector(i)).getNorm(), 2.0e-7);
        }
    }

    @Test
    public void testMathpbx02() {

        double[] mainTridiagonal = {
              7484.860960227216, 18405.28129035345, 13855.225609560746,
             10016.708722343366, 559.8117399576674, 6750.190788301587,
                71.21428769782159
        };
        double[] secondaryTridiagonal = {
             -4175.088570476366,1975.7955858241994,5193.178422374075,
              1995.286659169179,75.34535882933804,-234.0808002076056
        };

        // the reference values have been computed using routine DSTEMR
        // from the fortran library LAPACK version 3.2.1
        double[] refEigenValues = {
                20654.744890306974412,16828.208208485466457,
                6893.155912634994820,6757.083016675340332,
                5887.799885688558788,64.309089923240379,
                57.992628792736340
        };
        RealVector[] refEigenVectors = {
                new ArrayRealVector(new double[] {-0.270356342026904, 0.852811091326997, 0.399639490702077, 0.198794657813990, 0.019739323307666, 0.000106983022327, -0.000001216636321}),
                new ArrayRealVector(new double[] {0.179995273578326,-0.402807848153042,0.701870993525734,0.555058211014888,0.068079148898236,0.000509139115227,-0.000007112235617}),
                new ArrayRealVector(new double[] {-0.399582721284727,-0.056629954519333,-0.514406488522827,0.711168164518580,0.225548081276367,0.125943999652923,-0.004321507456014}),
                new ArrayRealVector(new double[] {0.058515721572821,0.010200130057739,0.063516274916536,-0.090696087449378,-0.017148420432597,0.991318870265707,-0.034707338554096}),
                new ArrayRealVector(new double[] {0.855205995537564,0.327134656629775,-0.265382397060548,0.282690729026706,0.105736068025572,-0.009138126622039,0.000367751821196}),
                new ArrayRealVector(new double[] {-0.002913069901144,-0.005177515777101,0.041906334478672,-0.109315918416258,0.436192305456741,0.026307315639535,0.891797507436344}),
                new ArrayRealVector(new double[] {-0.005738311176435,-0.010207611670378,0.082662420517928,-0.215733886094368,0.861606487840411,-0.025478530652759,-0.451080697503958})
        };

        // the following line triggers the exception
        EigenDecomposition decomposition;
        decomposition = new EigenDecomposition(mainTridiagonal, secondaryTridiagonal);

        double[] eigenValues = decomposition.getRealEigenvalues();
        for (int i = 0; i < refEigenValues.length; ++i) {
            Assert.assertEquals(refEigenValues[i], eigenValues[i], 1.0e-3);
            if (refEigenVectors[i].dotProduct(decomposition.getEigenvector(i)) < 0) {
                Assert.assertEquals(0, refEigenVectors[i].add(decomposition.getEigenvector(i)).getNorm(), 1.0e-5);
            } else {
                Assert.assertEquals(0, refEigenVectors[i].subtract(decomposition.getEigenvector(i)).getNorm(), 1.0e-5);
            }
        }
    }

    @Test
    public void testMathpbx03() {

        double[] mainTridiagonal = {
            1809.0978259647177,3395.4763425956166,1832.1894584712693,3804.364873592377,
            806.0482458637571,2403.656427234185,28.48691431556015
        };
        double[] secondaryTridiagonal = {
            -656.8932064545833,-469.30804108920734,-1021.7714889369421,
            -1152.540497328983,-939.9765163817368,-12.885877015422391
        };

        // the reference values have been computed using routine DSTEMR
        // from the fortran library LAPACK version 3.2.1
        double[] refEigenValues = {
            4603.121913685183245,3691.195818048970978,2743.442955402465032,1657.596442107321764,
            1336.797819095331306,30.129865209677519,17.035352085224986
        };

        RealVector[] refEigenVectors = {
            new ArrayRealVector(new double[] {-0.036249830202337,0.154184732411519,-0.346016328392363,0.867540105133093,-0.294483395433451,0.125854235969548,-0.000354507444044}),
            new ArrayRealVector(new double[] {-0.318654191697157,0.912992309960507,-0.129270874079777,-0.184150038178035,0.096521712579439,-0.070468788536461,0.000247918177736}),
            new ArrayRealVector(new double[] {-0.051394668681147,0.073102235876933,0.173502042943743,-0.188311980310942,-0.327158794289386,0.905206581432676,-0.004296342252659}),
            new ArrayRealVector(new double[] {0.838150199198361,0.193305209055716,-0.457341242126146,-0.166933875895419,0.094512811358535,0.119062381338757,-0.000941755685226}),
            new ArrayRealVector(new double[] {0.438071395458547,0.314969169786246,0.768480630802146,0.227919171600705,-0.193317045298647,-0.170305467485594,0.001677380536009}),
            new ArrayRealVector(new double[] {-0.003726503878741,-0.010091946369146,-0.067152015137611,-0.113798146542187,-0.313123000097908,-0.118940107954918,0.932862311396062}),
            new ArrayRealVector(new double[] {0.009373003194332,0.025570377559400,0.170955836081348,0.291954519805750,0.807824267665706,0.320108347088646,0.360202112392266}),
        };

        // the following line triggers the exception
        EigenDecomposition decomposition;
        decomposition = new EigenDecomposition(mainTridiagonal, secondaryTridiagonal);

        double[] eigenValues = decomposition.getRealEigenvalues();
        for (int i = 0; i < refEigenValues.length; ++i) {
            Assert.assertEquals(refEigenValues[i], eigenValues[i], 1.0e-4);
            if (refEigenVectors[i].dotProduct(decomposition.getEigenvector(i)) < 0) {
                Assert.assertEquals(0, refEigenVectors[i].add(decomposition.getEigenvector(i)).getNorm(), 1.0e-5);
            } else {
                Assert.assertEquals(0, refEigenVectors[i].subtract(decomposition.getEigenvector(i)).getNorm(), 1.0e-5);
            }
        }
    }

    /** test a matrix already in tridiagonal form. */
    @Test
    public void testTridiagonal() {
        Random r = new Random(4366663527842L);
        double[] ref = new double[30];
        for (int i = 0; i < ref.length; ++i) {
            if (i < 5) {
                ref[i] = 2 * r.nextDouble() - 1;
            } else {
                ref[i] = 0.0001 * r.nextDouble() + 6;
            }
        }
        Arrays.sort(ref);
        TriDiagonalTransformer t =
            new TriDiagonalTransformer(createTestMatrix(r, ref));
        EigenDecomposition ed;
        ed = new EigenDecomposition(t.getMainDiagonalRef(), t.getSecondaryDiagonalRef());
        double[] eigenValues = ed.getRealEigenvalues();
        Assert.assertEquals(ref.length, eigenValues.length);
        for (int i = 0; i < ref.length; ++i) {
            Assert.assertEquals(ref[ref.length - i - 1], eigenValues[i], 2.0e-14);
        }
    }

    /** test dimensions */
    @Test
    public void testDimensions() {
        final int m = matrix.getRowDimension();
        EigenDecomposition ed;
        ed = new EigenDecomposition(matrix);
        Assert.assertEquals(m, ed.getV().getRowDimension());
        Assert.assertEquals(m, ed.getV().getColumnDimension());
        Assert.assertEquals(m, ed.getD().getColumnDimension());
        Assert.assertEquals(m, ed.getD().getColumnDimension());
        Assert.assertEquals(m, ed.getVT().getRowDimension());
        Assert.assertEquals(m, ed.getVT().getColumnDimension());
    }

    /** test eigenvalues */
    @Test
    public void testEigenvalues() {
        EigenDecomposition ed;
        ed = new EigenDecomposition(matrix);
        double[] eigenValues = ed.getRealEigenvalues();
        Assert.assertEquals(refValues.length, eigenValues.length);
        for (int i = 0; i < refValues.length; ++i) {
            Assert.assertEquals(refValues[i], eigenValues[i], 3.0e-15);
        }
    }

    /** test eigenvalues for a big matrix. */
    @Test
    public void testBigMatrix() {
        Random r = new Random(17748333525117L);
        double[] bigValues = new double[200];
        for (int i = 0; i < bigValues.length; ++i) {
            bigValues[i] = 2 * r.nextDouble() - 1;
        }
        Arrays.sort(bigValues);
        EigenDecomposition ed;
        ed = new EigenDecomposition(createTestMatrix(r, bigValues));
        double[] eigenValues = ed.getRealEigenvalues();
        Assert.assertEquals(bigValues.length, eigenValues.length);
        for (int i = 0; i < bigValues.length; ++i) {
            Assert.assertEquals(bigValues[bigValues.length - i - 1], eigenValues[i], 2.0e-14);
        }
    }

    @Test
    public void testSymmetric() {
        RealMatrix symmetric = MatrixUtils.createRealMatrix(new double[][] {
                {4, 1, 1},
                {1, 2, 3},
                {1, 3, 6}
        });

        EigenDecomposition ed;
        ed = new EigenDecomposition(symmetric);

        RealMatrix d = ed.getD();
        RealMatrix v = ed.getV();
        RealMatrix vT = ed.getVT();

        double norm = v.multiply(d).multiply(vT).subtract(symmetric).getNorm();
        Assert.assertEquals(0, norm, 6.0e-13);
    }

    @Test
    public void testSquareRoot() {
        final double[][] data = {
            { 33, 24,  7 },
            { 24, 57, 11 },
            {  7, 11,  9 }
        };

        final EigenDecomposition dec = new EigenDecomposition(MatrixUtils.createRealMatrix(data));
        final RealMatrix sqrtM = dec.getSquareRoot();

        // Reconstruct initial matrix.
        final RealMatrix m = sqrtM.multiply(sqrtM);

        final int dim = data.length;
        for (int r = 0; r < dim; r++) {
            for (int c = 0; c < dim; c++) {
                Assert.assertEquals("m[" + r + "][" + c + "]",
                                    data[r][c], m.getEntry(r, c), 1e-13);
            }
        }
    }

    @Test(expected=MathUnsupportedOperationException.class)
    public void testSquareRootNonSymmetric() {
        final double[][] data = {
            { 1,  2, 4 },
            { 2,  3, 5 },
            { 11, 5, 9 }
        };

        final EigenDecomposition dec = new EigenDecomposition(MatrixUtils.createRealMatrix(data));
        @SuppressWarnings("unused")
        final RealMatrix sqrtM = dec.getSquareRoot();
    }

    @Test(expected=MathUnsupportedOperationException.class)
    public void testSquareRootNonPositiveDefinite() {
        final double[][] data = {
            { 1, 2,  4 },
            { 2, 3,  5 },
            { 4, 5, -9 }
        };

        final EigenDecomposition dec = new EigenDecomposition(MatrixUtils.createRealMatrix(data));
        @SuppressWarnings("unused")
        final RealMatrix sqrtM = dec.getSquareRoot();
    }

    @Test
    public void testUnsymmetric() {
        // Vandermonde matrix V(x;i,j) = x_i^{n - j} with x = (-1,-2,3,4)
        double[][] vData = { { -1.0, 1.0, -1.0, 1.0 },
                             { -8.0, 4.0, -2.0, 1.0 },
                             { 27.0, 9.0,  3.0, 1.0 },
                             { 64.0, 16.0, 4.0, 1.0 } };
        checkUnsymmetricMatrix(MatrixUtils.createRealMatrix(vData));

        RealMatrix randMatrix = MatrixUtils.createRealMatrix(new double[][] {
                {0,  1,     0,     0},
                {1,  0,     2.e-7, 0},
                {0, -2.e-7, 0,     1},
                {0,  0,     1,     0}
        });
        checkUnsymmetricMatrix(randMatrix);

        // from http://eigen.tuxfamily.org/dox/classEigen_1_1RealSchur.html
        double[][] randData2 = {
                {  0.680, -0.3300, -0.2700, -0.717, -0.687,  0.0259 },
                { -0.211,  0.5360,  0.0268,  0.214, -0.198,  0.6780 },
                {  0.566, -0.4440,  0.9040, -0.967, -0.740,  0.2250 },
                {  0.597,  0.1080,  0.8320, -0.514, -0.782, -0.4080 },
                {  0.823, -0.0452,  0.2710, -0.726,  0.998,  0.2750 },
                { -0.605,  0.2580,  0.4350,  0.608, -0.563,  0.0486 }
        };
        checkUnsymmetricMatrix(MatrixUtils.createRealMatrix(randData2));
    }

    @Test
    @Ignore
    public void testRandomUnsymmetricMatrix() {
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
            checkUnsymmetricMatrix(m);
        }
    }

    /**
     * Tests the porting of a bugfix in Jama-1.0.3 (from changelog):
     *
     *  Patched hqr2 method in Jama.EigenvalueDecomposition to avoid infinite loop;
     *  Thanks Frederic Devernay <frederic.devernay@m4x.org>
     */
    @Test
    public void testMath1051() {
        double[][] data = {
                {0,0,0,0,0},
                {0,0,0,0,1},
                {0,0,0,1,0},
                {1,1,0,0,1},
                {1,0,1,0,1}
        };

        RealMatrix m = MatrixUtils.createRealMatrix(data);
        checkUnsymmetricMatrix(m);
    }

    @Test
    @Ignore
    public void testNormalDistributionUnsymmetricMatrix() {
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
            checkUnsymmetricMatrix(m);
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
        checkUnsymmetricMatrix(m);
    }

    /**
     * Checks that the eigen decomposition of a general (unsymmetric) matrix is valid by
     * checking: A*V = V*D
     */
    private void checkUnsymmetricMatrix(final RealMatrix m) {
        try {
            EigenDecomposition ed = new EigenDecomposition(m);

            RealMatrix d = ed.getD();
            RealMatrix v = ed.getV();
            //RealMatrix vT = ed.getVT();

            RealMatrix x = m.multiply(v);
            RealMatrix y = v.multiply(d);

            double diffNorm = x.subtract(y).getNorm();
            Assert.assertTrue("The norm of (X-Y) is too large: " + diffNorm + ", matrix=" + m.toString(),
                    x.subtract(y).getNorm() < 1000 * Precision.EPSILON * JdkMath.max(x.getNorm(), y.getNorm()));

            RealMatrix invV = new LUDecomposition(v).getSolver().getInverse();
            double norm = v.multiply(d).multiply(invV).subtract(m).getNorm();
            Assert.assertEquals(0.0, norm, 1.0e-10);
        } catch (Exception e) {
            Assert.fail("Failed to create EigenDecomposition for matrix " + m.toString() + ", ex=" + e.toString());
        }
    }

    /** test eigenvectors */
    @Test
    public void testEigenvectors() {
        EigenDecomposition ed;
        ed = new EigenDecomposition(matrix);
        for (int i = 0; i < matrix.getRowDimension(); ++i) {
            double lambda = ed.getRealEigenvalue(i);
            RealVector v  = ed.getEigenvector(i);
            RealVector mV = matrix.operate(v);
            Assert.assertEquals(0, mV.subtract(v.mapMultiplyToSelf(lambda)).getNorm(), 1.0e-13);
        }
    }

    /** test A = VDVt */
    @Test
    public void testAEqualVDVt() {
        EigenDecomposition ed;
        ed = new EigenDecomposition(matrix);
        RealMatrix v  = ed.getV();
        RealMatrix d  = ed.getD();
        RealMatrix vT = ed.getVT();
        double norm = v.multiply(d).multiply(vT).subtract(matrix).getNorm();
        Assert.assertEquals(0, norm, 6.0e-13);
    }

    /** test that V is orthogonal */
    @Test
    public void testVOrthogonal() {
        RealMatrix v = new EigenDecomposition(matrix).getV();
        RealMatrix vTv = v.transpose().multiply(v);
        RealMatrix id  = MatrixUtils.createRealIdentityMatrix(vTv.getRowDimension());
        Assert.assertEquals(0, vTv.subtract(id).getNorm(), 2.0e-13);
    }

    /** test diagonal matrix */
    @Test
    public void testDiagonal() {
        double[] diagonal = new double[] { -3.0, -2.0, 2.0, 5.0 };
        RealMatrix m = MatrixUtils.createRealDiagonalMatrix(diagonal);
        EigenDecomposition ed;
        ed = new EigenDecomposition(m);
        Assert.assertEquals(diagonal[0], ed.getRealEigenvalue(3), 2.0e-15);
        Assert.assertEquals(diagonal[1], ed.getRealEigenvalue(2), 2.0e-15);
        Assert.assertEquals(diagonal[2], ed.getRealEigenvalue(1), 2.0e-15);
        Assert.assertEquals(diagonal[3], ed.getRealEigenvalue(0), 2.0e-15);
    }

    /**
     * Matrix with eigenvalues {8, -1, -1}
     */
    @Test
    public void testRepeatedEigenvalue() {
        RealMatrix repeated = MatrixUtils.createRealMatrix(new double[][] {
                {3,  2,  4},
                {2,  0,  2},
                {4,  2,  3}
        });
        EigenDecomposition ed;
        ed = new EigenDecomposition(repeated);
        checkEigenValues(new double[] {8, -1, -1}, ed, 1E-12);
        checkEigenVector(new double[] {2, 1, 2}, ed, 1E-12);
    }

    /**
     * Matrix with eigenvalues {2, 0, 12}
     */
    @Test
    public void testDistinctEigenvalues() {
        RealMatrix distinct = MatrixUtils.createRealMatrix(new double[][] {
                {3, 1, -4},
                {1, 3, -4},
                {-4, -4, 8}
        });
        EigenDecomposition ed;
        ed = new EigenDecomposition(distinct);
        checkEigenValues(new double[] {2, 0, 12}, ed, 1E-12);
        checkEigenVector(new double[] {1, -1, 0}, ed, 1E-12);
        checkEigenVector(new double[] {1, 1, 1}, ed, 1E-12);
        checkEigenVector(new double[] {-1, -1, 2}, ed, 1E-12);
    }

    /**
     * Verifies operation on indefinite matrix
     */
    @Test
    public void testZeroDivide() {
        RealMatrix indefinite = MatrixUtils.createRealMatrix(new double [][] {
                { 0.0, 1.0, -1.0 },
                { 1.0, 1.0, 0.0 },
                { -1.0,0.0, 1.0 }
        });
        EigenDecomposition ed;
        ed = new EigenDecomposition(indefinite);
        checkEigenValues(new double[] {2, 1, -1}, ed, 1E-12);
        double isqrt3 = 1/JdkMath.sqrt(3.0);
        checkEigenVector(new double[] {isqrt3,isqrt3,-isqrt3}, ed, 1E-12);
        double isqrt2 = 1/JdkMath.sqrt(2.0);
        checkEigenVector(new double[] {0.0,-isqrt2,-isqrt2}, ed, 1E-12);
        double isqrt6 = 1/JdkMath.sqrt(6.0);
        checkEigenVector(new double[] {2*isqrt6,-isqrt6,isqrt6}, ed, 1E-12);
    }

    /**
     * Verifies operation on very small values.
     * Matrix with eigenvalues {2e-100, 0, 12e-100}
     */
    @Test
    public void testTinyValues() {
        final double tiny = 1e-100;
        RealMatrix distinct = MatrixUtils.createRealMatrix(new double[][] {
                {3, 1, -4},
                {1, 3, -4},
                {-4, -4, 8}
        });
        distinct = distinct.scalarMultiply(tiny);

        final EigenDecomposition ed = new EigenDecomposition(distinct);
        checkEigenValues(MathArrays.scale(tiny, new double[] {2, 0, 12}), ed, 1e-12 * tiny);
        checkEigenVector(new double[] {1, -1, 0}, ed, 1e-12);
        checkEigenVector(new double[] {1, 1, 1}, ed, 1e-12);
        checkEigenVector(new double[] {-1, -1, 2}, ed, 1e-12);
    }

    /**
     * Verifies that the given EigenDecomposition has eigenvalues equivalent to
     * the targetValues, ignoring the order of the values and allowing
     * values to differ by tolerance.
     */
    protected void checkEigenValues(double[] targetValues,
            EigenDecomposition ed, double tolerance) {
        double[] observed = ed.getRealEigenvalues();
        for (int i = 0; i < observed.length; i++) {
            Assert.assertTrue(isIncludedValue(observed[i], targetValues, tolerance));
            Assert.assertTrue(isIncludedValue(targetValues[i], observed, tolerance));
        }
    }


    /**
     * Returns true iff there is an entry within tolerance of value in
     * searchArray.
     */
    private boolean isIncludedValue(double value, double[] searchArray,
            double tolerance) {
       boolean found = false;
       int i = 0;
       while (!found && i < searchArray.length) {
           if (JdkMath.abs(value - searchArray[i]) < tolerance) {
               found = true;
           }
           i++;
       }
       return found;
    }

    /**
     * Returns true iff eigenVector is a scalar multiple of one of the columns
     * of ed.getV().  Does not try linear combinations - i.e., should only be
     * used to find vectors in one-dimensional eigenspaces.
     */
    protected void checkEigenVector(double[] eigenVector,
            EigenDecomposition ed, double tolerance) {
        Assert.assertTrue(isIncludedColumn(eigenVector, ed.getV(), tolerance));
    }

    /**
     * Returns true iff there is a column that is a scalar multiple of column
     * in searchMatrix (modulo tolerance)
     */
    private boolean isIncludedColumn(double[] column, RealMatrix searchMatrix,
            double tolerance) {
        boolean found = false;
        int i = 0;
        while (!found && i < searchMatrix.getColumnDimension()) {
            double multiplier = 1.0;
            boolean matching = true;
            int j = 0;
            while (matching && j < searchMatrix.getRowDimension()) {
                double colEntry = searchMatrix.getEntry(j, i);
                // Use the first entry where both are non-zero as scalar
                if (JdkMath.abs(multiplier - 1.0) <= JdkMath.ulp(1.0) && JdkMath.abs(colEntry) > 1E-14
                        && JdkMath.abs(column[j]) > 1e-14) {
                    multiplier = colEntry / column[j];
                }
                if (JdkMath.abs(column[j] * multiplier - colEntry) > tolerance) {
                    matching = false;
                }
                j++;
            }
            found = matching;
            i++;
        }
        return found;
    }

    @Before
    public void setUp() {
        refValues = new double[] {
                2.003, 2.002, 2.001, 1.001, 1.000, 0.001
        };
        matrix = createTestMatrix(new Random(35992629946426L), refValues);
    }

    @After
    public void tearDown() {
        refValues = null;
        matrix    = null;
    }

    static RealMatrix createTestMatrix(final Random r, final double[] eigenValues) {
        final int n = eigenValues.length;
        final RealMatrix v = createOrthogonalMatrix(r, n);
        final RealMatrix d = MatrixUtils.createRealDiagonalMatrix(eigenValues);
        return v.multiply(d).multiply(v.transpose());
    }

    public static RealMatrix createOrthogonalMatrix(final Random r, final int size) {

        final double[][] data = new double[size][size];

        for (int i = 0; i < size; ++i) {
            final double[] dataI = data[i];
            double norm2 = 0;
            do {

                // generate randomly row I
                for (int j = 0; j < size; ++j) {
                    dataI[j] = 2 * r.nextDouble() - 1;
                }

                // project the row in the subspace orthogonal to previous rows
                for (int k = 0; k < i; ++k) {
                    final double[] dataK = data[k];
                    double dotProduct = 0;
                    for (int j = 0; j < size; ++j) {
                        dotProduct += dataI[j] * dataK[j];
                    }
                    for (int j = 0; j < size; ++j) {
                        dataI[j] -= dotProduct * dataK[j];
                    }
                }

                // normalize the row
                norm2 = 0;
                for (final double dataIJ : dataI) {
                    norm2 += dataIJ * dataIJ;
                }
                final double inv = 1.0 / JdkMath.sqrt(norm2);
                for (int j = 0; j < size; ++j) {
                    dataI[j] *= inv;
                }
            } while (norm2 * size < 0.01);
        }

        return MatrixUtils.createRealMatrix(data);
    }
}
