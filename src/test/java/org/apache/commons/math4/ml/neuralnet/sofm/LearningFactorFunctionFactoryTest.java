/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math4.ml.neuralnet.sofm;

import org.apache.commons.math4.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.exception.NumberIsTooLargeException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.ml.neuralnet.sofm.LearningFactorFunction;
import org.apache.commons.math4.ml.neuralnet.sofm.LearningFactorFunctionFactory;
import org.junit.Test;
import org.junit.Assert;

/**
 * Tests for {@link LearningFactorFunctionFactory} class.
 */
public class LearningFactorFunctionFactoryTest {
    @Test(expected=OutOfRangeException.class)
    public void testExponentialDecayPrecondition0() {
        LearningFactorFunctionFactory.exponentialDecay(0d, 0d, 2);
    }
    @Test(expected=OutOfRangeException.class)
    public void testExponentialDecayPrecondition1() {
        LearningFactorFunctionFactory.exponentialDecay(1 + 1e-10, 0d, 2);
    }
    @Test(expected=NotStrictlyPositiveException.class)
    public void testExponentialDecayPrecondition2() {
        LearningFactorFunctionFactory.exponentialDecay(1d, 0d, 2);
    }
    @Test(expected=NumberIsTooLargeException.class)
    public void testExponentialDecayPrecondition3() {
        LearningFactorFunctionFactory.exponentialDecay(1d, 1d, 100);
    }
    @Test(expected=NotStrictlyPositiveException.class)
    public void testExponentialDecayPrecondition4() {
        LearningFactorFunctionFactory.exponentialDecay(1d, 0.2, 0);
    }

    @Test
    public void testExponentialDecayTrivial() {
        final int n = 65;
        final double init = 0.5;
        final double valueAtN = 0.1;
        final LearningFactorFunction f
            = LearningFactorFunctionFactory.exponentialDecay(init, valueAtN, n);

        Assert.assertEquals(init, f.value(0), 0d);
        Assert.assertEquals(valueAtN, f.value(n), 0d);
        Assert.assertEquals(0, f.value(Long.MAX_VALUE), 0d);
    }

    @Test(expected=OutOfRangeException.class)
    public void testQuasiSigmoidDecayPrecondition0() {
        LearningFactorFunctionFactory.quasiSigmoidDecay(0d, -1d, 2);
    }
    @Test(expected=OutOfRangeException.class)
    public void testQuasiSigmoidDecayPrecondition1() {
        LearningFactorFunctionFactory.quasiSigmoidDecay(1 + 1e-10, -1d, 2);
    }
    @Test(expected=NumberIsTooLargeException.class)
    public void testQuasiSigmoidDecayPrecondition3() {
        LearningFactorFunctionFactory.quasiSigmoidDecay(1d, 0d, 100);
    }
    @Test(expected=NotStrictlyPositiveException.class)
    public void testQuasiSigmoidDecayPrecondition4() {
        LearningFactorFunctionFactory.quasiSigmoidDecay(1d, -1d, 0);
    }

    @Test
    public void testQuasiSigmoidDecayTrivial() {
        final int n = 65;
        final double init = 0.5;
        final double slope = -1e-1;
        final LearningFactorFunction f
            = LearningFactorFunctionFactory.quasiSigmoidDecay(init, slope, n);

        Assert.assertEquals(init, f.value(0), 0d);
        // Very approximate derivative.
        Assert.assertEquals(slope, f.value(n) - f.value(n - 1), 1e-2);
        Assert.assertEquals(0, f.value(Long.MAX_VALUE), 0d);
    }
}
