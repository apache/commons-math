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

import java.io.Serializable;

/**
 * <p>
 * A variable length double array implementation and extension of 
 * ExpandableDoubleArray which automatically handles expanding and
 * contracting double arrays.
 * </p>
 *
 * <p>
 * This class extends the functionality of ExpandableDoubleArray and
 * inherits the expansion parameters from that class.  If a developer
 * instantiates a ContractableDoubleArray and only adds values to
 * that instance, the behavior of this class is no different from
 * the behavior of the super-class ExpandableDoubleArray.  If, on the
 * other hand, elements are removed from the array, this implementation
 * tests an additional parameter <code>contractionCriteria</code>.  The
 * <code>contractionCriteria</code> dictates when this implementation
 * will contract the size of the internal storage array to
 * the number of elements + 1.  This check is performed after every
 * operation that alters the number of elements in the array.
 * </p>
 *
 * <p>
 * Note that the contractionCriteria must always be greater than the
 * expansionFactor.  If this were not the case (assume a 
 * contractionCriteria of 1.5f and a expansionFactor of 2.0f) an
 * endless series of expansions and contractions would occur.  If the 
 * length of this array is highly varied over time it is a good idea
 * to trade efficient memory usage for performance.  Each time an array
 * is expanded or contracted the meaningful portions of the internal
 * storage array are copied to a new array and the reference to the 
 * internal storage array is swapped.
 * </p>
 * 
 * @version $Revision: 1.12 $ $Date: 2004/02/21 21:35:16 $
 */
public class ContractableDoubleArray extends ExpandableDoubleArray implements Serializable {

    static final long serialVersionUID = -3485529955529426875L; 
   
    /** The contraction criteria defines the conditions under which this
     * object will "resize" the internal array to the number of elements
     * contained in the element array + 1
     */
    private float contractionCriteria = 2.5f;

    /**
     * Create an expandable double array with the default initial capacity of 
     * 16, an expansion factor of 2.00, and a contractionCriteria of 2.5
     */
    public ContractableDoubleArray() {
        super();
    }

