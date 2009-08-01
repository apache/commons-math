/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.commons.math.special;

import org.apache.commons.math.MathException;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class ErfTest extends TestCase {

    public void testErf0() throws MathException {
        double actual = Erf.erf(0.0);
        double expected = 0.0;
        assertEquals(expected, actual, 1.0e-5);
    }

    public void testErf1960() throws MathException {
        double x = 1.960 / Math.sqrt(2.0);
        double actual = Erf.erf(x);
        double expected = 0.95;
        assertEquals(expected, actual, 1.0e-5);

        actual = Erf.erf(-x);
        expected = -expected;
        assertEquals(expected, actual, 1.0e-5);
    }

    public void testErf2576() throws MathException {
        double x = 2.576 / Math.sqrt(2.0);
        double actual = Erf.erf(x);
        double expected = 0.99;
        assertEquals(expected, actual, 1.0e-5);
    
        actual = Erf.erf(-x);
        expected = -expected;
        assertEquals(expected, actual, 1.0e-5);
    }

    public void testErf2807() throws MathException {
        double x = 2.807 / Math.sqrt(2.0);
        double actual = Erf.erf(x);
        double expected = 0.995;
        assertEquals(expected, actual, 1.0e-5);
        
        actual = Erf.erf(-x);
        expected = -expected;
        assertEquals(expected, actual, 1.0e-5);
    }

    public void testErf3291() throws MathException {
        double x = 3.291 / Math.sqrt(2.0);
        double actual = Erf.erf(x);
        double expected = 0.999;
        assertEquals(expected, actual, 1.0e-5);
        
        actual = Erf.erf(-x);
        expected = -expected;
        assertEquals(expected, actual, 1.0e-5);
    }
}
