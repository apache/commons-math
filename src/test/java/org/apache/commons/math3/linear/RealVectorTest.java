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

package org.apache.commons.math3.linear;

import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.RealVector.Entry;
import java.util.Iterator;
import java.util.Random;

/**
 * Tests for {@link RealVector}.
 */
public class RealVectorTest {
    private double[] vec1 = { 1d, 2d, 3d, 4d, 5d };
    private double[] vec2 = { -3d, 0d, 0d, 2d, 1d };

    private static class TestVectorImpl extends RealVector {
        private double[] values;

        TestVectorImpl(double[] values) {
            this.values = values;
        }

        @Override
        public double[] toArray() { return values; }

        @Override
        public RealVector copy() {
            return new TestVectorImpl(values.clone());
        }

        UnsupportedOperationException unsupported() {
            return new UnsupportedOperationException("Test implementation only supports methods necessary for testing");
        }

        @Override
        public RealVector add(RealVector v) {
            RealVector result = new ArrayRealVector(v);
            return result.add(this);
        }

        @Override
        public RealVector subtract(RealVector v) {
            RealVector result = new ArrayRealVector(v);
            return result.subtract(this).mapMultiplyToSelf(-1);
        }

        @Override
        public RealVector mapAddToSelf(double d) {
            for(int i=0; i<values.length; i++) {
                values[i] += d;
            }
            return this;
        }

        @Override
        public RealVector mapSubtractToSelf(double d) {
            for(int i=0; i<values.length; i++) {
                values[i] -= d;
            }
            return this;
        }

        @Override
        public RealVector mapMultiplyToSelf(double d) {
            for(int i=0; i<values.length; i++) {
                values[i] *= d;
            }
            return this;
        }

        @Override
        public RealVector mapDivideToSelf(double d) {
            for(int i=0; i<values.length; i++) {
                values[i] /= d;
            }
            return this;
        }

        @Override
        public RealVector ebeMultiply(RealVector v) {
            throw unsupported();
        }

        @Override
        public RealVector ebeDivide(RealVector v) {
            throw unsupported();
        }

        @Override
        public double dotProduct(RealVector v) {
            throw unsupported();
        }

        @Override
        public double getNorm() {
            throw unsupported();
        }

        @Override
        public double getL1Norm() {
            throw unsupported();
        }

        @Override
        public double getLInfNorm() {
            throw unsupported();
        }

        @Override
        public RealVector projection(RealVector v) {
            throw unsupported();
        }

        @Override
        public double getEntry(int index) {
            return values[index];
        }

        @Override
        public void setEntry(int index, double value) {
            values[index] = value;
        }

        @Override
        public int getDimension() {
            return values.length;
        }

        @Override
        public RealVector append(RealVector v) {
            throw unsupported();
        }

        @Override
        public RealVector append(double d) {
            throw unsupported();
        }

        public RealVector append(double[] a) {
            throw unsupported();
        }

        @Override
        public RealVector getSubVector(int index, int n) {
            throw unsupported();
        }

        public void setSubVector(int index, double[] v) {
            throw unsupported();
        }
        @Override
        public void setSubVector(int index, RealVector v) {
            throw unsupported();
        }

        @Override
        public boolean isNaN() {
            throw unsupported();
        }

        @Override
        public boolean isInfinite() {
            throw unsupported();
        }
    }

    @Test
    public void testMap() throws Exception {
        double[] vec1Squared = { 1d, 4d, 9d, 16d, 25d };
        RealVector v = new TestVectorImpl(vec1.clone());
        RealVector w = v.map(new UnivariateFunction() { public double value(double x) { return x * x; } });
        double[] d2 = w.toArray();
        Assert.assertEquals(vec1Squared.length, d2.length);
        for(int i=0; i<vec1Squared.length; i++) {
            Assert.assertEquals(vec1Squared[i], d2[i], 0);
        }
    }

    @Test
    public void testIterator() throws Exception {
        RealVector v = new TestVectorImpl(vec2.clone());
        Entry e;
        int i = 0;
        for(Iterator<Entry> it = v.iterator(); it.hasNext() && (e = it.next()) != null; i++) {
            Assert.assertEquals(vec2[i], e.getValue(), 0);
        }
    }

