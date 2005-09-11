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
import org.apache.commons.math.util.MathUtils;

/**
 * Representation of a Complex number - a number which has both a 
 * real and imaginary part.
 *
 * @author Apache Software Foundation
 * @version $Revision$ $Date$
 */
public class Complex implements Serializable  {

    /** Serializable version identifier */
    static final long serialVersionUID = -6530173849413811929L;
    
    /** The square root of -1. A number representing "0.0 + 1.0i".*/    
    public static final Complex I = new Complex(0.0, 1.0);
    
    /** A complex number analogous to {@link java.lang.Double#NaN} */
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
        if (Math.abs(real) < Math.abs(imaginary)) {
            if (imaginary == 0.0) {
                return Math.abs(real);
            }
            double q = real / imaginary;
            return (Math.abs(imaginary) * Math.sqrt(1 + q*q));
        } else {
            if (real == 0.0) {
                return Math.abs(imaginary);
            }
            double q = imaginary / real;
            return (Math.abs(real) * Math.sqrt(1 + q*q));
        }
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

        double c = rhs.getReal();
        double d = rhs.getImaginary();
        if (c == 0.0 && d == 0.0) {
            throw new ArithmeticException("Error: division by zero.");
        }

        if (Math.abs(c) < Math.abs(d)) {
            if (d == 0.0) {
                return new Complex(real/c, imaginary/c);
            }
            double q = c / d;
            double denominator = c * q + d;
            return new Complex((real * q + imaginary) / denominator,
                (imaginary * q - real) / denominator);
        } else {
            if (c == 0.0) {
                return new Complex(imaginary/d, -real/c);
            }
            double q = d / c;
            double denominator = d * q + c;
            return new Complex((imaginary * q + real) / denominator,
                (imaginary - real * q) / denominator);
        }
    }
    
    /**
     * Test for the equality of two Complex objects.
     * <p>
     * If both the real and imaginary parts of two Complex numbers
     * are exactly the same, and neither is <code>Double.NaN</code>, the two
     * Complex objects are considered to be equal. 
     * <p>
     * All <code>NaN</code> values are considered to be equal - i.e, if either
     * (or both) real and imaginary parts of the complex number are equal
     * to Double.NaN, the complex number is equal to 
     * <code>Complex.NaN</code>.
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
        } else  {
            try {
                Complex rhs = (Complex)other;
                if (rhs.isNaN()) {
                    ret = this.isNaN();
                } else {
                ret = (Double.doubleToRawLongBits(real) ==
                        Double.doubleToRawLongBits(rhs.getReal())) &&
                    (Double.doubleToRawLongBits(imaginary) ==
                        Double.doubleToRawLongBits(rhs.getImaginary())); 
                }
            } catch (ClassCastException ex) {
                // ignore exception
                ret = false;
            }
        }
      
        return ret;
    }
    
    /**
     * Get a hashCode for the complex number.
     * <p>
     * All NaN values have the same hash code.
     * 
     * @return a hash code value for this object
     */
    public int hashCode() {
        if (isNaN()) {
            return 7;
        }
        return 37 * (17 * MathUtils.hash(imaginary) + 
            MathUtils.hash(real));
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
