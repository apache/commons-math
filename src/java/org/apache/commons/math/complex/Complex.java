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

import java.io.Serializable;
import org.apache.commons.math.util.MathUtils;

/**
 * Representation of a Complex number - a number which has both a 
 * real and imaginary part.
 * <p>
 * Implementations of arithmetic operations handle <code>NaN</code> and
 * infinite values according to the rules for {@link java.lang.Double}
 * arithmetic, applying definitional formulas and returning <code>NaN</code> or
 * infinite values in real or imaginary parts as these arise in computation. 
 * See individual method javadocs for details.
 * <p>
 * {@link #equals} identifies all values with <code>NaN</code> in either real 
 * or imaginary part - e.g., <pre>
 * <code>1 + NaNi  == NaN + i == NaN + NaNi.</code></pre>
 *
 * @author Apache Software Foundation
 * @version $Revision$ $Date$
 */
public class Complex implements Serializable  {

    /** Serializable version identifier */
    private static final long serialVersionUID = -6530173849413811929L;
    
    /** The square root of -1. A number representing "0.0 + 1.0i" */    
    public static final Complex I = new Complex(0.0, 1.0);
    
    /** A complex number representing "NaN + NaNi" */
    public static final Complex NaN = new Complex(Double.NaN, Double.NaN);

    /** A complex number representing "1.0 + 0.0i" */    
    public static final Complex ONE = new Complex(1.0, 0.0);
    
    /** A complex number representing "0.0 + 0.0i" */    
    public static final Complex ZERO = new Complex(0.0, 0.0);
    
    /** The imaginary part */
    protected double imaginary;
    
    /** The real part */
    protected double real;
    
    /**
     * Create a complex number given the real and imaginary parts.
     *
     * @param real the real part
     * @param imaginary the imaginary part
     */
    public Complex(double real, double imaginary) {
        super();
        this.real = real;
        this.imaginary = imaginary;
    }

