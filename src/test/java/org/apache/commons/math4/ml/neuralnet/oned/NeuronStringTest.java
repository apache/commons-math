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

package org.apache.commons.math4.ml.neuralnet.oned;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math4.ml.neuralnet.FeatureInitializer;
import org.apache.commons.math4.ml.neuralnet.FeatureInitializerFactory;
import org.apache.commons.math4.ml.neuralnet.Network;
import org.apache.commons.math4.ml.neuralnet.Neuron;
import org.apache.commons.math4.ml.neuralnet.oned.NeuronString;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link NeuronString} and {@link Network} functionality for
 * a one-dimensional network.
 */
public class NeuronStringTest {
    final FeatureInitializer init = FeatureInitializerFactory.uniform(0, 2);

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2-----3
     */
    @Test
    public void testSegmentNetwork() {
        final FeatureInitializer[] initArray = { init };
        final Network net = new NeuronString(4, false, initArray).getNetwork();

        Collection<Neuron> neighbours;

        // Neuron 0.
        neighbours = net.getNeighbours(net.getNeuron(0));
        for (long nId : new long[] { 1 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neihbourhood set.
        Assert.assertEquals(1, neighbours.size());

        // Neuron 1.
        neighbours = net.getNeighbours(net.getNeuron(1));
        for (long nId : new long[] { 0, 2 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neihbourhood set.
        Assert.assertEquals(2, neighbours.size());

        // Neuron 2.
        neighbours = net.getNeighbours(net.getNeuron(2));
        for (long nId : new long[] { 1, 3 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neihbourhood set.
        Assert.assertEquals(2, neighbours.size());

        // Neuron 3.
        neighbours = net.getNeighbours(net.getNeuron(3));
        for (long nId : new long[] { 2 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neihbourhood set.
        Assert.assertEquals(1, neighbours.size());
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2-----3
     */
    @Test
    public void testCircleNetwork() {
        final FeatureInitializer[] initArray = { init };
        final Network net = new NeuronString(4, true, initArray).getNetwork();

        Collection<Neuron> neighbours;

        // Neuron 0.
        neighbours = net.getNeighbours(net.getNeuron(0));
        for (long nId : new long[] { 1, 3 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neihbourhood set.
        Assert.assertEquals(2, neighbours.size());

        // Neuron 1.
        neighbours = net.getNeighbours(net.getNeuron(1));
        for (long nId : new long[] { 0, 2 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neihbourhood set.
        Assert.assertEquals(2, neighbours.size());

        // Neuron 2.
        neighbours = net.getNeighbours(net.getNeuron(2));
        for (long nId : new long[] { 1, 3 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neihbourhood set.
        Assert.assertEquals(2, neighbours.size());

        // Neuron 3.
        neighbours = net.getNeighbours(net.getNeuron(3));
        for (long nId : new long[] { 0, 2 }) {
            Assert.assertTrue(neighbours.contains(net.getNeuron(nId)));
        }
        // Ensures that no other neurons is in the neihbourhood set.
        Assert.assertEquals(2, neighbours.size());
    }

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2-----3-----4
     */
    @Test
    public void testGetNeighboursWithExclude() {
        final FeatureInitializer[] initArray = { init };
        final Network net = new NeuronString(5, true, initArray).getNetwork();
        final Collection<Neuron> exclude = new ArrayList<Neuron>();
        exclude.add(net.getNeuron(1));
        final Collection<Neuron> neighbours = net.getNeighbours(net.getNeuron(0),
                                                                exclude);
        Assert.assertTrue(neighbours.contains(net.getNeuron(4)));
        Assert.assertEquals(1, neighbours.size());
    }

    @Test
    public void testSerialize()
        throws IOException,
               ClassNotFoundException {
        final FeatureInitializer[] initArray = { init };
        final NeuronString out = new NeuronString(4, false, initArray);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(out);

        final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(bis);
        final NeuronString in = (NeuronString) ois.readObject();

        for (Neuron nOut : out.getNetwork()) {
            final Neuron nIn = in.getNetwork().getNeuron(nOut.getIdentifier());

            // Same values.
            final double[] outF = nOut.getFeatures();
            final double[] inF = nIn.getFeatures();
            Assert.assertEquals(outF.length, inF.length);
            for (int i = 0; i < outF.length; i++) {
                Assert.assertEquals(outF[i], inF[i], 0d);
            }

            // Same neighbours.
            final Collection<Neuron> outNeighbours = out.getNetwork().getNeighbours(nOut);
            final Collection<Neuron> inNeighbours = in.getNetwork().getNeighbours(nIn);
            Assert.assertEquals(outNeighbours.size(), inNeighbours.size());
            for (Neuron oN : outNeighbours) {
                Assert.assertTrue(inNeighbours.contains(in.getNetwork().getNeuron(oN.getIdentifier())));
            }
        }
    }
}
