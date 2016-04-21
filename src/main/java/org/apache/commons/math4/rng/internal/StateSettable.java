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
package org.apache.commons.math4.rng.internal;

import org.apache.commons.math4.rng.RandomSource;

/**
 * Indicates that the state of the instance can be saved and restored.
 *
 * @since 4.0
 */
public interface StateSettable {
    /**
     * Sets the instance's state.
     *
     * @param state State. The given argument must have been retrieved
     * by a call to {@link #getState()}.
     *
     * @throws org.apache.commons.math4.exception.MathUnsupportedOperationException
     * if not implemented.
     */
    void setState(RandomSource.State state);

    /**
     * Gets the instance's state.
     *
     * @return the current state. The given argument can then be passed
     * to {@link #setState(RandomSource.State)} in order to recover the
     * current state.
     *
     * @throws org.apache.commons.math4.exception.MathUnsupportedOperationException
     * if not implemented.
     */
     RandomSource.State getState();
}
