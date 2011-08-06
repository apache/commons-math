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
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.MathIllegalStateException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.MultivariateRealOptimizer;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.Array2DRowRealMatrix;

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

        final FortranArray xbase = new FortranArray(n);
        final FortranMatrix xpt = new FortranMatrix(npt, n);
        final FortranArray fval = new FortranArray(npt);
        final FortranArray xopt = new FortranArray(n);
        final FortranArray gopt = new FortranArray(n);
        final FortranArray hq = new FortranArray(n * np / 2);
        final FortranArray pq = new FortranArray(npt);
        final FortranMatrix bmat = new FortranMatrix(ndim, n);
        final FortranMatrix zmat = new FortranMatrix(npt, (npt - np));
        final ArrayRealVector sl = new ArrayRealVector(n);
        final ArrayRealVector su = new ArrayRealVector(n);
        final FortranArray xnew = new FortranArray(n);
        final FortranArray xalt = new FortranArray(n);
        final FortranArray d__ = new FortranArray(n);
        final FortranArray vlag = new FortranArray(ndim);

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
                      new FortranArray(sl),
                      new FortranArray(su),
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
            FortranArray xbase, 
            FortranMatrix xpt,
            FortranArray fval,
            FortranArray xopt,
            FortranArray gopt,
            FortranArray hq,
            FortranArray pq,
            FortranMatrix bmat,
            FortranMatrix zmat,
            FortranArray sl,
            FortranArray su,
            FortranArray xnew,
            FortranArray xalt,
            FortranArray d__,
            FortranArray vlag
    ) {
        // System.out.println("bobyqb"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;
        final int ndim = bmat.getRowDimension();
        final int np = n + 1;
        final int nptm = npt - np;
        final int nh = n * np / 2;

        final FortranArray work1 = new FortranArray(n);
        final FortranArray work2 = new FortranArray(npt);
        final FortranArray work3 = new FortranArray(npt);

        double cauchy = Double.NaN;
        double alpha = Double.NaN;
        double dsq = Double.NaN;
        double crvmin = Double.NaN;

        // System generated locals
        int xpt_offset;
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
        int nresc, nfsav;
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
        for (int i = 1; i <= n; i++) {
            xopt.setEntry(i, xpt.getEntry(trustRegionCenterInterpolationPointIndex, i));
            // Computing 2nd power
            final double deltaOne = xopt.getEntry(i);
            xoptsq += deltaOne * deltaOne;
        }
        fsave = fval.getEntry(1);
        kbase = 1;

        // Complete the settings that are required for the iterative procedure.

        rho = initialTrustRegionRadius;
        delta = rho;
        nresc = getEvaluations();
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
                for (int j = 1; j <= n; j++) {
                    for (int i = 1; i <= j; i++) {
                        ++ih;
                        if (i < j) {
                            gopt.setEntry(j,  gopt.getEntry(j) + hq.getEntry(ih) * xopt.getEntry(i));
                        }
                        gopt.setEntry(i,  gopt.getEntry(i) + hq.getEntry(ih) * xopt.getEntry(j));
                    }
                }
                if (getEvaluations() > npt) {
                    for (int k = 1; k <= npt; k++) {
                        temp = ZERO;
                        for (int j = 1; j <= n; j++) {
                            temp += xpt.getEntry(k, j) * xopt.getEntry(j);
                        }
                        temp = pq.getEntry(k) * temp;
                        for (int i = 1; i <= n; i++) {
                            gopt.setEntry(i, gopt.getEntry(i) + temp * xpt.getEntry(k, i));
                        }
                    }
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
            final FortranArray gnew = new FortranArray(n);
            final FortranArray xbdi = new FortranArray(n);
            final FortranArray s = new FortranArray(n);
            final FortranArray hs = new FortranArray(n);
            final FortranArray hred = new FortranArray(n);

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
                for (int j = 1; j <= n; j++) {
                    bdtest = bdtol;
                    if (xnew.getEntry(j) == sl.getEntry(j)) {
                        bdtest = work1.getEntry(j);
                    }
                    if (xnew.getEntry(j) == su.getEntry(j)) {
                        bdtest = -work1.getEntry(j);
                    }
                    if (bdtest < bdtol) {
                        curv = hq.getEntry((j + j * j) / 2);
                        for (int k = 1; k <= npt; k++) {
                            // Computing 2nd power
                            final double d1 = xpt.getEntry(k, j);
                            curv += pq.getEntry(k) * (d1 * d1);
                        }
                        bdtest += HALF * curv * rho;
                        if (bdtest < bdtol) {
                            state = 650; break;
                        }
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
                for (int k = 1; k <= npt; k++) {
                    sumpq += pq.getEntry(k);
                    sum = -HALF * xoptsq;
                    for (int i = 1; i <= n; i++) {
                        sum += xpt.getEntry(k, i) * xopt.getEntry(i);
                    }
                    work2.setEntry(k, sum);
                    temp = fracsq - HALF * sum;
                    for (int i = 1; i <= n; i++) {
                        work1.setEntry(i, bmat.getEntry(k, i));
                        vlag.setEntry(i, sum * xpt.getEntry(k, i) + temp * xopt.getEntry(i));
                        ip = npt + i;
                        for (int j = 1; j <= i; j++) {
                            bmat.setEntry(ip, j,
                                          bmat.getEntry(ip, j)
                                          + work1.getEntry(i) * vlag.getEntry(j)
                                          + vlag.getEntry(i) * work1.getEntry(j));
                        }
                    }
                }

                // Then the revisions of BMAT that depend on ZMAT are calculated.

                for (int m = 1; m <= nptm; m++) {
                    sumz = ZERO;
                    sumw = ZERO;
                    for (int k = 1; k <= npt; k++) {
                        sumz += zmat.getEntry(k, m);
                        vlag.setEntry(k, work2.getEntry(k) * zmat.getEntry(k, m));
                        sumw += vlag.getEntry(k);
                    }
                    for (int j = 1; j <= n; j++) {
                        sum = (fracsq * sumz - HALF * sumw) * xopt.getEntry(j);
                        for (int k = 1; k <= npt; k++) {
                            sum += vlag.getEntry(k) * xpt.getEntry(k, j);
                        }
                        work1.setEntry(j, sum);
                        for (int k = 1; k <= npt; k++) {
                            bmat.setEntry(k, j,
                                          bmat.getEntry(k, j)
                                          + sum * zmat.getEntry(k, m));
                        }
                    }
                    for (int i = 1; i <= n; i++) {
                        ip = i + npt;
                        temp = work1.getEntry(i);
                        for (int j = 1; j <= i; j++) {
                            bmat.setEntry(ip, j,
                                          bmat.getEntry(ip, j)
                                          + temp * work1.getEntry(j));
                        }
                    }
                }

                // The following instructions complete the shift, including the changes
                // to the second derivative parameters of the quadratic model.

                ih = 0;
                for (int j = 1; j <= n; j++) {
                    work1.setEntry(j, -HALF * sumpq * xopt.getEntry(j));
                    for (int k = 1; k <= npt; k++) {
                        work1.setEntry(j, work1.getEntry(j) + pq.getEntry(k) * xpt.getEntry(k, j));
                        xpt.setEntry(k, j, xpt.getEntry(k, j) - xopt.getEntry(j));
                    }
                    for (int i = 1; i <= j; i++) {
                        ++ih;
                        hq.setEntry(ih,
                                    hq.getEntry(ih)
                                    + work1.getEntry(i) * xopt.getEntry(j)
                                    + xopt.getEntry(i) * work1.getEntry(j));
                        bmat.setEntry(npt + i, j, bmat.getEntry(npt + j, i));
                    }
                }
                for (int i = 1; i <= n; i++) {
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
        case 190: {
            nfsav = getEvaluations();
            kbase = trustRegionCenterInterpolationPointIndex;

            rescue(xbase, xpt,
                    fval, xopt, gopt, hq, pq, bmat,
                    zmat, sl, su, delta,
                   vlag);

            // XOPT is updated now in case the branch below to label 720 is taken.
            // Any updating of GOPT occurs after the branch below to label 20, which
            // leads to a trust region iteration as does the branch to label 60.

            xoptsq = ZERO;
            if (trustRegionCenterInterpolationPointIndex != kbase) {
                for (int i = 1; i <= n; i++) {
                    xopt.setEntry(i, xpt.getEntry(trustRegionCenterInterpolationPointIndex, i));
                    // Computing 2nd power
                    final double d1 = xopt.getEntry(i);
                    xoptsq += d1 * d1;
                }
            }
            nresc = getEvaluations();
            if (nfsav < getEvaluations()) {
                nfsav = getEvaluations();
                state = 20; break;
            }
            if (ntrits > 0) {
                state = 60; break;
            }
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

            for (int i = 1; i <= n; i++) {
                d__.setEntry(i, xnew.getEntry(i) - xopt.getEntry(i));
            }

            // Calculate VLAG and BETA for the current choice of D. The scalar
            // product of D with XPT(K,.) is going to be held in W(NPT+K) for
            // use when VQUAD is calculated.

        }
        case 230: {
            for (int k = 1; k <= npt; k++) {
                suma = ZERO;
                sumb = ZERO;
                sum = ZERO;
                for (int j = 1; j <= n; j++) {
                    suma += xpt.getEntry(k, j) * d__.getEntry(j);
                    sumb += xpt.getEntry(k, j) * xopt.getEntry(j);
                    sum += bmat.getEntry(k, j) * d__.getEntry(j);
                }
                work3.setEntry(k, suma * (HALF * suma + sumb));
                vlag.setEntry(k, sum);
                work2.setEntry(k, suma);
            }
            beta = ZERO;
            for (int m = 1; m <= nptm; m++) {
                sum = ZERO;
                for (int k = 1; k <= npt; k++) {
                    sum += zmat.getEntry(k, m) * work3.getEntry(k);
                }
                beta -= sum * sum;
                for (int k = 1; k <= npt; k++) {
                    vlag.setEntry(k, vlag.getEntry(k) + sum * zmat.getEntry(k, m));
                }
            }
            dsq = ZERO;
            bsum = ZERO;
            dx = ZERO;
            for (int j = 1; j <= n; j++) {
                // Computing 2nd power
                final double d1 = d__.getEntry(j);
                dsq += d1 * d1;
                sum = ZERO;
                for (int k = 1; k <= npt; k++) {
                    sum += work3.getEntry(k) * bmat.getEntry(k, j);
                }
                bsum += sum * d__.getEntry(j);
                jp = npt + j;
                for (int i = 1; i <= n; i++) {
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
                d__1 = vlag.getEntry(knew); // XXX Same statement as a few lines below?
                denom = d__1 * d__1 + alpha * beta;
                if (denom < cauchy && cauchy > ZERO) {
                    for (int i = 1; i <= n; i++) {
                        xnew.setEntry(i, xalt.getEntry(i));
                        d__.setEntry(i, xnew.getEntry(i) - xopt.getEntry(i));
                    }
                    cauchy = ZERO; // XXX Useful statement?
                    state = 230; break;
                }
                // Computing 2nd power
                d__1 = vlag.getEntry(knew); // XXX Same statement as a few lines above?
                if (denom <= HALF * (d__1 * d__1)) {
                    if (getEvaluations() > nresc) {
                        state = 190; break;
                    }
                    throw new MathIllegalStateException(LocalizedFormats.TOO_MUCH_CANCELLATION, vquad);
                }

                // Alternatively, if NTRITS is positive, then set KNEW to the index of
                // the next interpolation point to be deleted to make room for a trust
                // region step. Again RESCUE may be called if rounding errors have damaged
                // the chosen denominator, which is the reason for attempting to select
                // KNEW before calculating the next value of the objective function.

            } else {
                delsq = delta * delta;
                scaden = ZERO;
                biglsq = ZERO;
                knew = 0;
                for (int k = 1; k <= npt; k++) {
                    if (k == trustRegionCenterInterpolationPointIndex) {
                        continue;
                    }
                    hdiag = ZERO;
                    for (int m = 1; m <= nptm; m++) {
                        // Computing 2nd power
                        final double d1 = zmat.getEntry(k, m);
                        hdiag += d1 * d1;
                    }
                    // Computing 2nd power
                    d__1 = vlag.getEntry(k);
                    den = beta * hdiag + d__1 * d__1;
                    distsq = ZERO;
                    for (int j = 1; j <= n; j++) {
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
                if (scaden <= HALF * biglsq) {
                    if (getEvaluations() > nresc) {
                        state = 190; break;
                    }
                    throw new MathIllegalStateException(LocalizedFormats.TOO_MUCH_CANCELLATION, vquad);
                }
            }

            // Put the variables for the next calculation of the objective function
            //   in XNEW, with any adjustments for the bounds.

            // Calculate the value of the objective function at XBASE+XNEW, unless
            //   the limit on the number of calculations of F has been reached.

        }
        case 360: {
            for (int i = 1; i <= n; i++) {
                // Computing MIN
                // Computing MAX
                d__3 = lowerBound[f2jai(i)];
                d__4 = xbase.getEntry(i) + xnew.getEntry(i);
                d__1 = Math.max(d__3, d__4);
                d__2 = upperBound[f2jai(i)];
                currentBest.setEntry(f2jai(i), Math.min(d__1, d__2));
                if (xnew.getEntry(i) == sl.getEntry(i)) {
                    currentBest.setEntry(f2jai(i), lowerBound[f2jai(i)]);
                }
                if (xnew.getEntry(i) == su.getEntry(i)) {
                    currentBest.setEntry(f2jai(i), upperBound[f2jai(i)]);
                }
            }

            f = computeObjectiveValue(currentBest.getData());

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
            for (int j = 1; j <= n; j++) {
                vquad += d__.getEntry(j) * gopt.getEntry(j);
                for (int i = 1; i <= j; i++) {
                    ++ih;
                    temp = d__.getEntry(i) * d__.getEntry(j);
                    if (i == j) {
                        temp = HALF * temp;
                    }
                    vquad += hq.getEntry(ih) * temp;
                }
            }
            for (int k = 1; k <= npt; k++) {
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
                    for (int k = 1; k <= npt; k++) {
                        hdiag = ZERO;
                        for (int m = 1; m <= nptm; m++) {
                            // Computing 2nd power
                            final double d1 = zmat.getEntry(k, m);
                            hdiag += d1 * d1;
                        }
                        // Computing 2nd power
                        d__1 = vlag.getEntry(k);
                        den = beta * hdiag + d__1 * d__1;
                        distsq = ZERO;
                        for (int j = 1; j <= n; j++) {
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
            for (int i = 1; i <= n; i++) {
                temp = pqold * xpt.getEntry(knew, i);
                for (int j = 1; j <= i; j++) {
                    ++ih;
                    hq.setEntry(ih, hq.getEntry(ih) + temp * xpt.getEntry(knew, j));
                }
            }
            for (int m = 1; m <= nptm; m++) {
                temp = diff * zmat.getEntry(knew, m);
                for (int k = 1; k <= npt; k++) {
                    pq.setEntry(k, pq.getEntry(k) + temp * zmat.getEntry(k, m));
                }
            }

            // Include the new interpolation point, and make the changes to GOPT at
            // the old XOPT that are caused by the updating of the quadratic model.

            fval.setEntry(knew,  f);
            for (int i = 1; i <= n; i++) {
                xpt.setEntry(knew, i, xnew.getEntry(i));
                work1.setEntry(i, bmat.getEntry(knew, i));
            }
            for (int k = 1; k <= npt; k++) {
                suma = ZERO;
                for (int m = 1; m <= nptm; m++) {
                    suma += zmat.getEntry(knew, m) * zmat.getEntry(k, m);
                }
                sumb = ZERO;
                for (int j = 1; j <= n; j++) {
                    sumb += xpt.getEntry(k, j) * xopt.getEntry(j);
                }
                temp = suma * sumb;
                for (int i = 1; i <= n; i++) {
                    work1.setEntry(i, work1.getEntry(i) + temp * xpt.getEntry(k, i));
                }
            }
            for (int i = 1; i <= n; i++) {
                gopt.setEntry(i, gopt.getEntry(i) + diff * work1.getEntry(i));
            }

            // Update XOPT, GOPT and KOPT if the new calculated F is less than FOPT.

            if (f < fopt) {
                trustRegionCenterInterpolationPointIndex = knew;
                xoptsq = ZERO;
                ih = 0;
                for (int j = 1; j <= n; j++) {
                    xopt.setEntry(j, xnew.getEntry(j));
                    // Computing 2nd power
                    final double d1 = xopt.getEntry(j);
                    xoptsq += d1 * d1;
                    for (int i = 1; i <= j; i++) {
                        ++ih;
                        if (i < j) {
                            gopt.setEntry(j, gopt.getEntry(j) + hq.getEntry(ih) * d__.getEntry(i));
                        }
                        gopt.setEntry(i, gopt.getEntry(i) + hq.getEntry(ih) * d__.getEntry(j));
                    }
                }
                for (int k = 1; k <= npt; k++) {
                    temp = ZERO;
                    for (int j = 1; j <= n; j++) {
                        temp += xpt.getEntry(k, j) * d__.getEntry(j);
                    }
                    temp = pq.getEntry(k) * temp;
                    for (int i = 1; i <= n; i++) {
                        gopt.setEntry(i, gopt.getEntry(i) + temp * xpt.getEntry(k, i));
                    }
                }
            }

            // Calculate the parameters of the least Frobenius norm interpolant to
            // the current data, the gradient of this interpolant at XOPT being put
            // into VLAG(NPT+I), I=1,2,...,N.

            if (ntrits > 0) {
                for (int k = 1; k <= npt; k++) {
                    vlag.setEntry(k, fval.getEntry(k) - fval.getEntry(trustRegionCenterInterpolationPointIndex));
                    work3.setEntry(k, ZERO);
                }
                for (int j = 1; j <= nptm; j++) {
                    sum = ZERO;
                    for (int k = 1; k <= npt; k++) {
                        sum += zmat.getEntry(k, j) * vlag.getEntry(k);
                    }
                    for (int k = 1; k <= npt; k++) {
                        work3.setEntry(k, work3.getEntry(k) + sum * zmat.getEntry(k, j));
                    }
                }
                for (int k = 1; k <= npt; k++) {
                    sum = ZERO;
                    for (int j = 1; j <= n; j++) {
                        sum += xpt.getEntry(k, j) * xopt.getEntry(j);
                    }
                    work2.setEntry(k, work3.getEntry(k));
                    work3.setEntry(k, sum * work3.getEntry(k));
                }
                gqsq = ZERO;
                gisq = ZERO;
                for (int i = 1; i <= n; i++) {
                    sum = ZERO;
                    for (int k = 1; k <= npt; k++) {
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
                    for (int i = 1, max = Math.max(npt, nh); i <= max; i++) {
                        if (i <= n) {
                            gopt.setEntry(i, vlag.getEntry(npt + i));
                        }
                        if (i <= npt) {
                            pq.setEntry(i, work2.getEntry(i));
                        }
                        if (i <= nh) {
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
            knew = 0;
            for (int k = 1; k <= npt; k++) {
                sum = ZERO;
                for (int j = 1; j <= n; j++) {
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

            if (knew > 0) {
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
                for (int i = 1; i <= n; i++) {
                    // Computing MIN
                    // Computing MAX
                    d__3 = lowerBound[f2jai(i)];
                    d__4 = xbase.getEntry(i) + xopt.getEntry(i);
                    d__1 = Math.max(d__3, d__4);
                    d__2 = upperBound[f2jai(i)];
                    currentBest.setEntry(f2jai(i), Math.min(d__1, d__2));
                    if (xopt.getEntry(i) == sl.getEntry(i)) {
                        currentBest.setEntry(f2jai(i), lowerBound[f2jai(i)]);
                    }
                    if (xopt.getEntry(i) == su.getEntry(i)) {
                        currentBest.setEntry(f2jai(i), upperBound[f2jai(i)]);
                    }
                }
                f = fval.getEntry(trustRegionCenterInterpolationPointIndex);
            }
            return f;
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
            FortranMatrix xpt,
            FortranArray xopt,
            FortranMatrix bmat,
            FortranMatrix zmat,
            FortranArray sl,
            FortranArray su,
            int knew,
            double adelt,
            FortranArray xnew,
            FortranArray xalt
    ) {
        // System.out.println("altmov"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;
        final int ndim = bmat.getRowDimension();

        final FortranArray glag = new FortranArray(n);
        final FortranArray hcol = new FortranArray(npt);

        final FortranArray work1 = new FortranArray(n);
        final FortranArray work2 = new FortranArray(n);

        double alpha = Double.NaN;
        double cauchy = Double.NaN;

        // System generated locals
        double d__1, d__2, d__3, d__4;

        // Local variables
        double ha, gw, diff;
        int ilbd, isbd;
        double slbd;
        int iubd;
        double vlag, subd, temp;
        int ksav = 0;
        double step = 0, curv = 0;
        int iflag;
        double scale = 0, csave = 0, tempa = 0, tempb = 0, tempd = 0, const__ = 0, sumin = 0, 
        ggfree = 0;
        int ibdsav = 0;
        double dderiv = 0, bigstp = 0, predsq = 0, presav = 0, distsq = 0, stpsav = 0, wfixsq = 0, wsqsav = 0;


        // Function Body
        const__ = ONE + Math.sqrt(2.);
        for (int k = 1; k <= npt; k++) {
            hcol.setEntry(k, ZERO);
        }
        for (int j = 1, max = npt - n - 1; j <= max; j++) {
            temp = zmat.getEntry(knew, j);
            for (int k = 1; k <= npt; k++) {
                hcol.setEntry(k, hcol.getEntry(k) + temp * zmat.getEntry(k, j));
            }
        }
        alpha = hcol.getEntry(knew);
        ha = HALF * alpha;

        // Calculate the gradient of the KNEW-th Lagrange function at XOPT.

        for (int i = 1; i <= n; i++) {
            glag.setEntry(i, bmat.getEntry(knew, i));
        }
        for (int k = 1; k <= npt; k++) {
            temp = ZERO;
            for (int j = 1; j <= n; j++) {
                temp += xpt.getEntry(k, j) * xopt.getEntry(j);
            }
            temp = hcol.getEntry(k) * temp;
            for (int i = 1; i <= n; i++) {
                glag.setEntry(i, glag.getEntry(i) + temp * xpt.getEntry(k, i));
            }
        }

        // Search for a large denominator along the straight lines through XOPT
        // and another interpolation point. SLBD and SUBD will be lower and upper
        // bounds on the step along each of these lines in turn. PREDSQ will be
        // set to the square of the predicted denominator for each line. PRESAV
        // will be set to the largest admissible value of PREDSQ that occurs.

        presav = ZERO;
        for (int k = 1; k <= npt; k++) {
            if (k == trustRegionCenterInterpolationPointIndex) {
                continue;
            }
            dderiv = ZERO;
            distsq = ZERO;
            for (int i = 1; i <= n; i++) {
                temp = xpt.getEntry(k, i) - xopt.getEntry(i);
                dderiv += glag.getEntry(i) * temp;
                distsq += temp * temp;
            }
            subd = adelt / Math.sqrt(distsq);
            slbd = -subd;
            ilbd = 0;
            iubd = 0;
            sumin = Math.min(ONE, subd);

            // Revise SLBD and SUBD if necessary because of the bounds in SL and SU.

            for (int i = 1; i <= n; i++) {
                temp = xpt.getEntry(k, i) - xopt.getEntry(i);
                if (temp > ZERO) {
                    if (slbd * temp < sl.getEntry(i) - xopt.getEntry(i)) {
                        slbd = (sl.getEntry(i) - xopt.getEntry(i)) / temp;
                        ilbd = -i;
                    }
                    if (subd * temp > su.getEntry(i) - xopt.getEntry(i)) {
                        // Computing MAX
                        d__1 = sumin;
                        d__2 = (su.getEntry(i) - xopt.getEntry(i)) / temp;
                        subd = Math.max(d__1, d__2);
                        iubd = i;
                    }
                } else if (temp < ZERO) {
                    if (slbd * temp > su.getEntry(i) - xopt.getEntry(i)) {
                        slbd = (su.getEntry(i) - xopt.getEntry(i)) / temp;
                        ilbd = i;
                    }
                    if (subd * temp < sl.getEntry(i) - xopt.getEntry(i)) {
                        // Computing MAX
                        d__1 = sumin;
                        d__2 = (sl.getEntry(i) - xopt.getEntry(i)) / temp;
                        subd = Math.max(d__1, d__2);
                        iubd = -i;
                    }
                }
            }

            // Seek a large modulus of the KNEW-th Lagrange function when the index
            // of the other interpolation point on the line through XOPT is KNEW.

            if (k == knew) {
                diff = dderiv - ONE;
                step = slbd;
                vlag = slbd * (dderiv - slbd * diff);
                isbd = ilbd;
                temp = subd * (dderiv - subd * diff);
                if (Math.abs(temp) > Math.abs(vlag)) {
                    step = subd;
                    vlag = temp;
                    isbd = iubd;
                }
                tempd = HALF * dderiv;
                tempa = tempd - diff * slbd;
                tempb = tempd - diff * subd;
                if (tempa * tempb < ZERO) {
                    temp = tempd * tempd / diff;
                    if (Math.abs(temp) > Math.abs(vlag)) {
                        step = tempd / diff;
                        vlag = temp;
                        isbd = 0;
                    }
                }

                // Search along each of the other lines through XOPT and another point.

            } else {
                step = slbd;
                vlag = slbd * (ONE - slbd);
                isbd = ilbd;
                temp = subd * (ONE - subd);
                if (Math.abs(temp) > Math.abs(vlag)) {
                    step = subd;
                    vlag = temp;
                    isbd = iubd;
                }
                if (subd > HALF) {
                    if (Math.abs(vlag) < .25) {
                        step = HALF;
                        vlag = ONE_OVER_FOUR;
                        isbd = 0;
                    }
                }
                vlag *= dderiv;
            }

            // Calculate PREDSQ for the current line search and maintain PRESAV.

            temp = step * (ONE - step) * distsq;
            predsq = vlag * vlag * (vlag * vlag + ha * temp * temp);
            if (predsq > presav) {
                presav = predsq;
                ksav = k;
                stpsav = step;
                ibdsav = isbd;
            }
        }

        // Construct XNEW in a way that satisfies the bound constraints exactly.

        for (int i = 1; i <= n; i++) {
            temp = xopt.getEntry(i) + stpsav * (xpt.getEntry(ksav, i) - xopt.getEntry(i));
            // Computing MAX
            // Computing MIN
            d__3 = su.getEntry(i);
            d__1 = sl.getEntry(i);
            d__2 = Math.min(d__3, temp);
            xnew.setEntry(i, Math.max(d__1, d__2));
        }
        if (ibdsav < 0) {
            xnew.setEntry(-ibdsav, sl.getEntry(-ibdsav));
        }
        if (ibdsav > 0) {
            xnew.setEntry(ibdsav, su.getEntry(ibdsav));
        }

        // Prepare for the iterative method that assembles the constrained Cauchy
        // step in W. The sum of squares of the fixed components of W is formed in
        // WFIXSQ, and the free components of W are set to BIGSTP.

        bigstp = adelt + adelt;
        iflag = 0;

        L100: for(;;) {
            wfixsq = ZERO;
            ggfree = ZERO;
            for (int i = 1; i <= n; i++) {
                work1.setEntry(i, ZERO);
                // Computing MIN
                d__1 = xopt.getEntry(i) - sl.getEntry(i);
                d__2 = glag.getEntry(i);
                tempa = Math.min(d__1, d__2);
                // Computing MAX
                d__1 = xopt.getEntry(i) - su.getEntry(i);
                d__2 = glag.getEntry(i);
                tempb = Math.max(d__1, d__2);
                if (tempa > ZERO || tempb < ZERO) {
                    work1.setEntry(i, bigstp);
                    // Computing 2nd power
                    final double d1 = glag.getEntry(i);
                    ggfree += d1 * d1;
                }
            }
            if (ggfree == ZERO) {
                cauchy = ZERO;
                return new double[] { alpha, cauchy };
            }

            // Investigate whether more components of W can be fixed.
            L120: {
                temp = adelt * adelt - wfixsq;
                if (temp > ZERO) {
                    wsqsav = wfixsq;
                    step = Math.sqrt(temp / ggfree);
                    ggfree = ZERO;
                    for (int i = 1; i <= n; i++) {
                        if (work1.getEntry(i) == bigstp) {
                            temp = xopt.getEntry(i) - step * glag.getEntry(i);
                            if (temp <= sl.getEntry(i)) {
                                work1.setEntry(i, sl.getEntry(i) - xopt.getEntry(i));
                                // Computing 2nd power
                                final double d1 = work1.getEntry(i);
                                wfixsq += d1 * d1;
                            } else if (temp >= su.getEntry(i)) {
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
                    if (!(wfixsq > wsqsav && ggfree > ZERO)) {
                        break L120;
                    }
                }} // end L120

            // Set the remaining free components of W and all components of XALT,
            // except that W may be scaled later.

            gw = ZERO;
            for (int i = 1; i <= n; i++) {
                if (work1.getEntry(i) == bigstp) {
                    work1.setEntry(i, -step * glag.getEntry(i));
                    final double min = Math.min(su.getEntry(i),
                                                xopt.getEntry(i) + work1.getEntry(i));
                    xalt.setEntry(i, Math.max(sl.getEntry(i), min));
                } else if (work1.getEntry(i) == ZERO) {
                    xalt.setEntry(i, xopt.getEntry(i));
                } else if (glag.getEntry(i) > ZERO) {
                    xalt.setEntry(i, sl.getEntry(i));
                } else {
                    xalt.setEntry(i, su.getEntry(i));
                }
                gw += glag.getEntry(i) * work1.getEntry(i);
            }

            // Set CURV to the curvature of the KNEW-th Lagrange function along W.
            // Scale W by a factor less than one if that can reduce the modulus of
            // the Lagrange function at XOPT+W. Set CAUCHY to the final value of
            // the square of this function.

            curv = ZERO;
            for (int k = 1; k <= npt; k++) {
                temp = ZERO;
                for (int j = 1; j <= n; j++) {
                    temp += xpt.getEntry(k, j) * work1.getEntry(j);
                }
                curv += hcol.getEntry(k) * temp * temp;
            }
            if (iflag == 1) {
                curv = -curv;
            }
            if (curv > -gw && curv < -const__ * gw) {
                scale = -gw / curv;
                for (int i = 1; i <= n; i++) {
                    temp = xopt.getEntry(i) + scale * work1.getEntry(i);
                    // Computing MAX
                    // Computing MIN
                    d__3 = su.getEntry(i);
                    d__2 = Math.min(d__3, temp);
                    xalt.setEntry(i, Math.max(sl.getEntry(i), d__2));
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
                for (int i = 1; i <= n; i++) {
                    glag.setEntry(i, -glag.getEntry(i));
                    work2.setEntry(i, xalt.getEntry(i));
                }
                csave = cauchy;
                iflag = 1;
            } else {
                break L100;
            }} // end L100
        if (csave > cauchy) {
            for (int i = 1; i <= n; i++) {
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
            FortranArray xbase,
            FortranMatrix xpt,
            FortranArray fval,
            FortranArray gopt,
            FortranArray hq,
            FortranArray pq,
            FortranMatrix bmat,
            FortranMatrix zmat,
            FortranArray sl,
            FortranArray su
    ) {
        // System.out.println("prelim"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;
        final int ndim = bmat.getRowDimension();

        final double rhosq = initialTrustRegionRadius * initialTrustRegionRadius;
        final double recip = ONE / rhosq;
        final int np = n + 1;

        // System generated locals
        double d__1, d__2, d__3, d__4;

        // Local variables
        double f;
        int ih, nfm;
        int nfx = 0, ipt = 0, jpt = 0;
        double fbeg = 0, diff = 0, temp = 0, stepa = 0, stepb = 0;
        int itemp;

        // Set some constants.

        // Function Body

        // Set XBASE to the initial vector of variables, and set the initial
        // elements of XPT, BMAT, HQ, PQ and ZMAT to zero.

        for (int j = 1; j <= n; j++) {
            xbase.setEntry(j, currentBest.getEntry(f2jai(j)));
            for (int k = 1; k <= npt; k++) {
                xpt.setEntry(k, j, ZERO);
            }
            for (int i = 1; i <= ndim; i++) {
                bmat.setEntry(i, j, ZERO);
            }
        }
        for (int i = 1, max = n * np / 2; i <= max; i++) {
            hq.setEntry(i, ZERO);
        }
        for (int k = 1; k <= npt; k++) {
            pq.setEntry(k, ZERO);
            for (int j = 1, max = npt - np; j <= max; j++) {
                zmat.setEntry(k, j, ZERO);
            }
        }

        // Begin the initialization procedure. NF becomes one more than the number
        // of function values so far. The coordinates of the displacement of the
        // next initial interpolation point from XBASE are set in XPT(NF+1,.).

        do {
            nfm = getEvaluations();
            nfx = getEvaluations() - n;
            final int curNumEvalPlusOne = getEvaluations() + 1;
            if (nfm <= n << 1) {
                if (nfm >= 1 && nfm <= n) {
                    stepa = initialTrustRegionRadius;
                    if (su.getEntry(nfm) == ZERO) {
                        stepa = -stepa;
                    }
                    xpt.setEntry(curNumEvalPlusOne, nfm, stepa);
                } else if (nfm > n) {
                    stepa = xpt.getEntry(curNumEvalPlusOne - n, nfx);
                    stepb = -initialTrustRegionRadius;
                    if (sl.getEntry(nfx) == ZERO) {
                        // Computing MIN
                        final double d1 = TWO * initialTrustRegionRadius;
                        stepb = Math.min(d1, su.getEntry(nfx));
                    }
                    if (su.getEntry(nfx) == ZERO) {
                        // Computing MAX
                        final double d1 = -TWO * initialTrustRegionRadius;
                        stepb = Math.max(d1, sl.getEntry(nfx));
                    }
                    xpt.setEntry(curNumEvalPlusOne, nfx, stepb);
                }
            } else {
                itemp = (nfm - np) / n;
                jpt = nfm - itemp * n - n;
                ipt = jpt + itemp;
                if (ipt > n) {
                    itemp = jpt;
                    jpt = ipt - n;
                    ipt = itemp;
                }
                xpt.setEntry(curNumEvalPlusOne, ipt, xpt.getEntry(ipt + 1, ipt));
                xpt.setEntry(curNumEvalPlusOne, jpt, xpt.getEntry(jpt + 1, jpt));
            }

            // Calculate the next value of F. The least function value so far and
            // its index are required.

            for (int j = 1; j <= n; j++) {
                // Computing MIN
                // Computing MAX
                d__3 = lowerBound[f2jai(j)];
                d__4 = xbase.getEntry(j) + xpt.getEntry(curNumEvalPlusOne, j);
                d__1 = Math.max(d__3, d__4);
                d__2 = upperBound[f2jai(j)];
                currentBest.setEntry(f2jai(j), Math.min(d__1, d__2));
                if (xpt.getEntry(curNumEvalPlusOne, j) == sl.getEntry(j)) {
                    currentBest.setEntry(f2jai(j), lowerBound[f2jai(j)]);
                }
                if (xpt.getEntry(curNumEvalPlusOne, j) == su.getEntry(j)) {
                    currentBest.setEntry(f2jai(j), upperBound[f2jai(j)]);
                }
            }

            f = computeObjectiveValue(currentBest.getData());

            if (!isMinimize)
                f = -f;
            fval.setEntry(getEvaluations(), f);
            if (getEvaluations() == 1) {
                fbeg = f;
                trustRegionCenterInterpolationPointIndex = 1;
            } else if (f < fval.getEntry(trustRegionCenterInterpolationPointIndex)) {
                trustRegionCenterInterpolationPointIndex = getEvaluations();
            }

            // Set the nonzero initial elements of BMAT and the quadratic model in the
            // cases when NF is at most 2*N+1. If NF exceeds N+1, then the positions
            // of the NF-th and (NF-N)-th interpolation points may be switched, in
            // order that the function value at the first of them contributes to the
            // off-diagonal second derivative terms of the initial quadratic model.

            if (getEvaluations() <= (n << 1) + 1) {
                if (getEvaluations() >= 2 && getEvaluations() <= n + 1) {
                    gopt.setEntry( nfm, (f - fbeg) / stepa);
                    if (npt < getEvaluations() + n) {
                        bmat.setEntry(1, nfm, -ONE / stepa);
                        bmat.setEntry( getEvaluations(), nfm, ONE / stepa);
                        bmat.setEntry( npt + nfm, nfm, -HALF * rhosq);
                    }
                } else if (getEvaluations() >= n + 2) {
                    ih = nfx * (nfx + 1) / 2;
                    temp = (f - fbeg) / stepb;
                    diff = stepb - stepa;
                    hq.setEntry(ih, TWO * (temp - gopt.getEntry(nfx)) / diff);
                    gopt.setEntry(nfx, (gopt.getEntry(nfx) * stepb - temp * stepa) / diff);
                    if (stepa * stepb < ZERO) {
                        if (f < fval.getEntry(getEvaluations() - n)) {
                            fval.setEntry(getEvaluations(), fval.getEntry(getEvaluations() - n));
                            fval.setEntry(getEvaluations() - n, f);
                            if (trustRegionCenterInterpolationPointIndex == getEvaluations()) {
                                trustRegionCenterInterpolationPointIndex = getEvaluations() - n;
                            }
                            xpt.setEntry(getEvaluations() - n, nfx, stepb);
                            xpt.setEntry(getEvaluations(), nfx, stepa);
                        }
                    }
                    bmat.setEntry(1, nfx, -(stepa + stepb) / (stepa * stepb));
                    bmat.setEntry( getEvaluations(), nfx, -HALF /
                                   xpt.getEntry(getEvaluations() - n, nfx));
                    bmat.setEntry( getEvaluations() - n, nfx, -bmat.getEntry(1, nfx) -
                                   bmat.getEntry( getEvaluations(), nfx));
                    zmat.setEntry(1, nfx, Math.sqrt(TWO) / (stepa * stepb));
                    zmat.setEntry( getEvaluations(), nfx, Math.sqrt(HALF) / rhosq);
                    zmat.setEntry( getEvaluations() - n, nfx, -zmat.getEntry(1, nfx) -
                                   zmat.getEntry( getEvaluations(), nfx));
                }

                // Set the off-diagonal second derivatives of the Lagrange functions and
                // the initial quadratic model.

            } else {
                ih = ipt * (ipt - 1) / 2 + jpt;
                zmat.setEntry(1, nfx, recip);
                zmat.setEntry( getEvaluations(), nfx, recip);
                zmat.setEntry(ipt + 1, nfx, -recip);
                zmat.setEntry( jpt + 1, nfx, -recip);
                temp = xpt.getEntry(getEvaluations(), ipt) * xpt.getEntry(getEvaluations(), jpt);
                hq.setEntry(ih, (fbeg - fval.getEntry(ipt + 1) - fval.getEntry(jpt + 1) + f) / temp);
            }
        } while (getEvaluations() < npt);
    } // prelim

    // ----------------------------------------------------------------------------------------

    /**
     *     The first NDIM+NPT elements of the array W are used for working space.
     *     The final elements of BMAT and ZMAT are set in a well-conditioned way
     *       to the values that are appropriate for the new interpolation points.
     *     The elements of GOPT, HQ and PQ are also revised to the values that are
     *       appropriate to the final quadratic model.
     *
     *     The arguments N, NPT, XL, XU, IPRINT, MAXFUN, XBASE, XPT, FVAL, XOPT,
     *       GOPT, HQ, PQ, BMAT, ZMAT, NDIM, SL and SU have the same meanings as
     *       the corresponding arguments of BOBYQB on the entry to RESCUE.
     *     NF is maintained as the number of calls of CALFUN so far, except that
     *       NF is set to -1 if the value of MAXFUN prevents further progress.
     *     KOPT is maintained so that FVAL(KOPT) is the least calculated function
     *       value. Its correct value must be given on entry. It is updated if a
     *       new least function value is found, but the corresponding changes to
     *       XOPT and GOPT have to be made later by the calling program.
     *     DELTA is the current trust region radius.
     *     VLAG is a working space vector that will be used for the values of the
     *       provisional Lagrange functions at each of the interpolation points.
     *       They are part of a product that requires VLAG to be of length NDIM.
     *     PTSAUX is also a working space array. For J=1,2,...,N, PTSAUX(1,J) and
     *       PTSAUX(2,J) specify the two positions of provisional interpolation
     *       points when a nonzero step is taken along e_J (the J-th coordinate
     *       direction) through XBASE+XOPT, as specified below. Usually these
     *       steps have length DELTA, but other lengths are chosen if necessary
     *       in order to satisfy the given bounds on the variables.
     *     PTSID is also a working space array. It has NPT components that denote
     *       provisional new positions of the original interpolation points, in
     *       case changes are needed to restore the linear independence of the
     *       interpolation conditions. The K-th point is a candidate for change
     *       if and only if PTSID(K) is nonzero. In this case let p and q be the
     *       int parts of PTSID(K) and (PTSID(K)-p) multiplied by N+1. If p
     *       and q are both positive, the step from XBASE+XOPT to the new K-th
     *       interpolation point is PTSAUX(1,p)*e_p + PTSAUX(1,q)*e_q. Otherwise
     *       the step is PTSAUX(1,p)*e_p or PTSAUX(2,q)*e_q in the cases q=0 or
     *       p=0, respectively.
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
     * @param delta
     * @param vlag
     */
    private void rescue(
            FortranArray xbase,
            FortranMatrix xpt,
            FortranArray fval,
            FortranArray xopt,
            FortranArray gopt,
            FortranArray hq,
            FortranArray pq,
            FortranMatrix bmat,
            FortranMatrix zmat,
            FortranArray sl,
            FortranArray su,
            double delta,
            FortranArray vlag
    ) {
        // System.out.println("rescue"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;
        final int ndim = bmat.getRowDimension();

        final FortranMatrix ptsaux = new FortranMatrix(n, 2);
        final FortranArray ptsid = new FortranArray(npt);

        final FortranArray work1 = new FortranArray(npt); // Originally: w(1 .. npt).
        final FortranArray work2 = new FortranArray(n); // Originally: w(npt+1 .. npt+n).
        final FortranArray work3 = new FortranArray(npt); // Originally: w(npt+n+1 .. npt+n+npt).

        final int np = n + 1;
        final double sfrac = HALF / (double) np;
        final int nptm = npt - np;

        // System generated locals
        double d__1, d__2, d__3, d__4;


        // Local variables
        double f;
        int ih, jp, ip, iq;
        double xp = 0, xq = 0, den = 0;
        int ihp = 0;
        int jpn, kpt;
        double sum = 0, diff = 0, beta = 0;
        int kold;
        double winc;
        int nrem, knew;
        double temp, bsum;
        double hdiag = 0, fbase = 0, denom = 0, vquad = 0, sumpq = 0;
        double dsqmin, distsq, vlmxsq;

        // Set some constants.

        // Function Body

        // Shift the interpolation points so that XOPT becomes the origin, and set
        // the elements of ZMAT to zero. The value of SUMPQ is required in the
        // updating of HQ below. The squares of the distances from XOPT to the
        // other interpolation points are set at the end of W. Increments of WINC
        // may be added later to these squares to balance the consideration of
        // the choice of point that is going to become current.

        sumpq = ZERO;
        winc = ZERO;
        for (int k = 1; k <= npt; k++) {
            distsq = ZERO;
            for (int j = 1; j <= n; j++) {
                xpt.setEntry(k, j, xpt.getEntry(k, j) - xopt.getEntry(j));
                // Computing 2nd power
                final double d1 = xpt.getEntry(k, j);
                distsq += d1 * d1;
            }
            sumpq += pq.getEntry(k);
            work3.setEntry(k, distsq);
            winc = Math.max(winc, distsq);
            for (int j = 1; j <= nptm; j++) {
                zmat.setEntry(k, j, ZERO);
            }
        }

        // Update HQ so that HQ and PQ define the second derivatives of the model
        // after XBASE has been shifted to the trust region centre.

        ih = 0;
        for (int j = 1; j <= n; j++) {
            work2.setEntry(j, HALF * sumpq * xopt.getEntry(j));
            for (int k = 1; k <= npt; k++) {
                work2.setEntry(j, work2.getEntry(j) + pq.getEntry(k) * xpt.getEntry(k, j));
            }
            for (int i = 1; i <= j; i++) {
                ++ih;
                hq.setEntry(ih, hq.getEntry(ih) + work2.getEntry(i) * xopt.getEntry(j) + work2.getEntry(j) * xopt.getEntry(i));
            }
        }

        // Shift XBASE, SL, SU and XOPT. Set the elements of BMAT to zero, and
        // also set the elements of PTSAUX.

        for (int j = 1; j <= n; j++) {
            xbase.setEntry(j, xbase.getEntry(j) + xopt.getEntry(j));
            sl.setEntry(j, sl.getEntry(j) - xopt.getEntry(j));
            su.setEntry(j, su.getEntry(j) - xopt.getEntry(j));
            xopt.setEntry(j, ZERO);
            // Computing MIN
            d__1 = delta;
            d__2 = su.getEntry(j);
            ptsaux.setEntry(j, 1, Math.min(d__1, d__2));
            // Computing MAX
            d__1 = -delta;
            d__2 = sl.getEntry(j);
            ptsaux.setEntry(j, 2, Math.max(d__1, d__2));
            if (ptsaux.getEntry(j, 1) + ptsaux.getEntry(j, 2) < ZERO) {
                temp = ptsaux.getEntry(j, 1);
                ptsaux.setEntry(j, 1, ptsaux.getEntry(j, 2));
                ptsaux.setEntry(j, 2, temp);
            }
            d__2 = ptsaux.getEntry(j, 2);
            d__1 = ptsaux.getEntry(j, 1);
            if (Math.abs(d__2) < HALF * Math.abs(d__1)) {
                ptsaux.setEntry(j, 2, HALF * ptsaux.getEntry(j, 1));
            }
            for (int i = 1; i <= ndim; i++) {
                bmat.setEntry(i, j, ZERO);
            }
        }
        fbase = fval.getEntry(trustRegionCenterInterpolationPointIndex);

        // Set the identifiers of the artificial interpolation points that are
        // along a coordinate direction from XOPT, and set the corresponding
        // nonzero elements of BMAT and ZMAT.

        ptsid.setEntry(1, sfrac);
        for (int j = 1; j <= n; j++) {
            jp = j + 1;
            jpn = jp + n;
            ptsid.setEntry(jp, (double) j + sfrac);
            if (jpn <= npt) {
                ptsid.setEntry(jpn, (double) j / (double) np + sfrac);
                temp = ONE / (ptsaux.getEntry(j, 1) - ptsaux.getEntry(j, 2));
                bmat.setEntry(jp, j, -temp + ONE / ptsaux.getEntry(j, 1));
                bmat.setEntry(jpn, j, temp + ONE / ptsaux.getEntry(j, 2));
                bmat.setEntry(1, j, -bmat.getEntry(jp, j) - bmat.getEntry(jpn, j));
                final double d1 = ptsaux.getEntry(j, 1) * ptsaux.getEntry(j, 2);
                zmat.setEntry(1, j,  Math.sqrt(TWO) / Math.abs(d1));
                zmat.setEntry(jp, j, zmat.getEntry(1, j) *
                        ptsaux.getEntry(j, 2) * temp);
                zmat.setEntry(jpn, j, -zmat.getEntry(1, j) *
                        ptsaux.getEntry(j, 1) * temp);
            } else {
                bmat.setEntry(1, j, -ONE / ptsaux.getEntry(j, 1));
                bmat.setEntry(jp, j, ONE / ptsaux.getEntry(j, 1));
                // Computing 2nd power
                final double d1 = ptsaux.getEntry(j, 1);
                bmat.setEntry(j + npt, j, -HALF * (d1 * d1));
            }
        }

        // Set any remaining identifiers with their nonzero elements of ZMAT.

        if (npt >= n + np) {
            for (int k = np << 1; k <= npt; k++) {
                int iw = (int) (((double) (k - np) - HALF) / (double) n);
                ip = k - np - iw * n;
                iq = ip + iw;
                if (iq > n) {
                    iq -= n;
                }
                ptsid.setEntry(k, (double) ip + (double) iq / (double) np +
                        sfrac);
                temp = ONE / (ptsaux.getEntry(ip, 1) * ptsaux.getEntry(iq, 1));
                zmat.setEntry(1, (k - np), temp);
                zmat.setEntry(ip + 1, k - np, -temp);
                zmat.setEntry(iq + 1, k - np, -temp);
                zmat.setEntry(k, k - np, temp);
            }
        }
        nrem = npt;
        kold = 1;
        knew = trustRegionCenterInterpolationPointIndex;

        // Reorder the provisional points in the way that exchanges PTSID(KOLD)
        // with PTSID(KNEW).

        int state = 80;
        for(;;) switch (state) {
        case 80: {
            for (int j = 1; j <= n; j++) {
                temp = bmat.getEntry(kold, j);
                bmat.setEntry(kold, j, bmat.getEntry(knew, j));
                bmat.setEntry(knew, j, temp);
            }
            for (int j = 1; j <= nptm; j++) {
                temp = zmat.getEntry(kold, j);
                zmat.setEntry(kold, j, zmat.getEntry(knew, j));
                zmat.setEntry(knew, j, temp);
            }
            ptsid.setEntry(kold, ptsid.getEntry(knew));
            ptsid.setEntry(knew, ZERO);
            work3.setEntry(knew, ZERO);
            --nrem;
            if (knew != trustRegionCenterInterpolationPointIndex) {
                temp = vlag.getEntry(kold);
                vlag.setEntry(kold, vlag.getEntry(knew));
                vlag.setEntry(knew, temp);

                // Update the BMAT and ZMAT matrices so that the status of the KNEW-th
                // interpolation point can be changed from provisional to original. The
                // branch to label 350 occurs if all the original points are reinstated.
                // The nonnegative values of W(NDIM+K) are required in the search below.

                update(bmat, zmat, vlag,
                        beta, denom, knew);

                if (nrem == 0) {
                    return;
                }
                for (int k = 1; k <= npt; k++) {
                    work3.setEntry(k, Math.abs(work3.getEntry(k)));
                }
            }

            // Pick the index KNEW of an original interpolation point that has not
            // yet replaced one of the provisional interpolation points, giving
            // attention to the closeness to XOPT and to previous tries with KNEW.
        }
        case 120: {
            dsqmin = ZERO;
            for (int k = 1; k <= npt; k++) {
                final double v1 = work3.getEntry(k);
                if (v1 > ZERO) {
                    if (dsqmin == ZERO ||
                        v1 < dsqmin) {
                        knew = k;
                        dsqmin = v1;
                    }
                }
            }
            if (dsqmin == ZERO) {
                state = 260; break;
            }

            // Form the W-vector of the chosen original interpolation point.

            for (int j = 1; j <= n; j++) {
                work2.setEntry(j, xpt.getEntry(knew, j));
            }
            for (int k = 1; k <= npt; k++) {
                sum = ZERO;
                if (k == trustRegionCenterInterpolationPointIndex) {
                } else if (ptsid.getEntry(k) == ZERO) {
                    for (int j = 1; j <= n; j++) {
                        sum += work2.getEntry(j) * xpt.getEntry(k, j);
                    }
                } else {
                    ip = (int) ptsid.getEntry(k);
                    if (ip > 0) {
                        sum = work2.getEntry(ip) * ptsaux.getEntry(ip, 1);
                    }
                    iq = (int) ((double) np * ptsid.getEntry(k) - (double) (ip * np));
                    if (iq > 0) {
                        int iw = 1;
                        if (ip == 0) {
                            iw = 2;
                        }
                        sum += work2.getEntry(iq) * ptsaux.getEntry(iq, iw);
                    }
                }
                work1.setEntry(k, HALF * sum * sum);
            }

            // Calculate VLAG and BETA for the required updating of the H matrix if
            // XPT(KNEW,.) is reinstated in the set of interpolation points.

            for (int k = 1; k <= npt; k++) {
                sum = ZERO;
                for (int j = 1; j <= n; j++) {
                    sum += bmat.getEntry(k, j) * work2.getEntry(j);
                }
                vlag.setEntry(k, sum);
            }
            beta = ZERO;
            for (int j = 1; j <= nptm; j++) {
                sum = ZERO;
                for (int k = 1; k <= npt; k++) {
                    sum += zmat.getEntry(k, j) * work1.getEntry(k);
                }
                beta -= sum * sum;
                for (int k = 1; k <= npt; k++) {
                    vlag.setEntry(k, vlag.getEntry(k) + sum * zmat.getEntry(k, j));
                }
            }
            bsum = ZERO;
            distsq = ZERO;
            for (int j = 1; j <= n; j++) {
                sum = ZERO;
                for (int k = 1; k <= npt; k++) {
                    sum += bmat.getEntry(k, j) * work1.getEntry(k);
                }
                jp = j + npt;
                bsum += sum * work2.getEntry(j);
                for (int k = 1; k <= n; k++) {
                    sum += bmat.getEntry(npt + k, j) * work2.getEntry(k);
                }
                bsum += sum * work2.getEntry(j);
                vlag.setEntry(jp, sum);
                // Computing 2nd power
                final double d1 = xpt.getEntry(knew, j);
                distsq += d1 * d1;
            }
            beta = HALF * distsq * distsq + beta - bsum;
            vlag.setEntry(trustRegionCenterInterpolationPointIndex, vlag.getEntry(trustRegionCenterInterpolationPointIndex) + ONE);

            // KOLD is set to the index of the provisional interpolation point that is
            // going to be deleted to make way for the KNEW-th original interpolation
            // point. The choice of KOLD is governed by the avoidance of a small value
            // of the denominator in the updating calculation of UPDATE.

            denom = ZERO;
            vlmxsq = ZERO;
            for (int k = 1; k <= npt; k++) {
                if (ptsid.getEntry(k) != ZERO) {
                    hdiag = ZERO;
                    for (int j = 1; j <= nptm; j++) {
                        // Computing 2nd power
                        final double d1 = zmat.getEntry(k, j);
                        hdiag += d1 * d1;
                    }
                    // Computing 2nd power
                    final double d1 = vlag.getEntry(k);
                    den = beta * hdiag + d1 * d1;
                    if (den > denom) {
                        kold = k;
                        denom = den;
                    }
                }
                // Computing MAX
                // Computing 2nd power
                final double d3 = vlag.getEntry(k);
                vlmxsq = Math.max(vlmxsq , d3 * d3);
            }
            if (denom <= vlmxsq * .01) {
                work3.setEntry(knew, -work3.getEntry(knew) - winc);
                state = 120; break;
            }
            state = 80; break;

            // When label 260 is reached, all the final positions of the interpolation
            // points have been chosen although any changes have not been included yet
            // in XPT. Also the final BMAT and ZMAT matrices are complete, but, apart
            // from the shift of XBASE, the updating of the quadratic model remains to
            // be done. The following cycle through the new interpolation points begins
            // by putting the new point in XPT(KPT,.) and by setting PQ(KPT) to zero,
            // except that a RETURN occurs if MAXFUN prohibits another value of F.

        }
        case 260: {
            for (kpt = 1; kpt <= npt; kpt++) {
                if (ptsid.getEntry(kpt) == ZERO) {
                    continue;
                }
                ih = 0;
                for (int j = 1; j <= n; j++) {
                    work2.setEntry(j, xpt.getEntry(kpt, j));
                    xpt.setEntry(kpt, j, ZERO);
                    temp = pq.getEntry(kpt) * work2.getEntry(j);
                    for (int i = 1; i <= j; i++) {
                        ++ih;
                        hq.setEntry(ih, hq.getEntry(ih) + temp * work2.getEntry(i));
                    }
                }
                pq.setEntry(kpt, ZERO);
                ip = (int) ptsid.getEntry(kpt);
                iq = (int) ((double) np * ptsid.getEntry(kpt) - (double) (ip * np));
                if (ip > 0) {
                    xp = ptsaux.getEntry(ip, 1);
                    xpt.setEntry(kpt, ip, xp);
                }
                if (iq > 0) {
                    xq = ptsaux.getEntry(iq, 1);
                    if (ip == 0) {
                        xq = ptsaux.getEntry(iq, 2);
                    }
                    xpt.setEntry(kpt, iq, xq);
                }

                // Set VQUAD to the value of the current model at the new point.

                vquad = fbase;
                if (ip > 0) {
                    ihp = (ip + ip * ip) / 2;
                    vquad += xp * (gopt.getEntry(ip) + HALF * xp * hq.getEntry(ihp));
                }
                if (iq > 0) {
                    int ihq = (iq + iq * iq) / 2;
                    vquad += xq * (gopt.getEntry(iq) + HALF * xq * hq.getEntry(ihq));
                    if (ip > 0) {
                        int iiw = Math.max(ihp, ihq) - Math.abs(ip - iq);
                        vquad += xp * xq * hq.getEntry(iiw);
                    }
                }
                for (int k = 1; k <= npt; k++) {
                    temp = ZERO;
                    if (ip > 0) {
                        temp += xp * xpt.getEntry(k, ip);
                    }
                    if (iq > 0) {
                        temp += xq * xpt.getEntry(k, iq);
                    }
                    vquad += HALF * pq.getEntry(k) * temp * temp;
                }

                // Calculate F at the new interpolation point, and set DIFF to the factor
                // that is going to multiply the KPT-th Lagrange function when the model
                // is updated to provide interpolation to the new function value.

                for (int i = 1; i <= n; i++) {
                    // Computing MIN
                    // Computing MAX
                    d__3 = lowerBound[f2jai(i)];
                    d__4 = xbase.getEntry(i) + xpt.getEntry(kpt, i);
                    d__1 = Math.max(d__3, d__4);
                    d__2 = upperBound[f2jai(i)];
                    work2.setEntry(i, Math.min(d__1, d__2));
                    if (xpt.getEntry(kpt, i) == sl.getEntry(i)) {
                        work2.setEntry(i, lowerBound[f2jai(i)]);
                    }
                    if (xpt.getEntry(kpt, i) == su.getEntry(i)) {
                        work2.setEntry(i, upperBound[f2jai(i)]);
                    }
                }

                f = computeObjectiveValue(work2.getData());

                if (!isMinimize)
                    f = -f;
                fval.setEntry(kpt, f);
                if (f < fval.getEntry(trustRegionCenterInterpolationPointIndex)) {
                    trustRegionCenterInterpolationPointIndex = kpt;
                }
                diff = f - vquad;

                // Update the quadratic model. The RETURN from the subroutine occurs when
                // all the new interpolation points are included in the model.

                for (int i = 1; i <= n; i++) {
                    gopt.setEntry(i, gopt.getEntry(i) + diff * bmat.getEntry(kpt, i));
                }
                for (int k = 1; k <= npt; k++) {
                    sum = ZERO;
                    for (int j = 1; j <= nptm; j++) {
                        sum += zmat.getEntry(k, j) * zmat.getEntry(kpt, j);
                    }
                    temp = diff * sum;
                    if (ptsid.getEntry(k) == ZERO) {
                        pq.setEntry(k, pq.getEntry(k) + temp);
                    } else {
                        ip = (int) ptsid.getEntry(k);
                        iq = (int) ((double) np * ptsid.getEntry(k) - (double) (ip * np));
                        int ihq = (iq * iq + iq) / 2;
                        if (ip == 0) {
                            // Computing 2nd power
                            final double d1 = ptsaux.getEntry(iq, 2);
                            hq.setEntry(ihq, hq.getEntry(ihq) + temp * (d1 * d1));
                        } else {
                            ihp = (ip * ip + ip) / 2;
                            // Computing 2nd power
                            final double d1 = ptsaux.getEntry(ip, 1);
                            hq.setEntry(ihp, hq.getEntry(ihp) + temp * (d1 * d1));
                            if (iq > 0) {
                                // Computing 2nd power
                                final double d2 = ptsaux.getEntry(iq, 1);
                                hq.setEntry(ihq, hq.getEntry(ihq) + temp * (d2 * d2));
                                int iw = Math.max(ihp,ihq) - Math.abs(iq - ip);
                                hq.setEntry(iw, hq.getEntry(iw)
                                            + temp * ptsaux.getEntry(ip, 1) * ptsaux.getEntry(iq, 1));
                            }
                        }
                    }
                }
                ptsid.setEntry(kpt, ZERO);
            }
            return;
        }}
    } // rescue



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
            FortranMatrix xpt,
            FortranArray xopt,
            FortranArray gopt,
            FortranArray hq,
            FortranArray pq,
            FortranArray sl,
            FortranArray su,
            double delta,
            FortranArray xnew,
            FortranArray d__,
            FortranArray gnew,
            FortranArray xbdi,
            FortranArray s,
            FortranArray hs,
            FortranArray hred
    ) {
        // System.out.println("trsbox"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;

        double dsq = Double.NaN;
        double crvmin = Double.NaN;

        // System generated locals
        double d__1, d__2, d__3, d__4;

        // Local variables
        int ih;
        double ds;
        int iu;
        double dhd, dhs, cth, shs, sth, ssq, beta=0, sdec, blen;
        int iact = 0, nact = 0;
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
        for (int i = 1; i <= n; i++) {
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
        for(;;) switch (state) {

        case 20: {
            beta = ZERO;
        }
        case 30: {
            stepsq = ZERO;
            for (int i = 1; i <= n; i++) {
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
            for (int i = 1; i <= n; i++) {
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

            iact = 0;
            for (int i = 1; i <= n; i++) {
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
                if (iact == 0 && temp > ZERO) {
                    crvmin = Math.min(crvmin,temp);
                    if (crvmin == MINUS_ONE) {
                        crvmin = temp;
                    }
                }
                ggsav = gredsq;
                gredsq = ZERO;
                for (int i = 1; i <= n; i++) {
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

            if (iact > 0) {
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
            for (int i = 1; i <= n; i++) {
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
            for (int i = 1; i <= n; i++) {
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
            iact = 0;
            for (int i = 1; i <= n; i++) {
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
            for (int i = 1; i <= n; i++) {
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
            isav = 0;
            redsav = ZERO;
            iu = (int) (angbd * 17. + 3.1);
            for (int i = 1; i <= iu; i++) {
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

            if (isav == 0) {
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
            for (int i = 1; i <= n; i++) {
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
            if (iact > 0 && isav == iu) {
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
            for (int i = 1; i <= n; i++) {
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
            for (int j = 1; j <= n; j++) {
                hs.setEntry(j, ZERO);
                for (int i = 1; i <= j; i++) {
                    ++ih;
                    if (i < j) {
                        hs.setEntry(j, hs.getEntry(j) + hq.getEntry(ih) * s.getEntry(i));
                    }
                    hs.setEntry(i, hs.getEntry(i) + hq.getEntry(ih) * s.getEntry(j));
                }
            }
            for (int k = 1; k <= npt; k++) {
                if (pq.getEntry(k) != ZERO) {
                    temp = ZERO;
                    for (int j = 1; j <= n; j++) {
                        temp += xpt.getEntry(k, j) * s.getEntry(j);
                    }
                    temp *= pq.getEntry(k);
                    for (int i = 1; i <= n; i++) {
                        hs.setEntry(i, hs.getEntry(i) + temp * xpt.getEntry(k, i));
                    }
                }
            }
            if (crvmin != ZERO) {
                state = 50; break;
            }
            if (iterc > itcsav) {
                state = 150; break;
            }
            for (int i = 1; i <= n; i++) {
                hred.setEntry(i, hs.getEntry(i));
            }
            state = 120; break;
        }}
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
            FortranMatrix bmat,
            FortranMatrix zmat,
            FortranArray vlag,
            double beta,
            double denom,
            int knew
    ) {
        // System.out.println("update"); // XXX

        final int n = currentBest.getDimension();
        final int npt = numberOfInterpolationPoints;
        final int nptm = npt - n - 1;

        // XXX Should probably be split into two arrays.
        final FortranArray work = new FortranArray(npt + n);


        // System generated locals
        double d__1, d__2, d__3;

        // Local variables
        int jp;
        double tau, temp;
        double alpha, tempa, tempb, ztest;

        // Function Body

        ztest = ZERO;
        for (int k = 1; k <= npt; k++) {
            for (int j = 1; j <= nptm; j++) {
                // Computing MAX
                ztest = Math.max(ztest, Math.abs(zmat.getEntry(k, j)));
            }
        }
        ztest *= 1e-20;

        // Apply the rotations that put zeros in the KNEW-th row of ZMAT.

        for (int j = 2; j <= nptm; j++) {
            d__1 = zmat.getEntry(knew, j);
            if (Math.abs(d__1) > ztest) {
                // Computing 2nd power
                d__1 = zmat.getEntry(knew, 1);
                // Computing 2nd power
                d__2 = zmat.getEntry(knew, j);
                temp = Math.sqrt(d__1 * d__1 + d__2 * d__2);
                tempa = zmat.getEntry(knew, 1) / temp;
                tempb = zmat.getEntry(knew, j) / temp;
                for (int i = 1; i <= npt; i++) {
                    temp = tempa * zmat.getEntry(i, 1) + tempb * zmat.getEntry(i, j);
                    zmat.setEntry(i, j, tempa * zmat.getEntry(i, j) -
                                  tempb * zmat.getEntry(i, 1));
                    zmat.setEntry(i, 1, temp);
                }
            }
            zmat.setEntry(knew, j, ZERO);
        }

        // Put the first NPT components of the KNEW-th column of HLAG into W,
        // and calculate the parameters of the updating formula.

        for (int i = 1; i <= npt; i++) {
            work.setEntry(i, zmat.getEntry(knew, 1) * zmat.getEntry(i, 1));
        }
        alpha = work.getEntry(knew);
        tau = vlag.getEntry(knew);
        vlag.setEntry(knew, vlag.getEntry(knew) - ONE);

        // Complete the updating of ZMAT.

        temp = Math.sqrt(denom);
        tempb = zmat.getEntry(knew, 1) / temp;
        tempa = tau / temp;
        for (int i= 1; i <= npt; i++) {
            zmat.setEntry(i, 1, tempa * zmat.getEntry(i, 1) -
                    tempb * vlag.getEntry(i));
        }

        // Finally, update the matrix BMAT.

        for (int j = 1; j <= n; j++) {
            jp = npt + j;
            work.setEntry(jp, bmat.getEntry(knew, j));
            tempa = (alpha * vlag.getEntry(jp) - tau * work.getEntry(jp)) / denom;
            tempb = (-beta * work.getEntry(jp) - tau * vlag.getEntry(jp)) / denom;
            for (int i = 1; i <= jp; i++) {
                bmat.setEntry(i, j, bmat.getEntry(i, j) + tempa *
                        vlag.getEntry(i) + tempb * work.getEntry(i));
                if (i > npt) {
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


    // auxiliary subclasses

    /**
     * 1-based indexing vector
     */
    private static class FortranArray extends ArrayRealVector {
        public FortranArray(int size) {
            super(size);
        }
        public FortranArray(ArrayRealVector data) {
            super(data, false);
        }

        /** {@inheritDoc} */
        public double getEntry(int index) {
            return super.getEntry(index - 1);
        }

        /** {@inheritDoc} */
        public void setEntry(int index, double value) {
            super.setEntry(index - 1, value);
        }
    }

    /**
     * 1-based indexing matrix
     */
    private static class FortranMatrix extends Array2DRowRealMatrix {
        public FortranMatrix(int row, int column) {
            super(row, column);
        }
        /** {@inheritDoc} */
        public double getEntry(int row, int col) {
            return super.getEntry(row - 1, col - 1);
        }

        /** {@inheritDoc} */
        public void setEntry(int row, int col, double value) {
            super.setEntry(row - 1, col - 1, value);
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

    // Fortan (1-based) to Java (0-based) array index.
    // For use in Fortran-like 1-based loops.  Calls to this offset
    // function will be removed when all loops are converted to 0-base.
    private static int f2jai(int j) {
        return j - 1;
    }
}
