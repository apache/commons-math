/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.math.MathException;

/**
 * Auxillary class for testing solvers.
 *
 * @version $Revision: 1.11 $ $Date: 2004/02/21 21:35:16 $ 
 */
public class QuinticFunction implements UnivariateRealFunction {

    /* Evaluate quintic.
     * @see org.apache.commons.math.UnivariateRealFunction#value(double)
     */
    public double value(double x) throws MathException {
        return (x-1)*(x-0.5)*x*(x+0.5)*(x+1);
    }

    /* First derivative of quintic.
     */
    public double firstDerivative(double x) throws MathException {
        return (5*x*x-3.75)*x*x+0.25;
    }

}
