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
package org.apache.commons.math4.legacy.linear;

import org.apache.commons.math4.legacy.core.dfp.Dfp;
import org.apache.commons.math4.legacy.core.dfp.DfpField;

/**
 * Dummy class for testing {@link org.apache.commons.math4.legacy.core.Field} functionalities.
 */
public final class Dfp25 {
    private static final DfpField FIELD = new DfpField(25);
    public static final Dfp ZERO = FIELD.newDfp(0d);
    public static final Dfp ONE = of(1d);
    public static final Dfp TWO = of(2d);

    /** No instances. */
    private Dfp25() {}

    public static Dfp of(double x) {
        return ZERO.newInstance(x);
    }
    public static Dfp of(double x, double y) {
        return of(x).divide(of(y));
    }

    public static DfpField getField() {
        return FIELD;
    }
}
