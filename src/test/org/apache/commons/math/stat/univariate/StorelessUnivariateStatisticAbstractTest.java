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

import org.apache.commons.math.TestUtils;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Revision: 1.11 $ $Date: 2004/02/21 21:35:17 $
 */
public abstract class StorelessUnivariateStatisticAbstractTest
    extends UnivariateStatisticAbstractTest {

    public StorelessUnivariateStatisticAbstractTest(String name) {
        super(name);
    }

    public abstract UnivariateStatistic getUnivariateStatistic();

    public abstract double expectedValue();

    public void testIncrementation() throws Exception {

        StorelessUnivariateStatistic statistic =
            (StorelessUnivariateStatistic) getUnivariateStatistic();

        statistic.clear();

        for (int i = 0; i < testArray.length; i++) {
            statistic.increment(testArray[i]);
        }

        assertEquals(expectedValue(), statistic.getResult(), getTolerance());

        statistic.clear();

        assertTrue(Double.isNaN(statistic.getResult()));

    }

    public void testSerialization() throws Exception {

        StorelessUnivariateStatistic statistic =
            (StorelessUnivariateStatistic) getUnivariateStatistic();

        statistic.clear();

        for (int i = 0; i < testArray.length; i++) {
            statistic.increment(testArray[i]);
            if(i % 5 == 0)
                statistic = (StorelessUnivariateStatistic)TestUtils.serializeAndRecover(statistic); 
        }
        
        assertEquals(expectedValue(), statistic.getResult(), getTolerance());

        statistic.clear();

        assertTrue(Double.isNaN(statistic.getResult()));

    }

}
