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

package org.apache.commons.math.stat;


/**
 * Implements the following test statistics <ul>
 * <li>
 *   <a href ="http://www.itl.nist.gov/div898/handbook/eda/section3/eda35f.htm">
 *   Chi-Square</a>
 * </li>
 * <li>
 *   <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda352.htm">
 *     One Sample t-test</a>
 * </li>
 * </ul>
 * @author Phil Steitz
 * @version $Revision: 1.1 $ $Date: 2003/06/21 23:00:39 $
 * 
 */
public class TestStatisticImpl implements TestStatistic {
    
    /**
     * Default constructor.
     */
    public TestStatisticImpl() {
    }
    
    /**
     * Computes Chi-Square statistic given observed and expected counts <br>
     * <strong>Algorithm</strong>: 
     * http://www.itl.nist.gov/div898/handbook/eda/section3/eda35f.htm <br>
     * <strong>Numerical considerations</strong>: none <br>
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @throws IllegalArgumentException if input arrays have different lengths
     * or length is less than 2
     */
    public double chiSquare(double[] expected, double[] observed) {
        double sumSq = 0.0d;
        double dev = 0.0d;
        if ((expected.length < 2) || (expected.length != observed.length)) {
            throw new IllegalArgumentException
                ("observed, expected array lengths incorrect");
        }
        for (int i = 0; i < observed.length; i++) {
            dev = (observed[i] - expected[i]);
            sumSq += dev * dev / expected[i];
        }
        
        return sumSq;
    }           

    /**
     * Computes t statistic given observed values<br/>
     * <strong>Algorithm</strong>: 
     * http://www.itl.nist.gov/div898/handbook/eda/section3/eda352.htm<br/>
     * <strong>Numerical considerations</strong>: none <br>
     * @param mu hypothesized mean value.
     * @param observed array of observed values
     * @return t-test statistic for the hypothesized mean and observed values.
     * @throws IllegalArgumentException if input array length is less than 2
     */
	public double t(double mu, double[] observed) {
        if((observed == null) || (observed.length < 2)) {
            throw new IllegalArgumentException
                ("observed array length incorrect");
        }
        
        // leverage Univariate to compute statistics
        Univariate univariate = new UnivariateImpl();
        for (int i = 0; i < observed.length; i++) {
			univariate.addValue(observed[i]);
		}
        double n = univariate.getN();
        double xbar = univariate.getMean();
        double std = univariate.getStandardDeviation();

        return (xbar - mu) / (std / Math.sqrt(n));
	}
}
