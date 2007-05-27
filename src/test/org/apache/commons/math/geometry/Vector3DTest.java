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

package org.apache.commons.math.geometry;

import org.apache.commons.math.geometry.Vector3D;

import junit.framework.*;

public class Vector3DTest
  extends TestCase {

  public Vector3DTest(String name) {
    super(name);
  }

  public void testCoordinates() {
    Vector3D v = new Vector3D(1, 2, 3);
    assertTrue(Math.abs(v.getX() - 1) < 1.0e-12);
    assertTrue(Math.abs(v.getY() - 2) < 1.0e-12);
    assertTrue(Math.abs(v.getZ() - 3) < 1.0e-12);
  }
  
  public void testNorm() {
    assertTrue(Math.abs(new Vector3D().getNorm()) < 1.0e-12);
    assertTrue(Math.abs(new Vector3D(1, 2, 3).getNorm() - Math.sqrt(14))
               < 1.0e-12);
  }

  public void testSubtract() {

    Vector3D v1 = new Vector3D(1, 2, 3);
    Vector3D v2 = new Vector3D(-3, -2, -1);
    v1 = v1.subtract(v2);
    checkVector(v1, new Vector3D(4, 4, 4));

    checkVector(v2.subtract(v1), new Vector3D(-7, -6, -5));

  }

  public void testAdd() {
    Vector3D v1 = new Vector3D(1, 2, 3);
    Vector3D v2 = new Vector3D(-3, -2, -1);
    v1 = v1.add(v2);
    checkVector(v1, new Vector3D(-2, 0, 2));

    checkVector(v2.add(v1), new Vector3D(-5, -2, 1));

  }

  public void testScalarProduct() {
    Vector3D v = new Vector3D(1, 2, 3);
    v = v.multiply(3);
    checkVector(v, new Vector3D(3, 6, 9));

    checkVector(v.multiply(0.5), new Vector3D(1.5, 3, 4.5));

  }

  public void testVectorialProducts() {
    Vector3D v1 = new Vector3D(2, 1, -4);
    Vector3D v2 = new Vector3D(3, 1, -1);

    assertTrue(Math.abs(Vector3D.dotProduct(v1, v2) - 11) < 1.0e-12);

    Vector3D v3 = Vector3D.crossProduct(v1, v2);
    checkVector(v3, new Vector3D(3, -10, -1));

    assertTrue(Math.abs(Vector3D.dotProduct(v1, v3)) < 1.0e-12);
    assertTrue(Math.abs(Vector3D.dotProduct(v2, v3)) < 1.0e-12);

  }

  public void testAngular() {

    assertEquals(0,           Vector3D.plusI.getAlpha(), 1.0e-10);
    assertEquals(0,           Vector3D.plusI.getDelta(), 1.0e-10);
    assertEquals(Math.PI / 2, Vector3D.plusJ.getAlpha(), 1.0e-10);
    assertEquals(0,           Vector3D.plusJ.getDelta(), 1.0e-10);
    assertEquals(0,           Vector3D.plusK.getAlpha(), 1.0e-10);
    assertEquals(Math.PI / 2, Vector3D.plusK.getDelta(), 1.0e-10);

    Vector3D u = new Vector3D(-1, 1, -1);
    assertEquals(3 * Math.PI /4, u.getAlpha(), 1.0e-10);
    assertEquals(-1.0 / Math.sqrt(3), Math.sin(u.getDelta()), 1.0e-10);

  }

  public void testAngularSeparation() {
    Vector3D v1 = new Vector3D(2, -1, 4);

    Vector3D  k = v1.normalize();
    Vector3D  i = k.orthogonal();
    Vector3D v2 = k.multiply(Math.cos(1.2)).add(i.multiply(Math.sin(1.2)));

    assertTrue(Math.abs(Vector3D.angle(v1, v2) - 1.2) < 1.0e-12);

  }

  private void checkVector(Vector3D v1, Vector3D v2) {
    assertTrue(v1.subtract(v2).getNorm() < 1.0e-12);
  }
  
  public static Test suite() {
    return new TestSuite(Vector3DTest.class);
  }

}
