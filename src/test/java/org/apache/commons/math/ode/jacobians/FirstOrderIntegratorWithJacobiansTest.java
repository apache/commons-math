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

package org.apache.commons.math.ode.jacobians;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.junit.Assert;
import org.junit.Test;

public class FirstOrderIntegratorWithJacobiansTest {

    @Test
    public void testLowAccuracyExternalDifferentiation()
        throws IntegratorException, DerivativeException {
        // this test does not really test FirstOrderIntegratorWithJacobians,
        // it only shows the WITHOUT this class, attempting to recover
        // the jacobians from external differentiation on simple integration
        // results with loo accuracy gives very poor results. In fact,
        // the curves dy/dp = g(b) when b varies from 2.88 to 3.08 are
        // essentially noise.
        // This test is taken from Heirer, Norsett and Wanner book
        // Solving Ordinary Differential Equations I (Nonstiff problems),
        // the curves dy/dp = g(b) are in figure 6.5
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, 1.0e-4, 1.0e-4);
        double hP = 1.0e-12;
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            double[] y = { 1.3, b };
            integ.integrate(brusselator, 0, y, 20.0, y);
            double[] yP = { 1.3, b + hP };
            brusselator.setParameter(0, b + hP);
            integ.integrate(brusselator, 0, yP, 20.0, yP);
            residualsP0.addValue((yP[0] - y[0]) / hP - brusselator.dYdP0());
            residualsP1.addValue((yP[1] - y[1]) / hP - brusselator.dYdP1());
        }
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) > 600);
        Assert.assertTrue(residualsP0.getStandardDeviation() > 30);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) > 800);
        Assert.assertTrue(residualsP1.getStandardDeviation() > 50);
    }

    @Test
    public void testHighAccuracyExternalDifferentiation()
        throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
        double hP = 1.0e-12;
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            double[] y = { 1.3, b };
            integ.integrate(brusselator, 0, y, 20.0, y);
            double[] yP = { 1.3, b + hP };
            brusselator.setParameter(0, b + hP);
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
        Assert.assertTrue(residualsP1.getStandardDeviation() > 0.006);
        Assert.assertTrue(residualsP1.getStandardDeviation() < 0.007);
    }

    @Test
    public void testInternalDifferentiation()
        throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, 1.0e-4, 1.0e-4);
        double hP = 1.0e-12;
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            brusselator.setParameter(0, b);
            double[] z = { 1.3, b };
            double[][] dZdZ0 = new double[2][2];
            double[][] dZdP  = new double[2][1];
            double hY = 1.0e-12;
            FirstOrderIntegratorWithJacobians extInt =
                new FirstOrderIntegratorWithJacobians(integ, brusselator, new double[] { b },
                                                 new double[] { hY, hY }, new double[] { hP });
            extInt.integrate(0, z, new double[][] { { 0.0 }, { 1.0 } }, 20.0, z, dZdZ0, dZdP);
            residualsP0.addValue(dZdP[0][0] - brusselator.dYdP0());
            residualsP1.addValue(dZdP[1][0] - brusselator.dYdP1());
        }
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) < 0.006);
        Assert.assertTrue(residualsP0.getStandardDeviation() < 0.0009);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) < 0.009);
        Assert.assertTrue(residualsP1.getStandardDeviation() < 0.0014);
    }

    @Test
    public void testAnalyticalDifferentiation()
        throws IntegratorException, DerivativeException, OptimizationException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            brusselator.setParameter(0, b);
            double[] z = { 1.3, b };
            double[][] dZdZ0 = new double[2][2];
            double[][] dZdP  = new double[2][1];
            FirstOrderIntegratorWithJacobians extInt =
                new FirstOrderIntegratorWithJacobians(integ, brusselator);
            extInt.integrate(0, z, new double[][] { { 0.0 }, { 1.0 } }, 20.0, z, dZdZ0, dZdP);
            residualsP0.addValue(dZdP[0][0] - brusselator.dYdP0());
            residualsP1.addValue(dZdP[1][0] - brusselator.dYdP1());
        }
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) < 0.004);
        Assert.assertTrue(residualsP0.getStandardDeviation() < 0.0008);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) < 0.005);
        Assert.assertTrue(residualsP1.getStandardDeviation() < 0.0010);
    }

    private static class Brusselator implements ParameterizedODEWithJacobians {

        private double b;

        public Brusselator(double b) {
            this.b = b;
        }

        public int getDimension() {
            return 2;
        }

        public void setParameter(int i, double p) {
            b = p;
        }

        public int getParametersDimension() {
            return 1;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot) {
            double prod = y[0] * y[0] * y[1];
            yDot[0] = 1 + prod - (b + 1) * y[0];
            yDot[1] = b * y[0] - prod;
        }

        public void computeJacobians(double t, double[] y, double[] yDot, double[][] dFdY, double[][] dFdP) {
            double p = 2 * y[0] * y[1];
            double y02 = y[0] * y[0];
            dFdY[0][0] = p - (1 + b);
            dFdY[0][1] = y02;
            dFdY[1][0] = b - p;
            dFdY[1][1] = -y02;
            dFdP[0][0] = -y[0];
            dFdP[1][0] = y[0];
        }

        public double dYdP0() {
            return -1088.232716447743 + (1050.775747149553 + (-339.012934631828 + 36.52917025056327 * b) * b) * b;
        }

        public double dYdP1() {
            return 1502.824469929139 + (-1438.6974831849952 + (460.959476642384 - 49.43847385647082 * b) * b) * b;
        }

    };

}
