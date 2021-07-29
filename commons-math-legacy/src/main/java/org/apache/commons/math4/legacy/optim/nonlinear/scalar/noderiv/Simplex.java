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
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.ZeroException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.legacy.optim.OptimizationData;
import org.apache.commons.math4.legacy.optim.PointValuePair;

/**
 * Represents a simplex.
 *
 * @see SimplexOptimizer
 * @see MultiDirectionalTransform
 * @see NelderMeadTransform
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
     * @param simplex Reference simplex.
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
     * Builds a unit hypercube simplex.
     *
     * @param n Dimension of the simplex.
     * @return a new instance.
     */
    public static Simplex of(int n) {
        return of(n, 1d);
    }

    /**
     * Builds a hypercube simplex with the given side length.
     *
     * @param n Dimension of the simplex.
     * @param sideLength Length of the sides of the hypercube.
     * @return a new instance.
     */
    public static Simplex of(int n,
                             double sideLength) {
        final double[] steps = new double[n];
        Arrays.fill(steps, sideLength);
        return of(steps);
    }

    /**
     * The start configuration for simplex is built from a box parallel to
     * the canonical axes of the space. The simplex is the subset of vertices
     * of a box parallel to the canonical axes. It is built as the path followed
     * while traveling from one vertex of the box to the diagonally opposite
     * vertex moving only along the box edges. The first vertex of the box will
     * be located at the start point of the optimization.
     * As an example, in dimension 3 a simplex has 4 vertices. Setting the
     * steps to (1, 10, 2) and the start point to (1, 1, 1) would imply the
     * start simplex would be: { (1, 1, 1), (2, 1, 1), (2, 11, 1), (2, 11, 3) }.
     * The first vertex would be set to the start point at (1, 1, 1) and the
     * last vertex would be set to the diagonally opposite vertex at (2, 11, 3).
     *
     * @param steps Steps along the canonical axes representing box edges.
     * They may be negative but not zero.
     * @throws ZeroException if one of the steps is zero.
     * @return a new instance.
     */
    public static Simplex of(double[] steps) {
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
                    throw new ZeroException(LocalizedFormats.EQUAL_VERTICES_IN_SIMPLEX);
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
     * Translates the simplex such that its first point is at the given {@code point}.
     *
     * @param point Coordinates of the new simplex's first point.
     * @return a new instance.
     * @throws DimensionMismatchException if the start point does not match
     * simplex dimension.
     */
    /* package private */ Simplex translate(final double[] point) {
        final int dim = getDimension();
        if (dim != point.length) {
            throw new DimensionMismatchException(dim, point.length);
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

        return of(coordinates);
    }

    /**
     * Builds a new simplex where the given {@code point} replaces the
     * one at {@code index} in this instance.
     *
     * @param index Index of the point to replace.
     * @param point Replacement for the point currently at {@code index}.
     * @return a new instance.
     * @throws IndexOutOfBoundsException if {@code index} is out of bounds.
     */
    /* package private */ Simplex withReplacement(int index,
                                                  PointValuePair point) {
        final int len = points.size();
        if (index < 0 ||
            index >= len) {
            throw new IndexOutOfBoundsException("index: " + index);
        }

        final List<PointValuePair> newPoints = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            final PointValuePair pv = i == index ?
                point :
                points.get(i);
            newPoints.add(new PointValuePair(pv.getPoint(), pv.getValue(), false));
        }

        return new Simplex(newPoints);
    }

    /**
     * Generator of simplex transform.
     */
    public interface TransformFactory
        extends BiFunction<MultivariateFunction, Comparator<PointValuePair>, UnaryOperator<Simplex>> {}

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
     * Utility for the "shrinking" a simplex: All the points will be transformed
     * except the one at index 0.
     *
     * @param sigma Shrink factor.
     * @param function Evaluation function.
     * @return a new instance.
     */
    /* package private */ Simplex shrink(double sigma,
                                         MultivariateFunction function) {
        final int size = getSize();
        final double[] xBest = get(0).getPoint();
        Simplex newSimplex = this;
        for (int i = 1; i < size; i++) {
            final PointValuePair p = newPoint(xBest,
                                              sigma,
                                              get(i).getPoint(),
                                              function);
            newSimplex = newSimplex.withReplacement(i, p);
        }

        return newSimplex;
    }
}
