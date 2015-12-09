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
package org.apache.commons.math3.fitting;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link GaussianCurveFitter}.
 *
 */
public class GaussianCurveFitterTest {
    /** Good data. */
    protected static final double[][] DATASET1 = new double[][] {
        {4.0254623,  531026.0},
        {4.02804905, 664002.0},
        {4.02934242, 787079.0},
        {4.03128248, 984167.0},
        {4.03386923, 1294546.0},
        {4.03580929, 1560230.0},
        {4.03839603, 1887233.0},
        {4.0396894,  2113240.0},
        {4.04162946, 2375211.0},
        {4.04421621, 2687152.0},
        {4.04550958, 2862644.0},
        {4.04744964, 3078898.0},
        {4.05003639, 3327238.0},
        {4.05132976, 3461228.0},
        {4.05326982, 3580526.0},
        {4.05585657, 3576946.0},
        {4.05779662, 3439750.0},
        {4.06038337, 3220296.0},
        {4.06167674, 3070073.0},
        {4.0636168,  2877648.0},
        {4.06620355, 2595848.0},
        {4.06749692, 2390157.0},
        {4.06943698, 2175960.0},
        {4.07202373, 1895104.0},
        {4.0733171,  1687576.0},
        {4.07525716, 1447024.0},
        {4.0778439,  1130879.0},
        {4.07978396, 904900.0},
        {4.08237071, 717104.0},
        {4.08366408, 620014.0}
    };
    /** Poor data: right of peak not symmetric with left of peak. */
    protected static final double[][] DATASET2 = new double[][] {
        {-20.15,   1523.0},
        {-19.65,   1566.0},
        {-19.15,   1592.0},
        {-18.65,   1927.0},
        {-18.15,   3089.0},
        {-17.65,   6068.0},
        {-17.15,  14239.0},
        {-16.65,  34124.0},
        {-16.15,  64097.0},
        {-15.65, 110352.0},
        {-15.15, 164742.0},
        {-14.65, 209499.0},
        {-14.15, 267274.0},
        {-13.65, 283290.0},
        {-13.15, 275363.0},
        {-12.65, 258014.0},
        {-12.15, 225000.0},
        {-11.65, 200000.0},
        {-11.15, 190000.0},
        {-10.65, 185000.0},
        {-10.15, 180000.0},
        { -9.65, 179000.0},
        { -9.15, 178000.0},
        { -8.65, 177000.0},
        { -8.15, 176000.0},
        { -7.65, 175000.0},
        { -7.15, 174000.0},
        { -6.65, 173000.0},
        { -6.15, 172000.0},
        { -5.65, 171000.0},
        { -5.15, 170000.0}
    };
    /** Poor data: long tails. */
    protected static final double[][] DATASET3 = new double[][] {
        {-90.15,   1513.0},
        {-80.15,   1514.0},
        {-70.15,   1513.0},
        {-60.15,   1514.0},
        {-50.15,   1513.0},
        {-40.15,   1514.0},
        {-30.15,   1513.0},
        {-20.15,   1523.0},
        {-19.65,   1566.0},
        {-19.15,   1592.0},
        {-18.65,   1927.0},
        {-18.15,   3089.0},
        {-17.65,   6068.0},
        {-17.15,  14239.0},
        {-16.65,  34124.0},
        {-16.15,  64097.0},
        {-15.65, 110352.0},
        {-15.15, 164742.0},
        {-14.65, 209499.0},
        {-14.15, 267274.0},
        {-13.65, 283290.0},
        {-13.15, 275363.0},
        {-12.65, 258014.0},
        {-12.15, 214073.0},
        {-11.65, 182244.0},
        {-11.15, 136419.0},
        {-10.65,  97823.0},
        {-10.15,  58930.0},
        { -9.65,  35404.0},
        { -9.15,  16120.0},
        { -8.65,   9823.0},
        { -8.15,   5064.0},
        { -7.65,   2575.0},
        { -7.15,   1642.0},
        { -6.65,   1101.0},
        { -6.15,    812.0},
        { -5.65,    690.0},
        { -5.15,    565.0},
        {  5.15,    564.0},
        { 15.15,    565.0},
        { 25.15,    564.0},
        { 35.15,    565.0},
        { 45.15,    564.0},
        { 55.15,    565.0},
        { 65.15,    564.0},
        { 75.15,    565.0}
    };
    /** Poor data: right of peak is missing. */
    protected static final double[][] DATASET4 = new double[][] {
        {-20.15,   1523.0},
        {-19.65,   1566.0},
        {-19.15,   1592.0},
        {-18.65,   1927.0},
        {-18.15,   3089.0},
        {-17.65,   6068.0},
        {-17.15,  14239.0},
        {-16.65,  34124.0},
        {-16.15,  64097.0},
        {-15.65, 110352.0},
        {-15.15, 164742.0},
        {-14.65, 209499.0},
        {-14.15, 267274.0},
        {-13.65, 283290.0}
    };
    /** Good data, but few points. */
    protected static final double[][] DATASET5 = new double[][] {
        {4.0254623,  531026.0},
        {4.03128248, 984167.0},
        {4.03839603, 1887233.0},
        {4.04421621, 2687152.0},
        {4.05132976, 3461228.0},
        {4.05326982, 3580526.0},
        {4.05779662, 3439750.0},
        {4.0636168,  2877648.0},
        {4.06943698, 2175960.0},
        {4.07525716, 1447024.0},
        {4.08237071, 717104.0},
        {4.08366408, 620014.0}
    };

