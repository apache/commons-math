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
package org.apache.commons.math4.legacy.stat.descriptive;

import java.util.Arrays;

import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.MathIllegalStateException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.math4.legacy.core.MathArrays;

/**
 * A variable length {@link DoubleArray} implementation that automatically
 * handles expanding and contracting its internal storage array as elements
 * are added and removed.
 * <p>
 * The internal storage array starts with capacity determined by the
 * {@code initialCapacity} property, which can be set by the constructor.
 * The default initial capacity is 16.  Adding elements using
 * {@link #addElement(double)} appends elements to the end of the array.
 * When there are no open entries at the end of the internal storage array,
 * the array is expanded.  The size of the expanded array depends on the
 * {@code expansionMode} and {@code expansionFactor} properties.
 * The {@code expansionMode} determines whether the size of the array is
 * multiplied by the {@code expansionFactor}
 * ({@link ExpansionMode#MULTIPLICATIVE}) or if the expansion is additive
 * ({@link ExpansionMode#ADDITIVE} -- {@code expansionFactor} storage
 * locations added).
 * The default {@code expansionMode} is {@code MULTIPLICATIVE} and the default
 * {@code expansionFactor} is 2.
 * <p>
 * The {@link #addElementRolling(double)} method adds a new element to the end
 * of the internal storage array and adjusts the "usable window" of the
 * internal array forward by one position (effectively making what was the
 * second element the first, and so on).  Repeated activations of this method
 * (or activation of {@link #discardFrontElements(int)}) will effectively orphan
 * the storage locations at the beginning of the internal storage array.  To
 * reclaim this storage, each time one of these methods is activated, the size
 * of the internal storage array is compared to the number of addressable
 * elements (the {@code numElements} property) and if the difference
 * is too large, the internal array is contracted to size
 * {@code numElements + 1}.  The determination of when the internal
 * storage array is "too large" depends on the {@code expansionMode} and
 * {@code contractionFactor} properties.  If  the {@code expansionMode}
 * is {@code MULTIPLICATIVE}, contraction is triggered when the
 * ratio between storage array length and {@code numElements} exceeds
 * {@code contractionFactor.}  If the {@code expansionMode}
 * is {@code ADDITIVE}, the number of excess storage locations
 * is compared to {@code contractionFactor}.
 * <p>
 * To avoid cycles of expansions and contractions, the
 * {@code expansionFactor} must not exceed the {@code contractionFactor}.
 * Constructors and mutators for both of these properties enforce this
 * requirement, throwing a {@code MathIllegalArgumentException} if it is
 * violated.
 * <p>
 * <b>Note:</b> this class is <b>NOT</b> thread-safe.
 */
class ResizableDoubleArray implements DoubleArray { // Not in public API.
    /** Default value for initial capacity. */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    /** Default value for array size modifier. */
    private static final double DEFAULT_EXPANSION_FACTOR = 2.0;
    /** Default value for expansion mode. */
    private static final ExpansionMode DEFAULT_EXPANSION_MODE = ExpansionMode.MULTIPLICATIVE;
    /**
     * Default value for the difference between {@link #contractionCriterion}
     * and {@link #expansionFactor}.
     */
    private static final double DEFAULT_CONTRACTION_DELTA = 0.5;

    /**
     * The contraction criteria determines when the internal array will be
     * contracted to fit the number of elements contained in the element
     *  array + 1.
     */
    private final double contractionCriterion;

    /**
     * The expansion factor of the array.  When the array needs to be expanded,
     * the new array size will be {@code internalArray.length * expansionFactor}
     * if {@code expansionMode} is set to MULTIPLICATIVE, or
     * {@code internalArray.length + expansionFactor} if
     * {@code expansionMode} is set to ADDITIVE.
     */
    private final double expansionFactor;

    /**
     * Determines whether array expansion by {@code expansionFactor}
     * is additive or multiplicative.
     */
    private final ExpansionMode expansionMode;

    /**
     * The internal storage array.
     */
    private double[] internalArray;

