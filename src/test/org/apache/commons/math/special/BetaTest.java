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
package org.apache.commons.math.special;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.3 $ $Date: 2003/10/13 08:08:06 $
 */
public class BetaTest extends TestCase {
    /**
     * Constructor for BetaTest.
     * @param name
     */
    public BetaTest(String name) {
        super(name);
    }

    private void testRegularizedBeta(double expected, double x, double a, double b) {
        double actual = Beta.regularizedBeta(x, a, b);
        if (Double.isNaN(expected)) {
            assertTrue(Double.isNaN(actual));
        } else {
            assertEquals(expected, actual, 10e-5);
        }
    }

    private void testLogBeta(double expected, double a, double b) {
        double actual = Beta.logBeta(a, b);
        if (Double.isNaN(expected)) {
            assertTrue(Double.isNaN(actual));
        } else {
            assertEquals(expected, actual, 10e-5);
        }
    }

    public void testRegularizedBetaNanPositivePositive() {
        testRegularizedBeta(Double.NaN, Double.NaN, 1.0, 1.0);
    }

    public void testRegularizedBetaPositiveNanPositive() {
        testRegularizedBeta(Double.NaN, 0.5, Double.NaN, 1.0);
    }

    public void testRegularizedBetaPositivePositiveNan() {
        testRegularizedBeta(Double.NaN, 0.5, 1.0, Double.NaN);
    }
    
    public void testRegularizedBetaNegativePositivePositive() {
        testRegularizedBeta(Double.NaN, -0.5, 1.0, 2.0);
    }
    
    public void testRegularizedBetaPositiveNegativePositive() {
        testRegularizedBeta(Double.NaN, 0.5, -1.0, 2.0);
    }
    
    public void testRegularizedBetaPositivePositiveNegative() {
        testRegularizedBeta(Double.NaN, 0.5, 1.0, -2.0);
    }
    
    public void testRegularizedBetaZeroPositivePositive() {
        testRegularizedBeta(0.0, 0.0, 1.0, 2.0);
    }
    
    public void testRegularizedBetaPositiveZeroPositive() {
        testRegularizedBeta(Double.NaN, 0.5, 0.0, 2.0);
    }
    
    public void testRegularizedBetaPositivePositiveZero() {
        testRegularizedBeta(Double.NaN, 0.5, 1.0, 0.0);
    }
    
    public void testRegularizedBetaPositivePositivePositive() {
        testRegularizedBeta(0.75, 0.5, 1.0, 2.0);
    }
    
    public void testLogBetaNanPositive() {
        testLogBeta(Double.NaN, Double.NaN, 2.0);
    }
    
    public void testLogBetaPositiveNan() {
        testLogBeta(Double.NaN, 1.0, Double.NaN);
    }
    
    public void testLogBetaNegativePositive() {
        testLogBeta(Double.NaN, -1.0, 2.0);
    }
    
    public void testLogBetaPositiveNegative() {
        testLogBeta(Double.NaN, 1.0, -2.0);
    }
    
    public void testLogBetaZeroPositive() {
        testLogBeta(Double.NaN, 0.0, 2.0);
    }
    
    public void testLogBetaPositiveZero() {
        testLogBeta(Double.NaN, 1.0, 0.0);
    }
    
    public void testLogBetaPositivePositive() {
        testLogBeta(-0.693147, 1.0, 2.0);
    }
}