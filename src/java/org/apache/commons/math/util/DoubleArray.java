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
package org.apache.commons.math.util;


/**
 * Provides a single interface for dealing with various flavors
 * of double arrays.  This arrays framework follows the model of the
 * Collections API by allowing a user to select from a number of 
 * array implementations with support for various storage mechanisms
 * such as automatic expansion, contraction, and array "rolling".
 * @version $Revision: 1.10 $ $Date: 2004/05/19 14:16:32 $
 */
public interface DoubleArray {

    /**
     * Returns the number of elements currently in the array.  Please note
     * that this is different from the length of the internal storage array.  
     * @return number of elements
     */
    int getNumElements();

    /**
     * Returns the element at the specified index.  Note that if an
     * out of bounds index is supplied a ArrayIndexOutOfBoundsException 
     * will be thrown.
     * 
     * @param index index to fetch a value from
     * @return value stored at the specified index
     * @throws ArrayIndexOutOfBoundsException if <code>index</code> is less than
     *         zero or is greater than <code>getNumElements() - 1</code>.
     */
    double getElement(int index);

    /**
     * Sets the element at the specified index.  This method will expand the 
     * internal storage array to accomodate the insertion of a value at an 
     * index beyond the current capacity.
     * @param index index to store a value in
     * @param value value to store at the specified index
     * @throws ArrayIndexOutOfBoundsException if <code>index</code> is less than
     *         zero or is greater than <code>getNumElements() - 1</code>.
     */
    void setElement(int index, double value);

    /**
     * Adds an element to the end of this expandable array
     * 
     * @param value to be added to end of array
     */
    void addElement(double value);

    /**
     * Adds an element and moves the window of elements up one.  This
     * has the effect of a FIFO.  when you "roll" the array an element is 
     * removed from the array.  The return value of this function is the 
     * discarded double.
     * 
     * @param value the value to be added to the array
     * @return the value which has been discarded or "pushed" out of the array
     *         by this rolling insert.
     */
    double addElementRolling(double value);

    /**
     * Returns a double[] of elements
     *
     * @return all elements added to the array
     */
    double[] getElements();

    /**
     * Clear the double array
     */
    void clear();

}
