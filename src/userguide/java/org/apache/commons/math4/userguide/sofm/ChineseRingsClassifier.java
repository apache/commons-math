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

package org.apache.commons.math4.userguide.sofm;

import java.util.Iterator;
import java.io.PrintWriter;
import java.io.IOException;
import org.apache.commons.math4.ml.neuralnet.SquareNeighbourhood;
import org.apache.commons.math4.ml.neuralnet.FeatureInitializer;
import org.apache.commons.math4.ml.neuralnet.FeatureInitializerFactory;
import org.apache.commons.math4.ml.neuralnet.MapUtils;
import org.apache.commons.math4.ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math4.ml.neuralnet.sofm.LearningFactorFunction;
import org.apache.commons.math4.ml.neuralnet.sofm.LearningFactorFunctionFactory;
import org.apache.commons.math4.ml.neuralnet.sofm.NeighbourhoodSizeFunction;
import org.apache.commons.math4.ml.neuralnet.sofm.NeighbourhoodSizeFunctionFactory;
import org.apache.commons.math4.ml.neuralnet.sofm.KohonenUpdateAction;
import org.apache.commons.math4.ml.neuralnet.sofm.KohonenTrainingTask;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.ml.distance.EuclideanDistance;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.Well19937c;
import org.apache.commons.math4.stat.descriptive.SummaryStatistics;
import org.apache.commons.math4.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.exception.MathUnsupportedOperationException;

/**
 * SOFM for categorizing points that belong to each of two intertwined rings.
 *
 * The output currently consists in 3 text files:
 * <ul>
 *  <li>"before.chinese.U.seq.dat": U-matrix of the SOFM before training</li>
 *  <li>"after.chinese.U.seq.dat": U-matrix of the SOFM after training</li>
 *  <li>"after.chinese.hit.seq.dat": Hit histogram after training</li>
 * <ul> 
 */
public class ChineseRingsClassifier {
    /** SOFM. */
    private final NeuronSquareMesh2D sofm;
    /** Rings. */
    private final ChineseRings rings;
    /** Distance function. */
    private final DistanceMeasure distance = new EuclideanDistance();

    public static void main(String[] args) {
        final ChineseRings rings = new ChineseRings(new Vector3D(1, 2, 3),
                                                    25, 2,
                                                    20, 1,
                                                    2000, 1500);
        final ChineseRingsClassifier classifier = new ChineseRingsClassifier(rings, 15, 15);
        printU("before.chinese.U.seq.dat", classifier);
        classifier.createSequentialTask(100000).run();
        printU("after.chinese.U.seq.dat", classifier);
        printHit("after.chinese.hit.seq.dat", classifier);
    }

    /**
     * @param rings Training data.
     * @param dim1 Number of rows of the SOFM.
     * @param dim2 Number of columns of the SOFM.
     */
    public ChineseRingsClassifier(ChineseRings rings,
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
        final double numNeurons = FastMath.sqrt(sofm.getNumberOfRows() * sofm.getNumberOfColumns());
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
     * Computes the U-matrix.
     *
     * @return the U-matrix of the network.
     */
    public double[][] computeU() {
        return MapUtils.computeU(sofm, distance);
    }

    /**
     * Computes the hit histogram.
     *
     * @return the histogram.
     */
    public int[][] computeHitHistogram() {
        return MapUtils.computeHitHistogram(createIterable(),
                                            sofm,
                                            distance);
    }

    /**
     * Computes the quantization error.
     *
     * @return the quantization error.
     */
    public double computeQuantizationError() {
        return MapUtils.computeQuantizationError(createIterable(),
                                                 sofm.getNetwork(),
                                                 distance);
    }

    /**
     * Computes the topographic error.
     *
     * @return the topographic error.
     */
    public double computeTopographicError() {
        return MapUtils.computeTopographicError(createIterable(),
                                                sofm.getNetwork(),
                                                distance);
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
        final SummaryStatistics[] centre = new SummaryStatistics[] {
            new SummaryStatistics(),
            new SummaryStatistics(),
            new SummaryStatistics()
        };
        for (Vector3D p : rings.getPoints()) {
            centre[0].addValue(p.getX());
            centre[1].addValue(p.getY());
            centre[2].addValue(p.getZ());
        }

        final double[] mean = new double[] {
            centre[0].getMean(),
            centre[1].getMean(),
            centre[2].getMean()
        };
        final double s = 0.1;
        final double[] dev = new double[] {
            s * centre[0].getStandardDeviation(),
            s * centre[1].getStandardDeviation(),
            s * centre[2].getStandardDeviation()
        };

        return new FeatureInitializer[] {
            FeatureInitializerFactory.uniform(mean[0] - dev[0], mean[0] + dev[0]),
            FeatureInitializerFactory.uniform(mean[1] - dev[1], mean[1] + dev[1]),
            FeatureInitializerFactory.uniform(mean[2] - dev[2], mean[2] + dev[2])
        };
    }

    /**
     * Creates an iterable that will present the points coordinates.
     *
     * @return the iterable.
     */
    private Iterable<double[]> createIterable() {
        return new Iterable<double[]>() {
            public Iterator<double[]> iterator() {
                return new Iterator<double[]>() {
                    /** Data. */
                    final Vector3D[] points = rings.getPoints();
                    /** Number of samples. */
                    private int n = 0;

                    /** {@inheritDoc} */
                    public boolean hasNext() {
                        return n < points.length;
                    }

                    /** {@inheritDoc} */
                    public double[] next() {
                        return points[n++].toArray();
                    }

                    /** {@inheritDoc} */
                    public void remove() {
                        throw new MathUnsupportedOperationException();
                    }
                };
            }
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
            final Vector3D[] points = rings.getPoints();
            /** RNG. */
            final RandomGenerator rng = new Well19937c();
            /** Number of samples. */
            private long n = 0;

            /** {@inheritDoc} */
            public boolean hasNext() {
                return n < numSamples;
            }

            /** {@inheritDoc} */
            public double[] next() {
                ++n;
                return points[rng.nextInt(points.length)].toArray();
            }

            /** {@inheritDoc} */
            public void remove() {
                throw new MathUnsupportedOperationException();
            }
        };
    }

    /**
     * Prints the U-matrix of the map to the given filename.
     *
     * @param filename File.
     * @param sofm Classifier.
     */
    private static void printU(String filename,
                               ChineseRingsClassifier sofm) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(filename);

            final double[][] uMatrix = sofm.computeU();
            for (int i = 0; i < uMatrix.length; i++) {
                for (int j = 0; j < uMatrix[0].length; j++) {
                    out.print(uMatrix[i][j] + " ");
                }
                out.println();
            }
            out.println("# Quantization error: " + sofm.computeQuantizationError());
            out.println("# Topographic error: " + sofm.computeTopographicError());
        } catch (IOException e) {
            // Do nothing.
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Prints the hit histogram of the map to the given filename.
     *
     * @param filename File.
     * @param sofm Classifier.
     */
    private static void printHit(String filename,
                                 ChineseRingsClassifier sofm) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(filename);

            final int[][] histo = sofm.computeHitHistogram();
            for (int i = 0; i < histo.length; i++) {
                for (int j = 0; j < histo[0].length; j++) {
                    out.print(histo[i][j] + " ");
                }
                out.println();
            }
        } catch (IOException e) {
            // Do nothing.
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
