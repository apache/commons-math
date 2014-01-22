package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.distribution.FDistribution;

/**
 * Implements the Clopper-Pearson method for creating a binomial proportion confidence interval.
 *
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Clopper-Pearson_interval">
 *      Clopper-Pearson interval (Wikipedia)</a>
 * @version $Id$
 * @since 3.3
 */
public class ClopperPearsonInterval implements BinomialConfidenceInterval {

    /** {@inheritDoc} */
    @Override
    public ConfidenceInterval createInterval(int numberOfTrials, int numberOfSuccesses,
                                             double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        double lowerBound = 0;
        double upperBound = 0;
        final double alpha = (1.0 - confidenceLevel) / 2.0;

        final FDistribution distributionLowerBound = new FDistribution(2 * (numberOfTrials - numberOfSuccesses + 1),
                                                                       2 * numberOfSuccesses);
        final double fValueLowerBound = distributionLowerBound.inverseCumulativeProbability(1 - alpha);
        if (numberOfSuccesses > 0) {
            lowerBound = numberOfSuccesses /
                         (numberOfSuccesses + (numberOfTrials - numberOfSuccesses + 1) * fValueLowerBound);
        }

        final FDistribution distributionUpperBound = new FDistribution(2 * (numberOfSuccesses + 1),
                                                                       2 * (numberOfTrials - numberOfSuccesses));
        final double fValueUpperBound = distributionUpperBound.inverseCumulativeProbability(1 - alpha);
        if (numberOfSuccesses > 0) {
            upperBound = (numberOfSuccesses + 1) * fValueUpperBound /
                         (numberOfTrials - numberOfSuccesses + (numberOfSuccesses + 1) * fValueUpperBound);
        }

        return new ConfidenceInterval(lowerBound, upperBound, confidenceLevel);
    }

}
