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
package org.apache.commons.math4.userguide;

import org.apache.commons.math4.PerfTestUtils;
import org.apache.commons.math4.util.FastMath;

/**
 * Performance benchmark for FastMath.
 *
 */
public class FastMathTestPerformance {
    private static final int RUNS = Integer.parseInt(System.getProperty("testRuns","10000000"));
    private static final double F1 = 1d / RUNS;

    // Header format
    private static final String FMT_HDR = "%-13s %13s %13s %13s Runs=%d Java %s (%s) %s (%s)";
    // Detail format
    private static final String FMT_DTL = "%-13s %6d %6.1f %6d %6.4f %6d %6.4f";

    public static void main(String[] args) {
        System.out.println(String.format(FMT_HDR,
                                         "Name","StrictMath","FastMath","Math",RUNS,
                                         System.getProperty("java.version"),
                                         System.getProperty("java.runtime.version","?"),
                                         System.getProperty("java.vm.name"),
                                         System.getProperty("java.vm.version")
                                         ));
        testAbs();
        testAcos();
        testAsin();
        testAtan();
        testAtan2();
        testCbrt();
        testCos();
        testCosh();
        testExp();
        testExpm1();
        testHypot();
        testLog();
        testLog10();
        testLog1p();
        testPow();
        testSin();
        testSinh();
        testSqrt();
        testTan();
        testTanh();
        testIEEEremainder();

        testSimpleBenchmark();
    }

