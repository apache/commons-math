/*
 * 
 * Copyright (c) 2004 The Apache Software Foundation. All rights reserved.
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

import junit.framework.TestCase;

/**
 * @version $Revision: 1.9 $ $Date: 2004/01/29 16:48:49 $
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
    }
    
    /**
     * 
     */
    public void testSetFunctionValueAccuracy(){
        double expected = 1.0e-2;
        
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        try {
            solver.setFunctionValueAccuracy(expected);
            assertEquals(expected, solver.getFunctionValueAccuracy(), 1.0e-2);
        } catch (MathException ex) {
            fail(ex.getMessage());
        }
    }        
    
    /**
     * 
     */
    public void testResetFunctionValueAccuracy(){
        double newValue = 1.0e-2;
        
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        try {
            double oldValue = solver.getFunctionValueAccuracy();
            solver.setFunctionValueAccuracy(newValue);
            solver.resetFunctionValueAccuracy();
            assertEquals(oldValue, solver.getFunctionValueAccuracy(), 1.0e-2);
        } catch(MathException ex){
            fail(ex.getMessage());
        }
    }        
    
    /**
     * 
     */
    public void testSetAbsoluteAccuracy(){
        double expected = 1.0e-2;
        
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        try {
            solver.setAbsoluteAccuracy(expected);
            assertEquals(expected, solver.getAbsoluteAccuracy(), 1.0e-2);
        } catch(MathException ex){
            fail(ex.getMessage());
        }
    }        
    
    /**
     * 
     */
    public void testResetAbsoluteAccuracy(){
        double newValue = 1.0e-2;
        
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        try {
            double oldValue = solver.getAbsoluteAccuracy();
            solver.setAbsoluteAccuracy(newValue);
            solver.resetAbsoluteAccuracy();
            assertEquals(oldValue, solver.getAbsoluteAccuracy(), 1.0e-2);
        } catch(MathException ex){
            fail(ex.getMessage());
        }
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
        try {
            solver.setRelativeAccuracy(expected);
            assertEquals(expected, solver.getRelativeAccuracy(), 1.0e-2);
        } catch(MathException ex){
            fail(ex.getMessage());
        }
    }        
    
    /**
     * 
     */
    public void testResetRelativeAccuracy(){
        double newValue = 1.0e-2;
        
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealSolver solver = new BisectionSolver(f);
        try {
            double oldValue = solver.getRelativeAccuracy();
            solver.setRelativeAccuracy(newValue);
            solver.resetRelativeAccuracy();
            assertEquals(oldValue, solver.getRelativeAccuracy(), 1.0e-2);
        } catch(MathException ex){
            fail(ex.getMessage());
        }
    }        
}
