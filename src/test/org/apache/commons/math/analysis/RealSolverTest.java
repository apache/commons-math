/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.analysis;

import org.apache.commons.math.MathException;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Testcase for UnivariateRealSolver.
 * Because Brent-Dekker is guaranteed to converge in less than the default
 * maximum iteration count due to bisection fallback, it is quite hard to
 * debug. I include measured iteration counts plus one in order to detect
 * regressions. On average Brent-Dekker should use 4..5 iterations for the
 * default absolute accuracy of 10E-8 for sinus and the quintic function around
 * zero, and 5..10 iterations for the other zeros.
 * 
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:09:08 $
 */
public final class RealSolverTest extends TestCase {

    public RealSolverTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(RealSolverTest.class);
        suite.setName("UnivariateRealSolver Tests");
        return suite;
    }

    public void testSinZero() throws MathException {
        // The sinus function is behaved well around the root at #pi. The second
        // order derivative is zero, which means linar approximating methods will
        // still converge quadratically. 
        UnivariateRealFunction f = new SinFunction();
        double result;
        UnivariateRealSolver solver = new BrentSolver(f);
        // Somewhat benign interval. The function is monotonous.
        result = solver.solve(3, 4);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());
        // 4 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 5);
        // Larger and somewhat less benign interval. The function is grows first.
        result = solver.solve(1, 4);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());
        // 5 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 6);
        solver = new SecantSolver(f);
        result = solver.solve(3, 4);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());
        // 4 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 5);
        result = solver.solve(1, 4);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());
        // 5 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 6);
    }

    public void testQuinticZero() throws MathException {
        // The quintic function has zeroes at 0, +-0.5 and +-1.
        // Around the root of 0 the function is well behaved, with a second derivative
        // of zero a 0.
        // The other roots are less well to find, in particular the root at 1, because
        // the function grows fast for x>1.
        // The function has extrema (first derivative is zero) at 0.27195613 and 0.82221643,
        // intervals containing these values are harder for the solvers.
        UnivariateRealFunction f = new QuinticFunction();
        double result;
        // Brent-Dekker solver.
        UnivariateRealSolver solver = new BrentSolver(f);
        // Symmetric bracket around 0. Test whether solvers can handle hitting
        // the root in the first iteration.
        result = solver.solve(-0.2, 0.2);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        assertTrue(solver.getIterationCount() <= 2);
        // 1 iterations on i586 JDK 1.4.1.
        // Asymmetric bracket around 0, just for fun. Contains extremum.
        result = solver.solve(-0.1, 0.3);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        // 5 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 6);
        // Large bracket around 0. Contains two extrema.
        result = solver.solve(-0.3, 0.45);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        // 6 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 7);
        // Benign bracket around 0.5, function is monotonous.
        result = solver.solve(0.3, 0.7);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 6 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 7);
        // Less benign bracket around 0.5, contains one extremum.
        result = solver.solve(0.2, 0.6);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 6 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 7);
        // Large, less benign bracket around 0.5, contains both extrema.
        result = solver.solve(0.05, 0.95);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 8 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 9);
        // Relatively benign bracket around 1, function is monotonous. Fast growth for x>1
        // is still a problem.
        result = solver.solve(0.85, 1.25);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 8 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 9);
        // Less benign bracket around 1 with extremum.
        result = solver.solve(0.8, 1.2);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 8 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 9);
        // Large bracket around 1. Monotonous.
        result = solver.solve(0.85, 1.75);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 10 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 11);
        // Large bracket around 1. Interval contains extremum.
        result = solver.solve(0.55, 1.45);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 7 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 8);
        // Very large bracket around 1 for testing fast growth behaviour.
        result = solver.solve(0.85, 5);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 12 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 13);
        // Secant solver.
        solver = new SecantSolver(f);
        result = solver.solve(-0.2, 0.2);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        // 1 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 2);
        result = solver.solve(-0.1, 0.3);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        // 5 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 6);
        result = solver.solve(-0.3, 0.45);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        // 6 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 7);
        result = solver.solve(0.3, 0.7);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 7 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 8);
        result = solver.solve(0.2, 0.6);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 6 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 7);
        result = solver.solve(0.05, 0.95);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 8 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 9);
        result = solver.solve(0.85, 1.25);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 10 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 11);
        result = solver.solve(0.8, 1.2);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 8 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 9);
        result = solver.solve(0.85, 1.75);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 14 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 15);
        // The followig is especially slow because the solver first has to reduce
        // the bracket to exclude the extremum. After that, convergence is rapide.
        result = solver.solve(0.55, 1.45);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 7 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 8);
        result = solver.solve(0.85, 5);
        System.out.println(
            "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 14 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 15);
        // Static solve method
        result = UnivariateRealSolverUtils.solve(f, -0.2, 0.2);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        result = UnivariateRealSolverUtils.solve(f, -0.1, 0.3);
        Assert.assertEquals(result, 0, 1E-8);
        result = UnivariateRealSolverUtils.solve(f, -0.3, 0.45);
        Assert.assertEquals(result, 0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.3, 0.7);
        Assert.assertEquals(result, 0.5, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.2, 0.6);
        Assert.assertEquals(result, 0.5, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.05, 0.95);
        Assert.assertEquals(result, 0.5, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.85, 1.25);
        Assert.assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.8, 1.2);
        Assert.assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.85, 1.75);
        Assert.assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.55, 1.45);
        Assert.assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.85, 5);
        Assert.assertEquals(result, 1.0, 1E-6);
    }
}
