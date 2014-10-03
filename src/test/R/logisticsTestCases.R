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
# R source file to validate Logistics distribution tests in
# org.apache.commons.math3.distribution.LogisticsDistributionTest
#
# To run the test, install R, put this file and testFunctions
# into the same directory, launch R from this directory and then enter
# source("<name-of-this-file>")
#
# R functions used
# dlogis(x, location = 0, scale = 1, log = FALSE, max = TRUE)
# plogis(q, location = 0, scale = 1, lower.tail = TRUE, max = TRUE)

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
    rDistValues[i] <- plogis(point, m, s)
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
    rDensityValues[i] <- dlogis(point, m, s)
  }
  output <- c("Density test m = ",m,", s = ", s)
  if (assertEquals(expected, rDensityValues, tol, "Density Values")) {
    displayPadded(output, SUCCEEDED, WIDTH)
  } else {
    displayPadded(output, FAILED, WIDTH)
  }
}

#--------------------------------------------------------------------------
cat("Logistics test cases\n")

m <- 5
s <- 2
distributionPoints <- c(-5:5)
densityValues <- c(0.0033240283353950773,0.005433114861112618,0.008831353106645559,0.014226511939867782,0.022588329865456065,0.03505185827255408,0.05249679270175326,0.07457322603516643,0.09830596662074093,0.11750185610079725,0.125)
distributionValues <- c(0.0066928509242848554,0.01098694263059318,0.01798620996209156,0.02931223075135632,0.04742587317756678,0.07585818002124355,0.11920292202211755,0.18242552380635635,0.2689414213699951,0.3775406687981454,0.5)
verifyDistribution(distributionPoints, distributionValues, m, s, tol)
verifyDensity(distributionPoints, densityValues, m, s, tol)

m <- 9
s <- 3
distributionPoints <- c(-5:5)
densityValues <- c(0.0030763907488492496,0.004261976157918787,0.005887568737763705,0.008101066534475556,0.01108624138724253,0.01505888657697071,0.020249392063798367,0.026861724758915424,0.03499786180116884,0.04454323746508397,0.0550303365509679)
distributionValues <- c(0.009315959345066693,0.012953727530695871,0.01798620996209156,0.02492442664711404,0.03444519566621118,0.04742587317756678,0.06496916912866407,0.08839967720705845,0.11920292202211755,0.15886910488091516,0.20860852732604496)
verifyDistribution(distributionPoints, distributionValues, m, s, tol)
verifyDensity(distributionPoints, densityValues, m, s, tol)
