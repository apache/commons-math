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

package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.JacobianMatrices.MismatchedEquations;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class JacobianMatricesTest {

    @Test
    public void testLowAccuracyExternalDifferentiation()
        throws NumberIsTooSmallException, DimensionMismatchException,
               MaxCountExceededException, NoBracketingException {
        // this test does not really test JacobianMatrices,
        // it only shows that WITHOUT this class, attempting to recover
        // the jacobians from external differentiation on simple integration
        // results with low accuracy gives very poor results. In fact,
        // the curves dy/dp = g(b) when b varies from 2.88 to 3.08 are
        // essentially noise.
        // This test is taken from Hairer, Norsett and Wanner book
        // Solving Ordinary Differential Equations I (Nonstiff problems),
        // the curves dy/dp = g(b) are in figure 6.5
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-4, 1.0e-4 }, new double[] { 1.0e-4, 1.0e-4 });
        double hP = 1.0e-12;
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            double[] y = { 1.3, b };
            integ.integrate(brusselator, 0, y, 20.0, y);
            double[] yP = { 1.3, b + hP };
            integ.integrate(brusselator, 0, yP, 20.0, yP);
            residualsP0.addValue((yP[0] - y[0]) / hP - brusselator.dYdP0());
            residualsP1.addValue((yP[1] - y[1]) / hP - brusselator.dYdP1());
        }
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) > 500);
        Assert.assertTrue(residualsP0.getStandardDeviation() > 30);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) > 700);
        Assert.assertTrue(residualsP1.getStandardDeviation() > 40);
    }

    @Test
    public void testHighAccuracyExternalDifferentiation()
        throws NumberIsTooSmallException, DimensionMismatchException,
               MaxCountExceededException, NoBracketingException, UnknownParameterException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-10, 1.0e-10 }, new double[] { 1.0e-10, 1.0e-10 });
        double hP = 1.0e-12;
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            ParamBrusselator brusselator = new ParamBrusselator(b);
            double[] y = { 1.3, b };
            integ.integrate(brusselator, 0, y, 20.0, y);
            double[] yP = { 1.3, b + hP };
            brusselator.setParameter("b", b + hP);
            integ.integrate(brusselator, 0, yP, 20.0, yP);
            residualsP0.addValue((yP[0] - y[0]) / hP - brusselator.dYdP0());
            residualsP1.addValue((yP[1] - y[1]) / hP - brusselator.dYdP1());
        }
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) > 0.02);
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) < 0.03);
        Assert.assertTrue(residualsP0.getStandardDeviation() > 0.003);
        Assert.assertTrue(residualsP0.getStandardDeviation() < 0.004);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) > 0.04);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) < 0.05);
        Assert.assertTrue(residualsP1.getStandardDeviation() > 0.007);
        Assert.assertTrue(residualsP1.getStandardDeviation() < 0.008);
    }

    @Test
    public void testWrongParameterName() {
        final String name = "an-unknown-parameter";
        try {
            ParamBrusselator brusselator = new ParamBrusselator(2.9);
            brusselator.setParameter(name, 3.0);
            Assert.fail("an exception should have been thrown");
        } catch (UnknownParameterException upe) {
            Assert.assertTrue(upe.getMessage().contains(name));
            Assert.assertEquals(name, upe.getName());
        }
    }

    @Test
    public void testInternalDifferentiation()
                    throws NumberIsTooSmallException, DimensionMismatchException,
                    MaxCountExceededException, NoBracketingException,
                    UnknownParameterException, MismatchedEquations {
        AbstractIntegrator integ =
                        new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-4, 1.0e-4 }, new double[] { 1.0e-4, 1.0e-4 });
        double hP = 1.0e-12;
        double hY = 1.0e-12;
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
                ParamBrusselator brusselator = new ParamBrusselator(b);
                brusselator.setParameter(ParamBrusselator.B, b);
            double[] z = { 1.3, b };
            double[][] dZdZ0 = new double[2][2];
            double[]   dZdP  = new double[2];

            JacobianMatrices jacob = new JacobianMatrices(brusselator, new double[] { hY, hY }, ParamBrusselator.B);
            jacob.setParameterizedODE(brusselator);
            jacob.setParameterStep(ParamBrusselator.B, hP);
            jacob.setInitialParameterJacobian(ParamBrusselator.B, new double[] { 0.0, 1.0 });

            ExpandableStatefulODE efode = new ExpandableStatefulODE(brusselator);
            efode.setTime(0);
            efode.setPrimaryState(z);
            jacob.registerVariationalEquations(efode);

            integ.setMaxEvaluations(5000);
            integ.integrate(efode, 20.0);
            jacob.getCurrentMainSetJacobian(dZdZ0);
            jacob.getCurrentParameterJacobian(ParamBrusselator.B, dZdP);
