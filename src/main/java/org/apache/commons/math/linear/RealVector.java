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

import java.util.Iterator;

import org.apache.commons.math.analysis.UnivariateRealFunction;


/**
 * Interface defining a real-valued vector with basic algebraic operations.
 * <p>
 * vector element indexing is 0-based -- e.g., {@code getEntry(0)}
 * returns the first element of the vector.
 * </p>
 * <p>
 * The {@code code map} and {@code mapToSelf} methods operate
 * on vectors element-wise, i.e. they perform the same operation (adding a scalar,
 * applying a function ...) on each element in turn. The {@code map}
 * version creates a new vector to hold the result and do not change the instance.
 * The {@code mapToSelf} version use the instance itself to store the
 * results, so the instance is changed by this method. In both cases, the result
 * vector is returned by the methods, this allows to use the <i>fluent API</i>
 * style, like this:
 * </p>
 * <pre>
 *   RealVector result = v.mapAddToSelf(3.4).mapToSelf(new Tan()).mapToSelf(new Power(2.3));
 * </pre>
 *
 * @version $Revision$ $Date$
 * @since 2.0
 */
public interface RealVector {
    /**
     * Acts as if it is implemented as:
     * <pre>
     *  Entry e = null;
     *  for(Iterator<Entry> it = iterator(); it.hasNext(); e = it.next()) {
     *      e.setValue(function.value(e.getValue()));
     *  }
     * </pre>
     *
     * @param function Function to apply to each entry.
     * @return this vector.
     * @throws org.apache.commons.math.exception.MathUserException
     * if the function throws it.
     */
    RealVector mapToSelf(UnivariateRealFunction function);

    /**
     * Acts as if implemented as:
     * <pre>
     *  return copy().map(function);
     * </pre>
     *
     * @param function Function to apply to each entry.
     * @return a new vector.
     * @throws org.apache.commons.math.exception.MathUserException
     * if the function throws it.
     */
    RealVector map(UnivariateRealFunction function);

    /** Class representing a modifiable entry in the vector. */
    public abstract class Entry {
        /** Index of the entry. */
        private int index;

        /**
         * Get the value of the entry.
         *
         * @return the value of the entry.
         */
        public abstract double getValue();
        /**
         * Set the value of the entry.
         *
         * @param value New value for the entry.
         */
        public abstract void setValue(double value);
        /**
         * Get the index of the entry.
         *
         * @return the index of the entry.
         */
        public int getIndex() {
            return index;
        }
        /**
         * Set the index of the entry.
         *
         * @param index New index for the entry.
         */
        public void setIndex(int index) {
            this.index = index;
        }
    }

    /**
     * Generic dense iterator.
     * It iterates in increasing order of the vector index.
     *
     * @return a dense iterator
     */
    Iterator<Entry> iterator();

    /**
     * Specialized implementations may choose to not iterate over all
     * dimensions, either because those values are unset, or are equal
     * to defaultValue(), or are small enough to be ignored for the
     * purposes of iteration.
     * No guarantees are made about order of iteration.
     * In dense implementations, this method will often delegate to
     * {@link #iterator()}.
     *
     * @return a sparse iterator
     */
    Iterator<Entry> sparseIterator();

    /**
     * Returns a (deep) copy of this vector.
     *
     * @return a vector copy.
     */
    RealVector copy();

    /**
     * Compute the sum of this vector and {@code v}.
     *
     * @param v Vector to be added.
     * @return {@code this} + {@code v}.
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealVector add(RealVector v);

    /**
     * Compute the sum of this vector and {@code v}.
     *
     * @param v Vector to be added.
     * @return {@code this} + {@code v}.
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealVector add(double[] v);


    /**
     * Subtract {@code v} from this vector.
     *
     * @param v Vector to be subtracted.
     * @return {@code this} - {@code v}.
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealVector subtract(RealVector v);

    /**
     * Subtract {@code v} from this vector.
     *
     * @param v Vector to be subtracted.
     * @return {@code this} - {@code v}.
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealVector subtract(double[] v);

    /**
     * Add a value to each entry.
     *
     * @param d Value to be added to each entry.
     * @return {@code this} + {@code d}.
     */
    RealVector mapAdd(double d);

    /**
     * Add a value to each entry.
     * The instance is changed in-place.
     *
     * @param d Value to be added to each entry.
     * @return {@code this}.
     */
    RealVector mapAddToSelf(double d);

