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


/**
 * This class contains test cases for the ExpandableDoubleArray.
 * 
 * @author <a href="mailto:tobrien@apache.org">Tim O'Brien</a>
 */
public class ContractableDoubleArrayTest extends DoubleArrayAbstractTest {

	public ContractableDoubleArrayTest(String name) {
		super( name );
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		da = new ContractableDoubleArray();
	}

	/** Test normal operations and then test internal storage */
	public void testAdd1000() {
		super.testAdd1000();
		assertEquals("Internal Storage length should be 1024 if we started out with initial capacity of " +
			"16 and an expansion factor of 2.0",
							1024, ((ExpandableDoubleArray) da).getInternalLength());
	}
	
	public void testSetElementArbitraryExpansion() {
		super.testSetElementArbitraryExpansion();
		assertEquals( "The length of the internal array should now be 1001, it isn't", ((ExpandableDoubleArray) da).getInternalLength(), 1001);
	}

	public void testAddElementRolling() {
		super.testAddElementRolling();
		assertTrue( "Even though there are only 6 element, internal storage should be less than 2.5 times the number of elements", 
			((ExpandableDoubleArray) da).getInternalLength() < ((int) 6 * 2.5) );
	}


	/** Test ERROR conditions */
	/** TEST ERROR CONDITIONS **/

	public void testIllegalInitialCapacity() {
		try {
			ContractableDoubleArray eDA = new ContractableDoubleArray(-3, 2.0f);
			fail( "That constructor should have thrown an IllegalArgumentException because " +
				"the initialCapacity was negative, if it didn't then" +
				" the range checking of initialCapacity is not working properly" );
		} catch( IllegalArgumentException iae ) {
		}
		try {
			ContractableDoubleArray eDA = new ContractableDoubleArray(0, 2.0f);
			fail( "That constructor should have thrown an IllegalArgumentException because " +
				"the initialCapacity was ZERO if it didn't then" +
				" the range checking of initialCapacity is not working properly" );
		} catch( IllegalArgumentException iae ) {
		}
	}
	
	public void testIllegalExpansionFactor() {
		try {
			ContractableDoubleArray eDA = new ContractableDoubleArray(3, 0.66f);
			fail( "That constructor should have thrown an IllegalArgumentException because " +
				"the expansionFactor for 0.66 which would shrink the array instead of expand the array");
		} catch( IllegalArgumentException iae ) {
		}
		try {
			ContractableDoubleArray eDA = new ContractableDoubleArray(3, 0.0f);
			fail( "That constructor should have thrown an IllegalArgumentException because " +
				"the expansionFactor for 0.0");
		} catch( IllegalArgumentException iae) {
		}
		
		try {
			ContractableDoubleArray eDA = new ContractableDoubleArray(3, -4.35f);
			fail( "That constructor should have thrown an IllegalArgumentException because " +
				"the expansionFactor for -4.35");
		} catch( IllegalArgumentException iae) {
		}
	}
	

}
