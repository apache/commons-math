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

/**
 * <p>
 * Provides an implementation of the DoubleArray with a maximum number of
 * elements.  Creating an array implementation with an upper limit on the
 * number of elements allows us to support a more efficient "rolling" 
 * mechanism to support addElementRoling(double). Please note that this
 * implementation will not preserve the order of the values supplied to
 * this array, calling getValues() will return an array of indeterminate
 * order.
 * </p>
 * 
 * <p>
 * Values are added to this array by calling addElement(double) or 
 * addElementRolling(double).  If addElement(double) is called on 
 * an array that already contains the maximum number of elements, an
 * ArrayIndexOutOfBoundsException will be thrown to reflect an attempt to
 * add a value beyond the boundaries of the fixed length array - in this
 * respect a FixedDoubleArray can be considered "full".  Calling 
 * addElementRolling(double) on an array which contains the maximum
 * number of elements will cause the array to overwrite the "oldest"
 * value in the array.
 * </p>
 *
 * <p>
 * This class is called FixedDoubleArray not because it is of a fixed size.
 * The name is appropriate because the internal storage array remains 
 * "fixed" in memory, this implementation will never allocate, or copy
 * the internal storage array to a new array instance.
 * </p>
 * @version $Revision: 1.8 $ $Date: 2003/10/13 08:11:23 $
 */
public class FixedDoubleArray implements DoubleArray {

    /**
     * This is the internal storage array.  This array is assigned
     * a known fixed size in the constructor
     */
    private double[] internalArray;

    /**
     * Size determined the number of elements in the array at
     * any given time. When an array is created is maxElements
     * of 100, it is of size 0, and size increases as values are
     * added.
     */
    private int size = 0;

    /**
     * This index points to the location of the next update.  Next
     * add, cycles from 0 to (maxElement-1)
     */
    private int nextAdd = 0;
 
    /**
     * The maximum number of elements in the FixedDoubleArray
     */
    private int maxElements = 0;

    /**
     * Create a fixed array for double primitives which can hold up to
     * <code>maxElements</codec> doubles.  This implementation of 
     * DoubleArray was created to provide a more "performance-oriented"
     * in-place rolling mechanism for calculations which need to
     * operate on a rolling window of values.
     *
     * @param maxElements the maximum number of elements this 
     *        FixeddoubleArray may contain.
     */
    public FixedDoubleArray(int maxElements) {
        this.maxElements = maxElements;
        internalArray = new double[maxElements];
    }

    /**
     * Create a fixed array backed by the provided double[] implementation. 
     * the array should have all the elements occupied. the size and maxElements
     * are drawn from the array's length.
     * 
     * This implementation of DoubleArray was created to provide a more 
     * "performance-oriented" in-place rolling mechanism for calculations 
     * which need to operate on a rolling window of values.
     * @param array the backing array
     */
    public FixedDoubleArray(double[] array) {
        this.maxElements = array.length;
        this.size = array.length;
        internalArray = array;
    }

    /**
     * Retrieves the current size of the array.
     * @see org.apache.commons.math.util.DoubleArray#getNumElements()
     */
    public int getNumElements() {
        return size;
    }

    /**
     * Returns the element value at the specified index.  Please note that
     * the size of the element array is not directly related to the 
     * maximum number of elements which this array can contain.  One can
     * create an instance of FixedDoubleArray with a maximum of
     * ten elements, add three items, and get any items from index 0 to index
     * 2 - trying to retrieve an element outside of the current element
     * array will throw an ArrayIndexOutOfBoundsException.
     *
     * @see org.apache.commons.math.util.DoubleArray#getElement(int)
     */
    public double getElement(int index) {
        if (index > (size - 1)) {
            String msg =
                "Attempted to retrieve an element outside of " +
                "the element array";
            throw new ArrayIndexOutOfBoundsException(msg);
        } else {
            // Return the element requested, if the index supplied
            // is negative this statement may also throw an
            // ArrayIndexOutOfBoundException.
            return internalArray[index];
        }
    }

