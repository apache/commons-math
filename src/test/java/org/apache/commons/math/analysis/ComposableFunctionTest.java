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
package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;
import org.junit.Assert;
import org.junit.Test;

public class ComposableFunctionTest {

    @Test
    public void testZero() throws FunctionEvaluationException {
        Assert.assertEquals(0.0, ComposableFunction.ZERO.value(1), 1.0e-15);
        Assert.assertEquals(0.0, ComposableFunction.ZERO.value(2), 1.0e-15);
    }

    @Test
    public void testOne() throws FunctionEvaluationException {
        Assert.assertEquals(1.0, ComposableFunction.ONE.value(1), 1.0e-15);
        Assert.assertEquals(1.0, ComposableFunction.ONE.value(2), 1.0e-15);
    }

    @Test
    public void testIdentity() throws FunctionEvaluationException {
        Assert.assertEquals(1.0, ComposableFunction.IDENTITY.value(1), 1.0e-15);
        Assert.assertEquals(2.0, ComposableFunction.IDENTITY.value(2), 1.0e-15);
    }

    @Test
    public void testRint() throws FunctionEvaluationException {
        Assert.assertEquals(1.0, ComposableFunction.RINT.value(0.9), 1.0e-15);
        Assert.assertEquals(2.0, ComposableFunction.RINT.value(2.2), 1.0e-15);
    }

    @Test
    public void testSignum() throws FunctionEvaluationException {
        Assert.assertEquals(1.0, ComposableFunction.SIGNUM.value(12.3), 1.0e-15);
        Assert.assertEquals(-1.0, ComposableFunction.SIGNUM.value(-6), 1.0e-15);
    }

    @Test
    public void testComposition() throws FunctionEvaluationException {
        ComposableFunction abs    = ComposableFunction.ABS;
        ComposableFunction acos   = ComposableFunction.ACOS;
        ComposableFunction asin   = ComposableFunction.ASIN;
        ComposableFunction atan   = ComposableFunction.ATAN;
        ComposableFunction cbrt   = ComposableFunction.CBRT;
        ComposableFunction ceil   = ComposableFunction.CEIL;
        ComposableFunction cos    = ComposableFunction.COS;
        ComposableFunction cosh   = ComposableFunction.COSH;
        ComposableFunction exp    = ComposableFunction.EXP;
        ComposableFunction expm1  = ComposableFunction.EXPM1;
        ComposableFunction floor  = ComposableFunction.FLOOR;
        ComposableFunction id     = ComposableFunction.IDENTITY;
        ComposableFunction log    = ComposableFunction.LOG;
        ComposableFunction log10  = ComposableFunction.LOG10;
        ComposableFunction negate = ComposableFunction.NEGATE;
        ComposableFunction sin    = ComposableFunction.SIN;
        ComposableFunction sinh   = ComposableFunction.SINH;
        ComposableFunction sqrt   = ComposableFunction.SQRT;
        ComposableFunction tan    = ComposableFunction.TAN;
        ComposableFunction tanh   = ComposableFunction.TANH;
        ComposableFunction ulp    = ComposableFunction.ULP;

        ComposableFunction f1 = sqrt.of(abs.of(expm1.of(cbrt.of(tanh).of(id))));
        for (double x = 0.1; x < 0.9; x += 0.01) {
            Assert.assertEquals(Math.sqrt(Math.abs(Math.expm1(Math.cbrt(Math.tanh(x))))),
                                f1.value(x), 1.0e-15);
        }

        ComposableFunction f2 = cosh.of(sinh.of(tanh.of(ceil.postCompose(log.postCompose(cosh)))));
        for (double x = 0.1; x < 12.9; x += 1.0) {
            Assert.assertEquals(Math.cosh(Math.sinh(Math.tanh(Math.cosh(Math.log(Math.ceil(x)))))),
                                f2.value(x), 1.0e-15);
        }

        ComposableFunction f3 = cos.of(sin.of(tan.of(acos.of(asin.of(log10.of(log.of(ulp)))))));
        for (double x = 1.0e16; x < 1.0e17; x += 1.0e16) {
            Assert.assertEquals(Math.cos(Math.sin(Math.tan(Math.acos(Math.asin(Math.log10(Math.log(Math.ulp(x)))))))),
                                f3.value(x), 1.0e-15);
        }

        ComposableFunction f4 = atan.of(exp.of(negate.of(floor)));
        for (double x = 1.1; x < 10.2; x += 1.0) {
            Assert.assertEquals(Math.atan(Math.exp(-Math.floor(x))),
                                f4.value(x), 1.0e-15);
        }

    }

    @Test
    public void testCombine() throws FunctionEvaluationException {

        ComposableFunction f =
            ComposableFunction.COS.combine(ComposableFunction.ASIN, BinaryFunction.POW);
        for (double x = 0.1; x < 0.9; x += 0.01) {
            Assert.assertEquals(Math.pow(Math.cos(x), Math.asin(x)), f.value(x), 1.0e-15);
        }

    }

    @Test
    public void testSimpleCombination() throws FunctionEvaluationException {

        ComposableFunction f1 = ComposableFunction.COS.add(3);
        ComposableFunction f2 = ComposableFunction.COS.add(ComposableFunction.SIN);
        ComposableFunction f3 = ComposableFunction.COS.subtract(ComposableFunction.SIN);
        ComposableFunction f4 = ComposableFunction.COS.multiply(ComposableFunction.SIN);
        ComposableFunction f5 = ComposableFunction.COS.multiply(5);
        ComposableFunction f6 = ComposableFunction.COS.divide(ComposableFunction.SIN);
        for (double x = 0.1; x < 0.9; x += 0.01) {
            Assert.assertEquals(Math.cos(x) + 3, f1.value(x), 1.0e-15);
            Assert.assertEquals(Math.cos(x) + Math.sin(x), f2.value(x), 1.0e-15);
            Assert.assertEquals(Math.cos(x) - Math.sin(x), f3.value(x), 1.0e-15);
            Assert.assertEquals(Math.cos(x) * Math.sin(x), f4.value(x), 1.0e-15);
            Assert.assertEquals(Math.cos(x) * 5, f5.value(x), 1.0e-15);
            Assert.assertEquals(Math.cos(x) / Math.sin(x), f6.value(x), 1.0e-15);
        }

    }

    @Test
    public void testCollector() throws FunctionEvaluationException {

        ComposableFunction f = BinaryFunction.POW.fix2ndArgument(2);
        Assert.assertEquals(30, f.asCollector().value(new double[] { 1, 2, 3, 4 }), 1.0e-15);
        Assert.assertEquals(33, f.asCollector(3).value(new double[] { 1, 2, 3, 4 }), 1.0e-15);
        Assert.assertEquals(-30, f.asCollector(BinaryFunction.SUBTRACT).value(new double[] { 1, 2, 3, 4 }), 1.0e-15);
        Assert.assertEquals(1152, f.asCollector(BinaryFunction.MULTIPLY, 2).value(new double[] { 1, 2, 3, 4 }), 1.0e-15);
    }

}
