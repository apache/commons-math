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
import java.util.NoSuchElementException;

import org.apache.commons.math.exception.MathUnsupportedOperationException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.analysis.FunctionUtils;
import org.apache.commons.math.analysis.function.Add;
import org.apache.commons.math.analysis.function.Multiply;
import org.apache.commons.math.analysis.function.Divide;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * This class provides default basic implementations for many methods in the
 * {@link RealVector} interface.
 *
 * @version $Id$
 * @since 2.1
 */
public abstract class AbstractRealVector implements RealVector {

    /**
     * Check if instance and specified vectors have the same dimension.
     *
     * @param v Vector to compare instance with.
     * @throws DimensionMismatchException if the vectors do not
     * have the same dimension.
     */
    protected void checkVectorDimensions(RealVector v) {
        checkVectorDimensions(v.getDimension());
    }

    /**
     * Check if instance dimension is equal to some expected value.
     *
     * @param n Expected dimension.
     * @throws DimensionMismatchException if the dimension is
     * inconsistent with the vector size.
     */
    protected void checkVectorDimensions(int n) {
        int d = getDimension();
        if (d != n) {
            throw new DimensionMismatchException(d, n);
        }
    }

    /**
     * Check if an index is valid.
     *
     * @param index Index to check.
     * @exception OutOfRangeException if {@code index} is not valid.
     */
    protected void checkIndex(final int index) {
        if (index < 0 ||
            index >= getDimension()) {
            throw new OutOfRangeException(LocalizedFormats.INDEX,
                                          index, 0, getDimension() - 1);
        }
    }

    /** {@inheritDoc} */
    public void setSubVector(int index, RealVector v) {
        checkIndex(index);
        checkIndex(index + v.getDimension() - 1);
        setSubVector(index, v.getData());
    }

    /** {@inheritDoc} */
    public void setSubVector(int index, double[] v) {
        checkIndex(index);
        checkIndex(index + v.length - 1);
        for (int i = 0; i < v.length; i++) {
            setEntry(i + index, v[i]);
        }
    }

