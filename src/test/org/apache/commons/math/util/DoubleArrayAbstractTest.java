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

import org.apache.commons.math.stat.StatUtils;

import junit.framework.TestCase;

/**
 * This class contains test cases for the ExpandableDoubleArray.
 * 
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:07:11 $
 */
public abstract class DoubleArrayAbstractTest extends TestCase {

	protected DoubleArray da = null;

	// Array used to test rolling
	protected DoubleArray ra = null;

	public DoubleArrayAbstractTest(String name) {
		super(name);
	}

	public void testAdd1000() {

		for (int i = 0; i < 1000; i++) {
			da.addElement(i);
		}

		assertEquals(
			"Number of elements should be equal to 1000 after adding 1000 values",
			1000,
			da.getNumElements());

		assertEquals(
			"The element at the 56th index should be 56",
			56.0,
			da.getElement(56),
			Double.MIN_VALUE);

	}

	public void testGetValues() {
		double[] controlArray = { 2.0, 4.0, 6.0 };

		da.addElement(2.0);
		da.addElement(4.0);
		da.addElement(6.0);
		double[] testArray = da.getElements();

		for (int i = 0; i < da.getNumElements(); i++) {
			assertEquals(
				"The testArray values should equal the controlArray values, index i: "
					+ i
					+ " does not match",
				testArray[i],
				controlArray[i],
				Double.MIN_VALUE);
		}

	}

	public void testAddElementRolling() {
		ra.addElement(0.5);
		ra.addElement(1.0);
		ra.addElement(1.0);
		ra.addElement(1.0);
		ra.addElement(1.0);
		ra.addElement(1.0);
		ra.addElementRolling(2.0);

		assertEquals(
			"There should be 6 elements in the eda",
			6,
			ra.getNumElements());
		assertEquals(
			"The max element should be 2.0",
			2.0,
            StatUtils.max(ra.getElements()),
			Double.MIN_VALUE);
		assertEquals(
			"The min element should be 1.0",
			1.0,
            StatUtils.min(ra.getElements()),
			Double.MIN_VALUE);

		for (int i = 0; i < 1024; i++) {
			ra.addElementRolling(i);
		}

		assertEquals(
			"We just inserted 1024 rolling elements, num elements should still be 6",
			6,
			ra.getNumElements());
	}

	public void testMinMax() {
		da.addElement(2.0);
		da.addElement(22.0);
		da.addElement(-2.0);
		da.addElement(21.0);
		da.addElement(22.0);
		da.addElement(42.0);
		da.addElement(62.0);
		da.addElement(22.0);
		da.addElement(122.0);
		da.addElement(1212.0);

		assertEquals("Min should be -2.0", -2.0, StatUtils.min(da.getElements()), Double.MIN_VALUE);
		assertEquals(
			"Max should be 1212.0",
			1212.0,
            StatUtils.max(da.getElements()),
			Double.MIN_VALUE);
	}

}
