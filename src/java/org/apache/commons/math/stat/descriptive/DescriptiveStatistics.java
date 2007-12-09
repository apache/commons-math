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
package org.apache.commons.math.stat.descriptive;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.apache.commons.discovery.tools.DiscoverClass;
import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Skewness;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;
import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.apache.commons.math.stat.descriptive.summary.SumOfSquares;
import org.apache.commons.math.util.ResizableDoubleArray;


/**
 * Maintains a dataset of values of a single variable and computes descriptive
 * statistics based on stored data. The {@link #getWindowSize() windowSize}
 * property sets a limit on the number of values that can be stored in the 
 * dataset.  The default value, INFINITE_WINDOW, puts no limit on the size of
 * the dataset.  This value should be used with caution, as the backing store
 * will grow without bound in this case.  For very large datasets, 
 * {@link SummaryStatistics}, which does not store the dataset, should be used
 * instead of this class. If <code>windowSize</code> is not INFINITE_WINDOW and
 * more values are added than can be stored in the dataset, new values are
 * added in a "rolling" manner, with new values replacing the "oldest" values 
 * in the dataset.
 * 
 * <p>Note: this class is not threadsafe.  Use 
 * {@link SynchronizedDescriptiveStatistics} if concurrent access from multiple
 * threads is required.</p>
 *
 * @version $Revision$ $Date$
 */
public class DescriptiveStatistics implements StatisticalSummary, Serializable {
    
    /** Serialization UID */
    private static final long serialVersionUID = -2734185686570407433L;
    
    /** hold the window size **/
    protected int windowSize = INFINITE_WINDOW;
    
    /** 
     *  Stored data values
     */
    protected ResizableDoubleArray eDA = new ResizableDoubleArray();
  
    // UnivariateStatistic stats implementations - can be reset by setters
    private UnivariateStatistic meanImpl = new Mean();
    private UnivariateStatistic geometricMeanImpl = new GeometricMean();
    private UnivariateStatistic kurtosisImpl = new Kurtosis();
    private UnivariateStatistic maxImpl = new Max();
    private UnivariateStatistic minImpl = new Min();
    private UnivariateStatistic percentileImpl = new Percentile();
    private UnivariateStatistic skewnessImpl = new Skewness();
    private UnivariateStatistic varianceImpl = new Variance();
    private UnivariateStatistic sumsqImpl = new SumOfSquares();
    private UnivariateStatistic sumImpl = new Sum();
    
    /**
     * Construct a DescriptiveStatistics instance with an infinite window
     */
    public DescriptiveStatistics() {
    }
    
    /**
     * Construct a DescriptiveStatistics instance with the specified window
     * 
     * @param window the window size.
     */
    public DescriptiveStatistics(int window) {
        super();
        setWindowSize(window);
    }
    
    /**
     * Create an instance of a <code>DescriptiveStatistics</code>
     * @param cls the type of <code>DescriptiveStatistics</code> object to
     *        create. 
     * @return a new instance. 
     * @throws InstantiationException is thrown if the object can not be
     *            created.
     * @throws IllegalAccessException is thrown if the type's default
     *            constructor is not accessible.
     * @deprecated to be removed in commons-math 2.0
     */
    public static DescriptiveStatistics newInstance(Class cls) throws InstantiationException, IllegalAccessException {
        return (DescriptiveStatistics)cls.newInstance();
    }
    
    /**
     * Create an instance of a <code>DescriptiveStatistics</code>
     * @return a new DescriptiveStatistics instance. 
     * @deprecated to be removed in commons-math 2.0
     */
    public static DescriptiveStatistics newInstance() {
        DescriptiveStatistics factory = null;
        try {
            DiscoverClass dc = new DiscoverClass();
            factory = (DescriptiveStatistics) dc.newInstance(
                DescriptiveStatistics.class,
                "org.apache.commons.math.stat.descriptive.DescriptiveStatisticsImpl");
        } catch(Throwable t) {
            return new DescriptiveStatisticsImpl();
        }
        return factory;
    }
    
    /**
     * Represents an infinite window size.  When the {@link #getWindowSize()}
     * returns this value, there is no limit to the number of data values
     * that can be stored in the dataset.
     */
    public static final int INFINITE_WINDOW = -1;

