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

package org.apache.commons.math.stat.distribution;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.10 $ $Date: 2003/10/13 08:08:38 $
 */
public class DistributionFactoryImplTest extends TestCase {
    /** */
    private DistributionFactory factory;
    
	/**
	 * Constructor for DistributionFactoryImplTest.
	 * @param name
	 */
	public DistributionFactoryImplTest(String name) {
		super(name);
	}
	/**
	 * @throws java.lang.Exception
	 */
	protected void setUp() throws Exception {
		super.setUp();
        factory = new DistributionFactoryImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected void tearDown() throws Exception {
        factory = null;
		super.tearDown();
	}
    
    public void testCreateChiSquareDistributionNegative(){
        try {
            factory.createChiSquareDistribution(-1.0);
            fail("negative degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateChiSquareDistributionZero(){
        try {
            factory.createChiSquareDistribution(0.0);
            fail("zero degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateChiSquareDistributionPositive(){
        try {
            factory.createChiSquareDistribution(1.0);
        } catch (IllegalArgumentException ex) {
            fail("positive degrees of freedom.  IllegalArgumentException is not expected");
        }
    }
    
    public void testCreateFDistributionNegativePositive(){
        try {
            factory.createFDistribution(-1.0, 1.0);
            fail("negative degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateFDistributionZeroPositive(){
        try {
            factory.createFDistribution(0.0, 1.0);
            fail("zero degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateFDistributionPositiveNegative(){
        try {
            factory.createFDistribution(1.0, -1.0);
            fail("negative degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateFDistributionPositiveZero(){
        try {
            factory.createFDistribution(1.0, 0.0);
            fail("zero degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateFDistributionPositivePositive(){
        try {
            factory.createFDistribution(1.0, 1.0);
        } catch (IllegalArgumentException ex) {
            fail("positive degrees of freedom.  IllegalArgumentException is not expected");
        }
    }
    
    public void testCreateExponentialDistributionNegative(){
        try {
            factory.createExponentialDistribution(-1.0);
            fail("negative mean.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateExponentialDistributionZero(){
        try {
            factory.createExponentialDistribution(0.0);
            fail("zero mean.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateExponentialDistributionPositive(){
        try {
            factory.createExponentialDistribution(1.0);
        } catch (IllegalArgumentException ex) {
            fail("positive mean.  IllegalArgumentException is not expected");
        }
    }
    
    public void testCreateGammaDistributionNegativePositive(){
        try {
            factory.createGammaDistribution(-1.0, 1.0);
            fail("negative alpha.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateGammaDistributionZeroPositive(){
        try {
            factory.createGammaDistribution(0.0, 1.0);
            fail("zero alpha.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateGammaDistributionPositiveNegative(){
        try {
            factory.createGammaDistribution(1.0, -1.0);
            fail("negative beta.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateGammaDistributionPositiveZero(){
        try {
            factory.createGammaDistribution(1.0, 0.0);
            fail("zero beta.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateGammaDistributionPositivePositive(){
        try {
            factory.createGammaDistribution(1.0, 1.0);
        } catch (IllegalArgumentException ex) {
            fail("positive alpah and beta.  IllegalArgumentException is not expected");
        }
    }
    
    public void testCreateTDistributionNegative(){
        try {
            factory.createTDistribution(-1.0);
            fail("negative degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateTDistributionZero(){
        try {
            factory.createTDistribution(0.0);
            fail("zero degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateTDistributionPositive(){
        try {
            factory.createTDistribution(1.0);
        } catch (IllegalArgumentException ex) {
            fail("positive degrees of freedom.  IllegalArgumentException is not expected");
        }
    }
    
    public void testBinomialDistributionNegativePositive(){
        try {
            factory.createBinomialDistribution(-1, 0.5);
            fail("negative number of trials.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex ) {
        }
    }
    
    public void testBinomialDistributionZeroPositive(){
        try {
            factory.createBinomialDistribution(0, 0.5);
        } catch (IllegalArgumentException ex ) {
            fail("zero number of trials.  IllegalArgumentException is not expected");
        }
    }
    
    public void testBinomialDistributionPositivePositive(){
        try {
            factory.createBinomialDistribution(10, 0.5);
        } catch (IllegalArgumentException ex ) {
            fail("positive number of trials.  IllegalArgumentException is not expected");
        }
    }
    
    public void testBinomialDistributionPositiveNegative(){
        try {
            factory.createBinomialDistribution(10, -0.5);
            fail("negative probability of success.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex ) {
        }
    }
    
    public void testBinomialDistributionPositiveZero(){
        try {
            factory.createBinomialDistribution(10, 0.0);
        } catch (IllegalArgumentException ex ) {
            fail("zero probability of success.  IllegalArgumentException is not expected");
        }
    }
    
    public void testBinomialDistributionPositiveOne(){
        try {
            factory.createBinomialDistribution(10, 1.0);
        } catch (IllegalArgumentException ex ) {
            fail("valid probability of success.  IllegalArgumentException is not expected");
        }
    }
    
    public void testBinomialDistributionPositiveTwo(){
        try {
            factory.createBinomialDistribution(10, 2.0);
            fail("high probability of success.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex ) {
        }
    }
    
    public void testHypergeometricDistributionNegativePositivePositive(){
        try {
            factory.createHypergeometricDistribution(-1, 10, 10);
            fail("negative population size.  IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testHypergeometricDistributionZeroPositivePositive(){
        try {
            factory.createHypergeometricDistribution(0, 10, 10);
            fail("zero population size.  IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testHypergeometricDistributionPositiveNegativePositive(){
        try {
            factory.createHypergeometricDistribution(20, -1, 10);
            fail("negative number of successes.  IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testHypergeometricDistributionPositiveZeroPositive(){
        try {
            factory.createHypergeometricDistribution(20, 0, 10);
        } catch(IllegalArgumentException ex) {
            fail("valid number of successes.  IllegalArgumentException is not expected");
        }
    }
    
    public void testHypergeometricDistributionPositivePositiveNegative(){
        try {
            factory.createHypergeometricDistribution(20, 10, -1);
            fail("negative sample size.  IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testHypergeometricDistributionPositivePositiveZero(){
        try {
            factory.createHypergeometricDistribution(20, 10, 0);
        } catch(IllegalArgumentException ex) {
            fail("valid sample size.  IllegalArgumentException is not expected");
        }
    }
}
