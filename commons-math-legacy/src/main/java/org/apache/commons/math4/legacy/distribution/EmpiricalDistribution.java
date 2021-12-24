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

package org.apache.commons.math4.legacy.distribution;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.numbers.core.Precision;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary;
import org.apache.commons.math4.legacy.stat.descriptive.SummaryStatistics;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * <p>Represents an <a href="http://en.wikipedia.org/wiki/Empirical_distribution_function">
 * empirical probability distribution</a>: Probability distribution derived
 * from observed data without making any assumptions about the functional
 * form of the population distribution that the data come from.</p>
 *
 * <p>An {@code EmpiricalDistribution} maintains data structures called
 * <i>distribution digests</i> that describe empirical distributions and
 * support the following operations:
 * <ul>
 *  <li>loading the distribution from "observed" data values</li>
 *  <li>dividing the input data into "bin ranges" and reporting bin
 *      frequency counts (data for histogram)</li>
 *  <li>reporting univariate statistics describing the full set of data
 *      values as well as the observations within each bin</li>
 *  <li>generating random values from the distribution</li>
 * </ul>
 *
 * Applications can use {@code EmpiricalDistribution} to build grouped
 * frequency histograms representing the input data or to generate random
 * values "like" those in the input, i.e. the values generated will follow
 * the distribution of the values in the file.
 *
 * <p>The implementation uses what amounts to the
 * <a href="http://nedwww.ipac.caltech.edu/level5/March02/Silverman/Silver2_6.html">
 * Variable Kernel Method</a> with Gaussian smoothing:<p>
 * <strong>Digesting the input file</strong>
 * <ol>
 *  <li>Pass the file once to compute min and max.</li>
 *  <li>Divide the range from min to max into {@code binCount} bins.</li>
 *  <li>Pass the data file again, computing bin counts and univariate
 *      statistics (mean and std dev.) for each bin.</li>
 *  <li>Divide the interval (0,1) into subintervals associated with the bins,
 *      with the length of a bin's subinterval proportional to its count.</li>
 * </ol>
 * <strong>Generating random values from the distribution</strong>
 * <ol>
 *  <li>Generate a uniformly distributed value in (0,1) </li>
 *  <li>Select the subinterval to which the value belongs.
 *  <li>Generate a random Gaussian value with mean = mean of the associated
 *      bin and std dev = std dev of associated bin.</li>
 * </ol>
 *
 * <p>EmpiricalDistribution implements the {@link ContinuousDistribution} interface
 * as follows.  Given x within the range of values in the dataset, let B
 * be the bin containing x and let K be the within-bin kernel for B.  Let P(B-)
 * be the sum of the probabilities of the bins below B and let K(B) be the
 * mass of B under K (i.e., the integral of the kernel density over B).  Then
 * set {@code P(X < x) = P(B-) + P(B) * K(x) / K(B)} where {@code K(x)} is the
 * kernel distribution evaluated at x. This results in a cdf that matches the
 * grouped frequency distribution at the bin endpoints and interpolates within
 * bins using within-bin kernels.</p>
 *
 * <strong>CAVEAT</strong>: It is advised that the {@link #from(int,double[])
 * bin count} is about one tenth of the size of the input array.
 */
