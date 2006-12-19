// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.roots;

import org.spaceroots.mantissa.functions.scalar.ComputableFunction;

import java.util.ArrayList;

/** This class implement a reference problem for junit tests. */
public abstract class TestProblem implements ComputableFunction {

  private double a;
  private double b;
  private double expectedRoot;

  protected TestProblem(double a, double b, double expectedRoot) {
    this.a            = a;
    this.b            = b;
    this.expectedRoot = expectedRoot;
  }

  public double getA() {
    return a;
  }

  public double getB() {
    return b;
  }

  public double getExpectedRoot() {
    return expectedRoot;
  }

  public boolean checkResult(double foundRoot, double tol) {
    return Math.abs(foundRoot - expectedRoot) <= tol;
  }

  /** Get the reference problems from G. E. Alefeld, F. A. Potra and Y. Shi. */
  public static TestProblem[] getAPSProblems() {

    ArrayList problems = new ArrayList();

    // problem 1
    problems.add(new APSProblem1(Math.PI / 2, Math.PI, 1.8954942670340));

    // problems 2 to 11
    double[] roots2To11 = {
      3.0229153472731,  6.6837535608081, 11.238701655002, 19.676000080623,
     29.828227326505,  41.906116195289,  55.953595800143, 71.985665586588,
     90.008868539167, 110.02653274833
    };
    for (int k = 0, n = 1; n <= 10; ++n) {
      problems.add(new APSProblems2To11(1.0e-9 + n * n,
                                        (n+1) * (n+1) - 1.0e-9,
                                        roots2To11[k++]));
    }

    // problems 12 to 14
    problems.add(new APSProblems12To14( -40, -9.0, 31.0, 0.0));
    problems.add(new APSProblems12To14(-100, -9.0, 31.0, 0.0));
    problems.add(new APSProblems12To14(-200, -9.0, 31.0, 0.0));

    // problems 15 to 17
    int[] n15 = { 4, 6, 8, 10, 12 };
    double[] roots15 = {
      0.66874030497642, 0.76472449133173, 0.81776543395794,
      0.85133992252078, 0.87448527222117
    };
    for (int k = 0; k < n15.length; ++k) {
      problems.add(new APSProblems15To17(n15[k], 0.2, 0.0, 5.0, roots15[k]));
    }

    int[] n16 = { 4, 6, 8, 10, 12 };
    for (int k = 0; k < n16.length; ++k) {
      problems.add(new APSProblems15To17(n16[k], 1.0, 0.0, 5.0, 1.0));
    }

    int[] n17 = { 8, 10, 12, 14 };
    for (int k = 0; k < n17.length; ++k) {
      problems.add(new APSProblems15To17(n17[k], 1.0, -0.95, 4.05, 1.0));
    }

    // problem 18
    problems.add(new APSProblem18(0.0, 1.5, 0.52359877559830));

    // problem 19
    int[] n19 = { 1, 2, 3, 4, 5, 20, 40, 60, 80, 100 };
    double[] roots19 = {
      0.42247770964124,   0.30669941048320,   0.22370545765466,
      0.17171914751951,   0.13825715505682,   3.4657359020854e-2,
      1.7328679513999e-2, 1.1552453009332e-2, 8.6643397569993e-3,
      6.9314718055995e-3
    };
    for (int k = 0; k < n19.length; ++k) {
      problems.add(new APSProblem19(n19[k], 0.0, 1.0, roots19[k]));
    }

    // problem 20
    int[] n20 = { 5, 10, 20 };
    double[] roots20 = {
      3.8402551840622e-2, 9.9000099980005e-3, 2.4937500390620e-3
    };
    for (int k = 0; k < n20.length; ++k) {
      problems.add(new APSProblem20(n20[k], 0.0, 1.0, roots20[k]));
    }

    // problem 21
    int[] n21 = { 2, 5, 10, 15, 20 };
    double[] roots21 = {
      0.5, 0.34595481584824, 0.24512233375331,
      0.19554762353657, 0.16492095727644
    };
    for (int k = 0; k < n21.length; ++k) {
      problems.add(new APSProblem21(n21[k], 0.0, 1.0, roots21[k]));
    }

    // problem 22
    int[] n22 = { 1, 2, 4, 5, 8, 15, 20 };
    double[] roots22 = {
      0.27550804099948,   0.13775402049974,   1.0305283778156e-2,
      3.6171081789041e-3, 4.1087291849640e-4, 2.5989575892908e-5,
      7.6685951221853e-6
    };
    for (int k = 0; k < n22.length; ++k) {
      problems.add(new APSProblem22(n22[k], 0.0, 1.0, roots22[k]));
    }

    // problem 23
    int[] n23 = { 1, 5, 10, 15, 20 };
    double[] roots23 = {
      0.40105813754155, 0.51615351875793, 0.53952222690842,
      0.54818229434066, 0.55270466667849
    };
    for (int k = 0; k < n23.length; ++k) {
      problems.add(new APSProblem23(n23[k], 0.0, 1.0, roots23[k]));
    }

    // problem 24
    int[] n24 = { 2, 5, 15, 20 };
    for (int k = 0; k < n24.length; ++k) {
      problems.add(new APSProblem24(n24[k], 0.01, 1, 1.0 / n24[k]));
    }

    // problem 25
    int[] n25 = {
       2,  3,  4,  5,  6,
       7,  9, 11, 13, 15,
      17, 19, 21, 23, 25,
      27, 29, 31, 33
    };
    for (int k = 0; k < n25.length; ++k) {
      problems.add(new APSProblem25(n25[k], 1.0, 100.0, n25[k]));
    }

    // problem 26
    problems.add(new APSProblem26(-1.0, 4.0, 0.0));

    // problem 27
    int[] n27 = {
      1,  2,  3,  4,  5,  6,  7,  8,  9,  10,
     11, 12, 13, 14, 15, 16, 17, 18, 19,  20,
     21, 22, 23, 24, 25, 26, 27, 28, 29,  30,
     31, 32, 33, 34, 35, 36, 37, 38, 39,  40
    };
    for (int k = 0; k < n27.length; ++k) {
      problems.add(new APSProblem27(n27[k], -10000.0, Math.PI / 2,
                                    0.62380651896161));
    }

    // problem 28
    int[] n28 = {
       20,  21,  22,  23,  24,  25,  26,  27,  28,   29,
       30,  31,  32,  33,  34,  35,  36,  37,  38,   39, 40,
      100, 200, 300, 400, 500, 600, 700, 800, 900, 1000 };
    double[] roots28 = {
      5.9051305594220e-5, 5.6367155339937e-5, 5.3916409455592e-5,
      5.1669892394942e-5, 4.9603096699145e-5, 4.7695285287639e-5,
      4.5928793239949e-5, 4.4288479195665e-5, 4.2761290257883e-5,
      4.1335913915954e-5, 4.0002497338020e-5, 3.8752419296207e-5,
      3.7578103559958e-5, 3.6472865219959e-5, 3.5430783356532e-5,
      3.4446594929961e-5, 3.3515605877800e-5, 3.2633616249437e-5,
      3.1796856858426e-5, 3.1001935436965e-5, 3.0245790670210e-5,
      1.2277994232462e-5, 6.1695393904409e-6, 4.1198585298293e-6,
      3.0924623877272e-6, 2.4752044261050e-6, 2.0633567678513e-6,
      1.7690120078154e-6, 1.5481615698859e-6, 1.3763345366022e-6,
      1.2388385788997e-6
    };
    for (int k = 0; k < n28.length; ++k) {
      problems.add(new APSProblem28(n28[k], -10000.0, 10000.0, roots28[k]));
    }

    return (TestProblem[]) problems.toArray(new TestProblem[problems.size()]);

  }

