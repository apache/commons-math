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
package org.apache.commons.math4.core.jdkmath;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for scalb.
 */
public class ScalbTest {
    
    @Test
    public void doubleSpecial() {
        
        double nonCanonicalNaN = Double.longBitsToDouble(0x7ff8000000000001L);
        
        for(int scaleFactor = Double.MIN_EXPONENT * 2; scaleFactor <= (Double.MAX_EXPONENT - Double.MIN_EXPONENT); scaleFactor++) {            
            assertBitPattern(StrictMath.scalb(Double.NaN, scaleFactor), AccurateMath.scalb(Double.NaN, scaleFactor));
            assertBitPattern(StrictMath.scalb(nonCanonicalNaN, scaleFactor), AccurateMath.scalb(nonCanonicalNaN, scaleFactor));
            assertBitPattern(StrictMath.scalb(Double.POSITIVE_INFINITY, scaleFactor), AccurateMath.scalb(Double.POSITIVE_INFINITY, scaleFactor));
            assertBitPattern(StrictMath.scalb(Double.NEGATIVE_INFINITY, scaleFactor), AccurateMath.scalb(Double.NEGATIVE_INFINITY, scaleFactor));
            assertBitPattern(StrictMath.scalb(Double.NEGATIVE_INFINITY, scaleFactor), AccurateMath.scalb(Double.NEGATIVE_INFINITY, scaleFactor));
        }
    }
      
    @Test
    public void doubleZero() {
        
        double zero = 0;
        double negativeZero = Math.copySign(zero, -1d);
        
        for(int scaleFactor = Double.MIN_EXPONENT * 2; scaleFactor <= (Double.MAX_EXPONENT - Double.MIN_EXPONENT); scaleFactor++) {   
            assertBitPattern(StrictMath.scalb(zero, scaleFactor), AccurateMath.scalb(zero, scaleFactor));
            assertBitPattern(StrictMath.scalb(negativeZero, scaleFactor), AccurateMath.scalb(negativeZero, scaleFactor));
        }
    }
        
    @Test
    public void doubleNormalValues() {
        
        long m = 1;
        for(int exp = Double.MIN_EXPONENT; exp <= Double.MAX_EXPONENT; exp++) {
            
            long exponentBits = (exp + 1023L) << 52;
            double powerOfTwo = Double.longBitsToDouble(exponentBits);
            
            for(int scaleFactor = Double.MIN_EXPONENT * 2; scaleFactor <= (Double.MAX_EXPONENT - Double.MIN_EXPONENT); scaleFactor++) {
                
                // check the power of two case.
                assertBitPattern(StrictMath.scalb(powerOfTwo, scaleFactor), AccurateMath.scalb(powerOfTwo, scaleFactor));
                
                for(int count = 0; count < 100; count++) {
                    
                    // generate mantissa and construct the value.
                    long mantissaBits = m & 0x000FFFFFFFFFFFFFL;
                    double x = Double.longBitsToDouble(exponentBits | mantissaBits);
                    
                    assertBitPattern(StrictMath.scalb(x, scaleFactor), AccurateMath.scalb(x, scaleFactor));
                    assertBitPattern(StrictMath.scalb(-x, scaleFactor), AccurateMath.scalb(-x, scaleFactor));
                    
                    // Marsaglia XOR pseudo-random shift.
                    m ^= m << 13;
                    m ^= m >> 17;
                    m ^= m << 5;
                }
            }            
        }
    }
    
    @Test
    public void doubleSubnormal() {
           
        long m = 1;
        for(int scaleFactor = Double.MIN_EXPONENT * 2; scaleFactor <= (Double.MAX_EXPONENT - Double.MIN_EXPONENT); scaleFactor++) {            
            for(int count = 0; count < 50000; count++) {
                
                long mantissaBits = m & 0x000FFFFFFFFFFFFFL;           
                double x = Double.longBitsToDouble(mantissaBits);
                
                Assert.assertTrue(x <= Double.MIN_NORMAL);
                assertBitPattern(StrictMath.scalb(x, scaleFactor), AccurateMath.scalb(x, scaleFactor));
                assertBitPattern(StrictMath.scalb(-x, scaleFactor), AccurateMath.scalb(-x, scaleFactor));

                m ^= m << 13;
                m ^= m >> 17;
                m ^= m << 5;
            }        
        }        
    }
        
