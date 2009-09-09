/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.complex.ComplexFormat;
import org.apache.commons.math.linear.FieldMatrix;
import org.apache.commons.math.linear.RealMatrix;

/**
 * @version $Revision$ $Date$
 */
public class TestUtils {
    /**
     * Collection of static methods used in math unit tests.
     */
    private TestUtils() {
        super();
    }

    /**
     * Verifies that expected and actual are within delta, or are both NaN or
     * infinities of the same sign.
     */
    public static void assertEquals(double expected, double actual, double delta) {
        assertEquals(null, expected, actual, delta);
    }

    /**
     * Verifies that expected and actual are within delta, or are both NaN or
     * infinities of the same sign.
     */
    public static void assertEquals(String msg, double expected, double actual, double delta) {
        // check for NaN
        if(Double.isNaN(expected)){
            Assert.assertTrue("" + actual + " is not NaN.",
                Double.isNaN(actual));
        } else {
            Assert.assertEquals(msg, expected, actual, delta);
        }
    }

    /**
     * Verifies that the two arguments are exactly the same, either
     * both NaN or infinities of same sign, or identical floating point values.
     */
    public static void assertSame(double expected, double actual) {
     assertEquals(expected, actual, 0);
    }

    /**
     * Verifies that real and imaginary parts of the two complex arguments
     * are exactly the same.  Also ensures that NaN / infinite components match.
     */
    public static void assertSame(Complex expected, Complex actual) {
        assertSame(expected.getReal(), actual.getReal());
        assertSame(expected.getImaginary(), actual.getImaginary());
    }

    /**
     * Verifies that real and imaginary parts of the two complex arguments
     * differ by at most delta.  Also ensures that NaN / infinite components match.
     */
    public static void assertEquals(Complex expected, Complex actual, double delta) {
        assertEquals(expected.getReal(), actual.getReal(), delta);
        assertEquals(expected.getImaginary(), actual.getImaginary(), delta);
    }

    /**
     * Verifies that two double arrays have equal entries, up to tolerance
     */
    public static void assertEquals(double expected[], double observed[], double tolerance) {
        assertEquals("Array comparison failure", expected, observed, tolerance);
    }