  private static class APSProblem1 extends TestProblem {
    private static final long serialVersionUID = -186095948802525864L;
    public APSProblem1(double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
    }
    public double valueAt(double x) {
      return Math.sin(x) - x / 2;
    }
  }

  private static class APSProblems2To11 extends TestProblem {
    private static final long serialVersionUID = -1284328672006328516L;
    public APSProblems2To11(double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
    }
    public double valueAt(double x) {
      double f = 0;
      for (int i = 1; i <= 20; ++i) {
        double n = 2.0 * i - 5.0;
        double d = x - i * i;
        f += n * n / (d * d * d);
      }
      return -2 * f;
    }
  }

  private static class APSProblems12To14 extends TestProblem {
    private static final long serialVersionUID = 3371996034561221313L;
    private int n;
    public APSProblems12To14(int n, double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
      this.n = n;
    }
    public double valueAt(double x) {
      return n * x * Math.exp(-x);
    }
  }

  private static class APSProblems15To17 extends TestProblem {
    private static final long serialVersionUID = -5460543876513796612L;
    private int    n;
    private double u;
    public APSProblems15To17(int n, double u,
                             double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
      this.n = n;
      this.u = u;
    }
    public double valueAt(double x) {
      return Math.pow(x, n) - u;
    }
  }

