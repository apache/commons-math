/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.stat;

import java.io.Serializable;

import java.util.Arrays;

import org.apache.commons.math.stat.univariate.UnivariateStatistic;
import org.apache.commons.math.util.ContractableDoubleArray;

/**
 * @version $Revision: 1.3 $ $Date: 2004/01/25 21:30:41 $
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
     */
    public DescriptiveStatisticsImpl(int window) {
    	super(window);
    	eDA = new ContractableDoubleArray();
    }

    public int getWindowSize() {
    	return windowSize;
    }
    
    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getValues()
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
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getSortedValues()
     */
    public double[] getSortedValues() {
    	double[] sort = getValues();
    	Arrays.sort(sort);
    	return sort;
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getElement(int)
     */
    public double getElement(int index) {
        return eDA.getElement(index);
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#getN()
     */
    public long getN() {
        return eDA.getNumElements();
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#addValue(double)
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
     * @see org.apache.commons.math.stat.DescriptiveStatistics#clear()
     */
    public synchronized void clear() {
        eDA.clear();
    }

    /**
     * @see org.apache.commons.math.stat.DescriptiveStatistics#setWindowSize(int)
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