    @Test
    public void floatSpecial() {
                
        for(int scaleFactor = Float.MIN_EXPONENT * 2; scaleFactor <= (Float.MAX_EXPONENT - Float.MIN_EXPONENT); scaleFactor++) {            
            assertBitPattern(StrictMath.scalb(Float.NaN, scaleFactor), AccurateMath.scalb(Float.NaN, scaleFactor));
            assertBitPattern(StrictMath.scalb(Float.POSITIVE_INFINITY, scaleFactor), AccurateMath.scalb(Float.POSITIVE_INFINITY, scaleFactor));
            assertBitPattern(StrictMath.scalb(Float.NEGATIVE_INFINITY, scaleFactor), AccurateMath.scalb(Float.NEGATIVE_INFINITY, scaleFactor));
            assertBitPattern(StrictMath.scalb(Float.NEGATIVE_INFINITY, scaleFactor), AccurateMath.scalb(Float.NEGATIVE_INFINITY, scaleFactor));
        }
    }
      
    @Test
    public void floatZero() {
        
        float zero = 0;
        float negativeZero = Math.copySign(zero, -1f);
        
        for(int scaleFactor = Float.MIN_EXPONENT * 2; scaleFactor <= (Float.MAX_EXPONENT - Float.MIN_EXPONENT); scaleFactor++) {   
            assertBitPattern(StrictMath.scalb(zero, scaleFactor), AccurateMath.scalb(zero, scaleFactor));
            assertBitPattern(StrictMath.scalb(negativeZero, scaleFactor), AccurateMath.scalb(negativeZero, scaleFactor));
        }
    }    
   
    @Test
    public void floatNormalValues() {
        
        int m = 1;
        for(int exp = Float.MIN_EXPONENT; exp <= Float.MAX_EXPONENT; exp++) {
            
            int exponentBits = (exp + 127) << 23;
            float powerOfTwo = Float.intBitsToFloat(exponentBits);
            
            for(int scaleFactor = Float.MIN_EXPONENT * 2; scaleFactor <= (Float.MAX_EXPONENT - Float.MIN_EXPONENT); scaleFactor++) {
                
                // check the power of two case.
                assertBitPattern(StrictMath.scalb(powerOfTwo, scaleFactor), AccurateMath.scalb(powerOfTwo, scaleFactor));
                
                for(int count = 0; count < 100; count++) {
                    
                    // generate mantissa and construct the value.
                    int mantissaBits = m & 0x7fffff;
                    float x = Float.intBitsToFloat(exponentBits | mantissaBits);
                    
                    assertBitPattern(StrictMath.scalb(x, scaleFactor), AccurateMath.scalb(x, scaleFactor));
                    assertBitPattern(StrictMath.scalb(-x, scaleFactor), AccurateMath.scalb(-x, scaleFactor));
                    
                    // Marsaglia XOR pseudo-random shift.
                    m ^= m << 13;
                    m ^= m >> 17;
                    m ^= m << 5;
                }
            }            
        }
    }
    
    @Test
    public void floatSubnormal() {
           
        int m = 1;
        for(int scaleFactor = Float.MIN_EXPONENT * 2; scaleFactor <= (Float.MAX_EXPONENT - Float.MIN_EXPONENT); scaleFactor++) {            
            for(int count = 0; count < 50000; count++) {
                
                int mantissaBits = m & 0x7fffff;               
                float x = Float.intBitsToFloat(mantissaBits);
                
                Assert.assertTrue(x <= Float.MIN_NORMAL);
                assertBitPattern(StrictMath.scalb(x, scaleFactor), AccurateMath.scalb(x, scaleFactor));
                assertBitPattern(StrictMath.scalb(-x, scaleFactor), AccurateMath.scalb(-x, scaleFactor));

                m ^= m << 13;
                m ^= m >> 17;
                m ^= m << 5;
            }        
        }        
    }
    
    /**
     * Assert that the specified doubles have the same bit pattern, except for NaN.
     * <p>
     * If <code>expected</code> is NaN, we only check that <code>actual</code> is also
     * NaN, not that they have the same fraction value.
     * 
     * @param expected Expected value.
     * @param actual Actual value.
     */
    private static void assertBitPattern(final double expected, final double actual) {
        
        if(Double.isNaN(expected)) { 
            Assert.assertTrue(Double.isNaN(actual));
            return;
        }
                
        Assert.assertEquals(Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(actual));
    }
    
    /**
     * Assert that the specified floats have the same bit pattern, except for NaN.
     * <p>
     * If <code>expected</code> is NaN, we only check that <code>actual</code> is also
     * NaN, not that they have the same fraction value.
     * 
     * @param expected Expected value.
     * @param actual Actual value.
     */
    private static void assertBitPattern(final float expected, final float actual) {
        
        if(Float.isNaN(expected)) { 
            Assert.assertTrue(Float.isNaN(actual));
            return;
        }
                
        Assert.assertEquals(Float.floatToIntBits(expected), Float.floatToIntBits(actual));
    }

}
