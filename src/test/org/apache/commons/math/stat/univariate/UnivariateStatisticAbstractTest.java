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
package org.apache.commons.math.stat.univariate;

import junit.framework.TestCase;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Revision: 1.10 $ $Date: 2004/02/21 21:35:17 $
 */
public abstract class UnivariateStatisticAbstractTest extends TestCase {

    protected double mean = 12.40454545454550;
    protected double geoMean = 12.070589161633011;

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
