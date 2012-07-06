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

import java.util.Iterator;
import java.util.Random;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.RealVector.Entry;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for {@link RealVector}.
 */
public class RealVectorTest extends RealVectorAbstractTest{

    @Override
    public RealVector create(final double[] data) {
        return new RealVectorTestImpl(data);
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
    @Ignore("Abstract class RealVector does not implement setSubvector(int, RealVector)")
    @Override
    public void testSetSubVectorSameType() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement setSubvector(int, RealVector)")
    @Override
    public void testSetSubVectorMixedType() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement setSubvector(int, RealVector)")
    @Override
    public void testSetSubVectorInvalidIndex1() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement setSubvector(int, RealVector)")
    @Override
    public void testSetSubVectorInvalidIndex2() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement setSubvector(int, RealVector)")
    @Override
    public void testSetSubVectorInvalidIndex3() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement isNaN()")
    @Override
    public void testIsNaN() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement isNaN()")
    @Override
    public void testIsInfinite() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement ebeMultiply(RealVector)")
    @Override
    public void testEbeMultiplySameType() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement ebeMultiply(RealVector)")
    @Override
    public void testEbeMultiplyMixedTypes() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement ebeMultiply(RealVector)")
    @Override
    public void testEbeMultiplyDimensionMismatch() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement ebeDivide(RealVector)")
    @Override
    public void testEbeDivideSameType() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement ebeDivide(RealVector)")
    @Override
    public void testEbeDivideMixedTypes() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement ebeDivide(RealVector)")
    @Override
    public void testEbeDivideDimensionMismatch() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement getL1Norm()")
    @Override
    public void testGetL1Norm() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not implement getLInfNorm()")
    @Override
    public void testGetLInfNorm() {
        // Do nothing
    }

    @Test
    public void testSparseIterator() throws Exception {
        /*
         * For non-default values, use x + 1, x + 2, etc... to make sure that
         * these values are really different from x.
         */
        final double x = getPreferredEntryValue();
        final double[] data = {
            x, x + 1d, x, x, x + 2d, x + 3d, x + 4d, x, x, x, x + 5d, x + 6d, x
        };

        RealVector v = create(data);
        Entry e;
        int i = 0;
        final double[] nonDefault = {
            x + 1d, x + 2d, x + 3d, x + 4d, x + 5d, x + 6d
        };
        for (Iterator<Entry> it = v.sparseIterator(); it.hasNext(); i++) {
            e = it.next();
            Assert.assertEquals(nonDefault[i], e.getValue(), 0);
        }
        double [] onlyOne = {x, x + 1d, x};
        v = create(onlyOne);
        for(Iterator<Entry> it = v.sparseIterator(); it.hasNext(); ) {
            e = it.next();
            Assert.assertEquals(onlyOne[1], e.getValue(), 0);
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCombineToSelfPrecondition() {
        final double a = 1d;
        final double b = 2d;
        double[] aux = new double[] { 3d, 4d, 5d };
        final RealVector x = new RealVectorTestImpl(aux);
        aux = new double[] { 6d, 7d };
        final RealVector y = new RealVectorTestImpl(aux);
        x.combineToSelf(a, b, y);
    }

    @Test
    public void testCombineToSelf() {
        final Random random = new Random(20110726);
        final int dim = 10;
        final double a = (2 * random.nextDouble() - 1);
        final double b = (2 * random.nextDouble() - 1);
        final RealVector x = new RealVectorTestImpl(new double[dim]);
        final RealVector y = new RealVectorTestImpl(new double[dim]);
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
        final RealVector x = new RealVectorTestImpl(v.clone());
        final double inc = 7;
        for (int i = 0; i < x.getDimension(); i++) {
            x.addToEntry(i, inc);
        }
        for (int i = 0; i < x.getDimension(); i++) {
            Assert.assertEquals(v[i] + inc, x.getEntry(i), 0);
        }
    }

    @Test
    @Ignore("Abstract class RealVector is not serializable.")
    @Override
    public void testSerial() {
        // Do nothing
    }

    @Test
    @Ignore("Abstract class RealVector does not override equals(Object).")
    @Override
    public void testEquals() {
        // Do nothing
    }
}
