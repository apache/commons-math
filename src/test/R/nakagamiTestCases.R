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
# R source file to validate Nakagami distribution tests in
# org.apache.commons.math4.distribution.NakagamiDistributionTest
#
# To run the test, install R, put this file and testFunctions
# into the same directory, launch R from this directory and then enter
# source("<name-of-this-file>")
#
# R functions used
# dnaka(x, location = 0, scale = 1, log = FALSE, max = TRUE)
# pnaka(q, location = 0, scale = 1, lower.tail = TRUE, max = TRUE)

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
    rDistValues[i] <- pnaka(point, s, m)
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
    rDensityValues[i] <- dnaka(point, s, m)
  }
  output <- c("Density test m = ",m,", s = ", s)
  if (assertEquals(expected, rDensityValues, tol, "Density Values")) {
    displayPadded(output, SUCCEEDED, WIDTH)
  } else {
    displayPadded(output, FAILED, WIDTH)
  }
}

#--------------------------------------------------------------------------
cat("Nakagami test cases\n")

m <- 0.5
s <- 1
distributionPoints <- seq(from = 0, to = 2, by = 0.2)
densityValues <- c(0.0,0.7820853879509118,0.7365402806066467,0.6664492057835993,0.5793831055229655,0.48394144903828673,0.38837210996642585,0.29945493127148964,0.2218416693589111,0.1579003166017883,0.10798193302637613)
distributionValues <- c(0.0,0.15851941887820603,0.3108434832206483,0.45149376449985296,0.5762892028332065,0.6826894921370859,0.7698606595565836,0.8384866815324576,0.8904014166008841,0.9281393617741498,0.9544997361036424)
verifyDistribution(distributionPoints, distributionValues, m, s, tol)
verifyDensity(distributionPoints, densityValues, m, s, tol)

m <- 1
s <- 2
distributionPoints <- seq(from = 0, to = 2, by = 0.2)
densityValues <- c(0.0,0.19603973466135105,0.36924653855465434,0.5011621268467633,0.5809192296589527,0.6065306597126334,0.584102707151966,0.5254355383919593,0.44485968072511056,0.3562176583505064,0.2706705664732254)
distributionValues <- c(0.0,0.0198013266932447,0.07688365361336423,0.16472978858872803,0.273850962926309,0.3934693402873665,0.5132477440400285,0.6246889011486005,0.7219626995468056,0.8021013009163853,0.8646647167633873)
verifyDistribution(distributionPoints, distributionValues, m, s, tol)
verifyDensity(distributionPoints, densityValues, m, s, tol)
