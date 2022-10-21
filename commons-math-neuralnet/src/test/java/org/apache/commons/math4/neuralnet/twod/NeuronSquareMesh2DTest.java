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

package org.apache.commons.math4.neuralnet.twod;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import org.apache.commons.math4.neuralnet.FeatureInitializer;
import org.apache.commons.math4.neuralnet.FeatureInitializerFactory;
import org.apache.commons.math4.neuralnet.Network;
import org.apache.commons.math4.neuralnet.Neuron;
import org.apache.commons.math4.neuralnet.SquareNeighbourhood;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link NeuronSquareMesh2D} and {@link Network} functionality for
 * a two-dimensional network.
 */
public class NeuronSquareMesh2DTest {

    private final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
    private final FeatureInitializer init = FeatureInitializerFactory.uniform(rng, 0, 2);

    @Test
    public void testMinimalNetworkSize1() {
        final FeatureInitializer[] initArray = {init};

        assertThrows(IllegalArgumentException.class, () ->
                new NeuronSquareMesh2D(1, false,
                                       2, false,
                                       SquareNeighbourhood.VON_NEUMANN,
                                       initArray));
    }

    @Test
    public void testMinimalNetworkSize2() {
        final FeatureInitializer[] initArray = {init};

        assertThrows(IllegalArgumentException.class, () ->
                new NeuronSquareMesh2D(2, false,
                                       0, false,
                                       SquareNeighbourhood.VON_NEUMANN,
                                       initArray));
    }

    @Test
    public void testGetFeaturesSize() {
        final FeatureInitializer[] initArray = {init, init, init};

        final Network net = new NeuronSquareMesh2D(2, false,
                                                   2, false,
                                                   SquareNeighbourhood.VON_NEUMANN,
                                                   initArray).getNetwork();
        Assert.assertEquals(3, net.getFeaturesSize());
    }

