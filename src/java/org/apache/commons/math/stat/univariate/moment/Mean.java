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
package org.apache.commons.math.stat.univariate.moment;

import org
    .apache
    .commons
    .math
    .stat
    .univariate
    .AbstractStorelessUnivariateStatistic;
import org.apache.commons.math.stat.univariate.summary.Sum;

/**
 * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
 * arithmetic mean </a> of the available values.
 * @version $Revision: 1.9 $ $Date: 2003/10/13 08:10:57 $
 */
public class Mean extends AbstractStorelessUnivariateStatistic {

    /** first moment of values that have been added */
    protected FirstMoment moment = null;

    /** */
    protected boolean incMoment = true;

    /** */
    public Mean() {
        moment = new FirstMoment();
    }

    /**
     * Constructs a Mean with an External Moment.
     * @param m1 the moment
     */
    public Mean(final FirstMoment m1) {
        this.moment = m1;
        incMoment = false;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        if (incMoment) {
            moment.increment(d);
        }
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        if (incMoment) {
            moment.clear();
        }
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        return moment.m1;
    }

    /*UnvariateStatistic Approach */

    /** */
    protected Sum sum = new Sum();

    /**
     * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
     * arithmetic mean </a> of a double[] of the available values.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the mean of the values or Double.NaN if the array is empty
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[], int, int)
     */
    public double evaluate(
        final double[] values,
        final int begin,
        final int length) {
        if (test(values, begin, length)) {
            return sum.evaluate(values) / ((double) length);
        }
        return Double.NaN;
    }
}