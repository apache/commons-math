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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/** Wrapper class enabling {@link FirstOrderDifferentialEquations basic simple}
 *  ODE instances to be used when processing {@link JacobianMatrices}.
 *
 * @version $Id$
 * @since 3.0
 */
class ParameterizedWrapper implements ParameterizedODE {

    /** Basic FODE without parameter. */
    private final FirstOrderDifferentialEquations fode;

    /** Simple constructor.
     * @param ode original first order differential equations
     */
    public ParameterizedWrapper(final FirstOrderDifferentialEquations ode) {
        this.fode = ode;
    }

    public int getDimension() {
        return fode.getDimension();
    }

    public void computeDerivatives(double t, double[] y, double[] yDot) {
        fode.computeDerivatives(t, y, yDot);
    }

    /** {@inheritDoc} */
    public Collection<String> getParametersNames() {
        return new ArrayList<String>();
    }

    /** {@inheritDoc} */
    public boolean isSupported(String name) {
        return false;
    }

    /** {@inheritDoc} */
    public double getParameter(String name)
        throws MathIllegalArgumentException {
        if (!isSupported(name)) {
            throw new MathIllegalArgumentException(LocalizedFormats.UNKNOWN_PARAMETER, name);
        }
        return Double.NaN;
    }

    /** {@inheritDoc} */
    public void setParameter(String name, double value) {
    }

}
