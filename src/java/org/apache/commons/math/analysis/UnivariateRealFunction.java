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
 * An interface representing a univariate real function.
 *  
 * @version $Revision: 1.12 $ $Date: 2004/02/21 21:35:14 $
 */
public interface UnivariateRealFunction {
    /**
     * Compute the value for the function.
     * @param x the point for which the function value should be computed
     * @return the value
     * @throws MathException if the function couldn't be computed due to
     *  missing additional data or other environmental problems.
     */
    public double value(double x) throws MathException;

}
