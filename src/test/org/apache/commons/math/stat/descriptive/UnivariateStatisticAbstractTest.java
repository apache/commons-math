/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.descriptive;

import junit.framework.TestCase;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Revision$ $Date$
 */
public abstract class UnivariateStatisticAbstractTest extends TestCase {

    protected double mean = 12.404545454545455d;
    protected double geoMean = 12.070589161633011d;

    protected double var = 10.00235930735931d;
    protected double std = Math.sqrt(var);
    protected double skew = 1.437423729196190d;
    protected double kurt = 2.377191264804700d;

    protected double min = 8.2d;
    protected double max = 21d;
    protected double median = 12d;
    protected double percentile5 = 8.29d;
    protected double percentile95 = 20.82d;

    protected double product = 628096400563833396009676.9200400128d;
    protected double sumLog = 54.7969806116451507d;
    protected double sumSq = 3595.250d;
    protected double sum = 272.90d;
    protected double secondMoment = 210.04954545454547d;
    protected double thirdMoment = 868.0906859504136;
    protected double fourthMoment = 9244.080993773481;

    protected double tolerance = 10E-12;

    protected double[] testArray =
        {12.5, 12, 11.8, 14.2, 14.9, 14.5, 21, 8.2, 10.3, 11.3,
          14.1, 9.9, 12.2, 12, 12.1, 11, 19.8, 11, 10,  8.8,
           9, 12.3 };

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
    
    public void testCopy() throws Exception {
        UnivariateStatistic original = getUnivariateStatistic();
        UnivariateStatistic copy = original.copy();
        assertEquals(
                expectedValue(),
                copy.evaluate(testArray),
                getTolerance());
    }
    
}
