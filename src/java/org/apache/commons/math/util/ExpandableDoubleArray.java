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
package org.apache.commons.math.util;

import java.io.Serializable;

/**
 * <p>
 * A DoubleArray implementation which automatically expands
 * an internal double array to handle an array of arbitrary length.  This 
 * implementation of DoubleArray is provided to support scenarios in
 * which the ultimate length of an array is unknown, and the 
 * developer can control the expansion process through the
 * <code>initialCapacity</code> and <code>expansionFactor</code> 
 * parameters.
 * </p>
 *
 * <p>
 * This implementation also allows a developer to:
 * <ul>
 *  <li>Resize an array via <code>setNumElements(int)</code></li>
 *  <li>Discard elements from the front of an array via
 *      <code>discardFrontElements(int)</code></li>
 *  <li>Find the minimum and maximum values stored in this array</li>
 * </ul>
 * </p>
 *
 * <p>
 * The initialCapacity parameter sets the capacity of the initial
 * storage array, and the expansionFactor is the factor by which
 * the current storage capacity is multiplied each time the internal
 * array needs to be expanded.  <b>Please note</b> that the length of the 
 * internal storage array has nothing to do with the number of elements 
 * currently stored in this array.  If one creates an instance of this
 * class with an initialCapacity of "2" and an expansion factor of "2", and
 * then adds 3546 elements to the array, this implementation will need to
 * expand the array 10 times - first from 2 -> 4. then 4 -> 8, 8 -> 16,
 * and so on until we reach 4096 which is sufficient to hold 3546 elements.
 * </p>
 * @version $Revision: 1.7 $ $Date: 2003/10/13 08:11:23 $
 */
public class ExpandableDoubleArray implements Serializable, DoubleArray {

    // TODO: expansionFactor is valuable, by if I only need storage
    // for 1025 items and I use the default settings, I'll end up
    // with enough storage for 2048 elements.  Maybe this implementation
    // should have flags for incremental growth - (i.e. when expanding, only
    // increase storage by a constant size - 100, 200 ) ?

    /** 
     * This is the internal storage array.
     */
    protected double[] internalArray;

    /** 
     * Number of elements in the array
     */
    protected int numElements = 0;

    /** 
     * Keeps track of a starting index
     */
    protected int startIndex = 0;

    /**The initial capacity of the array. 
     * Initial capacity is not exposed as a property as it is only meaningful
     * when passed to a constructor.
     */
    protected int initialCapacity = 16;

    /** The expand factor of the array.  When the array need to be expanded, 
     * the new array size will be internalArray.length * expandFactor 
     */
    protected float expansionFactor = 2.0f;

    /**
     * Create an expandable double array with the
     * default initial capactiy of 16 and an expansion factor of 2.00
     */
    public ExpandableDoubleArray() {
        internalArray = new double[initialCapacity];
    }

    /**
     * Create an expandable double array with the
     * specified initial capacity and the defult expansion factor of 2.00
     * 
     * @param initialCapacity The initial size of the internal storage array
     */
    public ExpandableDoubleArray(int initialCapacity) {
        setInitialCapacity(initialCapacity);
        internalArray = new double[this.initialCapacity];
    }

    /**
     * Create an expandable double array with the
     * specificed initial capacity and expand factor.
     * 
     * @param initialCapacity The initial size of the internal storage array
     * @param expansionFactor the array will be expanded based on this 
     *                        parameter
     */
    public ExpandableDoubleArray(int initialCapacity, float expansionFactor) {
        setInitialCapacity(initialCapacity);
        setExpansionFactor(expansionFactor);
        this.initialCapacity = initialCapacity;
        internalArray = new double[initialCapacity];
    }

    /**
     * The expansion factor controls the size of a new aray when an array 
     * needs to be expanded. When a value is inserted into a full array, the 
     * new array size is calculated as the current array size times this 
     * expansion factor.  The default expansion factor is 2.0
     * 
     * @return the expansion factor of this expandable double array
     */
    public float getExpansionFactor() {
        return expansionFactor;
    }

