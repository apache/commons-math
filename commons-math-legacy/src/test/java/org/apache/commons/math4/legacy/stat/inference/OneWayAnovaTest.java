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
package org.apache.commons.math4.legacy.stat.inference;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.stat.descriptive.SummaryStatistics;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for the OneWayAnovaImpl class.
 *
 */

public class OneWayAnovaTest {

    protected OneWayAnova testStatistic = new OneWayAnova();

    private double[] emptyArray = {};

    private double[] classA =
            {93.0, 103.0, 95.0, 101.0, 91.0, 105.0, 96.0, 94.0, 101.0 };
    private double[] classB =
            {99.0, 92.0, 102.0, 100.0, 102.0, 89.0 };
    private double[] classC =
            {110.0, 115.0, 111.0, 117.0, 128.0, 117.0 };

    @Test
    public void testAnovaFValue() {
        // Target comparison values computed using R version 2.6.0 (Linux version)
        List<double[]> threeClasses = new ArrayList<>();
        threeClasses.add(classA);
        threeClasses.add(classB);
        threeClasses.add(classC);

        Assert.assertEquals("ANOVA F-value",  24.67361709460624,
                 testStatistic.anovaFValue(threeClasses), 1E-12);

        List<double[]> twoClasses = new ArrayList<>();
        twoClasses.add(classA);
        twoClasses.add(classB);

        Assert.assertEquals("ANOVA F-value",  0.0150579150579,
                 testStatistic.anovaFValue(twoClasses), 1E-12);

        List<double[]> emptyContents = new ArrayList<>();
        emptyContents.add(emptyArray);
        emptyContents.add(classC);
        try {
            testStatistic.anovaFValue(emptyContents);
            Assert.fail("empty array for key classX, MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }

        List<double[]> tooFew = new ArrayList<>();
        tooFew.add(classA);
        try {
            testStatistic.anovaFValue(tooFew);
            Assert.fail("less than two classes, MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
    }


    @Test
    public void testAnovaPValue() {
        // Target comparison values computed using R version 2.6.0 (Linux version)
        List<double[]> threeClasses = new ArrayList<>();
        threeClasses.add(classA);
        threeClasses.add(classB);
        threeClasses.add(classC);

        Assert.assertEquals("ANOVA P-value", 6.959446E-06,
                 testStatistic.anovaPValue(threeClasses), 1E-12);

        List<double[]> twoClasses = new ArrayList<>();
        twoClasses.add(classA);
        twoClasses.add(classB);

        Assert.assertEquals("ANOVA P-value",  0.904212960464,
                 testStatistic.anovaPValue(twoClasses), 1E-12);
    }

    @Test
    public void testAnovaPValueSummaryStatistics() {
        // Target comparison values computed using R version 2.6.0 (Linux version)
        List<SummaryStatistics> threeClasses = new ArrayList<>();
        SummaryStatistics statsA = new SummaryStatistics();
        for (double a : classA) {
            statsA.addValue(a);
        }
        threeClasses.add(statsA);
        SummaryStatistics statsB = new SummaryStatistics();
        for (double b : classB) {
            statsB.addValue(b);
        }
        threeClasses.add(statsB);
        SummaryStatistics statsC = new SummaryStatistics();
        for (double c : classC) {
            statsC.addValue(c);
        }
        threeClasses.add(statsC);

        Assert.assertEquals("ANOVA P-value", 6.959446E-06,
                 testStatistic.anovaPValue(threeClasses, true), 1E-12);

        List<SummaryStatistics> twoClasses = new ArrayList<>();
        twoClasses.add(statsA);
        twoClasses.add(statsB);

        Assert.assertEquals("ANOVA P-value",  0.904212960464,
                 testStatistic.anovaPValue(twoClasses, false), 1E-12);
    }

    @Test
    public void testAnovaTest() {
        // Target comparison values computed using R version 2.3.1 (Linux version)
        List<double[]> threeClasses = new ArrayList<>();
        threeClasses.add(classA);
        threeClasses.add(classB);
        threeClasses.add(classC);

        Assert.assertTrue("ANOVA Test P<0.01", testStatistic.anovaTest(threeClasses, 0.01));

        List<double[]> twoClasses = new ArrayList<>();
        twoClasses.add(classA);
        twoClasses.add(classB);

        Assert.assertFalse("ANOVA Test P>0.01", testStatistic.anovaTest(twoClasses, 0.01));
    }
}
