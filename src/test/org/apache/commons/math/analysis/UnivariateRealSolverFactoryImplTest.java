/*
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.commons.math.analysis;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class UnivariateRealSolverFactoryImplTest extends TestCase {
    
    /** solver factory */
    private UnivariateRealSolverFactory factory;
    
    /** function */
    private DifferentiableUnivariateRealFunction function;
    /**
     * @throws java.lang.Exception
     * @see junit.framework.TestCase#tearDown()
     */
    protected void setUp() throws Exception {
        super.setUp();
        factory = new UnivariateRealSolverFactoryImpl();
        function = new SinFunction();
    }
    
    /**
     * @throws java.lang.Exception
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        factory = null;
        function = null;
        super.tearDown();
    }

    public void testNewBisectionSolverNull() {
        try {
            UnivariateRealSolver solver = factory.newBisectionSolver(null);
            fail();
        } catch(IllegalArgumentException ex) {
            // success
        }
    }

    public void testNewBisectionSolverValid() {
        UnivariateRealSolver solver = factory.newBisectionSolver(function);
        assertNotNull(solver);
        assertTrue(solver instanceof BisectionSolver);
    }

    public void testNewNewtonSolverNull() {
        try {
            UnivariateRealSolver solver = factory.newNewtonSolver(null);
            fail();
        } catch(IllegalArgumentException ex) {
            // success
        }
    }

    public void testNewNewtonSolverValid() {
        UnivariateRealSolver solver = factory.newNewtonSolver(function);
        assertNotNull(solver);
        assertTrue(solver instanceof NewtonSolver);
    }

    public void testNewBrentSolverNull() {
        try {
            UnivariateRealSolver solver = factory.newBrentSolver(null);
            fail();
        } catch(IllegalArgumentException ex) {
            // success
        }
    }

    public void testNewBrentSolverValid() {
        UnivariateRealSolver solver = factory.newBrentSolver(function);
        assertNotNull(solver);
        assertTrue(solver instanceof BrentSolver);
    }

    public void testNewSecantSolverNull() {
        try {
            UnivariateRealSolver solver = factory.newSecantSolver(null);
            fail();
        } catch(IllegalArgumentException ex) {
            // success
        }
    }

    public void testNewSecantSolverValid() {
        UnivariateRealSolver solver = factory.newSecantSolver(function);
        assertNotNull(solver);
        assertTrue(solver instanceof SecantSolver);
    }
}
