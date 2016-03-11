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
package org.apache.commons.math4.rng.internal.source64;

import org.junit.Assert;
import org.junit.Test;

public class SplitMix64Test {
    @Test
    public void testReferenceCode() {
        final long refSeed = 0x1a2b3c4d5e6f7531L;
        final SplitMix64 rng = new SplitMix64(refSeed);

        final long[] refValues = {
            0x4141302768c9e9d0L, 0x64df48c4eab51b1aL, 0x4e723b53dbd901b3L, 0xead8394409dd6454L,
            0x3ef60e485b412a0aL, 0xb2a23aee63aecf38L, 0x6cc3b8933c4fa332L, 0x9c9e75e031e6fccbL,
            0x0fddffb161c9f30fL, 0x2d1d75d4e75c12a3L, 0xcdcf9d2dde66da2eL, 0x278ba7d1d142cfecL,
            0x4ca423e66072e606L, 0x8f2c3c46ebc70bb7L, 0xc9def3b1eeae3e21L, 0x8e06670cd3e98bceL,
            0x2326dee7dd34747fL, 0x3c8fff64392bb3c1L, 0xfc6aa1ebe7916578L, 0x3191fb6113694e70L,
            0x3453605f6544dac6L, 0x86cf93e5cdf81801L, 0x0d764d7e59f724dfL, 0xae1dfb943ebf8659L,
            0x012de1babb3c4104L, 0xa5a818b8fc5aa503L, 0xb124ea2b701f4993L, 0x18e0374933d8c782L,
            0x2af8df668d68ad55L, 0x76e56f59daa06243L, 0xf58c016f0f01e30fL, 0x8eeafa41683dbbf4L,
            0x7bf121347c06677fL, 0x4fd0c88d25db5ccbL, 0x99af3be9ebe0a272L, 0x94f2b33b74d0bdcbL,
            0x24b5d9d7a00a3140L, 0x79d983d781a34a3cL, 0x582e4a84d595f5ecL, 0x7316fe8b0f606d20L,
        };

        for (int i = 0; i < refValues.length; ++i) {
            Assert.assertEquals(refValues[i], rng.nextLong());
        }
    }
}
