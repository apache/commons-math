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
package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import org.junit.Assert;

public class SincTest {

   @Test
   public void testShortcut() {
       final Sinc s = new Sinc();
       final UnivariateFunction f = new UnivariateFunction() {
               public double value(double x) {
                   return FastMath.sin(x) / x;
               }
           };

       for (double x = 1e-30; x < 1e10; x *= 2) {
           final double fX = f.value(x);
           final double sX = s.value(x);
           Assert.assertEquals("x=" + x, fX, sX, 0);
       }
   }

   @Test
   public void testCrossings() {
       final Sinc s = new Sinc(true);
       final int numCrossings = 1000;
       final double tol = 2e-16;
       for (int i = 1; i <= numCrossings; i++) {
           Assert.assertEquals("i=" + i, 0, s.value(i), tol);
       }
   }

   @Test
   public void testZero() {
       final Sinc s = new Sinc();
       Assert.assertEquals(1d, s.value(0), 0);
   }

   @Test
   public void testEuler() {
       final Sinc s = new Sinc();
       final double x = 123456.789;
       double prod = 1;
       double xOverPow2 = x / 2;
       while (xOverPow2 > 0) {
           prod *= FastMath.cos(xOverPow2);
           xOverPow2 /= 2;
       }
       Assert.assertEquals(prod, s.value(x), 1e-13);
   }

   @Test
   public void testDerivativeZero() {
       final UnivariateFunction sPrime = (new Sinc(true)).derivative();

       Assert.assertEquals(0, sPrime.value(0), 0);
   }

   @Test
   public void testDerivativeShortcut() {
       final UnivariateFunction sPrime = (new Sinc()).derivative();
       final UnivariateFunction f = new UnivariateFunction() {
               public double value(double x) {
                   return (FastMath.cos(x) - FastMath.sin(x) / x) / x;
               }
           };

       for (double x = 1e-30; x < 1e10; x *= 2) {
           final double fX = f.value(x);
           final double sX = sPrime.value(x);
           Assert.assertEquals("x=" + x, fX, sX, 0);
       }
   }
}
