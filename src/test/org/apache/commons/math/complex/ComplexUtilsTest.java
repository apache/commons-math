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

import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class ComplexUtilsTest extends TestCase {
    
    private double inf = Double.POSITIVE_INFINITY;
    private double negInf = Double.NEGATIVE_INFINITY;
    private double nan = Double.NaN;
    private double pi = Math.PI;
    
    private Complex oneInf = new Complex(1, inf);
    private Complex oneNegInf = new Complex(1, negInf);
    private Complex infOne = new Complex(inf, 1);
    private Complex negInfOne = new Complex(negInf, 1);
    private Complex negInfInf = new Complex(negInf, inf);
    private Complex infNegInf = new Complex(inf, negInf);
    private Complex infInf = new Complex(inf, inf);
    private Complex negInfNegInf = new Complex(negInf, negInf);
    private Complex oneNaN = new Complex(1, nan);
    private Complex infNaN = new Complex(inf, nan);
    private Complex negInfNaN = new Complex(negInf, nan);
    private Complex nanInf = new Complex(nan, inf);
    private Complex nanNegInf = new Complex(nan, negInf);
    private Complex zeroNaN = new Complex(0, nan);
    private Complex nanZero = new Complex(nan, 0);
    private Complex infZero = new Complex(inf, 0);
    private Complex zeroInf = new Complex(0, inf);
    private Complex zeroNegInf = new Complex(0, negInf);
    private Complex negInfZero = new Complex(negInf, 0);
    
    private ComplexFormat fmt = new ComplexFormat();
    
    public void testAcos() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(0.936812, -2.30551);
        TestUtils.assertEquals(expected, ComplexUtils.acos(z), 1.0e-5);
        TestUtils.assertEquals(new Complex(Math.acos(0), 0), 
                ComplexUtils.acos(Complex.ZERO), 1.0e-12);
    }
    
    public void testAcosInf() {
        TestUtils.assertSame(Complex.NaN, ComplexUtils.acos(oneInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.acos(oneNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.acos(infOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.acos(negInfOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.acos(infInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.acos(infNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.acos(negInfInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.acos(negInfNegInf));
    }
    
    public void testAcosNaN() {
        assertTrue(ComplexUtils.acos(Complex.NaN).isNaN());
    }
    
    public void testAcosNull() {
        try {
            Complex z = ComplexUtils.acos(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
    
    public void testAsin() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(0.633984, 2.30551);
        TestUtils.assertEquals(expected, ComplexUtils.asin(z), 1.0e-5);
    }
    
    public void testAsinNaN() {
        assertTrue(ComplexUtils.asin(Complex.NaN).isNaN());
    }
    
    public void testAsinInf() {
        TestUtils.assertSame(Complex.NaN, ComplexUtils.asin(oneInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.asin(oneNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.asin(infOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.asin(negInfOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.asin(infInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.asin(infNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.asin(negInfInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.asin(negInfNegInf));
    }
    
    public void testAsinNull() {
        try {
            Complex z = ComplexUtils.asin(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
    
    public void testAtan() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(1.44831, 0.158997);
        TestUtils.assertEquals(expected, ComplexUtils.atan(z), 1.0e-5);
    }
    
    public void testAtanInf() {
        TestUtils.assertSame(Complex.NaN, ComplexUtils.atan(oneInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.atan(oneNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.atan(infOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.atan(negInfOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.atan(infInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.atan(infNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.atan(negInfInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.atan(negInfNegInf));
    } 
    
    public void testAtanNaN() {
        assertTrue(ComplexUtils.atan(Complex.NaN).isNaN());
        assertTrue(ComplexUtils.atan(Complex.I).isNaN());
    }
    
    public void testAtanNull() {
        try {
            Complex z = ComplexUtils.atan(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
    
    public void testCos() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-27.03495, -3.851153);
        TestUtils.assertEquals(expected, ComplexUtils.cos(z), 1.0e-5);
    }
    
    public void testCosNaN() {
        assertTrue(ComplexUtils.cos(Complex.NaN).isNaN());
    }
    
    public void testCosInf() {
        TestUtils.assertSame(infNegInf, ComplexUtils.cos(oneInf));
        TestUtils.assertSame(infInf, ComplexUtils.cos(oneNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cos(infOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cos(negInfOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cos(infInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cos(infNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cos(negInfInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cos(negInfNegInf));
    } 
    
    public void testCosNull() {
        try {
            Complex z = ComplexUtils.cos(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
    
    public void testCosh() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-6.58066, -7.58155);
        TestUtils.assertEquals(expected, ComplexUtils.cosh(z), 1.0e-5);
    }
    
    public void testCoshNaN() {
        assertTrue(ComplexUtils.cosh(Complex.NaN).isNaN());
    }
    
    public void testCoshInf() {  
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cosh(oneInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cosh(oneNegInf));
        TestUtils.assertSame(infInf, ComplexUtils.cosh(infOne));
        TestUtils.assertSame(infNegInf, ComplexUtils.cosh(negInfOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cosh(infInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cosh(infNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cosh(negInfInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.cosh(negInfNegInf));
    } 
    
    public void testCoshNull() {
        try {
            Complex z = ComplexUtils.cosh(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
    
    public void testExp() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-13.12878, -15.20078);
        TestUtils.assertEquals(expected, ComplexUtils.exp(z), 1.0e-5);
        TestUtils.assertEquals(Complex.ONE, 
                ComplexUtils.exp(Complex.ZERO), 10e-12);
        Complex iPi = Complex.I.multiply(new Complex(pi,0));
        TestUtils.assertEquals(Complex.ONE.negate(), 
                ComplexUtils.exp(iPi), 10e-12);
    }
    
    public void testExpNaN() {
        assertTrue(ComplexUtils.exp(Complex.NaN).isNaN());
    }
    
    public void testExpInf() {
        TestUtils.assertSame(Complex.NaN, ComplexUtils.exp(oneInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.exp(oneNegInf));
        TestUtils.assertSame(infInf, ComplexUtils.exp(infOne));
        TestUtils.assertSame(Complex.ZERO, ComplexUtils.exp(negInfOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.exp(infInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.exp(infNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.exp(negInfInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.exp(negInfNegInf));
    }
    
    public void testExpNull() {
        try {
            Complex z = ComplexUtils.exp(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
    
    public void testLog() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(1.60944, 0.927295);
        TestUtils.assertEquals(expected, ComplexUtils.log(z), 1.0e-5);
    }
    
    public void testLogNaN() {
        assertTrue(ComplexUtils.log(Complex.NaN).isNaN());
    }
    
    public void testLogInf() {
        TestUtils.assertEquals(new Complex(inf, pi / 2),
                ComplexUtils.log(oneInf), 10e-12);
        TestUtils.assertEquals(new Complex(inf, -pi / 2),
                ComplexUtils.log(oneNegInf), 10e-12);
        TestUtils.assertEquals(infZero, ComplexUtils.log(infOne), 10e-12);
        TestUtils.assertEquals(new Complex(inf, pi),
                ComplexUtils.log(negInfOne), 10e-12);
        TestUtils.assertEquals(new Complex(inf, pi / 4),
                ComplexUtils.log(infInf), 10e-12);
        TestUtils.assertEquals(new Complex(inf, -pi / 4),
                ComplexUtils.log(infNegInf), 10e-12);
        TestUtils.assertEquals(new Complex(inf, 3d * pi / 4),
                ComplexUtils.log(negInfInf), 10e-12);
        TestUtils.assertEquals(new Complex(inf, - 3d * pi / 4),
                ComplexUtils.log(negInfNegInf), 10e-12);
    }
    
    public void testLogZero() {
        TestUtils.assertSame(negInfZero, ComplexUtils.log(Complex.ZERO));
    }
    
    public void testlogNull() {
        try {
            Complex z = ComplexUtils.log(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
    
    public void testPolar2Complex() {
        TestUtils.assertEquals(Complex.ONE, 
                ComplexUtils.polar2Complex(1, 0), 10e-12);
        TestUtils.assertEquals(Complex.ZERO, 
                ComplexUtils.polar2Complex(0, 1), 10e-12);
        TestUtils.assertEquals(Complex.ZERO, 
                ComplexUtils.polar2Complex(0, -1), 10e-12);
        TestUtils.assertEquals(Complex.I, 
                ComplexUtils.polar2Complex(1, pi/2), 10e-12);
        TestUtils.assertEquals(Complex.I.negate(), 
                ComplexUtils.polar2Complex(1, -pi/2), 10e-12);
        double r = 0;
        for (int i = 0; i < 5; i++) {
          r += i;
          double theta = 0;
          for (int j =0; j < 20; j++) {
              theta += pi / 6;
              TestUtils.assertEquals(altPolar(r, theta), 
                      ComplexUtils.polar2Complex(r, theta), 10e-12);
          }
          theta = -2 * pi;
          for (int j =0; j < 20; j++) {
              theta -= pi / 6;
              TestUtils.assertEquals(altPolar(r, theta), 
                      ComplexUtils.polar2Complex(r, theta), 10e-12);
          }
        }   
    }
    
    protected Complex altPolar(double r, double theta) {
        return ComplexUtils.exp(Complex.I.multiply
                (new Complex(theta, 0))).multiply(new Complex(r, 0));
    }
    
    public void testPolar2ComplexIllegalModulus() {
        try {
            Complex z = ComplexUtils.polar2Complex(-1, 0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }       
    }
    
    public void testPolar2ComplexNaN() {
        TestUtils.assertSame(Complex.NaN, ComplexUtils.polar2Complex(nan, 1));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.polar2Complex(1, nan));
        TestUtils.assertSame(Complex.NaN, 
                ComplexUtils.polar2Complex(nan, nan));     
    }
    
    public void testPolar2ComplexInf() {
        TestUtils.assertSame(Complex.NaN, ComplexUtils.polar2Complex(1, inf));
        TestUtils.assertSame(Complex.NaN,
                ComplexUtils.polar2Complex(1, negInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.polar2Complex(inf, inf));
        TestUtils.assertSame(Complex.NaN,
                ComplexUtils.polar2Complex(inf, negInf));
        TestUtils.assertSame(infInf, ComplexUtils.polar2Complex(inf, pi/4));
        TestUtils.assertSame(infNaN, ComplexUtils.polar2Complex(inf, 0));
        TestUtils.assertSame(infNegInf, ComplexUtils.polar2Complex(inf, -pi/4));
        TestUtils.assertSame(negInfInf, ComplexUtils.polar2Complex(inf, 3*pi/4));
        TestUtils.assertSame(negInfNegInf, ComplexUtils.polar2Complex(inf, 5*pi/4));
    }
    
    public void testPow() {
        Complex x = new Complex(3, 4);
        Complex y = new Complex(5, 6);
        Complex expected = new Complex(-1.860893, 11.83677);
        TestUtils.assertEquals(expected, ComplexUtils.pow(x, y), 1.0e-5);
    }
    
    public void testPowNaNBase() {
        Complex x = new Complex(3, 4);
        assertTrue(ComplexUtils.pow(Complex.NaN, x).isNaN());
    }
    
    public void testPowNaNExponent() {
        Complex x = new Complex(3, 4);
        assertTrue(ComplexUtils.pow(x, Complex.NaN).isNaN());
    }
    
   public void testPowInf() {
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(Complex.ONE, oneInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(Complex.ONE, oneNegInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(Complex.ONE, infOne));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(Complex.ONE, infInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(Complex.ONE, infNegInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(Complex.ONE, negInfInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(Complex.ONE, negInfNegInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(infOne, Complex.ONE));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(negInfOne, Complex.ONE));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(infInf, Complex.ONE));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(infNegInf, Complex.ONE));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(negInfInf, Complex.ONE));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(negInfNegInf, Complex.ONE));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(negInfNegInf, infNegInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(negInfNegInf, negInfNegInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(negInfNegInf, infInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(infInf, infNegInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(infInf, negInfNegInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(infInf, infInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(infNegInf, infNegInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(infNegInf, negInfNegInf));
       TestUtils.assertSame(Complex.NaN,ComplexUtils.pow(infNegInf, infInf));   
   }
   
   public void testPowZero() {
       TestUtils.assertSame(Complex.NaN, 
               ComplexUtils.pow(Complex.ZERO, Complex.ONE));
       TestUtils.assertSame(Complex.NaN, 
               ComplexUtils.pow(Complex.ZERO, Complex.ZERO));
       TestUtils.assertSame(Complex.NaN, 
               ComplexUtils.pow(Complex.ZERO, Complex.I));
       TestUtils.assertEquals(Complex.ONE,
               ComplexUtils.pow(Complex.ONE, Complex.ZERO), 10e-12);
       TestUtils.assertEquals(Complex.ONE,
               ComplexUtils.pow(Complex.I, Complex.ZERO), 10e-12);
       TestUtils.assertEquals(Complex.ONE,
               ComplexUtils.pow(new Complex(-1, 3), Complex.ZERO), 10e-12);
   }
    
    public void testpowNull() {
        try {
            Complex z = ComplexUtils.pow(null, Complex.ONE); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
        try {
            Complex z = ComplexUtils.pow(Complex.ONE, null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
    
    public void testSin() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(3.853738, -27.01681);
        TestUtils.assertEquals(expected, ComplexUtils.sin(z), 1.0e-5);
    }
    
    public void testSinInf() {
        TestUtils.assertSame(infInf, ComplexUtils.sin(oneInf));
        TestUtils.assertSame(infNegInf, ComplexUtils.sin(oneNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sin(infOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sin(negInfOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sin(infInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sin(infNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sin(negInfInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sin(negInfNegInf));
    }
    
    public void testSinNaN() {
        assertTrue(ComplexUtils.sin(Complex.NaN).isNaN());
    }
    
    public void testSinNull() {
        try {
            Complex z = ComplexUtils.sin(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
     
    public void testSinh() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-6.54812, -7.61923);
        TestUtils.assertEquals(expected, ComplexUtils.sinh(z), 1.0e-5);
    }
    
    public void testSinhNaN() {
        assertTrue(ComplexUtils.sinh(Complex.NaN).isNaN());
    }
    
    public void testSinhInf() {
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sinh(oneInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sinh(oneNegInf));
        TestUtils.assertSame(infInf, ComplexUtils.sinh(infOne));
        TestUtils.assertSame(negInfInf, ComplexUtils.sinh(negInfOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sinh(infInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sinh(infNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sinh(negInfInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.sinh(negInfNegInf));
    }
    
    public void testsinhNull() {
        try {
            Complex z = ComplexUtils.sinh(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
    
    public void testSqrtRealPositive() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(2, 1);
        TestUtils.assertEquals(expected, ComplexUtils.sqrt(z), 1.0e-5);
    }
    
    public void testSqrtRealZero() {
        Complex z = new Complex(0.0, 4);
        Complex expected = new Complex(1.41421, 1.41421);
        TestUtils.assertEquals(expected, ComplexUtils.sqrt(z), 1.0e-5);
    }
    
    public void testSqrtRealNegative() {
        Complex z = new Complex(-3.0, 4);
        Complex expected = new Complex(1, 2);
        TestUtils.assertEquals(expected, ComplexUtils.sqrt(z), 1.0e-5);
    }
    
    public void testSqrtImaginaryZero() {
        Complex z = new Complex(-3.0, 0.0);
        Complex expected = new Complex(0.0, 1.73205);
        TestUtils.assertEquals(expected, ComplexUtils.sqrt(z), 1.0e-5);
    }
    
    public void testSqrtImaginaryNegative() {
        Complex z = new Complex(-3.0, -4.0);
        Complex expected = new Complex(1.0, -2.0);
        TestUtils.assertEquals(expected, ComplexUtils.sqrt(z), 1.0e-5);
    }
    
    public void testSqrtPolar() {
        double r = 1;
        for (int i = 0; i < 5; i++) {
            r += i;
            double theta = 0;
            for (int j =0; j < 11; j++) {
                theta += pi /12;
                Complex z = ComplexUtils.polar2Complex(r, theta);
                Complex sqrtz = ComplexUtils.polar2Complex(Math.sqrt(r), theta / 2);
                TestUtils.assertEquals(sqrtz, ComplexUtils.sqrt(z), 10e-12);
            }
        }       
    }
    
    public void testSqrtNaN() {
        assertTrue(ComplexUtils.sqrt(Complex.NaN).isNaN());
    }
      
    public void testSqrtInf() {
        TestUtils.assertSame(infNaN, ComplexUtils.sqrt(oneInf));
        TestUtils.assertSame(infNaN, ComplexUtils.sqrt(oneNegInf));
        TestUtils.assertSame(infZero, ComplexUtils.sqrt(infOne));
        TestUtils.assertSame(zeroInf, ComplexUtils.sqrt(negInfOne));
        TestUtils.assertSame(infNaN, ComplexUtils.sqrt(infInf));
        TestUtils.assertSame(infNaN, ComplexUtils.sqrt(infNegInf));
        TestUtils.assertSame(nanInf, ComplexUtils.sqrt(negInfInf));
        TestUtils.assertSame(nanNegInf, ComplexUtils.sqrt(negInfNegInf));
    }
    
    public void testSqrtNull() {
        try {
            Complex z = ComplexUtils.sqrt(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void testSqrt1z() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(4.08033, -2.94094);
        TestUtils.assertEquals(expected, ComplexUtils.sqrt1z(z), 1.0e-5);
    }
    
    public void testSqrt1zNaN() {
        assertTrue(ComplexUtils.sqrt1z(Complex.NaN).isNaN());
    }
    
    public void testSqrt1zNull() {
        try {
            Complex z = ComplexUtils.sqrt1z(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
    
    public void testTan() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-0.000187346, 0.999356);
        TestUtils.assertEquals(expected, ComplexUtils.tan(z), 1.0e-5);
    }
    
    public void testTanNaN() {
        assertTrue(ComplexUtils.tan(Complex.NaN).isNaN());
    }
    
    public void testTanInf() {
        TestUtils.assertSame(zeroNaN, ComplexUtils.tan(oneInf));
        TestUtils.assertSame(zeroNaN, ComplexUtils.tan(oneNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tan(infOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tan(negInfOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tan(infInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tan(infNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tan(negInfInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tan(negInfNegInf));
    }
    
   public void testTanCritical() {
        TestUtils.assertSame(infNaN, ComplexUtils.tan(new Complex(pi/2, 0)));
        TestUtils.assertSame(negInfNaN, ComplexUtils.tan(new Complex(-pi/2, 0)));
    }
    
    public void testTanNull() {
        try {
            Complex z = ComplexUtils.tan(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
    
    public void testTanh() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(1.00071, 0.00490826);
        TestUtils.assertEquals(expected, ComplexUtils.tanh(z), 1.0e-5);
    }
    
    public void testTanhNaN() {
        assertTrue(ComplexUtils.tanh(Complex.NaN).isNaN());
    }
    
    public void testTanhInf() {
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tanh(oneInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tanh(oneNegInf));
        TestUtils.assertSame(nanZero, ComplexUtils.tanh(infOne));
        TestUtils.assertSame(nanZero, ComplexUtils.tanh(negInfOne));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tanh(infInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tanh(infNegInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tanh(negInfInf));
        TestUtils.assertSame(Complex.NaN, ComplexUtils.tanh(negInfNegInf));
    }
    
    public void testTanhCritical() {
        TestUtils.assertSame(nanInf, ComplexUtils.tanh(new Complex(0, pi/2)));
    }
    
    public void testTanhNull() {
        try {
            Complex z = ComplexUtils.tanh(null); 
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }
}
