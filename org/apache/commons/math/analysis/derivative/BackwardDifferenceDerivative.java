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

package org.apache.commons.math.analysis.derivative;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

/**
 * @todo add javadoc comment
 * @version $Revision$ $Date$
 */
public class BackwardDifferenceDerivative extends AbstractDifferenceDerivative {

    /**
     * @todo add javadoc comment
     */
    public BackwardDifferenceDerivative(UnivariateRealFunction function, double h) {
        super(function, h);
    }
    
    /**
     * @todo add javadoc comment
     */
    public double value(double x) throws MathException {
        UnivariateRealFunction f = getFunction();
        double h = getDelta();
        return (f.value(x) - f.value(x - h)) / h;
    } 
    
    /**
     * 
     */
    public static UnivariateRealFunction decorate(UnivariateRealFunction function, double h) {
        return new BackwardDifferenceDerivative(function, h);
    }
}
