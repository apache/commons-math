/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
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
package org.apache.commons.math;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:tobrien@apache.org">Tim O'Brien</a>
 */
public class ListUnivariateImpl extends AbstractStoreUnivariate {

	// Holds the value of the windowSize, initial windowSize is the constant
	// Univariate.INFINITE_WINDOW
	private int windowSize = Univariate.INIFINTE_WINDOW;

	// Holds a reference to a list - GENERICs are going to make
	// out lives easier here as we could only accept List<Number>
	List list;

	public ListUnivariateImpl( List list ) {
		this.list = list;
	}


	/* (non-Javadoc)
	 * @see org.apache.commons.math.StoreUnivariate#getValues()
	 */
	public double[] getValues() {

		// If we've got a windowSize, we might not care about the entire list.
		List effectiveList = list;
		
		// If the window size is not INIFINITE_WINDOW AND
		// the current list is larger that the window size, we need to
		// take into account only the last n elements of the list
		// as definied by windowSize
		if( windowSize != Univariate.INIFINTE_WINDOW &&
			windowSize < list.size() ) {
			effectiveList = list.subList( (list.size() - 1) - windowSize, (list.size()-1));
		}

		// Create an array to hold all values
		double[] copiedArray = new double[effectiveList.size()];

		int i = 0;
		Iterator it = effectiveList.iterator();
		while( it.hasNext() ) {
			Number n = (Number) it.next();
			copiedArray[i] = n.doubleValue();
			i++;
		}

		return copiedArray;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.StoreUnivariate#getElement(int)
	 */
	public double getElement(int index) {
		
		double value = Double.NaN;

		if( windowSize != Univariate.INIFINTE_WINDOW &&
			 windowSize < list.size() ) {
			Number n = (Number) list.get( ( (list.size() - 1) - windowSize ) + index ) ;
			value = n.doubleValue();
		} else {
			Number n = (Number) list.get(index);
			value = n.doubleValue();
		}
		
		return value;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.math.Univariate#getN()
	 */
	public double getN() {
		double N = 0.0;
		
		if( windowSize != Univariate.INIFINTE_WINDOW ) {
			if( list.size() > windowSize ) {
				N = windowSize;
			} else {
				N = list.size();
			}
		} else {
			N = list.size();
		}
		
		return N;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.math.Univariate#addValue(double)
	 */
	public void addValue(double v) {
		list.add( new Double(v));
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.Univariate#clear()
	 */
	public void clear() {
		list.clear();
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.Univariate#getWindowSize()
	 */
	public int getWindowSize() {
		return windowSize;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.Univariate#setWindowSize(int)
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

}
