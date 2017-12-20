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
package org.apache.commons.math4.geometry.euclidean.oned;

import org.apache.commons.math4.geometry.partitioning.SubHyperplane;
import org.apache.commons.math4.geometry.partitioning.SubHyperplane.SplitSubHyperplane;
import org.junit.Assert;
import org.junit.Test;

public class SubOrientedPointTest {
    private static final double DEFAULT_TOLERANCE = 1e-10;
    
    @Test
    public void testGetSize() {
        OrientedPoint hyperplane = new OrientedPoint(new Cartesian1D(1), true, DEFAULT_TOLERANCE);
        SubOrientedPoint pt = (SubOrientedPoint) hyperplane.wholeHyperplane();
        
        Assert.assertEquals(0.0, pt.getSize(), DEFAULT_TOLERANCE);
    }
    
    @Test
    public void testIsEmpty() {
        OrientedPoint hyperplane = new OrientedPoint(new Cartesian1D(1), true, DEFAULT_TOLERANCE);
        SubOrientedPoint pt = (SubOrientedPoint) hyperplane.wholeHyperplane();
        
        Assert.assertFalse(pt.isEmpty());
    }
    
    @Test
    public void testBuildNew() {
        OrientedPoint originalHyperplane = new OrientedPoint(new Cartesian1D(1), true, DEFAULT_TOLERANCE);
        SubOrientedPoint pt = (SubOrientedPoint) originalHyperplane.wholeHyperplane();
        
        OrientedPoint hyperplane = new OrientedPoint(new Cartesian1D(2), true, DEFAULT_TOLERANCE);
        IntervalsSet intervals = new IntervalsSet(2, 3, DEFAULT_TOLERANCE);
        
        SubHyperplane<Euclidean1D> result = pt.buildNew(hyperplane, intervals);
        
        Assert.assertTrue(result instanceof SubOrientedPoint);
        Assert.assertSame(hyperplane, result.getHyperplane());
        Assert.assertSame(intervals, ((SubOrientedPoint) result).getRemainingRegion());
    }
    
    @Test
    public void testSplit_usesToleranceFromParentHyperplane() {
        OrientedPoint hyperplane = new OrientedPoint(new Cartesian1D(1), true, 0.1);
        SubOrientedPoint pt = (SubOrientedPoint) hyperplane.wholeHyperplane();
        
        SplitSubHyperplane<Euclidean1D> plusSplit = pt.split(new OrientedPoint(new Cartesian1D(0.899), true, 1e-10));
        Assert.assertTrue(plusSplit.getMinus() == null);
        Assert.assertTrue(plusSplit.getPlus() != null);
        
        SplitSubHyperplane<Euclidean1D> lowWithinTolerance = pt.split(new OrientedPoint(new Cartesian1D(0.901), true, 1e-10));
        Assert.assertTrue(lowWithinTolerance.getMinus() == null);
        Assert.assertTrue(lowWithinTolerance.getPlus() == null);
        
        SplitSubHyperplane<Euclidean1D> highWithinTolerance = pt.split(new OrientedPoint(new Cartesian1D(1.09), true, 1e-10));
        Assert.assertTrue(highWithinTolerance.getMinus() == null);
        Assert.assertTrue(highWithinTolerance.getPlus() == null);
        
        SplitSubHyperplane<Euclidean1D> minusSplit = pt.split(new OrientedPoint(new Cartesian1D(1.101), true, 1e-10));
        Assert.assertTrue(minusSplit.getMinus() != null);
        Assert.assertTrue(minusSplit.getPlus() == null);
    }
}