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
package org.apache.commons.math4.legacy.stat.descriptive.rank;

import org.apache.commons.math4.legacy.core.MathArrays;

/**
 * A mid point strategy based on the average of begin and end indices.
 * @since 3.4
 */
public class CentralPivotingStrategy implements PivotingStrategy {
    /**
     * {@inheritDoc}
     * This in particular picks a average of begin and end indices
     * @return The index corresponding to a simple average of
     * the first and the last element indices of the array slice
     * @throws org.apache.commons.math4.legacy.exception.MathIllegalArgumentException MathIllegalArgumentException when indices exceeds range
     */
    @Override
    public int pivotIndex(final double[] work, final int begin, final int end) {
        MathArrays.verifyValues(work, begin, end - begin);
        return begin + (end - begin)/2;
    }
}
