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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link SynchronizedDescriptiveStatistics} class.
 */
public final class SynchronizedDescriptiveStatisticsTest extends DescriptiveStatisticsTest {

    /** How long a mutator is observed while the test thread holds the instance monitor. */
    private static final long LOCK_WAIT_MILLIS = 200;

    @Override
    protected DescriptiveStatistics createDescriptiveStatistics() {
        return new SynchronizedDescriptiveStatistics();
    }

    @Test
    public void testMutatorsAreSynchronized() throws NoSuchMethodException {
        // DescriptiveStatistics updates its storeless statistics in place, so every mutator has to take the
        // instance lock on the wrapper; a mutator that is not overridden runs unsynchronized.
        final Method[] mutators = {
            DescriptiveStatistics.class.getMethod("addValue", double.class),
            DescriptiveStatistics.class.getMethod("removeMostRecentValue"),
            DescriptiveStatistics.class.getMethod("replaceMostRecentValue", double.class),
            DescriptiveStatistics.class.getMethod("clear"),
            DescriptiveStatistics.class.getMethod("setWindowSize", int.class),
        };
        for (final Method mutator : mutators) {
            final Method override = SynchronizedDescriptiveStatistics.class.getDeclaredMethod(
                    mutator.getName(), mutator.getParameterTypes());
            Assert.assertTrue(mutator.getName() + "() must be synchronized",
                    Modifier.isSynchronized(override.getModifiers()));
        }
    }

    @Test
    public void testGetPercentileIsSynchronized() throws NoSuchMethodException {
        // DescriptiveStatistics.getPercentile sets the shared percentile quantile before calling the
        // synchronized apply, so the whole method (setQuantile + apply) has to take the instance lock;
        // otherwise a concurrent call can swap the quantile in between the two steps.
        final Method getPercentile = SynchronizedDescriptiveStatistics.class.getDeclaredMethod(
                "getPercentile", double.class);
        Assert.assertTrue("getPercentile(double) must be synchronized",
                Modifier.isSynchronized(getPercentile.getModifiers()));
    }

    @Test
    public void testRemoveMostRecentValueWaitsForInstanceLock() throws InterruptedException {
        assertWaitsForInstanceLock(false);
    }

    @Test
    public void testReplaceMostRecentValueWaitsForInstanceLock() throws InterruptedException {
        assertWaitsForInstanceLock(true);
    }

    /**
     * Starts a mutator while the test thread holds the instance monitor and checks that it does not finish
     * before the monitor is released.
     *
     * @param replace {@code true} to call replaceMostRecentValue, {@code false} to call removeMostRecentValue
     */
    private void assertWaitsForInstanceLock(final boolean replace) throws InterruptedException {
        final SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
        stats.addValue(1);
        final CountDownLatch started = new CountDownLatch(1);
        final CountDownLatch finished = new CountDownLatch(1);
        final Thread mutator = new Thread(() -> {
            started.countDown();
            if (replace) {
                stats.replaceMostRecentValue(2);
            } else {
                stats.removeMostRecentValue();
            }
            finished.countDown();
        }, "mutator");
        synchronized (stats) {
            mutator.start();
            Assert.assertTrue("mutator thread did not start", started.await(5, TimeUnit.SECONDS));
            Assert.assertFalse("mutator must wait for the instance lock",
                    finished.await(LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS));
        }
        Assert.assertTrue("mutator did not complete after the lock was released",
                finished.await(5, TimeUnit.SECONDS));
        if (replace) {
            Assert.assertEquals(1, stats.getN());
            Assert.assertEquals(2, stats.getElement(0), 0);
        } else {
            Assert.assertEquals(0, stats.getN());
        }
    }

    @Test
    public void testGetPercentileAppliesRequestedQuantileAtomically() throws InterruptedException {
        final SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
        final DescriptiveStatistics reference = new DescriptiveStatistics();
        for (int i = 1; i <= 100; i++) {
            stats.addValue(i);
            reference.addValue(i);
        }
        final double expectedQuartile = reference.getPercentile(25);
        final double expectedTop = reference.getPercentile(90);
        Assert.assertTrue("test setup: the two quantiles must differ", expectedQuartile != expectedTop);

        final AtomicReference<Double> other = new AtomicReference<>();
        final Thread percentileCaller = new Thread(() -> other.set(stats.getPercentile(25)), "percentile-caller");
        synchronized (stats) {
            percentileCaller.start();
            // Hold the instance monitor and wait until the other thread blocks trying to acquire it.
            // If getPercentile were not synchronized, the other thread would already have set the
            // quantile to 25 outside the lock and be parked inside apply(); with the synchronized
            // override it parks at the method entry, so our own call cannot disturb its quantile.
            final long deadline = System.currentTimeMillis() + 5000;
            while (percentileCaller.getState() != Thread.State.BLOCKED
                    && System.currentTimeMillis() < deadline) {
                Thread.sleep(1);
            }
            Assert.assertEquals("other thread should be blocked on the instance monitor",
                    Thread.State.BLOCKED, percentileCaller.getState());
            Assert.assertEquals("our own call must still see the requested quantile",
                    expectedTop, stats.getPercentile(90), 0);
        }
        percentileCaller.join(5000);
        Assert.assertFalse("other thread did not complete", percentileCaller.isAlive());
        Assert.assertEquals("getPercentile must apply the requested quantile atomically",
                expectedQuartile, other.get(), 0);
    }
}
