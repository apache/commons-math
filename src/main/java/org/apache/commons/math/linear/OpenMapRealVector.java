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

import java.io.Serializable;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.util.OpenIntToDoubleHashMap;
import org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator;

/**
 * This class implements the {@link RealVector} interface with a {@link OpenIntToDoubleHashMap} backing store.
 * @version $Revision$ $Date$
 * @since 2.0
*/
public class OpenMapRealVector extends AbstractRealVector implements SparseRealVector, Serializable {

    /** Default Tolerance for having a value considered zero. */
    public static final double DEFAULT_ZERO_TOLERANCE = 1.0e-12;

    /** Serializable version identifier. */
    private static final long serialVersionUID = 8772222695580707260L;

    /** Entries of the vector. */
    private final OpenIntToDoubleHashMap entries;

    /** Dimension of the vector. */
    private final int virtualSize;

    /** Negative tolerance for having a value considered zero. */
    private double minusEpsilon;

    /** Positive tolerance for having a value considered zero. */
    private double plusEpsilon;

    /**
     * Build a 0-length vector.
     * <p>Zero-length vectors may be used to initialized construction of vectors
     * by data gathering. We start with zero-length and use either the {@link
     * #OpenMapRealVector(OpenMapRealVector, int)} constructor
     * or one of the <code>append</code> method ({@link #append(double)}, {@link
     * #append(double[])}, {@link #append(RealVector)}) to gather data
     * into this vector.</p>
     */
    public OpenMapRealVector() {
        this(0, DEFAULT_ZERO_TOLERANCE, 0);
    }

    /**
     * Construct a (dimension)-length vector of zeros.
     * @param dimension size of the vector
     */
    public OpenMapRealVector(int dimension) {
        this(dimension, DEFAULT_ZERO_TOLERANCE, 0);
    }

    /**
     * Construct a (dimension)-length vector of zeros, specifying zero tolerance.
     * @param dimension Size of the vector
     * @param epsilon The tolerance for having a value considered zero
     * @param defaultValue value for non-specified entries
     */
    public OpenMapRealVector(int dimension, double epsilon, double defaultValue) {
        virtualSize = dimension;
        entries = new OpenIntToDoubleHashMap(defaultValue);
        setDefault(defaultValue, epsilon);
    }

    /**
     * Build a resized vector, for use with append.
     * @param v The original vector
     * @param resize The amount to resize it
     */
    protected OpenMapRealVector(OpenMapRealVector v, int resize) {
        virtualSize = v.getDimension() + resize;
        entries = new OpenIntToDoubleHashMap(v.entries);
        minusEpsilon = v.minusEpsilon;
        plusEpsilon = v.plusEpsilon;
    }

    /**
     * Build a vector with known the sparseness (for advanced use only).
     * @param dimension The size of the vector
     * @param expectedSize The expected number of non-zero entries
     */
    public OpenMapRealVector(int dimension, int expectedSize) {
        this(dimension, expectedSize, DEFAULT_ZERO_TOLERANCE);
    }

    /**
     * Build a vector with known the sparseness and zero tolerance setting (for advanced use only).
     * @param dimension The size of the vector
     * @param expectedSize The expected number of non-zero entries
     * @param epsilon The tolerance for having a value considered zero
     * @param defaultValue value for non-specified entries
     */
    public OpenMapRealVector(int dimension, int expectedSize, double epsilon, double defaultValue) {
        virtualSize = dimension;
        entries = new OpenIntToDoubleHashMap(expectedSize, defaultValue);
        setDefault(defaultValue, epsilon);
    }

    /**
     * Create from a double array.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     */
    public OpenMapRealVector(double[] values) {
        this(values, DEFAULT_ZERO_TOLERANCE);
    }

    /**
     * Create from a double array, specifying zero tolerance.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     * @param epsilon The tolerance for having a value considered zero
     */
    public OpenMapRealVector(double[] values, double epsilon) {
        virtualSize = values.length;
        entries = new OpenIntToDoubleHashMap(0.0);
        setDefault(0, epsilon);
        for (int key = 0; key < values.length; key++) {
            double value = values[key];
            if (!isDefaultValue(value)) {
                entries.put(key, value);
            }
        }
    }

