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

import junit.framework.TestCase;

/**
 * @todo add javadoc comment
 * @version $Revision$ $Date$
 */
public class UnivariateRealFunctionUtilsTest extends TestCase {
    /**
     *  
     */
    public void testLocalMaximumCentered() {
        UnivariateRealFunction function = new SinFunction();
        UnivariateRealFunction derivative = UnivariateRealFunctionUtils.centerDifferenceDerivative(function, 1.0e-5);
        testLocalMaximum(derivative);
    }
     
    /**
     *  
     */
    public void testLocalMaximumForward() {
        UnivariateRealFunction function = new SinFunction();
        UnivariateRealFunction derivative = UnivariateRealFunctionUtils.forwardDifferenceDerivative(function, 1.0e-5);
        testLocalMaximum(derivative);
    }
     
    /**
     * 
     */
    public void testLocalMaximumBackward() {
        UnivariateRealFunction function = new SinFunction();
        UnivariateRealFunction derivative = UnivariateRealFunctionUtils.backwardDifferenceDerivative(function, 1.0e-5);
        testLocalMaximum(derivative);
    }
    
    /**
     * Find a local extrema, i.e. f'(x) = 0. 
     */
    private void testLocalMaximum(UnivariateRealFunction derivative) {
        try {
            double maximum = UnivariateRealSolverUtils.solve(derivative, Math.PI / 3.0, Math.PI * 2.0 / 3.0);
            assertEquals(maximum, Math.PI / 2.0, 1.0e-5);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    } 
}
