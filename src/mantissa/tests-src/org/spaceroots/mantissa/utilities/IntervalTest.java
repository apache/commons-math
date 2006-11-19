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

package org.spaceroots.mantissa.utilities;

import junit.framework.*;

public class IntervalTest
  extends TestCase {

  public IntervalTest(String name) {
    super(name);
  }

  public void test1() {
    check(new Interval(-10.0, 10.0), new Interval(11.0, 12.0), 2.5,
          true, false, false,
          new Interval(-10.0, 12.0), new Interval(11.0, 11.0));
  }

  public void test2() {
    check(new Interval(-10.0, 10.0), new Interval(9.0, 12.0), 50.0,
          false, false, true,
          new Interval(-10.0, 12.0), new Interval(9.0, 10.0));
  }

  public void test3() {
    check(new Interval(-10.0, 10.0), new Interval(-12.0, -11.0), 0.0,
          true, false, false,
          new Interval(-12.0, 10.0), new Interval(-10.0, -10.0));
  }

  public void test4() {
    check(new Interval(-10.0, 10.0), new Interval(-4.0, 5.0), 0.0,
          true, true, true,
          new Interval(-10.0, 10.0), new Interval(-4.0, 5.0));
  }

  public void test5() {
    check(new Interval(-10.0, 10.0), new Interval(-10.0, 10.0), 0.0,
          true, true, true,
          new Interval(-10.0, 10.0), new Interval(-10.0, 10.0));
  }

  private void check(Interval i1, Interval i2, double x,
                     boolean b1, boolean b2, boolean b3,
                     Interval add, Interval inter) {

    assertTrue(i1.contains(x)    ^ (!b1));
    assertTrue(i1.contains(i2)   ^ (!b2));
    assertTrue(i1.intersects(i2) ^ (!b3));

    assertEquals(add.getInf(), Interval.add(i1, i2).getInf(), 1.0e-10);
    assertEquals(add.getSup(), Interval.add(i1, i2).getSup(), 1.0e-10);
    assertEquals(inter.getInf(), Interval.intersection(i1, i2).getInf(), 1.0e-10);
    assertEquals(inter.getSup(), Interval.intersection(i1, i2).getSup(), 1.0e-10);

    Interval ia = new Interval(i1);
    ia.addToSelf(i2);
    assertEquals(add.getInf(), ia.getInf(), 1.0e-10);
    assertEquals(add.getSup(), ia.getSup(), 1.0e-10);

    Interval ib = new Interval(i1);
    ib.intersectSelf(i2);
    assertEquals(inter.getInf(), ib.getInf(), 1.0e-10);
    assertEquals(inter.getSup(), ib.getSup(), 1.0e-10);

  }

  public static Test suite() {
    return new TestSuite(IntervalTest.class);
  }

}
