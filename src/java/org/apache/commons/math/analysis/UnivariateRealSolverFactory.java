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

import org.apache.commons.discovery.tools.DiscoverClass;

/**
 * A factory to easily get a default solver and some convenience
 * functions.
 * Because solvers are easily reusable, the factory does not
 * store configuration data and creates preconfigured solvers
 * (this may be controversial, because the configuration data
 * may also be used for the default solver used by the static
 * solve() method). 
 * @version $Revision: 1.7 $ $Date: 2003/10/13 08:09:31 $
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
