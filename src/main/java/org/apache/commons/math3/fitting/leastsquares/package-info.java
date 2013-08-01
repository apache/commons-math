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

/**
 * This package provides algorithms that minimize the residuals
 * between observations and model values.
 * The {@link org.apache.commons.math3.fitting.leastsquares.AbstractLeastSquaresOptimizer
 * non-linear least-squares optimizers} minimize the distance (called
 * <em>cost</em> or <em>&chi;<sup>2</sup></em>) between model and
 * observations.
 *
 * <br/>
 * Algorithms in this category need access to a <em>model function</em>
 * (represented by a {@link org.apache.commons.math3.analysis.MultivariateVectorFunction
 * MultivariateVectorFunction}).
 * Such a model predicts a set of values which the algorithm tries to match
 * with a set of given set of {@link org.apache.commons.math3.fitting.leastsquares.WithTarget
 * observed values}.
 * <br/>
 * The algorithms implemented in this package also require that the user
 * specifies the Jacobian matrix of the model (represented by a
 * {@link org.apache.commons.math3.analysis.MultivariateMatrixFunction
 * MultivariateMatrixFunction}).
 */
package org.apache.commons.math3.fitting.leastsquares;
