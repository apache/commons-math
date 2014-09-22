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


import java.util.Arrays;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionConversionException;
import org.apache.commons.math3.fraction.FractionField;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for the {@link SparseFieldVector} class.
 *
 */
public class SparseFieldVectorTest {

    //
    protected Fraction[][] ma1 = {{new Fraction(1), new Fraction(2), new Fraction(3)}, {new Fraction(4), new Fraction(5), new Fraction(6)}, {new Fraction(7), new Fraction(8), new Fraction(9)}};
    protected Fraction[] vec1 = {new Fraction(1), new Fraction(2), new Fraction(3)};
    protected Fraction[] vec2 = {new Fraction(4), new Fraction(5), new Fraction(6)};
    protected Fraction[] vec3 = {new Fraction(7), new Fraction(8), new Fraction(9)};
    protected Fraction[] vec4 = {new Fraction(1), new Fraction(2), new Fraction(3), new Fraction(4), new Fraction(5), new Fraction(6), new Fraction(7), new Fraction(8), new Fraction(9)};
    protected Fraction[] vec_null = {new Fraction(0), new Fraction(0), new Fraction(0)};
    protected Fraction[] dvec1 = {new Fraction(1), new Fraction(2), new Fraction(3), new Fraction(4), new Fraction(5), new Fraction(6), new Fraction(7), new Fraction(8),new Fraction(9)};
    protected Fraction[][] mat1 = {{new Fraction(1), new Fraction(2), new Fraction(3)}, {new Fraction(4), new Fraction(5), new Fraction(6)},{ new Fraction(7), new Fraction(8), new Fraction(9)}};

    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;

    protected FractionField field = FractionField.getInstance();

    @Test
    public void testMapFunctions() throws FractionConversionException {
        SparseFieldVector<Fraction> v1 = new SparseFieldVector<Fraction>(field,vec1);

        //octave =  v1 .+ 2.0
        FieldVector<Fraction> v_mapAdd = v1.mapAdd(new Fraction(2));
        Fraction[] result_mapAdd = {new Fraction(3), new Fraction(4), new Fraction(5)};
        Assert.assertArrayEquals("compare vectors" ,result_mapAdd,v_mapAdd.toArray());

        //octave =  v1 .+ 2.0
        FieldVector<Fraction> v_mapAddToSelf = v1.copy();
        v_mapAddToSelf.mapAddToSelf(new Fraction(2));
        Fraction[] result_mapAddToSelf = {new Fraction(3), new Fraction(4), new Fraction(5)};
        Assert.assertArrayEquals("compare vectors" ,result_mapAddToSelf,v_mapAddToSelf.toArray());

        //octave =  v1 .- 2.0
        FieldVector<Fraction> v_mapSubtract = v1.mapSubtract(new Fraction(2));
        Fraction[] result_mapSubtract = {new Fraction(-1), new Fraction(0), new Fraction(1)};
        Assert.assertArrayEquals("compare vectors" ,result_mapSubtract,v_mapSubtract.toArray());

        //octave =  v1 .- 2.0
        FieldVector<Fraction> v_mapSubtractToSelf = v1.copy();
        v_mapSubtractToSelf.mapSubtractToSelf(new Fraction(2));
        Fraction[] result_mapSubtractToSelf = {new Fraction(-1), new Fraction(0), new Fraction(1)};
        Assert.assertArrayEquals("compare vectors" ,result_mapSubtractToSelf,v_mapSubtractToSelf.toArray());

        //octave =  v1 .* 2.0
        FieldVector<Fraction> v_mapMultiply = v1.mapMultiply(new Fraction(2));
        Fraction[] result_mapMultiply = {new Fraction(2), new Fraction(4), new Fraction(6)};
        Assert.assertArrayEquals("compare vectors" ,result_mapMultiply,v_mapMultiply.toArray());

        //octave =  v1 .* 2.0
        FieldVector<Fraction> v_mapMultiplyToSelf = v1.copy();
        v_mapMultiplyToSelf.mapMultiplyToSelf(new Fraction(2));
        Fraction[] result_mapMultiplyToSelf = {new Fraction(2), new Fraction(4), new Fraction(6)};
        Assert.assertArrayEquals("compare vectors" ,result_mapMultiplyToSelf,v_mapMultiplyToSelf.toArray());

        //octave =  v1 ./ 2.0
        FieldVector<Fraction> v_mapDivide = v1.mapDivide(new Fraction(2));
        Fraction[] result_mapDivide = {new Fraction(.5d), new Fraction(1), new Fraction(1.5d)};
        Assert.assertArrayEquals("compare vectors" ,result_mapDivide,v_mapDivide.toArray());

        //octave =  v1 ./ 2.0
        FieldVector<Fraction> v_mapDivideToSelf = v1.copy();
        v_mapDivideToSelf.mapDivideToSelf(new Fraction(2));
        Fraction[] result_mapDivideToSelf = {new Fraction(.5d), new Fraction(1), new Fraction(1.5d)};
        Assert.assertArrayEquals("compare vectors" ,result_mapDivideToSelf,v_mapDivideToSelf.toArray());

        //octave =  v1 .^-1
        FieldVector<Fraction> v_mapInv = v1.mapInv();
        Fraction[] result_mapInv = {new Fraction(1),new Fraction(0.5d),new Fraction(3.333333333333333e-01d)};
        Assert.assertArrayEquals("compare vectors" ,result_mapInv,v_mapInv.toArray());

        //octave =  v1 .^-1
        FieldVector<Fraction> v_mapInvToSelf = v1.copy();
        v_mapInvToSelf.mapInvToSelf();
        Fraction[] result_mapInvToSelf = {new Fraction(1),new Fraction(0.5d),new Fraction(3.333333333333333e-01d)};
        Assert.assertArrayEquals("compare vectors" ,result_mapInvToSelf,v_mapInvToSelf.toArray());


    }

