package org.apache.commons.math;

public class ArgumentOutsideDomainException extends FunctionEvaluationException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -4965972841162580234L;

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param argument  the failing function argument 
     * @param lower lower bound of the domain
     * @param upper upper bound of the domain
     */
    public ArgumentOutsideDomainException(double argument, double lower, double upper) {
        super(argument,
              "Argument {0} outside domain [{1} ; {2}]",
              new Object[] { new Double(argument), new Double(lower), new Double(upper) });
    }

}
