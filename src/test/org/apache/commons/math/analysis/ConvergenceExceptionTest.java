/*
 * 
 * Copyright (c) 2003-2004 The Apache Software Foundation. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *  
 */

package org.apache.commons.math.analysis;

import org.apache.commons.math.ConvergenceException;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class ConvergenceExceptionTest extends TestCase {
    /**
     * 
     */
    public void testConstructor(){
        ConvergenceException ex = new ConvergenceException();
        assertNull(ex.getCause());
        assertNull(ex.getMessage());
    }
    
    /**
     * 
     */
    public void testConstructorMessage(){
        String msg = "message";
        ConvergenceException ex = new ConvergenceException(msg);
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
        ConvergenceException ex = new ConvergenceException(outMsg, cause);
        assertEquals(outMsg, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
    
    /**
     * 
     */
    public void testConstructorCause(){
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        ConvergenceException ex = new ConvergenceException(cause);
        assertEquals(cause, ex.getCause());
    }
}
