package org.apache.commons.math.fraction;

import org.apache.commons.math.MaxIterationsExceededException;

public class FractionConversionException extends MaxIterationsExceededException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 4588659344016668813L;

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param value double value to convert
     * @param maxIterations maximal number of iterations allowed
     */
    public FractionConversionException(double value, int maxIterations) {
        super(maxIterations,
              "Unable to convert {0} to fraction after {1} iterations",
              new Object[] { new Double(value), new Integer(maxIterations) });
    }

}
