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
package org.apache.commons.math4.genetics.listener;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.population.ListPopulation;
import org.apache.commons.math4.genetics.population.Population;
import org.junit.Assert;
import org.junit.Test;

public class ConvergenceListenerRegistryTest {

    @Test
    public void testRegister() {
        try {
            reset();
            ConvergenceListenerRegistry<String> registry = ConvergenceListenerRegistry.<String>getInstance();
            ConvergenceListener<String> convergenceListener = new ConvergenceListener<String>() {

                @Override
                public void notify(int generation, Population<String> population) {
                    // No op
                }
            };
            registry.addConvergenceListener(convergenceListener);
            Field listenersField = registry.getClass().getDeclaredField("listeners");
            boolean accessible = listenersField.isAccessible();
            if (!accessible) {
                listenersField.setAccessible(true);
            }
            List<ConvergenceListener<String>> listeners = (List<ConvergenceListener<String>>) listenersField
                    .get(registry);
            Assert.assertTrue(listeners.get(0) == convergenceListener);
            listenersField.setAccessible(accessible);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new GeneticException(e);
        } catch (IllegalArgumentException e) {
            throw new GeneticException(e);
        } catch (IllegalAccessException e) {
            throw new GeneticException(e);
        }
    }

    private void reset()
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        ConvergenceListenerRegistry<String> registry = ConvergenceListenerRegistry.<String>getInstance();
        Field listenersField = registry.getClass().getDeclaredField("listeners");
        boolean accessible = listenersField.isAccessible();
        if (!accessible) {
            listenersField.setAccessible(true);
        }
        @SuppressWarnings("unchecked")
        List<ConvergenceListener<String>> listeners = (List<ConvergenceListener<String>>) listenersField
                .get(ConvergenceListenerRegistry.getInstance());
        listeners.clear();
        listenersField.setAccessible(accessible);
    }

    @Test(expected = GeneticException.class)
    public void testNotifyAll() {
        try {
            reset();
            ConvergenceListenerRegistry<String> registry = ConvergenceListenerRegistry.<String>getInstance();
            ConvergenceListener<String> convergenceListener = new ConvergenceListener<String>() {

                @Override
                public void notify(int generation, Population<String> population) {
                    throw new GeneticException("Test Notify");
                }
            };
            registry.addConvergenceListener(convergenceListener);
            registry.notifyAll(0, new ListPopulation<>(10));
            Assert.assertTrue(false);
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            throw new GeneticException(e);
        }
    }

}
