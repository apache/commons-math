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

import java.util.Arrays;

import org.apache.commons.math.MathException;

/**
 * Represents a cubic spline function.
 * Spline functions map a certain interval of real numbers to real numbers.
 * A cubic spline consists of segments of cubic functions. For this class,
 * polynominal coefficents are used.
 * Arguments outside of the domain cause an IllegalArgumentException.
 * 
 * @author pietsch at apache.org
 *
 */
public class CubicSplineFunction implements UnivariateRealFunction {
    // Spline segment interval delimiters.
    // Size is N+1 for N segments.
    private double xval[];
    
    // The spline segment's polynominal coefficients.
    // The first index runs over the intervals, size is N.
    // The second index adresses the coefficients in the segment, with
    // index 0 being the absolute coefficient and index 3 the coefficient
    // for the third power.
    // The coefficients are setup so that x runs from 0 to xval[i+1]-xval[i].
    private double c[][];

    public CubicSplineFunction(double xval[],double c[][]) {
        // TODO: should copy the arguments here, for safety. This could be a major overhead.
        this.xval=xval;
        this.c=c;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealFunction#value(double)
     */
    public double value(double x) throws MathException {
        if(x<xval[0]||x>xval[xval.length-1]) {
            throw new IllegalArgumentException("Argument outside domain");
        }
        int i=Arrays.binarySearch(xval,x);
        if(i<0) {
            i=-i-2;
        }
        x=x-xval[i];
        return ((c[i][3]*x+c[i][2])*x+c[i][1])*x+c[i][0];
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealFunction#firstDerivative(double)
     */
    public double firstDerivative(double x) throws MathException {
        if(x<xval[0]||x>xval[xval.length-1]) {
            throw new IllegalArgumentException("Argument outside domain");
        }
        int i=Arrays.binarySearch(xval,x);
        if(i<0) {
            i=-i-2;
        }
        x=x-xval[i];
        return (3*c[i][3]*x+2*c[i][2])*x+c[i][1];
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.UnivariateRealFunction#secondDerivative(double)
     */
    public double secondDerivative(double x) throws MathException {
        if(x<xval[0]||x>xval[xval.length-1]) {
            throw new IllegalArgumentException("Argument outside domain");
        }
        int i=Arrays.binarySearch(xval,x);
        if(i<0) {
            i=-i-2;
        }
        x=x-xval[i];
        return 6*c[i][3]*x+2*c[i][2];
    }

}
