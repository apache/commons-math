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
package org.apache.commons.math.linear;

/**

 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract  class DecompositionFactory {

    /*
     * get a matrix specific decomposer factory
     * class RealMatrix {
     *     DecompositionFactory getDecompositionFactory();
     *  }
     */
     
    // get overall default factory
    public static DecompositionFactory newInstance(){
        return null;
    }

    // construct a new default decomposer
    public abstract Decomposer newDecomposer();

    // example for a specific decomposer (Householder or QR)
    public abstract Decomposer newQRDecopmposer();
}
