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

/**
 * Provide an interface univariate real functions.
 * The object may held temporary data which is shared between calculations
 * of the value and the derivatives for the same argument. It is not guaranteed
 * that derivatives are evaluated after the value, the evaluation algorithm
 * should throw an InvalidStateException if it can't cope with this.
 *  
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:09:31 $
 */
public interface UnivariateRealFunction {
    /**
     * Compute the value for the function.
     * @param x the point for which the function value should be computed
     * @return the value
     * @throws MathException if the function couldn't be computed due to
     *  missing additional data or other environmental problems.
     */
    public double value(double x) throws MathException;

    /**
     * Compute the value for the first derivative of the function.
     * It is recommended to provide this method only if the first derivative is
     * analytical. Numerical derivatives may be acceptable in some cases.
     * An implementation should throw an UnsupportedOperationException if
     * this method is not implemented.
     * @param x the point for which the first derivative should be computed
     * @return the value
     * @throws MathException if the derivative couldn't be computed.
     */
    public double firstDerivative(double x) throws MathException;

    /**
     * Compute the value for the second derivative of the function.
     * It is recommended to provide this method only if the second derivative is
     * analytical. Numerical derivatives may be acceptable in some cases.
     * An implementation should throw an UnsupportedOperationException if
     * this method is not implemented.
     * @param x the point for which the first derivative should be computed
     * @return the value
     * @throws MathException if the second derivative couldn't be computed.
     */
    public double secondDerivative(double x) throws MathException;
}
