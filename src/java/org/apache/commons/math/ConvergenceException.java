/*
 * Copyright 2003-2004 The Apache Software Foundation.
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

import java.io.Serializable;

/**
 * Error thrown when a numerical computation can not be performed because the
 * numerical result failed to converge to a finite value.
 * 
 * @version $Revision: 1.12 $ $Date: 2004/04/27 04:37:58 $
 */
public class ConvergenceException extends MathException implements Serializable{
    /**
     * Default constructor.
     */
    public ConvergenceException() {
        this(null, null);
    }
    
    /**
     * Construct an exception with the given message.
     * @param message descriptive error message. 
     */
    public ConvergenceException(String message) {
        this(message, null);
    }

    /**
     * Construct an exception with the given message and root cause.
     * @param message descriptive error message.
     * @param cause root cause.
     */
    public ConvergenceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create an exception with a given root cause.
     * @param throwable caught exception causing this problem
     */
    public ConvergenceException(Throwable throwable) {
        this(null, throwable);
    }
}
