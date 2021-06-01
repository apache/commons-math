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
package org.apache.commons.math4.legacy.linear;

import org.apache.commons.math4.legacy.core.IntegerSequence;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This abstract class provides a general framework for managing iterative
 * algorithms. The maximum number of iterations can be set, and methods are
 * provided to monitor the current iteration count. A lightweight event
 * framework is also provided.
 *
 */
public class IterationManager {
    /** The collection of all listeners attached to this iterative algorithm. */
    private final Collection<IterationListener> listeners;
    /** Maximum number of iterations. */
    private final int maxIterations;
    /** Callback. */
    private final IntegerSequence.Incrementor.MaxCountExceededCallback callback;
    /** Keeps a count of the number of iterations. */
    private IntegerSequence.Incrementor iterations;

    /**
     * Creates a new instance of this class.
     *
     * @param maxIterations Maximum number of iterations.
     * {@link org.apache.commons.math4.legacy.exception.MaxCountExceededException}
     * will be raised at counter exhaustion.
     */
    public IterationManager(final int maxIterations) {
        this(maxIterations, null);
    }

    /**
     * Creates a new instance of this class.
     *
     * @param maxIterations the maximum number of iterations
     * @param callback the function to be called when the maximum number of
     * iterations has been reached.
     * If {@code null}, {@link org.apache.commons.math4.legacy.exception.MaxCountExceededException}
     * will be raised at counter exhaustion.
     *
     * @since 3.1
     */
    public IterationManager(final int maxIterations,
                            final IntegerSequence.Incrementor.MaxCountExceededCallback callback) {
        this.maxIterations = maxIterations;
        this.callback = callback;
        this.listeners = new CopyOnWriteArrayList<>();
        resetCounter();
    }

    /**
     * Attaches a listener to this manager.
     *
     * @param listener A {@code IterationListener} object.
     */
    public void addIterationListener(final IterationListener listener) {
        listeners.add(listener);
    }

    /**
     * Informs all registered listeners that the initial phase (prior to the
     * main iteration loop) has been completed.
     *
     * @param e The {@link IterationEvent} object.
     */
    public void fireInitializationEvent(final IterationEvent e) {
        for (IterationListener l : listeners) {
            l.initializationPerformed(e);
        }
    }

    /**
     * Informs all registered listeners that a new iteration (in the main
     * iteration loop) has been performed.
     *
     * @param e The {@link IterationEvent} object.
     */
    public void fireIterationPerformedEvent(final IterationEvent e) {
        for (IterationListener l : listeners) {
            l.iterationPerformed(e);
        }
    }

    /**
     * Informs all registered listeners that a new iteration (in the main
     * iteration loop) has been started.
     *
     * @param e The {@link IterationEvent} object.
     */
    public void fireIterationStartedEvent(final IterationEvent e) {
        for (IterationListener l : listeners) {
            l.iterationStarted(e);
        }
    }

    /**
     * Informs all registered listeners that the final phase (post-iterations)
     * has been completed.
     *
     * @param e The {@link IterationEvent} object.
     */
    public void fireTerminationEvent(final IterationEvent e) {
        for (IterationListener l : listeners) {
            l.terminationPerformed(e);
        }
    }

    /**
     * Returns the number of iterations of this solver, 0 if no iterations has
     * been performed yet.
     *
     * @return the number of iterations.
     */
    public int getIterations() {
        return iterations.getCount();
    }

    /**
     * Returns the maximum number of iterations.
     *
     * @return the maximum number of iterations.
     */
    public int getMaxIterations() {
        return iterations.getMaximalCount();
    }

    /**
     * Increments the iteration count by one, and throws an exception if the
     * maximum number of iterations is reached. This method should be called at
     * the beginning of a new iteration.
     *
     * @throws org.apache.commons.math4.legacy.exception.MaxCountExceededException
     * if the maximum number of iterations is reached.
     */
    public void incrementIterationCount() {
        iterations.increment();
    }

    /**
     * Removes the specified iteration listener from the list of listeners
     * currently attached to {@code this} object. Attempting to remove a
     * listener which was <em>not</em> previously registered does not cause any
     * error.
     *
     * @param listener The {@link IterationListener} to be removed.
     */
    public void removeIterationListener(final IterationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Sets the iteration count to 0. This method must be called during the
     * initial phase.
     */
    public void resetIterationCount() {
        resetCounter();
    }

    /** Reset counter. */
    private void resetCounter() {
        iterations = IntegerSequence.Incrementor.create()
            .withMaximalCount(maxIterations);
        if (callback != null) {
            iterations = iterations.withCallback(callback);
        }
    }
}
