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
package org.apache.commons.math.stat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
/**
 * Test cases for the {@link DescriptiveStatistics} class.
 * @version $Revision: 1.13 $ $Date: 2004/01/25 21:30:41 $
 */
public class CertifiedDataTest extends TestCase  {

	protected double mean = Double.NaN;

	protected double std = Double.NaN;

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Certified Data Test Constructor
	 * @param name
	 */
	public CertifiedDataTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() {
	}

	/**
	 * @return The test suite
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite(CertifiedDataTest.class);
		suite.setName("Certified Tests");
		return suite;
	}

	/**
	 * Test StorelessDescriptiveStatistics
	*/
	public void testUnivariateImpl() {
		SummaryStatistics u = null;
		try {
			u = SummaryStatistics.newInstance(SummaryStatisticsImpl.class);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		loadStats("data/Lew.txt", u);
		assertEquals("Lew: std", std, u.getStandardDeviation(), .000000000001);
		assertEquals("Lew: mean", mean, u.getMean(), .000000000001);
		
		loadStats("data/Lottery.txt", u);
		assertEquals("Lottery: std", std, u.getStandardDeviation(), .000000000001);
		assertEquals("Lottery: mean", mean, u.getMean(), .000000000001);	
		
		loadStats("data/PiDigits.txt", u);
		assertEquals("PiDigits: std", std, u.getStandardDeviation(), .0000000000001);
		assertEquals("PiDigits: mean", mean, u.getMean(), .0000000000001);	

		loadStats("data/Mavro.txt", u);
		assertEquals("Mavro: std", std, u.getStandardDeviation(), .00000000000001);
		assertEquals("Mavro: mean", mean, u.getMean(), .00000000000001);
		
		//loadStats("data/Michelso.txt");
		//assertEquals("Michelso: std", std, u.getStandardDeviation(), .00000000000001);
		//assertEquals("Michelso: mean", mean, u.getMean(), .00000000000001);	
										
		loadStats("data/NumAcc1.txt", u);
		assertEquals("NumAcc1: std", std, u.getStandardDeviation(), .00000000000001);
		assertEquals("NumAcc1: mean", mean, u.getMean(), .00000000000001);
		
		//loadStats("data/NumAcc2.txt");
		//assertEquals("NumAcc2: std", std, u.getStandardDeviation(), .000000001);
		//assertEquals("NumAcc2: mean", mean, u.getMean(), .00000000000001);
	}

	/**
	 * Test StorelessDescriptiveStatistics
	 */
	public void testStoredUnivariateImpl() {

		DescriptiveStatistics u = DescriptiveStatistics.newInstance();
		
		loadStats("data/Lew.txt", u);
		assertEquals("Lew: std", std, u.getStandardDeviation(), .000000000001);
		assertEquals("Lew: mean", mean, u.getMean(), .000000000001);
		
		loadStats("data/Lottery.txt", u);
		assertEquals("Lottery: std", std, u.getStandardDeviation(), .000000000001);
		assertEquals("Lottery: mean", mean, u.getMean(), .000000000001);		
																  
		loadStats("data/PiDigits.txt", u);
		assertEquals("PiDigits: std", std, u.getStandardDeviation(), .0000000000001);
		assertEquals("PiDigits: mean", mean, u.getMean(), .0000000000001);
		
		loadStats("data/Mavro.txt", u);
		assertEquals("Mavro: std", std, u.getStandardDeviation(), .00000000000001);
		assertEquals("Mavro: mean", mean, u.getMean(), .00000000000001);		
		
		//loadStats("data/Michelso.txt");
		//assertEquals("Michelso: std", std, u.getStandardDeviation(), .00000000000001);
		//assertEquals("Michelso: mean", mean, u.getMean(), .00000000000001);	

		loadStats("data/NumAcc1.txt", u);
		assertEquals("NumAcc1: std", std, u.getStandardDeviation(), .00000000000001);
		assertEquals("NumAcc1: mean", mean, u.getMean(), .00000000000001);
		
		//loadStats("data/NumAcc2.txt");
		//assertEquals("NumAcc2: std", std, u.getStandardDeviation(), .000000001);
		//assertEquals("NumAcc2: mean", mean, u.getMean(), .00000000000001);
	}

	/**
	 * loads a DescriptiveStatistics off of a test file
	 * @param file
	 * @param statistical summary
	 */
	private void loadStats(String resource, Object u) {
		
		DescriptiveStatistics d = null;
		SummaryStatistics s = null;
		if (u instanceof DescriptiveStatistics) {
			d = (DescriptiveStatistics) u;
		} else {
			s = (SummaryStatistics) u;
		}
		try {
			u.getClass().getDeclaredMethod("clear", null).invoke(u, null);
			mean = Double.NaN;
			std = Double.NaN;

			BufferedReader in =
				new BufferedReader(
					new InputStreamReader(
						getClass().getResourceAsStream(resource)));

			String line = null;

			for (int j = 0; j < 60; j++) {
				line = in.readLine();
				if (j == 40) {
					mean =
						Double.parseDouble(
							line.substring(line.lastIndexOf(":") + 1).trim());
				}
				if (j == 41) {
					std =
						Double.parseDouble(
							line.substring(line.lastIndexOf(":") + 1).trim());
				}
			}

			line = in.readLine();

			while (line != null) {
				if (d != null) {
					d.addValue(Double.parseDouble(line.trim()));
				}  else {
					s.addValue(Double.parseDouble(line.trim()));
				}
				line = in.readLine();
			}

			in.close();

		} catch (FileNotFoundException fnfe) {
			log.error(fnfe.getMessage(), fnfe);
		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
		} catch (Exception ioe) {
			log.error(ioe.getMessage(), ioe);
		}
	}
}
