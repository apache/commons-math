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
 * href="http://mathworld.wolfram.com/Field.html">field</a> elements.
 * 
 * @param <T>
 *            the type of the elements
 */
public interface RingElement<T> extends MonoidElement<T> {

    /**
     * Compute n &times; this. Multiplication by an integer number is defined as
     * the following sum <center> n &times; this =
     * &sum;<sub>i=1</sub><sup>n</sup> this. </center>
     * 
     * @param n
     *            Number of times {@code this} must be added to itself.
     * @return A new element representing n &times; this.
     */
    T multiply(int n);

    /**
     * Compute this &times; a.
     * 
     * @param a
     *            element to multiply
     * @return a new element representing this &times; a
     * @throws NullArgumentException
     *             if {@code a} is {@code null}.
     */
    T multiply(T a) throws NullArgumentException;

    // TODO some getRing() would be appreciated
}
