/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
 * Exeption thrown when an error occurs evaluating a function.
 * <p>
 * Maintains an <code>argument</code> property holding the input value that
 * caused the function evaluation to fail.
 * 
 * @version $Revision$ $Date$
 */
public class FunctionEvaluationException extends MathException  {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = -317289374378977972L;
    
    /** Argument causing function evaluation failure */
    private double argument = Double.NaN;
    
    /**
     * Construct an exception indicating the argument value
     * that caused the function evaluation to fail.  Generates an exception
     * message of the form "Evaluation failed for argument = " + argument.
     * 
     * @param argument  the failing function argument 
     */
    public FunctionEvaluationException(double argument) {
        this(argument, "Evaluation failed for argument = " + argument);
    }
    
    /**
     * Construct an exception using the given argument and message
     * text.  The message text of the exception will start with 
     * <code>message</code> and be followed by 
     * " Evaluation failed for argument = " + argument.
     * 
     * @param argument  the failing function argument 
     * @param message  the exception message text
     */
    public FunctionEvaluationException(double argument, String message) {
        this(argument, message, null);
    }

    /**
     * Construct an exception with the given argument, message and root cause.
     * The message text of the exception will start with  <code>message</code>
     * and be followed by " Evaluation failed for argument = " + argument.
     * 
     * @param argument  the failing function argument 
     * @param message descriptive error message
     * @param cause root cause.
     */
    public FunctionEvaluationException(double argument, String message, 
            Throwable cause) {
        super(message + " Evaluation failed for argument=" + argument, cause);
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