    /**
     * The number of addressable elements in the array.  Note that this
     * has nothing to do with the length of the internal storage array.
     */
    private int numElements;

    /**
     * The position of the first addressable element in the internal storage
     * array.  The addressable elements in the array are
     * {@code internalArray[startIndex],...,internalArray[startIndex + numElements - 1]}.
     */
    private int startIndex;

    /**
     * Specification of expansion algorithm.
     * @since 3.1
     */
    public enum ExpansionMode {
        /** Multiplicative expansion mode. */
        MULTIPLICATIVE,
        /** Additive expansion mode. */
        ADDITIVE
    }

    /**
     * Creates an instance with default properties.
     * <ul>
     *  <li>{@code initialCapacity = 16}</li>
     *  <li>{@code expansionMode = MULTIPLICATIVE}</li>
     *  <li>{@code expansionFactor = 2.0}</li>
     *  <li>{@code contractionCriterion = 2.5}</li>
     * </ul>
     */
    ResizableDoubleArray() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates an instance with the specified initial capacity.
     * <p>
     * Other properties take default values:
     * <ul>
     *  <li>{@code expansionMode = MULTIPLICATIVE}</li>
     *  <li>{@code expansionFactor = 2.0}</li>
     *  <li>{@code contractionCriterion = 2.5}</li>
     * </ul>
     * @param initialCapacity Initial size of the internal storage array.
     * @throws MathIllegalArgumentException if {@code initialCapacity <= 0}.
     */
    ResizableDoubleArray(int initialCapacity) throws MathIllegalArgumentException {
        this(initialCapacity, DEFAULT_EXPANSION_FACTOR);
    }

    /**
     * Creates an instance from an existing {@code double[]} with the
     * initial capacity and numElements corresponding to the size of
     * the supplied {@code double[]} array.
     * <p>
     * If the supplied array is null, a new empty array with the default
     * initial capacity will be created.
     * The input array is copied, not referenced.
     * Other properties take default values:
     * <ul>
     *  <li>{@code expansionMode = MULTIPLICATIVE}</li>
     *  <li>{@code expansionFactor = 2.0}</li>
     *  <li>{@code contractionCriterion = 2.5}</li>
     * </ul>
     *
     * @param initialArray initial array
     * @since 2.2
     */
    ResizableDoubleArray(double[] initialArray) {
        this(initialArray == null || initialArray.length == 0 ?
              DEFAULT_INITIAL_CAPACITY :
              initialArray.length,
             DEFAULT_EXPANSION_FACTOR,
             DEFAULT_CONTRACTION_DELTA + DEFAULT_EXPANSION_FACTOR,
             DEFAULT_EXPANSION_MODE,
             initialArray);
    }

    /**
     * Creates an instance with the specified initial capacity
     * and expansion factor.
     * <p>
     * The remaining properties take default values:
     * <ul>
     *  <li>{@code expansionMode = MULTIPLICATIVE}</li>
     *  <li>{@code contractionCriterion = 0.5 + expansionFactor}</li>
     * </ul>
     * <p>
     * Throws MathIllegalArgumentException if the following conditions
     * are not met:
     * <ul>
     *  <li>{@code initialCapacity > 0}</li>
     *  <li>{@code expansionFactor > 1}</li>
     * </ul>
     *
     * @param initialCapacity Initial size of the internal storage array.
     * @param expansionFactor The array will be expanded based on this parameter.
     * @throws MathIllegalArgumentException if parameters are not valid.
     * @since 3.1
     */
    ResizableDoubleArray(int initialCapacity, double expansionFactor) throws MathIllegalArgumentException {
        this(initialCapacity, expansionFactor, DEFAULT_CONTRACTION_DELTA + expansionFactor);
    }

