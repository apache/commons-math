/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.commons.math.fraction;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.util.MathUtils;

/**
 * Representation of a rational number.
 *
 * @author Apache Software Foundation
 * @version $Revision$ $Date$
 */
public class Fraction extends Number implements Comparable {

    /** A fraction representing "1 / 1". */
    public static final Fraction ONE = new Fraction(1, 1);

    /** A fraction representing "0 / 1". */
    public static final Fraction ZERO = new Fraction(0, 1);
    
    /** Serializable version identifier */
    static final long serialVersionUID = 65382027393090L;
    
    /** The denominator. */
    private int denominator;
    
    /** The numerator. */
    private int numerator;

    /**
     * Create a fraction given the double value.
     * @param value the double value to convert to a fraction.
     * @throws ConvergenceException if the continued fraction failed to
     *         converge.
     */
    public Fraction(double value) throws ConvergenceException {
        this(value, 1.0e-5, 100);
    }

    /**
     * Create a fraction given the double value.
     * <p>
     * References:
     * <ul>
     * <li><a href="http://mathworld.wolfram.com/ContinuedFraction.html">
     * Continued Fraction</a> equations (11) and (22)-(26)</li>
     * </ul>
     * </p>
     * @param value the double value to convert to a fraction.
     * @param epsilon maximum error allowed.  The resulting fraction is within
     *        <code>epsilon</code> of <code>value</code>, in absolute terms.
     * @param maxIterations maximum number of convergents
     * @throws ConvergenceException if the continued fraction failed to
     *         converge.
     */
    public Fraction(double value, double epsilon, int maxIterations)
        throws ConvergenceException
    {
        double r0 = value;
        int a0 = (int)Math.floor(r0);
        
        int p0 = 1;
        int q0 = 0;
        int p1 = a0;
        int q1 = 1;

        int p2 = 0;
        int q2 = 1;

        int n = 0;
        boolean stop = false;
        do {
            ++n;
            double r1 = 1.0 / (r0 - a0);
            int a1 = (int)Math.floor(r1);
            p2 = (a1 * p1) + p0;
            q2 = (a1 * q1) + q0;
            
            double convergent = (double)p2 / (double)q2;
            if (n < maxIterations && Math.abs(convergent - value) > epsilon) {
                p0 = p1;
                p1 = p2;
                q0 = q1;
                q1 = q2;
                a0 = a1;
                r0 = r1;
            } else {
                stop = true;
            }
        } while (!stop);

        if (n >= maxIterations) {
            throw new ConvergenceException(
                    "Unable to convert double to fraction");
        }
        
        this.numerator = p2;
        this.denominator = q2;
        reduce();
    }
    
    /**
     * Create a fraction given the numerator and denominator.  The fraction is
     * reduced to lowest terms.
     * @param num the numerator.
     * @param den the denominator.
     */
    public Fraction(int num, int den) {
        super();
        this.numerator = num;
        this.denominator = den;
        reduce();
    }
    
    /**
     * Returns the absolute value of this fraction.
     * @return the absolute value.
     */
    public Fraction abs() {
        Fraction ret;
        if (numerator >= 0) {
            ret = this;
        } else {
            ret = negate();
        }
        return ret;        
    }
    
    /**
     * Return the sum of this fraction and the given fraction.  The returned
     * fraction is reduced to lowest terms.
     *
     * @param rhs the other fraction.
     * @return the fraction sum in lowest terms.
     */
    public Fraction add(Fraction rhs) {
        int den = MathUtils.lcm(denominator, rhs.denominator);
        int num = (numerator * (den / denominator)) +
            (rhs.numerator * (den / rhs.denominator));
        return new Fraction(num, den);
    }
    
    /**
     * Compares this object to another based on size.
     * @param object the object to compare to
     * @return -1 if this is less than <tt>object</tt>, +1 if this is greater
     *         than <tt>object</tt>, 0 if they are equal.
     */
    public int compareTo(Object object) {
        int ret = 0;
        
        if (this != object) { 
            Fraction other = (Fraction)object;
            double first = doubleValue();
            double second = other.doubleValue();
            
            if (first < second) {
                ret = -1;
            } else if (first > second) {
                ret = 1;
            }
        }
        
        return ret;
    }

