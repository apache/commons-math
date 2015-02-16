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

Validation of real functions
============================

This document details the procedure used in Commons-Math 3 to assess the
accuracy of the implementations of special functions. It is a two-step process

1. reference values are computed with a multi-precision software (for example,
   the Maxima Computer Algebra System) [1],
2. these reference values are compared with the Commons-Math3 implementation.
   The accuracy is computed in ulps.

This process relies on a small Java application, called RealFunctionValidation,
which can be found in $CM3_SRC/src/test/maxima/special, where $CM3_SRC is the
root directory to the source of Commons-Math 3


Compilation of RealFunctionValidation
-------------------------------------

Change to the relevant directory

  cd $CM3_SRC/src/test/maxima/special/RealFunctionValidation

Compile the source file. The jar file of Commons-Math3 should be included in
your classpath. If it is installed in your local maven repository, the
following command should work

  javac -classpath $HOME/.m2/repository/org/apache/commons/commons-math4/4.0-SNAPSHOT/commons-math4-4.0-SNAPSHOT.jar RealFunctionValidation.java

Create a jar file

  jar cfm RealFunctionValidation.jar MANIFEST.txt RealFunctionValidation*.class

Remove the unused *.class files

  rm *.class


Invocation of the application RealFunctionValidation
----------------------------------------------------

The java application comes with a shell script, RealFunctionValidaton.sh. You
should edit this file, and change the variables
- CM3_JAR: full path to the Commons-Math 3 jar file,
- APP_JAR: full path to the RealFunctionValidation application jar file.

Invoking this application is then very simple. For example, to validate the
implementation of Gamma.logGamma, change to directory reference

  cd $CM3_SRC/src/test/maxima/special/reference

and run the application

  ../RealFunctionValidation/RealFunctionValidation.sh logGamma.properties


Syntax of the *.properties files
--------------------------------

Parameters of the RealFunctionValidation application are specified through a
standard Java properties file. The following keys must be specified in this
file

- method: the fully qualified name to the function to be validated. This
  function should be static, take only primitive arguments, and return double.
- signature: this key is necessary to discriminate functions with same name.
  The signature should be specified as in a plain java file. For example
  signature = double, int, float
- inputFileMask: the name of the binary input file(s) containing the
  high-accuracy reference values. The format of this file is described in
  the next section. It is possible to specify multiple input files, which are
  indexed by an integer. Then this key should really be understood as a format
  string. In other words, the name of the file with index i is given by
  String.format(inputFileMask, i)
- outputFileMask: the name of the binary output file(s) containing the
  reference values, the values computed through the specified method, and
  the error (in ulps). The format of this file is described in the next section.  As for the input files, it is possible to specify multiple output files.
- from: the first index
- to: the last index (exclusive)
- by: the increment

As an example, here is the properties file for evaluation of
double Gamma.logGamma(double)

method=org.apache.commons.math4.special.Gamma.logGamma
signature=double
inputFileMask=logGamma-%02d.dat
outputFileMask=logGamma-out-%02d.dat
from=1
to=5
by=1

Format of the input and output binary files
-------------------------------------------

The reference values are saved in a binary file
- for a unary function f(x), the data is stored as follows
  x[0], f(x[0]), x[1], f(x[1]), ...
- for a binary function f(x, y), the data is stored as follows
  x[0], y[0], f(x[0], y[0]), x[1], y[1], f(x[1], y[1]), ...
- and similar storage pattern for a n-ary function.

The parameters x[i], y[i], ... can be of arbitrary (primitive) type. The return
value f(x[i], y[i], ...) must be of type double.

The output files are also saved in a binary file
- for a unary function f(x), the data is stored as follows
  x[0], reference value of f(x[0]), actual value of f(x[0], y[0]),
  error in ulps, x[1], y[1], reference value of f(x[1], y[1]), actual value of
  f(x[1], y[1]), error in ulps, ...
- for a binary function f(x, y), the data is stored as follows
  x[0], y[0], reference value of f(x[0], y[0]), actual value of f(x[0], y[0]),
  error in ulps, x[1], y[1], reference value of f(x[1], y[1]), actual value of
  f(x[1], y[1]), error in ulps, ...

The application also prints on the standard output some statistics about the
error.

References
----------

[1] http://maxima.sourceforge.net/
