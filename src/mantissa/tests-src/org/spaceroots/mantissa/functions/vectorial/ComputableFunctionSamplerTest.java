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

package org.spaceroots.mantissa.functions.vectorial;

import org.spaceroots.mantissa.functions.FunctionException;

import junit.framework.*;

public class ComputableFunctionSamplerTest
  extends TestCase {

  public ComputableFunctionSamplerTest(String name) {
    super(name);
  }

  public void testBeginStepNumber()
    throws FunctionException {

    ComputableFunctionSampler sampler =
      new ComputableFunctionSampler(new Function(0.0, 1.0), 0.0, 0.099, 11);

    assertTrue(sampler.size() == 11);
    assertTrue(sampler.getDimension() == 2);
    assertTrue(Math.abs(sampler.samplePointAt(0).getX()     - 0.000) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(0).getY()[0]  + 0.000) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(0).getY()[1]  + 0.000) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(5).getX()     - 0.495) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(5).getY()[0]  + 0.495) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(5).getY()[1]  + 0.990) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(10).getX()    - 0.990) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(10).getY()[0] + 0.990) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(10).getY()[1] + 1.980) < 1.0e-10);

  }

  public void testRangeNumber()
    throws FunctionException {

    double[] range = new double[2];
    range[0] = 0.0;
    range[1] = 1.0;
    ComputableFunctionSampler sampler =
      new ComputableFunctionSampler(new Function (0.0, 1.0), range, 11);

    assertTrue(sampler.size() == 11);
    assertTrue(sampler.getDimension() == 2);
    assertTrue(Math.abs(sampler.samplePointAt(0).getX()     - 0.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(0).getY()[0]  + 0.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(0).getY()[1]  + 0.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(5).getX()     - 0.5) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(5).getY()[0]  + 0.5) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(5).getY()[1]  + 1.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(10).getX()    - 1.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(10).getY()[0] + 1.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(10).getY()[1] + 2.0) < 1.0e-10);

  }

  public void testRangeStepNoAdjust()
    throws FunctionException {

    double[] range = new double[2];
    range[0] = 0.0;
    range[1] = 1.0;
    ComputableFunctionSampler sampler =
      new ComputableFunctionSampler(new Function(0.0, 1.0),
                                     range, 0.083, false);

    assertTrue(sampler.size() == 12);
    assertTrue(sampler.getDimension() == 2);
    assertTrue(Math.abs(sampler.samplePointAt(0).getX()     - 0.000) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(0).getY()[0]  + 0.000) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(0).getY()[1]  + 0.000) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(5).getX()     - 0.415) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(5).getY()[0]  + 0.415) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(5).getY()[1]  + 0.830) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(11).getX()    - 0.913) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(11).getY()[0] + 0.913) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(11).getY()[1] + 1.826) < 1.0e-10);

  }

  public void testRangeStepAdjust()
    throws FunctionException {

    double[] range = new double[2];
    range[0] = 0.0;
    range[1] = 1.0;
    ComputableFunctionSampler sampler =
      new ComputableFunctionSampler(new Function(0.0, 1.0),
                                    range, 0.083, true);

    assertTrue(sampler.size() == 13);
    assertTrue(sampler.getDimension() == 2);
    assertTrue(Math.abs(sampler.samplePointAt(0).getX()     - 0.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(0).getY()[0]  + 0.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(0).getY()[1]  + 0.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(6).getX()     - 0.5) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(6).getY()[0]  + 0.5) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(6).getY()[1]  + 1.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(12).getX()    - 1.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(12).getY()[0] + 1.0) < 1.0e-10);
    assertTrue(Math.abs(sampler.samplePointAt(12).getY()[1] + 2.0) < 1.0e-10);

  }

  public void testOutOfRange()
    throws FunctionException {

    ComputableFunctionSampler sampler =
      new ComputableFunctionSampler(new Function(0.0, 1.0), 0.0, 1.0, 10);

    boolean exceptionOccurred = false;
    try {
      sampler.samplePointAt(-1);
    } catch(ArrayIndexOutOfBoundsException e) {
      exceptionOccurred = true;
    }
    assertTrue(exceptionOccurred);

    exceptionOccurred = false;
    try {
      sampler.samplePointAt(10);
    } catch(ArrayIndexOutOfBoundsException e) {
      exceptionOccurred = true;
    }
    assertTrue(exceptionOccurred);

  }

  public void testUnderlyingException() {

    ComputableFunctionSampler sampler =
      new ComputableFunctionSampler(new ComputableFunction() {

          public int getDimension() {
            return 2;
          }

          public double[] valueAt(double x)
            throws FunctionException {
            if (x < 0.5) {
              double[] res = new double[2];
              res[0] = -x;
              res[1] = -2.0 * x;
              return res;
            }
            throw new FunctionException("upper half range exception");
           }

        },
                                    0.0, 0.1, 11);

    boolean exceptionOccurred = false;
    try {
      sampler.samplePointAt(2);
    } catch(FunctionException e) {
      exceptionOccurred = true;
    }
    assertTrue(! exceptionOccurred);

    exceptionOccurred = false;
    try {
      sampler.samplePointAt(8);
    } catch(FunctionException e) {
      exceptionOccurred = true;
    }
    assertTrue(exceptionOccurred);

  }

  public static Test suite() {
    return new TestSuite(ComputableFunctionSamplerTest.class);
  }

  private class Function
    implements ComputableFunction {
    private double   min;
    private double   max;
    private double[] values;

    public int getDimension() {
      return 2;
    }

    public Function(double min, double max) {
      this.min = min;
      this.max = max;
      values   = new double[2];
    }

    public double[] valueAt(double x)
      throws FunctionException {

      if (x < min || x > max) {
        throw new FunctionException("outside of range");
      }

      values[0] = -x;
      values[1] = -2.0 * x;
      return values;

    }
  }

}
