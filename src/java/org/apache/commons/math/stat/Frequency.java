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
package org.apache.commons.math.stat;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Comparator;
import java.util.TreeMap;

/** 
 * Maintains a frequency distribution.
 * <p>
 * Accepts int, long, char or Object values.  New values added must be 
 * comparable to those that have been added, otherwise the add method will 
 * throw an IllegalArgumentException.  
 * <p>
 * Integer values (int, long, Integer, Long) are not distinguished by type -- 
 * i.e. <code>addValue(new Long(2)), addValue(2), addValue(2l)</code> all have
 * the same effect (similarly for arguments to <code>getCount,</code> etc.).
 * <p>
 * The values are ordered using the default (natural order), unless a  
 * <code>Comparator</code> is supplied in the constructor.
 *
 * @version $Revision$ $Date$
 */
public class Frequency implements Serializable {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = -3845586908418844111L;

    /** underlying collection */
    private TreeMap freqTable = null;

    /**
     * Default constructor.
     */
    public Frequency() {
        freqTable = new TreeMap();
    }
    
    /**
     * Constructor allowing values Comparator to be specified.
     * 
     * @param comparator Comparator used to order values
     */
    public Frequency(Comparator comparator) {
        freqTable = new TreeMap(comparator);
    }

    /**
     * Return a string representation of this frequency
     * distribution.
     * 
     * @return a string representation.
     */
    public String toString() {
        NumberFormat nf = NumberFormat.getPercentInstance();
        StringBuffer outBuffer = new StringBuffer();
        outBuffer.append("Value \t Freq. \t Pct. \t Cum Pct. \n");
        Iterator iter = freqTable.keySet().iterator();
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
     * Adds 1 to the frequency count for v.
     * 
     * @param v the value to add.
     * @throws IllegalArgumentException if <code>v</code> is not comparable.
     */
    public void addValue(Object v) {
        Object obj = v;
        if (v instanceof Integer) {
           obj = new Long(((Integer) v).longValue());
        }
        try {
            Long count = (Long) freqTable.get(obj);
            if (count == null) {
                freqTable.put(obj, new Long(1));
            } else {
                freqTable.put(obj, new Long(count.longValue() + 1));
            }
        } catch (ClassCastException ex) {   
            //TreeMap will throw ClassCastException if v is not comparable
            throw new IllegalArgumentException("Value not comparable to existing values.");
        }
    }

    /**
     * Adds 1 to the frequency count for v.
     * 
     * @param v the value to add.
     */
    public void addValue(int v) {
        addValue(new Long(v));
    }
    
    /**
     * Adds 1 to the frequency count for v.
     * 
     * @param v the value to add.
     */
    public void addValue(Integer v) {
        addValue(new Long(v.longValue()));
    }

    /**
     * Adds 1 to the frequency count for v.
     * 
     * @param v the value to add.
     */
    public void addValue(long v) {
        addValue(new Long(v));
    }
    
    /**
     * Adds 1 to the frequency count for v.
     * 
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
     * <p>
     * If added values are itegral (i.e., integers, longs, Integers, or Longs), 
     * they are converted to Longs when they are added, so the objects returned
     * by the Iterator will in this case be Longs.
     * 
     * @return values Iterator
     */
    public Iterator valuesIterator() {
        return freqTable.keySet().iterator();
    }
    
    //-------------------------------------------------------------------------
    
    /**
     * Returns the sum of all frequencies.
     * 
     * @return the total frequency count.
     */
    public long getSumFreq() {
        long result = 0;
        Iterator iterator = freqTable.values().iterator();
        while (iterator.hasNext())  {
            result += ((Long) iterator.next()).longValue();
        }
        return result;
    }

    /**
     * Returns the number of values = v.
     * 
     * @param v the value to lookup.
     * @return the frequency of v.
     */
    public long getCount(Object v) {
        if (v instanceof Integer) {
            return getCount(((Integer) v).longValue());
        }
        long result = 0;
        try { 
            Long count =  (Long) freqTable.get(v);
            if (count != null) {
                result = count.longValue();
            }
        } catch (ClassCastException ex) {
            // ignore and return 0 -- ClassCastException will be thrown if value is not comparable
        }
        return result;
    }

    /**
     * Returns the number of values = v.
     * 
     * @param v the value to lookup.
     * @return the frequency of v.
     */
    public long getCount(int v) {
        return getCount(new Long(v));
    }
    
    /**
     * Returns the number of values = v.
     * 
     * @param v the value to lookup.
     * @return the frequency of v.
     */
    public long getCount(long v) {
        return getCount(new Long(v));
    }
    
    /**
     * Returns the number of values = v.
     * 
     * @param v the value to lookup.
     * @return the frequency of v.
     */
    public long getCount(char v) {
        return getCount(new Character(v));
    }
    
    //-------------------------------------------------------------

    /**
      * Returns the percentage of values that are equal to v
     * (as a proportion between 0 and 1).
     * <p>
     * Returns <code>Double.NaN</code> if no values have been added.
     * 
     * @param v the value to lookup
     * @return the proportion of values equal to v
     */
    public double getPct(Object v) {
        if (getSumFreq() == 0) {
            return Double.NaN;
        }
        return (double) getCount(v) / (double) getSumFreq();        
    }
    
    /**
      * Returns the percentage of values that are equal to v
     * (as a proportion between 0 and 1).
     * 
     * @param v the value to lookup
     * @return the proportion of values equal to v
     */
    public double getPct(int v) {
        return getPct(new Long(v));       
    }
    
    /**
      * Returns the percentage of values that are equal to v
     * (as a proportion between 0 and 1).
     * 
     * @param v the value to lookup
     * @return the proportion of values equal to v
     */
    public double getPct(long v) {
        return getPct(new Long(v));         
    }
    
    /**
     * Returns the percentage of values that are equal to v
     * (as a proportion between 0 and 1).
     * 
     * @param v the value to lookup
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
        if (getSumFreq() == 0) {
            return 0;
        }
        if (v instanceof Integer) {
            return getCumFreq(((Integer) v).longValue());
        }
        Comparator c = freqTable.comparator();
        if (c == null) {
            c = new NaturalComparator();
        }
        long result = 0;
        
        try {
            Long value = (Long) freqTable.get(v);
            if (value != null) {
                result = value.longValue();
            }
        } catch (ClassCastException ex) {
            return result;   // v is not comparable
        }
        
        if (c.compare(v, freqTable.firstKey()) < 0) {
            return 0;  // v is comparable, but less than first value
        }
        
        if (c.compare(v, freqTable.lastKey()) >= 0) {
            return getSumFreq();    // v is comparable, but greater than the last value
        }
        
        Iterator values = valuesIterator();
        while (values.hasNext()) {
            Object nextValue = values.next();
            if (c.compare(v, nextValue) > 0) {
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
     * @param v the value to lookup
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
     * @param v the value to lookup
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
     * @param v the value to lookup
     * @return the proportion of values equal to v
     */
    public long getCumFreq(char v) {
        return getCumFreq(new Character(v));         
    }
    
    //----------------------------------------------------------------------------------------------
    
     /**
     * Returns the cumulative percentage of values less than or equal to v
     * (as a proportion between 0 and 1).
     * <p>
     * Returns <code>Double.NaN</code> if no values have been added.
     * Returns 0 if at least one value has been added, but v is not comparable
     * to the values set.
     * 
     * @param v the value to lookup
     * @return the proportion of values less than or equal to v
     */
    public double getCumPct(Object v) {
        if (getSumFreq() == 0) {
            return Double.NaN;
        }
        return (double) getCumFreq(v) / (double) getSumFreq();        
    }
    
    /**
     * Returns the cumulative percentage of values less than or equal to v
     * (as a proportion between 0 and 1).
     * <p>
     * Returns 0 if v is not comparable to the values set.
     * 
     * @param v the value to lookup
     * @return the proportion of values less than or equal to v
     */
    public double getCumPct(int v) {
        return getCumPct(new Long(v));       
    }
    
    /**
     * Returns the cumulative percentage of values less than or equal to v
     * (as a proportion between 0 and 1).
     * <p>
     * Returns 0 if v is not comparable to the values set.
     * 
     * @param v the value to lookup
     * @return the proportion of values less than or equal to v
     */
    public double getCumPct(long v) {
        return getCumPct(new Long(v));         
    }
    
    /**
     * Returns the cumulative percentage of values less than or equal to v
     * (as a proportion between 0 and 1).
     * <p>
     * Returns 0 if v is not comparable to the values set.
     * 
     * @param v the value to lookup
     * @return the proportion of values less than or equal to v
     */
    public double getCumPct(char v) {
        return getCumPct(new Character(v));         
    }
    
    /**
     * A Comparator that compares comparable objects using the
     * natural order.  Copied from Commons Collections ComparableComparator.
     */
    private static class NaturalComparator implements Comparator {
        /**
         * Compare the two {@link Comparable Comparable} arguments.
         * This method is equivalent to:
         * <pre>(({@link Comparable Comparable})o1).{@link Comparable#compareTo compareTo}(o2)</pre>
         * 
         * @param  o1 the first object 
         * @param  o2 the second object
         * @return  result of comparison
         * @throws NullPointerException when <i>o1</i> is <code>null</code>, 
         *         or when <code>((Comparable)o1).compareTo(o2)</code> does
         * @throws ClassCastException when <i>o1</i> is not a {@link Comparable Comparable}, 
         *         or when <code>((Comparable)o1).compareTo(o2)</code> does
         */
        public int compare(Object o1, Object o2) {
            return ((Comparable)o1).compareTo(o2);
        }
    }
}
