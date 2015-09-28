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
package org.apache.commons.math4;

/**
 * Interface representing a <a href="http://mathworld.wolfram.com/Monoid.html">monoid</a>.
 * <p>
 * Classes implementing this interface will often be singletons.
 * </p>
 * @param <T> the type of the elements
 */
public interface Monoid<T> {

    /** Get the additive identity of the ring.
     * <p>
     * The additive identity is the element e<sub>0</sub> of the ring such that
     * for all elements a of the field, the equalities a + e<sub>0</sub> =
     * e<sub>0</sub> + a = a hold.
     * </p>
     * @return additive identity of the field
     */
    T getZero();

    /**
     * Returns the runtime class of the FieldElement.
     *
     * @return The {@code Class} object that represents the runtime
     *         class of this object.
     */
    Class<? extends MonoidElement<T>> getRuntimeClass(); //should return T, we suppose

}
