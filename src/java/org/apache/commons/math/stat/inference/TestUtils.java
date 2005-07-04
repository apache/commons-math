/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.commons.math.stat.inference;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;

/**
 * A collection of static methods to create inference test instances or to
 * perform inference tests.
 *
 * @since 1.1
 * @version $Revision$ $Date$ 
 */
public class TestUtils  {
    /**
     * Prevent instantiation.
     */
    protected TestUtils() {
        super();
    }
    
    /** Singleton TTest instance initialized using configured factory */
    private static TTest tTest = TestFactory.newInstance().createTTest();
   
    /** Singleton ChiSquareTest instance initialized using configured factory */
    private static ChiSquareTest chiSquareTest = 
        TestFactory.newInstance().createChiSquareTest();
    
    /**
     * Return a (singleton) TTest instance.  Does not create a new instance.
     * 
     * @return a TTest instance
     */
    public static TTest getTTest() {
        return tTest;
    }
    
    /**
     * Return a (singleton) ChiSquareTest instance.  Does not create a new instance.
     * 
     * @return a ChiSquareTest instance
     */
    public static ChiSquareTest getChiSquareTest() {
        return chiSquareTest;
    }
    
    /**
     * @see org.apache.commons.math.stat.inference.TTest#homoscedasticT(double[], double[])
     */
    public static double homoscedasticT(double[] sample1, double[] sample2)
        throws IllegalArgumentException {
        return tTest.homoscedasticT(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#homoscedasticT(org.apache.commons.math.stat.descriptive.StatisticalSummary, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double homoscedasticT(StatisticalSummary sampleStats1,
        StatisticalSummary sampleStats2)
        throws IllegalArgumentException {
        return tTest.homoscedasticT(sampleStats1, sampleStats2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#homoscedasticTTest(double[], double[], double)
     */
    public static boolean homoscedasticTTest(double[] sample1, double[] sample2,
            double alpha)
        throws IllegalArgumentException, MathException {
        return tTest. homoscedasticTTest(sample1, sample2, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#homoscedasticTTest(double[], double[])
     */
    public static double homoscedasticTTest(double[] sample1, double[] sample2)
        throws IllegalArgumentException, MathException {
        return tTest.homoscedasticTTest(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#homoscedasticTTest(org.apache.commons.math.stat.descriptive.StatisticalSummary, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double homoscedasticTTest(StatisticalSummary sampleStats1,
        StatisticalSummary sampleStats2)
        throws IllegalArgumentException, MathException {
        return tTest.homoscedasticTTest(sampleStats1, sampleStats2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#pairedT(double[], double[])
     */
    public static double pairedT(double[] sample1, double[] sample2)
        throws IllegalArgumentException, MathException {
        return tTest.pairedT(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#pairedTTest(double[], double[], double)
     */
    public static boolean pairedTTest(double[] sample1, double[] sample2,
        double alpha)
        throws IllegalArgumentException, MathException {
        return tTest.pairedTTest(sample1, sample2, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#pairedTTest(double[], double[])
     */
    public static double pairedTTest(double[] sample1, double[] sample2)
        throws IllegalArgumentException, MathException {
        return tTest.pairedTTest(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#t(double, double[])
     */
    public static double t(double mu, double[] observed)
        throws IllegalArgumentException {
        return tTest.t(mu, observed);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#t(double, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double t(double mu, StatisticalSummary sampleStats)
        throws IllegalArgumentException {
        return tTest.t(mu, sampleStats);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#t(double[], double[])
     */
    public static double t(double[] sample1, double[] sample2)
        throws IllegalArgumentException {
        return tTest.t(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#t(org.apache.commons.math.stat.descriptive.StatisticalSummary, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double t(StatisticalSummary sampleStats1,
            StatisticalSummary sampleStats2)
        throws IllegalArgumentException {
        return tTest.t(sampleStats1, sampleStats2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double, double[], double)
     */
    public static boolean tTest(double mu, double[] sample, double alpha)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(mu, sample, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double, double[])
     */
    public static double tTest(double mu, double[] sample)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(mu, sample);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double, org.apache.commons.math.stat.descriptive.StatisticalSummary, double)
     */
    public static boolean tTest(double mu, StatisticalSummary sampleStats,
        double alpha)
        throws IllegalArgumentException, MathException {
        return tTest. tTest(mu, sampleStats, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double tTest(double mu, StatisticalSummary sampleStats)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(mu, sampleStats);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double[], double[], double)
     */
    public static boolean tTest(double[] sample1, double[] sample2, double alpha)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(sample1, sample2, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double[], double[])
     */
    public static double tTest(double[] sample1, double[] sample2)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(org.apache.commons.math.stat.descriptive.StatisticalSummary, org.apache.commons.math.stat.descriptive.StatisticalSummary, double)
     */
    public static boolean tTest(StatisticalSummary sampleStats1,
        StatisticalSummary sampleStats2, double alpha)
        throws IllegalArgumentException, MathException {
        return tTest. tTest(sampleStats1, sampleStats2, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(org.apache.commons.math.stat.descriptive.StatisticalSummary, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double tTest(StatisticalSummary sampleStats1,
        StatisticalSummary sampleStats2)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(sampleStats1, sampleStats2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquare(double[], long[])
     */
    public static double chiSquare(double[] expected, long[] observed)
        throws IllegalArgumentException {
        return chiSquareTest.chiSquare(expected, observed);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquare(long[][])
     */
    public static double chiSquare(long[][] counts) 
        throws IllegalArgumentException {
        return chiSquareTest.chiSquare(counts);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquareTest(double[], long[], double)
     */
    public static boolean chiSquareTest(double[] expected, long[] observed,
        double alpha)
        throws IllegalArgumentException, MathException {
        return chiSquareTest.chiSquareTest(expected, observed, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquareTest(double[], long[])
     */
    public static double chiSquareTest(double[] expected, long[] observed)
        throws IllegalArgumentException, MathException {
        return chiSquareTest.chiSquareTest(expected, observed);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquareTest(long[][], double)
     */
    public static boolean chiSquareTest(long[][] counts, double alpha)
        throws IllegalArgumentException, MathException {
        return chiSquareTest. chiSquareTest(counts, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquareTest(long[][])
     */
    public static double chiSquareTest(long[][] counts)
        throws IllegalArgumentException, MathException {
        return chiSquareTest. chiSquareTest(counts);
    }

}
