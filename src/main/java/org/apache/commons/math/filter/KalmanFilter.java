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
package org.apache.commons.math.filter;

import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.CholeskyDecompositionImpl;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.MatrixDimensionMismatchException;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.NonSquareMatrixException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.linear.SingularMatrixException;
import org.apache.commons.math.util.MathUtils;

/**
 * Implementation of a Kalman filter to estimate the state <i>x<sub>k</sub> of a
 * discrete-time controlled process that is governed by the linear stochastic
 * difference equation:
 *
 * <pre>
 * <i>x<sub>k</sub> = <b>A</b><i>x<sub>k-1</sub> + <b>B</b><i>u<sub>k-1</sub> + <i>w<sub>k-1</sub>
 * </pre>
 *
 * with a measurement <i>x<sub>k</sub> that is
 *
 * <pre>
 * <i>z<sub>k</sub> = <b>H</b><i>x<sub>k</sub> + <i>v<sub>k</sub>.
 * </pre>
 *
 * The random variables <i>w<sub>k</sub> and <i>v<sub>k</sub> represent the
 * process and measurement noise and are assumed to be independent of each other
 * and distributed with normal probability (white noise).
 * <p>
 * The Kalman filter cycle involves the following steps:
 * <ol>
 * <li>predict: project the current state estimate ahead in time</li>
 * <li>correct: adjust the projected estimate by an actual measurement</li>
 * </ol>
 * </p>
 *
 * @see <a href="http://www.cs.unc.edu/~welch/kalman/">Kalman filter
 *      resources</a>
 * @see <a href="http://www.cs.unc.edu/~welch/media/pdf/kalman_intro.pdf">An
 *      introduction to the Kalman filter by Greg Welch and Gary Bishop</a>
 * @see <a
 *      href="http://academic.csuohio.edu/simond/courses/eec644/kalman.pdf">Kalman
 *      filter example by Dan Simon</a>
 *
 * @version $Id$
 */
public class KalmanFilter {
    /** Serializable version identifier. */
    private static final long serialVersionUID = 4878026651422612760L;
    /** The transition matrix, equivalent to A */
    private transient RealMatrix transitionMatrix;
    /** The transposed transition matrix */
    private transient RealMatrix transitionMatrixT;
    /** The control matrix, equivalent to B */
    private transient RealMatrix controlMatrix;
    /** The measurement matrix, equivalent to H */
    private transient RealMatrix measurementMatrix;
    /** The transposed measurement matrix */
    private transient RealMatrix measurementMatrixT;
    /** The internal state estimation vector, equivalent to x hat */
    private transient RealVector stateEstimation;
    /** The process noise covariance matrix, equivalent to Q */
    private transient RealMatrix processNoise;
    /** The measurement noise covariance matrix, equivalent to R */
    private transient RealMatrix measurementNoise;
    /** The error covariance matrix, equivalent to P */
    private transient RealMatrix errorCovariance;