    /**
     * Adds the value to the dataset. If the dataset is at the maximum size
     * (i.e., the number of stored elements equals the currently configured
     * windowSize), the first (oldest) element in the dataset is discarded
     * to make room for the new value.
     * 
     * @param v the value to be added 
     */
    public void addValue(double v) {
        if (windowSize != INFINITE_WINDOW) {
            if (getN() == windowSize) {
                eDA.addElementRolling(v);
            } else if (getN() < windowSize) {
                eDA.addElement(v);
            }
        } else {
            eDA.addElement(v);
        }
    }

    /** 
     * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
     * arithmetic mean </a> of the available values 
     * @return The mean or Double.NaN if no values have been added.
     */
    public double getMean() {
        return apply(meanImpl);
    }

    /** 
     * Returns the <a href="http://www.xycoon.com/geometric_mean.htm">
     * geometric mean </a> of the available values
     * @return The geometricMean, Double.NaN if no values have been added, 
     * or if the productof the available values is less than or equal to 0.
     */
    public double getGeometricMean() {
        return apply(geometricMeanImpl);
    }

    /** 
     * Returns the variance of the available values.
     * @return The variance, Double.NaN if no values have been added 
     * or 0.0 for a single value set.  
     */
    public double getVariance() {
        return apply(varianceImpl);
    }

    /** 
     * Returns the standard deviation of the available values.
     * @return The standard deviation, Double.NaN if no values have been added 
     * or 0.0 for a single value set. 
     */
    public double getStandardDeviation() {
        double stdDev = Double.NaN;
        if (getN() > 0) {
            if (getN() > 1) {
                stdDev = Math.sqrt(getVariance());
            } else {
                stdDev = 0.0;
            }
        }
        return (stdDev);
    }

    /**
     * Returns the skewness of the available values. Skewness is a 
     * measure of the asymmetry of a given distribution.
     * @return The skewness, Double.NaN if no values have been added 
     * or 0.0 for a value set &lt;=2. 
     */
    public double getSkewness() {
        return apply(skewnessImpl);
    }

    /**
     * Returns the Kurtosis of the available values. Kurtosis is a 
     * measure of the "peakedness" of a distribution
     * @return The kurtosis, Double.NaN if no values have been added, or 0.0 
     * for a value set &lt;=3. 
     */
    public double getKurtosis() {
        return apply(kurtosisImpl);
    }

    /** 
     * Returns the maximum of the available values
     * @return The max or Double.NaN if no values have been added.
     */
    public double getMax() {
        return apply(maxImpl);
    }

    /** 
    * Returns the minimum of the available values
    * @return The min or Double.NaN if no values have been added.
    */
    public double getMin() {
        return apply(minImpl);
    }

    /** 
     * Returns the number of available values
     * @return The number of available values
     */
    public long getN() {
        return eDA.getNumElements();
    }

    /**
     * Returns the sum of the values that have been added to Univariate.
     * @return The sum or Double.NaN if no values have been added
     */
    public double getSum() {
        return apply(sumImpl);
    }

    /**
     * Returns the sum of the squares of the available values.
     * @return The sum of the squares or Double.NaN if no 
     * values have been added.
     */
    public double getSumsq() {
        return apply(sumsqImpl);
    }

    /** 
     * Resets all statistics and storage
     */
    public void clear() {
        eDA.clear();
    }


    /**
     * Returns the maximum number of values that can be stored in the
     * dataset, or INFINITE_WINDOW (-1) if there is no limit.
     * 
     * @return The current window size or -1 if its Infinite.
     */
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * WindowSize controls the number of values which contribute 
     * to the reported statistics.  For example, if 
     * windowSize is set to 3 and the values {1,2,3,4,5} 
     * have been added <strong> in that order</strong> 
     * then the <i>available values</i> are {3,4,5} and all
     * reported statistics will be based on these values
     * @param windowSize sets the size of the window.
     */
    public void setWindowSize(int windowSize) {
        if (windowSize < 1) {
            if (windowSize != INFINITE_WINDOW) {
                throw new IllegalArgumentException("window size must be positive.");
            }
        }
        
        this.windowSize = windowSize;

        // We need to check to see if we need to discard elements
        // from the front of the array.  If the windowSize is less than 
        // the current number of elements.
        if (windowSize != INFINITE_WINDOW && windowSize < eDA.getNumElements()) {
            eDA.discardFrontElements(eDA.getNumElements() - windowSize);
        }
    }
    