    @Test
    public void testBasicFunctions() throws FractionConversionException {
        SparseFieldVector<Fraction> v1 = new SparseFieldVector<Fraction>(field,vec1);
        SparseFieldVector<Fraction> v2 = new SparseFieldVector<Fraction>(field,vec2);

        FieldVector<Fraction> v2_t = new ArrayFieldVectorTest.FieldVectorTestImpl<Fraction>(vec2);

        //octave =  v1 + v2
        FieldVector<Fraction> v_add = v1.add(v2);
        Fraction[] result_add = {new Fraction(5), new Fraction(7), new Fraction(9)};
        Assert.assertArrayEquals("compare vect" ,v_add.toArray(),result_add);

        FieldVector<Fraction> vt2 = new ArrayFieldVectorTest.FieldVectorTestImpl<Fraction>(vec2);
        FieldVector<Fraction> v_add_i = v1.add(vt2);
        Fraction[] result_add_i = {new Fraction(5), new Fraction(7), new Fraction(9)};
        Assert.assertArrayEquals("compare vect" ,v_add_i.toArray(),result_add_i);

        //octave =  v1 - v2
        SparseFieldVector<Fraction> v_subtract = v1.subtract(v2);
        Fraction[] result_subtract = {new Fraction(-3), new Fraction(-3), new Fraction(-3)};
        assertClose("compare vect" ,v_subtract.toArray(),result_subtract,normTolerance);

        FieldVector<Fraction> v_subtract_i = v1.subtract(vt2);
        Fraction[] result_subtract_i = {new Fraction(-3), new Fraction(-3), new Fraction(-3)};
        assertClose("compare vect" ,v_subtract_i.toArray(),result_subtract_i,normTolerance);

        // octave v1 .* v2
        FieldVector<Fraction>  v_ebeMultiply = v1.ebeMultiply(v2);
        Fraction[] result_ebeMultiply = {new Fraction(4), new Fraction(10), new Fraction(18)};
        assertClose("compare vect" ,v_ebeMultiply.toArray(),result_ebeMultiply,normTolerance);

        FieldVector<Fraction>  v_ebeMultiply_2 = v1.ebeMultiply(v2_t);
        Fraction[] result_ebeMultiply_2 = {new Fraction(4), new Fraction(10), new Fraction(18)};
        assertClose("compare vect" ,v_ebeMultiply_2.toArray(),result_ebeMultiply_2,normTolerance);

        // octave v1 ./ v2
        FieldVector<Fraction>  v_ebeDivide = v1.ebeDivide(v2);
        Fraction[] result_ebeDivide = {new Fraction(0.25d), new Fraction(0.4d), new Fraction(0.5d)};
        assertClose("compare vect" ,v_ebeDivide.toArray(),result_ebeDivide,normTolerance);

        FieldVector<Fraction>  v_ebeDivide_2 = v1.ebeDivide(v2_t);
        Fraction[] result_ebeDivide_2 = {new Fraction(0.25d), new Fraction(0.4d), new Fraction(0.5d)};
        assertClose("compare vect" ,v_ebeDivide_2.toArray(),result_ebeDivide_2,normTolerance);

        // octave  dot(v1,v2)
        Fraction dot =  v1.dotProduct(v2);
        Assert.assertEquals("compare val ",new Fraction(32), dot);

        // octave  dot(v1,v2_t)
        Fraction dot_2 =  v1.dotProduct(v2_t);
        Assert.assertEquals("compare val ",new Fraction(32), dot_2);

        FieldMatrix<Fraction> m_outerProduct = v1.outerProduct(v2);
        Assert.assertEquals("compare val ",new Fraction(4), m_outerProduct.getEntry(0,0));

        FieldMatrix<Fraction> m_outerProduct_2 = v1.outerProduct(v2_t);
        Assert.assertEquals("compare val ",new Fraction(4), m_outerProduct_2.getEntry(0,0));

    }

