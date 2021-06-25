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
package org.apache.commons.math4.legacy.exception;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link NotFiniteNumberException}.
 */
public final class NotFiniteNumberExceptionTest {
    @Test
    public void testCheckSingle() {
        try {
            NotFiniteNumberException.check(Double.POSITIVE_INFINITY);
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
        try {
            NotFiniteNumberException.check(Double.NEGATIVE_INFINITY);
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
        try {
            NotFiniteNumberException.check(Double.NaN);
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
    }

    @Test
    public void testCheckArray() {
        try {
            NotFiniteNumberException.check(new double[] {0, -1, Double.POSITIVE_INFINITY, -2, 3});
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
        try {
            NotFiniteNumberException.check(new double[] {1, Double.NEGATIVE_INFINITY, -2, 3});
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
        try {
            NotFiniteNumberException.check(new double[] {4, 3, -1, Double.NaN, -2, 1});
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
    }
}
