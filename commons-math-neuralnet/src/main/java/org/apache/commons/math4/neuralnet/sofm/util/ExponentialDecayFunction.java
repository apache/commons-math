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

package org.apache.commons.math4.neuralnet.sofm.util;

import java.util.function.LongToDoubleFunction;

import org.apache.commons.math4.neuralnet.internal.NeuralNetException;

/**
 * Exponential decay function: <code>a e<sup>-x / b</sup></code>,
 * where {@code x} is the (integer) independent variable.
 * <br>
 * Class is immutable.
 *
 * @since 3.3
 */
public class ExponentialDecayFunction implements LongToDoubleFunction {
    /** Factor {@code a}. */
    private final double a;
    /** Factor {@code 1 / b}. */
    private final double oneOverB;

    /**
     * Creates an instance. It will be such that
     * <ul>
     *  <li>{@code a = initValue}</li>
     *  <li>{@code b = -numCall / ln(valueAtNumCall / initValue)}</li>
     * </ul>
     *
     * @param initValue Initial value, i.e. {@link #applyAsDouble(long) applyAsDouble(0)}.
     * @param valueAtNumCall Value of the function at {@code numCall}.
     * @param numCall Argument for which the function returns
     * {@code valueAtNumCall}.
     * @throws IllegalArgumentException if {@code initValue <= 0},
     * {@code valueAtNumCall <= 0}, {@code valueAtNumCall >= initValue} or
     * {@code numCall <= 0}.
     */
    public ExponentialDecayFunction(double initValue,
                                    double valueAtNumCall,
                                    long numCall) {
        if (initValue <= 0) {
            throw new NeuralNetException(NeuralNetException.NOT_STRICTLY_POSITIVE, initValue);
        }
        if (valueAtNumCall <= 0) {
            throw new NeuralNetException(NeuralNetException.NOT_STRICTLY_POSITIVE, valueAtNumCall);
        }
        if (valueAtNumCall >= initValue) {
            throw new NeuralNetException(NeuralNetException.TOO_LARGE, valueAtNumCall, initValue);
        }
        if (numCall <= 0) {
            throw new NeuralNetException(NeuralNetException.NOT_STRICTLY_POSITIVE, numCall);
        }

        a = initValue;
        oneOverB = -Math.log(valueAtNumCall / initValue) / numCall;
    }

    /**
     * Computes <code>a e<sup>-numCall / b</sup></code>.
     *
     * @param numCall Current step of the training task.
     * @return the value of the function at {@code numCall}.
     */
    @Override
    public double applyAsDouble(long numCall) {
        return a * Math.exp(-numCall * oneOverB);
    }
}
