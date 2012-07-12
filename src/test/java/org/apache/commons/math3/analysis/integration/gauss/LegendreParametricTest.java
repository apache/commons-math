package org.apache.commons.math3.analysis.integration.gauss;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test of the {@link LegendreRuleFactory}.
 * This parameterized test extends the standard test for Gaussian quadrature
 * rule, where each monomial is tested in turn.
 * Parametrization allows to test automatically 0, 1, ... , {@link #MAX_NUM_POINTS}
 * quadrature rules.
 *
 * @version $Id$
 */
@RunWith(value=Parameterized.class)
public class LegendreParametricTest extends GaussianQuadratureAbstractTest {
    private static GaussIntegratorFactory factory = new GaussIntegratorFactory();

    /**
     * The highest order quadrature rule to be tested.
     */
    public static final int MAX_NUM_POINTS = 30;

    /**
     * Creates a new instance of this test, with the specified number of nodes
     * for the Gauss-Legendre quadrature rule.
     *
     * @param numberOfPoints Order of integration rule.
     * @param maxDegree Maximum degree of monomials to be tested.
     * @param eps Value of &epsilon;.
     * @param numUlps Value of the maximum relative error (in ulps).
     */
    public LegendreParametricTest(int numberOfPoints,
                                  int maxDegree,
                                  double eps,
                                  double numUlps) {
        super(factory.legendre(numberOfPoints),
              maxDegree, eps, numUlps);
    }

    /**
     * Returns the collection of parameters to be passed to the constructor of
     * this class.
     * Gauss-Legendre quadrature rules of order 1, ..., {@link #MAX_NUM_POINTS}
     * will be constructed.
     *
     * @return the collection of parameters for this parameterized test.
     */
    @Parameters
    public static Collection<Object[]> getParameters() {
        final ArrayList<Object[]> parameters = new ArrayList<Object[]>();
        for (int k = 1; k <= MAX_NUM_POINTS; k++) {
            parameters.add(new Object[] { k, 2 * k - 1, Math.ulp(1d), 91d });
        }
        return parameters;
    }

    @Override
    public double getExpectedValue(final int n) {
        if (n % 2 == 1) {
            return 0;
        }
        return 2d / (n + 1);
    }
}
