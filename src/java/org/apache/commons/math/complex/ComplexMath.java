/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2004 The Apache Software Foundation.  All rights
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

import org.apache.commons.math.util.MathUtils;

/**
 * Reference:
 *   http://myweb.lmu.edu/dmsmith/ZMLIB.pdf
 * 
 * @version $Revision: 1.6 $ $Date: 2004/01/29 00:48:58 $
 */
public class ComplexMath {
    
    /**
     * 
     */
    private ComplexMath() {
        super();
    }
    
    /**
     * 
     */
    public static Complex acos(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }

        return Complex.I.negate().multiply(log(z.add(
            Complex.I.multiply(sqrt1z(z)))));       
    }
    
    /**
     * 
     */
    public static Complex asin(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }

        return Complex.I.negate().multiply(log(sqrt1z(z).add(
            Complex.I.multiply(z))));       
    }
    
    /**
     * 
     */
    public static Complex atan(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }
        
        
        return Complex.I.multiply(
            log(Complex.I.add(z).divide(Complex.I.subtract(z))))
            .divide(new Complex(2.0, 0.0));
    }
    
    /**
     * 
     */
    public static Complex cos(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }
        
        double a = z.getReal();
        double b = z.getImaginary();
        
        return new Complex(Math.cos(a) * MathUtils.cosh(b),
            -Math.sin(a) * MathUtils.sinh(b));
    }
    
    /**
     * 
     */
    public static Complex cosh(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }
        
        double a = z.getReal();
        double b = z.getImaginary();
        
        return new Complex(MathUtils.cosh(a) * Math.cos(b),
            MathUtils.sinh(a) * Math.sin(b));
    }
    
    /**
     * 
     */
    public static Complex exp(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }
        
        double b = z.getImaginary();
        double expA = Math.exp(z.getReal());
        double sinB = Math.sin(b);
        double cosB = Math.cos(b);
        return new Complex(expA * cosB, expA * sinB);
    }
    
    /**
     * 
     */
    public static Complex log(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }

        return new Complex(Math.log(z.abs()),
            Math.atan2(z.getImaginary(), z.getReal()));        
    }
    
    /**
     * 
     */
    public static Complex pow(Complex y, Complex x) {
        return exp(x.multiply(log(y)));
    }
    
    /**
     * 
     */
    public static Complex sin(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }
        
        double a = z.getReal();
        double b = z.getImaginary();
        
        return new Complex(Math.sin(a) * MathUtils.cosh(b),
            Math.cos(a) * MathUtils.sinh(b));
    }
    
    /**
     * 
     */
    public static Complex sinh(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }
        
        double a = z.getReal();
        double b = z.getImaginary();
        
        return new Complex(MathUtils.sinh(a) * Math.cos(b),
            MathUtils.cosh(a) * Math.sin(b));
    }
    
    /**
     * 
     */
    public static Complex sqrt(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }
        
        double a = z.getReal();
        double b = z.getImaginary();
        
        double t = Math.sqrt((Math.abs(a) + z.abs()) / 2.0);
        if (a >= 0.0) {
            return new Complex(t, b / (2.0 * t));
        } else {
            return new Complex(Math.abs(z.getImaginary()) / (2.0 * t),
                MathUtils.indicator(b) * t);
        }
    }
    
    /**
     * Returns the square root of 1 - z^2.
     * @return the square root of 1 - z^2.
     */
    public static Complex sqrt1z(Complex z) {
        return sqrt(Complex.ONE.subtract(z.multiply(z)));
    }
    
    /**
     * 
     */
    public static Complex tan(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }
        
        double a2 = 2.0 * z.getReal();
        double b2 = 2.0 * z.getImaginary();
        double d = Math.cos(a2) + MathUtils.cosh(b2);
        
        return new Complex(Math.sin(a2) / d, MathUtils.sinh(b2) / d);
    }
    
    /**
     * 
     */
    public static Complex tanh(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }
        
        double a2 = 2.0 * z.getReal();
        double b2 = 2.0 * z.getImaginary();
        double d = MathUtils.cosh(a2) + Math.cos(b2);
        
        return new Complex(MathUtils.sinh(a2) / d, Math.sin(b2) / d);
    }
}
