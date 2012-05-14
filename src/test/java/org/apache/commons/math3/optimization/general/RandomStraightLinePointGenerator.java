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
 * Factory for generating a cloud of points that approximate a straight line.
 */
public class RandomStraightLinePointGenerator {
    /** RNG. */
    private final RandomData random;
    /** Slope. */
    private final double slope;
    /** Intercept. */
    private final double intercept;
    /** Error on the y-coordinate. */
    private final double sigma;
    /** Lowest value of the x-coordinate. */
    private final double lo;
    /** Highest value of the x-coordinate. */
    private final double hi;

    /**
     * The generator will create a cloud of points whose x-coordinates
     * will be randomly sampled between {@code xLo} and {@code xHi}, and
     * the correspoding y-coordinates will be computed as
     * <pre><code>
     *  y = a x + b + N(0, error)
     * </code></pre>
     * where {@code N(mean, sigma)} is a Gaussian distribution with the
     * given mean and standard deviation.
     *
     * @param a Slope.
     * @param b Intercept.
     * @param error Error on the y-coordinate of the point.
     * @param xLo Lowest value of the x-coordinate.
     * @param xHi Highest value of the x-coordinate.
     * @param seed RNG seed.
     */
    public RandomStraightLinePointGenerator(double a,
                                            double b,
                                            double error,
                                            double xLo,
                                            double xHi,
                                            long seed) {
        random = new RandomDataImpl(new Well44497b((seed)));
        slope = a;
        intercept = b;
        sigma = error;
        lo = xLo;
        hi = xHi;
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
        final double x = random.nextUniform(lo, hi);
        final double yModel = slope * x + intercept;
        final double y = yModel + random.nextGaussian(0, sigma);

        return new Point2D.Double(x, y);
    }
}
