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
package org.apache.commons.math4.analysis.differentiation.finite;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.util.Pair;

// TODO: Move to util?

/**
 * Helper class for row-major performing row-major iteration of a
 * <i>n</i>-dimensional tensor.
 * 
 * @since 4.0
 */
public class RowMajorIteration implements Iterable<Pair<int[], Integer>> {
    
    /**
     * The lengths.
     */
    private final int[] lengths;
    
    /**
     * The length of a row-major array needed to store a tensor with the
     * specified lengths.
     */
    private final int rowMajorLength;

    /**
     * Constructor.
     * 
     * @param lengths The array lengths.
     */
    public RowMajorIteration(final int... lengths) {
	if(lengths == null) {
	    throw new NullArgumentException();
	}
	
	if(lengths.length == 0) {
	    throw new MathIllegalArgumentException(LocalizedFormats.DIMENSION, 0);
	}
	
	this.lengths = lengths.clone();

	// calculate the total length in row-major space.
	int length = 1;
	for(int l : lengths) {
	    if(l <= 0) {
		throw new MathIllegalArgumentException(LocalizedFormats.DIMENSION, l); 
	    }
	    
	    length *= l;
	}	
	
	rowMajorLength = length;
    }
    
    /**
     * Gets an iterator.
     * <p>
     * The returned iterator does not support {@link Iterator#remove()}.
     * 
     * @return An iterator.
     * @see Iterable#iterator()
     */
    @Override
    public Iterator<Pair<int[], Integer>> iterator() {
	return new RowMajorIterator();
    }
    
    /**
     * Core iterator implementation.
     */
    private final class RowMajorIterator 
    	implements Iterator<Pair<int[], Integer>> {
	
	/**
	 * The index.
	 */
	private final int[] iteratorIndex;
	
	/**
	 * The index.
	 */
	private int iteratorRowMajorIndex;
	
	/**
	 * Constructor.
	 */
	RowMajorIterator() {	    
	    // a bit of a hack to simulate "iteratorIndex++"
	    this.iteratorIndex = new int[lengths.length];
	    this.iteratorIndex[(iteratorIndex.length - 1)] = -1;
	    this.iteratorRowMajorIndex = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
	    return (iteratorRowMajorIndex < rowMajorLength);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pair<int[], Integer> next() {
	    
	    if(hasNext() == false) {
		throw new NoSuchElementException();
	    }
	    
	    // increment the row-major/multidimensional index.
	    for(int index = (iteratorIndex.length - 1); index >= 0; index--) {
		iteratorIndex[index] += 1;
		
		if(iteratorIndex[index] == lengths[index]) {
		    iteratorIndex[index] = 0;
		} else {
		    break;
		}
	    }
	    
	    return new Pair<int[], Integer>(iteratorIndex.clone(), iteratorRowMajorIndex++);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove() {
	    throw new UnsupportedOperationException();
	}
		
    }

}
