/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math4.legacy.analysis;

import org.apache.commons.math4.legacy.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.legacy.analysis.differentiation.UnivariateDifferentiableFunction;

/**
 * Auxiliary class for testing solvers.
 *
 */
public class QuinticFunction implements UnivariateDifferentiableFunction {

    /* Evaluate quintic.
     * @see org.apache.commons.math4.legacy.UnivariateFunction#value(double)
     */
    @Override
    public double value(double x) {
        return (x-1)*(x-0.5)*x*(x+0.5)*(x+1);
    }

    @Override
    public DerivativeStructure value(DerivativeStructure t) {
        return t.subtract(1).multiply(t.subtract(0.5)).multiply(t).multiply(t.add(0.5)).multiply(t.add(1));
    }
}