public final class EmpiricalDistribution extends AbstractRealDistribution
    implements ContinuousDistribution {
    /** Bins characteristics. */
    private final List<SummaryStatistics> binStats;
    /** Sample statistics. */
    private final SummaryStatistics sampleStats;
    /** Max loaded value. */
    private final double max;
    /** Min loaded value. */
    private final double min;
    /** Grid size. */
    private final double delta;
    /** Number of bins. */
    private final int binCount;
    /** Upper bounds of subintervals in (0, 1) belonging to the bins. */
    private final double[] upperBounds;
    /** Kernel factory. */
    private final Function<SummaryStatistics, ContinuousDistribution> kernelFactory;

    /**
     * Creates a new instance with the specified data.
     *
     * @param binCount Number of bins.  Must be strictly positive.
     * @param input Input data.  Cannot be {@code null}.
     * @param kernelFactory Kernel factory.
     * @throws NotStrictlyPositiveException if {@code binCount <= 0}.
     */
    private EmpiricalDistribution(int binCount,
                                  double[] input,
                                  Function<SummaryStatistics, ContinuousDistribution> kernelFactory) {
        if (binCount <= 0) {
            throw new NotStrictlyPositiveException(binCount);
        }
        this.binCount = binCount;

        // First pass through the data.
        sampleStats = new SummaryStatistics();
        for (int i = 0; i < input.length; i++) {
            sampleStats.addValue(input[i]);
        }

        // Set up grid.
        min = sampleStats.getMin();
        max = sampleStats.getMax();
        delta = (max - min) / binCount;

        // Second pass through the data.
        binStats = createBinStats(input);

        // Assign upper bounds based on bin counts.
        upperBounds = new double[binCount];
        final double n = (double) sampleStats.getN();
        upperBounds[0] = binStats.get(0).getN() / n;
        for (int i = 1; i < binCount - 1; i++) {
            upperBounds[i] = upperBounds[i - 1] + binStats.get(i).getN() / n;
        }
        upperBounds[binCount - 1] = 1d;

        this.kernelFactory = kernelFactory;
     }

    /**
     * Factory that creates a new instance from the specified data.
     *
     * @param binCount Number of bins.  Must be strictly positive.
     * @param input Input data.  Cannot be {@code null}.
     * @param kernelFactory Factory for creating within-bin kernels.
     * @return a new instance.
     * @throws NotStrictlyPositiveException if {@code binCount <= 0}.
     */
    public static EmpiricalDistribution from(int binCount,
                                             double[] input,
                                             Function<SummaryStatistics, ContinuousDistribution> kernelFactory) {
        return new EmpiricalDistribution(binCount,
                                         input,
                                         kernelFactory);
    }

    /**
     * Factory that creates a new instance from the specified data.
     *
     * @param binCount Number of bins.  Must be strictly positive.
     * @param input Input data.  Cannot be {@code null}.
     * @return a new instance.
     * @throws NotStrictlyPositiveException if {@code binCount <= 0}.
     */
    public static EmpiricalDistribution from(int binCount,
                                             double[] input) {
        return from(binCount, input, defaultKernel());
    }

    /**
     * Create statistics (second pass through the data).
     *
     * @param input Input data.
     * @return bins statistics.
     */
    private List<SummaryStatistics> createBinStats(double[] input) {
        final List<SummaryStatistics> binStats = new ArrayList<>();

        for (int i = 0; i < binCount; i++) {
            binStats.add(i, new SummaryStatistics());
        }

        // Second pass though the data.
        for (int i = 0; i < input.length; i++) {
            final double v = input[i];
            binStats.get(findBin(v)).addValue(v);
        }

        return binStats;
    }

    /**
     * Returns the index of the bin to which the given value belongs.
     *
     * @param value Value whose bin we are trying to find.
     * @return the index of the bin containing the value.
     */
    private int findBin(double value) {
        return Math.min(Math.max((int) JdkMath.ceil((value - min) / delta) - 1,
                                 0),
                        binCount - 1);
    }

    /**
     * Returns a {@link StatisticalSummary} describing this distribution.
     * <strong>Preconditions:</strong><ul>
     * <li>the distribution must be loaded before invoking this method</li></ul>
     *
     * @return the sample statistics
     * @throws IllegalStateException if the distribution has not been loaded
     */
    public StatisticalSummary getSampleStats() {
        return sampleStats.copy();
    }

    /**
     * Returns the number of bins.
     *
     * @return the number of bins.
     */
    public int getBinCount() {
        return binCount;
    }

    /**
     * Returns a copy of the {@link SummaryStatistics} instances containing
     * statistics describing the values in each of the bins.
     * The list is indexed on the bin number.
     *
     * @return the bins statistics.
     */
    public List<SummaryStatistics> getBinStats() {
        final List<SummaryStatistics> copy = new ArrayList<>();
        for (SummaryStatistics s : binStats) {
            copy.add(s.copy());
        }
        return copy;
    }

    /**
     * Returns the upper bounds of the bins.
     *
     * Assuming array {@code u} is returned by this method, the bins are:
     * <ul>
     *  <li>{@code (min, u[0])},</li>
     *  <li>{@code (u[0], u[1])},</li>
     *  <li>... ,</li>
     *  <li>{@code (u[binCount - 2], u[binCount - 1] = max)},</li>
     * </ul>
     *
     * @return the bins upper bounds.
     *
     * @since 2.1
     */
    public double[] getUpperBounds() {
        double[] binUpperBounds = new double[binCount];
        for (int i = 0; i < binCount - 1; i++) {
            binUpperBounds[i] = min + delta * (i + 1);
        }
        binUpperBounds[binCount - 1] = max;
        return binUpperBounds;
    }

    /**
     * Returns the upper bounds of the subintervals of [0, 1] used in generating
     * data from the empirical distribution.
     * Subintervals correspond to bins with lengths proportional to bin counts.
     *
     * <strong>Preconditions:</strong><ul>
     * <li>the distribution must be loaded before invoking this method</li></ul>
     *
     * @return array of upper bounds of subintervals used in data generation
     * @throws NullPointerException unless a {@code load} method has been
     * called beforehand.
     *
     * @since 2.1
     */
    public double[] getGeneratorUpperBounds() {
        int len = upperBounds.length;
        double[] out = new double[len];
        System.arraycopy(upperBounds, 0, out, 0, len);
        return out;
    }

    // Distribution methods.

    /**
     * {@inheritDoc}
     *
     * Returns the kernel density normalized so that its integral over each bin
     * equals the bin mass.
     *
     * Algorithm description:
     * <ol>
     *  <li>Find the bin B that x belongs to.</li>
     *  <li>Compute K(B) = the mass of B with respect to the within-bin kernel (i.e., the
     *   integral of the kernel density over B).</li>
     *  <li>Return k(x) * P(B) / K(B), where k is the within-bin kernel density
     *   and P(B) is the mass of B.</li>
     * </ol>
     *
     * @since 3.1
     */
    @Override
    public double density(double x) {
        if (x < min || x > max) {
            return 0d;
        }
        final int binIndex = findBin(x);
        final ContinuousDistribution kernel = getKernel(binStats.get(binIndex));
        return kernel.density(x) * pB(binIndex) / kB(binIndex);
    }

    /**
     * {@inheritDoc}
     *
     * Algorithm description:
     * <ol>
     *  <li>Find the bin B that x belongs to.</li>
     *  <li>Compute P(B) = the mass of B and P(B-) = the combined mass of the bins below B.</li>
     *  <li>Compute K(B) = the probability mass of B with respect to the within-bin kernel
     *   and K(B-) = the kernel distribution evaluated at the lower endpoint of B</li>
     *  <li>Return P(B-) + P(B) * [K(x) - K(B-)] / K(B) where
     *   K(x) is the within-bin kernel distribution function evaluated at x.</li>
     * </ol>
     * If K is a constant distribution, we return P(B-) + P(B) (counting the full
     * mass of B).
     *
     * @since 3.1
     */
    @Override
    public double cumulativeProbability(double x) {
        if (x < min) {
            return 0d;
        } else if (x >= max) {
            return 1d;
        }
        final int binIndex = findBin(x);
        final double pBminus = pBminus(binIndex);
        final double pB = pB(binIndex);
        final ContinuousDistribution kernel = k(x);
        if (kernel instanceof ConstantContinuousDistribution) {
            if (x < kernel.getMean()) {
                return pBminus;
            } else {
                return pBminus + pB;
            }
        }
        final double[] binBounds = getUpperBounds();
        final double kB = kB(binIndex);
        final double lower = binIndex == 0 ? min : binBounds[binIndex - 1];
        final double withinBinCum =
            (kernel.cumulativeProbability(x) -  kernel.cumulativeProbability(lower)) / kB;
        return pBminus + pB * withinBinCum;
    }

    /**
     * {@inheritDoc}
     *
     * Algorithm description:
     * <ol>
     *  <li>Find the smallest i such that the sum of the masses of the bins
     *   through i is at least p.</li>
     *  <li>
     *   <ol>
     *    <li>Let K be the within-bin kernel distribution for bin i.</li>
     *    <li>Let K(B) be the mass of B under K.</li>
     *    <li>Let K(B-) be K evaluated at the lower endpoint of B (the combined
     *     mass of the bins below B under K).</li>
     *    <li>Let P(B) be the probability of bin i.</li>
     *    <li>Let P(B-) be the sum of the bin masses below bin i.</li>
     *    <li>Let pCrit = p - P(B-)</li>
     *   </ol>
     *  </li>
     *  <li>Return the inverse of K evaluated at
     *    K(B-) + pCrit * K(B) / P(B) </li>
     * </ol>
     *
     * @since 3.1
     */
    @Override
    public double inverseCumulativeProbability(final double p) {
        if (p < 0 ||
            p > 1) {
            throw new OutOfRangeException(p, 0, 1);
        }

        if (p == 0) {
            return getSupportLowerBound();
        }

        if (p == 1) {
            return getSupportUpperBound();
        }

        int i = 0;
        while (cumBinP(i) < p) {
            ++i;
        }

        final SummaryStatistics stats = binStats.get(i);
        final ContinuousDistribution kernel = getKernel(stats);
        final double kB = kB(i);
        final double[] binBounds = getUpperBounds();
        final double lower = i == 0 ? min : binBounds[i - 1];
        final double kBminus = kernel.cumulativeProbability(lower);
        final double pB = pB(i);
        final double pBminus = pBminus(i);
        final double pCrit = p - pBminus;
        if (pCrit <= 0) {
            return lower;
        }

        final double cP = kBminus + pCrit * kB / pB;

        return Precision.equals(cP, 1d) ?
            kernel.inverseCumulativeProbability(1d) :
            kernel.inverseCumulativeProbability(cP);
    }

    /**
     * {@inheritDoc}
     * @since 3.1
     */
    @Override
    public double getMean() {
       return sampleStats.getMean();
    }

    /**
     * {@inheritDoc}
     * @since 3.1
     */
    @Override
    public double getVariance() {
        return sampleStats.getVariance();
    }

    /**
     * {@inheritDoc}
     * @since 3.1
     */
    @Override
    public double getSupportLowerBound() {
       return min;
    }

    /**
     * {@inheritDoc}
     * @since 3.1
     */
    @Override
    public double getSupportUpperBound() {
        return max;
    }

    /**
     * The probability of bin i.
     *
     * @param i the index of the bin
     * @return the probability that selection begins in bin i
     */
    private double pB(int i) {
        return i == 0 ? upperBounds[0] :
            upperBounds[i] - upperBounds[i - 1];
    }

    /**
     * The combined probability of the bins up to but not including bin i.
     *
     * @param i the index of the bin
     * @return the probability that selection begins in a bin below bin i.
     */
    private double pBminus(int i) {
        return i == 0 ? 0 : upperBounds[i - 1];
    }

    /**
     * Mass of bin i under the within-bin kernel of the bin.
     *
     * @param i index of the bin
     * @return the difference in the within-bin kernel cdf between the
     * upper and lower endpoints of bin i
     */
    private double kB(int i) {
        final double[] binBounds = getUpperBounds();
        final ContinuousDistribution kernel = getKernel(binStats.get(i));
        return i == 0 ? kernel.probability(min, binBounds[0]) :
            kernel.probability(binBounds[i - 1], binBounds[i]);
    }

    /**
     * The within-bin kernel of the bin that x belongs to.
     *
     * @param x the value to locate within a bin
     * @return the within-bin kernel of the bin containing x
     */
    private ContinuousDistribution k(double x) {
        final int binIndex = findBin(x);
        return getKernel(binStats.get(binIndex));
    }

    /**
     * The combined probability of the bins up to and including binIndex.
     *
     * @param binIndex maximum bin index
     * @return sum of the probabilities of bins through binIndex
     */
    private double cumBinP(int binIndex) {
        return upperBounds[binIndex];
    }

    /**
     * @param stats Bin statistics.
     * @return the within-bin kernel.
     */
    private ContinuousDistribution getKernel(SummaryStatistics stats) {
        return kernelFactory.apply(stats);
    }

    /**
     * The within-bin smoothing kernel: A Gaussian distribution
     * (unless the bin contains 0 or 1 observation, in which case
     * a constant distribution is returned).
     *
     * @return the within-bin kernel factory.
     */
    private static Function<SummaryStatistics, ContinuousDistribution> defaultKernel() {
        return stats -> {
            if (stats.getN() <= 3 ||
                stats.getVariance() == 0) {
                return new ConstantContinuousDistribution(stats.getMean());
            } else {
                return NormalDistribution.of(stats.getMean(),
                                             stats.getStandardDeviation());
            }
        };
    }

    /**
     * Constant distribution.
     */
    private static class ConstantContinuousDistribution implements ContinuousDistribution {
        /** Constant value of the distribution. */
        private final double value;

        /**
         * Create a constant real distribution with the given value.
         *
         * @param value Value of this distribution.
         */
        ConstantContinuousDistribution(double value) {
            this.value = value;
        }

        /** {@inheritDoc} */
        @Override
        public double density(double x) {
            return x == value ? 1 : 0;
        }

        /** {@inheritDoc} */
        @Override
        public double cumulativeProbability(double x)  {
            return x < value ? 0 : 1;
        }

        /** {@inheritDoc} */
        @Override
        public double inverseCumulativeProbability(final double p) {
            if (p < 0 ||
                p > 1) {
                // Should never happen.
                throw new IllegalArgumentException("Internal error");
            }
            return value;
        }

        /** {@inheritDoc} */
        @Override
        public double getMean() {
            return value;
        }

        /** {@inheritDoc} */
        @Override
        public double getVariance() {
            return 0;
        }

        /**{@inheritDoc} */
        @Override
        public double getSupportLowerBound() {
            return value;
        }

        /** {@inheritDoc} */
        @Override
        public double getSupportUpperBound() {
            return value;
        }

        /**
         * {@inheritDoc}
         *
         * @param rng Not used: distribution contains a single value.
         * @return the value of the distribution.
         */
        @Override
        public ContinuousDistribution.Sampler createSampler(final UniformRandomProvider rng) {
            return this::getSupportLowerBound;
        }
    }
}
