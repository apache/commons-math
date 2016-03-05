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

package org.apache.commons.math4.analysis.solvers;


/**
 * Implements the <em>Illinois</em> method for root-finding (approximating
 * a zero of a univariate real function). It is a modified
 * {@link RegulaFalsiSolver <em>Regula Falsi</em>} method.
 *
 * <p>Like the <em>Regula Falsi</em> method, convergence is guaranteed by
 * maintaining a bracketed solution. The <em>Illinois</em> method however,
 * should converge much faster than the original <em>Regula Falsi</em>
 * method. Furthermore, this implementation of the <em>Illinois</em> method
 * should not suffer from the same implementation issues as the <em>Regula
 * Falsi</em> method, which may fail to convergence in certain cases.</p>
 *
 * <p>The <em>Illinois</em> method assumes that the function is continuous,
 * but not necessarily smooth.</p>
 *
 * <p>Implementation based on the following article: M. Dowell and P. Jarratt,
 * <em>A modified regula falsi method for computing the root of an
 * equation</em>, BIT Numerical Mathematics, volume 11, number 2,
 * pages 168-174, Springer, 1971.</p>
 *
 * @since 3.0
 */
public class IllinoisSolver extends BaseSecantSolver {

    /** Construct a solver with default accuracy (1e-6). */
    public IllinoisSolver() {
        super(DEFAULT_ABSOLUTE_ACCURACY, Method.ILLINOIS);
    }

    /**
     * Construct a solver.
     *
     * @param absoluteAccuracy Absolute accuracy.
     */
    public IllinoisSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy, Method.ILLINOIS);
    }

    /**
     * Construct a solver.
     *
     * @param relativeAccuracy Relative accuracy.
     * @param absoluteAccuracy Absolute accuracy.
     */
    public IllinoisSolver(final double relativeAccuracy,
                          final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, Method.ILLINOIS);
    }

    /**
     * Construct a solver.
     *
     * @param relativeAccuracy Relative accuracy.
     * @param absoluteAccuracy Absolute accuracy.
     * @param functionValueAccuracy Maximum function value error.
     */
    public IllinoisSolver(final double relativeAccuracy,
                          final double absoluteAccuracy,
                          final double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy, Method.PEGASUS);
    }
}
