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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.analysis.function.Acos;
import org.apache.commons.math3.analysis.function.Asin;
import org.apache.commons.math3.analysis.function.Atan;
import org.apache.commons.math3.analysis.function.Cbrt;
import org.apache.commons.math3.analysis.function.Ceil;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.analysis.function.Cosh;
import org.apache.commons.math3.analysis.function.Exp;
import org.apache.commons.math3.analysis.function.Expm1;
import org.apache.commons.math3.analysis.function.Floor;
import org.apache.commons.math3.analysis.function.Inverse;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.function.Log10;
import org.apache.commons.math3.analysis.function.Log1p;
import org.apache.commons.math3.analysis.function.Power;
import org.apache.commons.math3.analysis.function.Rint;
import org.apache.commons.math3.analysis.function.Signum;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.function.Sinh;
import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.analysis.function.Tan;
import org.apache.commons.math3.analysis.function.Tanh;
import org.apache.commons.math3.analysis.function.Ulp;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link ArrayRealVector} class.
 *
 * @version $Id$
 */
public class ArrayRealVectorTest extends RealVectorAbstractTest {

    // Testclass to test the RealVector interface
    // only with enough content to support the test
    public static class RealVectorTestImpl extends RealVector
        implements Serializable {

        /** Serializable version identifier. */
        private static final long serialVersionUID = 4715341047369582908L;

        /** Entries of the vector. */
        protected double data[];

        public RealVectorTestImpl(double[] d) {
            data = d.clone();
        }

        private UnsupportedOperationException unsupported() {
            return new UnsupportedOperationException("Not supported, unneeded for test purposes");
        }

        @Override
        public RealVector map(UnivariateFunction function) {
            throw unsupported();
        }

        @Override
        public RealVector mapToSelf(UnivariateFunction function) {
            throw unsupported();
        }

        @Override
        public Iterator<Entry> iterator() {
            return new Iterator<Entry>() {
                int i = 0;
                public boolean hasNext() {
                    return i<data.length;
                }
                public Entry next() {
                    final int j = i++;
                    Entry e = new Entry() {
                        @Override
                        public double getValue() {
                            return data[j];
                        }
                        @Override
                        public void setValue(double newValue) {
                            data[j] = newValue;
                        }
                    };
                    e.setIndex(j);
                    return e;
                }
                public void remove() { }
            };
        }

        @Override
        public Iterator<Entry> sparseIterator() {
            return iterator();
        }

        @Override
        public RealVector copy() {
            throw unsupported();
        }

        @Override
        public RealVector add(RealVector v) {
            throw unsupported();
        }

        public RealVector add(double[] v) {
            throw unsupported();
        }

        @Override
        public RealVector subtract(RealVector v) {
            throw unsupported();
        }

        public RealVector subtract(double[] v) {
            throw unsupported();
        }

        @Override
        public RealVector mapAdd(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapAddToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapSubtract(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapSubtractToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapMultiply(double d) {
            double[] out = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i] * d;
            }
            return new ArrayRealVector(out);
        }

        @Override
        public RealVector mapMultiplyToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapDivide(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapDivideToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector ebeMultiply(RealVector v) {
            throw unsupported();
        }

        public RealVector ebeMultiply(double[] v) {
            throw unsupported();
        }

        @Override
        public RealVector ebeDivide(RealVector v) {
            throw unsupported();
        }

        public RealVector ebeDivide(double[] v) {
            throw unsupported();
        }

        @Override
        public double dotProduct(RealVector v) {
            double dot = 0;
            for (int i = 0; i < data.length; i++) {
                dot += data[i] * v.getEntry(i);
            }
            return dot;
        }

        public double dotProduct(double[] v) {
            double dot = 0;
            for (int i = 0; i < data.length; i++) {
                dot += data[i] * v[i];
            }
            return dot;
        }

        @Override
        public double cosine(RealVector v) {
            throw unsupported();
        }

        public double cosine(double[] v) {
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
        public double getDistance(RealVector v) {
            throw unsupported();
        }

        public double getDistance(double[] v) {
            throw unsupported();
        }

        @Override
        public double getL1Distance(RealVector v) {
            throw unsupported();
        }

        public double getL1Distance(double[] v) {
            throw unsupported();
        }

        @Override
        public double getLInfDistance(RealVector v) {
            throw unsupported();
        }

        public double getLInfDistance(double[] v) {
            throw unsupported();
        }

        @Override
        public RealVector unitVector() {
            throw unsupported();
        }

        @Override
        public void unitize() {
            throw unsupported();
        }

        @Override
        public RealVector projection(RealVector v) {
            throw unsupported();
        }

        public RealVector projection(double[] v) {
            throw unsupported();
        }

        @Override
        public RealMatrix outerProduct(RealVector v) {
            throw unsupported();
        }

        public RealMatrix outerProduct(double[] v) {
            throw unsupported();
        }

        @Override
        public double getEntry(int index) {
            return data[index];
        }

        @Override
        public int getDimension() {
            return data.length;
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

        @Override
        public void setEntry(int index, double value) {
            throw unsupported();
        }

        @Override
        public void setSubVector(int index, RealVector v) {
            throw unsupported();
        }

        public void setSubVector(int index, double[] v) {
            throw unsupported();
        }

        @Override
        public void set(double value) {
            throw unsupported();
        }

        @Override
        public double[] toArray() {
            return data.clone();
        }

        @Override
        public boolean isNaN() {
            throw unsupported();
        }

        @Override
        public boolean isInfinite() {
            throw unsupported();
        }

        public RealVector combine(double a, double b, double[] y) {
            throw unsupported();
        }

        @Override
        public RealVector combine(double a, double b, RealVector y) {
            throw unsupported();
        }

        public RealVector combineToSelf(double a, double b, double[] y) {
            throw unsupported();
        }

        @Override
        public RealVector combineToSelf(double a, double b, RealVector y) {
            throw unsupported();
        }
    }

    @Test
    public void testConstructors() {

        ArrayRealVector v0 = new ArrayRealVector();
        Assert.assertEquals("testData len", 0, v0.getDimension());

        ArrayRealVector v1 = new ArrayRealVector(7);
        Assert.assertEquals("testData len", 7, v1.getDimension());
        Assert.assertEquals("testData is 0.0 ", 0.0, v1.getEntry(6), 0);

        ArrayRealVector v2 = new ArrayRealVector(5, 1.23);
        Assert.assertEquals("testData len", 5, v2.getDimension());
        Assert.assertEquals("testData is 1.23 ", 1.23, v2.getEntry(4), 0);

        ArrayRealVector v3 = new ArrayRealVector(vec1);
        Assert.assertEquals("testData len", 3, v3.getDimension());
        Assert.assertEquals("testData is 2.0 ", 2.0, v3.getEntry(1), 0);

        ArrayRealVector v3_bis = new ArrayRealVector(vec1, true);
        Assert.assertEquals("testData len", 3, v3_bis.getDimension());
        Assert.assertEquals("testData is 2.0 ", 2.0, v3_bis.getEntry(1), 0);
        Assert.assertNotSame(v3_bis.getDataRef(), vec1);
        Assert.assertNotSame(v3_bis.toArray(), vec1);

        ArrayRealVector v3_ter = new ArrayRealVector(vec1, false);
        Assert.assertEquals("testData len", 3, v3_ter.getDimension());
        Assert.assertEquals("testData is 2.0 ", 2.0, v3_ter.getEntry(1), 0);
        Assert.assertSame(v3_ter.getDataRef(), vec1);
        Assert.assertNotSame(v3_ter.toArray(), vec1);

        ArrayRealVector v4 = new ArrayRealVector(vec4, 3, 2);
        Assert.assertEquals("testData len", 2, v4.getDimension());
        Assert.assertEquals("testData is 4.0 ", 4.0, v4.getEntry(0), 0);
        try {
            new ArrayRealVector(vec4, 8, 3);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

        RealVector v5_i = new ArrayRealVector(dvec1);
        Assert.assertEquals("testData len", 9, v5_i.getDimension());
        Assert.assertEquals("testData is 9.0 ", 9.0, v5_i.getEntry(8), 0);

        ArrayRealVector v5 = new ArrayRealVector(dvec1);
        Assert.assertEquals("testData len", 9, v5.getDimension());
        Assert.assertEquals("testData is 9.0 ", 9.0, v5.getEntry(8), 0);

        ArrayRealVector v6 = new ArrayRealVector(dvec1, 3, 2);
        Assert.assertEquals("testData len", 2, v6.getDimension());
        Assert.assertEquals("testData is 4.0 ", 4.0, v6.getEntry(0), 0);
        try {
            new ArrayRealVector(dvec1, 8, 3);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

        ArrayRealVector v7 = new ArrayRealVector(v1);
        Assert.assertEquals("testData len", 7, v7.getDimension());
        Assert.assertEquals("testData is 0.0 ", 0.0, v7.getEntry(6), 0);

        RealVectorTestImpl v7_i = new RealVectorTestImpl(vec1);

        ArrayRealVector v7_2 = new ArrayRealVector(v7_i);
        Assert.assertEquals("testData len", 3, v7_2.getDimension());
        Assert.assertEquals("testData is 0.0 ", 2.0d, v7_2.getEntry(1), 0);

        ArrayRealVector v8 = new ArrayRealVector(v1, true);
        Assert.assertEquals("testData len", 7, v8.getDimension());
        Assert.assertEquals("testData is 0.0 ", 0.0, v8.getEntry(6), 0);
        Assert.assertNotSame("testData not same object ", v1.getDataRef(), v8.getDataRef());

        ArrayRealVector v8_2 = new ArrayRealVector(v1, false);
        Assert.assertEquals("testData len", 7, v8_2.getDimension());
        Assert.assertEquals("testData is 0.0 ", 0.0, v8_2.getEntry(6), 0);
        Assert.assertEquals("testData same object ", v1.getDataRef(), v8_2.getDataRef());

        ArrayRealVector v9 = new ArrayRealVector(v1, v3);
        Assert.assertEquals("testData len", 10, v9.getDimension());
        Assert.assertEquals("testData is 1.0 ", 1.0, v9.getEntry(7), 0);

        ArrayRealVector v10 = new ArrayRealVector(v2, new RealVectorTestImpl(vec3));
        Assert.assertEquals("testData len", 8, v10.getDimension());
        Assert.assertEquals("testData is 1.23 ", 1.23, v10.getEntry(4), 0);
        Assert.assertEquals("testData is 7.0 ", 7.0, v10.getEntry(5), 0);

        ArrayRealVector v11 = new ArrayRealVector(new RealVectorTestImpl(vec3), v2);
        Assert.assertEquals("testData len", 8, v11.getDimension());
        Assert.assertEquals("testData is 9.0 ", 9.0, v11.getEntry(2), 0);
        Assert.assertEquals("testData is 1.23 ", 1.23, v11.getEntry(3), 0);

        ArrayRealVector v12 = new ArrayRealVector(v2, vec3);
        Assert.assertEquals("testData len", 8, v12.getDimension());
        Assert.assertEquals("testData is 1.23 ", 1.23, v12.getEntry(4), 0);
        Assert.assertEquals("testData is 7.0 ", 7.0, v12.getEntry(5), 0);

        ArrayRealVector v13 = new ArrayRealVector(vec3, v2);
        Assert.assertEquals("testData len", 8, v13.getDimension());
        Assert.assertEquals("testData is 9.0 ", 9.0, v13.getEntry(2), 0);
        Assert.assertEquals("testData is 1.23 ", 1.23, v13.getEntry(3), 0);

        ArrayRealVector v14 = new ArrayRealVector(vec3, vec4);
        Assert.assertEquals("testData len", 12, v14.getDimension());
        Assert.assertEquals("testData is 9.0 ", 9.0, v14.getEntry(2), 0);
        Assert.assertEquals("testData is 1.0 ", 1.0, v14.getEntry(3), 0);

    }

    @Override
    @Test
    public void testDataInOut() {
        super.testDataInOut();

        ArrayRealVector v1 = new ArrayRealVector(vec1);

        RealVector v_copy = v1.copy();
        Assert.assertEquals("testData len", 3, v_copy.getDimension());
        Assert.assertNotSame("testData not same object ", v1.getDataRef(), v_copy.toArray());

        double[] a_double = v1.toArray();
        Assert.assertEquals("testData len", 3, a_double.length);
        Assert.assertNotSame("testData not same object ", v1.getDataRef(), a_double);
    }

    @Test
    public void testBasicFunctions() {
        ArrayRealVector v1 = new ArrayRealVector(vec1);
        ArrayRealVector v2 = new ArrayRealVector(vec2);
        ArrayRealVector v5 = new ArrayRealVector(vec5);
        ArrayRealVector v_null = new ArrayRealVector(vec_null);

        RealVectorTestImpl v2_t = new RealVectorTestImpl(vec2);

        // emacs calc: [-4, 0, 3, 1, -6, 3] A --> 8.4261497731763586307
        double d_getNorm = v5.getNorm();
        Assert.assertEquals("compare values  ", 8.4261497731763586307, d_getNorm, normTolerance);

        // emacs calc: [-4, 0, 3, 1, -6, 3] vN --> 17
        double d_getL1Norm = v5.getL1Norm();
        Assert.assertEquals("compare values  ", 17.0, d_getL1Norm, normTolerance);

        // emacs calc: [-4, 0, 3, 1, -6, 3] vn --> 6
        double d_getLInfNorm = v5.getLInfNorm();
        Assert.assertEquals("compare values  ", 6.0, d_getLInfNorm, normTolerance);


        //octave =  sqrt(sumsq(v1-v2))
        double dist = v1.getDistance(v2);
        Assert.assertEquals("compare values  ",v1.subtract(v2).getNorm(), dist, normTolerance);

        //octave =  sqrt(sumsq(v1-v2))
        double dist_2 = v1.getDistance(v2_t);
        Assert.assertEquals("compare values  ", v1.subtract(v2).getNorm(),dist_2, normTolerance);

        //octave =  sqrt(sumsq(v1-v2))
        double dist_3 = v1.getDistance(v2);
        Assert.assertEquals("compare values  ", v1.subtract(v2).getNorm(),dist_3, normTolerance);

        //octave =  ???
        double d_getL1Distance = v1. getL1Distance(v2);
        Assert.assertEquals("compare values  ", 9d, d_getL1Distance, normTolerance);

        double d_getL1Distance_2 = v1.getL1Distance(v2_t);
        Assert.assertEquals("compare values  ", 9d, d_getL1Distance_2, normTolerance);

        double d_getL1Distance_3 = v1.getL1Distance(v2);
        Assert.assertEquals("compare values  ", 9d, d_getL1Distance_3, normTolerance);

        //octave =  ???
        double d_getLInfDistance = v1.getLInfDistance(v2);
        Assert.assertEquals("compare values  ", 3d, d_getLInfDistance, normTolerance);

        double d_getLInfDistance_2 = v1. getLInfDistance(v2_t);
        Assert.assertEquals("compare values  ", 3d, d_getLInfDistance_2, normTolerance);

        double d_getLInfDistance_3 = v1. getLInfDistance(v2);
        Assert.assertEquals("compare values  ", 3d, d_getLInfDistance_3, normTolerance);

        //octave =  v1 + v2
        ArrayRealVector v_add = v1.add(v2);
        double[] result_add = {5d, 7d, 9d};
        assertClose("compare vect" ,v_add.toArray(), result_add, normTolerance);

        RealVectorTestImpl vt2 = new RealVectorTestImpl(vec2);
        RealVector v_add_i = v1.add(vt2);
        double[] result_add_i = {5d, 7d, 9d};
        assertClose("compare vect" ,v_add_i.toArray(),result_add_i,normTolerance);

        //octave =  v1 - v2
        ArrayRealVector v_subtract = v1.subtract(v2);
        double[] result_subtract = {-3d, -3d, -3d};
        assertClose("compare vect" ,v_subtract.toArray(),result_subtract,normTolerance);

        RealVector v_subtract_i = v1.subtract(vt2);
        double[] result_subtract_i = {-3d, -3d, -3d};
        assertClose("compare vect" ,v_subtract_i.toArray(),result_subtract_i,normTolerance);

        // octave v1 .* v2
        ArrayRealVector  v_ebeMultiply = v1.ebeMultiply(v2);
        double[] result_ebeMultiply = {4d, 10d, 18d};
        assertClose("compare vect" ,v_ebeMultiply.toArray(),result_ebeMultiply,normTolerance);

        RealVector  v_ebeMultiply_2 = v1.ebeMultiply(v2_t);
        double[] result_ebeMultiply_2 = {4d, 10d, 18d};
        assertClose("compare vect" ,v_ebeMultiply_2.toArray(),result_ebeMultiply_2,normTolerance);

        RealVector  v_ebeMultiply_3 = v1.ebeMultiply(v2);
        double[] result_ebeMultiply_3 = {4d, 10d, 18d};
        assertClose("compare vect" ,v_ebeMultiply_3.toArray(),result_ebeMultiply_3,normTolerance);

        // octave v1 ./ v2
        ArrayRealVector  v_ebeDivide = v1.ebeDivide(v2);
        double[] result_ebeDivide = {0.25d, 0.4d, 0.5d};
        assertClose("compare vect" ,v_ebeDivide.toArray(),result_ebeDivide,normTolerance);

        RealVector  v_ebeDivide_2 = v1.ebeDivide(v2_t);
        double[] result_ebeDivide_2 = {0.25d, 0.4d, 0.5d};
        assertClose("compare vect" ,v_ebeDivide_2.toArray(),result_ebeDivide_2,normTolerance);

        RealVector  v_ebeDivide_3 = v1.ebeDivide(v2);
        double[] result_ebeDivide_3 = {0.25d, 0.4d, 0.5d};
        assertClose("compare vect" ,v_ebeDivide_3.toArray(),result_ebeDivide_3,normTolerance);

        // octave  dot(v1,v2)
        double dot =  v1.dotProduct(v2);
        Assert.assertEquals("compare val ",32d, dot, normTolerance);

        // octave  dot(v1,v2_t)
        double dot_2 =  v1.dotProduct(v2_t);
        Assert.assertEquals("compare val ",32d, dot_2, normTolerance);

        RealMatrix m_outerProduct = v1.outerProduct(v2);
        Assert.assertEquals("compare val ",4d, m_outerProduct.getEntry(0,0), normTolerance);

        RealMatrix m_outerProduct_2 = v1.outerProduct(v2_t);
        Assert.assertEquals("compare val ",4d, m_outerProduct_2.getEntry(0,0), normTolerance);

        RealMatrix m_outerProduct_3 = v1.outerProduct(v2);
        Assert.assertEquals("compare val ",4d, m_outerProduct_3.getEntry(0,0), normTolerance);

        RealVector v_unitVector = v1.unitVector();
        RealVector v_unitVector_2 = v1.mapDivide(v1.getNorm());
        assertClose("compare vect" ,v_unitVector.toArray(),v_unitVector_2.toArray(),normTolerance);

        try {
            v_null.unitVector();
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // expected behavior
        }

        ArrayRealVector v_unitize = v1.copy();
        v_unitize.unitize();
        assertClose("compare vect" ,v_unitVector_2.toArray(),v_unitize.toArray(),normTolerance);
        try {
            v_null.unitize();
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // expected behavior
        }

        RealVector v_projection = v1.projection(v2);
        double[] result_projection = {1.662337662337662, 2.0779220779220777, 2.493506493506493};
        assertClose("compare vect", v_projection.toArray(), result_projection, normTolerance);

        RealVector v_projection_2 = v1.projection(v2_t);
        double[] result_projection_2 = {1.662337662337662, 2.0779220779220777, 2.493506493506493};
        assertClose("compare vect", v_projection_2.toArray(), result_projection_2, normTolerance);
    }

    @Test
    public void testMisc() {
        ArrayRealVector v1 = new ArrayRealVector(vec1);
        ArrayRealVector v4 = new ArrayRealVector(vec4);
        RealVector v4_2 = new ArrayRealVector(vec4);

        String out1 = v1.toString();
        Assert.assertTrue("some output ",  out1.length()!=0);
        /*
         double[] dout1 = v1.copyOut();
        Assert.assertEquals("testData len", 3, dout1.length);
        Assert.assertNotSame("testData not same object ", v1.getDataRef(), dout1);
         */
        try {
            v1.checkVectorDimensions(2);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

       try {
            v1.checkVectorDimensions(v4);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

        try {
            v1.checkVectorDimensions(v4_2);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

    }

    @Test
    public void testPredicates() {

        ArrayRealVector v = new ArrayRealVector(new double[] { 0, 1, 2 });

        Assert.assertFalse(v.isNaN());
        v.setEntry(1, Double.NaN);
        Assert.assertTrue(v.isNaN());

        Assert.assertFalse(v.isInfinite());
        v.setEntry(0, Double.POSITIVE_INFINITY);
        Assert.assertFalse(v.isInfinite());
        v.setEntry(1, 1);
        Assert.assertTrue(v.isInfinite());
        v.setEntry(0, 1);
        Assert.assertFalse(v.isInfinite());

        v.setEntry(0, 0);
        Assert.assertEquals(v, new ArrayRealVector(new double[] { 0, 1, 2 }));
        Assert.assertNotSame(v, new ArrayRealVector(new double[] { 0, 1, 2 + FastMath.ulp(2)}));
        Assert.assertNotSame(v, new ArrayRealVector(new double[] { 0, 1, 2, 3 }));

        Assert.assertEquals(new ArrayRealVector(new double[] { Double.NaN, 1, 2 }).hashCode(),
                     new ArrayRealVector(new double[] { 0, Double.NaN, 2 }).hashCode());

        Assert.assertTrue(new ArrayRealVector(new double[] { Double.NaN, 1, 2 }).hashCode() !=
                   new ArrayRealVector(new double[] { 0, 1, 2 }).hashCode());

        Assert.assertTrue(v.equals(v));
        Assert.assertTrue(v.equals(v.copy()));
        Assert.assertFalse(v.equals(null));
        Assert.assertFalse(v.equals(v.getDataRef()));
        Assert.assertFalse(v.equals(v.getSubVector(0, v.getDimension() - 1)));
        Assert.assertTrue(v.equals(v.getSubVector(0, v.getDimension())));

    }

    @Test
    public void testSerial()  {
        ArrayRealVector v = new ArrayRealVector(new double[] { 0, 1, 2 });
        Assert.assertEquals(v,TestUtils.serializeAndRecover(v));
    }

    @Test
    public void testZeroVectors() {
        Assert.assertEquals(0, new ArrayRealVector(new double[0]).getDimension());
        Assert.assertEquals(0, new ArrayRealVector(new double[0], true).getDimension());
        Assert.assertEquals(0, new ArrayRealVector(new double[0], false).getDimension());
    }

    @Test
    public void testMinMax()  {
        ArrayRealVector v1 = new ArrayRealVector(new double[] { 0, -6, 4, 12, 7 });
        Assert.assertEquals(1,  v1.getMinIndex());
        Assert.assertEquals(-6, v1.getMinValue(), 1.0e-12);
        Assert.assertEquals(3,  v1.getMaxIndex());
        Assert.assertEquals(12, v1.getMaxValue(), 1.0e-12);
        ArrayRealVector v2 = new ArrayRealVector(new double[] { Double.NaN, 3, Double.NaN, -2 });
        Assert.assertEquals(3,  v2.getMinIndex());
        Assert.assertEquals(-2, v2.getMinValue(), 1.0e-12);
        Assert.assertEquals(1,  v2.getMaxIndex());
        Assert.assertEquals(3, v2.getMaxValue(), 1.0e-12);
        ArrayRealVector v3 = new ArrayRealVector(new double[] { Double.NaN, Double.NaN });
        Assert.assertEquals(-1,  v3.getMinIndex());
        Assert.assertTrue(Double.isNaN(v3.getMinValue()));
        Assert.assertEquals(-1,  v3.getMaxIndex());
        Assert.assertTrue(Double.isNaN(v3.getMaxValue()));
        ArrayRealVector v4 = new ArrayRealVector(new double[0]);
        Assert.assertEquals(-1,  v4.getMinIndex());
        Assert.assertTrue(Double.isNaN(v4.getMinValue()));
        Assert.assertEquals(-1,  v4.getMaxIndex());
        Assert.assertTrue(Double.isNaN(v4.getMaxValue()));
    }

    @Test
    public void testCosine() {
        final ArrayRealVector v = new ArrayRealVector(new double[] {1, 0, 0});

        double[] wData = new double[] {1, 1, 0};
        RealVector w = new ArrayRealVector(wData);
        Assert.assertEquals(FastMath.sqrt(2) / 2, v.cosine(w), normTolerance);

        wData = new double[] {1, 0, 0};
        w = new ArrayRealVector(wData);
        Assert.assertEquals(1, v.cosine(w), normTolerance);

        wData = new double[] {0, 1, 0};
        w = new ArrayRealVector(wData);
        Assert.assertEquals(0, v.cosine(w), 0);

        wData = new double[] {-1, 0, 0};
        w = new ArrayRealVector(wData);
        Assert.assertEquals(-1, v.cosine(w), normTolerance);
    }

    @Test(expected=MathArithmeticException.class)
    public void testCosinePrecondition1() {
        final ArrayRealVector v = new ArrayRealVector(new double[] {0, 0, 0});
        final ArrayRealVector w = new ArrayRealVector(new double[] {1, 0, 0});
        v.cosine(w);
    }
    @Test(expected=MathArithmeticException.class)
    public void testCosinePrecondition2() {
        final ArrayRealVector v = new ArrayRealVector(new double[] {0, 0, 0});
        final ArrayRealVector w = new ArrayRealVector(new double[] {1, 0, 0});
        w.cosine(v);
    }
    @Test(expected=DimensionMismatchException.class)
    public void testCosinePrecondition3() {
        final ArrayRealVector v = new ArrayRealVector(new double[] {1, 2, 3});
        final ArrayRealVector w = new ArrayRealVector(new double[] {1, 2, 3, 4});
        v.cosine(w);
    }

    @Test
    public void testOuterProduct() {
        final ArrayRealVector u = new ArrayRealVector(new double[] {1, 2, -3});
        final ArrayRealVector v = new ArrayRealVector(new double[] {4, -2});

        final RealMatrix uv = u.outerProduct(v);

        final double tol = Math.ulp(1d);
        Assert.assertEquals(4, uv.getEntry(0, 0), tol);
        Assert.assertEquals(-2, uv.getEntry(0, 1), tol);
        Assert.assertEquals(8, uv.getEntry(1, 0), tol);
        Assert.assertEquals(-4, uv.getEntry(1, 1), tol);
        Assert.assertEquals(-12, uv.getEntry(2, 0), tol);
        Assert.assertEquals(6, uv.getEntry(2, 1), tol);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCombinePreconditionSameType() {
        final double a = 1d;
        final double b = 2d;
        double[] aux = new double[] { 3d, 4d, 5d };
        final RealVector x = new ArrayRealVector(aux, false);
        aux = new double[] { 6d, 7d };
        final RealVector y = new ArrayRealVector(aux, false);
        x.combine(a, b, y);
    }

    @Test
    public void testCombineSameType() {
        final Random random = new Random(20110726);
        final int dim = 10;
        final double a = (2 * random.nextDouble() - 1);
        final double b = (2 * random.nextDouble() - 1);
        final RealVector x = new ArrayRealVector(dim);
        final RealVector y = new ArrayRealVector(dim);
        final double[] expected = new double[dim];
        for (int i = 0; i < dim; i++) {
            final double xi = 2 * random.nextDouble() - 1;
            final double yi = 2 * random.nextDouble() - 1;
            x.setEntry(i, xi);
            y.setEntry(i, yi);
            expected[i] = a * xi + b * yi;
        }
        final double[] actual = x.combine(a, b, y).toArray();
        for (int i = 0; i < dim; i++) {
            final double delta;
            if (expected[i] == 0d) {
                delta = Math.ulp(1d);
            } else {
                delta = Math.ulp(expected[i]);
            }
            Assert.assertEquals("elements [" + i + "] differ",
                                expected[i],
                                actual[i],
                                delta);
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCombinePreconditionMixedType() {
        final double a = 1d;
        final double b = 2d;
        double[] aux = new double[] { 3d, 4d, 5d };
        final RealVector x = new ArrayRealVector(aux, false);
        aux = new double[] { 6d, 7d };
        final RealVector y = new OpenMapRealVector(aux);
        x.combine(a, b, y);
    }

    @Test
    public void testCombineMixedTypes() {
        final Random random = new Random(20110726);
        final int dim = 10;
        final double a = (2 * random.nextDouble() - 1);
        final double b = (2 * random.nextDouble() - 1);
        final RealVector x = new ArrayRealVector(dim);
        final RealVector y = new OpenMapRealVector(dim, 0d);
        final double[] expected = new double[dim];
        for (int i = 0; i < dim; i++) {
            final double xi = 2 * random.nextDouble() - 1;
            final double yi = 2 * random.nextDouble() - 1;
            x.setEntry(i, xi);
            y.setEntry(i, yi);
            expected[i] = a * xi + b * yi;
        }
        final double[] actual = x.combine(a, b, y).toArray();
        for (int i = 0; i < dim; i++) {
            final double delta;
            if (expected[i] == 0d) {
                delta = Math.ulp(1d);
            } else {
                delta = Math.ulp(expected[i]);
            }
            Assert.assertEquals("elements [" + i + "] differ",
                                expected[i],
                                actual[i],
                                delta);
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCombineToSelfPreconditionSameType() {
        final double a = 1d;
        final double b = 2d;
        double[] aux = new double[] { 3d, 4d, 5d };
        final RealVector x = new ArrayRealVector(aux, false);
        aux = new double[] { 6d, 7d };
        final RealVector y = new ArrayRealVector(aux, false);
        x.combineToSelf(a, b, y);
    }

    @Test
    public void testCombineToSelfSameType() {
        final Random random = new Random(20110726);
        final int dim = 10;
        final double a = (2 * random.nextDouble() - 1);
        final double b = (2 * random.nextDouble() - 1);
        final RealVector x = new ArrayRealVector(dim);
        final RealVector y = new ArrayRealVector(dim);
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
            Assert.assertEquals("elements [" + i + "] differ",
                                expected[i],
                                actual[i],
                                delta);
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCombineToSelfPreconditionMixedType() {
        final double a = 1d;
        final double b = 2d;
        double[] aux = new double[] { 3d, 4d, 5d };
        final RealVector x = new ArrayRealVector(aux, false);
        aux = new double[] { 6d, 7d };
        final RealVector y = new OpenMapRealVector(aux);
        x.combineToSelf(a, b, y);
    }

    @Test
    public void testCombineToSelfMixedTypes() {
        final Random random = new Random(20110726);
        final int dim = 10;
        final double a = (2 * random.nextDouble() - 1);
        final double b = (2 * random.nextDouble() - 1);
        final RealVector x = new ArrayRealVector(dim);
        final RealVector y = new OpenMapRealVector(dim, 0d);
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
            Assert.assertEquals("elements [" + i + "] differ",
                                expected[i],
                                actual[i],
                                delta);
        }
    }

    @Test
    public void testAddToEntry() {
        final double[] v = new double[] { 1, 2, 3 };
        final ArrayRealVector x = new ArrayRealVector(v);
        final double inc = 7;
        for (int i = 0; i < x.getDimension(); i++) {
            x.addToEntry(i, inc);
        }
        for (int i = 0; i < x.getDimension(); i++) {
            Assert.assertEquals(v[i] + inc, x.getEntry(i), 0);
        }
    }

    @Override
    public RealVector create(final double[] data) {
        return new ArrayRealVector(data, true);
    }

    @Override
    public RealVector createAlien(double[] data) {
        return new RealVectorTestImpl(data);
    }
}
