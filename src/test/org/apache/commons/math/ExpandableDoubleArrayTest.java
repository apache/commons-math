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

import junit.framework.TestCase;

/**
 * This class contains test cases for the ExpandableDoubleArray.
 * 
 * @author <a href="mailto:tobrien@apache.org">Tim O'Brien</a>
 */
public class ExpandableDoubleArrayTest extends TestCase {

	public ExpandableDoubleArrayTest(String name) {
		super( name );
	}
	
	/** TEST NORMAL OPERATIONS **/
	
	public void testAdd1000() {

		ExpandableDoubleArray exDoubleArr = new ExpandableDoubleArray();
		
		for( int i = 0; i < 1000; i++) {
			exDoubleArr.addElement( i );
		}
		
		assertTrue("Number of elements should be equal to 1000 after adding 1000 values",
							exDoubleArr.getNumElements() == 1000);
							
		assertTrue("Internal Storage length should be 1024 if we started out with initial capacity of " +			"16 and an expansion factor of 2.0",
						    exDoubleArr.getInternalLength() == 1024);
						    
		assertTrue("The element at the 56th index should be 56", 
							exDoubleArr.getElement(56) == 56 );
						    
	}
	
	public void testWithInitialCapacity() {
		ExpandableDoubleArray exDoubleArr = new ExpandableDoubleArray(2);

		assertTrue("Initial internal length should be 2", exDoubleArr.getInternalLength() == 2);
		assertTrue("Initial number of elements should be 0", exDoubleArr.getNumElements() == 0);

		int iterations = (int) Math.pow(2.0, 15.0);

		for( int i = 0; i < iterations; i++) {
			exDoubleArr.addElement( i );
		}
		
		assertTrue("Number of elements should be equal to 2^15", exDoubleArr.getNumElements() == (int) Math.pow(2.0, 15.0));
		assertTrue("Internal length should be 2^15", exDoubleArr.getInternalLength() == (int) Math.pow(2.0, 15.0));
		
		exDoubleArr.addElement( 2.0 );
		
		assertTrue("Number of elements should be equals to 2^15 + 1",
						   exDoubleArr.getNumElements() == ( (int) Math.pow(2.0, 15.0) + 1 ) );
		assertTrue("Internal length should be 2^16", exDoubleArr.getInternalLength() == (int) Math.pow(2.0, 16.0));
						   

	}

	public void testWithInitialCapacitAndExpansionFactor() {
		ExpandableDoubleArray exDoubleArr = new ExpandableDoubleArray(3, 3.0f);

		assertTrue("Initial internal length should be 3", exDoubleArr.getInternalLength() == 3);
		assertTrue("Initial number of elements should be 0", exDoubleArr.getNumElements() == 0);

		int iterations = (int) Math.pow(3.0, 7.0);

		for( int i = 0; i < iterations; i++) {
			exDoubleArr.addElement( i );
		}
		
		assertTrue("Number of elements should be equal to 3^7", exDoubleArr.getNumElements() == (int) Math.pow(3.0, 7.0));
		assertTrue("Internal length should be 3^7", exDoubleArr.getInternalLength() == (int) Math.pow(3.0, 7.0));
		
		exDoubleArr.addElement( 2.0 );
		
		assertTrue("Number of elements should be equals to 3^7 + 1",
						   exDoubleArr.getNumElements() == ( (int) Math.pow(3.0, 7.0) + 1 ) );
		assertTrue("Internal length should be 3^8", exDoubleArr.getInternalLength() == (int) Math.pow(3.0, 8.0));
						   
		assertTrue("Expansion factor should equal 3.0", exDoubleArr.getExpansionFactor() == 3.0f);
	}
	
	public void testGetValues() {
		
		ExpandableDoubleArray eDA = new ExpandableDoubleArray();
		
		double[] controlArray = {2.0, 4.0, 6.0};
		
		eDA.addElement(2.0);
		eDA.addElement(4.0);
		eDA.addElement(6.0);
		double[] testArray = eDA.getValues();
		
		for( int i = 0; i < eDA.getNumElements(); i++) {
			assertTrue( "The testArray values should equal the controlArray values, index i: " + i +
				" does not match", testArray[i] == controlArray[i]);
		}
		
	}
	
	public void testSetElementArbitraryExpansion() {
		
		ExpandableDoubleArray eDA = new ExpandableDoubleArray();
		
		double[] controlArray = {2.0, 4.0, 6.0};
		
		eDA.addElement(2.0);
		eDA.addElement(4.0);
		eDA.addElement(6.0);
		eDA.setElement(1, 3.0);
		
		// Expand the array arbitrarily to 1000 items
		eDA.setElement(1000, 3.4);

		assertTrue( "The length of the internal array should now be 1001, it isn't", eDA.getInternalLength() == 1001);
		assertTrue( "The number of elements should now be 1001, it isn't", eDA.getNumElements() == 1001);
		
		assertTrue( "Uninitialized Elements are default value of 0.0, index 766 wasn't", 
							eDA.getElement( 760 ) == 0.0);
		
		assertTrue( "The 1000th index should be 3.4, it isn't", eDA.getElement(1000) == 3.4);
		assertTrue( "The 0th index should be 2.0, it isn't", eDA.getElement(0) == 2.0);		
		
	}
	
