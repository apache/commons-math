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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
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

package org.apache.commons.math.util;

/**
 * Some useful additions to the built-in functions in {@link Math}.
 *
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:11:23 $
 */
public final class MathUtils {

    /**
     * Private Constructor
     */
    private MathUtils() {
    }

    /**
     * For a double precision value x, this method returns +1.0 if x >= 0
     * and -1.0 if x < 0.
     * @param x the value, a double
     * @return +1.0 or -1.0, depending on the the sign of x
     */
    public static double sign(final double x) {
        if (x >= 0.0) {
            return 1.0;
        } else {
            return -1.0;
        }
    }

    /**
     * For a float value x, this method returns +1.0F if x >= 0
     * and -1.0F if x < 0.
     * @param x the value, a float
     * @return +1.0F or -1.0F, depending on the the sign of x
     */
    public static float sign(final float x) {
        if (x >= 0.0F) {
            return 1.0F;
        } else {
            return -1.0F;
        }
    }

    /**
     * For a byte value x, this method returns (byte)(+1) if x >= 0
     * and (byte)(-1) if x < 0.
     * @param x the value, a byte
     * @return (byte)(+1) or (byte)(-1), depending on the the sign of x
     */
    public static byte sign(final byte x) {
        if (x >= (byte) 0) {
            return (byte) 1;
        } else {
            return (byte) (-1);
        }
    }

    /**
     * For a short value x, this method returns (short)(+1) if x >= 0
     * and (short)(-1) if x < 0.
     *
     * @param x the value, a short
     * @return (short)(+1) or (short)(-1), depending on the the sign of x
     */
    public static short sign(final short x) {
        if (x >= (short) 0) {
            return (short) 1;
        } else {
            return (short) (-1);
        }
    }

    /**
     * For an int value x, this method returns +1 if x >= 0
     * and -1 if x < 0.
     *
     * @param x the value, an int
     * @return +1 or -1, depending on the the sign of x
     */
    public static int sign(final int x) {
        if (x >= 0) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * For a long value x, this method returns +1L if x >= 0
     * and -1L if x < 0.
     *
     * @param x the value, a long
     * @return +1L or -1L, depending on the the sign of x
     */
    public static long sign(final long x) {
        if (x >= 0L) {
            return 1L;
        } else {
            return -1L;
        }
    }
    /**
     * Returns an exact representation of the
     * <a href="http://mathworld.wolfram.com/BinomialCoefficient.html">
     * Binomial Coefficient</a>,  "<code>n choose k</code>",
     * the number of <code>k</code>-element subsets that can be selected from
     * an <code>n</code>-element set.
     * <p>
     * <Strong>Preconditions</strong>:<ul>
     * <li> <code>0 < k <= n </code> (otherwise
     * <li> <code>0 < k <= n </code> (otherwise
     *      <code>IllegalArgumentException</code> is thrown)</li>
     * <li> The result is small enough to fit into a <code>long</code>. The
     *      largest value of <code>n</code> for which all coefficients are
     *      <code> < Long.MAX_VALUE</code> is 66.  If the computed value
     * <li> The result is small enough to fit into a <code>long</code>.  The
     *      largest value of <code>n</code> for which all coefficients are
     *      <code> < Long.MAX_VALUE</code> is 66.  If the computed value
     *      exceeds <code>Long.MAX_VALUE</code> an <code>ArithMeticException
     *      </code> is thrown.</li>
     * </ul>
     *
     *
     * @param n the size of the set
     * @param k the size of the subsets to be counted
     * @return <code>n choose k</code>
     */
    public static long binomialCoefficient(final int n, final int k) {
        if (n < k) {
            throw new IllegalArgumentException(
                "must have n >= k for binomial coefficient (n,k)");
        }
        if (n <= 0) {
            throw new IllegalArgumentException(
                "must have n > 0 for binomial coefficient (n,k)");
        }
        if ((n == k) || (k == 0)) {
            return 1;
        }
        if ((k == 1) || (k == n - 1)) {
            return n;
        }

        long result = Math.round(binomialCoefficientDouble(n, k));
        if (result == Long.MAX_VALUE) {
            throw new ArithmeticException(
                "result too large to represent in a long integer");
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
    public static double binomialCoefficientDouble(final int n, final int k) {
        return Math.floor(Math.exp(binomialCoefficientLog(n, k)) + 0.5);
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
    public static double binomialCoefficientLog(final int n, final int k) {
        if (n < k) {
            throw new IllegalArgumentException(
                "must have n >= k for binomial coefficient (n,k)");
        }
        if (n <= 0) {
            throw new IllegalArgumentException(
                "must have n > 0 for binomial coefficient (n,k)");
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
     * Returns n!.  Shorthand for <code>n</code>
     * <a href="http://mathworld.wolfram.com/Factorial.html">
     * Factorial</a>, the product of the numbers <code>1,...,n</code>.
     * 
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
     * </p>
     * 
     * @param n argument
     * @return <code>n!</code>
     */
    public static long factorial(final int n) {
        long result = Math.round(factorialDouble(n));
        if (result == Long.MAX_VALUE) {
            throw new ArithmeticException(
                "result too large to represent in a long integer");
        }
        return result;
    }

    /**
     * Returns n!.  Shorthand for <code>n</code>
     * <a href="http://mathworld.wolfram.com/Factorial.html">
     * Factorial</a>, the product of the numbers <code>1,...,n</code> as a
     * <code>double</code>.
     * 
     * <p>
     * <Strong>Preconditions</strong>:<ul>
     * <li> <code>n > 0</code> (otherwise
     *      <code>IllegalArgumentException</code> is thrown)</li>
     * <li> The result is small enough to fit into a <code>double</code>.  The
     *      largest value of <code>n</code> for which <code>n!</code>
     *      < Double.MAX_VALUE</code> is 170.  If the computed value exceeds
     *      Double.MAX_VALUE, Double.POSITIVE_INFINITY is returned</li>
     * </ul>
     * </p>
     * 
     * @param n argument
     * @return <code>n!</code>
     */
    public static double factorialDouble(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("must have n > 0 for n!");
        }
        return Math.floor(Math.exp(factorialLog(n)) + 0.5);
    }

    /**
      * Returns the natural logarithm of n!.
      * <p>
      * <Strong>Preconditions</strong>:<ul>
      * <li> <code>n > 0</code> (otherwise
      *      <code>IllegalArgumentException</code> is thrown)</li>
      * </ul>
      *
      * @param n argument
      * @return <code>n!</code>
      */
    public static double factorialLog(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("must have n > 0 for n!");
        }
        double logSum = 0;
        for (int i = 2; i <= n; i++) {
            logSum += Math.log((double) i);
        }
        return logSum;
    }
}