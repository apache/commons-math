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

package org.apache.commons.math4.util;

import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.util.DefaultTransformer;
import org.apache.commons.math4.util.NumberTransformer;
import org.apache.commons.math4.util.TransformerMap;
import org.junit.Assert;
import org.junit.Test;


/**
 */
public class TransformerMapTest {
    /**
     *
     */
    @Test
    public void testPutTransformer(){
        NumberTransformer expected = new DefaultTransformer();

        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        Assert.assertEquals(expected, map.getTransformer(TransformerMapTest.class));
    }

    /**
     *
     */
    @Test
    public void testContainsClass(){
        NumberTransformer expected = new DefaultTransformer();
        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        Assert.assertTrue(map.containsClass(TransformerMapTest.class));
    }

    /**
     *
     */
    @Test
    public void testContainsTransformer(){
        NumberTransformer expected = new DefaultTransformer();
        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        Assert.assertTrue(map.containsTransformer(expected));
    }

    /**
     *
     */
    @Test
    public void testRemoveTransformer(){
        NumberTransformer expected = new DefaultTransformer();

        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        Assert.assertTrue(map.containsClass(TransformerMapTest.class));
        Assert.assertTrue(map.containsTransformer(expected));
        map.removeTransformer(TransformerMapTest.class);
        Assert.assertFalse(map.containsClass(TransformerMapTest.class));
        Assert.assertFalse(map.containsTransformer(expected));
    }

    /**
     *
     */
    @Test
    public void testClear(){
        NumberTransformer expected = new DefaultTransformer();

        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        Assert.assertTrue(map.containsClass(TransformerMapTest.class));
        map.clear();
        Assert.assertFalse(map.containsClass(TransformerMapTest.class));
    }

    /**
     *
     */
    @Test
    public void testClasses(){
        NumberTransformer expected = new DefaultTransformer();
        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        Assert.assertTrue(map.classes().contains(TransformerMapTest.class));
    }

    /**
     *
     */
    @Test
    public void testTransformers(){
        NumberTransformer expected = new DefaultTransformer();
        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        Assert.assertTrue(map.transformers().contains(expected));
    }

    @Test
    public void testSerial(){
        NumberTransformer expected = new DefaultTransformer();
        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        Assert.assertEquals(map, TestUtils.serializeAndRecover(map));
    }

}
