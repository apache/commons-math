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
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:07:11 $
 */
public class FixedDoubleArrayTest extends DoubleArrayAbstractTest {

	public FixedDoubleArrayTest(String name) {
		super( name );
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		da = new FixedDoubleArray(4000);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		da = null;
	}
	
	
	/** TEST NORMAL OPERATIONS - calling super class test and then checking internal
	 *   storage **/
	
	public void testAddElementRolling() {
		ra = new FixedDoubleArray(6);

		super.testAddElementRolling();
		
		assertEquals( "FixedDoubleArray should have 6 size internal storage", 
								6, ((FixedDoubleArray) ra).getValues().length);		
	}
	
	public void testExceedingElements() {
		
		for( int i = 0; i < 3999; i++) {
			da.addElement( 1.0 );
		}

		da.addElement( 1.0 );
		
		try {
			da.addElement( 2.0 );
			fail( " Adding more than 4000 elements should cause an exception ");
		} catch( Exception e ) {
		}
		
		da.addElementRolling(2.0);
		assertEquals( "This is the first rolling add, the first element should be 2.0",
								2.0, da.getElement(0), Double.MIN_VALUE);
	}
	
	public void testGetExceeding() {
		try {
			da.getElement(100);
			fail( "I haven't added 100 elements to the list yet, trying to getElement(100) should " +				"thrown an error");
		} catch (Exception e ){ 
		}
		
	}

	public void testSetElement() {
		da.addElement( 1.0 );
		da.addElement( 1.0 );
		da.addElement( 1.0 );
		da.addElement( 1.0 );
		da.addElement( 1.0 );
		da.addElement( 1.0 );
		da.addElement( 1.0 );
		
		da.setElement( 2, 4.0 );
		assertEquals( "Index 2 should be 4.0", 4.0, da.getElement(2), Double.MIN_VALUE);
		
		try {
			da.setElement(2000, 45.0);
			fail( "The array does not contain 2000 elements yet, setting this element should" +				" cause an excpetion");
		} catch(Exception e) {
		}
		
	}

	public void testOnlyRolling() {
		for( int i = 0; i < 8000; i++) {
			da.addElementRolling( i );
		}
		
		assertEquals( "The 2000th element should equal 6000",
			6000.0, da.getElement(2000), Double.MIN_VALUE);
	}
	
	public void testClear() {
		for( int i = 0; i < 10; i++) {
			da.addElementRolling(1.0);
		}
		
		assertEquals( "There should be ten elements in the array",
								10, da.getNumElements() );
		
		da.clear();

		assertEquals( "There should be zero elements in the array",
								0, da.getNumElements() );

		for( int i = 0; i < 10; i++) {
			da.addElementRolling(1.0);
		}
		
		assertEquals( "There should be ten elements in the array",
								10, da.getNumElements() );
				
	}
	
		
}