    /**
     * Returns the current set of values in an array of double primitives.  
     * The order of addition is preserved.  The returned array is a fresh
     * copy of the underlying data -- i.e., it is not a reference to the
     * stored data.
     * 
     * @return returns the current set of numbers in the order in which they 
     *         were added to this set
     */
    public double[] getValues() {
        double[] copiedArray = new double[eDA.getNumElements()];
        System.arraycopy(eDA.getElements(), 0, copiedArray,
            0, eDA.getNumElements());
        return copiedArray;
    }

    /**
     * Returns the current set of values in an array of double primitives,  
     * sorted in ascending order.  The returned array is a fresh
     * copy of the underlying data -- i.e., it is not a reference to the
     * stored data.
     * @return returns the current set of 
     * numbers sorted in ascending order        
     */
    public double[] getSortedValues() {
        double[] sort = getValues();
        Arrays.sort(sort);
        return sort;
    }

    /**
     * Returns the element at the specified index
     * @param index The Index of the element
     * @return return the element at the specified index
     */
    public double getElement(int index) {
        return eDA.getElement(index);
    }

    /**
     * Returns an estimate for the pth percentile of the stored values. 
     * <p>
     * The implementation provided here follows the first estimation procedure presented
     * <a href="http://www.itl.nist.gov/div898/handbook/prc/section2/prc252.htm">here.</a>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>0 &lt; p &lt; 100</code> (otherwise an 
     * <code>IllegalArgumentException</code> is thrown)</li>
     * <li>at least one value must be stored (returns <code>Double.NaN
     *     </code> otherwise)</li>
     * </ul>
     * 
     * @param p the requested percentile (scaled from 0 - 100)
     * @return An estimate for the pth percentile of the stored data 
     * @throws IllegalStateException if percentile implementation has been
     *  overridden and the supplied implementation does not support setQuantile
     * values
     */
    public double getPercentile(double p) {
        if (percentileImpl instanceof Percentile) {
            ((Percentile) percentileImpl).setQuantile(p);
        } else {
            try {
                percentileImpl.getClass().getMethod("setQuantile", 
                        new Class[] {Double.TYPE}).invoke(percentileImpl,
                                new Object[] {new Double(p)});
            } catch (NoSuchMethodException e1) { // Setter guard should prevent
                throw new IllegalArgumentException(
                   "Percentile implementation does not support setQuantile");
            } catch (IllegalAccessException e2) {
                throw new IllegalArgumentException(
                    "IllegalAccessException setting quantile"); 
            } catch (InvocationTargetException e3) {
                throw new IllegalArgumentException(
                    "Error setting quantile" + e3.toString()); 
            }
        }
        return apply(percentileImpl);
    }
    
    /**
     * Generates a text report displaying univariate statistics from values
     * that have been added.  Each statistic is displayed on a separate
     * line.
     * 
     * @return String with line feeds displaying statistics
     */
    public String toString() {
        StringBuffer outBuffer = new StringBuffer();
        outBuffer.append("DescriptiveStatistics:\n");
        outBuffer.append("n: " + getN() + "\n");
        outBuffer.append("min: " + getMin() + "\n");
        outBuffer.append("max: " + getMax() + "\n");
        outBuffer.append("mean: " + getMean() + "\n");
        outBuffer.append("std dev: " + getStandardDeviation() + "\n");
        outBuffer.append("median: " + getPercentile(50) + "\n");
        outBuffer.append("skewness: " + getSkewness() + "\n");
        outBuffer.append("kurtosis: " + getKurtosis() + "\n");
        return outBuffer.toString();
    }
    
    /**
     * Apply the given statistic to the data associated with this set of statistics.
     * @param stat the statistic to apply
     * @return the computed value of the statistic.
     */
    public double apply(UnivariateStatistic stat) {
        return stat.evaluate(eDA.getValues(), eDA.start(), eDA.getNumElements());
    }

    // Implementation getters and setter
    
    /**
     * @return the meanImpl
     */
    public synchronized UnivariateStatistic getMeanImpl() {
        return meanImpl;
    }

    /**
     * @param meanImpl the meanImpl to set
     */
    public synchronized void setMeanImpl(UnivariateStatistic meanImpl) {
        this.meanImpl = meanImpl;
    }

