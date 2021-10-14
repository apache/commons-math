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

package org.apache.commons.math4.legacy.analysis.function;

import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for all uncovered classes in org.apache.commons.math4.legacy.analysis.function that implement BivariateFunction explicitly.
 */
public class BivariateFunctionTest {

    private static final double EPS = Math.ulp(1d);

    @Test
    public void testAtan2() {
        Atan2 atan2 = new Atan2();
        Assert.assertEquals(JdkMath.PI/4,atan2.value(1,1), EPS);
    }

    @Test
    public void testSubtract() {
        Subtract subtract = new Subtract();
        Assert.assertEquals(5, subtract.value(10,5), EPS);
        Assert.assertEquals(-5, subtract.value(5,10), EPS);
    }

}
