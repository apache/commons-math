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
package org.apache.commons.math4.legacy.optim.linear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.Assert;

import org.apache.commons.numbers.core.Precision;
import org.apache.commons.numbers.core.Sum;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.TooManyIterationsException;
import org.apache.commons.math4.legacy.optim.MaxIter;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;

public class SimplexSolverTest {
    private static final MaxIter DEFAULT_MAX_ITER = new MaxIter(100);

    @Test
    public void testMath842Cycle() {
        // from http://www.math.toronto.edu/mpugh/Teaching/APM236_04/bland
        //      maximize 10 x1 - 57 x2 - 9 x3 - 24 x4
        //      subject to
        //          1/2 x1 - 11/2 x2 - 5/2 x3 + 9 x4  <= 0
        //          1/2 x1 -  3/2 x2 - 1/2 x3 +   x4  <= 0
        //              x1                  <= 1
        //      x1,x2,x3,x4 >= 0

        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 10, -57, -9, -24}, 0);

        ArrayList<LinearConstraint> constraints = new ArrayList<>();

        constraints.add(new LinearConstraint(new double[] {0.5, -5.5, -2.5, 9}, Relationship.LEQ, 0));
        constraints.add(new LinearConstraint(new double[] {0.5, -1.5, -0.5, 1}, Relationship.LEQ, 0));
        constraints.add(new LinearConstraint(new double[] {  1,    0,    0, 0}, Relationship.LEQ, 1));

        double epsilon = 1e-6;
        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(f, new LinearConstraintSet(constraints),
                                                  GoalType.MAXIMIZE,
                                                  new NonNegativeConstraint(true),
                                                  PivotSelectionRule.BLAND);
        Assert.assertEquals(1.0d, solution.getValue(), epsilon);
        Assert.assertTrue(validSolution(solution, constraints, epsilon));
    }

    @Test
    public void testMath828() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(
                new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, 0.0);

        ArrayList<LinearConstraint> constraints = new ArrayList<>();

        constraints.add(new LinearConstraint(new double[] {0.0, 39.0, 23.0, 96.0, 15.0, 48.0, 9.0, 21.0, 48.0, 36.0, 76.0, 19.0, 88.0, 17.0, 16.0, 36.0,}, Relationship.GEQ, 15.0));
        constraints.add(new LinearConstraint(new double[] {0.0, 59.0, 93.0, 12.0, 29.0, 78.0, 73.0, 87.0, 32.0, 70.0, 68.0, 24.0, 11.0, 26.0, 65.0, 25.0,}, Relationship.GEQ, 29.0));
        constraints.add(new LinearConstraint(new double[] {0.0, 74.0, 5.0, 82.0, 6.0, 97.0, 55.0, 44.0, 52.0, 54.0, 5.0, 93.0, 91.0, 8.0, 20.0, 97.0,}, Relationship.GEQ, 6.0));
        constraints.add(new LinearConstraint(new double[] {8.0, -3.0, -28.0, -72.0, -8.0, -31.0, -31.0, -74.0, -47.0, -59.0, -24.0, -57.0, -56.0, -16.0, -92.0, -59.0,}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {25.0, -7.0, -99.0, -78.0, -25.0, -14.0, -16.0, -89.0, -39.0, -56.0, -53.0, -9.0, -18.0, -26.0, -11.0, -61.0,}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {33.0, -95.0, -15.0, -4.0, -33.0, -3.0, -20.0, -96.0, -27.0, -13.0, -80.0, -24.0, -3.0, -13.0, -57.0, -76.0,}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {7.0, -95.0, -39.0, -93.0, -7.0, -94.0, -94.0, -62.0, -76.0, -26.0, -53.0, -57.0, -31.0, -76.0, -53.0, -52.0,}, Relationship.GEQ, 0.0));

        double epsilon = 1e-6;
        PointValuePair solution = new SimplexSolver().optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                               GoalType.MINIMIZE, new NonNegativeConstraint(true));
        Assert.assertEquals(1.0d, solution.getValue(), epsilon);
        Assert.assertTrue(validSolution(solution, constraints, epsilon));
    }

    @Test
    public void testMath828Cycle() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(
                new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, 0.0);

        ArrayList<LinearConstraint> constraints = new ArrayList<>();

        constraints.add(new LinearConstraint(new double[] {0.0, 16.0, 14.0, 69.0, 1.0, 85.0, 52.0, 43.0, 64.0, 97.0, 14.0, 74.0, 89.0, 28.0, 94.0, 58.0, 13.0, 22.0, 21.0, 17.0, 30.0, 25.0, 1.0, 59.0, 91.0, 78.0, 12.0, 74.0, 56.0, 3.0, 88.0,}, Relationship.GEQ, 91.0));
        constraints.add(new LinearConstraint(new double[] {0.0, 60.0, 40.0, 81.0, 71.0, 72.0, 46.0, 45.0, 38.0, 48.0, 40.0, 17.0, 33.0, 85.0, 64.0, 32.0, 84.0, 3.0, 54.0, 44.0, 71.0, 67.0, 90.0, 95.0, 54.0, 99.0, 99.0, 29.0, 52.0, 98.0, 9.0,}, Relationship.GEQ, 54.0));
        constraints.add(new LinearConstraint(new double[] {0.0, 41.0, 12.0, 86.0, 90.0, 61.0, 31.0, 41.0, 23.0, 89.0, 17.0, 74.0, 44.0, 27.0, 16.0, 47.0, 80.0, 32.0, 11.0, 56.0, 68.0, 82.0, 11.0, 62.0, 62.0, 53.0, 39.0, 16.0, 48.0, 1.0, 63.0,}, Relationship.GEQ, 62.0));
        constraints.add(new LinearConstraint(new double[] {83.0, -76.0, -94.0, -19.0, -15.0, -70.0, -72.0, -57.0, -63.0, -65.0, -22.0, -94.0, -22.0, -88.0, -86.0, -89.0, -72.0, -16.0, -80.0, -49.0, -70.0, -93.0, -95.0, -17.0, -83.0, -97.0, -31.0, -47.0, -31.0, -13.0, -23.0,}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {41.0, -96.0, -41.0, -48.0, -70.0, -43.0, -43.0, -43.0, -97.0, -37.0, -85.0, -70.0, -45.0, -67.0, -87.0, -69.0, -94.0, -54.0, -54.0, -92.0, -79.0, -10.0, -35.0, -20.0, -41.0, -41.0, -65.0, -25.0, -12.0, -8.0, -46.0,}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {27.0, -42.0, -65.0, -49.0, -53.0, -42.0, -17.0, -2.0, -61.0, -31.0, -76.0, -47.0, -8.0, -93.0, -86.0, -62.0, -65.0, -63.0, -22.0, -43.0, -27.0, -23.0, -32.0, -74.0, -27.0, -63.0, -47.0, -78.0, -29.0, -95.0, -73.0,}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {15.0, -46.0, -41.0, -83.0, -98.0, -99.0, -21.0, -35.0, -7.0, -14.0, -80.0, -63.0, -18.0, -42.0, -5.0, -34.0, -56.0, -70.0, -16.0, -18.0, -74.0, -61.0, -47.0, -41.0, -15.0, -79.0, -18.0, -47.0, -88.0, -68.0, -55.0,}, Relationship.GEQ, 0.0));

        double epsilon = 1e-6;
        PointValuePair solution = new SimplexSolver().optimize(DEFAULT_MAX_ITER, f,
                                                               new LinearConstraintSet(constraints),
                                                               GoalType.MINIMIZE, new NonNegativeConstraint(true),
                                                               PivotSelectionRule.BLAND);
        Assert.assertEquals(1.0d, solution.getValue(), epsilon);
        Assert.assertTrue(validSolution(solution, constraints, epsilon));
    }

    @Test
    public void testMath781() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 2, 6, 7 }, 0);

        ArrayList<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1, 2, 1 }, Relationship.LEQ, 2));
        constraints.add(new LinearConstraint(new double[] { -1, 1, 1 }, Relationship.LEQ, -1));
        constraints.add(new LinearConstraint(new double[] { 2, -3, 1 }, Relationship.LEQ, -1));

        double epsilon = 1e-6;
        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MAXIMIZE, new NonNegativeConstraint(false));

        Assert.assertTrue(Precision.compareTo(solution.getPoint()[0], 0.0d, epsilon) > 0);
        Assert.assertTrue(Precision.compareTo(solution.getPoint()[1], 0.0d, epsilon) > 0);
        Assert.assertTrue(Precision.compareTo(solution.getPoint()[2], 0.0d, epsilon) < 0);
        Assert.assertEquals(2.0d, solution.getValue(), epsilon);
    }

    @Test
    public void testMath713NegativeVariable() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {1.0, 1.0}, 0.0d);
        ArrayList<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] {1, 0}, Relationship.EQ, 1));

        double epsilon = 1e-6;
        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MINIMIZE, new NonNegativeConstraint(true));

        Assert.assertTrue(Precision.compareTo(solution.getPoint()[0], 0.0d, epsilon) >= 0);
        Assert.assertTrue(Precision.compareTo(solution.getPoint()[1], 0.0d, epsilon) >= 0);
    }

    @Test
    public void testMath434NegativeVariable() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {0.0, 0.0, 1.0}, 0.0d);
        ArrayList<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] {1, 1, 0}, Relationship.EQ, 5));
        constraints.add(new LinearConstraint(new double[] {0, 0, 1}, Relationship.GEQ, -10));

        double epsilon = 1e-6;
        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MINIMIZE, new NonNegativeConstraint(false));

        Assert.assertEquals(5.0, solution.getPoint()[0] + solution.getPoint()[1], epsilon);
        Assert.assertEquals(-10.0, solution.getPoint()[2], epsilon);
        Assert.assertEquals(-10.0, solution.getValue(), epsilon);
    }

    @Test(expected = NoFeasibleSolutionException.class)
    public void testMath434UnfeasibleSolution() {
        double epsilon = 1e-6;

        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {1.0, 0.0}, 0.0);
        ArrayList<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] {epsilon/2, 0.5}, Relationship.EQ, 0));
        constraints.add(new LinearConstraint(new double[] {1e-3, 0.1}, Relationship.EQ, 10));

        SimplexSolver solver = new SimplexSolver();
        // allowing only non-negative values, no feasible solution shall be found
        solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                        GoalType.MINIMIZE, new NonNegativeConstraint(true));
    }

    @Test
    public void testMath434PivotRowSelection() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {1.0}, 0.0);

        double epsilon = 1e-6;
        ArrayList<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] {200}, Relationship.GEQ, 1));
        constraints.add(new LinearConstraint(new double[] {100}, Relationship.GEQ, 0.499900001));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MINIMIZE, new NonNegativeConstraint(false));

        Assert.assertTrue(Precision.compareTo(solution.getPoint()[0] * 200.d, 1.d, epsilon) >= 0);
        Assert.assertEquals(0.0050, solution.getValue(), epsilon);
    }

    @Test
    public void testMath434PivotRowSelection2() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {0.0d, 1.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d}, 0.0d);

        ArrayList<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] {1.0d, -0.1d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, Relationship.EQ, -0.1d));
        constraints.add(new LinearConstraint(new double[] {1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, Relationship.GEQ, -1e-18d));
        constraints.add(new LinearConstraint(new double[] {0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, Relationship.GEQ, 0.0d));
        constraints.add(new LinearConstraint(new double[] {0.0d, 0.0d, 0.0d, 1.0d, 0.0d, -0.0128588d, 1e-5d}, Relationship.EQ, 0.0d));
        constraints.add(new LinearConstraint(new double[] {0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 1e-5d, -0.0128586d}, Relationship.EQ, 1e-10d));
        constraints.add(new LinearConstraint(new double[] {0.0d, 0.0d, 1.0d, -1.0d, 0.0d, 0.0d, 0.0d}, Relationship.GEQ, 0.0d));
        constraints.add(new LinearConstraint(new double[] {0.0d, 0.0d, 1.0d, 1.0d, 0.0d, 0.0d, 0.0d}, Relationship.GEQ, 0.0d));
        constraints.add(new LinearConstraint(new double[] {0.0d, 0.0d, 1.0d, 0.0d, -1.0d, 0.0d, 0.0d}, Relationship.GEQ, 0.0d));
        constraints.add(new LinearConstraint(new double[] {0.0d, 0.0d, 1.0d, 0.0d, 1.0d, 0.0d, 0.0d}, Relationship.GEQ, 0.0d));

        double epsilon = 1e-7;
        SimplexSolver simplex = new SimplexSolver();
        PointValuePair solution = simplex.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                   GoalType.MINIMIZE, new NonNegativeConstraint(false));

        Assert.assertTrue(Precision.compareTo(solution.getPoint()[0], -1e-18d, epsilon) >= 0);
        Assert.assertEquals(1.0d, solution.getPoint()[1], epsilon);
        Assert.assertEquals(0.0d, solution.getPoint()[2], epsilon);
        Assert.assertEquals(1.0d, solution.getValue(), epsilon);
    }

    @Test
    public void testMath272() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 2, 2, 1 }, 0);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1, 1, 0 }, Relationship.GEQ,  1));
        constraints.add(new LinearConstraint(new double[] { 1, 0, 1 }, Relationship.GEQ,  1));
        constraints.add(new LinearConstraint(new double[] { 0, 1, 0 }, Relationship.GEQ,  1));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MINIMIZE, new NonNegativeConstraint(true));

        Assert.assertEquals(0.0, solution.getPoint()[0], .0000001);
        Assert.assertEquals(1.0, solution.getPoint()[1], .0000001);
        Assert.assertEquals(1.0, solution.getPoint()[2], .0000001);
        Assert.assertEquals(3.0, solution.getValue(), .0000001);
    }

    @Test
    public void testMath286() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 0.8, 0.2, 0.7, 0.3, 0.6, 0.4 }, 0 );
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1, 0, 1, 0, 1, 0 }, Relationship.EQ, 23.0));
        constraints.add(new LinearConstraint(new double[] { 0, 1, 0, 1, 0, 1 }, Relationship.EQ, 23.0));
        constraints.add(new LinearConstraint(new double[] { 1, 0, 0, 0, 0, 0 }, Relationship.GEQ, 10.0));
        constraints.add(new LinearConstraint(new double[] { 0, 0, 1, 0, 0, 0 }, Relationship.GEQ, 8.0));
        constraints.add(new LinearConstraint(new double[] { 0, 0, 0, 0, 1, 0 }, Relationship.GEQ, 5.0));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MAXIMIZE, new NonNegativeConstraint(true));

        Assert.assertEquals(25.8, solution.getValue(), .0000001);
        Assert.assertEquals(23.0, solution.getPoint()[0] + solution.getPoint()[2] + solution.getPoint()[4], 0.0000001);
        Assert.assertEquals(23.0, solution.getPoint()[1] + solution.getPoint()[3] + solution.getPoint()[5], 0.0000001);
        Assert.assertTrue(solution.getPoint()[0] >= 10.0 - 0.0000001);
        Assert.assertTrue(solution.getPoint()[2] >= 8.0 - 0.0000001);
        Assert.assertTrue(solution.getPoint()[4] >= 5.0 - 0.0000001);
    }

    @Test
    public void testDegeneracy() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 0.8, 0.7 }, 0 );
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1, 1 }, Relationship.LEQ, 18.0));
        constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.GEQ, 10.0));
        constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.GEQ, 8.0));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MINIMIZE, new NonNegativeConstraint(true));
        Assert.assertEquals(13.6, solution.getValue(), .0000001);
    }

    @Test
    public void testMath288() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 7, 3, 0, 0 }, 0 );
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 3, 0, -5, 0 }, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] { 2, 0, 0, -5 }, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] { 0, 3, 0, -5 }, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] { 1, 0, 0, 0 }, Relationship.LEQ, 1.0));
        constraints.add(new LinearConstraint(new double[] { 0, 1, 0, 0 }, Relationship.LEQ, 1.0));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MAXIMIZE, new NonNegativeConstraint(true));
        Assert.assertEquals(10.0, solution.getValue(), .0000001);
    }

    @Test
    public void testMath290GEQ() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1, 5 }, 0 );
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 2, 0 }, Relationship.GEQ, -1.0));
        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MINIMIZE, new NonNegativeConstraint(true));
        Assert.assertEquals(0, solution.getValue(), .0000001);
        Assert.assertEquals(0, solution.getPoint()[0], .0000001);
        Assert.assertEquals(0, solution.getPoint()[1], .0000001);
    }

    @Test(expected=NoFeasibleSolutionException.class)
    public void testMath290LEQ() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1, 5 }, 0 );
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 2, 0 }, Relationship.LEQ, -1.0));
        SimplexSolver solver = new SimplexSolver();
        solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                        GoalType.MINIMIZE, new NonNegativeConstraint(true));
    }

    @Test
    public void testMath293() {
      LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 0.8, 0.2, 0.7, 0.3, 0.4, 0.6}, 0 );
      Collection<LinearConstraint> constraints = new ArrayList<>();
      constraints.add(new LinearConstraint(new double[] { 1, 0, 1, 0, 1, 0 }, Relationship.EQ, 30.0));
      constraints.add(new LinearConstraint(new double[] { 0, 1, 0, 1, 0, 1 }, Relationship.EQ, 30.0));
      constraints.add(new LinearConstraint(new double[] { 0.8, 0.2, 0.0, 0.0, 0.0, 0.0 }, Relationship.GEQ, 10.0));
      constraints.add(new LinearConstraint(new double[] { 0.0, 0.0, 0.7, 0.3, 0.0, 0.0 }, Relationship.GEQ, 10.0));
      constraints.add(new LinearConstraint(new double[] { 0.0, 0.0, 0.0, 0.0, 0.4, 0.6 }, Relationship.GEQ, 10.0));

      SimplexSolver solver = new SimplexSolver();
      PointValuePair solution1 = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                 GoalType.MAXIMIZE, new NonNegativeConstraint(true));

      Assert.assertEquals(15.7143, solution1.getPoint()[0], .0001);
      Assert.assertEquals(0.0, solution1.getPoint()[1], .0001);
      Assert.assertEquals(14.2857, solution1.getPoint()[2], .0001);
      Assert.assertEquals(0.0, solution1.getPoint()[3], .0001);
      Assert.assertEquals(0.0, solution1.getPoint()[4], .0001);
      Assert.assertEquals(30.0, solution1.getPoint()[5], .0001);
      Assert.assertEquals(40.57143, solution1.getValue(), .0001);

      double valA = 0.8 * solution1.getPoint()[0] + 0.2 * solution1.getPoint()[1];
      double valB = 0.7 * solution1.getPoint()[2] + 0.3 * solution1.getPoint()[3];
      double valC = 0.4 * solution1.getPoint()[4] + 0.6 * solution1.getPoint()[5];

      f = new LinearObjectiveFunction(new double[] { 0.8, 0.2, 0.7, 0.3, 0.4, 0.6}, 0 );
      constraints = new ArrayList<>();
      constraints.add(new LinearConstraint(new double[] { 1, 0, 1, 0, 1, 0 }, Relationship.EQ, 30.0));
      constraints.add(new LinearConstraint(new double[] { 0, 1, 0, 1, 0, 1 }, Relationship.EQ, 30.0));
      constraints.add(new LinearConstraint(new double[] { 0.8, 0.2, 0.0, 0.0, 0.0, 0.0 }, Relationship.GEQ, valA));
      constraints.add(new LinearConstraint(new double[] { 0.0, 0.0, 0.7, 0.3, 0.0, 0.0 }, Relationship.GEQ, valB));
      constraints.add(new LinearConstraint(new double[] { 0.0, 0.0, 0.0, 0.0, 0.4, 0.6 }, Relationship.GEQ, valC));

      PointValuePair solution2 = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                 GoalType.MAXIMIZE, new NonNegativeConstraint(true));
      Assert.assertEquals(40.57143, solution2.getValue(), .0001);
    }

    @Test
    public void testMath930() {
        Collection<LinearConstraint> constraints = createMath930Constraints();

        double[] objFunctionCoeff = new double[33];
        objFunctionCoeff[3] = 1;
        LinearObjectiveFunction f = new LinearObjectiveFunction(objFunctionCoeff, 0);
        SimplexSolver solver = new SimplexSolver(1e-4, 10, 1e-6);

        PointValuePair solution = solver.optimize(new MaxIter(1000), f, new LinearConstraintSet(constraints),
                                                  GoalType.MINIMIZE, new NonNegativeConstraint(true));
        Assert.assertEquals(0.3752298, solution.getValue(), 1e-4);
    }

    private List<LinearConstraint> createMath930Constraints() {
        List<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] {1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1, -1, -1, 1, -1, 1, 1, -1, 1, -1, -1, 1, 1, -1, -1, 1, -1, 1, 1, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 1, 0, -1, 0, -1, 0, 1, 0, -1, 0, 1, 0, 1, 0, -1, 0, -1, 0, 1, 0, 1, 0, -1, 0, 1, 0, -1, 0, -1, 0, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.628803}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.676993}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 1, -1, 0, 0, -1, 1, 0, 0, -1, 1, 0, 0, 1, -1, 0, 0, -1, 1, 0, 0, 1, -1, 0, 0, 1, -1, 0, 0, -1, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.136677}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.444434}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.254028}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.302218}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 1, -1, -1, 1, 0, 0, 0, 0, -1, 1, 1, -1, 0, 0, 0, 0, -1, 1, 1, -1, 0, 0, 0, 0, 1, -1, -1, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.653981}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.690437}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, -1, 0, 1, 0, 0, 0, 0, 0, -1, 0, 1, 0, 0, 0, 0, 0, 1, 0, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.423786}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.486717}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, 0, -1, 1, 0, 0, 0, 0, 0, 0, -1, 1, 0, 0, 0, 0, 0, 0, 1, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.049232}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.304747}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.129826}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.205625}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, 1, -1, 1, 1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, -1, 1, -1, -1, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.621944}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.764385}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 1, 0, 1, 0, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.432572}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.480762}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, -1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 0, 0, 1, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.055983}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.11378}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.009607}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.057797}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.407308}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.452749}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.269677}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.321806}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.049232}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.06902}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.028754}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.484254}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.524607}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, -1, 0, 1, 0, -1, 0, 1, 0, 1, 0, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.385492}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.430134}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, -1, 1, 0, 0, -1, 1, 0, 0, 1, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.34983}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.375781}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.254028}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.281308}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, 1, 0, 0, 0, 0, -1, 1, 1, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.304995}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.345347}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, -1, 0, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.288899}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.332212}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, 0, -1, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.14351}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.17057}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, -0.129826}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, -0.157435}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, 1, -1, 1, 1, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, -0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, -1}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, -1, 0, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, -0.141071}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, -0.232574}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, -1, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, -0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, -1}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, -0.009607}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, -0.057797}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, -0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, -1}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, -0.091644}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, -0.203531}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, Relationship.GEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -0.028754}, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, Relationship.EQ, 1.0));
        return constraints;
    }

    @Test
    public void testSimplexSolver() {
        LinearObjectiveFunction f =
            new LinearObjectiveFunction(new double[] { 15, 10 }, 7);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.LEQ, 2));
        constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.LEQ, 3));
        constraints.add(new LinearConstraint(new double[] { 1, 1 }, Relationship.EQ, 4));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MAXIMIZE, new NonNegativeConstraint(true));
        Assert.assertEquals(2.0, solution.getPoint()[0], 0.0);
        Assert.assertEquals(2.0, solution.getPoint()[1], 0.0);
        Assert.assertEquals(57.0, solution.getValue(), 0.0);
    }

    @Test
    public void testSingleVariableAndConstraint() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 3 }, 0);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.LEQ, 10));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MAXIMIZE, new NonNegativeConstraint(false));
        Assert.assertEquals(10.0, solution.getPoint()[0], 0.0);
        Assert.assertEquals(30.0, solution.getValue(), 0.0);
    }

    /**
     * With no artificial variables needed (no equals and no greater than
     * constraints) we can go straight to Phase 2.
     */
    @Test
    public void testModelWithNoArtificialVars() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 15, 10 }, 0);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.LEQ, 2));
        constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.LEQ, 3));
        constraints.add(new LinearConstraint(new double[] { 1, 1 }, Relationship.LEQ, 4));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MAXIMIZE, new NonNegativeConstraint(false));
        Assert.assertEquals(2.0, solution.getPoint()[0], 0.0);
        Assert.assertEquals(2.0, solution.getPoint()[1], 0.0);
        Assert.assertEquals(50.0, solution.getValue(), 0.0);
    }

    @Test
    public void testMinimization() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { -2, 1 }, -5);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1, 2 }, Relationship.LEQ, 6));
        constraints.add(new LinearConstraint(new double[] { 3, 2 }, Relationship.LEQ, 12));
        constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.GEQ, 0));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MINIMIZE, new NonNegativeConstraint(false));
        Assert.assertEquals(4.0, solution.getPoint()[0], 0.0);
        Assert.assertEquals(0.0, solution.getPoint()[1], 0.0);
        Assert.assertEquals(-13.0, solution.getValue(), 0.0);
    }

    @Test
    public void testSolutionWithNegativeDecisionVariable() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { -2, 1 }, 0);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1, 1 }, Relationship.GEQ, 6));
        constraints.add(new LinearConstraint(new double[] { 1, 2 }, Relationship.LEQ, 14));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MAXIMIZE, new NonNegativeConstraint(false));
        Assert.assertEquals(-2.0, solution.getPoint()[0], 0.0);
        Assert.assertEquals(8.0, solution.getPoint()[1], 0.0);
        Assert.assertEquals(12.0, solution.getValue(), 0.0);
    }

    @Test(expected = NoFeasibleSolutionException.class)
    public void testInfeasibleSolution() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 15 }, 0);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.LEQ, 1));
        constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.GEQ, 3));

        SimplexSolver solver = new SimplexSolver();
        solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                        GoalType.MAXIMIZE, new NonNegativeConstraint(false));
    }

    @Test(expected = UnboundedSolutionException.class)
    public void testUnboundedSolution() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 15, 10 }, 0);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.EQ, 2));

        SimplexSolver solver = new SimplexSolver();
        solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                        GoalType.MAXIMIZE, new NonNegativeConstraint(false));
    }

    @Test
    public void testRestrictVariablesToNonNegative() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 409, 523, 70, 204, 339 }, 0);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] {    43,   56, 345,  56,    5 }, Relationship.LEQ,  4567456));
        constraints.add(new LinearConstraint(new double[] {    12,   45,   7,  56,   23 }, Relationship.LEQ,    56454));
        constraints.add(new LinearConstraint(new double[] {     8,  768,   0,  34, 7456 }, Relationship.LEQ,  1923421));
        constraints.add(new LinearConstraint(new double[] { 12342, 2342,  34, 678, 2342 }, Relationship.GEQ,     4356));
        constraints.add(new LinearConstraint(new double[] {    45,  678,  76,  52,   23 }, Relationship.EQ,    456356));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MAXIMIZE, new NonNegativeConstraint(true));
        Assert.assertEquals(2902.92783505155, solution.getPoint()[0], .0000001);
        Assert.assertEquals(480.419243986254, solution.getPoint()[1], .0000001);
        Assert.assertEquals(0.0, solution.getPoint()[2], .0000001);
        Assert.assertEquals(0.0, solution.getPoint()[3], .0000001);
        Assert.assertEquals(0.0, solution.getPoint()[4], .0000001);
        Assert.assertEquals(1438556.7491409, solution.getValue(), .0000001);
    }

    @Test
    public void testEpsilon() {
      LinearObjectiveFunction f =
          new LinearObjectiveFunction(new double[] { 10, 5, 1 }, 0);
      Collection<LinearConstraint> constraints = new ArrayList<>();
      constraints.add(new LinearConstraint(new double[] {  9, 8, 0 }, Relationship.EQ,  17));
      constraints.add(new LinearConstraint(new double[] {  0, 7, 8 }, Relationship.LEQ,  7));
      constraints.add(new LinearConstraint(new double[] { 10, 0, 2 }, Relationship.LEQ, 10));

      SimplexSolver solver = new SimplexSolver();
      PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                GoalType.MAXIMIZE, new NonNegativeConstraint(false));
      Assert.assertEquals(1.0, solution.getPoint()[0], 0.0);
      Assert.assertEquals(1.0, solution.getPoint()[1], 0.0);
      Assert.assertEquals(0.0, solution.getPoint()[2], 0.0);
      Assert.assertEquals(15.0, solution.getValue(), 0.0);
  }

    @Test
    public void testTrivialModel() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1, 1 }, 0);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 1, 1 }, Relationship.EQ,  0));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MAXIMIZE, new NonNegativeConstraint(true));
        Assert.assertEquals(0, solution.getValue(), .0000001);
    }

    @Test
    public void testLargeModel() {
        double[] objective = new double[] {
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 12, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           12, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 12, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 12, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 12, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 12, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                           1, 1, 1, 1, 1, 1};

        LinearObjectiveFunction f = new LinearObjectiveFunction(objective, 0);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(equationFromString(objective.length, "x0 + x1 + x2 + x3 - x12 = 0"));
        constraints.add(equationFromString(objective.length, "x4 + x5 + x6 + x7 + x8 + x9 + x10 + x11 - x13 = 0"));
        constraints.add(equationFromString(objective.length, "x4 + x5 + x6 + x7 + x8 + x9 + x10 + x11 >= 49"));
        constraints.add(equationFromString(objective.length, "x0 + x1 + x2 + x3 >= 42"));
        constraints.add(equationFromString(objective.length, "x14 + x15 + x16 + x17 - x26 = 0"));
        constraints.add(equationFromString(objective.length, "x18 + x19 + x20 + x21 + x22 + x23 + x24 + x25 - x27 = 0"));
        constraints.add(equationFromString(objective.length, "x14 + x15 + x16 + x17 - x12 = 0"));
        constraints.add(equationFromString(objective.length, "x18 + x19 + x20 + x21 + x22 + x23 + x24 + x25 - x13 = 0"));
        constraints.add(equationFromString(objective.length, "x28 + x29 + x30 + x31 - x40 = 0"));
        constraints.add(equationFromString(objective.length, "x32 + x33 + x34 + x35 + x36 + x37 + x38 + x39 - x41 = 0"));
        constraints.add(equationFromString(objective.length, "x32 + x33 + x34 + x35 + x36 + x37 + x38 + x39 >= 49"));
        constraints.add(equationFromString(objective.length, "x28 + x29 + x30 + x31 >= 42"));
        constraints.add(equationFromString(objective.length, "x42 + x43 + x44 + x45 - x54 = 0"));
        constraints.add(equationFromString(objective.length, "x46 + x47 + x48 + x49 + x50 + x51 + x52 + x53 - x55 = 0"));
        constraints.add(equationFromString(objective.length, "x42 + x43 + x44 + x45 - x40 = 0"));
        constraints.add(equationFromString(objective.length, "x46 + x47 + x48 + x49 + x50 + x51 + x52 + x53 - x41 = 0"));
        constraints.add(equationFromString(objective.length, "x56 + x57 + x58 + x59 - x68 = 0"));
        constraints.add(equationFromString(objective.length, "x60 + x61 + x62 + x63 + x64 + x65 + x66 + x67 - x69 = 0"));
        constraints.add(equationFromString(objective.length, "x60 + x61 + x62 + x63 + x64 + x65 + x66 + x67 >= 51"));
        constraints.add(equationFromString(objective.length, "x56 + x57 + x58 + x59 >= 44"));
        constraints.add(equationFromString(objective.length, "x70 + x71 + x72 + x73 - x82 = 0"));
        constraints.add(equationFromString(objective.length, "x74 + x75 + x76 + x77 + x78 + x79 + x80 + x81 - x83 = 0"));
        constraints.add(equationFromString(objective.length, "x70 + x71 + x72 + x73 - x68 = 0"));
        constraints.add(equationFromString(objective.length, "x74 + x75 + x76 + x77 + x78 + x79 + x80 + x81 - x69 = 0"));
        constraints.add(equationFromString(objective.length, "x84 + x85 + x86 + x87 - x96 = 0"));
        constraints.add(equationFromString(objective.length, "x88 + x89 + x90 + x91 + x92 + x93 + x94 + x95 - x97 = 0"));
        constraints.add(equationFromString(objective.length, "x88 + x89 + x90 + x91 + x92 + x93 + x94 + x95 >= 51"));
        constraints.add(equationFromString(objective.length, "x84 + x85 + x86 + x87 >= 44"));
        constraints.add(equationFromString(objective.length, "x98 + x99 + x100 + x101 - x110 = 0"));
        constraints.add(equationFromString(objective.length, "x102 + x103 + x104 + x105 + x106 + x107 + x108 + x109 - x111 = 0"));
        constraints.add(equationFromString(objective.length, "x98 + x99 + x100 + x101 - x96 = 0"));
        constraints.add(equationFromString(objective.length, "x102 + x103 + x104 + x105 + x106 + x107 + x108 + x109 - x97 = 0"));
        constraints.add(equationFromString(objective.length, "x112 + x113 + x114 + x115 - x124 = 0"));
        constraints.add(equationFromString(objective.length, "x116 + x117 + x118 + x119 + x120 + x121 + x122 + x123 - x125 = 0"));
        constraints.add(equationFromString(objective.length, "x116 + x117 + x118 + x119 + x120 + x121 + x122 + x123 >= 49"));
        constraints.add(equationFromString(objective.length, "x112 + x113 + x114 + x115 >= 42"));
        constraints.add(equationFromString(objective.length, "x126 + x127 + x128 + x129 - x138 = 0"));
        constraints.add(equationFromString(objective.length, "x130 + x131 + x132 + x133 + x134 + x135 + x136 + x137 - x139 = 0"));
        constraints.add(equationFromString(objective.length, "x126 + x127 + x128 + x129 - x124 = 0"));
        constraints.add(equationFromString(objective.length, "x130 + x131 + x132 + x133 + x134 + x135 + x136 + x137 - x125 = 0"));
        constraints.add(equationFromString(objective.length, "x140 + x141 + x142 + x143 - x152 = 0"));
        constraints.add(equationFromString(objective.length, "x144 + x145 + x146 + x147 + x148 + x149 + x150 + x151 - x153 = 0"));
        constraints.add(equationFromString(objective.length, "x144 + x145 + x146 + x147 + x148 + x149 + x150 + x151 >= 59"));
        constraints.add(equationFromString(objective.length, "x140 + x141 + x142 + x143 >= 42"));
        constraints.add(equationFromString(objective.length, "x154 + x155 + x156 + x157 - x166 = 0"));
        constraints.add(equationFromString(objective.length, "x158 + x159 + x160 + x161 + x162 + x163 + x164 + x165 - x167 = 0"));
        constraints.add(equationFromString(objective.length, "x154 + x155 + x156 + x157 - x152 = 0"));
        constraints.add(equationFromString(objective.length, "x158 + x159 + x160 + x161 + x162 + x163 + x164 + x165 - x153 = 0"));
        constraints.add(equationFromString(objective.length, "x83 + x82 - x168 = 0"));
        constraints.add(equationFromString(objective.length, "x111 + x110 - x169 = 0"));
        constraints.add(equationFromString(objective.length, "x170 - x182 = 0"));
        constraints.add(equationFromString(objective.length, "x171 - x183 = 0"));
        constraints.add(equationFromString(objective.length, "x172 - x184 = 0"));
        constraints.add(equationFromString(objective.length, "x173 - x185 = 0"));
        constraints.add(equationFromString(objective.length, "x174 - x186 = 0"));
        constraints.add(equationFromString(objective.length, "x175 + x176 - x187 = 0"));
        constraints.add(equationFromString(objective.length, "x177 - x188 = 0"));
        constraints.add(equationFromString(objective.length, "x178 - x189 = 0"));
        constraints.add(equationFromString(objective.length, "x179 - x190 = 0"));
        constraints.add(equationFromString(objective.length, "x180 - x191 = 0"));
        constraints.add(equationFromString(objective.length, "x181 - x192 = 0"));
        constraints.add(equationFromString(objective.length, "x170 - x26 = 0"));
        constraints.add(equationFromString(objective.length, "x171 - x27 = 0"));
        constraints.add(equationFromString(objective.length, "x172 - x54 = 0"));
        constraints.add(equationFromString(objective.length, "x173 - x55 = 0"));
        constraints.add(equationFromString(objective.length, "x174 - x168 = 0"));
        constraints.add(equationFromString(objective.length, "x177 - x169 = 0"));
        constraints.add(equationFromString(objective.length, "x178 - x138 = 0"));
        constraints.add(equationFromString(objective.length, "x179 - x139 = 0"));
        constraints.add(equationFromString(objective.length, "x180 - x166 = 0"));
        constraints.add(equationFromString(objective.length, "x181 - x167 = 0"));
        constraints.add(equationFromString(objective.length, "x193 - x205 = 0"));
        constraints.add(equationFromString(objective.length, "x194 - x206 = 0"));
        constraints.add(equationFromString(objective.length, "x195 - x207 = 0"));
        constraints.add(equationFromString(objective.length, "x196 - x208 = 0"));
        constraints.add(equationFromString(objective.length, "x197 - x209 = 0"));
        constraints.add(equationFromString(objective.length, "x198 + x199 - x210 = 0"));
        constraints.add(equationFromString(objective.length, "x200 - x211 = 0"));
        constraints.add(equationFromString(objective.length, "x201 - x212 = 0"));
        constraints.add(equationFromString(objective.length, "x202 - x213 = 0"));
        constraints.add(equationFromString(objective.length, "x203 - x214 = 0"));
        constraints.add(equationFromString(objective.length, "x204 - x215 = 0"));
        constraints.add(equationFromString(objective.length, "x193 - x182 = 0"));
        constraints.add(equationFromString(objective.length, "x194 - x183 = 0"));
        constraints.add(equationFromString(objective.length, "x195 - x184 = 0"));
        constraints.add(equationFromString(objective.length, "x196 - x185 = 0"));
        constraints.add(equationFromString(objective.length, "x197 - x186 = 0"));
        constraints.add(equationFromString(objective.length, "x198 + x199 - x187 = 0"));
        constraints.add(equationFromString(objective.length, "x200 - x188 = 0"));
        constraints.add(equationFromString(objective.length, "x201 - x189 = 0"));
        constraints.add(equationFromString(objective.length, "x202 - x190 = 0"));
        constraints.add(equationFromString(objective.length, "x203 - x191 = 0"));
        constraints.add(equationFromString(objective.length, "x204 - x192 = 0"));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(constraints),
                                                  GoalType.MINIMIZE, new NonNegativeConstraint(true));
        Assert.assertEquals(7518.0, solution.getValue(), .0000001);
    }

    @Test
    public void testSolutionCallback() {
        // re-use the problem from testcase for MATH-288
        // it normally requires 5 iterations

        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 7, 3, 0, 0 }, 0 );

        List<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] { 3, 0, -5, 0 }, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] { 2, 0, 0, -5 }, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] { 0, 3, 0, -5 }, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] { 1, 0, 0, 0 }, Relationship.LEQ, 1.0));
        constraints.add(new LinearConstraint(new double[] { 0, 1, 0, 0 }, Relationship.LEQ, 1.0));

        final SimplexSolver solver = new SimplexSolver();
        final SolutionCallback callback = new SolutionCallback();

        Assert.assertNull(callback.getSolution());
        Assert.assertFalse(callback.isSolutionOptimal());

        try {
            solver.optimize(new MaxIter(4), f, new LinearConstraintSet(constraints),
                            GoalType.MAXIMIZE, new NonNegativeConstraint(true), callback);
            Assert.fail("expected TooManyIterationsException");
        } catch (TooManyIterationsException ex) {
            // expected
        }

        final PointValuePair solution = callback.getSolution();
        Assert.assertNotNull(solution);
        Assert.assertTrue(validSolution(solution, constraints, 1e-4));
        Assert.assertFalse(callback.isSolutionOptimal());
        // the solution is clearly not optimal: optimal = 10.0
        Assert.assertEquals(7.0, solution.getValue(), 1e-4);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testDimensionMatch() {
        // min 2x1 +15x2 +18x3
        // Subject to
        //   -x1 +2x2 -6x3  <=-10
        //         x2 +2x3  <= 6
        //   2x1      +10x3 <= 19
        //   -x1  +x2       <= -2
        // x1,x2,x3 >= 0

        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 2, 15, 18 }, 0);
        Collection<LinearConstraint> constraints = new ArrayList<>();
        // this constraint is wrong, the dimension is less than expected one
        constraints.add(new LinearConstraint(new double[] { -1, 2 - 6 }, Relationship.LEQ, -10));
        constraints.add(new LinearConstraint(new double[] { 0, 1, 2 }, Relationship.LEQ, 6));
        constraints.add(new LinearConstraint(new double[] { 2, 0, 10 }, Relationship.LEQ, 19));
        constraints.add(new LinearConstraint(new double[] { -1, 1, 0 }, Relationship.LEQ, -2));

        SimplexSolver solver = new SimplexSolver();
        solver.optimize(f,
                        new LinearConstraintSet(constraints),
                        new NonNegativeConstraint(true),
                        PivotSelectionRule.BLAND);
    }

    /* linear transformation of constants should produce the same result */
    @Test
    public void testMath1549() {
        final double m = 10;
        double scale = 1e-12;
        for (int pow = 0; pow < 26; pow++) {
            tryMath1549(scale);
            scale *= m;
        }
    }

    /* See JIRA issue: MATH-1549 */
    private void tryMath1549(double scale) {
        final NonNegativeConstraint nnegconstr = new NonNegativeConstraint(true);
        final int ulps = 10;
        final double cutoff = 1e-10;
        final double eps = 1e-6;
        final SimplexSolver solver = new SimplexSolver(eps, ulps, cutoff);

        final LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {1, 1}, 0);
        final List<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[] {scale * 9000, scale * 1}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[] {scale * 10000, scale}, Relationship.GEQ, scale * 2000));
        constraints.add(new LinearConstraint(new double[] {scale, 0}, Relationship.GEQ, scale * 2));
        final LinearConstraintSet constraintSet = new LinearConstraintSet(constraints);
        final PointValuePair solution = solver.optimize(f, constraintSet, GoalType.MINIMIZE, nnegconstr);

        Assert.assertEquals(2.0, solution.getPoint()[0], eps);
    }

    /**
     * Converts a test string to a {@link LinearConstraint}.
     * Ex: x0 + x1 + x2 + x3 - x12 = 0
     */
    private LinearConstraint equationFromString(int numCoefficients, String s) {
        Relationship relationship;
        if (s.contains(">=")) {
            relationship = Relationship.GEQ;
        } else if (s.contains("<=")) {
            relationship = Relationship.LEQ;
        } else if (s.contains("=")) {
            relationship = Relationship.EQ;
        } else {
            throw new IllegalArgumentException();
        }

        String[] equationParts = s.split("[>|<]?=");
        double rhs = Double.parseDouble(equationParts[1].trim());

        double[] lhs = new double[numCoefficients];
        String left = equationParts[0].replaceAll(" ?x", "");
        String[] coefficients = left.split(" ");
        for (String coefficient : coefficients) {
            double value = coefficient.charAt(0) == '-' ? -1 : 1;
            int index = Integer.parseInt(coefficient.replaceFirst("[+|-]", "").trim());
            lhs[index] = value;
        }
        return new LinearConstraint(lhs, relationship, rhs);
    }

    private static boolean validSolution(PointValuePair solution, List<LinearConstraint> constraints, double epsilon) {
        double[] vals = solution.getPoint();
        for (LinearConstraint c : constraints) {
            double[] coeffs = c.getCoefficients().toArray();
            double result = 0.0d;
            for (int i = 0; i < vals.length; i++) {
                result += vals[i] * coeffs[i];
            }

            switch (c.getRelationship()) {
            case EQ:
                if (!Precision.equals(result, c.getValue(), epsilon)) {
                    return false;
                }
                break;

            case GEQ:
                if (Precision.compareTo(result, c.getValue(), epsilon) < 0) {
                    return false;
                }
                break;

            case LEQ:
                if (Precision.compareTo(result, c.getValue(), epsilon) > 0) {
                    return false;
                }
                break;
            default:
                Assert.fail();
            }
        }

        return true;
    }

    /* See JIRA report: MATH-1674 */
    @Test
    public void testMath1674() throws Exception {
        final double[] c = new double[]{109.8416722130, 3995.2249004467, 4153.0918625648, 4205.9691060980, 6864.1761563995, 9985.9467230921, 6354.9344334526, 1460.7566224849, 9404.5575989194, 3074.9619692427, 6738.9813645788, 4251.4858953042, 3110.4876355988, 4790.6080441726, 8105.0878555759, 6994.9651570951, 6436.5681652562, 1938.6650475701, 1991.3366023235, 8807.6044972149, 1319.3678145792, 279.4114357595, 2406.8106667864, 721.4302264969, 8229.6476579561, 8081.4398910261, 1120.8874807145, 1027.1292710688, 9763.0697846171, 4736.8579838298, 1551.5944809986, 7117.2342065109, 7101.9720301735, 765.0876567377, 3266.2721870594, 9126.3837897194, 5682.7410411790, 737.5506525782, 640.9898129299, 6651.9205926265, 9967.7712540466, 9923.1032741511, 7320.8677028895};

        final double[] d = new double[]{2443.4617219283173, 1173.9746798742185, 4392.083363038196, 1390.8295640100537, 304.54498814002363, 3945.528655298191, 3423.0368504289595, 4256.823613052879, 821.3597860392124, 2451.720430980302, 4458.615770028524, 4566.059436861297, 2823.6879242564887, 543.3702798436418, 1077.6404845531451, 4032.1898198315507, 2692.8144489810506, 3224.220920651318, 1027.2527158190137, 4185.736937549958, 3991.8068663807326, 3532.2224429213907, 4536.16351791805, 3366.741013973095, 2560.9821638572507, 4146.908647998803, 2658.8999476871754, 1330.2326236467416, 4790.506804753733, 1055.1687831958889, 1246.2962844738324, 2793.4186611618766, 556.4873527843156, 421.19883191270304, 4052.515015289009, 4832.5688332462805, 612.5756476062232, 4401.140676006083, 3435.1692062499455, 4897.945863909211, 1066.0243804705144, 2018.8939865481348, 4082.6606399739544};

        final int nCoef = 43;
        final List<LinearConstraint> constraints = new ArrayList<LinearConstraint>() {
            {
                add(new LinearConstraint(new double[]{-18.4721096232, -346.7827878415, 190.4497509093, -330.1702770265, 448.8851105324, -324.7721331591, -41.4894142939, 398.1964809931, 463.1362097737, -381.3526533422, -474.7408449269, 462.4340720610, -466.1439468990, 415.9941329301, -229.7720934493, -480.0419990356, -359.8361731590, -229.0774350493, 382.3688199190, 235.7328265513, -169.4850416538, 41.1742463794, -136.9424841461, -274.6086332855, 72.8639834681, 360.1892515767, -442.3793941427, 13.3350611571, 29.9115510055, 107.8814689691, 208.9474270013, -154.6760902044, -451.5934646577, 338.1523090476, 219.1294614240, 395.2588576773, 171.9818507822, -75.9622774112, 403.7042049548, -237.6943206923, -86.2391865819, -9.0464848582, -472.0377200827}, Relationship.LEQ, 634.5590200110));
                add(new LinearConstraint(new double[]{-142.0218156776, -325.6755905771, 325.9103281986, 425.5525979901, -100.8818306524, 359.9526168657, 19.8640958157, 101.0496674864, 72.5794388944, 343.8017139721, -417.7203389850, -276.8717066617, 482.6350083618, 282.4425554939, 111.3131183011, -408.8982090277, -208.5374729647, -103.7724850339, -272.2678819564, -19.1039841475, -402.7216612885, 134.3300718162, 343.1853280760, 20.6322270365, -199.8831750852, 414.1073518711, 498.4687409141, 107.7719112424, 230.5540525606, -393.1872939758, 225.3104253726, 3.2303818495, 399.6822482084, 157.5066113010, -453.6713809154, -343.1893957902, 217.9003474227, 99.0839107510, -204.8000179978, -106.4953973306, -74.4073239237, -251.3439423383, 412.7087629172}, Relationship.LEQ, -1686.1586570052));
                add(new LinearConstraint(new double[]{280.5264359768, -440.9029839729, 254.2293245718, 211.6627419456, 254.8057819197, -60.6178062887, -269.6717804385, -362.1205618329, 108.4787074840, -145.5803917982, 294.5542177837, -123.4965247386, -272.5995474795, -331.6065661963, -108.8823485644, -91.2369235269, -460.2851164778, 5.2095309814, -19.9563858245, -108.6507009265, 158.3232789187, 365.4537468646, -331.4843842776, -374.9844223849, 135.9679065769, -138.6563987535, 294.5099191939, 377.4528531873, -134.9349750377, -290.5256579169, -467.5470043617, -102.0045781052, -293.8194115055, 55.7305830746, -279.4822824137, 99.4075758821, -112.9511972467, 297.5555386557, -354.6935133839, -324.0532488512, -421.7076524478, 315.8393412688, 462.0524323553}, Relationship.LEQ, -5193.7723437731));
                add(new LinearConstraint(new double[]{-177.4894743518, -67.9908085983, -222.8973447862, -237.2123374249, -329.8409313977, 465.4735911581, 174.3300783257, -471.3072404862, -41.3462072806, 482.3935826175, -371.7462653377, -91.0188575698, -339.6122496783, -301.1754391495, 314.0294251084, 239.2963702057, -379.1406195440, -470.4083935254, 298.1063991917, 494.0195860421, -190.1331973394, -41.1672631643, 473.2276495541, 499.6494985034, -83.2682883237, 248.9637912508, -112.4152220927, -429.9454453342, 234.7734871573, -498.1102069486, 310.3210383488, 9.5505753245, 300.7626040906, 239.6071719633, 474.2089429031, 286.2026187725, -318.1604356201, -19.0877010059, -58.7883505173, 190.4449939723, -364.6058903054, 256.7514767364, -52.2298154604}, Relationship.LEQ, -2219.7550377040));
                add(new LinearConstraint(new double[]{106.6565294021, -371.1818345738, -95.9586185145, 201.7415424771, -325.7385298650, 54.3236171225, 444.9441819132, 336.9488035905, -394.1983141613, 374.4370581817, 60.7546944337, -394.6167862437, 143.3976341525, -205.2879276325, 371.1235081032, -256.5487760721, -496.4337469161, -27.1136131757, 48.7711410535, 73.2036734543, -153.8197644177, -267.2379241177, 247.7506005761, 259.7210844013, -355.9332501729, 482.4437633829, 146.0490189775, -218.3994501603, 467.8026791824, 131.0330821552, -126.5675946346, 75.1457238246, 189.5209006435, -330.0528718635, -337.1785559590, 388.0570267836, 5.0502849263, -386.6503019637, -462.8819138229, -390.1352433018, 475.4694651953, 176.7542348854, 223.2860141151}, Relationship.LEQ, -34.3492566056));
                add(new LinearConstraint(new double[]{-2.8370631388, 413.6772809576, 271.6209399819, 150.6847226997, -276.2750537300, -472.7076574957, -449.4708229028, -331.1752705878, -16.4346414648, 227.3535295815, 385.7658282909, 23.9202552069, -336.1712375444, 312.2294103332, -382.0329537481, -361.2778235142, -135.7536208646, -378.2927678121, -236.5567268759, -425.3073778324, 363.7978938321, 128.2982924008, 280.5832475304, -63.6683731926, 88.5682601884, 213.0955504480, -332.1483762726, 472.2171073031, -179.0577189940, 106.8047884540, -250.7544653883, -245.7413644168, -151.7124561789, 67.3909070347, 39.2084206943, 110.6104053377, 113.5343360451, 112.8513522610, -478.6176849124, 437.8842683298, -299.4314554117, 30.9490111830, -475.7877035055}, Relationship.LEQ, -12274.0396251518));
                add(new LinearConstraint(new double[]{164.1118381892, -52.6929605099, -368.6998544699, -418.3014942408, 296.7708341063, -50.9112446279, -274.4635428920, 290.7114646211, 415.1951631117, 133.5554583795, 333.4237188449, -136.2679881845, 142.7917138379, -20.8839686666, 409.6354488447, 330.3312796033, -31.7995014119, 121.0518940942, -418.2605909000, -237.3828464293, -433.2546692557, 454.9218185770, -338.8542143359, 389.3988225528, -13.6699375994, -12.2794719274, -81.2763486384, -448.1904173597, 319.7593938931, 10.1864818806, 222.1624316451, 209.0366585224, 412.2205953563, -26.7765924553, -362.9625025991, 215.5019746309, 485.6707071567, -82.7787506669, -495.3282190234, 259.7833805525, 301.1313464134, 425.1322578870, 327.2684981422}, Relationship.LEQ, -2296.6506433404));
                add(new LinearConstraint(new double[]{-374.5989502169, -151.5219749339, 14.4226309306, 357.9162462609, 320.4530835626, -9.9657806960, 175.0012462657, 10.4166342802, 331.7943741048, -482.2409478442, 174.5166172026, -466.4563945960, 216.3188631720, 131.6031294907, -379.5023819223, 451.2426649663, -379.3032893924, -91.8425766315, -213.0011901317, 169.9988024655, 298.0983224083, 307.6672326004, 244.2684429396, 481.9466025513, -165.9399747057, 43.7599328801, -379.2696061729, -315.9335924134, 241.5563541049, 388.3047797105, -8.1828300141, 255.5133740236, -19.1488710051, -487.8568004137, -136.1759946774, 266.5723928382, -238.5422128727, 146.9986320787, 100.8374168814, 80.4260802893, -308.4527662656, 194.6946668938, 428.9235366504}, Relationship.LEQ, -2524.9072398783));
                add(new LinearConstraint(new double[]{-45.6010907457, -93.1186154639, 344.1452758225, -164.7021085866, 211.9989950393, -88.2105531379, 349.6093606232, -457.2528562809, 362.6473587699, -325.8981255598, 451.6277474446, 461.4458321371, 112.4424937334, -60.1455726789, -243.7897271852, 139.3033364843, 59.7717060692, 340.3445860842, -175.5114719859, 427.9375810690, -362.5584153005, -392.6491442968, 169.3034533287, 326.2992816507, 308.8415883739, -213.4668189361, -94.9276060888, 313.6427683019, -127.4586416472, 426.7065574457, 99.1836194536, 253.5386895630, -406.0198862592, -30.2506260744, -184.4504347588, -99.1800827947, 153.4145818863, 469.2291740647, 189.5827231058, 149.7963163649, 76.0493900073, 325.3981973325, 128.7409424045}, Relationship.LEQ, -6430.0496355829));
                add(new LinearConstraint(new double[]{393.2818734855, -59.9684286941, 393.9213867728, 32.8295448441, -226.7491721961, 66.6514348656, -115.6339927551, -125.9102951498, 254.3870965919, 160.0007056159, -77.6267544076, -363.9964465444, 17.4209317152, 213.1998112124, 213.9941112423, 486.0501401269, 339.3093011083, 411.8412558021, -51.9764095515, 321.0616644466, -13.1955403123, 431.3457104075, 187.9806816005, -168.3585282637, -172.1079346745, 357.2251367606, 310.4036063770, -3.4281750202, 366.0857020195, 17.4674067599, -252.3303884129, 134.8055461642, 235.5721093559, -17.9217457044, -295.3189427289, 311.5807451228, 434.4593204449, -393.1139051560, -163.8570587062, -279.6068268832, 218.8877581825, 277.0356998638, 94.7892699729}, Relationship.LEQ, -4311.2423610787));
                add(new LinearConstraint(new double[]{132.5220276415, 440.1671576721, 90.7991473280, -264.6244963333, -396.0509121443, 110.0967219182, 338.7752165862, -346.6980624342, -91.2336832963, 268.8865198452, -288.9586214274, -462.1588724540, 63.6505937154, -1.4895284824, 264.6480042052, 416.9179370121, 396.4575432613, 232.8937855657, 364.3567255559, 398.3101731718, 119.4078490950, -263.0757611444, -314.9907213011, 420.1008257886, 221.6753537232, 291.1031647740, 244.0677935138, 407.8962059785, 393.1992988564, 88.8249027051, 88.7465313722, -81.4475844059, 493.3012168070, 37.8434030045, -357.9618341249, -262.1520686535, 285.6945146525, -1.6948321301, -1.9294605303, 443.4854567928, -224.2672685386, -13.2019167001, -350.3877093552}, Relationship.LEQ, -8135.8527617928));
                add(new LinearConstraint(new double[]{-493.7498617776, 29.1857671258, 229.1786073483, 468.6054596725, -404.8935838125, -345.4323724601, -149.9280647907, -132.3267396818, 130.5546682526, -11.4750580288, -455.1759579289, -491.4502441971, -220.1900416600, 25.8838812341, 115.8896600822, 306.5296351717, 111.7035182752, -58.4538224941, 437.0742082521, 168.3155804046, 186.0652931382, 232.5090487205, -125.9388483588, 134.9560728616, 420.4855085864, 75.0159006362, 28.5373053316, -86.7919039016, -390.6362631638, -472.1502191383, 452.7971698478, -266.5344853346, 240.6618396774, 116.1019306634, 330.3400258466, -421.1604496467, -67.4655679744, -110.6821232920, 372.8568988467, 92.6723765864, -104.9806565399, 263.7442200882, 345.1366029556}, Relationship.LEQ, 7709.3442470145));
                add(new LinearConstraint(new double[]{-188.7727375378, -169.5310846918, -303.1966255195, -420.6353176520, 427.9785158873, -160.4309006280, -246.5688251870, -487.0911467043, 447.5917412607, 254.9497948505, -200.5372457354, 210.2059120451, 20.5382986122, 115.3781090026, 171.4941603184, 395.4433421053, 252.9327094737, 434.2868305895, 172.8683199242, -172.4028689434, -336.6616706437, 24.2428933749, 219.1541938549, 439.7675158718, 450.1245649666, 441.5451577749, 128.9705980594, -177.7686466423, -236.3797811543, -287.7627229123, -114.2531574668, -247.9994459386, -83.4350594410, -377.1087525011, -486.6050415730, 296.8707857915, -388.1310013306, 137.8377373955, -67.4987562245, 3.3910603040, -181.5508882385, -60.9106810688, -206.4723790812}, Relationship.LEQ, -16983.7136378687));
                add(new LinearConstraint(new double[]{-116.0840969613, 176.0236541893, 319.6460134129, -332.4496729070, -162.8534030284, 258.9631354848, 134.2117958037, 135.0363468297, -395.7159912184, -65.8914562081, -103.4843857136, 153.9867048455, 38.4610292899, -63.1456218390, 324.3346573466, -443.8217892205, 246.5575463718, 236.3333822714, -157.0614570239, 107.4725395850, -314.4456320591, 256.1089122557, -332.4131320816, -198.8265393365, -103.4433478021, 484.2534406266, -399.8115573101, 392.7749386196, -453.6827041313, -429.3779859853, 200.6756581866, -450.2116962391, -345.9016325791, 349.9499536121, 279.7793434772, -465.7185366028, 495.9663723045, 226.4740745853, -147.7271994808, 35.9582858179, -300.8746071459, 9.0974857649, -211.6133183112}, Relationship.LEQ, 9165.7513012928));
                add(new LinearConstraint(new double[]{465.6494664987, -404.3123086097, 405.9785929502, 347.0026855289, -302.0163191133, -129.0812589156, 157.1747559572, 9.8292205414, 37.8410472448, -195.8621494638, 380.5833324032, 320.3120288624, 475.6303813355, 12.0577469801, -186.1876464245, 156.5349153410, 239.3078932632, -14.3540460805, 190.9073870661, 182.0399993664, 129.5530061776, -228.5973253467, -214.5424452754, -339.2096651463, 332.4271343461, 144.2893275872, 202.5009517364, 322.3309498094, 11.1813822044, 212.8897704191, 70.9930088447, 227.9847236802, -344.3625508953, -43.0427891147, -456.5147706710, -148.3856267524, 250.5735322214, 413.3192652537, -329.2069741248, 7.4276543290, -186.1950954656, 98.1846617847, -213.1842874184}, Relationship.LEQ, -2607.7651068364));
                add(new LinearConstraint(new double[]{-249.8432963461, -217.7849574530, 12.3034711862, -236.8850653427, -230.7155653959, -80.7044507171, -133.7508175074, 331.7872335009, -176.6951583506, -129.2412996213, -240.2837832926, -344.3605421425, -365.5413408480, -436.1143117092, -160.7044330882, -312.8271801157, -96.4663415211, 376.8298702529, 246.0187570979, -345.2216189795, 216.5346454118, -148.3221086008, 360.4359107405, -366.8996766400, 0.0947974934, 303.1834360606, 15.7007360126, -147.7121716840, -384.6194555787, -487.2204615498, -22.1123168824, 180.3703466136, -174.2500024286, 475.7954779524, 216.3984308914, 194.4362887569, 425.1828014949, -395.6255691436, -382.8012747972, 393.9900381633, -227.4931915362, -120.5911990429, -172.2415727611}, Relationship.LEQ, -9984.1959185636));
                add(new LinearConstraint(new double[]{340.7055228332, 191.8798668704, -135.0489137154, 463.8107893025, 161.0968896351, -482.6906410608, -34.1980892000, 109.9085785902, 218.9331519377, -373.3997050356, -84.1441046371, -287.5170643603, 439.9782716428, -247.6349478722, -248.9294670748, 458.1003519812, -103.1223569766, 1.9089360083, 296.5206574938, 435.2583333115, 259.6428436210, 336.3578139309, 478.8353594909, -283.5946369464, 316.3314266572, -380.0116485730, -270.8298407000, -283.8273775528, -401.9898148580, -415.3594825793, 79.6500747338, -350.2995650348, -243.6524738211, -448.8386892290, -251.3977537246, -384.8620339935, 362.6891177777, 403.1498034680, 108.5295852848, -319.1603087068, 224.2278891653, -382.1565649281, -64.3404107707}, Relationship.LEQ, 10768.7415120524));
                add(new LinearConstraint(new double[]{145.8881914948, 124.8061001421, -248.9645583668, 108.2343762982, 305.8011956353, 385.1484851277, -119.5009175183, -488.5656924659, 57.7562949402, 362.3320918154, -415.4507553431, -100.5784807509, 25.7717362478, -9.8221916581, 238.9395515691, 25.1813044978, -186.0652536901, 334.5209746453, 83.0152234813, -169.8475027125, -481.0800111064, -107.9749706700, 495.1694950281, -27.1285807549, -496.8979386883, 372.9855408050, 103.4333981728, -11.4669979270, -437.0632224979, -125.4724052259, 294.0959776024, 199.2512324647, -289.7074611547, -433.5804452045, -157.6156578353, -86.2546321676, 273.6718575321, -116.2042265493, 456.6157363375, 30.3740442489, -78.2297293069, -250.5973644248, 0.8671292866}, Relationship.LEQ, -25869.5819255597));
                add(new LinearConstraint(new double[]{-486.4217021206, -467.4490173170, 41.0900266896, 103.9422087749, 387.2392464598, 292.7796831550, 145.7313685349, 157.4409248006, -185.8660567480, 353.3043774206, 64.3042477644, -273.1899508685, -134.8286387832, 402.6464433744, -494.0153046113, 82.9544117496, -136.9788148143, 132.4070889074, -254.8062230479, 404.1786805838, -72.9666378284, -115.0278473496, -307.7428332974, -478.6171283368, -143.1537562050, -92.9968689006, 384.6170158210, 435.0140444859, -253.8061957917, -417.1093447510, 77.7035629006, -74.2958362115, 270.1793299925, 456.9165629575, 244.4572638038, -281.7807796383, -452.7084352901, -56.3086702497, -171.3752311680, -47.7097225697, -158.3033257950, 373.5039024254, 39.3342650736}, Relationship.LEQ, 5044.3640677206));
                add(new LinearConstraint(new double[]{141.8470098609, 422.5408670808, -176.9100253797, -411.4176239018, -171.7938138004, 37.5031194482, -95.7931411206, 355.6019584141, 225.8970424648, -320.0327557650, -54.1596611246, 71.5984121408, -172.6627075340, 87.9104795122, -48.8516985530, 354.0093508783, 365.4353693460, -219.8627584674, 497.7557289404, 116.8044222239, -280.3232022783, 213.9164831062, -221.4252863134, -206.6283997286, -154.5608039830, -153.0413536323, -291.8342755652, 226.8809455662, -468.8681689218, 351.9320906270, -453.7911587889, -341.5455503840, -191.7364399788, -114.9616800653, -411.7139110157, 99.9155784325, 266.3008368157, -380.2634399129, 479.0595883593, 390.5872913389, 357.1846521941, -277.4723948386, -385.9380514474}, Relationship.LEQ, 3902.8089927520));
                add(new LinearConstraint(new double[]{13.6344526147, -430.2218606986, -86.3007778471, 215.9913073246, -449.1268043093, 282.8860958719, -23.3009920749, 455.6349608322, -126.8330404790, -406.3412165528, 250.1188941470, -158.8879782875, 464.5794182879, -357.4048514455, 389.1640065078, -482.1430031973, -492.4002036851, -440.1657336644, 422.4442494437, -90.5125580096, -256.1894835548, -418.1476420800, -313.0643816691, -173.0191705691, 367.5185154660, 16.5608190357, -248.2079982049, -135.3168535237, 326.2822732925, -414.7217639337, -288.7128759196, -471.8738517541, -108.4758407477, -457.8803730646, 294.5489832963, 113.8254862890, 257.9255874681, 499.6663089839, 213.3820078920, 92.6770728695, 232.0604406780, 74.3504306410, 49.3547338588}, Relationship.LEQ, 9256.2359573664));
                add(new LinearConstraint(new double[]{-221.2703728708, 305.8079968772, -232.5360219037, -30.6660630025, 307.7180579448, 301.5253755327, -363.9309876951, 431.8956707468, 104.2025672231, -288.5766197206, -68.5778302327, 490.8409110829, 232.7972812415, 446.5946012489, 310.0052570196, 453.6780063108, -421.7687109338, -477.7708337208, 143.0133933409, 305.9213452138, 424.9100139475, 26.8344849484, 424.7117281805, -81.1305887899, 123.1212461885, -1.8247376875, 90.0221648059, -144.0481021554, -58.5972573133, 420.9300519737, -293.4190502819, 143.5208408959, 162.5977488288, -348.1191975482, -457.3599088405, -334.0934057345, -154.6185036297, 325.5227434371, -45.4308542264, 294.7543209942, 443.5499380961, 196.3013838212, -113.6543031920}, Relationship.LEQ, 15086.4737906725));
                add(new LinearConstraint(new double[]{-347.8798681921, 433.2547339785, 28.3068486031, -294.6418390485, 432.2986500266, -401.3417089841, -156.4340855726, 435.2761706826, 156.6331926853, -85.6589288282, 237.8817393927, -354.7696577597, 211.1695225080, 447.6613692970, 122.4007804501, 21.5281667458, 193.1944285200, -466.2991180177, 498.5751746066, -305.7987023452, 51.1524484454, 91.0564660002, -19.3079106949, 215.6568427847, 127.5825854718, 485.6789848089, -136.1663991063, 110.7672572505, 86.4287504892, 250.5289146665, -56.2822075518, 191.4228061548, 132.8353097500, 278.3889960515, 299.7560057000, -158.7344213782, -233.4526777388, 70.9941675959, 163.7928739566, -354.7309520243, -450.7073362645, 249.7093963946, -185.6211326157}, Relationship.LEQ, 3002.4375202268));
                add(new LinearConstraint(new double[]{12.0308357297, -188.6931051284, 160.0585557805, 334.0301149052, -120.2884648549, -453.0365554161, 305.0394912124, 413.0095655502, 249.1934459322, -366.5801283780, -73.2596367387, -416.8385206607, 220.2318954615, -347.6853204405, -262.9005740040, 275.5248594729, -250.0487508415, 197.0237107974, -351.8429991368, -214.9799529653, 177.9766275429, -214.0910789289, -482.6910404890, -191.5443244343, -47.5935584928, 16.6988004821, -491.2393046086, 156.7271025039, -423.9344119384, -231.2060789549, 175.0937988641, -432.3716570329, -312.0131264889, -211.3891617053, 336.4907554769, 277.1452048737, 496.8257662466, 177.6484475468, -284.9543880533, -105.2080446968, 490.9219402934, 143.7809121158, 67.7212504691}, Relationship.LEQ, 13323.0324149306));
                add(new LinearConstraint(new double[]{463.9782865378, -126.6486051866, 253.8972455705, 456.7618479127, 415.8361825765, -465.0383387450, 272.0841775369, -29.8215509600, -185.4161739495, 235.4225596403, -194.7358004314, -418.7448656171, 39.2417242699, 214.1505343552, 225.4494150021, 390.2341333030, -438.4724106572, -315.6336145987, 325.4333140158, 283.9492469810, -429.2890407892, -492.2283591102, 269.3426146368, -405.8680128690, 297.3249121927, 323.9495763785, 383.4801817962, 131.2949657317, 249.5107543449, -153.4429439100, 445.0823234988, 160.2625103187, -59.9708192295, -214.5557810423, 68.9964001370, 333.6150222878, 452.4790529314, 257.1595157428, -359.0822362913, 205.7575462327, 372.0228473426, -394.1326056786, 0.7078976617}, Relationship.LEQ, -14905.9132511163));
                add(new LinearConstraint(new double[]{188.9257485555, 272.5268019012, -259.5519682499, -75.8615756160, 272.4730575255, -277.7514160796, -450.7342886472, 440.6704977995, -91.6615770407, 468.0700465229, -489.3954296181, 58.3279198187, -305.7668603846, -86.0437229621, -362.5097958901, -312.0769221290, -203.3252187779, 80.6563658348, 217.4777865005, 78.5573371277, 185.9710314276, 275.1148656604, -268.6496121358, -414.1188165050, -12.9774811366, 417.4577469666, 208.2690645451, 84.6996580715, -489.6939036644, 49.1430646069, -115.0408284285, 137.8921061040, 360.4082681323, 11.6869647315, -52.3502732135, -297.4396041036, 258.7354859007, 407.4452028997, -47.1340021190, -20.4844781425, -137.4632321598, -267.7480192930, 246.8076760027}, Relationship.LEQ, -6515.0832352457));
                add(new LinearConstraint(new double[]{-85.8687748877, 4.2866019774, 332.9282018899, 130.4965975233, -487.7587740016, 304.3601910921, -326.5363481386, -351.9809978115, 161.5894532992, 389.8842749749, -146.9578838012, 133.2748206289, -472.5734364921, -51.8966578050, 381.1376502468, -20.4623150699, 498.4128551706, -386.2557553740, -334.9846623673, 222.5596586537, 244.2865603186, 92.0550239172, -154.0527643120, 493.4006464464, 444.0077821058, -399.7233232370, 258.8613065029, 420.5043941782, -244.4238770824, 368.9112397159, 249.2188102134, 45.1382687065, -99.5229987105, 128.7200003061, -39.2023191687, -297.9905729726, 443.7199404342, 350.2060006632, 494.1932037111, -139.1839535437, 464.6896994741, 499.4054863901, -471.7707533722}, Relationship.LEQ, 3407.3968117730));
                add(new LinearConstraint(new double[]{-165.4610208114, -272.8673269503, 95.1065522642, -62.0970032526, -257.3904625958, 218.3933904669, -295.0026988705, 68.0122555113, 447.3756582877, -105.4973528261, 426.9281350106, -403.6524478942, -33.3374469519, 282.5119139621, -149.8238318635, -23.7882902065, -432.6005384292, 144.8723920407, 74.0360540432, -300.6076989795, 466.9255532098, -103.1698585205, 3.6982980633, 249.3869152583, 115.8737912550, -70.1297524579, 266.1121820306, 313.6587196388, -203.0831859835, -293.4035141955, 2.0894835598, 177.4383348328, 44.1655727129, -104.2458246933, -360.4622834422, -171.9668832136, -435.2300093728, -124.3264044679, -492.4050793846, 61.3001674602, -72.7212638913, -349.7682323624, -187.4297780243}, Relationship.LEQ, -13731.9278544806));
                add(new LinearConstraint(new double[]{270.5724382583, -406.4988392858, 63.9777726576, -83.9757497260, -317.3069976318, 467.5735196577, -487.4160328535, 55.1503666330, -299.3504473227, -157.3822423827, -457.7287120636, 400.2436070455, -409.7111466599, 389.0511036653, 243.6541940028, -209.1931022834, -320.4238689057, -491.2270033800, -410.2341330665, 366.5463565205, 39.1404787022, 425.7313453473, -267.2328225376, -265.7203077591, 230.6958326120, 165.6613751134, -54.4878773625, -322.7082887340, -323.0425918936, -179.3424858085, -357.0632660307, -405.6987670546, 445.9304171659, 116.3088088601, -264.3786472231, 180.3989967105, -112.0436098514, -422.5508432628, 291.5089423429, 343.7615158334, -384.3403441490, -476.8566147634, 111.6447425224}, Relationship.LEQ, -5675.9150696455));
                add(new LinearConstraint(new double[]{-225.0552145496, -287.6486377011, 361.9359651763, 97.0778700799, 419.4533904019, -291.2187105377, -154.6878420434, 463.9737670023, 71.3965213493, -63.2346321950, -152.6995263051, -397.3838145546, 228.4367762832, -40.4404708024, -257.0875923825, -469.5687419976, -340.6980127574, -272.7089885922, -286.1283239265, -380.3054802799, -179.9298500970, 413.8462372493, -7.4045028631, 245.1208036947, -381.4268211483, -9.6870957478, 22.9744595750, -300.2994309133, 56.1247108698, -132.8397402768, -192.1211339623, -263.1540017868, -16.2147903179, -221.8388495645, 499.4526390583, 140.8541154884, 324.6351990364, -423.8689894716, -19.3619204401, -72.1089371047, -365.9645095045, -236.1219401380, -214.9920958256}, Relationship.LEQ, 6787.4187392409));
                add(new LinearConstraint(new double[]{-203.3890862894, 393.3298897887, 206.8640763352, -323.5031125266, -8.2981037655, 431.6221385110, -135.3047880709, 383.9831112191, -201.7137526731, -184.9955070618, 298.4674373312, 235.5602111346, 455.8810051491, 189.7404655446, 404.8564628007, -318.9240371565, -150.3078817866, -226.2529652858, -355.5886328972, 172.0873685962, -253.7121139709, -136.4591911698, -9.2268152600, 267.0911562624, 170.3292586792, 258.1279391526, -465.5453500174, 141.9333744166, -416.6015338641, 274.7946255565, -497.6801528276, -59.3910228445, -297.2749663706, 91.3261911645, 208.7855644451, 164.3582996810, 380.6158886830, -134.9976185261, 115.8988795084, 215.9119477378, -45.3453956266, 450.7873337416, -253.1717060538}, Relationship.LEQ, 11972.6891244731));
                add(new LinearConstraint(new double[]{-125.7580404843, -430.5476869122, -329.0613636976, 40.1374649634, 210.8081351842, -39.0593159812, 158.5027355167, -186.6851309429, -132.9648560098, 399.1037012553, 389.2355847304, -159.6746583063, -85.3152842872, 441.0068832093, 291.2195051827, 404.4582529847, -488.8638682516, 437.3265032213, 185.4221129909, -80.1081843261, 271.4949277850, -360.0731753511, 415.8907807194, -162.2654487960, -193.8576068637, 208.5276888386, 56.8409894288, 474.0607455243, 372.8527222336, -459.5562023283, -394.7614799575, 178.5497576069, 238.1128716194, -374.3035069151, 291.1076847218, -256.2159273207, 394.0382139167, 64.3692880565, 267.6815674611, -407.0435163731, -264.7158907145, -26.1009725197, 479.6422202307}, Relationship.LEQ, -24215.3472984807));
                add(new LinearConstraint(new double[]{-335.3874998383, 261.0643845250, 450.0822398726, 167.8034923491, 366.2790764480, -363.2458727582, -424.5487890416, -78.4667458203, -336.8342393925, 311.1677312205, -227.3657297988, 386.9519592152, 456.5898757790, -477.0788326502, 483.4418332745, 4.8518006798, 236.9799597504, -72.3988066645, 109.1731546473, 158.0899552529, 49.1156888434, 399.6315231994, -78.4512341685, 244.0717950758, -402.6127611954, -14.1152206841, 376.1898497261, -465.3237051478, -115.4628868874, -409.7054345278, -459.0447696688, 131.8817180181, -108.3944657517, 465.7916813360, 43.8296181707, -472.4549869507, 327.3081157662, -256.7902170545, -456.3103351452, -367.7667336067, 401.8322935994, 36.3751190318, 232.1567983162}, Relationship.LEQ, 36583.5490848071));
                add(new LinearConstraint(new double[]{-86.9529570658, -371.5839802671, 285.8791340642, -149.4422777221, -327.6220773493, -133.6021443615, -494.0328902263, 95.8435798960, 443.1076797193, 169.7091748018, -395.0593709913, -1.2785607051, 115.6199464662, 492.1958703537, -268.5717076358, -319.8677407108, -197.8881332917, 168.8595663312, -141.0083485838, 424.2511432258, -102.4408467105, -174.9926235520, -392.1690034058, -38.2454431156, -75.7754136456, 429.0603907112, -83.2386679384, -276.6394925845, 153.7592475600, -370.2016663336, -262.2695977322, 167.7982532626, -416.0467440927, 234.6382626784, -413.7167826797, 207.3093480228, -163.5492031015, 57.6158605105, 348.7604611659, -171.0230049389, -62.7481204954, 394.0572440400, 220.6510703581}, Relationship.LEQ, -932.5290113023));
                add(new LinearConstraint(new double[]{-256.4188442794, 63.5007620009, 30.4016501694, 265.7341655379, -11.2880623399, 303.2475486454, 489.1189626257, 227.7278044205, -469.4085739882, -208.0096822630, 98.9836017650, 320.2575788229, 410.6207487201, -263.7096465920, -448.1732437602, 246.4601949270, -244.2983761690, 262.2426725155, 454.2038335871, -21.0638973699, 389.6687856423, 320.4896124659, 126.4323944809, -366.8965002794, 259.4855069591, -65.1859884224, 40.4438917805, -220.2107863818, 427.0872414619, -97.1375839571, 184.9321680438, 3.3609172930, 429.5160906965, -7.8799509905, 276.1640329793, 149.4012681750, 267.9969170176, 265.0791263716, 268.9501880076, 499.8694845298, 150.8633295787, 414.6124376724, 64.6578267430}, Relationship.LEQ, 17063.8720528059));
                add(new LinearConstraint(new double[]{385.7499254679, -106.1156520446, -338.2215805749, 278.7619196076, 493.2249994649, 184.7021581775, 249.4478702989, -321.5514436648, -302.9548096880, -235.9697597976, -94.5516946672, -494.1891830186, 435.4722205151, 200.4605623382, -372.3961441281, -180.0303686322, 433.5991047879, 340.7530583419, 401.1156554556, 381.6868263631, 178.3907523687, -268.8705791913, -115.7358399012, 487.3202383376, 346.1528285485, 258.8247361338, 81.0393600226, -34.1383533463, -153.6008034345, 496.4714624547, 61.6425310838, -246.2387310773, -333.4029991068, 385.2101355095, 390.4360723537, -181.3126003242, -370.9741738808, -298.0075503077, 425.8006030094, -283.9348331769, -496.0044970806, 222.3616052909, 67.4911896643}, Relationship.LEQ, 2349.3199077921));
                add(new LinearConstraint(new double[]{-337.0754411150, -387.9340132320, -155.5320445811, 384.8558239556, -399.7105721507, -140.8179819806, 459.8081490899, 62.0625106321, 205.2121448091, 492.9925746017, 39.1908121635, -339.2970905850, -378.5376221934, -73.3124231363, 365.5767483564, 451.5252733552, -101.0929833532, 8.0238483958, 325.0904042270, -209.4983869589, 409.9485815752, 265.4013455721, -362.2579016841, -68.6416118345, 181.2517624779, 396.3834743795, -298.4873050824, -51.3304955921, -125.9353091555, 261.5092777249, 39.4551775446, -299.1365846744, 135.6998856724, 18.3158579589, 406.8502755728, -162.3085034759, 440.2470996492, 58.8094672852, 89.0865738710, 88.1501231154, 338.3486541746, 98.1939980787, 300.8179589036}, Relationship.LEQ, -3055.0662272041));
                add(new LinearConstraint(new double[]{243.8753390356, 103.4862849448, -116.9158032348, 466.3247096751, 499.5986526032, -252.2824865913, -312.5367716984, 403.5304480620, 484.4633991129, -271.2596467681, 134.9567317240, 144.1043902127, -48.1780194039, -398.7675789703, 442.1928392672, 442.0571022678, -379.0001193017, -263.6942507953, -81.3363570672, -499.6106779009, 283.8197034492, -354.7963291225, -443.9033506746, 83.8841241656, 333.2182699209, 181.8856633708, -145.3066611233, -117.6765151496, -327.1496728881, 73.8717558537, 460.8502333586, 474.3794314961, 424.5787669849, -178.4620888943, -272.8895810577, 36.3754502685, -284.9897164304, 253.1246765305, -252.5227659372, -109.6911415801, 92.7426420811, -73.9589292750, -230.1465705838}, Relationship.LEQ, -7222.8273344346));
                add(new LinearConstraint(new double[]{-195.2525091291, 124.6164685994, 142.2529075985, -477.3068630553, 243.3206763086, 420.2732330804, 110.5645014077, 367.2052647911, 364.2898604480, -367.8364205570, -146.9700144933, 435.2628643210, -227.3233170539, -445.3661537393, 110.2318148145, -483.8240432172, 473.7692273932, 2.4503243312, -350.7318584021, -156.9568651290, -4.4326087363, -434.4594714911, -413.0736422843, -212.5480399317, -297.1774311119, -37.9253435334, 289.0483562475, 105.5985796702, -356.5998423984, 32.0317129169, 161.9142009826, 413.2253504954, 194.4514409354, 222.9655931383, 156.3533848492, 79.4703509875, 108.8895102342, -114.4935319821, 216.4255116230, 398.6681311118, 301.9209844986, -181.7967434610, 221.8956154095}, Relationship.LEQ, 0.5599917670));
                add(new LinearConstraint(new double[]{-137.1060743081, 279.6329315315, 197.3614426922, 231.0282557735, 329.4796472661, 120.2733855944, -309.2265599218, 107.4259103756, 219.2138952115, -222.5437382016, 260.2204436622, 277.9954539704, 177.6811137083, -376.2132665340, -49.7624030384, 200.7196035517, -126.8671496466, -437.2623440818, 487.5248981135, -105.1313845705, -55.9105668208, -175.3045917568, -319.1513855364, 115.4667856060, -110.5180904036, -367.4444001026, 171.0296400922, -489.9445164985, -219.8512879282, -221.9632273480, 244.3065388302, -331.4494165993, 66.0097988820, 178.2004602365, -104.0067365527, 208.4361849240, -445.0014539542, 166.2366846227, -288.7656173837, 425.2326124978, -152.1607419010, -388.6060824599, 415.7939468539}, Relationship.LEQ, 9059.2810240193));
                add(new LinearConstraint(new double[]{127.0166849936, -39.9971042887, 142.1963182879, -387.1917959935, 218.7910108044, -57.1357601804, -444.8295072288, -131.8607936123, 273.8698310927, 3.8399496330, -14.7678782657, -450.2963339389, 245.3568401422, 470.3088588205, -369.6297342137, -357.4266406943, -418.1459895424, 108.2172398166, -296.8111920535, 484.5465096126, 199.1026172884, -17.2247008036, 124.4850638710, -409.3014982622, -154.6942161832, 480.4208008286, -5.8688799011, -10.2597873068, 433.2830116464, -61.5080686870, -393.7437272746, 122.6291696073, -223.1344495380, -423.8793509601, 218.1115833224, 304.0746391007, 439.4485577596, 323.1795900546, 130.6304335930, -145.9772758831, 79.3740693371, -296.3154471444, -106.4180591316}, Relationship.LEQ, -18038.2286053466));
                add(new LinearConstraint(new double[]{-240.1730204728, 145.8755080635, -390.6533731249, 221.3641193784, 180.1835036666, -36.7301979877, 376.7267957101, -247.5819297289, -318.7404862535, -220.6995059742, -110.1632353235, -248.6352397960, 178.6644482658, 352.7239680226, 87.0009102021, -293.4444288458, -113.5264103255, -398.1989347949, -469.2797383338, -245.9939275934, -192.3102234512, 122.3259048952, -107.8486033723, 251.0609758784, 488.4733740424, -352.1025724146, -487.8260535847, 458.7595590696, 459.8769526384, -289.6014589945, -483.9940542204, 429.2461195391, 123.8291284336, 224.4686030408, 369.4590327755, -136.4857293921, -420.6301830411, -158.1756538353, 82.3084977679, -470.0300776061, 30.6467319964, -300.3939039165, 487.8857377868}, Relationship.LEQ, 16209.3495555274));
                add(new LinearConstraint(new double[]{-30.0588147584, -378.5051316986, 157.8835245521, 120.3731377987, 286.9986665802, -78.8326674476, -452.5024927606, 95.4389515886, -179.8024252018, -295.2742472052, -91.5899174779, 183.8988891394, -364.4023093951, -27.9401916913, 355.7553534234, -480.6474695409, 496.6670478137, -123.3317858105, 230.0870951978, 8.1301750386, -429.5056130047, 219.2877390926, -443.4958157674, 336.0622027617, 405.5518207904, -377.2516937227, -29.5291171888, -283.0247125802, -388.4200171470, -136.2080068521, -46.4979173566, 431.4079171239, 473.9265032363, -477.8345107342, -398.2335043025, -408.2670896962, -57.3245898702, 448.1147326882, 436.7937079289, 113.8201190724, 51.1635774738, -453.5187634773, -40.1464074115}, Relationship.LEQ, -8958.6549281857));
                add(equationFromString(nCoef, "x0 <= 10"));
                add(equationFromString(nCoef, "x0 >= -10"));
                add(equationFromString(nCoef, "x1 <= 10"));
                add(equationFromString(nCoef, "x1 >= -10"));
                add(equationFromString(nCoef, "x2 <= 10"));
                add(equationFromString(nCoef, "x2 >= -10"));
                add(equationFromString(nCoef, "x3 <= 10"));
                add(equationFromString(nCoef, "x3 >= -10"));
                add(equationFromString(nCoef, "x4 <= 10"));
                add(equationFromString(nCoef, "x4 >= -10"));
                add(equationFromString(nCoef, "x5 <= 10"));
                add(equationFromString(nCoef, "x5 >= -10"));
                add(equationFromString(nCoef, "x6 <= 10"));
                add(equationFromString(nCoef, "x6 >= -10"));
                add(equationFromString(nCoef, "x7 <= 10"));
                add(equationFromString(nCoef, "x7 >= -10"));
                add(equationFromString(nCoef, "x8 <= 10"));
                add(equationFromString(nCoef, "x8 >= -10"));
                add(equationFromString(nCoef, "x9 <= 10"));
                add(equationFromString(nCoef, "x9 >= -10"));
                add(equationFromString(nCoef, "x10 <= 10"));
                add(equationFromString(nCoef, "x10 >= -10"));
                add(equationFromString(nCoef, "x11 <= 10"));
                add(equationFromString(nCoef, "x11 >= -10"));
                add(equationFromString(nCoef, "x12 <= 10"));
                add(equationFromString(nCoef, "x12 >= -10"));
                add(equationFromString(nCoef, "x13 <= 10"));
                add(equationFromString(nCoef, "x13 >= -10"));
                add(equationFromString(nCoef, "x14 <= 10"));
                add(equationFromString(nCoef, "x14 >= -10"));
                add(equationFromString(nCoef, "x15 <= 10"));
                add(equationFromString(nCoef, "x15 >= -10"));
                add(equationFromString(nCoef, "x16 <= 10"));
                add(equationFromString(nCoef, "x16 >= -10"));
                add(equationFromString(nCoef, "x17 <= 10"));
                add(equationFromString(nCoef, "x17 >= -10"));
                add(equationFromString(nCoef, "x18 <= 10"));
                add(equationFromString(nCoef, "x18 >= -10"));
                add(equationFromString(nCoef, "x19 <= 10"));
                add(equationFromString(nCoef, "x19 >= -10"));
                add(equationFromString(nCoef, "x20 <= 10"));
                add(equationFromString(nCoef, "x20 >= -10"));
                add(equationFromString(nCoef, "x21 <= 10"));
                add(equationFromString(nCoef, "x21 >= -10"));
                add(equationFromString(nCoef, "x22 <= 10"));
                add(equationFromString(nCoef, "x22 >= -10"));
                add(equationFromString(nCoef, "x23 <= 10"));
                add(equationFromString(nCoef, "x23 >= -10"));
                add(equationFromString(nCoef, "x24 <= 10"));
                add(equationFromString(nCoef, "x24 >= -10"));
                add(equationFromString(nCoef, "x25 <= 10"));
                add(equationFromString(nCoef, "x25 >= -10"));
                add(equationFromString(nCoef, "x26 <= 10"));
                add(equationFromString(nCoef, "x26 >= -10"));
                add(equationFromString(nCoef, "x27 <= 10"));
                add(equationFromString(nCoef, "x27 >= -10"));
                add(equationFromString(nCoef, "x28 <= 10"));
                add(equationFromString(nCoef, "x28 >= -10"));
                add(equationFromString(nCoef, "x29 <= 10"));
                add(equationFromString(nCoef, "x29 >= -10"));
                add(equationFromString(nCoef, "x30 <= 10"));
                add(equationFromString(nCoef, "x30 >= -10"));
                add(equationFromString(nCoef, "x31 <= 10"));
                add(equationFromString(nCoef, "x31 >= -10"));
                add(equationFromString(nCoef, "x32 <= 10"));
                add(equationFromString(nCoef, "x32 >= -10"));
                add(equationFromString(nCoef, "x33 <= 10"));
                add(equationFromString(nCoef, "x33 >= -10"));
                add(equationFromString(nCoef, "x34 <= 10"));
                add(equationFromString(nCoef, "x34 >= -10"));
                add(equationFromString(nCoef, "x35 <= 10"));
                add(equationFromString(nCoef, "x35 >= -10"));
                add(equationFromString(nCoef, "x36 <= 10"));
                add(equationFromString(nCoef, "x36 >= -10"));
                add(equationFromString(nCoef, "x37 <= 10"));
                add(equationFromString(nCoef, "x37 >= -10"));
                add(equationFromString(nCoef, "x38 <= 10"));
                add(equationFromString(nCoef, "x38 >= -10"));
                add(equationFromString(nCoef, "x39 <= 10"));
                add(equationFromString(nCoef, "x39 >= -10"));
                add(equationFromString(nCoef, "x40 <= 10"));
                add(equationFromString(nCoef, "x40 >= -10"));
                add(equationFromString(nCoef, "x41 <= 10"));
                add(equationFromString(nCoef, "x41 >= -10"));
                add(equationFromString(nCoef, "x42 <= 10"));
                add(equationFromString(nCoef, "x42 >= -10"));
            }};

        final PointValuePair resultOriginal = solve(c, constraints);
        final double z1 = resultOriginal.getValue();
        System.out.println("z1=" + z1); // XXX REMOVE

        final List<LinearConstraint> shiftedConstraints = new ArrayList<>();
        for (LinearConstraint cons : constraints) {
            final double[] coeffs = cons.getCoefficients().toArray();
            final double rhs = cons.getValue() + Sum.ofProducts(coeffs, d).getAsDouble();
            shiftedConstraints.add(new LinearConstraint(coeffs, cons.getRelationship(), rhs));
        }

        final PointValuePair resultShifted = solve(c, shiftedConstraints);
        final double z2 = resultShifted.getValue();

        final double z1New = z2 - Sum.ofProducts(c, d).getAsDouble();
        Assert.assertEquals("MR violated", z1, z1New, 1e-4);
    }

    private static PointValuePair solve(double[] c,
                                        Collection<LinearConstraint> constraints)
        throws Exception {
        final LinearObjectiveFunction f = new LinearObjectiveFunction(c, 0);
        final double epsilon = 1e-5; // Fails at 1e-6.
        final SimplexSolver solver = new SimplexSolver(epsilon);
        return solver.optimize(new MaxIter(1000),
                               f,
                               new LinearConstraintSet(constraints),
                               GoalType.MAXIMIZE);
    }
}
