/*
 * Copyright 2003-2004 The Apache Software Foundation.
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

package org.apache.commons.math.stat.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.stat.univariate.DescriptiveStatistics;
import org.apache.commons.math.stat.univariate.SummaryStatistics;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.2 $ $Date: 2004/04/12 02:27:50 $
 */
public abstract class CertifiedDataAbstractTest extends TestCase {
	
	private DescriptiveStatistics descriptives;
	
	private SummaryStatistics summaries;
	
	private Map certifiedValues;
	
	protected void setUp() throws Exception {
		descriptives = DescriptiveStatistics.newInstance();
		summaries = SummaryStatistics.newInstance();
		certifiedValues = new HashMap();
		
		loadData();
	}

	private void loadData() throws IOException {
		BufferedReader in = null;

		try {
			URL resourceURL = getClass().getClassLoader().getResource(getResourceName());
			in = new BufferedReader(new InputStreamReader(resourceURL.openStream()));
			
			String line = in.readLine();
			while (line != null) {
				line = StringUtils.trimToNull(line);
				if (line == null) {
					// empty line
				} else if (line.startsWith("#")) {
					// comment
				} else {
					int n = line.indexOf('=');
					if (n == -1) {
						// data value
						double value = Double.parseDouble(line);
						descriptives.addValue(value);
						summaries.addValue(value);
					} else {
						// certified value
						String name = line.substring(0, n).trim();
						String valueString = line.substring(n + 1).trim();
						Double value = new Double(valueString);
						certifiedValues.put(name, value);
					}
				}
				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * @return
	 */
	protected abstract String getResourceName();

	protected double getMaximumAbsoluteError() {
		return 1.0e-5;
	}
	
	protected void tearDown() throws Exception {
		descriptives.clear();
		descriptives = null;
		
		summaries.clear();
		summaries = null;
		
		certifiedValues.clear();
		certifiedValues = null;
	}
	
	public void testCertifiedValues() throws Exception {
		Iterator iter = certifiedValues.keySet().iterator();
		while (iter.hasNext()) {
			String name = iter.next().toString();
			Double expectedValue = (Double)certifiedValues.get(name);
			try {
				Double summariesValue = (Double)PropertyUtils.getProperty(summaries, name);
				TestUtils.assertEquals("summary value for " + name + " is incorrect.",
						summariesValue.doubleValue(), expectedValue.doubleValue(), getMaximumAbsoluteError());
			} catch (Exception ex) {
			}
			
			try {
				Double descriptivesValue = (Double)PropertyUtils.getProperty(descriptives, name);
				TestUtils.assertEquals("descriptive value for " + name + " is incorrect.",
						descriptivesValue.doubleValue(), expectedValue.doubleValue(), getMaximumAbsoluteError());
			} catch (Exception ex) {
			}
		}
 	}
}