    /**
     * Creates an instance with the specified initial capacity,
     * expansion factor, and contraction criteria.
     * <p>
     * The expansion mode will default to {@code MULTIPLICATIVE}.
     * <p>
     * Throws MathIllegalArgumentException if the following conditions
     * are not met:
     * <ul>
     *  <li>{@code initialCapacity > 0}</li>
     *  <li>{@code expansionFactor > 1}</li>
     *  <li>{@code contractionCriterion >= expansionFactor}</li>
     * </ul>
     *
     * @param initialCapacity Initial size of the internal storage array.
     * @param expansionFactor The array will be expanded based on this parameter.
     * @param contractionCriterion Contraction criterion.
     * @throws MathIllegalArgumentException if the parameters are not valid.
     * @since 3.1
     */
    ResizableDoubleArray(int initialCapacity, double expansionFactor, double contractionCriterion)
        throws MathIllegalArgumentException {
        this(initialCapacity, expansionFactor, contractionCriterion, DEFAULT_EXPANSION_MODE);
    }

    /**
     * Creates an instance with the specified properties.
     * <br>
     * Throws MathIllegalArgumentException if the following conditions
     * are not met:
     * <ul>
     *  <li>{@code initialCapacity > 0}</li>
     *  <li>{@code expansionFactor > 1}</li>
     *  <li>{@code contractionCriterion >= expansionFactor}</li>
     * </ul>
     *
     * @param initialCapacity Initial size of the internal storage array.
     * @param expansionFactor The array will be expanded based on this parameter.
     * @param contractionCriterion Contraction criteria.
     * @param expansionMode Expansion mode.
     * @param data Initial contents of the array.
     * @throws MathIllegalArgumentException if the parameters are not valid.
     * @throws NullArgumentException if expansionMode is null
     */
    ResizableDoubleArray(int initialCapacity,
                         double expansionFactor,
                         double contractionCriterion,
                         ExpansionMode expansionMode,
                         double ... data)
        throws MathIllegalArgumentException {
        if (initialCapacity <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.INITIAL_CAPACITY_NOT_POSITIVE,
                                                   initialCapacity);
        }
        checkContractExpand(contractionCriterion, expansionFactor);
        NullArgumentException.check(expansionMode);

        this.expansionFactor = expansionFactor;
        this.contractionCriterion = contractionCriterion;
        this.expansionMode = expansionMode;
        internalArray = new double[initialCapacity];
        numElements = 0;
        startIndex = 0;

