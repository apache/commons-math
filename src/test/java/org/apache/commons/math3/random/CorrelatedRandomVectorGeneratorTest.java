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

package org.apache.commons.math3.random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.moment.VectorialCovariance;
import org.apache.commons.math3.stat.descriptive.moment.VectorialMean;
import org.apache.commons.math3.util.FastMath;

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
        Assert.assertEquals(3, generator.getRank());
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

        for (int i = 0; i < 10; i++) {
            double[] generated = sg.nextVector();
            Assert.assertTrue(FastMath.abs(generated[0] - 1) > 0.1);
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
}