    /**
     * Subtract a value from each entry.
     *
     * @param d Value to be subtracted.
     * @return {@code this} - {@code d}.
     */
    RealVector mapSubtract(double d);

    /**
     * Subtract a value from each entry.
     * The instance is changed in-place.
     *
     * @param d Value to be subtracted.
     * @return {@code this}.
     */
    RealVector mapSubtractToSelf(double d);

    /**
     * Multiply each entry.
     *
     * @param d Multiplication factor.
     * @return {@code this} * {@code d}.
     */
    RealVector mapMultiply(double d);

    /**
     * Multiply each entry.
     * The instance is changed in-place.
     *
     * @param d Multiplication factor.
     * @return {@code this}.
     */
    RealVector mapMultiplyToSelf(double d);

    /**
     * Divide each entry.
     *
     * @param d Value to divide by.
     * @return {@code this} / {@code d}.
     */
    RealVector mapDivide(double d);

    /**
     * Divide each entry.
     * The instance is changed in-place.
     *
     * @param d Value to divide by.
     * @return {@code this}.
     */
    RealVector mapDivideToSelf(double d);

    /**
     * Element-by-element multiplication.
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing this[i] * v[i] for all i
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealVector ebeMultiply(RealVector v);

    /**
     * Element-by-element multiplication.
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing this[i] * v[i] for all i
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealVector ebeMultiply(double[] v);

    /**
     * Element-by-element division.
     * @param v vector by which instance elements must be divided
     * @return a vector containing this[i] / v[i] for all i
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealVector ebeDivide(RealVector v);

    /**
     * Element-by-element division.
     * @param v vector by which instance elements must be divided
     * @return a vector containing this[i] / v[i] for all i
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealVector ebeDivide(double[] v);

    /**
     * Returns vector entries as a double array.
     * @return double array of entries
     */
     double[] getData();

    /**
     * Compute the dot product.
     * @param v vector with which dot product should be computed
     * @return the scalar dot product between instance and v
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    double dotProduct(RealVector v);

    /**
     * Compute the dot product.
     * @param v vector with which dot product should be computed
     * @return the scalar dot product between instance and v
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    double dotProduct(double[] v);

    /**
     * Returns the L<sub>2</sub> norm of the vector.
     * <p>The L<sub>2</sub> norm is the root of the sum of
     * the squared elements.</p>
     * @return norm
     * @see #getL1Norm()
     * @see #getLInfNorm()
     * @see #getDistance(RealVector)
     */
    double getNorm();

    /**
     * Returns the L<sub>1</sub> norm of the vector.
     * <p>The L<sub>1</sub> norm is the sum of the absolute
     * values of elements.</p>
     * @return norm
     * @see #getNorm()
     * @see #getLInfNorm()
     * @see #getL1Distance(RealVector)
     */
    double getL1Norm();

    /**
     * Returns the L<sub>&infin;</sub> norm of the vector.
     * <p>The L<sub>&infin;</sub> norm is the max of the absolute
     * values of elements.</p>
     * @return norm
     * @see #getNorm()
     * @see #getL1Norm()
     * @see #getLInfDistance(RealVector)
     */
    double getLInfNorm();

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with the
     * L<sub>2</sub> norm, i.e. the square root of the sum of
     * elements differences, or euclidian distance.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     * @see #getL1Distance(RealVector)
     * @see #getLInfDistance(RealVector)
     * @see #getNorm()
     */
    double getDistance(RealVector v);

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with the
     * L<sub>2</sub> norm, i.e. the square root of the sum of
     * elements differences, or euclidian distance.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     * @see #getL1Distance(double[])
     * @see #getLInfDistance(double[])
     * @see #getNorm()
     */
    double getDistance(double[] v);

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>1</sub> norm, i.e. the sum of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     * @see #getDistance(RealVector)
     * @see #getLInfDistance(RealVector)
     * @see #getL1Norm()
     */
    double getL1Distance(RealVector v);

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>1</sub> norm, i.e. the sum of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     * @see #getDistance(double[])
     * @see #getLInfDistance(double[])
     * @see #getL1Norm()
     */
    double getL1Distance(double[] v);

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>&infin;</sub> norm, i.e. the max of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     * @see #getDistance(RealVector)
     * @see #getL1Distance(RealVector)
     * @see #getLInfNorm()
     */
    double getLInfDistance(RealVector v);

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>&infin;</sub> norm, i.e. the max of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     * @see #getDistance(double[])
     * @see #getL1Distance(double[])
     * @see #getLInfNorm()
     */
    double getLInfDistance(double[] v);

