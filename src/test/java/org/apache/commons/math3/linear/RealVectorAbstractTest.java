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

import junit.framework.Assert;

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
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.ArrayRealVectorTest.RealVectorTestImpl;
import org.junit.Test;


public abstract class RealVectorAbstractTest {
    /**
     * Creates a new instance of {@link RealVector}, with specified entries.
     * The returned vector must be of the type currently tested.
     *
     * @param data the entries of the vector to be created
     * @return a new {@link RealVector} of the type to be tested
     */
    public abstract RealVector create(double[] data);

    /**
     * Creates a new instance of {@link RealVector}, with specified entries.
     * The type of the returned vector must be different from the type currently
     * tested.
     *
     * @param data the entries of the vector to be created
     * @return a new {@link RealVector} of an alien type
     */
    public abstract RealVector createAlien(double[] data);

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

    @Test
    public void testDataInOut() {
        final RealVector v1 = create(vec1);
        final RealVector v2 = create(vec2);
        final RealVector v4 = create(vec4);
        final RealVector v2_t = createAlien(vec2);

        final RealVector v_append_1 = v1.append(v2);
        Assert.assertEquals("testData len", 6, v_append_1.getDimension());
        Assert.assertEquals("testData is 4.0 ", 4.0, v_append_1.getEntry(3), 0);

        final RealVector v_append_2 = v1.append(2.0);
        Assert.assertEquals("testData len", 4, v_append_2.getDimension());
        Assert.assertEquals("testData is 2.0 ", 2.0, v_append_2.getEntry(3), 0);

        final RealVector v_append_4 = v1.append(v2_t);
        Assert.assertEquals("testData len", 6, v_append_4.getDimension());
        Assert.assertEquals("testData is 4.0 ", 4.0, v_append_4.getEntry(3), 0);

        final RealVector v_append_5 = v1.append(v2);
        Assert.assertEquals("testData len", 6, v_append_5.getDimension());
        Assert.assertEquals("testData is 4.0 ", 4.0, v_append_5.getEntry(3), 0);

        final RealVector vout5 = v4.getSubVector(3, 3);
        Assert.assertEquals("testData len", 3, vout5.getDimension());
        Assert.assertEquals("testData is 4.0 ", 5.0, vout5.getEntry(1), 0);
        try {
            v4.getSubVector(3, 7);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector v_set1 = v1.copy();
        v_set1.setEntry(1, 11.0);
        Assert.assertEquals("testData is 11.0 ", 11.0, v_set1.getEntry(1), 0);
        try {
            v_set1.setEntry(3, 11.0);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector v_set2 = v4.copy();
        v_set2.setSubVector(3, v1);
        Assert.assertEquals("testData is 1.0 ", 1.0, v_set2.getEntry(3), 0);
        Assert.assertEquals("testData is 7.0 ", 7.0, v_set2.getEntry(6), 0);
        try {
            v_set2.setSubVector(7, v1);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector v_set3 = v1.copy();
        v_set3.set(13.0);
        Assert.assertEquals("testData is 13.0 ", 13.0, v_set3.getEntry(2), 0);

        try {
            v_set3.getEntry(23);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector v_set4 = v4.copy();
        v_set4.setSubVector(3, v2_t);
        Assert.assertEquals("testData is 1.0 ", 4.0, v_set4.getEntry(3), 0);
        Assert.assertEquals("testData is 7.0 ", 7.0, v_set4.getEntry(6), 0);
        try {
            v_set4.setSubVector(7, v2_t);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector vout10 = v1.copy();
        final RealVector vout10_2 = v1.copy();
        Assert.assertEquals(vout10, vout10_2);
        vout10_2.setEntry(0, 1.1);
        Assert.assertNotSame(vout10, vout10_2);
    }

    @Test
    public void testMapFunctions() {
        final RealVector v1 = create(vec1);

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
        final RealVector vat = create(vat_a);

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
        final RealVector abs_v = create(abs_a);

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
        final RealVector cbrt_v = create(cbrt_a);

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
        final RealVector v1 = create(vec1);
        final RealVector v2 = create(vec2);
        final RealVector v5 = create(vec5);
        final RealVector v_null = create(vec_null);

        final RealVector v2_t = createAlien(vec2);

        // emacs calc: [-4, 0, 3, 1, -6, 3] A --> 8.4261497731763586307
        double d_getNorm = v5.getNorm();
        Assert.assertEquals("compare values  ", 8.4261497731763586307,
                            d_getNorm, normTolerance);

        // emacs calc: [-4, 0, 3, 1, -6, 3] vN --> 17
        double d_getL1Norm = v5.getL1Norm();
        Assert.assertEquals("compare values  ", 17.0, d_getL1Norm,
                            normTolerance);

        // emacs calc: [-4, 0, 3, 1, -6, 3] vn --> 6
        double d_getLInfNorm = v5.getLInfNorm();
        Assert.assertEquals("compare values  ", 6.0, d_getLInfNorm,
                            normTolerance);

        // octave = sqrt(sumsq(v1-v2))
        double dist = v1.getDistance(v2);
        Assert.assertEquals("compare values  ", v1.subtract(v2).getNorm(),
                            dist, normTolerance);

        // octave = sqrt(sumsq(v1-v2))
        double dist_2 = v1.getDistance(v2_t);
        Assert.assertEquals("compare values  ", v1.subtract(v2).getNorm(),
                            dist_2, normTolerance);

        // octave = ???
        double d_getL1Distance = v1.getL1Distance(v2);
        Assert.assertEquals("compare values  ", 9d, d_getL1Distance,
                            normTolerance);

        double d_getL1Distance_2 = v1.getL1Distance(v2_t);
        Assert.assertEquals("compare values  ", 9d, d_getL1Distance_2,
                            normTolerance);

        // octave = ???
        double d_getLInfDistance = v1.getLInfDistance(v2);
        Assert.assertEquals("compare values  ", 3d, d_getLInfDistance,
                            normTolerance);

        double d_getLInfDistance_2 = v1.getLInfDistance(v2_t);
        Assert.assertEquals("compare values  ", 3d, d_getLInfDistance_2,
                            normTolerance);

        // octave = v1 + v2
        final RealVector v_add = v1.add(v2);
        double[] result_add = {
            5d, 7d, 9d
        };
        assertClose("compare vect", v_add.toArray(), result_add, normTolerance);

        final RealVector vt2 = createAlien(vec2);
        RealVector v_add_i = v1.add(vt2);
        double[] result_add_i = {
            5d, 7d, 9d
        };
        assertClose("compare vect", v_add_i.toArray(), result_add_i,
                    normTolerance);

        // octave = v1 - v2
        final RealVector v_subtract = v1.subtract(v2);
        double[] result_subtract = {
            -3d, -3d, -3d
        };
        assertClose("compare vect", v_subtract.toArray(), result_subtract,
                    normTolerance);

        final RealVector v_subtract_i = v1.subtract(vt2);
        double[] result_subtract_i = {
            -3d, -3d, -3d
        };
        assertClose("compare vect", v_subtract_i.toArray(), result_subtract_i,
                    normTolerance);

        // octave v1 .* v2
        final RealVector v_ebeMultiply = v1.ebeMultiply(v2);
        double[] result_ebeMultiply = {
            4d, 10d, 18d
        };
        assertClose("compare vect", v_ebeMultiply.toArray(),
                    result_ebeMultiply, normTolerance);

        final RealVector v_ebeMultiply_2 = v1.ebeMultiply(v2_t);
        double[] result_ebeMultiply_2 = {
            4d, 10d, 18d
        };
        assertClose("compare vect", v_ebeMultiply_2.toArray(),
                    result_ebeMultiply_2, normTolerance);

        // octave v1 ./ v2
        final RealVector v_ebeDivide = v1.ebeDivide(v2);
        double[] result_ebeDivide = {
            0.25d, 0.4d, 0.5d
        };
        assertClose("compare vect", v_ebeDivide.toArray(), result_ebeDivide,
                    normTolerance);

        final RealVector v_ebeDivide_2 = v1.ebeDivide(v2_t);
        double[] result_ebeDivide_2 = {
            0.25d, 0.4d, 0.5d
        };
        assertClose("compare vect", v_ebeDivide_2.toArray(),
                    result_ebeDivide_2, normTolerance);

        // octave dot(v1,v2)
        double dot = v1.dotProduct(v2);
        Assert.assertEquals("compare val ", 32d, dot, normTolerance);

        // octave dot(v1,v2_t)
        double dot_2 = v1.dotProduct(v2_t);
        Assert.assertEquals("compare val ", 32d, dot_2, normTolerance);

        RealMatrix m_outerProduct = v1.outerProduct(v2);
        Assert.assertEquals("compare val ", 4d, m_outerProduct.getEntry(0, 0),
                            normTolerance);

        RealMatrix m_outerProduct_2 = v1.outerProduct(v2_t);
        Assert.assertEquals("compare val ", 4d,
                            m_outerProduct_2.getEntry(0, 0), normTolerance);

        RealVector v_unitVector = v1.unitVector();
        RealVector v_unitVector_2 = v1.mapDivide(v1.getNorm());
        assertClose("compare vect", v_unitVector.toArray(),
                    v_unitVector_2.toArray(), normTolerance);

        try {
            v_null.unitVector();
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // expected behavior
        }

        RealVector v_unitize = v1.copy();
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

    /*
     * TESTS OF THE VISITOR PATTERN
     */

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
