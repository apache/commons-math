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

import org.apache.commons.numbers.core.Precision;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;


/**
 * Test cases for the {@link OpenIntToDoubleHashMap}.
 */
@SuppressWarnings("boxing")
public class OpenLongToDoubleHashMapTest {

    private Map<Long, Double> javaMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        javaMap.put(50L, 100.0);
        javaMap.put(75L, 75.0);
        javaMap.put(25L, 500.0);
        javaMap.put((long)Integer.MAX_VALUE, Double.MAX_VALUE);
        javaMap.put(0L, -1.0);
        javaMap.put(1L, 0.0);
        javaMap.put(33L, -0.1);
        javaMap.put(23234234L, -242343.0);
        javaMap.put(23321L, Double.MIN_VALUE);
        javaMap.put(-4444L, 332.0);
        javaMap.put(-1L, -2323.0);
        javaMap.put((long)Integer.MIN_VALUE, 44.0);

        /* Add a few more to cause the table to rehash */
        javaMap.putAll(generate());

    }

    private Map<Long, Double> generate() {
        Map<Long, Double> map = new HashMap<>();
        Random r = new Random();
        for (int i = 0; i < 2000; ++i) {
            map.put(r.nextLong(), r.nextDouble());
        }
        return map;
    }

    private OpenLongToDoubleHashMap createFromJavaMap() {
        OpenLongToDoubleHashMap map = new OpenLongToDoubleHashMap();
        for (Map.Entry<Long, Double> mapEntry : javaMap.entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
        }
        return map;
    }

    @Test
    public void testPutAndGetWith0ExpectedSize() {
        OpenLongToDoubleHashMap map = new OpenLongToDoubleHashMap(0);
        assertPutAndGet(map);
    }

    @Test
    public void testPutAndGetWithExpectedSize() {
        OpenLongToDoubleHashMap map = new OpenLongToDoubleHashMap(500);
        assertPutAndGet(map);
    }

    @Test
    public void testPutAndGet() {
        OpenLongToDoubleHashMap map = new OpenLongToDoubleHashMap();
        assertPutAndGet(map);
    }

    private void assertPutAndGet(OpenLongToDoubleHashMap map) {
        assertPutAndGet(map, 0, new HashSet<Long>());
    }

    private void assertPutAndGet(OpenLongToDoubleHashMap map, int mapSize,
            Set<Long> keysInMap) {
        Assert.assertEquals(mapSize, map.size());
        for (Map.Entry<Long, Double> mapEntry : javaMap.entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
            if (!keysInMap.contains(mapEntry.getKey())) {
                ++mapSize;
            }
            Assert.assertEquals(mapSize, map.size());
            Assert.assertTrue(Precision.equals(mapEntry.getValue(), map.get(mapEntry.getKey()), 1));
        }
    }

    @Test
    public void testPutAbsentOnExisting() {
        OpenLongToDoubleHashMap map = createFromJavaMap();
        int size = javaMap.size();
        for (Map.Entry<Long, Double> mapEntry : generateAbsent().entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
            Assert.assertEquals(++size, map.size());
            Assert.assertTrue(Precision.equals(mapEntry.getValue(), map.get(mapEntry.getKey()), 1));
        }
    }

    @Test
    public void testPutOnExisting() {
        OpenLongToDoubleHashMap map = createFromJavaMap();
        for (Map.Entry<Long, Double> mapEntry : javaMap.entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
            Assert.assertEquals(javaMap.size(), map.size());
            Assert.assertTrue(Precision.equals(mapEntry.getValue(), map.get(mapEntry.getKey()), 1));
        }
    }

    @Test
    public void testGetAbsent() {
        Map<Long, Double> generated = generateAbsent();
        OpenLongToDoubleHashMap map = createFromJavaMap();

        for (Map.Entry<Long, Double> mapEntry : generated.entrySet()) {
            Assert.assertTrue(Double.isNaN(map.get(mapEntry.getKey())));
        }
    }

    @Test
    public void testGetFromEmpty() {
        OpenLongToDoubleHashMap map = new OpenLongToDoubleHashMap();
        Assert.assertTrue(Double.isNaN(map.get(5)));
        Assert.assertTrue(Double.isNaN(map.get(0)));
        Assert.assertTrue(Double.isNaN(map.get(50)));
    }

    @Test
    public void testRemove() {
        OpenLongToDoubleHashMap map = createFromJavaMap();
        int mapSize = javaMap.size();
        Assert.assertEquals(mapSize, map.size());
        for (Map.Entry<Long, Double> mapEntry : javaMap.entrySet()) {
            map.remove(mapEntry.getKey());
            Assert.assertEquals(--mapSize, map.size());
            Assert.assertTrue(Double.isNaN(map.get(mapEntry.getKey())));
        }

        /* Ensure that put and get still work correctly after removals */
        assertPutAndGet(map);
    }

    /* This time only remove some entries */
    @Test
    public void testRemove2() {
        OpenLongToDoubleHashMap map = createFromJavaMap();
        int mapSize = javaMap.size();
        int count = 0;
        Set<Long> keysInMap = new HashSet<>(javaMap.keySet());
        for (Map.Entry<Long, Double> mapEntry : javaMap.entrySet()) {
            keysInMap.remove(mapEntry.getKey());
            map.remove(mapEntry.getKey());
            Assert.assertEquals(--mapSize, map.size());
            Assert.assertTrue(Double.isNaN(map.get(mapEntry.getKey())));
            if (count++ > 5) {
                break;
            }
        }

        /* Ensure that put and get still work correctly after removals */
        assertPutAndGet(map, mapSize, keysInMap);
    }

    @Test
    public void testRemoveFromEmpty() {
        OpenIntToDoubleHashMap map = new OpenIntToDoubleHashMap();
        Assert.assertTrue(Double.isNaN(map.remove(50)));
    }

    @Test
    public void testRemoveAbsent() {
        Map<Long, Double> generated = generateAbsent();

        OpenLongToDoubleHashMap map = createFromJavaMap();
        int mapSize = map.size();

        for (Map.Entry<Long, Double> mapEntry : generated.entrySet()) {
            map.remove(mapEntry.getKey());
            Assert.assertEquals(mapSize, map.size());
            Assert.assertTrue(Double.isNaN(map.get(mapEntry.getKey())));
        }
    }

    /**
     * Returns a map with at least 100 elements where each element is absent from javaMap.
     */
    private Map<Long, Double> generateAbsent() {
        Map<Long, Double> generated = new HashMap<>();
        do {
            generated.putAll(generate());
            for (Long key : javaMap.keySet()) {
                generated.remove(key);
            }
        } while (generated.size() < 100);
        return generated;
    }

    @Test
    public void testCopy() {
        OpenLongToDoubleHashMap copy =
            new OpenLongToDoubleHashMap(createFromJavaMap());
        Assert.assertEquals(javaMap.size(), copy.size());

        for (Map.Entry<Long, Double> mapEntry : javaMap.entrySet()) {
            Assert.assertTrue(Precision.equals(mapEntry.getValue(), copy.get(mapEntry.getKey()), 1));
        }
    }

    @Test
    public void testContainsKey() {
        OpenLongToDoubleHashMap map = createFromJavaMap();
        for (Map.Entry<Long, Double> mapEntry : javaMap.entrySet()) {
            Assert.assertTrue(map.containsKey(mapEntry.getKey()));
        }
        for (Map.Entry<Long, Double> mapEntry : generateAbsent().entrySet()) {
            Assert.assertFalse(map.containsKey(mapEntry.getKey()));
        }
        for (Map.Entry<Long, Double> mapEntry : javaMap.entrySet()) {
            long key = mapEntry.getKey();
            Assert.assertTrue(map.containsKey(key));
            map.remove(key);
            Assert.assertFalse(map.containsKey(key));
        }
    }

    @Test
    public void testIterator() {
        OpenLongToDoubleHashMap map = createFromJavaMap();
        OpenLongToDoubleHashMap.Iterator iterator = map.iterator();
        for (int i = 0; i < map.size(); ++i) {
            Assert.assertTrue(iterator.hasNext());
            iterator.advance();
            long key = iterator.key();
            Assert.assertTrue(map.containsKey(key));
            Assert.assertEquals(javaMap.get(key), map.get(key), 0);
            Assert.assertEquals(javaMap.get(key), iterator.value(), 0);
            Assert.assertTrue(javaMap.containsKey(key));
        }
        Assert.assertFalse(iterator.hasNext());
        try {
            iterator.advance();
            Assert.fail("an exception should have been thrown");
        } catch (NoSuchElementException nsee) {
            // expected
        }
    }

    @Test
    public void testConcurrentModification() {
        OpenLongToDoubleHashMap map = createFromJavaMap();
        OpenLongToDoubleHashMap.Iterator iterator = map.iterator();
        map.put(3, 3);
        try {
            iterator.advance();
            Assert.fail("an exception should have been thrown");
        } catch (ConcurrentModificationException cme) {
            // expected
        }
    }

    /**
     * Regression test for a bug in findInsertionIndex where the hashing in the second probing
     * loop was inconsistent with the first causing duplicate keys after the right sequence
     * of puts and removes.
     */
    @Test
    public void testPutKeysWithCollisions() {
        OpenIntToDoubleHashMap map = new OpenIntToDoubleHashMap();
        int key1 = -1996012590;
        double value1 = 1.0;
        map.put(key1, value1);
        int key2 = 835099822;
        map.put(key2, value1);
        int key3 = 1008859686;
        map.put(key3, value1);
        Assert.assertTrue(Precision.equals(value1, map.get(key3), 1));
        Assert.assertEquals(3, map.size());

        map.remove(key2);
        double value2 = 2.0;
        map.put(key3, value2);
        Assert.assertTrue(Precision.equals(value2, map.get(key3), 1));
        Assert.assertEquals(2, map.size());
    }

    /**
     * Similar to testPutKeysWithCollisions() but exercises the codepaths in a slightly
     * different manner.
     */
    @Test
    public void testPutKeysWithCollision2() {
        OpenIntToDoubleHashMap map = new OpenIntToDoubleHashMap();
        int key1 = 837989881;
        double value1 = 1.0;
        map.put(key1, value1);
        int key2 = 476463321;
        map.put(key2, value1);
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(Precision.equals(value1, map.get(key2), 1));

        map.remove(key1);
        double value2 = 2.0;
        map.put(key2, value2);
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(Precision.equals(value2, map.get(key2), 1));
    }

}
