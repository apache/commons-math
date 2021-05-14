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
package org.apache.commons.math4.legacy.exception;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link MaxCountExceededException}.
 *
 */
public class MaxCountExceededExceptionTest {
    @Test
    public void testAccessors() {
        final MaxCountExceededException e = new MaxCountExceededException(10);
        Assert.assertEquals(10, e.getMax());
    }
}
