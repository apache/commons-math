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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test cases for {@link AggregateSummaryStatistics}
 *
 */
public class AggregateSummaryStatisticsTest extends TestCase {
    
    /**
     * Tests the standard aggregation behavior
     */
    public void testAggregation() {
        AggregateSummaryStatistics aggregate = new AggregateSummaryStatistics();
        SummaryStatistics setOneStats = aggregate.createContributingStatistics();
        SummaryStatistics setTwoStats = aggregate.createContributingStatistics();
        
        assertNotNull("The set one contributing stats are null", setOneStats);
        assertNotNull("The set two contributing stats are null", setTwoStats);
        assertNotSame("Contributing stats objects are the same", setOneStats, setTwoStats);
        
        setOneStats.addValue(2);
        setOneStats.addValue(3);
        setOneStats.addValue(5);
        setOneStats.addValue(7);
        setOneStats.addValue(11);
        assertEquals("Wrong number of set one values", 5, setOneStats.getN());
        assertEquals("Wrong sum of set one values", 28.0, setOneStats.getSum());
        
        setTwoStats.addValue(2);
        setTwoStats.addValue(4);
        setTwoStats.addValue(8);
        assertEquals("Wrong number of set two values", 3, setTwoStats.getN());
        assertEquals("Wrong sum of set two values", 14.0, setTwoStats.getSum());
        
        assertEquals("Wrong number of aggregate values", 8, aggregate.getN());
        assertEquals("Wrong aggregate sum", 42.0, aggregate.getSum());
    }

    /**
     * Creates and returns a {@code Test} representing all the test cases in this
     * class
     *
     * @return a {@code Test} representing all the test cases in this class
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(AggregateSummaryStatisticsTest.class);
        suite.setName("AggregateSummaryStatistics tests");
        return suite;
    }
    
}
