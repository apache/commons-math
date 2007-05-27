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

package org.apache.commons.math.fraction;

import org.apache.commons.math.MaxIterationsExceededException;

public class FractionConversionException extends MaxIterationsExceededException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 4588659344016668813L;

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param value double value to convert
     * @param maxIterations maximal number of iterations allowed
     */
    public FractionConversionException(double value, int maxIterations) {
        super(maxIterations,
              "Unable to convert {0} to fraction after {1} iterations",
              new Object[] { new Double(value), new Integer(maxIterations) });
    }

}