    @Test
    public void testAccessors() {
        final FeatureInitializer[] initArray = {init};
        NeuronSquareMesh2D map;

        for (SquareNeighbourhood type : SquareNeighbourhood.values()) {
            map = new NeuronSquareMesh2D(4, false, 2, true, type, initArray);
            Assert.assertFalse(map.isWrappedRow());
            Assert.assertTrue(map.isWrappedColumn());
            Assert.assertEquals(type, map.getSquareNeighbourhood());

            map = new NeuronSquareMesh2D(3, true, 4, false, type, initArray);
            Assert.assertTrue(map.isWrappedRow());
            Assert.assertFalse(map.isWrappedColumn());
            Assert.assertEquals(type, map.getSquareNeighbourhood());
        }
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1
     *  |     |
     *  |     |
     *  2-----3
     */
    @Test
    public void test2x2Network() {
        final FeatureInitializer[] initArray = {init};
        final Network net = new NeuronSquareMesh2D(2, false,
                                                   2, false,
                                                   SquareNeighbourhood.VON_NEUMANN,
                                                   initArray).getNetwork();
        Collection<Neuron> neighbours;

        // Neurons 0 and 3.
        for (long id : new long[] {0, 3 }) {
            neighbours = net.getNeighbours(net.getNeuron(id));
            for (long nId : new long[] {1, 2 }) {
                Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
            }
            // Ensures that no other neurons is in the neighbourhood set.
            Assert.assertEquals(2, neighbours.size());
        }

        // Neurons 1 and 2.
        for (long id : new long[] {1, 2 }) {
            neighbours = net.getNeighbours(net.getNeuron(id));
            for (long nId : new long[] {0, 3 }) {
                Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
            }
            // Ensures that no other neurons is in the neighbourhood set.
            Assert.assertEquals(2, neighbours.size());
        }
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1
     *  |     |
     *  |     |
     *  2-----3
     */
    @Test
    public void test2x2Network2() {
        final FeatureInitializer[] initArray = {init};
        final Network net = new NeuronSquareMesh2D(2, false,
                                                   2, false,
                                                   SquareNeighbourhood.MOORE,
                                                   initArray).getNetwork();
        Collection<Neuron> neighbours;

        // All neurons
        for (long id : new long[] {0, 1, 2, 3 }) {
            neighbours = net.getNeighbours(net.getNeuron(id));
            for (long nId : new long[] {0, 1, 2, 3 }) {
                if (id != nId) {
                    Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
                }
            }
        }
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     *  |     |     |
     *  |     |     |
     *  3-----4-----5
     */
    @Test
    public void test3x2CylinderNetwork() {
        final FeatureInitializer[] initArray = {init};
        final Network net = new NeuronSquareMesh2D(2, false,
                                                   3, true,
                                                   SquareNeighbourhood.VON_NEUMANN,
                                                   initArray).getNetwork();
        Collection<Neuron> neighbours;

        // Neuron 0.
        neighbours = net.getNeighbours(net.getNeuron(0));
        for (long nId : new long[] {1, 2, 3 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(3, neighbours.size());

        // Neuron 1.
        neighbours = net.getNeighbours(net.getNeuron(1));
        for (long nId : new long[] {0, 2, 4 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(3, neighbours.size());

        // Neuron 2.
        neighbours = net.getNeighbours(net.getNeuron(2));
        for (long nId : new long[] {0, 1, 5 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(3, neighbours.size());

        // Neuron 3.
        neighbours = net.getNeighbours(net.getNeuron(3));
        for (long nId : new long[] {0, 4, 5 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(3, neighbours.size());

        // Neuron 4.
        neighbours = net.getNeighbours(net.getNeuron(4));
        for (long nId : new long[] {1, 3, 5 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(3, neighbours.size());

        // Neuron 5.
        neighbours = net.getNeighbours(net.getNeuron(5));
        for (long nId : new long[] {2, 3, 4 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(3, neighbours.size());
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     *  |     |     |
     *  |     |     |
     *  3-----4-----5
     */
    @Test
    public void test3x2CylinderNetwork2() {
        final FeatureInitializer[] initArray = {init};
        final Network net = new NeuronSquareMesh2D(2, false,
                                                   3, true,
                                                   SquareNeighbourhood.MOORE,
                                                   initArray).getNetwork();
        Collection<Neuron> neighbours;

        // All neurons.
        for (long id : new long[] {0, 1, 2, 3, 4, 5 }) {
            neighbours = net.getNeighbours(net.getNeuron(id));
            for (long nId : new long[] {0, 1, 2, 3, 4, 5 }) {
                if (id != nId) {
                    Assert.assertTrue("id=" + id + " nId=" + nId,
                                      neighbours.contains(net.getNeuron(nId)));
                }
            }
        }
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     *  |     |     |
     *  |     |     |
     *  3-----4-----5
     *  |     |     |
     *  |     |     |
     *  6-----7-----8
     */
    @Test
    public void test3x3TorusNetwork() {
        final FeatureInitializer[] initArray = {init};
        final Network net = new NeuronSquareMesh2D(3, true,
                                                   3, true,
                                                   SquareNeighbourhood.VON_NEUMANN,
                                                   initArray).getNetwork();
        Collection<Neuron> neighbours;

        // Neuron 0.
        neighbours = net.getNeighbours(net.getNeuron(0));
        for (long nId : new long[] {1, 2, 3, 6 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(4, neighbours.size());

        // Neuron 1.
        neighbours = net.getNeighbours(net.getNeuron(1));
        for (long nId : new long[] {0, 2, 4, 7 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(4, neighbours.size());

        // Neuron 2.
        neighbours = net.getNeighbours(net.getNeuron(2));
        for (long nId : new long[] {0, 1, 5, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(4, neighbours.size());

        // Neuron 3.
        neighbours = net.getNeighbours(net.getNeuron(3));
        for (long nId : new long[] {0, 4, 5, 6 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(4, neighbours.size());

        // Neuron 4.
        neighbours = net.getNeighbours(net.getNeuron(4));
        for (long nId : new long[] {1, 3, 5, 7 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(4, neighbours.size());

        // Neuron 5.
        neighbours = net.getNeighbours(net.getNeuron(5));
        for (long nId : new long[] {2, 3, 4, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(4, neighbours.size());

        // Neuron 6.
        neighbours = net.getNeighbours(net.getNeuron(6));
        for (long nId : new long[] {0, 3, 7, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(4, neighbours.size());

        // Neuron 7.
        neighbours = net.getNeighbours(net.getNeuron(7));
        for (long nId : new long[] {1, 4, 6, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(4, neighbours.size());

        // Neuron 8.
        neighbours = net.getNeighbours(net.getNeuron(8));
        for (long nId : new long[] {2, 5, 6, 7 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(4, neighbours.size());
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     *  |     |     |
     *  |     |     |
     *  3-----4-----5
     *  |     |     |
     *  |     |     |
     *  6-----7-----8
     */
    @Test
    public void test3x3TorusNetwork2() {
        final FeatureInitializer[] initArray = {init};
        final Network net = new NeuronSquareMesh2D(3, true,
                                                   3, true,
                                                   SquareNeighbourhood.MOORE,
                                                   initArray).getNetwork();
        Collection<Neuron> neighbours;

        // All neurons.
        for (long id : new long[] {0, 1, 2, 3, 4, 5, 6, 7, 8 }) {
            neighbours = net.getNeighbours(net.getNeuron(id));
            for (long nId : new long[] {0, 1, 2, 3, 4, 5, 6, 7, 8 }) {
                if (id != nId) {
                    Assert.assertTrue("id=" + id + " nId=" + nId,
                                      neighbours.contains(net.getNeuron(nId)));
                }
            }
        }
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     *  |     |     |
     *  |     |     |
     *  3-----4-----5
     *  |     |     |
     *  |     |     |
     *  6-----7-----8
     */
    @Test
    public void test3x3CylinderNetwork() {
        final FeatureInitializer[] initArray = {init};
        final Network net = new NeuronSquareMesh2D(3, false,
                                                   3, true,
                                                   SquareNeighbourhood.MOORE,
                                                   initArray).getNetwork();
        Collection<Neuron> neighbours;

        // Neuron 0.
        neighbours = net.getNeighbours(net.getNeuron(0));
        for (long nId : new long[] {1, 2, 3, 4, 5}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(5, neighbours.size());

        // Neuron 1.
        neighbours = net.getNeighbours(net.getNeuron(1));
        for (long nId : new long[] {0, 2, 3, 4, 5 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(5, neighbours.size());

        // Neuron 2.
        neighbours = net.getNeighbours(net.getNeuron(2));
        for (long nId : new long[] {0, 1, 3, 4, 5 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(5, neighbours.size());

        // Neuron 3.
        neighbours = net.getNeighbours(net.getNeuron(3));
        for (long nId : new long[] {0, 1, 2, 4, 5, 6, 7, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(8, neighbours.size());

        // Neuron 4.
        neighbours = net.getNeighbours(net.getNeuron(4));
        for (long nId : new long[] {0, 1, 2, 3, 5, 6, 7, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(8, neighbours.size());

        // Neuron 5.
        neighbours = net.getNeighbours(net.getNeuron(5));
        for (long nId : new long[] {0, 1, 2, 3, 4, 6, 7, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(8, neighbours.size());

        // Neuron 6.
        neighbours = net.getNeighbours(net.getNeuron(6));
        for (long nId : new long[] {3, 4, 5, 7, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(5, neighbours.size());

        // Neuron 7.
        neighbours = net.getNeighbours(net.getNeuron(7));
        for (long nId : new long[] {3, 4, 5, 6, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(5, neighbours.size());

        // Neuron 8.
        neighbours = net.getNeighbours(net.getNeuron(8));
        for (long nId : new long[] {3, 4, 5, 6, 7 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(5, neighbours.size());
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     *  |     |     |
     *  |     |     |
     *  3-----4-----5
     *  |     |     |
     *  |     |     |
     *  6-----7-----8
     */
    @Test
    public void test3x3CylinderNetwork2() {
        final FeatureInitializer[] initArray = {init};
        final Network net = new NeuronSquareMesh2D(3, false,
                                                   3, false,
                                                   SquareNeighbourhood.MOORE,
                                                   initArray).getNetwork();
        Collection<Neuron> neighbours;

        // Neuron 0.
        neighbours = net.getNeighbours(net.getNeuron(0));
        for (long nId : new long[] {1, 3, 4}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(3, neighbours.size());

        // Neuron 1.
        neighbours = net.getNeighbours(net.getNeuron(1));
        for (long nId : new long[] {0, 2, 3, 4, 5 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(5, neighbours.size());

        // Neuron 2.
        neighbours = net.getNeighbours(net.getNeuron(2));
        for (long nId : new long[] {1, 4, 5 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(3, neighbours.size());

        // Neuron 3.
        neighbours = net.getNeighbours(net.getNeuron(3));
        for (long nId : new long[] {0, 1, 4, 6, 7 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(5, neighbours.size());

        // Neuron 4.
        neighbours = net.getNeighbours(net.getNeuron(4));
        for (long nId : new long[] {0, 1, 2, 3, 5, 6, 7, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(8, neighbours.size());

        // Neuron 5.
        neighbours = net.getNeighbours(net.getNeuron(5));
        for (long nId : new long[] {1, 2, 4, 7, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(5, neighbours.size());

        // Neuron 6.
        neighbours = net.getNeighbours(net.getNeuron(6));
        for (long nId : new long[] {3, 4, 7 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(3, neighbours.size());

        // Neuron 7.
        neighbours = net.getNeighbours(net.getNeuron(7));
        for (long nId : new long[] {3, 4, 5, 6, 8 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(5, neighbours.size());

        // Neuron 8.
        neighbours = net.getNeighbours(net.getNeuron(8));
        for (long nId : new long[] {4, 5, 7 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(3, neighbours.size());
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2-----3-----4
     *  |     |     |     |     |
     *  |     |     |     |     |
     *  5-----6-----7-----8-----9
     *  |     |     |     |     |
     *  |     |     |     |     |
     *  10----11----12----13---14
     *  |     |     |     |     |
     *  |     |     |     |     |
     *  15----16----17----18---19
     *  |     |     |     |     |
     *  |     |     |     |     |
     *  20----21----22----23---24
     */
    @Test
    public void testConcentricNeighbourhood() {
        final FeatureInitializer[] initArray = {init};
        final Network net = new NeuronSquareMesh2D(5, true,
                                                   5, true,
                                                   SquareNeighbourhood.VON_NEUMANN,
                                                   initArray).getNetwork();

        Collection<Neuron> neighbours;
        Collection<Neuron> exclude = new HashSet<>();

        // Level-1 neighbourhood.
        neighbours = net.getNeighbours(net.getNeuron(12));
        for (long nId : new long[] {7, 11, 13, 17 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(4, neighbours.size());

        // 1. Add the neuron to the "exclude" list.
        exclude.add(net.getNeuron(12));
        // 2. Add all neurons from level-1 neighbourhood.
        exclude.addAll(neighbours);
        // 3. Retrieve level-2 neighbourhood.
        neighbours = net.getNeighbours(neighbours, exclude);
        for (long nId : new long[] {6, 8, 16, 18, 2, 10, 14, 22 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(8, neighbours.size());
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2-----3-----4
     *  |     |     |     |     |
     *  |     |     |     |     |
     *  5-----6-----7-----8-----9
     *  |     |     |     |     |
     *  |     |     |     |     |
     *  10----11----12----13---14
     *  |     |     |     |     |
     *  |     |     |     |     |
     *  15----16----17----18---19
     *  |     |     |     |     |
     *  |     |     |     |     |
     *  20----21----22----23---24
     */
    @Test
    public void testConcentricNeighbourhood2() {
        final FeatureInitializer[] initArray = {init};
        final Network net = new NeuronSquareMesh2D(5, true,
                                                   5, true,
                                                   SquareNeighbourhood.MOORE,
                                                   initArray).getNetwork();

        Collection<Neuron> neighbours;
        Collection<Neuron> exclude = new HashSet<>();

        // Level-1 neighbourhood.
        neighbours = net.getNeighbours(net.getNeuron(8));
        for (long nId : new long[] {2, 3, 4, 7, 9, 12, 13, 14}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(8, neighbours.size());

        // 1. Add the neuron to the "exclude" list.
        exclude.add(net.getNeuron(8));
        // 2. Add all neurons from level-1 neighbourhood.
        exclude.addAll(neighbours);
        // 3. Retrieve level-2 neighbourhood.
        neighbours = net.getNeighbours(neighbours, exclude);
        for (long nId : new long[] {1, 6, 11, 16, 17, 18, 19, 15, 10, 5, 0, 20, 24, 23, 22, 21}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(16, neighbours.size());
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1
     *  |     |
     *  |     |
     *  2-----3
     */
    @Test
    public void testGetNeuron() {
        final FeatureInitializer[] initArray = {init};
        final NeuronSquareMesh2D net = new NeuronSquareMesh2D(2, false,
                                                              2, true,
                                                              SquareNeighbourhood.VON_NEUMANN,
                                                              initArray);
        Assert.assertEquals(0, net.getNeuron(0, 0).getIdentifier());
        Assert.assertEquals(1, net.getNeuron(0, 1).getIdentifier());
        Assert.assertEquals(2, net.getNeuron(1, 0).getIdentifier());
        Assert.assertEquals(3, net.getNeuron(1, 1).getIdentifier());

        try {
            net.getNeuron(2, 0);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
        try {
            net.getNeuron(0, 2);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
        try {
            net.getNeuron(-1, 0);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
        try {
            net.getNeuron(0, -1);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     *  |     |     |
     *  |     |     |
     *  3-----4-----5
     *  |     |     |
     *  |     |     |
     *  6-----7-----8
     */
    @Test
    public void testGetNeuronAlongDirection() {
        final FeatureInitializer[] initArray = {init};
        final NeuronSquareMesh2D net = new NeuronSquareMesh2D(3, false,
                                                              3, false,
                                                              SquareNeighbourhood.VON_NEUMANN,
                                                              initArray);
        Assert.assertEquals(0, net.getNeuron(1, 1,
                                             NeuronSquareMesh2D.HorizontalDirection.LEFT,
                                             NeuronSquareMesh2D.VerticalDirection.UP).getIdentifier());
        Assert.assertEquals(1, net.getNeuron(1, 1,
                                             NeuronSquareMesh2D.HorizontalDirection.CENTER,
                                             NeuronSquareMesh2D.VerticalDirection.UP).getIdentifier());
        Assert.assertEquals(2, net.getNeuron(1, 1,
                                             NeuronSquareMesh2D.HorizontalDirection.RIGHT,
                                             NeuronSquareMesh2D.VerticalDirection.UP).getIdentifier());
        Assert.assertEquals(3, net.getNeuron(1, 1,
                                             NeuronSquareMesh2D.HorizontalDirection.LEFT,
                                             NeuronSquareMesh2D.VerticalDirection.CENTER).getIdentifier());
        Assert.assertEquals(4, net.getNeuron(1, 1,
                                             NeuronSquareMesh2D.HorizontalDirection.CENTER,
                                             NeuronSquareMesh2D.VerticalDirection.CENTER).getIdentifier());
        Assert.assertEquals(5, net.getNeuron(1, 1,
                                             NeuronSquareMesh2D.HorizontalDirection.RIGHT,
                                             NeuronSquareMesh2D.VerticalDirection.CENTER).getIdentifier());
        Assert.assertEquals(6, net.getNeuron(1, 1,
                                             NeuronSquareMesh2D.HorizontalDirection.LEFT,
                                             NeuronSquareMesh2D.VerticalDirection.DOWN).getIdentifier());
        Assert.assertEquals(7, net.getNeuron(1, 1,
                                             NeuronSquareMesh2D.HorizontalDirection.CENTER,
                                             NeuronSquareMesh2D.VerticalDirection.DOWN).getIdentifier());
        Assert.assertEquals(8, net.getNeuron(1, 1,
                                             NeuronSquareMesh2D.HorizontalDirection.RIGHT,
                                             NeuronSquareMesh2D.VerticalDirection.DOWN).getIdentifier());

        // Locations not in map.
        Assert.assertNull(net.getNeuron(0, 1,
                                        NeuronSquareMesh2D.HorizontalDirection.CENTER,
                                        NeuronSquareMesh2D.VerticalDirection.UP));
        Assert.assertNull(net.getNeuron(1, 0,
                                        NeuronSquareMesh2D.HorizontalDirection.LEFT,
                                        NeuronSquareMesh2D.VerticalDirection.CENTER));
        Assert.assertNull(net.getNeuron(2, 1,
                                        NeuronSquareMesh2D.HorizontalDirection.CENTER,
                                        NeuronSquareMesh2D.VerticalDirection.DOWN));
        Assert.assertNull(net.getNeuron(1, 2,
                                        NeuronSquareMesh2D.HorizontalDirection.RIGHT,
                                        NeuronSquareMesh2D.VerticalDirection.CENTER));
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     *  |     |     |
     *  |     |     |
     *  3-----4-----5
     *  |     |     |
     *  |     |     |
     *  6-----7-----8
     */
    @Test
    public void testGetNeuronAlongDirectionWrappedMap() {
        final FeatureInitializer[] initArray = {init};
        final NeuronSquareMesh2D net = new NeuronSquareMesh2D(3, true,
                                                              3, true,
                                                              SquareNeighbourhood.VON_NEUMANN,
                                                              initArray);
        // No wrapping.
        Assert.assertEquals(3, net.getNeuron(0, 0,
                                             NeuronSquareMesh2D.HorizontalDirection.CENTER,
                                             NeuronSquareMesh2D.VerticalDirection.DOWN).getIdentifier());
        // With wrapping.
        Assert.assertEquals(2, net.getNeuron(0, 0,
                                             NeuronSquareMesh2D.HorizontalDirection.LEFT,
                                             NeuronSquareMesh2D.VerticalDirection.CENTER).getIdentifier());
        Assert.assertEquals(7, net.getNeuron(0, 0,
                                             NeuronSquareMesh2D.HorizontalDirection.RIGHT,
                                             NeuronSquareMesh2D.VerticalDirection.UP).getIdentifier());
        Assert.assertEquals(8, net.getNeuron(0, 0,
                                             NeuronSquareMesh2D.HorizontalDirection.LEFT,
                                             NeuronSquareMesh2D.VerticalDirection.UP).getIdentifier());
        Assert.assertEquals(6, net.getNeuron(0, 0,
                                             NeuronSquareMesh2D.HorizontalDirection.CENTER,
                                             NeuronSquareMesh2D.VerticalDirection.UP).getIdentifier());
        Assert.assertEquals(5, net.getNeuron(0, 0,
                                             NeuronSquareMesh2D.HorizontalDirection.LEFT,
                                             NeuronSquareMesh2D.VerticalDirection.DOWN).getIdentifier());

        // No wrapping.
        Assert.assertEquals(1, net.getNeuron(1, 2,
                                             NeuronSquareMesh2D.HorizontalDirection.LEFT,
                                             NeuronSquareMesh2D.VerticalDirection.UP).getIdentifier());
        // With wrapping.
        Assert.assertEquals(0, net.getNeuron(1, 2,
                                             NeuronSquareMesh2D.HorizontalDirection.RIGHT,
                                             NeuronSquareMesh2D.VerticalDirection.UP).getIdentifier());
        Assert.assertEquals(3, net.getNeuron(1, 2,
                                             NeuronSquareMesh2D.HorizontalDirection.RIGHT,
                                             NeuronSquareMesh2D.VerticalDirection.CENTER).getIdentifier());
        Assert.assertEquals(6, net.getNeuron(1, 2,
                                             NeuronSquareMesh2D.HorizontalDirection.RIGHT,
                                             NeuronSquareMesh2D.VerticalDirection.DOWN).getIdentifier());
    }

    @Test
    public void testIterator() {
        final FeatureInitializer[] initArray = {init};
        final NeuronSquareMesh2D map = new NeuronSquareMesh2D(3, true,
                                                              3, true,
                                                              SquareNeighbourhood.VON_NEUMANN,
                                                              initArray);
        final Set<Neuron> fromMap = new HashSet<>();
        for (Neuron n : map) {
            fromMap.add(n);
        }

        final Network net = map.getNetwork();
        final Set<Neuron> fromNet = new HashSet<>();
        for (Neuron n : net) {
            fromNet.add(n);
        }

        for (Neuron n : fromMap) {
            Assert.assertTrue(fromNet.contains(n));
        }
        for (Neuron n : fromNet) {
            Assert.assertTrue(fromMap.contains(n));
        }
    }

    @Test
    public void testDataVisualization() {
        final FeatureInitializer[] initArray = {init};
        final NeuronSquareMesh2D map = new NeuronSquareMesh2D(3, true,
                                                              3, true,
                                                              SquareNeighbourhood.VON_NEUMANN,
                                                              initArray);

        // Trivial test: Use neurons' features as data.

        final List<double[]> data = StreamSupport.stream(map.spliterator(), false)
            .map(n -> n.getFeatures())
            .collect(Collectors.toList());
        final NeuronSquareMesh2D.DataVisualization v = map.computeQualityIndicators(data);

        final int numRows = map.getNumberOfRows();
        final int numCols = map.getNumberOfColumns();

        // Test hits.
        final double[][] hits = v.getNormalizedHits();
        final double expectedHits = 1d / (numRows * numCols);
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Assert.assertEquals(expectedHits, hits[i][j], 0d);
            }
        }

        // Test quantization error.
        final double[][] qe = v.getQuantizationError();
        final double expectedQE = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Assert.assertEquals(expectedQE, qe[i][j], 0d);
            }
        }
    }

}
