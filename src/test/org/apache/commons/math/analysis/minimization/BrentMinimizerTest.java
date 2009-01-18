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
package org.apache.commons.math.analysis.minimization;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;

/**
 * @version $Revision$ $Date$ 
 */
public final class BrentMinimizerTest extends TestCase {

    public BrentMinimizerTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(BrentMinimizerTest.class);
        suite.setName("BrentMinimizer Tests");
        return suite;
    }

    public void testSinMin() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealMinimizer minimizer = new BrentMinimizer();
        assertEquals(3 * Math.PI / 2, minimizer.minimize(f, 4, 5), 70 * minimizer.getAbsoluteAccuracy());
        assertTrue(minimizer.getIterationCount() <= 50);
        assertEquals(3 * Math.PI / 2, minimizer.minimize(f, 1, 5), 70 * minimizer.getAbsoluteAccuracy());
        assertTrue(minimizer.getIterationCount() <= 50);
    }

   public void testQuinticMin() throws MathException {
        // The quintic function has zeros at 0, +-0.5 and +-1.
        // The function has extrema (first derivative is zero) at 0.27195613 and 0.82221643,
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealMinimizer minimizer = new BrentMinimizer();
        assertEquals(-0.27195613, minimizer.minimize(f, -0.3, -0.2), 1.0e-8);
        assertEquals( 0.82221643, minimizer.minimize(f,  0.3,  0.9), 1.0e-8);
        assertTrue(minimizer.getIterationCount() <= 50);

        // search in a large interval
        assertEquals(-0.27195613, minimizer.minimize(f, -1.0, 0.2), 1.0e-8);
        assertTrue(minimizer.getIterationCount() <= 50);

   }
    
    public void testMinEndpoints() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealMinimizer solver = new BrentMinimizer();
        
        // endpoint is minimum
        double result = solver.minimize(f, 3 * Math.PI / 2, 5);
        assertEquals(3 * Math.PI / 2, result, 70 * solver.getAbsoluteAccuracy());

        result = solver.minimize(f, 4, 3 * Math.PI / 2);
        assertEquals(3 * Math.PI / 2, result, 70 * solver.getAbsoluteAccuracy());

    }
    
}
