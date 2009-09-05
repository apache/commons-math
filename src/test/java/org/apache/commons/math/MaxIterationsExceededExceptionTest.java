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

import java.util.Locale;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class MaxIterationsExceededExceptionTest extends TestCase {

    public void testSimpleConstructor(){
        MaxIterationsExceededException ex = new MaxIterationsExceededException(1000000);
        assertNull(ex.getCause());
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().indexOf("1,000,000") > 0);
        assertEquals(1000000, ex.getMaxIterations());
        assertFalse(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
    }

    public void testComplexConstructor(){
        MaxIterationsExceededException ex =
            new MaxIterationsExceededException(1000000,
                "Continued fraction convergents failed to converge for value {0}",
                1234567);
        assertNull(ex.getCause());
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().indexOf("1,000,000") < 0);
        assertTrue(ex.getMessage().indexOf("1,234,567") > 0);
        assertEquals(1000000, ex.getMaxIterations());
        assertFalse(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
    }

}
