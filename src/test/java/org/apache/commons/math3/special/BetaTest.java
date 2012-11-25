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
package org.apache.commons.math3.special;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.FastMath;

import org.junit.Assert;
import org.junit.Test;

/**
 * @version $Id$
 */
public class BetaTest {
    private void testRegularizedBeta(double expected, double x,
                                     double a, double b) {
        double actual = Beta.regularizedBeta(x, a, b);
        TestUtils.assertEquals(expected, actual, 10e-15);
    }

    private void testLogBeta(double expected, double a, double b) {
        double actual = Beta.logBeta(a, b);
        TestUtils.assertEquals(expected, actual, 10e-15);
    }

    @Test
    public void testRegularizedBetaNanPositivePositive() {
        testRegularizedBeta(Double.NaN, Double.NaN, 1.0, 1.0);
    }

    @Test
    public void testRegularizedBetaPositiveNanPositive() {
        testRegularizedBeta(Double.NaN, 0.5, Double.NaN, 1.0);
    }

    @Test
    public void testRegularizedBetaPositivePositiveNan() {
        testRegularizedBeta(Double.NaN, 0.5, 1.0, Double.NaN);
    }

    @Test
    public void testRegularizedBetaNegativePositivePositive() {
        testRegularizedBeta(Double.NaN, -0.5, 1.0, 2.0);
    }

    @Test
    public void testRegularizedBetaPositiveNegativePositive() {
        testRegularizedBeta(Double.NaN, 0.5, -1.0, 2.0);
    }

    @Test
    public void testRegularizedBetaPositivePositiveNegative() {
        testRegularizedBeta(Double.NaN, 0.5, 1.0, -2.0);
    }

    @Test
    public void testRegularizedBetaZeroPositivePositive() {
        testRegularizedBeta(0.0, 0.0, 1.0, 2.0);
    }

    @Test
    public void testRegularizedBetaPositiveZeroPositive() {
        testRegularizedBeta(Double.NaN, 0.5, 0.0, 2.0);
    }

    @Test
    public void testRegularizedBetaPositivePositiveZero() {
        testRegularizedBeta(Double.NaN, 0.5, 1.0, 0.0);
    }

    @Test
    public void testRegularizedBetaPositivePositivePositive() {
        testRegularizedBeta(0.75, 0.5, 1.0, 2.0);
    }

    @Test
    public void testLogBetaNanPositive() {
        testLogBeta(Double.NaN, Double.NaN, 2.0);
    }

    @Test
    public void testLogBetaPositiveNan() {
        testLogBeta(Double.NaN, 1.0, Double.NaN);
    }

    @Test
    public void testLogBetaNegativePositive() {
        testLogBeta(Double.NaN, -1.0, 2.0);
    }

    @Test
    public void testLogBetaPositiveNegative() {
        testLogBeta(Double.NaN, 1.0, -2.0);
    }

    @Test
    public void testLogBetaZeroPositive() {
        testLogBeta(Double.NaN, 0.0, 2.0);
    }

    @Test
    public void testLogBetaPositiveZero() {
        testLogBeta(Double.NaN, 1.0, 0.0);
    }

    @Test
    public void testLogBetaPositivePositive() {
        testLogBeta(-0.693147180559945, 1.0, 2.0);
    }

