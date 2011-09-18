package org.apache.commons.math.util;

import java.lang.reflect.Field;

/*
 * FastMath load test - requires that FastMath.USE_PRECOMPUTED_TABLES be set to non-final.
 * 
Sample output from:
java -cp target/classes;target/test-classes org.apache.commons.math.util.FastMathLoadCheck false false
java -cp target/classes;target/test-classes org.apache.commons.math.util.FastMathLoadCheck false true
java -cp target/classes;target/test-classes org.apache.commons.math.util.FastMathLoadCheck true  false
java -cp target/classes;target/test-classes org.apache.commons.math.util.FastMathLoadCheck true  true

Using exp(100); compute=false
new   12155456       9219       5308       5029       5587       5308       5029       5029       5029       5308
Using exp(100); compute=true
new   34929579       9499       5308       5308       5029       5029       5029       5308       5028       5029
Using max(0,0); compute=false
new       5029       3632       3073       3073       3073       3073       3073       3073       3073       3073
Using max(0,0); compute=true
new       5028       3911       3073       3073       3073       3073       3073       3073       3073       3073

 */
public class FastMathLoadCheck {

    private static int LOOPS = 10;
    private static boolean MAX = false;
    private static boolean compute = true;

    public static void main(String[] args) throws Exception {
        if (args.length>0) MAX = Boolean.valueOf(args[0]);
        if (args.length>1) compute = Boolean.valueOf(args[1]);
        if (args.length>2) LOOPS = Integer.valueOf(args[2]);
        p("Using "+ (MAX ? "max(0,0)" : "exp(100)") + "; compute=" + compute+"\n");
        Field usePrecompute = FastMath.class.getDeclaredField("RECOMPUTE_TABLES_AT_RUNTIME");
        usePrecompute.setAccessible(true);
        if (usePrecompute.getBoolean(null) != compute) {
            usePrecompute.setBoolean(null, compute);
        }
        usePrecompute.setAccessible(false);
        test();
    }
    private static void test(){
        p("times");
        for(int i=0; i< LOOPS; i++){
            p(" ");
            long t1 = System.nanoTime();
            if (MAX) {
                FastMath.max(0,0);
            } else {
                FastMath.exp(100);
            }
            long t2 = System.nanoTime();
            p("%10d",t2-t1);
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
