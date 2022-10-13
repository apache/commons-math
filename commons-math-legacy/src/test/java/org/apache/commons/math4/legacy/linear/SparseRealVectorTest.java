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

import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.legacy.analysis.function.Abs;
import org.apache.commons.math4.legacy.analysis.function.Acos;
import org.apache.commons.math4.legacy.analysis.function.Asin;
import org.apache.commons.math4.legacy.analysis.function.Atan;
import org.apache.commons.math4.legacy.analysis.function.Cbrt;
import org.apache.commons.math4.legacy.analysis.function.Ceil;
import org.apache.commons.math4.legacy.analysis.function.Cos;
import org.apache.commons.math4.legacy.analysis.function.Cosh;
import org.apache.commons.math4.legacy.analysis.function.Exp;
import org.apache.commons.math4.legacy.analysis.function.Expm1;
import org.apache.commons.math4.legacy.analysis.function.Floor;
import org.apache.commons.math4.legacy.analysis.function.Log1p;
import org.apache.commons.math4.legacy.analysis.function.Power;
import org.apache.commons.math4.legacy.analysis.function.Rint;
import org.apache.commons.math4.legacy.analysis.function.Signum;
import org.apache.commons.math4.legacy.analysis.function.Sin;
import org.apache.commons.math4.legacy.analysis.function.Sinh;
import org.apache.commons.math4.legacy.analysis.function.Sqrt;
import org.apache.commons.math4.legacy.analysis.function.Tan;
import org.apache.commons.math4.legacy.analysis.function.Tanh;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link OpenMapRealVector} class.
 *
 */
public class SparseRealVectorTest extends RealVectorAbstractTest {

    @Override
    public RealVector create(double[] data) {
        return new OpenMapRealVector(data);
    }

    @Test
    public void testConstructors() {
        final double[] vec1 = {1d, 2d, 3d};
        final Double[] dvec1 = {1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};

        OpenMapRealVector v0 = new OpenMapRealVector();
        Assert.assertEquals("testData len", 0, v0.getDimension());

        OpenMapRealVector v1 = new OpenMapRealVector(7);
        Assert.assertEquals("testData len", 7, v1.getDimension());
        Assert.assertEquals("testData is 0.0 ", 0.0, v1.getEntry(6), 0);

        OpenMapRealVector v3 = new OpenMapRealVector(vec1);
        Assert.assertEquals("testData len", 3, v3.getDimension());
        Assert.assertEquals("testData is 2.0 ", 2.0, v3.getEntry(1), 0);

        //SparseRealVector v4 = new SparseRealVector(vec4, 3, 2);
        //Assert.assertEquals("testData len", 2, v4.getDimension());
        //Assert.assertEquals("testData is 4.0 ", 4.0, v4.getEntry(0));
        //try {
        //    new SparseRealVector(vec4, 8, 3);
        //    Assert.fail("MathIllegalArgumentException expected");
        //} catch (MathIllegalArgumentException ex) {
            // expected behavior
        //}

        RealVector v5_i = new OpenMapRealVector(dvec1);
        Assert.assertEquals("testData len", 9, v5_i.getDimension());
        Assert.assertEquals("testData is 9.0 ", 9.0, v5_i.getEntry(8), 0);

        OpenMapRealVector v5 = new OpenMapRealVector(dvec1);
        Assert.assertEquals("testData len", 9, v5.getDimension());
        Assert.assertEquals("testData is 9.0 ", 9.0, v5.getEntry(8), 0);

        OpenMapRealVector v7 = new OpenMapRealVector(v1);
        Assert.assertEquals("testData len", 7, v7.getDimension());
        Assert.assertEquals("testData is 0.0 ", 0.0, v7.getEntry(6), 0);

        RealVectorTestImpl v7_i = new RealVectorTestImpl(vec1);

        OpenMapRealVector v7_2 = new OpenMapRealVector(v7_i);
        Assert.assertEquals("testData len", 3, v7_2.getDimension());
        Assert.assertEquals("testData is 0.0 ", 2.0d, v7_2.getEntry(1), 0);

        OpenMapRealVector v8 = new OpenMapRealVector(v1);
        Assert.assertEquals("testData len", 7, v8.getDimension());
        Assert.assertEquals("testData is 0.0 ", 0.0, v8.getEntry(6), 0);
    }

    /* Check that the operations do not throw an exception (cf. MATH-645). */
    @Test
    public void testConcurrentModification() {
        final RealVector u = new OpenMapRealVector(3, 1e-6);
        u.setEntry(0, 1);
        u.setEntry(1, 0);
        u.setEntry(2, 2);

        final RealVector v1 = new OpenMapRealVector(3, 1e-6);
        v1.setEntry(0, 0);
        v1.setEntry(1, 3);
        v1.setEntry(2, 0);

        u.ebeMultiply(v1);
        u.ebeDivide(v1);
    }

    @Test
    @Override
    public void testEbeMultiplyMixedTypes() {
        doTestEbeBinaryOperation(BinaryOperation.MUL, true, true);
    }

    @Test
    @Override
    public void testEbeMultiplySameType() {
        doTestEbeBinaryOperation(BinaryOperation.MUL, false, true);
    }

    @Test
    @Override
    public void testEbeDivideSameType() {
        doTestEbeBinaryOperation(BinaryOperation.DIV, false, true);
    }

    @Override
    protected UnivariateFunction[] createFunctions() {
        return new UnivariateFunction[] {
            new Power(2.0), new Exp(), new Expm1(),
            new Log1p(), new Cosh(), new Sinh(), new Tanh(), new Cos(),
            new Sin(), new Tan(), new Acos(), new Asin(), new Atan(),
            new Abs(), new Sqrt(), new Cbrt(), new Ceil(),
            new Floor(), new Rint(), new Signum()
        };
    }
}
