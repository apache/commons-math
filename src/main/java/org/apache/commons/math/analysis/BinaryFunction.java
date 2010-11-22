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

package org.apache.commons.math.analysis;

import org.apache.commons.math.exception.MathUserException;
import org.apache.commons.math.util.FastMath;



/**
 * Base class for {@link BivariateRealFunction} that can be composed with other functions.
 *
 * @since 2.1
 * @version $Revision$ $Date$
 * @deprecated in 2.2 (to be removed in 3.0). Please use the function classes
 * in the {@link org.apache.commons.math.analysis.function} package and the
 * methods in {@link FunctionUtils}.
 */
@Deprecated
public abstract class BinaryFunction implements BivariateRealFunction {
    public static BinaryFunction make(final BivariateRealFunction f) {
        return new BinaryFunction() {
            /** {@inheritDoc} */
            @Override
                public double value(double x, double y) {
                return f.value(x, y);
            }
        };
    }

    /** The + operator method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction ADD =
        make(new org.apache.commons.math.analysis.function.Add());

    /** The - operator method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction SUBTRACT =
        make(new org.apache.commons.math.analysis.function.Subtract());

    /** The * operator method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction MULTIPLY =
        make(new org.apache.commons.math.analysis.function.Multiply());

    /** The / operator method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction DIVIDE =
        make(new org.apache.commons.math.analysis.function.Divide());

    /** The {@code FastMath.pow} method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction POW =
        make(new org.apache.commons.math.analysis.function.Pow());

    /** The {@code FastMath.atan2} method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction ATAN2 =
        make(new org.apache.commons.math.analysis.function.Atan2());

    /** {@inheritDoc} */
    public abstract double value(double x, double y) throws MathUserException;

    /** Get a composable function by fixing the first argument of the instance.
     * @param fixedX fixed value of the first argument
     * @return a function such that {@code f.value(y) == value(fixedX, y)}
     */
    public ComposableFunction fix1stArgument(final double fixedX) {
        return ComposableFunction.make(FunctionUtils.fix1stArgument(this, fixedX));
    }

    /** Get a composable function by fixing the second argument of the instance.
     * @param fixedY fixed value of the second argument
     * @return a function such that {@code f.value(x) == value(x, fixedY)}
     */
    public ComposableFunction fix2ndArgument(final double fixedY) {
        return ComposableFunction.make(FunctionUtils.fix2ndArgument(this, fixedY));
    }
}
