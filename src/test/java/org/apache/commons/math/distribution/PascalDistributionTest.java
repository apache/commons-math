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
package org.apache.commons.math.distribution;

/**
 * Test cases for PascalDistribution.
 * Extends IntegerDistributionAbstractTest.  See class javadoc for
 * IntegerDistributionAbstractTest for details.
 *
 * @version $Revision$ $Date$
 */
public class PascalDistributionTest extends IntegerDistributionAbstractTest {

    /**
     * Constructor for PascalDistributionTest.
     * @param name
     */
    public PascalDistributionTest(String name) {
        super(name);
    }

    //-------------- Implementations for abstract methods -----------------------

    /** Creates the default discrete distribution instance to use in tests. */
    @Override
    public IntegerDistribution makeDistribution() {
        return new PascalDistributionImpl(10,0.70);
    }

    /** Creates the default probability density test input values */
    @Override
    public int[] makeDensityTestPoints() {
      return new int[] {-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    }

    /** Creates the default probability density test expected values */
    @Override
    public double[] makeDensityTestValues() {
      return new double[] {0d, 0.02824d, 0.08474d, 0.13982d,
          0.16779d, 0.16359d, 0.1374d, 0.10306d, 0.070673d, 0.04505d, 0.02703d, 0.01540d, 0.0084};
    }

    /** Creates the default cumulative probability density test input values */
    @Override
    public int[] makeCumulativeTestPoints() {
      return makeDensityTestPoints();
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
      return new double[] {0d, 0.02824d, 0.11299d, 0.25281d, 0.42060d, 0.58420d,
          0.72162d, 0.82468d, 0.89535d, 0.94041d, 0.967446d, 0.98285, 0.99125d};
        }

    /** Creates the default inverse cumulative probability test input values */
    @Override
    public double[] makeInverseCumulativeTestPoints() {
      return new double[] {0, 0.001d, 0.010d, 0.025d, 0.050d, 0.100d, 0.999d,
          0.990d, 0.975d, 0.950d, 0.900d, 1};
        }

    /** Creates the default inverse cumulative probability density test expected values */
    @Override
    public int[] makeInverseCumulativeTestValues() {
      return new int[] {-1, -1, -1, -1, 0, 0, 13, 10, 9, 8, 7, Integer.MAX_VALUE};
    }

    //----------------- Additional test cases ---------------------------------

    /** Test degenerate case p = 0   */
    public void testDegenerate0() throws Exception {
        setDistribution(new PascalDistributionImpl(5,0.0d));
        setCumulativeTestPoints(new int[] {-1, 0, 1, 5, 10 });
        setCumulativeTestValues(new double[] {0d, 0d, 0d, 0d, 0d});
        setDensityTestPoints(new int[] {-1, 0, 1, 10, 11});
        setDensityTestValues(new double[] {0d, 0d, 0d, 0d, 0d});
        setInverseCumulativeTestPoints(new double[] {0.1d, 0.5d});
        setInverseCumulativeTestValues(new int[] {Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 1});
        verifyDensities();
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();
    }

    /** Test degenerate case p = 1   */
    public void testDegenerate1() throws Exception {
        setDistribution(new PascalDistributionImpl(5,1.0d));
        setCumulativeTestPoints(new int[] {-1, 0, 1, 2, 5, 10 });
        setCumulativeTestValues(new double[] {0d, 1d, 1d, 1d, 1d, 1d});
        setDensityTestPoints(new int[] {-1, 0, 1, 2, 5, 10});
        setDensityTestValues(new double[] {0d, 1d, 0d, 0d, 0d, 0d});
        setInverseCumulativeTestPoints(new double[] {0.1d, 0.5d});
        setInverseCumulativeTestValues(new int[] {-1, -1});
        verifyDensities();
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();
    }
}
