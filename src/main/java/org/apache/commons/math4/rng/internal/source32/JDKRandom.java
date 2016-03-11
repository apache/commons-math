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
package org.apache.commons.math4.rng.internal.source32;

import java.util.Random;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/**
 * A provider that uses the {@link Random#nextInt()} method of the JDK's
 * {@code Random} class as the source of randomness.
 *
 * <p>
 * <b>Caveat:</b> All the other calls will be redirected to the methods
 * implemented within this library.
 * </p>
 *
 * <p>
 * The state of this source of randomness is saved and restored through
 * the serialization of the {@link Random} instance.
 * </p>
 *
 * @since 4.0
 */
public class JDKRandom extends IntProvider {
    /** Delegate.  Cannot be "final" (to allow serialization). */
    private Random delegate;

    /**
     * Creates an instance with the given seed.
     *
     * @param seed Initial seed.
     */
    public JDKRandom(Long seed) {
        delegate = new Random(seed);
    }

    /**
     * {@inheritDoc}
     *
     * @see Random#nextInt()
     */
    @Override
    public int next() {
        return delegate.nextInt();
    }

    /** {@inheritDoc} */
    @Override
    protected byte[] getStateInternal() {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(bos);

            // Serialize the "delegate".
            oos.writeObject(delegate);

            return bos.toByteArray();
        } catch (IOException e) {
            // Workaround checked exception.
            throw new RuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void setStateInternal(byte[] s) {
        try {
            final ByteArrayInputStream bis = new ByteArrayInputStream(s);
            final ObjectInputStream ois = new ObjectInputStream(bis);

            delegate = (Random) ois.readObject();
        } catch (ClassNotFoundException|IOException e) {
            // Workaround checked exception.
            throw new RuntimeException(e);
        }
    }
}
