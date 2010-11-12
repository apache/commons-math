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

import java.util.Locale;

import org.apache.commons.math.exception.util.ArgUtils;
import org.apache.commons.math.exception.util.MessageFactory;
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
public class MathUserException extends RuntimeException {
    /** Serializable version Id. */
    private static final long serialVersionUID = -6024911025449780478L;
    /**
     * Pattern used to build the message (problem description).
     */
    private final Localizable pattern;
    /**
     * Arguments used to build the message.
     */
    private final Object[] arguments;

    /**
     * Default constructor.
     */
    public MathUserException() {
        this(null);
    }

    /**
     * @param cause Cause of the error.
     * @param args Arguments.
     */
    public MathUserException(Throwable cause,
                             Object ... args) {
        this(null, cause, args);
    }

    /**
     * @param pattern Message pattern explaining the cause of the error.
     * @param cause Cause of the error.
     * @param args Arguments.
     */
    public MathUserException(Localizable pattern,
                             Throwable cause,
                             Object ... args) {
        this.pattern  = pattern;
        arguments = ArgUtils.flatten(args);
    }

    /**
     * Get the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated.
     * @return the localized message.
     */
    public String getMessage(final Locale locale) {
        return MessageFactory.buildMessage(locale,
                                           pattern,
                                           LocalizedFormats.USER_EXCEPTION,
                                           arguments);
    }

   /** {@inheritDoc} */
    @Override
    public String getMessage() {
        return getMessage(Locale.US);
    }

    /** {@inheritDoc} */
    @Override
    public String getLocalizedMessage() {
        return getMessage(Locale.getDefault());
    }
}