    /** Creates a unit vector pointing in the direction of this vector.
     * <p>The instance is not changed by this method.</p>
     * @return a unit vector pointing in direction of this vector
     * @exception ArithmeticException if the norm is null
     */
    RealVector unitVector();

    /** Converts this vector into a unit vector.
     * <p>The instance itself is changed by this method.</p>
     * @throws  org.apache.commons.math.exception.MathArithmeticException
     * if the norm is zero.
     */
    void unitize();

    /** Find the orthogonal projection of this vector onto another vector.
     * @param v vector onto which instance must be projected
     * @return projection of the instance onto v
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealVector projection(RealVector v);

    /** Find the orthogonal projection of this vector onto another vector.
     * @param v vector onto which instance must be projected
     * @return projection of the instance onto v
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealVector projection(double[] v);

    /**
     * Compute the outer product.
     * @param v vector with which outer product should be computed
     * @return the square matrix outer product between instance and v
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealMatrix outerProduct(RealVector v);

    /**
     * Compute the outer product.
     * @param v vector with which outer product should be computed
     * @return the square matrix outer product between instance and v
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if {@code v} is not the same size as this vector.
     */
    RealMatrix outerProduct(double[] v);

    /**
     * Returns the entry in the specified index.
     *
     * @param index Index location of entry to be fetched.
     * @return the vector entry at {@code index}.
     * @throws org.apache.commons.math.exception.OutOfRangeException
     * if the index is not valid.
     * @see #setEntry(int, double)
     */
    double getEntry(int index);

    /**
     * Set a single element.
     * @param index element index.
     * @param value new value for the element.
     * @throws org.apache.commons.math.exception.OutOfRangeException
     * if the index is not valid.
     * @see #getEntry(int)
     */
    void setEntry(int index, double value);

    /**
     * Returns the size of the vector.
     * @return size
     */
    int getDimension();

    /**
     * Construct a vector by appending a vector to this vector.
     * @param v vector to append to this one.
     * @return a new vector
     */
    RealVector append(RealVector v);

    /**
     * Construct a vector by appending a double to this vector.
     * @param d double to append.
     * @return a new vector
     */
    RealVector append(double d);

    /**
     * Construct a vector by appending a double array to this vector.
     * @param a double array to append.
     * @return a new vector
     */
    RealVector append(double[] a);

    /**
     * Get a subvector from consecutive elements.
     * @param index index of first element.
     * @param n number of elements to be retrieved.
     * @return a vector containing n elements.
     * @throws org.apache.commons.math.exception.OutOfRangeException
     * if the index is not valid.
     */
    RealVector getSubVector(int index, int n);

    /**
     * Set a set of consecutive elements.
     * @param index index of first element to be set.
     * @param v vector containing the values to set.
     * @throws org.apache.commons.math.exception.OutOfRangeException
     * if the index is not valid.
     * @see #setSubVector(int, double[])
     */
    void setSubVector(int index, RealVector v);

    /**
     * Set a set of consecutive elements.
     * @param index index of first element to be set.
     * @param v vector containing the values to set.
     * @throws org.apache.commons.math.exception.OutOfRangeException
     * if the index is not valid.
     * @see #setSubVector(int, RealVector)
     */
    void setSubVector(int index, double[] v);

    /**
     * Set all elements to a single value.
     * @param value single value to set for all elements
     */
    void set(double value);

    /**
     * Convert the vector to a double array.
     * <p>The array is independent from vector data, it's elements
     * are copied.</p>
     * @return array containing a copy of vector elements
     */
    double[] toArray();

    /**
     * Check whether any coordinate of this vector is {@code NaN}.
     * @return {@code true} if any coordinate of this vector is {@code NaN},
     * {@code false} otherwise.
     */
    boolean isNaN();

    /**
     * Check whether any coordinate of this vector is infinite and none are {@code NaN}.
     *
     * @return {@code true} if any coordinate of this vector is infinite and
     * none are {@code NaN}, {@code false} otherwise.
     */
    boolean isInfinite();
}
