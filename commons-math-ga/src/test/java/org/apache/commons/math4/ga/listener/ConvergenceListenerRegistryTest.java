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
package org.apache.commons.math4.ga.listener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConvergenceListenerRegistryTest {

    @Test
    public void testRegister() {
        try {
            reset();
            ConvergenceListenerRegistry<String> registry = ConvergenceListenerRegistry.<String>getInstance();

            List<ConvergenceListener<String>> listeners = new ArrayList<>();
            ConvergenceListener<String> convergenceListener = new ConvergenceListener<String>() {

                @Override
                public void notify(int generation, Population<String> population) {
                    // No op
                }
            };
            listeners.add(convergenceListener);
            ConvergenceListener<String> convergenceListener1 = new ConvergenceListener<String>() {

                @Override
                public void notify(int generation, Population<String> population) {
                    // No op
                }
            };
            listeners.add(convergenceListener1);
            registry.addConvergenceListeners(listeners);
            Field listenersField = registry.getClass().getDeclaredField("listeners");
            boolean accessible = listenersField.isAccessible();
            if (!accessible) {
                listenersField.setAccessible(true);
            }
            @SuppressWarnings("unchecked")
            List<ConvergenceListener<String>> listeners1 = (List<ConvergenceListener<String>>) listenersField
                    .get(registry);
            Assertions.assertSame(listeners1.get(0), convergenceListener);
            listenersField.setAccessible(accessible);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // No op
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

    @Test
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
            Assertions.assertThrows(GeneticException.class, () -> {
                registry.notifyAll(0, new ListPopulation<>(10));
            });
            Assertions.assertTrue(true);
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            // No op
        }
    }

}
