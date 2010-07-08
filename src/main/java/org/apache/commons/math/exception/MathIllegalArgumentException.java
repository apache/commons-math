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
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math.util.Localizable;

/**
 * Base class for all preconditions violation exceptions.
 * This class is not intended to be instantiated directly: it should serve
 * as a base class to create all the exceptions that share the semantics of
 * the standard {@link IllegalArgumentException}, but must also provide a
 * localized message.
 *
 * @since 2.2
 * @version $Revision$ $Date$
 */
public class MathIllegalArgumentException extends IllegalArgumentException {
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
    protected MathIllegalArgumentException(Localizable specific,
                                           Localizable general,
                                           Object ... args) {
        this.specific = specific;
        this.general = general;
        arguments = flatten(args).toArray();
    }
    /**
     * @param general Message pattern explaining the cause of the error.
     * @param args Arguments.
     */
    protected MathIllegalArgumentException(Localizable general,
                                           Object ... args) {
        this(null, general, args);
    }

    /** {@inheritDoc} */
    @Override
    public String getMessage() {
        final StringBuilder sb = new StringBuilder();

        if (specific != null) {
            sb.append(MessageFactory.buildMessage(Locale.US, specific, arguments));
            sb.append(": ");
        }
        sb.append(MessageFactory.buildMessage(Locale.US, general, arguments));

        return sb.toString();
    }
    
    /** {@inheritDoc} */
    @Override
    public String getLocalizedMessage() {
        final StringBuilder sb = new StringBuilder();

        if (specific != null) {
            sb.append(MessageFactory.buildMessage(Locale.getDefault(), specific, arguments));
            sb.append(": ");
        }
        sb.append(MessageFactory.buildMessage(Locale.getDefault(), general, arguments));

        return sb.toString();
    }

    /**
     * Transform a multidimensional array into a one-dimensional list.
     *
     * @param array Array (possibly multidimensional).
     * @return a list of all the {@code Object} instances contained in
     * {@code array}.
     */
    private List<Object> flatten(Object[] array) {
        final List<Object> list = new ArrayList<Object>();
        if (array != null) {
            for (Object o : array) {
                if (o instanceof Object[]) {
                    list.addAll(flatten((Object[]) o));
                } else {
                    list.add(o);
                }
            }
        }
        return list;
    }
}
