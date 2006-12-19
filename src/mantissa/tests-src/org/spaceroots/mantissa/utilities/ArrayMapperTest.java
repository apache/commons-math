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

public class ArrayMapperTest
  extends TestCase {

  public ArrayMapperTest(String name) {
    super(name);
    mapper = null;
    b1 = null;
    b2 = null;
    b3 = null;
  }

  public void testDimensionCheck() {
    int size = b1.getStateDimension();
    size += b2.getStateDimension();
    size += b3.getStateDimension();
    assertTrue(mapper.getDataArray().length == size);
  }

  public void testUpdateObjects() {

    double[] data = new double [7];
    for (int i = 0; i < 7; ++i) {
      data [i] = i * 0.1;
    }

    mapper.updateObjects(data);

    assertTrue(Math.abs(b1.getElement(0) - 0.0) < 1.0e-10);

    assertTrue(Math.abs(b2.getElement(0) - 0.4) < 1.0e-10);
    assertTrue(Math.abs(b2.getElement(1) - 0.3) < 1.0e-10);
    assertTrue(Math.abs(b2.getElement(2) - 0.2) < 1.0e-10);
    assertTrue(Math.abs(b2.getElement(3) - 0.1) < 1.0e-10);

    assertTrue(Math.abs(b3.getElement(0) - 0.6) < 1.0e-10);
    assertTrue(Math.abs(b3.getElement(1) - 0.5) < 1.0e-10);

  }
  
  public void testUpdateArray() {

    b1.setElement(0,  0.0);

    b2.setElement(0, 40.0);
    b2.setElement(1, 30.0);
    b2.setElement(2, 20.0);
    b2.setElement(3, 10.0);

    b3.setElement(0, 60.0);
    b3.setElement(1, 50.0);

    mapper.updateArray();

    double[] data = mapper.getDataArray();
    for (int i = 0; i < 7; ++i) {
      assertTrue(Math.abs(data [i] - i * 10.0) < 1.0e-10);
    }

  }
  
  public void setUp() {

    b1 = new DomainObject(1);
    b2 = new DomainObject(4);
    b3 = new DomainObject(2);

    mapper = new ArrayMapper();
    mapper.manageMappable(b1);
    mapper.manageMappable(b2);
    mapper.manageMappable(b3);

  }

  public void tearOff() {

    b1 = null;
    b2 = null;
    b3 = null;

    mapper = null;

  }

  public static Test suite() {
    return new TestSuite(ArrayMapperTest.class);
  }

  private static class DomainObject implements ArraySliceMappable {

    private double[] data;

    public DomainObject(int size) {
      data = new double [size];
    }

    public int getStateDimension() {
      return data.length;
    }

    public void mapStateFromArray(int start, double[] array) {
      for (int i = 0; i < data.length; ++i) {
        data [data.length - 1 - i] = array [start + i];
      }
    }
    
    public void mapStateToArray(int start, double[] array) {
      for (int i = 0; i < data.length; ++i) {
        array [start + i] = data [data.length - 1 - i];
      }
    }

    public double getElement(int i) {
      return data [i];
    }

    public void setElement(int i, double value) {
      data [i] = value;
    }

  }

  private DomainObject b1;
  private DomainObject b2;
  private DomainObject b3;

  private ArrayMapper mapper;

}
