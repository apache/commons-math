/*
 * Copyright 2003-2004 The Apache Software Foundation.
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
package org.apache.commons.math.stat.univariate;

import java.io.Serializable;

import java.util.Arrays;

import org.apache.commons.math.util.ContractableDoubleArray;

/**
 * Default implementation of
 * {@link org.apache.commons.math.stat.univariate.DescriptiveStatistics}.
 * 
 * @version $Revision: 1.3 $ $Date: 2004/04/26 19:15:48 $
 */
public class DescriptiveStatisticsImpl extends AbstractDescriptiveStatistics implements Serializable {

	/** hold the window size **/
	protected int windowSize = INFINITE_WINDOW;
    
    /** 
     *  Stored data values
     */
    protected ContractableDoubleArray eDA;

    /**
     * Construct a DescriptiveStatisticsImpl with infinite window
     */
    public DescriptiveStatisticsImpl() {
    	super();
        eDA = new ContractableDoubleArray();
    }
    
    /**
     * Construct a DescriptiveStatisticsImpl with finite window
     * @param window the finite window size.
     */
    public DescriptiveStatisticsImpl(int window) {
    	super(window);
    	eDA = new ContractableDoubleArray();
    }

    /**
     * Access the window size.
     * @return the current window size.
     */
    public int getWindowSize() {
    	return windowSize;
    }
    
    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getValues()
     */
    public double[] getValues() {

        double[] copiedArray = new double[eDA.getNumElements()];
        System.arraycopy(
            eDA.getElements(),
            0,
            copiedArray,
            0,
            eDA.getNumElements());
        return copiedArray;
    }
    
    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getSortedValues()
     */
    public double[] getSortedValues() {
    	double[] sort = getValues();
    	Arrays.sort(sort);
    	return sort;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getElement(int)
     */
    public double getElement(int index) {
        return eDA.getElement(index);
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#getN()
     */
    public long getN() {
        return eDA.getNumElements();
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#addValue(double)
     */
    public synchronized void addValue(double v) {
        if (windowSize != INFINITE_WINDOW) {
            if (getN() == windowSize) {
                eDA.addElementRolling(v);
            } else if (getN() < windowSize) {
                eDA.addElement(v);
            } else {
                String msg =
                    "A window Univariate had more element than " +
                    "the windowSize.  This is an inconsistent state.";
                throw new RuntimeException(msg);
            }
        } else {
            eDA.addElement(v);
        }
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#clear()
     */
    public synchronized void clear() {
        eDA.clear();
    }

    /**
     * @see org.apache.commons.math.stat.univariate.DescriptiveStatistics#setWindowSize(int)
     */
    public synchronized void setWindowSize(int windowSize) {
        this.windowSize = windowSize;

        // We need to check to see if we need to discard elements
        // from the front of the array.  If the windowSize is less than 
        // the current number of elements.
        if (windowSize < eDA.getNumElements()) {
            eDA.discardFrontElements(eDA.getNumElements() - windowSize);
        }
    }

    /**
     * Apply the given statistic to this univariate collection.
     * @param stat the statistic to apply
     * @return the computed value of the statistic.
     */
    public double apply(UnivariateStatistic stat) {
        if (eDA != null) {
            return stat.evaluate(eDA.getValues(), eDA.start(), eDA.getNumElements());
        }
        return Double.NaN;
    }
}