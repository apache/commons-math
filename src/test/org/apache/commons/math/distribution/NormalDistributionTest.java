/*
 * Copyright 2004,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
