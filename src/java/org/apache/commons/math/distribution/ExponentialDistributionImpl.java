package org.apache.commons.math.stat.distribution;

/**
 * The default implementation of {@link ExponentialDistribution}
 * 
 * @author Brent Worden
 */
public class ExponentialDistributionImpl
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
		if(x <= 0.0){
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
        double ret;
        
        if(p < 0.0 || p > 1.0){
            ret = Double.NaN;
        } else if(p == 1.0){
            ret = Double.POSITIVE_INFINITY;
        } else {
            ret = -getMean() * Math.log(1.0 - p);
        }
        
        return ret;
    }
    
    /**
     * For this disbution, X, this method returns P(x0 &lt; X &lt; x1).
     * @param x0 the lower bound
     * @param x1 the upper bound
     * @return the cummulative probability. 
     */
    public double cummulativeProbability(double x0, double x1) {
        return cummulativeProbability(x1) - cummulativeProbability(x0);
    }
}
