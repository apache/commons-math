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
 * Provides an interface to implemntations which function as an array
 * of double primitives.
 * 
 * @author <a href="mailto:tobrien@apache.org">Tim O'Brien</a>
 */
public interface DoubleArray {

    /**
     * Returns the number of elements currently in the array.  Please note
     * that this is different from the length of the internal storage array.  
     * @return number of elements
     */
    int getNumElements();

    //TODO: Throwing a NoSuchElementException might not be the right
    //thing to do, it may be more helpful to just throw ArrayOutOfBounds...

    /**
     * Returns the element at the specified index
     * 
     * @param index index to fetch a value from
     * @return value stored at the specified index
     * @throws NoSuchElementException exception thrown if the array index
     *         exceeds the known boundaries of this array.  
     *
     */
    double getElement(int index) throws NoSuchElementException;

    /**
     * Sets the element at the specified index.  This method will expand the 
     * internal storage array to accomodate the insertion of a value at an 
     * index beyond the current capacity.
     * @param index index to store a value in
     * @param value value to store at the specified index
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

    /**
     * Discards values from the front of the list.  This function removes n 
     * elements from the front of the array.
     * 
     *@param i number of elements to discard from the front of the array.
     */
    void discardFrontElements(int i);

    /**
     * Returns the minimum value stored in this array
     *
     * @return minimum value contained in this array
     */
    double getMin();

    /**
     * Returns the maximum value stored in this array
     *
     * @return maximum value contained in this array
     */
    double getMax();
}
