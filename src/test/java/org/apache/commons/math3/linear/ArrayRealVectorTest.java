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
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link ArrayRealVector} class.
 *
 * @version $Id$
 */
public class ArrayRealVectorTest {

    //
    protected double[][] ma1 = {{1d, 2d, 3d}, {4d, 5d, 6d}, {7d, 8d, 9d}};
    protected double[] vec1 = {1d, 2d, 3d};
    protected double[] vec2 = {4d, 5d, 6d};
    protected double[] vec3 = {7d, 8d, 9d};
    protected double[] vec4 = {1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};
    protected double[] vec5 = { -4d, 0d, 3d, 1d, -6d, 3d};
    protected double[] vec_null = {0d, 0d, 0d};
    protected Double[] dvec1 = {1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};
    protected double[][] mat1 = {{1d, 2d, 3d}, {4d, 5d, 6d},{ 7d, 8d, 9d}};

    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;

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

    @Test
    public void testDataInOut() {

        ArrayRealVector v1 = new ArrayRealVector(vec1);
        ArrayRealVector v2 = new ArrayRealVector(vec2);
        ArrayRealVector v4 = new ArrayRealVector(vec4);
        RealVectorTestImpl v2_t = new RealVectorTestImpl(vec2);

        RealVector v_append_1 = v1.append(v2);
        Assert.assertEquals("testData len", 6, v_append_1.getDimension());
        Assert.assertEquals("testData is 4.0 ", 4.0, v_append_1.getEntry(3), 0);

        RealVector v_append_2 = v1.append(2.0);
        Assert.assertEquals("testData len", 4, v_append_2.getDimension());
        Assert.assertEquals("testData is 2.0 ", 2.0, v_append_2.getEntry(3), 0);

        RealVector v_append_4 = v1.append(v2_t);
        Assert.assertEquals("testData len", 6, v_append_4.getDimension());
        Assert.assertEquals("testData is 4.0 ", 4.0, v_append_4.getEntry(3), 0);

        RealVector v_append_5 = v1.append((RealVector) v2);
        Assert.assertEquals("testData len", 6, v_append_5.getDimension());
        Assert.assertEquals("testData is 4.0 ", 4.0, v_append_5.getEntry(3), 0);

        RealVector v_copy = v1.copy();
        Assert.assertEquals("testData len", 3, v_copy.getDimension());
        Assert.assertNotSame("testData not same object ", v1.getDataRef(), v_copy.toArray());

        double[] a_double = v1.toArray();
        Assert.assertEquals("testData len", 3, a_double.length);
        Assert.assertNotSame("testData not same object ", v1.getDataRef(), a_double);


//      ArrayRealVector vout4 = (ArrayRealVector) v1.clone();
//      Assert.assertEquals("testData len", 3, vout4.getDimension());
//      Assert.assertEquals("testData not same object ", v1.getDataRef(), vout4.getDataRef());


        RealVector vout5 = v4.getSubVector(3, 3);
        Assert.assertEquals("testData len", 3, vout5.getDimension());
        Assert.assertEquals("testData is 4.0 ", 5.0, vout5.getEntry(1), 0);
        try {
            v4.getSubVector(3, 7);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        ArrayRealVector v_set1 = v1.copy();
        v_set1.setEntry(1, 11.0);
        Assert.assertEquals("testData is 11.0 ", 11.0, v_set1.getEntry(1), 0);
        try {
            v_set1.setEntry(3, 11.0);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        ArrayRealVector v_set3 = v1.copy();
        v_set3.set(13.0);
        Assert.assertEquals("testData is 13.0 ", 13.0, v_set3.getEntry(2), 0);

        try {
            v_set3.getEntry(23);
            Assert.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected behavior
        }

        ArrayRealVector v_set4 = v4.copy();
        v_set4.setSubVector(3, v2_t);
        Assert.assertEquals("testData is 1.0 ", 4.0, v_set4.getEntry(3), 0);
        Assert.assertEquals("testData is 7.0 ", 7.0, v_set4.getEntry(6), 0);
        try {
            v_set4.setSubVector(7, v2_t);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }


        ArrayRealVector vout10 = v1.copy();
        ArrayRealVector vout10_2 = v1.copy();
        Assert.assertEquals(vout10, vout10_2);
        vout10_2.setEntry(0, 1.1);
        Assert.assertNotSame(vout10, vout10_2);

    }

    @Test
    public void testMapFunctions() {
        ArrayRealVector v1 = new ArrayRealVector(vec1);

        //octave =  v1 .+ 2.0
        RealVector v_mapAdd = v1.mapAdd(2.0d);
        double[] result_mapAdd = {3d, 4d, 5d};
        assertClose("compare vectors" ,result_mapAdd,v_mapAdd.toArray(),normTolerance);

        //octave =  v1 .+ 2.0
        RealVector v_mapAddToSelf = v1.copy();
        v_mapAddToSelf.mapAddToSelf(2.0d);
        double[] result_mapAddToSelf = {3d, 4d, 5d};
        assertClose("compare vectors" ,result_mapAddToSelf,v_mapAddToSelf.toArray(),normTolerance);

        //octave =  v1 .- 2.0
        RealVector v_mapSubtract = v1.mapSubtract(2.0d);
        double[] result_mapSubtract = {-1d, 0d, 1d};
        assertClose("compare vectors" ,result_mapSubtract,v_mapSubtract.toArray(),normTolerance);

        //octave =  v1 .- 2.0
        RealVector v_mapSubtractToSelf = v1.copy();
        v_mapSubtractToSelf.mapSubtractToSelf(2.0d);
        double[] result_mapSubtractToSelf = {-1d, 0d, 1d};
        assertClose("compare vectors" ,result_mapSubtractToSelf,v_mapSubtractToSelf.toArray(),normTolerance);

        //octave =  v1 .* 2.0
        RealVector v_mapMultiply = v1.mapMultiply(2.0d);
        double[] result_mapMultiply = {2d, 4d, 6d};
        assertClose("compare vectors" ,result_mapMultiply,v_mapMultiply.toArray(),normTolerance);

        //octave =  v1 .* 2.0
        RealVector v_mapMultiplyToSelf = v1.copy();
        v_mapMultiplyToSelf.mapMultiplyToSelf(2.0d);
        double[] result_mapMultiplyToSelf = {2d, 4d, 6d};
        assertClose("compare vectors" ,result_mapMultiplyToSelf,v_mapMultiplyToSelf.toArray(),normTolerance);

        //octave =  v1 ./ 2.0
        RealVector v_mapDivide = v1.mapDivide(2.0d);
        double[] result_mapDivide = {.5d, 1d, 1.5d};
        assertClose("compare vectors" ,result_mapDivide,v_mapDivide.toArray(),normTolerance);

        //octave =  v1 ./ 2.0
        RealVector v_mapDivideToSelf = v1.copy();
        v_mapDivideToSelf.mapDivideToSelf(2.0d);
        double[] result_mapDivideToSelf = {.5d, 1d, 1.5d};
        assertClose("compare vectors" ,result_mapDivideToSelf,v_mapDivideToSelf.toArray(),normTolerance);

        //octave =  v1 .^ 2.0
        RealVector v_mapPow = v1.map(new Power(2));
        double[] result_mapPow = {1d, 4d, 9d};
        assertClose("compare vectors" ,result_mapPow,v_mapPow.toArray(),normTolerance);

        //octave =  v1 .^ 2.0
        RealVector v_mapPowToSelf = v1.copy();
        v_mapPowToSelf.mapToSelf(new Power(2));
        double[] result_mapPowToSelf = {1d, 4d, 9d};
        assertClose("compare vectors" ,result_mapPowToSelf,v_mapPowToSelf.toArray(),normTolerance);

        //octave =  exp(v1)
        RealVector v_mapExp = v1.map(new Exp());
        double[] result_mapExp = {2.718281828459045e+00d,7.389056098930650e+00d, 2.008553692318767e+01d};
        assertClose("compare vectors" ,result_mapExp,v_mapExp.toArray(),normTolerance);

        //octave =  exp(v1)
        RealVector v_mapExpToSelf = v1.copy();
        v_mapExpToSelf.mapToSelf(new Exp());
        double[] result_mapExpToSelf = {2.718281828459045e+00d,7.389056098930650e+00d, 2.008553692318767e+01d};
        assertClose("compare vectors" ,result_mapExpToSelf,v_mapExpToSelf.toArray(),normTolerance);


        //octave =  ???
        RealVector v_mapExpm1 = v1.map(new Expm1());
        double[] result_mapExpm1 = {1.718281828459045d,6.38905609893065d, 19.085536923187668d};
        assertClose("compare vectors" ,result_mapExpm1,v_mapExpm1.toArray(),normTolerance);

        //octave =  ???
        RealVector v_mapExpm1ToSelf = v1.copy();
        v_mapExpm1ToSelf.mapToSelf(new Expm1());
        double[] result_mapExpm1ToSelf = {1.718281828459045d,6.38905609893065d, 19.085536923187668d};
        assertClose("compare vectors" ,result_mapExpm1ToSelf,v_mapExpm1ToSelf.toArray(),normTolerance);

        //octave =  log(v1)
        RealVector v_mapLog = v1.map(new Log());
        double[] result_mapLog = {0d,6.931471805599453e-01d, 1.098612288668110e+00d};
        assertClose("compare vectors" ,result_mapLog,v_mapLog.toArray(),normTolerance);

        //octave =  log(v1)
        RealVector v_mapLogToSelf = v1.copy();
        v_mapLogToSelf.mapToSelf(new Log());
        double[] result_mapLogToSelf = {0d,6.931471805599453e-01d, 1.098612288668110e+00d};
        assertClose("compare vectors" ,result_mapLogToSelf,v_mapLogToSelf.toArray(),normTolerance);

        //octave =  log10(v1)
        RealVector v_mapLog10 = v1.map(new Log10());
        double[] result_mapLog10 = {0d,3.010299956639812e-01d, 4.771212547196624e-01d};
        assertClose("compare vectors" ,result_mapLog10,v_mapLog10.toArray(),normTolerance);

        //octave =  log(v1)
        RealVector v_mapLog10ToSelf = v1.copy();
        v_mapLog10ToSelf.mapToSelf(new Log10());
        double[] result_mapLog10ToSelf = {0d,3.010299956639812e-01d, 4.771212547196624e-01d};
        assertClose("compare vectors" ,result_mapLog10ToSelf,v_mapLog10ToSelf.toArray(),normTolerance);

        //octave =  ???
        RealVector v_mapLog1p = v1.map(new Log1p());
        double[] result_mapLog1p = {0.6931471805599453d,1.0986122886681096d,1.3862943611198906d};
        assertClose("compare vectors" ,result_mapLog1p,v_mapLog1p.toArray(),normTolerance);

        //octave =  ???
        RealVector v_mapLog1pToSelf = v1.copy();
        v_mapLog1pToSelf.mapToSelf(new Log1p());
        double[] result_mapLog1pToSelf = {0.6931471805599453d,1.0986122886681096d,1.3862943611198906d};
        assertClose("compare vectors" ,result_mapLog1pToSelf,v_mapLog1pToSelf.toArray(),normTolerance);

        //octave =  cosh(v1)
        RealVector v_mapCosh = v1.map(new Cosh());
        double[] result_mapCosh = {1.543080634815244e+00d,3.762195691083631e+00d, 1.006766199577777e+01d};
        assertClose("compare vectors" ,result_mapCosh,v_mapCosh.toArray(),normTolerance);

        //octave =  cosh(v1)
        RealVector v_mapCoshToSelf = v1.copy();
        v_mapCoshToSelf.mapToSelf(new Cosh());
        double[] result_mapCoshToSelf = {1.543080634815244e+00d,3.762195691083631e+00d, 1.006766199577777e+01d};
        assertClose("compare vectors" ,result_mapCoshToSelf,v_mapCoshToSelf.toArray(),normTolerance);

        //octave =  sinh(v1)
        RealVector v_mapSinh = v1.map(new Sinh());
        double[] result_mapSinh = {1.175201193643801e+00d,3.626860407847019e+00d, 1.001787492740990e+01d};
        assertClose("compare vectors" ,result_mapSinh,v_mapSinh.toArray(),normTolerance);

        //octave =  sinh(v1)
        RealVector v_mapSinhToSelf = v1.copy();
        v_mapSinhToSelf.mapToSelf(new Sinh());
        double[] result_mapSinhToSelf = {1.175201193643801e+00d,3.626860407847019e+00d, 1.001787492740990e+01d};
        assertClose("compare vectors" ,result_mapSinhToSelf,v_mapSinhToSelf.toArray(),normTolerance);

        //octave =  tanh(v1)
        RealVector v_mapTanh = v1.map(new Tanh());
        double[] result_mapTanh = {7.615941559557649e-01d,9.640275800758169e-01d,9.950547536867305e-01d};
        assertClose("compare vectors" ,result_mapTanh,v_mapTanh.toArray(),normTolerance);

        //octave =  tanh(v1)
        RealVector v_mapTanhToSelf = v1.copy();
        v_mapTanhToSelf.mapToSelf(new Tanh());
        double[] result_mapTanhToSelf = {7.615941559557649e-01d,9.640275800758169e-01d,9.950547536867305e-01d};
        assertClose("compare vectors" ,result_mapTanhToSelf,v_mapTanhToSelf.toArray(),normTolerance);

        //octave =  cos(v1)
        RealVector v_mapCos = v1.map(new Cos());
        double[] result_mapCos = {5.403023058681398e-01d,-4.161468365471424e-01d, -9.899924966004454e-01d};
        assertClose("compare vectors" ,result_mapCos,v_mapCos.toArray(),normTolerance);

        //octave =  cos(v1)
        RealVector v_mapCosToSelf = v1.copy();
        v_mapCosToSelf.mapToSelf(new Cos());
        double[] result_mapCosToSelf = {5.403023058681398e-01d,-4.161468365471424e-01d, -9.899924966004454e-01d};
        assertClose("compare vectors" ,result_mapCosToSelf,v_mapCosToSelf.toArray(),normTolerance);

        //octave =  sin(v1)
        RealVector v_mapSin = v1.map(new Sin());
        double[] result_mapSin = {8.414709848078965e-01d,9.092974268256817e-01d,1.411200080598672e-01d};
        assertClose("compare vectors" ,result_mapSin,v_mapSin.toArray(),normTolerance);

        //octave =  sin(v1)
        RealVector v_mapSinToSelf = v1.copy();
        v_mapSinToSelf.mapToSelf(new Sin());
        double[] result_mapSinToSelf = {8.414709848078965e-01d,9.092974268256817e-01d,1.411200080598672e-01d};
        assertClose("compare vectors" ,result_mapSinToSelf,v_mapSinToSelf.toArray(),normTolerance);

        //octave =  tan(v1)
        RealVector v_mapTan = v1.map(new Tan());
        double[] result_mapTan = {1.557407724654902e+00d,-2.185039863261519e+00d,-1.425465430742778e-01d};
        assertClose("compare vectors" ,result_mapTan,v_mapTan.toArray(),normTolerance);

        //octave =  tan(v1)
        RealVector v_mapTanToSelf = v1.copy();
        v_mapTanToSelf.mapToSelf(new Tan());
        double[] result_mapTanToSelf = {1.557407724654902e+00d,-2.185039863261519e+00d,-1.425465430742778e-01d};
        assertClose("compare vectors" ,result_mapTanToSelf,v_mapTanToSelf.toArray(),normTolerance);

        double[] vat_a = {0d, 0.5d, 1.0d};
        ArrayRealVector vat = new ArrayRealVector(vat_a);

        //octave =  acos(vat)
        RealVector v_mapAcos = vat.map(new Acos());
        double[] result_mapAcos = {1.570796326794897e+00d,1.047197551196598e+00d, 0.0d};
        assertClose("compare vectors" ,result_mapAcos,v_mapAcos.toArray(),normTolerance);

        //octave =  acos(vat)
        RealVector v_mapAcosToSelf = vat.copy();
        v_mapAcosToSelf.mapToSelf(new Acos());
        double[] result_mapAcosToSelf = {1.570796326794897e+00d,1.047197551196598e+00d, 0.0d};
        assertClose("compare vectors" ,result_mapAcosToSelf,v_mapAcosToSelf.toArray(),normTolerance);

        //octave =  asin(vat)
        RealVector v_mapAsin = vat.map(new Asin());
        double[] result_mapAsin = {0.0d,5.235987755982989e-01d,1.570796326794897e+00d};
        assertClose("compare vectors" ,result_mapAsin,v_mapAsin.toArray(),normTolerance);

        //octave =  asin(vat)
        RealVector v_mapAsinToSelf = vat.copy();
        v_mapAsinToSelf.mapToSelf(new Asin());
        double[] result_mapAsinToSelf = {0.0d,5.235987755982989e-01d,1.570796326794897e+00d};
        assertClose("compare vectors" ,result_mapAsinToSelf,v_mapAsinToSelf.toArray(),normTolerance);

        //octave =  atan(vat)
        RealVector v_mapAtan = vat.map(new Atan());
        double[] result_mapAtan = {0.0d,4.636476090008061e-01d,7.853981633974483e-01d};
        assertClose("compare vectors" ,result_mapAtan,v_mapAtan.toArray(),normTolerance);

        //octave =  atan(vat)
        RealVector v_mapAtanToSelf = vat.copy();
        v_mapAtanToSelf.mapToSelf(new Atan());
        double[] result_mapAtanToSelf = {0.0d,4.636476090008061e-01d,7.853981633974483e-01d};
        assertClose("compare vectors" ,result_mapAtanToSelf,v_mapAtanToSelf.toArray(),normTolerance);

        //octave =  v1 .^-1
        RealVector v_mapInv = v1.map(new Inverse());
        double[] result_mapInv = {1d,0.5d,3.333333333333333e-01d};
        assertClose("compare vectors" ,result_mapInv,v_mapInv.toArray(),normTolerance);

        //octave =  v1 .^-1
        RealVector v_mapInvToSelf = v1.copy();
        v_mapInvToSelf.mapToSelf(new Inverse());
        double[] result_mapInvToSelf = {1d,0.5d,3.333333333333333e-01d};
        assertClose("compare vectors" ,result_mapInvToSelf,v_mapInvToSelf.toArray(),normTolerance);

        double[] abs_a = {-1.0d, 0.0d, 1.0d};
        ArrayRealVector abs_v = new ArrayRealVector(abs_a);

        //octave =  abs(abs_v)
        RealVector v_mapAbs = abs_v.map(new Abs());
        double[] result_mapAbs = {1d,0d,1d};
        assertClose("compare vectors" ,result_mapAbs,v_mapAbs.toArray(),normTolerance);

        //octave = abs(abs_v)
        RealVector v_mapAbsToSelf = abs_v.copy();
        v_mapAbsToSelf.mapToSelf(new Abs());
        double[] result_mapAbsToSelf = {1d,0d,1d};
        assertClose("compare vectors" ,result_mapAbsToSelf,v_mapAbsToSelf.toArray(),normTolerance);

        //octave =   sqrt(v1)
        RealVector v_mapSqrt = v1.map(new Sqrt());
        double[] result_mapSqrt = {1d,1.414213562373095e+00d,1.732050807568877e+00d};
        assertClose("compare vectors" ,result_mapSqrt,v_mapSqrt.toArray(),normTolerance);

        //octave =  sqrt(v1)
        RealVector v_mapSqrtToSelf = v1.copy();
        v_mapSqrtToSelf.mapToSelf(new Sqrt());
        double[] result_mapSqrtToSelf = {1d,1.414213562373095e+00d,1.732050807568877e+00d};
        assertClose("compare vectors" ,result_mapSqrtToSelf,v_mapSqrtToSelf.toArray(),normTolerance);

        double[] cbrt_a = {-2.0d, 0.0d, 2.0d};
        ArrayRealVector cbrt_v = new ArrayRealVector(cbrt_a);

        //octave =  ???
        RealVector v_mapCbrt = cbrt_v.map(new Cbrt());
        double[] result_mapCbrt = {-1.2599210498948732d,0d,1.2599210498948732d};
        assertClose("compare vectors" ,result_mapCbrt,v_mapCbrt.toArray(),normTolerance);

        //octave = ???
        RealVector v_mapCbrtToSelf = cbrt_v.copy();
        v_mapCbrtToSelf.mapToSelf(new Cbrt());
        double[] result_mapCbrtToSelf =  {-1.2599210498948732d,0d,1.2599210498948732d};
        assertClose("compare vectors" ,result_mapCbrtToSelf,v_mapCbrtToSelf.toArray(),normTolerance);

        double[] ceil_a = {-1.1d, 0.9d, 1.1d};
        ArrayRealVector ceil_v = new ArrayRealVector(ceil_a);

        //octave =  ceil(ceil_v)
        RealVector v_mapCeil = ceil_v.map(new Ceil());
        double[] result_mapCeil = {-1d,1d,2d};
        assertClose("compare vectors" ,result_mapCeil,v_mapCeil.toArray(),normTolerance);

        //octave = ceil(ceil_v)
        RealVector v_mapCeilToSelf = ceil_v.copy();
        v_mapCeilToSelf.mapToSelf(new Ceil());
        double[] result_mapCeilToSelf =  {-1d,1d,2d};
        assertClose("compare vectors" ,result_mapCeilToSelf,v_mapCeilToSelf.toArray(),normTolerance);

        //octave =  floor(ceil_v)
        RealVector v_mapFloor = ceil_v.map(new Floor());
        double[] result_mapFloor = {-2d,0d,1d};
        assertClose("compare vectors" ,result_mapFloor,v_mapFloor.toArray(),normTolerance);

        //octave = floor(ceil_v)
        RealVector v_mapFloorToSelf = ceil_v.copy();
        v_mapFloorToSelf.mapToSelf(new Floor());
        double[] result_mapFloorToSelf =  {-2d,0d,1d};
        assertClose("compare vectors" ,result_mapFloorToSelf,v_mapFloorToSelf.toArray(),normTolerance);

        //octave =  ???
        RealVector v_mapRint = ceil_v.map(new Rint());
        double[] result_mapRint = {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapRint,v_mapRint.toArray(),normTolerance);

        //octave = ???
        RealVector v_mapRintToSelf = ceil_v.copy();
        v_mapRintToSelf.mapToSelf(new Rint());
        double[] result_mapRintToSelf =  {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapRintToSelf,v_mapRintToSelf.toArray(),normTolerance);

        //octave =  ???
        RealVector v_mapSignum = ceil_v.map(new Signum());
        double[] result_mapSignum = {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapSignum,v_mapSignum.toArray(),normTolerance);

        //octave = ???
        RealVector v_mapSignumToSelf = ceil_v.copy();
        v_mapSignumToSelf.mapToSelf(new Signum());
        double[] result_mapSignumToSelf =  {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapSignumToSelf,v_mapSignumToSelf.toArray(),normTolerance);


        // Is with the used resolutions of limited value as test
        //octave =  ???
        RealVector v_mapUlp = ceil_v.map(new Ulp());
        double[] result_mapUlp = {2.220446049250313E-16d,1.1102230246251565E-16d,2.220446049250313E-16d};
        assertClose("compare vectors" ,result_mapUlp,v_mapUlp.toArray(),normTolerance);

        //octave = ???
        RealVector v_mapUlpToSelf = ceil_v.copy();
        v_mapUlpToSelf.mapToSelf(new Ulp());
        double[] result_mapUlpToSelf = {2.220446049250313E-16d,1.1102230246251565E-16d,2.220446049250313E-16d};
        assertClose("compare vectors" ,result_mapUlpToSelf,v_mapUlpToSelf.toArray(),normTolerance);
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

    /** verifies that two vectors are close (sup norm) */
    protected void assertClose(String msg, double[] m, double[] n,
            double tolerance) {
        if (m.length != n.length) {
            Assert.fail("vectors have different lengths");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(msg + " " +  i + " elements differ", m[i],n[i],tolerance);
        }
    }
}
