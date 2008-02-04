// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.algebra;

import junit.framework.*;

public class PolynomialFractionTest
  extends TestCase {

  public PolynomialFractionTest(String name) {
    super(name);
  }

  public void testNullDenominator() {
    try {
      new PolynomialFraction(1l, 0l);
      fail("an exception should have been thrown");
    } catch (ArithmeticException e) {
    } catch (Exception e) {
      fail("wrong exception caught");
    }
  }

  public void testToString() {
    checkValue(new PolynomialFraction(1l, 2l),  "1/2");
    checkValue(new PolynomialFraction(-1l, 2l), "-1/2");
    checkValue(new PolynomialFraction(1l, -2l), "-1/2");
    checkValue(new PolynomialFraction(-1l, -2l), "1/2");
    checkValue(new PolynomialFraction(0l, 500l), "0");
    checkValue(new PolynomialFraction(-12l), "-12");
    checkValue(new PolynomialFraction(12l), "12");
  }

  public void testSimplification() {
    checkValue(new PolynomialFraction(2l, 4l), "1/2");
    checkValue(new PolynomialFraction(307692l, 999999l), "4/13");
    checkValue(new PolynomialFraction(999999l, 307692l), "13/4");
  }

  public void testInvert() {

    PolynomialFraction f = new PolynomialFraction(2l, 4l);
    f= f.invert();
    checkValue(f, "2");
    f = f.invert();
    checkValue(f, "1/2");

    f = new PolynomialFraction(120l);
    f = f.invert();
    checkValue(f, "1/120");

    f = new PolynomialFraction(0l, 4l);
    try {
      f = f.invert();
      fail("an exception should have been thrown");
    } catch (ArithmeticException e) {
    } catch (Exception e) {
      fail("wrong exception caught");
    }

    f = new PolynomialFraction(307692l, 999999l);
    PolynomialFraction fInverse = f.invert();
    checkValue(fInverse, "13/4");
    checkValue(f, "4/13");

  }

  public void testAddition() {

    PolynomialFraction f1 = new PolynomialFraction(4l, 6l);
    f1 = f1.add(f1);
    checkValue(f1, "4/3");

    checkValue(new PolynomialFraction(17l, 3l).add(new PolynomialFraction(-17l, 3l)),
               "0");
    checkValue(new PolynomialFraction(2l, 3l).add(new PolynomialFraction(3l, 4l)),
               "17/12");
    checkValue(new PolynomialFraction(1l, 6l).add(new PolynomialFraction(2l, 6l)),
               "1/2");
    checkValue(new PolynomialFraction(4l, 5l).add(new PolynomialFraction(-3l, 4l)),
               "1/20");
    checkValue(new PolynomialFraction(-3l, 4l).add(new PolynomialFraction(4l, 5l)),
               "1/20");

  }

  public void testSubtraction() {

    PolynomialFraction f1 = new PolynomialFraction(4l, 6l);
    checkValue(f1.subtract(f1), "0");

    checkValue(new PolynomialFraction(7l, 3l).subtract(new PolynomialFraction(-7l, 3l)),
               "14/3");

    checkValue(new PolynomialFraction(3l, 4l).subtract(new PolynomialFraction(2l, 3l)),
               "1/12");
    checkValue(new PolynomialFraction(3l, 4l).subtract(new PolynomialFraction(-2l, 3l)),
               "17/12");
    checkValue(new PolynomialFraction(-3l, 4l).subtract(new PolynomialFraction(2l, 3l)),
               "-17/12");
    checkValue(new PolynomialFraction(-3l, 4l).subtract(new PolynomialFraction(-2l, 3l)),
               "-1/12");

    checkValue(new PolynomialFraction(2l, 3l).subtract(new PolynomialFraction(3l, 4l)),
               "-1/12");
    checkValue(new PolynomialFraction(-2l, 3l).subtract(new PolynomialFraction(3l, 4l)),
               "-17/12");
    checkValue(new PolynomialFraction(2l, 3l).subtract(new PolynomialFraction(-3l, 4l)),
               "17/12");
    checkValue(new PolynomialFraction(-2l, 3l).subtract(new PolynomialFraction(-3l, 4l)),
               "1/12");

    checkValue(new PolynomialFraction(1l, 6l).subtract(new PolynomialFraction(2l, 6l)),
               "-1/6");
    checkValue(new PolynomialFraction(1l, 2l).subtract(new PolynomialFraction(1l, 6l)),
               "1/3");

  }

  public void testMultiplication() {

    PolynomialFraction f = new PolynomialFraction(2l, 3l);
    checkValue(f.multiply(new PolynomialFraction(9l,4l)), "3/2");

    checkValue(new PolynomialFraction(1l, 2l).multiply(new PolynomialFraction(0l)),
               "0");
    checkValue(new PolynomialFraction(4l, 15l).multiply(new PolynomialFraction(-5l, 2l)),
               "-2/3");
    checkValue(new PolynomialFraction(-4l, 15l).multiply(new PolynomialFraction(5l, 2l)),
               "-2/3");
    checkValue(new PolynomialFraction(4l, 15l).multiply(new PolynomialFraction(5l, 2l)),
               "2/3");
    checkValue(new PolynomialFraction(-4l, 15l).multiply(new PolynomialFraction(-5l, 2l)),
               "2/3");

  }

  public void testDivision() {

    PolynomialFraction f = new PolynomialFraction(2l, 3l);
    ;
    checkValue(f.divide(new PolynomialFraction(4l,9l)), "3/2");

    try {
      new PolynomialFraction(1l, 2l).divide(new PolynomialFraction(0l));
      fail("an exception should have been thrown");
    } catch (ArithmeticException e) {
    } catch (Exception e) {
      fail("wrong exception caught");
    }

    checkValue(new PolynomialFraction(4l, 15l).divide(new PolynomialFraction(-2l, 5l)),
               "-2/3");
    checkValue(new PolynomialFraction(-4l, 15l).divide(new PolynomialFraction(2l, 5l)),
               "-2/3");
    checkValue(new PolynomialFraction(4l, 15l).divide(new PolynomialFraction(2l, 5l)),
               "2/3");
    checkValue(new PolynomialFraction(-4l, 15l).divide(new PolynomialFraction(-2l, 5l)),
               "2/3");

  }

  public void testEuclidianDivision() {
    checkValue(new PolynomialFraction(new Polynomial.Rational(1l, 0l, -1l),
                                      new Polynomial.Rational(2l, 2l)),
               "-1/2 + 1/2 x");
    checkValue(new PolynomialFraction(new Polynomial.Rational(1l, 3l, 2l),
                                      new Polynomial.Rational(2l, 10l, 12l)),
               "(1 + x)/(6 + 2 x)");
  }

  private void checkValue(PolynomialFraction f, String reference) {
    assertTrue(f.toString().equals(reference));
  }

  public static Test suite() {
    return new TestSuite(PolynomialFractionTest.class);
  }

}
