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
 * Implementations of various transcendental functions for
 * {@link org.apache.commons.math.complex.Complex} arguments.
 *
 * Reference:
 * <ul>
 * <li><a href="http://myweb.lmu.edu/dmsmith/ZMLIB.pdf">
 * Multiple Precision Complex Arithmetic and Functions</a></li>
 * </ul>
 *
 * @version $Revision: 1.1 $ $Date: 2004/07/12 00:27:09 $
 */
public class ComplexUtils {
    
    /**
     * Default constructor.
     */
    private ComplexUtils() {
        super();
    }
    
    /**
     * Compute the <a href="http://mathworld.wolfram.com/InverseCosine.html">
     * inverse cosine</a> for the given complex argument.
     * @param z the value whose inverse cosine is to be returned.
     * @return the inverse cosine of <code>z</code>.
     */
    public static Complex acos(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }

        return Complex.I.negate().multiply(log(z.add(
            Complex.I.multiply(sqrt1z(z)))));       
    }
    
    /**
     * Compute the <a href="http://mathworld.wolfram.com/InverseSine.html">
     * inverse sine</a> for the given complex argument.
     * @param z the value whose inverse sine is to be returned.
     * @return the inverse sine of <code>z</code>.
     */
    public static Complex asin(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }

        return Complex.I.negate().multiply(log(sqrt1z(z).add(
            Complex.I.multiply(z))));       
    }
    
    /**
     * Compute the <a href="http://mathworld.wolfram.com/InverseTangent.html">
     * inverse tangent</a> for the given complex argument.
     * @param z the value whose inverse tangent is to be returned.
     * @return the inverse tangent of <code>z</code>.
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
     * Compute the <a href="http://mathworld.wolfram.com/Cosine.html">cosine</a>
     * for the given complex argument.
     * @param z the value whose cosine is to be returned.
     * @return the cosine of <code>z</code>.
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
     * Compute the <a href="http://mathworld.wolfram.com/HyperbolicCosine.html">
     * hyperbolic cosine</a> for the given complex argument.
     * @param z the value whose hyperbolic cosine is to be returned.
     * @return the hyperbolic cosine of <code>z</code>.
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
     * Compute the
     * <a href="http://mathworld.wolfram.com/ExponentialFunction.html">
     * exponential function</a> for the given complex argument.
     * @param z the value.
     * @return <i>e</i><sup><code>z</code></sup>.
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
     * Compute the <a href="http://mathworld.wolfram.com/NaturalLogarithm.html">
     * natural logarithm</a> for the given complex argument.
     * @param z the value.
     * @return ln <code>z</code>.
     */
    public static Complex log(Complex z) {
        if (z.isNaN()) {
            return Complex.NaN;
        }

        return new Complex(Math.log(z.abs()),
            Math.atan2(z.getImaginary(), z.getReal()));        
    }
    
    
    /**
     * Returns of value of <code>y</code> raised to the power of <code>x</code>.
     * @param y the base.
     * @param x the exponent.
     * @return <code>y</code><sup><code>z</code></sup>.
     */
    public static Complex pow(Complex y, Complex x) {
        return exp(x.multiply(log(y)));
    }
    
    /**
     * Compute the <a href="http://mathworld.wolfram.com/Sine.html">sine</a>
     * for the given complex argument.
     * @param z the value whose sine is to be returned.
     * @return the sine of <code>z</code>.
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
     * Compute the <a href="http://mathworld.wolfram.com/HyperbolicSine.html">
     * hyperbolic sine</a> for the given complex argument.
     * @param z the value whose hyperbolic sine is to be returned.
     * @return the hyperbolic sine of <code>z</code>.
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
     * Compute the <a href="http://mathworld.wolfram.com/SquareRoot.html">squre
     * root</a> for the given complex argument.
     * @param z the value whose square root is to be returned.
     * @return the square root of <code>z</code>.
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
     * Compute the <a href="http://mathworld.wolfram.com/SquareRoot.html">squre
     * root of 1 - <code>z</code><sup>2</sup> for the given complex argument.
     * @param z the value.
     * @return the square root of 1 - <code>z</code><sup>2</sup>.
     */
    public static Complex sqrt1z(Complex z) {
        return sqrt(Complex.ONE.subtract(z.multiply(z)));
    }
    
    /**
     * Compute the <a href="http://mathworld.wolfram.com/Tangent.html">
     * tangent</a> for the given complex argument.
     * @param z the value whose tangent is to be returned.
     * @return the tangent of <code>z</code>.
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
     * Compute the
     * <a href="http://mathworld.wolfram.com/HyperbolicTangent.html">
     * hyperbolic tangent</a> for the given complex argument.
     * @param z the value whose hyperbolic tangent is to be returned.
     * @return the hyperbolic tangent of <code>z</code>.
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
