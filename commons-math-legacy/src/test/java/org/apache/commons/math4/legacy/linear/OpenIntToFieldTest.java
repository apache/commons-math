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
package org.apache.commons.math4.legacy.linear;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.math4.legacy.core.Field;
import org.apache.commons.math4.legacy.core.dfp.Dfp;
import org.apache.commons.math4.legacy.core.dfp.DfpField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("boxing")
public class OpenIntToFieldTest {

    private Map<Integer, Dfp> javaMap = new HashMap<>();
    private DfpField field = Dfp25.getField();

    @Before
    public void setUp() {
        javaMap.put(50, Dfp25.of(100.0));
        javaMap.put(75, Dfp25.of(75.0));
        javaMap.put(25, Dfp25.of(500.0));
        javaMap.put(Integer.MAX_VALUE, Dfp25.of(Integer.MAX_VALUE));
        javaMap.put(0, Dfp25.of(-1.0));
        javaMap.put(1, Dfp25.of(0.0));
        javaMap.put(33, Dfp25.of(-0.1));
        javaMap.put(23234234, Dfp25.of(-242343.0));
        javaMap.put(23321, Dfp25.of (Integer.MIN_VALUE));
        javaMap.put(-4444, Dfp25.of(332.0));
        javaMap.put(-1, Dfp25.of(-2323.0));
        javaMap.put(Integer.MIN_VALUE, Dfp25.of(44.0));

        /* Add a few more to cause the table to rehash */
        javaMap.putAll(generate());
    }

    private Map<Integer, Dfp> generate() {
        Map<Integer, Dfp> map = new HashMap<>();
        Random r = new Random();
        double dd = 0;
        for (int i = 0; i < 2000; ++i) {
            dd = r.nextDouble();
        }
        map.put(r.nextInt(), Dfp25.of(dd));
        return map;
    }

    private OpenIntToFieldHashMap<Dfp> createFromJavaMap(Field<Dfp> field) {
        OpenIntToFieldHashMap<Dfp> map = new OpenIntToFieldHashMap<>(field);
        for (Map.Entry<Integer, Dfp> mapEntry : javaMap.entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
        }
        return map;
    }

    @Test
    public void testPutAndGetWith0ExpectedSize() {
        OpenIntToFieldHashMap<Dfp> map = new OpenIntToFieldHashMap<>(field,0);
        assertPutAndGet(map);
    }

    @Test
    public void testPutAndGetWithExpectedSize() {
        OpenIntToFieldHashMap<Dfp> map = new OpenIntToFieldHashMap<>(field,500);
        assertPutAndGet(map);
    }

    @Test
    public void testPutAndGet() {
        OpenIntToFieldHashMap<Dfp> map = new OpenIntToFieldHashMap<>(field);
        assertPutAndGet(map);
    }

    private void assertPutAndGet(OpenIntToFieldHashMap<Dfp> map) {
        assertPutAndGet(map, 0, new HashSet<>());
    }

    private void assertPutAndGet(OpenIntToFieldHashMap<Dfp> map, int mapSize,
            Set<Integer> keysInMap) {
        Assert.assertEquals(mapSize, map.size());
        for (Map.Entry<Integer, Dfp> mapEntry : javaMap.entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
            if (!keysInMap.contains(mapEntry.getKey())) {
                ++mapSize;
            }
            Assert.assertEquals(mapSize, map.size());
            Assert.assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
        }
    }

    @Test
    public void testPutAbsentOnExisting() {
        OpenIntToFieldHashMap<Dfp> map = createFromJavaMap(field);
        int size = javaMap.size();
        for (Map.Entry<Integer, Dfp> mapEntry : generateAbsent().entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
            Assert.assertEquals(++size, map.size());
            Assert.assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
        }
    }

    @Test
    public void testPutOnExisting() {
        OpenIntToFieldHashMap<Dfp> map = createFromJavaMap(field);
        for (Map.Entry<Integer, Dfp> mapEntry : javaMap.entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
            Assert.assertEquals(javaMap.size(), map.size());
            Assert.assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
        }
    }

    @Test
    public void testGetAbsent() {
        Map<Integer, Dfp> generated = generateAbsent();
        OpenIntToFieldHashMap<Dfp> map = createFromJavaMap(field);

        for (Map.Entry<Integer, Dfp> mapEntry : generated.entrySet()) {
            Assert.assertTrue(field.getZero().equals(map.get(mapEntry.getKey())));
        }
    }

    @Test
    public void testGetFromEmpty() {
        OpenIntToFieldHashMap<Dfp> map = new OpenIntToFieldHashMap<>(field);
        Assert.assertEquals(field.getZero(), map.get(5));
        Assert.assertEquals(field.getZero(), map.get(0));
        Assert.assertEquals(field.getZero(), map.get(50));
    }

    @Test
    public void testRemove() {
        OpenIntToFieldHashMap<Dfp> map = createFromJavaMap(field);
        int mapSize = javaMap.size();
        Assert.assertEquals(mapSize, map.size());
        for (Map.Entry<Integer, Dfp> mapEntry : javaMap.entrySet()) {
            map.remove(mapEntry.getKey());
            Assert.assertEquals(--mapSize, map.size());
            Assert.assertEquals(field.getZero(), map.get(mapEntry.getKey()));
        }

        /* Ensure that put and get still work correctly after removals */
        assertPutAndGet(map);
    }

