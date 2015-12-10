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

import org.apache.commons.math4.exception.NullArgumentException;

/**
 * Interface representing <a
 * href="http://mathworld.wolfram.com/Monoid.html">field</a> elements.
 * 
 * @param <T>
 *            the type of the elements
 */
public interface MonoidElement<T> {

    /**
     * Compute this + a.
     * 
     * @param a
     *            element to add
     * @return a new element representing this + a
     * @throws NullArgumentException
     *             if {@code addend} is {@code null}.
     */
    T add(T a) throws NullArgumentException;

    /**
     * Compute this - a.
     * 
     * @param a
     *            element to subtract
     * @return a new element representing this - a
     * @throws NullArgumentException
     *             if {@code a} is {@code null}.
     */
    T subtract(T a) throws NullArgumentException;

    /**
     * Returns the additive inverse of {@code this} element.
     * 
     * @return the opposite of {@code this}.
     */
    T negate();

    // TODO some getMonoid() would be appreciated
}