//            Assert.assertEquals(5000, integ.getMaxEvaluations());
//            Assert.assertTrue(integ.getEvaluations() > 1500);
//            Assert.assertTrue(integ.getEvaluations() < 2100);
//            Assert.assertEquals(4 * integ.getEvaluations(), integ.getEvaluations());
            residualsP0.addValue(dZdP[0] - brusselator.dYdP0());
            residualsP1.addValue(dZdP[1] - brusselator.dYdP1());
        }
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) < 0.02);
        Assert.assertTrue(residualsP0.getStandardDeviation() < 0.003);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) < 0.05);
        Assert.assertTrue(residualsP1.getStandardDeviation() < 0.01);
    }

    @Test
    public void testAnalyticalDifferentiation()
        throws MaxCountExceededException, DimensionMismatchException,
               NumberIsTooSmallException, NoBracketingException,
               UnknownParameterException, MismatchedEquations {
        AbstractIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-4, 1.0e-4 }, new double[] { 1.0e-4, 1.0e-4 });
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            double[] z = { 1.3, b };
            double[][] dZdZ0 = new double[2][2];
            double[]   dZdP  = new double[2];

            JacobianMatrices jacob = new JacobianMatrices(brusselator, Brusselator.B);
            jacob.addParameterJacobianProvider(brusselator);
            jacob.setInitialParameterJacobian(Brusselator.B, new double[] { 0.0, 1.0 });

            ExpandableStatefulODE efode = new ExpandableStatefulODE(brusselator);
            efode.setTime(0);
            efode.setPrimaryState(z);
            jacob.registerVariationalEquations(efode);

            integ.setMaxEvaluations(5000);
            integ.integrate(efode, 20.0);
            jacob.getCurrentMainSetJacobian(dZdZ0);
            jacob.getCurrentParameterJacobian(Brusselator.B, dZdP);