    /**
     * Creates a new Kalman filter with the given process and measurement
     * models.
     *
     * @param processModel
     *            the model defining the underlying process dynamics
     * @param measurementModel
     *            the model defining the given measurement characteristics
     * @throws NullArgumentException
     *             if any of the given inputs is null (except for the control
     *             matrix)
     * @throws NonSquareMatrixException
     *             if the transition matrix is non square
     * @throws MatrixDimensionMismatchException
     *             if the matrix dimensions do not fit together
     */
    public KalmanFilter(final ProcessModel processModel,
            final MeasurementModel measurementModel)
            throws NullArgumentException, NonSquareMatrixException,
            MatrixDimensionMismatchException {

        MathUtils.checkNotNull(processModel);
        MathUtils.checkNotNull(measurementModel);

        transitionMatrix = processModel.getStateTransitionMatrix();
        MathUtils.checkNotNull(transitionMatrix);
        transitionMatrixT = transitionMatrix.transpose();

        // create an empty matrix if no control matrix was given
        controlMatrix = (processModel.getControlMatrix() == null) ?
            new Array2DRowRealMatrix() :
            processModel.getControlMatrix();

        measurementMatrix = measurementModel.getMeasurementMatrix();
        MathUtils.checkNotNull(measurementMatrix);
        measurementMatrixT = measurementMatrix.transpose();

        processNoise = processModel.getProcessNoise();
        MathUtils.checkNotNull(processNoise);

        measurementNoise = measurementModel.getMeasurementNoise();
        MathUtils.checkNotNull(measurementNoise);

        // set the initial state estimate to a zero vector if it is not
        // available
        stateEstimation = (processModel.getInitialStateEstimate() == null) ?
            new ArrayRealVector(transitionMatrix.getColumnDimension()) :
            processModel.getInitialStateEstimate();
        MathUtils.checkNotNull(stateEstimation);

        if (transitionMatrix.getColumnDimension() != stateEstimation.getDimension()) {
            throw new DimensionMismatchException(transitionMatrix.getColumnDimension(),
                                                 stateEstimation.getDimension());
        }

        // initialize the error covariance to the process noise if it is not
        // available
        errorCovariance = (processModel.getInitialErrorCovariance() == null) ? processNoise
                .copy() : processModel.getInitialErrorCovariance();
        MathUtils.checkNotNull(errorCovariance);

        // sanity checks, the control matrix B may be null

        // A must be a square matrix
        if (!transitionMatrix.isSquare()) {
            throw new NonSquareMatrixException(
                    transitionMatrix.getRowDimension(),
                    transitionMatrix.getColumnDimension());
        }

        // row dimension of B must be equal to A
        if (controlMatrix != null &&
            controlMatrix.getRowDimension() > 0 &&
            controlMatrix.getColumnDimension() > 0 &&
            (controlMatrix.getRowDimension() != transitionMatrix.getRowDimension() ||
             controlMatrix.getColumnDimension() != 1)) {
            throw new MatrixDimensionMismatchException(controlMatrix.getRowDimension(),
                                                       controlMatrix.getColumnDimension(),
                                                       transitionMatrix.getRowDimension(), 1);
        }

        // Q must be equal to A
        MatrixUtils.checkAdditionCompatible(transitionMatrix, processNoise);

        // column dimension of H must be equal to row dimension of A
        if (measurementMatrix.getColumnDimension() != transitionMatrix.getRowDimension()) {
            throw new MatrixDimensionMismatchException(measurementMatrix.getRowDimension(),
                                                       measurementMatrix.getColumnDimension(),
                                                       measurementMatrix.getRowDimension(),
                                                       transitionMatrix.getRowDimension());
        }

        // row dimension of R must be equal to row dimension of H
        if (measurementNoise.getRowDimension() != measurementMatrix.getRowDimension() ||
            measurementNoise.getColumnDimension() != 1) {
            throw new MatrixDimensionMismatchException(measurementNoise.getRowDimension(),
                                                       measurementNoise.getColumnDimension(),
                                                       measurementMatrix.getRowDimension(), 1);
        }
    }

    /**
     * Returns the dimension of the state estimation vector.
     *
     * @return the state dimension
     */
    public int getStateDimension() {
        return stateEstimation.getDimension();
    }

    /**
     * Returns the dimension of the measurement vector.
     *
     * @return the measurement vector dimension
     */
    public int getMeasurementDimension() {
        return measurementMatrix.getRowDimension();
    }

    /**
     * Returns the current state estimation vector.
     *
     * @return the state estimation vector
     */
    public double[] getStateEstimation() {
        return stateEstimation.getData();
    }

    /**
     * Returns a copy of the current state estimation vector.
     *
     * @return the state estimation vector
     */
    public RealVector getStateEstimationVector() {
        return stateEstimation.copy();
    }

    /**
     * Returns the current error covariance matrix.
     *
     * @return the error covariance matrix
     */
    public double[][] getErrorCovariance() {
        return errorCovariance.getData();
    }

