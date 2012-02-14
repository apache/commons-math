/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.apache.commons.math3.filter;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixDimensionMismatchException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link KalmanFilter}.
 *
 * @version $Id$
 */
public class KalmanFilterTest {
    
    @Test(expected=MatrixDimensionMismatchException.class)
    public void testTransitionMeasurementMatrixMismatch() {
        
        // A and H matrix do not match in dimensions
        
        // A = [ 1 ]
        RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
        // no control input
        RealMatrix B = null;
        // H = [ 1 1 ]
        RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d, 1d });
        // Q = [ 0 ]
        RealMatrix Q = new Array2DRowRealMatrix(new double[] { 0 });
        // R = [ 0 ]
        RealMatrix R = new Array2DRowRealMatrix(new double[] { 0 });

        ProcessModel pm
            = new DefaultProcessModel(A, B, Q,
                                      new ArrayRealVector(new double[] { 0 }), null);
        MeasurementModel mm = new DefaultMeasurementModel(H, R);
        new KalmanFilter(pm, mm);
        Assert.fail("transition and measurement matrix should not be compatible");
    }

    @Test(expected=MatrixDimensionMismatchException.class)
    public void testTransitionControlMatrixMismatch() {
        
        // A and B matrix do not match in dimensions
        
        // A = [ 1 ]
        RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
        // B = [ 1 1 ]
        RealMatrix B = new Array2DRowRealMatrix(new double[] { 1d, 1d });
        // H = [ 1 ]
        RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });
        // Q = [ 0 ]
        RealMatrix Q = new Array2DRowRealMatrix(new double[] { 0 });
        // R = [ 0 ]
        RealMatrix R = new Array2DRowRealMatrix(new double[] { 0 });

        ProcessModel pm
            = new DefaultProcessModel(A, B, Q,
                                      new ArrayRealVector(new double[] { 0 }), null);
        MeasurementModel mm = new DefaultMeasurementModel(H, R);
        new KalmanFilter(pm, mm);
        Assert.fail("transition and control matrix should not be compatible");
    }
    
    @Test
    public void testConstant() {
        // simulates a simple process with a constant state and no control input
        
        double constantValue = 10d;
        double measurementNoise = 0.1d;
        double processNoise = 1e-5d;

        // A = [ 1 ]
        RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
        // no control input
        RealMatrix B = null;
        // H = [ 1 ]
        RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });
        // x = [ 10 ]
        RealVector x = new ArrayRealVector(new double[] { constantValue });
        // Q = [ 1e-5 ]
        RealMatrix Q = new Array2DRowRealMatrix(new double[] { processNoise });
        // R = [ 0.1 ]
        RealMatrix R = new Array2DRowRealMatrix(new double[] { measurementNoise });

        ProcessModel pm
            = new DefaultProcessModel(A, B, Q,
                                      new ArrayRealVector(new double[] { constantValue }), null);
        MeasurementModel mm = new DefaultMeasurementModel(H, R);
        KalmanFilter filter = new KalmanFilter(pm, mm);

        Assert.assertEquals(1, filter.getMeasurementDimension());
        Assert.assertEquals(1, filter.getStateDimension());

        assertMatrixEquals(Q.getData(), filter.getErrorCovariance());

        // check the initial state
        double[] expectedInitialState = new double[] { constantValue };
        assertVectorEquals(expectedInitialState, filter.getStateEstimation());

        RealVector pNoise = new ArrayRealVector(1);
        RealVector mNoise = new ArrayRealVector(1);

        RandomGenerator rand = new JDKRandomGenerator();
        // iterate 60 steps
        for (int i = 0; i < 60; i++) {
            filter.predict();

            // Simulate the process
            pNoise.setEntry(0, processNoise * rand.nextGaussian());

            // x = A * x + p_noise
            x = A.operate(x).add(pNoise);

            // Simulate the measurement
            mNoise.setEntry(0, measurementNoise * rand.nextGaussian());

            // z = H * x + m_noise
            RealVector z = H.operate(x).add(mNoise);

            filter.correct(z);

            // state estimate shouldn't be larger than measurement noise
            double diff = Math.abs(constantValue - filter.getStateEstimation()[0]);
            // System.out.println(diff);
            Assert.assertTrue(Precision.compareTo(diff, measurementNoise, 1e-6) < 0);
        }

        // error covariance should be already very low (< 0.02)
        Assert.assertTrue(Precision.compareTo(filter.getErrorCovariance()[0][0],
                                              0.02d, 1e-6) < 0);
    }

    @Test
    public void testConstantAcceleration() {
        // simulates a vehicle, accelerating at a constant rate (0.1 m/s)

        // discrete time interval
        double dt = 0.1d;
        // position measurement noise (meter)
        double measurementNoise = 10d;
        // acceleration noise (meter/sec^2)
        double accelNoise = 0.2d;

        // A = [ 1 dt ]
        //     [ 0  1 ]
        RealMatrix A = new Array2DRowRealMatrix(new double[][] { { 1, dt }, { 0, 1 } });

        // B = [ dt^2/2 ]
        //     [ dt     ]
        RealMatrix B = new Array2DRowRealMatrix(
                new double[][] { { Math.pow(dt, 2d) / 2d }, { dt } });

        // H = [ 1 0 ]
        RealMatrix H = new Array2DRowRealMatrix(new double[][] { { 1d, 0d } });

        // x = [ 0 0 ]
        RealVector x = new ArrayRealVector(new double[] { 0, 0 });

        RealMatrix tmp = new Array2DRowRealMatrix(
                new double[][] { { Math.pow(dt, 4d) / 4d, Math.pow(dt, 3d) / 2d },
                                 { Math.pow(dt, 3d) / 2d, Math.pow(dt, 2d) } });

        // Q = [ dt^4/4 dt^3/2 ]
        //     [ dt^3/2 dt^2   ]
        RealMatrix Q = tmp.scalarMultiply(Math.pow(accelNoise, 2));

        // P0 = [ 1 1 ]
        //      [ 1 1 ]
        RealMatrix P0 = new Array2DRowRealMatrix(new double[][] { { 1, 1 }, { 1, 1 } });

        // R = [ measurementNoise^2 ]
        RealMatrix R = new Array2DRowRealMatrix(
                new double[] { Math.pow(measurementNoise, 2) });

        // constant control input, increase velocity by 0.1 m/s per cycle
        RealVector u = new ArrayRealVector(new double[] { 0.1d });

        ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
        MeasurementModel mm = new DefaultMeasurementModel(H, R);
        KalmanFilter filter = new KalmanFilter(pm, mm);

        Assert.assertEquals(1, filter.getMeasurementDimension());
        Assert.assertEquals(2, filter.getStateDimension());

        assertMatrixEquals(P0.getData(), filter.getErrorCovariance());

        // check the initial state
        double[] expectedInitialState = new double[] { 0.0, 0.0 };
        assertVectorEquals(expectedInitialState, filter.getStateEstimation());

        RandomGenerator rand = new JDKRandomGenerator();

        RealVector tmpPNoise = new ArrayRealVector(
                new double[] { Math.pow(dt, 2d) / 2d, dt });

        RealVector mNoise = new ArrayRealVector(1);

        // iterate 60 steps
        for (int i = 0; i < 60; i++) {
            filter.predict(u);

            // Simulate the process
            RealVector pNoise = tmpPNoise.mapMultiply(accelNoise * rand.nextGaussian());

            // x = A * x + B * u + pNoise
            x = A.operate(x).add(B.operate(u)).add(pNoise);

            // Simulate the measurement
            mNoise.setEntry(0, measurementNoise * rand.nextGaussian());

            // z = H * x + m_noise
            RealVector z = H.operate(x).add(mNoise);

            filter.correct(z);

            // state estimate shouldn't be larger than the measurement noise
            double diff = Math.abs(x.getEntry(0) - filter.getStateEstimation()[0]);
            Assert.assertTrue(Precision.compareTo(diff, measurementNoise, 1e-6) < 0);
        }

        // error covariance of the velocity should be already very low (< 0.1)
        Assert.assertTrue(Precision.compareTo(filter.getErrorCovariance()[1][1],
                                              0.1d, 1e-6) < 0);
    }
    
    private void assertVectorEquals(double[] expected, double[] result) {
        Assert.assertEquals("Wrong number of rows.", expected.length,
                            result.length);
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals("Wrong value at position [" + i + "]",
                                expected[i], result[i], 1.0e-6);
        }
    }

    private void assertMatrixEquals(double[][] expected, double[][] result) {
        Assert.assertEquals("Wrong number of rows.", expected.length,
                            result.length);
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals("Wrong number of columns.", expected[i].length,
                                result[i].length);
            for (int j = 0; j < expected[i].length; j++) {
                Assert.assertEquals("Wrong value at position [" + i + "," + j
                                    + "]", expected[i][j], result[i][j], 1.0e-6);
            }
        }
    }
}
