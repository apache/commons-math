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

package org.apache.commons.math3.optimization;

import org.apache.commons.math3.util.Cloner;

/**
 * Target of the optimization procedure.
 * They are the values which the objective vector function must reproduce
 * When the parameters of the model have been optimized.
 * <br/>
 * Immutable class.
 *
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.1
 */
@Deprecated
public class Target implements OptimizationData {
    /** Target values (of the objective vector function). */
    private final double[] target;

    /**
     * @param observations Target values.
     */
    public Target(double[] observations) {
		target = Cloner.clone(observations);
    }

    /**
     * Gets the initial guess.
     *
     * @return the initial guess.
     */
    public double[] getTarget() {
		return Cloner.clone(target);
    }
}
