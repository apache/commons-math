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
package org.apache.commons.math4.legacy.analysis.function;

import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.legacy.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.legacy.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

public class SqrtTest {
   @Test
   public void testComparison() {
       final Sqrt s = new Sqrt();
       final UnivariateFunction f = new UnivariateFunction() {
           @Override
           public double value(double x) {
               return JdkMath.sqrt(x);
           }
       };

       for (double x = 1e-30; x < 1e10; x *= 2) {
           final double fX = f.value(x);
           final double sX = s.value(x);
           Assert.assertEquals("x=" + x, fX, sX, 0);
       }
   }

   @Test
   public void testDerivativeComparison() {
       final UnivariateDifferentiableFunction sPrime = new Sqrt();
       final UnivariateFunction f = new UnivariateFunction() {
               @Override
            public double value(double x) {
                   return 1 / (2 * JdkMath.sqrt(x));
               }
           };

       for (double x = 1e-30; x < 1e10; x *= 2) {
           final double fX = f.value(x);
           final double sX = sPrime.value(new DerivativeStructure(1, 1, 0, x)).getPartialDerivative(1);
           Assert.assertEquals("x=" + x, fX, sX, JdkMath.ulp(fX));
       }
   }

   @Test
   public void testDerivativesHighOrder() {
       DerivativeStructure s = new Sqrt().value(new DerivativeStructure(1, 5, 0, 1.2));
       Assert.assertEquals(1.0954451150103322269, s.getPartialDerivative(0), 1.0e-16);
       Assert.assertEquals(0.45643546458763842789, s.getPartialDerivative(1), 1.0e-16);
       Assert.assertEquals(-0.1901814435781826783,  s.getPartialDerivative(2), 1.0e-16);
       Assert.assertEquals(0.23772680447272834785,  s.getPartialDerivative(3), 1.0e-16);
       Assert.assertEquals(-0.49526417598485072465,   s.getPartialDerivative(4), 1.0e-16);
       Assert.assertEquals(1.4445205132891479465,  s.getPartialDerivative(5), 5.0e-16);
   }

}