    /**
     * @return the geometricMeanImpl
     */
    public synchronized UnivariateStatistic getGeometricMeanImpl() {
        return geometricMeanImpl;
    }

    /**
     * @param geometricMeanImpl the geometricMeanImpl to set
     */
    public synchronized void setGeometricMeanImpl(
            UnivariateStatistic geometricMeanImpl) {
        this.geometricMeanImpl = geometricMeanImpl;
    }

    /**
     * @return the kurtosisImpl
     */
    public synchronized UnivariateStatistic getKurtosisImpl() {
        return kurtosisImpl;
    }

    /**
     * @param kurtosisImpl the kurtosisImpl to set
     */
    public synchronized void setKurtosisImpl(UnivariateStatistic kurtosisImpl) {
        this.kurtosisImpl = kurtosisImpl;
    }

    /**
     * @return the maxImpl
     */
    public synchronized UnivariateStatistic getMaxImpl() {
        return maxImpl;
    }

    /**
     * @param maxImpl the maxImpl to set
     */
    public synchronized void setMaxImpl(UnivariateStatistic maxImpl) {
        this.maxImpl = maxImpl;
    }

    /**
     * @return the minImpl
     */
    public synchronized UnivariateStatistic getMinImpl() {
        return minImpl;
    }

    /**
     * @param minImpl the minImpl to set
     */
    public synchronized void setMinImpl(UnivariateStatistic minImpl) {
        this.minImpl = minImpl;
    }

    /**
     * @return the percentileImpl
     */
    public synchronized UnivariateStatistic getPercentileImpl() {
        return percentileImpl;
    }

    /**
     * Sets the implementation to be used by {@link #getPercentile(double)}.
     * The supplied <code>UnivariateStatistic</code> must provide a
     * <code>setQuantile(double)</code> method; otherwise 
     * <code>IllegalArgumentException</code> is thrown.
     * 
     * @param percentileImpl the percentileImpl to set
     * @throws IllegalArgumentException if the supplied implementation does not
     *  provide a <code>setQuantile</code> method
     */
    public synchronized void setPercentileImpl(
            UnivariateStatistic percentileImpl) {
        try {
            percentileImpl.getClass().getMethod("setQuantile", 
                    new Class[] {Double.TYPE}).invoke(percentileImpl,
                            new Object[] {new Double(50.0d)});
        } catch (NoSuchMethodException e1) { 
            throw new IllegalArgumentException(
                    "Percentile implementation does not support setQuantile");
        } catch (IllegalAccessException e2) {
            throw new IllegalArgumentException(
                "IllegalAccessException setting quantile"); 
        } catch (InvocationTargetException e3) {
            throw new IllegalArgumentException(
                "Error setting quantile" + e3.toString()); 
        }
        this.percentileImpl = percentileImpl;
    }

    /**
     * @return the skewnessImpl
     */
    public synchronized UnivariateStatistic getSkewnessImpl() {
        return skewnessImpl;
    }

    /**
     * @param skewnessImpl the skewnessImpl to set
     */
    public synchronized void setSkewnessImpl(
            UnivariateStatistic skewnessImpl) {
        this.skewnessImpl = skewnessImpl;
    }

    /**
     * @return the varianceImpl
     */
    public synchronized UnivariateStatistic getVarianceImpl() {
        return varianceImpl;
    }

    /**
     * @param varianceImpl the varianceImpl to set
     */
    public synchronized void setVarianceImpl(
            UnivariateStatistic varianceImpl) {
        this.varianceImpl = varianceImpl;
    }

    /**
     * @return the sumsqImpl
     */
    public synchronized UnivariateStatistic getSumsqImpl() {
        return sumsqImpl;
    }

    /**
     * @param sumsqImpl the sumsqImpl to set
     */
    public synchronized void setSumsqImpl(UnivariateStatistic sumsqImpl) {
        this.sumsqImpl = sumsqImpl;
    }

    /**
     * @return the sumImpl
     */
    public synchronized UnivariateStatistic getSumImpl() {
        return sumImpl;
    }

    /**
     * @param sumImpl the sumImpl to set
     */
    public synchronized void setSumImpl(UnivariateStatistic sumImpl) {
        this.sumImpl = sumImpl;
    }   
}
