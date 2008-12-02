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

package org.apache.commons.math.util;

import java.io.Serializable;

import org.apache.commons.math.MathException;

/**
 * A Default NumberTransformer for java.lang.Numbers and Numeric Strings. This 
 * provides some simple conversion capabilities to turn any java/lang.Number 
 * into a primitive double or to turn a String representation of a Number into 
 * a double.
 *
 * @version $Revision$ $Date$
 */
public class DefaultTransformer implements NumberTransformer, Serializable {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = 4019938025047800455L;
    
    /**
     * @param o  the object that gets transformed.
     * @return a double primitive representation of the Object o.
     * @throws org.apache.commons.math.MathException If it cannot successfully 
     * be transformed or is null.
     * @see org.apache.commons.collections.Transformer#transform(java.lang.Object)
     */
    public double transform(Object o) throws MathException{

        if (o == null) {
            throw new MathException("Conversion Exception in Transformation, Object is null", null);
        }

        if (o instanceof Number) {
            return ((Number)o).doubleValue();
        }
            
        try {
            return Double.valueOf(o.toString()).doubleValue();
        } catch (Exception e) {
            throw new MathException("Conversion Exception in Transformation: {0}",
                                    new Object[] { e.getMessage() }, e);
        }
    }
}
