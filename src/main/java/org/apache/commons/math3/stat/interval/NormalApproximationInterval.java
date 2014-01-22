package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;

/**
 * Implements the normal approximation method for creating a binomial proportion confidence interval.
 *
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Normal_approximation_interval">
 *      Normal approximation interval (Wikipedia)</a>
 * @version $Id$
 * @since 3.3
 */
public class NormalApproximationInterval implements BinomialConfidenceInterval {

    /** {@inheritDoc} */
    @Override
    public ConfidenceInterval createInterval(int numberOfTrials, int numberOfSuccesses,
                                             double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        final double mean = (double) numberOfSuccesses / (double) numberOfTrials;
        final double alpha = (1.0 - confidenceLevel) / 2;
        final NormalDistribution normalDistribution = new NormalDistribution();
        final double difference = normalDistribution.inverseCumulativeProbability(1 - alpha) *
                                  FastMath.sqrt(1.0 / numberOfTrials * mean * (1 - mean));
        return new ConfidenceInterval(mean - difference, mean + difference, confidenceLevel);
    }

}
