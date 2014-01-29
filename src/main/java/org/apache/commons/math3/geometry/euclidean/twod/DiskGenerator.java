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
public class DiskGenerator implements SupportBallGenerator<Euclidean2D, Vector2D> {

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
                    // a disk is 2D can be defined as:
                    // (1)   (x - x_0)^2 + (y - y_0)^2 = r^2
                    // which can be written:
                    // (2)   (x^2 + y^2) - 2 x_0 x - 2 y_0 y + (x_0^2 + y_0^2 - r^2) = 0
                    // or simply:
                    // (3)   (x^2 + y^2) + a x + b y + c= 0
                    // with disk center coordinates -a/2, -b/2
                    // If the sphere exists, a, b and c are a non zero solution to
                    // [ (x^2  + y^2 )   x    y   1 ]   [ 1 ]   [ 0 ]
                    // [ (xA^2 + yA^2)   xA   yA  1 ]   [ a ]   [ 0 ]
                    // [ (xB^2 + yB^2)   xB   yB  1 ] * [ b ] = [ 0 ]
                    // [ (xC^2 + yC^2)   xC   yC  1 ]   [ c ]   [ 0 ]
                    // So the determinant of the matrix is zero. Computing this determinant
                    // by expanding it using the minors m_ij of first row leads to
                    // (4)   m_11 (x^2 + y^2) - m_12 x + m_13 y - m_14 = 0
                    // So by identifying equations (2) and (4) we get the coordinates
                    // of center as:
                    //      x_0 = +m_12 / (2 m_11)
                    //      y_0 = -m_13 / (2 m_11)
                    // Note that the minors m_11, m_12 and m_13 all have the last column
                    // filled with 1.0, hence simplifying the computation
                    final double[] c1 = new double[] {
                        vA.getNormSq(), vB.getNormSq(), vC.getNormSq()
                    };
                    final double[] c2 = new double[] {
                        vA.getX(), vB.getX(), vC.getX()
                    };
                    final double[] c3 = new double[] {
                        vA.getY(), vB.getY(), vC.getY()
                    };
                    final double m11 = minor(c2, c3);
                    final double m12 = minor(c1, c3);
                    final double m13 = minor(c1, c2);
                    final Vector2D center = new Vector2D(0.5 * m12 / m11, -0.5 * m13 / m11);
                    return new EnclosingBall<Euclidean2D, Vector2D>(center, center.distance(vA), vA, vB, vC);
                }
            }
        }
    }

    /** Compute a dimension 3 minor, when 3<sup>d</sup> column is known to be filled with 1.0.
     * <p>
     * The computation is performed using {@link MathArrays#linearCombination(double[], double[])
     * high accuracy sum of products}, trying to avoid cancellations effect. This should reduce
     * risks in case of near co-planar points.
     * </p>
     * @param c1 first column
     * @param c2 second column
     * @return value of the minor computed to high accuracy
     */
    private double minor(final double[] c1, final double[] c2) {
        return MathArrays.linearCombination(new double[] {
                                                c1[0], c1[2], c1[1], -c1[2], -c1[0], -c1[1]
                                            },
                                            new double[] {
                                                c2[1], c2[0], c2[2],  c2[1],  c2[2],  c2[0]
                                            });
    }

}
