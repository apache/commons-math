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

package org.apache.commons.math.optimization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.univariate.BrentOptimizer;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.junit.Test;

public class MultiStartUnivariateRealOptimizerTest {

    @Test
    public void testSinMin() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealOptimizer underlying = new BrentOptimizer();
        JDKRandomGenerator g = new JDKRandomGenerator();
        g.setSeed(44428400075l);
        MultiStartUnivariateRealOptimizer minimizer =
            new MultiStartUnivariateRealOptimizer(underlying, 10, g);
        minimizer.optimize(f, GoalType.MINIMIZE, -100.0, 100.0);
        double[] optima = minimizer.getOptima();
        for (int i = 1; i < optima.length; ++i) {
            double d = (optima[i] - optima[i-1]) / (2 * Math.PI);
            assertTrue (Math.abs(d - Math.rint(d)) < 1.0e-8);
            assertEquals(-1.0, f.value(optima[i]), 1.0e-10);
        }
        assertTrue(minimizer.getEvaluations() > 2900);
        assertTrue(minimizer.getEvaluations() < 3100);

    }

}
