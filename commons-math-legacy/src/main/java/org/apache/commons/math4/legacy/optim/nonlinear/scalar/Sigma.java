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
package org.apache.commons.math4.legacy.optim.nonlinear.scalar;

import org.apache.commons.math4.legacy.optim.OptimizationData;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;

/**
 * Input sigma values define the initial coordinate-wise extent for
 * sampling the solution space around the initial guess.
 */
public class Sigma implements OptimizationData {
    /** Sigma values. */
    private final double[] sigma;

    /**
     * @param s Sigma values.
     * @throws NotStrictlyPositiveException if any of the array
     * entries is negative.
     */
    public Sigma(double[] s) {
        for (int i = 0; i < s.length; i++) {
            if (s[i] <= 0) {
                throw new NotStrictlyPositiveException(s[i]);
            }
        }

        sigma = s.clone();
    }

    /**
     * @return the sigma values.
     */
    public double[] getSigma() {
        return sigma.clone();
    }
}
