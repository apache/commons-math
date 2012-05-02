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

package org.apache.commons.math3.optimization.general;

import java.awt.geom.Point2D;
import org.apache.commons.math3.random.RandomData;
import org.apache.commons.math3.random.RandomDataImpl;
import org.apache.commons.math3.random.Well44497b;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.FastMath;

/**
 * Factory for generating a cloud of points that approximate a circle.
 */
public class RandomCirclePointGenerator {
    /** RNG. */
    private final RandomData random;
    /** Radius of the circle. */
    private final double radius;
    /** x-coordinate of the circle center. */
    private final double x;
    /** y-coordinate of the circle center. */
    private final double y;
    /** Error on the x-coordinate of the center. */
    private final double xSigma;
    /** Error on the y-coordinate of the center. */
    private final double ySigma;

    /**
     * @param x Abscissa of the circle center.
     * @param y Ordinate of the circle center.
     * @param radius Radius of the circle.
     * @param xSigma Error on the x-coordinate of the circumference points.
     * @param ySigma Error on the y-coordinate of the circumference points.
     * @param seed RNG seed.
     */
    public RandomCirclePointGenerator(double x,
                                      double y,
                                      double radius,
                                      double xSigma,
                                      double ySigma,
                                      long seed) {
        random = new RandomDataImpl(new Well44497b((seed)));
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.xSigma = xSigma;
        this.ySigma = ySigma;
    }

    /**
     * Point generator.
     *
     * @param n Number of points to create.
     * @return the cloud of {@code n} points.
     */
    public Point2D.Double[] generate(int n) {
        final Point2D.Double[] cloud = new Point2D.Double[n];
        for (int i = 0; i < n; i++) {
            cloud[i] = create();
        }
        return cloud;
    }

    /**
     * Create one point.
     *
     * @return a point.
     */
    private Point2D.Double create() {
        final double cX = random.nextGaussian(x, xSigma);
        final double cY = random.nextGaussian(y, ySigma);
        final double t = random.nextUniform(0, MathUtils.TWO_PI);

        final double pX = cX + radius * FastMath.cos(t);
        final double pY = cY + radius * FastMath.sin(t);

        return new Point2D.Double(pX, pY);
    }
}
