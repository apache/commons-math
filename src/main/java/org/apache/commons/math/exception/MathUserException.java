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
package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * This class is intended as a sort of communication channel between
 * layers of <em>user</em> code separated from each other by calls to
 * the Commons-Math library.
 * The Commons-Math code will never catch such an exception.
 *
 * @since 2.2
 * @version $Revision$ $Date$
 */
public class MathUserException extends MathRuntimeException {
    /** Serializable version Id. */
    private static final long serialVersionUID = -6024911025449780478L;

    /**
     * Build an exception with a default message.
     */
    public MathUserException() {
        addMessage(LocalizedFormats.USER_EXCEPTION);
    }

    /**
     * Build an exception with a default message.
     * @param cause Cause of the error (may be null).
     */
    public MathUserException(final Throwable cause) {
        super(cause);
        addMessage(LocalizedFormats.USER_EXCEPTION);
    }

    /**
     * Builds an exception with a localizable message.
     *
     * @param pattern Format specifier.
     * @param arguments Format arguments.
     */
    public MathUserException(final Localizable pattern,
                             final Object ... arguments) {
        addMessage(pattern, arguments);
    }

    /**
     * Builds an exception with a localizable message.
     *
     * @param cause Cause of the error (may be null).
     * @param pattern Format specifier.
     * @param arguments Format arguments.
     */
    public MathUserException(final Throwable cause,
                             final Localizable pattern,
                             final Object ... arguments) {
        super(cause);
        addMessage(pattern, arguments);
    }
}
