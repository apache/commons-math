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
 * Error thrown when two dimensions differ.
 * 
 * @since 1.2
 * @version $Revision$ $Date$
 */
public class DimensionMismatchException extends MathException {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = -1316089546353786411L;

    /**
     * Construct an exception from the mismatched dimensions
     * @param dimension1 first dimension
     * @param dimension2 second dimension
     */
    public DimensionMismatchException(int dimension1, int dimension2) {
        super("dimension mismatch {0} != {1}",
              new Object[] {
                Integer.valueOf(dimension1), Integer.valueOf(dimension2)
              });
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
    }

    /**
     * Get the first dimension
     * @return first dimension
     */
    public int getDimension1() {
        return dimension1;
    }
    
    /**
     * Get the second dimension
     * @return second dimension
     */
    public int getDimension2() {
        return dimension2;
    }

    /** First dimension. */
    private int dimension1;
    
    /** Second dimension. */
    private int dimension2;
    
}
