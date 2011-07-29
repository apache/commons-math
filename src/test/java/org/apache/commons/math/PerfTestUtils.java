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
package org.apache.commons.math;

import org.apache.commons.math.analysis.function.Add;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.FunctionUtils;

/**
 * Simple benchmarking utilities.
 */
public class PerfTestUtils {
    /** Nanoseconds to milliseconds conversion factor ({@value}). */
    public static final double NANO_TO_MILLI = 1e-6;

    /**
     * Timing.
     *
     * @param repeatChunk Each timing measurement will done done for that
     * number of repeats of the code.
     * @param repeatStat Timing will be averaged over that number of runs. 
     * @param methods Code being timed.
     * @return for each of the given {@code methods}, the averaged time (in
     * milliseconds) taken by a call to {@code run}.
     */
    public static double[] time(int repeatChunk,
                                int repeatStat,
                                Runnable ... methods) {
        final int numMethods = methods.length;
        final double[][] times = new double[numMethods][repeatStat];
    
        for (int k = 0; k < repeatStat; k++) {
            for (int j = 0; j < numMethods; j++) {
                final Runnable r = methods[j];
                final long start = System.nanoTime();
                for (int i = 0; i < repeatChunk; i++) {
                    r.run();
                }
                times[j][k] = (System.nanoTime() - start) * NANO_TO_MILLI;
            }
        }

        final MultivariateRealFunction acc = FunctionUtils.collector(new Add(), 0);
        final double[] avgTimes = new double[numMethods];

        final double normFactor = 1d / (repeatStat * repeatChunk);
        for (int j = 0; j < numMethods; j++) {
            avgTimes[j] = normFactor * acc.value(times[j]);
        }

        return avgTimes;
    }

    /**
     * Timing and report (to standard output).
     *
     * @param title Title of the test (for the report).
     * @param repeatChunk Each timing measurement will done done for that
     * number of repeats of the code.
     * @param repeatStat Timing will be averaged over that number of runs. 
     * @param methods Code being timed.
     * @return for each of the given {@code methods}, the averaged time (in
     * milliseconds) taken by a call to {@code run}.
     */
    public static double[] timeAndReport(String title,
                                         int repeatChunk,
                                         int repeatStat,
                                         RunTest ... methods) {
        System.out.println(title);
        final double[] time = time(repeatChunk, repeatStat, methods);
        for (int i = 0; i < time.length; i++) {
            System.out.println(methods[i].getName() + ": " + time[i] + " ms");
        }

        return time;
    }

    /**
     * Utility class for storing a test label.
     */
    public static abstract class RunTest implements Runnable {
        private final String name;

        /**
         * @param name Test name.
         */
        public RunTest(String name) {
            this.name = name;
        }

        /**
         * @return the name of this test.
         */
        public String getName() {
            return name;
        }

        /** {@inheritDoc} */
        public abstract void run();
    }
}
