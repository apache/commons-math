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

package org.apache.commons.math.util;

import java.math.BigDecimal;

import org.apache.commons.math.MathException;
import junit.framework.TestCase;

/**
 * @version $Revision: 1.11 $ $Date: 2004/05/23 00:33:41 $
 */
public class DefaultTransformerTest extends TestCase {
    /**
     * 
     */
    public void testTransformDouble(){
        double expected = 1.0;
        Double input = new Double(expected);
        DefaultTransformer t = new DefaultTransformer();
        try {
			assertEquals(expected, t.transform(input), 1.0e-4);
		} catch (MathException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 
     */
    public void testTransformNull(){
        DefaultTransformer t = new DefaultTransformer();
        try {
			t.transform(null);
			fail("Expection MathException");
		} catch (MathException e) {
			// expected
		}
    }
    
    /**
     * 
     */
    public void testTransformInteger(){
        double expected = 1.0;
        Integer input = new Integer(1);
        DefaultTransformer t = new DefaultTransformer();
        try {
			assertEquals(expected, t.transform(input), 1.0e-4);
		} catch (MathException e) {
			e.printStackTrace();
		}
    }        
    
    /**
     * 
     */
    public void testTransformBigDecimal(){
        double expected = 1.0;
        BigDecimal input = new BigDecimal("1.0");
        DefaultTransformer t = new DefaultTransformer();
        try {
			assertEquals(expected, t.transform(input), 1.0e-4);
		} catch (MathException e) {
			e.printStackTrace();
		}
    }        
    
    /**
     * 
     */
    public void testTransformString(){
        double expected = 1.0;
        String input = "1.0";
        DefaultTransformer t = new DefaultTransformer();
        try {
			assertEquals(expected, t.transform(input), 1.0e-4);
		} catch (MathException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 
     */
    public void testTransformObject(){
        Boolean input = Boolean.TRUE;
        DefaultTransformer t = new DefaultTransformer();
        try {
			t.transform(input);
			fail("Expecting MathException");
		} catch (MathException e) {
		    // expected
		}
    }
}
