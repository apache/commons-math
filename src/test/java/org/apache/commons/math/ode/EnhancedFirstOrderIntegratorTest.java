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

package org.apache.commons.math.ode;

import org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.junit.Assert;
import org.junit.Test;

public class EnhancedFirstOrderIntegratorTest {

    @Test
    public void testLowAccuracyExternalDifferentiation()
        throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, 1.0e-4, 1.0e-4);
        double hP = 1.0e-12;
        SummaryStatistics residuals0 = new SummaryStatistics();
        SummaryStatistics residuals1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            double[] y = { 1.3, b };
            integ.integrate(brusselator, 0, y, 20.0, y);
            double[] yP = { 1.3, b + hP };
            brusselator.setParameter(0, b + hP);
            integ.integrate(brusselator, 0, yP, 20.0, yP);
            residuals0.addValue((yP[0] - y[0]) / hP - brusselator.dYdP0());
            residuals1.addValue((yP[1] - y[1]) / hP - brusselator.dYdP1());
        }
        Assert.assertTrue((residuals0.getMax() - residuals0.getMin()) > 600);
        Assert.assertTrue(residuals0.getStandardDeviation() > 30);
        Assert.assertTrue((residuals1.getMax() - residuals1.getMin()) > 800);
        Assert.assertTrue(residuals1.getStandardDeviation() > 50);
    }

    @Test
    public void testHighAccuracyExternalDifferentiation()
        throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
        double hP = 1.0e-12;
        SummaryStatistics residuals0 = new SummaryStatistics();
        SummaryStatistics residuals1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            double[] y = { 1.3, b };
            integ.integrate(brusselator, 0, y, 20.0, y);
            double[] yP = { 1.3, b + hP };
            brusselator.setParameter(0, b + hP);
            integ.integrate(brusselator, 0, yP, 20.0, yP);
            residuals0.addValue((yP[0] - y[0]) / hP - brusselator.dYdP0());
            residuals1.addValue((yP[1] - y[1]) / hP - brusselator.dYdP1());
        }
        Assert.assertTrue((residuals0.getMax() - residuals0.getMin()) > 0.02);
        Assert.assertTrue((residuals0.getMax() - residuals0.getMin()) < 0.03);
        Assert.assertTrue(residuals0.getStandardDeviation() > 0.003);
        Assert.assertTrue(residuals0.getStandardDeviation() < 0.004);
        Assert.assertTrue((residuals1.getMax() - residuals1.getMin()) > 0.04);
        Assert.assertTrue((residuals1.getMax() - residuals1.getMin()) < 0.05);
        Assert.assertTrue(residuals1.getStandardDeviation() > 0.006);
        Assert.assertTrue(residuals1.getStandardDeviation() < 0.007);
    }

    @Test
    public void testInternalDifferentiation()
        throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, 1.0e-4, 1.0e-4);
        double hP = 1.0e-12;
        SummaryStatistics residuals0 = new SummaryStatistics();
        SummaryStatistics residuals1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            brusselator.setParameter(0, b);
            double[] z = { 1.3, b };
            double[][] dZdZ0 = new double[2][2];
            double[][] dZdP  = new double[2][1];
            double hY = 1.0e-12;
            EnhancedFirstOrderIntegrator extInt =
                new EnhancedFirstOrderIntegrator(integ, brusselator, new double[] { b },
                                                 new double[] { hY, hY }, new double[] { hP });
            extInt.integrate(0, z, new double[][] { { 0.0 }, { 1.0 } }, 20.0, z, dZdZ0, dZdP);
            residuals0.addValue(dZdP[0][0] - brusselator.dYdP0());
            residuals1.addValue(dZdP[1][0] - brusselator.dYdP1());
        }
        Assert.assertTrue((residuals0.getMax() - residuals0.getMin()) < 0.006);
        Assert.assertTrue(residuals0.getStandardDeviation() < 0.0009);
        Assert.assertTrue((residuals1.getMax() - residuals1.getMin()) < 0.006);
        Assert.assertTrue(residuals1.getStandardDeviation() < 0.0012);
    }

    @Test
    public void testAnalyticalDifferentiation()
        throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, 1.0e-4, 1.0e-4);
        SummaryStatistics residuals0 = new SummaryStatistics();
        SummaryStatistics residuals1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            brusselator.setParameter(0, b);
            double[] z = { 1.3, b };
            double[][] dZdZ0 = new double[2][2];
            double[][] dZdP  = new double[2][1];
            EnhancedFirstOrderIntegrator extInt =
                new EnhancedFirstOrderIntegrator(integ, brusselator);
            extInt.integrate(0, z, new double[][] { { 0.0 }, { 1.0 } }, 20.0, z, dZdZ0, dZdP);
            residuals0.addValue(dZdP[0][0] - brusselator.dYdP0());
            residuals1.addValue(dZdP[1][0] - brusselator.dYdP1());
       }
        Assert.assertTrue((residuals0.getMax() - residuals0.getMin()) < 0.004);
        Assert.assertTrue(residuals0.getStandardDeviation() < 0.001);
        Assert.assertTrue((residuals1.getMax() - residuals1.getMin()) < 0.005);
        Assert.assertTrue(residuals1.getStandardDeviation() < 0.001);
    }

    private static class Brusselator implements ParameterizedFirstOrderDifferentialEquationsWithPartials {

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
            return -1087.8787631970476 + (1050.4387741821572 + (-338.90621620263096 + 36.51793006801084 * b) * b) * b;
        }

        public double dYdP1() {
            return 1499.0904666097015 + (-1434.9574631810726 + (459.71079478756945 - 49.29949940968588 * b) * b) * b;
        }

    };

}
