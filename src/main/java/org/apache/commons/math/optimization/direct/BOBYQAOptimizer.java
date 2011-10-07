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

package org.apache.commons.math.optimization.direct;

import java.util.Arrays;

import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.MathIllegalStateException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.MultivariateRealOptimizer;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.util.MathUtils;

/**
 * Powell's BOBYQA algorithm. This implementation is translated and
 * adapted from the Fortran version available
 * <a href="http://plato.asu.edu/ftp/other_software/bobyqa.zip">here</a>.
 * See <a href="http://www.optimization-online.org/DB_HTML/2010/05/2616.html">
 * this paper</a> for an introduction.
 * <br/>
 * BOBYQA is particularly well suited for high dimensional problems
 * where derivatives are not available. In most cases it outperforms the
 * {@link PowellOptimizer} significantly. Stochastic algorithms like
 * {@link CMAESOptimizer} succeed more often than BOBYQA, but are more
 * expensive. BOBYQA could also be considered as a replacement of any
 * derivative-based optimizer when the derivatives are approximated by
 * finite differences.
 *
 * @version $Id$
 * @since 3.0
 */
public class BOBYQAOptimizer
    extends BaseAbstractScalarOptimizer<MultivariateRealFunction>
    implements MultivariateRealOptimizer {
    private static final double ZERO = 0d;
    private static final double ONE = 1d;
    private static final double TWO = 2d;
    private static final double TEN = 10d;
    private static final double SIXTEEN = 16d;
    private static final double TWO_HUNDRED_FIFTY = 250d;
    private static final double MINUS_ONE = -ONE;
    private static final double HALF = ONE / 2;
    private static final double ONE_OVER_FOUR = ONE / 4;
    private static final double ONE_OVER_EIGHT = ONE / 8;
    private static final double ONE_OVER_TEN = ONE / 10;
    private static final double ONE_OVER_A_THOUSAND = ONE / 1000;

    /** Minimum dimension of the problem: {@value} */
    public static final int MINIMUM_PROBLEM_DIMENSION = 2;
    /** Default value for {@link #initialTrustRegionRadius}: {@value} . */
    public static final double DEFAULT_INITIAL_RADIUS = 10.0;
    /** Default value for {@link #stoppingTrustRegionRadius}: {@value} . */
    public static final double DEFAULT_STOPPING_RADIUS = 1E-8;

    /**
     * numberOfInterpolationPoints XXX
     */
    private final int numberOfInterpolationPoints;
    /**
     * initialTrustRegionRadius XXX
     */
    private double initialTrustRegionRadius;
    /**
     * stoppingTrustRegionRadius XXX
     */
    private final double stoppingTrustRegionRadius;
    /**
     * Lower bounds of the objective variables.
     * {@code null} means no bounds.
     * XXX Should probably be passed to the "optimize" method (overload not existing yet).
     */
    private double[] lowerBound;
    /**
     * Upper bounds of the objective variables.
     * {@code null} means no bounds.
     * XXX Should probably be passed to the "optimize" method (overload not existing yet).
     */
    private double[] upperBound;

    /** Goal type (minimize or maximize). */
    private boolean isMinimize;
    /**
     * Current best values for the variables to be optimized.
     * The vector will be changed in-place to contain the values of the least
     * calculated objective function values.
     */
    private ArrayRealVector currentBest;
    /** Differences between the upper and lower bounds. */
    private double[] boundDifference;
    /**
     * Index of the interpolation point at the trust region center.
     */
    private int trustRegionCenterInterpolationPointIndex;

    /**
     * @param numberOfInterpolationPoints Number of interpolation conditions.
     * For a problem of dimension {@code n}, its value must be in the interval
     * {@code [n+2, (n+1)(n+2)/2]}.
     * Choices that exceed {@code 2n+1} are not recommended.
     */
    public BOBYQAOptimizer(int numberOfInterpolationPoints) {
        this(numberOfInterpolationPoints, null, null);
    }

    /**
     * @param numberOfInterpolationPoints Number of interpolation conditions.
     * For a problem of dimension {@code n}, its value must be in the interval
     * {@code [n+2, (n+1)(n+2)/2]}.
     * Choices that exceed {@code 2n+1} are not recommended.
     * @param lowerBound Lower bounds (constraints) of the objective variables.
     * @param upperBound Upperer bounds (constraints) of the objective variables.
     */
    public BOBYQAOptimizer(int numberOfInterpolationPoints,
                           double[] lowerBound,
                           double[] upperBound) {
        this(numberOfInterpolationPoints,
             lowerBound,
             upperBound,
             DEFAULT_INITIAL_RADIUS,
             DEFAULT_STOPPING_RADIUS);
    }

    /**
     * @param numberOfInterpolationPoints Number of interpolation conditions.
     * For a problem of dimension {@code n}, its value must be in the interval
     * {@code [n+2, (n+1)(n+2)/2]}.
     * Choices that exceed {@code 2n+1} are not recommended.
     * @param lowerBound Lower bounds (constraints) of the objective variables.
     * @param upperBound Upperer bounds (constraints) of the objective variables.
     * @param initialTrustRegionRadius Initial trust region radius.
     * @param stoppingTrustRegionRadius Stopping trust region radius.
     */
    public BOBYQAOptimizer(int numberOfInterpolationPoints,
                           double[] lowerBound,
                           double[] upperBound,
                           double initialTrustRegionRadius,
                           double stoppingTrustRegionRadius) {
        this.lowerBound = lowerBound == null ? null : MathUtils.copyOf(lowerBound);
        this.upperBound = upperBound == null ? null : MathUtils.copyOf(upperBound);
        this.numberOfInterpolationPoints = numberOfInterpolationPoints;
        this.initialTrustRegionRadius = initialTrustRegionRadius;
        this.stoppingTrustRegionRadius = stoppingTrustRegionRadius;
    }

    /** {@inheritDoc} */
    @Override
    protected RealPointValuePair doOptimize() {
        // Validity checks.
        setup();

        isMinimize = (getGoalType() == GoalType.MINIMIZE);
        currentBest = new ArrayRealVector(getStartPoint());

        final double value = bobyqa();

        return new RealPointValuePair(currentBest.getDataRef(),
                                      isMinimize ? value : -value);
    }

    /**
     *     This subroutine seeks the least value of a function of many variables,
     *     by applying a trust region method that forms quadratic models by
     *     interpolation. There is usually some freedom in the interpolation
     *     conditions, which is taken up by minimizing the Frobenius norm of
     *     the change to the second derivative of the model, beginning with the
     *     zero matrix. The values of the variables are constrained by upper and
     *     lower bounds. The arguments of the subroutine are as follows.
     *
     *     N must be set to the number of variables and must be at least two.
     *     NPT is the number of interpolation conditions. Its value must be in
     *       the interval [N+2,(N+1)(N+2)/2]. Choices that exceed 2*N+1 are not
     *       recommended.
     *     Initial values of the variables must be set in X(1),X(2),...,X(N). They
     *       will be changed to the values that give the least calculated F.
     *     For I=1,2,...,N, XL(I) and XU(I) must provide the lower and upper
     *       bounds, respectively, on X(I). The construction of quadratic models
     *       requires XL(I) to be strictly less than XU(I) for each I. Further,
     *       the contribution to a model from changes to the I-th variable is
     *       damaged severely by rounding errors if XU(I)-XL(I) is too small.
     *     RHOBEG and RHOEND must be set to the initial and final values of a trust
     *       region radius, so both must be positive with RHOEND no greater than
     *       RHOBEG. Typically, RHOBEG should be about one tenth of the greatest
     *       expected change to a variable, while RHOEND should indicate the
     *       accuracy that is required in the final values of the variables. An
     *       error return occurs if any of the differences XU(I)-XL(I), I=1,...,N,
     *       is less than 2*RHOBEG.
     *     MAXFUN must be set to an upper bound on the number of calls of CALFUN.
     *     The array W will be used for working space. Its length must be at least
     *       (NPT+5)*(NPT+N)+3*N*(N+5)/2.
     * @return
     */
    private double bobyqa() {
        // System.out.println("bobyqa"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;

        final int np = n + 1;
        final int ndim = npt + n;

        // Partition the working space array, so that different parts of it can
        // be treated separately during the calculation of BOBYQB. The partition
        // requires the first (NPT+2)*(NPT+N)+3*N*(N+5)/2 elements of W plus the
        // space that is taken by the last array in the argument list of BOBYQB.

        final ArrayRealVector xbase = new ArrayRealVector(n);
        final Array2DRowRealMatrix xpt = new Array2DRowRealMatrix(npt, n);
        final ArrayRealVector fval = new ArrayRealVector(npt);
        final ArrayRealVector xopt = new ArrayRealVector(n);
        final ArrayRealVector gopt = new ArrayRealVector(n);
        final ArrayRealVector hq = new ArrayRealVector(n * np / 2);
        final ArrayRealVector pq = new ArrayRealVector(npt);
        final Array2DRowRealMatrix bmat = new Array2DRowRealMatrix(ndim, n);
        final Array2DRowRealMatrix zmat = new Array2DRowRealMatrix(npt, (npt - np));
        final ArrayRealVector sl = new ArrayRealVector(n);
        final ArrayRealVector su = new ArrayRealVector(n);
        final ArrayRealVector xnew = new ArrayRealVector(n);
        final ArrayRealVector xalt = new ArrayRealVector(n);
        final ArrayRealVector d__ = new ArrayRealVector(n);
        final ArrayRealVector vlag = new ArrayRealVector(ndim);

        // Return if there is insufficient space between the bounds. Modify the
        // initial X if necessary in order to avoid conflicts between the bounds
        // and the construction of the first quadratic model. The lower and upper
        // bounds on moves from the updated X are set now, in the ISL and ISU
        // partitions of W, in order to provide useful and exact information about
        // components of X that become within distance RHOBEG from their bounds.

        for (int j = 0; j < n; j++) {
            final double boundDiff = boundDifference[j];
            sl.setEntry(j, lowerBound[j] - currentBest.getEntry(j));
            su.setEntry(j, upperBound[j] - currentBest.getEntry(j));
            if (sl.getEntry(j) >= -initialTrustRegionRadius) {
                if (sl.getEntry(j) >= ZERO) {
                    currentBest.setEntry(j, lowerBound[j]);
                    sl.setEntry(j, ZERO);
                    su.setEntry(j, boundDiff);
                } else {
                    currentBest.setEntry(j, lowerBound[j] + initialTrustRegionRadius);
                    sl.setEntry(j, -initialTrustRegionRadius);
                    // Computing MAX
                    final double deltaOne = upperBound[j] - currentBest.getEntry(j);
                    su.setEntry(j, Math.max(deltaOne, initialTrustRegionRadius));
                }
            } else if (su.getEntry(j) <= initialTrustRegionRadius) {
                if (su.getEntry(j) <= ZERO) {
                    currentBest.setEntry(j, upperBound[j]);
                    sl.setEntry(j, -boundDiff);
                    su.setEntry(j, ZERO);
                } else {
                    currentBest.setEntry(j, upperBound[j] - initialTrustRegionRadius);
                    // Computing MIN
                    final double deltaOne = lowerBound[j] - currentBest.getEntry(j);
                    final double deltaTwo = -initialTrustRegionRadius;
                    sl.setEntry(j, Math.min(deltaOne, deltaTwo));
                    su.setEntry(j, initialTrustRegionRadius);
                }
            }
        }

        // Make the call of BOBYQB.

        return bobyqb(xbase,
                      xpt,
                      fval,
                      xopt,
                      gopt,
                      hq,
                      pq,
                      bmat,
                      zmat,
                      sl,
                      su,
                      xnew,
                      xalt,
                      d__,
                      vlag);
    } // bobyqa

    // ----------------------------------------------------------------------------------------

    /**
     *     The arguments N, NPT, X, XL, XU, RHOBEG, RHOEND, IPRINT and MAXFUN
     *       are identical to the corresponding arguments in SUBROUTINE BOBYQA.
     *     XBASE holds a shift of origin that should reduce the contributions
     *       from rounding errors to values of the model and Lagrange functions.
     *     XPT is a two-dimensional array that holds the coordinates of the
     *       interpolation points relative to XBASE.
     *     FVAL holds the values of F at the interpolation points.
     *     XOPT is set to the displacement from XBASE of the trust region centre.
     *     GOPT holds the gradient of the quadratic model at XBASE+XOPT.
     *     HQ holds the explicit second derivatives of the quadratic model.
     *     PQ contains the parameters of the implicit second derivatives of the
     *       quadratic model.
     *     BMAT holds the last N columns of H.
     *     ZMAT holds the factorization of the leading NPT by NPT submatrix of H,
     *       this factorization being ZMAT times ZMAT^T, which provides both the
     *       correct rank and positive semi-definiteness.
     *     NDIM is the first dimension of BMAT and has the value NPT+N.
     *     SL and SU hold the differences XL-XBASE and XU-XBASE, respectively.
     *       All the components of every XOPT are going to satisfy the bounds
     *       SL(I) .LEQ. XOPT(I) .LEQ. SU(I), with appropriate equalities when
     *       XOPT is on a constraint boundary.
     *     XNEW is chosen by SUBROUTINE TRSBOX or ALTMOV. Usually XBASE+XNEW is the
     *       vector of variables for the next call of CALFUN. XNEW also satisfies
     *       the SL and SU constraints in the way that has just been mentioned.
     *     XALT is an alternative to XNEW, chosen by ALTMOV, that may replace XNEW
     *       in order to increase the denominator in the updating of UPDATE.
     *     D is reserved for a trial step from XOPT, which is usually XNEW-XOPT.
     *     VLAG contains the values of the Lagrange functions at a new point X.
     *       They are part of a product that requires VLAG to be of length NDIM.
     *     W is a one-dimensional array that is used for working space. Its length
     *       must be at least 3*NDIM = 3*(NPT+N).
     *
     * @param xbase
     * @param xpt
     * @param fval
     * @param xopt
     * @param gopt
     * @param hq
     * @param pq
     * @param bmat
     * @param zmat
     * @param sl
     * @param su
     * @param xnew
     * @param xalt
     * @param d__
     * @param vlag
     * @return
     */
    private double bobyqb(
            ArrayRealVector xbase,
            Array2DRowRealMatrix xpt,
            ArrayRealVector fval,
            ArrayRealVector xopt,
            ArrayRealVector gopt,
            ArrayRealVector hq,
            ArrayRealVector pq,
            Array2DRowRealMatrix bmat,
            Array2DRowRealMatrix zmat,
            ArrayRealVector sl,
            ArrayRealVector su,
            ArrayRealVector xnew,
            ArrayRealVector xalt,
            ArrayRealVector d__,
            ArrayRealVector vlag
    ) {
        // System.out.println("bobyqb"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;
        final int np = n + 1;
        final int nptm = npt - np;
        final int nh = n * np / 2;

        final ArrayRealVector work1 = new ArrayRealVector(n);
        final ArrayRealVector work2 = new ArrayRealVector(npt);
        final ArrayRealVector work3 = new ArrayRealVector(npt);

        double cauchy = Double.NaN;
        double alpha = Double.NaN;
        double dsq = Double.NaN;
        double crvmin = Double.NaN;

        // System generated locals
        double d__1, d__2, d__3, d__4;

        // Local variables
        double f = 0;
        int ih, ip, jp;
        double dx;
        double den = 0, rho = 0, sum = 0, diff = 0, beta = 0, gisq = 0;
        int knew = 0;
        double temp, suma, sumb, bsum, fopt;
        double curv;
        int ksav;
        double gqsq = 0, dist = 0, sumw = 0, sumz = 0, diffa = 0, diffb = 0, diffc = 0, hdiag = 0;
        int kbase;
        double delta = 0, adelt = 0, denom = 0, fsave = 0, bdtol = 0, delsq = 0;
        int nfsav;
        double ratio = 0, dnorm = 0, vquad = 0, pqold = 0;
        int itest;
        double sumpq, scaden;
        double errbig, fracsq, biglsq, densav;
        double bdtest;
        double frhosq;
        double distsq = 0;
        int ntrits;

        // Set some constants.
        // Parameter adjustments

        // Function Body

        // The call of PRELIM sets the elements of XBASE, XPT, FVAL, GOPT, HQ, PQ,
        // BMAT and ZMAT for the first iteration, with the corresponding values of
        // of NF and KOPT, which are the number of calls of CALFUN so far and the
        // index of the interpolation point at the trust region centre. Then the
        // initial XOPT is set too. The branch to label 720 occurs if MAXFUN is
        // less than NPT. GOPT will be updated if KOPT is different from KBASE.

        trustRegionCenterInterpolationPointIndex = 0;

        prelim(currentBest, xbase,
               xpt, fval, gopt, hq, pq, bmat,
                zmat, sl, su);
        double xoptsq = ZERO;
        for (int i = 0; i < n; i++) {
            xopt.setEntry(i, xpt.getEntry(trustRegionCenterInterpolationPointIndex, i));
            // Computing 2nd power
            final double deltaOne = xopt.getEntry(i);
            xoptsq += deltaOne * deltaOne;
        }
        fsave = fval.getEntry(0);
        kbase = 0;

        // Complete the settings that are required for the iterative procedure.

        rho = initialTrustRegionRadius;
        delta = rho;
        ntrits = 0;
        diffa = ZERO;
        diffb = ZERO;
        itest = 0;
        nfsav = getEvaluations();

        // Update GOPT if necessary before the first iteration and after each
        // call of RESCUE that makes a call of CALFUN.

        int state = 20;
        for(;;) switch (state) {
        case 20: {
            if (trustRegionCenterInterpolationPointIndex != kbase) {
                ih = 0;
                for (int j = 0; j < n; j++) {
                    for (int i = 0; i <= j; i++) {
                        if (i < j) {
                            gopt.setEntry(j,  gopt.getEntry(j) + hq.getEntry(ih) * xopt.getEntry(i));
                        }
                        gopt.setEntry(i,  gopt.getEntry(i) + hq.getEntry(ih) * xopt.getEntry(j));
                        ih++;
                    }
                }
                if (getEvaluations() > npt) {
                    for (int k = 0; k < npt; k++) {
                        temp = ZERO;
                        for (int j = 0; j < n; j++) {
                            temp += xpt.getEntry(k, j) * xopt.getEntry(j);
                        }
                        temp = pq.getEntry(k) * temp;
                        for (int i = 0; i < n; i++) {
                            gopt.setEntry(i, gopt.getEntry(i) + temp * xpt.getEntry(k, i));
                        }
                    }
                    throw new PathIsExploredException(); // XXX
                }
            }

            // Generate the next point in the trust region that provides a small value
            // of the quadratic model subject to the constraints on the variables.
            // The int NTRITS is set to the number "trust region" iterations that
            // have occurred since the last "alternative" iteration. If the length
            // of XNEW-XOPT is less than HALF*RHO, however, then there is a branch to
            // label 650 or 680 with NTRITS=-1, instead of calculating F at XNEW.

        }
        case 60: {
            final ArrayRealVector gnew = new ArrayRealVector(n);
            final ArrayRealVector xbdi = new ArrayRealVector(n);
            final ArrayRealVector s = new ArrayRealVector(n);
            final ArrayRealVector hs = new ArrayRealVector(n);
            final ArrayRealVector hred = new ArrayRealVector(n);

            final double[] dsqCrvmin = trsbox(xpt, xopt, gopt, hq, pq, sl,
                                              su, delta, xnew, d__, gnew, xbdi, s,
                                              hs, hred);
            dsq = dsqCrvmin[0];
            crvmin = dsqCrvmin[1];

            // Computing MIN
            double deltaOne = delta;
            double deltaTwo = Math.sqrt(dsq);
            dnorm = Math.min(deltaOne, deltaTwo);
            if (dnorm < HALF * rho) {
                ntrits = -1;
                // Computing 2nd power
                deltaOne = TEN * rho;
                distsq = deltaOne * deltaOne;
                if (getEvaluations() <= nfsav + 2) {
                    state = 650; break;
                }

                // The following choice between labels 650 and 680 depends on whether or
                // not our work with the current RHO seems to be complete. Either RHO is
                // decreased or termination occurs if the errors in the quadratic model at
                // the last three interpolation points compare favourably with predictions
                // of likely improvements to the model within distance HALF*RHO of XOPT.

                // Computing MAX
                deltaOne = Math.max(diffa, diffb);
                errbig = Math.max(deltaOne, diffc);
                frhosq = rho * ONE_OVER_EIGHT * rho;
                if (crvmin > ZERO &&
                    errbig > frhosq * crvmin) {
                    state = 650; break;
                }
                bdtol = errbig / rho;
                for (int j = 0; j < n; j++) {
                    bdtest = bdtol;
                    if (xnew.getEntry(j) == sl.getEntry(j)) {
                        bdtest = work1.getEntry(j);
                    }
                    if (xnew.getEntry(j) == su.getEntry(j)) {
                        bdtest = -work1.getEntry(j);
                    }
                    if (bdtest < bdtol) {
                        curv = hq.getEntry((j + j * j) / 2);
                        for (int k = 0; k < npt; k++) {
                            // Computing 2nd power
                            final double d1 = xpt.getEntry(k, j);
                            curv += pq.getEntry(k) * (d1 * d1);
                        }
                        bdtest += HALF * curv * rho;
                        if (bdtest < bdtol) {
                            state = 650; break;
                        }
                        throw new PathIsExploredException(); // XXX
                    }
                }
                state = 680; break;
            }
            ++ntrits;

            // Severe cancellation is likely to occur if XOPT is too far from XBASE.
            // If the following test holds, then XBASE is shifted so that XOPT becomes
            // zero. The appropriate changes are made to BMAT and to the second
            // derivatives of the current model, beginning with the changes to BMAT
            // that do not depend on ZMAT. VLAG is used temporarily for working space.

        }
        case 90: {
            if (dsq <= xoptsq * ONE_OVER_A_THOUSAND) {
                fracsq = xoptsq * ONE_OVER_FOUR;
                sumpq = ZERO;
                // final RealVector sumVector
                //     = new ArrayRealVector(npt, -HALF * xoptsq).add(xpt.operate(xopt));
                for (int k = 0; k < npt; k++) {
                    sumpq += pq.getEntry(k);
                    sum = -HALF * xoptsq;
                    for (int i = 0; i < n; i++) {
                        sum += xpt.getEntry(k, i) * xopt.getEntry(i);
                    }
                    // sum = sumVector.getEntry(k); // XXX "testAckley" and "testDiffPow" fail.
                    work2.setEntry(k, sum);
                    temp = fracsq - HALF * sum;
                    for (int i = 0; i < n; i++) {
                        work1.setEntry(i, bmat.getEntry(k, i));
                        vlag.setEntry(i, sum * xpt.getEntry(k, i) + temp * xopt.getEntry(i));
                        ip = npt + i;
                        for (int j = 0; j <= i; j++) {
                            bmat.setEntry(ip, j,
                                          bmat.getEntry(ip, j)
                                          + work1.getEntry(i) * vlag.getEntry(j)
                                          + vlag.getEntry(i) * work1.getEntry(j));
                        }
                    }
                }

                // Then the revisions of BMAT that depend on ZMAT are calculated.

                for (int m = 0; m < nptm; m++) {
                    sumz = ZERO;
                    sumw = ZERO;
                    for (int k = 0; k < npt; k++) {
                        sumz += zmat.getEntry(k, m);
                        vlag.setEntry(k, work2.getEntry(k) * zmat.getEntry(k, m));
                        sumw += vlag.getEntry(k);
                    }
                    for (int j = 0; j < n; j++) {
                        sum = (fracsq * sumz - HALF * sumw) * xopt.getEntry(j);
                        for (int k = 0; k < npt; k++) {
                            sum += vlag.getEntry(k) * xpt.getEntry(k, j);
                        }
                        work1.setEntry(j, sum);
                        for (int k = 0; k < npt; k++) {
                            bmat.setEntry(k, j,
                                          bmat.getEntry(k, j)
                                          + sum * zmat.getEntry(k, m));
                        }
                    }
                    for (int i = 0; i < n; i++) {
                        ip = i + npt;
                        temp = work1.getEntry(i);
                        for (int j = 0; j <= i; j++) {
                            bmat.setEntry(ip, j,
                                          bmat.getEntry(ip, j)
                                          + temp * work1.getEntry(j));
                        }
                    }
                }

                // The following instructions complete the shift, including the changes
                // to the second derivative parameters of the quadratic model.

                ih = 0;
                for (int j = 0; j < n; j++) {
                    work1.setEntry(j, -HALF * sumpq * xopt.getEntry(j));
                    for (int k = 0; k < npt; k++) {
                        work1.setEntry(j, work1.getEntry(j) + pq.getEntry(k) * xpt.getEntry(k, j));
                        xpt.setEntry(k, j, xpt.getEntry(k, j) - xopt.getEntry(j));
                    }
                    for (int i = 0; i <= j; i++) {
                         hq.setEntry(ih,
                                    hq.getEntry(ih)
                                    + work1.getEntry(i) * xopt.getEntry(j)
                                    + xopt.getEntry(i) * work1.getEntry(j));
                        bmat.setEntry(npt + i, j, bmat.getEntry(npt + j, i));
                        ih++;
                    }
                }
                for (int i = 0; i < n; i++) {
                    xbase.setEntry(i, xbase.getEntry(i) + xopt.getEntry(i));
                    xnew.setEntry(i, xnew.getEntry(i) - xopt.getEntry(i));
                    sl.setEntry(i, sl.getEntry(i) - xopt.getEntry(i));
                    su.setEntry(i, su.getEntry(i) - xopt.getEntry(i));
                    xopt.setEntry(i, ZERO);
                }
                xoptsq = ZERO;
            }
            if (ntrits == 0) {
                state = 210; break;
            }
            state = 230; break;

            // XBASE is also moved to XOPT by a call of RESCUE. This calculation is
            // more expensive than the previous shift, because new matrices BMAT and
            // ZMAT are generated from scratch, which may include the replacement of
            // interpolation points whose positions seem to be causing near linear
            // dependence in the interpolation conditions. Therefore RESCUE is called
            // only if rounding errors have reduced by at least a factor of two the
            // denominator of the formula for updating the H matrix. It provides a
            // useful safeguard, but is not invoked in most applications of BOBYQA.

        }
        case 210: {
            // Pick two alternative vectors of variables, relative to XBASE, that
            // are suitable as new positions of the KNEW-th interpolation point.
            // Firstly, XNEW is set to the point on a line through XOPT and another
            // interpolation point that minimizes the predicted value of the next
            // denominator, subject to ||XNEW - XOPT|| .LEQ. ADELT and to the SL
            // and SU bounds. Secondly, XALT is set to the best feasible point on
            // a constrained version of the Cauchy step of the KNEW-th Lagrange
            // function, the corresponding value of the square of this function
            // being returned in CAUCHY. The choice between these alternatives is
            // going to be made when the denominator is calculated.

            final double[] alphaCauchy = altmov(xpt, xopt,
                                                bmat, zmat,
                                                sl, su, knew, adelt, xnew, xalt);
            alpha = alphaCauchy[0];
            cauchy = alphaCauchy[1];

            for (int i = 0; i < n; i++) {
                d__.setEntry(i, xnew.getEntry(i) - xopt.getEntry(i));
            }

            // Calculate VLAG and BETA for the current choice of D. The scalar
            // product of D with XPT(K,.) is going to be held in W(NPT+K) for
            // use when VQUAD is calculated.

        }
        case 230: {
            for (int k = 0; k < npt; k++) {
                suma = ZERO;
                sumb = ZERO;
                sum = ZERO;
                for (int j = 0; j < n; j++) {
                    suma += xpt.getEntry(k, j) * d__.getEntry(j);
                    sumb += xpt.getEntry(k, j) * xopt.getEntry(j);
                    sum += bmat.getEntry(k, j) * d__.getEntry(j);
                }
                work3.setEntry(k, suma * (HALF * suma + sumb));
                vlag.setEntry(k, sum);
                work2.setEntry(k, suma);
            }
            beta = ZERO;
            for (int m = 0; m < nptm; m++) {
                sum = ZERO;
                for (int k = 0; k < npt; k++) {
                    sum += zmat.getEntry(k, m) * work3.getEntry(k);
                }
                beta -= sum * sum;
                for (int k = 0; k < npt; k++) {
                    vlag.setEntry(k, vlag.getEntry(k) + sum * zmat.getEntry(k, m));
                }
            }
            dsq = ZERO;
            bsum = ZERO;
            dx = ZERO;
            for (int j = 0; j < n; j++) {
                // Computing 2nd power
                final double d1 = d__.getEntry(j);
                dsq += d1 * d1;
                sum = ZERO;
                for (int k = 0; k < npt; k++) {
                    sum += work3.getEntry(k) * bmat.getEntry(k, j);
                }
                bsum += sum * d__.getEntry(j);
                jp = npt + j;
                for (int i = 0; i < n; i++) {
                    sum += bmat.getEntry(jp, i) * d__.getEntry(i);
                }
                vlag.setEntry(jp, sum);
                bsum += sum * d__.getEntry(j);
                dx += d__.getEntry(j) * xopt.getEntry(j);
            }
            beta = dx * dx + dsq * (xoptsq + dx + dx + HALF * dsq) + beta - bsum;
            vlag.setEntry(trustRegionCenterInterpolationPointIndex, vlag.getEntry(trustRegionCenterInterpolationPointIndex) + ONE);

            // If NTRITS is zero, the denominator may be increased by replacing
            // the step D of ALTMOV by a Cauchy step. Then RESCUE may be called if
            // rounding errors have damaged the chosen denominator.

            if (ntrits == 0) {
                // Computing 2nd power
                final double d1 = vlag.getEntry(knew);
                denom = d1 * d1 + alpha * beta;
                if (denom < cauchy && cauchy > ZERO) {
                    for (int i = 0; i < n; i++) {
                        xnew.setEntry(i, xalt.getEntry(i));
                        d__.setEntry(i, xnew.getEntry(i) - xopt.getEntry(i));
                    }
                    cauchy = ZERO; // XXX Useful statement?
                    state = 230; break;
                }
                // Alternatively, if NTRITS is positive, then set KNEW to the index of
                // the next interpolation point to be deleted to make room for a trust
                // region step. Again RESCUE may be called if rounding errors have damaged_
                // the chosen denominator, which is the reason for attempting to select
                // KNEW before calculating the next value of the objective function.

            } else {
                delsq = delta * delta;
                scaden = ZERO;
                biglsq = ZERO;
                knew = 0;
                for (int k = 0; k < npt; k++) {
                    if (k == trustRegionCenterInterpolationPointIndex) {
                        continue;
                    }
                    hdiag = ZERO;
                    for (int m = 0; m < nptm; m++) {
                        // Computing 2nd power
                        final double d1 = zmat.getEntry(k, m);
                        hdiag += d1 * d1;
                    }
                    // Computing 2nd power
                    d__1 = vlag.getEntry(k);
                    den = beta * hdiag + d__1 * d__1;
                    distsq = ZERO;
                    for (int j = 0; j < n; j++) {
                        // Computing 2nd power
                        final double d1 = xpt.getEntry(k, j) - xopt.getEntry(j);
                        distsq += d1 * d1;
                    }
                    // Computing MAX
                    // Computing 2nd power
                    d__3 = distsq / delsq;
                    d__1 = ONE;
                    d__2 = d__3 * d__3;
                    temp = Math.max(d__1,d__2);
                    if (temp * den > scaden) {
                        scaden = temp * den;
                        knew = k;
                        denom = den;
                    }
                    // Computing MAX
                    // Computing 2nd power
                    d__3 = vlag.getEntry(k);
                    d__1 = biglsq;
                    d__2 = temp * (d__3 * d__3);
                    biglsq = Math.max(d__1, d__2);
                }
            }

            // Put the variables for the next calculation of the objective function
            //   in XNEW, with any adjustments for the bounds.

            // Calculate the value of the objective function at XBASE+XNEW, unless
            //   the limit on the number of calculations of F has been reached.

        }
        case 360: {
            for (int i = 0; i < n; i++) {
                // Computing MIN
                // Computing MAX
                d__3 = lowerBound[i];
                d__4 = xbase.getEntry(i) + xnew.getEntry(i);
                d__1 = Math.max(d__3, d__4);
                d__2 = upperBound[i];
                currentBest.setEntry(i, Math.min(d__1, d__2));
                if (xnew.getEntry(i) == sl.getEntry(i)) {
                    currentBest.setEntry(i, lowerBound[i]);
                }
                if (xnew.getEntry(i) == su.getEntry(i)) {
                    currentBest.setEntry(i, upperBound[i]);
                }
            }

            f = computeObjectiveValue(currentBest.toArray());

            if (!isMinimize)
                f = -f;
            if (ntrits == -1) {
                fsave = f;
                state = 720; break;
            }

            // Use the quadratic model to predict the change in F due to the step D,
            //   and set DIFF to the error of this prediction.

            fopt = fval.getEntry(trustRegionCenterInterpolationPointIndex);
            vquad = ZERO;
            ih = 0;
            for (int j = 0; j < n; j++) {
                vquad += d__.getEntry(j) * gopt.getEntry(j);
                for (int i = 0; i <= j; i++) {
                     temp = d__.getEntry(i) * d__.getEntry(j);
                    if (i == j) {
                        temp = HALF * temp;
                    }
                    vquad += hq.getEntry(ih) * temp;
                    ih++;
               }
            }
            for (int k = 0; k < npt; k++) {
                // Computing 2nd power
                final double d1 = work2.getEntry(k);
                final double d2 = d1 * d1; // "d1" must be squared first to prevent test failures.
                vquad += HALF * pq.getEntry(k) * d2;
            }
            diff = f - fopt - vquad;
            diffc = diffb;
            diffb = diffa;
            diffa = Math.abs(diff);
            if (dnorm > rho) {
                nfsav = getEvaluations();
            }

            // Pick the next value of DELTA after a trust region step.

            if (ntrits > 0) {
                if (vquad >= ZERO) {
                    throw new MathIllegalStateException(LocalizedFormats.TRUST_REGION_STEP_FAILED, vquad);
                }
                ratio = (f - fopt) / vquad;
                if (ratio <= ONE_OVER_TEN) {
                    // Computing MIN
                    d__1 = HALF * delta;
                    delta = Math.min(d__1,dnorm);
                } else if (ratio <= .7) {
                    // Computing MAX
                    d__1 = HALF * delta;
                    delta = Math.max(d__1,dnorm);
                } else {
                    // Computing MAX
                    d__1 = HALF * delta;
                    d__2 = dnorm + dnorm;
                    delta = Math.max(d__1,d__2);
                }
                if (delta <= rho * 1.5) {
                    delta = rho;
                }

                // Recalculate KNEW and DENOM if the new F is less than FOPT.

                if (f < fopt) {
                    ksav = knew;
                    densav = denom;
                    delsq = delta * delta;
                    scaden = ZERO;
                    biglsq = ZERO;
                    knew = 0;
                    for (int k = 0; k < npt; k++) {
                        hdiag = ZERO;
                        for (int m = 0; m < nptm; m++) {
                            // Computing 2nd power
                            final double d1 = zmat.getEntry(k, m);
                            hdiag += d1 * d1;
                        }
                        // Computing 2nd power
                        d__1 = vlag.getEntry(k);
                        den = beta * hdiag + d__1 * d__1;
                        distsq = ZERO;
                        for (int j = 0; j < n; j++) {
                            // Computing 2nd power
                            final double d1 = xpt.getEntry(k, j) - xnew.getEntry(j);
                            distsq += d1 * d1;
                        }
                        // Computing MAX
                        // Computing 2nd power
                        d__3 = distsq / delsq;
                        d__1 = ONE;
                        d__2 = d__3 * d__3;
                        temp = Math.max(d__1, d__2);
                        if (temp * den > scaden) {
                            scaden = temp * den;
                            knew = k;
                            denom = den;
                        }
                        // Computing MAX
                        // Computing 2nd power
                        d__3 = vlag.getEntry(k);
                        d__1 = biglsq;
                        d__2 = temp * (d__3 * d__3);
                        biglsq = Math.max(d__1, d__2);
                    }
                    if (scaden <= HALF * biglsq) {
                        knew = ksav;
                        denom = densav;
                    }
                }
            }

            // Update BMAT and ZMAT, so that the KNEW-th interpolation point can be
            // moved. Also update the second derivative terms of the model.

            update(bmat, zmat, vlag,
                    beta, denom, knew);

            ih = 0;
            pqold = pq.getEntry(knew);
            pq.setEntry(knew, ZERO);
            for (int i = 0; i < n; i++) {
                temp = pqold * xpt.getEntry(knew, i);
                for (int j = 0; j <= i; j++) {
                    hq.setEntry(ih, hq.getEntry(ih) + temp * xpt.getEntry(knew, j));
                    ih++;
                }
            }
            for (int m = 0; m < nptm; m++) {
                temp = diff * zmat.getEntry(knew, m);
                for (int k = 0; k < npt; k++) {
                    pq.setEntry(k, pq.getEntry(k) + temp * zmat.getEntry(k, m));
                }
            }

            // Include the new interpolation point, and make the changes to GOPT at
            // the old XOPT that are caused by the updating of the quadratic model.

            fval.setEntry(knew,  f);
            for (int i = 0; i < n; i++) {
                xpt.setEntry(knew, i, xnew.getEntry(i));
                work1.setEntry(i, bmat.getEntry(knew, i));
            }
            for (int k = 0; k < npt; k++) {
                suma = ZERO;
                for (int m = 0; m < nptm; m++) {
                    suma += zmat.getEntry(knew, m) * zmat.getEntry(k, m);
                }
                sumb = ZERO;
                for (int j = 0; j < n; j++) {
                    sumb += xpt.getEntry(k, j) * xopt.getEntry(j);
                }
                temp = suma * sumb;
                for (int i = 0; i < n; i++) {
                    work1.setEntry(i, work1.getEntry(i) + temp * xpt.getEntry(k, i));
                }
            }
            for (int i = 0; i < n; i++) {
                gopt.setEntry(i, gopt.getEntry(i) + diff * work1.getEntry(i));
            }

            // Update XOPT, GOPT and KOPT if the new calculated F is less than FOPT.

            if (f < fopt) {
                trustRegionCenterInterpolationPointIndex = knew;
                xoptsq = ZERO;
                ih = 0;
                for (int j = 0; j < n; j++) {
                    xopt.setEntry(j, xnew.getEntry(j));
                    // Computing 2nd power
                    final double d1 = xopt.getEntry(j);
                    xoptsq += d1 * d1;
                    for (int i = 0; i <= j; i++) {
                        if (i < j) {
                            gopt.setEntry(j, gopt.getEntry(j) + hq.getEntry(ih) * d__.getEntry(i));
                        }
                        gopt.setEntry(i, gopt.getEntry(i) + hq.getEntry(ih) * d__.getEntry(j));
                        ih++;
                    }
                }
                for (int k = 0; k < npt; k++) {
                    temp = ZERO;
                    for (int j = 0; j < n; j++) {
                        temp += xpt.getEntry(k, j) * d__.getEntry(j);
                    }
                    temp = pq.getEntry(k) * temp;
                    for (int i = 0; i < n; i++) {
                        gopt.setEntry(i, gopt.getEntry(i) + temp * xpt.getEntry(k, i));
                    }
                }
            }

            // Calculate the parameters of the least Frobenius norm interpolant to
            // the current data, the gradient of this interpolant at XOPT being put
            // into VLAG(NPT+I), I=1,2,...,N.

            if (ntrits > 0) {
                for (int k = 0; k < npt; k++) {
                    vlag.setEntry(k, fval.getEntry(k) - fval.getEntry(trustRegionCenterInterpolationPointIndex));
                    work3.setEntry(k, ZERO);
                }
                for (int j = 0; j < nptm; j++) {
                    sum = ZERO;
                    for (int k = 0; k < npt; k++) {
                        sum += zmat.getEntry(k, j) * vlag.getEntry(k);
                    }
                    for (int k = 0; k < npt; k++) {
                        work3.setEntry(k, work3.getEntry(k) + sum * zmat.getEntry(k, j));
                    }
                }
                for (int k = 0; k < npt; k++) {
                    sum = ZERO;
                    for (int j = 0; j < n; j++) {
                        sum += xpt.getEntry(k, j) * xopt.getEntry(j);
                    }
                    work2.setEntry(k, work3.getEntry(k));
                    work3.setEntry(k, sum * work3.getEntry(k));
                }
                gqsq = ZERO;
                gisq = ZERO;
                for (int i = 0; i < n; i++) {
                    sum = ZERO;
                    for (int k = 0; k < npt; k++) {
                        sum += bmat.getEntry(k, i) *
                            vlag.getEntry(k) + xpt.getEntry(k, i) * work3.getEntry(k);
                    }
                    if (xopt.getEntry(i) == sl.getEntry(i)) {
                        // Computing MIN
                        d__2 = ZERO;
                        d__3 = gopt.getEntry(i);
                        // Computing 2nd power
                        d__1 = Math.min(d__2, d__3);
                        gqsq += d__1 * d__1;
                        // Computing 2nd power
                        d__1 = Math.min(ZERO, sum);
                        gisq += d__1 * d__1;
                    } else if (xopt.getEntry(i) == su.getEntry(i)) {
                        // Computing MAX
                        d__2 = ZERO;
                        d__3 = gopt.getEntry(i);
                        // Computing 2nd power
                        d__1 = Math.max(d__2, d__3);
                        gqsq += d__1 * d__1;
                        // Computing 2nd power
                        d__1 = Math.max(ZERO, sum);
                        gisq += d__1 * d__1;
                    } else {
                        // Computing 2nd power
                        d__1 = gopt.getEntry(i);
                        gqsq += d__1 * d__1;
                        gisq += sum * sum;
                    }
                    vlag.setEntry(npt + i, sum);
                }

                // Test whether to replace the new quadratic model by the least Frobenius
                // norm interpolant, making the replacement if the test is satisfied.

                ++itest;
                if (gqsq < TEN * gisq) {
                    itest = 0;
                }
                if (itest >= 3) {
                    for (int i = 0, max = Math.max(npt, nh); i < max; i++) {
                        if (i < n) {
                            gopt.setEntry(i, vlag.getEntry(npt + i));
                        }
                        if (i < npt) {
                            pq.setEntry(i, work2.getEntry(i));
                        }
                        if (i < nh) {
                            hq.setEntry(i, ZERO);
                        }
                        itest = 0;
                    }
                }
            }

            // If a trust region step has provided a sufficient decrease in F, then
            // branch for another trust region calculation. The case NTRITS=0 occurs
            // when the new interpolation point was reached by an alternative step.

            if (ntrits == 0) {
                state = 60; break;
            }
            if (f <= fopt + ONE_OVER_TEN * vquad) {
                state = 60; break;
            }

            // Alternatively, find out if the interpolation points are close enough
            //   to the best point so far.

            // Computing MAX
            // Computing 2nd power
            d__3 = TWO * delta;
            // Computing 2nd power
            d__4 = TEN * rho;
            d__1 = d__3 * d__3;
            d__2 = d__4 * d__4;
            distsq = Math.max(d__1, d__2);
        }
        case 650: {
            knew = -1;
            for (int k = 0; k < npt; k++) {
                sum = ZERO;
                for (int j = 0; j < n; j++) {
                    // Computing 2nd power
                    final double d1 = xpt.getEntry(k, j) - xopt.getEntry(j);
                    sum += d1 * d1;
                }
                if (sum > distsq) {
                    knew = k;
                    distsq = sum;
                }
            }

            // If KNEW is positive, then ALTMOV finds alternative new positions for
            // the KNEW-th interpolation point within distance ADELT of XOPT. It is
            // reached via label 90. Otherwise, there is a branch to label 60 for
            // another trust region iteration, unless the calculations with the
            // current RHO are complete.

            if (knew >= 0) {
                dist = Math.sqrt(distsq);
                if (ntrits == -1) {
                    // Computing MIN
                    d__1 = ONE_OVER_TEN * delta;
                    d__2 = HALF * dist;
                    delta = Math.min(d__1,d__2);
                    if (delta <= rho * 1.5) {
                        delta = rho;
                    }
                }
                ntrits = 0;
                // Computing MAX
                // Computing MIN
                d__2 = ONE_OVER_TEN * dist;
                d__1 = Math.min(d__2, delta);
                adelt = Math.max(d__1, rho);
                dsq = adelt * adelt;
                state = 90; break;
            }
            if (ntrits == -1) {
                state = 680; break;
            }
            if (ratio > ZERO) {
                state = 60; break;
            }
            if (Math.max(delta, dnorm) > rho) {
                state = 60; break;
            }

            // The calculations with the current value of RHO are complete. Pick the
            //   next values of RHO and DELTA.
        }
        case 680: {
            if (rho > stoppingTrustRegionRadius) {
                delta = HALF * rho;
                ratio = rho / stoppingTrustRegionRadius;
                if (ratio <= SIXTEEN) {
                    rho = stoppingTrustRegionRadius;
                } else if (ratio <= TWO_HUNDRED_FIFTY) {
                    rho = Math.sqrt(ratio) * stoppingTrustRegionRadius;
                } else {
                    rho = ONE_OVER_TEN * rho;
                }
                delta = Math.max(delta, rho);
                ntrits = 0;
                nfsav = getEvaluations();
                state = 60; break;
            }

            // Return from the calculation, after another Newton-Raphson step, if
            //   it is too short to have been tried before.

            if (ntrits == -1) {
                state = 360; break;
            }
        }
        case 720: {
            if (fval.getEntry(trustRegionCenterInterpolationPointIndex) <= fsave) {
                for (int i = 0; i < n; i++) {
                    // Computing MIN
                    // Computing MAX
                    d__3 = lowerBound[i];
                    d__4 = xbase.getEntry(i) + xopt.getEntry(i);
                    d__1 = Math.max(d__3, d__4);
                    d__2 = upperBound[i];
                    currentBest.setEntry(i, Math.min(d__1, d__2));
                    if (xopt.getEntry(i) == sl.getEntry(i)) {
                        currentBest.setEntry(i, lowerBound[i]);
                    }
                    if (xopt.getEntry(i) == su.getEntry(i)) {
                        currentBest.setEntry(i, upperBound[i]);
                    }
                }
                f = fval.getEntry(trustRegionCenterInterpolationPointIndex);
            }
            return f;
        }
        default: {
            throw new MathIllegalStateException(LocalizedFormats.SIMPLE_MESSAGE, "bobyqb");
        }}
    } // bobyqb

    // ----------------------------------------------------------------------------------------

    /**
     *     The arguments N, NPT, XPT, XOPT, BMAT, ZMAT, NDIM, SL and SU all have
     *       the same meanings as the corresponding arguments of BOBYQB.
     *     KOPT is the index of the optimal interpolation point.
     *     KNEW is the index of the interpolation point that is going to be moved.
     *     ADELT is the current trust region bound.
     *     XNEW will be set to a suitable new position for the interpolation point
     *       XPT(KNEW,.). Specifically, it satisfies the SL, SU and trust region
     *       bounds and it should provide a large denominator in the next call of
     *       UPDATE. The step XNEW-XOPT from XOPT is restricted to moves along the
     *       straight lines through XOPT and another interpolation point.
     *     XALT also provides a large value of the modulus of the KNEW-th Lagrange
     *       function subject to the constraints that have been mentioned, its main
     *       difference from XNEW being that XALT-XOPT is a constrained version of
     *       the Cauchy step within the trust region. An exception is that XALT is
     *       not calculated if all components of GLAG (see below) are zero.
     *     ALPHA will be set to the KNEW-th diagonal element of the H matrix.
     *     CAUCHY will be set to the square of the KNEW-th Lagrange function at
     *       the step XALT-XOPT from XOPT for the vector XALT that is returned,
     *       except that CAUCHY is set to zero if XALT is not calculated.
     *     GLAG is a working space vector of length N for the gradient of the
     *       KNEW-th Lagrange function at XOPT.
     *     HCOL is a working space vector of length NPT for the second derivative
     *       coefficients of the KNEW-th Lagrange function.
     *     W is a working space vector of length 2N that is going to hold the
     *       constrained Cauchy step from XOPT of the Lagrange function, followed
     *       by the downhill version of XALT when the uphill step is calculated.
     *
     *     Set the first NPT components of W to the leading elements of the
     *     KNEW-th column of the H matrix.
     * @param xpt
     * @param xopt
     * @param bmat
     * @param zmat
     * @param sl
     * @param su
     * @param knew
     * @param adelt
     * @param xnew
     * @param xalt
     */
    private double[] altmov(
            Array2DRowRealMatrix xpt,
            ArrayRealVector xopt,
            Array2DRowRealMatrix bmat,
            Array2DRowRealMatrix zmat,
            ArrayRealVector sl,
            ArrayRealVector su,
            int knew,
            double adelt,
            ArrayRealVector xnew,
            ArrayRealVector xalt
    ) {
        // System.out.println("altmov"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;

        final ArrayRealVector glag = new ArrayRealVector(n);
        final ArrayRealVector hcol = new ArrayRealVector(npt);

        final ArrayRealVector work1 = new ArrayRealVector(n);
        final ArrayRealVector work2 = new ArrayRealVector(n);

        for (int k = 0; k < npt; k++) {
            hcol.setEntry(k, ZERO);
        }
        for (int j = 0, max = npt - n - 1; j < max; j++) {
            final double tmp = zmat.getEntry(knew, j);
            for (int k = 0; k < npt; k++) {
                hcol.setEntry(k, hcol.getEntry(k) + tmp * zmat.getEntry(k, j));
            }
        }
        final double alpha = hcol.getEntry(knew);
        final double ha = HALF * alpha;

        // Calculate the gradient of the KNEW-th Lagrange function at XOPT.

        for (int i = 0; i < n; i++) {
            glag.setEntry(i, bmat.getEntry(knew, i));
        }
        for (int k = 0; k < npt; k++) {
            double tmp = ZERO;
            for (int j = 0; j < n; j++) {
                tmp += xpt.getEntry(k, j) * xopt.getEntry(j);
            }
            tmp *= hcol.getEntry(k);
            for (int i = 0; i < n; i++) {
                glag.setEntry(i, glag.getEntry(i) + tmp * xpt.getEntry(k, i));
            }
        }

        // Search for a large denominator along the straight lines through XOPT
        // and another interpolation point. SLBD and SUBD will be lower and upper
        // bounds on the step along each of these lines in turn. PREDSQ will be
        // set to the square of the predicted denominator for each line. PRESAV
        // will be set to the largest admissible value of PREDSQ that occurs.

        double presav = ZERO;
        double step = Double.NaN;
        int ksav = 0;
        int ibdsav = 0;
        double stpsav = 0;
        for (int k = 0; k < npt; k++) {
            if (k == trustRegionCenterInterpolationPointIndex) {
                continue;
            }
            double dderiv = ZERO;
            double distsq = ZERO;
            for (int i = 0; i < n; i++) {
                final double tmp = xpt.getEntry(k, i) - xopt.getEntry(i);
                dderiv += glag.getEntry(i) * tmp;
                distsq += tmp * tmp;
            }
            double subd = adelt / Math.sqrt(distsq);
            double slbd = -subd;
            int ilbd = 0;
            int iubd = 0;
            final double sumin = Math.min(ONE, subd);

            // Revise SLBD and SUBD if necessary because of the bounds in SL and SU.

            for (int i = 0; i < n; i++) {
                final double tmp = xpt.getEntry(k, i) - xopt.getEntry(i);
                if (tmp > ZERO) {
                    if (slbd * tmp < sl.getEntry(i) - xopt.getEntry(i)) {
                        slbd = (sl.getEntry(i) - xopt.getEntry(i)) / tmp;
                        ilbd = -i - 1;
                    }
                    if (subd * tmp > su.getEntry(i) - xopt.getEntry(i)) {
                        // Computing MAX
                        subd = Math.max(sumin,
                                        (su.getEntry(i) - xopt.getEntry(i)) / tmp);
                        iubd = i + 1;
                    }
                } else if (tmp < ZERO) {
                    if (slbd * tmp > su.getEntry(i) - xopt.getEntry(i)) {
                        slbd = (su.getEntry(i) - xopt.getEntry(i)) / tmp;
                        ilbd = i + 1;
                    }
                    if (subd * tmp < sl.getEntry(i) - xopt.getEntry(i)) {
                        // Computing MAX
                        subd = Math.max(sumin,
                                        (sl.getEntry(i) - xopt.getEntry(i)) / tmp);
                        iubd = -i - 1;
                    }
                }
            }

            // Seek a large modulus of the KNEW-th Lagrange function when the index
            // of the other interpolation point on the line through XOPT is KNEW.

            step = slbd;
            int isbd = ilbd;
            double vlag = Double.NaN;
            if (k == knew) {
                final double diff = dderiv - ONE;
                vlag = slbd * (dderiv - slbd * diff);
                final double d1 = subd * (dderiv - subd * diff);
                if (Math.abs(d1) > Math.abs(vlag)) {
                    step = subd;
                    vlag = d1;
                    isbd = iubd;
                }
                final double d2 = HALF * dderiv;
                final double d3 = d2 - diff * slbd;
                final double d4 = d2 - diff * subd;
                if (d3 * d4 < ZERO) {
                    final double d5 = d2 * d2 / diff;
                    if (Math.abs(d5) > Math.abs(vlag)) {
                        step = d2 / diff;
                        vlag = d5;
                        isbd = 0;
                    }
                }

                // Search along each of the other lines through XOPT and another point.

            } else {
                vlag = slbd * (ONE - slbd);
                final double tmp = subd * (ONE - subd);
                if (Math.abs(tmp) > Math.abs(vlag)) {
                    step = subd;
                    vlag = tmp;
                    isbd = iubd;
                }
                if (subd > HALF) {
                    if (Math.abs(vlag) < ONE_OVER_FOUR) {
                        step = HALF;
                        vlag = ONE_OVER_FOUR;
                        isbd = 0;
                    }
                }
                vlag *= dderiv;
            }

            // Calculate PREDSQ for the current line search and maintain PRESAV.

            final double tmp = step * (ONE - step) * distsq;
            final double predsq = vlag * vlag * (vlag * vlag + ha * tmp * tmp);
            if (predsq > presav) {
                presav = predsq;
                ksav = k;
                stpsav = step;
                ibdsav = isbd;
            }
        }

        // Construct XNEW in a way that satisfies the bound constraints exactly.

        for (int i = 0; i < n; i++) {
            final double tmp = xopt.getEntry(i) + stpsav * (xpt.getEntry(ksav, i) - xopt.getEntry(i));
            xnew.setEntry(i, Math.max(sl.getEntry(i),
                                      Math.min(su.getEntry(i), tmp)));
        }
        if (ibdsav < 0) {
            xnew.setEntry(-ibdsav - 1, sl.getEntry(-ibdsav - 1));
        }
        if (ibdsav > 0) {
            xnew.setEntry(ibdsav - 1, su.getEntry(ibdsav - 1));
        }

        // Prepare for the iterative method that assembles the constrained Cauchy
        // step in W. The sum of squares of the fixed components of W is formed in
        // WFIXSQ, and the free components of W are set to BIGSTP.

        final double bigstp = adelt + adelt;
        int iflag = 0;
        double cauchy = Double.NaN;
        double csave = ZERO;
        while (true) {
            double wfixsq = ZERO;
            double ggfree = ZERO;
            for (int i = 0; i < n; i++) {
                final double glagValue = glag.getEntry(i);
                work1.setEntry(i, ZERO);
                if (Math.min(xopt.getEntry(i) - sl.getEntry(i), glagValue) > ZERO ||
                    Math.max(xopt.getEntry(i) - su.getEntry(i), glagValue) < ZERO) {
                    work1.setEntry(i, bigstp);
                    // Computing 2nd power
                    ggfree += glagValue * glagValue;
                }
            }
            if (ggfree == ZERO) {
                return new double[] { alpha, ZERO };
            }

            // Investigate whether more components of W can be fixed.
            L120: {
                final double tmp = adelt * adelt - wfixsq;
                if (tmp > ZERO) {
                    final double wsqsav = wfixsq;
                    step = Math.sqrt(tmp / ggfree);
                    ggfree = ZERO;
                    for (int i = 0; i < n; i++) {
                        if (work1.getEntry(i) == bigstp) {
                            final double tmp2 = xopt.getEntry(i) - step * glag.getEntry(i);
                            if (tmp2 <= sl.getEntry(i)) {
                                work1.setEntry(i, sl.getEntry(i) - xopt.getEntry(i));
                                // Computing 2nd power
                                final double d1 = work1.getEntry(i);
                                wfixsq += d1 * d1;
                            } else if (tmp2 >= su.getEntry(i)) {
                                work1.setEntry(i, su.getEntry(i) - xopt.getEntry(i));
                                // Computing 2nd power
                                final double d1 = work1.getEntry(i);
                                wfixsq += d1 * d1;
                            } else {
                                // Computing 2nd power
                                final double d1 = glag.getEntry(i);
                                ggfree += d1 * d1;
                            }
                        }
                    }
                    if (!(wfixsq > wsqsav &&
                          ggfree > ZERO)) {
                        break L120;
                    }
                }
            } // end L120

            // Set the remaining free components of W and all components of XALT,
            // except that W may be scaled later.

            double gw = ZERO;
            for (int i = 0; i < n; i++) {
                final double glagValue = glag.getEntry(i);
                if (work1.getEntry(i) == bigstp) {
                    work1.setEntry(i, -step * glagValue);
                    final double min = Math.min(su.getEntry(i),
                                                xopt.getEntry(i) + work1.getEntry(i));
                    xalt.setEntry(i, Math.max(sl.getEntry(i), min));
                } else if (work1.getEntry(i) == ZERO) {
                    xalt.setEntry(i, xopt.getEntry(i));
                } else if (glagValue > ZERO) {
                    xalt.setEntry(i, sl.getEntry(i));
                } else {
                    xalt.setEntry(i, su.getEntry(i));
                }
                gw += glagValue * work1.getEntry(i);
            }

            // Set CURV to the curvature of the KNEW-th Lagrange function along W.
            // Scale W by a factor less than one if that can reduce the modulus of
            // the Lagrange function at XOPT+W. Set CAUCHY to the final value of
            // the square of this function.

            double curv = ZERO;
            for (int k = 0; k < npt; k++) {
                double tmp = ZERO;
                for (int j = 0; j < n; j++) {
                    tmp += xpt.getEntry(k, j) * work1.getEntry(j);
                }
                curv += hcol.getEntry(k) * tmp * tmp;
            }
            if (iflag == 1) {
                curv = -curv;
            }
            if (curv > -gw &&
                curv < -gw * (ONE + Math.sqrt(TWO))) {
                final double scale = -gw / curv;
                for (int i = 0; i < n; i++) {
                    final double tmp = xopt.getEntry(i) + scale * work1.getEntry(i);
                    xalt.setEntry(i, Math.max(sl.getEntry(i),
                                              Math.min(su.getEntry(i), tmp)));
                }
                // Computing 2nd power
                final double d1 = HALF * gw * scale;
                cauchy = d1 * d1;
            } else {
                // Computing 2nd power
                final double d1 = gw + HALF * curv;
                cauchy = d1 * d1;
            }

            // If IFLAG is zero, then XALT is calculated as before after reversing
            // the sign of GLAG. Thus two XALT vectors become available. The one that
            // is chosen is the one that gives the larger value of CAUCHY.

            if (iflag == 0) {
                for (int i = 0; i < n; i++) {
                    glag.setEntry(i, -glag.getEntry(i));
                    work2.setEntry(i, xalt.getEntry(i));
                }
                csave = cauchy;
                iflag = 1;
            } else {
                break;
            }
        }
        if (csave > cauchy) {
            for (int i = 0; i < n; i++) {
                xalt.setEntry(i, work2.getEntry(i));
            }
            cauchy = csave;
        }

        return new double[] { alpha, cauchy };
    } // altmov

    // ----------------------------------------------------------------------------------------

    /**
     *     SUBROUTINE PRELIM sets the elements of XBASE, XPT, FVAL, GOPT, HQ, PQ,
     *     BMAT and ZMAT for the first iteration, and it maintains the values of
     *     NF and KOPT. The vector X is also changed by PRELIM.
     *
     *     The arguments N, NPT, X, XL, XU, RHOBEG, IPRINT and MAXFUN are the
     *       same as the corresponding arguments in SUBROUTINE BOBYQA.
     *     The arguments XBASE, XPT, FVAL, HQ, PQ, BMAT, ZMAT, NDIM, SL and SU
     *       are the same as the corresponding arguments in BOBYQB, the elements
     *       of SL and SU being set in BOBYQA.
     *     GOPT is usually the gradient of the quadratic model at XOPT+XBASE, but
     *       it is set by PRELIM to the gradient of the quadratic model at XBASE.
     *       If XOPT is nonzero, BOBYQB will change it to its usual value later.
     *     NF is maintaned as the number of calls of CALFUN so far.
     *     KOPT will be such that the least calculated value of F so far is at
     *       the point XPT(KOPT,.)+XBASE in the space of the variables.
     *
     * @param currentBest
     * @param xbase
     * @param xpt
     * @param fval
     * @param gopt
     * @param hq
     * @param pq
     * @param bmat
     * @param zmat
     * @param sl
     * @param su
     */
    private void prelim(
            ArrayRealVector currentBest,
            ArrayRealVector xbase,
            Array2DRowRealMatrix xpt,
            ArrayRealVector fval,
            ArrayRealVector gopt,
            ArrayRealVector hq,
            ArrayRealVector pq,
            Array2DRowRealMatrix bmat,
            Array2DRowRealMatrix zmat,
            ArrayRealVector sl,
            ArrayRealVector su
    ) {
        // System.out.println("prelim"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;
        final int ndim = bmat.getRowDimension();

        final double rhosq = initialTrustRegionRadius * initialTrustRegionRadius;
        final double recip = 1d / rhosq;
        final int np = n + 1;

        // Set XBASE to the initial vector of variables, and set the initial
        // elements of XPT, BMAT, HQ, PQ and ZMAT to zero.

        for (int j = 0; j < n; j++) {
            xbase.setEntry(j, currentBest.getEntry(j));
            for (int k = 0; k < npt; k++) {
                xpt.setEntry(k, j, ZERO);
            }
            for (int i = 0; i < ndim; i++) {
                bmat.setEntry(i, j, ZERO);
            }
        }
        for (int i = 0, max = n * np / 2; i < max; i++) {
            hq.setEntry(i, ZERO);
        }
        for (int k = 0; k < npt; k++) {
            pq.setEntry(k, ZERO);
            for (int j = 0, max = npt - np; j < max; j++) {
                zmat.setEntry(k, j, ZERO);
            }
        }

        // Begin the initialization procedure. NF becomes one more than the number
        // of function values so far. The coordinates of the displacement of the
        // next initial interpolation point from XBASE are set in XPT(NF+1,.).

        int ipt = 0;
        int jpt = 0;
        double fbeg = Double.NaN;
        do {
            final int nfm = getEvaluations();
            final int nfx = nfm - n;
            final int nfmm = nfm - 1;
            final int nfxm = nfx - 1;
            double stepa = 0;
            double stepb = 0;
            if (nfm <= 2 * n) {
                if (nfm >= 1 &&
                    nfm <= n) {
                    stepa = initialTrustRegionRadius;
                    if (su.getEntry(nfmm) == ZERO) {
                        stepa = -stepa;
                        throw new PathIsExploredException(); // XXX
                    }
                    xpt.setEntry(nfm, nfmm, stepa);
                } else if (nfm > n) {
                    stepa = xpt.getEntry(nfx, nfxm);
                    stepb = -initialTrustRegionRadius;
                    if (sl.getEntry(nfxm) == ZERO) {
                        stepb = Math.min(TWO * initialTrustRegionRadius, su.getEntry(nfxm));
                        throw new PathIsExploredException(); // XXX
                    }
                    if (su.getEntry(nfxm) == ZERO) {
                        stepb = Math.max(-TWO * initialTrustRegionRadius, sl.getEntry(nfxm));
                        throw new PathIsExploredException(); // XXX
                    }
                    xpt.setEntry(nfm, nfxm, stepb);
                }
            } else {
                final int tmp1 = (nfm - np) / n;
                jpt = nfm - tmp1 * n - n;
                ipt = jpt + tmp1;
                if (ipt > n) {
                    final int tmp2 = jpt;
                    jpt = ipt - n;
                    ipt = tmp2;
                    throw new PathIsExploredException(); // XXX
                }
                xpt.setEntry(nfm, ipt, xpt.getEntry(ipt, ipt));
                xpt.setEntry(nfm, jpt, xpt.getEntry(jpt, jpt));
            }

            // Calculate the next value of F. The least function value so far and
            // its index are required.

            for (int j = 0; j < n; j++) {
                currentBest.setEntry(j, Math.min(Math.max(lowerBound[j],
                                                          xbase.getEntry(j) + xpt.getEntry(nfm, j)),
                                                 upperBound[j]));
                if (xpt.getEntry(nfm, j) == sl.getEntry(j)) {
                    currentBest.setEntry(j, lowerBound[j]);
                }
                if (xpt.getEntry(nfm, j) == su.getEntry(j)) {
                    currentBest.setEntry(j, upperBound[j]);
                }
            }

            final double objectiveValue = computeObjectiveValue(currentBest.toArray());
            final double f = isMinimize ? objectiveValue : -objectiveValue;
            final int numEval = getEvaluations(); // nfm + 1
            fval.setEntry(nfm, f);

            if (numEval == 1) {
                fbeg = f;
                trustRegionCenterInterpolationPointIndex = 0;
            } else if (f < fval.getEntry(trustRegionCenterInterpolationPointIndex)) {
                trustRegionCenterInterpolationPointIndex = nfm;
            }

            // Set the nonzero initial elements of BMAT and the quadratic model in the
            // cases when NF is at most 2*N+1. If NF exceeds N+1, then the positions
            // of the NF-th and (NF-N)-th interpolation points may be switched, in
            // order that the function value at the first of them contributes to the
            // off-diagonal second derivative terms of the initial quadratic model.

            if (numEval <= 2 * n + 1) {
                if (numEval >= 2 &&
                    numEval <= n + 1) {
                    gopt.setEntry(nfmm, (f - fbeg) / stepa);
                    if (npt < numEval + n) {
                        final double oneOverStepA = ONE / stepa;
                        bmat.setEntry(0, nfmm, -oneOverStepA);
                        bmat.setEntry(nfm, nfmm, oneOverStepA);
                        bmat.setEntry(npt + nfmm, nfmm, -HALF * rhosq);
                        throw new PathIsExploredException(); // XXX
                    }
                } else if (numEval >= n + 2) {
                    final int ih = nfx * (nfx + 1) / 2 - 1;
                    final double tmp = (f - fbeg) / stepb;
                    final double diff = stepb - stepa;
                    hq.setEntry(ih, TWO * (tmp - gopt.getEntry(nfxm)) / diff);
                    gopt.setEntry(nfxm, (gopt.getEntry(nfxm) * stepb - tmp * stepa) / diff);
                    if (stepa * stepb < ZERO) {
                        if (f < fval.getEntry(nfm - n)) {
                            fval.setEntry(nfm, fval.getEntry(nfm - n));
                            fval.setEntry(nfm - n, f);
                            if (trustRegionCenterInterpolationPointIndex == nfm) {
                                trustRegionCenterInterpolationPointIndex = nfm - n;
                            }
                            xpt.setEntry(nfm - n, nfxm, stepb);
                            xpt.setEntry(nfm, nfxm, stepa);
                        }
                    }
                    bmat.setEntry(0, nfxm, -(stepa + stepb) / (stepa * stepb));
                    bmat.setEntry(nfm, nfxm, -HALF / xpt.getEntry(nfm - n, nfxm));
                    bmat.setEntry(nfm - n, nfxm,
                                  -bmat.getEntry(0, nfxm) - bmat.getEntry(nfm, nfxm));
                    zmat.setEntry(0, nfxm, Math.sqrt(TWO) / (stepa * stepb));
                    zmat.setEntry(nfm, nfxm, Math.sqrt(HALF) / rhosq);
                    // zmat.setEntry(nfm, nfxm, Math.sqrt(HALF) * recip); // XXX "testAckley" and "testDiffPow" fail.
                    zmat.setEntry(nfm - n, nfxm,
                                  -zmat.getEntry(0, nfxm) - zmat.getEntry(nfm, nfxm));
                }

                // Set the off-diagonal second derivatives of the Lagrange functions and
                // the initial quadratic model.

            } else {
                zmat.setEntry(0, nfxm, recip);
                zmat.setEntry(nfm, nfxm, recip);
                zmat.setEntry(ipt, nfxm, -recip);
                zmat.setEntry(jpt, nfxm, -recip);

                final int ih = ipt * (ipt - 1) / 2 + jpt - 1;
                final double tmp = xpt.getEntry(nfm, ipt - 1) * xpt.getEntry(nfm, jpt - 1);
                hq.setEntry(ih, (fbeg - fval.getEntry(ipt) - fval.getEntry(jpt) + f) / tmp);
                throw new PathIsExploredException(); // XXX
            }
        } while (getEvaluations() < npt);
    } // prelim


    // ----------------------------------------------------------------------------------------

    /**
     *     A version of the truncated conjugate gradient is applied. If a line
     *     search is restricted by a constraint, then the procedure is restarted,
     *     the values of the variables that are at their bounds being fixed. If
     *     the trust region boundary is reached, then further changes may be made
     *     to D, each one being in the two dimensional space that is spanned
     *     by the current D and the gradient of Q at XOPT+D, staying on the trust
     *     region boundary. Termination occurs when the reduction in Q seems to
     *     be close to the greatest reduction that can be achieved.
     *     The arguments N, NPT, XPT, XOPT, GOPT, HQ, PQ, SL and SU have the same
     *       meanings as the corresponding arguments of BOBYQB.
     *     DELTA is the trust region radius for the present calculation, which
     *       seeks a small value of the quadratic model within distance DELTA of
     *       XOPT subject to the bounds on the variables.
     *     XNEW will be set to a new vector of variables that is approximately
     *       the one that minimizes the quadratic model within the trust region
     *       subject to the SL and SU constraints on the variables. It satisfies
     *       as equations the bounds that become active during the calculation.
     *     D is the calculated trial step from XOPT, generated iteratively from an
     *       initial value of zero. Thus XNEW is XOPT+D after the final iteration.
     *     GNEW holds the gradient of the quadratic model at XOPT+D. It is updated
     *       when D is updated.
     *     xbdi.get( is a working space vector. For I=1,2,...,N, the element xbdi.get((I) is
     *       set to -1.0, 0.0, or 1.0, the value being nonzero if and only if the
     *       I-th variable has become fixed at a bound, the bound being SL(I) or
     *       SU(I) in the case xbdi.get((I)=-1.0 or xbdi.get((I)=1.0, respectively. This
     *       information is accumulated during the construction of XNEW.
     *     The arrays S, HS and HRED are also used for working space. They hold the
     *       current search direction, and the changes in the gradient of Q along S
     *       and the reduced D, respectively, where the reduced D is the same as D,
     *       except that the components of the fixed variables are zero.
     *     DSQ will be set to the square of the length of XNEW-XOPT.
     *     CRVMIN is set to zero if D reaches the trust region boundary. Otherwise
     *       it is set to the least curvature of H that occurs in the conjugate
     *       gradient searches that are not restricted by any constraints. The
     *       value CRVMIN=-1.0D0 is set, however, if all of these searches are
     *       constrained.
     * @param xpt
     * @param xopt
     * @param gopt
     * @param hq
     * @param pq
     * @param sl
     * @param su
     * @param delta
     * @param xnew
     * @param d__
     * @param gnew
     * @param xbdi
     * @param s
     * @param hs
     * @param hred
     */
    private double[] trsbox(
            Array2DRowRealMatrix xpt,
            ArrayRealVector xopt,
            ArrayRealVector gopt,
            ArrayRealVector hq,
            ArrayRealVector pq,
            ArrayRealVector sl,
            ArrayRealVector su,
            double delta,
            ArrayRealVector xnew,
            ArrayRealVector d__,
            ArrayRealVector gnew,
            ArrayRealVector xbdi,
            ArrayRealVector s,
            ArrayRealVector hs,
            ArrayRealVector hred
    ) {
        // System.out.println("trsbox"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;

        double dsq = Double.NaN;
        double crvmin = Double.NaN;

        // Local variables
        int ih;
        double ds;
        int iu;
        double dhd, dhs, cth, shs, sth, ssq, beta=0, sdec, blen;
        int iact = -1;
        int nact = 0;
        double angt = 0, qred;
        int isav;
        double temp = 0, xsav = 0, xsum = 0, angbd = 0, dredg = 0, sredg = 0;
        int iterc;
        double resid = 0, delsq = 0, ggsav = 0, tempa = 0, tempb = 0,
        redmax = 0, dredsq = 0, redsav = 0, gredsq = 0, rednew = 0;
        int itcsav = 0;
        double rdprev = 0, rdnext = 0, stplen = 0, stepsq = 0;
        int itermax = 0;

        // Set some constants.

        // Function Body

        // The sign of GOPT(I) gives the sign of the change to the I-th variable
        // that will reduce Q from its value at XOPT. Thus xbdi.get((I) shows whether
        // or not to fix the I-th variable at one of its bounds initially, with
        // NACT being set to the number of fixed variables. D and GNEW are also
        // set for the first iteration. DELSQ is the upper bound on the sum of
        // squares of the free variables. QRED is the reduction in Q so far.

        iterc = 0;
        nact = 0;
        for (int i = 0; i < n; i++) {
            xbdi.setEntry(i, ZERO);
            if (xopt.getEntry(i) <= sl.getEntry(i)) {
                if (gopt.getEntry(i) >= ZERO) {
                    xbdi.setEntry(i, MINUS_ONE);
                }
            } else if (xopt.getEntry(i) >= su.getEntry(i)) {
                if (gopt.getEntry(i) <= ZERO) {
                    xbdi.setEntry(i, ONE);
                }
            }
            if (xbdi.getEntry(i) != ZERO) {
                ++nact;
            }
            d__.setEntry(i, ZERO);
            gnew.setEntry(i, gopt.getEntry(i));
        }
        delsq = delta * delta;
        qred = ZERO;
        crvmin = MINUS_ONE;

        // Set the next search direction of the conjugate gradient method. It is
        // the steepest descent direction initially and when the iterations are
        // restarted because a variable has just been fixed by a bound, and of
        // course the components of the fixed variables are zero. ITERMAX is an
        // upper bound on the indices of the conjugate gradient iterations.

        int state = 20;
        for(;;) {
            // System.out.println("loop in trsbox: state=" + state); // XXX
            switch (state) {
        case 20: {
            beta = ZERO;
        }
        case 30: {
            stepsq = ZERO;
            for (int i = 0; i < n; i++) {
                if (xbdi.getEntry(i) != ZERO) {
                    s.setEntry(i, ZERO);
                } else if (beta == ZERO) {
                    s.setEntry(i, -gnew.getEntry(i));
                } else {
                    s.setEntry(i, beta * s.getEntry(i) - gnew.getEntry(i));
                }
                // Computing 2nd power
                final double d1 = s.getEntry(i);
                stepsq += d1 * d1;
            }
            if (stepsq == ZERO) {
                state = 190; break;
            }
            if (beta == ZERO) {
                gredsq = stepsq;
                itermax = iterc + n - nact;
            }
            if (gredsq * delsq <= qred * 1e-4 * qred) {
                state = 190; break;
            }

            // Multiply the search direction by the second derivative matrix of Q and
            // calculate some scalars for the choice of steplength. Then set BLEN to
            // the length of the the step to the trust region boundary and STPLEN to
            // the steplength, ignoring the simple bounds.

            state = 210; break;
        }
        case 50: {
            resid = delsq;
            ds = ZERO;
            shs = ZERO;
            for (int i = 0; i < n; i++) {
                if (xbdi.getEntry(i) == ZERO) {
                    // Computing 2nd power
                    final double d1 = d__.getEntry(i);
                    resid -= d1 * d1;
                    ds += s.getEntry(i) * d__.getEntry(i);
                    shs += s.getEntry(i) * hs.getEntry(i);
                }
            }
            if (resid <= ZERO) {
                state = 90; break;
            }
            temp = Math.sqrt(stepsq * resid + ds * ds);
            if (ds < ZERO) {
                blen = (temp - ds) / stepsq;
            } else {
                blen = resid / (temp + ds);
            }
            stplen = blen;
            if (shs > ZERO) {
                // Computing MIN
                stplen = Math.min(blen, gredsq / shs);
            }

            // Reduce STPLEN if necessary in order to preserve the simple bounds,
            // letting IACT be the index of the new constrained variable.

            iact = -1;
            for (int i = 0; i < n; i++) {
                if (s.getEntry(i) != ZERO) {
                    xsum = xopt.getEntry(i) + d__.getEntry(i);
                    if (s.getEntry(i) > ZERO) {
                        temp = (su.getEntry(i) - xsum) / s.getEntry(i);
                    } else {
                        temp = (sl.getEntry(i) - xsum) / s.getEntry(i);
                    }
                    if (temp < stplen) {
                        stplen = temp;
                        iact = i;
                    }
                }
            }

            // Update CRVMIN, GNEW and D. Set SDEC to the decrease that occurs in Q.

            sdec = ZERO;
            if (stplen > ZERO) {
                ++iterc;
                temp = shs / stepsq;
                if (iact == -1 && temp > ZERO) {
                    crvmin = Math.min(crvmin,temp);
                    if (crvmin == MINUS_ONE) {
                        crvmin = temp;
                    }
                }
                ggsav = gredsq;
                gredsq = ZERO;
                for (int i = 0; i < n; i++) {
                    gnew.setEntry(i, gnew.getEntry(i) + stplen * hs.getEntry(i));
                    if (xbdi.getEntry(i) == ZERO) {
                        // Computing 2nd power
                        final double d1 = gnew.getEntry(i);
                        gredsq += d1 * d1;
                    }
                    d__.setEntry(i, d__.getEntry(i) + stplen * s.getEntry(i));
                }
                // Computing MAX
                final double d1 = stplen * (ggsav - HALF * stplen * shs);
                sdec = Math.max(d1, ZERO);
                qred += sdec;
            }

            // Restart the conjugate gradient method if it has hit a new bound.

            if (iact >= 0) {
                ++nact;
                xbdi.setEntry(iact, ONE);
                if (s.getEntry(iact) < ZERO) {
                    xbdi.setEntry(iact, MINUS_ONE);
                }
                // Computing 2nd power
                final double d1 = d__.getEntry(iact);
                delsq -= d1 * d1;
                if (delsq <= ZERO) {
                    state = 190; break;
                }
                state = 20; break;
            }

            // If STPLEN is less than BLEN, then either apply another conjugate
            // gradient iteration or RETURN.

            if (stplen < blen) {
                if (iterc == itermax) {
                    state = 190; break;
                }
                if (sdec <= qred * .01) {
                    state = 190; break;
                }
                beta = gredsq / ggsav;
                state = 30; break;
            }
        }
        case 90: {
            crvmin = ZERO;

            // Prepare for the alternative iteration by calculating some scalars
            // and by multiplying the reduced D by the second derivative matrix of
            // Q, where S holds the reduced D in the call of GGMULT.

        }
        case 100: {
            if (nact >= n - 1) {
                state = 190; break;
            }
            dredsq = ZERO;
            dredg = ZERO;
            gredsq = ZERO;
            for (int i = 0; i < n; i++) {
                if (xbdi.getEntry(i) == ZERO) {
                    // Computing 2nd power
                    double d1 = d__.getEntry(i);
                    dredsq += d1 * d1;
                    dredg += d__.getEntry(i) * gnew.getEntry(i);
                    // Computing 2nd power
                    d1 = gnew.getEntry(i);
                    gredsq += d1 * d1;
                    s.setEntry(i, d__.getEntry(i));
                } else {
                    s.setEntry(i, ZERO);
                }
            }
            itcsav = iterc;
            state = 210; break;
            // Let the search direction S be a linear combination of the reduced D
            // and the reduced G that is orthogonal to the reduced D.
        }
        case 120: {
            ++iterc;
            temp = gredsq * dredsq - dredg * dredg;
            if (temp <= qred * 1e-4 * qred) {
                state = 190; break;
            }
            temp = Math.sqrt(temp);
            for (int i = 0; i < n; i++) {
                if (xbdi.getEntry(i) == ZERO) {
                    s.setEntry(i, (dredg * d__.getEntry(i) - dredsq * gnew.getEntry(i)) / temp);
                } else {
                    s.setEntry(i, ZERO);
                }
            }
            sredg = -temp;

            // By considering the simple bounds on the variables, calculate an upper
            // bound on the tangent of half the angle of the alternative iteration,
            // namely ANGBD, except that, if already a free variable has reached a
            // bound, there is a branch back to label 100 after fixing that variable.

            angbd = ONE;
            iact = -1;
            for (int i = 0; i < n; i++) {
                if (xbdi.getEntry(i) == ZERO) {
                    tempa = xopt.getEntry(i) + d__.getEntry(i) - sl.getEntry(i);
                    tempb = su.getEntry(i) - xopt.getEntry(i) - d__.getEntry(i);
                    if (tempa <= ZERO) {
                        ++nact;
                        xbdi.setEntry(i, MINUS_ONE);
                        state = 100; break;
                    } else if (tempb <= ZERO) {
                        ++nact;
                        xbdi.setEntry(i, ONE);
                        state = 100; break;
                    }
                    // Computing 2nd power
                    double d1 = d__.getEntry(i);
                    // Computing 2nd power
                    double d2 = s.getEntry(i);
                    ssq = d1 * d1 + d2 * d2;
                    // Computing 2nd power
                    d1 = xopt.getEntry(i) - sl.getEntry(i);
                    temp = ssq - d1 * d1;
                    if (temp > ZERO) {
                        temp = Math.sqrt(temp) - s.getEntry(i);
                        if (angbd * temp > tempa) {
                            angbd = tempa / temp;
                            iact = i;
                            xsav = MINUS_ONE;
                        }
                    }
                    // Computing 2nd power
                    d1 = su.getEntry(i) - xopt.getEntry(i);
                    temp = ssq - d1 * d1;
                    if (temp > ZERO) {
                        temp = Math.sqrt(temp) + s.getEntry(i);
                        if (angbd * temp > tempb) {
                            angbd = tempb / temp;
                            iact = i;
                            xsav = ONE;
                        }
                    }
                }
            }

            // Calculate HHD and some curvatures for the alternative iteration.

            state = 210; break;
        }
        case 150: {
            shs = ZERO;
            dhs = ZERO;
            dhd = ZERO;
            for (int i = 0; i < n; i++) {
                if (xbdi.getEntry(i) == ZERO) {
                    shs += s.getEntry(i) * hs.getEntry(i);
                    dhs += d__.getEntry(i) * hs.getEntry(i);
                    dhd += d__.getEntry(i) * hred.getEntry(i);
                }
            }

            // Seek the greatest reduction in Q for a range of equally spaced values
            // of ANGT in [0,ANGBD], where ANGT is the tangent of half the angle of
            // the alternative iteration.

            redmax = ZERO;
            isav = -1;
            redsav = ZERO;
            iu = (int) (angbd * 17. + 3.1);
            for (int i = 0; i < iu; i++) {
                angt = angbd * (double) i / (double) iu;
                sth = (angt + angt) / (ONE + angt * angt);
                temp = shs + angt * (angt * dhd - dhs - dhs);
                rednew = sth * (angt * dredg - sredg - HALF * sth * temp);
                if (rednew > redmax) {
                    redmax = rednew;
                    isav = i;
                    rdprev = redsav;
                } else if (i == isav + 1) {
                    rdnext = rednew;
                }
                redsav = rednew;
            }

            // Return if the reduction is zero. Otherwise, set the sine and cosine
            // of the angle of the alternative iteration, and calculate SDEC.

            if (isav < 0) {
                state = 190; break;
            }
            if (isav < iu) {
                temp = (rdnext - rdprev) / (redmax + redmax - rdprev - rdnext);
                angt = angbd * ((double) isav + HALF * temp) / (double) iu;
            }
            cth = (ONE - angt * angt) / (ONE + angt * angt);
            sth = (angt + angt) / (ONE + angt * angt);
            temp = shs + angt * (angt * dhd - dhs - dhs);
            sdec = sth * (angt * dredg - sredg - HALF * sth * temp);
            if (sdec <= ZERO) {
                state = 190; break;
            }

            // Update GNEW, D and HRED. If the angle of the alternative iteration
            // is restricted by a bound on a free variable, that variable is fixed
            // at the bound.

            dredg = ZERO;
            gredsq = ZERO;
            for (int i = 0; i < n; i++) {
                gnew.setEntry(i, gnew.getEntry(i) + (cth - ONE) * hred.getEntry(i) + sth * hs.getEntry(i));
                if (xbdi.getEntry(i) == ZERO) {
                    d__.setEntry(i, cth * d__.getEntry(i) + sth * s.getEntry(i));
                    dredg += d__.getEntry(i) * gnew.getEntry(i);
                    // Computing 2nd power
                    final double d1 = gnew.getEntry(i);
                    gredsq += d1 * d1;
                }
                hred.setEntry(i, cth * hred.getEntry(i) + sth * hs.getEntry(i));
            }
            qred += sdec;
            if (iact >= 0 && isav == iu) {
                ++nact;
                xbdi.setEntry(iact, xsav);
                state = 100; break;
            }

            // If SDEC is sufficiently small, then RETURN after setting XNEW to
            // XOPT+D, giving careful attention to the bounds.

            if (sdec > qred * .01) {
                state = 120; break;
            }
        }
        case 190: {
            dsq = ZERO;
            for (int i = 0; i < n; i++) {
                // Computing MAX
                // Computing MIN
                final double min = Math.min(xopt.getEntry(i) + d__.getEntry(i),
                                            su.getEntry(i));
                xnew.setEntry(i, Math.max(min, sl.getEntry(i)));
                if (xbdi.getEntry(i) == MINUS_ONE) {
                    xnew.setEntry(i, sl.getEntry(i));
                }
                if (xbdi.getEntry(i) == ONE) {
                    xnew.setEntry(i, su.getEntry(i));
                }
                d__.setEntry(i, xnew.getEntry(i) - xopt.getEntry(i));
                // Computing 2nd power
                final double d1 = d__.getEntry(i);
                dsq += d1 * d1;
            }
            return new double[] { dsq, crvmin };
            // The following instructions multiply the current S-vector by the second
            // derivative matrix of the quadratic model, putting the product in HS.
            // They are reached from three different parts of the software above and
            // they can be regarded as an external subroutine.
        }
        case 210: {
            ih = 0;
            for (int j = 0; j < n; j++) {
                hs.setEntry(j, ZERO);
                for (int i = 0; i <= j; i++) {
                    if (i < j) {
                        hs.setEntry(j, hs.getEntry(j) + hq.getEntry(ih) * s.getEntry(i));
                    }
                    hs.setEntry(i, hs.getEntry(i) + hq.getEntry(ih) * s.getEntry(j));
                    ih++;
                }
            }
            final RealVector tmp = xpt.operate(s).ebeMultiply(pq);
            for (int k = 0; k < npt; k++) {
                if (pq.getEntry(k) != ZERO) {
                    for (int i = 0; i < n; i++) {
                        hs.setEntry(i, hs.getEntry(i) + tmp.getEntry(k) * xpt.getEntry(k, i));
                    }
                }
            }
            if (crvmin != ZERO) {
                state = 50; break;
            }
            if (iterc > itcsav) {
                state = 150; break;
            }
            for (int i = 0; i < n; i++) {
                hred.setEntry(i, hs.getEntry(i));
            }
            state = 120; break;
        }
        default: {
            throw new MathIllegalStateException(LocalizedFormats.SIMPLE_MESSAGE, "trsbox");
        }}
        }
    } // trsbox

    // ----------------------------------------------------------------------------------------

    /**
     *     The arrays BMAT and ZMAT are updated, as required by the new position
     *     of the interpolation point that has the index KNEW. The vector VLAG has
     *     N+NPT components, set on entry to the first NPT and last N components
     *     of the product Hw in equation (4.11) of the Powell (2006) paper on
     *     NEWUOA. Further, BETA is set on entry to the value of the parameter
     *     with that name, and DENOM is set to the denominator of the updating
     *     formula. Elements of ZMAT may be treated as zero if their moduli are
     *     at most ZTEST. The first NDIM elements of W are used for working space.
     * @param bmat
     * @param zmat
     * @param vlag
     * @param beta
     * @param denom
     * @param knew
     */
    private void update(
            Array2DRowRealMatrix bmat,
            Array2DRowRealMatrix zmat,
            ArrayRealVector vlag,
            double beta,
            double denom,
            int knew
    ) {
        // System.out.println("update"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;
        final int nptm = npt - n - 1;

        // XXX Should probably be split into two arrays.
        final ArrayRealVector work = new ArrayRealVector(npt + n);

        double ztest = ZERO;
        for (int k = 0; k < npt; k++) {
            for (int j = 0; j < nptm; j++) {
                // Computing MAX
                ztest = Math.max(ztest, Math.abs(zmat.getEntry(k, j)));
            }
        }
        ztest *= 1e-20;

        // Apply the rotations that put zeros in the KNEW-th row of ZMAT.

        for (int j = 1; j < nptm; j++) {
            final double d1 = zmat.getEntry(knew, j);
            if (Math.abs(d1) > ztest) {
                // Computing 2nd power
                final double d2 = zmat.getEntry(knew, 0);
                // Computing 2nd power
                final double d3 = zmat.getEntry(knew, j);
                final double d4 = Math.sqrt(d2 * d2 + d3 * d3);
                final double d5 = zmat.getEntry(knew, 0) / d4;
                final double d6 = zmat.getEntry(knew, j) / d4;
                for (int i = 0; i < npt; i++) {
                    final double d7 = d5 * zmat.getEntry(i, 0) + d6 * zmat.getEntry(i, j);
                    zmat.setEntry(i, j, d5 * zmat.getEntry(i, j) - d6 * zmat.getEntry(i, 0));
                    zmat.setEntry(i, 0, d7);
                }
            }
            zmat.setEntry(knew, j, ZERO);
        }

        // Put the first NPT components of the KNEW-th column of HLAG into W,
        // and calculate the parameters of the updating formula.

        for (int i = 0; i < npt; i++) {
            work.setEntry(i, zmat.getEntry(knew, 0) * zmat.getEntry(i, 0));
        }
        final double alpha = work.getEntry(knew);
        final double tau = vlag.getEntry(knew);
        vlag.setEntry(knew, vlag.getEntry(knew) - ONE);

        // Complete the updating of ZMAT.

        final double sqrtDenom = Math.sqrt(denom);
        final double d1 = tau / sqrtDenom;
        final double d2 = zmat.getEntry(knew, 0) / sqrtDenom;
        for (int i = 0; i < npt; i++) {
            zmat.setEntry(i, 0,
                          d1 * zmat.getEntry(i, 0) - d2 * vlag.getEntry(i));
        }

        // Finally, update the matrix BMAT.

        for (int j = 0; j < n; j++) {
            final int jp = npt + j;
            work.setEntry(jp, bmat.getEntry(knew, j));
            final double d3 = (alpha * vlag.getEntry(jp) - tau * work.getEntry(jp)) / denom;
            final double d4 = (-beta * work.getEntry(jp) - tau * vlag.getEntry(jp)) / denom;
            for (int i = 0; i <= jp; i++) {
                bmat.setEntry(i, j,
                              bmat.getEntry(i, j) + d3 * vlag.getEntry(i) + d4 * work.getEntry(i));
                if (i >= npt) {
                    bmat.setEntry(jp, (i - npt), bmat.getEntry(i, j));
                }
            }
        }
    } // update

    /**
     * Performs validity checks and adapt the {@link #lowerBound} and
     * {@link #upperBound} array if no constraints were provided.
     */
    private void setup() {
        // System.out.println("setup"); // XXX

        double[] init = getStartPoint();
        final int dimension = init.length;

        // Check problem dimension.
        if (dimension < MINIMUM_PROBLEM_DIMENSION) {
            throw new NumberIsTooSmallException(dimension, MINIMUM_PROBLEM_DIMENSION, true);
        }
        // Check number of interpolation points.
        final int[] nPointsInterval = { dimension + 2, (dimension + 2) * (dimension + 1) / 2 };
        if (numberOfInterpolationPoints < nPointsInterval[0] ||
            numberOfInterpolationPoints > nPointsInterval[1]) {
            throw new OutOfRangeException(LocalizedFormats.NUMBER_OF_INTERPOLATION_POINTS,
                                          numberOfInterpolationPoints,
                                          nPointsInterval[0],
                                          nPointsInterval[1]);
        }

        // Check (and possibly adapt) bounds.
        if (lowerBound == null) {
            lowerBound = fillNewArray(dimension, Double.NEGATIVE_INFINITY);
        } else if (lowerBound.length != init.length) {
            throw new DimensionMismatchException(lowerBound.length, dimension);
        }

        if (upperBound == null) {
            upperBound = fillNewArray(dimension, Double.POSITIVE_INFINITY);
        } else if (upperBound.length != init.length) {
            throw new DimensionMismatchException(upperBound.length, dimension);
        }

       for (int i = 0; i < dimension; i++) {
            final double v = init[i];
            final double lo = lowerBound[i];
            final double hi = upperBound[i];
            if (v < lo || v > hi) {
                throw new OutOfRangeException(v, lo, hi);
            }
        }

        // Initialize bound differences.
        boundDifference = new double[dimension];

        double requiredMinDiff = 2 * initialTrustRegionRadius;
        double minDiff = Double.POSITIVE_INFINITY;
       for (int i = 0; i < dimension; i++) {
            boundDifference[i] = upperBound[i] - lowerBound[i];
            minDiff = Math.min(minDiff, boundDifference[i]);
        }
        if (minDiff < requiredMinDiff) {
            initialTrustRegionRadius = minDiff / 3.0;
        }
    }

    /**
     * Creates a new array.
     *
     * @param n Dimension of the returned array.
     * @param value Value for each element.
     * @return an array containing {@code n} elements set to the given
     * {@code value}.
     */
    private static double[] fillNewArray(int n,
                                         double value) {
        double[] ds = new double[n];
        Arrays.fill(ds, value);
        return ds;
    }
}

/**
 * Marker for code paths that are not explored with the current unit tests.
 * If the path becomes explored, it should just be removed from the code.
 */
class PathIsExploredException extends RuntimeException {
    private static final String PATH_IS_EXPLORED
        = "If this exception is thrown, just remove it from the code";

    PathIsExploredException() {
        super(PATH_IS_EXPLORED);
    }
}
