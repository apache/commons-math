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

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class MathConfigurationExceptionTest extends TestCase {
    /**
     * 
     */
    public void testConstructor(){
        MathConfigurationException ex = new MathConfigurationException();
        assertNull(ex.getCause());
        assertNull(ex.getMessage());
    }
    
    /**
     * 
     */
    public void testConstructorMessage(){
        String msg = "message";
        MathConfigurationException ex = new MathConfigurationException(msg);
        assertNull(ex.getCause());
        assertEquals(msg, ex.getMessage());
    }
    
    /**
     * 
     */
    public void testConstructorMessageCause(){
        String outMsg = "outer message";
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        MathConfigurationException ex = new MathConfigurationException(outMsg, cause);
        assertEquals(outMsg, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
    
    /**
     * 
     */
    public void testConstructorCause(){
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        MathConfigurationException ex = new MathConfigurationException(cause);
        assertEquals(cause, ex.getCause());
    }
}