	public void testSetNumberOfElements() {
		
		ExpandableDoubleArray eDA = new ExpandableDoubleArray();
		eDA.addElement( 1.0 );
		eDA.addElement( 1.0 );
		eDA.addElement( 1.0 );
		eDA.addElement( 1.0 );
		eDA.addElement( 1.0 );
		eDA.addElement( 1.0 );
		assertTrue( "Number of elements should equal 6", eDA.getNumElements() == 6);
		
		eDA.setNumElements( 3 );
		assertTrue( "Number of elements should equal 3", eDA.getNumElements() == 3);
		
		try {
			eDA.setNumElements( -3 );
			fail( "Setting number of elements to negative should've thrown an exception");
		} catch( IllegalArgumentException iae ) {
		}

		eDA.setNumElements(1024);
		assertTrue( "Number of elements should now be 1024", eDA.getNumElements() == 1024);
		assertTrue( "Element 453 should be a default double", eDA.getElement( 453 ) == 0.0);
				
	}
	
	public void testAddElementRolling() {
		
		ExpandableDoubleArray eDA = new ExpandableDoubleArray();
		
		eDA.addElement( 1.0 );
		eDA.addElement( 1.0 );
		eDA.addElement( 1.0 );
		eDA.addElement( 1.0 );
		eDA.addElement( 1.0 );
		eDA.addElement( 1.0 );
		eDA.addElementRolling( 2.0 );
		
		assertTrue( "There should be 6 elements in the eda", eDA.getNumElements() == 6);
		assertTrue( "The last element should be 2.0", eDA.getElement( eDA.getNumElements() -1 ) == 2.0);
		
		for( int i = 0; i  < 1024; i++ ) {
			eDA.addElementRolling( i );
		}
		
		assertTrue( "We just inserted 1024 rolling elements, num elements should still be 6", eDA.getNumElements() == 6);
		assertTrue( "Even though there are only 6 element, internal storage should be 2048", eDA.getInternalLength() == 2048);
		assertEquals( "The start index should be 1025", 1025, eDA.getStartIndex());
		
		eDA.setStartIndex( 0 );
		
		assertEquals( "There shoud now be 1031 elements in this array", 1031, eDA.getNumElements(), 0.001);
		assertEquals( "The first element should be 1.0",1.0,  eDA.getElement(0), 0.001);
		
		try {
			eDA.setStartIndex( 100000 );
			fail( "TRying to set the start index outside of the current array should have caused an error");
		} catch( IllegalArgumentException iae ) {
		}

		try {
			eDA.setStartIndex( -1 );
			fail( "TRying to set the start index to a negative number should have caused an error");
		} catch( IllegalArgumentException iae ) {
		}
	}

	/** TEST ERROR CONDITIONS **/

	public void testIllegalInitialCapacity() {
		try {
			ExpandableDoubleArray eDA = new ExpandableDoubleArray(-3, 2.0f);
			fail( "That constructor should have thrown an IllegalArgumentException because " +				"the initialCapacity was negative, if it didn't then" +				" the range checking of initialCapacity is not working properly" );
		} catch( IllegalArgumentException iae ) {
		}
		try {
			ExpandableDoubleArray eDA = new ExpandableDoubleArray(0, 2.0f);
			fail( "That constructor should have thrown an IllegalArgumentException because " +
				"the initialCapacity was ZERO if it didn't then" +
				" the range checking of initialCapacity is not working properly" );
		} catch( IllegalArgumentException iae ) {
		}
	}
	
	public void testIllegalExpansionFactor() {
		try {
			ExpandableDoubleArray eDA = new ExpandableDoubleArray(3, 0.66f);
			fail( "That constructor should have thrown an IllegalArgumentException because " +				"the expansionFactor for 0.66 which would shrink the array instead of expand the array");
		} catch( IllegalArgumentException iae ) {
		}
		try {
			ExpandableDoubleArray eDA = new ExpandableDoubleArray(3, 0.0f);
			fail( "That constructor should have thrown an IllegalArgumentException because " +
				"the expansionFactor for 0.0");
		} catch( IllegalArgumentException iae) {
		}
		
		try {
			ExpandableDoubleArray eDA = new ExpandableDoubleArray(3, -4.35f);
			fail( "That constructor should have thrown an IllegalArgumentException because " +
				"the expansionFactor for -4.35");
		} catch( IllegalArgumentException iae) {
		}
	}
	
	public void testGetOutOfBounds() {

		ExpandableDoubleArray eDA = new ExpandableDoubleArray();
		eDA.addElement(2.0);
		eDA.addElement(3.0);
		
		try {
			eDA.getElement(0);
			eDA.getElement(1);
		} catch( NoSuchElementException nsee ) {
			fail( "There are values for index 0 and 1, this should not have thrown an exception");
		}
		
		try {
			eDA.getElement(2);
			fail( "There are 2 elements in the array, you asked for index 2 implying that there are 3.  " +				"exception should have been thrown");
		} catch( NoSuchElementException nsee ) {
		}	
		
		try {
			eDA.getElement(-234);	
			fail( "You tried to retrieve a negative index, this should have thrown an exception. " );
		} catch( IllegalArgumentException iae) {
		}
	}

	public void testSetOutOfBounds() {

		ExpandableDoubleArray eDA = new ExpandableDoubleArray();
		
		try {
			eDA.setElement( -3, 3.4 );
			fail( "You tried to set an element with a negative index, thisshould have thrown an error");
		} catch( IllegalArgumentException iae ) {
		}
	}

}
