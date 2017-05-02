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
package org.apache.commons.math4.analysis.differentiation.finite;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the length and multiplier calculations in the finite difference
 * descriptor class.
 */
public class FiniteDifferenceTest {
   
    @Test
    public void testLength() {
	Assert.assertEquals(3, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 2).getLength());
	Assert.assertEquals(3, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 3).getLength());
	Assert.assertEquals(5, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 4).getLength());
	Assert.assertEquals(5, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 5).getLength());
	
	Assert.assertEquals(3, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 2).getLength());
	Assert.assertEquals(5, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 3).getLength());
	Assert.assertEquals(5, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 4).getLength());
	Assert.assertEquals(7, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 5).getLength());
	
	Assert.assertEquals(2, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 1).getLength());
	Assert.assertEquals(3, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 2).getLength());
	Assert.assertEquals(4, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 3).getLength());
	Assert.assertEquals(5, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 4).getLength());

	Assert.assertEquals(3, new FiniteDifference(FiniteDifferenceType.FORWARD, 2, 1).getLength());
	Assert.assertEquals(5, new FiniteDifference(FiniteDifferenceType.FORWARD, 3, 2).getLength());
	Assert.assertEquals(7, new FiniteDifference(FiniteDifferenceType.FORWARD, 4, 3).getLength());
	Assert.assertEquals(9, new FiniteDifference(FiniteDifferenceType.FORWARD, 5, 4).getLength());

	Assert.assertEquals(2, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 1).getLength());
	Assert.assertEquals(3, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 2).getLength());
	Assert.assertEquals(4, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 3).getLength());
	Assert.assertEquals(5, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 4).getLength());
	
	Assert.assertEquals(3, new FiniteDifference(FiniteDifferenceType.BACKWARD, 2, 1).getLength());
	Assert.assertEquals(5, new FiniteDifference(FiniteDifferenceType.BACKWARD, 3, 2).getLength());
	Assert.assertEquals(7, new FiniteDifference(FiniteDifferenceType.BACKWARD, 4, 3).getLength());
	Assert.assertEquals(9, new FiniteDifference(FiniteDifferenceType.BACKWARD, 5, 4).getLength());
    }

    @Test
    public void testLeftMultiplier() {	
	Assert.assertEquals(-1, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 2).getLeftMultiplier());
	Assert.assertEquals(-1, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 3).getLeftMultiplier());
	Assert.assertEquals(-2, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 4).getLeftMultiplier());
	Assert.assertEquals(-2, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 5).getLeftMultiplier());
	
	Assert.assertEquals(-1, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 2).getLeftMultiplier());
	Assert.assertEquals(-2, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 3).getLeftMultiplier());
	Assert.assertEquals(-2, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 4).getLeftMultiplier());
	Assert.assertEquals(-3, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 5).getLeftMultiplier());
	
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 1).getLeftMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 2).getLeftMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 3).getLeftMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 4).getLeftMultiplier());

	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.FORWARD, 2, 1).getLeftMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.FORWARD, 3, 2).getLeftMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.FORWARD, 4, 3).getLeftMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.FORWARD, 5, 4).getLeftMultiplier());

	Assert.assertEquals(-2, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 1).getLeftMultiplier());
	Assert.assertEquals(-3, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 2).getLeftMultiplier());
	Assert.assertEquals(-4, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 3).getLeftMultiplier());
	Assert.assertEquals(-5, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 4).getLeftMultiplier());
	
	Assert.assertEquals(-3, new FiniteDifference(FiniteDifferenceType.BACKWARD, 2, 1).getLeftMultiplier());
	Assert.assertEquals(-5, new FiniteDifference(FiniteDifferenceType.BACKWARD, 3, 2).getLeftMultiplier());
	Assert.assertEquals(-7, new FiniteDifference(FiniteDifferenceType.BACKWARD, 4, 3).getLeftMultiplier());
	Assert.assertEquals(-9, new FiniteDifference(FiniteDifferenceType.BACKWARD, 5, 4).getLeftMultiplier());
    }

    @Test
    public void testRightMultiplier() {
	Assert.assertEquals(1, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 2).getRightMultiplier());
	Assert.assertEquals(1, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 3).getRightMultiplier());
	Assert.assertEquals(2, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 4).getRightMultiplier());
	Assert.assertEquals(2, new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 5).getRightMultiplier());
	
	Assert.assertEquals(1, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 2).getRightMultiplier());
	Assert.assertEquals(2, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 3).getRightMultiplier());
	Assert.assertEquals(2, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 4).getRightMultiplier());
	Assert.assertEquals(3, new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 5).getRightMultiplier());
	
	Assert.assertEquals(2, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 1).getRightMultiplier());
	Assert.assertEquals(3, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 2).getRightMultiplier());
	Assert.assertEquals(4, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 3).getRightMultiplier());
	Assert.assertEquals(5, new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 4).getRightMultiplier());

	Assert.assertEquals(3, new FiniteDifference(FiniteDifferenceType.FORWARD, 2, 1).getRightMultiplier());
	Assert.assertEquals(5, new FiniteDifference(FiniteDifferenceType.FORWARD, 3, 2).getRightMultiplier());
	Assert.assertEquals(7, new FiniteDifference(FiniteDifferenceType.FORWARD, 4, 3).getRightMultiplier());
	Assert.assertEquals(9, new FiniteDifference(FiniteDifferenceType.FORWARD, 5, 4).getRightMultiplier());

	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 1).getRightMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 2).getRightMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 3).getRightMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.BACKWARD, 1, 4).getRightMultiplier());
	
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.BACKWARD, 2, 1).getRightMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.BACKWARD, 3, 2).getRightMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.BACKWARD, 4, 3).getRightMultiplier());
	Assert.assertEquals(0, new FiniteDifference(FiniteDifferenceType.BACKWARD, 5, 4).getRightMultiplier());
    }

}
