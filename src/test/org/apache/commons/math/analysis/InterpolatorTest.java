/*
 * 
 * Copyright (c) 2004 The Apache Software Foundation. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *  
 */
package org.apache.commons.math.analysis;

import org.apache.commons.math.MathException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test the interpolation framework.
 *
 * @version $Revision: 1.12 $ $Date: 2004/02/17 04:33:16 $ 
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
        //System.out.println(" deg 2 seg");
        double xval[] = { 0.0, 0.5, 1.0 };
        double yval[] = { 0.0, 0.5, 1.0 };
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(xval, yval);
        
        /*  todo: rewrite using assertions
        
        double x;
        x = 0.0;
        System.out.println(
            "x="
               + x
              + " y="
               + f.value(x));

        x = 0.5;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        x = 1 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));
    */
    }

    public void testInterpolateLinearDegenerateThreeSegment()
        throws MathException {
//       System.out.println(" deg 3 seg");
        double xval[] = { 0.0, 0.5, 1.0, 1.5 };
        double yval[] = { 0.0, 0.5, 1.0, 1.5 };
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(xval, yval);
        /* todo: rewrite with assertions
        double x;
        x = 0.0;
        System.out.println(
                "x="
                + x
                + " y="
                + f.value(x));

        x = 0.5;
        System.out.println(
                "x="
                + x
                + " y="
                + f.value(x));

        x = 1 - 1E-6;
        System.out.println(
                "x="
                + x
                + " y="
                + f.value(x));
    */

    }

    public void testInterpolateLinear() throws MathException {
       // System.out.println(" triang 2 seg");
        double xval[] = { 0.0, 0.5, 1.0 };
        double yval[] = { 0.0, 0.5, 0.0 };
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(xval, yval);
        /* todo: rewrite with assertions
        double x;
        x = 0.0;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        x = 0.5 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        x = 0.5;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        x = 1 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));
*/
    }
    
    public void testInterpolateSin() throws MathException {
        //System.out.println(" sin");
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

        //System.out.println("n=" + xval.length);
        UnivariateRealInterpolator i = new SplineInterpolator();
        UnivariateRealFunction f = i.interpolate(xval, yval);
        
        /* todo: rewrite using assertions
        
        double x;
        x = 0.0;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        x = Math.PI / 6.0 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        x = Math.PI / 6.0 + 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        x = Math.PI / 2 - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        x = Math.PI / 2 + 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        x = Math.PI - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        x = Math.PI + 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        x = 2.0 * Math.PI - 1E-6;
        System.out.println(
            "x="
                + x
                + " y="
                + f.value(x));

        //assertEquals(0.5,f.value(Math.PI/6.0),)
         
  */
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
