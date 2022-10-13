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
package org.apache.commons.math4.legacy.linear;


import java.util.Arrays;

import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.core.dfp.Dfp;
import org.apache.commons.math4.legacy.core.dfp.DfpField;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for the {@link SparseFieldVector} class.
 *
 */
public class SparseFieldVectorTest {

    //
    protected Dfp[][] ma1 = {{Dfp25.of(1), Dfp25.of(2), Dfp25.of(3)}, {Dfp25.of(4), Dfp25.of(5), Dfp25.of(6)}, {Dfp25.of(7), Dfp25.of(8), Dfp25.of(9)}};
    protected Dfp[] vec1 = {Dfp25.of(1), Dfp25.of(2), Dfp25.of(3)};
    protected Dfp[] vec2 = {Dfp25.of(4), Dfp25.of(5), Dfp25.of(6)};
    protected Dfp[] vec3 = {Dfp25.of(7), Dfp25.of(8), Dfp25.of(9)};
    protected Dfp[] vec4 = {Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4), Dfp25.of(5), Dfp25.of(6), Dfp25.of(7), Dfp25.of(8), Dfp25.of(9)};
    protected Dfp[] vec_null = {Dfp25.of(0), Dfp25.of(0), Dfp25.of(0)};
    protected Dfp[] dvec1 = {Dfp25.of(1), Dfp25.of(2), Dfp25.of(3), Dfp25.of(4), Dfp25.of(5), Dfp25.of(6), Dfp25.of(7), Dfp25.of(8),Dfp25.of(9)};
    protected Dfp[][] mat1 = {{Dfp25.of(1), Dfp25.of(2), Dfp25.of(3)}, {Dfp25.of(4), Dfp25.of(5), Dfp25.of(6)},{ Dfp25.of(7), Dfp25.of(8), Dfp25.of(9)}};

    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;

    protected DfpField field = Dfp25.getField();

    @Test
    public void testMapFunctions() {
        SparseFieldVector<Dfp> v1 = new SparseFieldVector<>(field,vec1);

        //octave =  v1 .+ 2.0
        FieldVector<Dfp> v_mapAdd = v1.mapAdd(Dfp25.of(2));
        Dfp[] result_mapAdd = {Dfp25.of(3), Dfp25.of(4), Dfp25.of(5)};
        Assert.assertArrayEquals("compare vectors" ,result_mapAdd,v_mapAdd.toArray());

        //octave =  v1 .+ 2.0
        FieldVector<Dfp> v_mapAddToSelf = v1.copy();
        v_mapAddToSelf.mapAddToSelf(Dfp25.of(2));
        Dfp[] result_mapAddToSelf = {Dfp25.of(3), Dfp25.of(4), Dfp25.of(5)};
        Assert.assertArrayEquals("compare vectors" ,result_mapAddToSelf,v_mapAddToSelf.toArray());

        //octave =  v1 .- 2.0
        FieldVector<Dfp> v_mapSubtract = v1.mapSubtract(Dfp25.of(2));
        Dfp[] result_mapSubtract = {Dfp25.of(-1), Dfp25.of(0), Dfp25.of(1)};
        Assert.assertArrayEquals("compare vectors" ,result_mapSubtract,v_mapSubtract.toArray());

        //octave =  v1 .- 2.0
        FieldVector<Dfp> v_mapSubtractToSelf = v1.copy();
        v_mapSubtractToSelf.mapSubtractToSelf(Dfp25.of(2));
        Dfp[] result_mapSubtractToSelf = {Dfp25.of(-1), Dfp25.of(0), Dfp25.of(1)};
        Assert.assertArrayEquals("compare vectors" ,result_mapSubtractToSelf,v_mapSubtractToSelf.toArray());

        //octave =  v1 .* 2.0
        FieldVector<Dfp> v_mapMultiply = v1.mapMultiply(Dfp25.of(2));
        Dfp[] result_mapMultiply = {Dfp25.of(2), Dfp25.of(4), Dfp25.of(6)};
        Assert.assertArrayEquals("compare vectors" ,result_mapMultiply,v_mapMultiply.toArray());

        //octave =  v1 .* 2.0
        FieldVector<Dfp> v_mapMultiplyToSelf = v1.copy();
        v_mapMultiplyToSelf.mapMultiplyToSelf(Dfp25.of(2));
        Dfp[] result_mapMultiplyToSelf = {Dfp25.of(2), Dfp25.of(4), Dfp25.of(6)};
        Assert.assertArrayEquals("compare vectors" ,result_mapMultiplyToSelf,v_mapMultiplyToSelf.toArray());

        //octave =  v1 ./ 2.0
        FieldVector<Dfp> v_mapDivide = v1.mapDivide(Dfp25.of(2));
        Dfp[] result_mapDivide = {Dfp25.of(.5d), Dfp25.of(1), Dfp25.of(1.5d)};
        Assert.assertArrayEquals("compare vectors" ,result_mapDivide,v_mapDivide.toArray());

        //octave =  v1 ./ 2.0
        FieldVector<Dfp> v_mapDivideToSelf = v1.copy();
        v_mapDivideToSelf.mapDivideToSelf(Dfp25.of(2));
        Dfp[] result_mapDivideToSelf = {Dfp25.of(.5d), Dfp25.of(1), Dfp25.of(1.5d)};
        Assert.assertArrayEquals("compare vectors" ,result_mapDivideToSelf,v_mapDivideToSelf.toArray());

        //octave =  v1 .^-1
        FieldVector<Dfp> v_mapInv = v1.mapInv();
        Dfp[] result_mapInv = {Dfp25.of(1),Dfp25.of(0.5d),Dfp25.of(1, 3)};
        Assert.assertArrayEquals("compare vectors" ,result_mapInv,v_mapInv.toArray());

        //octave =  v1 .^-1
        FieldVector<Dfp> v_mapInvToSelf = v1.copy();
        v_mapInvToSelf.mapInvToSelf();
        Dfp[] result_mapInvToSelf = {Dfp25.of(1),Dfp25.of(0.5d),Dfp25.of(1, 3)};
        Assert.assertArrayEquals("compare vectors" ,result_mapInvToSelf,v_mapInvToSelf.toArray());

    }

    @Test
    public void testBasicFunctions() {
        SparseFieldVector<Dfp> v1 = new SparseFieldVector<>(field,vec1);
        SparseFieldVector<Dfp> v2 = new SparseFieldVector<>(field,vec2);

        FieldVector<Dfp> v2_t = new ArrayFieldVectorTest.FieldVectorTestImpl<>(vec2);

        //octave =  v1 + v2
        FieldVector<Dfp> v_add = v1.add(v2);
        Dfp[] result_add = {Dfp25.of(5), Dfp25.of(7), Dfp25.of(9)};
        Assert.assertArrayEquals("compare vect" ,v_add.toArray(),result_add);

        FieldVector<Dfp> vt2 = new ArrayFieldVectorTest.FieldVectorTestImpl<>(vec2);
        FieldVector<Dfp> v_add_i = v1.add(vt2);
        Dfp[] result_add_i = {Dfp25.of(5), Dfp25.of(7), Dfp25.of(9)};
        Assert.assertArrayEquals("compare vect" ,v_add_i.toArray(),result_add_i);

        //octave =  v1 - v2
        SparseFieldVector<Dfp> v_subtract = v1.subtract(v2);
        Dfp[] result_subtract = {Dfp25.of(-3), Dfp25.of(-3), Dfp25.of(-3)};
        assertClose("compare vect" ,v_subtract.toArray(),result_subtract,normTolerance);

        FieldVector<Dfp> v_subtract_i = v1.subtract(vt2);
        Dfp[] result_subtract_i = {Dfp25.of(-3), Dfp25.of(-3), Dfp25.of(-3)};
        assertClose("compare vect" ,v_subtract_i.toArray(),result_subtract_i,normTolerance);

        // octave v1 .* v2
        FieldVector<Dfp>  v_ebeMultiply = v1.ebeMultiply(v2);
        Dfp[] result_ebeMultiply = {Dfp25.of(4), Dfp25.of(10), Dfp25.of(18)};
        assertClose("compare vect" ,v_ebeMultiply.toArray(),result_ebeMultiply,normTolerance);

        FieldVector<Dfp>  v_ebeMultiply_2 = v1.ebeMultiply(v2_t);
        Dfp[] result_ebeMultiply_2 = {Dfp25.of(4), Dfp25.of(10), Dfp25.of(18)};
        assertClose("compare vect" ,v_ebeMultiply_2.toArray(),result_ebeMultiply_2,normTolerance);

        // octave v1 ./ v2
        FieldVector<Dfp>  v_ebeDivide = v1.ebeDivide(v2);
        Dfp[] result_ebeDivide = {Dfp25.of(0.25d), Dfp25.of(0.4d), Dfp25.of(0.5d)};
        assertClose("compare vect" ,v_ebeDivide.toArray(),result_ebeDivide,normTolerance);

        FieldVector<Dfp>  v_ebeDivide_2 = v1.ebeDivide(v2_t);
        Dfp[] result_ebeDivide_2 = {Dfp25.of(0.25d), Dfp25.of(0.4d), Dfp25.of(0.5d)};
        assertClose("compare vect" ,v_ebeDivide_2.toArray(),result_ebeDivide_2,normTolerance);

        // octave  dot(v1,v2)
        Dfp dot =  v1.dotProduct(v2);
        Assert.assertEquals("compare val ",Dfp25.of(32), dot);

        // octave  dot(v1,v2_t)
        Dfp dot_2 =  v1.dotProduct(v2_t);
        Assert.assertEquals("compare val ",Dfp25.of(32), dot_2);

        FieldMatrix<Dfp> m_outerProduct = v1.outerProduct(v2);
        Assert.assertEquals("compare val ",Dfp25.of(4), m_outerProduct.getEntry(0,0));

        FieldMatrix<Dfp> m_outerProduct_2 = v1.outerProduct(v2_t);
        Assert.assertEquals("compare val ",Dfp25.of(4), m_outerProduct_2.getEntry(0,0));
    }

    @Test
    public void testOuterProduct() {
        final SparseFieldVector<Dfp> u
            = new SparseFieldVector<>(Dfp25.getField(),
                                              new Dfp[] {Dfp25.of(1),
                                                              Dfp25.of(2),
                                                              Dfp25.of(-3)});
        final SparseFieldVector<Dfp> v
            = new SparseFieldVector<>(Dfp25.getField(),
                                              new Dfp[] {Dfp25.of(4),
                                                              Dfp25.of(-2)});

        final FieldMatrix<Dfp> uv = u.outerProduct(v);

        final double tol = Math.ulp(1d);
        Assert.assertEquals(Dfp25.of(4).toDouble(), uv.getEntry(0, 0).toDouble(), tol);
        Assert.assertEquals(Dfp25.of(-2).toDouble(), uv.getEntry(0, 1).toDouble(), tol);
        Assert.assertEquals(Dfp25.of(8).toDouble(), uv.getEntry(1, 0).toDouble(), tol);
        Assert.assertEquals(Dfp25.of(-4).toDouble(), uv.getEntry(1, 1).toDouble(), tol);
        Assert.assertEquals(Dfp25.of(-12).toDouble(), uv.getEntry(2, 0).toDouble(), tol);
        Assert.assertEquals(Dfp25.of(6).toDouble(), uv.getEntry(2, 1).toDouble(), tol);
    }

    @Test
    public void testMisc() {
        SparseFieldVector<Dfp> v1 = new SparseFieldVector<>(field,vec1);

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

        SparseFieldVector<Dfp> v = new SparseFieldVector<>(field, new Dfp[] { Dfp25.of(0), Dfp25.of(1), Dfp25.of(2) });

        v.setEntry(0, field.getZero());
        Assert.assertEquals(v, new SparseFieldVector<>(field, new Dfp[] { Dfp25.of(0), Dfp25.of(1), Dfp25.of(2) }));
        Assert.assertNotSame(v, new SparseFieldVector<>(field, new Dfp[] { Dfp25.of(0), Dfp25.of(1), Dfp25.of(2), Dfp25.of(3) }));
    }

    /** verifies that two vectors are close (sup norm) */
    protected void assertEquals(String msg, Dfp[] m, Dfp[] n) {
        if (m.length != n.length) {
            Assert.fail("vectors have different lengths");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(msg + " " +  i + " elements differ", m[i],n[i]);
        }
    }

    /** verifies that two vectors are close (sup norm) */
    protected void assertClose(String msg, Dfp[] m, Dfp[] n, double tolerance) {
        if (m.length != n.length) {
            Assert.fail("vectors have different lengths");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(msg + " " +  i + " elements differ", m[i].toDouble(),n[i].toDouble(), tolerance);
        }
    }

    /*
     * TESTS OF THE VISITOR PATTERN
     */

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor1() {
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final SparseFieldVector<Dfp> v = new SparseFieldVector<>(field, data);
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {

            private int expectedIndex;

            @Override
            public void visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
            }
        };
        v.walkInDefaultOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor2() {
        final SparseFieldVector<Dfp> v = create(5);
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {

            @Override
            public void visit(int index, Dfp value) {
                // Do nothing
            }

            @Override
            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
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
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final SparseFieldVector<Dfp> v = new SparseFieldVector<>(field, data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {

            private int expectedIndex;

            @Override
            public void visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
            }
        };
        v.walkInDefaultOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor1() {
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final SparseFieldVector<Dfp> v = new SparseFieldVector<>(field, data);
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {
            private final boolean[] visited = new boolean[data.length];

            @Override
            public void visit(final int actualIndex, final Dfp actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            @Override
            public Dfp end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Dfp25.ZERO;
            }
        };
        v.walkInOptimizedOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor2() {
        final SparseFieldVector<Dfp> v = create(5);
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {

            @Override
            public void visit(int index, Dfp value) {
                // Do nothing
            }

            @Override
            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
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
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final SparseFieldVector<Dfp> v = new SparseFieldVector<>(field, data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {
            private final boolean[] visited = new boolean[data.length];

            @Override
            public void visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                visited[actualIndex] = true;
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            @Override
            public Dfp end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Dfp25.ZERO;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor1() {
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final SparseFieldVector<Dfp> v = new SparseFieldVector<>(field, data);
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {

            private int expectedIndex;

            @Override
            public Dfp visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
                return actualValue.add(actualIndex);
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
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
        final SparseFieldVector<Dfp> v = create(5);
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {

            @Override
            public Dfp visit(int index, Dfp value) {
                return Dfp25.ZERO;
            }

            @Override
            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
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
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final SparseFieldVector<Dfp> v = new SparseFieldVector<>(field, data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {

            private int expectedIndex;

            @Override
            public Dfp visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
                return actualValue.add(actualIndex);
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
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
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final SparseFieldVector<Dfp> v = new SparseFieldVector<>(field, data);
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {
            private final boolean[] visited = new boolean[data.length];

            @Override
            public Dfp visit(final int actualIndex, final Dfp actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                return actualValue.add(actualIndex);
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            @Override
            public Dfp end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Dfp25.ZERO;
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
        final SparseFieldVector<Dfp> v = create(5);
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {

            @Override
            public Dfp visit(int index, Dfp value) {
                return Dfp25.ZERO;
            }

            @Override
            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
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
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final SparseFieldVector<Dfp> v = new SparseFieldVector<>(field, data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {
            private final boolean[] visited = new boolean[data.length];

            @Override
            public Dfp visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                visited[actualIndex] = true;
                return actualValue.add(actualIndex);
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            @Override
            public Dfp end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Dfp25.ZERO;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
        for (int i = expectedStart; i <= expectedEnd; i++) {
            Assert.assertEquals("entry " + i, data[i].add(i), v.getEntry(i));
        }
    }

    private SparseFieldVector<Dfp> create(int n) {
        Dfp[] t = new Dfp[n];
        for (int i = 0; i < n; ++i) {
            t[i] = Dfp25.ZERO;
        }
        return new SparseFieldVector<>(field, t);
    }
}
