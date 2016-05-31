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
package org.apache.commons.math4.exception.util;

import java.util.Locale;
import java.util.Arrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import org.apache.commons.math4.exception.util.ExceptionContext;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link ExceptionContext}.
 *
 */
public class ExceptionContextTest {
    @Test
    public void testMessageChain() {
        final ExceptionContext c = new ExceptionContext(new Exception("oops"));
        final String sep = " | "; // Non-default separator.
        final String m1 = "column index (0)";
        c.addMessage(LocalizedFormats.COLUMN_INDEX, 0);
        final String m2 = "got 1x2 but expected 3x4";
        c.addMessage(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, 1, 2, 3, 4);
        final String m3 = "It didn't work out";
        c.addMessage(LocalizedFormats.SIMPLE_MESSAGE, m3);

        Assert.assertEquals(c.getMessage(Locale.US, sep),
                            m1 + sep + m2 + sep + m3);
    }

    @Test
    public void testNoArgAddMessage() {
        final ExceptionContext c = new ExceptionContext(new Exception("hello"));
        c.addMessage(LocalizedFormats.SIMPLE_MESSAGE);
        Assert.assertEquals(c.getMessage(), "{0}");
    }

    @Test
    public void testContext() {
        final ExceptionContext c = new ExceptionContext(new Exception("bye"));

        final String[] keys = {"Key 1", "Key 2"};
        final Object[] values = {"Value 1", Integer.valueOf(2)};

        for (int i = 0; i < keys.length; i++) {
            c.setValue(keys[i], values[i]);
        }

        // Check that all keys are present.
        Assert.assertTrue(c.getKeys().containsAll(Arrays.asList(keys)));

        // Check that all values are correctly stored.
        for (int i = 0; i < keys.length; i++) {
            Assert.assertEquals(values[i], c.getValue(keys[i]));
        }

        // Check behaviour on missing key.
        Assert.assertNull(c.getValue("xyz"));
    }

    @Test
    public void testSerialize()
        throws IOException,
               ClassNotFoundException {
        final ExceptionContext cOut = new ExceptionContext(new Exception("Apache"));
        cOut.addMessage(LocalizedFormats.COLUMN_INDEX, 0);
        cOut.setValue("Key 1", Integer.valueOf(0));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(cOut);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        ExceptionContext cIn = (ExceptionContext) ois.readObject();

        Assert.assertTrue(cOut.getMessage().equals(cIn.getMessage()));
        for (String key : cIn.getKeys()) {
            Assert.assertTrue(cOut.getValue(key).equals(cIn.getValue(key)));
        }
    }

    @Test
    public void testSerializeUnserializable() throws Exception {
        final ExceptionContext cOut = new ExceptionContext(new Exception("Apache Commons Math"));
        cOut.addMessage(LocalizedFormats.SIMPLE_MESSAGE, "OK");
        cOut.addMessage(LocalizedFormats.SIMPLE_MESSAGE, new Unserializable());
        String key = "Key 1";
        cOut.setValue(key, new Unserializable());

        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(cOut);

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            ExceptionContext cIn = (ExceptionContext) ois.readObject();

            String nsObjStr = (String) cIn.getValue(key);
            Assert.assertTrue(nsObjStr.matches(".*could not be serialized.*"));
        }
    }

    /**
     * Class used by {@link #testSerializeUnserializable()}.
     */
    private static class Unserializable {
        Unserializable() {}
    }
}
