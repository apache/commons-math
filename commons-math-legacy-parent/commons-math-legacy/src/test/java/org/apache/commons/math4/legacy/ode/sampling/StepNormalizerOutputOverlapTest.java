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

package org.apache.commons.math4.legacy.ode.sampling;

/** Step normalizer output tests, for problems where the first and last points
 * are overlap fixed points.
 */
public class StepNormalizerOutputOverlapTest extends StepNormalizerOutputTestBase {
    @Override
    protected double getStart() {
        return 0.0;
    }

    @Override
    protected double getEnd() {
        return 10.0;
    }

    @Override
    protected double[] getExpInc() {
        return new double[] { 0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0,
                              4.5, 5.0, 5.5, 6.0, 6.5, 7.0, 7.5, 8.0, 8.5,
                              9.0, 9.5, 10.0 };
    }

    @Override
    protected double[] getExpIncRev() {
        return new double[] { 10.0, 9.5, 9.0, 8.5, 8.0, 7.5, 7.0, 6.5,
                              6.0, 5.5, 5.0, 4.5, 4.0, 3.5, 3.0, 2.5,
                              2.0, 1.5, 1.0, 0.5, 0.0 };
    }

    @Override
    protected double[] getExpMul() {
        return new double[] { 0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0,
                              4.5, 5.0, 5.5, 6.0, 6.5, 7.0, 7.5, 8.0, 8.5,
                              9.0, 9.5, 10.0 };
    }

    @Override
    protected double[] getExpMulRev() {
        return new double[] { 10.0, 9.5, 9.0, 8.5, 8.0, 7.5, 7.0, 6.5,
                              6.0, 5.5, 5.0, 4.5, 4.0, 3.5, 3.0, 2.5,
                              2.0, 1.5, 1.0, 0.5, 0.0 };
    }

    @Override
    protected int[][] getO() {
        return new int[][] {{1, 0}, {1, 0}, {0, 0}, {0, 0},
                            {1, 0}, {1, 0}, {0, 0}, {0, 0},
                            {1, 0}, {1, 0}, {0, 0}, {0, 0},
                            {1, 0}, {1, 0}, {0, 0}, {0, 0}};
    }
}
