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
package org.apache.commons.math.stat.univariate;

/**
 * Abstract Implementation for UnivariateStatistics.
 * Provides the ability to extend polymophically so that
 * indiviual statistics do not need to implement these methods.
 * @version $Revision: 1.7 $ $Date: 2003/10/13 08:10:57 $
 */
public abstract class AbstractUnivariateStatistic
    implements UnivariateStatistic {

    /**
     * This implementation provides a simple wrapper around the double[]
     * and passes the request onto the evaluate(DoubleArray da) method.
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[])
     */
    public double evaluate(final double[] values) {
        return evaluate(values, 0, values.length);
    }

    /**
     * Subclasses of AbstractUnivariateStatistc need to implement this method.
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[], int, int)
     */
    public abstract double evaluate(
        final double[] values,
        final int begin,
        final int length);

    /**
     * this protected test method used by all methods to verify the content
     * of the array and indicies are correct.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return this is used to determine if the array is of 0 length or not,
     * it is used by an individual statistic to determine if continuation
     * of a statistical calculation should continue or return NaN.
     */
    protected boolean test(
        final double[] values,
        final int begin,
        final int length) {

        if (length > values.length) {
            throw new IllegalArgumentException("length > values.length");
        }

        if (begin + length > values.length) {
            throw new IllegalArgumentException(
                "begin + length > values.length");
        }

        if (values == null) {
            throw new IllegalArgumentException("input value array is null");
        }

        if (values.length == 0 || length == 0) {
            return false;
        }

        return true;

    }
}