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

package org.apache.commons.math3.ode.sampling;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.GraggBulirschStoerIntegrator;
import org.junit.Test;

/** Base class for step normalizer output tests. */
public abstract class StepNormalizerOutputTestBase
    implements FirstOrderDifferentialEquations, FixedStepHandler {

    /** The normalized output time values. */
    private List<Double> output;

    /**
     * Returns the start time.
     * @return the start time
     */
    protected abstract double getStart();

    /**
     * Returns the end time.
     * @return the end time
     */
    protected abstract double getEnd();

    /**
     * Returns the expected normalized output time values for increment mode.
     * @return the expected normalized output time values for increment mode
     */
    protected abstract double[] getExpInc();

    /**
     * Returns the expected reversed normalized output time values for
     * increment mode.
     * @return the expected reversed normalized output time values for
     * increment mode
     */
    protected abstract double[] getExpIncRev();

    /**
     * Returns the expected normalized output time values for multiples mode.
     * @return the expected normalized output time values for multiples mode
     */
    protected abstract double[] getExpMul();

    /**
     * Returns the expected reversed normalized output time values for
     * multiples mode.
     * @return the expected reversed normalized output time values for
     * multiples mode
     */
    protected abstract double[] getExpMulRev();

    /**
     * Returns the offsets for the unit tests below, in the order they are
     * given below. For each test, the left and right offsets are returned.
     * @return the offsets for the unit tests below, in the order they are
     * given below
     */
    protected abstract int[][] getO();

    /**
     * Get the array, given left and right offsets.
     * @param a the input array
     * @param offsetL the left side offset
     * @param offsetR the right side offset
     * @return the modified array
     */
    private double[] getArray(double[] a, int offsetL, int offsetR) {
        double[] copy = new double[a.length - offsetR - offsetL];
        System.arraycopy(a, offsetL, copy, 0, copy.length);
        return copy;
    }

    @Test
    public void testIncNeither()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpInc(), getO()[0][0], getO()[0][1]);
        doTest(StepNormalizerMode.INCREMENT, StepNormalizerBounds.NEITHER, exp, false);
    }

    @Test
    public void testIncNeitherRev()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpIncRev(), getO()[1][0], getO()[1][1]);
        doTest(StepNormalizerMode.INCREMENT, StepNormalizerBounds.NEITHER, exp, true);
    }

    @Test
    public void testIncFirst()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpInc(), getO()[2][0], getO()[2][1]);
        doTest(StepNormalizerMode.INCREMENT, StepNormalizerBounds.FIRST, exp, false);
    }

    @Test
    public void testIncFirstRev()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpIncRev(), getO()[3][0], getO()[3][1]);
        doTest(StepNormalizerMode.INCREMENT, StepNormalizerBounds.FIRST, exp, true);
    }

    @Test
    public void testIncLast()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpInc(), getO()[4][0], getO()[4][1]);
        doTest(StepNormalizerMode.INCREMENT, StepNormalizerBounds.LAST, exp, false);
    }

    @Test
    public void testIncLastRev()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpIncRev(), getO()[5][0], getO()[5][1]);
        doTest(StepNormalizerMode.INCREMENT, StepNormalizerBounds.LAST, exp, true);
    }

    @Test
    public void testIncBoth()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpInc(), getO()[6][0], getO()[6][1]);
        doTest(StepNormalizerMode.INCREMENT, StepNormalizerBounds.BOTH, exp, false);
    }

    @Test
    public void testIncBothRev()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpIncRev(), getO()[7][0], getO()[7][1]);
        doTest(StepNormalizerMode.INCREMENT, StepNormalizerBounds.BOTH, exp, true);
    }

    @Test
    public void testMulNeither()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpMul(), getO()[8][0], getO()[8][1]);
        doTest(StepNormalizerMode.MULTIPLES, StepNormalizerBounds.NEITHER, exp, false);
    }

    @Test
    public void testMulNeitherRev()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpMulRev(), getO()[9][0], getO()[9][1]);
        doTest(StepNormalizerMode.MULTIPLES, StepNormalizerBounds.NEITHER, exp, true);
    }

    @Test
    public void testMulFirst()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpMul(), getO()[10][0], getO()[10][1]);
        doTest(StepNormalizerMode.MULTIPLES, StepNormalizerBounds.FIRST, exp, false);
    }

    @Test
    public void testMulFirstRev()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpMulRev(), getO()[11][0], getO()[11][1]);
        doTest(StepNormalizerMode.MULTIPLES, StepNormalizerBounds.FIRST, exp, true);
    }

    @Test
    public void testMulLast()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpMul(), getO()[12][0], getO()[12][1]);
        doTest(StepNormalizerMode.MULTIPLES, StepNormalizerBounds.LAST, exp, false);
    }

    @Test
    public void testMulLastRev()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpMulRev(), getO()[13][0], getO()[13][1]);
        doTest(StepNormalizerMode.MULTIPLES, StepNormalizerBounds.LAST, exp, true);
    }

    @Test
    public void testMulBoth()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpMul(), getO()[14][0], getO()[14][1]);
        doTest(StepNormalizerMode.MULTIPLES, StepNormalizerBounds.BOTH, exp, false);
    }

    @Test
    public void testMulBothRev()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        double[] exp = getArray(getExpMulRev(), getO()[15][0], getO()[15][1]);
        doTest(StepNormalizerMode.MULTIPLES, StepNormalizerBounds.BOTH, exp, true);
    }

    /**
     * The actual step normalizer output test code, shared by all the unit
     * tests.
     *
     * @param mode the step normalizer mode to use
     * @param bounds the step normalizer bounds setting to use
     * @param expected the expected output (normalized time points)
     * @param reverse whether to reverse the integration direction
     * @throws NoBracketingException
     * @throws MaxCountExceededException
     * @throws NumberIsTooSmallException
     * @throws DimensionMismatchException
     */
    private void doTest(StepNormalizerMode mode, StepNormalizerBounds bounds,
                        double[] expected, boolean reverse)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        // Forward test.
        FirstOrderIntegrator integ = new GraggBulirschStoerIntegrator(
                                                        1e-8, 1.0, 1e-5, 1e-5);
        integ.addStepHandler(new StepNormalizer(0.5, this, mode, bounds));
        double[] y   = {0.0};
        double start = reverse ? getEnd()   : getStart();
        double end   = reverse ? getStart() : getEnd();
        output       = new ArrayList<Double>();
        integ.integrate(this, start, y, end, y);
        double[] actual = new double[output.size()];
        for(int i = 0; i < actual.length; i++) {
            actual[i] = output.get(i);
        }
        Assert.assertArrayEquals(expected, actual, 1e-5);
    }

    /** {@inheritDoc} */
    public int getDimension() {
        return 1;
    }

    /** {@inheritDoc} */
    public void computeDerivatives(double t, double[] y, double[] yDot) {
        yDot[0] = y[0];
    }

    /** {@inheritDoc} */
    public void init(double t0, double[] y0, double t) {
    }

    /** {@inheritDoc} */
    public void handleStep(double t, double[] y, double[] yDot, boolean isLast) {
        output.add(t);
    }

}
