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

import junit.framework.TestCase;

/**
 * @version $Revision: 1.7 $ $Date: 2004/02/21 21:35:16 $
 */
public class UnivariateRealSolverUtilsTest extends TestCase {
    /**
     * 
     */
    public void testSolveNull(){
        try {
            UnivariateRealSolverUtils.solve(null, 0.0, 4.0);
            fail();
        } catch(MathException ex){
            fail("math exception should no be thrown.");
        } catch(IllegalArgumentException ex){
            // success
        }
    }
    
    /**
     * 
     */
    public void testSolveSin(){
        try {
            double x = UnivariateRealSolverUtils.solve(new SinFunction(), 1.0,
                4.0);
            assertEquals(Math.PI, x, 1.0e-4);
        } catch(MathException ex){
            fail("math exception should no be thrown.");
        }
    }

    /**
     * 
     */
    public void testSolveAccuracyNull(){
        try {
            double accuracy = 1.0e-6;
            UnivariateRealSolverUtils.solve(null, 0.0, 4.0, accuracy);
            fail();
        } catch(MathException ex){
            fail("math exception should no be thrown.");
        } catch(IllegalArgumentException ex){
            // success
        }
    }
    
    /**
     * 
     */
    public void testSolveAccuracySin(){
        try {
            double accuracy = 1.0e-6;
            double x = UnivariateRealSolverUtils.solve(new SinFunction(), 1.0,
                4.0, accuracy);
            assertEquals(Math.PI, x, accuracy);
        } catch(MathException ex){
            fail("math exception should no be thrown.");
        }
    }
}
