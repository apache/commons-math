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
package org.apache.commons.math.util;

import org.junit.Test;
import org.junit.Ignore;

/**
 * Performance tests for FastMath.
 * Not enabled by default, as the class does not have Test in its name.
 * 
 * Invoke by running {@code mvn test -Dtest=FastMathTestPerformance}
 */
public class FastMathTestPerformance {
    private static final int RUNS = 10000000;

    private void print(String funcName) {
        System.out.print(funcName + " ");
    }
    private void print(long time, long unitTime) {
        final double ratio = time / (double) unitTime;
        System.out.print(time + " " + ratio + " ");
    }
    private void println(long time, long unitTime) {
        print(time, unitTime);
        System.out.println();
    }

    @Test
    public void testLog() {
        print("log");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.log(Math.PI + i/* 1.0 + i/1e9 */);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.log(Math.PI + i/* 1.0 + i/1e9 */);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.log(Math.PI + i/* 1.0 + i/1e9 */);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }

    @Test
    public void testPow() {
        print("pow");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.pow(Math.PI + i / 1e6, i / 1e6);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.pow(Math.PI + i / 1e6, i / 1e6);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.pow(Math.PI + i / 1e6, i / 1e6);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }

    @Test
    public void testExp() {
        print("exp");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.exp(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.exp(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.exp(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }

    @Test
    public void testSin() {
        print("sin");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.sin(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.sin(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.sin(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }

    @Test
    public void testAsin() {
        print("asin");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.asin(i / 10000000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.asin(i / 10000000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.asin(i / 10000000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }

    @Test
    public void testCos() {
        print("cos");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.cos(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.cos(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.cos(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }
            
    @Test
    public void testAcos() {
        print("acos");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.acos(i / 10000000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.acos(i / 10000000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.acos(i / 10000000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }

    @Test
    public void testTan() {
        print("tan");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.tan(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.tan(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.tan(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }

    @Test
    public void testAtan() {
        print("atan");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.atan(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.atan(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.atan(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }
     
    @Test
    public void testCbrt() {
        print("cbrt");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.cbrt(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.cbrt(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.cbrt(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }

    @Test
    public void testCosh() {
        print("cosh");        
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.cosh(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.cosh(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.cosh(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }

    @Test
    public void testSinh() {
        print("sinh");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.sinh(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.sinh(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.sinh(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }

    @Test
    public void testTanh() {
        print("tanh");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.tanh(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.tanh(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.tanh(i / 1000000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }
     
    @Test
    public void testExpm1() {
        print("expm1");
        double x = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += StrictMath.expm1(-i / 100000.0);
        time = System.currentTimeMillis() - time;
        long unitTime = time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += FastMath.expm1(-i / 100000.0);
        time = System.currentTimeMillis() - time;
        print(time, unitTime);

        x = 0;
        time = System.currentTimeMillis();
        for (int i = 0; i < RUNS; i++)
            x += Math.expm1(-i / 100000.0);
        time = System.currentTimeMillis() - time;
        println(time, unitTime);
    }
}
