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

import org.apache.commons.math.exception.MaxCountExceededException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link Incrementor}.
 */
public class IncrementorTest {

    @Test
    public void testAccessor() {
        final Incrementor i = new Incrementor();

        i.setMaximalCount(10);
        Assert.assertEquals(10, i.getMaximalCount());
        Assert.assertEquals(0, i.getCount());
    }

    @Test
    public void testBelowMaxCount() {
        final Incrementor i = new Incrementor();

        i.setMaximalCount(3);
        i.incrementCount();
        i.incrementCount();
        i.incrementCount();

        Assert.assertEquals(3, i.getCount());
    }

    @Test(expected = MaxCountExceededException.class)
    public void testAboveMaxCount() {
        final Incrementor i = new Incrementor();

        i.setMaximalCount(3);
        i.incrementCount();
        i.incrementCount();
        i.incrementCount();
        i.incrementCount();
    }

    @Test
    public void testReset() {
        final Incrementor i = new Incrementor();

        i.setMaximalCount(3);
        i.incrementCount();
        i.incrementCount();
        i.incrementCount();
        Assert.assertEquals(3, i.getCount());
        i.resetCount();
        Assert.assertEquals(0, i.getCount());
    }

    @Test
    public void testBulkIncrement() {
        final Incrementor i = new Incrementor();

        i.setMaximalCount(3);
        i.incrementCount(2);
        Assert.assertEquals(2, i.getCount());
        i.incrementCount(1);
        Assert.assertEquals(3, i.getCount());
    }
}