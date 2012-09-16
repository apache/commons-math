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
package org.apache.commons.math3.stat.descriptive.rank;

import java.io.Serializable;

import org.apache.commons.math3.exception.NullArgumentException;


/**
 * Returns the median of the available values.  This is the same as the 50th percentile.
 * See {@link Percentile} for a description of the algorithm used.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.</p>
 *
 * @version $Id$
 */
public class Median extends Percentile implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -3961477041290915687L;

    /**
     * Default constructor.
     */
    public Median() {
        // No try-catch or advertised exception - arg is valid
        super(50.0);
    }

    /**
     * Copy constructor, creates a new {@code Median} identical
     * to the {@code original}
     *
     * @param original the {@code Median} instance to copy
     * @throws NullArgumentException if original is null
     */
    public Median(Median original) throws NullArgumentException {
        super(original);
    }

}
