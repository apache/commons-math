/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.commons.math.stat.inference;

/**
 * A concrete inference test factory.  This is the default factory used by
 * Commons-Math.
 *  
 * @since 1.1
 * @version $Revision$ $Date$
 */
public class TestFactoryImpl extends TestFactory {

    /**
     * Default constructor. 
     */
    public TestFactoryImpl() {
        super();
    }
    
     /**
     * Create a TTest instance.
     * 
     * @return a new TTest instance
     */
    public TTest createTTest() {       
        return new TTestImpl();
    }
    
    /**
     * Create a ChiSquareTest instance.
     * 
     * @return a new ChiSquareTest instance
     */
    public ChiSquareTest createChiSquareTest() {
        return new ChiSquareTestImpl();
    }
    
}
