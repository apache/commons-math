/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.commons.math.complex;

import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.1 $ $Date: 2003/11/15 18:52:31 $
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