    /**
     * Returns a copy of the current error covariance matrix.
     *
     * @return the error covariance matrix
     */
    public RealMatrix getErrorCovarianceMatrix() {
        return errorCovariance.copy();
    }

    /**
     * Predict the internal state estimation one time step ahead.
     */
    public void predict() {
        predict((RealVector) null);
    }

    /**
     * Predict the internal state estimation one time step ahead.
     *
     * @param u
     *            the control vector
     * @throws DimensionMismatchException
     *             if the dimension of the control vector does not fit
     */
    public void predict(final double[] u) throws DimensionMismatchException {
        predict(new ArrayRealVector(u));
    }

    /**
     * Predict the internal state estimation one time step ahead.
     *
     * @param u
     *            the control vector
     * @throws DimensionMismatchException
     *             if the dimension of the control vector does not fit
     */
    public void predict(final RealVector u) throws DimensionMismatchException {
        // sanity checks
        if (u != null &&
            u.getDimension() != controlMatrix.getColumnDimension()) {
            throw new DimensionMismatchException(u.getDimension(),
                                                 controlMatrix.getColumnDimension());
        }

        // project the state estimation ahead (a priori state)
        // xHat(k)- = A * xHat(k-1) + B * u(k-1)
        stateEstimation = transitionMatrix.operate(stateEstimation);

        // add control input if it is available
        if (u != null) {
            stateEstimation = stateEstimation.add(controlMatrix.operate(u));
        }

        // project the error covariance ahead
        // P(k)- = A * P(k-1) * A' + Q
        errorCovariance = transitionMatrix.multiply(errorCovariance)
                .multiply(transitionMatrixT).add(processNoise);
    }

    /**
     * Correct the current state estimate with an actual measurement.
     *
     * @param z
     *            the measurement vector
     * @throws DimensionMismatchException
     *             if the dimension of the measurement vector does not fit
     * @throws SingularMatrixException
     *             if the covariance matrix could not be inverted
     */
    public void correct(final double[] z) throws DimensionMismatchException,
                                                 SingularMatrixException {
        correct(new ArrayRealVector(z));
    }

    /**
     * Correct the current state estimate with an actual measurement.
     *
     * @param z
     *            the measurement vector
     * @throws DimensionMismatchException
     *             if the dimension of the measurement vector does not fit
     * @throws SingularMatrixException
     *             if the covariance matrix could not be inverted
     */
    public void correct(final RealVector z) throws DimensionMismatchException,
                                                   SingularMatrixException {
        // sanity checks
        if (z != null &&
            z.getDimension() != measurementMatrix.getRowDimension()) {
            throw new DimensionMismatchException(z.getDimension(),
                                                 measurementMatrix.getRowDimension());
        }

        // S = H * P(k) - * H' + R
        RealMatrix S = measurementMatrix.multiply(errorCovariance)
            .multiply(measurementMatrixT).add(measurementNoise);

        // invert S
        // as the error covariance matrix is a symmetric positive
        // semi-definite matrix, we can use the cholesky decomposition
        DecompositionSolver solver = new CholeskyDecompositionImpl(S).getSolver();
        RealMatrix invertedS = solver.getInverse();

        // Inn = z(k) - H * xHat(k)-
        RealVector innovation = z.subtract(measurementMatrix.operate(stateEstimation));

        // calculate gain matrix
        // K(k) = P(k)- * H' * (H * P(k)- * H' + R)^-1
        // K(k) = P(k)- * H' * S^-1
        RealMatrix kalmanGain = errorCovariance.multiply(measurementMatrixT).multiply(invertedS);

        // update estimate with measurement z(k)
        // xHat(k) = xHat(k)- + K * Inn
        stateEstimation = stateEstimation.add(kalmanGain.operate(innovation));

        // update covariance of prediction error
        // P(k) = (I - K * H) * P(k)-
        RealMatrix Identity = MatrixUtils.createRealIdentityMatrix(kalmanGain.getRowDimension());
        errorCovariance = Identity.subtract(kalmanGain.multiply(measurementMatrix)).multiply(errorCovariance);
    }
}