    /**
     * Create an expandable double array with the specified initial capacity, 
     * the defult expansion factor of 2.00, and a contractionCriteria of 2.5
     * 
     * @param initialCapacity The initial size of the internal storage array
     */
    public ContractableDoubleArray(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Create an expandable double array with the specificed initial capacity 
     * and expand factor, with a contractionCriteria of 2.5
     * 
     * @param initialCapacity The initial size of the internal storage array
     * @param expansionFactor the array will be expanded based on this 
     *                        parameter
     */
    public ContractableDoubleArray(
        int initialCapacity,
        float expansionFactor) {
        this.expansionFactor = expansionFactor;
        setInitialCapacity(initialCapacity);
        internalArray = new double[initialCapacity];
        checkContractExpand(getContractionCriteria(), expansionFactor);
    }

    /**
     * Create an expandable double array with the
     * specificed initial capacity, expand factor, and contractionCriteria
     * 
     * @param initialCapacity The initial size of the internal storage array
     * @param expansionFactor the array will be expanded based on this 
     *                        parameter
     * @param contractionCriteria The contraction Criteria.
     */
    public ContractableDoubleArray(
        int initialCapacity,
        float expansionFactor,
        float contractionCriteria) {
        this.contractionCriteria = contractionCriteria;
        this.expansionFactor = expansionFactor;
        setInitialCapacity(initialCapacity);
        internalArray = new double[initialCapacity];
        checkContractExpand(contractionCriteria, expansionFactor);
    }

    /**
     * Contracts the storage array to the (size of the element set) + 1 - to 
     * avoid a zero length array. This function also resets the startIndex to 
     * zero. 
     */
    public synchronized void contract() {
        double[] tempArray = new double[numElements + 1];

        // Copy and swap - copy only the element array from the src array.
        System.arraycopy(internalArray, startIndex, tempArray, 0, numElements);
        internalArray = tempArray;

        // Reset the start index to zero
        startIndex = 0;
    }

    /**
     * Adds an element to the end of this expandable array
     * 
     * @param value to be added to end of array
     */
    public synchronized void addElement(double value) {
        super.addElement(value);
        if (shouldContract()) {
            contract();
        }
    }

    /**
     * <p>
     * Adds an element to the end of this expandable array and 
     * discards a value from the front of the array.  This method
     * has the effect of adding a value to the end of the list
     * and discarded an element from the front of the list.
     * </p>
     *
     * <p>
     * When an array rolls it actually "scrolls" the element array in 
     * the internal storage array.  An element is added to the end of the
     * array, and the first element of the array is discard by incrementing
     * the starting index of the element array within the internal
     * storage array.  Over time this will create an orphaned prefix
     * to the element array within the internal storage array.  If this
     * function is called frequently, this orphaned prefix list will
     * gradually push the internal storage vs. element storage to
     * the contractionCriteria.
     * </p>
     * @param value to be added to end of array
     * @return value added
     */
    public synchronized double addElementRolling(double value) {
        double discarded = super.addElementRolling(value);
        // Check the contraction criteria
        if (shouldContract()) {
            contract();
        }
        return discarded;
    }

    /**
     * Should contract returns true if the ratio of (internal storage length) 
     * to (number of elements) is larger than the contractionCriteria value.  
     * In other words, using the default value of 2.5, if the internal storage
     * array provides more than 2.5x the space needed to store numElements, 
     * then this function returns true
     * 
     * @return true if array satisfies the contraction criteria
     */
    private synchronized boolean shouldContract() {
        boolean shouldContract = false;
        if ((internalArray.length / numElements) > contractionCriteria) {
            shouldContract = true;
        }
        return shouldContract;
    }

    /**
     * @see org.apache.commons.math.util.DoubleArray#setElement(int, double)
     */
    public synchronized void setElement(int index, double value) {
        super.setElement(index, value);
        if (shouldContract()) {
            contract();
        }
    }

    /**
     * Method invokes the super class' setExpansionFactor but first it
     * must validate the combination of expansionFactor and 
     * contractionCriteria.
     *
     * @see org.apache.commons.math.util.ExpandableDoubleArray#setExpansionFactor(float)
     */
    public void setExpansionFactor(float expansionFactor) {
        checkContractExpand(getContractionCriteria(), expansionFactor);
        super.setExpansionFactor(expansionFactor);
    }

    /**
     * The contraction criteria defines when the internal array will contract 
     * to store only the number of elements in the element array.  This 
     * contractionCriteria gaurantees that the internal storage array will 
     * never exceed this factor more than the space needed to store 
     * numElements.
     * 
     * @return the contraction criteria used to reclaim memory when array is 
     *         empty
     */
    public float getContractionCriteria() {
        return contractionCriteria;
    }

    /**
     * Sets the contraction criteria for this ExpandContractDoubleArray. 
     * 
     * @param contractionCriteria contraction criteria
     */
    public void setContractionCriteria(float contractionCriteria) {
        checkContractExpand(contractionCriteria, getExpansionFactor());

        this.contractionCriteria = contractionCriteria;
    }

    /**
     * Checks the expansion factor and the contraction criteria and throws an 
     * IllegalArgumentException if the contractionCriteria is less than the 
     * expansionCriteria
     * 
     * @param expansionFactor factor to be checked
     * @param contractionCritera critera to be checked
     */
    protected void checkContractExpand(
        float contractionCritera,
        float expansionFactor) {

        if (contractionCritera < expansionFactor) {
            String msg =
                "Contraction criteria can never be smaller than " +
                "the expansion factor.  This would lead to a never " +
                "ending loop of expansion and contraction as a newly " +
                "expanded internal storage array would immediately " +
                "satisfy the criteria for contraction";
            throw new IllegalArgumentException(msg);
        }

        if (contractionCriteria <= 1.0) {
            String msg =
                "The contraction criteria must be a number larger " +
                "than one.  If the contractionCriteria is less than or " +
                "equal to one an endless loop of contraction and " +
                "expansion would ensue as an internalArray.length " +
                "== numElements would satisfy the contraction criteria";
            throw new IllegalArgumentException(msg);
        }

        if (expansionFactor < 1.0) {
            String msg =
                "The expansion factor must be a number greater than 1.0";
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * @see org.apache.commons.math.util.ExpandableDoubleArray#discardFrontElements(int)
     */
    public synchronized void discardFrontElements(int i) {
        super.discardFrontElements(i);
        if (shouldContract()) {
            contract();
        }
    }
}
