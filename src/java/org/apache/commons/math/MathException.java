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
package org.apache.commons.math;

import java.io.Serializable;

import org.apache.commons.lang.exception.NestableException;

/**
 * A generic exception indicating problems in the math package.
 * @version $Revision: 1.16 $ $Date: 2004/06/02 00:05:28 $
 */
public class MathException extends NestableException implements Serializable {

    /** Serializable version identifier */
    static final long serialVersionUID = -8594613561393443827L;
    
    /**
     * Constructs a MathException
     */
    public MathException() {
        this(null, null);
    }

    /**
     * Create an exception with a given error message.
     * @param message message describing the problem
     */
    public MathException(final String message) {
        this(message, null);
    }

    /**
     * Create an exception with a given error message and root cause.
     * @param message message describing the problem
     * @param throwable caught exception causing this problem
     */
    public MathException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Create an exception with a given root cause.
     * @param throwable caught exception causing this problem
     */
    public MathException(final Throwable throwable) {
        this(null, throwable);
    }
}
