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
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.random.RandomData;


/**
 * This class contains test cases for the ResizableDoubleArray.
 * 
 * @version $Revision$ $Date$
 */
public class ResizableDoubleArrayTest extends DoubleArrayAbstractTest {
    
    public ResizableDoubleArrayTest(String name) {
        super( name );
    }
      
    protected void tearDown() throws Exception {
        da = null;
        ra = null;
    }
       
    protected void setUp() throws Exception {
        da = new ResizableDoubleArray();
        ra = new ResizableDoubleArray();
    }
    
    public void testConstructors() {
        float defaultExpansionFactor = 2.0f;
        float defaultContractionCriteria = 2.5f;
        int defaultMode = ResizableDoubleArray.MULTIPLICATIVE_MODE;
        
        ResizableDoubleArray testDa = new ResizableDoubleArray(2);
        assertEquals(0, testDa.getNumElements());
        assertEquals(2, testDa.getInternalLength());
        assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        assertEquals(defaultContractionCriteria, testDa.getContractionCriteria(), 0);
        assertEquals(defaultMode, testDa.getExpansionMode());
        try {
            da = new ResizableDoubleArray(-1);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
        testDa = new ResizableDoubleArray(2, 2.0f);
        assertEquals(0, testDa.getNumElements());
        assertEquals(2, testDa.getInternalLength());
        assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        assertEquals(defaultContractionCriteria, testDa.getContractionCriteria(), 0);
        assertEquals(defaultMode, testDa.getExpansionMode());
        
        try {
            da = new ResizableDoubleArray(2, 0.5f);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
        testDa = new ResizableDoubleArray(2, 3.0f);
        assertEquals(3.0f, testDa.getExpansionFactor(), 0);
        assertEquals(3.5f, testDa.getContractionCriteria(), 0);
        
        testDa = new ResizableDoubleArray(2, 2.0f, 3.0f);
        assertEquals(0, testDa.getNumElements());
        assertEquals(2, testDa.getInternalLength());
        assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        assertEquals(3.0f, testDa.getContractionCriteria(), 0);
        assertEquals(defaultMode, testDa.getExpansionMode());
        
        try {
            da = new ResizableDoubleArray(2, 2.0f, 1.5f);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
        testDa = new ResizableDoubleArray(2, 2.0f, 3.0f, 
                ResizableDoubleArray.ADDITIVE_MODE);
        assertEquals(0, testDa.getNumElements());
        assertEquals(2, testDa.getInternalLength());
        assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        assertEquals(3.0f, testDa.getContractionCriteria(), 0);
        assertEquals(ResizableDoubleArray.ADDITIVE_MODE, 
                testDa.getExpansionMode());
        
        try {
            da = new ResizableDoubleArray(2, 2.0f, 2.5f, -1);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
    }
    
    
    public void testSetElementArbitraryExpansion() {
        
        // MULTIPLICATIVE_MODE 
        da.addElement(2.0);
        da.addElement(4.0);
        da.addElement(6.0);
        da.setElement(1, 3.0);
        
        // Expand the array arbitrarily to 1000 items
        da.setElement(1000, 3.4);
        
        assertEquals( "The number of elements should now be 1001, it isn't", 
                da.getNumElements(), 1001);
        
        assertEquals( "Uninitialized Elements are default value of 0.0, index 766 wasn't", 0.0,
                da.getElement( 760 ), Double.MIN_VALUE );
        
        assertEquals( "The 1000th index should be 3.4, it isn't", 3.4, da.getElement(1000), 
                Double.MIN_VALUE );
        assertEquals( "The 0th index should be 2.0, it isn't", 2.0, da.getElement(0), 
                Double.MIN_VALUE); 
        
        // Make sure numElements and expansion work correctly for expansion boundary cases
        da.clear();
        da.addElement(2.0);
        da.addElement(4.0);
        da.addElement(6.0);
        assertEquals(4, ((ResizableDoubleArray) da).getInternalLength());
        assertEquals(3, da.getNumElements());
        da.setElement(3, 7.0);
        assertEquals(4, ((ResizableDoubleArray) da).getInternalLength());
        assertEquals(4, da.getNumElements());
        da.setElement(10, 10.0);
        assertEquals(11, ((ResizableDoubleArray) da).getInternalLength());
        assertEquals(11, da.getNumElements());
        da.setElement(9, 10.0);
        assertEquals(11, ((ResizableDoubleArray) da).getInternalLength());
        assertEquals(11, da.getNumElements());
        
        try {
            da.setElement(-2, 3);
            fail("Expecting ArrayIndexOutOfBoundsException for negative index");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        }
        
        // ADDITIVE_MODE
        
        ResizableDoubleArray testDa = new ResizableDoubleArray(2, 2.0f, 3.0f, 
                ResizableDoubleArray.ADDITIVE_MODE);
        assertEquals(2, testDa.getInternalLength());
        testDa.addElement(1d);
        testDa.addElement(1d);
        assertEquals(2, testDa.getInternalLength());
        testDa.addElement(1d);
        assertEquals(4, testDa.getInternalLength());         
    }
    
    public void testAdd1000() {
        super.testAdd1000();
        assertEquals("Internal Storage length should be 1024 if we started out with initial capacity of " +
                "16 and an expansion factor of 2.0",
                1024, ((ResizableDoubleArray) da).getInternalLength());
    }
    
    public void testAddElementRolling() {
        super.testAddElementRolling();
        
        // MULTIPLICATIVE_MODE
        da.clear();
        da.addElement(1);
        da.addElement(2);
        da.addElementRolling(3);
        assertEquals(3, da.getElement(1), 0);
        da.addElementRolling(4);
        assertEquals(3, da.getElement(0), 0);
        assertEquals(4, da.getElement(1), 0);
        da.addElement(5);
        assertEquals(5, da.getElement(2), 0);
        da.addElementRolling(6);
        assertEquals(4, da.getElement(0), 0);
        assertEquals(5, da.getElement(1), 0);
        assertEquals(6, da.getElement(2), 0);   
        
        // ADDITIVE_MODE  (x's are occupied storage locations, 0's are open)
        ResizableDoubleArray testDa = new ResizableDoubleArray(2, 2.0f, 2.5f, 
                ResizableDoubleArray.ADDITIVE_MODE);
        assertEquals(2, testDa.getInternalLength());
        testDa.addElement(1d); // x,0
        testDa.addElement(2d); // x,x
        testDa.addElement(3d); // x,x,x,0 -- expanded
        assertEquals(1d, testDa.getElement(0), 0);
        assertEquals(2d, testDa.getElement(1), 0);
        assertEquals(3d, testDa.getElement(2), 0);   
        assertEquals(4, testDa.getInternalLength());  // x,x,x,0 
        assertEquals(3, testDa.getNumElements());
        testDa.addElementRolling(4d);
        assertEquals(2d, testDa.getElement(0), 0);
        assertEquals(3d, testDa.getElement(1), 0);
        assertEquals(4d, testDa.getElement(2), 0);   
        assertEquals(4, testDa.getInternalLength());  // 0,x,x,x
        assertEquals(3, testDa.getNumElements());
        testDa.addElementRolling(5d);   // 0,0,x,x,x,0 -- time to contract
        assertEquals(3d, testDa.getElement(0), 0);
        assertEquals(4d, testDa.getElement(1), 0);
        assertEquals(5d, testDa.getElement(2), 0);   
        assertEquals(4, testDa.getInternalLength());  // contracted -- x,x,x,0     
        assertEquals(3, testDa.getNumElements());
        try {
            testDa.getElement(4);
            fail("Expecting ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        }  
        try {
            testDa.getElement(-1);
            fail("Expecting ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        }
    }
    
    public void testSetNumberOfElements() {
        da.addElement( 1.0 );
        da.addElement( 1.0 );
        da.addElement( 1.0 );
        da.addElement( 1.0 );
        da.addElement( 1.0 );
        da.addElement( 1.0 );
        assertEquals( "Number of elements should equal 6", da.getNumElements(), 6);
        
        ((ResizableDoubleArray) da).setNumElements( 3 );
        assertEquals( "Number of elements should equal 3", da.getNumElements(), 3);
        
        try {
            ((ResizableDoubleArray) da).setNumElements( -3 );
            fail( "Setting number of elements to negative should've thrown an exception");
        } catch( IllegalArgumentException iae ) {
        }
        
        ((ResizableDoubleArray) da).setNumElements(1024);
        assertEquals( "Number of elements should now be 1024", da.getNumElements(), 1024);
        assertEquals( "Element 453 should be a default double", da.getElement( 453 ), 0.0, Double.MIN_VALUE);
        
    }
    
    public void testWithInitialCapacity() {
        
        ResizableDoubleArray eDA2 = new ResizableDoubleArray(2);
        assertEquals("Initial number of elements should be 0", 0, eDA2.getNumElements());
        
        RandomData randomData = new RandomDataImpl();
        int iterations = randomData.nextInt(100, 1000);
        
        for( int i = 0; i < iterations; i++) {
            eDA2.addElement( i );
        }
        
        assertEquals("Number of elements should be equal to " + iterations, iterations, eDA2.getNumElements());
        
        eDA2.addElement( 2.0 );
        
        assertEquals("Number of elements should be equals to " + (iterations +1),
                iterations + 1 , eDA2.getNumElements() );
    }
    
    public void testWithInitialCapacityAndExpansionFactor() {
        
        ResizableDoubleArray eDA3 = new ResizableDoubleArray(3, 3.0f, 3.5f);
        assertEquals("Initial number of elements should be 0", 0, eDA3.getNumElements() );
        
        RandomData randomData = new RandomDataImpl();
        int iterations = randomData.nextInt(100, 3000);
        
        for( int i = 0; i < iterations; i++) {
            eDA3.addElement( i );
        }
        
        assertEquals("Number of elements should be equal to " + iterations, iterations,eDA3.getNumElements());
        
        eDA3.addElement( 2.0 );
        
        assertEquals("Number of elements should be equals to " + (iterations +1),
                iterations +1, eDA3.getNumElements() );
        
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
        
        ((ResizableDoubleArray)da).discardFrontElements(5);
        assertEquals( "Number of elements should be 6", 6, da.getNumElements());
        
        try {
            ((ResizableDoubleArray)da).discardFrontElements(-1);
            fail( "Trying to discard a negative number of element is not allowed");
        } catch( Exception e ){
        }
        
        try {
            ((ResizableDoubleArray)da).discardFrontElements( 10000 );
            fail( "You can't discard more elements than the array contains");
        } catch( Exception e ){
        }
    }
    
    public void testMutators() {
        ((ResizableDoubleArray)da).setContractionCriteria(10f);
        assertEquals(10f, ((ResizableDoubleArray)da).getContractionCriteria(), 0);
        ((ResizableDoubleArray)da).setExpansionFactor(8f);  
        assertEquals(8f, ((ResizableDoubleArray)da).getExpansionFactor(), 0);
        try {
            ((ResizableDoubleArray)da).setExpansionFactor(11f);  // greater than contractionCriteria
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        ((ResizableDoubleArray)da).setExpansionMode(
                ResizableDoubleArray.ADDITIVE_MODE);
        assertEquals(ResizableDoubleArray.ADDITIVE_MODE, 
                ((ResizableDoubleArray)da).getExpansionMode());
        try {
            ((ResizableDoubleArray)da).setExpansionMode(-1);
            fail ("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
}
