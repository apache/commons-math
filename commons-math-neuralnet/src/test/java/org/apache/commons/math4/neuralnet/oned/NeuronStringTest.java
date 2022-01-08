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

package org.apache.commons.math4.neuralnet.oned;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import org.apache.commons.math4.neuralnet.FeatureInitializer;
import org.apache.commons.math4.neuralnet.FeatureInitializerFactory;
import org.apache.commons.math4.neuralnet.Network;
import org.apache.commons.math4.neuralnet.Neuron;

/**
 * Tests for {@link NeuronString} and {@link Network} functionality for
 * a one-dimensional network.
 */
public class NeuronStringTest {
    private final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
    private final FeatureInitializer init = FeatureInitializerFactory.uniform(rng, 0, 2);

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2-----3
     */
    @Test
    public void testSegmentNetwork() {
        final FeatureInitializer[] initArray = {init};
        final NeuronString line = new NeuronString(4, false, initArray);
        Assert.assertFalse(line.isWrapped());

        final Network net = line.getNetwork();
        Collection<Neuron> neighbours;

        // Neuron 0.
        neighbours = net.getNeighbours(net.getNeuron(0));
        for (long nId : new long[] {1}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(1, neighbours.size());

        // Neuron 1.
        neighbours = net.getNeighbours(net.getNeuron(1));
        for (long nId : new long[] {0, 2}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(2, neighbours.size());

        // Neuron 2.
        neighbours = net.getNeighbours(net.getNeuron(2));
        for (long nId : new long[] {1, 3}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(2, neighbours.size());

        // Neuron 3.
        neighbours = net.getNeighbours(net.getNeuron(3));
        for (long nId : new long[] {2}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(1, neighbours.size());
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2-----3
     */
    @Test
    public void testCircleNetwork() {
        final FeatureInitializer[] initArray = {init};
        final NeuronString line = new NeuronString(4, true, initArray);
        Assert.assertTrue(line.isWrapped());

        final Network net = line.getNetwork();
        Collection<Neuron> neighbours;

        // Neuron 0.
        neighbours = net.getNeighbours(net.getNeuron(0));
        for (long nId : new long[] {1, 3}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(2, neighbours.size());

        // Neuron 1.
        neighbours = net.getNeighbours(net.getNeuron(1));
        for (long nId : new long[] {0, 2}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(2, neighbours.size());

        // Neuron 2.
        neighbours = net.getNeighbours(net.getNeuron(2));
        for (long nId : new long[] {1, 3}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(2, neighbours.size());

        // Neuron 3.
        neighbours = net.getNeighbours(net.getNeuron(3));
        for (long nId : new long[] {0, 2}) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neighbourhood set.
        Assert.assertEquals(2, neighbours.size());
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2-----3-----4
     */
    @Test
    public void testGetNeighboursWithExclude() {
        final FeatureInitializer[] initArray = {init};
        final Network net = new NeuronString(5, true, initArray).getNetwork();
        final Collection<Neuron> exclude = new ArrayList<>();
        exclude.add(net.getNeuron(1));
        final Collection<Neuron> neighbours = net.getNeighbours(net.getNeuron(0),
                                                                exclude);
        Assert.assertTrue(neighbours.contains(net.getNeuron(4)));
        Assert.assertEquals(1, neighbours.size());
    }
}
