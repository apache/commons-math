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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Geronimo" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Geronimo", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
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
 *
 * ====================================================================
 */

package org.apache.commons.math.complex;

/**
 * Reference:
 *   http://myweb.lmu.edu/dmsmith/ZMLIB.pdf
 * 
 * @version $Revision: 1.3 $ $Date: 2003/11/14 22:22:22 $
 */
public class Complex {

    /** The square root of -1. */    
    public static final Complex I = new Complex(0.0, 1.0);
    
    /** */
    public static final Complex NaN = new Complex(Double.NaN, Double.NaN);

    /** 1. */    
    public static final Complex ONE = new Complex(1.0, 0.0);
    
    /** The imaginary part. */
    protected double imaginary;
    
    /** The real part. */
    protected double real;
    
    /**
     * Create a complex number given the real and imaginary parts.
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
     * Return the conjugate of this complex number.
     * @return the conjugate.
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
     * @return the imaginary part.
     */
    public double getImaginary() {
        return imaginary;
    }

    /**
     * Access the real part.
     * @return the real part.
     */
    public double getReal() {
        return real;
    }
    
    /**
     * Returns true if this complex number is the special Not-a-Number (NaN)
     * value.
     * @return true if the value represented by this object is NaN; false
     *         otherwise.
     */
    public boolean isNaN() {
        return Double.isNaN(real) || Double.isNaN(imaginary);        
    }
    
    /**
     * Return the product of this complex number and the given complex number.
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
     * @return the square sum.
     */
    private double squareSum() {
        return real * real + imaginary * imaginary;
    }
    
    /**
     * Return the difference between this complex number and the given complex
     * number.
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
