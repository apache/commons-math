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

package org.spaceroots.mantissa.fitting;

import java.io.Serializable;

import org.spaceroots.mantissa.functions.FunctionException;
import org.spaceroots.mantissa.functions.ExhaustedSampleException;
import org.spaceroots.mantissa.functions.vectorial.SampledFunctionIterator;
import org.spaceroots.mantissa.functions.vectorial.VectorialValuedPair;

/** This class provides sampled values of the function t -> [f(t), f'(t)].

 * This class is a helper class used to compute a first guess of the
 * harmonic coefficients of a function <code>f (t) = a cos (omega t +
 * phi)</code>.

 * @see F2FP2Iterator
 * @see HarmonicCoefficientsGuesser

 * @version $Id$
 * @author L. Maisonobe

 */

class FFPIterator
  implements SampledFunctionIterator, Serializable {

  public FFPIterator(AbstractCurveFitter.FitMeasurement[] measurements) {
    this.measurements = measurements;

    // initialize the points of the raw sample
    current   = measurements[0];
    currentY  = current.getMeasuredValue();
    next      = measurements[1];
    nextY     = next.getMeasuredValue();
    nextIndex = 2;

  }

  public int getDimension() {
    return 2;
  }

  public boolean hasNext() {
    return nextIndex < measurements.length;
  }

  public VectorialValuedPair nextSamplePoint()
    throws ExhaustedSampleException, FunctionException {
    if (nextIndex >= measurements.length) {
      throw new ExhaustedSampleException(measurements.length);
    }

    // shift the points
    previous  = current;
    previousY = currentY;
    current   = next;
    currentY  = nextY;
    next      = measurements[nextIndex++];
    nextY     = next.getMeasuredValue();

    // return the two dimensions vector [f(x), f'(x)]
    double[] table = new double[2];
    table[0] = currentY;
    table[1] = (nextY - previousY) / (next.x - previous.x);
    return new VectorialValuedPair(current.x, table);

  }

  private AbstractCurveFitter.FitMeasurement[] measurements;
  private int nextIndex;

  private AbstractCurveFitter.FitMeasurement previous;
  private double previousY;

  private AbstractCurveFitter.FitMeasurement current;
  private double nextY;

  private AbstractCurveFitter.FitMeasurement next;
  private double currentY;

  private static final long serialVersionUID = -3187229691615380125L;

}
