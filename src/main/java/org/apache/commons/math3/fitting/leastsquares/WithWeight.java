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
package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Interface for "fluent-API" that advertizes a capability of the optimizer.
 *
 * @param <T> Concrete optimizer implementation.
 *
 * @version $Id$
 * @since 3.3
 */
public interface WithWeight<T> {
    /**
     * Creates a new instance with the specified parameter.
     *
     * @param weight Weight matrix of the observations.
     * @return a new optimizer instance with all fields identical to this
     * instance except for the given argument.
     */
    T withWeight(RealMatrix weight);
}
