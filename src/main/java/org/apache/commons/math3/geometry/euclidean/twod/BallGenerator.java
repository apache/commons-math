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
package org.apache.commons.math3.geometry.euclidean.twod;

import java.util.List;

import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import org.apache.commons.math3.geometry.enclosing.SupportBallGenerator;
import org.apache.commons.math3.util.MathArrays;

/** Class generating an enclosing ball from its support points.
 * @version $Id$
 * @since 3.3
 */
public class BallGenerator implements SupportBallGenerator<Euclidean2D, Vector2D> {

    /** {@inheritDoc} */
    public EnclosingBall<Euclidean2D, Vector2D> ballOnSupport(final List<Vector2D> support) {

        if (support.size() < 1) {
            return new EnclosingBall<Euclidean2D, Vector2D>(Vector2D.ZERO, -1.0);
        } else {
            final Vector2D vA = support.get(0);
            if (support.size() < 2) {
                return new EnclosingBall<Euclidean2D, Vector2D>(vA, 0, vA);
            } else {
                final Vector2D vB = support.get(1);
                if (support.size() < 3) {
                    return new EnclosingBall<Euclidean2D, Vector2D>(new Vector2D(0.5, vA, 0.5, vB),
                                                                    0.5 * vA.distance(vB),
                                                                    vA, vB);
                } else {
                    final Vector2D vC = support.get(2);
                    final Vector2D bc = vB.subtract(vC);
                    final Vector2D ca = vC.subtract(vA);
                    final Vector2D ab = vA.subtract(vB);
                    final double vA2  = vA.getNormSq();
                    final double vB2  = vB.getNormSq();
                    final double vC2  = vC.getNormSq();
                    final double d    = 2 * MathArrays.linearCombination(vA.getX(), bc.getY(),
                                                                         vB.getX(), ca.getY(),
                                                                         vC.getX(), ab.getY());
                    final Vector2D center = new Vector2D( MathArrays.linearCombination(vA2, bc.getY(),
                                                                                       vB2, ca.getY(),
                                                                                       vC2, ab.getY()) / d,
                                                         -MathArrays.linearCombination(vA2, bc.getX(),
                                                                                       vB2, ca.getX(),
                                                                                       vC2, ab.getX()) / d);
                    return new EnclosingBall<Euclidean2D, Vector2D>(center, center.distance(vA), vA, vB, vC);
                }
            }
        }
    }

}