    /**
     * <p>
     * Sets the element at the specified index to the value supplied.
     * </p>
     *
     * <p>Implementation Notes:
     * <ul>
     *  This implementation will not expand the array to the specified
     *  size.  Unlike the expandable double array implementation calling
     *  setElement(10, 3.0) on an array with 5 elements will throw an
     *  ArrayIndexOutOfBoundsException.
     * </ul>
     * <ul>
     *  The number of elements in an array corresponds to the number
     *  of elements that have been added to this FixedDoubleArray.  This
     *  is not the same as the maximum number of elements which can be
     *  contained in this array.  A FixedDoubleArray instance can be
     *  created with a maximum upper limit of 10 elements, until 10
     *  elements have been added to this array, the size of the array
     *  reflects the number of elements added.
     * </ul>
     * </p>
     *
     * @see org.apache.commons.math.util.DoubleArray#setElement(int, double)
     */
    public void setElement(int index, double value) {
        if (index > (size - 1)) {
            String msg =
                "Attempted to set an element outside of" + "the element array";
            throw new ArrayIndexOutOfBoundsException(msg);
        } else {
            internalArray[index] = value;
        }
    }

    /** 
     * Add an element to the current array, testing to see if 
     * this array has already met or exceeded the maximum number
     * of elements
     *
     * @see org.apache.commons.math.util.DoubleArray#addElement(double)
     */
    public void addElement(double value) {
        if (size < internalArray.length) {
            size++;

            internalArray[nextAdd] = value;

            // Incremenet nextAdd and then modulo it against maxElements
            // this has the effect of repeatedly "cycling" nextAdd
            // between 0 and (maxElements-1) endlessly.
            nextAdd++;
            nextAdd = nextAdd % (maxElements);

        } else {
            // If the array has ALREADY reached the maximum size allowable,
            // we throw an ArrayIndexOutOfBoundsException - the end-user
            // is trying to add an element beyond the boundaries of the
            // fixed array.
            String msg =
                "Attempted to add a value to an array of fixed " +
                "size, please use addElementRolling " +
                "to avoid this exception";
            throw new ArrayIndexOutOfBoundsException(msg);
        }
    }

    /**
     * <p>
     * Adds an element by "rolling" the new value into the current array 
     * while discarding the element which was added <code>maxElement</code>
     * add operations ago.  The value replaced is returned from this 
     * method.  Until an array contains the maximum number of element, this
     * method has the same result as the addElement(double) operation.  Once
     * the maximum number of elements has been reached this implementation
     * inserts the new values starting at index 0 of the internal storage 
     * array.  This allows for efficient rolling, but prevents us from 
     * preserving the order of the added values.
     * </p>
     *
     * <p>
     * <b>Note:</b> This function will return <code>Double.NaN</code> if
     * no value has been discarded in this roll.  This can happen when
     * the array has not met the size limitation introduced in the 
     * constructor.
     * </p>
     * @param value the value to be added to the array
     * @return Returns the value which a has been "removed" from the 
     *         database.  <b>Important:</b> If the element array has
     *         not reached the maximum size, then it is possible that
     *         no element will be discarded from a given roll.  In this
     *         case this method will return a <code>Double.NaN</code> value.
     *
     * @see org.apache.commons.math.util.DoubleArray#addElementRolling(double)
     */
    public double addElementRolling(double value) {

        // Create the discarded primitive.  If no element is
        // discarded by this roll, this method will return a
        // Double.NaN value.
        double discarded = Double.NaN;

        if (size < internalArray.length) {
            size++;
        } else {
            // If we've reached the length of the internal
            // storage array, we have to start "discarding"
            // values from the original array.

            // Obtain the value discarded by this overwrite
            discarded = internalArray[nextAdd];
        }

        internalArray[nextAdd] = value;

        // nextAdd cycles between 0 and (maxElements-1).
        nextAdd++;
        nextAdd = nextAdd % maxElements;

        // but we return the value which was "replaced"
        return (discarded);
    }

    /**
     * Provides an array of double[] which contain the
     * number of elements added to this array.  This  
     * method will return an array from zero to maxElements in length.
     * 
     * @return The array of elements added to this DoubleArray
     *         implementation.
     * @see org.apache.commons.math.util.DoubleArray#getElements()
     */
    public double[] getElements() {
        double[] copy = new double[size];
        System.arraycopy(internalArray, 0, copy, 0, size);
        return copy;
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
     * The starting index in the InternalArray.
     * @return starting index.
     */
    public int start() {
        return 0;
    }

    /**
     * Clear the array - drop all the data and start with a blank
     * internal array.  This implementation takes care of
     * setting the size of the array back to zero, and reinitializing
     * the internal storage array.
     *
     * @see org.apache.commons.math.util.DoubleArray#clear()
     */
    public void clear() {
        size = 0;
        nextAdd = 0;
        internalArray = new double[maxElements];
    }

}
