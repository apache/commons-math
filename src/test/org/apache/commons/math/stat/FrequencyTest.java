/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2004 The Apache Software Foundation.  All rights
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
 *    nor may "Apache" appear in their name without prior written
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
package org.apache.commons.math.stat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for the {@link Frequency} class.
 *
 * @version $Revision: 1.10 $ $Date: 2004/02/18 04:04:18 $
 */

public final class FrequencyTest extends TestCase {
    private long oneL = 1;
    private long twoL = 2;
    private long threeL = 3;
    private int oneI = 1;
    private int twoI = 2;
    private int threeI=3;
    private String oneS = "1";
    private String twoS = "2";
    private double tolerance = 10E-15;
    private Frequency f = null;
    
    public FrequencyTest(String name) {
        super(name);
    }
    
    public void setUp() {  
    	f = new Frequency();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(FrequencyTest.class);
        suite.setName("Frequency Tests");
        return suite;
    }
    
    /** test freq counts */
    public void testCounts() {
        assertEquals("total count",0,f.getSumFreq());
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(1);
        f.addValue(oneI);
        assertEquals("one frequency count",3,f.getCount(1));
        assertEquals("two frequency count",1,f.getCount(2));
        assertEquals("three frequency count",0,f.getCount(3));
        assertEquals("total count",4,f.getSumFreq());
        assertEquals("zero cumulative frequency", 0, f.getCumFreq(0));
        assertEquals("one cumulative frequency", 3,  f.getCumFreq(1));
        assertEquals("two cumulative frequency", 4,  f.getCumFreq(2));
        assertEquals("two cumulative frequency", 4,  f.getCumFreq(5));
        assertEquals("two cumulative frequency", 0,  f.getCumFreq("foo"));
        f.clear();
        assertEquals("total count",0,f.getSumFreq());
    }     
    
    /** test pcts */
    public void testPcts() {
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(oneI);
        f.addValue(twoI);
        f.addValue(threeL);
        f.addValue(threeL);
        f.addValue(3);
        f.addValue(threeI);
        assertEquals("one pct",0.25,f.getPct(1),tolerance);
        assertEquals("two pct",0.25,f.getPct(new Long(2)),tolerance);
        assertEquals("three pct",0.5,f.getPct(threeL),tolerance);
        assertEquals("five pct",0,f.getPct(5),tolerance);
        assertEquals("foo pct",0,f.getPct("foo"),tolerance);
        assertEquals("one cum pct",0.25,f.getCumPct(1),tolerance);
        assertEquals("two cum pct",0.50,f.getCumPct(new Long(2)),tolerance);
        assertEquals("three cum pct",1.0,f.getCumPct(threeL),tolerance);
        assertEquals("five cum pct",1.0,f.getCumPct(5),tolerance);
        assertEquals("zero cum pct",0.0,f.getCumPct(0),tolerance);
        assertEquals("foo cum pct",0,f.getCumPct("foo"),tolerance);
    }
    
    /** test adding incomparable values */
    public void testAdd() {
    	char aChar = 'a';
    	char bChar = 'b';
    	String aString = "a";
    	f.addValue(aChar);
    	f.addValue(bChar);
    	try {
    		f.addValue(aString); 	
    		fail("Expecting IllegalArgumentException");
    	} catch (IllegalArgumentException ex) {
    		// expected
    	}
    	assertEquals("a pct",0.5,f.getPct(aChar),tolerance);
    	assertEquals("b cum pct",1.0,f.getCumPct(bChar),tolerance);
    	assertEquals("a string pct",0.0,f.getPct(aString),tolerance);
    	assertEquals("a string cum pct",0.0,f.getCumPct(aString),tolerance);
    }
    
    /**
     * Tests toString() 
     */
    public void testToString(){
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(oneI);
        f.addValue(twoI);
        
        String s = f.toString();
        //System.out.println(s);
        assertNotNull(s);
        BufferedReader reader = new BufferedReader(new StringReader(s));
        try {
            String line = reader.readLine(); // header line
            assertNotNull(line);
            
            line = reader.readLine(); // one's or two's line
            assertNotNull(line);
                        
            line = reader.readLine(); // one's or two's line
            assertNotNull(line);

            line = reader.readLine(); // no more elements
            assertNull(line);
        } catch(IOException ex){
            fail(ex.getMessage());
        }        
    }
}