    /**
     * Create from a Double array.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     */
    public OpenMapRealVector(Double[] values) {
        this(values, DEFAULT_ZERO_TOLERANCE, 0);
    }

    /**
     * Create from a Double array.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     * @param epsilon The tolerance for having a value considered zero
     * @param defaultValue value for non-specified entries
     */
    public OpenMapRealVector(Double[] values, double epsilon, double defaultValue) {
        virtualSize = values.length;
        entries = new OpenIntToDoubleHashMap(defaultValue);
        setDefault(defaultValue, epsilon);
        for (int key = 0; key < values.length; key++) {
            double value = values[key].doubleValue();
            if (!isDefaultValue(value)) {
                entries.put(key, value);
            }
        }
    }

    /**
     * Copy constructor.
     * @param v The instance to copy from
     */
    public OpenMapRealVector(OpenMapRealVector v) {
        virtualSize = v.getDimension();
        entries = new OpenIntToDoubleHashMap(v.getEntries());
        plusEpsilon = v.plusEpsilon;
        minusEpsilon = v.minusEpsilon;
    }

    /**
     * Generic copy constructor.
     * @param v The instance to copy from
     */
    public OpenMapRealVector(RealVector v) {
        virtualSize = v.getDimension();
        entries = new OpenIntToDoubleHashMap(0.0);
        setDefault(0, DEFAULT_ZERO_TOLERANCE);
        for (int key = 0; key < virtualSize; key++) {
            double value = v.getEntry(key);
            if (!isDefaultValue(value)) {
                entries.put(key, value);
            }
        }
    }

    /** Set defaults.
     * @param defaultValue value for non-specified entries
     * @param epsilon tolerance to check for equality with default value
     */
    private void setDefault(double defaultValue, double epsilon) {
      if (epsilon < 0) {
        throw new IllegalArgumentException("default tolerance must be > 0 :" + epsilon);
      }
      plusEpsilon  = defaultValue + epsilon;
      minusEpsilon = defaultValue - epsilon;
    }

    /**
     * Get the entries of this instance.
     * @return entries of this instance
     */
    private OpenIntToDoubleHashMap getEntries() {
        return entries;
    }

    /**
     * Determine if this value is within epsilon of the defaultValue (currently always zero).
     * @param value The value to test
     * @return <code>true</code> if this value is within epsilon to the defaultValue, <code>false</code> otherwise
     */
    protected boolean isDefaultValue(double value) {
        return value < plusEpsilon && value > minusEpsilon;
    }