    private static final double[][] BCORR_REF = {
        { 10.0 , 10.0 , .01249480717472882 },
        { 10.0 , 11.0 , .01193628470267385 },
        { 10.0 , 12.0 , .01148578547212797 },
        { 10.0 , 13.0 , .01111659739668398 },
        { 10.0 , 14.0 , .01080991216314295 },
        { 10.0 , 15.0 , .01055214134859758 },
        { 10.0 , 16.0 , .01033324912491747 },
        { 10.0 , 17.0 , .01014568069918883 },
        { 10.0 , 18.0 , .009983653199146491 },
        { 10.0 , 19.0 , .009842674320242729 },
        { 10.0 , 20.0 , 0.0097192081956071 },
        { 11.0 , 10.0 , .01193628470267385 },
        { 11.0 , 11.0 , .01135973290745925 },
        { 11.0 , 12.0 , .01089355537047828 },
        { 11.0 , 13.0 , .01051064829297728 },
        { 11.0 , 14.0 , 0.0101918899639826 },
        { 11.0 , 15.0 , .009923438811859604 },
        { 11.0 , 16.0 , .009695052724952705 },
        { 11.0 , 17.0 , 0.00949900745283617 },
        { 11.0 , 18.0 , .009329379874933402 },
        { 11.0 , 19.0 , 0.00918156080743147 },
        { 11.0 , 20.0 , 0.00905191635141762 },
        { 12.0 , 10.0 , .01148578547212797 },
        { 12.0 , 11.0 , .01089355537047828 },
        { 12.0 , 12.0 , .01041365883144029 },
        { 12.0 , 13.0 , .01001867865848564 },
        { 12.0 , 14.0 , 0.00968923999191334 },
        { 12.0 , 15.0 , .009411294976563555 },
        { 12.0 , 16.0 , .009174432043268762 },
        { 12.0 , 17.0 , .008970786693291802 },
        { 12.0 , 18.0 , .008794318926790865 },
        { 12.0 , 19.0 , .008640321527910711 },
        { 12.0 , 20.0 , .008505077879954796 },
        { 13.0 , 10.0 , .01111659739668398 },
        { 13.0 , 11.0 , .01051064829297728 },
        { 13.0 , 12.0 , .01001867865848564 },
        { 13.0 , 13.0 , .009613018147953376 },
        { 13.0 , 14.0 , .009274085618154277 },
        { 13.0 , 15.0 , 0.0089876637564166 },
        { 13.0 , 16.0 , .008743200745261382 },
        { 13.0 , 17.0 , .008532715206686251 },
        { 13.0 , 18.0 , .008350069108807093 },
        { 13.0 , 19.0 , .008190472517984874 },
        { 13.0 , 20.0 , .008050138630244345 },
        { 14.0 , 10.0 , .01080991216314295 },
        { 14.0 , 11.0 , 0.0101918899639826 },
        { 14.0 , 12.0 , 0.00968923999191334 },
        { 14.0 , 13.0 , .009274085618154277 },
        { 14.0 , 14.0 , .008926676241967286 },
        { 14.0 , 15.0 , .008632654302369184 },
        { 14.0 , 16.0 , .008381351102615795 },
        { 14.0 , 17.0 , .008164687232662443 },
        { 14.0 , 18.0 , .007976441942841219 },
        { 14.0 , 19.0 , .007811755112234388 },
        { 14.0 , 20.0 , .007666780069317652 },
        { 15.0 , 10.0 , .01055214134859758 },
        { 15.0 , 11.0 , .009923438811859604 },
        { 15.0 , 12.0 , .009411294976563555 },
        { 15.0 , 13.0 , 0.0089876637564166 },
        { 15.0 , 14.0 , .008632654302369184 },
        { 15.0 , 15.0 , 0.00833179217417291 },
        { 15.0 , 16.0 , .008074310643041299 },
        { 15.0 , 17.0 , .007852047581145882 },
        { 15.0 , 18.0 , .007658712051540045 },
        { 15.0 , 19.0 , .007489384065757007 },
        { 15.0 , 20.0 , .007340165635725612 },
        { 16.0 , 10.0 , .01033324912491747 },
        { 16.0 , 11.0 , .009695052724952705 },
        { 16.0 , 12.0 , .009174432043268762 },
        { 16.0 , 13.0 , .008743200745261382 },
        { 16.0 , 14.0 , .008381351102615795 },
        { 16.0 , 15.0 , .008074310643041299 },
        { 16.0 , 16.0 , .007811229919967624 },
        { 16.0 , 17.0 , .007583876618287594 },
        { 16.0 , 18.0 , .007385899933505551 },
        { 16.0 , 19.0 , .007212328560607852 },
        { 16.0 , 20.0 , .007059220321091879 },
        { 17.0 , 10.0 , .01014568069918883 },
        { 17.0 , 11.0 , 0.00949900745283617 },
        { 17.0 , 12.0 , .008970786693291802 },
        { 17.0 , 13.0 , .008532715206686251 },
        { 17.0 , 14.0 , .008164687232662443 },
        { 17.0 , 15.0 , .007852047581145882 },
        { 17.0 , 16.0 , .007583876618287594 },
        { 17.0 , 17.0 , .007351882161431358 },
        { 17.0 , 18.0 , .007149662089534654 },
        { 17.0 , 19.0 , .006972200907152378 },
        { 17.0 , 20.0 , .006815518216094137 },
        { 18.0 , 10.0 , .009983653199146491 },
        { 18.0 , 11.0 , .009329379874933402 },
        { 18.0 , 12.0 , .008794318926790865 },
        { 18.0 , 13.0 , .008350069108807093 },
        { 18.0 , 14.0 , .007976441942841219 },
        { 18.0 , 15.0 , .007658712051540045 },
        { 18.0 , 16.0 , .007385899933505551 },
        { 18.0 , 17.0 , .007149662089534654 },
        { 18.0 , 18.0 , .006943552208153373 },
        { 18.0 , 19.0 , .006762516574228829 },
        { 18.0 , 20.0 , .006602541598043117 },
        { 19.0 , 10.0 , .009842674320242729 },
        { 19.0 , 11.0 , 0.00918156080743147 },
        { 19.0 , 12.0 , .008640321527910711 },
        { 19.0 , 13.0 , .008190472517984874 },
        { 19.0 , 14.0 , .007811755112234388 },
        { 19.0 , 15.0 , .007489384065757007 },
        { 19.0 , 16.0 , .007212328560607852 },
        { 19.0 , 17.0 , .006972200907152378 },
        { 19.0 , 18.0 , .006762516574228829 },
        { 19.0 , 19.0 , .006578188655176814 },
        { 19.0 , 20.0 , .006415174623476747 },
        { 20.0 , 10.0 , 0.0097192081956071 },
        { 20.0 , 11.0 , 0.00905191635141762 },
        { 20.0 , 12.0 , .008505077879954796 },
        { 20.0 , 13.0 , .008050138630244345 },
        { 20.0 , 14.0 , .007666780069317652 },
        { 20.0 , 15.0 , .007340165635725612 },
        { 20.0 , 16.0 , .007059220321091879 },
        { 20.0 , 17.0 , .006815518216094137 },
        { 20.0 , 18.0 , .006602541598043117 },
        { 20.0 , 19.0 , .006415174623476747 },
        { 20.0 , 20.0 , .006249349445691423 },
    };

    @Test
    public void testBcorr() {

        final int ulps = 3;
        for (int i = 0; i < BCORR_REF.length; i++) {
            final double[] ref = BCORR_REF[i];
            final double a = ref[0];
            final double b = ref[1];
            final double expected = ref[2];
            final double actual = Beta.bcorr(a, b);
            final double tol = ulps * FastMath.ulp(expected);
            final StringBuilder builder = new StringBuilder();
            builder.append(a).append(", ").append(b);
            Assert.assertEquals(builder.toString(), expected, actual, tol);
        }
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testBcorrPrecondition1() {

        Beta.bcorr(9.0, 10.0);
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testBcorrPrecondition2() {

        Beta.bcorr(10.0, 9.0);
    }
}
