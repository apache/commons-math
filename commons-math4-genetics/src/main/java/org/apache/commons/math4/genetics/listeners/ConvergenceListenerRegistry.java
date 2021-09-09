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

package org.apache.commons.math4.genetics.listeners;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.genetics.model.Population;

/**
 * This class is the default implementation of ConvergenceListenerRegistry. It
 * will be responsible for registering the interested listeners and notifying
 * all when required.
 *
 */
public final class ConvergenceListenerRegistry {

    /**
     * The instance of the singleton class.
     */
    private static final ConvergenceListenerRegistry INSTANCE = new ConvergenceListenerRegistry();

    /**
     * List of registered listeners.
     */
    private List<ConvergenceListener> listeners = new ArrayList<>();

    private ConvergenceListenerRegistry() {
    }

    /**
     * Registers the interested ConvergenceListener passed as an argument.
     * @param convergenceListener
     */
    public void addConvergenceListener(ConvergenceListener convergenceListener) {
        this.listeners.add(convergenceListener);
    }

    /**
     * Notifies all registered ConvergenceListeners about the population statistics.
     * @param population
     */
    public void notifyAll(Population population) {
        for (ConvergenceListener convergenceListener : listeners) {
            convergenceListener.notify(population);
        }
    }

    /**
     * Add instance of convergence listener.
     * @param convergenceListeners
     */
    public void addConvergenceListeners(List<ConvergenceListener> convergenceListeners) {
        if (convergenceListeners != null) {
            for (ConvergenceListener convergenceListener : convergenceListeners) {
                this.listeners.add(convergenceListener);
            }
        }
    }

    /**
     * Returns instance of this class.
     * @return instance of this class.
     */
    public static ConvergenceListenerRegistry getInstance() {
        return INSTANCE;
    }

}
