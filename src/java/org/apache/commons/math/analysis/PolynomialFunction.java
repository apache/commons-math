/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.analysis;

import java.io.Serializable;

/**
 * Immutable representation of a real polynomial function with real coefficients.
 * <p>
 * <a href="http://mathworld.wolfram.com/HornersMethod.html">Horner's Method</a>
 *  is used to evaluate the function.</p>
 *
 * @version $Revision$ $Date$
 */
public class PolynomialFunction implements DifferentiableUnivariateRealFunction, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = 3322454535052136809L;
    
    /**
     * The coefficients of the polynomial, ordered by degree -- i.e.,  
     * coefficients[0] is the constant term and coefficients[n] is the 
     * coefficient of x^n where n is the degree of the polynomial.
     */
    private double coefficients[];

    /**
     * Construct a polynomial with the given coefficients.  The first element
     * of the coefficients array is the constant term.  Higher degree
     * coefficients follow in sequence.  The degree of the resulting polynomial
     * is the length of the array minus 1. 
     * <p>
     * The constructor makes a copy of the input array and assigns the copy to
     * the coefficients property.</p>
     * 
     * @param c polynominal coefficients
     * @throws NullPointerException if c is null
     * @throws IllegalArgumentException if c is empty
     */
    public PolynomialFunction(double c[]) {
        super();
        if (c.length < 1) {
            throw new IllegalArgumentException("Polynomial coefficient array must have postive length.");
        }
        this.coefficients = new double[c.length];
        System.arraycopy(c, 0, this.coefficients, 0, c.length);
    }

    /**
     * Compute the value of the function for the given argument.
     * <p>
     *  The value returned is <br>
     *   <code>coefficients[n] * x^n + ... + coefficients[1] * x  + coefficients[0]</code>
     * </p>
     * 
     * @param x the argument for which the function value should be computed
     * @return the value of the polynomial at the given point
     * @see UnivariateRealFunction#value(double)
     */
    public double value(double x) {
       return evaluate(coefficients, x);
    }


    /**
     *  Returns the degree of the polynomial
     * 
     * @return the degree of the polynomial
     */
    public int degree() {
        return coefficients.length - 1;
    }
    
    /**
     * Returns a copy of the coefficients array.
     * <p>
     * Changes made to the returned copy will not affect the coefficients of
     * the polynomial.</p>
     * 
     * @return  a fresh copy of the coefficients array
     */
    public double[] getCoefficients() {
        double[] out = new double[coefficients.length];
        System.arraycopy(coefficients,0, out, 0, coefficients.length);
        return out;
    }
    
    /**
     * Uses Horner's Method to evaluate the polynomial with the given coefficients at
     * the argument.
     * 
     * @param coefficients  the coefficients of the polynomial to evaluate
     * @param argument  the input value
     * @return  the value of the polynomial 
     * @throws IllegalArgumentException if coefficients is empty
     * @throws NullPointerException if coefficients is null
     */
    protected static double evaluate(double[] coefficients, double argument) {
        int n = coefficients.length;
        if (n < 1) {
            throw new IllegalArgumentException("Coefficient array must have positive length for evaluation");
        }
        double result = coefficients[n - 1];
        for (int j = n -2; j >=0; j--) {
            result = argument * result + coefficients[j];
        }
        return result;
    }
    
    /**
     * Returns the coefficients of the derivative of the polynomial with the given coefficients.
     * 
     * @param coefficients  the coefficients of the polynomial to differentiate
     * @return the coefficients of the derivative or null if coefficients has length 1.
     * @throws IllegalArgumentException if coefficients is empty
     * @throws NullPointerException if coefficients is null
     */
    protected static double[] differentiate(double[] coefficients) {
        int n = coefficients.length;
        if (n < 1) {
            throw new IllegalArgumentException("Coefficient array must have positive length for differentiation");
        }
        if (n == 1) {
            return new double[]{0};
        }
        double[] result = new double[n - 1];
        for (int i = n - 1; i  > 0; i--) {
            result[i - 1] = (double) i * coefficients[i];
        }
        return result;
    }
    
    /**
     * Returns the derivative as a PolynomialRealFunction
     * 
     * @return  the derivative polynomial
     */
    public PolynomialFunction polynomialDerivative() {
        return new PolynomialFunction(differentiate(coefficients));
    }
    
    /**
     * Returns the derivative as a UnivariateRealFunction
     * 
     * @return  the derivative function
     */
    public UnivariateRealFunction derivative() {
        return polynomialDerivative();
    }
   
}
