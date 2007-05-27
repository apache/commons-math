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

package org.apache.commons.math.estimation;

import org.apache.commons.math.MathException;

/** 
 * This class represents exceptions thrown by the estimation solvers.
 *
 * @version $Id: EstimationException.java 1705 2006-09-17 19:57:39Z luc $
 *
 */

public class EstimationException
extends MathException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -7414806622114810487L;

    /** 
     * Simple constructor.
     * Build an exception by translating and formating a message
     * @param specifier format specifier (to be translated)
     * @param parts to insert in the format (no translation)
     */
    public EstimationException(String specifier, String[] parts) {
        super(specifier, parts);
    }

    /** 
     * Simple constructor.
     * Build an exception from a cause
     * @param cause cause of this exception
     */
    public EstimationException(Throwable cause) {
        super(cause);
    }

}
