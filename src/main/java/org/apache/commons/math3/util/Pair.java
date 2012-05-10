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
package org.apache.commons.math3.util;

/**
 * Generic pair.
 * <br/>
 * Although the instances of this class are immutable, it is impossible
 * to ensure that the references passed to the constructor will not be
 * modified by the caller.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 *
 * @version $Id$
 * @since 3.0
 */
public class Pair<K, V> {
    /** Key. */
    private final K key;
    /** Value. */
    private final V value;
    /** Whether the pair contents can be assumed to be immutable. */
    private final boolean isImmutable;
    /** Cached has code. */
    private final int cachedHashCode;

    /**
     * Create an entry representing a mapping from the specified key to the
     * specified value.
     * If the pair can be assumed to be immutable, the hash code will be
     * cached.
     *
     * @param k Key.
     * @param v Value.
     * @param assumeImmutable Whether the pair contents can be assumed to
     * be immutable.
     */
    public Pair(K k, V v, boolean assumeImmutable) {
        key = k;
        value = v;
        isImmutable = assumeImmutable;
        cachedHashCode = computeHashCode();
    }

    /**
     * Create an entry representing a mapping from the specified key to the
     * specified value.
     *
     * @param k Key.
     * @param v Value.
     */
    public Pair(K k, V v) {
        this(k, v, false);
    }

    /**
     * Create an entry representing the same mapping as the specified entry.
     * If the pair can be assumed to be immutable, the hash code will be
     * cached.
     *
     * @param entry Entry to copy.
     * @param assumeImmutable Whether the pair contents can be assumed to
     * be immutable.
     */
    public Pair(Pair<? extends K, ? extends V> entry, boolean assumeImmutable) {
        this(entry.getKey(), entry.getValue(), assumeImmutable);
    }

    /**
     * Create an entry representing the same mapping as the specified entry.
     *
     * @param entry Entry to copy.
     */
    public Pair(Pair<? extends K, ? extends V> entry) {
        this(entry, false);
    }

    /**
     * Get the key.
     *
     * @return the key.
     */
    public K getKey() {
        return key;
    }

    /**
     * Get the value.
     *
     * @return the value.
     */
    public V getValue() {
        return value;
    }

    /**
     * Compare the specified object with this entry for equality.
     *
     * @param o Object.
     * @return {@code true} if the given object is also a map entry and
     * the two entries represent the same mapping.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Pair)) {
            return false;
        } else {
            Pair<?, ?> oP = (Pair<?, ?>) o;
            return (key == null ?
                    oP.getKey() == null :
                    key.equals(oP.getKey())) &&
                (value == null ?
                 oP.getValue() == null :
                 value.equals(oP.getValue()));
        }
    }

    /**
     * Compute a hash code.
     *
     * @return the hash code value.
     */
    @Override
    public int hashCode() {
        return isImmutable ? cachedHashCode : computeHashCode();
    }

    /**
     * Compute a hash code.
     *
     * @return the hash code value.
     */
    private final int computeHashCode() {
        int result = key == null ? 0 : key.hashCode();

        final int h = value == null ? 0 : value.hashCode();
        result = 37 * result + h ^ (h >>> 16);

        return result;
    }
}
