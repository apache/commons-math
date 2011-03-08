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
package org.apache.commons.math.linear;

import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Exception to be thrown when a symmetric matrix is expected.
 *
 * @since 3.0
 * @version $Revision$ $Date$
 */
public class NonPositiveDefiniteMatrixException extends MathIllegalArgumentException {
    /** Serializable version Id. */
    private static final long serialVersionUID = 1641613838113738061L;
    /** Index (diagonal element). */
    private final int index;
    /** Threshold. */
    private final double threshold;

    /**
     * Construct an exception.
     *
     * @param index Row (and column) index.
     * @param threshold Absolute positivity threshold.
     */
    public NonPositiveDefiniteMatrixException(int index,
                                              double threshold) {
        super(LocalizedFormats.NON_POSITIVE_DEFINITE_MATRIX, index, threshold);
        this.index = index;
        this.threshold = threshold;
    }

    /**
     * @return the row index.
     */
    public int getRow() {
        return index;
    }
    /**
     * @return the column index.
     */
    public int getColumn() {
        return index;
    }
    /**
     * @return the absolute positivity threshold.
     */
    public double getThreshold() {
        return threshold;
    }
}
