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

import org.apache.commons.math.MathException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.exception.TooManyEvaluationsException;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

/**
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (Sat, 05 Sep 2009) $
 */
public final class BrentOptimizerTest {

    @Test
    public void testSinMin() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealOptimizer optimizer = new BrentOptimizer();
        optimizer.setConvergenceChecker(new BrentOptimizer.BrentConvergenceChecker(1e-10, 1e-14));
        optimizer.setMaxEvaluations(200);
        assertEquals(200, optimizer.getMaxEvaluations());
        assertEquals(3 * Math.PI / 2, optimizer.optimize(f, GoalType.MINIMIZE, 4, 5).getPoint(),
                     100 * optimizer.getConvergenceChecker().getRelativeThreshold());
        assertTrue(optimizer.getEvaluations() <= 50);
        assertEquals(3 * Math.PI / 2, optimizer.optimize(f, GoalType.MINIMIZE, 1, 5).getPoint(),
                     100 * optimizer.getConvergenceChecker().getRelativeThreshold());
        assertTrue(optimizer.getEvaluations() <= 100);
        assertTrue(optimizer.getEvaluations() >= 15);
        optimizer.setMaxEvaluations(10);
        try {
            optimizer.optimize(f, GoalType.MINIMIZE, 4, 5);
            fail("an exception should have been thrown");
        } catch (TooManyEvaluationsException fee) {
            // expected
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    @Test
    public void testQuinticMin() throws MathException {
        // The function has local minima at -0.27195613 and 0.82221643.
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealOptimizer optimizer = new BrentOptimizer();
        optimizer.setConvergenceChecker(new BrentOptimizer.BrentConvergenceChecker(1e-10, 1e-14));
        optimizer.setMaxEvaluations(200);
        assertEquals(-0.27195613, optimizer.optimize(f, GoalType.MINIMIZE, -0.3, -0.2).getPoint(), 1.0e-8);
        assertEquals( 0.82221643, optimizer.optimize(f, GoalType.MINIMIZE,  0.3,  0.9).getPoint(), 1.0e-8);
        assertTrue(optimizer.getEvaluations() <= 50);

        // search in a large interval
        assertEquals(-0.27195613, optimizer.optimize(f, GoalType.MINIMIZE, -1.0, 0.2).getPoint(), 1.0e-8);
        assertTrue(optimizer.getEvaluations() <= 50);
    }

    @Test
    public void testQuinticMinStatistics() throws MathException {
        // The function has local minima at -0.27195613 and 0.82221643.
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealOptimizer optimizer = new BrentOptimizer();
        optimizer.setConvergenceChecker(new BrentOptimizer.BrentConvergenceChecker(1e-12, 1e-14));
        optimizer.setMaxEvaluations(40);

        final DescriptiveStatistics[] stat = new DescriptiveStatistics[2];
        for (int i = 0; i < stat.length; i++) {
            stat[i] = new DescriptiveStatistics();
        }

        final double min = -0.75;
        final double max = 0.25;
        final int nSamples = 200;
        final double delta = (max - min) / nSamples;
        for (int i = 0; i < nSamples; i++) {
            final double start = min + i * delta;
            stat[0].addValue(optimizer.optimize(f, GoalType.MINIMIZE, min, max, start).getPoint());
            stat[1].addValue(optimizer.getEvaluations());
        }

        final double meanOptValue = stat[0].getMean();
        final double medianEval = stat[1].getPercentile(50);
        assertTrue(meanOptValue > -0.2719561281 && meanOptValue < -0.2719561280);
        assertEquals((int) medianEval, 27);
    }

    @Test(expected = TooManyEvaluationsException.class)
    public void testQuinticMax() throws MathException {
        // The quintic function has zeros at 0, +-0.5 and +-1.
        // The function has a local maximum at 0.27195613.
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealOptimizer optimizer = new BrentOptimizer();
        optimizer.setConvergenceChecker(new BrentOptimizer.BrentConvergenceChecker(1e-12, 1e-14));
        assertEquals(0.27195613, optimizer.optimize(f, GoalType.MAXIMIZE, 0.2, 0.3).getPoint(), 1e-8);
        optimizer.setMaxEvaluations(5);
        try {
            optimizer.optimize(f, GoalType.MAXIMIZE, 0.2, 0.3);
            fail("an exception should have been thrown");
        } catch (TooManyEvaluationsException miee) {
            // expected
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    @Test
    public void testMinEndpoints() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealOptimizer optimizer = new BrentOptimizer();
        optimizer.setConvergenceChecker(new BrentOptimizer.BrentConvergenceChecker(1e-8, 1e-14));
        optimizer.setMaxEvaluations(50);

        // endpoint is minimum
        double result = optimizer.optimize(f, GoalType.MINIMIZE, 3 * Math.PI / 2, 5).getPoint();
        assertEquals(3 * Math.PI / 2, result, 100 * optimizer.getConvergenceChecker().getRelativeThreshold());

        result = optimizer.optimize(f, GoalType.MINIMIZE, 4, 3 * Math.PI / 2).getPoint();
        assertEquals(3 * Math.PI / 2, result, 100 * optimizer.getConvergenceChecker().getRelativeThreshold());
    }
}
