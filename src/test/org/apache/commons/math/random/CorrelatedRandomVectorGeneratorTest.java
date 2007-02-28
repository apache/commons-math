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

package org.apache.commons.math.random;

import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;
import org.apache.commons.math.stat.descriptive.moment.VectorialCovariance;
import org.apache.commons.math.stat.descriptive.moment.VectorialMean;

import junit.framework.*;

public class CorrelatedRandomVectorGeneratorTest
extends TestCase {

    public CorrelatedRandomVectorGeneratorTest(String name) {
        super(name);
        mean       = null;
        covariance = null;
        generator  = null;
    }

    public void testRank() {
        assertEquals(3, generator.getRank());
    }

    public void testRootMatrix() {
        RealMatrix b = generator.getRootMatrix();
        RealMatrix bbt = b.multiply(b.transpose());
        for (int i = 0; i < covariance.getRowDimension(); ++i) {
            for (int j = 0; j < covariance.getColumnDimension(); ++j) {
                assertEquals(covariance.getEntry(i, j), bbt.getEntry(i, j), 1.0e-12);
            }
        }
    }

    public void testMeanAndCovariance() throws DimensionMismatchException {

        VectorialMean meanStat = new VectorialMean(mean.length);
        VectorialCovariance covStat = new VectorialCovariance(mean.length);
        for (int i = 0; i < 5000; ++i) {
            double[] v = generator.nextVector();
            meanStat.increment(v);
            covStat.increment(v);
        }

        double[] estimatedMean = meanStat.getResult();
        RealMatrix estimatedCovariance = covStat.getResult();
        for (int i = 0; i < estimatedMean.length; ++i) {
            assertEquals(mean[i], estimatedMean[i], 0.07);
            for (int j = 0; j <= i; ++j) {
                assertEquals(covariance.getEntry(i, j),
                        estimatedCovariance.getEntry(i, j),
                        0.1 * (1.0 + Math.abs(mean[i])) * (1.0 + Math.abs(mean[j])));
            }
        }

    }

    public void setUp() {
        try {
            mean = new double[] { 0.0, 1.0, -3.0, 2.3};

            RealMatrixImpl b = new RealMatrixImpl(4, 3);
            double[][] bData = b.getDataRef();
            int counter = 0;
            for (int i = 0; i < bData.length; ++i) {
                double[] bi = bData[i];
                for (int j = 0; j < b.getColumnDimension(); ++j) {
                    bi[j] = 1.0 + 0.1 * ++counter;
                }
            }
            RealMatrix bbt = b.multiply(b.transpose());
            covariance = new RealMatrixImpl(mean.length, mean.length);
            double[][] covData = covariance.getDataRef();
            for (int i = 0; i < covariance.getRowDimension(); ++i) {
                covData[i][i] = bbt.getEntry(i, i);
                for (int j = 0; j < covariance.getColumnDimension(); ++j) {
                    double s = bbt.getEntry(i, j);
                    covData[i][j] = s;
                    covData[j][i] = s;
                }
            }

            RandomGenerator rg = new JDKRandomGenerator();
            rg.setSeed(17399225432l);
            GaussianRandomGenerator rawGenerator = new GaussianRandomGenerator(rg);
            generator = new CorrelatedRandomVectorGenerator(mean,
                                                            covariance,
                                                            1.0e-12 * covariance.getNorm(),
                                                            rawGenerator);
        } catch (DimensionMismatchException e) {
            fail(e.getMessage());
        } catch (NotPositiveDefiniteMatrixException e) {
            fail("not positive definite matrix");
        }
    }

    public void tearDown() {
        mean       = null;
        covariance = null;
        generator  = null;
    }

    public static Test suite() {
        return new TestSuite(CorrelatedRandomVectorGeneratorTest.class);
    }

    private double[] mean;
    private RealMatrixImpl covariance;
    private CorrelatedRandomVectorGenerator generator;

}
