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

public class GLSMultipleLinearRegressionTest extends MultipleLinearRegressionAbstractTest {

    private double[] y;
    private double[][] x;
    private double[][] omega;

    @Before
    @Override
    public void setUp(){
        y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
        x = new double[6][];
        x[0] = new double[]{1.0, 0, 0, 0, 0, 0};
        x[1] = new double[]{1.0, 2.0, 0, 0, 0, 0};
        x[2] = new double[]{1.0, 0, 3.0, 0, 0, 0};
        x[3] = new double[]{1.0, 0, 0, 4.0, 0, 0};
        x[4] = new double[]{1.0, 0, 0, 0, 5.0, 0};
        x[5] = new double[]{1.0, 0, 0, 0, 0, 6.0};
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
        return x[0].length;
    }

    @Override
    protected int getSampleSize() {
        return y.length;
    }

}
