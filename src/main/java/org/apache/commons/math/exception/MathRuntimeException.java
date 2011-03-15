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
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
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
    private List<SerializablePair<Localizable, Object[]>> messages
        = new ArrayList<SerializablePair<Localizable, Object[]>>();
    /**
     * Arbitrary context information.
     */
    private Map<String, Object> context = new HashMap<String, Object>();

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
    public void addMessage(Localizable pattern) {
        messages.add(new SerializablePair<Localizable, Object[]>(pattern, (Object[]) null));
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

    /**
     * Serialize this object to the given stream.
     *
     * @param out Stream.
     * @throws IOException This should never happen.
     */
    private void writeObject(ObjectOutputStream out)
        throws IOException {
        serializeMessages(out);
        serializeContext(out);
    }
    /**
     * Deserialize this object from the given stream.
     *
     * @param in Stream.
     * @throws IOException This should never happen.
     * @throws ClassNotFoundException This should never happen.
     */
    private void readObject(ObjectInputStream in)
        throws IOException,
               ClassNotFoundException {
        deSerializeMessages(in);
        deSerializeContext(in);
    }

    /**
     * Serialize {@link #messages}.
     *
     * @param out Stream.
     * @throws IOException This should never happen.
     */
    private void serializeMessages(ObjectOutputStream out)
        throws IOException {
        // Step 1.
        final int len = messages.size();
        out.writeInt(len);
        // Step 2.
        for (int i = 0; i < len; i++) {
            SerializablePair<Localizable, Object[]> pair = messages.get(i);
            // Step 3.
            out.writeObject(pair.getKey());
            final Object[] args = pair.getValue();
            final int aLen = args.length;
            // Step 4.
            out.writeInt(aLen);
            for (int j = 0; j < aLen; j++) {
                if (args[j] instanceof Serializable) {
                    // Step 5a.
                    out.writeObject(args[j]);
                } else {
                    // Step 5b.
                    out.writeObject(nonSerializableReplacement(args[j]));
                }
            }
        }
    }

    /**
     * Deserialize {@link #messages}.
     *
     * @param in Stream.
     * @throws IOException This should never happen.
     * @throws ClassNotFoundException This should never happen.
     */
    private void deSerializeMessages(ObjectInputStream in)
        throws IOException,
               ClassNotFoundException {
        // Step 1.
        final int len = in.readInt();
        messages = new ArrayList<SerializablePair<Localizable, Object[]>>(len);
        // Step 2.
        for (int i = 0; i < len; i++) {
            // Step 3.
            final Localizable key = (Localizable) in.readObject();
            // Step 4.
            final int aLen = in.readInt();
            final Object[] args = new Object[aLen];
            for (int j = 0; j < aLen; j++) {
                // Step 5.
                args[j] = in.readObject();
            }
            messages.add(new SerializablePair<Localizable, Object[]>(key, args));
        }
    }

    /**
     * Serialize {@link #context}.
     *
     * @param out Stream.
     * @throws IOException This should never happen.
     */
    private void serializeContext(ObjectOutputStream out)
        throws IOException {
        // Step 1.
        final int len = context.keySet().size();
        out.writeInt(len);
        for (String key : context.keySet()) {
            // Step 2.
            out.writeObject(key);
            final Object value = context.get(key);
            if (value instanceof Serializable) {
                // Step 3a.
                out.writeObject(value);
            } else {
                // Step 3b.
                out.writeObject(nonSerializableReplacement(value));
            }
        }
    }

    /**
     * Deserialize {@link #context}.
     *
     * @param in Stream.
     * @throws IOException This should never happen.
     * @throws ClassNotFoundException This should never happen.
     */
    private void deSerializeContext(ObjectInputStream in)
        throws IOException,
               ClassNotFoundException {
        // Step 1.
        final int len = in.readInt();
        context = new HashMap<String, Object>();
        for (int i = 0; i < len; i++) {
            // Step 2.
            final String key = (String) in.readObject();
            // Step 3.
            final Object value = in.readObject();
            context.put(key, value);
        }
    }

    /**
     * Replaces a non-serializable object with an error message string.
     *
     * @param obj Object that does not implement the {@code Serializable
     * interface
     * @return a string that mentions which class could not be serialized.
     */
    private String nonSerializableReplacement(Object obj) {
        return "[Object could not be serialized: " + obj.getClass().getName() + "]";
    }
}
