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
package org.apache.commons.math4.legacy.analysis.interpolation;

import org.apache.commons.math4.legacy.analysis.UnivariateFunction;

/**
 * Interface representing a univariate real interpolating function.
 */
public interface UnivariateInterpolator {
    /**
     * Computes an interpolating function for the dataset.
     *
     * @param xval Arguments for the interpolation points.
     * @param yval Values for the interpolation points.
     * @return a function which interpolates the dataset.
     * @throws org.apache.commons.math4.legacy.exception.MathIllegalArgumentException
     * if the arguments violate assumptions made by the interpolation algorithm.
     */
    UnivariateFunction interpolate(double[] xval, double[] yval);
}
