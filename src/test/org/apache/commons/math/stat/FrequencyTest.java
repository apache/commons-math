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
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:08:38 $
 */

public final class FrequencyTest extends TestCase {
    private long oneL = 1;
    private long twoL = 2;
    private int oneI = 1;
    private int twoI = 2;
    private String oneS = "1";
    private String twoS = "2";
    private double tolerance = 10E-15;
    
    public FrequencyTest(String name) {
        super(name);
    }
    
    public void setUp() {  
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(FrequencyTest.class);
        suite.setName("Frequency Tests");
        return suite;
    }
    
    /** test freq counts */
    public void testCounts() {
        Frequency f = new Frequency("test counts"); 
        assertEquals("total count",0,f.getSumFreq());
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(oneS);
        f.addValue(oneI);
        assertEquals("one frequency count",3,f.getCount("1"));
        assertEquals("two frequency count",1,f.getCount("2"));
        assertEquals("foo frequency count",0,f.getCount("foo"));
        assertEquals("total count",4,f.getSumFreq());
        f.clear();
        assertEquals("total count",0,f.getSumFreq());
    }     
    
    /** test pcts */
    public void testPcts() {
        Frequency f = new Frequency("test pcts"); 
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(oneI);
        f.addValue(twoI);
        f.addValue("foo");
        f.addValue("foo");
        f.addValue("foo");
        f.addValue("foo");
        assertEquals("one pct",0.25,f.getPct("1"),tolerance);
        assertEquals("two pct",0.25,f.getPct("2"),tolerance);
        assertEquals("foo pct",0.5,f.getPct("foo"),tolerance);
        assertEquals("bar pct",0,f.getPct("bar"),tolerance);
    }
    
    /**
     * 
     */
    public void testToString(){
        Frequency f = new Frequency("test toString"); 
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(oneI);
        f.addValue(twoI);
        
        String s = f.toString();
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
    
    /**
     * 
     */
    public void testSetName(){
        String name = "name";
        Frequency f = new Frequency();
        f.setName(name);
        assertEquals(name, f.getName());
    }              
}

