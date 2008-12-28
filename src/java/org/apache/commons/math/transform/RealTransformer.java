package org.apache.commons.math.transform;

import java.io.Serializable;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.complex.Complex;

/**
 * Interface for one-dimensional data sets transformations producing real results.
 * <p>Such transforms include {@link FastSineTransformer sine transform},
 * {@link FastCosineTransformer cosine transform} or {@link
 * FastHadamardTransformer Hadamard transform}. {@link FastFourierTransformer
 * Fourier transform} is of a different kind and does not implement this
 * interface since it produces {@link Complex complex} results instead of real
 * ones.
 * </p>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public interface RealTransformer extends Serializable {

    /**
     * Transform the given real data set.
     * @param f the real data array to be transformed (signal)
     * @return the real transformed array (spectrum)
     * @throws IllegalArgumentException if any parameters are invalid
     */
    double[] transform(double f[])
        throws IllegalArgumentException;

    /**
     * Transform the given real function, sampled on the given interval.
     * @param f the function to be sampled and transformed
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the number of sample points
     * @return the real transformed array
     * @throws FunctionEvaluationException if function cannot be evaluated
     * at some point
     * @throws IllegalArgumentException if any parameters are invalid
     */
    double[] transform(UnivariateRealFunction f, double min, double max, int n)
        throws FunctionEvaluationException, IllegalArgumentException;

    /**
     * Inversely transform the given real data set.
     * @param f the real data array to be inversely transformed (spectrum)
     * @return the real inversely transformed array (signal)
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public abstract double[] inversetransform(double f[])
        throws IllegalArgumentException;

    /**
     * Inversely transform the given real function, sampled on the given interval.
     * @param f the function to be sampled and inversely transformed
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the number of sample points
     * @return the real inversely transformed array
     * @throws FunctionEvaluationException if function cannot be evaluated
     * at some point
     * @throws IllegalArgumentException if any parameters are invalid
     */
    double[] inversetransform(UnivariateRealFunction f, double min, double max, int n)
        throws FunctionEvaluationException, IllegalArgumentException;

}