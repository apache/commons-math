package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.analysis.function.Inverse;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.junit.Test;
import org.junit.Assert;

/**
 * Test of the {@link LegendreHighPrecisionRuleFactory}.
 *
 * @version $Id$
 */
public class LegendreHighPrecisionTest {
    private static GaussIntegratorFactory factory = new GaussIntegratorFactory();

    @Test
    public void testCos() {
        final UnivariateFunction cos = new Cos();

        final GaussIntegrator integrator = factory.legendreHighPrecision(7, 0, Math.PI / 2);
        final double s = integrator.integrate(cos);
        // System.out.println("s=" + s + " e=" + 1);
        Assert.assertEquals(1, s, Math.ulp(1d));
    }


    @Test
    public void testInverse() {
        final UnivariateFunction inv = new Inverse();
        final UnivariateFunction log = new Log();

        final double lo = 12.34;
        final double hi = 456.78;

        final GaussIntegrator integrator = factory.legendreHighPrecision(60, lo, hi);
        final double s = integrator.integrate(inv);
        final double expected = log.value(hi) - log.value(lo);
        // System.out.println("s=" + s + " e=" + expected);
        Assert.assertEquals(expected, s, 1e-15);
    }
}
