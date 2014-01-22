package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;

/**
 * Implements the Wilson score method for creating a binomial proportion confidence interval.
 *
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Wilson_score_interval">
 *      Wilson score interval (Wikipedia)</a>
 * @version $Id$
 * @since 3.3
 */
public class WilsonScoreInterval implements BinomialConfidenceInterval {

    /** {@inheritDoc} */
    @Override
    public ConfidenceInterval createInterval(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        final double alpha = (1.0 - confidenceLevel) / 2;
        final NormalDistribution normalDistribution = new NormalDistribution();
        final double z = normalDistribution.inverseCumulativeProbability(1 - alpha);
        final double zSquared = FastMath.pow(z, 2);
        final double mean = (double) numberOfSuccesses / (double) numberOfTrials;

        final double factor = 1.0 / (1 + (1.0 / numberOfTrials) * zSquared);
        final double modifiedSuccessRatio = mean + (1.0 / (2 * numberOfTrials)) * zSquared;
        final double difference = z *
                                  FastMath.sqrt(1.0 / numberOfTrials * mean * (1 - mean) +
                                                (1.0 / (4 * FastMath.pow(numberOfTrials, 2)) * zSquared));

        final double lowerBound = factor * (modifiedSuccessRatio - difference);
        final double upperBound = factor * (modifiedSuccessRatio + difference);
        return new ConfidenceInterval(lowerBound, upperBound, confidenceLevel);
    }

}
