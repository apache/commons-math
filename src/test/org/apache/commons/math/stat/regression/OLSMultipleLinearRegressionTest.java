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

import org.junit.Before;
import org.junit.Test;
import org.apache.commons.math.TestUtils;

public class OLSMultipleLinearRegressionTest extends AbstractMultipleLinearRegressionTest {

    private double[] y;
    private double[][] x;
    
    @Before
    public void setUp(){
        y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
        x = new double[6][];
        x[0] = new double[]{1.0, 0, 0, 0, 0, 0};
        x[1] = new double[]{1.0, 2.0, 0, 0, 0, 0};
        x[2] = new double[]{1.0, 0, 3.0, 0, 0, 0};
        x[3] = new double[]{1.0, 0, 0, 4.0, 0, 0};
        x[4] = new double[]{1.0, 0, 0, 0, 5.0, 0};
        x[5] = new double[]{1.0, 0, 0, 0, 0, 6.0};
        super.setUp();
    }

    protected MultipleLinearRegression createRegression() {
        MultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.addData(y, x, null);
        return regression;
    }

    protected int getNumberOfRegressors() {
        return x[0].length;
    }

    protected int getSampleSize() {
        return y.length;
    }
    
    @Test
    public void testPerfectFit() {
        double[] betaHat = regression.estimateRegressionParameters();
        TestUtils.assertEquals(betaHat, 
          new double[]{11.0,0.5,0.666666666666667,0.75,0.8,0.8333333333333333},
                1e-12);
        double[] residuals = regression.estimateResiduals();
        TestUtils.assertEquals(residuals, new double[]{0d,0d,0d,0d,0d,0d},
                      1e-12);
        double[][] errors = regression.estimateRegressionParametersVariance();
        // TODO: translate this into standard error vector and check
    }
    
    
    /**
     * Test Longley dataset against certified values provided by NIST.
     * Data Source: J. Longley (1967) "An Appraisal of Least Squares
     * Programs for the Electronic Computer from the Point of View of the User"
     * Journal of the American Statistical Association, vol. 62. September,
     * pp. 819-841.
     * 
     * Certified values (and data) are from NIST:
     * http://www.itl.nist.gov/div898/strd/lls/data/LINKS/DATA/Longley.dat
     */
    @Test
    public void testLongly() {
        // Y values are first, then independent vars
        // Each row is one observation
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
        
        // Transform to Y and X required by interface
        double[] y = new double[16];
        double[][] x = new double[16][7];
        int pointer = 0;
        for (int i = 0; i < 16; i++) {
            y[i] = design[pointer++];
            x[i][0] = 1.0d;
            for (int j = 1; j < 7; j++) {
                x[i][j] = design[pointer++];
            }
        }
        
        // Estimate the model
        MultipleLinearRegression model = new OLSMultipleLinearRegression();
        model.addData(y, x, null);
        
        // Check expected beta values from NIST
        double[] betaHat = model.estimateRegressionParameters();
        TestUtils.assertEquals(betaHat, 
          new double[]{-3482258.63459582, 15.0618722713733,
                -0.358191792925910E-01,-2.02022980381683,
                -1.03322686717359,-0.511041056535807E-01,
                 1829.15146461355}, 1E-1); // <- UGH! need better accuracy!
        
        // Check expected residuals from R
        double[] residuals = model.estimateResiduals();
        TestUtils.assertEquals(residuals, new double[]{
                267.340029759711,-94.0139423988359,46.28716775752924,
                -410.114621930906,309.7145907602313,-249.3112153297231,
                -164.0489563956039,-13.18035686637081,14.30477260005235,
                 455.394094551857,-17.26892711483297,-39.0550425226967,
                -155.5499735953195,-85.6713080421283,341.9315139607727,
                -206.7578251937366},
                      1E-2); // <- UGH again! need better accuracy!
        
        // Check standard errors from NIST
        double[][] errors = model.estimateRegressionParametersVariance();
        //TODO:  translate this into std error vector and check
        
    }
}
