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
import org.apache.commons.discovery.tools.DiscoverClass;

/**
 * Abstract factory to create inference test instances.
 *
 * @since 1.1
 * @version $Revision$ $Date$ 
 */
public abstract class TestFactory {
    /**
     * Default constructor.
     */
    protected TestFactory() {
        super();
    }
    
    /**
     * Create an instance of a <code>TestFactory</code>
     * 
     * @return a new factory. 
     */
    public static TestFactory newInstance() {
        TestFactory factory = null;
        try {
            DiscoverClass dc = new DiscoverClass();
            factory = (TestFactory) dc.newInstance(
                    TestFactory.class,
            "org.apache.commons.math.stat.inference.TestFactoryImpl");
        } catch(Throwable t) {
            return new TestFactoryImpl();
        }
        return factory;
    }
    
    /**
     * Create a TTest instance.
     * 
     * @return a new TTest instance
     */
    public abstract TTest createTTest();
    
    /**
     * Create a ChiSquareTest instance.
     * 
     * @return a new ChiSquareTest instance
     */
    public abstract ChiSquareTest createChiSquareTest();  
}
