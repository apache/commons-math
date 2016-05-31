//Licensed to the Apache Software Foundation (ASF) under one
//or more contributor license agreements.  See the NOTICE file
//distributed with this work for additional information
//regarding copyright ownership.  The ASF licenses this file
//to you under the Apache License, Version 2.0 (the
//"License"); you may not use this file except in compliance
//with the License.  You may obtain a copy of the License at

//http://www.apache.org/licenses/LICENSE-2.0

//Unless required by applicable law or agreed to in writing,
//software distributed under the License is distributed on an
//"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//KIND, either express or implied.  See the License for the
//specific language governing permissions and limitations
//under the License.

package org.apache.commons.math4.random;

import java.util.Arrays;

import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.MatrixUtils;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.random.CorrelatedRandomVectorGenerator;
import org.apache.commons.math4.random.GaussianRandomGenerator;
import org.apache.commons.math4.random.JDKRandomGenerator;
import org.apache.commons.math4.random.NormalizedRandomGenerator;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.stat.correlation.StorelessCovariance;
import org.apache.commons.math4.stat.descriptive.moment.VectorialCovariance;
import org.apache.commons.math4.stat.descriptive.moment.VectorialMean;
import org.apache.commons.math4.util.FastMath;
import org.junit.Test;
import org.junit.Assert;

public class CorrelatedRandomVectorGeneratorTest {
    private double[] mean;
    private RealMatrix covariance;
    private CorrelatedRandomVectorGenerator generator;

    public CorrelatedRandomVectorGeneratorTest() {
        mean = new double[] { 0.0, 1.0, -3.0, 2.3 };

        RealMatrix b = MatrixUtils.createRealMatrix(4, 3);
        int counter = 0;
        for (int i = 0; i < b.getRowDimension(); ++i) {
            for (int j = 0; j < b.getColumnDimension(); ++j) {
                b.setEntry(i, j, 1.0 + 0.1 * ++counter);
            }
        }
        RealMatrix bbt = b.multiply(b.transpose());
        covariance = MatrixUtils.createRealMatrix(mean.length, mean.length);
        for (int i = 0; i < covariance.getRowDimension(); ++i) {
            covariance.setEntry(i, i, bbt.getEntry(i, i));
            for (int j = 0; j < covariance.getColumnDimension(); ++j) {
                double s = bbt.getEntry(i, j);
                covariance.setEntry(i, j, s);
                covariance.setEntry(j, i, s);
            }
        }

        RandomGenerator rg = new JDKRandomGenerator();
        rg.setSeed(17399225432l);
        GaussianRandomGenerator rawGenerator = new GaussianRandomGenerator(rg);
        generator = new CorrelatedRandomVectorGenerator(mean,
                                                        covariance,
                                                        1.0e-12 * covariance.getNorm(),
                                                        rawGenerator);
    }

    @Test
    public void testRank() {
        Assert.assertEquals(2, generator.getRank());
    }

    @Test
    public void testMath226() {
        double[] mean = { 1, 1, 10, 1 };
        double[][] cov = {
                { 1, 3, 2, 6 },
                { 3, 13, 16, 2 },
                { 2, 16, 38, -1 },
                { 6, 2, -1, 197 }
        };
        RealMatrix covRM = MatrixUtils.createRealMatrix(cov);
        JDKRandomGenerator jg = new JDKRandomGenerator();
        jg.setSeed(5322145245211l);
        NormalizedRandomGenerator rg = new GaussianRandomGenerator(jg);
        CorrelatedRandomVectorGenerator sg =
            new CorrelatedRandomVectorGenerator(mean, covRM, 0.00001, rg);

        double[] min = new double[mean.length];
        Arrays.fill(min, Double.POSITIVE_INFINITY);
        double[] max = new double[mean.length];
        Arrays.fill(max, Double.NEGATIVE_INFINITY);
        for (int i = 0; i < 10; i++) {
            double[] generated = sg.nextVector();
            for (int j = 0; j < generated.length; ++j) {
                min[j] = FastMath.min(min[j], generated[j]);
                max[j] = FastMath.max(max[j], generated[j]);
            }
        }
        for (int j = 0; j < min.length; ++j) {
            Assert.assertTrue(max[j] - min[j] > 2.0);
        }

    }

