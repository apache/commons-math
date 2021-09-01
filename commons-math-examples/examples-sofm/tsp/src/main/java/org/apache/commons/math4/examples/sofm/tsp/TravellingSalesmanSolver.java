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

package org.apache.commons.math4.examples.sofm.tsp;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.DoubleUnaryOperator;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.CollectionSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;

import org.apache.commons.math4.neuralnet.DistanceMeasure;
import org.apache.commons.math4.neuralnet.EuclideanDistance;
import org.apache.commons.math4.neuralnet.FeatureInitializer;
import org.apache.commons.math4.neuralnet.FeatureInitializerFactory;
import org.apache.commons.math4.neuralnet.Network;
import org.apache.commons.math4.neuralnet.Neuron;
import org.apache.commons.math4.neuralnet.oned.NeuronString;
import org.apache.commons.math4.neuralnet.sofm.KohonenUpdateAction;
import org.apache.commons.math4.neuralnet.sofm.KohonenTrainingTask;
import org.apache.commons.math4.neuralnet.sofm.NeighbourhoodSizeFunctionFactory;
import org.apache.commons.math4.neuralnet.sofm.NeighbourhoodSizeFunction;
import org.apache.commons.math4.neuralnet.sofm.LearningFactorFunctionFactory;
import org.apache.commons.math4.neuralnet.sofm.LearningFactorFunction;

/**
 * Handles the <a href="https://en.wikipedia.org/wiki/Travelling_salesman_problem">
 * "Travelling Salesman's Problem"</a> (i.e. trying to find the sequence of
 * cities that minimizes the travel distance) using a 1D SOFM.
 */
public final class TravellingSalesmanSolver {
    /** The ID for the first neuron. */
    private static final long FIRST_NEURON_ID = 0;
    /** SOFM. */
    private final Network net;
    /** Distance function. */
    private final DistanceMeasure distance = new EuclideanDistance();
    /** Total number of neurons. */
    private final int numberOfNeurons;

    /**
     * @param numNeurons Number of neurons.
     * @param init Neuron intializers.
     */
    private TravellingSalesmanSolver(int numNeurons,
                                     FeatureInitializer[] init) {
        // Total number of neurons.
        numberOfNeurons = numNeurons;

        // Create a network with circle topology.
        net = new NeuronString(numberOfNeurons, true, init).getNetwork();
    }

