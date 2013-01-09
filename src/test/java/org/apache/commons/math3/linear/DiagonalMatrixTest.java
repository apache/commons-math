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

import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.TestUtils;

/**
 * Test cases for the {@link DiagonalMatrix} class.
 */
public class DiagonalMatrixTest {
    @Test
    public void testConstructor1() {
        final int dim = 3;
        final DiagonalMatrix m = new DiagonalMatrix(dim);
        Assert.assertEquals(dim, m.getRowDimension());
        Assert.assertEquals(dim, m.getColumnDimension());
    }

    @Test
    public void testConstructor2() {
        final double[] d = { -1.2, 3.4, 5 };
        final DiagonalMatrix m = new DiagonalMatrix(d);
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = 0; j < m.getRowDimension(); j++) {
                if (i == j) {
                    Assert.assertEquals(d[i], m.getEntry(i, j), 0d);
                } else {
                    Assert.assertEquals(0d, m.getEntry(i, j), 0d);
                }
            }
        }

        // Check that the underlying was copied.
        d[0] = 0;
        Assert.assertFalse(d[0] == m.getEntry(0, 0));
    }

    @Test
    public void testConstructor3() {
        final double[] d = { -1.2, 3.4, 5 };
        final DiagonalMatrix m = new DiagonalMatrix(d, false);
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = 0; j < m.getRowDimension(); j++) {
                if (i == j) {
                    Assert.assertEquals(d[i], m.getEntry(i, j), 0d);
                } else {
                    Assert.assertEquals(0d, m.getEntry(i, j), 0d);
                }
            }
        }

        // Check that the underlying is referenced.
        d[0] = 0;
        Assert.assertTrue(d[0] == m.getEntry(0, 0));

    }

    @Test
    public void testGetData() {
        final double[] data = { -1.2, 3.4, 5 };
        final int dim = 3;
        final DiagonalMatrix m = new DiagonalMatrix(dim);
        for (int i = 0; i < dim; i++) {
            m.setEntry(i, i, data[i]);
        }

        final double[][] out = m.getData();
        Assert.assertEquals(dim, out.length);
        for (int i = 0; i < m.getRowDimension(); i++) {
            Assert.assertEquals(dim, out[i].length);
            for (int j = 0; j < m.getRowDimension(); j++) {
                if (i == j) {
                    Assert.assertEquals(data[i], out[i][j], 0d);
                } else {
                    Assert.assertEquals(0d, out[i][j], 0d);
                }
            }
        }        
    }

    @Test
    public void testAdd() {
        final double[] data1 = { -1.2, 3.4, 5 };
        final DiagonalMatrix m1 = new DiagonalMatrix(data1);
 
        final double[] data2 = { 10.1, 2.3, 45 };
        final DiagonalMatrix m2 = new DiagonalMatrix(data2);

        final DiagonalMatrix result = m1.add(m2);
        Assert.assertEquals(m1.getRowDimension(), result.getRowDimension());
        for (int i = 0; i < result.getRowDimension(); i++) {
            for (int j = 0; j < result.getRowDimension(); j++) {
                if (i == j) {
                    Assert.assertEquals(data1[i] + data2[i], result.getEntry(i, j), 0d);
                } else {
                    Assert.assertEquals(0d, result.getEntry(i, j), 0d);
                }
            }
        }
    }

    @Test
    public void testAddToEntry() {
        final double[] data = { -1.2, 3.4, 5 };
        final DiagonalMatrix m = new DiagonalMatrix(data);

        for (int i = 0; i < m.getRowDimension(); i++) {
            m.addToEntry(i, i, i);
            Assert.assertEquals(data[i] + i, m.getEntry(i, i), 0d);
        }
    }

    @Test
    public void testMultiplyEntry() {
        final double[] data = { -1.2, 3.4, 5 };
        final DiagonalMatrix m = new DiagonalMatrix(data);

        for (int i = 0; i < m.getRowDimension(); i++) {
            m.multiplyEntry(i, i, i);
            Assert.assertEquals(data[i] * i, m.getEntry(i, i), 0d);
        }
    }

    @Test
    public void testMultiply1() {
        final double[] data1 = { -1.2, 3.4, 5 };
        final DiagonalMatrix m1 = new DiagonalMatrix(data1);
        final double[] data2 = { 10.1, 2.3, 45 };
        final DiagonalMatrix m2 = new DiagonalMatrix(data2);

        final DiagonalMatrix result = m1.multiply(m2);
        Assert.assertEquals(m1.getRowDimension(), result.getRowDimension());
        for (int i = 0; i < result.getRowDimension(); i++) {
            for (int j = 0; j < result.getRowDimension(); j++) {
                if (i == j) {
                    Assert.assertEquals(data1[i] * data2[i], result.getEntry(i, j), 0d);
                } else {
                    Assert.assertEquals(0d, result.getEntry(i, j), 0d);
                }
            }
        }
    }

    @Test
    public void testMultiply2() {
        final double[] data1 = { -1.2, 3.4, 5 };
        final DiagonalMatrix diag1 = new DiagonalMatrix(data1);

        final double[][] data2 = { { -1.2, 3.4 },
                                   { -5.6, 7.8 },
                                   {  9.1, 2.3 } };
        final RealMatrix dense2 = new Array2DRowRealMatrix(data2);
        final RealMatrix dense1 = new Array2DRowRealMatrix(diag1.getData());

        final RealMatrix diagResult = diag1.multiply(dense2);
        final RealMatrix denseResult = dense1.multiply(dense2);

        for (int i = 0; i < dense1.getRowDimension(); i++) {
            for (int j = 0; j < dense2.getColumnDimension(); j++) {
                Assert.assertEquals(denseResult.getEntry(i, j),
                                    diagResult.getEntry(i, j), 0d);
            }
        }
    }

    @Test
    public void testOperate() {
        final double[] data = { -1.2, 3.4, 5 };
        final DiagonalMatrix diag = new DiagonalMatrix(data);
        final RealMatrix dense = new Array2DRowRealMatrix(diag.getData());

        final double[] v = { 6.7, 890.1, 23.4 };
        final double[] diagResult = diag.operate(v);
        final double[] denseResult = dense.operate(v);

        TestUtils.assertEquals(diagResult, denseResult, 0d);
    }

    @Test
    public void testPreMultiply() {
        final double[] data = { -1.2, 3.4, 5 };
        final DiagonalMatrix diag = new DiagonalMatrix(data);
        final RealMatrix dense = new Array2DRowRealMatrix(diag.getData());

        final double[] v = { 6.7, 890.1, 23.4 };
        final double[] diagResult = diag.preMultiply(v);
        final double[] denseResult = dense.preMultiply(v);

        TestUtils.assertEquals(diagResult, denseResult, 0d);
    }

    @Test(expected=MathUnsupportedOperationException.class)
    public void testSetNonDiagonalEntry() {
        final DiagonalMatrix diag = new DiagonalMatrix(3);
        diag.setEntry(1, 2, 3.4);
    }

    @Test(expected=OutOfRangeException.class)
    public void testSetEntryOutOfRange() {
        final DiagonalMatrix diag = new DiagonalMatrix(3);
        diag.setEntry(3, 3, 3.4);
    }
}
