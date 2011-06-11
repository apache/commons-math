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

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

/**
 * Default implementation of a {@link ProcessModel} for the use with a
 * {@link KalmanFilter}.
 *
 * @version $Id$
 */
public class DefaultProcessModel implements ProcessModel {

    private RealMatrix stateTransitionMatrix;
    private RealMatrix controlMatrix;
    private RealMatrix processNoise;
    private RealVector initialStateEstimate;
    private RealMatrix initialErrorCovariance;

    /**
     * Create a new {@link ProcessModel}, taking double arrays as input
     * parameters.
     *
     * @param stateTransitionMatrix
     *            the state transition matrix
     * @param controlMatrix
     *            the control matrix
     * @param processNoise
     *            the process noise matrix
     * @param initialStateEstimate
     *            the initial state estimate vector
     * @param initialErrorCovariance
     *            the initial error covariance matrix
     */
    public DefaultProcessModel(final double[][] stateTransitionMatrix,
            final double[][] controlMatrix, final double[][] processNoise,
            final double[] initialStateEstimate,
            final double[][] initialErrorCovariance) {
        this(new Array2DRowRealMatrix(stateTransitionMatrix),
                new Array2DRowRealMatrix(controlMatrix),
                new Array2DRowRealMatrix(processNoise), new ArrayRealVector(
                        initialStateEstimate), new Array2DRowRealMatrix(
                        initialErrorCovariance));
    }

    /**
     * Create a new {@link ProcessModel}, taking double arrays as input
     * parameters. The initial state estimate and error covariance are omitted
     * and will be initialized by the {@link KalmanFilter} to default values.
     *
     * @param stateTransitionMatrix
     *            the state transition matrix
     * @param controlMatrix
     *            the control matrix
     * @param processNoise
     *            the process noise matrix
     */
    public DefaultProcessModel(final double[][] stateTransitionMatrix,
            final double[][] controlMatrix, final double[][] processNoise) {
        this(new Array2DRowRealMatrix(stateTransitionMatrix),
                new Array2DRowRealMatrix(controlMatrix),
                new Array2DRowRealMatrix(processNoise), null, null);
    }

    /**
     * Create a new {@link ProcessModel}, taking double arrays as input
     * parameters.
     *
     * @param stateTransitionMatrix
     *            the state transition matrix
     * @param controlMatrix
     *            the control matrix
     * @param processNoise
     *            the process noise matrix
     * @param initialStateEstimate
     *            the initial state estimate vector
     * @param initialErrorCovariance
     *            the initial error covariance matrix
     */
    public DefaultProcessModel(final RealMatrix stateTransitionMatrix,
            final RealMatrix controlMatrix, final RealMatrix processNoise,
            final RealVector initialStateEstimate,
            final RealMatrix initialErrorCovariance) {
        this.stateTransitionMatrix = stateTransitionMatrix;
        this.controlMatrix = controlMatrix;
        this.processNoise = processNoise;
        this.initialStateEstimate = initialStateEstimate;
        this.initialErrorCovariance = initialErrorCovariance;
    }

    /**
     * {@inheritDoc}
     */
    public RealMatrix getStateTransitionMatrix() {
        return stateTransitionMatrix;
    }

    /**
     * {@inheritDoc}
     */
    public RealMatrix getControlMatrix() {
        return controlMatrix;
    }

    /**
     * {@inheritDoc}
     */
    public RealMatrix getProcessNoise() {
        return processNoise;
    }

    /**
     * {@inheritDoc}
     */
    public RealVector getInitialStateEstimate() {
        return initialStateEstimate;
    }

    /**
     * {@inheritDoc}
     */
    public RealMatrix getInitialErrorCovariance() {
        return initialErrorCovariance;
    }
}
