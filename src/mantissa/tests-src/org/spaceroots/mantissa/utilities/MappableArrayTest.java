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
  }

  public void testDimensionCheck() {
    assertTrue(mapper.getInternalDataArray().length == 9);
  }

  public void testRealloc() {

    for (int i = 0; i < reusedArray.length; ++i) {
      reusedArray[i] = -1.0;
    }

    for (int i = 0; i < clonedArray.length; ++i) {
      clonedArray[i] = -1.0;
    }

    double[] data = new double [mapper.getInternalDataArray().length];
    for (int i = 0; i < data.length; ++i) {
      data [i] = i * 0.1;
    }

    mapper.updateObjects(data);

    assertTrue(Math.abs(reusedArray[0] - 0.4) < 1.0e-10);
    assertTrue(Math.abs(reusedArray[1] - 0.5) < 1.0e-10);

    assertTrue(Math.abs(clonedArray[0] + 1.0) < 1.0e-10);
    assertTrue(Math.abs(clonedArray[1] + 1.0) < 1.0e-10);
    assertTrue(Math.abs(clonedArray[2] + 1.0) < 1.0e-10);

  }
  
  public void testUpdateObjects() {

    double[] data = new double [mapper.getInternalDataArray().length];
    for (int i = 0; i < data.length; ++i) {
      data [i] = i * 0.1;
    }

    mapper.updateObjects(data);

    assertTrue(Math.abs(array1.getArray()[0] - 0.0) < 1.0e-10);
    assertTrue(Math.abs(array1.getArray()[1] - 0.1) < 1.0e-10);
    assertTrue(Math.abs(array1.getArray()[2] - 0.2) < 1.0e-10);
    assertTrue(Math.abs(array1.getArray()[3] - 0.3) < 1.0e-10);

    assertTrue(Math.abs(array2.getArray()[0] - 0.4) < 1.0e-10);
    assertTrue(Math.abs(array2.getArray()[1] - 0.5) < 1.0e-10);

    assertTrue(Math.abs(array3.getArray()[0] - 0.6) < 1.0e-10);
    assertTrue(Math.abs(array3.getArray()[1] - 0.7) < 1.0e-10);
    assertTrue(Math.abs(array3.getArray()[2] - 0.8) < 1.0e-10);

  }
  
  public void testUpdateArray() {

    array1.getArray()[0] = 00.0;
    array1.getArray()[1] = 10.0;
    array1.getArray()[2] = 20.0;
    array1.getArray()[3] = 30.0;

    array2.getArray()[0] = 40.0;
    array2.getArray()[1] = 50.0;

    array3.getArray()[0] = 60.0;
    array3.getArray()[1] = 70.0;
    array3.getArray()[2] = 80.0;

    mapper.updateArray();

    double[] data = mapper.getInternalDataArray();
    for (int i = 0; i < data.length; ++i) {
      assertTrue(Math.abs(data [i] - i * 10.0) < 1.0e-10);
    }

  }
  
  public static Test suite() {
    return new TestSuite(MappableArrayTest.class);
  }

  public void setUp() {

    reusedArray = new double[2];
    clonedArray = new double[3];

    array1 = new MappableArray(4);
    array2 = new MappableArray(reusedArray, false);
    array3 = new MappableArray(clonedArray, true);

    mapper = new ArrayMapper();
    mapper.manageMappable(array1);
    mapper.manageMappable(array2);
    mapper.manageMappable(array3);

  }

  public void tearDown() {
    reusedArray = null;
    clonedArray = null;

    array1 = null;
    array2 = null;
    array3 = null;

    mapper = null;

  }

  private double[] reusedArray;
  private double[] clonedArray;

  private MappableArray array1;
  private MappableArray array2;
  private MappableArray array3;

  private ArrayMapper mapper;

}
