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
package org.apache.commons.math4.legacy.stat.descriptive;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link SynchronizedDescriptiveStatisticsTest} class.
 *          2007) $
 */
public final class SynchronizedDescriptiveStatisticsTest extends DescriptiveStatisticsTest {

    @Override
    protected DescriptiveStatistics createDescriptiveStatistics() {
        return new SynchronizedDescriptiveStatistics();
    }

    /**
     * A state-touching method of {@link SynchronizedDescriptiveStatistics} must
     * acquire the instance monitor, so a call from another thread has to block
     * while the monitor is held. The methods exercised below used not to be
     * overridden, so they ran without the lock.
     *
     * @param call the method under test.
     * @throws InterruptedException if the test is interrupted while waiting.
     */
    private void checkLocksOnInstance(final MethodCall call) throws InterruptedException {
        final SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
        stats.addValue(1);
        stats.addValue(2);
        stats.addValue(3);

        final CountDownLatch started  = new CountDownLatch(1);
        final CountDownLatch finished = new CountDownLatch(1);
        final Thread worker;
        synchronized (stats) {
            worker = new Thread(() -> {
                started.countDown();
                call.apply(stats);
                finished.countDown();
            });
            worker.start();
            // Make sure the worker is running before we check that it is blocked.
            started.await();
            Assert.assertFalse("method ran without holding the instance lock",
                               finished.await(500, TimeUnit.MILLISECONDS));
        }
        worker.join();
    }

    @Test
    public void testRemoveMostRecentValueIsSynchronized() throws InterruptedException {
        checkLocksOnInstance(DescriptiveStatistics::removeMostRecentValue);
    }

    @Test
    public void testReplaceMostRecentValueIsSynchronized() throws InterruptedException {
        checkLocksOnInstance(s -> s.replaceMostRecentValue(4));
    }

    /** A call taking a {@link DescriptiveStatistics} instance. */
    private interface MethodCall {
        /**
         * @param stats the instance to operate on.
         */
        void apply(DescriptiveStatistics stats);
    }
}
