package org.apache.commons.math.stat.distribution;

/**
 * The default implementation of {@link ChiSquaredDistribution}
 * 
 * @author Brent Worden
 */
public class ChiSquaredDistributionImpl
    extends AbstractContinuousDistribution
    implements ChiSquaredDistribution {
    
    /** Internal Gamma distribution. */    
    private GammaDistribution gamma;
    
    /**
     * Create a Chi-Squared distribution with the given degrees of freedom.
     * @param degreesOfFreedom degrees of freedom.
     */
    public ChiSquaredDistributionImpl(double degreesOfFreedom){
        super();
        setGamma(DistributionFactory.newInstance().createGammaDistribution(
            degreesOfFreedom / 2.0, 2.0));
    }
    
    /**
     * Modify the degrees of freedom.
     * @param degreesOfFreedom the new degrees of freedom.
     */
    public void setDegreesOfFreedom(double degreesOfFreedom) {
        getGamma().setAlpha(degreesOfFreedom / 2.0);
    }
        
    /**
     * Access the degrees of freedom.
     * @return the degrees of freedom.
     */
    public double getDegreesOfFreedom() {
        return getGamma().getAlpha() * 2.0;
    }
        
    /**
     * For this disbution, X, this method returns P(X &lt; x).
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution. 
     */
    public double cummulativeProbability(double x) {
        return getGamma().cummulativeProbability(x);
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
        return Double.MIN_VALUE * getGamma().getBeta();
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
        // NOTE: chi squared is skewed to the left
        // NOTE: therefore, P(X < &mu;) > .5

        double ret;

        if(p < .5){
            // use mean
            ret = getDegreesOfFreedom();
        } else {
            // use max
            ret = Double.MAX_VALUE;
        }
        
        return ret;
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
        // NOTE: chi squared is skewed to the left
        // NOTE: therefore, P(X < &mu;) > .5
        
        double ret;

        if(p < .5){
            // use 1/2 mean
            ret = getDegreesOfFreedom() * .5;
        } else {
            // use mean
            ret = getDegreesOfFreedom();
        }
        
        return ret;
    }
    
    /**
     * Modify the Gamma distribution.
     * @param gamma the new distribution.
     */
    private void setGamma(GammaDistribution gamma) {
        this.gamma = gamma;
    }

    /**
     * Access the Gamma distribution.
     * @return the internal Gamma distribution.
     */
    private GammaDistribution getGamma() {
        return gamma;
    }
}
