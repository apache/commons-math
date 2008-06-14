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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public abstract class AbstractMultipleLinearRegressionTest {

    protected MultipleLinearRegression regression;

    @Before
    public void setUp(){
        regression = createRegression();
    }

    protected abstract MultipleLinearRegression createRegression();
    
    protected abstract int getNumberOfRegressors();
    
    protected abstract int getSampleSize();

    @Test
    public void canEstimateRegressionParameters(){
        double[] beta = regression.estimateRegressionParameters();        
        assertEquals(getNumberOfRegressors(), beta.length);
    }

    @Test
    public void canEstimateResiduals(){
        double[] e = regression.estimateResiduals();
        assertEquals(getSampleSize(), e.length);
    }
    
    @Test
    public void canEstimateRegressionParametersVariance(){
        double[][] variance = regression.estimateRegressionParametersVariance();
        assertEquals(getNumberOfRegressors(), variance.length);
    }

    @Test
    public void canEstimateRegressandVariance(){
        double variance = regression.estimateRegressandVariance();
        assertTrue(variance > 0.0);
    }   
    
    @Test(expected=IllegalArgumentException.class)
    public void cannotAddXSampleData() {
        regression.addData(new double[]{}, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotAddNullYSampleData() {
        regression.addData(null, new double[][]{}, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void cannotAddSampleDataWithSizeMismatch() {
        double[] y = new double[]{1.0, 2.0};
        double[][] x = new double[1][];
        x[0] = new double[]{1.0, 0};
        regression.addData(y, x, null);
    }
    
    /**
     * Loads model Y[] and X[][] arrays from a flat array of data.
     * Assumes that rows are concatenated with y values first in each row.
     * 
     * @param data input data array
     * @param y vector of y values to be filled
     * @param x matrix of x values to be filled
     * @param nobs number of observations (rows)
     * @param nvars number of independent variables (columnns, not counting y)
     */
    protected void loadModelData(double[] data, double[] y, double[][] x, int nobs, int nvars) {
        int pointer = 0;
        for (int i = 0; i < nobs; i++) {
            y[i] = data[pointer++];
            x[i][0] = 1.0d;
            for (int j = 1; j < nvars + 1; j++) {
                x[i][j] = data[pointer++];
            }
        }
        
    }

}
