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

import junit.framework.TestCase;

/**
 * @version $Revision: 1.6 $ $Date: 2004/02/21 21:35:16 $
 */
public class ComplexTest extends TestCase {
    
    public void testConstructor() {
        Complex z = new Complex(3.0, 4.0);
        assertEquals(3.0, z.getReal(), 1.0e-5);
        assertEquals(4.0, z.getImaginary(), 1.0e-5);
    }
    
    public void testConstructorNaN() {
        Complex z = new Complex(3.0, Double.NaN);
        assertTrue(z.isNaN());

        z = new Complex(Double.NaN, 4.0);
        assertTrue(z.isNaN());

        z = new Complex(3.0, 4.0);
        assertFalse(z.isNaN());
    }
    
    public void testAbs() {
        Complex z = new Complex(3.0, 4.0);
        assertEquals(5.0, z.abs(), 1.0e-5);
    }
    
    public void testAbsNaN() {
        assertTrue(Double.isNaN(Complex.NaN.abs()));
    }
    
    public void testAdd() {
        Complex x = new Complex(3.0, 4.0);
        Complex y = new Complex(5.0, 6.0);
        Complex z = x.add(y);
        assertEquals(8.0, z.getReal(), 1.0e-5);
        assertEquals(10.0, z.getImaginary(), 1.0e-5);
    }
    
    public void testAddNaN() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.add(Complex.NaN);
        assertTrue(z.isNaN());
    }
    
    public void testConjugate() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.conjugate();
        assertEquals(3.0, z.getReal(), 1.0e-5);
        assertEquals(-4.0, z.getImaginary(), 1.0e-5);
    }
    
    public void testConjugateNaN() {
        Complex z = Complex.NaN.conjugate();
        assertTrue(z.isNaN());
    }
    
    public void testDivide() {
        Complex x = new Complex(3.0, 4.0);
        Complex y = new Complex(5.0, 6.0);
        Complex z = x.divide(y);
        assertEquals(39.0 / 61.0, z.getReal(), 1.0e-5);
        assertEquals(2.0 / 61.0, z.getImaginary(), 1.0e-5);
    }
    
    public void testDivideNaN() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.divide(Complex.NaN);
        assertTrue(z.isNaN());
    }
    
    public void testMultiply() {
        Complex x = new Complex(3.0, 4.0);
        Complex y = new Complex(5.0, 6.0);
        Complex z = x.multiply(y);
        assertEquals(-9.0, z.getReal(), 1.0e-5);
        assertEquals(38.0, z.getImaginary(), 1.0e-5);
    }
    
    public void testMultiplyNaN() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.multiply(Complex.NaN);
        assertTrue(z.isNaN());
    }
    
    public void testNegate() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.negate();
        assertEquals(-3.0, z.getReal(), 1.0e-5);
        assertEquals(-4.0, z.getImaginary(), 1.0e-5);
    }
    
    public void testNegateNaN() {
        Complex z = Complex.NaN.negate();
        assertTrue(z.isNaN());
    }
    
    public void testSubtract() {
        Complex x = new Complex(3.0, 4.0);
        Complex y = new Complex(5.0, 6.0);
        Complex z = x.subtract(y);
        assertEquals(-2.0, z.getReal(), 1.0e-5);
        assertEquals(-2.0, z.getImaginary(), 1.0e-5);
    }
    
    public void testSubtractNaN() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.subtract(Complex.NaN);
        assertTrue(z.isNaN());
    }
    
    public void testEqualsNull() {
        Complex x = new Complex(3.0, 4.0);
        assertFalse(x.equals(null));
    }
    
    public void testEqualsClass() {
        Complex x = new Complex(3.0, 4.0);
        assertFalse(x.equals(this));
    }
    
    public void testEqualsSame() {
        Complex x = new Complex(3.0, 4.0);
        assertTrue(x.equals(x));
    }
    
    public void testEqualsTrue() {
        Complex x = new Complex(3.0, 4.0);
        Complex y = new Complex(3.0, 4.0);
        assertTrue(x.equals(y));
    }
    
    public void testEqualsRealDifference() {
        Complex x = new Complex(0.0, 0.0);
        Complex y = new Complex(0.0 + Double.MIN_VALUE, 0.0);
        assertFalse(x.equals(y));
    }
    
    public void testEqualsImaginaryDifference() {
        Complex x = new Complex(0.0, 0.0);
        Complex y = new Complex(0.0, 0.0 + Double.MIN_VALUE);
        assertFalse(x.equals(y));
    }
}
