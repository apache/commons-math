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

import java.util.NoSuchElementException;

/**
 * Provides a fixed size implementation of the DoubleArray with
 * support to true "rolling" functionality.  If a program attempts to add
 * a value to a fixed array which has reach a maximum number of 
 * elements a ArrayIndexOutOfBoundsException will be thrown.   
 * 
 * @author <a href="mailto:tobrien@apache.org">Tim O'Brien</a>
 */
public class FixedDoubleArray implements DoubleArray {

	double[] internalArray;
	
	int size = 0;
	int nextAdd = 0;
	int maxElements = 0;

	public FixedDoubleArray(int maxElements) {
		this.maxElements = maxElements;
		internalArray = new double[maxElements];
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.DoubleArray#getNumElements()
	 */
	public int getNumElements() {
		return size;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.DoubleArray#getElement(int)
	 */
	public double getElement(int index) throws NoSuchElementException {
		if( index > (size-1) ) {
			throw new ArrayIndexOutOfBoundsException("Attempted to retrieve an element outside of" +				"the element array");
		} else {
			return internalArray[index];
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.DoubleArray#setElement(int, double)
	 */
	public void setElement(int index, double value) {
		if( index > (size-1) ) {
			throw new ArrayIndexOutOfBoundsException("Attempted to set an element outside of" +
				"the element array");
		} else {
			internalArray[index] = value;
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.DoubleArray#addElement(double)
	 */
	public void addElement(double value) {
		if( size < internalArray.length ) {
			size++;
			
			internalArray[nextAdd] = value;
			
			nextAdd++;
			nextAdd = nextAdd % (maxElements);

		} else {
			throw new ArrayIndexOutOfBoundsException("Attempted to add a value to an array of fixed size, please " +				"use addElementRolling to avoid this exception");
		}
		
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.DoubleArray#addElementRolling(double)
	 */
	public double addElementRolling(double value) {
		if( size < internalArray.length ) {
			size++;
		} 
		
		double discarded = internalArray[nextAdd];

		internalArray[nextAdd] = value;

		nextAdd++;
		nextAdd = nextAdd % maxElements;	
		
		// but we return the value which was "replaced"
		return( discarded );		
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.DoubleArray#getElements()
	 */
	public double[] getElements() {
		double[] copy = new double[internalArray.length];
		System.arraycopy(internalArray, 0, copy, 0, internalArray.length);
		return copy;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.DoubleArray#clear()
	 */
	public void clear() {
		size = 0;
		nextAdd = 0;
		internalArray = new double[maxElements];
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.DoubleArray#discardFrontElements(int)
	 */
	public void discardFrontElements(int i) {
		// TODO: AH! implemented there is not concept of "front"
		// in an array that discards values when rolling.....  anyone?
		throw new RuntimeException("Discarding front element not supported in FixedDoubleArray");
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.DoubleArray#getMin()
	 */
	public double getMin() {
		double min = internalArray[0];
		for( int i = 1; i < size; i++) {
			if( internalArray[i] < min ) {
				min = internalArray[i];
			}
		}
		return min;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.math.DoubleArray#getMax()
	 */
	public double getMax() {
		double max = internalArray[0];
		for( int i = 1; i < size; i++) {
			if( internalArray[i] > max ) {
				max = internalArray[i];
			}
		}
		return max;
	}

}
