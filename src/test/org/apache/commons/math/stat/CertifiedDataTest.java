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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.logging.*;
/**
 * Test cases for the {@link Univariate} class.
 *
 * @author Mark R. Diggory
 * @version $Revision: 1.3 $ $Date: 2003/06/02 04:58:33 $
 */

public class CertifiedDataTest extends TestCase {

	protected double tolerance = .01;

	protected File[] dataFiles = null;

	protected Univariate u = null;

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
		URL url = getClass().getResource("data");
		File file = new File(url.getFile());

		dataFiles = file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".dat");
			}
		});

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
	 * Test UnivariateImpl
 	
	public void testUnivariateImpl() {

		for (int i = 0; i < dataFiles.length; i++) {

			u = new UnivariateImpl();

			loadStats(dataFiles[i]);

			assertEquals(
				dataFiles[i].getName() + ":std",
				std,
				u.getStandardDeviation(),
				tolerance);
				
			assertEquals(
				dataFiles[i].getName() + ":mean",
				mean,
				u.getMean(),
				tolerance);

		}
	}
	*/
	/**
	 * Test UnivariateImpl
	 */
	public void testStoredUnivariateImpl() {

		for (int i = 0; i < dataFiles.length; i++) {

			u = new StoreUnivariateImpl();

			loadStats(dataFiles[i]);

			assertEquals(
				dataFiles[i].getName() + ":std",
				std,
				u.getStandardDeviation(),
				tolerance);
			assertEquals(
				dataFiles[i].getName() + ":mean",
				mean,
				u.getMean(),
				tolerance);

		}
	}

	/**
	 * loads a Univariate off of a test file
	 * @param file
	 */
	private void loadStats(File file) {

		try {

			u.clear();
			mean = Double.NaN;
			std = Double.NaN;
			
			BufferedReader in = new BufferedReader(new FileReader(file));

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

				u.addValue(Double.parseDouble(line.trim()));
				line = in.readLine();
			}

			in.close();

		} catch (FileNotFoundException fnfe) {
			log.error(fnfe.getMessage(), fnfe);
		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
		}
	}
}
