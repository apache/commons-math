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

package org.spaceroots.mantissa.functions.scalar;

import org.spaceroots.mantissa.functions.FunctionException;
import org.spaceroots.mantissa.functions.ExhaustedSampleException;

import junit.framework.*;

public class BasicSampledFunctionIteratorTest
  extends TestCase {

  public BasicSampledFunctionIteratorTest(String name) {
    super(name);
  }

  public void testIteration()
    throws ExhaustedSampleException, FunctionException {

    BasicSampledFunctionIterator iter =
      new BasicSampledFunctionIterator(new Function(0.0, 0.1, 10));

    for (int i = 0; i < 10; ++i) {
      assertTrue(iter.hasNext());
      ScalarValuedPair pair = iter.nextSamplePoint();
      assertTrue(Math.abs(pair.getX() - 0.1 * i) < 1.0e-10);
      assertTrue(Math.abs(pair.getY() + 0.1 * i) < 1.0e-10);
    }

  }

  public void testExhausted()
    throws ExhaustedSampleException, FunctionException {

    BasicSampledFunctionIterator iter =
      new BasicSampledFunctionIterator(new Function(0.0, 0.1, 10));

    for (int i = 0; i < 10; ++i) {
      assertTrue(iter.hasNext());
      iter.nextSamplePoint();
    }

    assertTrue(! iter.hasNext());

    boolean exceptionOccurred = false;
    try {
      iter.nextSamplePoint();
    } catch(ExhaustedSampleException e) {
      exceptionOccurred = true;
    }
    assertTrue(exceptionOccurred);

  }

  public void testUnderlyingException()
    throws ExhaustedSampleException, FunctionException {

    BasicSampledFunctionIterator iter =
      new BasicSampledFunctionIterator(new ExceptionGeneratingFunction());

    boolean exceptionOccurred = false;
    try {
      iter.nextSamplePoint();
    } catch(FunctionException e) {
      exceptionOccurred = true;
    }
    assertTrue(! exceptionOccurred);

    exceptionOccurred = false;
    try {
      iter.nextSamplePoint();
    } catch (FunctionException e) {
      exceptionOccurred = true;
    }
    assertTrue(exceptionOccurred);

  }

  public static Test suite() {
    return new TestSuite(BasicSampledFunctionIteratorTest.class);
  }

  private static class Function
    implements SampledFunction {

    private static final long serialVersionUID = -5071329620086891960L;
    private double begin;
    private double step;
    private int    n;

    public Function(double begin, double step, int n) {
      this.begin = begin;
      this.step  = step;
      this.n     = n;
    }

    public int size() {
      return n;
    }

    public ScalarValuedPair samplePointAt(int i)
      throws FunctionException {

      if (i < 0 || i >= n) {
        throw new FunctionException("outside of range");
      }

      double x = begin + i * step;
      return new ScalarValuedPair(x, -x);

    }
  }

  private static class ExceptionGeneratingFunction
    implements SampledFunction {

    private static final long serialVersionUID = 1417147976215668305L;
    private boolean fireException = false;

    public int size() {
      return 2;
    }

    public ScalarValuedPair samplePointAt(int i)
      throws FunctionException {
      if (fireException) {
        throw new FunctionException("boom");
      }
      fireException = true;
      return new ScalarValuedPair(0.0, 0.0);
    }

  }
}
