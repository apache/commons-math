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

package org.apache.commons.math4.neuralnet.sofm;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math4.neuralnet.DistanceMeasure;
import org.apache.commons.math4.neuralnet.MapRanking;
import org.apache.commons.math4.neuralnet.Network;
import org.apache.commons.math4.neuralnet.Neuron;
import org.apache.commons.math4.neuralnet.UpdateAction;

/**
 * Update formula for <a href="http://en.wikipedia.org/wiki/Kohonen">
 * Kohonen's Self-Organizing Map</a>.
 * <br>
 * The {@link #update(Network,double[]) update} method modifies the
 * features {@code w} of the "winning" neuron and its neighbours
 * according to the following rule:
 * <code>
 *  w<sub>new</sub> = w<sub>old</sub> + &alpha; e<sup>(-d / &sigma;)</sup> * (sample - w<sub>old</sub>)
 * </code>
 * where
 * <ul>
 *  <li>&alpha; is the current <em>learning rate</em>, </li>
 *  <li>&sigma; is the current <em>neighbourhood size</em>, and</li>
 *  <li>{@code d} is the number of links to traverse in order to reach
 *   the neuron from the winning neuron.</li>
 * </ul>
 * <br>
 * This class is thread-safe as long as the arguments passed to the
 * {@link #KohonenUpdateAction(DistanceMeasure,LearningFactorFunction,
 * NeighbourhoodSizeFunction) constructor} are instances of thread-safe
 * classes.
 * <br>
 * Each call to the {@link #update(Network,double[]) update} method
 * will increment the internal counter used to compute the current
 * values for
 * <ul>
 *  <li>the <em>learning rate</em>, and</li>
 *  <li>the <em>neighbourhood size</em>.</li>
 * </ul>
 * Consequently, the function instances that compute those values (passed
 * to the constructor of this class) must take into account whether this
 * class's instance will be shared by multiple threads, as this will impact
 * the training process.
 *
 * @since 3.3
 */
public class KohonenUpdateAction implements UpdateAction {
    /** Distance function. */
    private final DistanceMeasure distance;
    /** Learning factor update function. */
    private final LearningFactorFunction learningFactor;
    /** Neighbourhood size update function. */
    private final NeighbourhoodSizeFunction neighbourhoodSize;
    /** Number of calls to {@link #update(Network,double[])}. */
    private final AtomicLong numberOfCalls = new AtomicLong(0);

    /**
     * @param distance Distance function.
     * @param learningFactor Learning factor update function.
     * @param neighbourhoodSize Neighbourhood size update function.
     */
    public KohonenUpdateAction(DistanceMeasure distance,
                               LearningFactorFunction learningFactor,
                               NeighbourhoodSizeFunction neighbourhoodSize) {
        this.distance = distance;
        this.learningFactor = learningFactor;
        this.neighbourhoodSize = neighbourhoodSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Network net,
                       double[] features) {
        final long numCalls = numberOfCalls.incrementAndGet() - 1;
        final double currentLearning = learningFactor.value(numCalls);
        final Neuron best = findAndUpdateBestNeuron(net,
                                                    features,
                                                    currentLearning);

        final int currentNeighbourhood = neighbourhoodSize.value(numCalls);
        // The farther away the neighbour is from the winning neuron, the
        // smaller the learning rate will become.
        final Gaussian neighbourhoodDecay
            = new Gaussian(currentLearning, currentNeighbourhood);

        if (currentNeighbourhood > 0) {
            // Initial set of neurons only contains the winning neuron.
            Collection<Neuron> neighbours = new HashSet<>();
            neighbours.add(best);
            // Winning neuron must be excluded from the neighbours.
            final HashSet<Neuron> exclude = new HashSet<>();
            exclude.add(best);

            int radius = 1;
            do {
                // Retrieve immediate neighbours of the current set of neurons.
                neighbours = net.getNeighbours(neighbours, exclude);

                // Update all the neighbours.
                for (final Neuron n : neighbours) {
                    updateNeighbouringNeuron(n, features, neighbourhoodDecay.applyAsDouble(radius));
                }

                // Add the neighbours to the exclude list so that they will
                // not be updated more than once per training step.
                exclude.addAll(neighbours);
                ++radius;
            } while (radius <= currentNeighbourhood);
        }
    }

    /**
     * Retrieves the number of calls to the {@link #update(Network,double[]) update}
     * method.
     *
     * @return the current number of calls.
     */
    public long getNumberOfCalls() {
        return numberOfCalls.get();
    }

    /**
     * Tries to update a neuron.
     *
     * @param n Neuron to be updated.
     * @param features Training data.
     * @param learningRate Learning factor.
     * @return {@code true} if the update succeeded, {@code true} if a
     * concurrent update has been detected.
     */
    private boolean attemptNeuronUpdate(Neuron n,
                                        double[] features,
                                        double learningRate) {
        final double[] expect = n.getFeatures();
        final double[] update = computeFeatures(expect,
                                                features,
                                                learningRate);

        return n.compareAndSetFeatures(expect, update);
    }

    /**
     * Atomically updates the given neuron.
     *
     * @param n Neuron to be updated.
     * @param features Training data.
     * @param learningRate Learning factor.
     */
    private void updateNeighbouringNeuron(Neuron n,
                                          double[] features,
                                          double learningRate) {
        while (true) {
            if (attemptNeuronUpdate(n, features, learningRate)) {
                break;
            }
        }
    }

    /**
     * Searches for the neuron whose features are closest to the given
     * sample, and atomically updates its features.
     *
     * @param net Network.
     * @param features Sample data.
     * @param learningRate Current learning factor.
     * @return the winning neuron.
     */
    private Neuron findAndUpdateBestNeuron(Network net,
                                           double[] features,
                                           double learningRate) {
        final MapRanking rank = new MapRanking(net, distance);

        while (true) {
            final Neuron best = rank.rank(features, 1).get(0);

            if (attemptNeuronUpdate(best, features, learningRate)) {
                return best;
            }

            // If another thread modified the state of the winning neuron,
            // it may not be the best match anymore for the given training
            // sample: Hence, the winner search is performed again.
        }
    }

    /**
     * Computes the new value of the features set.
     *
     * @param current Current values of the features.
     * @param sample Training data.
     * @param learningRate Learning factor.
     * @return the new values for the features.
     */
    private double[] computeFeatures(double[] current,
                                     double[] sample,
                                     double learningRate) {
        final int len = current.length;
        final double[] r = new double[len];
        for (int i = 0; i < len; i++) {
            final double c = current[i];
            final double s = sample[i];
            r[i] = c + learningRate * (s - c);
        }
        return r;
    }

    /**
     * Gaussian function with zero mean.
     */
    private static class Gaussian implements DoubleUnaryOperator {
        /** Inverse of twice the square of the standard deviation. */
        private final double i2s2;
        /** Normalization factor. */
        private final double norm;

        /**
         * @param norm Normalization factor.
         * @param sigma Standard deviation.
         */
        Gaussian(double norm,
                 double sigma) {
            this.norm = norm;
            i2s2 = 1d / (2 * sigma * sigma);
        }

        @Override
        public double applyAsDouble(double x) {
            return norm * Math.exp(-x * x * i2s2);
        }
    }
}
