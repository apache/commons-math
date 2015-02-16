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

package org.apache.commons.math3.util;

import java.math.BigDecimal;


import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class DefaultTransformerTest {
    /**
     *
     */
    @Test
    public void testTransformDouble() throws Exception {
        double expected = 1.0;
        Double input = Double.valueOf(expected);
        DefaultTransformer t = new DefaultTransformer();
        Assert.assertEquals(expected, t.transform(input), 1.0e-4);
    }

    /**
     *
     */
    @Test
    public void testTransformNull() throws Exception {
        DefaultTransformer t = new DefaultTransformer();
        try {
            t.transform(null);
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException e) {
            // expected
        }
    }

    /**
     *
     */
    @Test
    public void testTransformInteger() throws Exception {
        double expected = 1.0;
        Integer input = Integer.valueOf(1);
        DefaultTransformer t = new DefaultTransformer();
        Assert.assertEquals(expected, t.transform(input), 1.0e-4);
    }

    /**
     *
     */
    @Test
    public void testTransformBigDecimal() throws Exception {
        double expected = 1.0;
        BigDecimal input = new BigDecimal("1.0");
        DefaultTransformer t = new DefaultTransformer();
        Assert.assertEquals(expected, t.transform(input), 1.0e-4);
    }

    /**
     *
     */
    @Test
    public void testTransformString() throws Exception {
        double expected = 1.0;
        String input = "1.0";
        DefaultTransformer t = new DefaultTransformer();
        Assert.assertEquals(expected, t.transform(input), 1.0e-4);
    }

    /**
     *
     */
    @Test(expected=MathIllegalArgumentException.class)
    public void testTransformObject(){
        Boolean input = Boolean.TRUE;
        DefaultTransformer t = new DefaultTransformer();
        t.transform(input);
    }

    @Test
    public void testSerial() {
        Assert.assertEquals(new DefaultTransformer(), TestUtils.serializeAndRecover(new DefaultTransformer()));
    }

}
