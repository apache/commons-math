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
package org.apache.commons.math4.fitting.leastsquares;

import org.apache.commons.math4.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math4.linear.ArrayRealVector;
import org.apache.commons.math4.linear.DecompositionSolver;
import org.apache.commons.math4.linear.QRDecomposition;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.linear.RealVector;
import org.apache.commons.math4.util.FastMath;

/**
 * An implementation of {@link Evaluation} that is designed for extension. All of the
 * methods implemented here use the methods that are left unimplemented.
 * <p/>
 * TODO cache results?
 *
 * @since 3.3
 */
public abstract class AbstractEvaluation implements Evaluation {

    /** number of observations */
    private final int observationSize;

    /**
     * Constructor.
     *
     * @param observationSize the number of observations.
     * Needed for {@link #getRMS()} and {@link #getReducedChiSquare()}.
     */
    AbstractEvaluation(final int observationSize) {
        this.observationSize = observationSize;
    }

    /** {@inheritDoc} */
    @Override
    public RealMatrix getCovariances(double threshold) {
        // Set up the Jacobian.
        final RealMatrix j = this.getJacobian();

        // Compute transpose(J)J.
        final RealMatrix jTj = j.transpose().multiply(j);

        // Compute the covariances matrix.
        final DecompositionSolver solver
                = new QRDecomposition(jTj, threshold).getSolver();
        return solver.getInverse();
    }

    /** {@inheritDoc} */
    @Override
    public RealVector getSigma(double covarianceSingularityThreshold) {
        final RealMatrix cov = this.getCovariances(covarianceSingularityThreshold);
        final int nC = cov.getColumnDimension();
        final RealVector sig = new ArrayRealVector(nC);
        for (int i = 0; i < nC; ++i) {
            sig.setEntry(i, FastMath.sqrt(cov.getEntry(i,i)));
        }
        return sig;
    }

    /** {@inheritDoc} */
    @Override
    public double getRMS() {
        return FastMath.sqrt(getReducedChiSquare(1));
    }

    /** {@inheritDoc} */
    @Override
    public double getCost() {
        return FastMath.sqrt(getChiSquare());
    }

    /** {@inheritDoc} */
    @Override
    public double getChiSquare() {
        final ArrayRealVector r = new ArrayRealVector(getResiduals());
        return r.dotProduct(r);
    }

    /** {@inheritDoc} */
    @Override
    public double getReducedChiSquare(int numberOfFittedParameters) {
        return getChiSquare() / (observationSize - numberOfFittedParameters + 1);
    }
}
