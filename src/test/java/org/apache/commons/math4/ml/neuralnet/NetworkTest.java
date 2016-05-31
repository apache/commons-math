/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math4.ml.neuralnet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.apache.commons.math4.ml.neuralnet.FeatureInitializer;
import org.apache.commons.math4.ml.neuralnet.FeatureInitializerFactory;
import org.apache.commons.math4.ml.neuralnet.Network;
import org.apache.commons.math4.ml.neuralnet.Neuron;
import org.apache.commons.math4.ml.neuralnet.SquareNeighbourhood;
import org.apache.commons.math4.ml.neuralnet.twod.NeuronSquareMesh2D;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link Network}.
 */
public class NetworkTest {
    final FeatureInitializer init = FeatureInitializerFactory.uniform(0, 2);

    @Test
    public void testGetFeaturesSize() {
        final FeatureInitializer[] initArray = { init, init, init };

        final Network net = new NeuronSquareMesh2D(2, false,
                                                   2, false,
                                                   SquareNeighbourhood.VON_NEUMANN,
                                                   initArray).getNetwork();
        Assert.assertEquals(3, net.getFeaturesSize());
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
    public void testDeleteLink() {
        final FeatureInitializer[] initArray = { init };
        final Network net = new NeuronSquareMesh2D(2, false,
                                                   2, false,
                                                   SquareNeighbourhood.VON_NEUMANN,
                                                   initArray).getNetwork();
        Collection<Neuron> neighbours;

        // Delete 0 --> 1.
        net.deleteLink(net.getNeuron(0),
                       net.getNeuron(1));

        // Link from 0 to 1 was deleted.
        neighbours = net.getNeighbours(net.getNeuron(0));
        Assert.assertFalse(neighbours.contains(net.getNeuron(1)));
        // Link from 1 to 0 still exists.
        neighbours = net.getNeighbours(net.getNeuron(1));
        Assert.assertTrue(neighbours.contains(net.getNeuron(0)));
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
    public void testDeleteNeuron() {
        final FeatureInitializer[] initArray = { init };
        final Network net = new NeuronSquareMesh2D(2, false,
                                                   2, false,
                                                   SquareNeighbourhood.VON_NEUMANN,
                                                   initArray).getNetwork();

        Assert.assertEquals(2, net.getNeighbours(net.getNeuron(0)).size());
        Assert.assertEquals(2, net.getNeighbours(net.getNeuron(3)).size());

        // Delete neuron 1.
        net.deleteNeuron(net.getNeuron(1));

        try {
            net.getNeuron(1);
        } catch (NoSuchElementException expected) {}

        Assert.assertEquals(1, net.getNeighbours(net.getNeuron(0)).size());
        Assert.assertEquals(1, net.getNeighbours(net.getNeuron(3)).size());
    }

    @Test
    public void testIterationOrder() {
        final FeatureInitializer[] initArray = { init };
        final Network net = new NeuronSquareMesh2D(4, false,
                                                   3, true,
                                                   SquareNeighbourhood.VON_NEUMANN,
                                                   initArray).getNetwork();

        // Check that the comparator provides a specific order.
        boolean isUnspecifiedOrder = false;
        long previousId = Long.MIN_VALUE;
        for (Neuron n : net.getNeurons(new Network.NeuronIdentifierComparator())) {
            final long currentId = n.getIdentifier();
            if (currentId < previousId) {
                isUnspecifiedOrder = true;
                break;
            }
            previousId = currentId;
        }
        Assert.assertFalse(isUnspecifiedOrder);
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
    public void testCopy() {
        final FeatureInitializer[] initArray = { init };
        final Network net = new NeuronSquareMesh2D(2, false,
                                                   2, false,
                                                   SquareNeighbourhood.VON_NEUMANN,
                                                   initArray).getNetwork();

        final Network copy = net.copy();

        final Neuron netNeuron0 = net.getNeuron(0);
        final Neuron copyNeuron0 = copy.getNeuron(0);
        final Neuron netNeuron1 = net.getNeuron(1);
        final Neuron copyNeuron1 = copy.getNeuron(1);
        Collection<Neuron> netNeighbours;
        Collection<Neuron> copyNeighbours;

        // Check that both networks have the same connections.
        netNeighbours = net.getNeighbours(netNeuron0);
        copyNeighbours = copy.getNeighbours(copyNeuron0);
        Assert.assertTrue(netNeighbours.contains(netNeuron1));
        Assert.assertTrue(copyNeighbours.contains(copyNeuron1));

        // Delete neuron 1 from original.
        net.deleteNeuron(netNeuron1);

        // Check that the networks now differ.
        netNeighbours = net.getNeighbours(netNeuron0);
        copyNeighbours = copy.getNeighbours(copyNeuron0);
        Assert.assertFalse(netNeighbours.contains(netNeuron1));
        Assert.assertTrue(copyNeighbours.contains(copyNeuron1));
    }

    @Test
    public void testSerialize()
        throws IOException,
               ClassNotFoundException {
        final FeatureInitializer[] initArray = { init };
        final Network out = new NeuronSquareMesh2D(4, false,
                                                   3, true,
                                                   SquareNeighbourhood.VON_NEUMANN,
                                                   initArray).getNetwork();

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(out);

        final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(bis);
        final Network in = (Network) ois.readObject();

        for (Neuron nOut : out) {
            final Neuron nIn = in.getNeuron(nOut.getIdentifier());

            // Same values.
            final double[] outF = nOut.getFeatures();
            final double[] inF = nIn.getFeatures();
            Assert.assertEquals(outF.length, inF.length);
            for (int i = 0; i < outF.length; i++) {
                Assert.assertEquals(outF[i], inF[i], 0d);
            }

            // Same neighbours.
            final Collection<Neuron> outNeighbours = out.getNeighbours(nOut);
            final Collection<Neuron> inNeighbours = in.getNeighbours(nIn);
            Assert.assertEquals(outNeighbours.size(), inNeighbours.size());
            for (Neuron oN : outNeighbours) {
                Assert.assertTrue(inNeighbours.contains(in.getNeuron(oN.getIdentifier())));
            }
        }
    }
}
