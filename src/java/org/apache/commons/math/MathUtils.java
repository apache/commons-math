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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 */

package org.apache.commons.math;

/**
 * Some useful additions to the built-in functions in lang.Math<p>
 *
 * @author Phil Steitz
 * @version $Revision: 1.1 $ $Date: 2003/06/04 02:31:13 $
 */
public class MathUtils {

    /**
     * Returns an exact representation of the 
     * <a href="http://mathworld.wolfram.com/BinomialCoefficient.html"> 
     * Binomial Coefficient</a>,  "<code>n choose k</code>", 
     * the number of <code>k</code>-element subsets that can be selected from 
     * an <code>n</code>-element set.
     * <p>
     * <Strong>Preconditions</strong>:<ul>
     * <li> <code>0 < k <= n </code> (otherwise 
     *      <code>IllegalArgumentException</code> is thrown)</li>
     * <li> The result is small enough to fit into a <code>long</code>.  The 
     *      largest value of <code>n</code> for which all coefficients are 
     *      <code> < Long.MAX_VALUE</code> is 66.  If the computed value 
     *      exceeds <code>Long.MAX_VALUE</code> an <code>ArithMeticException
     *      </code> is thrown.</li>
     * </ul>
     * 
     * @param n the size of the set
     * @param k the size of the subsets to be counted
     * @return <code>n choose k</code>
     */
    public static long binomialCoefficient(int n, int k) {     
        if (n < k) {
            throw new IllegalArgumentException
                ("must have n >= k for binomial coefficient (n,k)");
        }
        if (n <= 0)  {
            throw new IllegalArgumentException
                ("must have n > 0 for binomial coefficient (n,k)");
        }
        if ((n == k) || (k == 0)) {
            return 1;
        }
        if ((k == 1) || (k == n - 1)) {
            return n;
        }
        
        long result = Math.round(binomialCoefficientDouble(n, k));
        if (result == Long.MAX_VALUE) {
            throw new ArithmeticException
                ("result too large to represent in a long integer");
        }
        return result;   
    } 
    
    /**
     * Returns a <code>double</code> representation of the 
     * <a href="http://mathworld.wolfram.com/BinomialCoefficient.html"> 
     * Binomial Coefficient</a>,  "<code>n choose k</code>", 
     * the number of <code>k</code>-element subsets that can be selected from 
     * an <code>n</code>-element set.
     * <p>
     * <Strong>Preconditions</strong>:<ul>
     * <li> <code>0 < k <= n </code> (otherwise 
     *      <code>IllegalArgumentException</code> is thrown)</li>
     * <li> The result is small enough to fit into a <code>double</code>.  
     *      The largest value of <code>n</code> for which all coefficients are 
     *      < Double.MAX_VALUE is 1029.  If the computed value exceeds 
     *      Double.MAX_VALUE, Double.POSITIVE_INFINITY is returned</li>
     * </ul>
     * 
     * @param n the size of the set
     * @param k the size of the subsets to be counted
     * @return <code>n choose k</code>
     */
    public static double binomialCoefficientDouble(int n, int k) {  
        return Math.floor(Math.exp(binomialCoefficientLog(n, k)) + .5);    
    }
    
    /**
     * Returns the natural <code>log</code> of the
     * <a href="http://mathworld.wolfram.com/BinomialCoefficient.html"> 
     * Binomial Coefficient</a>,  "<code>n choose k</code>", 
     * the number of <code>k</code>-element subsets that can be selected from 
     * an <code>n</code>-element set.
     * <p>
     * <Strong>Preconditions</strong>:<ul>
     * <li> <code>0 < k <= n </code> (otherwise 
     *      <code>IllegalArgumentException</code> is thrown)</li>
     * </ul>
     * 
     * @param n the size of the set
     * @param k the size of the subsets to be counted
     * @return <code>n choose k</code>
     */
    public static double binomialCoefficientLog(int n, int k) {
        if (n < k) {
            throw new IllegalArgumentException
                ("must have n >= k for binomial coefficient (n,k)");
        }
        if (n <= 0)  {
            throw new IllegalArgumentException
                ("must have n > 0 for binomial coefficient (n,k)");
        }
        if ((n == k) || (k == 0)) {
            return 0;
        }
        if ((k == 1) || (k == n - 1)) {
            return Math.log((double) n);
        }    
        double logSum = 0; 
        
        // n!/k!
        for (int i = k + 1; i <= n; i++) {
            logSum += Math.log((double) i);
        }
        
        // divide by (n-k)!
        for (int i = 2; i <= n - k; i++) {
            logSum -= Math.log((double) i);
        }
        
        return logSum;
    }
    
    /**
     * Returns <code>n</code>
     * <a href="http://mathworld.wolfram.com/Factorial.html"> 
     * Factorial</a>, or <code>n!</code>,  
     * the product of the numbers <code>1,...,n</code>.
     * <p>
     * <Strong>Preconditions</strong>:<ul>
     * <li> <code>n > 0</code> (otherwise 
     *      <code>IllegalArgumentException</code> is thrown)</li>
     * <li> The result is small enough to fit into a <code>long</code>.  The 
     *      largest value of <code>n</code> for which <code>n!</code> 
     *      < Long.MAX_VALUE</code> is 20.  If the computed value 
     *      exceeds <code>Long.MAX_VALUE</code> an <code>ArithMeticException
     *      </code> is thrown.</li>
     * </ul>
     * 
     * @param n argument
     * @return <code>n!</code>
     */
    public static long factorial(int n) {
        long result = Math.round(factorialDouble(n));
        if (result == Long.MAX_VALUE) {
            throw new ArithmeticException
                ("result too large to represent in a long integer");
        }
        return result;  
    }
    
    /**
     * Returns <code>n</code>
     * <a href="http://mathworld.wolfram.com/Factorial.html"> 
     * Factorial</a>, or <code>n!</code>,  
     * the product of the numbers <code>1,...,n</code>, as as 
     * <code>double</code>.
     * <p>
     * <Strong>Preconditions</strong>:<ul>
     * <li> <code>n > 0</code> (otherwise 
     *      <code>IllegalArgumentException</code> is thrown)</li>
     * <li> The result is small enough to fit into a <code>double</code>.  The 
     *      largest value of <code>n</code> for which <code>n!</code> 
     *      < Double.MAX_VALUE</code> is 170.  If the computed value exceeds 
     *      Double.MAX_VALUE, Double.POSITIVE_INFINITY is returned</li>
     * </ul>
     * 
     * @param n argument
     * @return <code>n!</code>
     */
    public static double factorialDouble(int n) {
        if (n <= 0)  {
            throw new IllegalArgumentException
                ("must have n > 0 for n!");
        }
        return Math.floor(Math.exp(factorialLog(n)) + 0.5); 
    }
    
   /**
     * Returns the natural <code>log</code> of <code>n</code>
     * <a href="http://mathworld.wolfram.com/Factorial.html"> 
     * Factorial</a>, or <code>n!</code>,  
     * the product of the numbers <code>1,...,n</code>, as as 
     * <code>double</code>.
     * <p>
     * <Strong>Preconditions</strong>:<ul>
     * <li> <code>n > 0</code> (otherwise 
     *      <code>IllegalArgumentException</code> is thrown)</li>
     * </ul>
     * 
     * @param n argument
     * @return <code>n!</code>
     */
    public static double factorialLog(int n) {
        if (n <= 0)  {
            throw new IllegalArgumentException
                ("must have n > 0 for n!");
        }
        double logSum = 0;
        for (int i = 2; i <= n; i++) {
            logSum += Math.log((double) i);
        }   
        return logSum;
    }           
}