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
package org.apache.commons.math.optimization.univariate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.UnivariateRealOptimizer;
import org.junit.Test;

/**
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (Sat, 05 Sep 2009) $
 */
public final class BrentOptimizerTest {

    @Test
    public void testSinMin() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealOptimizer minimizer = new BrentOptimizer();
        minimizer.setMaxEvaluations(200);
        assertEquals(200, minimizer.getMaxEvaluations());
        try {
            minimizer.getResult();
            fail("an exception should have been thrown");
        } catch (NoDataException ise) {
            // expected
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        assertEquals(3 * Math.PI / 2, minimizer.optimize(f, GoalType.MINIMIZE, 4, 5), 70 * minimizer.getAbsoluteAccuracy());
        assertTrue(minimizer.getIterationCount() <= 50);
        assertEquals(3 * Math.PI / 2, minimizer.optimize(f, GoalType.MINIMIZE, 1, 5), 70 * minimizer.getAbsoluteAccuracy());
        assertTrue(minimizer.getIterationCount() <= 50);
        assertTrue(minimizer.getEvaluations()    <= 100);
        assertTrue(minimizer.getEvaluations()    >=  30);
        minimizer.setMaxEvaluations(50);
        try {
            minimizer.optimize(f, GoalType.MINIMIZE, 4, 5);
            fail("an exception should have been thrown");
        } catch (FunctionEvaluationException fee) {
            // expected
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    @Test
    public void testQuinticMin() throws MathException {
        // The function has local minima at -0.27195613 and 0.82221643.
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealOptimizer minimizer = new BrentOptimizer();
        assertEquals(-0.27195613, minimizer.optimize(f, GoalType.MINIMIZE, -0.3, -0.2), 1.0e-8);
        assertEquals( 0.82221643, minimizer.optimize(f, GoalType.MINIMIZE,  0.3,  0.9), 1.0e-8);
        assertTrue(minimizer.getIterationCount() <= 50);

        // search in a large interval
        assertEquals(-0.27195613, minimizer.optimize(f, GoalType.MINIMIZE, -1.0, 0.2), 1.0e-8);
        assertTrue(minimizer.getIterationCount() <= 50);
    }

    @Test
    public void testQuinticMinPythonComparison() throws MathException {
        // The function has local minima at -0.27195613 and 0.82221643.
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealOptimizer minimizer = new BrentOptimizer();
        minimizer.setRelativeAccuracy(1e-12);
        minimizer.setAbsoluteAccuracy(1e-11);

        double result;
        int nIter, nEval;

        result = minimizer.optimize(f, GoalType.MINIMIZE, -0.3, -0.2, -0.25);
        nIter = minimizer.getIterationCount();
        nEval = minimizer.getEvaluations();
        // XXX Python: -0.27195612805911351 (instead of -0.2719561279558559).
        assertEquals(-0.2719561279558559, result, 1e-12);
        // XXX Python: 15 (instead of 18).
        assertEquals(18, nEval);
        // XXX Python: 11 (instead of 17).
        assertEquals(17, nIter);

        result = minimizer.optimize(f, GoalType.MINIMIZE, 0.7, 0.9, 0.8);
        nIter = minimizer.getIterationCount();
        nEval = minimizer.getEvaluations();
        // XXX Python: 0.82221643488363705 (instead of 0.8222164326561908).
        assertEquals(0.8222164326561908, result, 1e-12);
        // XXX Python: 25 (instead of 43).
        assertEquals(43, nEval);
        // XXX Python: 21 (instead of 24).
        assertEquals(24, nIter);
    }

    @Test
    public void testQuinticMax() throws MathException {
        // The quintic function has zeros at 0, +-0.5 and +-1.
        // The function has a local maximum at 0.27195613.
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealOptimizer minimizer = new BrentOptimizer();
        assertEquals(0.27195613, minimizer.optimize(f, GoalType.MAXIMIZE, 0.2, 0.3), 1.0e-8);
        minimizer.setMaximalIterationCount(20);
        try {
            minimizer.optimize(f, GoalType.MAXIMIZE, 0.2, 0.3);
            fail("an exception should have been thrown");
        } catch (MaxIterationsExceededException miee) {
            // expected
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    @Test
    public void testMinEndpoints() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealOptimizer solver = new BrentOptimizer();

        // endpoint is minimum
        double result = solver.optimize(f, GoalType.MINIMIZE, 3 * Math.PI / 2, 5);
        assertEquals(3 * Math.PI / 2, result, 70 * solver.getAbsoluteAccuracy());

        result = solver.optimize(f, GoalType.MINIMIZE, 4, 3 * Math.PI / 2);
        assertEquals(3 * Math.PI / 2, result, 80 * solver.getAbsoluteAccuracy());
    }
}
