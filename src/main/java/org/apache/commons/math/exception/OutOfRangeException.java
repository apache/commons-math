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
package org.apache.commons.math.exception;

import org.apache.commons.math.util.LocalizedFormats;

/**
 * Exception to be thrown when some argument is out of range.
 *
 * @since 2.2
 * @version $Revision$ $Date$
 */
public class OutOfRangeException extends MathIllegalArgumentException {
    /** Lower bound. */
    private final Number lo;
    /** Higher bound. */
    private final Number hi;
    /** Requested. */
    private final Number requested;

    /**
     * Construct an exception from the mismatched dimensions.
     *
     * @param requested Requested value.
     * @param lo Lower bound.
     * @param hi Higher bound.
     */
    public OutOfRangeException(Number requested,
                               Number lo,
                               Number hi) {
        super(LocalizedFormats.OUT_OF_RANGE_SIMPLE, requested, lo, hi);

        this.requested = requested;
        this.lo = lo;
        this.hi = hi;
    }

    /**
     * @return the requested value.
     */
    public Number getRequested() {
        return requested;
    }
    /**
     * @return the lower bound.
     */
    public Number getLo() {
        return lo;
    }
    /**
     * @return the higher bound.
     */
    public Number getHi() {
        return hi;
    }
}
