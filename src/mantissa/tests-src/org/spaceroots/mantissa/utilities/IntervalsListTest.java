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

import java.util.Iterator;

import junit.framework.*;

public class IntervalsListTest
  extends TestCase {

  public IntervalsListTest(String name) {
    super(name);
  }

  public void testAddBetween() {
    IntervalsList il =
      new IntervalsList (new Interval(10, 20), new Interval(50, 60));
    il.addToSelf(new Interval(30, 40));
    checkEquals(new Interval[] {
                  new Interval(10, 20),
                  new Interval(30, 40),
                  new Interval(50, 60)
                }, il);
  }

  public void testAddReducingLastHole() {
    IntervalsList il =
      new IntervalsList (new Interval(10, 20), new Interval(50, 60));
    il.addToSelf(new Interval(30, 55));
    checkEquals(new Interval[] {
                  new Interval(10, 20),
                  new Interval(30, 60)
                }, il);
  }

  public void test1() {

    IntervalsList list1 = new IntervalsList(-2.0, -1.0);
    IntervalsList list2 = new IntervalsList(new Interval(-0.9, -0.8));
    check(list1, list2, 2.5,
          true, false, 1, true, false, 1, false,
          new Interval[] { new Interval(-2.0, -1.0),
                           new Interval(-0.9, -0.8) },
          new Interval[0]);

    list2.addToSelf(new Interval(1.0, 3.0));
    check(list1, list2, 2.5,
          true, false, 1, false, false, 2, false,
          new Interval[] { new Interval(-2.0, -1.0),
                           new Interval(-0.9, -0.8),
                           new Interval( 1.0,  3.0) },
          new Interval[0]);

    list1.addToSelf(new Interval(-1.2, 0.0));
    check(list1, list2, -1.1,
          true, false, 1, false, false, 2, true,
          new Interval[] { new Interval(-2.0,  0.0),
                           new Interval( 1.0,  3.0) },
          new Interval[] { new Interval(-0.9, -0.8) });

    IntervalsList list = new IntervalsList(new Interval(-10.0, -8.0));
    list.addToSelf(new Interval(-6.0, -4.0));
    list.addToSelf(new Interval(-0.85, 1.2));
    list1.addToSelf(list);
    check(list1, list2, 0,
          false, false, 3, false, false, 2, true,
          new Interval[] { new Interval(-10.0, -8.0),
                           new Interval( -6.0, -4.0),
                           new Interval( -2.0,  3.0) },
          new Interval[] { new Interval( -0.9, -0.8),
                           new Interval(  1.0,  1.2) });

  }

  private void check(IntervalsList l1, IntervalsList l2, double x,
                     boolean b1, boolean b2, int i1,
                     boolean b3, boolean b4, int i2,
                     boolean b5, Interval[] add, Interval[] inter) {
    assertTrue(l1.isConnex()     ^ (!b1));
    assertTrue(l1.isEmpty()      ^ (!b2));
    assertEquals(i1, l1.getIntervals().size());
    assertTrue(l2.isConnex()     ^ (!b3));
    assertTrue(l2.isEmpty()      ^ (!b4));
    assertEquals(i2, l2.getIntervals().size());
    assertTrue(l1.contains(x)    ^ (!b5));
    checkEquals(add,   IntervalsList.add(l1, l2));
    checkEquals(inter, IntervalsList.intersection(l1, l2));
  }

  private void checkEquals(Interval[] sa, IntervalsList sb) {
    assertEquals(sa.length, sb.getIntervals().size());
    Iterator iterB = sb.getIntervals().iterator();
    for (int i = 0; i < sa.length; ++i) {
      Interval ib = (Interval) iterB.next();
      assertEquals(sa[i].getInf(), ib.getInf(), 1.0e-10);
      assertEquals(sa[i].getSup(), ib.getSup(), 1.0e-10);
    }
  }

  public static Test suite() {
    return new TestSuite(IntervalsListTest.class);
  }

}
