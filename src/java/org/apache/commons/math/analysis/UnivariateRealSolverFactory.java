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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.math.MathConfigurationException;
import org.apache.commons.math.MathException;

/**
 * A factory to easily get a default solver and some convenience
 * functions.
 * Because solvers are easily reusable, the factory does not
 * store configuration data and creates preconfigured solvers
 * (this may be controversial, because the configuration data
 * may also be used for the default solver used by the static
 * solve() method). 
 * @version $Revision: 1.3 $ $Date: 2003/07/11 15:59:14 $
 */
public class UnivariateRealSolverFactory {
    /**
     * Default constructor.
     */
    private UnivariateRealSolverFactory() {
    }

    /**
     * Create a new {@link UnivariateRealSolver} for the given function.  The
     * actual solver returned can be controlled by defining the
     * <code>org.apache.commons.math.analysis.UnivariateRealSolver</code>
     * property on the JVM command-line (<code>
     * -Dorg.apache.commons.math.analysis.UnivariateRealSolver=
     * <i>class name</i></code>).  The value of the property should be any,
     * fully qualified class name for a type that implements the
     * {@link UnivariateRealSolver} interface.  By default, an instance of
     * {@link BrentSolver} is returned.
     * @param f the function.
     * @return the new solver.
     * @throws MathConfigurationException if a
     */
    public static UnivariateRealSolver newSolver(UnivariateRealFunction f)
        throws MathConfigurationException {
        String solverClassName =
            System.getProperty(
                "org.apache.commons.math.analysis.UnivariateRealSolver",
                "org.apache.commons.math.analysis.BrentSolver");
        try {
            Class clazz = Class.forName(solverClassName);
            Class paramClass[] = new Class[1];
            paramClass[0] = UnivariateRealFunction.class;
            Object param[] = new Object[1];
            param[0] = f;
            return (UnivariateRealSolver)clazz.getConstructor(
                paramClass).newInstance(
                param);
        } catch (IllegalArgumentException e) {
            throw new MathConfigurationException(e);
        } catch (SecurityException e) {
            throw new MathConfigurationException(
                "Can't access " + solverClassName,
                e);
        } catch (ClassNotFoundException e) {
            throw new MathConfigurationException(
                "Class not found: " + solverClassName,
                e);
        } catch (InstantiationException e) {
            throw new MathConfigurationException(
                "Can't instantiate " + solverClassName,
                e);
        } catch (IllegalAccessException e) {
            throw new MathConfigurationException(
                "Can't access " + solverClassName,
                e);
        } catch (InvocationTargetException e) {
            throw new MathConfigurationException(e);
        } catch (NoSuchMethodException e) {
            throw new MathConfigurationException(
                "No constructor with UnivariateRealFunction in " +
                solverClassName,
                e);
        }
    }

    /**
     * Convience method to solve for zeros of real univariate functions.  A
     * default solver is created and used for solving. 
     * @param f the function.
     * @param x0 the lower bound for the interval.
     * @param x1 the upper bound for the interval.
     * @return a value where the function is zero.
     * @throws MathException if the iteration count was exceeded or the
     *         solver detects convergence problems otherwise.
     */
    public static double solve(UnivariateRealFunction f, double x0, double x1)
        throws MathException {
        return newSolver(f).solve(x0, x1);
    }

    /**
     * Convience method to solve for zeros of real univariate functions.  A
     * default solver is created and used for solving. 
     * @param f the function.
     * @param x0 the lower bound for the interval.
     * @param x1 the upper bound for the interval.
     * @param absoluteAccuracy the accuracy to be used by the solver.
     * @return a value where the function is zero.
     * @throws MathException if the iteration count was exceeded or the
     *         solver detects convergence problems otherwise.
     */
    public static double solve(
        UnivariateRealFunction f,
        double x0,
        double x1,
        double absoluteAccuracy)
        throws MathException {
        UnivariateRealSolver solver = newSolver(f);
        solver.setAbsoluteAccuracy(absoluteAccuracy);
        return solver.solve(x0, x1);
    }
}
