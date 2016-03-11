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
package org.apache.commons.math4.userguide.rng;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.DataOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.math4.rng.UniformRandomProvider;

/**
 * Class that can be used for testing a generator by piping the values
 * returned by its {@link UniformRandomProvider#nextInt()} method to a
 * program that reads {@code int} values from its standard input and
 * writes an analysis report to standard output.
 *
 * <p>
 *  The <a href="http://www.phy.duke.edu/~rgb/General/dieharder.php">
 *  "dieharder"</a> test suite is such a software.
 *  <br>
 *  Example of command line, assuming that "examples.jar" specifies this
 *  class as the "main" class (see {@link #main(String[]) main} method):
 *  <pre><code>
 *   $ java -jar examples.jar \
 *     report/dh_ \
 *     4 \
 *     org.apache.commons.math4.userguide.rng.GeneratorsList \
 *     /usr/bin/dieharder -a -g 200 -Y 1 -k 2
 *  </code></pre>
 * </p>
 */
public class RandomStressTester {
    /** Comment prefix. */
    private static final String C = "# ";
    /** New line. */
    private static final String N = "\n";
    /** Command line. */
    private final List<String> cmdLine;
    /** Output prefix. */
    private final String fileOutputPrefix;

    /**
     * Creates the application.
     *
     * @param cmd Command line.
     * @param outputPrefix Output prefix for file reports.
     */
    private RandomStressTester(List<String> cmd,
                               String outputPrefix) {
        final File exec = new File(cmd.get(0));
        if (!exec.exists() ||
            !exec.canExecute()) {
            throw new RuntimeException("Program is not executable: " + exec);
        }

        cmdLine = new ArrayList<String>(cmd);
        fileOutputPrefix = outputPrefix;

        final File reportDir = new File(fileOutputPrefix).getParentFile();
        if (!reportDir.exists() ||
            !reportDir.isDirectory() ||
            !reportDir.canWrite()) {
            throw new RuntimeException("Invalid output directory: " + reportDir);
        }
    }

    /**
     * Program's entry point.
     *
     * @param args Application's arguments.
     * The order is as follows:
     * <ul>
     *  <li>Output prefix: Filename prefix where the output of the analysis will
     *   written to.  The appended suffix is the index of the instance within the
     *   list of generators to be tested.</li>
     *  <li>Number of threads to use concurrently: One thread will process one of
     *    the generators to be tested.</li>
     *  <li>Name of a class that implements {@code Iterable<UniformRandomProvider>}
     *   (and defines a default constructor): Each generator of the list will be
     *   tested by one instance of the analyzer program</li>
     *  <li>Path to the executable: this is the analyzer software that reads 32-bits
     *   integers from stdin.</li>
     *  <li>All remaining arguments are passed to the executable.</li>
     * </ul>
     */
    public static void main(String[] args) throws Exception {
        final String output = args[0];
        final int numThreads = Integer.valueOf(args[1]);

        final Iterable<UniformRandomProvider> rngList = createGeneratorsList(args[2]);

        final List<String> cmdLine = new ArrayList<>();
        cmdLine.addAll(Arrays.asList(Arrays.copyOfRange(args, 3, args.length)));

        final RandomStressTester app = new RandomStressTester(cmdLine, output);
        app.run(rngList, numThreads);
    }

    /**
     * Creates the tasks and starts the processes.
     *
     * @param generators List of generators to be analyzed.
     * @param numConcurrentTasks Number of concurrent tasks.
     * Twice as many threads will be started: one thread for the RNG and one
     * for the analyzer.
     */
    private void run(Iterable<UniformRandomProvider> generators,
                     int numConcurrentTasks)
        throws IOException {
        // Parallel execution.
        final ExecutorService service = Executors.newFixedThreadPool(numConcurrentTasks);

        // Placeholder (output will be "null").
        final List<Future<?>> execOutput = new ArrayList<Future<?>>();

        // Run tasks.
        int count = 0;
        for (UniformRandomProvider rng : generators) {
            final File output = new File(fileOutputPrefix + (++count));
            final Runnable r = new Task(rng, output);
            execOutput.add(service.submit(r));
        }

        // Wait for completion (ignoring return value).
        try {
            for (Future<?> f : execOutput) {
                try {
                    f.get();
                } catch (ExecutionException e) {
                    System.err.println(e.getCause().getMessage());
                }
            }
        } catch (InterruptedException ignored) {}

        // Terminate all threads.
        service.shutdown();
    }

