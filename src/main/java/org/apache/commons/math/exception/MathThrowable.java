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
import java.util.Set;

import org.apache.commons.math.exception.util.Localizable;

/**
* Interface for commons-math throwables.
*
* @version $Revision$ $Date$
* @since 2.2
*/
public interface MathThrowable {
    /**
     * Sets a message.
     *
     * @param pattern Message.
     */
    void addMessage(Localizable pattern);

    /**
     * Sets a message.
     *
     * @param pattern Message pattern.
     * @param arguments Values for replacing the placeholders in the message
     * pattern.
     */
    void addMessage(Localizable pattern,
                    Object ... arguments);

    /**
     * Sets the context (key, value) pair.
     * Keys are assumed to be unique within an instance. If the same key is
     * assigned a new value, the previous one will be lost.
     *
     * @param key Context key (not null).
     * @param value Context value.
     */
    void setContext(String key, Object value);

    /**
     * Gets the value associated to the given context key.
     *
     * @param key Context key.
     * @return the context value or {@code null} if the key does not exist.
     */
    Object getContext(String key);

    /**
     * Gets all the keys stored in the exception
     *
     * @return the set of keys.
     */
    Set<String> getContextKeys();

    /**
     * Gets the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated
     * @return localized message
     */
    String getMessage(final Locale locale);

    /**
     * Gets the message in a conventional US locale.
     *
     * @return localized message
     */
    String getMessage();

    /**
     * Gets the message in the system default locale.
     *
     * @return localized message
     */
    String getLocalizedMessage();

}