    /**
     * Basic.
     */
    @Test
    public void testFit01() {
        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        double[] parameters = fitter.fit(createDataset(DATASET1).toList());

        Assert.assertEquals(3496978.1837704973, parameters[0], 1e-4);
        Assert.assertEquals(4.054933085999146, parameters[1], 1e-4);
        Assert.assertEquals(0.015039355620304326, parameters[2], 1e-4);
    }

    @Test
    public void testWithMaxIterations1() {
        final int maxIter = 20;
        final double[] init = { 3.5e6, 4.2, 0.1 };

        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        double[] parameters = fitter
            .withMaxIterations(maxIter)
            .withStartPoint(init)
            .fit(createDataset(DATASET1).toList());

        Assert.assertEquals(3496978.1837704973, parameters[0], 1e-2);
        Assert.assertEquals(4.054933085999146, parameters[1], 1e-4);
        Assert.assertEquals(0.015039355620304326, parameters[2], 1e-4);
    }

    @Test(expected=TooManyIterationsException.class)
    public void testWithMaxIterations2() {
        final int maxIter = 1; // Too few iterations.
        final double[] init = { 3.5e6, 4.2, 0.1 };

        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        fitter.withMaxIterations(maxIter)
              .withStartPoint(init)
              .fit(createDataset(DATASET1).toList());
    }

    @Test
    public void testWithStartPoint() {
        final double[] init = { 3.5e6, 4.2, 0.1 };

        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        double[] parameters = fitter
            .withStartPoint(init)
            .fit(createDataset(DATASET1).toList());

        Assert.assertEquals(3496978.1837704973, parameters[0], 1e-2);
        Assert.assertEquals(4.054933085999146, parameters[1], 1e-4);
        Assert.assertEquals(0.015039355620304326, parameters[2], 1e-4);
    }

    /**
     * Zero points is not enough observed points.
     */
    @Test(expected=MathIllegalArgumentException.class)
    public void testFit02() {
        GaussianCurveFitter.create().fit(new WeightedObservedPoints().toList());
    }

    /**
     * Two points is not enough observed points.
     */
    @Test(expected=MathIllegalArgumentException.class)
    public void testFit03() {
        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        fitter.fit(createDataset(new double[][] {
                    {4.0254623,  531026.0},
                    {4.02804905, 664002.0}
                }).toList());
    }

    /**
     * Poor data: right of peak not symmetric with left of peak.
     */
    @Test
    public void testFit04() {
        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        double[] parameters = fitter.fit(createDataset(DATASET2).toList());

        Assert.assertEquals(233003.2967252038, parameters[0], 1e-4);
        Assert.assertEquals(-10.654887521095983, parameters[1], 1e-4);
        Assert.assertEquals(4.335937353196641, parameters[2], 1e-4);
    }

