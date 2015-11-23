# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#------------------------------------------------------------------------------
# R source file to validate Lévy distribution tests in
# org.apache.commons.math4.distribution.LevyDistributionTest
#
# To run the test, install R, put this file and testFunctions
# into the same directory, launch R from this directory and then enter
# source("<name-of-this-file>")
#
# R functions used
# dlevy(q, m=0, s=1, log=FALSE)
# plevy(q, m=0, s=1)
# qlevy(p, m=0, s=1)
#-----------------------------------------------------------------------------
tol <- 1E-9

# Function definitions

source("testFunctions")           # utility test functions

# function to verify distribution computations

verifyDistribution <- function(points, expected, m, s, tol) {
 rDistValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDistValues[i] <- plevy(point, m, s)
    }
    output <- c("Distribution test m = ",m,", s = ", s)
    if (assertEquals(expected, rDistValues, tol, "Distribution Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }
}

# function to verify density computations

verifyDensity <- function(points, expected, m, s, tol) {
 rDensityValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDensityValues[i] <- dlevy(point, m, s, log=FALSE)
    }
    output <- c("Density test m = ",m,", s = ", s)
    if (assertEquals(expected, rDensityValues, tol, "Density Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }
}

#--------------------------------------------------------------------------
cat("Levy test cases\n")

m <- 1.2
s <- 0.4
distributionPoints <- c(1.2001, 1.21, 1.225, 1.25, 1.3, 1.9, 3.4, 5.6)
densityValues <- c(0.0, 5.200563737654472E-7, 0.021412836122382383, 0.4133397070818418, 1.0798193302637613, 0.3237493191610873, 0.07060325500936372, 0.026122839883975738)
distributionValues <- c(0.0, 2.539628589470901E-10, 6.334248366624259E-5, 0.004677734981047284, 0.04550026389635843, 0.4496917979688907, 0.6698153575994166, 0.763024600552995)
verifyDistribution(distributionPoints, distributionValues, m, s, tol)
verifyDensity(distributionPoints, densityValues, m, s, tol)

m <- 5
s <- 1.3
distributionPoints <- c(5.0001, 6, 7, 8, 9, 10, 11, 12, 13, 14)
densityValues <- c(0.0, 0.23745992633364185, 0.1161959636020616, 0.07048597672583455, 0.04833023442399538, 0.03572468867742048, 0.02777194506550441, 0.022382435270909086, 0.018533623436073274, 0.0156730047506865)
distributionValues <- c(0.0, 0.25421322360396437, 0.42011267955064, 0.5103578488686935, 0.5686182086635944, 0.6101201547975077, 0.6415915735304425, 0.6665077778509312, 0.6868651803414656, 0.7039020091632311)
verifyDistribution(distributionPoints, distributionValues, m, s, tol)
verifyDensity(distributionPoints, densityValues, m, s, tol)

displayDashes(WIDTH)