    @Test
    public void testOuterProduct() {
        final SparseFieldVector<Fraction> u
            = new SparseFieldVector<Fraction>(FractionField.getInstance(),
                                              new Fraction[] {new Fraction(1),
                                                              new Fraction(2),
                                                              new Fraction(-3)});
        final SparseFieldVector<Fraction> v
            = new SparseFieldVector<Fraction>(FractionField.getInstance(),
                                              new Fraction[] {new Fraction(4),
                                                              new Fraction(-2)});

        final FieldMatrix<Fraction> uv = u.outerProduct(v);

        final double tol = Math.ulp(1d);
        Assert.assertEquals(new Fraction(4).doubleValue(), uv.getEntry(0, 0).doubleValue(), tol);
        Assert.assertEquals(new Fraction(-2).doubleValue(), uv.getEntry(0, 1).doubleValue(), tol);
        Assert.assertEquals(new Fraction(8).doubleValue(), uv.getEntry(1, 0).doubleValue(), tol);
        Assert.assertEquals(new Fraction(-4).doubleValue(), uv.getEntry(1, 1).doubleValue(), tol);
        Assert.assertEquals(new Fraction(-12).doubleValue(), uv.getEntry(2, 0).doubleValue(), tol);
        Assert.assertEquals(new Fraction(6).doubleValue(), uv.getEntry(2, 1).doubleValue(), tol);
    }

    @Test
    public void testMisc() {
        SparseFieldVector<Fraction> v1 = new SparseFieldVector<Fraction>(field,vec1);

        String out1 = v1.toString();
        Assert.assertTrue("some output ",  out1.length()!=0);
        try {
            v1.checkVectorDimensions(2);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }


    }

    @Test
    public void testPredicates() {

        SparseFieldVector<Fraction> v = new SparseFieldVector<Fraction>(field, new Fraction[] { new Fraction(0), new Fraction(1), new Fraction(2) });

        v.setEntry(0, field.getZero());
        Assert.assertEquals(v, new SparseFieldVector<Fraction>(field, new Fraction[] { new Fraction(0), new Fraction(1), new Fraction(2) }));
        Assert.assertNotSame(v, new SparseFieldVector<Fraction>(field, new Fraction[] { new Fraction(0), new Fraction(1), new Fraction(2), new Fraction(3) }));

    }

    /** verifies that two vectors are close (sup norm) */
    protected void assertEquals(String msg, Fraction[] m, Fraction[] n) {
        if (m.length != n.length) {
            Assert.fail("vectors have different lengths");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(msg + " " +  i + " elements differ", m[i],n[i]);
        }
    }

    /** verifies that two vectors are close (sup norm) */
    protected void assertClose(String msg, Fraction[] m, Fraction[] n, double tolerance) {
        if (m.length != n.length) {
            Assert.fail("vectors have different lengths");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(msg + " " +  i + " elements differ", m[i].doubleValue(),n[i].doubleValue(), tolerance);
        }
    }