    /**
     * Serializes an object to a bytes array and then recovers the object from the bytes array.
     * Returns the deserialized object.
     *
     * @param o  object to serialize and recover
     * @return  the recovered, deserialized object
     */
    public static Object serializeAndRecover(Object o) {
        try {
            // serialize the Object
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bos);
            so.writeObject(o);

            // deserialize the Object
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream si = new ObjectInputStream(bis);
            return si.readObject();
        } catch (IOException ioe) {
            return null;
        } catch (ClassNotFoundException cnfe) {
            return null;
        }
    }

    /**
     * Verifies that serialization preserves equals and hashCode.
     * Serializes the object, then recovers it and checks equals and hash code.
     *
     * @param object  the object to serialize and recover
     */
    public static void checkSerializedEquality(Object object) {
        Object object2 = serializeAndRecover(object);
        Assert.assertEquals("Equals check", object, object2);
        Assert.assertEquals("HashCode check", object.hashCode(), object2.hashCode());
    }

    /**
     * Verifies that the relative error in actual vs. expected is less than or
     * equal to relativeError.  If expected is infinite or NaN, actual must be
     * the same (NaN or infinity of the same sign).
     *
     * @param expected expected value
     * @param actual  observed value
     * @param relativeError  maximum allowable relative error
     */
    public static void assertRelativelyEquals(double expected, double actual,
            double relativeError) {
        assertRelativelyEquals(null, expected, actual, relativeError);
    }

    /**
     * Verifies that the relative error in actual vs. expected is less than or
     * equal to relativeError.  If expected is infinite or NaN, actual must be
     * the same (NaN or infinity of the same sign).
     *
     * @param msg  message to return with failure
     * @param expected expected value
     * @param actual  observed value
     * @param relativeError  maximum allowable relative error
     */
    public static void assertRelativelyEquals(String msg, double expected,
            double actual, double relativeError) {
        if (Double.isNaN(expected)) {
            Assert.assertTrue(msg, Double.isNaN(actual));
        } else if (Double.isNaN(actual)) {
            Assert.assertTrue(msg, Double.isNaN(expected));
        } else if (Double.isInfinite(actual) || Double.isInfinite(expected)) {
            Assert.assertEquals(expected, actual, relativeError);
        } else if (expected == 0.0) {
            Assert.assertEquals(msg, actual, expected, relativeError);
        } else {
            double absError = Math.abs(expected) * relativeError;
            Assert.assertEquals(msg, expected, actual, absError);
        }
    }

    /**
     * Fails iff values does not contain a number within epsilon of z.
     *
     * @param msg  message to return with failure
     * @param values complex array to search
     * @param z  value sought
     * @param epsilon  tolerance
     */
    public static void assertContains(String msg, Complex[] values,
            Complex z, double epsilon) {
        int i = 0;
        boolean found = false;
        while (!found && i < values.length) {
            try {
                assertEquals(values[i], z, epsilon);
                found = true;
            } catch (AssertionFailedError er) {
                // no match
            }
            i++;
        }
        if (!found) {
            Assert.fail(msg +
                " Unable to find " + ComplexFormat.formatComplex(z));
        }
    }

    /**
     * Fails iff values does not contain a number within epsilon of z.
     *
     * @param values complex array to search
     * @param z  value sought
     * @param epsilon  tolerance
     */
    public static void assertContains(Complex[] values,
            Complex z, double epsilon) {
        assertContains(null, values, z, epsilon);
    }

    /**
     * Fails iff values does not contain a number within epsilon of x.
     *
     * @param msg  message to return with failure
     * @param values double array to search
     * @param x value sought
     * @param epsilon  tolerance
     */
    public static void assertContains(String msg, double[] values,
            double x, double epsilon) {
        int i = 0;
        boolean found = false;
        while (!found && i < values.length) {
            try {
                assertEquals(values[i], x, epsilon);
                found = true;
            } catch (AssertionFailedError er) {
                // no match
            }
            i++;
        }
        if (!found) {
            Assert.fail(msg + " Unable to find" + x);
        }
    }

    /**
     * Fails iff values does not contain a number within epsilon of x.
     *
     * @param values double array to search
     * @param x value sought
     * @param epsilon  tolerance
     */
    public static void assertContains(double[] values, double x,
            double epsilon) {
       assertContains(null, values, x, epsilon);
    }

    /** verifies that two matrices are close (1-norm) */
    public static void assertEquals(String msg, RealMatrix expected, RealMatrix observed,
        double tolerance) {

        if (observed == null) {
            Assert.fail(msg + "\nObserved is null");
        }

        if (expected.getColumnDimension() != observed.getColumnDimension() ||
                expected.getRowDimension() != observed.getRowDimension()) {
            StringBuffer messageBuffer = new StringBuffer(msg);
            messageBuffer.append("\nObserved has incorrect dimensions.");
            messageBuffer.append("\nobserved is " + observed.getRowDimension() +
                    " x " + observed.getColumnDimension());
            messageBuffer.append("\nexpected " + expected.getRowDimension() +
                    " x " + expected.getColumnDimension());
            Assert.fail(messageBuffer.toString());
        }

        RealMatrix delta = expected.subtract(observed);
        if (delta.getNorm() >= tolerance) {
            StringBuffer messageBuffer = new StringBuffer(msg);
            messageBuffer.append("\nExpected: " + expected);
            messageBuffer.append("\nObserved: " + observed);
            messageBuffer.append("\nexpected - observed: " + delta);
            Assert.fail(messageBuffer.toString());
        }
    }

    /** verifies that two matrices are equal */
    public static void assertEquals(FieldMatrix<? extends FieldElement<?>> expected,
                                    FieldMatrix<? extends FieldElement<?>> observed) {

        if (observed == null) {
            Assert.fail("Observed is null");
        }

        if (expected.getColumnDimension() != observed.getColumnDimension() ||
                expected.getRowDimension() != observed.getRowDimension()) {
            StringBuffer messageBuffer = new StringBuffer();
            messageBuffer.append("Observed has incorrect dimensions.");
            messageBuffer.append("\nobserved is " + observed.getRowDimension() +
                    " x " + observed.getColumnDimension());
            messageBuffer.append("\nexpected " + expected.getRowDimension() +
                    " x " + expected.getColumnDimension());
            Assert.fail(messageBuffer.toString());
        }

        for (int i = 0; i < expected.getRowDimension(); ++i) {
            for (int j = 0; j < expected.getColumnDimension(); ++j) {
                FieldElement<?> eij = expected.getEntry(i, j);
                FieldElement<?> oij = observed.getEntry(i, j);
                Assert.assertEquals(eij, oij);
            }
        }
    }

    /** verifies that two arrays are close (sup norm) */
    public static void assertEquals(String msg, double[] expected, double[] observed,
        double tolerance) {
        StringBuffer out = new StringBuffer(msg);
        if (expected.length != observed.length) {
            out.append("\n Arrays not same length. \n");
            out.append("expected has length ");
            out.append(expected.length);
            out.append(" observed length = ");
            out.append(observed.length);
            Assert.fail(out.toString());
        }
        boolean failure = false;
        for (int i=0; i < expected.length; i++) {
            try {
                assertEquals(expected[i], observed[i], tolerance);
            } catch (AssertionFailedError ex) {
                failure = true;
                out.append("\n Elements at index ");
                out.append(i);
                out.append(" differ. ");
                out.append(" expected = ");
                out.append(expected[i]);
                out.append(" observed = ");
                out.append(observed[i]);
            }
        }
        if (failure) {
            Assert.fail(out.toString());
        }
    }

    /** verifies that two arrays are equal */
    public static <T extends FieldElement<T>> void assertEquals(T[] m, T[] n) {
        if (m.length != n.length) {
            Assert.fail("vectors not same length");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(m[i],n[i]);
        }
    }

}
