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

import java.io.Serializable;


/**
 * A factory to easily get a default solver and some convenience
 * functions.
 * Because solvers are easily reusable, the factory does not
 * store configuration data and creates preconfigured solvers
 * (this may be controversial, because the configuration data
 * may also be used for the default solver used by the static
 * solve() method). 
 * @version $Revision: 1.9 $ $Date: 2004/02/21 21:35:14 $
 */
public class UnivariateRealSolverFactoryImpl
    extends UnivariateRealSolverFactory implements Serializable {
        
    /**
     * Default constructor.
     */
    public UnivariateRealSolverFactoryImpl() {
    }

    /**
     * Create a new {@link UnivariateRealSolver} for the given function.  The
     * actual solver returned is determined by the underlying factory.
     * 
     * This factory returns a {@link BrentSolver} instance.
     *
     * @param f the function.
     * @return the new solver.
     */
    public UnivariateRealSolver newDefaultSolver(UnivariateRealFunction f) {
        return newBrentSolver(f);
    }
    
    /**
     * Create a new {@link UnivariateRealSolver} for the given function.  The
     * solver is an implementation of the bisection method.
     * @param f the function.
     * @return the new solver.
     */
    public UnivariateRealSolver newBisectionSolver(UnivariateRealFunction f) {
        return new BisectionSolver(f);
    }

    /**
     * Create a new {@link UnivariateRealSolver} for the given function.  The
     * solver is an implementation of the Brent method.
     * @param f the function.
     * @return the new solver.
     */
    public UnivariateRealSolver newBrentSolver(UnivariateRealFunction f) {
        return new BrentSolver(f);
    }

    /**
     * Create a new {@link UnivariateRealSolver} for the given function.  The
     * solver is an implementation of the secant method.
     * @param f the function.
     * @return the new solver.
     */
    public UnivariateRealSolver newSecantSolver(UnivariateRealFunction f) {
        return new SecantSolver(f);
    }
}
