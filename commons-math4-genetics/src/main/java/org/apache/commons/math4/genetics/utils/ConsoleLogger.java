package org.apache.commons.math4.genetics.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.math4.genetics.exception.GeneticException;

public final class ConsoleLogger {

    /** writer instance to log messages to system console. **/
    private static final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

    /**
     * initializer.
     */
    static {

        // Create a shutdown hook to close the writer.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                writer.close();
            } catch (IOException e) {
                throw new GeneticException(e);
            }
        }));
    }

    /**
     * constructor.
     */
    private ConsoleLogger() {
    }

    /**
     * Logs a message.
     * @param message
     */
    public static void log(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new GeneticException(e);
        }
    }

    /**
     * Logs the message after formatting with the args.
     * @param message
     * @param args
     */
    public static void log(String message, Object... args) {
        try {
            writer.write(String.format(message, args));
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new GeneticException(e);
        }
    }

}
