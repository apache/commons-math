package org.apache.commons.math;

import org.apache.commons.math.ConvergenceException;

public class MaxIterationsExceededException extends ConvergenceException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -2154780004193976271L;

    /** Maximal number of iterations allowed. */
    private int maxIterations;

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param maxIterations maximal number of iterations allowed
     */
    public MaxIterationsExceededException(int maxIterations) {
        super("Maximal number of iterations ({0}) exceeded",
              new Object[] { new Integer(maxIterations) });
        this.maxIterations = maxIterations;
    }

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param argument  the failing function argument 
     * @param pattern format specifier
     * @param arguments format arguments
     */
    public MaxIterationsExceededException(int maxIterations,
                                          String pattern, Object[] arguments) {
        super(pattern, arguments);
        this.maxIterations = maxIterations;
    }

    /** Get the maximal number of iterations allowed.
     * @return maximal number of iterations allowed
     */
    public int getMaxIterations() {
        return maxIterations;
    }

}
