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

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import org.apache.commons.math4.ml.neuralnet.Neuron;
import org.junit.Test;
import org.junit.Assert;

/**
 * Tests for {@link Neuron}.
 */
public class NeuronTest {
    @Test
    public void testGetIdentifier() {
        final long id = 1234567;
        final Neuron n = new Neuron(id, new double[] { 0 });

        Assert.assertEquals(id, n.getIdentifier());
    }

    @Test
    public void testGetSize() {
        final double[] features = { -1, -1e-97, 0, 23.456, 9.01e203 } ;
        final Neuron n = new Neuron(1, features);
        Assert.assertEquals(features.length, n.getSize());
    }

    @Test
    public void testGetFeatures() {
        final double[] features = { -1, -1e-97, 0, 23.456, 9.01e203 } ;
        final Neuron n = new Neuron(1, features);

        final double[] f = n.getFeatures();
        // Accessor returns a copy.
        Assert.assertFalse(f == features);

        // Values are the same.
        Assert.assertEquals(features.length, f.length);
        for (int i = 0; i < features.length; i++) {
            Assert.assertEquals(features[i], f[i], 0d);
        }
    }

    @Test
    public void testCompareAndSetFeatures() {
        final Neuron n = new Neuron(1, new double[] { 0 });
        double[] expect = n.getFeatures();
        double[] update = new double[] { expect[0] + 1.23 };

        // Test "success".
        boolean ok = n.compareAndSetFeatures(expect, update);
        // Check that the update is reported as successful.
        Assert.assertTrue(ok);
        // Check that the new value is correct.
        Assert.assertEquals(update[0],  n.getFeatures()[0], 0d);

        // Test "failure".
        double[] update1 = new double[] { update[0] + 4.56 };
        // Must return "false" because the neuron has been
        // updated: a new update can only succeed if "expect"
        // is set to the new features.
        ok = n.compareAndSetFeatures(expect, update1);
        // Check that the update is reported as failed.
        Assert.assertFalse(ok);
        // Check that the value was not changed.
        Assert.assertEquals(update[0],  n.getFeatures()[0], 0d);
    }

    @Test
    public void testCopy() {
        final Neuron n = new Neuron(1, new double[] { 9.87 });

        // Update original.
        double[] update = new double[] { n.getFeatures()[0] + 2.34 };
        n.compareAndSetFeatures(n.getFeatures(), update);

        // Create a copy.
        final Neuron copy = n.copy();

        // Check that original and copy have the same value.
        Assert.assertTrue(n.getFeatures()[0] == copy.getFeatures()[0]);
        Assert.assertEquals(n.getNumberOfAttemptedUpdates(),
                            copy.getNumberOfAttemptedUpdates());

        // Update original.
        update = new double[] { 1.23 * n.getFeatures()[0] };
        n.compareAndSetFeatures(n.getFeatures(), update);

        // Check that original and copy differ.
        Assert.assertFalse(n.getFeatures()[0] == copy.getFeatures()[0]);
        Assert.assertNotEquals(n.getNumberOfSuccessfulUpdates(),
                               copy.getNumberOfSuccessfulUpdates());
    }

    @Test
    public void testSerialize()
        throws IOException,
               ClassNotFoundException {
        final Neuron out = new Neuron(123, new double[] { -98.76, -1, 0, 1e-23, 543.21, 1e234 });
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(out);

        final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(bis);
        final Neuron in = (Neuron) ois.readObject();

        // Same identifier.
        Assert.assertEquals(out.getIdentifier(),
                            in.getIdentifier());
        // Same values.
        final double[] outF = out.getFeatures();
        final double[] inF = in.getFeatures();
        Assert.assertEquals(outF.length, inF.length);
        for (int i = 0; i < outF.length; i++) {
            Assert.assertEquals(outF[i], inF[i], 0d);
        }
    }
}