    /**
     * Return the absolute value of this complex number.
     * <p>
     * Returns <code>NaN</code> if either real or imaginary part is
     * <code>NaN</code> and <code>Double.POSITIVE_INFINITY</code> if
     * neither part is <code>NaN</code>, but at least one part takes an infinite
     * value.
     *
     * @return the absolute value
     */
    public double abs() {
        if (isNaN()) {
            return Double.NaN;
        }
        
        if (isInfinite()) {
            return Double.POSITIVE_INFINITY;
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
     * <p>
     * Uses the definitional formula 
     * <pre>
     * (a + bi) + (c + di) = (a+c) + (b+d)i
     * </pre>
     * <p>
     * If either this or <code>rhs</code> has a NaN value in either part,
     * {@link #NaN} is returned; otherwise Inifinite and NaN values are
     * returned in the parts of the result according to the rules for
     * {@link java.lang.Double} arithmetic. 
     *
     * @param rhs the other complex number
     * @return the complex number sum
     * @throws NullPointerException if <code>rhs</code> is null
     */
    public Complex add(Complex rhs) {   
        return new Complex(real + rhs.getReal(),
            imaginary + rhs.getImaginary());
    }
    
    /**
     * Return the conjugate of this complex number. The conjugate of
     * "A + Bi" is "A - Bi". 
     * <p>
     * {@link #NaN} is returned if either the real or imaginary
     * part of this Complex number equals <code>Double.NaN</code>.
     * <p>
     * If the imaginary part is infinite, and the real part is not NaN, 
     * the returned value has infinite imaginary part of the opposite
     * sign - e.g. the conjugate of <code>1 + POSITIVE_INFINITY i</code>
     * is <code>1 - NEGATIVE_INFINITY i</code>
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
     * <p>
     * Implements the definitional formula
     * <pre><code>
     *    a + bi          ac + bd + (bc - ad)i
     *    ----------- = -------------------------
     *    c + di               c<sup>2</sup> + d<sup>2</sup>
     * </code></pre>
     * but uses 
     * <a href="http://doi.acm.org/10.1145/1039813.1039814">
     * prescaling of operands</a> to limit the effects of overflows and
     * underflows in the computation.
     * <p>
     * Infinite and NaN values are handled / returned according to the
     * following rules, applied in the order presented:
     * <ul>
     * <li>If either this or <code>rhs</code> has a NaN value in either part,
     *  {@link #NaN} is returned.</li>
     * <li>If <code>rhs</code> equals {@link #ZERO}, {@link #NaN} is returned.
     * </li>
     * <li>If this and <code>rhs</code> are both infinite,
     * {@link #NaN} is returned.</li>
     * <li>If this is finite (i.e., has no infinite or NaN parts) and
     *  <code>rhs</code> is infinite (one or both parts infinite), 
     * {@link #ZERO} is returned.</li>
     * <li>If this is infinite and <code>rhs</code> is finite, NaN values are
     * returned in the parts of the result if the {@link java.lang.Double}
     * rules applied to the definitional formula force NaN results.</li>
     * </ul>
     * 
     * @param rhs the other complex number
     * @return the complex number quotient
     * @throws NullPointerException if <code>rhs</code> is null
     */
    public Complex divide(Complex rhs) {
        if (isNaN() || rhs.isNaN()) {
            return NaN;
        }

        double c = rhs.getReal();
        double d = rhs.getImaginary();
        if (c == 0.0 && d == 0.0) {
            return NaN;
        }
        
        if (rhs.isInfinite() && !isInfinite()) {
            return ZERO;
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
     * to <code>Double.NaN</code>, the complex number is equal to 
     * <code>Complex.NaN</code>.
     *
     * @param other Object to test for equality to this
     * @return true if two Complex objects are equal, false if
     *         object is null, not an instance of Complex, or
     *         not equal to this Complex instance
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
     * @return the imaginary part
     */
    public double getImaginary() {
        return imaginary;
    }

    /**
     * Access the real part.
     *
     * @return the real part
     */
    public double getReal() {
        return real;
    }
    
    /**
     * Returns true if either or both parts of this complex number is NaN;
     * false otherwise
     *
     * @return  true if either or both parts of this complex number is NaN;
     * false otherwise
     */
    public boolean isNaN() {
        return Double.isNaN(real) || Double.isNaN(imaginary);        
    }
    
    /**
     * Returns true if either the real or imaginary part of this complex number
     * takes an infinite value (either <code>Double.POSITIVE_INFINITY</code> or 
     * <code>Double.NEGATIVE_INFINITY</code>) and neither part
     * is <code>NaN</code>.
     * 
     * @return true if one or both parts of this complex number are infinite
     * and neither part is <code>NaN</code>
     */
    public boolean isInfinite() {
        return !isNaN() && 
        (Double.isInfinite(real) || Double.isInfinite(imaginary));        
    }
    
    /**
     * Return the product of this complex number and the given complex number.
     * <p>
     * Implements the definitional formula:
     * <pre><code>
     * (a + bi)(c + di) = (ac - bd) + (ad + bc)i
     * </code></pre>
     * <p>
     * Returns {@link #NaN} if either this or <code>rhs</code> has one or more
     * NaN parts.
     * <p>
     * Returns NaN or infinite values in components of the result per the
     * definitional formula and and the rules for {@link java.lang.Double}
     * arithmetic.  Examples:
     * <pre><code>
     *  (1 + i) (INF + i)  =  INF + INFi
     *  (1 + INFi) (1 - INFi) = INF + NaNi
     *  (-INF + -INFi)(1 + NaNi) = NaN + NaNi
     *  </code></pre>
     * 
     * @param rhs the other complex number
     * @return the complex number product
     * @throws NullPointerException if <code>rhs</code> is null
     */
    public Complex multiply(Complex rhs) {
        if (isNaN() || rhs.isNaN()) {
            return NaN;
        }
        return new Complex(real * rhs.real - imaginary * rhs.imaginary,
                real * rhs.imaginary + imaginary * rhs.real);
    }
    
    /**
     * Return the additive inverse of this complex number.
     * <p>
     * Returns <code>Complex.NaN</code> if either real or imaginary
     * part of this Complex number equals <code>Double.NaN</code>.
     *
     * @return the negation of this complex number
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
      * <p>
     * Uses the definitional formula 
     * <pre>
     * (a + bi) - (c + di) = (a-c) + (b-d)i
     * </pre>
     * <p>
     * If either this or <code>rhs</code> has a NaN value in either part,
     * {@link #NaN} is returned; otherwise inifinite and NaN values are
     * returned in the parts of the result according to the rules for
     * {@link java.lang.Double} arithmetic. 
     * 
     * @param rhs the other complex number
     * @return the complex number difference
     * @throws NullPointerException if <code>rhs</code> is null
     */
    public Complex subtract(Complex rhs) {
        if (isNaN() || rhs.isNaN()) {
            return NaN;
        }
        
        return new Complex(real - rhs.getReal(),
            imaginary - rhs.getImaginary());
    }
}
