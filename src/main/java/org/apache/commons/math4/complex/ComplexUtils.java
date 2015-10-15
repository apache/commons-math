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

package org.apache.commons.math4.complex;

import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.IntegerSequence;

/**
 * Static implementations of common
 * {@link org.apache.commons.math3.complex.Complex} utilities functions.
 *
 */
public class ComplexUtils {

    /**
     * Default constructor.
     */
    private ComplexUtils() {}

    /**
     * Creates a complex number from the given polar representation.
     * <p>
     * The value returned is <code>r&middot;e<sup>i&middot;theta</sup></code>,
     * computed as <code>r&middot;cos(theta) + r&middot;sin(theta)i</code></p>
     * <p>
     * If either <code>r</code> or <code>theta</code> is NaN, or
     * <code>theta</code> is infinite, {@link Complex#NaN} is returned.</p>
     * <p>
     * If <code>r</code> is infinite and <code>theta</code> is finite,
     * infinite or NaN values may be returned in parts of the result, following
     * the rules for double arithmetic.<pre>
     * Examples:
     * <code>
     * polar2Complex(INFINITY, &pi;/4) = INFINITY + INFINITY i
     * polar2Complex(INFINITY, 0) = INFINITY + NaN i
     * polar2Complex(INFINITY, -&pi;/4) = INFINITY - INFINITY i
     * polar2Complex(INFINITY, 5&pi;/4) = -INFINITY - INFINITY i </code></pre></p>
     *
     * @param r the modulus of the complex number to create
     * @param theta  the argument of the complex number to create
     * @return <code>r&middot;e<sup>i&middot;theta</sup></code>
     * @throws MathIllegalArgumentException if {@code r} is negative.
     * @since 1.1
     */
    public static Complex polar2Complex(double r, double theta) throws MathIllegalArgumentException {
        if (r < 0) {
            throw new MathIllegalArgumentException(
                  LocalizedFormats.NEGATIVE_COMPLEX_MODULE, r);
        }
        return new Complex(r * FastMath.cos(theta), r * FastMath.sin(theta));
    }

    /**
     * Returns double from array {@code real[]} at entry {@code index} as a {@code Complex}.
     *
     * @param real Array of real numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromRealArray(double[] real, int index) {
        return new Complex(real[index]);
    }

	/**
     * Returns float from array {@code real[]} at entry {@code index} as a {@code Complex}.
     *
     * @param real Array of real numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromRealArray(float[] real, int index) {
        return new Complex(real[index]);
    }

	/**
     * Returns real component of Complex from array {@code complex[]} 
	 * at entry {@code index} as a {@code double}.
     *
     * @param complex Array of complex numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static double extractRealFromComplexArray(Complex[] complex, int index) {
        return complex[index].getReal();
    }

	/**
     * Returns real component of array {@code complex[]} at entry {@code index} as a {@code float}.
     *
     * @param complex Array of complex numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static float extractRealFloatFromComplexArray(Complex[] complex, int index) {
        return (float)complex[index].getReal();
    }

	
    /**
     * Returns Complex object from interleaved {@code double[]} array {@code Complex[]} at entry {@code index}.
     *
     * @param d {@code double[]} of interleaved complex numbers alternating real and imaginary values
	 * @param index Location in the array. This is the location by complex number, e.g.
	 * index number 5 in the {@code double[]} array will return a {@code new Complex(d[10], d[11])}
     * @return size 2 {@code double[]} array.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromInterleavedArray(double[] d, int index) {
        return new Complex(d[index*2], d[index*2+1]);
    }
    
    /**
     * Returns Complex object from interleaved {@code float[]} array {@code Complex[]} at entry {@code index}.
     *
     * @param f {@code float[]} of interleaved complex numbers alternating real and imaginary values
	 * @param index Location in the array. This is the location by complex number, e.g.
	 * index number 5 in the {@code float[]} array will return a new {@code Complex(d[10], d[11])}
     * @return size 2 {@code float[]} array.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromInterleavedArray(float[] f, int index) {
        return new Complex(f[index*2], f[index*2+1]);
    }
        
    /**
     * Returns values of Complex object from array {@code Complex[]} at entry {@code index} as a
	 * size 2 {@code double} of the form {real, imag}.
     *
     * @param complex Array of complex numbers.
	 * @param index Location in the array.
     * @return size 2 {@code double[]} array.
     *
     * @since 4.0
     */
    public static double[] extractInterleavedFromComplexArray(Complex[] complex, int index) {
        return new double[]{complex[index].getReal(), complex[index].getImaginary()};
    }

