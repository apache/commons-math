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

public class MappableArrayTest
  extends TestCase {

  public MappableArrayTest(String name) {
    super(name);
    array1 = null;
    array2 = null;
    array3 = null;
    mapper = null;
  }

  public void testDimensionCheck() {
    assertTrue(mapper.getDataArray().length == 9);
  }

  public void testUpdateObjects() {

    double[] data = new double [mapper.getDataArray().length];
    for (int i = 0; i < data.length; ++i) {
      data [i] = i * 0.1;
    }

    mapper.updateObjects(data);

    double[] a1 = array1.getArray();
    assertTrue(Math.abs(a1[0] - 0.0) < 1.0e-10);
    assertTrue(Math.abs(a1[1] - 0.1) < 1.0e-10);
    assertTrue(Math.abs(a1[2] - 0.2) < 1.0e-10);
    assertTrue(Math.abs(a1[3] - 0.3) < 1.0e-10);

    double[] a2 = array2.getArray();
    assertTrue(Math.abs(a2[0] - 0.4) < 1.0e-10);
    assertTrue(Math.abs(a2[1] - 0.5) < 1.0e-10);

    double[] a3 = array3.getArray();
    assertTrue(Math.abs(a3[0] - 0.6) < 1.0e-10);
    assertTrue(Math.abs(a3[1] - 0.7) < 1.0e-10);
    assertTrue(Math.abs(a3[2] - 0.8) < 1.0e-10);

  }
  
  public static Test suite() {
    return new TestSuite(MappableArrayTest.class);
  }

  public void setUp() {

    array1 = new MappableArray(4);
    array2 = new MappableArray(new double[2]);
    array3 = new MappableArray(new double[3]);

    mapper = new ArrayMapper();
    mapper.manageMappable(array1);
    mapper.manageMappable(array2);
    mapper.manageMappable(array3);

  }

  public void tearDown() {
    array1 = null;
    array2 = null;
    array3 = null;
    mapper = null;
  }

  private MappableArray array1;
  private MappableArray array2;
  private MappableArray array3;
  private ArrayMapper   mapper;

}
