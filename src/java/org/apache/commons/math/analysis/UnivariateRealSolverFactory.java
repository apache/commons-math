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
 * A factory to easily get a default solver and some convenience
 * functions.
 * Because solvers are easily reusable, the factory does not
 * store configuration data and creates preconfigured solvers
 * (this may be controversial, because the configuration data
 * may also be used for the default solver used by the static
 * solve() method). 
 * @version $Revision: 1.12 $ $Date: 2004/02/21 21:35:14 $
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
        } catch(Exception ex) {
            // ignore as default implementation will be used.
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
     * solver is an implementation of the secant method.
     * @param f the function.
     * @return the new solver.
     */
    public abstract UnivariateRealSolver newSecantSolver(
        UnivariateRealFunction f);
}
