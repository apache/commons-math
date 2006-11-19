// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.geometry;

import junit.framework.*;

public class ImmutableVector3DTest
  extends TestCase {

  public ImmutableVector3DTest(String name) {
    super(name);
  }

  public void testCanonical() {
    try {
      Vector3D.plusK.normalizeSelf();
      fail("an exception should have been thrown");
    } catch (UnsupportedOperationException uoe) {
    } catch (Exception e) {
      fail ("wrong exception caught");
    }
  }
  
  public static Test suite() {
    return new TestSuite(ImmutableVector3DTest.class);
  }

}
