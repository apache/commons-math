/*
 * 
 * Copyright (c) 2003-2004 The Apache Software Foundation. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *  
 */
package org.apache.commons.math.analysis;

import org.apache.commons.math.MathException;
import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public final class BisectionSolverTest extends TestCase {
    /**
     *
     */
    public void testSinZero() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        double result;
        
        UnivariateRealSolver solver = new BisectionSolver(f);
        result = solver.solve(3, 4);
        assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());

        result = solver.solve(1, 4);
        assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());
    }

    /**
     *
     */
    public void testQuinticZero() throws MathException {
        UnivariateRealFunction f = new QuinticFunction();
        double result;

        UnivariateRealSolver solver = new BisectionSolver(f);
        result = solver.solve(-0.2, 0.2);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(-0.1, 0.3);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(-0.3, 0.45);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(0.3, 0.7);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(0.2, 0.6);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(0.05, 0.95);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(0.85, 1.25);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(0.8, 1.2);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(0.85, 1.75);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(0.55, 1.45);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(0.85, 5);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        
        assertEquals(result, solver.getResult(), 0);
        assertTrue(solver.getIterationCount() > 0);
    }
    
    /**
     * 
     */
    public void testSetFunctionValueAccuracy(){
        double expected = 1.0e-2;    
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        solver.setFunctionValueAccuracy(expected);
        assertEquals(expected, solver.getFunctionValueAccuracy(), 1.0e-2);
    }        
    
    /**
     * 
     */
    public void testResetFunctionValueAccuracy(){
        double newValue = 1.0e-2;    
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        double oldValue = solver.getFunctionValueAccuracy();
        solver.setFunctionValueAccuracy(newValue);
        solver.resetFunctionValueAccuracy();
        assertEquals(oldValue, solver.getFunctionValueAccuracy(), 1.0e-2);
    }        
    
    /**
     * 
     */
    public void testSetAbsoluteAccuracy(){
        double expected = 1.0e-2; 
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        solver.setAbsoluteAccuracy(expected);
        assertEquals(expected, solver.getAbsoluteAccuracy(), 1.0e-2); 
    }        
    
    /**
     * 
     */
    public void testResetAbsoluteAccuracy(){
        double newValue = 1.0e-2;       
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        double oldValue = solver.getAbsoluteAccuracy();
        solver.setAbsoluteAccuracy(newValue);
        solver.resetAbsoluteAccuracy();
        assertEquals(oldValue, solver.getAbsoluteAccuracy(), 1.0e-2);
    }        
    
    /**
     * 
     */
    public void testSetMaximalIterationCount(){
        int expected = 100;
        
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        solver.setMaximalIterationCount(expected);
        assertEquals(expected, solver.getMaximalIterationCount());
    }        
    
    /**
     * 
     */
    public void testResetMaximalIterationCount(){
        int newValue = 10000;
        
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        int oldValue = solver.getMaximalIterationCount();
        solver.setMaximalIterationCount(newValue);
        solver.resetMaximalIterationCount();
        assertEquals(oldValue, solver.getMaximalIterationCount());
    }        
    
    /**
     * 
     */
    public void testSetRelativeAccuracy(){
        double expected = 1.0e-2;
        
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        solver.setRelativeAccuracy(expected);
        assertEquals(expected, solver.getRelativeAccuracy(), 1.0e-2);
    }        
    
    /**
     * 
     */
    public void testResetRelativeAccuracy(){
        double newValue = 1.0e-2;        
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        double oldValue = solver.getRelativeAccuracy();
        solver.setRelativeAccuracy(newValue);
        solver.resetRelativeAccuracy();
        assertEquals(oldValue, solver.getRelativeAccuracy(), 1.0e-2);
    }        
    
    /**
     * Test Serialization and Recovery
     */
   public void testSerialization() throws MathException {
       UnivariateRealFunction f = (UnivariateRealFunction)TestUtils.serializeAndRecover(new QuinticFunction());
       double result;
       
       BisectionSolver solver = new BisectionSolver(f);
       UnivariateRealSolver solver2 = (UnivariateRealSolver)TestUtils.serializeAndRecover(solver);
       
       result = solver.solve(-0.2, 0.2);
       assertEquals(result, 0, solver.getAbsoluteAccuracy());
       assertEquals(solver2.solve(-0.2, 0.2), result, solver2.getAbsoluteAccuracy());
       
       result = solver.solve(-0.1, 0.3);
       assertEquals(result, 0, solver.getAbsoluteAccuracy());
       assertEquals(solver2.solve(-0.1, 0.3), result, solver2.getAbsoluteAccuracy());
       
       result = solver.solve(-0.3, 0.45);
       assertEquals(result, 0, solver.getAbsoluteAccuracy());
       assertEquals(solver2.solve(-0.3, 0.45), result, solver2.getAbsoluteAccuracy());
       
       result = solver.solve(0.3, 0.7);
       assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
       assertEquals(solver2.solve(0.3, 0.7), result, solver2.getAbsoluteAccuracy());
       
       result = solver.solve(0.2, 0.6);
       assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
       assertEquals(solver2.solve(0.2, 0.6), result, solver2.getAbsoluteAccuracy());
       
       result = solver.solve(0.05, 0.95);
       assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
       assertEquals(solver2.solve(0.05, 0.95), result, solver2.getAbsoluteAccuracy());
       
       result = solver.solve(0.85, 1.25);
       assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
       assertEquals(solver2.solve(0.85, 1.25), result, solver2.getAbsoluteAccuracy());
       
       result = solver.solve(0.8, 1.2);
       assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
       assertEquals(solver2.solve(0.8, 1.2), result, solver2.getAbsoluteAccuracy());
       
       result = solver.solve(0.85, 1.75);
       assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
       assertEquals(solver2.solve(0.85, 1.75), result, solver2.getAbsoluteAccuracy());
       
       result = solver.solve(0.55, 1.45);
       assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
       assertEquals(solver2.solve(0.55, 1.45), result, solver2.getAbsoluteAccuracy());
       
       result = solver.solve(0.85, 5);
       assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
       assertEquals(solver2.solve(0.85, 5), result, solver2.getAbsoluteAccuracy());
       
       /* Test Reset */
       double newValue = 1.0e-2;
       f = (UnivariateRealFunction)TestUtils.serializeAndRecover(new QuinticFunction());
       solver = new BisectionSolver(f);
       
       double oldValue = solver.getRelativeAccuracy();
       solver.setRelativeAccuracy(newValue);
       solver.resetRelativeAccuracy();
       assertEquals(oldValue, solver.getRelativeAccuracy(), 1.0e-2);
       
       solver2 = (UnivariateRealSolver)TestUtils.serializeAndRecover(solver); 
       
       assertEquals(oldValue, solver2.getRelativeAccuracy(), 1.0e-2);
       
       solver2.setRelativeAccuracy(newValue);
       solver2.resetRelativeAccuracy();
       
       assertEquals(oldValue, solver2.getRelativeAccuracy(), 1.0e-2);
       
   }
   
}