    @Test
    public void testSparseIterator() throws Exception {
        RealVector v = new TestVectorImpl(vec2.clone());
        Entry e;
        int i = 0;
        double[] nonDefaultV2 = { -3d, 2d, 1d };
        for(Iterator<Entry> it = v.sparseIterator(); it.hasNext() && (e = it.next()) != null; i++) {
            Assert.assertEquals(nonDefaultV2[i], e.getValue(), 0);
        }
        double [] onlyOne = {0d, 1.0, 0d};
        v = new TestVectorImpl(onlyOne);
        for(Iterator<Entry> it = v.sparseIterator(); it.hasNext() && (e = it.next()) != null; ) {
            Assert.assertEquals(onlyOne[1], e.getValue(), 0);
        }

    }

    @Test
    public void testClone() throws Exception {
        double[] d = new double[1000000];
        Random r = new Random(1234);
        for(int i=0;i<d.length; i++) d[i] = r.nextDouble();
        Assert.assertTrue(new ArrayRealVector(d).getNorm() > 0);
        double[] c = d.clone();
        c[0] = 1;
        Assert.assertNotSame(c[0], d[0]);
        d[0] = 1;
        Assert.assertEquals(new ArrayRealVector(d).getNorm(), new ArrayRealVector(c).getNorm(), 0);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCombinePrecondition() {
        final double a = 1d;
        final double b = 2d;
        double[] aux = new double[] { 3d, 4d, 5d };
        final TestVectorImpl x = new TestVectorImpl(aux);
        aux = new double[] { 6d, 7d };
        final TestVectorImpl y = new TestVectorImpl(aux);
        x.combine(a, b, y);
    }

    @Test
    public void testCombine() {
        final Random random = new Random(20110726);
        final int dim = 10;
        final double a = (2 * random.nextDouble() - 1);
        final double b = (2 * random.nextDouble() - 1);
        final RealVector x = new TestVectorImpl(new double[dim]);
        final RealVector y = new TestVectorImpl(new double[dim]);
        final double[] expected = new double[dim];
        for (int i = 0; i < dim; i++) {
            final double xi = 2 * random.nextDouble() - 1;
            final double yi = 2 * random.nextDouble() - 1;
            x.setEntry(i, xi);
            y.setEntry(i, yi);
            expected[i] = a * xi + b * yi;
        }
        final RealVector z = x.combine(a, b, y);
        Assert.assertTrue(z != x);
        final double[] actual = z.toArray();
        for (int i = 0; i < dim; i++) {
            final double delta;
            if (expected[i] == 0d) {
                delta = Math.ulp(1d);
            } else {
                delta = Math.ulp(expected[i]);
            }
            Assert.assertEquals("elements [" + i + "] differ", expected[i],
                                actual[i], delta);
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCombineToSelfPrecondition() {
        final double a = 1d;
        final double b = 2d;
        double[] aux = new double[] { 3d, 4d, 5d };
        final TestVectorImpl x = new TestVectorImpl(aux);
        aux = new double[] { 6d, 7d };
        final TestVectorImpl y = new TestVectorImpl(aux);
        x.combineToSelf(a, b, y);
    }

    @Test
    public void testCombineToSelf() {
        final Random random = new Random(20110726);
        final int dim = 10;
        final double a = (2 * random.nextDouble() - 1);
        final double b = (2 * random.nextDouble() - 1);
        final RealVector x = new TestVectorImpl(new double[dim]);
        final RealVector y = new TestVectorImpl(new double[dim]);
        final double[] expected = new double[dim];
        for (int i = 0; i < dim; i++) {
            final double xi = 2 * random.nextDouble() - 1;
            final double yi = 2 * random.nextDouble() - 1;
            x.setEntry(i, xi);
            y.setEntry(i, yi);
            expected[i] = a * xi + b * yi;
        }
        Assert.assertSame(x, x.combineToSelf(a, b, y));
        final double[] actual = x.toArray();
        for (int i = 0; i < dim; i++) {
            final double delta;
            if (expected[i] == 0d) {
                delta = Math.ulp(1d);
            } else {
                delta = Math.ulp(expected[i]);
            }
            Assert.assertEquals("elements [" + i + "] differ", expected[i],
                                actual[i], delta);
        }
    }

    @Test
    public void testAddToEntry() {
        final double[] v = new double[] { 1, 2, 3 };
        final RealVector x = new TestVectorImpl(v.clone());
        final double inc = 7;
        for (int i = 0; i < x.getDimension(); i++) {
            x.addToEntry(i, inc);
        }
        for (int i = 0; i < x.getDimension(); i++) {
            Assert.assertEquals(v[i] + inc, x.getEntry(i), 0);
        }
    }
}
