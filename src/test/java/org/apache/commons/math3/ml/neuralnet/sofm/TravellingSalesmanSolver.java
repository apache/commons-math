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

package org.apache.commons.math3.ml.neuralnet.sofm;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Constant;
import org.apache.commons.math3.analysis.function.HarmonicOscillator;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.ml.neuralnet.FeatureInitializer;
import org.apache.commons.math3.ml.neuralnet.FeatureInitializerFactory;
import org.apache.commons.math3.ml.neuralnet.Network;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.oned.NeuronString;
import org.apache.commons.math3.ml.neuralnet.sofm.KohonenTrainingTask;
import org.apache.commons.math3.ml.neuralnet.sofm.KohonenUpdateAction;
import org.apache.commons.math3.ml.neuralnet.sofm.LearningFactorFunction;
import org.apache.commons.math3.ml.neuralnet.sofm.LearningFactorFunctionFactory;
import org.apache.commons.math3.ml.neuralnet.sofm.NeighbourhoodSizeFunction;
import org.apache.commons.math3.ml.neuralnet.sofm.NeighbourhoodSizeFunctionFactory;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well44497b;
import org.apache.commons.math3.util.FastMath;

/**
 * Solves the "Travelling Salesman's Problem" (i.e. trying to find the
 * sequence of cities that minimizes the travel distance) using a 1D
 * SOFM.
 */
public class TravellingSalesmanSolver {
    private static final long FIRST_NEURON_ID = 0;
    /** RNG. */
    private final RandomGenerator random;
    /** Set of cities. */
    private final Set<City> cities = new HashSet<City>();
    /** SOFM. */
    private final Network net;
    /** Distance function. */
    private final DistanceMeasure distance = new EuclideanDistance();
    /** Total number of neurons. */
    private final int numberOfNeurons;

    /**
     * @param cityList List of cities to visit in a single travel.
     * @param numNeuronsPerCity Number of neurons per city.
     */
    public TravellingSalesmanSolver(City[] cityList,
                                    double numNeuronsPerCity) {
        this(cityList, numNeuronsPerCity, new Well44497b().nextLong());
    }

    /**
     * @param cityList List of cities to visit in a single travel.
     * @param numNeuronsPerCity Number of neurons per city.
     * @param seed Seed for the RNG that is used to present the samples
     * to the trainer.
     */
    public TravellingSalesmanSolver(City[] cityList,
                                    double numNeuronsPerCity,
                                    long seed) {
        random = new Well44497b(seed);

        // Make sure that each city will appear only once in the list.
        for (City city : cityList) {
            cities.add(city);
        }

        // Total number of neurons.
        numberOfNeurons = (int) numNeuronsPerCity * cities.size();

        // Create a network with circle topology.
        net = new NeuronString(numberOfNeurons, true, makeInitializers()).getNetwork();
    }

    /**
     * Creates training tasks.
     *
     * @param numTasks Number of tasks to create.
     * @param numSamplesPerTask Number of training samples per task.
     * @return the created tasks.
     */
    public Runnable[] createParallelTasks(int numTasks,
                                          long numSamplesPerTask) {
        final Runnable[] tasks = new Runnable[numTasks];
        final LearningFactorFunction learning
            = LearningFactorFunctionFactory.exponentialDecay(2e-1,
                                                             5e-2,
                                                             numSamplesPerTask / 2);
        final NeighbourhoodSizeFunction neighbourhood
            = NeighbourhoodSizeFunctionFactory.exponentialDecay(0.5 * numberOfNeurons,
                                                                0.1 * numberOfNeurons,
                                                                numSamplesPerTask / 2);

        for (int i = 0; i < numTasks; i++) {
            final KohonenUpdateAction action = new KohonenUpdateAction(distance,
                                                                       learning,
                                                                       neighbourhood);
            tasks[i] = new KohonenTrainingTask(net,
                                               createRandomIterator(numSamplesPerTask),
                                               action);
        }

        return tasks;
    }

    /**
     * Creates a training task.
     *
     * @param numSamples Number of training samples.
     * @return the created task.
     */
    public Runnable createSequentialTask(long numSamples) {
        return createParallelTasks(1, numSamples)[0];
    }

    /**
     * Measures the network's concurrent update performance.
     *
     * @return the ratio between the number of succesful network updates
     * and the number of update attempts.
     */
    public double getUpdateRatio() {
        return computeUpdateRatio(net);
    }

    /**
     * Measures the network's concurrent update performance.
     *
     * @param net Network to be trained with the SOFM algorithm.
     * @return the ratio between the number of successful network updates
     * and the number of update attempts.
     */
    private static double computeUpdateRatio(Network net) {
        long numAttempts = 0;
        long numSuccesses = 0;

        for (Neuron n : net) {
            numAttempts += n.getNumberOfAttemptedUpdates();
            numSuccesses += n.getNumberOfSuccessfulUpdates();
        }

        return (double) numSuccesses / (double) numAttempts;
    }

