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
 * Immutable class.
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

    /**
     * Create an entry representing a mapping from the specified key to the
     * specified value.
     *
     * @param k Key.
     * @param v Value.
     */
    public Pair(K k, V v) {
        key = k;
        value = v;
    }

    /**
     * Create an entry representing the same mapping as the specified entry.
     *
     * @param entry Entry to copy.
     */
    public Pair(Pair<? extends K, ? extends V> entry) {
        key = entry.getKey();
        value = entry.getValue();
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
        return (key == null ? 0 : key.hashCode()) ^
            (value == null ? 0 : value.hashCode());
    }
}
