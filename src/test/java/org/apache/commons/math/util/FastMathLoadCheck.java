package org.apache.commons.math.util;

import java.lang.reflect.Field;

/*
 * FastMath load performance test - requires that
 * <ul>
 *  <li>{@code FastMath.RECOMPUTE_TABLES_AT_RUNTIME}</li>
 *  <li>{@code FastMath.LOAD_RESOURCES}</li>
 * </ul>
 * be non-"final".
 * 
 * For example, this shell command:
 * <pre>
 *  $ for max in false true ; do for how in compute resources array; do java -cp target/classes:target/test-classes org.apache.commons.math.util.FastMathLoadCheck $max $how 4 ; done ; done
 * </pre>
 * will produce an output similar to the following:
 * <pre>
 * Using exp(100); how=computeUsing exp(100); how=compute
 *     times       result
 *  43534147 2.688117e+43
 *      4547 2.688117e+43
 *      1970 2.688117e+43
 *      1823 2.688117e+43
 *
 * Using exp(100); how=array
 *     times       result
 *  12596573 2.688117e+43
 *      4484 2.688117e+43
 *      1861 2.688117e+43
 *      1864 2.688117e+43
 *
 * Using exp(100); how=resources
 *     times       result
 *  13087186 2.688117e+43
 *      4974 2.688117e+43
 *      1834 2.688117e+43
 *      1900 2.688117e+43
 *
 * Using max(0,0); how=compute
 *     times       result
 *      3172 0.000000e+00
 *       692 0.000000e+00
 *       385 0.000000e+00
 *       358 0.000000e+00
 *
 * Using max(0,0); how=array
 *     times       result
 *      2746 0.000000e+00
 *       527 0.000000e+00
 *       382 0.000000e+00
 *       390 0.000000e+00
 *
 * Using max(0,0); how=resources
 *     times       result
 *      3762 0.000000e+00
 *       506 0.000000e+00
 *       394 0.000000e+00
 *       364 0.000000e+00
 * </pre>
 */
public class FastMathLoadCheck {
    private final static String COMP = "compute";
    private final static String RES = "resources";
    private final static String ARR = "array";

    private static int LOOPS = 10;
    private static boolean MAX = false;
    private static String how = ARR;

    public static void main(String[] args) throws Exception {
        if (args.length > 0) MAX = Boolean.valueOf(args[0]);
        if (args.length > 1) how = args[1];
        if (args.length > 2) LOOPS = Integer.valueOf(args[2]);
        p("Using "+ (MAX ? "max(0,0)" : "exp(100)") + "; how=" + how + "\n");

        final Field recompute = FastMath.class.getDeclaredField("RECOMPUTE_TABLES_AT_RUNTIME");
        final Field load = FastMath.class.getDeclaredField("LOAD_RESOURCES");
        recompute.setAccessible(true);
        load.setAccessible(true);
        if (how.equals(COMP)) {
            recompute.setBoolean(null, true);
            load.setBoolean(null, false);
        } else if (how.equals(RES)) {
            recompute.setBoolean(null, false);
            load.setBoolean(null, true);
        } else if (how.equals(ARR)) {
            recompute.setBoolean(null, false);
            load.setBoolean(null, false);
        } else {
            throw new IllegalArgumentException("'how' must be 'compute' or 'resources' or 'array'");
        }
        recompute.setAccessible(false);
        load.setAccessible(false);

        test();
    }
    private static void test(){
        p("%9s %12s\n", "times", "result");
        double result;
        for(int i = 0; i < LOOPS; i++) {
            long t1 = System.nanoTime();
            if (MAX) {
                result = FastMath.max(0, 0);
            } else {
                result = FastMath.exp(100);
            }
            long t2 = System.nanoTime();
            p("%9d %e\n", t2 - t1, result);
        }
        p("\n");
    }

    private static void p(String format, Object ... p){
        System.out.printf(format, p);
    }
    private static void p(Object p){
        System.out.print(p);
    }
}
