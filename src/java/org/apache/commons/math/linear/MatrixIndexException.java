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

package org.apache.commons.math.linear;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * Thrown when an operation addresses a matrix coordinate (row,col)
 * which is outside of the dimensions of a matrix.
 * @version $Revision: 1.6 $ $Date: 2004/06/07 03:26:31 $
 */
public class MatrixIndexException extends NestableRuntimeException {

    /** Serializable version identifier */
    static final long serialVersionUID = -1341109412864309526L;

    /**
     * Default constructor.
     */
    public MatrixIndexException() {
        this(null, null);
    }

    /**
     * Construct an exception with the given message.
     * @param message descriptive error message.
     */
    public MatrixIndexException(String message) {
        this(message, null);
    }

    /**
     * Construct an exception with the given message and root cause.
     * @param message descriptive error message.
     * @param cause root cause.
     */
    public MatrixIndexException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create an exception with a given root cause.
     * @param throwable caught exception causing this problem
     */
    public MatrixIndexException(Throwable throwable) {
        this(null, throwable);
    }
}
