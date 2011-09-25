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

/** Wrapper class to compute jacobian matrices by finite differences for ODE
 *  which do not compute them by themselves.
 *  
 * @version $Id$
 * @since 3.0
 */
class MainStateJacobianWrapper implements MainStateJacobianProvider {

    /** Raw ODE without jacobians computation skill to be wrapped into a MainStateJacobianProvider. */
    private final FirstOrderDifferentialEquations ode;

    /** Steps for finite difference computation of the jacobian df/dy w.r.t. state. */
    private final double[] hY;

    /** Wrap a {@link FirstOrderDifferentialEquations} into a {@link MainStateJacobianProvider}.
     * @param ode original ODE problem, without jacobians computation skill
     * @param hY step sizes to compute the jacobian df/dy
     * @see JacobianMatrices#setMainStateSteps(double[])
     */
    public MainStateJacobianWrapper(final FirstOrderDifferentialEquations ode,
                                    final double[] hY) {
        this.ode = ode;
        this.hY = hY.clone();
    }

    /** {@inheritDoc} */
    public int getDimension() {
        return ode.getDimension();
    }

    /** {@inheritDoc} */
    public void computeDerivatives(double t, double[] y, double[] yDot) {
        ode.computeDerivatives(t, y, yDot);
    }

    /** {@inheritDoc} */
    public void computeMainStateJacobian(double t, double[] y, double[] yDot,
                                         double[][] dFdY) {

        final int n = ode.getDimension();
        final double[] tmpDot = new double[n];

        for (int j = 0; j < n; ++j) {
            final double savedYj = y[j];
            y[j] += hY[j];
            ode.computeDerivatives(t, y, tmpDot);
            for (int i = 0; i < n; ++i) {
                dFdY[i][j] = (tmpDot[i] - yDot[i]) / hY[j];
            }
            y[j] = savedYj;
        }
    }

}
