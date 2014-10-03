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
# R source file to validate Gumbel distribution tests in
# org.apache.commons.math3.distribution.GumbelDistributionTest
#
# To run the test, install R, put this file and testFunctions
# into the same directory, launch R from this directory and then enter
# source("<name-of-this-file>")
#
# R functions used
# dgumbel(x, location = 0, scale = 1, log = FALSE, max = TRUE)
# pgumbel(q, location = 0, scale = 1, lower.tail = TRUE, max = TRUE)

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
        rDistValues[i] <- pgumbel(point, m, s)
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
        rDensityValues[i] <- dgumbel(point, m, s)
    }
    output <- c("Density test m = ",m,", s = ", s)
    if (assertEquals(expected, rDensityValues, tol, "Density Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }
}

#--------------------------------------------------------------------------
cat("Gumbel test cases\n")

m <- 0.5
s <- 2
distributionPoints <- c(-5:5)
densityValues <- c(1.2582621126545528E-6,3.5946885566568164E-4,0.009115765822384943,0.05321099995044945,0.12743521041151834,0.17778637369097208,0.17871767308609124,0.14726615762017733,0.10756585897012155,0.07302735923472656,0.047427815138561126)
distributionValues <- c(1.6087601139887782E-7,7.577547728260715E-5,0.003168165149053243,0.03049041346306221,0.12039226207982957,0.27692033409990896,0.4589560693076638,0.6235249162568004,0.7508834766393948,0.8404868737475784,0.8999651626606278)
verifyDistribution(distributionPoints, distributionValues, m, s, tol)
verifyDensity(distributionPoints, densityValues, m, s, tol)

m <- 1.5
s <- 3
distributionPoints <- c(-5:5)
densityValues <- c(4.707967970909721E-4,0.004005928431315734,0.01690237120332691,0.04314381688828758,0.07682272023182242,0.10568064035931406,0.12083781131699158,0.12102469295161239,0.11023476629680602,0.09380437047469757,0.07602582610501195)
distributionValues <- c(1.6180181049060529E-4,0.0019214004612365587,0.011314286380459627,0.04030537101298144,0.10016104975617146,0.1922956455479649,0.30686099686684076,0.42892134369676455,0.545239211892605,0.6475247847679736,0.7324184876698198)
verifyDistribution(distributionPoints, distributionValues, m, s, tol)
verifyDensity(distributionPoints, densityValues, m, s, tol)
