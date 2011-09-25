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

package org.apache.commons.math.ode;

/**
 * This interface allows users to add their own differential equations to a main
 * set of differential equations.
 * <p>
 * In some cases users may need to integrate some problem-specific equations along
 * with a main set of differential equations. One example is optimal control where
 * adjoined parameters linked to the minimized hamiltonian must be integrated.
 * </p>
 * <p>
 * This interface allows users to add such equations to a main set of {@link
 * FirstOrderDifferentialEquations first order differential equations}
 * thanks to the {@link
 * ExpandableFirstOrderDifferentialEquations#addAdditionalEquations(AdditionalEquations)}
 * method.
 * </p>
 * @see ExpandableFirstOrderDifferentialEquations
 * @version $Id$
 * @since 3.0
 */
public interface AdditionalEquations {

    /** Get the dimension of the additional state parameters.
     * @return dimension of the additional state parameters
     */
    int getDimension();

    /** Compute the derivatives related to the additional state parameters.
     * @param t current value of the independent <I>time</I> variable
     * @param y array containing the current value of the main state vector
     * @param yDot array containing the derivative of the main state vector
     * @param z array containing the current value of the additional state vector
     * @param zDot placeholder array where to put the derivative of the additional state vector
     */
    void computeDerivatives(double t, double[] y, double[] yDot, double[] z, double[] zDot);

}
