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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test the interpolation framework.
 *
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:09:07 $ 
 */
public class InterpolatorTest extends TestCase {

    public InterpolatorTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(InterpolatorTest.class);
        suite.setName("UnivariateRealInterpolator Tests");
        return suite;
    }

    public void testInterpolateLinearDegenerateTwoSegment()
        throws MathException {
        System.out.println(" deg 2 seg");
        double xval[] = { 0.0, 0.5, 1.0 };
        double yval[] = { 0.0, 0.5, 1.0 };
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(xval, yval);
        double x;
        x = 0.0;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = 0.5;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = 1 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
    }

    public void testInterpolateLinearDegenerateThreeSegment()
        throws MathException {
        System.out.println(" deg 3 seg");
        double xval[] = { 0.0, 0.5, 1.0, 1.5 };
        double yval[] = { 0.0, 0.5, 1.0, 1.5 };
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(xval, yval);
        double x;
        x = 0.0;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = 0.5 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = 0.5;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = 1 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = 1;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = 1.5 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
    }

    public void testInterpolateLinear() throws MathException {
        System.out.println(" triang 2 seg");
        double xval[] = { 0.0, 0.5, 1.0 };
        double yval[] = { 0.0, 0.5, 0.0 };
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(xval, yval);
        double x;
        x = 0.0;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = 0.5 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = 0.5;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = 1 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
    }

    public void testInterpolateSin() throws MathException {
        System.out.println(" sin");
        double xval[] =
            {
                0.0,
                Math.PI / 6.0,
                Math.PI / 2.0,
                5.0 * Math.PI / 6.0,
                Math.PI,
                7.0 * Math.PI / 6.0,
                3.0 * Math.PI / 2.0,
                11.0 * Math.PI / 6.0,
                2.0 * Math.PI };
        double yval[] = { 0.0, 0.5, 1.0, 0.5, 0.0, -0.5, -1.0, -0.5, 0.0 };

        System.out.println("n=" + xval.length);
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(xval, yval);
        double x;
        x = 0.0;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = Math.PI / 6.0 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = Math.PI / 6.0 + 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = Math.PI / 2 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = Math.PI / 2 + 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = Math.PI - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = Math.PI + 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        x = 2.0 * Math.PI - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x)
                + " y'="
                + f.firstDerivative(x)
                + " y''="
                + f.secondDerivative(x));
        //assertEquals(0.5,f.value(Math.PI/6.0),)
    }

    public void testIllegalArguments() throws MathException {
        // Data set arrays of different size.
        UnivariateRealInterpolator i = new SplineInterpolator();
        try {
            double xval[] = { 0.0, 1.0 };
            double yval[] = { 0.0, 1.0, 2.0 };
            i.interpolate(xval, yval);
            fail("Failed to detect data set array with different sizes.");
        } catch (IllegalArgumentException iae) {
        }
        // X values not sorted.
        try {
            double xval[] = { 0.0, 1.0, 0.5 };
            double yval[] = { 0.0, 1.0, 2.0 };
            i.interpolate(xval, yval);
            fail("Failed to detect unsorted arguments.");
        } catch (IllegalArgumentException iae) {
        }
    }
}
