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

package org.apache.commons.math4.legacy.linear;

import org.apache.commons.math4.legacy.exception.DimensionMismatchException;

/**
 * Interface defining very basic matrix operations.
 *
 * @since 2.0
 */
public interface AnyMatrix {
    /**
     * Indicates whether this is a square matrix.
     *
     * @return {@code true} if the number of rows is the same as the number of columns.
     */
    default boolean isSquare() {
        return getRowDimension() == getColumnDimension();
    }

    /**
     * Gets the number of rows.
     *
     * @return the number of rows.
     */
    int getRowDimension();

    /**
     * Gets the number of columns.
     *
     * @return the number of columns.
     */
    int getColumnDimension();

    /**
     * Checks that this matrix and the {@code other} matrix can be added.
     *
     * @param other Matrix to be added.
     * @return {@code false} if the dimensions do not match.
     */
    default boolean canAdd(AnyMatrix other) {
        return getRowDimension() == other.getRowDimension() &&
            getColumnDimension() == other.getColumnDimension();
    }

    /**
     * Checks that this matrix and the {@code other} matrix can be added.
     *
     * @param other Matrix to check.
     * @throws IllegalArgumentException if the dimensions do not match.
     */
    default void checkAdd(AnyMatrix other) {
        if (!canAdd(other)) {
            throw new MatrixDimensionMismatchException(getRowDimension(), getColumnDimension(),
                                                       other.getRowDimension(), other.getColumnDimension());
        }
    }

    /**
     * Checks that this matrix can be multiplied by the {@code other} matrix.
     *
     * @param other Matrix to be added.
     * @return {@code false} if the dimensions do not match.
     */
    default boolean canMultiply(AnyMatrix other) {
        return getColumnDimension() == other.getRowDimension();
    }

    /**
     * Checks that this matrix can be multiplied by the {@code other} matrix.
     *
     * @param other Matrix to check.
     * @throws IllegalArgumentException if the dimensions do not match.
     */
    default void checkMultiply(AnyMatrix other) {
        if (!canMultiply(other)) {
            throw new DimensionMismatchException(getColumnDimension(),
                                                 other.getRowDimension());
        }
    }
}