    /** {@inheritDoc} */
    public RealVector add(double[] v) {
        double[] result = v.clone();
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            result[e.getIndex()] += e.getValue();
        }
        return new ArrayRealVector(result, false);
    }

    /** {@inheritDoc} */
    public RealVector add(RealVector v) {
        if (v instanceof ArrayRealVector) {
            double[] values = ((ArrayRealVector)v).getDataRef();
            return add(values);
        }
        RealVector result = v.copy();
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            final int index = e.getIndex();
            result.setEntry(index, e.getValue() + result.getEntry(index));
        }
        return result;
    }

    /** {@inheritDoc} */
    public RealVector subtract(double[] v) {
        double[] result = v.clone();
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            final int index = e.getIndex();
            result[index] = e.getValue() - result[index];
        }
        return new ArrayRealVector(result, false);
    }

    /** {@inheritDoc} */
    public RealVector subtract(RealVector v) {
        if (v instanceof ArrayRealVector) {
            double[] values = ((ArrayRealVector)v).getDataRef();
            return add(values);
        }
        RealVector result = v.copy();
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            final int index = e.getIndex();
            v.setEntry(index, e.getValue() - result.getEntry(index));
        }
        return result;
    }

    /** {@inheritDoc} */
    public RealVector mapAdd(double d) {
        return copy().mapAddToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapAddToSelf(double d) {
        if (d != 0) {
            return mapToSelf(FunctionUtils.fix2ndArgument(new Add(), d));
        }
        return this;
    }

    /** {@inheritDoc} */
    public abstract AbstractRealVector copy();

    /** {@inheritDoc} */
    public double dotProduct(double[] v) {
        return dotProduct(new ArrayRealVector(v, false));
    }

    /** {@inheritDoc} */
    public double dotProduct(RealVector v) {
        checkVectorDimensions(v);
        double d = 0;
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            d += e.getValue() * v.getEntry(e.getIndex());
        }
        return d;
    }

    /** {@inheritDoc} */
    public double cosine(RealVector v) {
        final double norm = getNorm();
        final double vNorm = v.getNorm();

        if (norm == 0 ||
            vNorm == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM);
        }
        return dotProduct(v) / (norm * vNorm);
    }

    /** {@inheritDoc} */
    public double cosine(double[] v) {
        return cosine(new ArrayRealVector(v, false));
    }

    /** {@inheritDoc} */
    public RealVector ebeDivide(double[] v) {
        return ebeDivide(new ArrayRealVector(v, false));
    }

    /** {@inheritDoc} */
    public RealVector ebeMultiply(double[] v) {
        return ebeMultiply(new ArrayRealVector(v, false));
    }

    /** {@inheritDoc} */
    public double getDistance(RealVector v) {
        checkVectorDimensions(v);
        double d = 0;
        Iterator<Entry> it = iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            final double diff = e.getValue() - v.getEntry(e.getIndex());
            d += diff * diff;
        }
        return FastMath.sqrt(d);
    }

    /** {@inheritDoc} */
    public double getNorm() {
        double sum = 0;
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            final double value = e.getValue();
            sum += value * value;
        }
        return FastMath.sqrt(sum);
    }

    /** {@inheritDoc} */
    public double getL1Norm() {
        double norm = 0;
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            norm += FastMath.abs(e.getValue());
        }
        return norm;
    }

    /** {@inheritDoc} */
    public double getLInfNorm() {
        double norm = 0;
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            norm = FastMath.max(norm, FastMath.abs(e.getValue()));
        }
        return norm;
    }

    /** {@inheritDoc} */
    public double getDistance(double[] v) {
        return getDistance(new ArrayRealVector(v,false));
    }

    /** {@inheritDoc} */
    public double getL1Distance(RealVector v) {
        checkVectorDimensions(v);
        double d = 0;
        Iterator<Entry> it = iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            d += FastMath.abs(e.getValue() - v.getEntry(e.getIndex()));
        }
        return d;
    }

    /** {@inheritDoc} */
    public double getL1Distance(double[] v) {
        checkVectorDimensions(v.length);
        double d = 0;
        Iterator<Entry> it = iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            d += FastMath.abs(e.getValue() - v[e.getIndex()]);
        }
        return d;
    }

    /** {@inheritDoc} */
    public double getLInfDistance(RealVector v) {
        checkVectorDimensions(v);
        double d = 0;
        Iterator<Entry> it = iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            d = FastMath.max(FastMath.abs(e.getValue() - v.getEntry(e.getIndex())), d);
        }
        return d;
    }

    /** {@inheritDoc} */
    public double getLInfDistance(double[] v) {
        checkVectorDimensions(v.length);
        double d = 0;
        Iterator<Entry> it = iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            d = FastMath.max(FastMath.abs(e.getValue() - v[e.getIndex()]), d);
        }
        return d;
    }

    /** Get the index of the minimum entry.
     * @return index of the minimum entry or -1 if vector length is 0
     * or all entries are NaN
     */
    public int getMinIndex() {
        int minIndex    = -1;
        double minValue = Double.POSITIVE_INFINITY;
        Iterator<Entry> iterator = iterator();
        while (iterator.hasNext()) {
            final Entry entry = iterator.next();
            if (entry.getValue() <= minValue) {
                minIndex = entry.getIndex();
                minValue = entry.getValue();
            }
        }
        return minIndex;
    }

    /** Get the value of the minimum entry.
     * @return value of the minimum entry or NaN if all entries are NaN
     */
    public double getMinValue() {
        final int minIndex = getMinIndex();
        return minIndex < 0 ? Double.NaN : getEntry(minIndex);
    }

    /** Get the index of the maximum entry.
     * @return index of the maximum entry or -1 if vector length is 0
     * or all entries are NaN
     */
    public int getMaxIndex() {
        int maxIndex    = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        Iterator<Entry> iterator = iterator();
        while (iterator.hasNext()) {
            final Entry entry = iterator.next();
            if (entry.getValue() >= maxValue) {
                maxIndex = entry.getIndex();
                maxValue = entry.getValue();
            }
        }
        return maxIndex;
    }

    /** Get the value of the maximum entry.
     * @return value of the maximum entry or NaN if all entries are NaN
     */
    public double getMaxValue() {
        final int maxIndex = getMaxIndex();
        return maxIndex < 0 ? Double.NaN : getEntry(maxIndex);
    }


    /** {@inheritDoc} */
    public RealVector mapMultiply(double d) {
        return copy().mapMultiplyToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapMultiplyToSelf(double d){
        return mapToSelf(FunctionUtils.fix2ndArgument(new Multiply(), d));
    }

    /** {@inheritDoc} */
    public RealVector mapSubtract(double d) {
        return copy().mapSubtractToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapSubtractToSelf(double d){
        return mapAddToSelf(-d);
    }

    /** {@inheritDoc} */
    public RealVector mapDivide(double d) {
        return copy().mapDivideToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapDivideToSelf(double d){
        return mapToSelf(FunctionUtils.fix2ndArgument(new Divide(), d));
    }

    /** {@inheritDoc} */
    public RealMatrix outerProduct(RealVector v) {
        RealMatrix product;
        if (v instanceof SparseRealVector || this instanceof SparseRealVector) {
            product = new OpenMapRealMatrix(this.getDimension(),
                                            v.getDimension());
        } else {
            product = new Array2DRowRealMatrix(this.getDimension(),
                                               v.getDimension());
        }
        Iterator<Entry> thisIt = sparseIterator();
        Entry thisE = null;
        while (thisIt.hasNext() && (thisE = thisIt.next()) != null) {
            Iterator<Entry> otherIt = v.sparseIterator();
            Entry otherE = null;
            while (otherIt.hasNext() && (otherE = otherIt.next()) != null) {
                product.setEntry(thisE.getIndex(), otherE.getIndex(),
                                 thisE.getValue() * otherE.getValue());
            }
        }

        return product;

    }

    /** {@inheritDoc} */
    public RealMatrix outerProduct(double[] v) {
        return outerProduct(new ArrayRealVector(v, false));
    }

    /** {@inheritDoc} */
    public RealVector projection(double[] v) {
        return projection(new ArrayRealVector(v, false));
    }

    /** {@inheritDoc} */
    public void set(double value) {
        Iterator<Entry> it = iterator();
        Entry e = null;
        while (it.hasNext() && (e = it.next()) != null) {
            e.setValue(value);
        }
    }

    /** {@inheritDoc} */
    public double[] toArray() {
        int dim = getDimension();
        double[] values = new double[dim];
        for (int i = 0; i < dim; i++) {
            values[i] = getEntry(i);
        }
        return values;
    }

    /** {@inheritDoc} */
    public double[] getData() {
        return toArray();
    }

    /** {@inheritDoc} */
    public RealVector unitVector() {
        RealVector copy = copy();
        copy.unitize();
        return copy;
    }

    /** {@inheritDoc} */
    public void unitize() {
        mapDivideToSelf(getNorm());
    }

    /** {@inheritDoc} */
    public Iterator<Entry> sparseIterator() {
        return new SparseEntryIterator();
    }

    /** {@inheritDoc} */
    public Iterator<Entry> iterator() {
        final int dim = getDimension();
        return new Iterator<Entry>() {

            /** Current index. */
            private int i = 0;

            /** Current entry. */
            private EntryImpl e = new EntryImpl();

            /** {@inheritDoc} */
            public boolean hasNext() {
                return i < dim;
            }

            /** {@inheritDoc} */
            public Entry next() {
                e.setIndex(i++);
                return e;
            }

            /** {@inheritDoc} */
            public void remove() {
                throw new MathUnsupportedOperationException();
            }
        };
    }

    /** {@inheritDoc} */
    public RealVector map(UnivariateRealFunction function) {
        return copy().mapToSelf(function);
    }

    /** {@inheritDoc} */
    public RealVector mapToSelf(UnivariateRealFunction function) {
        Iterator<Entry> it = (function.value(0) == 0) ? sparseIterator() : iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            e.setValue(function.value(e.getValue()));
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector combine(double a, double b, double[] y) {
        return copy().combineToSelf(a, b, y);
    }

    /** {@inheritDoc} */
    public RealVector combine(double a, double b, RealVector y) {
        return copy().combineToSelf(a, b, y);
    }

    /** {@inheritDoc} */
    public RealVector combineToSelf(double a, double b, double[] y) {
        return combineToSelf(a, b, new ArrayRealVector(y, false));
    }

    /** {@inheritDoc} */
    public RealVector combineToSelf(double a, double b, RealVector y) {
        checkVectorDimensions(y);
        for (int i = 0; i < getDimension(); i++) {
            final double xi = getEntry(i);
            final double yi = y.getEntry(i);
            setEntry(i, a * xi + b * yi);
        }
        return this;
    }

    /** An entry in the vector. */
    protected class EntryImpl extends Entry {

        /** Simple constructor. */
        public EntryImpl() {
            setIndex(0);
        }

        /** {@inheritDoc} */
        @Override
        public double getValue() {
            return getEntry(getIndex());
        }

        /** {@inheritDoc} */
        @Override
        public void setValue(double newValue) {
            setEntry(getIndex(), newValue);
        }
    }

    /**
     * This class should rare be used, but is here to provide
     * a default implementation of sparseIterator(), which is implemented
     * by walking over the entries, skipping those whose values are the default one.
     *
     * Concrete subclasses which are SparseVector implementations should
     * make their own sparse iterator, not use this one.
     *
     * This implementation might be useful for ArrayRealVector, when expensive
     * operations which preserve the default value are to be done on the entries,
     * and the fraction of non-default values is small (i.e. someone took a
     * SparseVector, and passed it into the copy-constructor of ArrayRealVector)
     */
    protected class SparseEntryIterator implements Iterator<Entry> {

        /** Dimension of the vector. */
        private final int dim;

        /** last entry returned by {@link #next()} */
        private EntryImpl current;

        /** Next entry for {@link #next()} to return. */
        private EntryImpl next;

        /** Simple constructor. */
        protected SparseEntryIterator() {
            dim = getDimension();
            current = new EntryImpl();
            next = new EntryImpl();
            if (next.getValue() == 0) {
                advance(next);
            }
        }

        /** Advance an entry up to the next nonzero one.
         * @param e entry to advance
         */
        protected void advance(EntryImpl e) {
            if (e == null) {
                return;
            }
            do {
                e.setIndex(e.getIndex() + 1);
            } while (e.getIndex() < dim && e.getValue() == 0);
            if (e.getIndex() >= dim) {
                e.setIndex(-1);
            }
        }

        /** {@inheritDoc} */
        public boolean hasNext() {
            return next.getIndex() >= 0;
        }

        /** {@inheritDoc} */
        public Entry next() {
            int index = next.getIndex();
            if (index < 0) {
                throw new NoSuchElementException();
            }
            current.setIndex(index);
            advance(next);
            return current;
        }

        /** {@inheritDoc} */
        public void remove() {
            throw new MathUnsupportedOperationException();
        }
    }
}
