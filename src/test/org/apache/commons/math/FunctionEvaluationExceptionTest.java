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

package org.apache.commons.math;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class FunctionEvaluationExceptionTest extends TestCase {
    
    public void testConstructor(){
        FunctionEvaluationException ex = new FunctionEvaluationException(0.0);
        assertNull(ex.getCause());
        assertNotNull(ex.getMessage());
        assertEquals(0.0, ex.getArgument(), 0);
    }
    
    public void testConstructorMessage(){
        String msg = "message";
        FunctionEvaluationException  ex = new FunctionEvaluationException(0.0, msg);
        assertNull(ex.getCause());
        assertTrue(ex.getMessage().startsWith(msg));
        assertTrue(ex.getMessage().indexOf("0") > 0);
        assertEquals(0.0, ex.getArgument(), 0);
    }
    
    public void testConstructorMessageCause(){
        String outMsg = "outer message";
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        FunctionEvaluationException ex = new FunctionEvaluationException(0, outMsg, cause);
        assertTrue(ex.getMessage().startsWith(outMsg));
        assertTrue(ex.getMessage().indexOf("0") > 0);
        assertEquals(cause, ex.getCause());
        assertEquals(0.0, ex.getArgument(), 0);
    }
}
