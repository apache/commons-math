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

import java.io.Serializable;

/**
 * Representation of a Complex number - a number which has both a 
 * real and imaginary part.
 *
 * @author Apache Software Foundation
 * @version $Revision: 1.9 $ $Date: 2004/06/23 16:26:16 $
 */
public class Complex implements Serializable  {

    /** Serializable version identifier */
    static final long serialVersionUID = -6530173849413811929L;
    
    /** The square root of -1. A number representing "0.0 + 1.0i".*/    
    public static final Complex I = new Complex(0.0, 1.0);
    
    /** A complex number representing "(Double.NaN) + (Double.NaN)i" */
    public static final Complex NaN = new Complex(Double.NaN, Double.NaN);

    /** A complex number representing "1.0 + 0.0i" */    
    public static final Complex ONE = new Complex(1.0, 0.0);
    
    /** The imaginary part. */
    protected double imaginary;
    
    /** The real part. */
    protected double real;
    
    /**
     * Create a complex number given the real and imaginary parts.
     *
     * @param real the real part.
     * @param imaginary the imaginary part.
     */
    public Complex(double real, double imaginary) {
        super();
        this.real = real;
        this.imaginary = imaginary;
    }

    /**
     * Return the absolute value of this complex number.
     *
     * @return the absolute value.
     */
    public double abs() {
        if (isNaN()) {
            return Double.NaN;
        }
        return Math.sqrt(squareSum());       
    }
    
    /**
     * Return the sum of this complex number and the given complex number.
     *
     * @param rhs the other complex number.
     * @return the complex number sum.
     */
    public Complex add(Complex rhs) {
        if (isNaN() || rhs.isNaN()) {
            return NaN;
        }
        
        return new Complex(real + rhs.getReal(),
            imaginary + rhs.getImaginary());
    }
    
    /**
     * Return the conjugate of this complex number.  The conjugate of
     * "A + Bi" is "A - Bi".  Complex.NaN is returned if either the real or imaginary part of 
     * this Complex number equals Double.NaN.
     *
     * @return the conjugate of this Complex object
     */
    public Complex conjugate() {
        if (isNaN()) {
            return NaN;
        }
        
        return new Complex(real, -imaginary);
    }
    
    /**
     * Return the quotient of this complex number and the given complex number.
     * @param rhs the other complex number.
     * @return the complex number quotient.
     */
    public Complex divide(Complex rhs) {
        if (isNaN() || rhs.isNaN()) {
            return NaN;
        }
        
        if (Math.abs(rhs.getReal()) < Math.abs(rhs.getImaginary())) {
            double q = rhs.getReal() / rhs.getImaginary();
            double d = (rhs.getReal() * q) + rhs.getImaginary();
            return new Complex(((real * q) + imaginary) / d,
                ((imaginary * q) - real) / d);
        } else {
            double q = rhs.getImaginary() / rhs.getReal();
            double d = (rhs.getImaginary() * q) + rhs.getReal();
            return new Complex(((imaginary * q) + real) / d,
                (imaginary - (real * q)) / d);
        }
    }
    
    /**
     * Test for the equality of two Complex objects.  If both the
     * real and imaginary parts of two Complex numbers are exactly
     * the same, the two Complex objects are considered to be equal.
     *
     * @param other Object to test for equality to this
     * @return true if two Complex objects are equal, false if
     *         object is null, not an instance of Complex, or
     *         not equal to this Complex instance.
     * 
     */
    public boolean equals(Object other) {
        boolean ret;
        
        if (this == other) { 
            ret = true;
        } else if (other == null) {
            ret = false;
        } else {
            try {
                Complex rhs = (Complex)other;
                ret = (Double.doubleToRawLongBits(real) ==
                        Double.doubleToRawLongBits(rhs.getReal())) &&
                    (Double.doubleToRawLongBits(imaginary) ==
                        Double.doubleToRawLongBits(rhs.getImaginary())); 
            } catch (ClassCastException ex) {
                // ignore exception
                ret = false;
            }
        }
        
        return ret;
    }

    /**
     * Access the imaginary part.
     *
     * @return the imaginary part.
     */
    public double getImaginary() {
        return imaginary;
    }

    /**
     * Access the real part.
     *
     * @return the real part.
     */
    public double getReal() {
        return real;
    }
    
    /**
     * Returns true if this complex number is the special Not-a-Number (NaN)
     * value.
     *
     * @return true if the value represented by this object is NaN; false
     *         otherwise.
     */
    public boolean isNaN() {
        return Double.isNaN(real) || Double.isNaN(imaginary);        
    }
    
    /**
     * Return the product of this complex number and the given complex number.
     *
     * @param rhs the other complex number.
     * @return the complex number product.
     */
    public Complex multiply(Complex rhs) {
        if (isNaN() || rhs.isNaN()) {
            return NaN;
        }
        
        double p = (real + imaginary) * (rhs.getReal() + rhs.getImaginary());
        double ac = real * rhs.getReal();
        double bd = imaginary * rhs.getImaginary();
        return new Complex(ac - bd, p - ac - bd);
    }
    
    /**
     * Return the additive inverse of this complex number.
     *
     * @return the negation of this complex number.
     */
    public Complex negate() {
        if (isNaN()) {
            return NaN;
        }
        
        return new Complex(-real, -imaginary);
    }
    
    /**
     * Return the sum of the squared terms.
     *
     * @return the square sum.
     */
    private double squareSum() {
        return real * real + imaginary * imaginary;
    }
    
    /**
     * Return the difference between this complex number and the given complex
     * number.
     *
     * @param rhs the other complex number.
     * @return the complex number difference.
     */
    public Complex subtract(Complex rhs) {
        if (isNaN() || rhs.isNaN()) {
            return NaN;
        }
        
        return new Complex(real - rhs.getReal(),
            imaginary - rhs.getImaginary());
    }
}
