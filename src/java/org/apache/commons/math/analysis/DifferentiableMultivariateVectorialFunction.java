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

package org.apache.commons.math.analysis;


/** 
 * Extension of {@link MultivariateVectorialFunction} representing a differentiable
 * multivariate vectorial function.
 * @version $Revision$ $Date$
 * @since 2.0
 */
public interface DifferentiableMultivariateVectorialFunction
    extends MultivariateVectorialFunction {

    /**
     * Returns the partial derivative of the function with respect to point
     * coordinate x<sub>j</sub>.
     * <p>
     * The partial derivative basically represents column j of the jacobian
     * matrix. If the partial derivatives with respect to all coordinates are
     * needed, it may be more efficient to use the {@link #jacobian()} method
     * which will compute the complete matrix at once.
     * </p>
     * @param j index of the coordinate with respect to which the partial
     * derivative is computed
     * @return the partial derivative function with respect to point coordinate
     * x<sub>i</sub>
     */
    MultivariateVectorialFunction partialDerivative(int j);

    /**
     * Returns the gradient function of the i<sup>th</sup> component of
     * the vectorial function.
     * 
     * <p>
     * The i<sup>th</sup> gradient basically represents row i of the jacobian
     * matrix. If all gradients are needed, it may be more efficient to use the
     * {@link #jacobian()} method which will compute the complete matrix at once.
     * </p>
     * @param i index of the function component for which the gradient is requested
     * @return the gradient function of the i<sup>th</sup> component of
     * the vectorial function
     */
    MultivariateVectorialFunction gradient(int i);

    /**
     * Returns the jacobian function.
     * <p>
     * If only one column of the jacobian is needed, it may be more efficient to
     * use the {@link #partialDerivative(int)} method which will compute only the
     * specified column. If only one row of the jacobian is needed, it may be more
     * efficient to use the {@link #gradient(int)} method which will compute only the
     * specified row.
     * </p>
     * @return the jacobian function
     */
    MultivariateMatrixFunction jacobian();

}
