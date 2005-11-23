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

package org.apache.commons.math;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;

import org.apache.commons.math.complex.Complex;

/**
 * @version $Revision$ $Date$
 */
public class TestUtils {
    /**
     * 
     */
    private TestUtils() {
        super();
    }

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
    
    /*
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
    public static void assertEquals(double a[], double b[], double tolerance) {
        Assert.assertEquals(a.length, b.length);
        for (int i = 0; i < a.length; i++) {
            Assert.assertEquals(a[i], b[i], tolerance);
        }
    }
    
    public static Object serializeAndRecover(Object o){
        
        Object result = null;
        
        File tmp = null;
        FileOutputStream fo = null;
        FileInputStream fi = null;
        
        try {
            // serialize the Object
            tmp = File.createTempFile("test",".ser");
            fo = new FileOutputStream(tmp);
            ObjectOutputStream so = new ObjectOutputStream(fo);
            so.writeObject(o);
            so.flush();
            fo.close();

            // deserialize the Book
            fi = new FileInputStream(tmp);
            ObjectInputStream si = new ObjectInputStream(fi);  
            result = si.readObject();
        } catch (Exception ex) {
        	
        } finally {
        	if (fo != null) {
        		try {
        			fo.close();
        		} catch (IOException ex) {
        		}
        	}

        	if (fi != null) {
        		try {
            		fi.close();
        		} catch (IOException ex) {
        		}
        	}
        }
        
        
        if (tmp != null) {
        	tmp.delete();
        }
        
        return result;
    }
    
    /**
     * Verifies that serialization preserves equals and hashCode
     * 
     * @param object
     */
    public static void checkSerializedEquality(Object object) {
        Object object2 = serializeAndRecover(object);
        Assert.assertEquals("Equals check", object, object2);
        Assert.assertEquals("HashCode check", object.hashCode(), object2.hashCode());
    }

	public static void assertRelativelyEquals(double expected, double actual, double relativeError) {
		assertRelativelyEquals(null, expected, actual, relativeError);
	}
	
	public static void assertRelativelyEquals(String msg, double expected, double actual, double relativeError) {
        if (Double.isNaN(expected)) {
            Assert.assertTrue(msg, Double.isNaN(actual));
        } else if (Double.isNaN(actual)) {
        	Assert.assertTrue(msg, Double.isNaN(expected));
        } else if (Double.isInfinite(actual) || Double.isInfinite(expected)) {
            Assert.assertEquals(expected, actual, relativeError);
        } else if (expected == 0.0) {
            Assert.assertEquals(msg, actual, expected, relativeError);
        } else {
            double x = Math.abs((expected - actual) / expected);
            Assert.assertEquals(msg, 0.0, x, relativeError);
        }
	}
}
