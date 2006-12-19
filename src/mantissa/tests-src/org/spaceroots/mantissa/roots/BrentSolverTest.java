// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.roots;

import org.spaceroots.mantissa.functions.FunctionException;

import junit.framework.*;

public class BrentSolverTest
  extends TestCase {

  public BrentSolverTest(String name) {
    super(name);
  }

  public void testAlefeldPotraShi()
  throws FunctionException {

    TestProblem[] problems = TestProblem.getAPSProblems();
    BrentSolver solver     = new BrentSolver();

    for (int i = 0; i < problems.length; ++i) {
      TestProblem p = problems[i];
      double tol = 1.0e-10 * Math.abs(p.getExpectedRoot());
      assertTrue(solver.findRoot(p, new Checker(tol), 1000,
                                 p.getA(), p.valueAt(p.getA()),
                                 p.getB(), p.valueAt(p.getB())));
      assertTrue(p.checkResult(solver.getRoot(), tol));
    }

  }

  private static class Checker implements ConvergenceChecker {

    private double tolerance;

    public Checker (double tolerance) {
      this.tolerance = tolerance;
    }

    public int converged (double xLow, double fLow,
                          double xHigh, double fHigh) {
      return (Math.abs(xHigh - xLow) <= tolerance)
        ? ((Math.abs(fLow) <= Math.abs(fHigh))
           ? ConvergenceChecker.LOW
           : ConvergenceChecker.HIGH)
        : ConvergenceChecker.NONE;
    }

  }

  public static Test suite() {
    return new TestSuite(BrentSolverTest.class);
  }

}
