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

/**
 * Base class for all exceptions that signal a mismatch between the
 * current state and the user's expectations.
 *
 * @since 3.0
 * @version $Revision$ $Date$
 */
public class MathIllegalStateException extends IllegalStateException {

    /** Serializable version Id. */
    private static final long serialVersionUID = -6024911025449780478L;

    /**
     * Pattern used to build the message (specific context).
     */
    private final Localizable specific;
    /**
     * Pattern used to build the message (general problem description).
     */
    private final Localizable general;
    /**
     * Arguments used to build the message.
     */
    private final Object[] arguments;

    /**
     * @param specific Message pattern providing the specific context of
     * the error.
     * @param general Message pattern explaining the cause of the error.
     * @param args Arguments.
     */
    public MathIllegalStateException(Localizable specific,
                                     Localizable general,
                                     Object ... args) {
        this.specific = specific;
        this.general = general;
        arguments = ArgUtils.flatten(args);
    }
    /**
     * @param general Message pattern explaining the cause of the error.
     * @param args Arguments.
     */
    public MathIllegalStateException(Localizable general,
                                     Object ... args) {
        this(null, general, args);
    }

    /**
     * Get the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated.
     *
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
