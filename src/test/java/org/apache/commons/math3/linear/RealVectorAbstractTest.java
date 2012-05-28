package org.apache.commons.math3.linear;

import java.util.Arrays;

import junit.framework.Assert;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.junit.Test;


public abstract class RealVectorAbstractTest {

    public abstract RealVector create(double[] data);

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            private int expectedIndex;

            public void visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            public void visit(int index, double value) {
                // Do nothing
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
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
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            private int expectedIndex;

            public void visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public void visit(final int actualIndex, final double actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            public double end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            public void visit(int index, double value) {
                // Do nothing
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
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
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public void visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                visited[actualIndex] = true;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            public double end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            private int expectedIndex;

            public double visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor);
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            public double visit(int index, double value) {
                return 0.0;
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
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
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            private int expectedIndex;

            public double visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor, expectedStart, expectedEnd);
        for (int i = expectedStart; i <= expectedEnd; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public double visit(final int actualIndex, final double actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            public double end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor);
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            public double visit(int index, double value) {
                return 0.0;
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
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
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public double visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                visited[actualIndex] = true;
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            public double end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
        for (int i = expectedStart; i <= expectedEnd; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }
}
