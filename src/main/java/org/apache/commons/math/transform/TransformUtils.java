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
package org.apache.commons.math.transform;

import org.apache.commons.math.complex.Complex;

/**
 * Useful functions for the implementation of various transforms.
 *
 * @version $Id$
 * @since 3.0
 */
public class TransformUtils {
    /** Private constructor. */
    private TransformUtils() {
        super();
    }

    /**
     * Multiply every component in the given real array by the
     * given real number. The change is made in place.
     *
     * @param f the real array to be scaled
     * @param d the real scaling coefficient
     * @return a reference to the scaled array
     */
    public static double[] scaleArray(double[] f, double d) {

        for (int i = 0; i < f.length; i++) {
            f[i] *= d;
        }
        return f;
    }

    /**
     * Multiply every component in the given complex array by the
     * given real number. The change is made in place.
     *
     * @param f the complex array to be scaled
     * @param d the real scaling coefficient
     * @return a reference to the scaled array
     */
    public static Complex[] scaleArray(Complex[] f, double d) {

        for (int i = 0; i < f.length; i++) {
            f[i] = new Complex(d * f[i].getReal(), d * f[i].getImaginary());
        }
        return f;
    }

}
