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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;

import org.apache.commons.math.complex.Complex;

/**
 * @version $Revision: 1.14 $ $Date: 2004/06/17 21:41:56 $
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
     * 
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
        
        try {
            
            // serialize the Object
            tmp = File.createTempFile("test",".ser");
            FileOutputStream fo = new FileOutputStream(tmp);
            ObjectOutputStream so = new ObjectOutputStream(fo);
            so.writeObject(o);
            so.flush();

            // deserialize the Book
            FileInputStream fi = new FileInputStream(tmp);
            ObjectInputStream si = new ObjectInputStream(fi);  
            result = si.readObject();
            
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(tmp != null) tmp.delete();
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
}
