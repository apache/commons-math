/*
 * Copyright 2003-2005 The Apache Software Foundation.
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
 * @version $Revision$ $Date$
 */
public class ComplexTest extends TestCase {
    
    private double inf = Double.POSITIVE_INFINITY;
    private double neginf = Double.NEGATIVE_INFINITY;
    private double nan = Double.NaN;
    private Complex oneInf = new Complex(1, inf);
    private Complex oneNegInf = new Complex(1, neginf);
    private Complex infOne = new Complex(inf, 1);
    private Complex negInfInf = new Complex(neginf, inf);
    private Complex negInfNegInf = new Complex(neginf, neginf);
    private Complex oneNaN = new Complex(1, nan);
    
    public void testConstructor() {
        Complex z = new Complex(3.0, 4.0);
        assertEquals(3.0, z.getReal(), 1.0e-5);
        assertEquals(4.0, z.getImaginary(), 1.0e-5);
    }
    
    public void testConstructorNaN() {
        Complex z = new Complex(3.0, Double.NaN);
        assertTrue(z.isNaN());

        z = new Complex(nan, 4.0);
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
        Complex z = new Complex(inf, nan);
        assertTrue(Double.isNaN(z.abs()));
    }
    
    public void testAbsInfinite() {
        Complex z = new Complex(inf, 0);
        assertEquals(inf, z.abs(), 0);
        z = new Complex(0, neginf);
        assertEquals(inf, z.abs(), 0);
        z = new Complex(inf, neginf);
        assertEquals(inf, z.abs(), 0);     
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
        z = new Complex(1, nan);
        Complex w = x.add(z);
        assertEquals(w.real, 4.0, 0);
        assertTrue(Double.isNaN(w.imaginary));
    }
    
    public void testAddInfinite() {
        Complex x = new Complex(1, 1);
        Complex z = new Complex(inf, 0);
        Complex w = x.add(z);
        assertEquals(w.imaginary, 1, 0);
        assertEquals(inf, w.real, 0);
        
        x = new Complex(neginf, 0);
        assertTrue(Double.isNaN(x.add(z).real));
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
    
    public void testConjugateInfiinite() {
        Complex z = new Complex(0, inf);
        assertEquals(neginf, z.conjugate().imaginary, 0);
        z = new Complex(0, neginf);
        assertEquals(inf, z.conjugate().imaginary, 0);
    }
    
    public void testDivide() {
        Complex x = new Complex(3.0, 4.0);
        Complex y = new Complex(5.0, 6.0);
        Complex z = x.divide(y);
        assertEquals(39.0 / 61.0, z.getReal(), 1.0e-5);
        assertEquals(2.0 / 61.0, z.getImaginary(), 1.0e-5);
    }
    
    public void testDivideInfinite() {
        Complex x = new Complex(3, 4);
        Complex w = new Complex(neginf, inf);
        assertTrue(x.divide(w).equals(Complex.ZERO));
        
        Complex z = w.divide(x);
        assertTrue(Double.isNaN(z.real));
        assertEquals(inf, z.imaginary, 0);
        
        w = new Complex(inf, inf);
        z = w.divide(x);
        assertTrue(Double.isNaN(z.imaginary));
        assertEquals(inf, z.real, 0);
        
        w = new Complex(1, inf);
        z = w.divide(w);
        assertTrue(Double.isNaN(z.real));
        assertTrue(Double.isNaN(z.imaginary));
    }
    
    public void testDivideNaN() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.divide(Complex.NaN);
        assertTrue(z.isNaN());
    }
    
    public void testDivideNaNInf() {  
       Complex z = oneInf.divide(Complex.ONE);
       assertTrue(Double.isNaN(z.real));
       assertEquals(inf, z.imaginary, 0);
       
       z = negInfNegInf.divide(oneNaN);
       assertTrue(Double.isNaN(z.real));
       assertTrue(Double.isNaN(z.imaginary));
       
       z = negInfInf.divide(Complex.ONE);
       assertTrue(Double.isNaN(z.real));
       assertTrue(Double.isNaN(z.imaginary));
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
    
    public void testMultiplyNaNInf() {
        Complex z = new Complex(1,1);
        Complex w = z.multiply(infOne);
        assertEquals(w.real, inf, 0);
        assertEquals(w.imaginary, inf, 0);
        
        w = oneInf.multiply(oneNegInf);
        assertEquals(w.real, inf, 0);
        assertTrue(Double.isNaN(w.imaginary));
        
        w = negInfNegInf.multiply(oneNaN);
        assertTrue(Double.isNaN(w.real));
        assertTrue(Double.isNaN(w.imaginary));  
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
    
    public void testEqualsNaN() {
        Complex realNaN = new Complex(Double.NaN, 0.0);
        Complex imaginaryNaN = new Complex(0.0, Double.NaN);
        Complex complexNaN = Complex.NaN;
        assertTrue(realNaN.equals(imaginaryNaN));
        assertTrue(imaginaryNaN.equals(complexNaN));
        assertTrue(realNaN.equals(complexNaN));
    }
    
    public void testHashCode() {
        Complex x = new Complex(0.0, 0.0);
        Complex y = new Complex(0.0, 0.0 + Double.MIN_VALUE);
        assertFalse(x.hashCode()==y.hashCode());
        y = new Complex(0.0 + Double.MIN_VALUE, 0.0);
        assertFalse(x.hashCode()==y.hashCode());
        Complex realNaN = new Complex(Double.NaN, 0.0);
        Complex imaginaryNaN = new Complex(0.0, Double.NaN);
        assertEquals(realNaN.hashCode(), imaginaryNaN.hashCode());
        assertEquals(imaginaryNaN.hashCode(), Complex.NaN.hashCode());
    }
}
