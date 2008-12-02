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

/** This class provides sampled values of the function t -> [f(t)^2, f'(t)^2].

 * This class is a helper class used to compute a first guess of the
 * harmonic coefficients of a function <code>f (t) = a cos (omega t +
 * phi)</code>.

 * @see FFPIterator
 * @see HarmonicCoefficientsGuesser

 * @version $Id$
 * @author L. Maisonobe

 */

class F2FP2Iterator
  implements SampledFunctionIterator, Serializable {

  public F2FP2Iterator(AbstractCurveFitter.FitMeasurement[] measurements) {
    ffpIterator = new FFPIterator(measurements);
  }

  public int getDimension() {
    return 2;
  }

  public boolean hasNext() {
    return ffpIterator.hasNext();
  }

  public VectorialValuedPair nextSamplePoint()
    throws ExhaustedSampleException, FunctionException {

    // get the raw values from the underlying FFPIterator
    VectorialValuedPair point = ffpIterator.nextSamplePoint();
    double[] y = point.y;

    // square the values
    return new VectorialValuedPair(point.x,
                                   new double[] {
                                     y[0] * y[0], y[1] * y[1]             
                                   });

  }

  private FFPIterator ffpIterator;

  private static final long serialVersionUID = -8113110433795298072L;

}