    /* This time only remove some entries */
    @Test
    public void testRemove2() {
        OpenIntToFieldHashMap<Dfp> map = createFromJavaMap(field);
        int mapSize = javaMap.size();
        int count = 0;
        Set<Integer> keysInMap = new HashSet<>(javaMap.keySet());
        for (Map.Entry<Integer, Dfp> mapEntry : javaMap.entrySet()) {
            keysInMap.remove(mapEntry.getKey());
            map.remove(mapEntry.getKey());
            Assert.assertEquals(--mapSize, map.size());
            Assert.assertEquals(field.getZero(), map.get(mapEntry.getKey()));
            if (count++ > 5) {
                break;
            }
        }

        /* Ensure that put and get still work correctly after removals */
        assertPutAndGet(map, mapSize, keysInMap);
    }

    @Test
    public void testRemoveFromEmpty() {
        OpenIntToFieldHashMap<Dfp> map = new OpenIntToFieldHashMap<>(field);
        Assert.assertEquals(field.getZero(), map.remove(50));
    }

    @Test
    public void testRemoveAbsent() {
        Map<Integer, Dfp> generated = generateAbsent();

        OpenIntToFieldHashMap<Dfp> map = createFromJavaMap(field);
        int mapSize = map.size();

        for (Map.Entry<Integer, Dfp> mapEntry : generated.entrySet()) {
            map.remove(mapEntry.getKey());
            Assert.assertEquals(mapSize, map.size());
            Assert.assertEquals(field.getZero(), map.get(mapEntry.getKey()));
        }
    }

    /**
     * Returns a map with at least 100 elements where each element is absent from javaMap.
     */
    private Map<Integer, Dfp> generateAbsent() {
        Map<Integer, Dfp> generated = new HashMap<>();
        do {
            generated.putAll(generate());
            for (Integer key : javaMap.keySet()) {
                generated.remove(key);
            }
        } while (generated.size() < 100);
        return generated;
    }

    @Test
    public void testCopy() {
        OpenIntToFieldHashMap<Dfp> copy =
            new OpenIntToFieldHashMap<>(createFromJavaMap(field));
        Assert.assertEquals(javaMap.size(), copy.size());

        for (Map.Entry<Integer, Dfp> mapEntry : javaMap.entrySet()) {
            Assert.assertEquals(mapEntry.getValue(), copy.get(mapEntry.getKey()));
        }
    }

    @Test
    public void testContainsKey() {
        OpenIntToFieldHashMap<Dfp> map = createFromJavaMap(field);
        for (Entry<Integer, Dfp> mapEntry : javaMap.entrySet()) {
            Assert.assertTrue(map.containsKey(mapEntry.getKey()));
        }
        for (Map.Entry<Integer, Dfp> mapEntry : generateAbsent().entrySet()) {
            Assert.assertFalse(map.containsKey(mapEntry.getKey()));
        }
        for (Entry<Integer, Dfp> mapEntry : javaMap.entrySet()) {
            int key = mapEntry.getKey();
            Assert.assertTrue(map.containsKey(key));
            map.remove(key);
            Assert.assertFalse(map.containsKey(key));
        }
    }

    @Test
    public void testIterator() {
        OpenIntToFieldHashMap<Dfp> map = createFromJavaMap(field);
        OpenIntToFieldHashMap<Dfp>.Iterator iterator = map.iterator();
        for (int i = 0; i < map.size(); ++i) {
            Assert.assertTrue(iterator.hasNext());
            iterator.advance();
            int key = iterator.key();
            Assert.assertTrue(map.containsKey(key));
            Assert.assertEquals(javaMap.get(key), map.get(key));
            Assert.assertEquals(javaMap.get(key), iterator.value());
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
        OpenIntToFieldHashMap<Dfp> map = createFromJavaMap(field);
        OpenIntToFieldHashMap<Dfp>.Iterator iterator = map.iterator();
        map.put(3, Dfp25.of(3));
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
        OpenIntToFieldHashMap<Dfp> map = new OpenIntToFieldHashMap<>(field);
        int key1 = -1996012590;
        Dfp value1 = Dfp25.of(1);
        map.put(key1, value1);
        int key2 = 835099822;
        map.put(key2, value1);
        int key3 = 1008859686;
        map.put(key3, value1);
        Assert.assertEquals(value1, map.get(key3));
        Assert.assertEquals(3, map.size());

        map.remove(key2);
        Dfp value2 = Dfp25.of(2);
        map.put(key3, value2);
        Assert.assertEquals(value2, map.get(key3));
        Assert.assertEquals(2, map.size());
    }

    /**
     * Similar to testPutKeysWithCollisions() but exercises the codepaths in a slightly
     * different manner.
     */
    @Test
    public void testPutKeysWithCollision2() {
        OpenIntToFieldHashMap<Dfp> map = new OpenIntToFieldHashMap<>(field);
        int key1 = 837989881;
        Dfp value1 = Dfp25.of(1);
        map.put(key1, value1);
        int key2 = 476463321;
        map.put(key2, value1);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(value1, map.get(key2));

        map.remove(key1);
        Dfp value2 = Dfp25.of(2);
        map.put(key2, value2);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(value2, map.get(key2));
    }
}
