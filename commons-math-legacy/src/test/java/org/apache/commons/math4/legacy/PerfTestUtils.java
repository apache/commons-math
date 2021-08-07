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
package org.apache.commons.math4.legacy;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.concurrent.Callable;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.sampling.PermutationSampler;
import org.apache.commons.math4.legacy.exception.MathIllegalStateException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.legacy.stat.descriptive.StatisticalSummary;
import org.apache.commons.math4.legacy.stat.descriptive.SummaryStatistics;

/**
 * Simple benchmarking utilities.
 */
public final class PerfTestUtils {
    /** Formatting. */
    private static final int DEFAULT_MAX_NAME_WIDTH = 45;
    /** Formatting. */
    private static final String ELLIPSIS = "...";
    /** Formatting. */
    private static final String TO_STRING_MEMORY_ADDRESS_REGEX = "@\\p{XDigit}{1,8}";
    /** Formatting. */
    private static final String JAVA_IDENTIFIER_REGEX =
        "(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)*\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
    /** Formatting. */
    private static final Pattern JAVA_IDENTIFIER_PATTERN =
        Pattern.compile(JAVA_IDENTIFIER_REGEX);
    /** Nanoseconds to milliseconds conversion factor ({@value}). */
    public static final double NANO_TO_MILLI = 1e-6;
    /** Default number of code repeat per timed block. */
    private static final int DEFAULT_REPEAT_CHUNK = 1000;
    /** Default number of code repeats for computing the average run time. */
    private static final int DEFAULT_REPEAT_STAT = 10000;
    /** RNG. */
    private static UniformRandomProvider rng = RandomSource.WELL_19937_C.create();

    /** No instances. */
    private PerfTestUtils() {}

    /**
     * Timing.
     *
     * @param repeatChunk Each timing measurement will done done for that
     * number of repeats of the code.
     * @param repeatStat Timing will be averaged over that number of runs.
     * @param runGC Call {@code System.gc()} between each timed block. When
     * set to {@code true}, the test will run much slower.
     * @param methods Codes being timed.
     * @return for each of the given {@code methods}, a
     * {@link StatisticalSummary} of the average times (in milliseconds)
     * taken by a single call to the {@code call} method (i.e. the time
     * taken by each timed block divided by {@code repeatChunk}).
     */
    @SafeVarargs
    public static StatisticalSummary[] time(int repeatChunk,
                                            int repeatStat,
                                            boolean runGC,
                                            Callable<Double> ... methods) {
        final double[][][] times = timesAndResults(repeatChunk,
                                                   repeatStat,
                                                   runGC,
                                                   methods);

        final int len = methods.length;
        final StatisticalSummary[] stats = new StatisticalSummary[len];
        for (int j = 0; j < len; j++) {
            final SummaryStatistics s = new SummaryStatistics();
            for (int k = 0; k < repeatStat; k++) {
                s.addValue(times[j][k][0]);
            }
            stats[j] = s.getSummary();
        }

        return stats;
    }

