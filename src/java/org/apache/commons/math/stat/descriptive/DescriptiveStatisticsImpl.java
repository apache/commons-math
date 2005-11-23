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
package org.apache.commons.math.stat.descriptive;

import java.io.Serializable;

import org.apache.commons.math.util.ResizableDoubleArray;

/**
 * Default implementation of
 * {@link org.apache.commons.math.stat.descriptive.DescriptiveStatistics}.
 *
 * @version $Revision$ $Date$
 */
public class DescriptiveStatisticsImpl extends DescriptiveStatistics implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -1868088725461221010L;
    
    /** hold the window size **/
    protected int windowSize;
    
    /** 
     *  Stored data values
     */
    protected ResizableDoubleArray eDA;

    /**
     * Construct a DescriptiveStatisticsImpl with infinite window
     */
    public DescriptiveStatisticsImpl() {
        this(INFINITE_WINDOW);
    }
    
    /**
     * Construct a DescriptiveStatisticsImpl with finite window
     * @param window the finite window size.
     */
    public DescriptiveStatisticsImpl(int window) {
        super();
        eDA = new ResizableDoubleArray();
        setWindowSize(window);
    }

    /**
     * Access the window size.
     * @return the current window size.
     */
    public int getWindowSize() {
        return windowSize;
    }
    
    /**
     * @see org.apache.commons.math.stat.descriptive.DescriptiveStatistics#getValues()
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
     * @see org.apache.commons.math.stat.descriptive.DescriptiveStatistics#getElement(int)
     */
    public double getElement(int index) {
        return eDA.getElement(index);
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.DescriptiveStatistics#getN()
     */
    public long getN() {
        return eDA.getNumElements();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.DescriptiveStatistics#addValue(double)
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
     * @see org.apache.commons.math.stat.descriptive.DescriptiveStatistics#clear()
     */
    public void clear() {
        eDA.clear();
    }

    /**
     * @see org.apache.commons.math.stat.descriptive.DescriptiveStatistics#setWindowSize(int)
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
     * Apply the given statistic to this univariate collection.
     * @param stat the statistic to apply
     * @return the computed value of the statistic.
     */
    public double apply(UnivariateStatistic stat) {
        return stat.evaluate(eDA.getValues(), eDA.start(), eDA.getNumElements());
    }
}
