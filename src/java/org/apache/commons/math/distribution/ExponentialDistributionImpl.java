package org.apache.commons.math.stat.distribution;

/**
 * The default implementation of {@link ExponentialDistribution}
 * 
 * @author Brent Worden
 */
public class ExponentialDistributionImpl
	extends AbstractContinuousDistribution
	implements ExponentialDistribution {

    /** The mean of this distribution. */
    private double mean;
    
    /**
     * Create a exponential distribution with the given mean.
     * @param degreesOfFreedom degrees of freedom.
     */
	public ExponentialDistributionImpl(double mean) {
		super();
        setMean(mean);
	}

        
    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCummulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code> 
     */
    protected double getDomainLowerBound(double p){
        return 0.0;
    }

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCummulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code> 
     */
    protected double getDomainUpperBound(double p){
        return Double.MAX_VALUE;
    }

    /**
     * Access the initial domain value, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCummulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return initial domain value
     */
    protected double getInitialDomain(double p){
        return getMean();
    }

    /**
     * Modify the mean.
     * @param mean the new mean.
     */
	public void setMean(double mean) {
        if(mean <= 0.0){
            throw new IllegalArgumentException("mean must be positive.");
        }
        this.mean = mean;
	}

    /**
     * Access the mean.
     * @return the mean.
     */
	public double getMean() {
		return mean;
	}

    /**
     * <p>
     * For this disbution, X, this method returns P(X &lt; x).
     * </p>
     * 
     * <p>
     * The implementation of this method is based on:
     * <ul>
     * <li>
     * <a href="http://mathworld.wolfram.com/ExponentialDistribution.html">
     * Exponential Distribution</a>, equation (1).</li>
     * </ul>
     * </p>
     * 
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     */
	public double cummulativeProbability(double x) {
        double ret;
		if(x < 0.0){
            ret = 0.0;
		} else {
            ret = 1.0 - Math.exp(-x / getMean());
		}
        return ret;
	}
    
    /**
     * For this distribution, X, this method returns the critical point x, such
     * that P(X &lt; x) = <code>p</code>.
     *
     * @param p the desired probability
     * @return x, such that P(X &lt; x) = <code>p</code>
     */
    public double inverseCummulativeProbability(double p){
        if(p < 0.0 || p > 1.0){
            throw new IllegalArgumentException(
                "p must be between 0.0 and 1.0, inclusive.");
        }
        return -getMean() * Math.log(1.0 - p);
    }
}
