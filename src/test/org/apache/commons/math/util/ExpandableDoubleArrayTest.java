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
 * This class contains test cases for the ExpandableDoubleArray.
 * 
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:07:11 $
 */
public class ExpandableDoubleArrayTest extends DoubleArrayAbstractTest {

	public ExpandableDoubleArrayTest(String name) {
		super( name );
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		da = new ExpandableDoubleArray();
		ra = new ExpandableDoubleArray();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		da = null;
		ra = null;
	}
	
	
	/** TEST NORMAL OPERATIONS - calling super class test and then checking internal
	 *   storage **/
	
	
	public void testSetElementArbitraryExpansion() {
		double[] controlArray = {2.0, 4.0, 6.0};
		
		da.addElement(2.0);
		da.addElement(4.0);
		da.addElement(6.0);
		da.setElement(1, 3.0);
		
		// Expand the array arbitrarily to 1000 items
		da.setElement(1000, 3.4);

		assertEquals( "The number of elements should now be 1001, it isn't", da.getNumElements(), 1001);
		
		assertEquals( "Uninitialized Elements are default value of 0.0, index 766 wasn't", 0.0,
							da.getElement( 760 ), Double.MIN_VALUE );
		
		assertEquals( "The 1000th index should be 3.4, it isn't", 3.4, da.getElement(1000), Double.MIN_VALUE );
		assertEquals( "The 0th index should be 2.0, it isn't", 2.0, da.getElement(0), Double.MIN_VALUE);		
		
	}
		
	public void testAdd1000() {
		super.testAdd1000();
		assertEquals("Internal Storage length should be 1024 if we started out with initial capacity of " +
			"16 and an expansion factor of 2.0",
							1024, ((ExpandableDoubleArray) da).getInternalLength());
	}
	
	public void testAddElementRolling() {
		super.testAddElementRolling();
	}

	public void testSetNumberOfElements() {
		da.addElement( 1.0 );
		da.addElement( 1.0 );
		da.addElement( 1.0 );
		da.addElement( 1.0 );
		da.addElement( 1.0 );
		da.addElement( 1.0 );
		assertEquals( "Number of elements should equal 6", da.getNumElements(), 6);
		
		((ExpandableDoubleArray) da).setNumElements( 3 );
		assertEquals( "Number of elements should equal 3", da.getNumElements(), 3);
		
		try {
			((ExpandableDoubleArray) da).setNumElements( -3 );
			fail( "Setting number of elements to negative should've thrown an exception");
		} catch( IllegalArgumentException iae ) {
		}

		((ExpandableDoubleArray) da).setNumElements(1024);
		assertEquals( "Number of elements should now be 1024", da.getNumElements(), 1024);
		assertEquals( "Element 453 should be a default double", da.getElement( 453 ), 0.0, Double.MIN_VALUE);
				
	}

	/** TESTS WHICH FOCUS ON ExpandableSpecific internal storage */

	public void testWithInitialCapacity() {

		ExpandableDoubleArray eDA2 = new ExpandableDoubleArray(2);
		assertEquals("Initial number of elements should be 0", 0, eDA2.getNumElements());

		int iterations = (int) Math.pow(2.0, 15.0);

		for( int i = 0; i < iterations; i++) {
			eDA2.addElement( i );
		}
		
		assertEquals("Number of elements should be equal to 2^15", (int) Math.pow(2.0, 15.0), eDA2.getNumElements());
		
		eDA2.addElement( 2.0 );
		
		assertEquals("Number of elements should be equals to 2^15 + 1",
		        ( (int) Math.pow(2.0, 15.0) + 1 ), eDA2.getNumElements() );
	}

	public void testWithInitialCapacityAndExpansionFactor() {

		ExpandableDoubleArray eDA3 = new ExpandableDoubleArray(3, 3.0f);
		assertEquals("Initial number of elements should be 0", 0, eDA3.getNumElements() );

		int iterations = (int) Math.pow(3.0, 7.0);

		for( int i = 0; i < iterations; i++) {
			eDA3.addElement( i );
		}
		
		assertEquals("Number of elements should be equal to 3^7", (int) Math.pow(3.0, 7.0), eDA3.getNumElements());
		
		eDA3.addElement( 2.0 );
		
		assertEquals("Number of elements should be equals to 3^7 + 1",
			( (int) Math.pow(3.0, 7.0) + 1 ), eDA3.getNumElements() );
						   
		assertEquals("Expansion factor should equal 3.0", 3.0f, eDA3.getExpansionFactor(), Double.MIN_VALUE);
	}
	
	public void testDiscard() {
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		assertEquals( "Number of elements should be 11", 11, da.getNumElements());
		
		((ExpandableDoubleArray)da).discardFrontElements(5);
		assertEquals( "Number of elements should be 6", 6, da.getNumElements());
		
		try {
            ((ExpandableDoubleArray)da).discardFrontElements(-1);
			fail( "Trying to discard a negative number of element is not allowed");
		} catch( Exception e ){
		}
		
		try {
            ((ExpandableDoubleArray)da).discardFrontElements( 10000 );
			fail( "You can't discard more elements than the array contains");
		} catch( Exception e ){
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
	
	public void testSetOutOfBounds() {
			try {
				da.setElement( -1, 2.0);
				fail( "Cannot set a negative index");	
			} catch( Exception e ){
			}
	}
	
	public void testGetOutOfBounds() {
		try {
			da.getElement(10000);
			fail( "Cannot get an element that is larger than the number of elements");
		} catch( Exception e ) {
		}
		
		try {
			da.getElement(-3);
			fail("Cannot get a negative index");
		} catch( Exception e ){
		}
	}
	
}
