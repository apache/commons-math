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
 * Exception to be thrown when two dimensions differ.
 *
 * @since 2.2
 * @version $Revision$ $Date$
 */
public class DimensionMismatchException extends MathIllegalArgumentException {
    /** First dimension. */
    private final int dimension1;

    /** Second dimension. */
    private final int dimension2;

    /**
     * Construct an exception from the mismatched dimensions.
     *
     * @param dimension1 First dimension.
     * @param dimension2 Second dimension.
     */
    public DimensionMismatchException(int dimension1,
                                      int dimension2) {
        super(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, dimension1, dimension2);

        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
    }

    /**
     * @return the first dimension.
     */
    public int getDimension1() {
        return dimension1;
    }
    /**
     * @return the second dimension.
     */
    public int getDimension2() {
        return dimension2;
    }
}