    /**
     * Creates the list of generators to be tested.
     *
     * @param name Name of the class that contains the generators to be
     * analyzed.
     * @return the list of generators.
     */
    private static Iterable<UniformRandomProvider> createGeneratorsList(String name)
        throws ClassNotFoundException,
               InstantiationException,
               IllegalAccessException {
        return (Iterable<UniformRandomProvider>) Class.forName(name).newInstance();
    }

    /**
     * Pipes random numbers to the standard input of an analyzer.
     */
    private class Task implements Runnable {
        /** Directory for reports of the tester processes. */
        private final File output;
        /** RNG to be tested. */
        private final UniformRandomProvider rng;

        /**
         * Creates the task.
         *
         * @param random RNG to be tested.
         * @param report Report file.
         */
        public Task(UniformRandomProvider random,
                    File report) {
            rng = random;
            output = report;
        }

        /** {@inheritDoc} */
        @Override
            public void run() {
            try {
                // Write header.
                printHeader(output, rng);

                // Start test suite.
                final ProcessBuilder builder = new ProcessBuilder(cmdLine);
                builder.redirectOutput(ProcessBuilder.Redirect.appendTo(output));
                final Process testingProcess = builder.start();
                final DataOutputStream sink = new DataOutputStream(testingProcess.getOutputStream());

                final long startTime = System.nanoTime();

                try {
                    while (true) {
                        sink.writeInt(rng.nextInt());
                    }
                } catch (IOException e) {
                    // Hopefully getting here when the analyzing software terminates.
                }

                final long endTime = System.nanoTime();

                // Write footer.
                printFooter(output, endTime - startTime);

            } catch (IOException e) {
                throw new RuntimeException("Failed to start task: " + e.getMessage());
            }
        }
    }

    /**
     * @param output File.
     * @param rng Generator being tested.
     * @param cmdLine
     */
    private void printHeader(File output,
                             UniformRandomProvider rng)
        throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append(C).append(N);
        sb.append(C).append("RNG: ").append(rng.toString()).append(N);
        sb.append(C).append(N);
        sb.append(C).append("Java: ").append(System.getProperty("java.version")).append(N);
        sb.append(C).append("Runtime: ").append(System.getProperty("java.runtime.version", "?")).append(N);
        sb.append(C).append("JVM: ").append(System.getProperty("java.vm.name"))
            .append(" ").append(System.getProperty("java.vm.version")).append(N);
        sb.append(C).append("OS: ").append(System.getProperty("os.name"))
            .append(" ").append(System.getProperty("os.version"))
            .append(" ").append(System.getProperty("os.arch")).append(N);
        sb.append(C).append(N);

        sb.append(C).append("Analyzer: ");
        for (String s : cmdLine) {
            sb.append(s).append(" ");
        }
        sb.append(N);
        sb.append(C).append(N);

        final PrintWriter w = new PrintWriter(new FileWriter(output, true));
        w.print(sb.toString());
        w.close();
    }

    /**
     * @param output File.
     * @param nanoTime Duration of the run.
     */
    private void printFooter(File output,
                             long nanoTime)
        throws IOException {
        final PrintWriter w = new PrintWriter(new FileWriter(output, true));
        w.println(C);

        final double duration = ((double) nanoTime) * 1e-9 / 60;
        w.println(C + "Test duration: " + duration + " minutes");

        w.println(C);
        w.close();
    }
}
