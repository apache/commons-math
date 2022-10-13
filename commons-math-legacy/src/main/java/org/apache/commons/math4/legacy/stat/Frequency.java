/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math4.legacy.stat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;

/**
 * Maintains a frequency distribution.
 *
 * <p>The values are ordered using the default (natural order), unless a
 * <code>Comparator</code> is supplied in the constructor.</p>
 *
 * @param <T> a comparable type used in the frequency distribution
 */
public class Frequency<T extends Comparable<T>> {
    /** underlying collection. */
    private final SortedMap<T, Long> freqTable;

    /**
     * Default constructor.
     */
    public Frequency() {
        freqTable = new TreeMap<>();
    }

    /**
     * Constructor allowing values Comparator to be specified.
     *
     * @param comparator Comparator used to order values
     */
    public Frequency(Comparator<T> comparator) {
        freqTable = new TreeMap<>(comparator);
    }

    /**
     * Return a string representation of this frequency distribution.
     *
     * @return a string representation.
     */
    @Override
    public String toString() {
        NumberFormat nf = NumberFormat.getPercentInstance();
        StringBuilder outBuffer = new StringBuilder();
        outBuffer.append("Value \t Freq. \t Pct. \t Cum Pct. \n");
        Iterator<T> iter = freqTable.keySet().iterator();
        while (iter.hasNext()) {
            T value = iter.next();
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
     */
    public void addValue(T v) {
        incrementValue(v, 1);
    }

    /**
     * Increments the frequency count for v.
     *
     * @param v the value to add.
     * @param increment the amount by which the value should be incremented
     * @since 3.1
     */
    public void incrementValue(T v, long increment) {
        Long count = freqTable.get(v);
        if (count == null) {
            freqTable.put(v, Long.valueOf(increment));
        } else {
            freqTable.put(v, Long.valueOf(count.longValue() + increment));
        }
    }

    /** Clears the frequency table. */
    public void clear() {
        freqTable.clear();
    }

    /**
     * Returns an Iterator over the set of values that have been added.
     *
     * @return values Iterator
     */
    public Iterator<T> valuesIterator() {
        return freqTable.keySet().iterator();
    }

    /**
     * Return an Iterator over the set of keys and values that have been added.
     * Using the entry set to iterate is more efficient in the case where you
     * need to access respective counts as well as values, since it doesn't
     * require a "get" for every key...the value is provided in the Map.Entry.
     *
     * @return entry set Iterator
     * @since 3.1
     */
    public Iterator<Map.Entry<T, Long>> entrySetIterator() {
        return freqTable.entrySet().iterator();
    }

    //-------------------------------------------------------------------------

    /**
     * Returns the sum of all frequencies.
     *
     * @return the total frequency count.
     */
    public long getSumFreq() {
        long result = 0;
        Iterator<Long> iterator = freqTable.values().iterator();
        while (iterator.hasNext())  {
            result += iterator.next().longValue();
        }
        return result;
    }

    /**
     * Returns the number of values equal to v.
     *
     * @param v the value to lookup.
     * @return the frequency of v.
     */
    public long getCount(T v) {
        long result = 0;
        Long count =  freqTable.get(v);
        if (count != null) {
            result = count.longValue();
        }
        return result;
    }

    /**
     * Returns the number of values in the frequency table.
     *
     * @return the number of unique values that have been added to the frequency table.
     * @see #valuesIterator()
     */
    public int getUniqueCount(){
        return freqTable.keySet().size();
    }

    /**
     * Returns the percentage of values that are equal to v
     * (as a proportion between 0 and 1).
     * <p>
     * Returns <code>Double.NaN</code> if no values have been added.
     * </p>
     *
     * @param v the value to lookup
     * @return the proportion of values equal to v
     */
    public double getPct(T v) {
        final long sumFreq = getSumFreq();
        if (sumFreq == 0) {
            return Double.NaN;
        }
        return (double) getCount(v) / (double) sumFreq;
    }

    //-----------------------------------------------------------------------------------------

    /**
     * Returns the cumulative frequency of values less than or equal to v.
     *
     * @param v the value to lookup.
     * @return the proportion of values equal to v
     */
    public long getCumFreq(T v) {
        if (getSumFreq() == 0) {
            return 0;
        }
        Comparator<? super T> c = freqTable.comparator();
        if (c == null) {
            c = new NaturalComparator<>();
        }
        long result = 0;

        Long value = freqTable.get(v);
        if (value != null) {
            result = value.longValue();
        }

        if (c.compare(v, freqTable.firstKey()) < 0) {
            return 0;  // v is comparable, but less than first value
        }

        if (c.compare(v, freqTable.lastKey()) >= 0) {
            return getSumFreq();    // v is comparable, but greater than the last value
        }

        Iterator<T> values = valuesIterator();
        while (values.hasNext()) {
            T nextValue = values.next();
            if (c.compare(v, nextValue) > 0) {
                result += getCount(nextValue);
            } else {
                return result;
            }
        }
        return result;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Returns the cumulative percentage of values less than or equal to v
     * (as a proportion between 0 and 1).
     * <p>
     * Returns <code>Double.NaN</code> if no values have been added.
     * </p>
     *
     * @param v the value to lookup
     * @return the proportion of values less than or equal to v
     */
    public double getCumPct(T v) {
        final long sumFreq = getSumFreq();
        if (sumFreq == 0) {
            return Double.NaN;
        }
        return (double) getCumFreq(v) / (double) sumFreq;
    }

    /**
     * Returns the mode value(s) in comparator order.
     *
     * @return a list containing the value(s) which appear most often.
     * @since 3.3
     */
    public List<T> getMode() {
        long mostPopular = 0; // frequencies are always positive

        // Get the max count first, so we avoid having to recreate the List each time
        for(Long l : freqTable.values()) {
            long frequency = l.longValue();
            if (frequency > mostPopular) {
                mostPopular = frequency;
            }
        }

        List<T> modeList = new ArrayList<>();
        for (Entry<T, Long> ent : freqTable.entrySet()) {
            long frequency = ent.getValue().longValue();
            if (frequency == mostPopular) {
               modeList.add(ent.getKey());
            }
        }
        return modeList;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Merge another Frequency object's counts into this instance.
     * This Frequency's counts will be incremented (or set when not already set)
     * by the counts represented by other.
     *
     * @param other the other {@link Frequency} object to be merged
     * @throws NullArgumentException if {@code other} is null
     * @since 3.1
     */
    public void merge(final Frequency<T> other) throws NullArgumentException {
        NullArgumentException.check(other, LocalizedFormats.NULL_NOT_ALLOWED);

        final Iterator<Map.Entry<T, Long>> iter = other.entrySetIterator();
        while (iter.hasNext()) {
            final Map.Entry<T, Long> entry = iter.next();
            incrementValue(entry.getKey(), entry.getValue().longValue());
        }
    }

    /**
     * Merge a {@link Collection} of {@link Frequency} objects into this instance.
     * This Frequency's counts will be incremented (or set when not already set)
     * by the counts represented by each of the others.
     *
     * @param others the other {@link Frequency} objects to be merged
     * @throws NullArgumentException if the collection is null
     * @since 3.1
     */
    public void merge(final Collection<Frequency<T>> others) throws NullArgumentException {
        NullArgumentException.check(others, LocalizedFormats.NULL_NOT_ALLOWED);

        for (final Frequency<T> freq : others) {
            merge(freq);
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * A Comparator that compares comparable objects using the
     * natural order. Copied from Commons Collections ComparableComparator.
     *
     * @param <U> the type of the objects compared
     */
    private static class NaturalComparator<U extends Comparable<U>> implements Comparator<U> {
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
         */
        @Override
        public int compare(U o1, U o2) {
            return o1.compareTo(o2);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result +
                 ((freqTable == null) ? 0 : freqTable.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Frequency<?>)) {
            return false;
        }
        Frequency<?> other = (Frequency<?>) obj;
        return freqTable.equals(other.freqTable);
    }
}
