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
package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.junit.Test;
import org.junit.Assert;

public class SimpleUnivariateValueCheckerTest {
    @Test(expected=NotStrictlyPositiveException.class)
    public void testIterationCheckPrecondition() {
        new SimpleUnivariateValueChecker(1e-1, 1e-2, 0);
    }

    @Test
    public void testIterationCheck() {
        final int max = 10;
        final SimpleUnivariateValueChecker checker = new SimpleUnivariateValueChecker(1e-1, 1e-2, max);
        Assert.assertTrue(checker.converged(max, null, null));
        Assert.assertTrue(checker.converged(max + 1, null, null));
    }

    @Test
    public void testIterationCheckDisabled() {
        final SimpleUnivariateValueChecker checker = new SimpleUnivariateValueChecker(1e-8, 1e-8);

        final UnivariatePointValuePair a = new UnivariatePointValuePair(1d, 1d);
        final UnivariatePointValuePair b = new UnivariatePointValuePair(10d, 10d);

        Assert.assertFalse(checker.converged(-1, a, b));
        Assert.assertFalse(checker.converged(0, a, b));
        Assert.assertFalse(checker.converged(1000000, a, b));

        Assert.assertTrue(checker.converged(-1, a, a));
        Assert.assertTrue(checker.converged(-1, b, b));
    }

}
