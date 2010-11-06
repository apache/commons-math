/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.math.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link Pair}.
 */
public class PairTest {

    @Test
    public void testAccessor() {
        final Pair<Integer, Double> p
            = new Pair<Integer, Double>(new Integer(1), new Double(2));
        Assert.assertEquals(new Integer(1), p.getKey());
        Assert.assertEquals(new Double(2), p.getValue(), Math.ulp(1d));
    }

    @Test
    public void testEquals() {
        Pair<Integer, Double> p1 = new Pair<Integer, Double>(null, null);
        Assert.assertFalse(p1.equals(null));

        Pair<Integer, Double> p2 = new Pair<Integer, Double>(null, null);
        Assert.assertTrue(p1.equals(p2));

        p1 = new Pair<Integer, Double>(new Integer(1), new Double(2));
        Assert.assertFalse(p1.equals(p2));

        p2 = new Pair<Integer, Double>(new Integer(1), new Double(2));
        Assert.assertTrue(p1.equals(p2));

        Pair<Integer, Float> p3 = new Pair<Integer, Float>(new Integer(1), new Float(2));
        Assert.assertFalse(p1.equals(p3));
    }
}