    /**
     * Sets the expansion factor for this expandable double array.  
     * The expansion factor will affect the next expansion of this array.
     * 
     * @param expansionFactor the expansion factor of this array
     */
    public void setExpansionFactor(float expansionFactor) {

        // The expansion factor *must* be larger than 1.0, otherwise we'll 
        // have an inconsistency upon expansion we'll start shrinking which 
        // will lead to ArrayIndexOutOfBound exceptions.
        if (expansionFactor > 1.0) {
            this.expansionFactor = expansionFactor;
        } else {
            String msg =
                "The expansion factor must be a number greater " + "than 1.0";
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Sets the initial capacity
     * @param initialCapacity of the array
     */
    public void setInitialCapacity(int initialCapacity) {
        if (initialCapacity > 0) {
            this.initialCapacity = initialCapacity;
        } else {
            String msg =
                "The initial capacity supplied: " + initialCapacity +
                "must be a positive integer";
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Returns the internal storage array
     * 
     * @return the internal storage array used by this object
     */
    public double[] getValues() {
        return (internalArray);
    }

    /**
     * Returns the starting index of the internal array.
     * @return starting index
     */
    public int start() {
        return startIndex;
    }

    /**
     * Returns the number of elements currently in the array.  Please note
     * that this is different from the length of the internal storage array.  
     * @return number of elements
     */
    public int getNumElements() {
        return (numElements);
    }

    /**
     * This function allows you to control the number of elements contained 
     * in this array, and can be used to "throw" out the last n values in an 
     * array.  This feature is mainly targetted at the subclasses of this 
     * array class.  Note that this function will also expand the internal 
     * array as needed.
     * 
     * @param i a new number of elements
     */
    public synchronized void setNumElements(int i) {

        // If index is negative thrown an error
        if (i < 0) {
            String msg =
                "Number of elements must be zero or a positive " + "integer";
            throw new IllegalArgumentException(msg);
        }

        // Test the new num elements, check to see if the array needs to be 
        // expanded to accomodate this new number of elements
        if ((startIndex + i) > internalArray.length) {
            expandTo(startIndex + i);
        }

        // Set the new number of elements to new value
        numElements = i;
    }

    /**
     * Returns the element at the specified index
     * 
     * @param index index to fetch a value from
     * @return value stored at the specified index
     */
    public double getElement(int index) {
        double value = Double.NaN;
        if (index >= numElements) {
            String msg =
                "The index specified: " + index +
                " is larger than the current number of elements";
            throw new ArrayIndexOutOfBoundsException(msg);
        } else if (index >= 0) {
            value = internalArray[startIndex + index];
        } else {
            String msg =
                "Elements cannot be retrieved from a negative array index";
            throw new ArrayIndexOutOfBoundsException(msg);
        }
        return value;
    }

    /**
     * Sets the element at the specified index.  This method will expand the 
     * internal storage array to accomodate the insertion of a value at an 
     * index beyond the current capacity.
     *
     * @param index index to store a value in
     * @param value value to store at the specified index
     */
    public synchronized void setElement(int index, double value) {

        if (index < 0) {
            String msg = "Cannot set an element at a negative index";
            throw new ArrayIndexOutOfBoundsException(msg);
        }

        if ((startIndex + index) >= internalArray.length) {
            expandTo(startIndex + (index + 1));
            numElements = index + 1;
        }
        internalArray[startIndex + index] = value;
    }

    /**
     * Expands the internal storage array to the specified size.
     * 
     * @param size Size of the new internal storage array
     */
    private synchronized void expandTo(int size) {
        double[] tempArray = new double[size];
        // Copy and swap
        System.arraycopy(internalArray, 0, tempArray, 0, internalArray.length);
        internalArray = tempArray;
    }

    /**
     * Expands the internal storage array using the expansion factor
     */
    protected synchronized void expand() {

        // notice the use of Math.ceil(), this gaurantees that we will always 
        // have an array of at least currentSize + 1.   Assume that the 
        // current initial capacity is 1 and the expansion factor
        // is 1.000000000000000001.  The newly calculated size will be 
        // rounded up to 2 after the multiplication is performed.
        int newSize = (int) Math.ceil(internalArray.length * expansionFactor);
        double[] tempArray = new double[newSize];

        // Copy and swap
        System.arraycopy(internalArray, 0, tempArray, 0, internalArray.length);
        internalArray = tempArray;
    }

    /**
     * Adds an element to the end of this expandable array
     * 
     * @param value value to be added to end of array
     */
    public synchronized void addElement(double value) {
        numElements++;
        if ((startIndex + numElements) > internalArray.length) {
            expand();
        }
        internalArray[startIndex + (numElements - 1)] = value;
    }

    /**
     * Adds an element and moves the window of elements up one.  This
     * has the effect of a FIFO.  when you "roll" the array an element is 
     * removed from the array.  The return value of this function is the 
     * discarded double.
     * @param value the value to add
     * @return the value which has been discarded or "pushed" out of the array 
     * by this rolling insert.
     */
    public synchronized double addElementRolling(double value) {
        double discarded = internalArray[startIndex];

        if ((startIndex + (numElements + 1)) > internalArray.length) {
            expand();
        }
        // Increment the start index
        startIndex += 1;

        // Add the new value
        internalArray[startIndex + (numElements - 1)] = value;

        return discarded;
    }

    /**
     * Notice the package scope on this method.   This method is simply here 
     * for the JUnit test, it allows us check if the expansion is working 
     * properly after a number of expansions.  This is not meant to be a part 
     * of the public interface of this class.
     * 
     * @return the length of the internal storage array.
     */
    int getInternalLength() {
        return (internalArray.length);
    }

    /**
     * Clear the array, reset the size to the initialCapacity and the number 
     * of elements to zero.
     */
    public synchronized void clear() {
        numElements = 0;
        internalArray = new double[initialCapacity];
    }

    /**
     * Discards values from the front of the list.  This function removes n 
     * elements from the front of the array.
     * 
     * @param i number of elements to discard from the front of the array.
     */
    public synchronized void discardFrontElements(int i) {

        if (i > numElements) {
            String msg = "Cannot discard more elements than are" +
                "contained in this array.";
            throw new IllegalArgumentException(msg);
        } else if (i < 0) {
            String msg = "Cannot discard a negative number of elements.";
            throw new IllegalArgumentException(msg);
        } else {
            // "Subtract" this number of discarded from numElements 
            numElements -= i;
            startIndex += i;
        }
    }

    /**
     * @see org.apache.commons.math.util.DoubleArray#getElements()
     */
    public double[] getElements() {
        double[] elementArray = new double[numElements];
        System.arraycopy(
            internalArray,
            startIndex,
            elementArray,
            0,
            numElements);
        return elementArray;
    }

}
