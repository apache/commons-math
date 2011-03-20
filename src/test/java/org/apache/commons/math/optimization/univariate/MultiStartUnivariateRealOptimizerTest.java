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

import org.apache.commons.math.exception.MathUserException;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.univariate.BrentOptimizer;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.util.FastMath;
import org.junit.Test;
import org.junit.Assert;

public class MultiStartUnivariateRealOptimizerTest {

    @Test
    public void testSinMin() {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealOptimizer underlying = new BrentOptimizer(1e-10, 1e-14);
        JDKRandomGenerator g = new JDKRandomGenerator();
        g.setSeed(44428400075l);
        MultiStartUnivariateRealOptimizer<UnivariateRealFunction> optimizer =
            new MultiStartUnivariateRealOptimizer<UnivariateRealFunction>(underlying, 10, g);
        optimizer.optimize(300, f, GoalType.MINIMIZE, -100.0, 100.0);
        UnivariateRealPointValuePair[] optima = optimizer.getOptima();
        for (int i = 1; i < optima.length; ++i) {
            double d = (optima[i].getPoint() - optima[i-1].getPoint()) / (2 * FastMath.PI);
            Assert.assertTrue(FastMath.abs(d - FastMath.rint(d)) < 1.0e-8);
            Assert.assertEquals(-1.0, f.value(optima[i].getPoint()), 1.0e-10);
            Assert.assertEquals(f.value(optima[i].getPoint()), optima[i].getValue(), 1.0e-10);
        }
        Assert.assertTrue(optimizer.getEvaluations() > 200);
        Assert.assertTrue(optimizer.getEvaluations() < 300);
    }

    @Test
    public void testQuinticMin() {
        // The quintic function has zeros at 0, +-0.5 and +-1.
        // The function has extrema (first derivative is zero) at 0.27195613 and 0.82221643,
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealOptimizer underlying = new BrentOptimizer(1e-9, 1e-14);
        JDKRandomGenerator g = new JDKRandomGenerator();
        g.setSeed(4312000053L);
        MultiStartUnivariateRealOptimizer<UnivariateRealFunction> optimizer =
            new MultiStartUnivariateRealOptimizer<UnivariateRealFunction>(underlying, 5, g);

        UnivariateRealPointValuePair optimum
            = optimizer.optimize(300, f, GoalType.MINIMIZE, -0.3, -0.2);
        Assert.assertEquals(-0.2719561293, optimum.getPoint(), 1e-9);
        Assert.assertEquals(-0.0443342695, optimum.getValue(), 1e-9);

        UnivariateRealPointValuePair[] optima = optimizer.getOptima();
        for (int i = 0; i < optima.length; ++i) {
            Assert.assertEquals(f.value(optima[i].getPoint()), optima[i].getValue(), 1e-9);
        }
        Assert.assertTrue(optimizer.getEvaluations() >= 50);
        Assert.assertTrue(optimizer.getEvaluations() <= 100);
    }

    @Test
    public void testBadFunction() {
        UnivariateRealFunction f = new UnivariateRealFunction() {
                public double value(double x) {
                    if (x < 0) {
                        throw new MathUserException();
                    }
                    return 0;
                }
            };
        UnivariateRealOptimizer underlying = new BrentOptimizer(1e-9, 1e-14);
        JDKRandomGenerator g = new JDKRandomGenerator();
        g.setSeed(4312000053L);
        MultiStartUnivariateRealOptimizer<UnivariateRealFunction> optimizer =
            new MultiStartUnivariateRealOptimizer<UnivariateRealFunction>(underlying, 5, g);
 
        try {
            optimizer.optimize(300, f, GoalType.MINIMIZE, -0.3, -0.2);
            Assert.fail();
        } catch (MathUserException e) {
            // Expected.
        }

        // Ensure that the exception was thrown because no optimum was found.
        Assert.assertTrue(optimizer.getOptima()[0] == null);
    }
}