	/**
     * Returns Complex object from array {@code Complex[]} at entry {@code index} as a
	 * size 2 {@code float} of the form {real, imag}.
     *
     * @param complex Array of complex numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static float[] extractInterleavedFloatFromComplexArray(Complex[] complex, int index) {
        return new float[]{(float)complex[index].getReal(), (float)complex[index].getImaginary()};
    }
    
    /**
     * Converts a {@code double[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end}.
     *
     * @param real Array of real numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }
    
    /**
     * Converts a {@code float[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end}.
     *
     * @param real Array of real numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a {@code float[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real, int start, int end, int increment) {
    	Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }
  

	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects
     * for the {@code IntegerSequence} range.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real, Iterable<Integer> range) {
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }


	/**
     * Converts a {@code float[]} array to an array of {@code Complex} objects
     * for the {@code IntegerSequence} range.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real, Iterable<Integer> range) {
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }
    
	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real) {
		int index = 0;
        final Complex c[] = new Complex[real.length];
        for (double d : real) {
            c[index] = new Complex(d);
            index++;
        }
        return c;
    }
 
    /**
     * Converts a {@code float[]} array to an array of {@code Complex} objects.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real) {
		int index = 0;
        final Complex c[] = new Complex[real.length];
        for (float d : real) {
            c[index] = new Complex(d);
            index++;
        }
        return c;
    }

    /**
     * Converts an array of {@code Complex} objects to a {@code double[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param c Array of {@code Complex} objects.
     * @param start Start index.
     * @param end End index.
     * @return a {@code double[]} array of the real component.
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)];
        for (Integer i : range) {
            d[index] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }
    
    /**
     * Converts an array of {@code Complex} objects to a {@code float[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param c Array of Complex objects
     * @param start Start index.
     * @param end End index.
     * @return a {@code float[]} array of the real component.
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)];
        for (Integer i : range) {
            f[index] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

	/**
     * Converts an array of {@code Complex} objects to a {@code double[]} array 
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param c Array of {@code Complex} objects.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return a {@code double[]} array of the real component.
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)];
        for (Integer i : range) {
            d[index] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

	/**
     * Converts an array of {@code Complex} objects to a {@code float[]} array 
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param c Array of {@code Complex} objects.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return a {@code float[]} array of the real component.
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)];
        for (Integer i : range) {
            f[index] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }
  

	/**
     * Converts an array of {@code Complex} objects to a {@code double[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param c Array of {@code Complex} objects.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return a {@code double[]} array of the real component.
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c, Iterable<Integer> range) {
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)];
        for (Integer i : range) {
            d[index] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }


    /**
     * Converts an array of {@code Complex} objects to a {@code float[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param c Array of {@code Complex} objects.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return a {@code float[]} array of the real component.
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c, Iterable<Integer> range) {
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)];
        for (Integer i : range) {
            f[index] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }
    
	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects.
     *
     * @param c Array of {@code Complex} objects.
     * @return a {@code double[]} array of the real component.
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c) {
		int index = 0;
        final double d[] = new double[c.length];
        for (Complex cc : c) {
            d[index] = cc.getReal();
            index++;
        }
        return d;
    }
 
    /**
     * Converts a {@code float[]} array to an array of {@code Complex} objects.
     *
     * @param c Array of {@code Complex} objects.
     * @return a {@code float[]} array of the real component.
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c) {
		int index = 0;
        final float f[] = new float[c.length];
        for (Complex cc : c) {
            f[index] = (float)cc.getReal();
            index++;
        }
        return f;
    }

    // BEGIN INTERLEAVED METHODS
        
    /**
     * Converts a complex interleaved {@code double[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end}.
     *
     * @param interleaved {@code double[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }
    
    /**
     * Converts a complex interleaved {@code float[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end}.
     *
     * @param interleaved {@code float[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a complex interleaved {@code double[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param interleaved {@code double[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a complex interleaved {@code float[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param interleaved {@code float[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved, int start, int end, int increment) {
    	Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }
  

	/**
     * Converts a complex interleaved {@code double[]} array to an array of {@code Complex} objects
     * for the {@code IntegerSequence} range.
     *
     * @param interleaved {@code double[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved, Iterable<Integer> range) {
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }


	/**
     * Converts a complex interleaved {@code float[]} array to an array of {@code Complex} objects
     * for the {@code IntegerSequence} range.
     *
     * @param interleaved {@code float[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved, Iterable<Integer> range) {
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }
    
	/**
     * Converts a complex interleaved {@code double[]} array to an array of {@code Complex} objects
     *
     * @param interleaved {@code double[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved) {
		final int length = interleaved.length/2;
        final Complex c[] = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = new Complex(interleaved[n*2], interleaved[n*2+1]);
        }
        return c;
    }
 
    /**
     * Converts a complex interleaved {@code float[]} array to an array of {@code Complex} objects
     *
     * @param interleaved {@code float[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved) {
		final int length = interleaved.length/2;
        final Complex c[] = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = new Complex(interleaved[n*2], interleaved[n*2+1]);
        }
        return c;
    }

    /**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code double[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param c Array of Complex objects.
     * @param start Start index.
     * @param end End index.
     * @return a complex interleaved {@code double[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            d[real] = c[i].getReal();
            d[imag] = c[i].getImaginary();
            index++;
        }
        return d;
    }
    
    /**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code float[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param c Array of Complex objects.
     * @param start Start index.
     * @param end End index.
     * @return a complex interleaved {@code float[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            f[real] = (float)c[i].getReal();
            f[imag] = (float)c[i].getImaginary();
            index++;
        }
        return f;
    }
    
	/**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code double[]} array
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param c Array of Complex objects.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return a complex interleaved {@code double[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            d[real] = c[i].getReal();
            d[imag] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    /**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code float[]} array
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param c Array of Complex objects.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return a complex interleaved {@code float[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            f[real] = (float)c[i].getReal();
            f[imag] = (float)c[i].getImaginary();
            index++;
        }
        return f;
    }
  

	/**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code double[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param c Array of Complex objects.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return a complex interleaved {@code double[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c, Iterable<Integer> range) {
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            d[real] = c[i].getReal();
            d[imag] = c[i].getImaginary();
            index++;
        }
        return d;
    }


    /**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code float[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param c Array of Complex objects.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return a complex interleaved {@code float[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c, Iterable<Integer> range) {
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            f[real] = (float)c[i].getReal();
            f[imag] = (float)c[i].getImaginary();
            index++;
        }
        return f;
    }
    
	/**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code double[]} array
     *
     * @param c Array of Complex objects.
     * @return a complex interleaved {@code double[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c) {
		int index = 0;
        final double d[] = new double[c.length*2];
        for (Complex cc : c) {
        	int real = index*2;
        	int imag = index*2+1;
            d[real] = cc.getReal();
            d[imag] = cc.getImaginary();
            index++;
        }
        return d;
    }

    /**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code float[]} array
     *
     * @param c Array of Complex objects.
     * @return a complex interleaved {@code float[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c) {
		int index = 0;
        final float f[] = new float[c.length*2];
        for (Complex cc : c) {
        	int real = index*2;
        	int imag = index*2+1;
            f[real] = (float)cc.getReal();
            f[imag] = (float)cc.getImaginary();
            index++;
        }
        return f;
    }

}