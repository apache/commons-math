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
package org.apache.commons.math.stat.regression;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

public class GLSMultipleLinearRegressionTest extends MultipleLinearRegressionAbstractTest {

    private double[] y;
    private double[][] x;
    private double[][] omega;

    @Before
    @Override
    public void setUp(){
        y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
        x = new double[6][];
        x[0] = new double[]{0, 0, 0, 0, 0};
        x[1] = new double[]{2.0, 0, 0, 0, 0};
        x[2] = new double[]{0, 3.0, 0, 0, 0};
        x[3] = new double[]{0, 0, 4.0, 0, 0};
        x[4] = new double[]{0, 0, 0, 5.0, 0};
        x[5] = new double[]{0, 0, 0, 0, 6.0};
        omega = new double[6][];
        omega[0] = new double[]{1.0, 0, 0, 0, 0, 0};
        omega[1] = new double[]{0, 2.0, 0, 0, 0, 0};
        omega[2] = new double[]{0, 0, 3.0, 0, 0, 0};
        omega[3] = new double[]{0, 0, 0, 4.0, 0, 0};
        omega[4] = new double[]{0, 0, 0, 0, 5.0, 0};
        omega[5] = new double[]{0, 0, 0, 0, 0, 6.0};
        super.setUp();
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotAddXSampleData() {
        createRegression().newSampleData(new double[]{}, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotAddNullYSampleData() {
        createRegression().newSampleData(null, new double[][]{}, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotAddSampleDataWithSizeMismatch() {
        double[] y = new double[]{1.0, 2.0};
        double[][] x = new double[1][];
        x[0] = new double[]{1.0, 0};
        createRegression().newSampleData(y, x, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotAddNullCovarianceData() {
        createRegression().newSampleData(new double[]{}, new double[][]{}, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void notEnoughData() {
        double[]   reducedY = new double[y.length - 1];
        double[][] reducedX = new double[x.length - 1][];
        double[][] reducedO = new double[omega.length - 1][];
        System.arraycopy(y,     0, reducedY, 0, reducedY.length);
        System.arraycopy(x,     0, reducedX, 0, reducedX.length);
        System.arraycopy(omega, 0, reducedO, 0, reducedO.length);
        createRegression().newSampleData(reducedY, reducedX, reducedO);
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotAddCovarianceDataWithSampleSizeMismatch() {
        double[] y = new double[]{1.0, 2.0};
        double[][] x = new double[2][];
        x[0] = new double[]{1.0, 0};
        x[1] = new double[]{0, 1.0};
        double[][] omega = new double[1][];
        omega[0] = new double[]{1.0, 0};
        createRegression().newSampleData(y, x, omega);
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotAddCovarianceDataThatIsNotSquare() {
        double[] y = new double[]{1.0, 2.0};
        double[][] x = new double[2][];
        x[0] = new double[]{1.0, 0};
        x[1] = new double[]{0, 1.0};
        double[][] omega = new double[3][];
        omega[0] = new double[]{1.0, 0};
        omega[1] = new double[]{0, 1.0};
        omega[2] = new double[]{0, 2.0};
        createRegression().newSampleData(y, x, omega);
    }

    @Override
    protected GLSMultipleLinearRegression createRegression() {
        GLSMultipleLinearRegression regression = new GLSMultipleLinearRegression();
        regression.newSampleData(y, x, omega);
        return regression;
    }

    @Override
    protected int getNumberOfRegressors() {
        return x[0].length + 1;
    }

    @Override
    protected int getSampleSize() {
        return y.length;
    }

    /**
     * test calculateYVariance
     */
    @Test
    public void testYVariance() {

        // assumes: y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};

        GLSMultipleLinearRegression model = new GLSMultipleLinearRegression();
        model.newSampleData(y, x, omega);
        TestUtils.assertEquals(model.calculateYVariance(), 3.5, 0);
    }
    
    /**
     * Verifies that setting X, Y and covariance separately has the same effect as newSample(X,Y,cov).
     */
    @Test
    public void testNewSample2() throws Exception {
        double[] y = new double[] {1, 2, 3, 4}; 
        double[][] x = new double[][] {
          {19, 22, 33},
          {20, 30, 40},
          {25, 35, 45},
          {27, 37, 47}   
        };
        double[][] covariance = MatrixUtils.createRealIdentityMatrix(4).scalarMultiply(2).getData();
        GLSMultipleLinearRegression regression = new GLSMultipleLinearRegression();
        regression.newSampleData(y, x, covariance);
        RealMatrix combinedX = regression.X.copy();
        RealVector combinedY = regression.Y.copy();
        RealMatrix combinedCovInv = regression.getOmegaInverse();
        regression.newXSampleData(x);
        regression.newYSampleData(y);
        assertEquals(combinedX, regression.X);
        assertEquals(combinedY, regression.Y);
        assertEquals(combinedCovInv, regression.getOmegaInverse());
    }
    
    /**
     * Verifies that GLS with identity covariance matrix gives the same results
     * as OLS.
     */
    @Test
    public void testGLSOLSConsistency() throws Exception {
        // Use Longley data to test
        double[] design = new double[] {
                60323,83.0,234289,2356,1590,107608,1947,
                61122,88.5,259426,2325,1456,108632,1948,
                60171,88.2,258054,3682,1616,109773,1949,
                61187,89.5,284599,3351,1650,110929,1950,
                63221,96.2,328975,2099,3099,112075,1951,
                63639,98.1,346999,1932,3594,113270,1952,
                64989,99.0,365385,1870,3547,115094,1953,
                63761,100.0,363112,3578,3350,116219,1954,
                66019,101.2,397469,2904,3048,117388,1955,
                67857,104.6,419180,2822,2857,118734,1956,
                68169,108.4,442769,2936,2798,120445,1957,
                66513,110.8,444546,4681,2637,121950,1958,
                68655,112.6,482704,3813,2552,123366,1959,
                69564,114.2,502601,3931,2514,125368,1960,
                69331,115.7,518173,4806,2572,127852,1961,
                70551,116.9,554894,4007,2827,130081,1962
            };
        RealMatrix identityCov = MatrixUtils.createRealIdentityMatrix(16);
        GLSMultipleLinearRegression glsModel = new GLSMultipleLinearRegression();
        OLSMultipleLinearRegression olsModel = new OLSMultipleLinearRegression();
        glsModel.newSampleData(design, 16, 6);
        olsModel.newSampleData(design, 16, 6);
        glsModel.newCovarianceData(identityCov.getData());
        double[] olsBeta = olsModel.calculateBeta().getData();
        double[] glsBeta = glsModel.calculateBeta().getData();
        // TODO:  Should have assertRelativelyEquals(double[], double[], eps) in TestUtils
        //        Should also add RealVector and RealMatrix versions
        for (int i = 0; i < olsBeta.length; i++) {
            TestUtils.assertRelativelyEquals(olsBeta[i], glsBeta[i], 10E-7);
        }
    }
}