    @Test
    public void testRootMatrix() {
        RealMatrix b = generator.getRootMatrix();
        RealMatrix bbt = b.multiply(b.transpose());
        for (int i = 0; i < covariance.getRowDimension(); ++i) {
            for (int j = 0; j < covariance.getColumnDimension(); ++j) {
                Assert.assertEquals(covariance.getEntry(i, j), bbt.getEntry(i, j), 1.0e-12);
            }
        }
    }

    @Test
    public void testMeanAndCovariance() {

        VectorialMean meanStat = new VectorialMean(mean.length);
        VectorialCovariance covStat = new VectorialCovariance(mean.length, true);
        for (int i = 0; i < 5000; ++i) {
            double[] v = generator.nextVector();
            meanStat.increment(v);
            covStat.increment(v);
        }

        double[] estimatedMean = meanStat.getResult();
        RealMatrix estimatedCovariance = covStat.getResult();
        for (int i = 0; i < estimatedMean.length; ++i) {
            Assert.assertEquals(mean[i], estimatedMean[i], 0.07);
            for (int j = 0; j <= i; ++j) {
                Assert.assertEquals(covariance.getEntry(i, j),
                                    estimatedCovariance.getEntry(i, j),
                                    0.1 * (1.0 + FastMath.abs(mean[i])) * (1.0 + FastMath.abs(mean[j])));
            }
        }

    }

    @Test
    public void testSampleWithZeroCovariance() {
        final double[][] covMatrix1 = new double[][]{
                {0.013445532, 0.010394690, 0.009881156, 0.010499559},
                {0.010394690, 0.023006616, 0.008196856, 0.010732709},
                {0.009881156, 0.008196856, 0.019023866, 0.009210099},
                {0.010499559, 0.010732709, 0.009210099, 0.019107243}
        };

        final double[][] covMatrix2 = new double[][]{
                {0.0, 0.0, 0.0, 0.0, 0.0},
                {0.0, 0.013445532, 0.010394690, 0.009881156, 0.010499559},
                {0.0, 0.010394690, 0.023006616, 0.008196856, 0.010732709},
                {0.0, 0.009881156, 0.008196856, 0.019023866, 0.009210099},
                {0.0, 0.010499559, 0.010732709, 0.009210099, 0.019107243}
        };

        final double[][] covMatrix3 = new double[][]{
                {0.013445532, 0.010394690, 0.0, 0.009881156, 0.010499559},
                {0.010394690, 0.023006616, 0.0, 0.008196856, 0.010732709},
                {0.0, 0.0, 0.0, 0.0, 0.0},
                {0.009881156, 0.008196856, 0.0, 0.019023866, 0.009210099},
                {0.010499559, 0.010732709, 0.0, 0.009210099, 0.019107243}
        };

        testSampler(covMatrix1, 10000, 0.001);
        testSampler(covMatrix2, 10000, 0.001);
        testSampler(covMatrix3, 10000, 0.001);

    }

    private CorrelatedRandomVectorGenerator createSampler(double[][] cov) {
        RealMatrix matrix = new Array2DRowRealMatrix(cov);
        double small = 10e-12 * matrix.getNorm();
        return new CorrelatedRandomVectorGenerator(
                new double[cov.length],
                matrix,
                small,
                new GaussianRandomGenerator(new Well1024a(0x366a26b94e520f41l)));
    }

    private void testSampler(final double[][] covMatrix, int samples, double epsilon) {
        CorrelatedRandomVectorGenerator sampler = createSampler(covMatrix);

        StorelessCovariance cov = new StorelessCovariance(covMatrix.length);
        for (int i = 0; i < samples; ++i) {
            cov.increment(sampler.nextVector());
        }

        double[][] sampleCov = cov.getData();
        for (int r = 0; r < covMatrix.length; ++r) {
            TestUtils.assertEquals(covMatrix[r], sampleCov[r], epsilon);
        }

    }

}
