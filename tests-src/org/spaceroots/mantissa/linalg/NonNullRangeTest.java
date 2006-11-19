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

package org.spaceroots.mantissa.linalg;

import junit.framework.*;

public class NonNullRangeTest
  extends TestCase {

  public NonNullRangeTest(String name) {
    super(name);
  }

  public void testPublicAttributes() {
    NonNullRange r = new NonNullRange(2, 7);
    assertTrue(r.begin == 2);
    assertTrue(r.end   == 7);
  }

  public void testCopy() {
    NonNullRange r1 = new NonNullRange(2, 7);
    NonNullRange r2 = new NonNullRange(r1);
    assertTrue(r2.begin == r1.begin);
    assertTrue(r1.end   == r1.end);
  }

  public void testIntersection() {
    NonNullRange r1 = new NonNullRange(-4, 8);
    NonNullRange r2 = new NonNullRange(3, 12);
    NonNullRange r3 = NonNullRange.intersection(r1, r2);
    assertTrue(r3.begin == 3);
    assertTrue(r3.end   == 8);
  }

  public void testReunion() {
    NonNullRange r1 = new NonNullRange(-4, 8);
    NonNullRange r2 = new NonNullRange(3, 12);
    NonNullRange r3 = NonNullRange.reunion(r1, r2);
    assertTrue(r3.begin == -4);
    assertTrue(r3.end   == 12);
  }

  public static Test suite() {
    return new TestSuite(NonNullRangeTest.class);
  }

}
