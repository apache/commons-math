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

package org.apache.commons.math4.ml.clustering.initialization;

import org.apache.commons.math4.ml.clustering.CentroidCluster;
import org.apache.commons.math4.ml.clustering.Clusterable;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.rng.UniformRandomProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Use K-means++ to choose the initial centers.
 *
 * @see <a href="http://en.wikipedia.org/wiki/K-means%2B%2B">K-means++ (wikipedia)</a>
 */
public class KMeansPlusPlusCentroidInitializer implements CentroidInitializer {
    private final DistanceMeasure measure;
    private final UniformRandomProvider random;

    /**
     * Build a K-means++ CentroidInitializer
     * @param measure the distance measure to use
     * @param random the random to use.
     */
    public KMeansPlusPlusCentroidInitializer(final DistanceMeasure measure, final UniformRandomProvider random) {
        this.measure = measure;
        this.random = random;
    }

    /**
     * Use K-means++ to choose the initial centers.
     *
     * @param points the points to choose the initial centers from
     * @param k      The number of clusters
     * @return the initial centers
     */
    @Override
    public <T extends Clusterable> List<CentroidCluster<T>> selectCentroids(final Collection<T> points, final int k) {
        // Convert to list for indexed access. Make it unmodifiable, since removal of items
        // would screw up the logic of this method.
        final List<T> pointList = Collections.unmodifiableList(new ArrayList<>(points));

        // The number of points in the list.
        final int numPoints = pointList.size();

        // Set the corresponding element in this array to indicate when
        // elements of pointList are no longer available.
        final boolean[] taken = new boolean[numPoints];

        // The resulting list of initial centers.
        final List<CentroidCluster<T>> resultSet = new ArrayList<>();

        // Choose one center uniformly at random from among the data points.
        final int firstPointIndex = random.nextInt(numPoints);

        final T firstPoint = pointList.get(firstPointIndex);

        resultSet.add(new CentroidCluster<>(firstPoint));

        // Must mark it as taken
        taken[firstPointIndex] = true;

        // To keep track of the minimum distance squared of elements of
        // pointList to elements of resultSet.
        final double[] minDistSquared = new double[numPoints];

        // Initialize the elements.  Since the only point in resultSet is firstPoint,
        // this is very easy.
        for (int i = 0; i < numPoints; i++) {
            if (i != firstPointIndex) { // That point isn't considered
                double d = distance(firstPoint, pointList.get(i));
                minDistSquared[i] = d * d;
            }
        }

        while (resultSet.size() < k) {

            // Sum up the squared distances for the points in pointList not
            // already taken.
            double distSqSum = 0.0;

            for (int i = 0; i < numPoints; i++) {
                if (!taken[i]) {
                    distSqSum += minDistSquared[i];
                }
            }

            // Add one new data point as a center. Each point x is chosen with
            // probability proportional to D(x)2
            final double r = random.nextDouble() * distSqSum;

            // The index of the next point to be added to the resultSet.
            int nextPointIndex = -1;

            // Sum through the squared min distances again, stopping when
            // sum >= r.
            double sum = 0.0;
            for (int i = 0; i < numPoints; i++) {
                if (!taken[i]) {
                    sum += minDistSquared[i];
                    if (sum >= r) {
                        nextPointIndex = i;
                        break;
                    }
                }
            }

            // If it's not set to >= 0, the point wasn't found in the previous
            // for loop, probably because distances are extremely small.  Just pick
            // the last available point.
            if (nextPointIndex == -1) {
                for (int i = numPoints - 1; i >= 0; i--) {
                    if (!taken[i]) {
                        nextPointIndex = i;
                        break;
                    }
                }
            }

            // We found one.
            if (nextPointIndex >= 0) {

                final T p = pointList.get(nextPointIndex);

                resultSet.add(new CentroidCluster<>(p));

                // Mark it as taken.
                taken[nextPointIndex] = true;

                if (resultSet.size() < k) {
                    // Now update elements of minDistSquared.  We only have to compute
                    // the distance to the new center to do this.
                    for (int j = 0; j < numPoints; j++) {
                        // Only have to worry about the points still not taken.
                        if (!taken[j]) {
                            double d = distance(p, pointList.get(j));
                            double d2 = d * d;
                            if (d2 < minDistSquared[j]) {
                                minDistSquared[j] = d2;
                            }
                        }
                    }
                }

            } else {
                // None found --
                // Break from the while loop to prevent
                // an infinite loop.
                break;
            }
        }

        return resultSet;
    }

    /**
     * Calculates the distance between two {@link Clusterable} instances
     * with the configured {@link DistanceMeasure}.
     *
     * @param p1 the first clusterable
     * @param p2 the second clusterable
     * @return the distance between the two clusterables
     */
    protected double distance(final Clusterable p1, final Clusterable p2) {
        return measure.compute(p1.getPoint(), p2.getPoint());
    }
}
