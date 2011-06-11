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
import org.apache.commons.math.linear.RealMatrix;

/**
 * Default implementation of a {@link MeasurementModel} for the use with a
 * {@link KalmanFilter}.
 *
 * @version $Id$
 */
public class DefaultMeasurementModel implements MeasurementModel {

    private RealMatrix measurementMatrix;
    private RealMatrix measurementNoise;

    /**
     * Create a new {@link MeasurementModel}, taking double arrays as input
     * parameters for the respective measurement matrix and noise.
     *
     * @param measurementMatrix
     *            the measurement matrix
     * @param measurementNoise
     *            the measurement noise matrix
     */
    public DefaultMeasurementModel(final double[][] measurementMatrix,
            final double[][] measurementNoise) {
        this(new Array2DRowRealMatrix(measurementMatrix),
                new Array2DRowRealMatrix(measurementNoise));
    }

    /**
     * Create a new {@link MeasurementModel}, taking {@link RealMatrix} objects
     * as input parameters for the respective measurement matrix and noise.
     *
     * @param measurementMatrix
     * @param measurementNoise
     */
    public DefaultMeasurementModel(final RealMatrix measurementMatrix,
            final RealMatrix measurementNoise) {
        this.measurementMatrix = measurementMatrix;
        this.measurementNoise = measurementNoise;
    }

    /**
     * {@inheritDoc}
     */
    public RealMatrix getMeasurementMatrix() {
        return measurementMatrix;
    }

    /**
     * {@inheritDoc}
     */
    public RealMatrix getMeasurementNoise() {
        return measurementNoise;
    }
}
