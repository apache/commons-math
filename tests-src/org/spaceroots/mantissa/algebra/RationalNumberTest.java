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

public class RationalNumberTest
  extends TestCase {

  public RationalNumberTest(String name) {
    super(name);
  }

  public void testNullDenominator() {
    try {
      new RationalNumber(1l, 0l);
      fail("an exception should have been thrown");
    } catch (ArithmeticException e) {
    } catch (Exception e) {
      fail("wrong exception caught");
    }
  }

  public void testToString() {
    checkValue(new RationalNumber(1l, 2l),  "1/2");
    checkValue(new RationalNumber(-1l, 2l), "-1/2");
    checkValue(new RationalNumber(1l, -2l), "-1/2");
    checkValue(new RationalNumber(-1l, -2l), "1/2");
    checkValue(new RationalNumber(0l, 500l), "0");
    checkValue(new RationalNumber(-12l), "-12");
    checkValue(new RationalNumber(12l), "12");
  }

  public void testSimplification() {
    checkValue(new RationalNumber(2l, 4l), "1/2");
    checkValue(new RationalNumber(307692l, 999999l), "4/13");
    checkValue(new RationalNumber(999999l, 307692l), "13/4");
  }

  public void testInvert() {

    RationalNumber f = new RationalNumber(2l, 4l);
    f.invertSelf();
    checkValue(f, "2");
    f.invertSelf();
    checkValue(f, "1/2");

    f = new RationalNumber(120l);
    f.invertSelf();
    checkValue(f, "1/120");

    f = new RationalNumber(0l, 4l);
    try {
      f.invertSelf();
      fail("an exception should have been thrown");
    } catch (ArithmeticException e) {
    } catch (Exception e) {
      fail("wrong exception caught");
    }

    f = new RationalNumber(307692l, 999999l);
    RationalNumber fInverse = RationalNumber.invert(f);
    checkValue(fInverse, "13/4");
    checkValue(f, "4/13");

  }

  public void testAddition() {

    RationalNumber f1 = new RationalNumber(4l, 6l);
    f1.addToSelf(f1);
    checkValue(f1, "4/3");

    checkValue(RationalNumber.add(new RationalNumber(17l, 3l),
                                  new RationalNumber(-17l, 3l)),
               "0");
    checkValue(RationalNumber.add(new RationalNumber(2l, 3l),
                                  new RationalNumber(3l, 4l)),
               "17/12");
    checkValue(RationalNumber.add(new RationalNumber(1l, 6l),
                                  new RationalNumber(2l, 6l)),
               "1/2");
    checkValue(RationalNumber.add(new RationalNumber(4l, 5l),
                                  new RationalNumber(-3l, 4l)),
               "1/20");
    checkValue(RationalNumber.add(new RationalNumber(-3l, 4l),
                                  new RationalNumber(4l, 5l)),
               "1/20");

  }

  public void testSubtraction() {

    RationalNumber f1 = new RationalNumber(4l, 6l);
    f1.subtractFromSelf(f1);
    checkValue(f1, "0");

    checkValue(RationalNumber.subtract(new RationalNumber(7l, 3l),
                                       new RationalNumber(-7l, 3l)),
               "14/3");

    checkValue(RationalNumber.subtract(new RationalNumber(3l, 4l),
                                       new RationalNumber(2l, 3l)),
               "1/12");
    checkValue(RationalNumber.subtract(new RationalNumber(3l, 4l),
                                       new RationalNumber(-2l, 3l)),
               "17/12");
    checkValue(RationalNumber.subtract(new RationalNumber(-3l, 4l),
                                       new RationalNumber(2l, 3l)),
               "-17/12");
    checkValue(RationalNumber.subtract(new RationalNumber(-3l, 4l),
                                       new RationalNumber(-2l, 3l)),
               "-1/12");

    checkValue(RationalNumber.subtract(new RationalNumber(2l, 3l),
                                       new RationalNumber(3l, 4l)),
               "-1/12");
    checkValue(RationalNumber.subtract(new RationalNumber(-2l, 3l),
                                       new RationalNumber(3l, 4l)),
               "-17/12");
    checkValue(RationalNumber.subtract(new RationalNumber(2l, 3l),
                                       new RationalNumber(-3l, 4l)),
               "17/12");
    checkValue(RationalNumber.subtract(new RationalNumber(-2l, 3l),
                                       new RationalNumber(-3l, 4l)),
               "1/12");

    checkValue(RationalNumber.subtract(new RationalNumber(1l, 6l),
                                       new RationalNumber(2l, 6l)),
               "-1/6");
    checkValue(RationalNumber.subtract(new RationalNumber(1l, 2l),
                                       new RationalNumber(1l, 6l)),
               "1/3");

  }

  public void testMultiplication() {

    RationalNumber f = new RationalNumber(2l, 3l);
    f.multiplySelf(new RationalNumber(9l,4l));
    checkValue(f, "3/2");

    checkValue(RationalNumber.multiply(new RationalNumber(1l, 2l),
                                       new RationalNumber(0l)),
               "0");
    checkValue(RationalNumber.multiply(new RationalNumber(4l, 15l),
                                       new RationalNumber(-5l, 2l)),
               "-2/3");
    checkValue(RationalNumber.multiply(new RationalNumber(-4l, 15l),
                                       new RationalNumber(5l, 2l)),
               "-2/3");
    checkValue(RationalNumber.multiply(new RationalNumber(4l, 15l),
                                       new RationalNumber(5l, 2l)),
               "2/3");
    checkValue(RationalNumber.multiply(new RationalNumber(-4l, 15l),
                                       new RationalNumber(-5l, 2l)),
               "2/3");

  }

  public void testDivision() {

    RationalNumber f = new RationalNumber(2l, 3l);
    f.divideSelf(new RationalNumber(4l,9l));
    checkValue(f, "3/2");

    try {
      RationalNumber.divide(new RationalNumber(1l, 2l),
                            new RationalNumber(0l));
      fail("an exception should have been thrown");
    } catch (ArithmeticException e) {
    } catch (Exception e) {
      fail("wrong exception caught");
    }

    checkValue(RationalNumber.divide(new RationalNumber(4l, 15l),
                                     new RationalNumber(-2l, 5l)),
               "-2/3");
    checkValue(RationalNumber.divide(new RationalNumber(-4l, 15l),
                                     new RationalNumber(2l, 5l)),
               "-2/3");
    checkValue(RationalNumber.divide(new RationalNumber(4l, 15l),
                                     new RationalNumber(2l, 5l)),
               "2/3");
    checkValue(RationalNumber.divide(new RationalNumber(-4l, 15l),
                                     new RationalNumber(-2l, 5l)),
               "2/3");

  }

  private void checkValue(RationalNumber f, String reference) {
    assertTrue(f.toString().equals(reference));
  }

  public static Test suite() {
    return new TestSuite(RationalNumberTest.class);
  }

}
