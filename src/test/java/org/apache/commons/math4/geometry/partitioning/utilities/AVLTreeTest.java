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
package org.apache.commons.math3.geometry.partitioning.utilities;

import org.apache.commons.math3.geometry.partitioning.utilities.AVLTree;
import org.junit.Assert;
import org.junit.Test;

@Deprecated
public class AVLTreeTest {

    @Test
    public void testInsert() {
        // this array in this order allows to pass in all branches
        // of the insertion algorithm
        int[] array = { 16, 13, 15, 14,  2,  0, 12,  9,  8,  5,
            11, 18, 19, 17,  4,  7,  1,  3,  6, 10 };
        AVLTree<Integer> tree = buildTree(array);

        Assert.assertEquals(array.length, tree.size());

        for (int i = 0; i < array.length; ++i) {
            Assert.assertEquals(array[i], value(tree.getNotSmaller(new Integer(array[i]))));
        }

        checkOrder(tree);

    }

    @Test
    public void testDelete1() {
        int[][][] arrays = {
            { { 16, 13, 15, 14, 2, 0, 12, 9, 8, 5, 11, 18, 19, 17, 4, 7, 1, 3, 6, 10 },
                { 11, 10, 9, 12, 16, 15, 13, 18, 5, 0, 3, 2, 14, 6, 19, 17, 8, 4, 7, 1 } },
                { { 16, 13, 15, 14, 2, 0, 12, 9, 8, 5, 11, 18, 19, 17, 4, 7, 1, 3, 6, 10 },
                    { 0, 17, 14, 15, 16, 18,  6 } },
                    { { 6, 2, 7, 8, 1, 4, 3, 5 }, { 8 } },
                    { { 6, 2, 7, 8, 1, 4, 5 }, { 8 } },
                    { { 3, 7, 2, 1, 5, 8, 4 }, { 1 } },
                    { { 3, 7, 2, 1, 5, 8, 6 }, { 1 } }
        };
        for (int i = 0; i < arrays.length; ++i) {
            AVLTree<Integer> tree = buildTree(arrays[i][0]);
            Assert.assertTrue(! tree.delete(new Integer(-2000)));
            for (int j = 0; j < arrays[i][1].length; ++j) {
                Assert.assertTrue(tree.delete(tree.getNotSmaller(new Integer(arrays[i][1][j])).getElement()));
                Assert.assertEquals(arrays[i][0].length - j - 1, tree.size());
            }
        }
    }

    @Test
    public void testNavigation() {
        int[] array = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        AVLTree<Integer> tree = buildTree(array);

        AVLTree<Integer>.Node node = tree.getSmallest();
        Assert.assertEquals(array[0], value(node));
        for (int i = 0; i < array.length; ++i) {
            Assert.assertEquals(array[i], value(node));
            node = node.getNext();
        }
        Assert.assertNull(node);

        node = tree.getLargest();
        Assert.assertEquals(array[array.length - 1], value(node));
        for (int i = array.length - 1; i >= 0; --i) {
            Assert.assertEquals(array[i], value(node));
            node = node.getPrevious();
        }
        Assert.assertNull(node);

        checkOrder(tree);

    }

    @Test
    public void testSearch() {
        int[] array = { 2, 4, 6, 8, 10, 12, 14 };
        AVLTree<Integer> tree = buildTree(array);

        Assert.assertNull(tree.getNotLarger(new Integer(array[0] - 1)));
        Assert.assertNull(tree.getNotSmaller(new Integer(array[array.length - 1] + 1)));

        for (int i = 0; i < array.length; ++i) {
            Assert.assertEquals(array[i],
                                value(tree.getNotSmaller(new Integer(array[i] - 1))));
            Assert.assertEquals(array[i],
                                value(tree.getNotLarger(new Integer(array[i] + 1))));
        }

        checkOrder(tree);

    }

    @Test
    public void testRepetition() {
        int[] array = { 1, 1, 3, 3, 4, 5, 6, 7, 7, 7, 7, 7 };
        AVLTree<Integer> tree = buildTree(array);
        Assert.assertEquals(array.length, tree.size());

        AVLTree<Integer>.Node node = tree.getNotSmaller(new Integer(3));
        Assert.assertEquals(3, value(node));
        Assert.assertEquals(1, value(node.getPrevious()));
        Assert.assertEquals(3, value(node.getNext()));
        Assert.assertEquals(4, value(node.getNext().getNext()));

        node = tree.getNotLarger(new Integer(2));
        Assert.assertEquals(1, value(node));
        Assert.assertEquals(1, value(node.getPrevious()));
        Assert.assertEquals(3, value(node.getNext()));
        Assert.assertNull(node.getPrevious().getPrevious());

        AVLTree<Integer>.Node otherNode = tree.getNotSmaller(new Integer(1));
        Assert.assertTrue(node != otherNode);
        Assert.assertEquals(1, value(otherNode));
        Assert.assertNull(otherNode.getPrevious());

        node = tree.getNotLarger(new Integer(10));
        Assert.assertEquals(7, value(node));
        Assert.assertNull(node.getNext());
        node = node.getPrevious();
        Assert.assertEquals(7, value(node));
        node = node.getPrevious();
        Assert.assertEquals(7, value(node));
        node = node.getPrevious();
        Assert.assertEquals(7, value(node));
        node = node.getPrevious();
        Assert.assertEquals(7, value(node));
        node = node.getPrevious();
        Assert.assertEquals(6, value(node));

        checkOrder(tree);

    }

    private AVLTree<Integer> buildTree(int[] array) {
        AVLTree<Integer> tree = new AVLTree<Integer>();
        for (int i = 0; i < array.length; ++i) {
            tree.insert(new Integer(array[i]));
            tree.insert(null);
        }
        return tree;
    }

    private int value(AVLTree<Integer>.Node node) {
        return node.getElement().intValue();
    }

    private void checkOrder(AVLTree<Integer> tree) {
        AVLTree<Integer>.Node next = null;
        for (AVLTree<Integer>.Node node = tree.getSmallest();
        node != null;
        node = next) {
            next = node.getNext();
            if (next != null) {
                Assert.assertTrue(node.getElement().compareTo(next.getElement()) <= 0);
            }
        }
    }

}
