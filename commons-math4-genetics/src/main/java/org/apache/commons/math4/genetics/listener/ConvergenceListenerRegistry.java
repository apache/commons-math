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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.genetics.population.Population;

/**
 * This class is the default implementation of ConvergenceListenerRegistry. It
 * will be responsible for registering the interested listeners and notifying
 * all when required.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public final class ConvergenceListenerRegistry<P> {

    /**
     * The instance of the singleton class.
     */
    @SuppressWarnings("rawtypes")
    private static final ConvergenceListenerRegistry INSTANCE = new ConvergenceListenerRegistry<>();

    /**
     * List of registered listeners.
     */
    private final List<ConvergenceListener<P>> listeners = new ArrayList<>();

    /**
     * constructor.
     */
    private ConvergenceListenerRegistry() {
    }

    /**
     * Registers the interested ConvergenceListener passed as an argument.
     * @param convergenceListener The {@link ConvergenceListener}
     */
    public void addConvergenceListener(ConvergenceListener<P> convergenceListener) {
        this.listeners.add(convergenceListener);
    }

    /**
     * Notifies all registered ConvergenceListeners about the population statistics.
     * @param generation current generation
     * @param population population of chromosomes
     */
    public void notifyAll(int generation, Population<P> population) {
        for (ConvergenceListener<P> convergenceListener : listeners) {
            convergenceListener.notify(generation, population);
        }
    }

    /**
     * Add instance of convergence listener.
     * @param convergenceListeners list of {@link ConvergenceListener}
     */
    public void addConvergenceListeners(List<ConvergenceListener<P>> convergenceListeners) {
        if (convergenceListeners != null) {
            for (ConvergenceListener<P> convergenceListener : convergenceListeners) {
                this.listeners.add(convergenceListener);
            }
        }
    }

    /**
     * Returns instance of this class.
     * @param <P> The phenotype of chromosome
     * @return instance
     */
    @SuppressWarnings("unchecked")
    public static <P> ConvergenceListenerRegistry<P> getInstance() {
        return INSTANCE;
    }

}