    /**
     * Creates an iterator that will present a series of city's coordinates in
     * a random order.
     *
     * @param numSamples Number of samples.
     * @return the iterator.
     */
    private Iterator<double[]> createRandomIterator(final long numSamples) {
        final List<City> cityList = new ArrayList<City>();
        cityList.addAll(cities);

        return new Iterator<double[]>() {
            /** Number of samples. */
            private long n = 0;
            /** {@inheritDoc} */
            public boolean hasNext() {
                return n < numSamples;
            }
            /** {@inheritDoc} */
            public double[] next() {
                ++n;
                return cityList.get(random.nextInt(cityList.size())).getCoordinates();
            }
            /** {@inheritDoc} */
            public void remove() {
                throw new MathUnsupportedOperationException();
            }
        };
    }

    /**
     * @return the list of linked neurons (i.e. the one-dimensional
     * SOFM).
     */
    private List<Neuron> getNeuronList() {
        // Sequence of coordinates.
        final List<Neuron> list = new ArrayList<Neuron>();

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
        final List<double[]> coordinatesList = new ArrayList<double[]>();

        for (Neuron n : getNeuronList()) {
            coordinatesList.add(n.getFeatures());
        }

        return coordinatesList;
    }

    /**
     * Returns the travel proposed by the solver.
     * Note: cities can be missing or duplicated.
     *
     * @return the list of cities in travel order.
     */
    public City[] getCityList() {
        final List<double[]> coord = getCoordinatesList();
        final City[] cityList = new City[coord.size()];
        for (int i = 0; i < cityList.length; i++) {
            final double[] c = coord.get(i);
            cityList[i] = getClosestCity(c[0], c[1]);
        }
        return cityList;
    }

    /**
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @return the city whose coordinates are closest to {@code (x, y)}.
     */
    public City getClosestCity(double x,
                               double y) {
        City closest = null;
        double min = Double.POSITIVE_INFINITY;
        for (City c : cities) {
            final double d = c.distance(x, y);
            if (d < min) {
                min = d;
                closest = c;
            }
        }
        return closest;
    }

    /**
     * Computes the barycentre of all city locations.
     *
     * @param cities City list.
     * @return the barycentre.
     */
    private static double[] barycentre(Set<City> cities) {
        double xB = 0;
        double yB = 0;

        int count = 0;
        for (City c : cities) {
            final double[] coord = c.getCoordinates();
            xB += coord[0];
            yB += coord[1];

            ++count;
        }

        return new double[] { xB / count, yB / count };
    }

    /**
     * Computes the largest distance between the point at coordinates
     * {@code (x, y)} and any of the cities.
     *
     * @param x x-coodinate.
     * @param y y-coodinate.
     * @param cities City list.
     * @return the largest distance.
     */
    private static double largestDistance(double x,
                                          double y,
                                          Set<City> cities) {
        double maxDist = 0;
        for (City c : cities) {
            final double dist = c.distance(x, y);
            if (dist > maxDist) {
                maxDist = dist;
            }
        }

        return maxDist;
    }

    /**
     * Creates the features' initializers: an approximate circle around the
     * barycentre of the cities.
     *
     * @return an array containing the two initializers.
     */
    private FeatureInitializer[] makeInitializers() {
        // Barycentre.
        final double[] centre = barycentre(cities);
        // Largest distance from centre.
        final double radius = 0.5 * largestDistance(centre[0], centre[1], cities);

        final double omega = 2 * Math.PI / numberOfNeurons;
        final UnivariateFunction h1 = new HarmonicOscillator(radius, omega, 0);
        final UnivariateFunction h2 = new HarmonicOscillator(radius, omega, 0.5 * Math.PI);

        final UnivariateFunction f1 = FunctionUtils.add(h1, new Constant(centre[0]));
        final UnivariateFunction f2 = FunctionUtils.add(h2, new Constant(centre[1]));

        final RealDistribution u
            = new UniformRealDistribution(random, -0.05 * radius, 0.05 * radius);

        return new FeatureInitializer[] {
            FeatureInitializerFactory.randomize(u, FeatureInitializerFactory.function(f1, 0, 1)),
            FeatureInitializerFactory.randomize(u, FeatureInitializerFactory.function(f2, 0, 1))
        };
    }
}

/**
 * A city, represented by a name and two-dimensional coordinates.
 */
class City {
    /** Identifier. */
    final String name;
    /** x-coordinate. */
    final double x;
    /** y-coordinate. */
    final double y;

    /**
     * @param name Name.
     * @param x Cartesian x-coordinate.
     * @param y Cartesian y-coordinate.
     */
    public City(String name,
                double x,
                double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    /**
     * @retun the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the (x, y) coordinates.
     */
    public double[] getCoordinates() {
        return new double[] { x, y };
    }

    /**
     * Computes the distance between this city and
     * the given point.
     *
     * @param x x-coodinate.
     * @param y y-coodinate.
     * @return the distance between {@code (x, y)} and this
     * city.
     */
    public double distance(double x,
                           double y) {
        final double xDiff = this.x - x;
        final double yDiff = this.y - y;

        return FastMath.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    /** {@inheritDoc} */
    public boolean equals(Object o) {
        if (o instanceof City) {
            final City other = (City) o;
            return x == other.x &&
                y == other.y;
        }
        return false;
    }

    /** {@inheritDoc} */
    public int hashCode() {
        int result = 17;

        final long c1 = Double.doubleToLongBits(x);
        result = 31 * result + (int) (c1 ^ (c1 >>> 32));

        final long c2 = Double.doubleToLongBits(y);
        result = 31 * result + (int) (c2 ^ (c2 >>> 32));

        return result;
    }
}
