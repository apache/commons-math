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
package org.apache.commons.math.stat;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.util.NumberTransformer;
import org.apache.commons.math.util.TransformerMap;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for the {@link Univariate} class.
 *
 * @version $Revision: 1.2 $ $Date: 2003/10/13 08:08:38 $
 */

public final class MixedListUnivariateImplTest extends TestCase {
    private double one = 1;
    private float two = 2;
    private int three = 3;

    private double mean = 2;
    private double sumSq = 18;
    private double sum = 8;
    private double var = 0.666666666666666666667;
    private double std = Math.sqrt(var);
    private double n = 4;
    private double min = 1;
    private double max = 3;
    private double skewness = 0;
    private double kurtosis = 0.5;
    private int kClass = StoreUnivariate.LEPTOKURTIC;
    private double tolerance = 10E-15;

    private TransformerMap transformers = new TransformerMap();
    
    public MixedListUnivariateImplTest(String name) {
        super(name);
        transformers = new TransformerMap();

        transformers.putTransformer(Foo.class, new NumberTransformer() {
            public double transform(Object o) {
                return Double.parseDouble(((Foo) o).heresFoo());
            }
        });

        transformers.putTransformer(Bar.class, new NumberTransformer() {
            public double transform(Object o) {
                return Double.parseDouble(((Bar) o).heresBar());
            }

        });

    }

    public void setUp() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(MixedListUnivariateImplTest.class);
        suite.setName("Mixed List Tests");
        return suite;
    }

    /** test stats */
    public void testStats() {
        List externalList = new ArrayList();

        StoreUnivariate u = new ListUnivariateImpl(externalList,transformers);

        assertEquals("total count", 0, u.getN(), tolerance);
        u.addValue(one);
        u.addValue(two);
        u.addValue(two);
        u.addValue(three);
        assertEquals("N", n, u.getN(), tolerance);
        assertEquals("sum", sum, u.getSum(), tolerance);
        assertEquals("sumsq", sumSq, u.getSumsq(), tolerance);
        assertEquals("var", var, u.getVariance(), tolerance);
        assertEquals("std", std, u.getStandardDeviation(), tolerance);
        assertEquals("mean", mean, u.getMean(), tolerance);
        assertEquals("min", min, u.getMin(), tolerance);
        assertEquals("max", max, u.getMax(), tolerance);
        u.clear();
        assertEquals("total count", 0, u.getN(), tolerance);
    }

    public void testN0andN1Conditions() throws Exception {
        List list = new ArrayList();

        StoreUnivariate u = new ListUnivariateImpl(new ArrayList(),transformers);

        assertTrue(
            "Mean of n = 0 set should be NaN",
            Double.isNaN(u.getMean()));
        assertTrue(
            "Standard Deviation of n = 0 set should be NaN",
            Double.isNaN(u.getStandardDeviation()));
        assertTrue(
            "Variance of n = 0 set should be NaN",
            Double.isNaN(u.getVariance()));

        u.addValue(one);

        assertTrue(
            "Mean of n = 1 set should be value of single item n1, instead it is " + u.getMean() ,
            u.getMean() == one);
            
        assertTrue(
            "StdDev of n = 1 set should be zero, instead it is: "
                + u.getStandardDeviation(),
            u.getStandardDeviation() == 0);
        assertTrue(
            "Variance of n = 1 set should be zero",
            u.getVariance() == 0);
    }

    public void testSkewAndKurtosis() {
        ListUnivariateImpl u =
            new ListUnivariateImpl(new ArrayList(), transformers);

        u.addObject("12.5");
        u.addObject(new Integer(12));
        u.addObject("11.8");
        u.addObject("14.2");
        u.addObject(new Foo());
        u.addObject("14.5");
        u.addObject(new Long(21));
        u.addObject("8.2");
        u.addObject("10.3");
        u.addObject("11.3");
        u.addObject(new Float(14.1));
        u.addObject("9.9");
        u.addObject("12.2");
        u.addObject(new Bar());
        u.addObject("12.1");
        u.addObject("11");
        u.addObject(new Double(19.8));
        u.addObject("11");
        u.addObject("10");
        u.addObject("8.8");
        u.addObject("9");
        u.addObject("12.3");


        assertEquals("mean", 12.40455, u.getMean(), 0.0001);
        assertEquals("variance", 10.00236, u.getVariance(), 0.0001);
        assertEquals("skewness", 1.437424, u.getSkewness(), 0.0001);
        assertEquals("kurtosis", 2.37719, u.getKurtosis(), 0.0001);
    }

    public void testProductAndGeometricMean() throws Exception {
        ListUnivariateImpl u = new ListUnivariateImpl(new ArrayList(),transformers);
        u.setWindowSize(10);

        u.addValue(1.0);
        u.addValue(2.0);
        u.addValue(3.0);
        u.addValue(4.0);

        assertEquals(
            "Geometric mean not expected",
            2.213364,
            u.getGeometricMean(),
            0.00001);

        // Now test rolling - UnivariateImpl should discount the contribution
        // of a discarded element
        for (int i = 0; i < 10; i++) {
            u.addValue(i + 2);
        }
        // Values should be (2,3,4,5,6,7,8,9,10,11)
        assertEquals(
            "Geometric mean not expected",
            5.755931,
            u.getGeometricMean(),
            0.00001);

    }

    public final class Foo {
        public String heresFoo() {
            return "14.9";
        }
    }

    public final class Bar {
        public String heresBar() {
            return "12.0";
        }
    }
}
