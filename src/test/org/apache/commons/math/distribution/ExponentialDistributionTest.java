/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2004 The Apache Software Foundation.  All rights
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
 *    nor may "Apache" appear in their name without prior written
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
package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;
import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.11 $ $Date: 2004/02/18 04:04:17 $
 */
public class ExponentialDistributionTest extends TestCase {
    private ExponentialDistribution exp;

    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public ExponentialDistributionTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        exp =
            DistributionFactory.newInstance().createExponentialDistribution(
                5.0);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        exp = null;
        super.tearDown();
    }

    public void testInverseCumulativeProbability001() {
        testValue(.005003, .001);
    }

    public void testInverseCumulativeProbability010() {
        testValue(0.050252, .010);
    }

    public void testInverseCumulativeProbability025() {
        testValue(0.126589, .025);
    }

    public void testInverseCumulativeProbability050() {
        testValue(0.256566, .050);
    }

    public void testInverseCumulativeProbability100() {
        testValue(0.526803, .100);
    }

    public void testInverseCumulativeProbability999() {
        testValue(34.5388, .999);
    }

    public void testInverseCumulativeProbability990() {
        testValue(23.0259, .990);
    }

    public void testInverseCumulativeProbability975() {
        testValue(18.4444, .975);
    }

    public void testInverseCumulativeProbability950() {
        testValue(14.9787, .950);
    }

    public void testInverseCumulativeProbability900() {
        testValue(11.5129, .900);
    }

    public void testCumulativeProbability001() {
        testProbability(0.005003, .001);
    }

    public void testCumulativeProbability010() {
        testProbability(0.050252, .010);
    }

    public void testCumulativeProbability025() {
        testProbability(0.126589, .025);
    }

    public void testCumulativeProbability050() {
        testProbability(0.256566, .050);
    }

    public void testCumulativeProbability100() {
        testProbability(0.526803, .100);
    }

    public void testCumulativeProbability999() {
        testProbability(34.5388, .999);
    }

    public void testCumulativeProbability990() {
        testProbability(23.0259, .990);
    }

    public void testCumulativeProbability975() {
        testProbability(18.4444, .975);
    }

    public void testCumulativeProbability950() {
        testProbability(14.9787, .950);
    }

    public void testCumulativeProbability900() {
        testProbability(11.5129, .900);
    }

    public void testCumulativeProbabilityNegative() {
        testProbability(-1.0, 0.0);
    }

    public void testCumulativeProbabilityZero() {
        testProbability(0.0, 0.0);
    }

    public void testInverseCumulativeProbabilityNegative() {
        testValue(Double.NaN, -1.0);
    }

    public void testInverseCumulativeProbabilityZero() {
        testValue(0.0, 0.0);
    }

    public void testInverseCumulativeProbabilityOne() {
        testValue(Double.POSITIVE_INFINITY, 1.0);
    }

    public void testInverseCumulativeProbabilityPositive() {
        testValue(Double.NaN, 2.0);
    }

    public void testCumulativeProbability2() {
        try {
            double actual = exp.cumulativeProbability(0.25, 0.75);
            assertEquals(0.0905214, actual, 10e-4);
        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void testProbability(double x, double expected) {
        try {
            double actual = exp.cumulativeProbability(x);
            TestUtils.assertEquals(expected, actual, 10e-4);
        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void testValue(double expected, double p) {
        try {
            double actual = exp.inverseCumulativeProbability(p);
            TestUtils.assertEquals(expected, actual, 10e-4);
        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
