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
import org.apache.commons.math.exception.MultiDimensionMismatchException;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.MultivariateRealOptimizer;
import org.apache.commons.math.optimization.RealPointValuePair;

/**
 * BOBYQA algorithm. This code is translated and adapted from the Fortran version
 * of this algorithm as implemented in http://plato.asu.edu/ftp/other_software/bobyqa.zip .
 * <em>http://</em>. <br>
 * See <em>http://www.optimization-online.org/DB_HTML/2010/05/2616.html</em>
 * for an introduction.
 *
 * <p>BOBYQA is particularly well suited for high dimensional problems
 * where derivatives are not available. In most cases it outperforms the
 * PowellOptimizer significantly. Stochastic algorithms like CMAESOptimizer
 * succeed more often than BOBYQA, but are more expensive. BOBYQA could
 * also be considered if you currently use a derivative based (Differentiable)
 * optimizer approximating the derivatives by finite differences.
 *
 * Comments of the subroutines were copied directly from the original sources.
 *
 * @version $Revision$ $Date$
 * @since 3.0
 */

public class BOBYQAOptimizer extends
BaseAbstractScalarOptimizer<MultivariateRealFunction> implements
MultivariateRealOptimizer {

    /** Default value for {@link #initialTrustRegionRadius}: {@value} . */
    public static final double DEFAULT_INITIAL_RADIUS = 10.0;
    /** Default value for {@link #stoppingTrustRegionRadius}: {@value} . */
    public static final double DEFAULT_STOPPING_RADIUS = 1E-8;

    /**
     * numberOfInterpolationPoints
     */
    private int numberOfInterpolationPoints;
    /**
     * initialTrustRegionRadius;
     */
    private double initialTrustRegionRadius;
    /**
     * stoppingTrustRegionRadius;
     */
    private double stoppingTrustRegionRadius;
    /**
     * Lower and upper boundaries of the objective variables. boundaries == null
     * means no boundaries.
     */
    private double[][] boundaries;
    /** Number of objective variables/problem dimension */
    private int dimension;
    /** goal (minimize or maximize) */
    private boolean isMinimize = true;

    /**
     * Default constructor, uses default parameters
     */
    public BOBYQAOptimizer() {
        this(null);
    }

    /**
     * @param boundaries
     *             Boundaries for objective variables.
     */
    public BOBYQAOptimizer(double[][] boundaries) {
        this(boundaries, -1, DEFAULT_INITIAL_RADIUS,
                DEFAULT_STOPPING_RADIUS);
    }

    /**
     * @param boundaries
     *            Boundaries for objective variables.
     * @param numberOfInterpolationPoints
     *            number of interpolation conditions. Its value must be for
     *            dimension=N in the interval [N+2,(N+1)(N+2)/2]. Choices that
     *            exceed 2*N+1 are not recommended. -1 means undefined, then
     *            2*N+1 is used as default.
     * @param initialTrustRegionRadius
     *            initial trust region radius.
     * @param stoppingTrustRegionRadius
     *            stopping trust region radius.
     */
    public BOBYQAOptimizer(double[][] boundaries,
            int numberOfInterpolationPoints, double initialTrustRegionRadius,
            double stoppingTrustRegionRadius) {
        this.boundaries = boundaries;
        this.numberOfInterpolationPoints = numberOfInterpolationPoints;
        this.initialTrustRegionRadius = initialTrustRegionRadius;
        this.stoppingTrustRegionRadius = stoppingTrustRegionRadius;
    }

    /** {@inheritDoc} */
    @Override
    protected RealPointValuePair doOptimize() {
        // -------------------- Initialization --------------------------------
        isMinimize = getGoalType().equals(GoalType.MINIMIZE);
        final double[] guess = getStartPoint();
        // number of objective variables/problem dimension
        dimension = guess.length;
        checkParameters();
        if (numberOfInterpolationPoints < 0)
            numberOfInterpolationPoints = 2 * dimension + 1;
        ScopedPtr x = new ScopedPtr(guess.clone(), 0);
        ScopedPtr xl;
        ScopedPtr xu;
        if (boundaries != null) {
            xl = new ScopedPtr(boundaries[0].clone(), 0);
            xu = new ScopedPtr(boundaries[1].clone(), 0);
            double minDiff = Double.MAX_VALUE;
            for (int i = 0; i < dimension; i++) {
                double diff = boundaries[1][i] - boundaries[0][i];
                minDiff = Math.min(minDiff, diff);
            }
            if (minDiff < 2 * initialTrustRegionRadius)
                initialTrustRegionRadius = minDiff / 3.0;
        } else {
            xl = new ScopedPtr(point(dimension, -1e300), 0);
            xu = new ScopedPtr(point(dimension, 1e300), 0);
        }
        double value = bobyqa(dimension, numberOfInterpolationPoints, x, xl,
                xu, initialTrustRegionRadius, stoppingTrustRegionRadius,
                getMaxEvaluations());
        return new RealPointValuePair(x.getAll(), isMinimize ? value : -value);
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
     * @param n
     * @param npt
     * @param x
     * @param xl
     * @param xu
     * @param rhobeg
     * @param rhoend
     * @param maxfun
     * @return
     */
    private double bobyqa(
            int n,
            int npt,
            ScopedPtr x,
            ScopedPtr xl,
            ScopedPtr xu,
            double rhobeg,
            double rhoend,
            int maxfun
    ) {

        ScopedPtr w = new ScopedPtr(new double[(npt+5)*(npt+n)+3*n*(n+5)/2],0);

        // System generated locals
        int i__1;
        double d__1, d__2;

        // Local variables
        int j, id_, np, iw, igo, ihq, ixb, ixa, ifv, isl, jsl, ipq, ivl, ixn, ixo, ixp, isu, jsu, ndim;
        double temp, zero;
        int ibmat, izmat;

        // Parameter adjustments
        w = w.ptr(-1);
        xu = xu.ptr(-1);
        xl = xl.ptr(-1);
        x = x.ptr(-1);

        // Function Body
        np = n + 1;

        // Return if the value of NPT is unacceptable.
        if (npt < n + 2 || npt > (n + 2) * np / 2)
            throw new MathIllegalArgumentException(LocalizedFormats.NUMBER_OF_INTERPOLATION_POINTS, npt);

        // Partition the working space array, so that different parts of it can
        // be treated separately during the calculation of BOBYQB. The partition
        // requires the first (NPT+2)*(NPT+N)+3*N*(N+5)/2 elements of W plus the
        // space that is taken by the last array in the argument list of BOBYQB.

        ndim = npt + n;
        ixb = 1;
        ixp = ixb + n;
        ifv = ixp + n * npt;
        ixo = ifv + npt;
        igo = ixo + n;
        ihq = igo + n;
        ipq = ihq + n * np / 2;
        ibmat = ipq + npt;
        izmat = ibmat + ndim * n;
        isl = izmat+ npt * (npt - np);
        isu = isl + n;
        ixn = isu + n;
        ixa = ixn + n;
        id_ = ixa + n;
        ivl = id_ + n;
        iw = ivl + ndim;

        // Return if there is insufficient space between the bounds. Modify the
        // initial X if necessary in order to avoid conflicts between the bounds
        // and the construction of the first quadratic model. The lower and upper
        // bounds on moves from the updated X are set now, in the ISL and ISU
        // partitions of W, in order to provide useful and exact information about
        // components of X that become within distance RHOBEG from their bounds.

        zero = 0.;
        i__1 = n;
        for (j = 1; j <= i__1; j++) {
            temp = xu.get(j) - xl.get(j);
            if (temp < rhobeg + rhobeg) {
                throw new NumberIsTooSmallException(temp, rhobeg + rhobeg, true);
            }
            jsl = isl + j - 1;
            jsu = jsl + n;
            w.set(jsl, xl.get(j) - x.get(j));
            w.set(jsu, xu.get(j) - x.get(j));
            if (w.get(jsl) >= -rhobeg) {
                if (w.get(jsl) >= zero) {
                    x.set(j, xl.get(j));
                    w.set(jsl, zero);
                    w.set(jsu, temp);
                } else {
                    x.set(j, xl.get(j) + rhobeg);
                    w.set(jsl, -rhobeg);
                    // Computing MAX
                    d__1 = xu.get(j) - x.get(j);
                    w.set(jsu, Math.max(d__1,rhobeg));
                }
            } else if (w.get(jsu) <= rhobeg) {
                if (w.get(jsu) <= zero) {
                    x.set(j, xu.get(j));
                    w.set(jsl, -temp);
                    w.set(jsu, zero);
                } else {
                    x.set(j, xu.get(j) - rhobeg);
                    // Computing MIN
                    d__1 = xl.get(j) - x.get(j);
                    d__2 = -rhobeg;
                    w.set(jsl, Math.min(d__1,d__2));
                    w.set(jsu, rhobeg);
                }
            }
        }

        // Make the call of BOBYQB.

        return bobyqb(n, npt, x, xl, xu, rhobeg, rhoend, maxfun,
                w.ptr(ixb-1), w.ptr(ixp-npt-1), w.ptr(ifv-1), w.ptr(ixo-1), w.ptr(igo-1), w.ptr(ihq-1), w.ptr(ipq-1),
                w.ptr(ibmat-ndim-1), w.ptr(izmat-npt-1), ndim, w.ptr(isl-1), w.ptr(isu-1), w.ptr(ixn-1), w.ptr(ixa-1),
                w.ptr(id_-1), w.ptr(ivl-1), w.ptr(iw-1));
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
     * @param n
     * @param npt
     * @param x
     * @param xl
     * @param xu
     * @param rhobeg
     * @param rhoend
     * @param maxfun
     * @param xbase
     * @param xpt
     * @param fval
     * @param xopt
     * @param gopt
     * @param hq
     * @param pq
     * @param bmat
     * @param zmat
     * @param ndim
     * @param sl
     * @param su
     * @param xnew
     * @param xalt
     * @param d__
     * @param vlag
     * @param w
     * @return
     */
    private double bobyqb(
            int n,
            int npt,
            ScopedPtr x,
            ScopedPtr xl,
            ScopedPtr xu,
            double rhobeg,
            double rhoend,
            int maxfun,
            ScopedPtr xbase, 
            ScopedPtr xpt,
            ScopedPtr fval,
            ScopedPtr xopt,
            ScopedPtr gopt,
            ScopedPtr hq,
            ScopedPtr pq,
            ScopedPtr bmat,
            ScopedPtr zmat,
            int ndim,
            ScopedPtr sl,
            ScopedPtr su, 
            ScopedPtr xnew,
            ScopedPtr xalt,
            ScopedPtr d__,
            ScopedPtr vlag,
            ScopedPtr w
    ) {
        // System generated locals
        int xpt_dim1, bmat_dim1, zmat_dim1; 
        int i__1, i__2, i__3;
        double d__1, d__2, d__3, d__4;

        // Local variables
        double f = 0;
        int i__, j, k, ih, jj, nh, ip, jp;
        double dx;
        int np;
        double den = 0, one = 0, ten = 0, rho = 0, sum = 0, two = 0, diff = 0, half = 0, beta = 0, gisq = 0;
        int knew = 0;
        double temp, suma, sumb, bsum, fopt;
        int nptm;
        double zero, curv;
        int ksav;
        double gqsq = 0, dist = 0, sumw = 0, sumz = 0, diffa = 0, diffb = 0, diffc = 0, hdiag = 0;
        int kbase;
        double delta = 0, adelt = 0, denom = 0, fsave = 0, bdtol = 0, delsq = 0;
        int nresc, nfsav;
        double ratio = 0, dnorm = 0, vquad = 0, pqold = 0, tenth = 0;
        int itest;
        double sumpq, scaden;
        double errbig, fracsq, biglsq, densav;
        double bdtest;
        double frhosq;
        double distsq = 0;
        int ntrits;
        double xoptsq;

        // Set some constants.
        // Parameter adjustments
        zmat_dim1 = npt;
        xpt_dim1 = npt;
        bmat_dim1 = ndim;
 
        // Function Body
        half = .5;
        one = 1.;
        ten = 10.;
        tenth = .1;
        two = 2.;
        zero = 0.;
        np = n + 1;
        nptm = npt - np;
        nh = n * np / 2;

        // The call of PRELIM sets the elements of XBASE, XPT, FVAL, GOPT, HQ, PQ,
        // BMAT and ZMAT for the first iteration, with the corresponding values of
        // of NF and KOPT, which are the number of calls of CALFUN so far and the
        // index of the interpolation point at the trust region centre. Then the
        // initial XOPT is set too. The branch to label 720 occurs if MAXFUN is
        // less than NPT. GOPT will be updated if KOPT is different from KBASE.

        IntRef nf = new IntRef(0);
        IntRef kopt = new IntRef(0);
        DoubleRef dsq = new DoubleRef(0);
        DoubleRef crvmin = new DoubleRef(0);
        DoubleRef cauchy = new DoubleRef(0);
        DoubleRef alpha = new DoubleRef(0);

        prelim(n, npt, x, xl, xu, rhobeg, maxfun, xbase,
                xpt, fval, gopt, hq, pq, bmat,
                zmat, ndim, sl, su, nf, kopt);
        xoptsq = zero;
        i__1 = n;
        for (i__ = 1; i__ <= i__1; i__++) {
            xopt.set(i__, xpt.get(kopt.value + i__ * xpt_dim1));
            // Computing 2nd power
            d__1 = xopt.get(i__);
            xoptsq += d__1 * d__1;
        }
        fsave = fval.get(1);
        if (nf.value < npt) { // should not happen
            throw new RuntimeException("Return from BOBYQA because the objective function has been called " +
                    nf.value + " times.");
        }
        kbase = 1;

        // Complete the settings that are required for the iterative procedure.

        rho = rhobeg;
        delta = rho;
        nresc = nf.value;
        ntrits = 0;
        diffa = zero;
        diffb = zero;
        itest = 0;
        nfsav = nf.value;

        // Update GOPT if necessary before the first iteration and after each
        // call of RESCUE that makes a call of CALFUN.

        int state = 20;
        for(;;) switch (state) {
        case 20: {
            if (kopt.value != kbase) {
                ih = 0;
                i__1 = n;
                for (j = 1; j <= i__1; j++) {
                    i__2 = j;
                    for (i__ = 1; i__ <= i__2; i__++) {
                        ++ih;
                        if (i__ < j) {
                            gopt.set(j,  gopt.get(j) + hq.get(ih) * xopt.get(i__));
                        }
                        gopt.set(i__,  gopt.get(i__) + hq.get(ih) * xopt.get(j));
                    }
                }
                if (nf.value > npt) {
                    i__2 = npt;
                    for (k = 1; k <= i__2; k++) {
                        temp = zero;
                        i__1 = n;
                        for (j = 1; j <= i__1; j++) {
                            temp += xpt.get(k + j * xpt_dim1) * xopt.get(j);
                        }
                        temp = pq.get(k) * temp;
                        i__1 = n;
                        for (i__ = 1; i__ <= i__1; i__++) {
                            gopt.set(i__, gopt.get(i__) + temp * xpt.get(k + i__ * xpt_dim1));
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
            trsbox(n, npt, xpt, xopt, gopt, hq, pq, sl,
                    su, delta, xnew, d__, w, w.ptr(np-1), w.ptr(np+n-1),
                    w.ptr(np + (n << 1)-1), w.ptr(np + n*3-1), dsq, crvmin);

            // Computing MIN
            d__1 = delta;
            d__2 = Math.sqrt(dsq.value);
            dnorm = Math.min(d__1,d__2);
            if (dnorm < half * rho) {
                ntrits = -1;
                // Computing 2nd power
                d__1 = ten * rho;
                distsq = d__1 * d__1;
                if (nf.value <= nfsav + 2) {
                    state = 650; break;
                }

                // The following choice between labels 650 and 680 depends on whether or
                // not our work with the current RHO seems to be complete. Either RHO is
                // decreased or termination occurs if the errors in the quadratic model at
                // the last three interpolation points compare favourably with predictions
                // of likely improvements to the model within distance HALF*RHO of XOPT.

                // Computing MAX
                d__1 = Math.max(diffa,diffb);
                errbig = Math.max(d__1,diffc);
                frhosq = rho * .125 * rho;
                if (crvmin.value > zero && errbig > frhosq * crvmin.value) {
                    state = 650; break;
                }
                bdtol = errbig / rho;
                i__1 = n;
                for (j = 1; j <= i__1; j++) {
                    bdtest = bdtol;
                    if (xnew.get(j) == sl.get(j)) {
                        bdtest = w.get(j);
                    }
                    if (xnew.get(j) == su.get(j)) {
                        bdtest = -w.get(j);
                    }
                    if (bdtest < bdtol) {
                        curv = hq.get((j + j * j) / 2);
                        i__2 = npt;
                        for (k = 1; k <= i__2; k++) {
                            // Computing 2nd power
                            d__1 = xpt.get(k + j * xpt_dim1);
                            curv += pq.get(k) * (d__1 * d__1);
                        }
                        bdtest += half * curv * rho;
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
            if (dsq.value <= xoptsq * .001) {
                fracsq = xoptsq * .25;
                sumpq = zero;
                i__1 = npt;
                for (k = 1; k <= i__1; k++) {
                    sumpq += pq.get(k);
                    sum = -half * xoptsq;
                    i__2 = n;
                    for (i__ = 1; i__ <= i__2; i__++) {
                        sum += xpt.get(k + i__ * xpt_dim1) * xopt.get(i__);
                    }
                    w.set(npt + k, sum);
                    temp = fracsq - half * sum;
                    i__2 = n;
                    for (i__ = 1; i__ <= i__2; i__++) {
                        w.set(i__, bmat.get(k + i__ * bmat_dim1));
                        vlag.set(i__, sum * xpt.get(k + i__ * xpt_dim1) + temp * xopt.get(i__));
                        ip = npt + i__;
                        i__3 = i__;
                        for (j = 1; j <= i__3; j++) {
                            bmat.set(ip + j * bmat_dim1, bmat.get(ip + j *
                                    bmat_dim1) + w.get(i__) * vlag.get(j) + vlag.get(i__) * w.get(j));
                        }
                    }
                }

                // Then the revisions of BMAT that depend on ZMAT are calculated.

                i__3 = nptm;
                for (jj = 1; jj <= i__3; jj++) {
                    sumz = zero;
                    sumw = zero;
                    i__2 = npt;
                    for (k = 1; k <= i__2; k++) {
                        sumz += zmat.get(k + jj * zmat_dim1);
                        vlag.set(k, w.get(npt + k) * zmat.get(k + jj * zmat_dim1));
                        sumw += vlag.get(k);
                    }
                    i__2 = n;
                    for (j = 1; j <= i__2; j++) {
                        sum = (fracsq * sumz - half * sumw) * xopt.get(j);
                        i__1 = npt;
                        for (k = 1; k <= i__1; k++) {
                            sum += vlag.get(k) * xpt.get(k + j * xpt_dim1);
                        }
                        w.set(j, sum);
                        i__1 = npt;
                        for (k = 1; k <= i__1; k++) {
                            bmat.set(k + j * bmat_dim1,  bmat.get(k + j * bmat_dim1) +
                                    sum * zmat.get(k + jj * zmat_dim1));
                        }
                    }
                    i__1 = n;
                    for (i__ = 1; i__ <= i__1; i__++) {
                        ip = i__ + npt;
                        temp = w.get(i__);
                        i__2 = i__;
                        for (j = 1; j <= i__2; j++) {
                            bmat.set(ip + j * bmat_dim1,  bmat.get(ip + j * bmat_dim1) +
                                    temp * w.get(j));
                        }
                    }
                }

                // The following instructions complete the shift, including the changes
                // to the second derivative parameters of the quadratic model.

                ih = 0;
                i__2 = n;
                for (j = 1; j <= i__2; j++) {
                    w.set(j, -half * sumpq * xopt.get(j));
                    i__1 = npt;
                    for (k = 1; k <= i__1; k++) {
                        w.set(j, w.get(j) + pq.get(k) * xpt.get(k + j * xpt_dim1));
                        xpt.set(k + j * xpt_dim1, xpt.get(k + j * xpt_dim1) - xopt.get(j));
                    }
                    i__1 = j;
                    for (i__ = 1; i__ <= i__1; i__++) {
                        ++ih;
                        hq.set(ih, hq.get(ih) + w.get(i__) * xopt.get(j) + xopt.get(i__) * w.get(j));
                        bmat.set(npt + i__ + j * bmat_dim1, bmat.get(npt + j + i__ * bmat_dim1));
                    }
                }
                i__1 = n;
                for (i__ = 1; i__ <= i__1; i__++) {
                    xbase.set(i__, xbase.get(i__) + xopt.get(i__));
                    xnew.set(i__, xnew.get(i__) - xopt.get(i__));
                    sl.set(i__, sl.get(i__) - xopt.get(i__));
                    su.set(i__, su.get(i__) - xopt.get(i__));
                    xopt.set(i__, zero);
                }
                xoptsq = zero;
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
            nfsav = nf.value;
            kbase = kopt.value;

            rescue(n, npt, xl, xu, maxfun, xbase, xpt,
                    fval, xopt, gopt, hq, pq, bmat,
                    zmat, ndim,  sl, su, nf, delta,
                    kopt, vlag, w.ptr(-2), w.ptr(n+np-1), w.ptr(ndim+np-1));

            // XOPT is updated now in case the branch below to label 720 is taken.
            // Any updating of GOPT occurs after the branch below to label 20, which
            // leads to a trust region iteration as does the branch to label 60.

            xoptsq = zero;
            if (kopt.value != kbase) {
                i__1 = n;
                for (i__ = 1; i__ <= i__1; i__++) {
                    xopt.set(i__, xpt.get(kopt.value + i__ * xpt_dim1));
                    // Computing 2nd power
                    d__1 = xopt.get(i__);
                    xoptsq += d__1 * d__1;
                }
            }
            nresc = nf.value;
            if (nfsav < nf.value) {
                nfsav = nf.value;
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

            altmov(n, npt, xpt, xopt,
                    bmat, zmat,
                    ndim, sl, su, kopt.value, knew, adelt, xnew, xalt, alpha, cauchy,
                    w, w.ptr(np-1), w.ptr(ndim));
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                d__.set(i__, xnew.get(i__) - xopt.get(i__));
            }

            // Calculate VLAG and BETA for the current choice of D. The scalar
            // product of D with XPT(K,.) is going to be held in W(NPT+K) for
            // use when VQUAD is calculated.

        }
        case 230: {
            i__1 = npt;
            for (k = 1; k <= i__1; k++) {
                suma = zero;
                sumb = zero;
                sum = zero;
                i__2 = n;
                for (j = 1; j <= i__2; j++) {
                    suma += xpt.get(k + j * xpt_dim1) * d__.get(j);
                    sumb += xpt.get(k + j * xpt_dim1) * xopt.get(j);
                    sum += bmat.get(k + j * bmat_dim1) * d__.get(j);
                }
                w.set(k, suma * (half * suma + sumb));
                vlag.set(k, sum);
                w.set(npt + k, suma);
            }
            beta = zero;
            i__1 = nptm;
            for (jj = 1; jj <= i__1; jj++) {
                sum = zero;
                i__2 = npt;
                for (k = 1; k <= i__2; k++) {
                    sum += zmat.get(k + jj * zmat_dim1) * w.get(k);
                }
                beta -= sum * sum;
                i__2 = npt;
                for (k = 1; k <= i__2; k++) {
                    vlag.set(k, vlag.get(k) + sum * zmat.get(k + jj * zmat_dim1));
                }
            }
            dsq.value = zero;
            bsum = zero;
            dx = zero;
            i__2 = n;
            for (j = 1; j <= i__2; j++) {
                // Computing 2nd power
                d__1 = d__.get(j);
                dsq.value += d__1 * d__1;
                sum = zero;
                i__1 = npt;
                for (k = 1; k <= i__1; k++) {
                    sum += w.get(k) * bmat.get(k + j * bmat_dim1);
                }
                bsum += sum * d__.get(j);
                jp = npt + j;
                i__1 = n;
                for (i__ = 1; i__ <= i__1; i__++) {
                    sum += bmat.get(jp + i__ * bmat_dim1) * d__.get(i__);
                }
                vlag.set(jp, sum);
                bsum += sum * d__.get(j);
                dx += d__.get(j) * xopt.get(j);
            }
            beta = dx * dx + dsq.value * (xoptsq + dx + dx + half * dsq.value) + beta - bsum;
            vlag.set(kopt.value, vlag.get(kopt.value) + one);

            // If NTRITS is zero, the denominator may be increased by replacing
            // the step D of ALTMOV by a Cauchy step. Then RESCUE may be called if
            // rounding errors have damaged the chosen denominator.

            if (ntrits == 0) {
                // Computing 2nd power
                d__1 = vlag.get(knew);
                denom = d__1 * d__1 + alpha.value * beta;
                if (denom < cauchy.value && cauchy.value > zero) {
                    i__2 = n;
                    for (i__ = 1; i__ <= i__2; i__++) {
                        xnew.set(i__, xalt.get(i__));
                        d__.set(i__, xnew.get(i__) - xopt.get(i__));
                    }
                    cauchy.value = zero;
                    state = 230; break;
                }
                // Computing 2nd power
                d__1 = vlag.get(knew);
                if (denom <= half * (d__1 * d__1)) {
                    if (nf.value > nresc) {
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
                scaden = zero;
                biglsq = zero;
                knew = 0;
                i__2 = npt;
                for (k = 1; k <= i__2; k++) {
                    if (k == kopt.value) {
                        continue;
                    }
                    hdiag = zero;
                    i__1 = nptm;
                    for (jj = 1; jj <= i__1; jj++) {
                        // Computing 2nd power
                        d__1 = zmat.get(k + jj * zmat_dim1);
                        hdiag += d__1 * d__1;
                    }
                    // Computing 2nd power
                    d__1 = vlag.get(k);
                    den = beta * hdiag + d__1 * d__1;
                    distsq = zero;
                    i__1 = n;
                    for (j = 1; j <= i__1; j++) {
                        // Computing 2nd power
                        d__1 = xpt.get(k + j * xpt_dim1) - xopt.get(j);
                        distsq += d__1 * d__1;
                    }
                    // Computing MAX
                    // Computing 2nd power
                    d__3 = distsq / delsq;
                    d__1 = one;
                    d__2 = d__3 * d__3;
                    temp = Math.max(d__1,d__2);
                    if (temp * den > scaden) {
                        scaden = temp * den;
                        knew = k;
                        denom = den;
                    }
                    // Computing MAX
                    // Computing 2nd power
                    d__3 = vlag.get(k);
                    d__1 = biglsq;
                    d__2 = temp * (d__3 * d__3);
                    biglsq = Math.max(d__1,d__2);
                }
                if (scaden <= half * biglsq) {
                    if (nf.value > nresc) {
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
            i__2 = n;
            for (i__ = 1; i__ <= i__2; i__++) {
                // Computing MIN
                // Computing MAX
                d__3 = xl.get(i__);
                d__4 = xbase.get(i__) + xnew.get(i__);
                d__1 = Math.max(d__3,d__4);
                d__2 = xu.get(i__);
                x.set(i__, Math.min(d__1,d__2));
                if (xnew.get(i__) == sl.get(i__)) {
                    x.set(i__, xl.get(i__));
                }
                if (xnew.get(i__) == su.get(i__)) {
                    x.set(i__, xu.get(i__));
                }
            }
            if (nf.value > maxfun) { // should not happen,
                // TooManyEvaluationsException is thrown before
                throw new RuntimeException("Return from BOBYQA because the objective function has been called max_f_evals times.");
            }
            nf.value++;
            f = computeObjectiveValue(x.getAll());
            if (!isMinimize)
                f = -f;
            if (ntrits == -1) {
                fsave = f;
                state = 720; break;
            }

            // Use the quadratic model to predict the change in F due to the step D,
            //   and set DIFF to the error of this prediction.

            fopt = fval.get(kopt.value);
            vquad = zero;
            ih = 0;
            i__2 = n;
            for (j = 1; j <= i__2; j++) {
                vquad += d__.get(j) * gopt.get(j);
                i__1 = j;
                for (i__ = 1; i__ <= i__1; i__++) {
                    ++ih;
                    temp = d__.get(i__) * d__.get(j);
                    if (i__ == j) {
                        temp = half * temp;
                    }
                    vquad += hq.get(ih) * temp;
                }
            }
            i__1 = npt;
            for (k = 1; k <= i__1; k++) {
                // Computing 2nd power
                d__1 = w.get(npt + k);
                vquad += half * pq.get(k) * (d__1 * d__1);
            }
            diff = f - fopt - vquad;
            diffc = diffb;
            diffb = diffa;
            diffa = Math.abs(diff);
            if (dnorm > rho) {
                nfsav = nf.value;
            }

            // Pick the next value of DELTA after a trust region step.

            if (ntrits > 0) {
                if (vquad >= zero) {
                    throw new MathIllegalStateException(LocalizedFormats.TRUST_REGION_STEP_FAILED, vquad);
                }
                ratio = (f - fopt) / vquad;
                if (ratio <= tenth) {
                    // Computing MIN
                    d__1 = half * delta;
                    delta = Math.min(d__1,dnorm);
                } else if (ratio <= .7) {
                    // Computing MAX
                    d__1 = half * delta;
                    delta = Math.max(d__1,dnorm);
                } else {
                    // Computing MAX
                    d__1 = half * delta;
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
                    scaden = zero;
                    biglsq = zero;
                    knew = 0;
                    i__1 = npt;
                    for (k = 1; k <= i__1; k++) {
                        hdiag = zero;
                        i__2 = nptm;
                        for (jj = 1; jj <= i__2; jj++) {
                            // Computing 2nd power
                            d__1 = zmat.get(k + jj * zmat_dim1);
                            hdiag += d__1 * d__1;
                        }
                        // Computing 2nd power
                        d__1 = vlag.get(k);
                        den = beta * hdiag + d__1 * d__1;
                        distsq = zero;
                        i__2 = n;
                        for (j = 1; j <= i__2; j++) {
                            // Computing 2nd power
                            d__1 = xpt.get(k + j * xpt_dim1) - xnew.get(j);
                            distsq += d__1 * d__1;
                        }
                        // Computing MAX
                        // Computing 2nd power
                        d__3 = distsq / delsq;
                        d__1 = one;
                        d__2 = d__3 * d__3;
                        temp = Math.max(d__1,d__2);
                        if (temp * den > scaden) {
                            scaden = temp * den;
                            knew = k;
                            denom = den;
                        }
                        // Computing MAX
                        // Computing 2nd power
                        d__3 = vlag.get(k);
                        d__1 = biglsq;
                        d__2 = temp * (d__3 * d__3);
                        biglsq = Math.max(d__1,d__2);
                    }
                    if (scaden <= half * biglsq) {
                        knew = ksav;
                        denom = densav;
                    }
                }
            }

            // Update BMAT and ZMAT, so that the KNEW-th interpolation point can be
            // moved. Also update the second derivative terms of the model.

            update(n, npt, bmat, zmat, ndim, vlag,
                    beta, denom, knew, w);

            ih = 0;
            pqold = pq.get(knew);
            pq.set(knew, zero);
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                temp = pqold * xpt.get(knew + i__ * xpt_dim1);
                i__2 = i__;
                for (j = 1; j <= i__2; j++) {
                    ++ih;
                    hq.set(ih, hq.get(ih) + temp * xpt.get(knew + j * xpt_dim1));
                }
            }
            i__2 = nptm;
            for (jj = 1; jj <= i__2; jj++) {
                temp = diff * zmat.get(knew + jj * zmat_dim1);
                i__1 = npt;
                for (k = 1; k <= i__1; k++) {
                    pq.set(k, pq.get(k) + temp * zmat.get(k + jj * zmat_dim1));
                }
            }

            // Include the new interpolation point, and make the changes to GOPT at
            // the old XOPT that are caused by the updating of the quadratic model.

            fval.set(knew,  f);
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                xpt.set(knew + i__ * xpt_dim1, xnew.get(i__));
                w.set(i__, bmat.get(knew + i__ * bmat_dim1));
            }
            i__1 = npt;
            for (k = 1; k <= i__1; k++) {
                suma = zero;
                i__2 = nptm;
                for (jj = 1; jj <= i__2; jj++) {
                    suma += zmat.get(knew + jj * zmat_dim1) * zmat.get(k + jj * zmat_dim1);
                }
                sumb = zero;
                i__2 = n;
                for (j = 1; j <= i__2; j++) {
                    sumb += xpt.get(k + j * xpt_dim1) * xopt.get(j);
                }
                temp = suma * sumb;
                i__2 = n;
                for (i__ = 1; i__ <= i__2; i__++) {
                    w.set(i__, w.get(i__) + temp * xpt.get(k + i__ * xpt_dim1));
                }
            }
            i__2 = n;
            for (i__ = 1; i__ <= i__2; i__++) {
                gopt.set(i__, gopt.get(i__) + diff * w.get(i__));
            }

            // Update XOPT, GOPT and KOPT if the new calculated F is less than FOPT.

            if (f < fopt) {
                kopt.value = knew;
                xoptsq = zero;
                ih = 0;
                i__2 = n;
                for (j = 1; j <= i__2; j++) {
                    xopt.set(j, xnew.get(j));
                    // Computing 2nd power
                    d__1 = xopt.get(j);
                    xoptsq += d__1 * d__1;
                    i__1 = j;
                    for (i__ = 1; i__ <= i__1; i__++) {
                        ++ih;
                        if (i__ < j) {
                            gopt.set(j, gopt.get(j) + hq.get(ih) * d__.get(i__));
                        }
                        gopt.set(i__, gopt.get(i__) + hq.get(ih) * d__.get(j));
                    }
                }
                i__1 = npt;
                for (k = 1; k <= i__1; k++) {
                    temp = zero;
                    i__2 = n;
                    for (j = 1; j <= i__2; j++) {
                        temp += xpt.get(k + j * xpt_dim1) * d__.get(j);
                    }
                    temp = pq.get(k) * temp;
                    i__2 = n;
                    for (i__ = 1; i__ <= i__2; i__++) {
                        gopt.set(i__, gopt.get(i__) + temp * xpt.get(k + i__ * xpt_dim1));
                    }
                }
            }

            // Calculate the parameters of the least Frobenius norm interpolant to
            // the current data, the gradient of this interpolant at XOPT being put
            // into VLAG(NPT+I), I=1,2,...,N.

            if (ntrits > 0) {
                i__2 = npt;
                for (k = 1; k <= i__2; k++) {
                    vlag.set(k, fval.get(k) - fval.get(kopt.value));
                    w.set(k, zero);
                }
                i__2 = nptm;
                for (j = 1; j <= i__2; j++) {
                    sum = zero;
                    i__1 = npt;
                    for (k = 1; k <= i__1; k++) {
                        sum += zmat.get(k + j * zmat_dim1) * vlag.get(k);
                    }
                    i__1 = npt;
                    for (k = 1; k <= i__1; k++) {
                        w.set(k, w.get(k) + sum * zmat.get(k + j * zmat_dim1));
                    }
                }
                i__1 = npt;
                for (k = 1; k <= i__1; k++) {
                    sum = zero;
                    i__2 = n;
                    for (j = 1; j <= i__2; j++) {
                        sum += xpt.get(k + j * xpt_dim1) * xopt.get(j);
                    }
                    w.set(k + npt, w.get(k));
                    w.set(k, sum * w.get(k));
                }
                gqsq = zero;
                gisq = zero;
                i__1 = n;
                for (i__ = 1; i__ <= i__1; i__++) {
                    sum = zero;
                    i__2 = npt;
                    for (k = 1; k <= i__2; k++) {
                        sum = sum + bmat.get(k + i__ * bmat_dim1) *
                        vlag.get(k) + xpt.get(k + i__ * xpt_dim1) * w.get(k);
                    }
                    if (xopt.get(i__) == sl.get(i__)) {
                        // Computing MIN
                        d__2 = zero;
                        d__3 = gopt.get(i__);
                        // Computing 2nd power
                        d__1 = Math.min(d__2,d__3);
                        gqsq += d__1 * d__1;
                        // Computing 2nd power
                        d__1 = Math.min(zero,sum);
                        gisq += d__1 * d__1;
                    } else if (xopt.get(i__) == su.get(i__)) {
                        // Computing MAX
                        d__2 = zero;
                        d__3 = gopt.get(i__);
                        // Computing 2nd power
                        d__1 = Math.max(d__2,d__3);
                        gqsq += d__1 * d__1;
                        // Computing 2nd power
                        d__1 = Math.max(zero,sum);
                        gisq += d__1 * d__1;
                    } else {
                        // Computing 2nd power
                        d__1 = gopt.get(i__);
                        gqsq += d__1 * d__1;
                        gisq += sum * sum;
                    }
                    vlag.set(npt + i__, sum);
                }

                // Test whether to replace the new quadratic model by the least Frobenius
                // norm interpolant, making the replacement if the test is satisfied.

                ++itest;
                if (gqsq < ten * gisq) {
                    itest = 0;
                }
                if (itest >= 3) {
                    i__1 = Math.max(npt,nh);
                    for (i__ = 1; i__ <= i__1; i__++) {
                        if (i__ <= n) {
                            gopt.set(i__, vlag.get(npt + i__));
                        }
                        if (i__ <= npt) {
                            pq.set(i__, w.get(npt + i__));
                        }
                        if (i__ <= nh) {
                            hq.set(i__, zero);
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
            if (f <= fopt + tenth * vquad) {
                state = 60; break;
            }

            // Alternatively, find out if the interpolation points are close enough
            //   to the best point so far.

            // Computing MAX
            // Computing 2nd power
            d__3 = two * delta;
            // Computing 2nd power
            d__4 = ten * rho;
            d__1 = d__3 * d__3;
            d__2 = d__4 * d__4;
            distsq = Math.max(d__1,d__2);
        }
        case 650: {
            knew = 0;
            i__1 = npt;
            for (k = 1; k <= i__1; k++) {
                sum = zero;
                i__2 = n;
                for (j = 1; j <= i__2; j++) {
                    // Computing 2nd power
                    d__1 = xpt.get(k + j * xpt_dim1) - xopt.get(j);
                    sum += d__1 * d__1;
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
                    d__1 = tenth * delta;
                    d__2 = half * dist;
                    delta = Math.min(d__1,d__2);
                    if (delta <= rho * 1.5) {
                        delta = rho;
                    }
                }
                ntrits = 0;
                // Computing MAX
                // Computing MIN
                d__2 = tenth * dist;
                d__1 = Math.min(d__2,delta);
                adelt = Math.max(d__1,rho);
                dsq.value = adelt * adelt;
                state = 90; break;
            }
            if (ntrits == -1) {
                state = 680; break;
            }
            if (ratio > zero) {
                state = 60; break;
            }
            if (Math.max(delta,dnorm) > rho) {
                state = 60; break;
            }

            // The calculations with the current value of RHO are complete. Pick the
            //   next values of RHO and DELTA.
        }
        case 680: {
            if (rho > rhoend) {
                delta = half * rho;
                ratio = rho / rhoend;
                if (ratio <= 16.) {
                    rho = rhoend;
                } else if (ratio <= 250.) {
                    rho = Math.sqrt(ratio) * rhoend;
                } else {
                    rho = tenth * rho;
                }
                delta = Math.max(delta,rho);
                ntrits = 0;
                nfsav = nf.value;
                state = 60; break;
            }

            // Return from the calculation, after another Newton-Raphson step, if
            //   it is too short to have been tried before.

            if (ntrits == -1) {
                state = 360; break;
            }
        }
        case 720: {
            if (fval.get(kopt.value) <= fsave) {
                i__1 = n;
                for (i__ = 1; i__ <= i__1; i__++) {
                    // Computing MIN
                    // Computing MAX
                    d__3 = xl.get(i__);
                    d__4 = xbase.get(i__) + xopt.get(i__);
                    d__1 = Math.max(d__3,d__4);
                    d__2 = xu.get(i__);
                    x.set(i__, Math.min(d__1,d__2));
                    if (xopt.get(i__) == sl.get(i__)) {
                        x.set(i__, xl.get(i__));
                    }
                    if (xopt.get(i__) == su.get(i__)) {
                        x.set(i__, xu.get(i__));
                    }
                }
                f = fval.get(kopt.value);
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
     * @param n
     * @param npt
     * @param xpt
     * @param xopt
     * @param bmat
     * @param zmat
     * @param ndim
     * @param sl
     * @param su
     * @param kopt
     * @param knew
     * @param adelt
     * @param xnew
     * @param xalt
     * @param alpha
     * @param cauchy
     * @param glag
     * @param hcol
     * @param w
     */
    private void altmov(
            int n,
            int npt,
            ScopedPtr xpt,
            ScopedPtr xopt,
            ScopedPtr bmat,
            ScopedPtr zmat,
            int ndim, 
            ScopedPtr sl,
            ScopedPtr su,
            int kopt,
            int knew,
            double adelt,
            ScopedPtr xnew,
            ScopedPtr xalt,
            DoubleRef alpha,
            DoubleRef cauchy,
            ScopedPtr glag,
            ScopedPtr hcol,
            ScopedPtr w
    ) {
        // System generated locals
        int xpt_dim1, bmat_dim1, zmat_dim1, i__1, i__2;
        double d__1, d__2, d__3, d__4;

        // Local variables
        int i__, j, k;
        double ha, gw, one, diff, half;
        int ilbd, isbd;
        double slbd;
        int iubd;
        double vlag, subd, temp;
        int ksav = 0;
        double step = 0, zero = 0, curv = 0;
        int iflag;
        double scale = 0, csave = 0, tempa = 0, tempb = 0, tempd = 0, const__ = 0, sumin = 0, 
        ggfree = 0;
        int ibdsav = 0;
        double dderiv = 0, bigstp = 0, predsq = 0, presav = 0, distsq = 0, stpsav = 0, wfixsq = 0, wsqsav = 0;


        zmat_dim1 = npt;
        xpt_dim1 = npt;
        bmat_dim1 = ndim;

        // Function Body
        half = .5;
        one = 1.;
        zero = 0.;
        const__ = one + Math.sqrt(2.);
        i__1 = npt;
        for (k = 1; k <= i__1; k++) {
            hcol.set(k, zero);
        }
        i__1 = npt - n - 1;
        for (j = 1; j <= i__1; j++) {
            temp = zmat.get(knew + j * zmat_dim1);
            i__2 = npt;
            for (k = 1; k <= i__2; k++) {
                hcol.set(k, hcol.get(k) + temp * zmat.get(k + j * zmat_dim1));
            }
        }
        alpha.value = hcol.get(knew);
        ha = half * alpha.value;

        // Calculate the gradient of the KNEW-th Lagrange function at XOPT.

        i__2 = n;
        for (i__ = 1; i__ <= i__2; i__++) {
            glag.set(i__, bmat.get(knew + i__ * bmat_dim1));
        }
        i__2 = npt;
        for (k = 1; k <= i__2; k++) {
            temp = zero;
            i__1 = n;
            for (j = 1; j <= i__1; j++) {
                temp += xpt.get(k + j * xpt_dim1) * xopt.get(j);
            }
            temp = hcol.get(k) * temp;
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                glag.set(i__, glag.get(i__) + temp * xpt.get(k + i__ * xpt_dim1));
            }
        }

        // Search for a large denominator along the straight lines through XOPT
        // and another interpolation point. SLBD and SUBD will be lower and upper
        // bounds on the step along each of these lines in turn. PREDSQ will be
        // set to the square of the predicted denominator for each line. PRESAV
        // will be set to the largest admissible value of PREDSQ that occurs.

        presav = zero;
        i__1 = npt;
        for (k = 1; k <= i__1; k++) {
            if (k == kopt) {
                continue;
            }
            dderiv = zero;
            distsq = zero;
            i__2 = n;
            for (i__ = 1; i__ <= i__2; i__++) {
                temp = xpt.get(k + i__ * xpt_dim1) - xopt.get(i__);
                dderiv += glag.get(i__) * temp;
                distsq += temp * temp;
            }
            subd = adelt / Math.sqrt(distsq);
            slbd = -subd;
            ilbd = 0;
            iubd = 0;
            sumin = Math.min(one,subd);

            // Revise SLBD and SUBD if necessary because of the bounds in SL and SU.

            i__2 = n;
            for (i__ = 1; i__ <= i__2; i__++) {
                temp = xpt.get(k + i__ * xpt_dim1) - xopt.get(i__);
                if (temp > zero) {
                    if (slbd * temp < sl.get(i__) - xopt.get(i__)) {
                        slbd = (sl.get(i__) - xopt.get(i__)) / temp;
                        ilbd = -i__;
                    }
                    if (subd * temp > su.get(i__) - xopt.get(i__)) {
                        // Computing MAX
                        d__1 = sumin;
                        d__2 = (su.get(i__) - xopt.get(i__)) / temp;
                        subd = Math.max(d__1,d__2);
                        iubd = i__;
                    }
                } else if (temp < zero) {
                    if (slbd * temp > su.get(i__) - xopt.get(i__)) {
                        slbd = (su.get(i__) - xopt.get(i__)) / temp;
                        ilbd = i__;
                    }
                    if (subd * temp < sl.get(i__) - xopt.get(i__)) {
                        // Computing MAX
                        d__1 = sumin;
                        d__2 = (sl.get(i__) - xopt.get(i__)) / temp;
                        subd = Math.max(d__1,d__2);
                        iubd = -i__;
                    }
                }
            }

            // Seek a large modulus of the KNEW-th Lagrange function when the index
            // of the other interpolation point on the line through XOPT is KNEW.

            if (k == knew) {
                diff = dderiv - one;
                step = slbd;
                vlag = slbd * (dderiv - slbd * diff);
                isbd = ilbd;
                temp = subd * (dderiv - subd * diff);
                if (Math.abs(temp) > Math.abs(vlag)) {
                    step = subd;
                    vlag = temp;
                    isbd = iubd;
                }
                tempd = half * dderiv;
                tempa = tempd - diff * slbd;
                tempb = tempd - diff * subd;
                if (tempa * tempb < zero) {
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
                vlag = slbd * (one - slbd);
                isbd = ilbd;
                temp = subd * (one - subd);
                if (Math.abs(temp) > Math.abs(vlag)) {
                    step = subd;
                    vlag = temp;
                    isbd = iubd;
                }
                if (subd > half) {
                    if (Math.abs(vlag) < .25) {
                        step = half;
                        vlag = .25;
                        isbd = 0;
                    }
                }
                vlag *= dderiv;
            }

            // Calculate PREDSQ for the current line search and maintain PRESAV.

            temp = step * (one - step) * distsq;
            predsq = vlag * vlag * (vlag * vlag + ha * temp * temp);
            if (predsq > presav) {
                presav = predsq;
                ksav = k;
                stpsav = step;
                ibdsav = isbd;
            }
        }

        // Construct XNEW in a way that satisfies the bound constraints exactly.

        i__1 = n;
        for (i__ = 1; i__ <= i__1; i__++) {
            temp = xopt.get(i__) + stpsav * (xpt.get(ksav + i__ * xpt_dim1) - xopt.get(i__));
            // Computing MAX
            // Computing MIN
            d__3 = su.get(i__);
            d__1 = sl.get(i__);
            d__2 = Math.min(d__3,temp);
            xnew.set(i__, Math.max(d__1,d__2));
        }
        if (ibdsav < 0) {
            xnew.set(-ibdsav, sl.get(-ibdsav));
        }
        if (ibdsav > 0) {
            xnew.set(ibdsav, su.get(ibdsav));
        }

        // Prepare for the iterative method that assembles the constrained Cauchy
        // step in W. The sum of squares of the fixed components of W is formed in
        // WFIXSQ, and the free components of W are set to BIGSTP.

        bigstp = adelt + adelt;
        iflag = 0;

        L100: for(;;) {
            wfixsq = zero;
            ggfree = zero;
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                w.set(i__, zero);
                // Computing MIN
                d__1 = xopt.get(i__) - sl.get(i__);
                d__2 = glag.get(i__);
                tempa = Math.min(d__1,d__2);
                // Computing MAX
                d__1 = xopt.get(i__) - su.get(i__);
                d__2 = glag.get(i__);
                tempb = Math.max(d__1,d__2);
                if (tempa > zero || tempb < zero) {
                    w.set(i__, bigstp);
                    // Computing 2nd power
                    d__1 = glag.get(i__);
                    ggfree += d__1 * d__1;
                }
            }
            if (ggfree == zero) {
                cauchy.value = zero;
                return;
            }

            // Investigate whether more components of W can be fixed.
            L120: {
                temp = adelt * adelt - wfixsq;
                if (temp > zero) {
                    wsqsav = wfixsq;
                    step = Math.sqrt(temp / ggfree);
                    ggfree = zero;
                    i__1 = n;
                    for (i__ = 1; i__ <= i__1; i__++) {
                        if (w.get(i__) == bigstp) {
                            temp = xopt.get(i__) - step * glag.get(i__);
                            if (temp <= sl.get(i__)) {
                                w.set(i__, sl.get(i__) - xopt.get(i__));
                                // Computing 2nd power
                                d__1 = w.get(i__);
                                wfixsq += d__1 * d__1;
                            } else if (temp >= su.get(i__)) {
                                w.set(i__, su.get(i__) - xopt.get(i__));
                                // Computing 2nd power
                                d__1 = w.get(i__);
                                wfixsq += d__1 * d__1;
                            } else {
                                // Computing 2nd power
                                d__1 = glag.get(i__);
                                ggfree += d__1 * d__1;
                            }
                        }
                    }
                    if (!(wfixsq > wsqsav && ggfree > zero)) {
                        break L120;
                    }
                }} // end L120

            // Set the remaining free components of W and all components of XALT,
            // except that W may be scaled later.

            gw = zero;
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                if (w.get(i__) == bigstp) {
                    w.set(i__, -step * glag.get(i__));
                    // Computing MAX
                    // Computing MIN
                    d__3 = su.get(i__);
                    d__4 = xopt.get(i__) + w.get(i__);
                    d__1 = sl.get(i__);
                    d__2 = Math.min(d__3,d__4);
                    xalt.set(i__, Math.max(d__1,d__2));
                } else if (w.get(i__) == zero) {
                    xalt.set(i__, xopt.get(i__));
                } else if (glag.get(i__) > zero) {
                    xalt.set(i__, sl.get(i__));
                } else {
                    xalt.set(i__, su.get(i__));
                }
                gw += glag.get(i__) * w.get(i__);
            }

            // Set CURV to the curvature of the KNEW-th Lagrange function along W.
            // Scale W by a factor less than one if that can reduce the modulus of
            // the Lagrange function at XOPT+W. Set CAUCHY to the final value of
            // the square of this function.

            curv = zero;
            i__1 = npt;
            for (k = 1; k <= i__1; k++) {
                temp = zero;
                i__2 = n;
                for (j = 1; j <= i__2; j++) {
                    temp += xpt.get(k + j * xpt_dim1) * w.get(j);
                }
                curv += hcol.get(k) * temp * temp;
            }
            if (iflag == 1) {
                curv = -curv;
            }
            if (curv > -gw && curv < -const__ * gw) {
                scale = -gw / curv;
                i__1 = n;
                for (i__ = 1; i__ <= i__1; i__++) {
                    temp = xopt.get(i__) + scale * w.get(i__);
                    // Computing MAX
                    // Computing MIN
                    d__3 = su.get(i__);
                    d__1 = sl.get(i__);
                    d__2 = Math.min(d__3,temp);
                    xalt.set(i__, Math.max(d__1,d__2));
                }
                // Computing 2nd power
                d__1 = half * gw * scale;
                cauchy.value = d__1 * d__1;
            } else {
                // Computing 2nd power
                d__1 = gw + half * curv;
                cauchy.value = d__1 * d__1;
            }

            // If IFLAG is zero, then XALT is calculated as before after reversing
            // the sign of GLAG. Thus two XALT vectors become available. The one that
            // is chosen is the one that gives the larger value of CAUCHY.

            if (iflag == 0) {
                i__1 = n;
                for (i__ = 1; i__ <= i__1; i__++) {
                    glag.set(i__, -glag.get(i__));
                    w.set(n + i__, xalt.get(i__));
                }
                csave = cauchy.value;
                iflag = 1;
            } else {
                break L100;
            }} // end L100
        if (csave > cauchy.value) {
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                xalt.set(i__, w.get(n + i__));
            }
            cauchy.value = csave;
        }
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
     * @param n
     * @param npt
     * @param x
     * @param xl
     * @param xu
     * @param rhobeg
     * @param maxfun
     * @param xbase
     * @param xpt
     * @param fval
     * @param gopt
     * @param hq
     * @param pq
     * @param bmat
     * @param zmat
     * @param ndim
     * @param sl
     * @param su
     * @param nf
     * @param kopt
     */
    private void prelim(
            int n,
            int npt,
            ScopedPtr x,
            ScopedPtr xl,
            ScopedPtr xu,
            double rhobeg,
            int maxfun,
            ScopedPtr xbase,
            ScopedPtr xpt,
            ScopedPtr fval,
            ScopedPtr gopt,
            ScopedPtr hq,
            ScopedPtr pq,
            ScopedPtr bmat,
            ScopedPtr zmat,
            int ndim,
            ScopedPtr sl,
            ScopedPtr su,
            IntRef nf,
            IntRef kopt
    ) {
        // System generated locals
        int xpt_dim1, bmat_dim1, zmat_dim1, i__1, i__2;
        double d__1, d__2, d__3, d__4;

        // Local variables
        double f;
        int i__, j, k, ih, np, nfm;
        double one;
        int nfx = 0, ipt = 0, jpt = 0;
        double two = 0, fbeg = 0, diff = 0, half = 0, temp = 0, zero = 0, recip = 0, stepa = 0, stepb = 0;
        int itemp;
        double rhosq;

        // Set some constants.

        zmat_dim1 = npt;
        xpt_dim1 = npt;
        bmat_dim1 = ndim;

        // Function Body
        half = .5;
        one = 1.;
        two = 2.;
        zero = 0.;
        rhosq = rhobeg * rhobeg;
        recip = one / rhosq;
        np = n + 1;

        // Set XBASE to the initial vector of variables, and set the initial
        // elements of XPT, BMAT, HQ, PQ and ZMAT to zero.

        i__1 = n;
        for (j = 1; j <= i__1; j++) {
            xbase.set(j, x.get(j));
            i__2 = npt;
            for (k = 1; k <= i__2; k++) {
                xpt.set(k + j * xpt_dim1, zero);
            }
            i__2 = ndim;
            for (i__ = 1; i__ <= i__2; i__++) {
                bmat.set(i__ + j * bmat_dim1, zero);
            }
        }
        i__2 = n * np / 2;
        for (ih = 1; ih <= i__2; ih++) {
            hq.set(ih, zero);
        }
        i__2 = npt;
        for (k = 1; k <= i__2; k++) {
            pq.set(k, zero);
            i__1 = npt - np;
            for (j = 1; j <= i__1; j++) {
                zmat.set(k + j * zmat_dim1, zero);
            }
        }

        // Begin the initialization procedure. NF becomes one more than the number
        // of function values so far. The coordinates of the displacement of the
        // next initial interpolation point from XBASE are set in XPT(NF+1,.).

        nf.value = 0;
        do {
            nfm = nf.value;
            nfx = nf.value - n;
            nf.value++;
            if (nfm <= n << 1) {
                if (nfm >= 1 && nfm <= n) {
                    stepa = rhobeg;
                    if (su.get(nfm) == zero) {
                        stepa = -stepa;
                    }
                    xpt.set(nf.value + nfm * xpt_dim1, stepa);
                } else if (nfm > n) {
                    stepa = xpt.get(nf.value - n + nfx * xpt_dim1);
                    stepb = -rhobeg;
                    if (sl.get(nfx) == zero) {
                        // Computing MIN
                        d__1 = two * rhobeg;
                        d__2 = su.get(nfx);
                        stepb = Math.min(d__1,d__2);
                    }
                    if (su.get(nfx) == zero) {
                        // Computing MAX
                        d__1 = -two * rhobeg;
                        d__2 = sl.get(nfx);
                        stepb = Math.max(d__1,d__2);
                    }
                    xpt.set(nf.value + nfx * xpt_dim1, stepb);
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
                xpt.set(nf.value + ipt * xpt_dim1, xpt.get(ipt + 1 + ipt * xpt_dim1));
                xpt.set(nf.value + jpt * xpt_dim1, xpt.get(jpt + 1 + jpt * xpt_dim1));
            }

            // Calculate the next value of F. The least function value so far and
            // its index are required.

            i__1 = n;
            for (j = 1; j <= i__1; j++) {
                // Computing MIN
                // Computing MAX
                d__3 = xl.get(j);
                d__4 = xbase.get(j) + xpt.get(nf.value + j * xpt_dim1);
                d__1 = Math.max(d__3,d__4);
                d__2 = xu.get(j);
                x.set(j, Math.min(d__1,d__2));
                if (xpt.get(nf.value + j * xpt_dim1) == sl.get(j)) {
                    x.set(j, xl.get(j));
                }
                if (xpt.get(nf.value + j * xpt_dim1) == su.get(j)) {
                    x.set(j, xu.get(j));
                }
            }
            f = computeObjectiveValue(x.getAll());
            if (!isMinimize)
                f = -f;
            fval.set(nf.value, f);
            if (nf.value == 1) {
                fbeg = f;
                kopt.value = 1;
            } else if (f < fval.get(kopt.value)) {
                kopt.value = nf.value;
            }

            // Set the nonzero initial elements of BMAT and the quadratic model in the
            // cases when NF is at most 2*N+1. If NF exceeds N+1, then the positions
            // of the NF-th and (NF-N)-th interpolation points may be switched, in
            // order that the function value at the first of them contributes to the
            // off-diagonal second derivative terms of the initial quadratic model.

            if (nf.value <= (n << 1) + 1) {
                if (nf.value >= 2 && nf.value <= n + 1) {
                    gopt.set( nfm, (f - fbeg) / stepa);
                    if (npt < nf.value + n) {
                        bmat.set( nfm * bmat_dim1 + 1, -one / stepa);
                        bmat.set( nf.value + nfm * bmat_dim1, one / stepa);
                        bmat.set( npt + nfm + nfm * bmat_dim1, -half * rhosq);
                    }
                } else if (nf.value >= n + 2) {
                    ih = nfx * (nfx + 1) / 2;
                    temp = (f - fbeg) / stepb;
                    diff = stepb - stepa;
                    hq.set(ih, two * (temp - gopt.get(nfx)) / diff);
                    gopt.set(nfx, (gopt.get(nfx) * stepb - temp * stepa) / diff);
                    if (stepa * stepb < zero) {
                        if (f < fval.get(nf.value - n)) {
                            fval.set(nf.value, fval.get(nf.value - n));
                            fval.set(nf.value - n, f);
                            if (kopt.value == nf.value) {
                                kopt.value = nf.value - n;
                            }
                            xpt.set(nf.value - n + nfx * xpt_dim1, stepb);
                            xpt.set(nf.value + nfx * xpt_dim1, stepa);
                        }
                    }
                    bmat.set( nfx * bmat_dim1 + 1, -(stepa + stepb) / (stepa * stepb));
                    bmat.set( nf.value + nfx * bmat_dim1, -half /
                            xpt.get(nf.value - n + nfx * xpt_dim1));
                    bmat.set( nf.value - n + nfx * bmat_dim1, -bmat.get( nfx * bmat_dim1 + 1) -
                            bmat.get( nf.value + nfx * bmat_dim1));
                    zmat.set( nfx * zmat_dim1 + 1, Math.sqrt(two) / (stepa * stepb));
                    zmat.set( nf.value + nfx * zmat_dim1, Math.sqrt(half) / rhosq);
                    zmat.set( nf.value - n + nfx * zmat_dim1, -zmat.get( nfx * zmat_dim1 + 1) -
                            zmat.get( nf.value + nfx * zmat_dim1));
                }

                // Set the off-diagonal second derivatives of the Lagrange functions and
                // the initial quadratic model.

            } else {
                ih = ipt * (ipt - 1) / 2 + jpt;
                zmat.set( nfx * zmat_dim1 + 1, recip);
                zmat.set( nf.value + nfx * zmat_dim1, recip);
                zmat.set(ipt + 1 + nfx * zmat_dim1, -recip);
                zmat.set( jpt + 1 + nfx * zmat_dim1, -recip);
                temp = xpt.get(nf.value + ipt * xpt_dim1) * xpt.get(nf.value + jpt * xpt_dim1);
                hq.set(ih, (fbeg - fval.get(ipt + 1) - fval.get(jpt + 1) + f) / temp);
            }
        } while (nf.value < npt && nf.value < maxfun);
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
     * @param n
     * @param npt
     * @param xl
     * @param xu
     * @param maxfun
     * @param xbase
     * @param xpt
     * @param fval
     * @param xopt
     * @param gopt
     * @param hq
     * @param pq
     * @param bmat
     * @param zmat
     * @param ndim
     * @param sl
     * @param su
     * @param nf
     * @param delta
     * @param kopt
     * @param vlag
     * @param ptsaux
     * @param ptsid
     * @param w
     */
    private void rescue (
            int n,
            int npt,
            ScopedPtr xl,
            ScopedPtr xu,
            int maxfun,
            ScopedPtr xbase,
            ScopedPtr xpt,
            ScopedPtr fval,
            ScopedPtr xopt,
            ScopedPtr gopt,
            ScopedPtr hq,
            ScopedPtr pq,
            ScopedPtr bmat,
            ScopedPtr zmat,
            int ndim,
            ScopedPtr sl,
            ScopedPtr su,
            IntRef nf,
            double delta,
            IntRef kopt,
            ScopedPtr vlag,
            ScopedPtr ptsaux,
            ScopedPtr ptsid,
            ScopedPtr w
    ) {
        // System generated locals
        int xpt_dim1, bmat_dim1, zmat_dim1,
        i__1, i__2, i__3;
        double d__1, d__2, d__3, d__4;


        // Local variables
        double f;
        int i__, j, k, ih, jp, ip, iq, np;
        double xp = 0, xq = 0, den = 0;
        int ihp = 0;
        double one;
        int jpn, kpt;
        double sum = 0, diff = 0, half = 0, beta = 0;
        int kold;
        double winc;
        int nrem, knew;
        double temp, bsum;
        int nptm;
        double zero = 0, hdiag = 0, fbase = 0, sfrac = 0, denom = 0, vquad = 0, sumpq = 0;
        double dsqmin, distsq, vlmxsq;

        // Set some constants.
        zmat_dim1 = npt;
        xpt_dim1 = npt;
        bmat_dim1 = ndim;

        // Function Body
        half = .5;
        one = 1.;
        zero = 0.;
        np = n + 1;
        sfrac = half / (double) np;
        nptm = npt - np;

        // Shift the interpolation points so that XOPT becomes the origin, and set
        // the elements of ZMAT to zero. The value of SUMPQ is required in the
        // updating of HQ below. The squares of the distances from XOPT to the
        // other interpolation points are set at the end of W. Increments of WINC
        // may be added later to these squares to balance the consideration of
        // the choice of point that is going to become current.

        sumpq = zero;
        winc = zero;
        i__1 = npt;
        for (k = 1; k <= i__1; k++) {
            distsq = zero;
            i__2 = n;
            for (j = 1; j <= i__2; j++) {
                xpt.set(k + j * xpt_dim1, xpt.get(k + j * xpt_dim1) - xopt.get(j));
                // Computing 2nd power
                d__1 = xpt.get(k + j * xpt_dim1);
                distsq += d__1 * d__1;
            }
            sumpq += pq.get(k);
            w.set(ndim + k, distsq);
            winc = Math.max(winc,distsq);
            i__2 = nptm;
            for (j = 1; j <= i__2; j++) {
                zmat.set(k + j * zmat_dim1, zero);
            }
        }

        // Update HQ so that HQ and PQ define the second derivatives of the model
        // after XBASE has been shifted to the trust region centre.

        ih = 0;
        i__2 = n;
        for (j = 1; j <= i__2; j++) {
            w.set(j, half * sumpq * xopt.get(j));
            i__1 = npt;
            for (k = 1; k <= i__1; k++) {
                w.set(j, w.get(j) + pq.get(k) * xpt.get(k + j * xpt_dim1));
            }
            i__1 = j;
            for (i__ = 1; i__ <= i__1; i__++) {
                ++ih;
                hq.set(ih, hq.get(ih) + w.get(i__) * xopt.get(j) + w.get(j) * xopt.get(i__));
            }
        }

        // Shift XBASE, SL, SU and XOPT. Set the elements of BMAT to zero, and
        // also set the elements of PTSAUX.

        i__1 = n;
        for (j = 1; j <= i__1; j++) {
            xbase.set(j, xbase.get(j) + xopt.get(j));
            sl.set(j, sl.get(j) - xopt.get(j));
            su.set(j, su.get(j) - xopt.get(j));
            xopt.set(j, zero);
            // Computing MIN
            d__1 = delta;
            d__2 = su.get(j);
            ptsaux.set((j << 1) + 1, Math.min(d__1,d__2));
            // Computing MAX
            d__1 = -delta;
            d__2 = sl.get(j);
            ptsaux.set((j << 1) + 2, Math.max(d__1,d__2));
            if (ptsaux.get((j << 1) + 1) + ptsaux.get((j << 1) + 2) < zero) {
                temp = ptsaux.get((j << 1) + 1);
                ptsaux.set((j << 1) + 1, ptsaux.get((j << 1) + 2));
                ptsaux.set((j << 1) + 2, temp);
            }
            d__2 = ptsaux.get((j << 1) + 2);
            d__1 = ptsaux.get((j << 1) + 1);
            if (Math.abs(d__2) < half * Math.abs(d__1)) {
                ptsaux.set((j << 1) + 2, half * ptsaux.get((j << 1) + 1));
            }
            i__2 = ndim;
            for (i__ = 1; i__ <= i__2; i__++) {
                bmat.set(i__ + j * bmat_dim1, zero);
            }
        }
        fbase = fval.get(kopt.value);

        // Set the identifiers of the artificial interpolation points that are
        // along a coordinate direction from XOPT, and set the corresponding
        // nonzero elements of BMAT and ZMAT.

        ptsid.set(1, sfrac);
        i__2 = n;
        for (j = 1; j <= i__2; j++) {
            jp = j + 1;
            jpn = jp + n;
            ptsid.set(jp, (double) j + sfrac);
            if (jpn <= npt) {
                ptsid.set(jpn, (double) j / (double) np + sfrac);
                temp = one / (ptsaux.get((j << 1) + 1) - ptsaux.get((j << 1) + 2));
                bmat.set(jp + j * bmat_dim1, -temp + one / ptsaux.get((j << 1) + 1));
                bmat.set(jpn + j * bmat_dim1, temp + one / ptsaux.get((j << 1) + 2));
                bmat.set(j * bmat_dim1 + 1, -bmat.get(jp + j * bmat_dim1) - bmat.get(jpn +
                        j * bmat_dim1));
                d__1 = ptsaux.get((j << 1) + 1) * ptsaux.get((j << 1) + 2);
                zmat.set(j * zmat_dim1 + 1,  Math.sqrt(2.) / Math.abs(d__1));
                zmat.set(jp + j * zmat_dim1, zmat.get(j * zmat_dim1 + 1) *
                        ptsaux.get((j << 1) + 2) * temp);
                zmat.set(jpn + j * zmat_dim1, -zmat.get(j * zmat_dim1 + 1) *
                        ptsaux.get((j << 1) + 1) * temp);
            } else {
                bmat.set(j * bmat_dim1 + 1, -one / ptsaux.get((j << 1) + 1));
                bmat.set(jp + j * bmat_dim1, one / ptsaux.get((j << 1) + 1));
                // Computing 2nd power
                d__1 = ptsaux.get((j << 1) + 1);
                bmat.set(j + npt + j * bmat_dim1, -half * (d__1 * d__1));
            }
        }

        // Set any remaining identifiers with their nonzero elements of ZMAT.

        if (npt >= n + np) {
            i__2 = npt;
            for (k = np << 1; k <= i__2; k++) {
                int iw = (int) (((double) (k - np) - half) / (double) n);
                ip = k - np - iw * n;
                iq = ip + iw;
                if (iq > n) {
                    iq -= n;
                }
                ptsid.set(k, (double) ip + (double) iq / (double) np +
                        sfrac);
                temp = one / (ptsaux.get((ip << 1) + 1) * ptsaux.get((iq << 1) + 1));
                zmat.set((k - np) * zmat_dim1 + 1, temp);
                zmat.set(ip + 1 + (k - np) * zmat_dim1, -temp);
                zmat.set(iq + 1 + (k - np) * zmat_dim1, -temp);
                zmat.set(k + (k - np) * zmat_dim1, temp);
            }
        }
        nrem = npt;
        kold = 1;
        knew = kopt.value;

        // Reorder the provisional points in the way that exchanges PTSID(KOLD)
        // with PTSID(KNEW).

        int state = 80;
        for(;;) switch (state) {
        case 80: {
            i__2 = n;
            for (j = 1; j <= i__2; j++) {
                temp = bmat.get(kold + j * bmat_dim1);
                bmat.set(kold + j * bmat_dim1, bmat.get(knew + j * bmat_dim1));
                bmat.set(knew + j * bmat_dim1, temp);
            }
            i__2 = nptm;
            for (j = 1; j <= i__2; j++) {
                temp = zmat.get(kold + j * zmat_dim1);
                zmat.set(kold + j * zmat_dim1, zmat.get(knew + j * zmat_dim1));
                zmat.set(knew + j * zmat_dim1, temp);
            }
            ptsid.set(kold, ptsid.get(knew));
            ptsid.set(knew, zero);
            w.set(ndim + knew, zero);
            --nrem;
            if (knew != kopt.value) {
                temp = vlag.get(kold);
                vlag.set(kold, vlag.get(knew));
                vlag.set(knew, temp);

                // Update the BMAT and ZMAT matrices so that the status of the KNEW-th
                // interpolation point can be changed from provisional to original. The
                // branch to label 350 occurs if all the original points are reinstated.
                // The nonnegative values of W(NDIM+K) are required in the search below.

                update(n, npt, bmat, zmat, ndim, vlag,
                        beta, denom, knew, w);

                if (nrem == 0) {
                    return;
                }
                i__2 = npt;
                for (k = 1; k <= i__2; k++) {
                    d__1 = w.get(ndim + k);
                    w.set(ndim + k, Math.abs(d__1));
                }
            }

            // Pick the index KNEW of an original interpolation point that has not
            // yet replaced one of the provisional interpolation points, giving
            // attention to the closeness to XOPT and to previous tries with KNEW.
        }
        case 120: {
            dsqmin = zero;
            i__2 = npt;
            for (k = 1; k <= i__2; k++) {
                if (w.get(ndim + k) > zero) {
                    if (dsqmin == zero || w.get(ndim + k) < dsqmin) {
                        knew = k;
                        dsqmin = w.get(ndim + k);
                    }
                }
            }
            if (dsqmin == zero) {
                state = 260; break;
            }

            // Form the W-vector of the chosen original interpolation point.

            i__2 = n;
            for (j = 1; j <= i__2; j++) {
                w.set(npt + j, xpt.get(knew + j * xpt_dim1));
            }
            i__2 = npt;
            for (k = 1; k <= i__2; k++) {
                sum = zero;
                if (k == kopt.value) {
                } else if (ptsid.get(k) == zero) {
                    i__1 = n;
                    for (j = 1; j <= i__1; j++) {
                        sum += w.get(npt + j) * xpt.get(k + j * xpt_dim1);
                    }
                } else {
                    ip = (int) ptsid.get(k);
                    if (ip > 0) {
                        sum = w.get(npt + ip) * ptsaux.get((ip << 1) + 1);
                    }
                    iq = (int) ((double) np * ptsid.get(k) - (double) (ip * np));
                    if (iq > 0) {
                        int iw = 1;
                        if (ip == 0) {
                            iw = 2;
                        }
                        sum += w.get(npt + iq) * ptsaux.get(iw + (iq << 1));
                    }
                }
                w.set(k, half * sum * sum);
            }

            // Calculate VLAG and BETA for the required updating of the H matrix if
            // XPT(KNEW,.) is reinstated in the set of interpolation points.

            i__2 = npt;
            for (k = 1; k <= i__2; k++) {
                sum = zero;
                i__1 = n;
                for (j = 1; j <= i__1; j++) {
                    sum += bmat.get(k + j * bmat_dim1) * w.get(npt + j);
                }
                vlag.set(k, sum);
            }
            beta = zero;
            i__2 = nptm;
            for (j = 1; j <= i__2; j++) {
                sum = zero;
                i__1 = npt;
                for (k = 1; k <= i__1; k++) {
                    sum += zmat.get(k + j * zmat_dim1) * w.get(k);
                }
                beta -= sum * sum;
                i__1 = npt;
                for (k = 1; k <= i__1; k++) {
                    vlag.set(k, vlag.get(k) + sum * zmat.get(k + j * zmat_dim1));
                }
            }
            bsum = zero;
            distsq = zero;
            i__1 = n;
            for (j = 1; j <= i__1; j++) {
                sum = zero;
                i__2 = npt;
                for (k = 1; k <= i__2; k++) {
                    sum += bmat.get(k + j * bmat_dim1) * w.get(k);
                }
                jp = j + npt;
                bsum += sum * w.get(jp);
                i__2 = ndim;
                for (ip = npt; ip <= i__2; ip++) {
                    sum += bmat.get(ip + j * bmat_dim1) * w.get(ip);
                }
                bsum += sum * w.get(jp);
                vlag.set(jp, sum);
                // Computing 2nd power
                d__1 = xpt.get(knew + j * xpt_dim1);
                distsq += d__1 * d__1;
            }
            beta = half * distsq * distsq + beta - bsum;
            vlag.set(kopt.value, vlag.get(kopt.value) + one);

            // KOLD is set to the index of the provisional interpolation point that is
            // going to be deleted to make way for the KNEW-th original interpolation
            // point. The choice of KOLD is governed by the avoidance of a small value
            // of the denominator in the updating calculation of UPDATE.

            denom = zero;
            vlmxsq = zero;
            i__1 = npt;
            for (k = 1; k <= i__1; k++) {
                if (ptsid.get(k) != zero) {
                    hdiag = zero;
                    i__2 = nptm;
                    for (j = 1; j <= i__2; j++) {
                        // Computing 2nd power
                        d__1 = zmat.get(k + j * zmat_dim1);
                        hdiag += d__1 * d__1;
                    }
                    // Computing 2nd power
                    d__1 = vlag.get(k);
                    den = beta * hdiag + d__1 * d__1;
                    if (den > denom) {
                        kold = k;
                        denom = den;
                    }
                }
                // Computing MAX
                // Computing 2nd power
                d__3 = vlag.get(k);
                d__1 = vlmxsq;
                d__2 = d__3 * d__3;
                vlmxsq = Math.max(d__1,d__2);
            }
            if (denom <= vlmxsq * .01) {
                w.set(ndim + knew, -w.get(ndim + knew) - winc);
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
            i__1 = npt;
            for (kpt = 1; kpt <= i__1; kpt++) {
                if (ptsid.get(kpt) == zero) {
                    continue;
                }
                if (nf.value >= maxfun) {
                    nf.value = -1;
                    return;
                }
                ih = 0;
                i__2 = n;
                for (j = 1; j <= i__2; j++) {
                    w.set(j, xpt.get(kpt + j * xpt_dim1));
                    xpt.set(kpt + j * xpt_dim1, zero);
                    temp = pq.get(kpt) * w.get(j);
                    i__3 = j;
                    for (i__ = 1; i__ <= i__3; i__++) {
                        ++ih;
                        hq.set(ih, hq.get(ih) + temp * w.get(i__));
                    }
                }
                pq.set(kpt, zero);
                ip = (int) ptsid.get(kpt);
                iq = (int) ((double) np * ptsid.get(kpt) - (double) (ip * np))
                ;
                if (ip > 0) {
                    xp = ptsaux.get((ip << 1) + 1);
                    xpt.set(kpt + ip * xpt_dim1, xp);
                }
                if (iq > 0) {
                    xq = ptsaux.get((iq << 1) + 1);
                    if (ip == 0) {
                        xq = ptsaux.get((iq << 1) + 2);
                    }
                    xpt.set(kpt + iq * xpt_dim1, xq);
                }

                // Set VQUAD to the value of the current model at the new point.

                vquad = fbase;
                if (ip > 0) {
                    ihp = (ip + ip * ip) / 2;
                    vquad += xp * (gopt.get(ip) + half * xp * hq.get(ihp));
                }
                if (iq > 0) {
                    int ihq = (iq + iq * iq) / 2;
                    vquad += xq * (gopt.get(iq) + half * xq * hq.get(ihq));
                    if (ip > 0) {
                        i__3 = ip - iq;
                        int iiw = Math.max(ihp,ihq) - Math.abs(i__3);
                        vquad += xp * xq * hq.get(iiw);
                    }
                }
                i__3 = npt;
                for (k = 1; k <= i__3; k++) {
                    temp = zero;
                    if (ip > 0) {
                        temp += xp * xpt.get(k + ip * xpt_dim1);
                    }
                    if (iq > 0) {
                        temp += xq * xpt.get(k + iq * xpt_dim1);
                    }
                    vquad += half * pq.get(k) * temp * temp;
                }

                // Calculate F at the new interpolation point, and set DIFF to the factor
                // that is going to multiply the KPT-th Lagrange function when the model
                // is updated to provide interpolation to the new function value.

                i__3 = n;
                for (i__ = 1; i__ <= i__3; i__++) {
                    // Computing MIN
                    // Computing MAX
                    d__3 = xl.get(i__);
                    d__4 = xbase.get(i__) + xpt.get(kpt + i__ * xpt_dim1);
                    d__1 = Math.max(d__3,d__4);
                    d__2 = xu.get(i__);
                    w.set(i__, Math.min(d__1,d__2));
                    if (xpt.get(kpt + i__ * xpt_dim1) == sl.get(i__)) {
                        w.set(i__, xl.get(i__));
                    }
                    if (xpt.get(kpt + i__ * xpt_dim1) == su.get(i__)) {
                        w.set(i__, xu.get(i__));
                    }
                }
                nf.value++;
                f = computeObjectiveValue(w.getAll(1,n));
                if (!isMinimize)
                    f = -f;
                fval.set(kpt, f);
                if (f < fval.get(kopt.value)) {
                    kopt.value = kpt;
                }
                diff = f - vquad;

                // Update the quadratic model. The RETURN from the subroutine occurs when
                // all the new interpolation points are included in the model.

                i__3 = n;
                for (i__ = 1; i__ <= i__3; i__++) {
                    gopt.set(i__, gopt.get(i__) + diff * bmat.get(kpt + i__ * bmat_dim1));
                }
                i__3 = npt;
                for (k = 1; k <= i__3; k++) {
                    sum = zero;
                    i__2 = nptm;
                    for (j = 1; j <= i__2; j++) {
                        sum += zmat.get(k + j * zmat_dim1) * zmat.get(kpt + j * zmat_dim1);
                    }
                    temp = diff * sum;
                    if (ptsid.get(k) == zero) {
                        pq.set(k, pq.get(k) + temp);
                    } else {
                        ip = (int) ptsid.get(k);
                        iq = (int) ((double) np * ptsid.get(k) - (double) (ip * np));
                        int ihq = (iq * iq + iq) / 2;
                        if (ip == 0) {
                            // Computing 2nd power
                            d__1 = ptsaux.get((iq << 1) + 2);
                            hq.set(ihq, hq.get(ihq) + temp * (d__1 * d__1));
                        } else {
                            ihp = (ip * ip + ip) / 2;
                            // Computing 2nd power
                            d__1 = ptsaux.get((ip << 1) + 1);
                            hq.set(ihp, hq.get(ihp) + temp * (d__1 * d__1));
                            if (iq > 0) {
                                // Computing 2nd power
                                d__1 = ptsaux.get((iq << 1) + 1);
                                hq.set(ihq, hq.get(ihq) + temp * (d__1 * d__1));
                                i__2 = iq - ip;
                                int iw = Math.max(ihp,ihq) - Math.abs(i__2);
                                hq.set(iw, hq.get(iw) + temp * ptsaux.get((ip << 1) + 1) * ptsaux.get((iq <<
                                        1) + 1));
                            }
                        }
                    }
                }
                ptsid.set(kpt, zero);
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
     * @param n
     * @param npt
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
     * @param dsq
     * @param crvmin
     */
    private void trsbox(
            int n,
            int npt,
            ScopedPtr xpt,
            ScopedPtr xopt,
            ScopedPtr gopt,
            ScopedPtr hq,
            ScopedPtr pq,
            ScopedPtr sl,
            ScopedPtr su,
            double delta,
            ScopedPtr xnew,
            ScopedPtr d__,
            ScopedPtr gnew,
            ScopedPtr xbdi,
            ScopedPtr s,
            ScopedPtr hs,
            ScopedPtr hred,
            DoubleRef dsq,
            DoubleRef crvmin
    ) {
        // System generated locals
        int xpt_dim1, i__1, i__2;
        double d__1, d__2, d__3, d__4;

        // Local variables
        int i__, j, k, ih;
        double ds;
        int iu;
        double dhd, dhs, cth, one, shs, sth, ssq, half, beta=0, sdec, blen;
        int iact = 0, nact = 0;
        double angt = 0, qred;
        int isav;
        double temp = 0, zero = 0, xsav = 0, xsum = 0, angbd = 0, dredg = 0, sredg = 0;
        int iterc;
        double resid = 0, delsq = 0, ggsav = 0, tempa = 0, tempb = 0,
        redmax = 0, dredsq = 0, redsav = 0, onemin = 0, gredsq = 0, rednew = 0;
        int itcsav = 0;
        double rdprev = 0, rdnext = 0, stplen = 0, stepsq = 0;
        int itermax = 0;

        // Set some constants.
        xpt_dim1 = npt;

        // Function Body
        half = .5;
        one = 1.;
        onemin = -1.;
        zero = 0.;

        // The sign of GOPT(I) gives the sign of the change to the I-th variable
        // that will reduce Q from its value at XOPT. Thus xbdi.get((I) shows whether
        // or not to fix the I-th variable at one of its bounds initially, with
        // NACT being set to the number of fixed variables. D and GNEW are also
        // set for the first iteration. DELSQ is the upper bound on the sum of
        // squares of the free variables. QRED is the reduction in Q so far.

        iterc = 0;
        nact = 0;
        i__1 = n;
        for (i__ = 1; i__ <= i__1; i__++) {
            xbdi.set(i__, zero);
            if (xopt.get(i__) <= sl.get(i__)) {
                if (gopt.get(i__) >= zero) {
                    xbdi.set(i__, onemin);
                }
            } else if (xopt.get(i__) >= su.get(i__)) {
                if (gopt.get(i__) <= zero) {
                    xbdi.set(i__, one);
                }
            }
            if (xbdi.get(i__) != zero) {
                ++nact;
            }
            d__.set(i__, zero);
            gnew.set(i__, gopt.get(i__));
        }
        delsq = delta * delta;
        qred = zero;
        crvmin.value = onemin;

        // Set the next search direction of the conjugate gradient method. It is
        // the steepest descent direction initially and when the iterations are
        // restarted because a variable has just been fixed by a bound, and of
        // course the components of the fixed variables are zero. ITERMAX is an
        // upper bound on the indices of the conjugate gradient iterations.

        int state = 20;
        for(;;) switch (state) {

        case 20: {
            beta = zero;
        }
        case 30: {
            stepsq = zero;
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                if (xbdi.get(i__) != zero) {
                    s.set(i__, zero);
                } else if (beta == zero) {
                    s.set(i__, -gnew.get(i__));
                } else {
                    s.set(i__, beta * s.get(i__) - gnew.get(i__));
                }
                // Computing 2nd power
                d__1 = s.get(i__);
                stepsq += d__1 * d__1;
            }
            if (stepsq == zero) {
                state = 190; break;
            }
            if (beta == zero) {
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
            ds = zero;
            shs = zero;
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                if (xbdi.get(i__) == zero) {
                    // Computing 2nd power
                    d__1 = d__.get(i__);
                    resid -= d__1 * d__1;
                    ds += s.get(i__) * d__.get(i__);
                    shs += s.get(i__) * hs.get(i__);
                }
            }
            if (resid <= zero) {
                state = 90; break;
            }
            temp = Math.sqrt(stepsq * resid + ds * ds);
            if (ds < zero) {
                blen = (temp - ds) / stepsq;
            } else {
                blen = resid / (temp + ds);
            }
            stplen = blen;
            if (shs > zero) {
                // Computing MIN
                d__1 = blen;
                d__2 = gredsq / shs;
                stplen = Math.min(d__1,d__2);
            }

            // Reduce STPLEN if necessary in order to preserve the simple bounds,
            // letting IACT be the index of the new constrained variable.

            iact = 0;
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                if (s.get(i__) != zero) {
                    xsum = xopt.get(i__) + d__.get(i__);
                    if (s.get(i__) > zero) {
                        temp = (su.get(i__) - xsum) / s.get(i__);
                    } else {
                        temp = (sl.get(i__) - xsum) / s.get(i__);
                    }
                    if (temp < stplen) {
                        stplen = temp;
                        iact = i__;
                    }
                }
            }

            // Update CRVMIN, GNEW and D. Set SDEC to the decrease that occurs in Q.

            sdec = zero;
            if (stplen > zero) {
                ++iterc;
                temp = shs / stepsq;
                if (iact == 0 && temp > zero) {
                    crvmin.value = Math.min(crvmin.value,temp);
                    if (crvmin.value == onemin) {
                        crvmin.value = temp;
                    }
                }
                ggsav = gredsq;
                gredsq = zero;
                i__1 = n;
                for (i__ = 1; i__ <= i__1; i__++) {
                    gnew.set(i__, gnew.get(i__) + stplen * hs.get(i__));
                    if (xbdi.get(i__) == zero) {
                        // Computing 2nd power
                        d__1 = gnew.get(i__);
                        gredsq += d__1 * d__1;
                    }
                    d__.set(i__, d__.get(i__) + stplen * s.get(i__));
                }
                // Computing MAX
                d__1 = stplen * (ggsav - half * stplen * shs);
                sdec = Math.max(d__1,zero);
                qred += sdec;
            }

            // Restart the conjugate gradient method if it has hit a new bound.

            if (iact > 0) {
                ++nact;
                xbdi.set(iact, one);
                if (s.get(iact) < zero) {
                    xbdi.set(iact, onemin);
                }
                // Computing 2nd power
                d__1 = d__.get(iact);
                delsq -= d__1 * d__1;
                if (delsq <= zero) {
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
            crvmin.value = zero;

            // Prepare for the alternative iteration by calculating some scalars
            // and by multiplying the reduced D by the second derivative matrix of
            // Q, where S holds the reduced D in the call of GGMULT.

        }
        case 100: {
            if (nact >= n - 1) {
                state = 190; break;
            }
            dredsq = zero;
            dredg = zero;
            gredsq = zero;
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                if (xbdi.get(i__) == zero) {
                    // Computing 2nd power
                    d__1 = d__.get(i__);
                    dredsq += d__1 * d__1;
                    dredg += d__.get(i__) * gnew.get(i__);
                    // Computing 2nd power
                    d__1 = gnew.get(i__);
                    gredsq += d__1 * d__1;
                    s.set(i__, d__.get(i__));
                } else {
                    s.set(i__, zero);
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
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                if (xbdi.get(i__) == zero) {
                    s.set(i__, (dredg * d__.get(i__) - dredsq * gnew.get(i__)) / temp);
                } else {
                    s.set(i__, zero);
                }
            }
            sredg = -temp;

            // By considering the simple bounds on the variables, calculate an upper
            // bound on the tangent of half the angle of the alternative iteration,
            // namely ANGBD, except that, if already a free variable has reached a
            // bound, there is a branch back to label 100 after fixing that variable.

            angbd = one;
            iact = 0;
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                if (xbdi.get(i__) == zero) {
                    tempa = xopt.get(i__) + d__.get(i__) - sl.get(i__);
                    tempb = su.get(i__) - xopt.get(i__) - d__.get(i__);
                    if (tempa <= zero) {
                        ++nact;
                        xbdi.set(i__, onemin);
                        state = 100; break;
                    } else if (tempb <= zero) {
                        ++nact;
                        xbdi.set(i__, one);
                        state = 100; break;
                    }
                    // Computing 2nd power
                    d__1 = d__.get(i__);
                    // Computing 2nd power
                    d__2 = s.get(i__);
                    ssq = d__1 * d__1 + d__2 * d__2;
                    // Computing 2nd power
                    d__1 = xopt.get(i__) - sl.get(i__);
                    temp = ssq - d__1 * d__1;
                    if (temp > zero) {
                        temp = Math.sqrt(temp) - s.get(i__);
                        if (angbd * temp > tempa) {
                            angbd = tempa / temp;
                            iact = i__;
                            xsav = onemin;
                        }
                    }
                    // Computing 2nd power
                    d__1 = su.get(i__) - xopt.get(i__);
                    temp = ssq - d__1 * d__1;
                    if (temp > zero) {
                        temp = Math.sqrt(temp) + s.get(i__);
                        if (angbd * temp > tempb) {
                            angbd = tempb / temp;
                            iact = i__;
                            xsav = one;
                        }
                    }
                }
            }

            // Calculate HHD and some curvatures for the alternative iteration.

            state = 210; break;
        }
        case 150: {
            shs = zero;
            dhs = zero;
            dhd = zero;
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                if (xbdi.get(i__) == zero) {
                    shs += s.get(i__) * hs.get(i__);
                    dhs += d__.get(i__) * hs.get(i__);
                    dhd += d__.get(i__) * hred.get(i__);
                }
            }

            // Seek the greatest reduction in Q for a range of equally spaced values
            // of ANGT in [0,ANGBD], where ANGT is the tangent of half the angle of
            // the alternative iteration.

            redmax = zero;
            isav = 0;
            redsav = zero;
            iu = (int) (angbd * 17. + 3.1);
            i__1 = iu;
            for (i__ = 1; i__ <= i__1; i__++) {
                angt = angbd * (double) i__ / (double) iu;
                sth = (angt + angt) / (one + angt * angt);
                temp = shs + angt * (angt * dhd - dhs - dhs);
                rednew = sth * (angt * dredg - sredg - half * sth * temp);
                if (rednew > redmax) {
                    redmax = rednew;
                    isav = i__;
                    rdprev = redsav;
                } else if (i__ == isav + 1) {
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
                angt = angbd * ((double) isav + half * temp) / (double) iu;
            }
            cth = (one - angt * angt) / (one + angt * angt);
            sth = (angt + angt) / (one + angt * angt);
            temp = shs + angt * (angt * dhd - dhs - dhs);
            sdec = sth * (angt * dredg - sredg - half * sth * temp);
            if (sdec <= zero) {
                state = 190; break;
            }

            // Update GNEW, D and HRED. If the angle of the alternative iteration
            // is restricted by a bound on a free variable, that variable is fixed
            // at the bound.

            dredg = zero;
            gredsq = zero;
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                gnew.set(i__, gnew.get(i__) + (cth - one) * hred.get(i__) + sth * hs.get(i__));
                if (xbdi.get(i__) == zero) {
                    d__.set(i__, cth * d__.get(i__) + sth * s.get(i__));
                    dredg += d__.get(i__) * gnew.get(i__);
                    // Computing 2nd power
                    d__1 = gnew.get(i__);
                    gredsq += d__1 * d__1;
                }
                hred.set(i__, cth * hred.get(i__) + sth * hs.get(i__));
            }
            qred += sdec;
            if (iact > 0 && isav == iu) {
                ++nact;
                xbdi.set(iact, xsav);
                state = 100; break;
            }

            // If SDEC is sufficiently small, then RETURN after setting XNEW to
            // XOPT+D, giving careful attention to the bounds.

            if (sdec > qred * .01) {
                state = 120; break;
            }
        }
        case 190: {
            dsq.value = zero;
            i__1 = n;
            for (i__ = 1; i__ <= i__1; i__++) {
                // Computing MAX
                // Computing MIN
                d__3 = xopt.get(i__) + d__.get(i__);
                d__4 = su.get(i__);
                d__1 = Math.min(d__3,d__4);
                d__2 = sl.get(i__);
                xnew.set(i__, Math.max(d__1,d__2));
                if (xbdi.get(i__) == onemin) {
                    xnew.set(i__, sl.get(i__));
                }
                if (xbdi.get(i__) == one) {
                    xnew.set(i__, su.get(i__));
                }
                d__.set(i__, xnew.get(i__) - xopt.get(i__));
                // Computing 2nd power
                d__1 = d__.get(i__);
                dsq.value += d__1 * d__1;
            }
            return;
            // The following instructions multiply the current S-vector by the second
            // derivative matrix of the quadratic model, putting the product in HS.
            // They are reached from three different parts of the software above and
            // they can be regarded as an external subroutine.
        }
        case 210: {
            ih = 0;
            i__1 = n;
            for (j = 1; j <= i__1; j++) {
                hs.set(j, zero);
                i__2 = j;
                for (i__ = 1; i__ <= i__2; i__++) {
                    ++ih;
                    if (i__ < j) {
                        hs.set(j, hs.get(j) + hq.get(ih) * s.get(i__));
                    }
                    hs.set(i__, hs.get(i__) + hq.get(ih) * s.get(j));
                }
            }
            i__2 = npt;
            for (k = 1; k <= i__2; k++) {
                if (pq.get(k) != zero) {
                    temp = zero;
                    i__1 = n;
                    for (j = 1; j <= i__1; j++) {
                        temp += xpt.get(k + j * xpt_dim1) * s.get(j);
                    }
                    temp *= pq.get(k);
                    i__1 = n;
                    for (i__ = 1; i__ <= i__1; i__++) {
                        hs.set(i__, hs.get(i__) + temp * xpt.get(k + i__ * xpt_dim1));
                    }
                }
            }
            if (crvmin.value != zero) {
                state = 50; break;
            }
            if (iterc > itcsav) {
                state = 150; break;
            }
            i__2 = n;
            for (i__ = 1; i__ <= i__2; i__++) {
                hred.set(i__, hs.get(i__));
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
     * @param n
     * @param npt
     * @param bmat
     * @param zmat
     * @param ndim
     * @param vlag
     * @param beta
     * @param denom
     * @param knew
     * @param w
     */
    private void update(
            int n,
            int npt,
            ScopedPtr bmat,
            ScopedPtr zmat,
            int ndim,
            ScopedPtr vlag,
            double beta,
            double denom,
            int knew,
            ScopedPtr w
    ) {
        // System generated locals
        int bmat_dim1, zmat_dim1, i__1, i__2;
        double d__1, d__2, d__3;

        // Local variables
        int i__, j, k, jp;
        double one, tau, temp;
        int nptm;
        double zero, alpha, tempa, tempb, ztest;

        zmat_dim1 = npt;
        bmat_dim1 = ndim;

        // Function Body
        one = 1.;
        zero = 0.;
        nptm = npt - n - 1;
        ztest = zero;
        i__1 = npt;
        for (k = 1; k <= i__1; k++) {
            i__2 = nptm;
            for (j = 1; j <= i__2; j++) {
                // Computing MAX
                d__2 = ztest;
                d__1 = zmat.get(k + j * zmat_dim1);
                d__3 = Math.abs(d__1);
                ztest = Math.max(d__2,d__3);
            }
        }
        ztest *= 1e-20;

        // Apply the rotations that put zeros in the KNEW-th row of ZMAT.

        i__2 = nptm;
        for (j = 2; j <= i__2; j++) {
            d__1 = zmat.get(knew + j * zmat_dim1);
            if (Math.abs(d__1) > ztest) {
                // Computing 2nd power
                d__1 = zmat.get(knew + zmat_dim1);
                // Computing 2nd power
                d__2 = zmat.get(knew + j * zmat_dim1);
                temp = Math.sqrt(d__1 * d__1 + d__2 * d__2);
                tempa = zmat.get(knew + zmat_dim1) / temp;
                tempb = zmat.get(knew + j * zmat_dim1) / temp;
                i__1 = npt;
                for (i__ = 1; i__ <= i__1; i__++) {
                    temp = tempa * zmat.get(i__ + zmat_dim1) + tempb * zmat.get(i__ + j *
                            zmat_dim1);
                    zmat.set(i__ + j * zmat_dim1, tempa * zmat.get(i__ + j * zmat_dim1) -
                            tempb * zmat.get(i__ + zmat_dim1));
                    zmat.set(i__ + zmat_dim1, temp);
                }
            }
            zmat.set(knew + j * zmat_dim1, zero);
        }

        // Put the first NPT components of the KNEW-th column of HLAG into W,
        // and calculate the parameters of the updating formula.

        i__2 = npt;
        for (i__ = 1; i__ <= i__2; i__++) {
            w.set(i__, zmat.get(knew + zmat_dim1) * zmat.get(i__ + zmat_dim1));
        }
        alpha = w.get(knew);
        tau = vlag.get(knew);
        vlag.set(knew, vlag.get(knew) - one);

        // Complete the updating of ZMAT.

        temp = Math.sqrt(denom);
        tempb = zmat.get(knew + zmat_dim1) / temp;
        tempa = tau / temp;
        i__2 = npt;
        for (i__ = 1; i__ <= i__2; i__++) {
            zmat.set(i__ + zmat_dim1, tempa * zmat.get(i__ + zmat_dim1) -
                    tempb * vlag.get(i__));
        }

        // Finally, update the matrix BMAT.

        i__2 = n;
        for (j = 1; j <= i__2; j++) {
            jp = npt + j;
            w.set(jp, bmat.get(knew + j * bmat_dim1));
            tempa = (alpha * vlag.get(jp) - tau * w.get(jp)) / denom;
            tempb = (-beta * w.get(jp) - tau * vlag.get(jp)) / denom;
            i__1 = jp;
            for (i__ = 1; i__ <= i__1; i__++) {
                bmat.set(i__ + j * bmat_dim1, bmat.get(i__ + j * bmat_dim1) + tempa *
                        vlag.get(i__) + tempb * w.get(i__));
                if (i__ > npt) {
                    bmat.set(jp + (i__ - npt) * bmat_dim1, bmat.get(i__ + j * bmat_dim1));
                }
            }
        }
    } // update

    /**
     * Checks dimensions and values of boundaries and inputSigma if defined.
     */
    private void checkParameters() {
        double[] init = getStartPoint();
        if (boundaries != null) {
            if (boundaries.length != 2) {
                throw new MultiDimensionMismatchException(
                        new Integer[] { boundaries.length },
                        new Integer[] { 2 });
            }
            if (boundaries[0] == null || boundaries[1] == null) {
                throw new NoDataException();
            }
            if (boundaries[0].length != init.length) {
                throw new MultiDimensionMismatchException(
                        new Integer[] { boundaries[0].length },
                        new Integer[] { init.length });
            }
            if (boundaries[1].length != init.length) {
                throw new MultiDimensionMismatchException(
                        new Integer[] { boundaries[1].length },
                        new Integer[] { init.length });
            }
            for (int i = 0; i < init.length; i++) {
                if (boundaries[0][i] > init[i] || boundaries[1][i] < init[i]) {
                    throw new OutOfRangeException(init[i], boundaries[0][i],
                            boundaries[1][i]);
                }
            }
        }
    }

    // auxiliary subclasses

    /**
     * Double reference
     */
    private static class DoubleRef {
        /**
         * stored double value.
         */
        private double value;

        /**
         * @param value stored double value.
         */
        DoubleRef(double value) {
            this.value = value;
        }
    }

    /**
     * Integer reference
     */
    private static class IntRef {
        /**
         * stored int value.
         */
        private int value;

        /**
         * @param value stored int value.
         */
        IntRef(int value) {
            this.value = value;
        }
    }

    /**
     * Used to simulate Fortran pointers.
     */
    private static class ScopedPtr {
        /**
         * array storing elements.
         */
        private double[] w;
        /**
         * base index for access.
         */
        private int base;

        /**
         * @param w array storing elements.
         * @param base base index for access.
         */
        ScopedPtr(double[] w, int base) {
            this.w = w;
            this.base = base;
        }

        /**
         * @param index realtive index of returned ScopedPtr
         * @return ScopedPtr with new base = this.base + index
         */
        ScopedPtr ptr(int index) {
            return new ScopedPtr(w, base + index);
        }

        /**
         * @param index of accessed element relative to base.
         * @return value returned value at index.
         */
        double get(int index) {
            return w[base + index];
        }

        /**
         * @param index of accessed elements relative to base.
         * @param n number of values to be returned.
         * @return n values starting at index.
         */
        double[] getAll(int index, int n) {
            return Arrays.copyOfRange(w, base+index, base+index+n);
        }

        /**
         * @return all elements.
         */
        double[] getAll() {
            return w;
        }

        /**
         * @param index index of accessed element relative to base.
         * @param value stored at index.
         */
        void set(int index, double value) {
            w[base + index] = value;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 20; i++)
                if (base + i >= 0 && base + i < w.length)
                    sb.append("" + i + ":" + w[base + i] + "\n");
            return sb.toString();
        }

    }

    /**
     * @param n dimension.
     * @param value value set for each element.
     * @return array containing n values.
     */
    private static double[] point(int n, double value) {
        double[] ds = new double[n];
        Arrays.fill(ds, value);
        return ds;
    }

}
