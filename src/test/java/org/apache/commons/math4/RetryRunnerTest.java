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

package org.apache.commons.math4;

import java.util.Random;

import org.apache.commons.math4.exception.MathIllegalStateException;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for the "Retry" functionality (retrying Junit test methods).
 */
@RunWith(RetryRunner.class)
public class RetryRunnerTest {
    final Random rng = new Random();

    /**
     * Shows that an always failing test will fail even if it is retried.
     */
    @Test(expected=MathIllegalStateException.class)
    @Retry
    public void testRetryFailAlways() {
        throw new MathIllegalStateException();
    }

    /**
     * Shows that a test that sometimes fail might succeed if it is retried.
     * In this case the high number of retries makes it quite unlikely that
     * the exception will be thrown by all of the calls.
     */
    @Test
    @Retry(100)
    public void testRetryFailSometimes() {
        if (rng.nextBoolean()) {
            throw new MathIllegalStateException();
        }
    }
}
