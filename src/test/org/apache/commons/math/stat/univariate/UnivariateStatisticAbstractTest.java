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
package org.apache.commons.math.stat.univariate;

import junit.framework.TestCase;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 *
 * @author Mark Diggory
 */
public abstract class UnivariateStatisticAbstractTest extends TestCase {

    protected double mean = 12.40454545454550;
    protected double var = 10.00235930735930;
    protected double std = Math.sqrt(var);
    protected double skew = 1.437423729196190;
    protected double kurt = 2.377191264804700;

    protected double min = 8.2;
    protected double max = 21;
    protected double median = 12;
    protected double percentile5 = 8.81;
    protected double percentile95 = 19.555;

    protected double product = 628096400563833200000000.0;
    protected double sumLog = 54.79698061164520;
    protected double sumSq = 3595.250;
    protected double sum = 272.90;

    protected double tolerance = 10E-12;

    protected double[] testArray =
        {
            12.5,
            12,
            11.8,
            14.2,
            14.9,
            14.5,
            21,
            8.2,
            10.3,
            11.3,
            14.1,
            9.9,
            12.2,
            12,
            12.1,
            11,
            19.8,
            11,
            10,
            8.8,
            9,
            12.3 };

    public UnivariateStatisticAbstractTest(String name) {
        super(name);
    }

    public abstract UnivariateStatistic getUnivariateStatistic();

    public abstract double expectedValue();

    public double getTolerance() {
        return tolerance;
    }

    public void testEvaluation() throws Exception {
        assertEquals(
            expectedValue(),
            getUnivariateStatistic().evaluate(testArray),
            getTolerance());
    }
    
}
