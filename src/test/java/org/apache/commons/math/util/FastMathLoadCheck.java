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
 *  $ for max in false true ; do for how in compute resources array; do java -cp target/classes:target/test-classes org.apache.commons.math.util.FastMathLoadCheck $max $how ; done ; done
 * </pre>
 * will produce an output similar to the following:
 * <pre>
 *  Using exp(100); how=compute
 *  times  50955053      4062      1783      1708      1731      1728      1739      1735      1746      1735
 *  Using exp(100); how=resources
 *  times  18467554      4822      1953      1769      1851      1746      1821      1817      1813      1742
 *  Using exp(100); how=array
 *  times   5952415      2960      1839      1776      1720      1847      1839      1780      1788      1742
 *  Using max(0,0); how=compute
 *  times      1596       521       401       352       345       405       393       390       397       382
 *  Using max(0,0); how=resources
 *  times      1517       521       401       386       386       394       363       386       382       383
 *  Using max(0,0); how=array
 *  times      1569       453       398       390       389       394       333       390       334       359
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
        if (how.equals(COMP)) {
            recompute.setAccessible(true);
            recompute.setBoolean(null, true);
            recompute.setAccessible(false);
            load.setAccessible(true);
            load.setBoolean(null, false);
            load.setAccessible(false);
        } else if (how.equals(RES)) {
            recompute.setAccessible(true);
            recompute.setBoolean(null, false);
            recompute.setAccessible(false);
            load.setAccessible(true);
            load.setBoolean(null, true);
            load.setAccessible(false);
        } else if (how.equals(ARR)) {
            recompute.setAccessible(true);
            recompute.setBoolean(null, false);
            recompute.setAccessible(false);
            load.setAccessible(true);
            load.setBoolean(null, false);
            load.setAccessible(false);
        } else {
            throw new IllegalArgumentException("'how' must be 'compute' or 'resources' or 'array'");
        }

        test();
    }
    private static void test(){
        p("times");
        for(int i = 0; i < LOOPS; i++){
            p(" ");
            long t1 = System.nanoTime();
            if (MAX) {
                FastMath.max(0, 0);
            } else {
                FastMath.exp(100);
            }
            long t2 = System.nanoTime();
            p("%9d", t2 - t1);
        }
        p("\n");
    }

    private static void p(String format, Object p){
        System.out.printf(format, p);
    }
    private static void p(Object p){
        System.out.print(p);
    }
}