    /**
     * Timing.
     *
     * @param repeatChunk Each timing measurement will done done for that
     * number of repeats of the code.
     * @param repeatStat Timing will be averaged over that number of runs.
     * @param runGC Call {@code System.gc()} between each timed block. When
     * set to {@code true}, the test will run much slower.
     * @param methods Codes being timed.
     * @return for each of the given {@code methods} (first dimension), and
     * each of the {@code repeatStat} runs (second dimension):
     * <ul>
     *  <li>
     *   the average time (in milliseconds) taken by a single call to the
     *   {@code call} method (i.e. the time taken by each timed block divided
     *   by {@code repeatChunk})
     *  </li>
     *  <li>
     *   the result returned by the {@code call} method.
     *  </li>
     * </ul>
     */
    @SafeVarargs
    public static double[][][] timesAndResults(int repeatChunk,
                                               int repeatStat,
                                               boolean runGC,
                                               Callable<Double> ... methods) {
        final int numMethods = methods.length;
        final double[][][] timesAndResults = new double[numMethods][repeatStat][2];

        // Indices into the array containing the methods to benchmark.
        // The purpose is that at each repeat, the "methods" are called in a different order.
        final int[] methodSequence = PermutationSampler.natural(numMethods);

        try {
            for (int k = 0; k < repeatStat; k++) {
                PermutationSampler.shuffle(rng, methodSequence);
                for (int n = 0; n < numMethods; n++) {
                    final int j = methodSequence[n]; // Index of the timed method.

                    if (runGC) {
                        // Try to perform GC outside the timed block.
                        System.gc();
                    }

                    final Callable<Double> r = methods[j];
                    final double[] result = new double[repeatChunk];

                    // Timed block.
                    final long start = System.nanoTime();
                    for (int i = 0; i < repeatChunk; i++) {
                        result[i] = r.call().doubleValue();
                    }
                    final long stop = System.nanoTime();

                    // Collect run time.
                    timesAndResults[j][k][0] = (stop - start) * NANO_TO_MILLI;
                    // Keep track of a randomly selected result.
                    timesAndResults[j][k][1] = result[rng.nextInt(repeatChunk)];
                }
            }
        } catch (Exception e) {
            // Abort benchmarking if codes throw exceptions.
            throw new MathIllegalStateException(LocalizedFormats.SIMPLE_MESSAGE, e.getMessage());
        }

        final double normFactor = 1d / repeatChunk;
        for (int j = 0; j < numMethods; j++) {
            for (int k = 0; k < repeatStat; k++) {
                timesAndResults[j][k][0] *= normFactor;
            }
        }

        return timesAndResults;
    }

    /**
     * Timing and report (to standard output) the average time and standard
     * deviation of a single call.
     * The timing is performed by calling the
     * {@link #time(int,int,boolean,Callable[]) time} method.
     *
     * @param title Title of the test (for the report).
     * @param maxNameWidth Maximum width of the first column of the report.
     * @param repeatChunk Each timing measurement will done done for that
     * number of repeats of the code.
     * @param repeatStat Timing will be averaged over that number of runs.
     * @param runGC Call {@code System.gc()} between each timed block. When
     * set to {@code true}, the test will run much slower.
     * @param methods Codes being timed.
     * @return for each of the given {@code methods}, a statistics of the
     * average times (in milliseconds) taken by a single call to the
     * {@code call} method (i.e. the time taken by each timed block divided
     * by {@code repeatChunk}).
     */
    @SuppressWarnings("boxing")
    public static StatisticalSummary[] timeAndReport(String title,
                                                     int maxNameWidth,
                                                     int repeatChunk,
                                                     int repeatStat,
                                                     boolean runGC,
                                                     RunTest ... methods) {
        // Header format.
        final String hFormat = "%s (calls per timed block: %d, timed blocks: %d, time unit: ms)";

        // TODO: user-defined parameter?
        final boolean removePackageName = false;

        // Width of the longest name.
        int nameLength = 0;
        for (RunTest m : methods) {
            int len = shorten(m.getName(), removePackageName).length();
            if (len > nameLength) {
                nameLength = len;
            }
        }
        final int actualNameLength = nameLength < maxNameWidth ?
            nameLength :
            maxNameWidth;
        final String nameLengthFormat = "%" + actualNameLength + "s";

        // Column format.
        final String cFormat = nameLengthFormat + " %9s %7s %10s %5s %4s %10s";
        // Result format.
        final String format = nameLengthFormat + " %.3e %.1e %.4e %.3f %.2f %.4e";

        System.out.println(String.format(hFormat,
                                         title,
                                         repeatChunk,
                                         repeatStat));
        System.out.println(String.format(cFormat,
                                         "name",
                                         "time/call",
                                         "std dev",
                                         "total time",
                                         "ratio",
                                         "cv",
                                         "difference"));
        final StatisticalSummary[] time = time(repeatChunk,
                                               repeatStat,
                                               runGC,
                                               methods);
        final double refSum = time[0].getSum() * repeatChunk;
        for (int i = 0, max = time.length; i < max; i++) {
            final StatisticalSummary s = time[i];
            final double sum = s.getSum() * repeatChunk;
            final double mean = s.getMean();
            final double sigma = s.getStandardDeviation();
            System.out.println(String.format(format,
                                             truncate(shorten(methods[i].getName(),
                                                              removePackageName),
                                                      actualNameLength,
                                                      ELLIPSIS),
                                             mean,
                                             sigma,
                                             sum,
                                             sum / refSum,
                                             sigma / mean,
                                             sum - refSum));
        }

        return time;
    }

