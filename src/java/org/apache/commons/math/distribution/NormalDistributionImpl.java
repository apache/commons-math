/*
 * Copyright 2004 The Apache Software Foundation.
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

package org.apache.commons.math.distribution;

import java.io.Serializable;

/**
 * Default implementation of
 * {@link org.apache.commons.math.distribution.NormalDistribution}.<p>
 * You can choose the algorithm used to calculate cumulative probability
 * using method {@link #setCdfAlgorithm}. The deafault is the Cody algorithm 
 * {@link org.apache.commons.math.distribution.NormalCDFPreciseAlgorithm}
 * 
 * @version $Revision: 1.5 $ $Date: 2004/04/27 04:37:58 $
 */
public class NormalDistributionImpl extends AbstractContinuousDistribution 
		implements NormalDistribution, Serializable {
    
    /** The mean of this distribution. */
	private double mean = 0;
    
    /** The standard deviation of this distribution. */
	private double standardDeviation = 1;
    
    /** The algorithm used to compute cumulative probabilities. */
	private NormalCDFAlgorithm cdfAlgorithm = new NormalCDFPreciseAlgorithm();
	
	/**
	 * Create a normal distribution using the given mean and standard deviation.
	 * @param mean mean for this distribution
	 * @param sd standard deviation for this distribution
	 */
	public NormalDistributionImpl(double mean, double sd){
		super();
		setMean(mean);
		setStandardDeviation(sd);
	}
	/**
	 * Creates normal distribution with the mean equal to zero and standard
	 * deviation equal to one. 
	 */
	public NormalDistributionImpl(){
		this(0.0, 1.0);
	}	
	/**
	 * Access the mean.
	 * @return mean for this distribution
	 */	
	public double getMean() {
		return mean;
	}
	/**
	 * Modify the mean.
	 * @param mean for this distribution
	 */
	public void setMean(double mean) {
		this.mean = mean;
	}

	/**
	 * Access the standard deviation.
	 * @return standard deviation for this distribution
	 */
	public double getStandardDeviation() {
		return standardDeviation;
	}

	/**
	 * Modify the standard deviation.
	 * @param sd standard deviation for this distribution
	 */
	public void setStandardDeviation(double sd) {
		if (sd < 0.0) {
			throw new IllegalArgumentException("Standard deviation must be" +				"positive or zero.");
		}		
		standardDeviation = sd;
	}

	/**
	 * For this disbution, X, this method returns P(X &lt; <code>x</code>).
	 * @param x the value at which the CDF is evaluated.
	 * @return CDF evaluted at <code>x</code>. 
	 */
	public double cumulativeProbability(double x) {
		double z = x;
		if(standardDeviation > 0){
			z = (x - mean) / standardDeviation;
		}else{
			return 0.0;
		}
		return cdfAlgorithm.cdf(z);
	}


	/**
	 * For this distribution, X, this method returns the critical point x, such
	 * that P(X &lt; x) = <code>p</code>.<p>
	 * Provided implementation is adopted from 
     * <a href="http://www.r-project.org/">R statistical package</a> function
     * <code>qnorm(...)</code>.<p>
	 * References:
	 * <ul>
	 * <li>
	 *  Beasley, J. D. and S. G. Springer (1977).
	 *  <a href="http://lib.stat.cmu.edu/apstat/111">
	 *	Algorithm AS 111: The percentage points of the normal distribution</a>,
	 *	Applied Statistics, 26, 118-121.
	 * </li>
	 * <li>
	 *  Wichura, M.J. (1988).
	 *  <a href="http://lib.stat.cmu.edu/apstat/241">
	 *  Algorithm AS 241: The Percentage Points of the Normal Distribution.</a>
	 *  Applied Statistics, 37, 477-484.
	 * </li>
	 * </ul>
	 *
	 * @param p the desired probability
	 * @return x, such that P(X &lt; x) = <code>p</code>
	 */
	public double inverseCumulativeProbability(double p) {
		if (p < 0.0 || p > 1.0) {
			throw new IllegalArgumentException(
					"p must be between 0.0 and 1.0, inclusive.");
		}
		
		//TODO is this ok?
		if(standardDeviation == 0){
			return mean;
		}
		
		double r, val;		
		double q = p - 0.5;

		if (Math.abs(q) <= .425) {/* 0.075 <= p <= 0.925 */
			r = 0.180625 - q*q;
			val =
				q * (((((((r * 2509.0809287301226727 +
						   33430.575583588128105) * r + 67265.770927008700853) * r +
						 45921.953931549871457) * r + 13731.693765509461125) * r +
					   1971.5909503065514427) * r + 133.14166789178437745) * r +
					 3.387132872796366608)
				/ (((((((r * 5226.495278852854561 +
						 28729.085735721942674) * r + 39307.89580009271061) * r +
					   21213.794301586595867) * r + 5394.1960214247511077) * r +
					 687.1870074920579083) * r + 42.313330701600911252) * r + 1.);
		}else { //closer than 0.075 from {0,1} boundary
		if (q > 0) {
			r = 1 - p;
		} else {
			r = p;
		}
		r = Math.sqrt(- Math.log(r));
		if (r <= 5.0) {
			r += -1.6;
			val = (((((((r * 7.7454501427834140764e-4 +
					   0.0227238449892691845833) * r + 0.24178072517745061177) *
					 r + 1.27045825245236838258) * r +
					3.64784832476320460504) * r + 5.7694972214606914055) *
				  r + 4.6303378461565452959) * r +
				 1.42343711074968357734)
				/ (((((((r *
						 1.05075007164441684324e-9 + 5.475938084995344946e-4) *
						r + 0.0151986665636164571966) * r +
					   0.14810397642748007459) * r + 0.68976733498510000455) *
					 r + 1.6763848301838038494) * r +
					2.05319162663775882187) * r + 1.0);
		}else { //very close to  0 or 1
			r += -5.;
			val = (((((((r * 2.01033439929228813265e-7 +
					   2.71155556874348757815e-5) * r +
					  0.0012426609473880784386) * r + 0.026532189526576123093) *
					r + 0.29656057182850489123) * r +
				   1.7848265399172913358) * r + 5.4637849111641143699) *
				 r + 6.6579046435011037772) /
				 (((((((r *
						 2.04426310338993978564e-15 + 1.4215117583164458887e-7)*
						r + 1.8463183175100546818e-5) * r +
					   7.868691311456132591e-4) * r + 0.0148753612908506148525) *
					   r + 0.13692988092273580531) * r +
					0.59983220655588793769) * r + 1.0);
		}
		if(q < 0.0)
			val = -val;
		}
		return mean + standardDeviation * val;
	}


	/**
	 * Access algorithm used to calculate cumulative probability
	 * @return cdfAlgorithm the value of cumulative probability
	 */
	public NormalCDFAlgorithm getCdfAlgorithm() {
		return cdfAlgorithm;
	}


	/**
	 * Modify the algorithm used to calculate cumulative probability
	 * @param normalCDF the algorithm used to calculate cumulative probability
	 */
	public void setCdfAlgorithm(NormalCDFAlgorithm normalCDF) {
		cdfAlgorithm = normalCDF;
	}

	
	/**
	 * Access the domain value lower bound, based on <code>p</code>, used to
	 * bracket a CDF root.  This method is used by
	 * {@link #inverseCumulativeProbability(double)} to find critical values.
	 * 
	 * @param p the desired probability for the critical value
	 * @return domain value lower bound, i.e.
	 *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code> 
	 */
	protected double getDomainLowerBound(double p) {
		return -Double.MAX_VALUE;
	}

	/**
	 * Access the domain value upper bound, based on <code>p</code>, used to
	 * bracket a CDF root.  This method is used by
	 * {@link #inverseCumulativeProbability(double)} to find critical values.
	 * 
	 * @param p the desired probability for the critical value
	 * @return domain value upper bound, i.e.
	 *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code> 
	 */
	protected double getDomainUpperBound(double p) {
		return Double.MAX_VALUE;
	}

	/**
	 * Access the initial domain value, based on <code>p</code>, used to
	 * bracket a CDF root.  This method is used by
	 * {@link #inverseCumulativeProbability(double)} to find critical values.
	 * 
	 * @param p the desired probability for the critical value
	 * @return initial domain value
	 */
	protected double getInitialDomain(double p) {
		return 0.0;
	}
}
