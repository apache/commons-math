/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.complex;

import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.3 $ $Date: 2004/02/21 21:35:16 $
 */
public class ComplexMathTest extends TestCase {
    
    public void testAcos() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(0.936812, -2.30551);
        TestUtils.assertEquals(expected, ComplexMath.acos(z), 1.0e-5);
    }
    
    public void testAcosNaN() {
        assertTrue(ComplexMath.acos(Complex.NaN).isNaN());
    }
    
    public void testAsin() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(0.633984, 2.30551);
        TestUtils.assertEquals(expected, ComplexMath.asin(z), 1.0e-5);
    }
    
    public void testAsinNaN() {
        assertTrue(ComplexMath.asin(Complex.NaN).isNaN());
    }
    
    public void testAtan() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(1.44831, 0.158997);
        TestUtils.assertEquals(expected, ComplexMath.atan(z), 1.0e-5);
    }
    
    public void testAtanNaN() {
        assertTrue(ComplexMath.atan(Complex.NaN).isNaN());
    }
    
    public void testCos() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-27.03495, -3.851153);
        TestUtils.assertEquals(expected, ComplexMath.cos(z), 1.0e-5);
    }
    
    public void testCosh() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-6.58066, -7.58155);
        TestUtils.assertEquals(expected, ComplexMath.cosh(z), 1.0e-5);
    }
    
    public void testCoshNaN() {
        assertTrue(ComplexMath.cosh(Complex.NaN).isNaN());
    }
    
    public void testCosNaN() {
        assertTrue(ComplexMath.cos(Complex.NaN).isNaN());
    }
    
    public void testExp() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-13.12878, -15.20078);
        TestUtils.assertEquals(expected, ComplexMath.exp(z), 1.0e-5);
    }
    
    public void testExpNaN() {
        assertTrue(ComplexMath.exp(Complex.NaN).isNaN());
    }
    
    public void testLog() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(1.60944, 0.927295);
        TestUtils.assertEquals(expected, ComplexMath.log(z), 1.0e-5);
    }
    
    public void testLogNaN() {
        assertTrue(ComplexMath.log(Complex.NaN).isNaN());
    }
    
    public void testPow() {
        Complex x = new Complex(3, 4);
        Complex y = new Complex(5, 6);
        Complex expected = new Complex(-1.860893, 11.83677);
        TestUtils.assertEquals(expected, ComplexMath.pow(x, y), 1.0e-5);
    }
    
    public void testPowNaNBase() {
        Complex x = new Complex(3, 4);
        assertTrue(ComplexMath.pow(Complex.NaN, x).isNaN());
    }
    
    public void testPowNaNExponent() {
        Complex x = new Complex(3, 4);
        assertTrue(ComplexMath.pow(x, Complex.NaN).isNaN());
    }
    
    public void testSin() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(3.853738, -27.01681);
        TestUtils.assertEquals(expected, ComplexMath.sin(z), 1.0e-5);
    }
    
    public void testSinh() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-6.54812, -7.61923);
        TestUtils.assertEquals(expected, ComplexMath.sinh(z), 1.0e-5);
    }
    
    public void testSinhNaN() {
        assertTrue(ComplexMath.sinh(Complex.NaN).isNaN());
    }
    
    public void testSinNaN() {
        assertTrue(ComplexMath.sin(Complex.NaN).isNaN());
    }
    
    public void testSqrtRealPositive() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(2, 1);
        TestUtils.assertEquals(expected, ComplexMath.sqrt(z), 1.0e-5);
    }
    
    public void testSqrtRealZero() {
        Complex z = new Complex(0.0, 4);
        Complex expected = new Complex(1.41421, 1.41421);
        TestUtils.assertEquals(expected, ComplexMath.sqrt(z), 1.0e-5);
    }
    
    public void testSqrtRealNegative() {
        Complex z = new Complex(-3.0, 4);
        Complex expected = new Complex(1, 2);
        TestUtils.assertEquals(expected, ComplexMath.sqrt(z), 1.0e-5);
    }
    
    public void testSqrtImaginaryZero() {
        Complex z = new Complex(-3.0, 0.0);
        Complex expected = new Complex(0.0, 1.73205);
        TestUtils.assertEquals(expected, ComplexMath.sqrt(z), 1.0e-5);
    }
    
    public void testSqrtImaginaryNegative() {
        Complex z = new Complex(-3.0, -4.0);
        Complex expected = new Complex(1.0, -2.0);
        TestUtils.assertEquals(expected, ComplexMath.sqrt(z), 1.0e-5);
    }

    public void testSqrt1z() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(4.08033, -2.94094);
        TestUtils.assertEquals(expected, ComplexMath.sqrt1z(z), 1.0e-5);
    }
    
    public void testSqrt1zNaN() {
        assertTrue(ComplexMath.sqrt1z(Complex.NaN).isNaN());
    }
    
    public void testSqrtNaN() {
        assertTrue(ComplexMath.sqrt(Complex.NaN).isNaN());
    }
    
    public void testTan() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-0.000187346, 0.999356);
        TestUtils.assertEquals(expected, ComplexMath.tan(z), 1.0e-5);
    }
    
    public void testTanh() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(1.00071, 0.00490826);
        TestUtils.assertEquals(expected, ComplexMath.tanh(z), 1.0e-5);
    }
    
    public void testTanhNaN() {
        assertTrue(ComplexMath.tanh(Complex.NaN).isNaN());
    }
    
    public void testTanNaN() {
        assertTrue(ComplexMath.tan(Complex.NaN).isNaN());
    }
}
