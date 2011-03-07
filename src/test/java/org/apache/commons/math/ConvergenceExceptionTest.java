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

import junit.framework.TestCase;

import java.util.Locale;

import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * @version $Revision$ $Date$
 */
public class ConvergenceExceptionTest extends TestCase {

    public void testConstructor(){
        ConvergenceException ex = new ConvergenceException();
        assertNull(ex.getCause());
        assertNotNull(ex.getMessage());
        assertNotNull(ex.getMessage(Locale.FRENCH));
        assertFalse(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
    }

    public void testConstructorCause(){
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        ConvergenceException ex = new ConvergenceException(cause);
        assertEquals(cause, ex.getCause());
    }
}
