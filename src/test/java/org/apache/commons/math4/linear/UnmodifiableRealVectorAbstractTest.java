/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.commons.math4.linear;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.math4.analysis.UnivariateFunction;
import org.apache.commons.math4.analysis.function.Sin;
import org.apache.commons.math4.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.linear.ArrayRealVector;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.linear.RealVector;
import org.apache.commons.math4.linear.RealVector.Entry;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * This is an abstract test of the {@link
 * RealVector#unmodifiableRealVector(RealVector) unmodifiable vector}
 * implementation. These unmodifiable vectors decorate a (modifiable)
 * {@link RealVector}; therefore, a new implementation of this abstract
 * test should be considered for each implementation of
 * {@link RealVector}.
 *
 *
 */
public abstract class UnmodifiableRealVectorAbstractTest {
    /** The dimension of the randomly generated vectors. */
    protected static final int DIM = 100;
    /** Absolute tolerance. */
    protected static final double EPS = 10 * Math.ulp(1d);
    /**
     * The list of methods which are excluded from the general test
     * {@link #testAllButExcluded()}.
     */
    protected static final Set<String> EXCLUDE = new HashSet<String>();
    /** The random number generator (always initialized with the same seed. */
    protected static final Random RANDOM;

    static {
        EXCLUDE.add("getEntry");
        EXCLUDE.add("setEntry");
        EXCLUDE.add("addToEntry");
        EXCLUDE.add("getSubVector");
        EXCLUDE.add("setSubVector");
        EXCLUDE.add("iterator");
        EXCLUDE.add("sparseIterator");
        EXCLUDE.add("walkInDefaultOrder");
        EXCLUDE.add("walkInOptimizedOrder");
        EXCLUDE.add("ebeDivide");
        EXCLUDE.add("ebeMultiply");

        // Excluded because they are inherited from "Object".
        for (Method m : Object.class.getMethods()) {
            EXCLUDE.add(m.getName());
        }
        RANDOM = new Random(20110813);
    }

    /**
     * Returns {@code true} if the specified {@code double} are equal (within a
     * given tolerance).
     *
     * @param x First {@code double}.
     * @param y Second {@code double}.
     * @return {@code true} if {@code x} and {@code y} are equal.
     */
    public static boolean equals(final double x, final double y) {
        if (x == y) {
            return true;
        } else if (FastMath.abs(x) <= EPS) {
            return FastMath.abs(y) <= EPS;
        } else if (FastMath.abs(y) <= EPS) {
            return FastMath.abs(x) <= EPS;
        } else {
            return FastMath.abs(x - y) <= EPS * FastMath.min(FastMath.abs(x), FastMath.abs(y));
        }
    }

    /**
     * Returns {@code true} if the specified {@code double} arrays are equal
     * (within a given tolerance).
     *
     * @param x First array.
     * @param y Second array.
     * @return {@code true} if {@code x} and {@code y} are equal.
     */
    public static boolean equals(final double[] x, final double[] y) {
        if (x.length != y.length) {
            return false;
        }
        final int n = x.length;
        for (int i = 0; i < n; i++) {
            if (!equals(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the specified {@code RealVector} are equal
     * (within a given tolerance).
     *
     * @param x First vector.
     * @param y Second vector.
     * @return {@code true} if {@code x} and {@code y} are equal.
     */
    public static boolean equals(final RealVector x, final RealVector y) {
        if (x.getDimension() != y.getDimension()) {
            return false;
        }
        final int n = x.getDimension();
        for (int i = 0; i < n; i++) {
            if (!equals(x.getEntry(i), y.getEntry(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the specified {@code RealVector} is equal to the
     * specified {@code double} array (within a given tolerance).
     *
     * @param x Vector.
     * @param y Array.
     * @return {@code true} if {@code x} and {@code y} are equal.
     */
    public static boolean equals(final RealVector x, final double[] y) {
        if (x.getDimension() != y.length) {
            return false;
        }
        final int n = x.getDimension();
        for (int i = 0; i < n; i++) {
            if (!equals(x.getEntry(i), y[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the specified {@code RealMatrix} are equal
     * (within a given tolerance).
     *
     * @param x First matrix.
     * @param y Second matrix.
     * @return {@code true} if {@code x} and {@code y} are equal.
     */
    public static boolean equals(final RealMatrix x, final RealMatrix y) {
        if (x.getRowDimension() != y.getRowDimension()) {
            return false;
        }
        if (x.getColumnDimension() != y.getColumnDimension()) {
            return false;
        }
        final int rows = x.getRowDimension();
        final int cols = x.getColumnDimension();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!equals(x.getEntry(i, j), y.getEntry(i, j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the specified {@code Object} are equal.
     *
     * @param x First object.
     * @param y Second object.
     * @return {@code true} if {@code x} and {@code y} are equal.
     * @throws IllegalArgumentException if {@code x} and {@code y} could
     * not be compared.
     */
    public static boolean equals(final Object x, final Object y) {
        if (x instanceof Boolean) {
            if (y instanceof Boolean) {
                return ((Boolean) x).booleanValue() == ((Boolean) y)
                        .booleanValue();
            } else {
                return false;
            }
        }
        if (x instanceof Integer) {
            if (y instanceof Integer) {
                return ((Integer) x).intValue() == ((Integer) y).intValue();
            } else {
                return false;
            }
        } else if (x instanceof Double) {
            if (y instanceof Double) {
                return equals(((Double) x).doubleValue(),
                        ((Double) y).doubleValue());
            } else {
                return false;
            }
        } else if (x instanceof double[]) {
            if (y instanceof double[]) {
                return equals((double[]) x, (double[]) y);
            } else if (y instanceof RealVector) {
                return equals((RealVector) y, (double[]) x);
            } else {
                return false;
            }
        } else if (x instanceof RealVector) {
            if (y instanceof double[]) {
                return equals((RealVector) x, (double[]) y);
            } else if (y instanceof RealVector) {
                return equals((RealVector) x, (RealVector) y);
            } else {
                return false;
            }
        } else if (x instanceof RealMatrix) {
            if (y instanceof RealMatrix) {
                return equals((RealMatrix) x, (RealMatrix) y);
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException("could not compare " + x + ", "
                    + y);
        }
    }

    /**
     * Creates a new random vector of a specified type. This vector is then to
     * be wrapped in an unmodifiable vector.
     *
     * @return a new random vector.
     */
    public abstract RealVector createVector();

    /**
     * Creates a new random object of the specified type.
     *
     * @param c Class of the object to be created.
     * @return a new random object.
     * @throws IllegalArgumentException if the specified class is not
     * recognized by this method.
     */
    public Object createParameter(final Class<?> c) {
        if (c == Integer.TYPE) {
            return Integer.valueOf(RANDOM.nextInt());
        } else if (c == Double.TYPE) {
            return Double.valueOf(RANDOM.nextDouble());
        } else if (c == double[].class) {
            final double[] v = new double[DIM];
            for (int i = 0; i < DIM; i++) {
                v[i] = RANDOM.nextDouble();
            }
            return v;
        } else if (c.isAssignableFrom(RealVector.class)) {
            return createVector();
        } else if (c.isAssignableFrom(UnivariateFunction.class)) {
            return new Sin();
        } else {
            throw new IllegalArgumentException("could not create " + c);
        }
    }

    /**
     * This is the general test of most methods in the
     * {@link RealVector#unmodifiableRealVector(RealVector) unmodifiable vector}.
     * It works as follows.
     * First, an unmodifiable view of a copy of the specified random vector
     * {@code u} is created: this defines {@code v}. Then the <em>same</em>
     * method {@code m} is invoked on {@code u} and {@code v}, with randomly
     * generated parameters {@code args}.
     * If it turns out that {@code u} has changed after the call of method
     * {@code m}, then this test checks that the call of this method on
     * {@code v} resulted in a {@link MathUnsupportedOperationException}. If
     * {@code u} was not modified, then this test checks that the results
     * returned by the call of method {@code m} on {@code u} and {@code v}
     * returned the same result.
     *
     * @param m Method to be tested.
     * @param u Random vector from which the unmodifiable view is to be
     *constructed.
     * @param args Arguments to be passed to method {@code m}.
     */
    private void callMethod(final Method m,
                            final RealVector u,
                            final Object... args)
        throws IllegalAccessException,
               IllegalArgumentException,
               InvocationTargetException {
        final RealVector uu = u.copy();
        final RealVector v = RealVector.unmodifiableRealVector(u.copy());
        Object exp = m.invoke(u, args);
        if (equals(uu, u)) {
            Object act = m.invoke(v, args);
            Assert.assertTrue(m.toGenericString() + ", unmodifiable vector has changed",
                              equals(uu, v));
            Assert.assertTrue(m.toGenericString() + ", wrong result",
                              equals(exp, act));

        } else {
            boolean flag = false;
            try {
                m.invoke(v, args);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof MathUnsupportedOperationException) {
                    flag = true;
                }
            }
            Assert.assertTrue(m.toGenericString()+", exception should have been thrown", flag);
        }
    }

    /**
     * This test calls {@link #callMethod(Method, RealVector, Object...)} on
     * every method defined in interface {@link RealVector}. It generates the
     * appropriate random arguments. Some methods are manually excluded (see
     * {@link #EXCLUDE}), they must be handled by separate tests.
     */
    @Test
    public void testAllButExcluded()
        throws IllegalAccessException,
               IllegalArgumentException,
               InvocationTargetException {
        Method[] method = RealVector.class.getMethods();
        for (int i = 0; i < method.length; i++) {
            Method m = method[i];
            if (!EXCLUDE.contains(m.getName())) {
                RealVector u = (RealVector) createParameter(RealVector.class);
                Class<?>[] paramType = m.getParameterTypes();
                Object[] param = new Object[paramType.length];
                for (int j = 0; j < paramType.length; j++) {
                    param[j] = createParameter(paramType[j]);
                }
                callMethod(m, u, param);
            }
        }
    }

    @Test
    public void testGetEntry() {
        RealVector u = createVector();
        RealVector v = RealVector.unmodifiableRealVector(u);
        for (int i = 0; i < DIM; i++) {
            Assert.assertTrue(equals(u.getEntry(i), v.getEntry(i)));
        }
    }

    @Test(expected = MathUnsupportedOperationException.class)
    public void testSetEntry() {
        RealVector u = createVector();
        RealVector v = RealVector.unmodifiableRealVector(u);
        for (int i = 0; i < DIM; i++) {
            v.setEntry(i, 0d);
        }
    }

    @Test(expected = MathUnsupportedOperationException.class)
    public void testAddToEntry() {
        RealVector u = createVector();
        RealVector v = RealVector.unmodifiableRealVector(u);
        for (int i = 0; i < DIM; i++) {
            v.addToEntry(i, 0d);
        }
    }

    @Test
    public void testGetSubVector() {
        RealVector u = createVector();
        RealVector v = RealVector.unmodifiableRealVector(u);
        for (int i = 0; i < DIM; i++) {
            for (int n = 1; n < DIM - i; n++) {
                RealVector exp = u.getSubVector(i, n);
                RealVector act = v.getSubVector(i, n);
                Assert.assertTrue(equals(exp, act));
            }
        }
    }

    @Test(expected = MathUnsupportedOperationException.class)
    public void testSetSubVector() {
        RealVector u = createVector();
        RealVector v = RealVector.unmodifiableRealVector(u);
        v.setSubVector(0, new ArrayRealVector());
    }

    @Test
    public void testIterator() {
        RealVector u = createVector();
        Iterator<Entry> i = u.iterator();
        RealVector v = RealVector.unmodifiableRealVector(u.copy());
        Iterator<Entry> j = v.iterator();
        boolean flag;
        while (i.hasNext()) {
            Assert.assertTrue(j.hasNext());
            Entry exp = i.next();
            Entry act = j.next();
            Assert.assertTrue(equals(exp.getIndex(), act.getIndex()));
            Assert.assertTrue(equals(exp.getValue(), act.getValue()));
            exp.setIndex(RANDOM.nextInt(DIM));
            act.setIndex(RANDOM.nextInt(DIM));
            flag = false;
            try {
                act.setValue(RANDOM.nextDouble());
            } catch (MathUnsupportedOperationException e) {
                flag = true;
            }
            Assert.assertTrue("exception should have been thrown", flag);
        }
        Assert.assertFalse(j.hasNext());
    }

    @Test
    public void testSparseIterator() {
        RealVector u = createVector();
        Iterator<Entry> i = u.sparseIterator();
        RealVector v = RealVector.unmodifiableRealVector(u.copy());
        Iterator<Entry> j = v.sparseIterator();
        boolean flag;
        while (i.hasNext()) {
            Assert.assertTrue(j.hasNext());
            Entry exp = i.next();
            Entry act = j.next();
            Assert.assertTrue(equals(exp.getIndex(), act.getIndex()));
            Assert.assertTrue(equals(exp.getValue(), act.getValue()));
            exp.setIndex(RANDOM.nextInt(DIM));
            act.setIndex(RANDOM.nextInt(DIM));
            flag = false;
            try {
                act.setValue(RANDOM.nextDouble());
            } catch (MathUnsupportedOperationException e) {
                flag = true;
            }
            Assert.assertTrue("exception should have been thrown", flag);
        }
        Assert.assertFalse(j.hasNext());
    }
}
