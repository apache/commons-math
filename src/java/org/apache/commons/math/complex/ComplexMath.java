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

import org.apache.commons.math.util.MathUtils;

/**
 * Reference:
 *   http://myweb.lmu.edu/dmsmith/ZMLIB.pdf
 * 
 * @version $Revision: 1.7 $ $Date: 2004/02/21 21:35:14 $
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
