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

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.util.OpenIntToDoubleHashMap;
import org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator;

/**
 * This class implements the {@link RealVector} interface with a {@link OpenIntToDoubleHashMap} backing store.
 * @version $Revision: 728186 $ $Date$
 * @since 2.0
*/
public class SparseRealVector implements RealVector {

    /** Entries of the vector. */
    private  OpenIntToDoubleHashMap entries;

    /** Dimension of the vector. */
    private final int virtualSize;

    /** Tolerance for having a value considered zero. */
    private double epsilon = 1.0e-12;

    /**
     * Build a 0-length vector.
     * <p>Zero-length vectors may be used to initialized construction of vectors
     * by data gathering. We start with zero-length and use either the {@link
     * #SparseRealVector(SparseRealVector, int)} constructor
     * or one of the <code>append</code> method ({@link #append(double)}, {@link
     * #append(double[])}, {@link #append(RealVector)}) to gather data
     * into this vector.</p>
     */
    public SparseRealVector() {
        virtualSize = 0;
        entries = new OpenIntToDoubleHashMap(0.0);
    }

    /**
     * Construct a (dimension)-length vector of zeros.
     * @param dimension size of the vector
     */
    public SparseRealVector(int dimension) {
        virtualSize = dimension;
        entries = new OpenIntToDoubleHashMap(0.0);
    }

    /**
     * Construct a (dimension)-length vector of zeros, specifying zero tolerance
     * @param dimension Size of the vector
     * @param epsilon The tolerance for having a value considered zero
     */
    public SparseRealVector(int dimension, double epsilon){
        virtualSize = dimension;
        entries = new OpenIntToDoubleHashMap(0.0);
        this.epsilon = epsilon;
    }
    
    /**
     * Resize the vector, for use with append
     * @param v The original vector
     * @param resize The amount to resize it
     */
    protected SparseRealVector(SparseRealVector v, int resize) {
        virtualSize = v.getDimension() + resize;
        entries = new OpenIntToDoubleHashMap(v.entries);
    }

    /**
     * For advanced use, when you know the sparseness 
     * @param dimension The size of the vector
     * @param expectedSize The excpected number of non-zero entries
     */
    public SparseRealVector(int dimension, int expectedSize) {
        entries = new OpenIntToDoubleHashMap(expectedSize, 0.0);
        virtualSize = dimension;
    }

    /**
     * For advanced use, when you know the sparseness and want to specify zero tolerance
     * @param dimension The size of the vector
     * @param expectedSize The expected number of non-zero entries
     * @param epsilon The tolerance for having a value considered zero
     */
    public SparseRealVector(int dimension, int expectedSize, double epsilon){
        virtualSize = dimension;
        entries = new OpenIntToDoubleHashMap(expectedSize, 0.0);
        this.epsilon = epsilon;
    }
    
    /**
     * Create from a double array.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     */
    public SparseRealVector(double[] values) {
        virtualSize = values.length;
        fromDoubleArray(values);
    }

    /**
     * Create from a double array, specifying zero tolerance.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     * @param epsilon The tolerance for having a value considered zero 
     */
    public SparseRealVector(double [] values, double epsilon){
        virtualSize = values.length;
        this.epsilon = epsilon;
        fromDoubleArray(values);
    }
    
    /**
     * Create from a Double array.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     */
    public SparseRealVector(Double [] values) {
        virtualSize = values.length;
        double[] vals = new double[values.length];
        for(int i=0; i < values.length; i++){
            vals[i] = values[i].doubleValue();
        }
        fromDoubleArray(vals);
    }
    
    /**
     * Create from a Double array.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     * @param epsilon The tolerance for having a value considered zero
     */
    public SparseRealVector(Double [] values, double epsilon){
        virtualSize = values.length;
        this.epsilon = epsilon;
        double[] vals = new double[values.length];
        for(int i=0; i < values.length; i++){
            vals[i] = values[i].doubleValue();
        }
        fromDoubleArray(vals);
    }
    
    /**
     * Copy constructer
     * @param v The instance to copy from
     */
    public SparseRealVector(SparseRealVector v){
        virtualSize = v.getDimension();
        epsilon = v.getEpsilon();
        entries = new OpenIntToDoubleHashMap(v.getEntries());
    }

