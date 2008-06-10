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

/**
 * Exception thrown when an error occurs evaluating a function.
 * <p>
 * Maintains an <code>argument</code> property holding the input value that
 * caused the function evaluation to fail.
 * 
 * @version $Revision$ $Date$
 */
public class FunctionEvaluationException extends MathException  {
    
    /** Serializable version identifier. */
    private static final long serialVersionUID = -2193260774031645876L;

    /** Argument causing function evaluation failure */
    private double argument = Double.NaN;
    
    /**
     * Construct an exception indicating the argument value
     * that caused the function evaluation to fail.
     * 
     * @param argument  the failing function argument 
     */
    public FunctionEvaluationException(double argument) {
        super("Evaluation failed for argument = {0}",
              new Object[] { Double.valueOf(argument) });
        this.argument = argument;
    }
    
    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param argument  the failing function argument 
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 1.2
     */
    public FunctionEvaluationException(double argument,
                                       String pattern, Object[] arguments) {
        super(pattern, arguments);
        this.argument = argument;
    }

    /**
     * Constructs an exception with specified root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param argument  the failing function argument 
     * @param cause  the exception or error that caused this exception to be thrown
     * @since 1.2
     */
    public FunctionEvaluationException(double argument, Throwable cause) {
        super(cause);
        this.argument = argument;
    }

    /**
     * Constructs an exception with specified formatted detail message and root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param argument  the failing function argument 
     * @param pattern format specifier
     * @param arguments format arguments
     * @param cause  the exception or error that caused this exception to be thrown
     * @since 1.2
     */
    public FunctionEvaluationException(double argument,
                                       String pattern, Object[] arguments,
                                       Throwable cause) {
        super(pattern, arguments, cause);
        this.argument = argument;
    }

    /**
     * Returns the function argument that caused this exception.
     * 
     * @return  argument that caused function evaluation to fail
     */
    public double getArgument() {
        return this.argument;
    }

}