    /**
     * Return the quotient of this fraction and the given fraction.  The
     * returned fraction is reduced to lowest terms.
     * @param rhs the other fraction.
     * @return the fraction quotient in lowest terms.
     */
    public Fraction divide(Fraction rhs) {
        return multiply(rhs.reciprocal());
    }
    
    /**
     * Gets the fraction as a <tt>double</tt>. This calculates the fraction as
     * the numerator divided by denominator.
     * @return the fraction as a <tt>double</tt>
     */
    public double doubleValue() {
        return (double)numerator / (double)denominator;
    }
    
    /**
     * Test for the equality of two fractions.  If the lowest term
     * numerator and denominators are the same for both fractions, the two
     * fractions are considered to be equal.
     * @param other fraction to test for equality to this fraction
     * @return true if two fractions are equal, false if object is
     *         <tt>null</tt>, not an instance of {@link Fraction}, or not equal
     *         to this fraction instance.
     */
    public boolean equals(Object other) {
        boolean ret;
        
        if (this == other) { 
            ret = true;
        } else if (other == null) {
            ret = false;
        } else {
            try {
                // since fractions are always in lowest terms, numerators and
                // denominators can be compared directly for equality.
                Fraction rhs = (Fraction)other;
                ret = (numerator == rhs.numerator) &&
                    (denominator == rhs.denominator);
            } catch (ClassCastException ex) {
                // ignore exception
                ret = false;
            }
        }
        
        return ret;
    }
    
    /**
     * Gets the fraction as a <tt>float</tt>. This calculates the fraction as
     * the numerator divided by denominator.
     * @return the fraction as a <tt>float</tt>
     */
    public float floatValue() {
        return (float)doubleValue();
    }
    
    /**
     * Access the denominator.
     * @return the denominator.
     */
    public int getDenominator() {
        return denominator;
    }
    
    /**
     * Access the numerator.
     * @return the numerator.
     */
    public int getNumerator() {
        return numerator;
    }
    
    /**
     * Gets a hashCode for the fraction.
     * @return a hash code value for this object
     */
    public int hashCode() {
        return 37 * (37 * 17 + getNumerator()) + getDenominator();
    }
    
    /**
     * Gets the fraction as an <tt>int</tt>. This returns the whole number part
     * of the fraction.
     * @return the whole number fraction part
     */
    public int intValue() {
        return (int)doubleValue();
    }
    
    /**
     * Gets the fraction as a <tt>long</tt>. This returns the whole number part
     * of the fraction.
     * @return the whole number fraction part
     */
    public long longValue() {
        return (long)doubleValue();
    }
    
    /**
     * Return the product of this fraction and the given fraction.  The returned
     * fraction is reduced to lowest terms.
     * @param rhs the other fraction.
     * @return the fraction product in lowest terms.
     */
    public Fraction multiply(Fraction rhs) {
        return new Fraction(numerator * rhs.numerator, 
                denominator * rhs.denominator);
    }
    
    /**
     * Return the additive inverse of this fraction.
     * @return the negation of this fraction.
     */
    public Fraction negate() {
        return new Fraction(-numerator, denominator);
    }

    /**
     * Return the multiplicative inverse of this fraction.
     * @return the reciprocal fraction
     */
    public Fraction reciprocal() {
        return new Fraction(denominator, numerator);
    }
    
    /**
     * Return the difference between this fraction and the given fraction.  The
     * returned fraction is reduced to lowest terms.
     * @param rhs the other fraction.
     * @return the fraction difference in lowest terms.
     */
    public Fraction subtract(Fraction rhs) {
        return add(rhs.negate());
    }
    
    /**
     * Reduce this fraction to lowest terms.  This is accomplished by dividing
     * both numerator and denominator by their greatest common divisor.
     */
    private void reduce() {
        // reduce numerator and denominator by greatest common denominator.
        int d = MathUtils.gcd(numerator, denominator);
        if (d > 1) {
            numerator /= d;
            denominator /= d;
        }

        // move sign to numerator.
        if (denominator < 0) {
            numerator *= -1;
            denominator *= -1;
        }
    }
}
