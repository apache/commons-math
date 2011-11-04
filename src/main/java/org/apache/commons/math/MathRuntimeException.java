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
package org.apache.commons.math;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ConcurrentModificationException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
* Base class for commons-math unchecked exceptions.
*
* @version $Id$
* @since 2.0
* @deprecated To be removed before 3.0.  Please do not use in any new code.
*/
@Deprecated
public class MathRuntimeException extends RuntimeException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 9058794795027570002L;

    /** Deprecation message. */
    private static final String DEPRECATION_MESSAGE = "This class is deprecated; calling this method is a bug.";

    /**
     * Pattern used to build the message.
     */
    private final Localizable pattern;

    /**
     * Arguments used to build the message.
     */
    private final Object[] arguments;

    /**
     * Constructs a new <code>MathRuntimeException</code> with specified
     * formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.2
     */
    public MathRuntimeException(final Localizable pattern, final Object ... arguments) {
        this.pattern   = pattern;
        this.arguments = (arguments == null) ? new Object[0] : arguments.clone();
    }

    /**
     * Constructs a new <code>MathRuntimeException</code> with specified
     * nested <code>Throwable</code> root cause.
     *
     * @param rootCause  the exception or error that caused this exception
     *                   to be thrown.
     */
    public MathRuntimeException(final Throwable rootCause) {
        super(rootCause);
        this.pattern   = LocalizedFormats.SIMPLE_MESSAGE;
        this.arguments = new Object[] { (rootCause == null) ? "" : rootCause.getMessage() };
    }

    /**
     * Constructs a new <code>MathRuntimeException</code> with specified
     * formatted detail message and nested <code>Throwable</code> root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param rootCause the exception or error that caused this exception
     * to be thrown.
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.2
     */
    public MathRuntimeException(final Throwable rootCause,
                                final Localizable pattern, final Object ... arguments) {
        super(rootCause);
        this.pattern   = pattern;
        this.arguments = (arguments == null) ? new Object[0] : arguments.clone();
    }

    /**
     * Sets a message.
     *
     * @param pat Message pattern.
     * @param args Values for replacing the placeholders in the message
     * pattern.
     */
    public void addMessage(Localizable pat,
                           Object ... args) {
        throw new UnsupportedOperationException(DEPRECATION_MESSAGE);
    }

    /**
     * Sets the context (key, value) pair.
     * Keys are assumed to be unique within an instance. If the same key is
     * assigned a new value, the previous one will be lost.
     *
     * @param key Context key (not null).
     * @param value Context value.
     */
    public void setContext(String key, Object value) {
        throw new UnsupportedOperationException(DEPRECATION_MESSAGE);
    }

    /**
     * Gets the value associated to the given context key.
     *
     * @param key Context key.
     * @return the context value or {@code null} if the key does not exist.
     */
    public Object getContext(String key) {
        throw new UnsupportedOperationException(DEPRECATION_MESSAGE);
    }

    /**
     * Gets all the keys stored in the exception
     *
     * @return the set of keys.
     */
    public Set<String> getContextKeys() {
        throw new UnsupportedOperationException(DEPRECATION_MESSAGE);
    }

    /**
     * Builds a message string by from a pattern and its arguments.
     * @param locale Locale in which the message should be translated
     * @param pattern format specifier
     * @param arguments format arguments
     * @return a message string
     * @since 2.2
     */
    private static String buildMessage(final Locale locale, final Localizable pattern,
                                       final Object ... arguments) {
        return new MessageFormat(pattern.getLocalizedString(locale), locale).format(arguments);
    }

    /** Gets the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated
     *
     * @return localized message
     */
    public String getMessage(final Locale locale) {
        if (pattern != null) {
            return buildMessage(locale, pattern, arguments);
        }
        return "";
    }

    /**
     * Gets the message in a conventional US locale.
     *
     * @return localized message
     */
    @Override
    public String getMessage() {
        return getMessage(Locale.US);
    }

    /**
     * Gets the message in the system default locale.
     *
     * @return localized message
     */
    @Override
    public String getLocalizedMessage() {
        return getMessage(Locale.getDefault());
    }

    /**
     * Prints the stack trace of this exception to the standard error stream.
     */
    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints the stack trace of this exception to the specified stream.
     *
     * @param out  the <code>PrintStream</code> to use for output
     */
    @Override
    public void printStackTrace(final PrintStream out) {
        synchronized (out) {
            PrintWriter pw = new PrintWriter(out, false);
            printStackTrace(pw);
            // Flush the PrintWriter before it's GC'ed.
            pw.flush();
        }
    }

    /**
     * Constructs a new <code>ArithmeticException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static ArithmeticException createArithmeticException(final Localizable pattern,
                                                                final Object ... arguments) {
        return new ArithmeticException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 5305498554076846637L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return buildMessage(Locale.US, pattern, arguments);
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return buildMessage(Locale.getDefault(), pattern, arguments);
            }

        };
    }

    /**
     * Constructs a new <code>ArrayIndexOutOfBoundsException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static ArrayIndexOutOfBoundsException createArrayIndexOutOfBoundsException(final Localizable pattern,
                                                                                      final Object ... arguments) {
        return new ArrayIndexOutOfBoundsException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 6718518191249632175L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return buildMessage(Locale.US, pattern, arguments);
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return buildMessage(Locale.getDefault(), pattern, arguments);
            }

        };
    }

    /**
     * Constructs a new <code>EOFException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static EOFException createEOFException(final Localizable pattern,
                                                  final Object ... arguments) {
        return new EOFException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 6067985859347601503L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return buildMessage(Locale.US, pattern, arguments);
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return buildMessage(Locale.getDefault(), pattern, arguments);
            }

        };
    }

    /**
     * Constructs a new <code>IOException</code> with specified nested
     * <code>Throwable</code> root cause.
     * <p>This factory method allows chaining of other exceptions within an
     * <code>IOException</code> even for Java 5. The constructor for
     * <code>IOException</code> with a cause parameter was introduced only
     * with Java 6.</p>
     * @param rootCause the exception or error that caused this exception
     * to be thrown.
     * @return built exception
     */
    public static IOException createIOException(final Throwable rootCause) {
        IOException ioe = new IOException(rootCause.getLocalizedMessage());
        ioe.initCause(rootCause);
        return ioe;
    }

    /**
     * Constructs a new <code>IllegalArgumentException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static IllegalArgumentException createIllegalArgumentException(final Localizable pattern,
                                                                          final Object ... arguments) {
        return new IllegalArgumentException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = -4284649691002411505L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return buildMessage(Locale.US, pattern, arguments);
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return buildMessage(Locale.getDefault(), pattern, arguments);
            }

        };
    }

    /**
     * Constructs a new <code>IllegalArgumentException</code> with specified nested
     * <code>Throwable</code> root cause.
     * @param rootCause the exception or error that caused this exception
     * to be thrown.
     * @return built exception
     */
    public static IllegalArgumentException createIllegalArgumentException(final Throwable rootCause) {
        IllegalArgumentException iae = new IllegalArgumentException(rootCause.getLocalizedMessage());
        iae.initCause(rootCause);
        return iae;
    }

    /**
     * Constructs a new <code>IllegalStateException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static IllegalStateException createIllegalStateException(final Localizable pattern,
                                                                    final Object ... arguments) {
        return new IllegalStateException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 6880901520234515725L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return buildMessage(Locale.US, pattern, arguments);
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return buildMessage(Locale.getDefault(), pattern, arguments);
            }

        };
    }

    /**
     * Constructs a new <code>ConcurrentModificationException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static ConcurrentModificationException createConcurrentModificationException(final Localizable pattern,
                                                                                        final Object ... arguments) {
        return new ConcurrentModificationException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = -1878427236170442052L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return buildMessage(Locale.US, pattern, arguments);
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return buildMessage(Locale.getDefault(), pattern, arguments);
            }

        };
    }

    /**
     * Constructs a new <code>NoSuchElementException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static NoSuchElementException createNoSuchElementException(final Localizable pattern,
                                                                      final Object ... arguments) {
        return new NoSuchElementException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 1632410088350355086L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return buildMessage(Locale.US, pattern, arguments);
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return buildMessage(Locale.getDefault(), pattern, arguments);
            }

        };
    }

    /**
     * Constructs a new <code>ParseException</code> with specified
     * formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param offset offset at which error occurred
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static ParseException createParseException(final int offset,
                                                      final Localizable pattern,
                                                      final Object ... arguments) {
        return new ParseException(null, offset) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 8153587599409010120L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return buildMessage(Locale.US, pattern, arguments);
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return buildMessage(Locale.getDefault(), pattern, arguments);
            }

        };
    }

    /** Create an {@link java.lang.RuntimeException} for an internal error.
     * @param cause underlying cause
     * @return an {@link java.lang.RuntimeException} for an internal error
     */
    public static RuntimeException createInternalError(final Throwable cause) {

        final String argument = "https://issues.apache.org/jira/browse/MATH";

        return new RuntimeException(cause) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = -201865440834027016L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return buildMessage(Locale.US, LocalizedFormats.INTERNAL_ERROR, argument);
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return buildMessage(Locale.getDefault(), LocalizedFormats.INTERNAL_ERROR, argument);
            }

        };

    }

}