    /**
     * @param cities List of cities to be visited.
     * @param neuronsPerCity Average number of neurons per city.
     * @param numUpdates Number of updates for training the network.
     * @param numTasks Number of concurrent tasks.
     * @param random RNG for presenting samples to the trainer.
     * @return the solution (list of cities in travel order).
     */
    public static City[] solve(City[] cities,
                               double neuronsPerCity,
                               long numUpdates,
                               int numTasks,
                               UniformRandomProvider random) {
        if (cities.length <= 2) {
            return cities;
        }

        // Make sure that each city will appear only once in the list.
        final Set<City> uniqueCities = City.unique(cities);

        final int numNeurons = (int) (neuronsPerCity * uniqueCities.size());
        if (numNeurons < uniqueCities.size()) {
            throw new IllegalArgumentException("Too few neurons");
        }

        // Set up network.
        final FeatureInitializer[] init = makeInitializers(numNeurons,
                                                           uniqueCities,
                                                           random);
        final TravellingSalesmanSolver solver = new TravellingSalesmanSolver(numNeurons,
                                                                             init);

        // Parallel execution.
        final ExecutorService service = Executors.newCachedThreadPool();
        final Runnable[] tasks = solver.createTasks(uniqueCities,
                                                    random,
                                                    numTasks,
                                                    numUpdates / numTasks);
        final List<Future<?>> execOutput = new ArrayList<>();
        // Run tasks.
        for (final Runnable r : tasks) {
            execOutput.add(service.submit(r));
        }
        // Wait for completion (ignoring return value).
        try {
            for (final Future<?> f : execOutput) {
                f.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException(e);
        }
        // Terminate all threads.
        service.shutdown();

        return solver.getCityList(uniqueCities).toArray(new City[0]);
    }

    /**
     * Creates training tasks.
     *
     * @param cities List of cities to be visited.
     * @param random RNG for presenting samples to the trainer.
     * @param numTasks Number of tasks to create.
     * @param numSamplesPerTask Number of training samples per task.
     * @return the created tasks.
     */
    private Runnable[] createTasks(Set<City> cities,
                                   UniformRandomProvider random,
                                   int numTasks,
                                   long numSamplesPerTask) {
        final Runnable[] tasks = new Runnable[numTasks];
        final LearningFactorFunction learning
            = LearningFactorFunctionFactory.exponentialDecay(0.9,
                                                             0.05,
                                                             numSamplesPerTask / 2);
        final NeighbourhoodSizeFunction neighbourhood
            = NeighbourhoodSizeFunctionFactory.exponentialDecay(numberOfNeurons,
                                                                1,
                                                                numSamplesPerTask / 2);

        for (int i = 0; i < numTasks; i++) {
            final KohonenUpdateAction action = new KohonenUpdateAction(distance,
                                                                       learning,
                                                                       neighbourhood);
            tasks[i] = new KohonenTrainingTask(net,
                                               createIterator(numSamplesPerTask,
                                                              cities,
                                                              random),
                                               action);
        }

        return tasks;
    }

    /**
     * Creates an iterator that will present a series of city's coordinates
     * in random order.
     *
     * @param numSamples Number of samples.
     * @param uniqueCities Cities.
     * @param random RNG.
     * @return the iterator.
     */
    private static Iterator<double[]> createIterator(final long numSamples,
                                                     final Set<City> uniqueCities,
                                                     final UniformRandomProvider random) {
        final CollectionSampler<City> sampler = new CollectionSampler<>(random, uniqueCities);

        return new Iterator<double[]>() {
            /** Number of samples. */
            private long n;
            /** {@inheritDoc} */
            @Override
            public boolean hasNext() {
                return n < numSamples;
            }
            /** {@inheritDoc} */
            @Override
            public double[] next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                ++n;
                return sampler.sample().getCoordinates();
            }
            /** {@inheritDoc} */
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * @return the list of linked neurons (i.e. the one-dimensional SOFM).
     */
    private List<Neuron> getNeuronList() {
        // Sequence of coordinates.
        final List<Neuron> list = new ArrayList<>();

        // First neuron.
        Neuron current = net.getNeuron(FIRST_NEURON_ID);
        while (true) {
            list.add(current);
            final Collection<Neuron> neighbours
                = net.getNeighbours(current, list);

            final Iterator<Neuron> iter = neighbours.iterator();
            if (!iter.hasNext()) {
                // All neurons have been visited.
                break;
            }

            current = iter.next();
        }

        return list;
    }

    /**
     * @return the list of features (coordinates) of linked neurons.
     */
    public List<double[]> getCoordinatesList() {
        // Sequence of coordinates.
        final List<double[]> coordinatesList = new ArrayList<>();

        for (final Neuron n : getNeuronList()) {
            coordinatesList.add(n.getFeatures());
        }

        return coordinatesList;
    }

    /**
     * Returns the travel proposed by the solver.
     *
     * @param cities Cities
     * @return the list of cities in travel order.
     */
    private List<City> getCityList(Set<City> cities) {
        final List<double[]> coord = getCoordinatesList();
        final List<City> cityList = new ArrayList<>();
        City previous = null;
        final int max = coord.size();
        for (int i = 0; i < max; i++) {
            final double[] c = coord.get(i);
            final City next = City.closest(c[0], c[1], cities);
            if (!next.equals(previous)) {
                cityList.add(next);
                previous = next;
            }
        }
        return cityList;
    }

    /**
     * Creates the features' initializers: an approximate circle around the
     * barycentre of the cities.
     *
     * @param numNeurons Number of neurons.
     * @param uniqueCities Cities.
     * @param random RNG.
     * @return an array containing the two initializers.
     */
    private static FeatureInitializer[] makeInitializers(final int numNeurons,
                                                         final Set<City> uniqueCities,
                                                         final UniformRandomProvider random) {
        // Barycentre.
        final double[] centre = City.barycentre(uniqueCities);
        // Largest distance from centre.
        final double radius = 0.5 * City.largestDistance(centre[0], centre[1], uniqueCities);

        final double omega = 2 * Math.PI / numNeurons;
        final DoubleUnaryOperator h1 = new HarmonicOscillator(radius, omega, 0, centre[0]);
        final DoubleUnaryOperator h2 = new HarmonicOscillator(radius, omega, 0.5 * Math.PI, centre[1]);

        final double r = 0.05 * radius;
        final ContinuousUniformSampler u = new ContinuousUniformSampler(random, -r, r);

        return new FeatureInitializer[] {
            FeatureInitializerFactory.randomize(u, FeatureInitializerFactory.function(h1, 0, 1)),
            FeatureInitializerFactory.randomize(u, FeatureInitializerFactory.function(h2, 0, 1))
        };
    }
}

/**
 * Function.
 */
class HarmonicOscillator implements DoubleUnaryOperator {
    /** Amplitude. */
    private final double amplitude;
    /** Angular speed. */
    private final double omega;
    /** Phase. */
    private final double phase;
    /** Offset. */
    private final double offset;

    /**
     * @param amplitude Amplitude.
     * @param omega Angular speed.
     * @param phase Phase.
     * @param offset Offset (ordinate).
     */
    HarmonicOscillator(double amplitude,
                       double omega,
                       double phase,
                       double offset) {
        this.amplitude = amplitude;
        this.omega = omega;
        this.phase = phase;
        this.offset = offset;
    }

    @Override
    public double applyAsDouble(double x) {
        return offset + amplitude * Math.cos(omega * x + phase);
    }
}
