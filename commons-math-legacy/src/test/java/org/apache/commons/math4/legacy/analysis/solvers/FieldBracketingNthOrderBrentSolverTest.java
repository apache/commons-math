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

package org.apache.commons.math4.legacy.analysis.solvers;

import org.apache.commons.math4.legacy.analysis.RealFieldUnivariateFunction;
import org.apache.commons.math4.legacy.core.dfp.Dfp;
import org.apache.commons.math4.legacy.core.dfp.DfpField;
import org.apache.commons.math4.legacy.core.dfp.DfpMath;
import org.apache.commons.math4.legacy.exception.MathInternalError;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link FieldBracketingNthOrderBrentSolver bracketing n<sup>th</sup> order Brent} solver.
 *
 */
public final class FieldBracketingNthOrderBrentSolverTest {

    @Test(expected=NumberIsTooSmallException.class)
    public void testInsufficientOrder3() {
        new FieldBracketingNthOrderBrentSolver<>(relativeAccuracy, absoluteAccuracy,
                                                    functionValueAccuracy, 1);
    }

    @Test
    public void testConstructorOK() {
        FieldBracketingNthOrderBrentSolver<Dfp> solver =
                new FieldBracketingNthOrderBrentSolver<>(relativeAccuracy, absoluteAccuracy,
                                                            functionValueAccuracy, 2);
        Assert.assertEquals(2, solver.getMaximalOrder());
    }

    @Test
    public void testConvergenceOnFunctionAccuracy() {
        FieldBracketingNthOrderBrentSolver<Dfp> solver =
                new FieldBracketingNthOrderBrentSolver<>(relativeAccuracy, absoluteAccuracy,
                                                            field.newDfp(1.0e-20), 20);
        RealFieldUnivariateFunction<Dfp> f = new RealFieldUnivariateFunction<Dfp>() {
            @Override
            public Dfp value(Dfp x) {
                Dfp one     = field.getOne();
                Dfp oneHalf = one.divide(2);
                Dfp xMo     = x.subtract(one);
                Dfp xMh     = x.subtract(oneHalf);
                Dfp xPh     = x.add(oneHalf);
                Dfp xPo     = x.add(one);
                return xMo.multiply(xMh).multiply(x).multiply(xPh).multiply(xPo);
            }
        };

        Dfp result = solver.solve(20, f, field.newDfp(0.2), field.newDfp(0.9),
                                  field.newDfp(0.4), AllowedSolution.BELOW_SIDE);
        Assert.assertTrue(f.value(result).abs().lessThan(solver.getFunctionValueAccuracy()));
        Assert.assertTrue(f.value(result).negativeOrNull());
        Assert.assertTrue(result.subtract(field.newDfp(0.5)).subtract(solver.getAbsoluteAccuracy()).positiveOrNull());
        result = solver.solve(20, f, field.newDfp(-0.9), field.newDfp(-0.2),
                              field.newDfp(-0.4), AllowedSolution.ABOVE_SIDE);
        Assert.assertTrue(f.value(result).abs().lessThan(solver.getFunctionValueAccuracy()));
        Assert.assertTrue(f.value(result).positiveOrNull());
        Assert.assertTrue(result.add(field.newDfp(0.5)).subtract(solver.getAbsoluteAccuracy()).negativeOrNull());
    }

    @Test
    public void testNeta() {

        // the following test functions come from Beny Neta's paper:
        // "Several New Methods for solving Equations"
        // intern J. Computer Math Vol 23 pp 265-282
        // available here: http://www.math.nps.navy.mil/~bneta/SeveralNewMethods.PDF
        for (AllowedSolution allowed : AllowedSolution.values()) {
            check(new RealFieldUnivariateFunction<Dfp>() {
                @Override
                public Dfp value(Dfp x) {
                    return DfpMath.sin(x).subtract(x.divide(2));
                }
            }, 200, -2.0, 2.0, allowed);

            check(new RealFieldUnivariateFunction<Dfp>() {
                @Override
                public Dfp value(Dfp x) {
                    return DfpMath.pow(x, 5).add(x).subtract(field.newDfp(10000));
                }
            }, 200, -5.0, 10.0, allowed);

            check(new RealFieldUnivariateFunction<Dfp>() {
                @Override
                public Dfp value(Dfp x) {
                    return x.sqrt().subtract(field.getOne().divide(x)).subtract(field.newDfp(3));
                }
            }, 200, 0.001, 10.0, allowed);

            check(new RealFieldUnivariateFunction<Dfp>() {
                @Override
                public Dfp value(Dfp x) {
                    return DfpMath.exp(x).add(x).subtract(field.newDfp(20));
                }
            }, 200, -5.0, 5.0, allowed);

            check(new RealFieldUnivariateFunction<Dfp>() {
                @Override
                public Dfp value(Dfp x) {
                    return DfpMath.log(x).add(x.sqrt()).subtract(field.newDfp(5));
                }
            }, 200, 0.001, 10.0, allowed);

            check(new RealFieldUnivariateFunction<Dfp>() {
                @Override
                public Dfp value(Dfp x) {
                    return x.subtract(field.getOne()).multiply(x).multiply(x).subtract(field.getOne());
                }
            }, 200, -0.5, 1.5, allowed);
        }
    }

    private void check(RealFieldUnivariateFunction<Dfp> f, int maxEval, double min, double max,
                       AllowedSolution allowedSolution) {
        FieldBracketingNthOrderBrentSolver<Dfp> solver =
                new FieldBracketingNthOrderBrentSolver<>(relativeAccuracy, absoluteAccuracy,
                                                     functionValueAccuracy, 20);
        Dfp xResult = solver.solve(maxEval, f, field.newDfp(min), field.newDfp(max),
                                   allowedSolution);
        Dfp yResult = f.value(xResult);
        boolean increasing;
        switch (allowedSolution) {
        case ANY_SIDE :
            Assert.assertTrue(yResult.abs().lessThan(functionValueAccuracy.multiply(2)));
            break;
        case LEFT_SIDE :
            increasing = f.value(xResult).add(absoluteAccuracy).greaterThan(yResult);
            Assert.assertTrue(increasing ? yResult.negativeOrNull() : yResult.positiveOrNull());
            break;
        case RIGHT_SIDE :
            increasing = f.value(xResult).add(absoluteAccuracy).greaterThan(yResult);
            Assert.assertTrue(increasing ? yResult.positiveOrNull() : yResult.negativeOrNull());
            break;
        case BELOW_SIDE :
            Assert.assertTrue(yResult.negativeOrNull());
            break;
        case ABOVE_SIDE :
            Assert.assertTrue(yResult.positiveOrNull());
            break;
        default :
            // this should never happen
            throw new MathInternalError(null);
        }
    }

    @Before
    public void setUp() {
        field                 = new DfpField(50);
        absoluteAccuracy      = field.newDfp(1.0e-45);
        relativeAccuracy      = field.newDfp(1.0e-45);
        functionValueAccuracy = field.newDfp(1.0e-45);
    }

    private DfpField field;
    private Dfp      absoluteAccuracy;
    private Dfp      relativeAccuracy;
    private Dfp      functionValueAccuracy;
}
