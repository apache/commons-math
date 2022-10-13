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

package org.apache.commons.math4.legacy.analysis.function;

import org.apache.commons.math4.legacy.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for all classes in org.apache.commons.math4.legacy.analysis.function that implement UnivariateDifferentiableFunction explicitly.
 */
public class UnivariateDifferentiableFunctionTest {

    private static final double EPS = Math.ulp(1d);

    @Test
    public void testAcos() {
        Acos acos = new Acos();
        Assert.assertEquals(JdkMath.PI/3, acos.value(0.5), EPS);
        Assert.assertEquals(JdkMath.PI/4, acos.value(Double.valueOf(1/JdkMath.sqrt(2))), EPS);
        double a = 0.5;
        Assert.assertEquals(-1/JdkMath.sqrt(1-JdkMath.pow(a,2)), acos.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testAcosh() {
        Acosh acosh = new Acosh();
        Assert.assertEquals(0,acosh.value(1), EPS);
        double a = 1.2345;
        Assert.assertEquals(a,acosh.value(JdkMath.cosh(a)), EPS);
        Assert.assertEquals(1/(JdkMath.sqrt(a-1)*JdkMath.sqrt(a+1)),acosh.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testAsin() {
        Asin asin = new Asin();
        double a = 1.2345;
        Assert.assertEquals(a, asin.value(JdkMath.sin(a)), EPS);
        Assert.assertEquals(1/JdkMath.sqrt(1 - JdkMath.pow(a,2)), asin.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testAsinh() {
        Asinh asinh = new Asinh();
        double a = 1.2345;
        Assert.assertEquals(a, asinh.value(JdkMath.sinh(a)), EPS);
        Assert.assertEquals(1/JdkMath.sqrt(JdkMath.pow(a,2.0) + 1), asinh.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testAtan() {
        Atan atan = new Atan();
        double a = 1.2345;
        Assert.assertEquals(a, atan.value(JdkMath.tan(a)), EPS);
        Assert.assertEquals(1/(JdkMath.pow(a,2.0) + 1), atan.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testAtanh() {
        Atanh atanh = new Atanh();
        double a = 1.2345;
        Assert.assertEquals(a, atanh.value(JdkMath.tanh(a)), EPS);
        Assert.assertEquals(1/(1 - JdkMath.pow(a,2.0)), atanh.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testCbrt() {
        Cbrt cbrt = new Cbrt();
        double a = 1.2345;
        Assert.assertEquals(a, cbrt.value(JdkMath.pow(a,3)), EPS);
        Assert.assertEquals(1.0/(3.0*JdkMath.pow(a, 2.0/3.0)), cbrt.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testConstant() {
        double a = 123.456;
        Constant constantNeg1 = new Constant(-1);
        Constant constant0 = new Constant(0);
        Constant constant5 = new Constant(5);
        Assert.assertEquals(-1, constantNeg1.value(a), EPS);
        Assert.assertEquals(0, constant0.value(a), EPS);
        Assert.assertEquals(5, constant5.value(a), EPS);
        Assert.assertEquals(0, constantNeg1.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
        Assert.assertEquals(0, constant0.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
        Assert.assertEquals(0, constant5.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testCos() {
        Cos cos = new Cos();
        double a = 0.987;
        Assert.assertEquals(a, cos.value(JdkMath.acos(a)), EPS);
        Assert.assertEquals(-JdkMath.sin(a), cos.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testCosh() {
        Cosh cosh = new Cosh();
        double a = 1.2345;
        Assert.assertEquals(a, cosh.value(JdkMath.acosh(a)), EPS);
        Assert.assertEquals(JdkMath.sinh(a), cosh.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testExp() {
        Exp exp= new Exp();
        double a = 1.2345;
        Assert.assertEquals(a, exp.value(JdkMath.log(a)), EPS);
        Assert.assertEquals(exp.value(a), exp.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testExpm1() {
        Expm1 expm1 = new Expm1();
        double a = 1.2345;
        Assert.assertEquals(a-1, expm1.value(JdkMath.log(a)), EPS);
        Assert.assertEquals(JdkMath.exp(a), expm1.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testIdentity() {
        Identity identity = new Identity();
        double a = 123.456;
        Assert.assertEquals(a, identity.value(a), EPS);
        Assert.assertEquals(1, identity.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testInverse() {
        Inverse inverse = new Inverse();
        double a = 123.456;
        Assert.assertEquals(1/a, inverse.value(a), EPS);
        Assert.assertEquals(-1/JdkMath.pow(a,2), inverse.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testLog() {
        double a = 123.456;
        Log log = new Log();
        Assert.assertEquals(Math.log(a), log.value(a), EPS);
        Assert.assertEquals(1/a,log.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testLog10() {
        Log10 log10 = new Log10();
        double a =1.2345;
        Assert.assertEquals(a, log10.value(JdkMath.pow(10, a)), EPS);
        Assert.assertEquals(1/(a*JdkMath.log(10)), log10.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testLog1p() {
        Log1p log1p = new Log1p();
        double a = 1.2345;
        Assert.assertEquals(a+1,JdkMath.exp(log1p.value(a)), EPS);
        Assert.assertEquals(1/(1+a), log1p.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testMinus() {
        Minus minus = new Minus();
        double a = 123.456;
        Assert.assertEquals(-a, minus.value(a), EPS);
        Assert.assertEquals(-1, minus.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testPower() {
        Power squared = new Power(2);
        Power power2_5 = new Power(2.5);
        double a = 123.456;
        Assert.assertEquals(JdkMath.pow(a,2), squared.value(a), EPS);
        Assert.assertEquals(JdkMath.pow(a, 2.5), power2_5.value(a), EPS);
        Assert.assertEquals(2*a, squared.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
        Assert.assertEquals(2.5*JdkMath.pow(a,1.5), power2_5.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testSin() {
        Sin sin = new Sin();
        double a = 0.987;
        Assert.assertEquals(a, sin.value(JdkMath.asin(a)), EPS);
        Assert.assertEquals(JdkMath.cos(a), sin.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testSinh() {
        Sinh sinh = new Sinh();
        double a = 1.2345;
        Assert.assertEquals(a, sinh.value(JdkMath.asinh(a)), EPS);
        Assert.assertEquals(JdkMath.cosh(a), sinh.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testTan() {
        Tan tan = new Tan();
        double a = 0.987;
        Assert.assertEquals(a, tan.value(JdkMath.atan(a)), EPS);
        Assert.assertEquals(1/(JdkMath.pow(JdkMath.cos(a),2)), tan.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }

    @Test
    public void testTanh() {
        Tanh tanh = new Tanh();
        double a = 0.987;
        Assert.assertEquals(a, tanh.value(JdkMath.atanh(a)), EPS);
        Assert.assertEquals(1/JdkMath.pow(JdkMath.cosh(a),2), tanh.value(new DerivativeStructure(1,1,0,a)).getPartialDerivative(1), EPS);
    }
}
