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
package org.apache.commons.math4.legacy.random;

import java.util.Arrays;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.Assert;

import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.stat.correlation.StorelessCovariance;
import org.apache.commons.math4.legacy.stat.descriptive.moment.VectorialCovariance;
import org.apache.commons.math4.legacy.stat.descriptive.moment.VectorialMean;
import org.apache.commons.math4.core.jdkmath.JdkMath;

public class CorrelatedVectorFactoryTest {
    private double[] mean;
    private RealMatrix covariance;
    private Supplier<double[]> generator;

    public CorrelatedVectorFactoryTest() {
        mean = new double[] { 0.0, 1.0, -3.0, 2.3 };

        final RealMatrix b = MatrixUtils.createRealMatrix(4, 3);
        int counter = 0;
        for (int i = 0; i < b.getRowDimension(); ++i) {
            for (int j = 0; j < b.getColumnDimension(); ++j) {
                b.setEntry(i, j, 1.0 + 0.1 * ++counter);
            }
        }
        final RealMatrix bbt = b.multiply(b.transpose());
        covariance = MatrixUtils.createRealMatrix(mean.length, mean.length);
        for (int i = 0; i < covariance.getRowDimension(); ++i) {
            covariance.setEntry(i, i, bbt.getEntry(i, i));
            for (int j = 0; j < covariance.getColumnDimension(); ++j) {
                double s = bbt.getEntry(i, j);
                covariance.setEntry(i, j, s);
                covariance.setEntry(j, i, s);
            }
        }

        generator = new CorrelatedVectorFactory(mean,
                                                covariance,
                                                1e-12 * covariance.getNorm())
            .gaussian(RandomSource.KISS.create());
    }

    @Test
    public void testMath226() {
        final double[] mean = { 1, 1, 10, 1 };
        final double[][] cov = {
            { 1, 3, 2, 6 },
            { 3, 13, 16, 2 },
            { 2, 16, 38, -1 },
            { 6, 2, -1, 197 }
        };
        final RealMatrix covRM = MatrixUtils.createRealMatrix(cov);
        final Supplier<double[]> sg = new CorrelatedVectorFactory(mean, covRM, 1e-5)
            .gaussian(RandomSource.WELL_1024_A.create());

        final double[] min = new double[mean.length];
        Arrays.fill(min, Double.POSITIVE_INFINITY);
        final double[] max = new double[mean.length];
        Arrays.fill(max, Double.NEGATIVE_INFINITY);
        for (int i = 0; i < 10; i++) {
            double[] generated = sg.get();
            for (int j = 0; j < generated.length; ++j) {
                min[j] = JdkMath.min(min[j], generated[j]);
                max[j] = JdkMath.max(max[j], generated[j]);
            }
        }
        for (int j = 0; j < min.length; ++j) {
            Assert.assertTrue(max[j] - min[j] > 2.0);
        }
    }

    @Test
    public void testMeanAndCovariance() {
        final VectorialMean meanStat = new VectorialMean(mean.length);
        final VectorialCovariance covStat = new VectorialCovariance(mean.length, true);
        for (int i = 0; i < 5000; ++i) {
            final double[] v = generator.get();
            meanStat.increment(v);
            covStat.increment(v);
        }

        final double[] estimatedMean = meanStat.getResult();
        final RealMatrix estimatedCovariance = covStat.getResult();
        for (int i = 0; i < estimatedMean.length; ++i) {
            Assert.assertEquals(mean[i], estimatedMean[i], 0.07);
            for (int j = 0; j <= i; ++j) {
                Assert.assertEquals(covariance.getEntry(i, j),
                                    estimatedCovariance.getEntry(i, j),
                                    1e-1 * (1 + JdkMath.abs(mean[i])) * (1 + JdkMath.abs(mean[j])));
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

        testSampler(covMatrix1, 10000, 1e-3);
        testSampler(covMatrix2, 10000, 1e-3);
        testSampler(covMatrix3, 10000, 1e-3);
    }

    private Supplier<double[]> createSampler(double[][] cov) {
        final RealMatrix matrix = new Array2DRowRealMatrix(cov);
        final double small = 1e-12 * matrix.getNorm();
        return new CorrelatedVectorFactory(matrix, small)
            .gaussian(RandomSource.WELL_1024_A.create());
    }

    private void testSampler(final double[][] covMatrix,
                             int samples,
                             double epsilon) {
        final Supplier<double[]> sampler = createSampler(covMatrix);

        final StorelessCovariance cov = new StorelessCovariance(covMatrix.length);
        for (int i = 0; i < samples; ++i) {
            cov.increment(sampler.get());
        }

        final double[][] sampleCov = cov.getData();
        for (int r = 0; r < covMatrix.length; ++r) {
            TestUtils.assertEquals(covMatrix[r], sampleCov[r], epsilon);
        }
    }
}
