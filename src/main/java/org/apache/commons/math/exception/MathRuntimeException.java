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
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.MessageFactory;

/**
 * This class is primarily intended as a base class for exceptions
 * that must wrap low-level exceptions in order to propagate an
 * exception that better corresponds to the high-level action that
 * triggered the problem.
 *
 * @since 3.0
 * @version $Revision$ $Date$
 */
public class MathRuntimeException extends RuntimeException
    implements MathThrowable {
    /** Serializable version Id. */
    private static final long serialVersionUID = -6024911025449780478L;
    /**
     * Pattern used to build the specific part of the message (problem description).
     */
    private final Localizable specific;
    /**
     * Pattern used to build the general part of the message (problem description).
     */
    private final Localizable general;
    /**
     * Arguments used to build the message.
     */
    private final Object[] arguments;

    /**
     * Build an exception from two patterns (specific and general) and
     * an argument list.
     *
     * @param cause Cause of the error (may be null).
     * @param specific Format specifier for the specific part (may be null).
     * @param general Format specifier for the general part (may be null).
     * @param arguments Format arguments. They will be substituted in
     * <em>both</em> the {@code general} and {@code specific} format specifiers.
     */
    public MathRuntimeException(final Throwable cause,
                                final Localizable specific,
                                final Localizable general,
                                final Object ... arguments) {
        super(cause);
        this.specific = specific;
        this.general = general;
        this.arguments = ArgUtils.flatten(arguments);
    }

    /**
     * Wrap an exception.
     *
     * @param cause Cause of the error (may be null).
     */
    public MathRuntimeException(final Throwable cause) {
        this(cause, null, null);
    }

    /** {@inheritDoc} */
    public Localizable getSpecificPattern() {
        return specific;
    }

    /** {@inheritDoc} */
    public Localizable getGeneralPattern() {
        return general;
    }

    /** {@inheritDoc} */
    public Object[] getArguments() {
        return arguments.clone();
    }

    /**
     * Get the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated.
     * @return the localized message.
     */
    public String getMessage(final Locale locale) {
        return MessageFactory.buildMessage(locale, specific, general, arguments);
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
