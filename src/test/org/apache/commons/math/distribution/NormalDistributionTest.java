/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
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

import junit.framework.TestCase;

/**
 *  Tests for NormalDistribution implementation
 * 
 * "True" results are taken from R - the same as in Mathematica
 *
 */
public class NormalDistributionTest extends TestCase {
	
	private NormalDistribution z;
	private static final double PRECISION = 10e-6;	
	private static final double M = 2.1;
	private static final double SD = 1.4;
	
	/**
	 * Constructor for NormalDistributionTest.
	 * @param arg0
	 */
	public NormalDistributionTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(NormalDistributionTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		z = DistributionFactory.newInstance().createNormalDistribution(M, SD);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		z = null;
	}

	public void testCumulativeProbabilitydoubleM_MINUS_2SD() throws MathException {
		testProbability(M - 2*SD, 0.02275013);
	}

	public void testCumulativeProbabilitydoubleM_MINUS_SD() throws MathException {
		testProbability(M - SD, 0.1586553);
	}

	public void testCumulativeProbabilitydoubleM() throws MathException {
		testProbability(M, 0.5);
	}

	public void testCumulativeProbabilitydoubleM_PLUS_SD() throws MathException {
		testProbability(M + SD, 0.8413447);
	}
	
	public void testCumulativeProbabilitydoubleM_PLUS_2SD() throws MathException {
		testProbability(M + 2*SD, 0.9772499);
	}
	
	public void testCumulativeProbabilitydoubleM_PLUS_3SD() throws MathException {
		testProbability(M + 3*SD, 0.9986501);
	}
	
	public void testCumulativeProbabilitydoubleM_PLUS_4SD() throws MathException {
		testProbability(M + 4*SD, 0.9999683);
	}
	
	public void testCumulativeProbabilitydoubleM_PLUS_5SD() throws MathException {
		testProbability(M + 5*SD, 0.9999997);
	}
	
	public void testInverseCumulativeProbability0() throws MathException {
		assertEquals(Double.isNaN(z.inverseCumulativeProbability(0.0)), true);
	}

	public void testInverseCumulativeProbability001() throws MathException {
		testValue(-2.226325, .001);
	}

	public void testInverseCumulativeProbability010() throws MathException{
		testValue(-1.156887, .010);
	}

	public void testInverseCumulativeProbability025() throws MathException{
		testValue(-0.6439496, .025);
	}

	public void testInverseCumulativeProbability050() throws MathException{
		testValue(-0.2027951, .050);
	}

	public void testInverseCumulativeProbability100() throws MathException{
		testValue(0.3058278, .100);
	}

	public void testInverseCumulativeProbability900() throws MathException{
		testValue(3.894172, .900);
	}

	public void testInverseCumulativeProbability950() throws MathException{
		testValue(4.402795, .950);
	}

	public void testInverseCumulativeProbability975() throws MathException{
		testValue(4.84395, .975);
	}

	public void testInverseCumulativeProbability990() throws MathException{
		testValue(5.356887, .990);
	}

	public void testInverseCumulativeProbability999() throws MathException{
		testValue(6.426325, .999);
	}

	public void testInverseCumulativeProbability1() throws MathException {
		assertEquals(Double.isNaN(z.inverseCumulativeProbability(1.0)), true);
	}

	public void testGetMean() {
		assertEquals(M, z.getMean(), 0);
	}

	public void testSetMean() throws MathException {
		double mu = Math.random();
		z.setMean(mu);
		assertEquals(mu, z.getMean(), 0);
		assertEquals(0.5d, z.cumulativeProbability(mu), PRECISION);
	}

	public void testGetStandardDeviation() {
		assertEquals(SD, z.getStandardDeviation(), 0);	
	}

	public void testSetStandardDeviation() throws MathException{
		double sigma = 0.1d + Math.random();
		z.setStandardDeviation(sigma);
		assertEquals(sigma, z.getStandardDeviation(), 0);
		assertEquals(0.84134475, z.cumulativeProbability(z.getMean() + z.getStandardDeviation()), PRECISION );
	}

	public void testGetCdfAlgorithm() {
		assertTrue(z.getCdfAlgorithm() != null);
	}

	public void testSetCdfAlgorithm() {
		z.setCdfAlgorithm(new NormalCDFFastAlgorithm());
		assertTrue(z.getCdfAlgorithm() instanceof NormalCDFFastAlgorithm);
	}
	
	private void testProbability(double x, double expected) throws MathException {
		double actual = Double.NaN;
		z.setCdfAlgorithm(new NormalCDFPreciseAlgorithm());
		actual =  z.cumulativeProbability(x);
		assertEquals(expected, actual, PRECISION);
		z.setCdfAlgorithm(new NormalCDFFastAlgorithm());
		actual =  z.cumulativeProbability(x);
		assertEquals(expected, actual, PRECISION);
	}

	private void testValue(double expected, double p) throws MathException {
		double actual = z.inverseCumulativeProbability(p);
		assertEquals(expected, actual, PRECISION);
	}

}
