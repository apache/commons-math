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

/**
 * Signals a configuration problem with any of the factory methods.
 * @version $Revision$ $Date$
 */
public class MathConfigurationException extends MathException implements Serializable {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = -7958299004965931723L;

    /**
     * Default constructor.
     */
    public MathConfigurationException() {
        this(null, null);
    }

    /**
     * Construct an exception with the given message.
     * @param message message describing the problem
     */
    public MathConfigurationException(final String message) {
        this(message, null);
    }

    /**
     * Construct an exception with the given message and root cause.
     * @param message message describing the problem
     * @param throwable caught exception causing this problem
     */
    public MathConfigurationException(
        final String message,
        final Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Construct an exception with the given root cause.
     * @param throwable caught exception causing this problem
     */
    public MathConfigurationException(final Throwable throwable) {
        this(null, throwable);
    }
}
