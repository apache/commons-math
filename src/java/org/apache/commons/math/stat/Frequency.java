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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Comparator;
import java.text.NumberFormat;

import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.TreeBag;

/** 
 * Maintains a frequency distribution.
 * <p>
 * Accepts int, long, char or Object values.  New values added must be comparable to 
 * those that have been added, otherwise the add method will throw an IllegalArgumentException.
 * The values are ordered using the default (natural order), unless a  <code>Comparator</code>
 *  is supplied in the constructor.
 * 
 * @version $Revision: 1.14 $ $Date: 2004/02/18 04:04:18 $
 */
public class Frequency implements Serializable {
    
    /** underlying collection */
    private SortedBag freqTable = null;

    /**
     * Default constructor.
     */
    public Frequency() {
        freqTable = new TreeBag();
    }
    
    /**
     * Constructor allowing values Comparator to be specified.
     * @param comparator Comparator used to order values
     */
    public Frequency(Comparator comparator) {
    	freqTable = new TreeBag(comparator);
    }

    /**
     * Return a string representation of this frequency
     * distribution.
     * @return a string representation.
     */
    public String toString() {
    	NumberFormat nf = NumberFormat.getPercentInstance();
        StringBuffer outBuffer = new StringBuffer();
        outBuffer.append("Value \t Freq. \t Pct. \t Cum Pct. \n");
        Iterator iter = freqTable.uniqueSet().iterator();
        while (iter.hasNext()) {
            Object value = iter.next();
            outBuffer.append(value);
            outBuffer.append('\t');
            outBuffer.append(getCount(value));
            outBuffer.append('\t');
            outBuffer.append(nf.format(getPct(value)));
            outBuffer.append('\t');
            outBuffer.append(nf.format(getCumPct(value)));
            outBuffer.append('\n');
        }
        return outBuffer.toString();
    }

    /**
     * Adds 1 to the frequency count for v
     * @param v the value to add.
     */
    public void addValue(Object v) {
    	try {
    		freqTable.add(v);
    	} catch (ClassCastException ex) {	
    		//TreeBag will throw ClassCastException if v is not comparable
    		throw new IllegalArgumentException("Value not comparable to existing values.");
    	}
    }

    /**
     * Adds 1 to the frequency count for v
     * @param v the value to add.
     */
    public void addValue(int v) {
        addValue(new Long(v));
    }

    /**
     * Adds 1 to the frequency count for v.
     * @param v the value to add.
     */
    public void addValue(long v) {
        addValue(new Long(v));
    }
    
    /**
     * Adds 1 to the frequency count for v.
     * @param v the value to add.
     */
    public void addValue(char v) {
    	addValue(new Character(v));
    }
    
    /** Clears the frequency table */
    public void clear() {
    	freqTable.clear();
    }
    
    /**
     * Returns an Iterator over the set of values that have been added.
     * @return values Iterator
     */
    public Iterator valuesIterator() {
    	return freqTable.uniqueSet().iterator();
    }
    
    //-------------------------------------------------------------------------
    
    /**
     * Returns the sum of all frequencies
     * @return the total frequency count.
     */
    public long getSumFreq() {
    	return freqTable.size();
    }

    /**
     * Returns the number of values = v
     * @param v the value to lookup.
     * @return the frequency of v.
     */
    public long getCount(Object v) {
    	long result = 0;
    	try { 
    		result = freqTable.getCount(v);
    	} catch (Exception ex) {
    		// ignore and return 0 -- ClassCastException will be thrown if value is not comparable
    	}
    	return result;
    }

    /**
     * Returns the number of values = v
     * @param v the value to lookup.
     * @return the frequency of v.
     */
    public long getCount(int v) {
    	long result = 0;
    	try { 
    		result = freqTable.getCount(new Long(v));
    	} catch (Exception ex) {
    		// ignore and return 0 -- ClassCastException will be thrown if value is not comparable
    	}
    	return result;
    }
    
    /**
     * Returns the number of values = v
     * @param v the value to lookup.
     * @return the frequency of v.
     */
    public long getCount(long v) {
    	long result = 0;
    	try { 
    		result = freqTable.getCount(new Long(v));
    	} catch (Exception ex) {
    		// ignore and return 0 -- ClassCastException will be thrown if value is not comparable
    	}
    	return result;
    }
    