    /** {@inheritDoc} */
    public RealVector add(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return add((OpenMapRealVector) v);
        } else {
            return super.add(v);
        }
    }

    /**
     * Optimized method to add two OpenMapRealVectors.  Copies the larger vector, iterates over the smaller.
     * @param v Vector to add with
     * @return The sum of <code>this</code> with <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public OpenMapRealVector add(OpenMapRealVector v) throws IllegalArgumentException{
        checkVectorDimensions(v.getDimension());
        boolean copyThis = entries.size() > v.entries.size();
        OpenMapRealVector res = copyThis ? this.copy() : v.copy();
        Iterator iter = copyThis ? v.entries.iterator() : entries.iterator();
        OpenIntToDoubleHashMap randomAccess = copyThis ? entries : v.entries;
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (randomAccess.containsKey(key)) {
                res.setEntry(key, randomAccess.get(key) + iter.value());
            } else {
                res.setEntry(key, iter.value());
            }
        }
        return res;
    }

    /**
     * Optimized method to append a OpenMapRealVector.
     * @param v vector to append
     * @return The result of appending <code>v</code> to self
     */
    public OpenMapRealVector append(OpenMapRealVector v) {
        OpenMapRealVector res = new OpenMapRealVector(this, v.getDimension());
        Iterator iter = v.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key() + virtualSize, iter.value());
        }
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector append(RealVector v) {
        if (v instanceof OpenMapRealVector) {
            return append((OpenMapRealVector) v);
        }
        return append(v.getData());
    }

    /** {@inheritDoc} */
    public OpenMapRealVector append(double d) {
        OpenMapRealVector res = new OpenMapRealVector(this, 1);
        res.setEntry(virtualSize, d);
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector append(double[] a) {
        OpenMapRealVector res = new OpenMapRealVector(this, a.length);
        for (int i = 0; i < a.length; i++) {
            res.setEntry(i + virtualSize, a[i]);
        }
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector copy() {
        return new OpenMapRealVector(this);
    }


    /** {@inheritDoc} */
    public OpenMapRealVector ebeDivide(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() / v.getEntry(iter.key()));
        }
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector ebeDivide(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() / v[iter.key()]);
        }
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector ebeMultiply(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() * v.getEntry(iter.key()));
        }
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector ebeMultiply(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() * v[iter.key()]);
        }
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector getSubVector(int index, int n) throws MatrixIndexException {
        checkIndex(index);
        checkIndex(index + n - 1);
        OpenMapRealVector res = new OpenMapRealVector(n);
        int end = index + n;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (key >= index && key < end) {
                res.setEntry(key - index, iter.value());
            }
        }
        return res;
    }

    /** {@inheritDoc} */
    public double[] getData() {
        double[] res = new double[virtualSize];
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res[iter.key()] = iter.value();
        }
        return res;
    }

    /** {@inheritDoc} */
    public int getDimension() {
        return virtualSize;
    }

    /**
     * Optimized method to compute distance.
     * @param v The vector to compute distance to
     * @return The distance from <code>this</code> and <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public double getDistance(OpenMapRealVector v) throws IllegalArgumentException {
        Iterator iter = entries.iterator();
        double res = 0;
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            double delta;
            delta = iter.value() - v.getEntry(key);
            res += delta * delta;
        }
        iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (!entries.containsKey(key)) {
                final double value = iter.value();
                res += value * value;
            }
        }
        return Math.sqrt(res);
    }

    /** {@inheritDoc} */
    public double getDistance(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return getDistance((OpenMapRealVector) v);
        }
        return getDistance(v.getData());
    }

    /** {@inheritDoc} */
    public double getDistance(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double res = 0;
        for (int i = 0; i < v.length; i++) {
            double delta = entries.get(i) - v[i];
            res += delta * delta;
        }
        return Math.sqrt(res);
    }

    /** {@inheritDoc} */
    public double getEntry(int index) throws MatrixIndexException {
        checkIndex(index);
        return entries.get(index);
    }

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>1</sub> norm, i.e. the sum of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     */
    public double getL1Distance(OpenMapRealVector v) {
        double max = 0;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            double delta = Math.abs(iter.value() - v.getEntry(iter.key()));
            max += delta;
        }
        iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (!entries.containsKey(key)) {
                double delta = Math.abs(iter.value());
                max +=  Math.abs(delta);
            }
        }
        return max;
    }

    /** {@inheritDoc} */
    public double getL1Distance(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return getL1Distance((OpenMapRealVector) v);
        }
        return getL1Distance(v.getData());
    }

    /** {@inheritDoc} */
    public double getL1Distance(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double max = 0;
        for (int i = 0; i < v.length; i++) {
            double delta = Math.abs(getEntry(i) - v[i]);
            max += delta;
        }
        return max;
    }

    /** {@inheritDoc} */
    public double getL1Norm() {
        double res = 0;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res += Math.abs(iter.value());
        }
        return res;
    }

    /**
     * Optimized method to compute LInfDistance.
     * @param v The vector to compute from
     * @return the LInfDistance
     */
    private double getLInfDistance(OpenMapRealVector v) {
        double max = 0;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            double delta = Math.abs(iter.value() - v.getEntry(iter.key()));
            if (delta > max) {
                max = delta;
            }
        }
        iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (!entries.containsKey(key)) {
                if (iter.value() > max) {
                    max = iter.value();
                }
            }
        }
        return max;
    }

    /** {@inheritDoc} */
    public double getLInfDistance(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return getLInfDistance((OpenMapRealVector) v);
        }
        return getLInfDistance(v.getData());
    }

    /** {@inheritDoc} */
    public double getLInfDistance(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double max = 0;
        for (int i = 0; i < v.length; i++) {
            double delta = Math.abs(getEntry(i) - v[i]);
            if (delta > max) {
                max = delta;
            }
        }
        return max;
    }

    /** {@inheritDoc} */
    public double getLInfNorm() {
        double max = 0;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            max += iter.value();
        }
        return max;
    }

    /** {@inheritDoc} */
    public double getNorm() {
        double res = 0;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res += iter.value() * iter.value();
        }
        return Math.sqrt(res);
    }

    /** {@inheritDoc} */
    public boolean isInfinite() {
        boolean infiniteFound = false;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final double value = iter.value();
            if (Double.isNaN(value)) {
                return false;
            }
            if (Double.isInfinite(value)) {
                infiniteFound = true;
            }
        }
        return infiniteFound;
    }

    /** {@inheritDoc} */
    public boolean isNaN() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            if (Double.isNaN(iter.value())) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapAbs() {
        return copy().mapAbsToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapAbsToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.abs(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapAcos() {
        return copy().mapAcosToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapAcosToSelf() {
        for (int i = 0; i < virtualSize; i++) {
            setEntry(i, Math.acos(getEntry(i)));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapAdd(double d) {
        return copy().mapAddToSelf(d);
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapAddToSelf(double d) {
        for (int i = 0; i < virtualSize; i++) {
            setEntry(i, getEntry(i) + d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapAsin() {
        return copy().mapAsinToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapAsinToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.asin(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapAtan() {
        return copy().mapAtanToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapAtanToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.atan(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapCbrt() {
        return copy().mapCbrtToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapCbrtToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.cbrt(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapCeil() {
        return copy().mapCeilToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapCeilToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.ceil(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapCos() {
        return copy().mapCosToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapCosToSelf() {
        for (int i = 0; i < virtualSize; i++) {
            setEntry(i, Math.cos(getEntry(i)));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapCosh() {
        return copy().mapCoshToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapCoshToSelf() {
        for (int i = 0; i < virtualSize; i++) {
            setEntry(i, Math.cosh(getEntry(i)));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapDivide(double d) {
        return copy().mapDivideToSelf(d);
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapDivideToSelf(double d) {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), iter.value() / d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapExp() {
        return copy().mapExpToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapExpToSelf() {
        for (int i = 0; i < virtualSize; i++) {
            entries.put(i, Math.exp(entries.get(i)));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapExpm1() {
        return copy().mapExpm1ToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapExpm1ToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.expm1(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapFloor() {
        return copy().mapFloorToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapFloorToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.floor(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapInv() {
        return copy().mapInvToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapInvToSelf() {
        for (int i = 0; i < virtualSize; i++) {
            setEntry(i, 1.0/getEntry(i));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapLog() {
        return copy().mapLogToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapLog10() {
        return copy().mapLog10ToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapLog10ToSelf() {
        for (int i = 0; i < virtualSize; i++) {
            setEntry(i, Math.log10(getEntry(i)));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapLog1p() {
        return copy().mapLog1pToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapLog1pToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.log1p(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapLogToSelf() {
        for (int i = 0; i < virtualSize; i++) {
            setEntry(i, Math.log(getEntry(i)));
        }
       return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapMultiply(double d) {
        return copy().mapMultiplyToSelf(d);
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapMultiplyToSelf(double d) {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), iter.value() * d);
        }
        return this;
    }
    /** {@inheritDoc} */
    public OpenMapRealVector mapPow(double d) {
        return copy().mapPowToSelf(d);
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapPowToSelf(double d) {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.pow(iter.value(), d));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapRint() {
        return copy().mapRintToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapRintToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.rint(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapSignum() {
        return copy().mapSignumToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapSignumToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.signum(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapSin() {
        return copy().mapSinToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapSinToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.sin(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapSinh() {
        return copy().mapSinhToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapSinhToSelf() {

        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.sinh(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapSqrt() {
        return copy().mapSqrtToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapSqrtToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.sqrt(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapSubtract(double d) {
        return copy().mapSubtractToSelf(d);
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapSubtractToSelf(double d) {
        return mapAddToSelf(-d);
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapTan() {
        return copy().mapTanToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapTanToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.tan(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapTanh() {
        return copy().mapTanhToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapTanhToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.tanh(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapUlp() {
        return copy().mapUlpToSelf();
    }

    /** {@inheritDoc} */
    public OpenMapRealVector mapUlpToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.ulp(iter.value()));
        }
        return this;
    }

    /**
     * Optimized method to compute the outer product.
     * @param v The vector to comput the outer product on
     * @return The outer product of <code>this</code> and <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public OpenMapRealMatrix outerproduct(OpenMapRealVector v) throws IllegalArgumentException{
        checkVectorDimensions(v.getDimension());
        OpenMapRealMatrix res = new OpenMapRealMatrix(virtualSize, virtualSize);
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            Iterator iter2 = v.getEntries().iterator();
            while (iter2.hasNext()) {
                iter2.advance();
                res.setEntry(iter.key(), iter2.key(), iter.value()*iter2.value());
            }
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealMatrix outerProduct(RealVector v)
            throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return outerproduct((OpenMapRealVector)v);
        }
        RealMatrix res = new OpenMapRealMatrix(virtualSize, virtualSize);
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            int row = iter.key();
            for (int col = 0; col < virtualSize; col++) {
                res.setEntry(row, col, iter.value()*v.getEntry(col));
            }
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealMatrix outerProduct(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        RealMatrix res = new OpenMapRealMatrix(virtualSize, virtualSize);
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            int row = iter.key();
            double value = iter.value();
            for (int col = 0; col < virtualSize; col++) {
                res.setEntry(row, col, value * v[col]);
            }
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealVector projection(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        return v.mapMultiply(dotProduct(v) / v.dotProduct(v));
    }

    /** {@inheritDoc} */
    public OpenMapRealVector projection(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        return (OpenMapRealVector) projection(new OpenMapRealVector(v));
    }

    /** {@inheritDoc} */
    public void setEntry(int index, double value) throws MatrixIndexException {
        checkIndex(index);
        if (!isDefaultValue(value)) {
            entries.put(index, value);
        } else if (entries.containsKey(index)) {
            entries.remove(index);
        }
    }

    /** {@inheritDoc} */
    public void setSubVector(int index, RealVector v) throws MatrixIndexException {
        checkIndex(index);
        checkIndex(index + v.getDimension() - 1);
        setSubVector(index, v.getData());
    }

    /** {@inheritDoc} */
    public void setSubVector(int index, double[] v) throws MatrixIndexException {
        checkIndex(index);
        checkIndex(index + v.length - 1);
        for (int i = 0; i < v.length; i++) {
            setEntry(i + index, v[i]);
        }
    }

    /** {@inheritDoc} */
    public void set(double value) {
        for (int i = 0; i < virtualSize; i++) {
            setEntry(i, value);
        }
    }

    /**
     * Optimized method to subtract OpenMapRealVectors.
     * @param v The vector to subtract from <code>this</code>
     * @return The difference of <code>this</code> and <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public OpenMapRealVector subtract(OpenMapRealVector v) throws IllegalArgumentException{
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = copy();
        Iterator iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (entries.containsKey(key)) {
                res.setEntry(key, entries.get(key) - iter.value());
            } else {
                res.setEntry(key, -iter.value());
            }
        }
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector subtract(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return subtract((OpenMapRealVector) v);
        }
        return subtract(v.getData());
    }

    /** {@inheritDoc} */
    public OpenMapRealVector subtract(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        OpenMapRealVector res = new OpenMapRealVector(this);
        for (int i = 0; i < v.length; i++) {
            if (entries.containsKey(i)) {
                res.setEntry(i, entries.get(i) - v[i]);
            } else {
                res.setEntry(i, -v[i]);
            }
        }
        return res;
    }


    /** {@inheritDoc} */
    public OpenMapRealVector unitVector() {
        OpenMapRealVector res = copy();
        res.unitize();
        return res;
    }

    /** {@inheritDoc} */
    public void unitize() {
        double norm = getNorm();
        if (isDefaultValue(norm)) {
            throw  MathRuntimeException.createArithmeticException("cannot normalize a zero norm vector");
        }
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), iter.value() / norm);
        }

    }


    /** {@inheritDoc} */
    public double[] toArray() {
        return getData();
    }

    /** {@inheritDoc}
     * <p> Implementation Note: This works on exact values, and as a result
     * it is possible for {@code a.subtract(b)} to be the zero vector, while
     * {@code a.hashCode() != b.hashCode()}.</p>
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(plusEpsilon) + Double.doubleToLongBits(minusEpsilon);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + virtualSize;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            temp = Double.doubleToLongBits(iter.value());
            result = prime * result + (int) (temp ^ (temp >>32));
        }
        return result;
    }

    /**
     * <p> Implementation Note: This performs an exact comparison, and as a result
     * it is possible for {@code a.subtract(b}} to be the zero vector, while
     * {@code  a.equals(b) == false}.</p>
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OpenMapRealVector)) {
            return false;
        }
        OpenMapRealVector other = (OpenMapRealVector) obj;
        if (virtualSize != other.virtualSize) {
            return false;
        }
        if (Double.doubleToLongBits(minusEpsilon) !=
            Double.doubleToLongBits(other.minusEpsilon)) {
            return false;
        }
        if (Double.doubleToLongBits(plusEpsilon) !=
            Double.doubleToLongBits(other.plusEpsilon)) {
            return false;
        }
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            double test = other.getEntry(iter.key());
            if (Double.doubleToLongBits(test) != Double.doubleToLongBits(iter.value())) {
                return false;
            }
        }
        iter = other.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            double test = iter.value();
            if (Double.doubleToLongBits(test) != Double.doubleToLongBits(getEntry(iter.key()))) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return the percentage of none zero elements as a decimal percent.
     */
    public double getSparcity() {
        return (double)entries.size()/(double)getDimension();
    }

    /** @{InheritDoc} */
    public java.util.Iterator<Entry> sparseIterator() {
        return new OpenMapSparseIterator();
    }
    
    /**
     *  Implementation of <code>Entry</code> optimized for OpenMap.
     * <p>This implementation does not allow arbitrary calls to <code>setIndex</code>
     * since the order that entries are returned is undefined.
     */
    protected class OpenMapEntry extends Entry {
        private final Iterator iter;

        protected OpenMapEntry(Iterator iter) {
            this.iter = iter;
        }
        /** {@InheritDoc} */
        @Override
        public double getValue() {
            return iter.value();
        }

        /** {@InheritDoc} */
        @Override
        public void setValue(double value) {
            entries.put(iter.key(), value);
        }
        
        /** {@InheritDoc} */
        @Override
        public int getIndex() {
            return iter.key();
        }
    }
    
    /**
     *  Iterator class to do iteration over just the non-zero elements.
     *  <p>This implementation is fail-fast, so cannot be used to modify any zero element. 
     *
     */
    
    protected class OpenMapSparseIterator implements java.util.Iterator<Entry> {
        private final Iterator iter;
        private final Entry current;
        
        protected OpenMapSparseIterator() {
            iter = entries.iterator();
            current = new OpenMapEntry(iter);
        }

        /** {@InheritDoc} */
        public boolean hasNext() {
            return iter.hasNext();
        }

        /** {@InheritDoc} */
        public Entry next() {
            iter.advance();
            return current;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported");
       }
        
    }
}
