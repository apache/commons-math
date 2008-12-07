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

import org.apache.commons.math.MathException;
import org.apache.commons.math.TestUtils;


import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public final class NewtonSolverTest extends TestCase {

    @Deprecated
    public void testDeprecated() throws MathException {
        DifferentiableUnivariateRealFunction f = new SinFunction();
        double result;
        
        UnivariateRealSolver solver = new NewtonSolver(f);
        result = solver.solve(3, 4);
        assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());

        result = solver.solve(1, 4);
        assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());
        
        //TODO:  create abstract solver test class, move these there
        assertEquals(result, solver.getResult(), 0);
        assertTrue(solver.getIterationCount() > 0);
    }

    /**
    *
    */
   public void testSinZero() throws MathException {
       DifferentiableUnivariateRealFunction f = new SinFunction();
       double result;
       
       UnivariateRealSolver solver = new NewtonSolver();
       result = solver.solve(f, 3, 4);
       assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());

       result = solver.solve(f, 1, 4);
       assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());
       
       //TODO:  create abstract solver test class, move these there
       assertEquals(result, solver.getResult(), 0);
       assertTrue(solver.getIterationCount() > 0);
   }

   /**
     *
     */
    public void testQuinticZero() throws MathException {
        DifferentiableUnivariateRealFunction f = new QuinticFunction();
        double result;

        UnivariateRealSolver solver = new NewtonSolver();
        result = solver.solve(f, -0.2, 0.2);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, -0.1, 0.3);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, -0.3, 0.45);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.3, 0.7);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.2, 0.6);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.05, 0.95);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.85, 1.25);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.8, 1.2);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.85, 1.75);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.55, 1.45);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.85, 5);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
    }
    
    /**
     * Test Serialization and Recovery
     */
    public void testSerialization() throws MathException {
        DifferentiableUnivariateRealFunction f = new QuinticFunction();
        double result;
        
        NewtonSolver solver = new NewtonSolver();
        NewtonSolver solver2 = (NewtonSolver)TestUtils.serializeAndRecover(solver);
        
        result = solver.solve(f, -0.2, 0.2);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        assertEquals(solver2.solve(f, -0.2, 0.2), result, solver2.getAbsoluteAccuracy());
        
        result = solver.solve(f, -0.1, 0.3);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        assertEquals(solver2.solve(f, -0.1, 0.3), result, solver2.getAbsoluteAccuracy());
        
        result = solver.solve(f, -0.3, 0.45);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        assertEquals(solver2.solve(f, -0.3, 0.45), result, solver2.getAbsoluteAccuracy());
        
        result = solver.solve(f, 0.3, 0.7);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        assertEquals(solver2.solve(f, 0.3, 0.7), result, solver2.getAbsoluteAccuracy());
        
        result = solver.solve(f, 0.2, 0.6);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        assertEquals(solver2.solve(f, 0.2, 0.6), result, solver2.getAbsoluteAccuracy());
        
        result = solver.solve(f, 0.05, 0.95);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        assertEquals(solver2.solve(f, 0.05, 0.95), result, solver2.getAbsoluteAccuracy());
        
        result = solver.solve(f, 0.85, 1.25);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        assertEquals(solver2.solve(f, 0.85, 1.25), result, solver2.getAbsoluteAccuracy());
        
        result = solver.solve(f, 0.8, 1.2);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        assertEquals(solver2.solve(f, 0.8, 1.2), result, solver2.getAbsoluteAccuracy());
        
        result = solver.solve(f, 0.85, 1.75);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        assertEquals(solver2.solve(f, 0.85, 1.75), result, solver2.getAbsoluteAccuracy());
        
        result = solver.solve(f, 0.55, 1.45);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        assertEquals(solver2.solve(f, 0.55, 1.45), result, solver2.getAbsoluteAccuracy());
        
        result = solver.solve(f, 0.85, 5);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        assertEquals(solver2.solve(f, 0.85, 5), result, solver2.getAbsoluteAccuracy());
        
        /* Test Reset */
        double newValue = 1.0e-2;
        f = new QuinticFunction();
        solver = new NewtonSolver();
        
        double oldValue = solver.getRelativeAccuracy();
        solver.setRelativeAccuracy(newValue);
        solver.resetRelativeAccuracy();
        assertEquals(oldValue, solver.getRelativeAccuracy(), 1.0e-2);
        
        solver2 = (NewtonSolver)TestUtils.serializeAndRecover(solver); 
        
        assertEquals(oldValue, solver2.getRelativeAccuracy(), 1.0e-2);
        
        solver2.setRelativeAccuracy(newValue);
        solver2.resetRelativeAccuracy();
        
        assertEquals(oldValue, solver2.getRelativeAccuracy(), 1.0e-2);
        
    }
}