  private static class APSProblem18 extends TestProblem {
    private static final long serialVersionUID = 6762799934117390438L;
    public APSProblem18(double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
    }
    public double valueAt(double x) {
      return Math.sin(x) - 0.5;
    }
  }

  private static class APSProblem19 extends TestProblem {
    private static final long serialVersionUID = 4962041891152128524L;
    private int n;
    public APSProblem19(int n, double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
      this.n = n;
    }
    public double valueAt(double x) {
      return 2.0 * x * Math.exp(-n) - 2.0 *Math.exp(-n * x) + 1.0;
    }
  }

  private static class APSProblem20 extends TestProblem {
    private static final long serialVersionUID = -7391954140799812791L;
    private int n;
    private int oPoMn2;
    public APSProblem20(int n, double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
      this.n = n;
      int oMn =  1 - n;
      oPoMn2 = 1 + oMn * oMn;
    }
    public double valueAt(double x) {
      double v = 1.0 - n * x;
      return oPoMn2 * x - v * v;
    }
  }

  private static class APSProblem21 extends TestProblem {
    private static final long serialVersionUID = -4160028543895639114L;
    private int n;
    public APSProblem21(int n, double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
      this.n = n;
    }
    public double valueAt(double x) {
      return x * x - Math.pow(1 - x, n);
    }
  }

  private static class APSProblem22 extends TestProblem {
    private static final long serialVersionUID = 3807046732154081146L;
    private int n;
    private int oPoMn4;
    public APSProblem22(int n, double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
      this.n   = n;
      int oMn  = 1 - n;
      int oMn2 = oMn * oMn;
      oPoMn4   = 1 + oMn2 * oMn2;
    }
    public double valueAt(double x) {
      double oMnx  = 1 - n * x;
      double oMnx2 = oMnx * oMnx;
      return oPoMn4 * x - oMnx2 * oMnx2;
    }
  }

  private static class APSProblem23 extends TestProblem {
    private static final long serialVersionUID = -486669213837396921L;
    private int n;
    public APSProblem23(int n, double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
      this.n = n;
    }
    public double valueAt(double x) {
      return (x - 1.0) * Math.exp(-n * x) + Math.pow(x, n);
    }
  }

  private static class APSProblem24 extends TestProblem {
    private static final long serialVersionUID = -628275471717968182L;
    private int n;
    public APSProblem24(int n, double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
      this.n = n;
    }
    public double valueAt(double x) {
      return (n * x - 1.0) / ((n - 1) * x);
    }
  }

  private static class APSProblem25 extends TestProblem {
    private static final long serialVersionUID = 5207170686914959073L;
    private double u;
    private double v;;
    public APSProblem25(int n, double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
      u = 1.0 / n;
      v = Math.pow(n, u);
    }
    public double valueAt(double x) {
      return Math.pow(x, u) - v;
    }
  }

  private static class APSProblem26 extends TestProblem {
    private static final long serialVersionUID = 1063884352586457076L;

    public APSProblem26(double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
    }
    public double valueAt(double x) {
      if (x == 0.0) {
        return 0;
      }
      return x / Math.exp(1 / (x * x));
    }

    // this is a very special case since there is a wide range around
    // the true root (which is 0) for which |f(x)| is smaller than the
    // smallest representable positive number (according to IEEE 754):
    //    f(0.03762210865...) = 2^-1024
    //    f(0.03764056462...) = 2^-1023
    //    f(0.03765904777...) = 2^-1022
    //    f(0.03767755816...) = 2^-1021
    // any root between -0.03768 and +0.03768 should be considered good
    public boolean checkResult(double foundRoot, double tol) {
      return Math.abs(foundRoot) <= 0.03768;
    }

  }

  private static class APSProblem27 extends TestProblem {
    private static final long serialVersionUID = -3549158218723499035L;
    private double u;
    public APSProblem27(int n, double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
      u = n / 20.0;
    }
    public double valueAt(double x) {
      if (x >= 0.0) {
        return (x / 1.5 + Math.sin(x) - 1.0) * u;
      }
      return -u;
    }
  }

  private static class APSProblem28 extends TestProblem {
    private static final long serialVersionUID = -8198306839874267863L;
    private double threshold;
    private static final double yHigh= Math.exp(1.0) - 1.859;
    private int    u;
    public APSProblem28(int n, double a, double b, double expectedRoot) {
      super(a, b, expectedRoot);
      threshold = 0.002 / (1 + n);
      u         = (n + 1) * 500;
    }
    public double valueAt(double x) {
      if (x >= threshold) {
        return yHigh;
      } else if (x >= 0) {
        return Math.exp(u * x) - 1.859;
      } else {
        return -0.859;
      }
    }
  }

}
