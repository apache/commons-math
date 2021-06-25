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
package org.apache.commons.math4.legacy.field.linalg;

import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.numbers.field.FP64;
import org.apache.commons.numbers.field.FP64Field;
import org.apache.commons.math4.legacy.core.Pair;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.Array2DRowRealMatrix;

/**
 * Tests for {@link FieldDenseMatrix} (using {@link FP64} as field elements).
 */
public class FP64FieldDenseMatrixTest {
    @Test
    public void testGetRowDimension() {
        final int r = 6;
        final int c = 9;
        final FieldDenseMatrix<FP64> a = FieldDenseMatrix.create(FP64Field.get(), r, c);
        Assert.assertEquals(r, a.getRowDimension());
    }

    @Test
    public void testGetColumnDimension() {
        final int r = 6;
        final int c = 9;
        final FieldDenseMatrix<FP64> a = FieldDenseMatrix.create(FP64Field.get(), r, c);
        Assert.assertEquals(c, a.getColumnDimension());
    }

    @Test
    public void testSetGet() {
        final int r = 17;
        final int c = 20;
        final FieldDenseMatrix<FP64> a = FieldDenseMatrix.create(FP64Field.get(), r, c);

        int count = 0;
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                a.set(i, j, FP64.of(count++));
            }
        }
        Assert.assertEquals(r * c, count);

        count = 0;
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                Assert.assertEquals((double) count++,
                                    a.get(i, j).doubleValue(),
                                    0d);
            }
        }
    }

    @Test
    public void testAdd() {
        final int r = 5;
        final int c = 3;
        final double scale = 1e3;
        final Pair<FieldDenseMatrix<FP64>, RealMatrix> p1 = createRandom(r, c, scale);
        final Pair<FieldDenseMatrix<FP64>, RealMatrix> p2 = createRandom(r, c, scale);

        assertEquals(p1.getFirst().add(p2.getFirst()),
                     p1.getSecond().add(p2.getSecond()),
                     0d);
    }

    @Test
    public void testSubtract() {
        final int r = 2;
        final int c = 6;
        final double scale = 1e3;
        final Pair<FieldDenseMatrix<FP64>, RealMatrix> p1 = createRandom(r, c, scale);
        final Pair<FieldDenseMatrix<FP64>, RealMatrix> p2 = createRandom(r, c, scale);

        assertEquals(p1.getFirst().subtract(p2.getFirst()),
                     p1.getSecond().subtract(p2.getSecond()),
                     0d);
    }

    @Test
    public void testMultiply() {
        final int r = 7;
        final int c1 = 4;
        final int c2 = 5;
        final double scale = 1e2;
        final Pair<FieldDenseMatrix<FP64>, RealMatrix> p1 = createRandom(r, c1, scale);
        final Pair<FieldDenseMatrix<FP64>, RealMatrix> p2 = createRandom(c1, c2, scale);

        assertEquals(p1.getFirst().multiply(p2.getFirst()),
                     p1.getSecond().multiply(p2.getSecond()),
                     0d);
    }

    @Test
    public void testNegate() {
        final int dim = 13;
        final double scale = 1;
        final Pair<FieldDenseMatrix<FP64>, RealMatrix> p = createRandom(dim, dim, scale);

        assertEquals(p.getFirst().negate(),
                     p.getSecond().scalarMultiply(-1),
                     0d);
    }

    @Test
    public void testPowZero() {
        final int dim = 5;
        final double scale = 1e100;
        final Pair<FieldDenseMatrix<FP64>, RealMatrix> p = createRandom(dim, dim, scale);

        final int exp = 0;
        assertEquals(p.getFirst().pow(exp),
                     p.getSecond().power(exp),
                     0d);
    }

    @Test
    public void testPowOne() {
        final int dim = 5;
        final double scale = 1e100;
        final Pair<FieldDenseMatrix<FP64>, RealMatrix> p = createRandom(dim, dim, scale);

        final int exp = 1;
        assertEquals(p.getFirst().pow(exp),
                     p.getSecond().power(exp),
                     0d);
    }

    @Test
    public void testPow() {
        final int dim = 5;
        final double scale = 1e2;
        final Pair<FieldDenseMatrix<FP64>, RealMatrix> p = createRandom(dim, dim, scale);

        final int exp = 4;
        assertEquals(p.getFirst().pow(exp),
                     p.getSecond().power(exp),
                     0d);
    }

    @Test
    public void testGetField() {
        final FieldDenseMatrix<FP64> a = FieldDenseMatrix.create(FP64Field.get(), 7, 5);
        Assert.assertEquals(FP64Field.get(), a.getField());
    }

    @Test
    public void testEquals() {
        // Reference equality.
        final FieldDenseMatrix<FP64> a = FieldDenseMatrix.create(FP64Field.get(), 7, 2);
        Assert.assertEquals(a, a);

        // Dimension mismatch
        final FieldDenseMatrix<FP64> b = FieldDenseMatrix.create(FP64Field.get(), 7, 3);
        Assert.assertNotEquals(a, b);
        final FieldDenseMatrix<FP64> c = FieldDenseMatrix.create(FP64Field.get(), 6, 2);
        Assert.assertNotEquals(a, c);

        // Contents.
        final FieldDenseMatrix<FP64> d = FieldDenseMatrix.create(FP64Field.get(), 7, 2);
        Assert.assertEquals(a, d); // Unitialized contents.
        a.fill(FP64.of(1.23456789));
        d.fill(FP64.of(1.23456789));
        Assert.assertEquals(a, d); // Initialized contents.
        d.set(6, 1, d.get(6, 1).add(FP64.of(1e-15)));
        Assert.assertNotEquals(a, d);
    }

    @Test
    public void testCopy() {
        final FieldDenseMatrix<FP64> a = FieldDenseMatrix.create(FP64Field.get(), 7, 3)
            .fill(FP64Field.get().one());
        final FieldDenseMatrix<FP64> b = a.copy();
        Assert.assertEquals(a, b);

        b.set(0, 0, FP64Field.get().zero());
        Assert.assertNotEquals(a, b);
    }

    @Test
    public void testTranspose() {
        final int r = 4;
        final int c = 5;
        final FieldDenseMatrix<FP64> a = FieldDenseMatrix.create(FP64Field.get(), r, c);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                final double j2 = j * j;
                a.set(i, j,
                      FP64.of(1.2 * i + 3.4 * j2));
            }
        }

        final FieldDenseMatrix<FP64> b = a.transpose();
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                Assert.assertEquals(a.get(i, j), b.get(j, i));
            }
        }

        Assert.assertEquals(a, b.transpose());
    }

    @Test
    public void testIdentity() {
        final int dim = 3;
        final FieldDenseMatrix<FP64> a = FieldDenseMatrix.identity(FP64Field.get(), dim);
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (i == j) {
                    Assert.assertEquals(FP64Field.get().one(),                                        a.get(i, j));
                } else {
                    Assert.assertEquals(FP64Field.get().zero(),
                                        a.get(i, j));
                }
            }
        }
    }

    /**
     * Compares with result obtained from "Commons Math".
     *
     * @param a "o.a.c.m.field.linalg" result.
     * @param b "o.a.c.m.linear" result.
     * @param tol Tolerance.
     */
    private void assertEquals(FieldDenseMatrix<FP64> a,
                              RealMatrix b,
                              double tol) {
        if (a.getRowDimension() != b.getRowDimension() ||
            a.getColumnDimension() != b.getColumnDimension()) {
            Assert.fail("Dimension mismatch");
        }

        for (int i = 0; i < a.getRowDimension(); i++) {
            for (int j = 0; j < a.getColumnDimension(); j++) {
                Assert.assertEquals("(" + i + ", " + j + ")",
                                    a.get(i, j).doubleValue(),
                                    b.getEntry(i, j),
                                    tol);
            }
        }
    }

    /**
     * Creates test matrices with random entries.
     *
     * @param r Rows.
     * @param c Columns.
     * @param scale Range of the entries.
     * @return a pair of matrices whose entries are in the interval
     * {@code [-scale, scale]}.
     */
    private Pair<FieldDenseMatrix<FP64>, RealMatrix> createRandom(int r,
                                                                  int c,
                                                                  double scale) {
        final FieldDenseMatrix<FP64> a = FieldDenseMatrix.create(FP64Field.get(), r, c);
        final RealMatrix b = new Array2DRowRealMatrix(r, c);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                final double v = scale * (2 * Math.random() - 1);
                a.set(i, j, FP64.of(v));
                b.setEntry(i, j, v);
            }
        }

        return new Pair<>(a, b);
    }
}
