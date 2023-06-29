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
package org.apache.commons.math4.legacy.optim;

import org.junit.Test;
import org.junit.Assert;

/**
 * Tests for {@link BaseOptimizer}.
 */
public class BaseOptimizerTest {
    @Test
    public void testDefault() {
        final DummyOptimizer dummy = new DummyOptimizer(null);

        final Object result = dummy.optimize();

        // No default checker.
        Assert.assertEquals(null, dummy.getConvergenceChecker());
        // Default "MaxIter".
        Assert.assertEquals(Integer.MAX_VALUE, dummy.getMaxIterations());
        // Default "MaxEval".
        Assert.assertEquals(0, dummy.getMaxEvaluations());
    }

    @Test
    public void testParseOptimizationData() {
        final DummyOptimizer dummy = new DummyOptimizer(null);

        final ConvergenceChecker<Object> checker = new ConvergenceChecker<Object>() {
                @Override
                public boolean converged(int iteration,
                                         Object previous,
                                         Object current) {
                    return true;
                }
            };

        final int maxEval = 123;
        final int maxIter = 4;
        final Object result = dummy.optimize(checker,
                                             new MaxEval(maxEval),
                                             new MaxIter(maxIter));

        Assert.assertEquals(checker, dummy.getConvergenceChecker());
        Assert.assertEquals(maxIter, dummy.getMaxIterations());
        Assert.assertEquals(maxEval, dummy.getMaxEvaluations());
    }
}

class DummyOptimizer extends BaseOptimizer<Object> {
    /**
     * @param checker Checker.
     */
    DummyOptimizer(ConvergenceChecker<Object> checker) {
        super(checker);
    }

    @Override
    protected Object doOptimize() {
        return new Object();
    }
}