    /**
     * Poor data: long tails.
     */
    @Test
    public void testFit05() {
        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        double[] parameters = fitter.fit(createDataset(DATASET3).toList());

        Assert.assertEquals(283863.81929180305, parameters[0], 1e-4);
        Assert.assertEquals(-13.29641995105174, parameters[1], 1e-4);
        Assert.assertEquals(1.7297330293549908, parameters[2], 1e-4);
    }

    /**
     * Poor data: right of peak is missing.
     */
    @Test
    public void testFit06() {
        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        double[] parameters = fitter.fit(createDataset(DATASET4).toList());

        Assert.assertEquals(285250.66754309234, parameters[0], 1e-4);
        Assert.assertEquals(-13.528375695228455, parameters[1], 1e-4);
        Assert.assertEquals(1.5204344894331614, parameters[2], 1e-4);
    }

    /**
     * Basic with smaller dataset.
     */
    @Test
    public void testFit07() {
        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        double[] parameters = fitter.fit(createDataset(DATASET5).toList());

        Assert.assertEquals(3514384.729342235, parameters[0], 1e-4);
        Assert.assertEquals(4.054970307455625, parameters[1], 1e-4);
        Assert.assertEquals(0.015029412832160017, parameters[2], 1e-4);
    }

    @Test
    public void testMath519() {
        // The optimizer will try negative sigma values but "GaussianCurveFitter"
        // will catch the raised exceptions and return NaN values instead.

        final double[] data = {
            1.1143831578403364E-29,
            4.95281403484594E-28,
            1.1171347211930288E-26,
            1.7044813962636277E-25,
            1.9784716574832164E-24,
            1.8630236407866774E-23,
            1.4820532905097742E-22,
            1.0241963854632831E-21,
            6.275077366673128E-21,
            3.461808994532493E-20,
            1.7407124684715706E-19,
            8.056687953553974E-19,
            3.460193945992071E-18,
            1.3883326374011525E-17,
            5.233894983671116E-17,
            1.8630791465263745E-16,
            6.288759227922111E-16,
            2.0204433920597856E-15,
            6.198768938576155E-15,
            1.821419346860626E-14,
            5.139176445538471E-14,
            1.3956427429045787E-13,
            3.655705706448139E-13,
            9.253753324779779E-13,
            2.267636001476696E-12,
            5.3880460095836855E-12,
            1.2431632654852931E-11
        };

        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < data.length; i++) {
            obs.add(i, data[i]);
        }
        final double[] p = GaussianCurveFitter.create().fit(obs.toList());

        Assert.assertEquals(53.1572792, p[1], 1e-7);
        Assert.assertEquals(5.75214622, p[2], 1e-8);
    }

    @Test
    public void testMath798() {
        // When the data points are not commented out below, the fit stalls.
        // This is expected however, since the whole dataset hardly looks like
        // a Gaussian.
        // When commented out, the fit proceeds fine.

        final WeightedObservedPoints obs = new WeightedObservedPoints();

        obs.add(0.23, 395.0);
        //obs.add(0.68, 0.0);
        obs.add(1.14, 376.0);
        //obs.add(1.59, 0.0);
        obs.add(2.05, 163.0);
        //obs.add(2.50, 0.0);
        obs.add(2.95, 49.0);
        //obs.add(3.41, 0.0);
        obs.add(3.86, 16.0);
        //obs.add(4.32, 0.0);
        obs.add(4.77, 1.0);

        final double[] p = GaussianCurveFitter.create().fit(obs.toList());

        // Values are copied from a previous run of this test.
        Assert.assertEquals(420.8397296167364, p[0], 1e-12);
        Assert.assertEquals(0.603770729862231, p[1], 1e-15);
        Assert.assertEquals(1.0786447936766612, p[2], 1e-14);
    }

    /**
     * Adds the specified points to specified <code>GaussianCurveFitter</code>
     * instance.
     *
     * @param points Data points where first dimension is a point index and
     *        second dimension is an array of length two representing the point
     *        with the first value corresponding to X and the second value
     *        corresponding to Y.
     * @return the collection of observed points.
     */
    private static WeightedObservedPoints createDataset(double[][] points) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < points.length; i++) {
            obs.add(points[i][0], points[i][1]);
        }
        return obs;
    }
}
