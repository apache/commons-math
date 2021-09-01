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

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

/**
 * A city, represented by a name and two-dimensional coordinates.
 */
public class City {
    /** Identifier. */
    private final String name;
    /** x-coordinate. */
    private final double x;
    /** y-coordinate. */
    private final double y;

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
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the (x, y) coordinates.
     */
    public double[] getCoordinates() {
        return new double[] {x, y};
    }

    /**
     * Computes the distance between this city and
     * the given point.
     *
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @return the distance between {@code (x, y)} and this
     * city.
     */
    public double distance(double x,
                           double y) {
        final double xDiff = this.x - x;
        final double yDiff = this.y - y;

        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    /**
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param cities City list.
     * @return the city whose coordinates are closest to {@code (x, y)}.
     */
    public static City closest(double x,
                               double y,
                               Set<City> cities) {
        City closest = null;
        double min = Double.POSITIVE_INFINITY;
        for (final City c : cities) {
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
    public static double[] barycentre(Set<City> cities) {
        double xB = 0;
        double yB = 0;

        int count = 0;
        for (final City c : cities) {
            final double[] coord = c.getCoordinates();
            xB += coord[0];
            yB += coord[1];

            ++count;
        }

        return new double[] {xB / count, yB / count};
    }

    /**
     * Computes the largest distance between the point at coordinates
     * {@code (x, y)} and any of the cities.
     *
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param cities City list.
     * @return the largest distance.
     */
    public static double largestDistance(double x,
                                         double y,
                                         Set<City> cities) {
        double maxDist = 0;
        for (final City c : cities) {
            final double dist = c.distance(x, y);
            if (dist > maxDist) {
                maxDist = dist;
            }
        }

        return maxDist;
    }

    /**
     * @param cities List of cities.
     * @return a list with no duplicate city.
     */
    public static Set<City> unique(City[] cities) {
        final Set<City> uniqueCities = new HashSet<>();
        uniqueCities.addAll(Arrays.asList(cities));
        return uniqueCities;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (o instanceof City) {
            final City other = (City) o;
            return x == other.x &&
                y == other.y;
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = 17;

        final long c1 = Double.doubleToLongBits(x);
        result = 31 * result + (int) (c1 ^ (c1 >>> 32));

        final long c2 = Double.doubleToLongBits(y);
        result = 31 * result + (int) (c2 ^ (c2 >>> 32));

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getName();
    }
}
