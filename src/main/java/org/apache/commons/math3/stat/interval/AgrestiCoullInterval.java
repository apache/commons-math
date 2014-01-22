package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;

/**
 * Implements the Agresti-Coull method for creating a binomial proportion confidence interval.
 *
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Agresti-Coull_Interval">
 *      Agresti-Coull interval (Wikipedia)</a>
 * @version $Id$
 * @since 3.3
 */
public class AgrestiCoullInterval implements BinomialConfidenceInterval {

    /** {@inheritDoc} */
    @Override
    public ConfidenceInterval createInterval(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        final double alpha = (1.0 - confidenceLevel) / 2;
        final NormalDistribution normalDistribution = new NormalDistribution();
        final double z = normalDistribution.inverseCumulativeProbability(1 - alpha);
        final double zSquared = FastMath.pow(z, 2);
        final double modifiedNumberOfTrials = numberOfTrials + zSquared;
        final double modifiedSuccessesRatio = (1.0 / modifiedNumberOfTrials) * (numberOfSuccesses + 0.5 * zSquared);
        final double difference = z *
                                  FastMath.sqrt(1.0 / modifiedNumberOfTrials * modifiedSuccessesRatio *
                                                (1 - modifiedSuccessesRatio));
        return new ConfidenceInterval(modifiedSuccessesRatio - difference, modifiedSuccessesRatio + difference,
                                      confidenceLevel);
    }

}