        if (data != null && data.length > 0) {
            addElements(data);
        }
    }

    /**
     * Copy constructor.
     * <p>
     * Creates a new ResizableDoubleArray that is a deep, fresh copy of the original.
     * Original may not be null; otherwise a {@link NullArgumentException} is thrown.
     *
     * @param original array to copy
     * @exception NullArgumentException if original is null
     * @since 2.0
     */
    ResizableDoubleArray(final ResizableDoubleArray original)
        throws NullArgumentException {
        NullArgumentException.check(original);
        this.contractionCriterion = original.contractionCriterion;
        this.expansionFactor = original.expansionFactor;
        this.expansionMode = original.expansionMode;
        this.internalArray = new double[original.internalArray.length];
        System.arraycopy(original.internalArray, 0, this.internalArray, 0, this.internalArray.length);
        this.numElements = original.numElements;
        this.startIndex = original.startIndex;
    }

    /**
     * Adds an element to the end of this expandable array.
     *
     * @param value Value to be added to end of array.
     */
    @Override
    public void addElement(final double value) {
        if (internalArray.length <= startIndex + numElements) {
            expand();
        }
        internalArray[startIndex + numElements++] = value;
    }

    /**
     * Adds several element to the end of this expandable array.
     *
     * @param values Values to be added to end of array.
     * @since 2.2
     */
    @Override
    public void addElements(final double[] values) {
        final double[] tempArray = new double[numElements + values.length + 1];
        System.arraycopy(internalArray, startIndex, tempArray, 0, numElements);
        System.arraycopy(values, 0, tempArray, numElements, values.length);
        internalArray = tempArray;
        startIndex = 0;
        numElements += values.length;
    }

    /**
     * Adds an element to the end of the array and removes the first
     * element in the array.  Returns the discarded first element.
     * <p>
     * The effect is similar to a push operation in a FIFO queue.
     * <p>
     * Example: If the array contains the elements 1, 2, 3, 4 (in that order)
     * and addElementRolling(5) is invoked, the result is an array containing
     * the entries 2, 3, 4, 5 and the value returned is 1.
     *
     * @param value Value to be added to the array.
     * @return the value which has been discarded or "pushed" out of the array
     * by this rolling insert.
     */
    @Override
    public double addElementRolling(double value) {
        double discarded = internalArray[startIndex];

        if ((startIndex + (numElements + 1)) > internalArray.length) {
            expand();
        }
        // Increment the start index
        startIndex += 1;

        // Add the new value
        internalArray[startIndex + (numElements - 1)] = value;

        // Check the contraction criterion.
        if (shouldContract()) {
            contract();
        }
        return discarded;
    }

    /**
     * Substitutes {@code value} for the most recently added value.
     * <p>
     * Returns the value that has been replaced. If the array is empty (i.e.
     * if {@link #numElements} is zero), an MathIllegalStateException is thrown.
     *
     * @param value New value to substitute for the most recently added value
     * @return the value that has been replaced in the array.
     * @throws MathIllegalStateException if the array is empty
     * @since 2.0
     */
    public double substituteMostRecentElement(double value) throws MathIllegalStateException {
        if (numElements < 1) {
            throw new MathIllegalStateException(LocalizedFormats.CANNOT_SUBSTITUTE_ELEMENT_FROM_EMPTY_ARRAY);
        }

        final int substIndex = startIndex + (numElements - 1);
        final double discarded = internalArray[substIndex];

        internalArray[substIndex] = value;

        return discarded;
    }

    /**
     * Checks the expansion factor and the contraction criterion and raises
     * an exception if the contraction criterion is smaller than the
     * expansion criterion.
     *
     * @param contraction Criterion to be checked.
     * @param expansion Factor to be checked.
     * @throws NumberIsTooSmallException if {@code contraction < expansion}.
     * @throws NumberIsTooSmallException if {@code contraction <= 1}.
     * @throws NumberIsTooSmallException if {@code expansion <= 1 }.
     * @since 3.1
     */
    protected void checkContractExpand(double contraction, double expansion) throws NumberIsTooSmallException {
        if (contraction < expansion) {
            final NumberIsTooSmallException e = new NumberIsTooSmallException(contraction, 1, true);
            e.getContext().addMessage(LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_EXPANSION_FACTOR,
                                      contraction, expansion);
            throw e;
        }

        if (contraction <= 1) {
            final NumberIsTooSmallException e = new NumberIsTooSmallException(contraction, 1, false);
            e.getContext().addMessage(LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_ONE,
                                      contraction);
            throw e;
        }

        if (expansion <= 1) {
            final NumberIsTooSmallException e = new NumberIsTooSmallException(contraction, 1, false);
            e.getContext().addMessage(LocalizedFormats.EXPANSION_FACTOR_SMALLER_THAN_ONE,
                                      expansion);
            throw e;
        }
    }

    /**
     * Clear the array contents, resetting the number of elements to zero.
     */
    @Override
    public void clear() {
        numElements = 0;
        startIndex = 0;
    }

    /**
     * Contracts the storage array to the (size of the element set) + 1 - to avoid
     * a zero length array. This function also resets the startIndex to zero.
     */
    public void contract() {
        final double[] tempArray = new double[numElements + 1];

        // Copy and swap - copy only the element array from the src array.
        System.arraycopy(internalArray, startIndex, tempArray, 0, numElements);
        internalArray = tempArray;

        // Reset the start index to zero
        startIndex = 0;
    }

    /**
     * Discards the {@code i} initial elements of the array.
     * <p>
     * For example, if the array contains the elements 1,2,3,4, invoking
     * {@code discardFrontElements(2)} will cause the first two elements
     * to be discarded, leaving 3,4 in the array.
     *
     * @param i  the number of elements to discard from the front of the array
     * @throws MathIllegalArgumentException if i is greater than numElements.
     * @since 2.0
     */
    public void discardFrontElements(int i) throws MathIllegalArgumentException {
        discardExtremeElements(i,true);
    }

    /**
     * Discards the {@code i} last elements of the array.
     * <p>
     * For example, if the array contains the elements 1,2,3,4, invoking
     * {@code discardMostRecentElements(2)} will cause the last two elements
     * to be discarded, leaving 1,2 in the array.
     *
     * @param i  the number of elements to discard from the end of the array
     * @throws MathIllegalArgumentException if i is greater than numElements.
     * @since 2.0
     */
    public void discardMostRecentElements(int i) throws MathIllegalArgumentException {
        discardExtremeElements(i,false);
    }

    /**
     * Discards the {@code i} first or last elements of the array,
     * depending on the value of {@code front}.
     * <p>
     * For example, if the array contains the elements 1,2,3,4, invoking
     * {@code discardExtremeElements(2,false)} will cause the last two elements
     * to be discarded, leaving 1,2 in the array.
     * For example, if the array contains the elements 1,2,3,4, invoking
     * {@code discardExtremeElements(2,true)} will cause the first two elements
     * to be discarded, leaving 3,4 in the array.
     *
     * @param i  the number of elements to discard from the front/end of the array
     * @param front true if elements are to be discarded from the front
     * of the array, false if elements are to be discarded from the end
     * of the array
     * @throws MathIllegalArgumentException if i is greater than numElements.
     * @since 2.0
     */
    private void discardExtremeElements(int i, boolean front) throws MathIllegalArgumentException {
        if (i > numElements) {
            throw new MathIllegalArgumentException(
                    LocalizedFormats.TOO_MANY_ELEMENTS_TO_DISCARD_FROM_ARRAY,
                    i, numElements);
       } else if (i < 0) {
           throw new MathIllegalArgumentException(
                   LocalizedFormats.CANNOT_DISCARD_NEGATIVE_NUMBER_OF_ELEMENTS,
                   i);
        } else {
            // "Subtract" this number of discarded from numElements
            numElements -= i;
            if (front) {
                startIndex += i;
            }
        }
        if (shouldContract()) {
            contract();
        }
    }

    /**
     * Expands the internal storage array using the expansion factor.
     * <p>
     * If {@code expansionMode} is set to MULTIPLICATIVE,
     * the new array size will be {@code internalArray.length * expansionFactor}.
     * If {@code expansionMode} is set to ADDITIVE, the length
     * after expansion will be {@code internalArray.length + expansionFactor}.
     */
    protected void expand() {
        // notice the use of JdkMath.ceil(), this guarantees that we will always
        // have an array of at least currentSize + 1.   Assume that the
        // current initial capacity is 1 and the expansion factor
        // is 1.000000000000000001.  The newly calculated size will be
        // rounded up to 2 after the multiplication is performed.
        int newSize = 0;
        if (expansionMode == ExpansionMode.MULTIPLICATIVE) {
            newSize = (int) JdkMath.ceil(internalArray.length * expansionFactor);
        } else {
            newSize = (int) (internalArray.length + JdkMath.round(expansionFactor));
        }
        final double[] tempArray = new double[newSize];

        // Copy and swap
        System.arraycopy(internalArray, 0, tempArray, 0, internalArray.length);
        internalArray = tempArray;
    }

    /**
     * Expands the internal storage array to the specified size.
     *
     * @param size Size of the new internal storage array.
     */
    private void expandTo(int size) {
        final double[] tempArray = new double[size];
        // Copy and swap
        System.arraycopy(internalArray, 0, tempArray, 0, internalArray.length);
        internalArray = tempArray;
    }

    /**
     * The contraction criterion defines when the internal array will contract
     * to store only the number of elements in the element array.
     * <p>
     * If the {@code expansionMode} is {@code MULTIPLICATIVE},
     * contraction is triggered when the ratio between storage array length
     * and {@code numElements} exceeds {@code contractionFactor}.
     * If the {@code expansionMode} is {@code ADDITIVE}, the
     * number of excess storage locations is compared to {@code contractionFactor}.
     *
     * @return the contraction criterion used to reclaim memory.
     * @since 3.1
     */
    public double getContractionCriterion() {
        return contractionCriterion;
    }

    /**
     * Returns the element at the specified index.
     *
     * @param index index to fetch a value from
     * @return value stored at the specified index
     * @throws ArrayIndexOutOfBoundsException if {@code index} is less than
     * zero or is greater than {@code getNumElements() - 1}.
     */
    @Override
    public double getElement(int index) {
        if (index >= numElements) {
            throw new ArrayIndexOutOfBoundsException(index);
        } else if (index >= 0) {
            return internalArray[startIndex + index];
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

     /**
     * Returns a double array containing the elements of this ResizableArray.
     * <p>
     * This method returns a copy, not a reference to the underlying array,
     * so that changes made to the returned array have no effect on this ResizableArray.
     *
     * @return the double array.
     */
    @Override
    public double[] getElements() {
        final double[] elementArray = new double[numElements];
        System.arraycopy(internalArray, startIndex, elementArray, 0, numElements);
        return elementArray;
    }

    /**
     * The expansion factor controls the size of a new array when an array
     * needs to be expanded.
     * <p>
     * The {@code expansionMode} determines whether the size of the array
     * is multiplied by the {@code expansionFactor} (MULTIPLICATIVE) or if
     * the expansion is additive (ADDITIVE -- {@code expansionFactor}
     * storage locations added).  The default {@code expansionMode} is
     * MULTIPLICATIVE and the default {@code expansionFactor} is 2.0.
     *
     * @return the expansion factor of this expandable double array
     */
    public double getExpansionFactor() {
        return expansionFactor;
    }

    /**
     * The expansion mode determines whether the internal storage
     * array grows additively or multiplicatively when it is expanded.
     *
     * @return the expansion mode.
     */
    public ExpansionMode getExpansionMode() {
        return expansionMode;
    }

    /**
     * Gets the currently allocated size of the internal data structure used
     * for storing elements.
     * This is not to be confused with {@link #getNumElements() the number of
     * elements actually stored}.
     *
     * @return the length of the internal array.
     * @since 3.1
     */
    public int getCapacity() {
        return internalArray.length;
    }

    /**
     * Returns the number of elements currently in the array.  Please note
     * that this is different from the length of the internal storage array.
     *
     * @return the number of elements.
     */
    @Override
    public int getNumElements() {
        return numElements;
    }

    /**
     * Provides <em>direct</em> access to the internal storage array.
     * Please note that this method returns a reference to this object's
     * storage array, not a copy.
     * <p>
     * To correctly address elements of the array, the "start index" is
     * required (available via the {@link #getStartIndex() getStartIndex}
     * method.
     * <p>
     * This method should only be used to avoid copying the internal array.
     * The returned value <em>must</em> be used for reading only; other
     * uses could lead to this object becoming inconsistent.
     * <p>
     * The {@link #getElements} method has no such limitation since it
     * returns a copy of this array's addressable elements.
     *
     * @return the internal storage array used by this object.
     * @since 3.1
     */
    protected double[] getArrayRef() {
        return internalArray;
    }

    /**
     * Returns the "start index" of the internal array.
     * This index is the position of the first addressable element in the
     * internal storage array.
     * <p>
     * The addressable elements in the array are at indices contained in
     * the interval [{@link #getStartIndex()},
     *               {@link #getStartIndex()} + {@link #getNumElements()} - 1].
     *
     * @return the start index.
     * @since 3.1
     */
    protected int getStartIndex() {
        return startIndex;
    }

    /**
     * Performs an operation on the addressable elements of the array.
     *
     * @param f Function to be applied on this array.
     * @return the result.
     * @since 3.1
     */
    public double compute(MathArrays.Function f) {
        return f.evaluate(internalArray, startIndex, numElements);
    }

    /**
     * Sets the element at the specified index.
     * <p>
     * If the specified index is greater than {@code getNumElements() - 1},
     * the {@code numElements} property is increased to {@code index +1}
     * and additional storage is allocated (if necessary) for the new element and
     * all (uninitialized) elements between the new element and the previous end
     * of the array).
     *
     * @param index index to store a value in
     * @param value value to store at the specified index
     * @throws ArrayIndexOutOfBoundsException if {@code index < 0}.
     */
    @Override
    public void setElement(int index, double value) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (index + 1 > numElements) {
            numElements = index + 1;
        }
        if ((startIndex + index) >= internalArray.length) {
            expandTo(startIndex + (index + 1));
        }
        internalArray[startIndex + index] = value;
    }

    /**
     * This function allows you to control the number of elements contained
     * in this array, and can be used to "throw out" the last n values in an
     * array. This function will also expand the internal array as needed.
     *
     * @param i a new number of elements
     * @throws MathIllegalArgumentException if {@code i} is negative.
     */
    public void setNumElements(int i) throws MathIllegalArgumentException {
        // If index is negative thrown an error.
        if (i < 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.INDEX_NOT_POSITIVE, i);
        }

        // Test the new num elements, check to see if the array needs to be
        // expanded to accommodate this new number of elements.
        final int newSize = startIndex + i;
        if (newSize > internalArray.length) {
            expandTo(newSize);
        }

        // Set the new number of elements to new value.
        numElements = i;
    }

    /**
     * Returns true if the internal storage array has too many unused
     * storage positions.
     *
     * @return true if array satisfies the contraction criteria
     */
    private boolean shouldContract() {
        if (expansionMode == ExpansionMode.MULTIPLICATIVE) {
            return (internalArray.length / ((float) numElements)) > contractionCriterion;
        } else {
            return (internalArray.length - numElements) > contractionCriterion;
        }
    }

    /**
     * Returns a copy of the ResizableDoubleArray.  Does not contract before
     * the copy, so the returned object is an exact copy of this.
     *
     * @return a new ResizableDoubleArray with the same data and configuration
     * properties as this
     * @since 2.0
     */
    public ResizableDoubleArray copy() {
        return new ResizableDoubleArray(this);
    }

    /**
     * Returns true iff object is a ResizableDoubleArray with the same properties
     * as this and an identical internal storage array.
     *
     * @param object object to be compared for equality with this
     * @return true iff object is a ResizableDoubleArray with the same data and
     * properties as this
     * @since 2.0
     */
    @Override
    public boolean equals(Object object) {
        if (object == this ) {
            return true;
        }
        if (!(object instanceof ResizableDoubleArray)) {
            return false;
        }
        boolean result = true;
        final ResizableDoubleArray other = (ResizableDoubleArray) object;
        result = result && other.contractionCriterion == contractionCriterion;
        result = result && other.expansionFactor == expansionFactor;
        result = result && other.expansionMode == expansionMode;
        result = result && other.numElements == numElements;
        result = result && other.startIndex == startIndex;
        if (!result) {
            return false;
        } else {
            return Arrays.equals(internalArray, other.internalArray);
        }
    }

    /**
     * Returns a hash code consistent with equals.
     *
     * @return the hash code representing this {@code ResizableDoubleArray}.
     * @since 2.0
     */
    @Override
    public int hashCode() {
        final int[] hashData = new int[6];
        hashData[0] = Double.valueOf(expansionFactor).hashCode();
        hashData[1] = Double.valueOf(contractionCriterion).hashCode();
        hashData[2] = expansionMode.hashCode();
        hashData[3] = Arrays.hashCode(internalArray);
        hashData[4] = numElements;
        hashData[5] = startIndex;
        return Arrays.hashCode(hashData);
    }
}