    /**
     * Timing and report (to standard output).
     * This method calls {@link #timeAndReport(String,int,int,int,boolean,RunTest[])
     * timeAndReport(title, 45, 1000, 10000, false, methods)}.
     *
     * @param title Title of the test (for the report).
     * @param methods Codes being timed.
     * @return for each of the given {@code methods}, a statistics of the
     * average times (in milliseconds) taken by a single call to the
     * {@code call} method (i.e. the time taken by each timed block divided
     * by {@code repeatChunk}).
     */
    public static StatisticalSummary[] timeAndReport(String title,
                                                     RunTest ... methods) {
        return timeAndReport(title,
                             DEFAULT_MAX_NAME_WIDTH,
                             DEFAULT_REPEAT_CHUNK,
                             DEFAULT_REPEAT_STAT,
                             false,
                             methods);
    }

    /**
     * Utility class for storing a test label.
     */
    public abstract static class RunTest implements Callable<Double> {
        private final String name;

        /**
         * @param name Test name.
         */
        public RunTest(String name) {
            this.name = name;
        }

        /**
         * @return the name of this test.
         */
        public String getName() {
            return name;
        }

        /** {@inheritDoc} */
        @Override
        public abstract Double call() throws Exception;
    }

    /**
     * Truncates a string so that it will not be longer than the
     * specified length.
     *
     * @param str String to truncate.
     * @param maxLength Maximum length.
     * @param ellipsis String to use in place of the part being removed
     * from the original string.
     * @return the truncated string.
     * @throws NumberIsTooLargeException if the length of {@code ellipsis}
     * is larger than {@code maxLength - 2}.
     */
    private static String truncate(String str,
                                   int maxLength,
                                   String ellipsis) {
        final int ellSize = ellipsis.length();
        if (ellSize > maxLength - 2) {
            throw new NumberIsTooLargeException(ellSize, maxLength - 2, false);
        }

        final int strSize = str.length();
        if (strSize <= maxLength) {
            // Size is OK.
            return str;
        }

        return str.substring(0, maxLength - ellSize) + ellipsis;
    }

    /**
     * Shortens a string.
     * It will shorten package names and remove memory addresses
     * that appear in an instance's name.
     *
     * @param str Original string.
     * @param removePackageName Whether package name part of a
     * fully-qualified name should be removed entirely.
     * @return the shortened string.
     */
    private static String shorten(String str,
                                  boolean removePackageName) {
        final Matcher m = JAVA_IDENTIFIER_PATTERN.matcher(str);
        final StringBuffer sb = new StringBuffer();
        while (m.find()) {
            final MatchResult r = m.toMatchResult();
            m.appendReplacement(sb, shortenPackageName(r.group(),
                                                       removePackageName));
        }
        m.appendTail(sb);

        return sb.toString().replaceAll(TO_STRING_MEMORY_ADDRESS_REGEX, "");
    }

    /**
     * Shortens package part of the name of a class.
     *
     * @param name Class name.
     * @param remove Whether package name part of a fully-qualified
     * name should be removed entirely.
     * @return the shortened name.
     */
    private static String shortenPackageName(String name,
                                             boolean remove) {
        final String[] comp = name.split("\\.");
        final int last = comp.length - 1;

        if (remove) {
            return comp[last];
        }

        final StringBuilder s = new StringBuilder();
        for (int i = 0; i < last; i++) {
            s.append(comp[i].substring(0, 1)).append(".");
        }
        s.append(comp[last]);

        return s.toString();
    }
}