    @SuppressWarnings("boxing")
    private static void report(String name, long strictMathTime, long fastMathTime, long mathTime) {
        long unitTime = strictMathTime;
        System.out.println(String.format(FMT_DTL,
                name,
                strictMathTime / RUNS, (double) strictMathTime / unitTime,
                fastMathTime / RUNS, (double) fastMathTime / unitTime,
                mathTime / RUNS, (double) mathTime / unitTime
                ));
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            System.err.println("assertion failed!");
            System.exit(1);
        }
    }
    private static void testLog() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.log(0.01 + i);
        }
        long strictMath = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.log(0.01 + i);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.log(0.01 + i);
        }
        long mathTime = System.nanoTime() - time;

        report("log",strictMath,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testLog10() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.log10(0.01 + i);
        }
        long strictMath = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.log10(0.01 + i);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.log10(0.01 + i);
        }
        long mathTime = System.nanoTime() - time;

        report("log10",strictMath,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testLog1p() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.log1p(-0.9 + i);
        }
        long strictMath = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.log1p(-0.9 + i);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.log1p(-0.9 + i);
        }
        long mathTime = System.nanoTime() - time;

        report("log1p",strictMath,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testPow() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.pow(0.01 + i * F1, i * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.pow(0.01 + i * F1, i * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.pow(0.01 + i * F1, i * F1);
        }
        long mathTime = System.nanoTime() - time;
        report("pow",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testExp() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.exp(100 * i * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.exp(100 * i * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.exp(100 * i * F1);
        }
        long mathTime = System.nanoTime() - time;

        report("exp",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testSin() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.sin(100 * (i - RUNS/2) * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.sin(100 * (i - RUNS/2) * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.sin(100 * (i - RUNS/2) * F1);
        }
        long mathTime = System.nanoTime() - time;

        report("sin",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testAsin() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.asin(0.999 * (i - RUNS/2) * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.asin(0.999 * (i - RUNS/2) * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.asin(0.999 * (i - RUNS/2) * F1);
        }
        long mathTime = System.nanoTime() - time;

        report("asin",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testCos() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.cos(100 * (i - RUNS/2) * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.cos(100 * (i - RUNS/2) * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.cos(100 * (i - RUNS/2) * F1);
        }
        long mathTime = System.nanoTime() - time;

        report("cos",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testAcos() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.acos(0.999 * (i - RUNS/2) * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.acos(0.999 * (i - RUNS/2) * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.acos(0.999 * (i - RUNS/2) * F1);
        }
        long mathTime = System.nanoTime() - time;
        report("acos",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testTan() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.tan(100 * (i - RUNS/2) * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.tan(100 * (i - RUNS/2) * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.tan(100 * (i - RUNS/2) * F1);
        }
        long mathTime = System.nanoTime() - time;

        report("tan",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testAtan() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.atan(100 * (i - RUNS/2) * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.atan(100 * (i - RUNS/2) * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.atan(100 * (i - RUNS/2) * F1);
        }
        long mathTime = System.nanoTime() - time;

        report("atan",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testAtan2() {
        double x = 0;
        long time = System.nanoTime();
        int max   = (int) FastMath.floor(FastMath.sqrt(RUNS));
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                x += StrictMath.atan2((i - max/2) * (100.0 / max), (j - max/2) * (100.0 / max));
            }
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                x += FastMath.atan2((i - max/2) * (100.0 / max), (j - max/2) * (100.0 / max));
            }
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                x += Math.atan2((i - max/2) * (100.0 / max), (j - max/2) * (100.0 / max));
            }
        }
        long mathTime = System.nanoTime() - time;

        report("atan2",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testHypot() {
        double x = 0;
        long time = System.nanoTime();
        int max   = (int) FastMath.floor(FastMath.sqrt(RUNS));
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                x += StrictMath.atan2((i - max/2) * (100.0 / max), (j - max/2) * (100.0 / max));
            }
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                x += FastMath.atan2((i - max/2) * (100.0 / max), (j - max/2) * (100.0 / max));
            }
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                x += Math.atan2((i - max/2) * (100.0 / max), (j - max/2) * (100.0 / max));
            }
        }
        long mathTime = System.nanoTime() - time;

        report("hypot",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testIEEEremainder() {
        double x = 0;
        long time = System.nanoTime();
        int max   = (int) FastMath.floor(FastMath.sqrt(RUNS));
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                x += StrictMath.IEEEremainder((i - max/2) * (100.0 / max), (j + 1) * (100.0 / max));
            }
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                x += FastMath.IEEEremainder((i - max/2) * (100.0 / max), (j + 1) * (100.0 / max));
            }
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                x += Math.IEEEremainder((i - max/2) * (100.0 / max), (j + 1) * (100.0 / max));
            }
        }
        long mathTime = System.nanoTime() - time;

        report("IEEEremainder",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testCbrt() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.cbrt(100 * i * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.cbrt(100 * i * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.cbrt(100 * i * F1);
        }
        long mathTime = System.nanoTime() - time;

        report("cbrt",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testSqrt() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.sqrt(100 * i * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.sqrt(100 * i * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.sqrt(100 * i * F1);
        }
        long mathTime = System.nanoTime() - time;

        report("sqrt",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testCosh() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.cosh(100 * (i - RUNS/2) * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.cosh(100 * (i - RUNS/2) * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.cosh(100 * (i - RUNS/2) * F1);
        }
        long mathTime = System.nanoTime() - time;

        report("cosh",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testSinh() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.sinh(100 * (i - RUNS/2) * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.sinh(100 * (i - RUNS/2) * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.sinh(100 * (i - RUNS/2) * F1);
        }
        long mathTime = System.nanoTime() - time;

        report("sinh",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testTanh() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.tanh(100 * (i - RUNS/2) * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.tanh(100 * (i - RUNS/2) * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.tanh(100 * (i - RUNS/2) * F1);
        }
        long mathTime = System.nanoTime() - time;

        report("tanh",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testExpm1() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.expm1(100 * (i - RUNS/2) * F1);
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.expm1(100 * (i - RUNS/2) * F1);
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.expm1(100 * (i - RUNS/2) * F1);
        }
        long mathTime = System.nanoTime() - time;
        report("expm1",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    private static void testAbs() {
        double x = 0;
        long time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += StrictMath.abs(i * (1 - 0.5 * RUNS));
        }
        long strictTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += FastMath.abs(i * (1 - 0.5 * RUNS));
        }
        long fastTime = System.nanoTime() - time;

        x = 0;
        time = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            x += Math.abs(i * (1 - 0.5 * RUNS));
        }
        long mathTime = System.nanoTime() - time;

        report("abs",strictTime,fastTime,mathTime);
        assertTrue(!Double.isNaN(x));
    }

    @SuppressWarnings("boxing")
    private static void testSimpleBenchmark() {
        final String SM = "StrictMath";
        final String M = "Math";
        final String FM = "FastMath";

        final int maxWidth = 15;
        final int numStat = 100;
        final int numCall = RUNS / numStat;

        final double x = Math.random();
        final double y = Math.random();

        PerfTestUtils.timeAndReport("log",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.log(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.log(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.log(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("log10",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.log10(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.log10(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.log10(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("log1p",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.log1p(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.log1p(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.log1p(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("pow",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.pow(x, y);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.pow(x, y);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.pow(x, y);
                                        }
                                    });

        PerfTestUtils.timeAndReport("exp",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.exp(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.exp(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.exp(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("sin",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.sin(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.sin(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.sin(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("asin",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.asin(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.asin(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.asin(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("cos",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.cos(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.cos(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.cos(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("acos",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.acos(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.acos(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.acos(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("tan",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.tan(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.tan(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.tan(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("atan",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.atan(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.atan(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.atan(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("atan2",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.atan2(x, y);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.atan2(x, y);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.atan2(x, y);
                                        }
                                    });

        PerfTestUtils.timeAndReport("hypot",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.hypot(x, y);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.hypot(x, y);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.hypot(x, y);
                                        }
                                    });


        PerfTestUtils.timeAndReport("cbrt",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.cbrt(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.cbrt(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.cbrt(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("sqrt",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.sqrt(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.sqrt(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.sqrt(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("cosh",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.cosh(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.cosh(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.cosh(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("sinh",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.sinh(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.sinh(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.sinh(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("tanh",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.tanh(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.tanh(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.tanh(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("expm1",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.expm1(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.expm1(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.expm1(x);
                                        }
                                    });

        PerfTestUtils.timeAndReport("abs",
                                    maxWidth,
                                    numCall,
                                    numStat,
                                    false,
                                    new PerfTestUtils.RunTest(SM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return StrictMath.abs(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(M) {
                                        @Override
                                        public Double call() throws Exception {
                                            return Math.abs(x);
                                        }
                                    },
                                    new PerfTestUtils.RunTest(FM) {
                                        @Override
                                        public Double call() throws Exception {
                                            return FastMath.abs(x);
                                        }
                                    });
    }
}
