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
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

/**
* Base class for commons-math unchecked exceptions.
* 
* @version $Revision$ $Date$
* @since 2.0
*/
public class MathRuntimeException extends RuntimeException {
    
    /** Serializable version identifier. */
    private static final long serialVersionUID = -143052521750625264L;

    /** Cache for resources bundle. */
    private static ResourceBundle cachedResources = null;
 
    /**
     * Pattern used to build the message.
     */
    private final String pattern;

    /**
     * Arguments used to build the message.
     */
    private final Object[] arguments;

    /**
     * Translate a string to a given locale.
     * @param s string to translate
     * @param locale locale into which to translate the string
     * @return translated string or original string
     * for unsupported locales or unknown strings
     */
    private static String translate(final String s, final Locale locale) {
        try {
            if ((cachedResources == null) || (! cachedResources.getLocale().equals(locale))) {
                // caching the resource bundle
                cachedResources =
                    ResourceBundle.getBundle("org.apache.commons.math.MessagesResources", locale);
            }

            if (cachedResources.getLocale().getLanguage().equals(locale.getLanguage())) {
                // the value of the resource is the translated string
                return cachedResources.getString(s);
            }
            
        } catch (MissingResourceException mre) {
            // do nothing here
        }

        // the locale is not supported or the resource is unknown
        // don't translate and fall back to using the string as is
        return s;

    }

    /**
     * Builds a message string by from a pattern and its arguments.
     * @param pattern format specifier
     * @param arguments format arguments
     * @param locale Locale in which the message should be translated
     * @return a message string
     */
    private static String buildMessage(final String pattern, final Object[] arguments,
                                       final Locale locale) {
        return (pattern == null) ? "" : new MessageFormat(translate(pattern, locale), locale).format(arguments);        
    }

    /**
     * Constructs a new <code>MathRuntimeException</code> with specified
     * formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     */
    public MathRuntimeException(final String pattern, final Object[] arguments) {
      super(buildMessage(pattern, arguments, Locale.US));
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
        this.pattern   = getMessage();
        this.arguments = new Object[0];
    }
    
    /**
     * Constructs a new <code>MathRuntimeException</code> with specified
     * formatted detail message and nested <code>Throwable</code> root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @param rootCause the exception or error that caused this exception
     * to be thrown.
     */
    public MathRuntimeException(final String pattern, final Object[] arguments,
                                final Throwable rootCause) {
      super(buildMessage(pattern, arguments, Locale.US), rootCause);
      this.pattern   = pattern;
      this.arguments = (arguments == null) ? new Object[0] : arguments.clone();
    }

    /** Gets the pattern used to build the message of this throwable.
     *
     * @return the pattern used to build the message of this throwable
     */
    public String getPattern() {
        return pattern;
    }

    /** Gets the arguments used to build the message of this throwable.
     *
     * @return the arguments used to build the message of this throwable
     */
    public Object[] getArguments() {
        return arguments.clone();
    }

    /** Gets the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated
     * 
     * @return localized message
     */
    public String getMessage(final Locale locale) {
        return buildMessage(pattern, arguments, locale);
    }

    /** {@inheritDoc} */
    public String getLocalizedMessage() {
        return getMessage(Locale.getDefault());
    }

    /**
     * Prints the stack trace of this exception to the standard error stream.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }
    
    /**
     * Prints the stack trace of this exception to the specified stream.
     *
     * @param out  the <code>PrintStream</code> to use for output
     */
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
     */
    public static ArithmeticException createArithmeticException(final String pattern,
                                                                final Object[] arguments) {
        return new ArithmeticException(buildMessage(pattern, arguments, Locale.US)) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 7705628723242533939L;

            /** {@inheritDoc} */
            public String getLocalizedMessage() {
                return buildMessage(pattern, arguments, Locale.getDefault());
            }

        };
    }

    /**
     * Constructs a new <code>ArrayIndexOutOfBoundsException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     */
    public static ArrayIndexOutOfBoundsException createArrayIndexOutOfBoundsException(final String pattern,
                                                                                      final Object[] arguments) {
        return new ArrayIndexOutOfBoundsException(buildMessage(pattern, arguments, Locale.US)) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 8077627622976962141L;

            /** {@inheritDoc} */
            public String getLocalizedMessage() {
                return buildMessage(pattern, arguments, Locale.getDefault());
            }

        };
    }

    /**
     * Constructs a new <code>EOFException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     */
    public static EOFException createEOFException(final String pattern,
                                                  final Object[] arguments) {
        return new EOFException(buildMessage(pattern, arguments, Locale.US)) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 279461544586092584L;

            /** {@inheritDoc} */
            public String getLocalizedMessage() {
                return buildMessage(pattern, arguments, Locale.getDefault());
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
     */
    public static IllegalArgumentException createIllegalArgumentException(final String pattern,
                                                                          final Object[] arguments) {
        return new IllegalArgumentException(buildMessage(pattern, arguments, Locale.US)) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = -7537852425838457684L;

            /** {@inheritDoc} */
            public String getLocalizedMessage() {
                return buildMessage(pattern, arguments, Locale.getDefault());
            }

        };
    }

    /**
     * Constructs a new <code>IllegalStateException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     */
    public static IllegalStateException createIllegalStateException(final String pattern,
                                                                    final Object[] arguments) {
        return new IllegalStateException(buildMessage(pattern, arguments, Locale.US)) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 5173599768297434381L;

            /** {@inheritDoc} */
            public String getLocalizedMessage() {
                return buildMessage(pattern, arguments, Locale.getDefault());
            }

        };
    }

    /**
     * Constructs a new <code>ConcurrentModificationException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     */
    public static ConcurrentModificationException createConcurrentModificationException(final String pattern,
                                                                                        final Object[] arguments) {
        return new ConcurrentModificationException(buildMessage(pattern, arguments, Locale.US)) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 6134247282754009421L;

            /** {@inheritDoc} */
            public String getLocalizedMessage() {
                return buildMessage(pattern, arguments, Locale.getDefault());
            }

        };
    }

    /**
     * Constructs a new <code>NoSuchElementException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     */
    public static NoSuchElementException createNoSuchElementException(final String pattern,
                                                                      final Object[] arguments) {
        return new NoSuchElementException(buildMessage(pattern, arguments, Locale.US)) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 7304273322489425799L;

            /** {@inheritDoc} */
            public String getLocalizedMessage() {
                return buildMessage(pattern, arguments, Locale.getDefault());
            }

        };
    }

    /**
     * Constructs a new <code>ParseException</code> with specified
     * formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @param offset offset at which error occurred
     * @return built exception
     */
    public static ParseException createParseException(final String pattern,
                                                      final Object[] arguments,
                                                      final int offset) {
        return new ParseException(buildMessage(pattern, arguments, Locale.US), offset) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = -1103502177342465975L;

            /** {@inheritDoc} */
            public String getLocalizedMessage() {
                return buildMessage(pattern, arguments, Locale.getDefault());
            }

        };
    }

}