    /*
     * TESTS OF THE VISITOR PATTERN
     */

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor1() {
        final Fraction[] data = new Fraction[] {
            Fraction.ZERO, Fraction.ONE, Fraction.ZERO,
            Fraction.ZERO, Fraction.TWO, Fraction.ZERO,
            Fraction.ZERO, Fraction.ZERO, new Fraction(3)
        };
        final SparseFieldVector<Fraction> v = new SparseFieldVector<Fraction>(field, data);
        final FieldVectorPreservingVisitor<Fraction> visitor;
        visitor = new FieldVectorPreservingVisitor<Fraction>() {

            private int expectedIndex;

            public void visit(final int actualIndex, final Fraction actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            public Fraction end() {
                return Fraction.ZERO;
            }
        };
        v.walkInDefaultOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor2() {
        final SparseFieldVector<Fraction> v = create(5);
        final FieldVectorPreservingVisitor<Fraction> visitor;
        visitor = new FieldVectorPreservingVisitor<Fraction>() {

            public void visit(int index, Fraction value) {
                // Do nothing
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public Fraction end() {
                return Fraction.ZERO;
            }
        };
        try {
            v.walkInDefaultOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor3() {
        final Fraction[] data = new Fraction[] {
            Fraction.ZERO, Fraction.ONE, Fraction.ZERO,
            Fraction.ZERO, Fraction.TWO, Fraction.ZERO,
            Fraction.ZERO, Fraction.ZERO, new Fraction(3)
        };
        final SparseFieldVector<Fraction> v = new SparseFieldVector<Fraction>(field, data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorPreservingVisitor<Fraction> visitor;
        visitor = new FieldVectorPreservingVisitor<Fraction>() {

            private int expectedIndex;

            public void visit(final int actualIndex, final Fraction actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            public Fraction end() {
                return Fraction.ZERO;
            }
        };
        v.walkInDefaultOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor1() {
        final Fraction[] data = new Fraction[] {
            Fraction.ZERO, Fraction.ONE, Fraction.ZERO,
            Fraction.ZERO, Fraction.TWO, Fraction.ZERO,
            Fraction.ZERO, Fraction.ZERO, new Fraction(3)
        };
        final SparseFieldVector<Fraction> v = new SparseFieldVector<Fraction>(field, data);
        final FieldVectorPreservingVisitor<Fraction> visitor;
        visitor = new FieldVectorPreservingVisitor<Fraction>() {
            private final boolean[] visited = new boolean[data.length];

            public void visit(final int actualIndex, final Fraction actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            public Fraction end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Fraction.ZERO;
            }
        };
        v.walkInOptimizedOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor2() {
        final SparseFieldVector<Fraction> v = create(5);
        final FieldVectorPreservingVisitor<Fraction> visitor;
        visitor = new FieldVectorPreservingVisitor<Fraction>() {

            public void visit(int index, Fraction value) {
                // Do nothing
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public Fraction end() {
                return Fraction.ZERO;
            }
        };
        try {
            v.walkInOptimizedOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor3() {
        final Fraction[] data = new Fraction[] {
            Fraction.ZERO, Fraction.ONE, Fraction.ZERO,
            Fraction.ZERO, Fraction.TWO, Fraction.ZERO,
            Fraction.ZERO, Fraction.ZERO, new Fraction(3)
        };
        final SparseFieldVector<Fraction> v = new SparseFieldVector<Fraction>(field, data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorPreservingVisitor<Fraction> visitor;
        visitor = new FieldVectorPreservingVisitor<Fraction>() {
            private final boolean[] visited = new boolean[data.length];

            public void visit(final int actualIndex, final Fraction actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                visited[actualIndex] = true;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            public Fraction end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Fraction.ZERO;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor1() {
        final Fraction[] data = new Fraction[] {
            Fraction.ZERO, Fraction.ONE, Fraction.ZERO,
            Fraction.ZERO, Fraction.TWO, Fraction.ZERO,
            Fraction.ZERO, Fraction.ZERO, new Fraction(3)
        };
        final SparseFieldVector<Fraction> v = new SparseFieldVector<Fraction>(field, data);
        final FieldVectorChangingVisitor<Fraction> visitor;
        visitor = new FieldVectorChangingVisitor<Fraction>() {

            private int expectedIndex;

            public Fraction visit(final int actualIndex, final Fraction actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
                return actualValue.add(actualIndex);
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            public Fraction end() {
                return Fraction.ZERO;
            }
        };
        v.walkInDefaultOrder(visitor);
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals("entry " + i, data[i].add(i), v.getEntry(i));
        }
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor2() {
        final SparseFieldVector<Fraction> v = create(5);
        final FieldVectorChangingVisitor<Fraction> visitor;
        visitor = new FieldVectorChangingVisitor<Fraction>() {

            public Fraction visit(int index, Fraction value) {
                return Fraction.ZERO;
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public Fraction end() {
                return Fraction.ZERO;
            }
        };
        try {
            v.walkInDefaultOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor3() {
        final Fraction[] data = new Fraction[] {
            Fraction.ZERO, Fraction.ONE, Fraction.ZERO,
            Fraction.ZERO, Fraction.TWO, Fraction.ZERO,
            Fraction.ZERO, Fraction.ZERO, new Fraction(3)
        };
        final SparseFieldVector<Fraction> v = new SparseFieldVector<Fraction>(field, data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorChangingVisitor<Fraction> visitor;
        visitor = new FieldVectorChangingVisitor<Fraction>() {

            private int expectedIndex;

            public Fraction visit(final int actualIndex, final Fraction actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
                return actualValue.add(actualIndex);
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            public Fraction end() {
                return Fraction.ZERO;
            }
        };
        v.walkInDefaultOrder(visitor, expectedStart, expectedEnd);
        for (int i = expectedStart; i <= expectedEnd; i++) {
            Assert.assertEquals("entry " + i, data[i].add(i), v.getEntry(i));
        }
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor1() {
        final Fraction[] data = new Fraction[] {
            Fraction.ZERO, Fraction.ONE, Fraction.ZERO,
            Fraction.ZERO, Fraction.TWO, Fraction.ZERO,
            Fraction.ZERO, Fraction.ZERO, new Fraction(3)
        };
        final SparseFieldVector<Fraction> v = new SparseFieldVector<Fraction>(field, data);
        final FieldVectorChangingVisitor<Fraction> visitor;
        visitor = new FieldVectorChangingVisitor<Fraction>() {
            private final boolean[] visited = new boolean[data.length];

            public Fraction visit(final int actualIndex, final Fraction actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                return actualValue.add(actualIndex);
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            public Fraction end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Fraction.ZERO;
            }
        };
        v.walkInOptimizedOrder(visitor);
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals("entry " + i, data[i].add(i), v.getEntry(i));
        }
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor2() {
        final SparseFieldVector<Fraction> v = create(5);
        final FieldVectorChangingVisitor<Fraction> visitor;
        visitor = new FieldVectorChangingVisitor<Fraction>() {

            public Fraction visit(int index, Fraction value) {
                return Fraction.ZERO;
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public Fraction end() {
                return Fraction.ZERO;
            }
        };
        try {
            v.walkInOptimizedOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor3() {
        final Fraction[] data = new Fraction[] {
            Fraction.ZERO, Fraction.ONE, Fraction.ZERO,
            Fraction.ZERO, Fraction.TWO, Fraction.ZERO,
            Fraction.ZERO, Fraction.ZERO, new Fraction(3)
        };
        final SparseFieldVector<Fraction> v = new SparseFieldVector<Fraction>(field, data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorChangingVisitor<Fraction> visitor;
        visitor = new FieldVectorChangingVisitor<Fraction>() {
            private final boolean[] visited = new boolean[data.length];

            public Fraction visit(final int actualIndex, final Fraction actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                visited[actualIndex] = true;
                return actualValue.add(actualIndex);
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            public Fraction end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Fraction.ZERO;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
        for (int i = expectedStart; i <= expectedEnd; i++) {
            Assert.assertEquals("entry " + i, data[i].add(i), v.getEntry(i));
        }
    }

    private SparseFieldVector<Fraction> create(int n) {
        Fraction[] t = new Fraction[n];
        for (int i = 0; i < n; ++i) {
            t[i] = Fraction.ZERO;
        }
        return new SparseFieldVector<Fraction>(field, t);
    }
}
