/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.analysis;

import org.apache.commons.discovery.tools.DiscoverClass;

/**
 * Abstract factory class used to create {@link UnivariateRealSolver} instances.
 * <p>
 * Solvers implementing the following algorithms are supported:
 * <ul>
 * <li>Bisection</li>
 * <li>Brent's method</li>
 * <li>Secant method</li>
 * </ul>
 * Concrete factories extending this class also specify a default solver, instances of which
 * are returned by <code>newDefaultSolver()</code>.
 * <p>
 * Common usage:<pre>
 * SolverFactory factory = UnivariateRealSolverFactory.newInstance();
 *
 * // create a Brent solver to use with a UnivariateRealFunction f
 * BrentSolver solver = factory.newBrentSolver(f);
 * </pre>
 *
 * <a href="http://jakarta.apache.org/commons/discovery/">Jakarta Commons Discovery</a>
 * is used to determine the concrete factory returned by 
 * <code>UnivariateRealSolverFactory.newInstance().</code>  The default is
 * {@link UnivariateRealSolverFactoryImpl}.
 *
 * @version $Revision$ $Date$
 */
public abstract class UnivariateRealSolverFactory {
    /**
     * Default constructor.
     */
    protected UnivariateRealSolverFactory() {
    }

    /**
     * Create a new factory.
     * @return a new factory.
     */
    public static UnivariateRealSolverFactory newInstance() {
        UnivariateRealSolverFactory factory = null;
        try {
            DiscoverClass dc = new DiscoverClass();
            factory = (UnivariateRealSolverFactory) dc.newInstance(
                UnivariateRealSolverFactory.class,
                "org.apache.commons.math.analysis.UnivariateRealSolverFactoryImpl");
        } catch(Throwable t) {
            return new UnivariateRealSolverFactoryImpl();
        }
        return factory;
    }
    
    /**
     * Create a new {@link UnivariateRealSolver} for the given function.  The
     * actual solver returned is determined by the underlying factory.
     * @param f the function.
     * @return the new solver.
     */
    public abstract UnivariateRealSolver newDefaultSolver(
        UnivariateRealFunction f);
    
    /**
     * Create a new {@link UnivariateRealSolver} for the given function.  The
     * solver is an implementation of the bisection method.
     * @param f the function.
     * @return the new solver.
     */
    public abstract UnivariateRealSolver newBisectionSolver(
        UnivariateRealFunction f);
    
    /**
     * Create a new {@link UnivariateRealSolver} for the given function.  The
     * solver is an implementation of the Brent method.
     * @param f the function.
     * @return the new solver.
     */
    public abstract UnivariateRealSolver newBrentSolver(
        UnivariateRealFunction f);
    
    /**
     * Create a new {@link UnivariateRealSolver} for the given function.  The
     * solver is an implementation of Newton's Method.
     * @param f the function.
     * @return the new solver.
     */
    public abstract UnivariateRealSolver newNewtonSolver(
        DifferentiableUnivariateRealFunction f);
    
    /**
     * Create a new {@link UnivariateRealSolver} for the given function.  The
     * solver is an implementation of the secant method.
     * @param f the function.
     * @return the new solver.
     */
    public abstract UnivariateRealSolver newSecantSolver(
        UnivariateRealFunction f);
}
