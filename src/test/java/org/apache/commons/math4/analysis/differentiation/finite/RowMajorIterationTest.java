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

import junit.framework.Assert;

import org.apache.commons.math4.util.Pair;
import org.junit.Test;

/**
 * Some simple tests for the row-major iteration helper.
 */
public class RowMajorIterationTest {
        
    @Test
    public void test1Dimension() {
	RowMajorIteration iteration = new RowMajorIteration(10);
	
	for(Pair<int[], Integer> pair : iteration) {
	    int[] multi = pair.getFirst();
	    int rowMajor = pair.getSecond();
	    
	    Assert.assertEquals(1, multi.length);
	    Assert.assertEquals(rowMajor, multi[0]);
	}	
    }
    
    @Test
    public void test2Dimension1() {
	int n = 10;
	int m = 1;
	RowMajorIteration iteration = new RowMajorIteration(n, m);
	
	for(Pair<int[], Integer> pair : iteration) {
	    int[] multi = pair.getFirst();
	    int rowMajor = pair.getSecond();
	    
	    Assert.assertEquals(rowMajor, multi[0]);
	    Assert.assertEquals(0, multi[1]);
	}	
    }

    @Test
    public void test2Dimension2() {
	int n = 5;
	int m = 3;
	RowMajorIteration iteration = new RowMajorIteration(n, m);
	
	for(Pair<int[], Integer> pair : iteration) {
	    int[] multi = pair.getFirst();
	    int rowMajor = pair.getSecond();
	    
	    int expected = multi[1] + (multi[0] * m);
	    Assert.assertEquals(expected, rowMajor);
	}	
    }

    @Test
    public void test2Dimension3() {
	int n = 7;
	int m = 7;
	RowMajorIteration iteration = new RowMajorIteration(n, m);
	
	for(Pair<int[], Integer> pair : iteration) {
	    int[] multi = pair.getFirst();
	    int rowMajor = pair.getSecond();
	    
	    int expected = multi[1] + (multi[0] * m);
	    Assert.assertEquals(expected, rowMajor);
	}	
    }
    
    @Test
    public void test3Dimension1() {
	int n = 7;
	int m = 7;
	int p = 5;
	RowMajorIteration iteration = new RowMajorIteration(n, m, p);
	
	for(Pair<int[], Integer> pair : iteration) {
	    int[] multi = pair.getFirst();
	    int rowMajor = pair.getSecond();
	    
	    int expected = multi[2] + (p * multi[1]) + (m * p * multi[0]); 
	    Assert.assertEquals(expected, rowMajor);	    
	}	
    }

}

