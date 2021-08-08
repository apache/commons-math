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
package org.apache.commons.math4.legacy.fitting;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.numbers.core.Precision;

/**
 * Tests {@link WeightedObservedPoints}.
 *
 */
public class WeightedObservedPointsTest {
    @Test
    public void testAdd1() {
        final WeightedObservedPoints store = new WeightedObservedPoints();

        final double x = 1.2;
        final double y = 34.56;
        final double w = 0.789;

        store.add(w, x, y);

        Assert.assertTrue(lastElementIsSame(store, new WeightedObservedPoint(w, x, y)));
    }

    @Test
    public void testAdd2() {
        final WeightedObservedPoints store = new WeightedObservedPoints();

        final double x = 1.2;
        final double y = 34.56;
        final double w = 0.789;

        store.add(new WeightedObservedPoint(w, x, y));

        Assert.assertTrue(lastElementIsSame(store, new WeightedObservedPoint(w, x, y)));
    }

    @Test
    public void testAdd3() {
        final WeightedObservedPoints store = new WeightedObservedPoints();

        final double x = 1.2;
        final double y = 34.56;

        store.add(x, y);

        Assert.assertTrue(lastElementIsSame(store, new WeightedObservedPoint(1, x, y)));
    }

    @Test
    public void testClear() {
        final WeightedObservedPoints store = new WeightedObservedPoints();

        store.add(new WeightedObservedPoint(1, 2, 3));
        store.add(new WeightedObservedPoint(2, -1, -2));
        Assert.assertTrue(store.toList().size() == 2);

        store.clear();
        Assert.assertTrue(store.toList().isEmpty());
    }

    // Ensure that an instance returned by "toList()" is independent from
    // the original container.
    @Test
    public void testToListCopy() {
        final WeightedObservedPoints store = new WeightedObservedPoints();

        store.add(new WeightedObservedPoint(1, 2, 3));
        store.add(new WeightedObservedPoint(2, -3, -4));

        final List<WeightedObservedPoint> list = store.toList();
        Assert.assertEquals(2, list.size());

        // Adding an element to "list" has no impact on "store".
        list.add(new WeightedObservedPoint(1.2, 3.4, 5.6));
        Assert.assertNotEquals(list.size(), store.toList().size());

        // Clearing "store" has no impact on "list".
        store.clear();
        Assert.assertFalse(list.isEmpty());
    }

    /**
     * Checks that the contents of the last element is equal to the
     * contents of {@code p}.
     *
     * @param store Container.
     * @param point Observation.
     * @return {@code true} if both elements have the same contents.
     */
    private boolean lastElementIsSame(WeightedObservedPoints store,
                                      WeightedObservedPoint point) {
        final List<WeightedObservedPoint> list = store.toList();
        final WeightedObservedPoint lastPoint = list.get(list.size() - 1);

        if (!Precision.equals(lastPoint.getX(), point.getX())) {
            return false;
        }
        if (!Precision.equals(lastPoint.getY(), point.getY())) {
            return false;
        }
        if (!Precision.equals(lastPoint.getWeight(), point.getWeight())) {
            return false;
        }

        return true;
    }
}
