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

package org.apache.commons.math4.legacy.optim.nonlinear.scalar.noderiv;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.function.UnaryOperator;
import java.util.function.DoublePredicate;

import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.ZeroException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.legacy.optim.OptimizationData;
import org.apache.commons.math4.legacy.optim.PointValuePair;

/**
 * Represents a <a href="https://en.wikipedia.org/wiki/Simplex">simplex</a>.
 *
 * @see SimplexOptimizer
 */
public final class Simplex implements OptimizationData {
    /** Coordinates. */
    private final List<PointValuePair> points;

    /**
     * Builds from a given set of coordinates.
     *
     * @param referenceSimplex Reference simplex.
     * @throws NotStrictlyPositiveException if the reference simplex does not
     * contain at least one point.
     * @throws DimensionMismatchException if there is a dimension mismatch
     * in the reference simplex.
     * @throws IllegalArgumentException if one of its vertices is duplicated.
     */
    private Simplex(double[][] referenceSimplex) {
        if (referenceSimplex.length <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SIMPLEX_NEED_ONE_POINT,
                                                   referenceSimplex.length);
        }
        final int len = referenceSimplex.length;
        points = new ArrayList<>(len);

        final int dim = len - 1;

        // Loop over vertices.
        for (int i = 0; i < len; i++) {
            final double[] refI = referenceSimplex[i];

            // Safety checks.
            if (refI.length != dim) {
                throw new DimensionMismatchException(refI.length, dim);
            }
            for (int j = 1; j < i; j++) {
                final double[] refJ = referenceSimplex[j];
                boolean allEquals = true;
                for (int k = 0; k < dim; k++) {
                    if (refI[k] != refJ[k]) {
                        allEquals = false;
                        break;
                    }
                }
                if (allEquals) {
                    throw new MathIllegalArgumentException(LocalizedFormats.EQUAL_VERTICES_IN_SIMPLEX,
                                                           i, j);
                }
            }

            points.add(new PointValuePair(refI, Double.NaN));
        }
    }

    /**
     * Builds from an existing simplex.
     *
     * @param points Simplex data.  Reference will be stored in the newly
     * constructed instance.
     */
    private Simplex(List<PointValuePair> points) {
        this.points = points;
    }

    /**
     * Builds from a given set of coordinates.
     *
     * @param simplex Simplex coordinates.
     * @return a new instance.
     * @throws NotStrictlyPositiveException if the reference simplex does not
     * contain at least one point.
     * @throws DimensionMismatchException if there is a dimension mismatch
     * in the reference simplex.
     * @throws IllegalArgumentException if one of its vertices is duplicated.
     */
    public static Simplex of(double[][] simplex) {
        return new Simplex(simplex);
    }

    /**
     * Builds simplex with the given side length.
     *
     * @param dim Space dimensions.
     * @param sideLength Length of the sides of the hypercube.
     * @return a new instance.
     */
    public static Simplex equalSidesAlongAxes(int dim,
                                              double sideLength) {
        final double[] steps = new double[dim];
        Arrays.fill(steps, sideLength);
        return alongAxes(steps);
    }

    /**
     * The start configuration for simplex is built from a box parallel to
     * the canonical axes of the space. The simplex is the subset of vertices
     * of a box parallel to the canonical axes. It is built as the path followed
     * while traveling from one vertex of the box to the diagonally opposite
     * vertex moving only along the box edges. The first vertex of the box will
     * be located at the origin of the coordinate system.
     *
     * To be used for simplex-based optimization, the simplex must be
     * {@link #translate(double[]) translated} so that its first vertex will be
     * the {@link org.apache.commons.math4.legacy.optim.InitialGuess initial guess}.
     *
     * For example, in dimension 3 a simplex has 4 vertices. Setting the
     * steps to (1, 10, 2) and the start point to (1, 1, 1) would imply the
     * initial simplex would be:
     * <ol>
     *  <li>(1, 1, 1),</li>
     *  <li>(2, 1, 1),</li>
     *  <li>(2, 11, 1),</li>
     *  <li>(2, 11, 3).</li>
     * </ol>
     *
     * @param steps Steps along the canonical axes representing box edges.
     * They may be negative but not zero.
     * @throws ZeroException if one of the steps is zero.
     * @return a new instance.
     */
    public static Simplex alongAxes(double[] steps) {
        if (steps.length == 0) {
            throw new ZeroException();
        }
        final int dim = steps.length;
        final int len = dim + 1;

        // Only the relative position of the n final vertices with respect
        // to the first one are stored.
        final double[][] simplex = new double[len][dim];
        for (int i = 1; i < len; i++) { // First point is the origin (zero).
            final double[] vertexI = simplex[i];
            for (int j = 0; j < i; j++) {
                if (steps[j] == 0) {
                    throw new ZeroException();
                }
                System.arraycopy(steps, 0, vertexI, 0, j + 1);
            }
        }

        return new Simplex(simplex);
    }

    /**
     * Returns the space dimension.
     *
     * @return the dimension of the simplex.
     */
    public int getDimension() {
        return points.size() - 1;
    }

    /**
     * Returns the number of vertices.
     *
     * @return the size of the simplex.
     */
    public int getSize() {
        return points.size();
    }

    /**
     * Evaluates the (non-evaluated) simplex points and returns a new instance
     * with vertices sorted from best to worst.
     *
     * @param function Evaluation function.
     * @param comparator Comparator for sorting vertices, from best to worst.
     * @return a new instance in which the vertices are sorted according to
     * the given {@code comparator}.
     */
    public Simplex evaluate(MultivariateFunction function,
                            Comparator<PointValuePair> comparator) {
        final List<PointValuePair> newPoints = new ArrayList<>(points.size());
        for (PointValuePair pv : points) {
            final double[] coord = pv.getPoint();
            final double value = Double.isNaN(pv.getValue()) ?
                function.value(coord) :
                pv.getValue();

            newPoints.add(new PointValuePair(coord, value, false));
        }

        Collections.sort(newPoints, comparator);
        return new Simplex(newPoints);
    }

    /**
     * Retrieves a copy of the simplex point stored at {@code index}.
     *
     * @param index Location.
     * @return the point at location {@code index}.
     */
    public PointValuePair get(int index) {
        final PointValuePair p = points.get(index);
        return new PointValuePair(p.getPoint(), p.getValue());
    }

    /**
     * Creates a (deep) copy of the simplex points.
     *
     * @return the points.
     */
    public List<PointValuePair> asList() {
        return asList(0, points.size());
    }

    /**
     * Generator of simplex transform.
     *
     * @see MultiDirectionalTransform
     * @see NelderMeadTransform
     * @see HedarFukushimaTransform
     */
    public interface TransformFactory extends OptimizationData {
        /**
         * Creates a simplex transformation.
         *
         * @param evaluationFunction Evaluation function.
         * @param comparator Vertex fitness comparator.
         * @param saAcceptance Simulated annealing acceptance test.
         * @return the simplex transform operator.
         */
        UnaryOperator<Simplex> create(MultivariateFunction evaluationFunction,
                                      Comparator<PointValuePair> comparator,
                                      DoublePredicate saAcceptance);
    }

    /**
     * Creates a (deep) copy of the simplex points within slots
     * {@code from} (included) and {@code to} (excluded).
     *
     * @param from Index of the first point to retrieve.
     * @param to One past the index of the last point to retrieve.
     * @return the points.
     * @throws IllegalArgumentException if {@code from} and {@code to} are
     * not within the {@code [0, n + 1]} interval (where {@code n} is the
     * space dimension) or {@code from > to}.
     */
    /* package private */ List<PointValuePair> asList(int from,
                                                      int to) {
        if (from < 0 ||
            to > points.size() ||
            from > to) {
            throw new IllegalArgumentException("Index");
        }

        final int len = to - from;
        final List<PointValuePair> copy = new ArrayList<>(len);
        for (int i = from; i < to; i++) {
            copy.add(get(i));
        }

        return copy;
    }

    /**
     * Utility for evaluating a point with coordinates \( a_i + s (b_i - a_i) \).
     *
     * @param a Cartesian coordinates.
     * @param s Scaling factor.
     * @param b Cartesian coordinates.
     * @param function Evaluation function.
     * @return a new point.
     */
    /* package private */ static PointValuePair newPoint(double[] a,
                                                         double s,
                                                         double[] b,
                                                         MultivariateFunction function) {
        final int dim = a.length;
        final double[] r = new double[dim];
        for (int i = 0; i < dim; i++) {
            final double m = a[i];
            r[i] = m + s * (b[i] - m);
        }

        return new PointValuePair(r, function.value(r), false);
    }

    /**
     * Utility for the "shrinking" a simplex: All the points will be
     * transformed except the one at index 0.
     *
     * @param sigma Shrink factor.
     * @param function Evaluation function.
     * @return a new instance.
     */
    /* package private */ Simplex shrink(double sigma,
                                         MultivariateFunction function) {
        final int replSize = getSize() - 1;
        final List<PointValuePair> replacement = new ArrayList<>();
        final double[] bestPoint = get(0).getPoint();
        for (int i = 0; i < replSize; i++) {
            replacement.add(Simplex.newPoint(bestPoint,
                                             sigma,
                                             get(i + 1).getPoint(),
                                             function));
        }

        return replaceLast(replacement);
    }

    /**
     * Translates the simplex such that the first point's new coordinates
     * will be at the given {@code point}.
     *
     * @param point Coordinates of the new simplex's first point.
     * @return the translated points.
     * @throws DimensionMismatchException if the dimensions do not match.
     */
    /* package private */ Simplex translate(double[] point) {
        final int dim = point.length;
        if (getDimension() != dim) {
            throw new DimensionMismatchException(getDimension(), dim);
        }
        final int len = points.size();
        final double[][] coordinates = new double[len][dim];
        final double[] current0 = points.get(0).getPoint(); // Current first point.

        // Set new vertices.
        for (int i = 0; i < len; i++) {
            final double[] currentI = points.get(i).getPoint();

            final double[] newI = coordinates[i];
            for (int k = 0; k < dim; k++) {
                newI[k] = point[k] + currentI[k] - current0[k];
            }
        }

        return new Simplex(coordinates);
    }

    /**
     * Creates a new simplex where the given {@code point} replaces the one at the
     * last position.
     * Caveat: No check is done that the resulting set of points forms is a simplex.
     *
     * @param point Point.
     * @return a new instance.
     */
    /* package private */ Simplex replaceLast(PointValuePair point) {
        final List<PointValuePair> newPoints = asList(0, getDimension()); // Deep copy.
        newPoints.add(new PointValuePair(point.getPoint(), // Deep copy.
                                         point.getValue(),
                                         false));

        return new Simplex(newPoints);
    }

    /**
     * Replace the last points of the simplex with the points from the given
     * {@code replacement} list.
     * Caveat: No check is done that the resulting set of points is a simplex.
     *
     * @param replacement List of points that will replace the last points of
     * the {@code simplex}.
     * @return a new instance.
     */
    /* package private */ Simplex replaceLast(List<PointValuePair> replacement) {
        final int nPoints = replacement.size();
        final int from = points.size() - nPoints;
        final List<PointValuePair> newPoints = asList(0, from); // Deep copy.


        for (int i = 0; i < nPoints; i++) {
            final PointValuePair p = replacement.get(i);
            newPoints.add(new PointValuePair(p.getPoint(), // Deep copy.
                                             p.getValue(),
                                             false));
        }

        return new Simplex(newPoints);
    }

    /**
     * @param list List of simplex points.
     * @return the centroid of the points in the given {@code list}.
     */
    /* package private */ static double[] centroid(List<PointValuePair> list) {
        final double[] centroid = list.get(0).getPoint();

        final int nPoints = list.size();
        final int dim = centroid.length;
        for (int i = 1; i < nPoints; i++) {
            final double[] p = list.get(i).getPoint();
            for (int k = 0; k < dim; k++) {
                centroid[k] += p[k];
            }
        }

        for (int k = 0; k < dim; k++) {
            centroid[k] /= nPoints;
        }

        return centroid;
    }
}
