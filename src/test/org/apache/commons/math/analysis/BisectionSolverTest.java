/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.analysis;

import org.apache.commons.math.MathException;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:09:07 $
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
