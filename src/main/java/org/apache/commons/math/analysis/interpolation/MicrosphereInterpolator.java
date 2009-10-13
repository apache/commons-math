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
package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.random.UnitSphereRandomVectorGenerator;

/**
 * Interpolator that implements the algorithm described in
 * <em>William Dudziak</em>'s
 * <a href="http://www.dudziak.com/microsphere.pdf">MS thesis</a>
 *
 * @version $Revision$ $Date$
 */
public class MicrosphereInterpolator
    implements MultivariateRealInterpolator {

    /**
     * Default number of surface elements that composes the microsphere.
     */
    public static final int DEFAULT_MICROSPHERE_ELEMENTS = 2000;

    /**
     * Default exponent used the weights calculation.
     */
    public static final int DEFAULT_BRIGHTNESS_EXPONENT = 2;

    /**
     * Number of surface elements of the microsphere.
     */
    private int microsphereElements;

    /**
     * Exponent used in the power law that computes the weights of the
     * sample data.
     */
    private int brightnessExponent;

    /** Create a microsphere interpolator with default settings.
     * <p>Calling this constructor is equivalent to call {@link
     * #MicrosphereInterpolator(int, int)
     * MicrosphereInterpolator(MicrosphereInterpolator.DEFAULT_MICROSPHERE_ELEMENTS,
     * MicrosphereInterpolator.DEFAULT_BRIGHTNESS_EXPONENT)}.</p>
     * weights of the sample data
     */
    public MicrosphereInterpolator() {
        this(DEFAULT_MICROSPHERE_ELEMENTS, DEFAULT_BRIGHTNESS_EXPONENT);
    }

    /** Create a microsphere interpolator.
     * @param microsphereElements number of surface elements of the microsphere
     * @param brightnessExponent exponent used in the power law that computes the
     * weights of the sample data
     * @throws IllegalArgumentException if {@code microsphereElements <= 0}
     * or {@code brightnessExponent < 0}
     */
    public MicrosphereInterpolator(final int microsphereElements,
                                   final int brightnessExponent) {
        setMicropshereElements(microsphereElements);
        setBrightnessExponent(brightnessExponent);
    }

    /**
     * {@inheritDoc}
     */
    public MultivariateRealFunction interpolate(final double[][] xval,
                                                final double[] yval)
        throws MathException, IllegalArgumentException {
        final UnitSphereRandomVectorGenerator rand
            = new UnitSphereRandomVectorGenerator(xval[0].length);
        return new MicrosphereInterpolatingFunction(xval, yval,
                                                    brightnessExponent,
                                                    microsphereElements,
                                                    rand);
    }

    /**
     * Set the brightness exponent.
     * @param brightnessExponent Exponent for computing the distance dimming
     * factor.
     * @throws IllegalArgumentException if {@code brightnessExponent < 0}.
     */
    public void setBrightnessExponent(final int brightnessExponent) {
        if (brightnessExponent < 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                "brightness exponent should be positive or null, but got {0}",
                brightnessExponent);
        }
        this.brightnessExponent = brightnessExponent;
    }

    /**
     * Set the number of microsphere elements.
     * @param elements Number of surface elements of the microsphere.
     * @throws IllegalArgumentException if {@code microsphereElements <= 0}.
     */
    public void setMicropshereElements(final int elements) {
        if (microsphereElements < 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                "number of microsphere elements must be positive, but got {0}",
                microsphereElements);
        }
        this.microsphereElements = elements;
    }

}
