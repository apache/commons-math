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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.RealVector.Entry;
import org.apache.commons.math3.util.MathArrays;

import java.util.Iterator;
import java.util.Random;

/**
 * Tests for {@link RealVector}.
 */
public class RealVectorTest extends RealVectorAbstractTest{
    private double[] vec1 = { 1d, 2d, 3d, 4d, 5d };
    private double[] vec2 = { -3d, 0d, 0d, 2d, 1d };

    private static class TestVectorImpl extends RealVector {
        private double[] values;

        TestVectorImpl(double[] values) {
            this.values = MathArrays.copyOf(values);
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
            try {
                return values[index];
            } catch (IndexOutOfBoundsException e) {
                throw new OutOfRangeException(LocalizedFormats.INDEX, index, 0,
                    getDimension() - 1);
            }
        }

        @Override
        public void setEntry(int index, double value) {
            try {
                values[index] = value;
            } catch (IndexOutOfBoundsException e) {
                throw new OutOfRangeException(LocalizedFormats.INDEX, index, 0,
                    getDimension() - 1);
            }
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

    @Override
    public RealVector create(final double[] data) {
        return new TestVectorImpl(data);
    }

    @Override
    public RealVector createAlien(double[] data) {
        return new TestVectorImpl(data);
    }

    @Test
    @Ignore("Abstract class RealVector does not implement append(RealVector).")
    @Override
    public void testAppendVector() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement append(double)")
    @Override
    public void testAppendScalar() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement getSubvector(int, int)")
    @Override
    public void testGetSubVector() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement getSubvector(int, int)")
    @Override
    public void testGetSubVectorInvalidIndex1() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement getSubvector(int, int)")
    @Override
    public void testGetSubVectorInvalidIndex2() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement getSubvector(int, int)")
    @Override
    public void testGetSubVectorInvalidIndex3() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement getSubvector(int, int)")
    @Override
    public void testGetSubVectorInvalidIndex4() {
        // Do nothing
    }

    @Test
    @Ignore
    @Override
    public void testBasicFunctions() {
        /*
         *  TODO this test is huge, and some of the methods being tested are not
         *  supported by TestVectorImpl. For the time being, this test is skipped
         *  (testBasicFunctions() is overriden, ommitting the @Test anotation).
         *
         *  What should really be done: split testBasicFunctions() in many
         *  smaller unit tests, and skip only those tests which are not meaningfull
         *  for RealVector.
         */
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

    @Override
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

    @Test
    @Ignore
    @Override
    public void testDataInOut() {
        /*
         *  TODO Some of the tests carried out in testDataInOut() do not pass,
         *  as the methods to be tested are not implemented in TestVectorImpl.
         *  For the time being, testDataInOut() is overriden, while ommitting
         *  the @Test annotation, which effectively skips the test.
         *
         *  In the future, testDataInOut() should be split in smaller units, and
         *  only those units which do not make sense should be skipped.
         */
    }

    @Test
    @Ignore
    @Override
    public void testPredicates() {
        /*
         *  TODO Some of the tests carried out in testPredicates() do not pass,
         *  as the methods to be tested are not implemented in TestVectorImpl.
         *  For the time being, testPredicates() is overriden, while ommitting
         *  the @Test annotation, which effectively skips the test.
         *
         *  In the future, testPredicates() should be split in smaller units, and
         *  only those units which do not make sense should be skipped.
         */
    }

    @Test
    @Ignore("Abstract class RealVector is not serializable.")
    @Override
    public void testSerial() {
        // Do nothing
    }
}
