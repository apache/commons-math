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

package org.apache.commons.math4.examples.sofm.chineserings;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

import org.apache.commons.math4.neuralnet.SquareNeighbourhood;
import org.apache.commons.math4.neuralnet.FeatureInitializer;
import org.apache.commons.math4.neuralnet.FeatureInitializerFactory;
import org.apache.commons.math4.neuralnet.DistanceMeasure;
import org.apache.commons.math4.neuralnet.EuclideanDistance;
import org.apache.commons.math4.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math4.neuralnet.sofm.LearningFactorFunction;
import org.apache.commons.math4.neuralnet.sofm.LearningFactorFunctionFactory;
import org.apache.commons.math4.neuralnet.sofm.NeighbourhoodSizeFunction;
import org.apache.commons.math4.neuralnet.sofm.NeighbourhoodSizeFunctionFactory;
import org.apache.commons.math4.neuralnet.sofm.KohonenUpdateAction;
import org.apache.commons.math4.neuralnet.sofm.KohonenTrainingTask;
import org.apache.commons.math4.legacy.stat.descriptive.SummaryStatistics;

/**
 * SOFM for categorizing points that belong to each of two intertwined rings.
 */
class ChineseRingsClassifier {
    /** SOFM. */
    private final NeuronSquareMesh2D sofm;
    /** Rings. */
    private final ChineseRings rings;
    /** Distance function. */
    private final DistanceMeasure distance = new EuclideanDistance();

    /**
     * @param rings Training data.
     * @param dim1 Number of rows of the SOFM.
     * @param dim2 Number of columns of the SOFM.
     */
    ChineseRingsClassifier(ChineseRings rings,
                           int dim1,
                           int dim2) {
        this.rings = rings;
        sofm = new NeuronSquareMesh2D(dim1, false,
                                      dim2, false,
                                      SquareNeighbourhood.MOORE,
                                      makeInitializers());
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
            = LearningFactorFunctionFactory.exponentialDecay(1e-1,
                                                             5e-2,
                                                             numSamplesPerTask / 2);
        final double numNeurons = Math.sqrt((double) sofm.getNumberOfRows() * sofm.getNumberOfColumns());
        final NeighbourhoodSizeFunction neighbourhood
            = NeighbourhoodSizeFunctionFactory.exponentialDecay(0.5 * numNeurons,
                                                                0.2 * numNeurons,
                                                                numSamplesPerTask / 2);

        for (int i = 0; i < numTasks; i++) {
            final KohonenUpdateAction action = new KohonenUpdateAction(distance,
                                                                       learning,
                                                                       neighbourhood);
            tasks[i] = new KohonenTrainingTask(sofm.getNetwork(),
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
     * Computes various quality measures.
     *
     * @return the indicators.
     */
    public NeuronSquareMesh2D.DataVisualization computeQualityIndicators() {
        return sofm.computeQualityIndicators(rings.createIterable());
    }

    /**
     * Creates the features' initializers.
     * They are sampled from a uniform distribution around the barycentre of
     * the rings.
     *
     * @return an array containing the initializers for the x, y and
     * z coordinates of the features array of the neurons.
     */
    private FeatureInitializer[] makeInitializers() {
        final SummaryStatistics[] centre = {
            new SummaryStatistics(),
            new SummaryStatistics(),
            new SummaryStatistics()
        };
        for (final Vector3D p : rings.getPoints()) {
            centre[0].addValue(p.getX());
            centre[1].addValue(p.getY());
            centre[2].addValue(p.getZ());
        }

        final double[] mean = {
            centre[0].getMean(),
            centre[1].getMean(),
            centre[2].getMean()
        };
        final double[] dev = {
            0.1 * centre[0].getStandardDeviation(),
            0.1 * centre[1].getStandardDeviation(),
            0.1 * centre[2].getStandardDeviation()
        };

        final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
        return new FeatureInitializer[] {
            FeatureInitializerFactory.uniform(rng, mean[0] - dev[0], mean[0] + dev[0]),
            FeatureInitializerFactory.uniform(rng, mean[1] - dev[1], mean[1] + dev[1]),
            FeatureInitializerFactory.uniform(rng, mean[2] - dev[2], mean[2] + dev[2])
        };
    }

    /**
     * Creates an iterator that will present a series of points coordinates in
     * a random order.
     *
     * @param numSamples Number of samples.
     * @return the iterator.
     */
    private Iterator<double[]> createRandomIterator(final long numSamples) {
        return new Iterator<double[]>() {
            /** Data. */
            private final Vector3D[] points = rings.getPoints();
            /** RNG. */
            private final UniformRandomProvider rng = RandomSource.KISS.create();
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
                return points[rng.nextInt(points.length)].toArray();
            }

            /** {@inheritDoc} */
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
