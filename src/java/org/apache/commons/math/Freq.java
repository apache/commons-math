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

import java.util.Hashtable;
import java.util.Enumeration;

/** 
 * Maintains a frequency distribution. <br>
 * Accepts int, long or string values, converting
 * all to Strings and maintaining frequency counts.
 *
 * @author Phil Steitz
 */

public class Freq {

    private String name;

    private Hashtable freqTable;

    /** instance initializer */
    {
        freqTable = new Hashtable();
    }

    public Freq() {
    }

    public Freq(String name) {
        this.name = name;
    }

    public String toString() {
        StringBuffer outBuffer = new StringBuffer();
        outBuffer.append("Value \t Frequency \n");
        Enumeration e = freqTable.keys();
        Long count = null;
        String value = null;
        while (e.hasMoreElements()) {
            value = (String)e.nextElement();
            count = (Long)freqTable.get(value);
            outBuffer.append(value);
            outBuffer.append("\t");
            outBuffer.append(count.toString());
            outBuffer.append("\n");
        }
        return outBuffer.toString();
    }

    public String toXML() {
        return null;
    }

    /** Adds 1 to the frequency count for v */
    public void addValue(java.lang.String v) {
        insertValue(v);
    }

    /** Adds 1 to the frequency count for v */
    public void addValue(int v) {
        insertValue((new Integer(v)).toString());
    }

    /** Adds 1 to the frequency count for v */
    public void addValue(long v) {
        insertValue((new Long(v)).toString());
    }
    
    /** Returns the number of values = v */
    public long getCount(String v) {
        Long ct = (Long)freqTable.get(v);
        if (ct == null) {
            return 0;
        } else {
            return ct.longValue();
        }
    }
    
    /** Returns the sum of all frequencies */
    public long getSumFreq() {
        Enumeration e = freqTable.keys();
        long count = 0;
        String value = null;
        while (e.hasMoreElements()) {
            value = (String)e.nextElement();
            count += ((Long)freqTable.get(value)).longValue();
        }
        return count;
    }
    
    /** Returns the percentage of values = v */
    public double getPct(String v) {
        return (new Double(getCount(v))).doubleValue()
                   /(new Double(getSumFreq())).doubleValue();
    }
    
    /** Clears the frequency table */
    public void clear() {
        freqTable.clear();
    }
        
    /** Adds 1 to the frequency count for v */
    private void insertValue(String v) {
        Long ct = (Long)freqTable.get(v);
        if (ct == null) {
            Long val = new Long(1);
            freqTable.put(v,val);
        } else {
            freqTable.put(v,(new Long(ct.longValue()+1)));
        }
    }

    /** Getter for property name.
     * @return Value of property name.
     */
    public java.lang.String getName() {
        return name;
    }    

    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
}
