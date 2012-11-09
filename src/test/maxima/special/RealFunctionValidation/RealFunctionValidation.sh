#!/bin/bash

# Location of the Commons-Math3 jar file
CM3_JAR=$HOME/.m2/repository/org/apache/commons/commons-math3/3.1-SNAPSHOT/commons-math3-3.1-SNAPSHOT.jar

# Location of file RealFunctionValidation.jar
APP_JAR=$HOME/Documents/workspace/commons-math3/src/test/maxima/special/RealFunctionValidation/RealFunctionValidation.jar

java -cp $CM3_JAR:$APP_JAR RealFunctionValidation $1