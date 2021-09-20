package org.apache.commons.math4.genetics.utils;

import org.apache.commons.math4.genetics.exception.GeneticException;

public interface ValidationUtils {

    static void checkForNull(String name, Object value) {
        if (value == null) {
            throw new GeneticException(GeneticException.NULL_ARGUMENT, name);
        }
    }
}
