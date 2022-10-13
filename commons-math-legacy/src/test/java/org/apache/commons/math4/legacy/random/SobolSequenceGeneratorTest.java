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
package org.apache.commons.math4.legacy.random;

import org.junit.Assert;

import java.io.InputStream;

import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.junit.Before;
import org.junit.Test;

public class SobolSequenceGeneratorTest {
    private static final String RESOURCE_NAME = "/assets/org/apache/commons/math4/legacy/random/new-joe-kuo-6.21201";

    private double[][] referenceValues = {
            { 0.0, 0.0, 0.0 },
            { 0.5, 0.5, 0.5 },
            { 0.75, 0.25, 0.25 },
            { 0.25, 0.75, 0.75 },
            { 0.375, 0.375, 0.625 },
            { 0.875, 0.875, 0.125 },
            { 0.625, 0.125, 0.875 },
            { 0.125, 0.625, 0.375 },
            { 0.1875, 0.3125, 0.9375 },
            { 0.6875, 0.8125, 0.4375 }
    };

    private SobolSequenceGenerator generator;

    @Before
    public void setUp() {
        generator = new SobolSequenceGenerator(3);
    }

    @Test
    public void test3DReference() {
        for (int i = 0; i < referenceValues.length; i++) {
            double[] result = generator.get();
            Assert.assertArrayEquals(referenceValues[i], result, 1e-6);
            Assert.assertEquals(i + 1, generator.getNextIndex());
        }
    }

    @Test
    public void testConstructor() {
        try {
            new SobolSequenceGenerator(0);
            Assert.fail("an exception should have been thrown");
        } catch (OutOfRangeException e) {
            // expected
        }

        try {
            new SobolSequenceGenerator(21202);
            Assert.fail("an exception should have been thrown");
        } catch (OutOfRangeException e) {
            // expected
        }
    }

    @Test
    public void testConstructor2() throws Exception{
        try {
            final InputStream is = getClass().getResourceAsStream(RESOURCE_NAME);
            new SobolSequenceGenerator(21202, is);
            Assert.fail("an exception should have been thrown");
        } catch (OutOfRangeException e) {
            // expected
        }

        try {
            new SobolSequenceGenerator(21202);
            Assert.fail("an exception should have been thrown");
        } catch (OutOfRangeException e) {
            // expected
        }
    }

    @Test
    public void testSkip() {
        double[] result = generator.skipTo(5);
        Assert.assertArrayEquals(referenceValues[5], result, 1e-6);
        Assert.assertEquals(6, generator.getNextIndex());

        for (int i = 6; i < referenceValues.length; i++) {
            result = generator.get();
            Assert.assertArrayEquals(referenceValues[i], result, 1e-6);
            Assert.assertEquals(i + 1, generator.getNextIndex());
        }
    }
}
