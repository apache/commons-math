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
 * This class contains test cases for the ExpandableDoubleArray.
 * 
 * @version $Revision: 1.10 $ $Date: 2004/02/21 21:35:18 $
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