    /**
     * Returns the number of values = v
     * @param v the value to lookup.
     * @return the frequency of v.
     */
    public long getCount(char v) {
    	long result = 0;
    	try { 
    		result = freqTable.getCount(new Character(v));
    	} catch (Exception ex) {
    		// ignore and return 0 -- ClassCastException will be thrown if value is not comparable
    	}
    	return result;
    }
    
    //-------------------------------------------------------------

    /**
     * Returns the percentage of values = v (as a proportion -- i.e. between 0 and 1).
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public double getPct(Object v) {
    	return (double) getCount(v) / (double) getSumFreq();        
    }
    
    /**
     * Returns the percentage of values = v (as a proportion -- i.e. between 0 and 1).
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public double getPct(int v) {
        return getPct(new Long(v));       
    }
    
    /**
     * Returns the percentage of values = v (as a proportion -- i.e. between 0 and 1).
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public double getPct(long v) {
    	return getPct(new Long(v));         
    }
    
    /**
     * Returns the percentage of values = v (as a proportion -- i.e. between 0 and 1).
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public double getPct(char v) {
    	return getPct(new Character(v));         
    }
    
    //-----------------------------------------------------------------------------------------
    
    /**
     * Returns the cumulative frequency of values less than or equal to v.
     * <p>
     * Returns 0 if v is not comparable to the values set.
     * 
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public long getCumFreq(Object v) {
    	long result = 0;
    	try {
    		result = freqTable.getCount(v);
    	} catch (ClassCastException ex) {
    		return result;   // v is not comparable
    	}
    	Comparable c = (Comparable) v;
    	if (c.compareTo(freqTable.first()) < 0) {
    		return 0;	// v is comparable, but less than the first value
    	}
    	if (c.compareTo(freqTable.last()) > 0) {
    		return getSumFreq();	// v is comparable, but greater than the last value
    	}
    	Iterator values = valuesIterator();
    	while (values.hasNext()) {
    		Object nextValue = values.next();
    		if (c.compareTo(nextValue) > 0) {
    			result += getCount(nextValue);
    		} else {
    			return result;
    		}
    	}
    	return result;
    }
    
     /**
     * Returns the cumulative frequency of values less than or equal to v.
     * <p>
     * Returns 0 if v is not comparable to the values set.
     * 
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public long getCumFreq(int v) {
    	return getCumFreq(new Long(v));       
    }
    
     /**
     * Returns the cumulative frequency of values less than or equal to v.
     * <p>
     * Returns 0 if v is not comparable to the values set.
     * 
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public long getCumFreq(long v) {
    	return getCumFreq(new Long(v));         
    }
    
    /**
     * Returns the cumulative frequency of values less than or equal to v.
     * <p>
     * Returns 0 if v is not comparable to the values set.
     * 
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public long getCumFreq(char v) {
    	return getCumFreq(new Character(v));         
    }
    
    //----------------------------------------------------------------------------------------------
    
     /**
     * Returns the cumulative percentatge of values less than or equal to v
     * (as a proportion between 0 and 1).
     * <p>
     * Returns 0 if v is not comparable to the values set.
     * 
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public double getCumPct(Object v) {
    	return (double) getCumFreq(v) / (double) getSumFreq();        
    }
    
    /**
     * Returns the cumulative percentatge of values less than or equal to v
     * (as a proportion between 0 and 1).
     * <p>
     * Returns 0 if v is not comparable to the values set.
     * 
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public double getCumPct(int v) {
    	return getCumPct(new Long(v));       
    }
    
    /**
     * Returns the cumulative percentatge of values less than or equal to v
     * (as a proportion between 0 and 1).
     * <p>
     * Returns 0 if v is not comparable to the values set.
     * 
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public double getCumPct(long v) {
    	return getCumPct(new Long(v));         
    }
    
    /**
     * Returns the cumulative percentatge of values less than or equal to v
     * (as a proportion between 0 and 1).
     * <p>
     * Returns 0 if v is not comparable to the values set.
     * 
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public double getCumPct(char v) {
    	return getCumPct(new Character(v));         
    }
}
