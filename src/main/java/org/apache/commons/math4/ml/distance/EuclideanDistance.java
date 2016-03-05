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
package org.apache.commons.math4.ml.distance;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.util.MathArrays;

/**
 * Calculates the L<sub>2</sub> (Euclidean) distance between two points.
 *
 * @since 3.2
 */
public class EuclideanDistance implements DistanceMeasure {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 1717556319784040040L;

    /** {@inheritDoc} */
    @Override
    public double compute(double[] a, double[] b)
    throws DimensionMismatchException {
        return MathArrays.distance(a, b);
    }

}