//            Assert.assertEquals(5000, integ.getMaxEvaluations());
//            Assert.assertTrue(integ.getEvaluations() > 350);
//            Assert.assertTrue(integ.getEvaluations() < 510);
            residualsP0.addValue(dZdP[0] - brusselator.dYdP0());
            residualsP1.addValue(dZdP[1] - brusselator.dYdP1());
        }
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) < 0.014);
        Assert.assertTrue(residualsP0.getStandardDeviation() < 0.003);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) < 0.05);
        Assert.assertTrue(residualsP1.getStandardDeviation() < 0.01);
    }

    @Test
    public void testFinalResult()
        throws MaxCountExceededException, DimensionMismatchException,
               NumberIsTooSmallException, NoBracketingException,
               UnknownParameterException, MismatchedEquations {

        AbstractIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-10, 1.0e-10 }, new double[] { 1.0e-10, 1.0e-10 });
        double[] y = new double[] { 0.0, 1.0 };
        Circle circle = new Circle(y, 1.0, 1.0, 0.1);

        JacobianMatrices jacob = new JacobianMatrices(circle, Circle.CX, Circle.CY, Circle.OMEGA);
        jacob.addParameterJacobianProvider(circle);
        jacob.setInitialMainStateJacobian(circle.exactDyDy0(0));
        jacob.setInitialParameterJacobian(Circle.CX, circle.exactDyDcx(0));
        jacob.setInitialParameterJacobian(Circle.CY, circle.exactDyDcy(0));
        jacob.setInitialParameterJacobian(Circle.OMEGA, circle.exactDyDom(0));

        ExpandableStatefulODE efode = new ExpandableStatefulODE(circle);
        efode.setTime(0);
        efode.setPrimaryState(y);
        jacob.registerVariationalEquations(efode);

        integ.setMaxEvaluations(5000);

        double t = 18 * FastMath.PI;
        integ.integrate(efode, t);
        y = efode.getPrimaryState();
        for (int i = 0; i < y.length; ++i) {
            Assert.assertEquals(circle.exactY(t)[i], y[i], 1.0e-9);
        }

        double[][] dydy0 = new double[2][2];
        jacob.getCurrentMainSetJacobian(dydy0);
        for (int i = 0; i < dydy0.length; ++i) {
            for (int j = 0; j < dydy0[i].length; ++j) {
                Assert.assertEquals(circle.exactDyDy0(t)[i][j], dydy0[i][j], 1.0e-9);
            }
        }
        double[] dydcx = new double[2];
        jacob.getCurrentParameterJacobian(Circle.CX, dydcx);
        for (int i = 0; i < dydcx.length; ++i) {
            Assert.assertEquals(circle.exactDyDcx(t)[i], dydcx[i], 1.0e-7);
        }
        double[] dydcy = new double[2];
        jacob.getCurrentParameterJacobian(Circle.CY, dydcy);
        for (int i = 0; i < dydcy.length; ++i) {
            Assert.assertEquals(circle.exactDyDcy(t)[i], dydcy[i], 1.0e-7);
        }
        double[] dydom = new double[2];
        jacob.getCurrentParameterJacobian(Circle.OMEGA, dydom);
        for (int i = 0; i < dydom.length; ++i) {
            Assert.assertEquals(circle.exactDyDom(t)[i], dydom[i], 1.0e-7);
        }
    }

    @Test
    public void testParameterizable()
        throws MaxCountExceededException, DimensionMismatchException,
               NumberIsTooSmallException, NoBracketingException,
               UnknownParameterException, MismatchedEquations {

        AbstractIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-10, 1.0e-10 }, new double[] { 1.0e-10, 1.0e-10 });
        double[] y = new double[] { 0.0, 1.0 };
        ParameterizedCircle pcircle = new ParameterizedCircle(y, 1.0, 1.0, 0.1);

        double hP = 1.0e-12;
        double hY = 1.0e-12;

        JacobianMatrices jacob = new JacobianMatrices(pcircle, new double[] { hY, hY },
                                                      ParameterizedCircle.CX, ParameterizedCircle.CY,
                                                      ParameterizedCircle.OMEGA);
        jacob.setParameterizedODE(pcircle);
        jacob.setParameterStep(ParameterizedCircle.CX,    hP);
        jacob.setParameterStep(ParameterizedCircle.CY,    hP);
        jacob.setParameterStep(ParameterizedCircle.OMEGA, hP);
        jacob.setInitialMainStateJacobian(pcircle.exactDyDy0(0));
        jacob.setInitialParameterJacobian(ParameterizedCircle.CX, pcircle.exactDyDcx(0));
        jacob.setInitialParameterJacobian(ParameterizedCircle.CY, pcircle.exactDyDcy(0));
        jacob.setInitialParameterJacobian(ParameterizedCircle.OMEGA, pcircle.exactDyDom(0));

        ExpandableStatefulODE efode = new ExpandableStatefulODE(pcircle);
        efode.setTime(0);
        efode.setPrimaryState(y);
        jacob.registerVariationalEquations(efode);

        integ.setMaxEvaluations(50000);

        double t = 18 * FastMath.PI;
        integ.integrate(efode, t);
        y = efode.getPrimaryState();
        for (int i = 0; i < y.length; ++i) {
            Assert.assertEquals(pcircle.exactY(t)[i], y[i], 1.0e-9);
        }

        double[][] dydy0 = new double[2][2];
        jacob.getCurrentMainSetJacobian(dydy0);
        for (int i = 0; i < dydy0.length; ++i) {
            for (int j = 0; j < dydy0[i].length; ++j) {
                Assert.assertEquals(pcircle.exactDyDy0(t)[i][j], dydy0[i][j], 5.0e-4);
            }
        }

        double[] dydp0 = new double[2];
        jacob.getCurrentParameterJacobian(ParameterizedCircle.CX, dydp0);
        for (int i = 0; i < dydp0.length; ++i) {
            Assert.assertEquals(pcircle.exactDyDcx(t)[i], dydp0[i], 5.0e-4);
        }

        double[] dydp1 = new double[2];
        jacob.getCurrentParameterJacobian(ParameterizedCircle.OMEGA, dydp1);
        for (int i = 0; i < dydp1.length; ++i) {
            Assert.assertEquals(pcircle.exactDyDom(t)[i], dydp1[i], 1.0e-2);
        }
    }

    private static class Brusselator extends AbstractParameterizable
        implements MainStateJacobianProvider, ParameterJacobianProvider {

        public static final String B = "b";

        private double b;

        public Brusselator(double b) {
            super(B);
            this.b = b;
        }

        public int getDimension() {
            return 2;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot) {
            double prod = y[0] * y[0] * y[1];
            yDot[0] = 1 + prod - (b + 1) * y[0];
            yDot[1] = b * y[0] - prod;
        }

        public void computeMainStateJacobian(double t, double[] y, double[] yDot,
                                             double[][] dFdY) {
            double p = 2 * y[0] * y[1];
            double y02 = y[0] * y[0];
            dFdY[0][0] = p - (1 + b);
            dFdY[0][1] = y02;
            dFdY[1][0] = b - p;
            dFdY[1][1] = -y02;
        }

        public void computeParameterJacobian(double t, double[] y, double[] yDot,
                                             String paramName, double[] dFdP) {
            if (isSupported(paramName)) {
                dFdP[0] = -y[0];
                dFdP[1] = y[0];
            } else {
                dFdP[0] = 0;
                dFdP[1] = 0;
            }
        }

        public double dYdP0() {
            return -1088.232716447743 + (1050.775747149553 + (-339.012934631828 + 36.52917025056327 * b) * b) * b;
        }

        public double dYdP1() {
            return 1502.824469929139 + (-1438.6974831849952 + (460.959476642384 - 49.43847385647082 * b) * b) * b;
        }

    }

    private static class ParamBrusselator extends AbstractParameterizable
        implements FirstOrderDifferentialEquations, ParameterizedODE {

        public static final String B = "b";

        private double b;

        public ParamBrusselator(double b) {
            super(B);
            this.b = b;
        }

        public int getDimension() {
            return 2;
        }

        /** {@inheritDoc} */
        public double getParameter(final String name)
            throws UnknownParameterException {
            complainIfNotSupported(name);
            return b;
        }

        /** {@inheritDoc} */
        public void setParameter(final String name, final double value)
            throws UnknownParameterException {
            complainIfNotSupported(name);
            b = value;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot) {
            double prod = y[0] * y[0] * y[1];
            yDot[0] = 1 + prod - (b + 1) * y[0];
            yDot[1] = b * y[0] - prod;
        }

        public double dYdP0() {
            return -1088.232716447743 + (1050.775747149553 + (-339.012934631828 + 36.52917025056327 * b) * b) * b;
        }

        public double dYdP1() {
            return 1502.824469929139 + (-1438.6974831849952 + (460.959476642384 - 49.43847385647082 * b) * b) * b;
        }

    }

    /** ODE representing a point moving on a circle with provided center and angular rate. */
    private static class Circle extends AbstractParameterizable
        implements MainStateJacobianProvider, ParameterJacobianProvider {

        public static final String CX = "cx";
        public static final String CY = "cy";
        public static final String OMEGA = "omega";

        private final double[] y0;
        private double cx;
        private double cy;
        private double omega;

        public Circle(double[] y0, double cx, double cy, double omega) {
            super(CX,CY,OMEGA);
            this.y0    = y0.clone();
            this.cx    = cx;
            this.cy    = cy;
            this.omega = omega;
        }

        public int getDimension() {
            return 2;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot) {
            yDot[0] = omega * (cy - y[1]);
            yDot[1] = omega * (y[0] - cx);
        }

        public void computeMainStateJacobian(double t, double[] y,
                                             double[] yDot, double[][] dFdY) {
            dFdY[0][0] = 0;
            dFdY[0][1] = -omega;
            dFdY[1][0] = omega;
            dFdY[1][1] = 0;
        }

        public void computeParameterJacobian(double t, double[] y, double[] yDot,
                                             String paramName, double[] dFdP)
            throws UnknownParameterException {
            complainIfNotSupported(paramName);
            if (paramName.equals(CX)) {
                dFdP[0] = 0;
                dFdP[1] = -omega;
            } else if (paramName.equals(CY)) {
                dFdP[0] = omega;
                dFdP[1] = 0;
            }  else {
                dFdP[0] = cy - y[1];
                dFdP[1] = y[0] - cx;
            }
        }

        public double[] exactY(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            double dx0 = y0[0] - cx;
            double dy0 = y0[1] - cy;
            return new double[] {
                cx + cos * dx0 - sin * dy0,
                cy + sin * dx0 + cos * dy0
            };
        }

        public double[][] exactDyDy0(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            return new double[][] {
                { cos, -sin },
                { sin,  cos }
            };
        }

        public double[] exactDyDcx(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            return new double[] {1 - cos, -sin};
        }

        public double[] exactDyDcy(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            return new double[] {sin, 1 - cos};
        }

        public double[] exactDyDom(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            double dx0 = y0[0] - cx;
            double dy0 = y0[1] - cy;
            return new double[] { -t * (sin * dx0 + cos * dy0) , t * (cos * dx0 - sin * dy0) };
        }

    }

    /** ODE representing a point moving on a circle with provided center and angular rate. */
    private static class ParameterizedCircle extends AbstractParameterizable
        implements FirstOrderDifferentialEquations, ParameterizedODE {

        public static final String CX = "cx";
        public static final String CY = "cy";
        public static final String OMEGA = "omega";

        private final double[] y0;
        private double cx;
        private double cy;
        private double omega;

        public ParameterizedCircle(double[] y0, double cx, double cy, double omega) {
            super(CX,CY,OMEGA);
            this.y0    = y0.clone();
            this.cx    = cx;
            this.cy    = cy;
            this.omega = omega;
        }

        public int getDimension() {
            return 2;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot) {
            yDot[0] = omega * (cy - y[1]);
            yDot[1] = omega * (y[0] - cx);
        }

        public double getParameter(final String name)
            throws UnknownParameterException {
            if (name.equals(CX)) {
                return cx;
            } else if (name.equals(CY)) {
                    return cy;
            } else if (name.equals(OMEGA)) {
                return omega;
            } else {
                throw new UnknownParameterException(name);
            }
        }

        public void setParameter(final String name, final double value)
            throws UnknownParameterException {
            if (name.equals(CX)) {
                cx = value;
            } else if (name.equals(CY)) {
                cy = value;
            } else if (name.equals(OMEGA)) {
                omega = value;
            } else {
                throw new UnknownParameterException(name);
            }
        }

        public double[] exactY(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            double dx0 = y0[0] - cx;
            double dy0 = y0[1] - cy;
            return new double[] {
                cx + cos * dx0 - sin * dy0,
                cy + sin * dx0 + cos * dy0
            };
        }

        public double[][] exactDyDy0(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            return new double[][] {
                { cos, -sin },
                { sin,  cos }
            };
        }

        public double[] exactDyDcx(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            return new double[] {1 - cos, -sin};
        }

        public double[] exactDyDcy(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            return new double[] {sin, 1 - cos};
        }

        public double[] exactDyDom(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            double dx0 = y0[0] - cx;
            double dy0 = y0[1] - cy;
            return new double[] { -t * (sin * dx0 + cos * dy0) , t * (cos * dx0 - sin * dy0) };
        }

    }

}