    /**
     * Generic copy constructer
     * @param v The instance to copy from
     */
    public SparseRealVector(RealVector v) {
        virtualSize = v.getDimension();
        fromDoubleArray(v.getData());
    }

    
    /**
     * Fill in the values from a double array
     * @param values The set of values to use
     */
    private void fromDoubleArray(double[] values) {
        entries = new OpenIntToDoubleHashMap(0.0);
        for (int key = 0; key < values.length; key++) {
            double value = values[key];
            if (!isZero(value)) {
                entries.put(key, value);
            }
        }
    }

    /**
     * 
     * @return The entries of this instance
     */
    private OpenIntToDoubleHashMap getEntries() {
        return entries;
    }

    
    /**
     * Determine if this value is zero
     * @param value The value to test
     * @return <code>true</code> if this value is zero, <code>false</code> otherwise
     */
    protected boolean isZero(double value) {
        return value > -epsilon && value < epsilon;
    }

    /**
     * 
     * @return The test range for testing if a value is zero
     */
    public double getEpsilon() {
        return epsilon;
    }

    /**
     * 
     * @param epsilon The test range for testing if a value is zero
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    /** {@inheritDoc} */
    public RealVector add(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof SparseRealVector)
            return add((SparseRealVector) v);
        return add(v.getData());

    }

    /**
     * Optimized method to add two SparseRealVectors
     * @param v Vector to add with
     * @return The sum of <code>this</code> with <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public SparseRealVector add(SparseRealVector v) throws IllegalArgumentException{
        checkVectorDimensions(v.getDimension());
        SparseRealVector res = (SparseRealVector)copy();
        Iterator iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (entries.containsKey(key)) {
                res.setEntry(key, entries.get(key) + iter.value());
            } else {
                res.setEntry(key, iter.value());
            }
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealVector add(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        SparseRealVector res = new SparseRealVector(getDimension());
        for (int i = 0; i < v.length; i++) {
            res.setEntry(i, v[i] + getEntry(i));
        }
        return res;
    }

    /**
     * Optimized method to append a SparseRealVector
     * @param v vector to append
     * @return The result of appending <code>v</code> to self
     */
    public SparseRealVector append(SparseRealVector v) {
        SparseRealVector res = new SparseRealVector(this, v.getDimension());
        Iterator iter = v.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key() + virtualSize, iter.value());
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealVector append(RealVector v) {
        if (v instanceof SparseRealVector) {
            return append((SparseRealVector) v);
        }
        return append(v.getData());
    }

    /** {@inheritDoc} */
    public RealVector append(double d) {
        RealVector res = new SparseRealVector(this, 1);
        res.setEntry(virtualSize, d);
        return res;
    }

    /** {@inheritDoc} */
    public RealVector append(double[] a) {
        RealVector res = new SparseRealVector(this, a.length);
        for (int i = 0; i < a.length; i++) {
            res.setEntry(i + virtualSize, a[i]);
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealVector copy() {
        return new SparseRealVector(this);
    }

    /** {@inheritDoc} */
    public double dotProduct(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        double res = 0;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res += v.getEntry(iter.key()) * iter.value();
        }
        return res;
    }

    /** {@inheritDoc} */
    public double dotProduct(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double res = 0;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            int idx = iter.key();
            double value = 0;
            if (idx < v.length)
                value = v[idx];
            res += value * iter.value();
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealVector ebeDivide(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        SparseRealVector res = new SparseRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() / v.getEntry(iter.key()));
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealVector ebeDivide(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        SparseRealVector res = new SparseRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() / v[iter.key()]);
        }
        return null;
    }

    /** {@inheritDoc} */
    public RealVector ebeMultiply(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        SparseRealVector res = new SparseRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() * v.getEntry(iter.key()));
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealVector ebeMultiply(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        SparseRealVector res = new SparseRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() * v[iter.key()]);
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealVector getSubVector(int index, int n) throws MatrixIndexException {
        checkIndex(index);
        checkIndex(index+n-1);
        SparseRealVector res = new SparseRealVector(n);
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
     * Optimized method to compute distance
     * @param v The vector to compute distance to
     * @return The distance from <code>this</code> and <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public double getDistance(SparseRealVector v) throws IllegalArgumentException {
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
                res += iter.value() * iter.value();
            }
        }
        return Math.sqrt(res);
    }

    /** {@inheritDoc} */
    public double getDistance(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof SparseRealVector) {
            return getDistance((SparseRealVector) v);
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

    /** {@inheritDoc} */
    public double getL1Distance(SparseRealVector v) {
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
        if (v instanceof SparseRealVector) {
            return getL1Distance((SparseRealVector) v);
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
     * Optimized method to compute LInfDistance  
     * @param v The vector to compute from
     * @return the LInfDistance
     */
    private double getLInfDistance(SparseRealVector v) {
        double max = 0;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            double delta = Math.abs(iter.value() - v.getEntry(iter.key()));
            if(delta > max)
                max = delta;
        }
        iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (!entries.containsKey(key)) {
                if(iter.value() > max)
                    max = iter.value();
            }
        }
        return max;
    }

    /** {@inheritDoc} */
    public double getLInfDistance(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof SparseRealVector) {
            return getLInfDistance((SparseRealVector) v);
        }
        return getLInfDistance(v.getData());
    }

    /** {@inheritDoc} */
    public double getLInfDistance(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double max = 0;
        for (int i = 0; i < v.length; i++) {
            double delta = Math.abs(getEntry(i) - v[i]);
            if(delta > max)
                max = delta;
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
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            if (Double.isInfinite(iter.value()))
                return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    public boolean isNaN() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            if (Double.isNaN(iter.value()))
                return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    public RealVector mapAbs() {
        return copy().mapAbsToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapAbsToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.abs(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapAcos() {
        return copy().mapAcosToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapAcosToSelf() {
        for(int i=0; i < virtualSize; i++){
            setEntry(i, Math.acos(getEntry(i)));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapAdd(double d) {
        return copy().mapAddToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapAddToSelf(double d) {
        for (int i = 0; i < virtualSize; i++) {
            setEntry(i, getEntry(i) + d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapAsin() {
        return copy().mapAsinToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapAsinToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.asin(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapAtan() {
        return copy().mapAtanToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapAtanToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.atan(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapCbrt() {
        return copy().mapCbrtToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapCbrtToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.cbrt(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapCeil() {
        return copy().mapCeilToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapCeilToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.ceil(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapCos() {
        return copy().mapCosToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapCosToSelf() {
        for(int i=0; i < virtualSize; i++){
            setEntry(i, Math.cos(getEntry(i)));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapCosh() {
        return copy().mapCoshToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapCoshToSelf() {
        for(int i = 0; i < virtualSize; i++){
            setEntry(i, Math.cosh(getEntry(i)));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapDivide(double d) {
        return copy().mapDivideToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapDivideToSelf(double d) {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), iter.value() / d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapExp() {
        return copy().mapExpToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapExpToSelf() {
        for (int i = 0; i < virtualSize; i++) {
            entries.put(i, Math.exp(entries.get(i)));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapExpm1() {
        return copy().mapExpm1ToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapExpm1ToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.expm1(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapFloor() {
        return copy().mapFloorToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapFloorToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.floor(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapInv() {
        return copy().mapInvToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapInvToSelf() {
        for(int i=0; i < virtualSize; i++){
            setEntry(i, 1.0/getEntry(i));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapLog() {
        return copy().mapLogToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapLog10() {
        return copy().mapLog10ToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapLog10ToSelf() {
        for(int i=0; i < virtualSize; i++){
            setEntry(i, Math.log10(getEntry(i)));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapLog1p() {
        return copy().mapLog1pToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapLog1pToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.log1p(iter.value()));
        }
        return this;
    }
    
    /** {@inheritDoc} */
    public RealVector mapLogToSelf() {
        for(int i=0; i < virtualSize; i++){
            setEntry(i, Math.log(getEntry(i)));
        }
       return this;
    }

    /** {@inheritDoc} */
    public RealVector mapMultiply(double d) {
        return copy().mapMultiplyToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapMultiplyToSelf(double d) {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), iter.value() * d);
        }
        return this;
    }
    /** {@inheritDoc} */
    public RealVector mapPow(double d) {
        return copy().mapPowToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapPowToSelf(double d) {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.pow(iter.value(), d));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapRint() {
        return copy().mapRintToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapRintToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.rint(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapSignum() {
        return copy().mapSignumToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapSignumToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.signum(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapSin() {
        return copy().mapSinToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapSinToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.sin(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapSinh() {
        return copy().mapSinhToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapSinhToSelf() {

        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.sinh(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapSqrt() {
        return copy().mapSqrtToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapSqrtToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.sqrt(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapSubtract(double d) {
        return copy().mapSubtractToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapSubtractToSelf(double d) {
        return mapAddToSelf(-d);
    }

    /** {@inheritDoc} */
    public RealVector mapTan() {
        return copy().mapTanToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapTanToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.tan(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapTanh() {
        return copy().mapTanhToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapTanhToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.tanh(iter.value()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapUlp() {
        return copy().mapUlpToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapUlpToSelf() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), Math.ulp(iter.value()));
        }
        return this;
    }

    /**
     * Optimized method to compute the outer product
     * @param v The vector to comput the outer product on
     * @return The outer product of <code>this</code> and <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public SparseRealMatrix outerproduct(SparseRealVector v) throws IllegalArgumentException{
        checkVectorDimensions(v.getDimension());
        SparseRealMatrix res = new SparseRealMatrix(virtualSize, virtualSize);
        Iterator iter = entries.iterator();
        while(iter.hasNext()){
            iter.advance();
            Iterator iter2 = v.getEntries().iterator();
            while(iter2.hasNext()){
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
        if(v instanceof SparseRealVector){
            return outerproduct((SparseRealVector)v);
        }
        RealMatrix res = new SparseRealMatrix(virtualSize, virtualSize);
        Iterator iter = entries.iterator();
        while(iter.hasNext()){
            iter.advance();
            int row = iter.key();
            for(int col=0; col < virtualSize; col++){
                res.setEntry(row, col, iter.value()*v.getEntry(col));
            }
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealMatrix outerProduct(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        RealMatrix res = new SparseRealMatrix(virtualSize, virtualSize);
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
    public RealVector projection(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        return projection(new SparseRealVector(v));
    }

    /** {@inheritDoc} */
    public void setEntry(int index, double value) throws MatrixIndexException {
        checkIndex(index);
        if (!isZero(value)) {
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
        for(int i=0; i < virtualSize; i++){
            setEntry(i, value);
        }
    }

    /**
     * Optimized method to subtract SparseRealVectors
     * @param v The vector to subtract from <code>this</code>
     * @return The difference of <code>this</code> and <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public SparseRealVector subtract(SparseRealVector v) throws IllegalArgumentException{
        checkVectorDimensions(v.getDimension());
        SparseRealVector res = (SparseRealVector)copy();
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
    public RealVector subtract(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof SparseRealVector) {
            return subtract((SparseRealVector) v);
        }
        return subtract(v.getData());
    }

    /** {@inheritDoc} */
    public RealVector subtract(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        SparseRealVector res = new SparseRealVector(this);
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
    public RealVector unitVector() {
        RealVector res = copy();
        res.unitize();
        return res;
    }

    /** {@inheritDoc} */
    public void unitize() {
        double norm = getNorm();
        if(isZero(norm)){
            throw  MathRuntimeException.createArithmeticException("cannot normalize a zero norm vector",
                    null);
            
        }
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), iter.value() / norm);
        }

    }

    /**
     * Check if an index is valid.
     * 
     * @param index
     *            index to check
     * @exception MatrixIndexException
     *                if index is not valid
     */
    private void checkIndex(final int index) throws MatrixIndexException {
        if (index < 0 || index >= getDimension()) {
            throw new MatrixIndexException(
                    "index {0} out of allowed range [{1}, {2}]",
                    new Object[] { index, 0, getDimension() - 1 });
        }
    }

    /**
     * Check if instance dimension is equal to some expected value.
     * 
     * @param n
     *            expected dimension.
     * @exception IllegalArgumentException
     *                if the dimension is inconsistent with vector size
     */
    protected void checkVectorDimensions(int n) throws IllegalArgumentException {
        if (getDimension() != n) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "vector length mismatch: got {0} but expected {1}",
                    new Object[] { getDimension(), n });
        }
    }

    /** {@inheritDoc} */
    public double[] toArray() {
        return getData();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(epsilon);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + virtualSize;
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SparseRealVector))
            return false;
        SparseRealVector other = (SparseRealVector) obj;
        if (virtualSize != other.virtualSize)
            return false;
        if (Double.doubleToLongBits(epsilon) != Double
                .doubleToLongBits(other.epsilon))
            return false;
        Iterator iter = entries.iterator();
        while(iter.hasNext()){
            iter.advance();
            double test = iter.value() - other.getEntry(iter.key());
            if(Math.abs(test) > epsilon)
                return false;
        }
        iter = other.getEntries().iterator();
        while(iter.hasNext()){
            iter.advance();
            double test = iter.value() - getEntry(iter.key());
            if(!isZero(test))
                return false;
        }
        return true;
    }

}
