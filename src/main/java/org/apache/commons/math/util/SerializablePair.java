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
package org.apache.commons.math.util;

import java.io.Serializable;

/**
 * Generic pair.
 * Immutable class.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 *
 * @version $Revision$ $Date$
 * @since 3.0
 */
public class SerializablePair<K extends Serializable, V extends Serializable>
    extends Pair<K, V>
    implements Serializable {
    /**
     * Create an entry representing a mapping from the specified key to the
     * specified value.
     *
     * @param k Key.
     * @param v Value.
     */
    public SerializablePair(K k, V v) {
        super(k, v);
    }

    /**
     * Create an entry representing the same mapping as the specified entry.
     *
     * @param entry Entry to copy.
     */
    public SerializablePair(SerializablePair<? extends K, ? extends V> entry) {
        super(entry);
    }
}
