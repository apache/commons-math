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

/**
 * Performance tests for FastMath.
 * Not enabled by default, as the class does not have Test in its name.
 * 
 * Invoke by running {@code mvn test -Dtest=FastMathTestPerformance}
 */
public class FastMathTestPerformance {
    @Test
    public void testPerformance() {
        final int numberOfRuns = 10000000;
        for (int j = 0; j < 10; j++) {
            double x = 0;
            long time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.log(Math.PI + i/* 1.0 + i/1e9 */);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.log " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.log(Math.PI + i/* 1.0 + i/1e9 */);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.log " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.pow(Math.PI + i / 1e6, i / 1e6);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.pow " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.pow(Math.PI + i / 1e6, i / 1e6);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.pow " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.exp(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.exp " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.exp(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.exp " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.sin(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.sin " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.sin(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.sin " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.asin(i / 10000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.asin " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.asin(i / 10000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.asin " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.cos(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.cos " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.cos(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.cos " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.acos(i / 10000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.acos " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.acos(i / 10000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.acos " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.tan(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.tan " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.tan(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.tan " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.atan(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.atan " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.atan(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.atan " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.cbrt(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.cbrt " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.cbrt(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.cbrt " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.cosh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.cosh " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.cosh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.cosh " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.sinh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.sinh " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.sinh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.sinh " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.tanh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.tanh " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.tanh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.tanh " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.expm1(-i / 100000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.expm1 " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.expm1(-i / 100000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.expm1 " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.expm1(-i / 100000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.expm1 " + time + "\t" + x);

        }
    }
    
}