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

package org.apache.commons.math4.filter;

import org.apache.commons.math4.distribution.NormalDistribution;
import org.apache.commons.math4.filter.DefaultMeasurementModel;
import org.apache.commons.math4.filter.DefaultProcessModel;
import org.apache.commons.math4.filter.KalmanFilter;
import org.apache.commons.math4.filter.MeasurementModel;
import org.apache.commons.math4.filter.ProcessModel;
import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.ArrayRealVector;
import org.apache.commons.math4.linear.MatrixDimensionMismatchException;
import org.apache.commons.math4.linear.MatrixUtils;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.linear.RealVector;
import org.apache.commons.math4.random.JDKRandomGenerator;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.Well19937c;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Precision;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link KalmanFilter}.
 *
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
            double diff = FastMath.abs(constantValue - filter.getStateEstimation()[0]);
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
                new double[][] { { FastMath.pow(dt, 2d) / 2d }, { dt } });

        // H = [ 1 0 ]
        RealMatrix H = new Array2DRowRealMatrix(new double[][] { { 1d, 0d } });

        // x = [ 0 0 ]
        RealVector x = new ArrayRealVector(new double[] { 0, 0 });

        RealMatrix tmp = new Array2DRowRealMatrix(
                new double[][] { { FastMath.pow(dt, 4d) / 4d, FastMath.pow(dt, 3d) / 2d },
                                 { FastMath.pow(dt, 3d) / 2d, FastMath.pow(dt, 2d) } });

        // Q = [ dt^4/4 dt^3/2 ]
        //     [ dt^3/2 dt^2   ]
        RealMatrix Q = tmp.scalarMultiply(FastMath.pow(accelNoise, 2));

        // P0 = [ 1 1 ]
        //      [ 1 1 ]
        RealMatrix P0 = new Array2DRowRealMatrix(new double[][] { { 1, 1 }, { 1, 1 } });

        // R = [ measurementNoise^2 ]
        RealMatrix R = new Array2DRowRealMatrix(
                new double[] { FastMath.pow(measurementNoise, 2) });

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
                new double[] { FastMath.pow(dt, 2d) / 2d, dt });

        // iterate 60 steps
        for (int i = 0; i < 60; i++) {
            filter.predict(u);

            // Simulate the process
            RealVector pNoise = tmpPNoise.mapMultiply(accelNoise * rand.nextGaussian());

            // x = A * x + B * u + pNoise
            x = A.operate(x).add(B.operate(u)).add(pNoise);

            // Simulate the measurement
            double mNoise = measurementNoise * rand.nextGaussian();

            // z = H * x + m_noise
            RealVector z = H.operate(x).mapAdd(mNoise);

            filter.correct(z);

            // state estimate shouldn't be larger than the measurement noise
            double diff = FastMath.abs(x.getEntry(0) - filter.getStateEstimation()[0]);
            Assert.assertTrue(Precision.compareTo(diff, measurementNoise, 1e-6) < 0);
        }

        // error covariance of the velocity should be already very low (< 0.1)
        Assert.assertTrue(Precision.compareTo(filter.getErrorCovariance()[1][1],
                                              0.1d, 1e-6) < 0);
    }

    /**
     * Represents an idealized Cannonball only taking into account gravity.
     */
    public static class Cannonball {

        private final double[] gravity = { 0, -9.81 };

        private final double[] velocity;
        private final double[] location;

        private double timeslice;

        public Cannonball(double timeslice, double angle, double initialVelocity) {
            this.timeslice = timeslice;

            final double angleInRadians = FastMath.toRadians(angle);
            this.velocity = new double[] {
                    initialVelocity * FastMath.cos(angleInRadians),
                    initialVelocity * FastMath.sin(angleInRadians)
            };

            this.location = new double[] { 0, 0 };
        }

        public double getX() {
            return location[0];
        }

        public double getY() {
            return location[1];
        }

        public double getXVelocity() {
            return velocity[0];
        }

        public double getYVelocity() {
            return velocity[1];
        }

        public void step() {
            // break gravitational force into a smaller time slice.
            double[] slicedGravity = gravity.clone();
            for ( int i = 0; i < slicedGravity.length; i++ ) {
                slicedGravity[i] *= timeslice;
            }

            // apply the acceleration to velocity.
            double[] slicedVelocity = velocity.clone();
            for ( int i = 0; i < velocity.length; i++ ) {
                velocity[i] += slicedGravity[i];
                slicedVelocity[i] = velocity[i] * timeslice;
                location[i] += slicedVelocity[i];
            }

            // cannonballs shouldn't go into the ground.
            if ( location[1] < 0 ) {
                location[1] = 0;
            }
        }
    }

    @Test
    public void testCannonball() {
        // simulates the flight of a cannonball (only taking gravity and initial thrust into account)

        // number of iterations
        final int iterations = 144;
        // discrete time interval
        final double dt = 0.1d;
        // position measurement noise (meter)
        final double measurementNoise = 30d;
        // the initial velocity of the cannonball
        final double initialVelocity = 100;
        // shooting angle
        final double angle = 45;

        final Cannonball cannonball = new Cannonball(dt, angle, initialVelocity);

        final double speedX = cannonball.getXVelocity();
        final double speedY = cannonball.getYVelocity();

        // A = [ 1, dt, 0,  0 ]  =>  x(n+1) = x(n) + vx(n)
        //     [ 0,  1, 0,  0 ]  => vx(n+1) =        vx(n)
        //     [ 0,  0, 1, dt ]  =>  y(n+1) =              y(n) + vy(n)
        //     [ 0,  0, 0,  1 ]  => vy(n+1) =                     vy(n)
        final RealMatrix A = MatrixUtils.createRealMatrix(new double[][] {
                { 1, dt, 0,  0 },
                { 0,  1, 0,  0 },
                { 0,  0, 1, dt },
                { 0,  0, 0,  1 }
        });

        // The control vector, which adds acceleration to the kinematic equations.
        // 0          =>  x(n+1) =  x(n+1)
        // 0          => vx(n+1) = vx(n+1)
        // -9.81*dt^2 =>  y(n+1) =  y(n+1) - 1/2 * 9.81 * dt^2
        // -9.81*dt   => vy(n+1) = vy(n+1) - 9.81 * dt
        final RealVector controlVector =
                MatrixUtils.createRealVector(new double[] { 0, 0, 0.5 * -9.81 * dt * dt, -9.81 * dt } );

        // The control matrix B only expects y and vy, see control vector
        final RealMatrix B = MatrixUtils.createRealMatrix(new double[][] {
                { 0, 0, 0, 0 },
                { 0, 0, 0, 0 },
                { 0, 0, 1, 0 },
                { 0, 0, 0, 1 }
        });

        // We only observe the x/y position of the cannonball
        final RealMatrix H = MatrixUtils.createRealMatrix(new double[][] {
                { 1, 0, 0, 0 },
                { 0, 0, 0, 0 },
                { 0, 0, 1, 0 },
                { 0, 0, 0, 0 }
        });

        // our guess of the initial state.
        final RealVector initialState = MatrixUtils.createRealVector(new double[] { 0, speedX, 0, speedY } );

        // the initial error covariance matrix, the variance = noise^2
        final double var = measurementNoise * measurementNoise;
        final RealMatrix initialErrorCovariance = MatrixUtils.createRealMatrix(new double[][] {
                { var,    0,   0,    0 },
                {   0, 1e-3,   0,    0 },
                {   0,    0, var,    0 },
                {   0,    0,   0, 1e-3 }
        });

        // we assume no process noise -> zero matrix
        final RealMatrix Q = MatrixUtils.createRealMatrix(4, 4);

        // the measurement covariance matrix
        final RealMatrix R = MatrixUtils.createRealMatrix(new double[][] {
                { var,    0,   0,    0 },
                {   0, 1e-3,   0,    0 },
                {   0,    0, var,    0 },
                {   0,    0,   0, 1e-3 }
        });

        final ProcessModel pm = new DefaultProcessModel(A, B, Q, initialState, initialErrorCovariance);
        final MeasurementModel mm = new DefaultMeasurementModel(H, R);
        final KalmanFilter filter = new KalmanFilter(pm, mm);

        final RandomGenerator rng = new Well19937c(1000);
        final NormalDistribution dist = new NormalDistribution(rng, 0, measurementNoise);

        for (int i = 0; i < iterations; i++) {
            // get the "real" cannonball position
            double x = cannonball.getX();
            double y = cannonball.getY();

            // apply measurement noise to current cannonball position
            double nx = x + dist.sample();
            double ny = y + dist.sample();

            cannonball.step();

            filter.predict(controlVector);
            // correct the filter with our measurements
            filter.correct(new double[] { nx, 0, ny, 0 } );

            // state estimate shouldn't be larger than the measurement noise
            double diff = FastMath.abs(cannonball.getY() - filter.getStateEstimation()[2]);
            Assert.assertTrue(Precision.compareTo(diff, measurementNoise, 1e-6) < 0);
        }

        // error covariance of the x/y-position should be already very low (< 3m std dev = 9 variance)

        Assert.assertTrue(Precision.compareTo(filter.getErrorCovariance()[0][0],
                                              9, 1e-6) < 0);

        Assert.assertTrue(Precision.compareTo(filter.getErrorCovariance()[2][2],
                                              9, 1e-6) < 0);
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
