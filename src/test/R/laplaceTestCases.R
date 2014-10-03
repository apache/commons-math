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
# R source file to validate Laplace distribution tests in
# org.apache.commons.math3.distribution.LaplaceDistributionTest
#
# To run the test, install R, put this file and testFunctions
# into the same directory, launch R from this directory and then enter
# source("<name-of-this-file>")
#
# R functions used
# dlaplace(x, location = 0, scale = 1, log = FALSE, max = TRUE)
# plaplace(q, location = 0, scale = 1, lower.tail = TRUE, max = TRUE)

#-----------------------------------------------------------------------------
tol <- 1E-9

# Function definitions

source("testFunctions")           # utility test functions
library("VGAM")

# function to verify distribution computations
verifyDistribution <- function(points, expected, m, s, tol) {
  rDistValues <- rep(0, length(points))
  i <- 0
  for (point in points) {
    i <- i + 1
    rDistValues[i] <- plaplace(point, m, s)
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
    rDensityValues[i] <- dlaplace(point, m, s)
  }
  output <- c("Density test m = ",m,", s = ", s)
  if (assertEquals(expected, rDensityValues, tol, "Density Values")) {
    displayPadded(output, SUCCEEDED, WIDTH)
  } else {
    displayPadded(output, FAILED, WIDTH)
  }
}

#--------------------------------------------------------------------------
cat("Laplace test cases\n")

m <- 0
s <- 1
distributionPoints <- c(-5:5)
densityValues <- c(0.0033689734995427335,0.00915781944436709,0.024893534183931972,0.06766764161830635,0.18393972058572117,0.5,0.18393972058572117,0.06766764161830635,0.024893534183931972,0.00915781944436709,0.0033689734995427335)
distributionValues <- c(0.0033689734995427335,0.00915781944436709,0.024893534183931972,0.06766764161830635,0.18393972058572117,0.5,0.8160602794142788,0.9323323583816936,0.9751064658160681,0.9908421805556329,0.9966310265004573)
verifyDistribution(distributionPoints, distributionValues, m, s, tol)
verifyDensity(distributionPoints, densityValues, m, s, tol)

m <- -5
s <- 4
distributionPoints <- c(-5:5)
densityValues <- c(0.125,0.09735009788392561,0.07581633246407918,0.059045819092626836,0.04598493014643029,0.03581309960752376,0.027891270018553727,0.021721742931305642,0.016916910404576588,0.013174903070233042,0.01026062482798735)
distributionValues <- c(0.5,0.6105996084642975,0.6967346701436833,0.7638167236294926,0.8160602794142788,0.8567476015699049,0.888434919925785,0.9131130282747775,0.9323323583816936,0.9473003877190679,0.9589575006880506)
verifyDistribution(distributionPoints, distributionValues, m, s, tol)
verifyDensity(distributionPoints, densityValues, m, s, tol)
