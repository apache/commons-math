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
import org.apache.commons.math4.ml.neuralnet.sofm.NeighbourhoodSizeFunction;
import org.apache.commons.math4.ml.neuralnet.sofm.NeighbourhoodSizeFunctionFactory;
import org.junit.Test;
import org.junit.Assert;

/**
 * Tests for {@link NeighbourhoodSizeFunctionFactory} class.
 */
public class NeighbourhoodSizeFunctionFactoryTest {
    @Test(expected=NotStrictlyPositiveException.class)
    public void testExponentialDecayPrecondition1() {
        NeighbourhoodSizeFunctionFactory.exponentialDecay(0, 0, 2);
    }
    @Test(expected=NotStrictlyPositiveException.class)
    public void testExponentialDecayPrecondition2() {
        NeighbourhoodSizeFunctionFactory.exponentialDecay(1, 0, 2);
    }
    @Test(expected=NumberIsTooLargeException.class)
    public void testExponentialDecayPrecondition3() {
        NeighbourhoodSizeFunctionFactory.exponentialDecay(1, 1, 100);
    }
    @Test(expected=NotStrictlyPositiveException.class)
    public void testExponentialDecayPrecondition4() {
        NeighbourhoodSizeFunctionFactory.exponentialDecay(2, 1, 0);
    }

    @Test
    public void testExponentialDecayTrivial() {
        final int n = 65;
        final int init = 4;
        final int valueAtN = 3;
        final NeighbourhoodSizeFunction f
            = NeighbourhoodSizeFunctionFactory.exponentialDecay(init, valueAtN, n);

        Assert.assertEquals(init, f.value(0));
        Assert.assertEquals(valueAtN, f.value(n));
        Assert.assertEquals(0, f.value(Long.MAX_VALUE));
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testQuasiSigmoidDecayPrecondition1() {
        NeighbourhoodSizeFunctionFactory.quasiSigmoidDecay(0d, -1d, 2);
    }
    @Test(expected=NumberIsTooLargeException.class)
    public void testQuasiSigmoidDecayPrecondition3() {
        NeighbourhoodSizeFunctionFactory.quasiSigmoidDecay(1d, 0d, 100);
    }
    @Test(expected=NotStrictlyPositiveException.class)
    public void testQuasiSigmoidDecayPrecondition4() {
        NeighbourhoodSizeFunctionFactory.quasiSigmoidDecay(1d, -1d, 0);
    }

    @Test
    public void testQuasiSigmoidDecayTrivial() {
        final int n = 65;
        final double init = 4;
        final double slope = -1e-1;
        final NeighbourhoodSizeFunction f
            = NeighbourhoodSizeFunctionFactory.quasiSigmoidDecay(init, slope, n);

        Assert.assertEquals(init, f.value(0), 0d);
        Assert.assertEquals(0, f.value(Long.MAX_VALUE), 0d);
    }
}
