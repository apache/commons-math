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

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.text.MessageFormat;
import java.util.Locale;

import org.apache.commons.math.exception.util.ArgUtils;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.util.SerializablePair;

/**
 * This class is the base class for all exceptions.
 *
 * @since 3.0
 * @version $Revision$ $Date$
 */
public class MathRuntimeException extends RuntimeException
    implements MathThrowable {
    /** Serializable version Id. */
    private static final long serialVersionUID = -6024911025449780478L;
    /**
     * Various informations that enrich the informative message.
     */
    private final List<SerializablePair<Localizable, Object[]>> messages
        = new ArrayList<SerializablePair<Localizable, Object[]>>();
    /**
     * Arbitrary context information.
     */
    private final Map<String, Object> context = new HashMap<String, Object>();

    /**
     * Builds an exception.
     */
    public MathRuntimeException() {}

    /**
     * Builds an exception.
     *
     * @param cause Cause of the error (may be null).
     */
    public MathRuntimeException(final Throwable cause) {
        super(cause);
    }

    /** {@inheritDoc} */
    public void addMessage(Localizable pattern,
                           Object ... arguments) {
        messages.add(new SerializablePair<Localizable, Object[]>(pattern,
                                                                 ArgUtils.flatten(arguments)));
    }

    /** {@inheritDoc} */
    public void setContext(String key, Object value) {
        context.put(key, value);
    }

    /** {@inheritDoc} */
    public Object getContext(String key) {
        return context.get(key);
    }

    /** {@inheritDoc} */
    public Set<String> getContextKeys() {
        return context.keySet();
    }

    /**
     * Gets the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated.
     * @return the localized message.
     */
    public String getMessage(final Locale locale) {
        return buildMessage(locale, ": ");
    }

    /**
     * Gets the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated.
     * @param separator Separator inserted between the message parts.
     * @return the localized message.
     */
    public String getMessage(final Locale locale,
                             final String separator) {
        return buildMessage(locale, separator);
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

    /**
     * Builds a message string.
     *
     * @param locale Locale in which the message should be translated.
     * @param separator Message separator.
     * @return a localized message string.
     */
    private String buildMessage(Locale locale,
                                String separator) {
        final StringBuilder sb = new StringBuilder();
        int count = 0;
        final int len = messages.size();
        for (SerializablePair<Localizable, Object[]> pair : messages) {
            final MessageFormat fmt = new MessageFormat(pair.getKey().getLocalizedString(locale),
                                                        locale);
            sb.append(fmt.format(pair.getValue()));
            if (++count < len) {
                // Add a separator if there are other messages.
                sb.append(separator);
            }
        }

        return sb.toString();
    }
}
