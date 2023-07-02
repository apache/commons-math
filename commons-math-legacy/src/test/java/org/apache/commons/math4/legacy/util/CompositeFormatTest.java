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

package org.apache.commons.math4.legacy.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/** Tests for {@link CompositeFormat}. */
public class CompositeFormatTest {
  @Test
  public void testExtractNumbers() {
    List<Double> numbers = new ArrayList<>();
    assertEquals(numbers, CompositeFormat.extractNumbers(null));
    assertEquals(numbers, CompositeFormat.extractNumbers(""));
    assertEquals(numbers, CompositeFormat.extractNumbers("this is test text"));
    numbers.add(12.3d);
    assertEquals(numbers, CompositeFormat.extractNumbers(" Duration : 12.3 days, some minutes"));
    numbers.add(34.0d);
    assertEquals(numbers, CompositeFormat.extractNumbers(" Duration : 12.3 days, 34minutes"));
    numbers.clear();
    numbers.add(76.0d);
    numbers.add(180.2d);
    assertEquals(numbers, CompositeFormat.extractNumbers("Weight is 76 and height is 180.2 cm"));
    numbers.clear();
    numbers.add(12.22198254786d);
    numbers.add(90.0d);
    assertEquals(numbers, CompositeFormat.extractNumbers("Between 12.22198254786 and 90"));
    numbers.clear();
    numbers.add(1289.0d);
    numbers.add(9283.112d);
    numbers.add(281.0d);
    assertEquals(
        numbers, CompositeFormat.extractNumbers("First: 1289.0 Second: 9283.112 Third: 281"));
  }
}
