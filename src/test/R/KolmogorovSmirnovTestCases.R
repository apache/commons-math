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
# R source file to validate KolmogorovSmirnov tests in
# org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest
#
# To run the test, install R, put this file and testFunctions
# into the same directory, launch R from this directory and then enter
# source("<name-of-this-file>")
#
# NOTE: the 2-sample bootstrap test requires the "Matching" library
## https://cran.r-project.org/web/packages/Matching/index.html
## See http://sekhon.berkeley.edu/matching for additional documentation.
## Jasjeet S. Sekhon. 2011. ``Multivariate and Propensity Score Matching
## Software with Automated Balance Optimization: The Matching package for R.''
## Journal of Statistical Software, 42(7): 1-52.
#
#------------------------------------------------------------------------------
tol <- 1E-14                     # error tolerance for tests
#------------------------------------------------------------------------------
# Function definitions

source("testFunctions")           # utility test functions
require("Matching")               # for ks.boot

verifyOneSampleGaussian <- function(data, expectedP, expectedD, mean, sigma, exact, tol, desc) {
    results <- ks.test(data, "pnorm", mean, sigma, exact = exact)
    if (assertEquals(expectedP, results$p.value, tol, "p-value")) {
        displayPadded(c(desc," p-value test"), SUCCEEDED, WIDTH)
    } else {
        displayPadded(c(desc, " p-value test"), FAILED, WIDTH)
    }
    if (assertEquals(expectedD, results$statistic, tol, "D statistic value")) {
        displayPadded(c(desc," D statistic test"), SUCCEEDED, WIDTH)
    } else {
        displayPadded(c(desc, " D statistic test"), FAILED, WIDTH)
    }
}

verifyOneSampleUniform <- function(data, expectedP, expectedD, min, max, exact, tol, desc) {
    results <- ks.test(data, "punif", min, max, exact = exact)
    if (assertEquals(expectedP, results$p.value, tol, "p-value")) {
        displayPadded(c(desc," p-value test"), SUCCEEDED, WIDTH)
    } else {
        displayPadded(c(desc, " p-value test"), FAILED, WIDTH)
    }
    if (assertEquals(expectedD, results$statistic, tol, "D statistic value")) {
        displayPadded(c(desc," D statistic test"), SUCCEEDED, WIDTH)
    } else {
        displayPadded(c(desc, " D statistic test"), FAILED, WIDTH)
    }
}

verifyTwoSampleLargeSamples <- function(sample1, sample2, expectedP, expectedD, tol, desc) {
    results <- ks.test(sample1, sample2)
    if (assertEquals(expectedP, results$p.value, tol, "p-value")) {
        displayPadded(c(desc," p-value test"), SUCCEEDED, WIDTH)
    } else {
        displayPadded(c(desc, " p-value test"), FAILED, WIDTH)
    }
    if (assertEquals(expectedD, results$statistic, tol, "D statistic value")) {
        displayPadded(c(desc," D statistic test"), SUCCEEDED, WIDTH)
    } else {
        displayPadded(c(desc, " D statistic test"), FAILED, WIDTH)
    }
}

verifyTwoSampleSmallSamplesExact <- function(sample1, sample2, expectedP, expectedD, tol, desc) {
    results <- ks.test(sample1, sample2, exact = TRUE)
    if (assertEquals(expectedP, results$p.value, tol, "p-value")) {
        displayPadded(c(desc," p-value test"), SUCCEEDED, WIDTH)
    } else {
        displayPadded(c(desc, " p-value test"), FAILED, WIDTH)
    }
    if (assertEquals(expectedD, results$statistic, tol, "D statistic value")) {
        displayPadded(c(desc," D statistic test"), SUCCEEDED, WIDTH)
    } else {
        displayPadded(c(desc, " D statistic test"), FAILED, WIDTH)
    }
}

verifyTwoSampleBootstrap <- function(sample1, sample2, expectedP, tol, desc) {
    results <- ks.boot(sample1, sample2,nboots=10000 )
    if (assertEquals(expectedP, results$ks.boot.pvalue, tol, "p-value")) {
        displayPadded(c(desc, " p-value test"), SUCCEEDED, WIDTH)
    } else {
        displayPadded(c(desc, " p-value test"), FAILED, WIDTH)
    }
}

cat("KolmogorovSmirnovTest test cases\n")

gaussian <- c(0.26055895, -0.63665233, 1.51221323, 0.61246988, -0.03013003, -1.73025682,
  -0.51435805, 0.70494168, 0.18242945, 0.94734336, -0.04286604, -0.37931719, -1.07026403, -2.05861425,
   0.11201862, 0.71400136, -0.52122185, -0.02478725, -1.86811649, -1.79907688, 0.15046279, 1.32390193,
   1.55889719, 1.83149171, -0.03948003, -0.98579207, -0.76790540, 0.89080682, 0.19532153, 0.40692841,
   0.15047336, -0.58546562, -0.39865469, 0.77604271, -0.65188221, -1.80368554, 0.65273365, -0.75283102,
  -1.91022150, -0.07640869, -1.08681188, -0.89270600, 2.09017508,  0.43907981, 0.10744033, -0.70961218,
   1.15707300, 0.44560525, -2.04593349, 0.53816843, -0.08366640,  0.24652218, 1.80549401, -0.99220707,
  -1.14589408, -0.27170290, -0.49696855, 0.00968353, -1.87113545, -1.91116529, 0.97151891, -0.73576115,
  -0.59437029, 0.72148436, 0.01747695, -0.62601157, -1.00971538, -1.42691397, 1.03250131, -0.30672627,
  -0.15353992, -1.19976069, -0.68364218, 0.37525652, -0.46592881, -0.52116168, -0.17162202, 1.04679215,
   0.25165971, -0.04125231, -0.23756244, -0.93389975, 0.75551407, 0.08347445, -0.27482228, -0.4717632,
  -0.1867746, -0.1166976, 0.5763333, 0.1307952, 0.7630584, -0.3616248, 2.1383790,-0.7946630,
   0.0231885, 0.7919195, 1.6057144, -0.3802508, 0.1229078, 1.5252901, -0.8543149, 0.3025040)

shortGaussian <- gaussian[1:50]

gaussian2 <- c(2.88041498038308, -0.632349445671017, 0.402121295225571, 0.692626364613243, 1.30693446815426,
  -0.714176317131286, -0.233169206599583, 1.09113298322107, -1.53149079994305, 1.23259966205809,
   1.01389927412503, 0.0143898711497477, -0.512813545447559, 2.79364360835469, 0.662008875538092,
   1.04861546834788, -0.321280099931466, 0.250296656278743, 1.75820367603736, -2.31433523590905,
  -0.462694696086403, 0.187725700950191, -2.24410950019152, 2.83473751105445, 0.252460174391016,
   1.39051945380281, -1.56270144203134, 0.998522814471644, -1.50147469080896, 0.145307533554146,
   0.469089457043406, -0.0914780723809334, -0.123446939266548, -0.610513388160565, -3.71548343891957,
  -0.329577317349478, -0.312973794075871, 2.02051909758923, 2.85214308266271, 0.0193222002327237,
  -0.0322422268266562, 0.514736012106768, 0.231484953375887, -2.22468798953629, 1.42197716075595,
   2.69988043856357, 0.0443757119128293, 0.721536984407798, -0.0445688839903234, -0.294372724550705,
   0.234041580912698, -0.868973119365727, 1.3524893453845, -0.931054600134503, -0.263514296006792,
   0.540949457402918, -0.882544288773685, -0.34148675747989, 1.56664494810034, 2.19850536566584,
  -0.667972122928022, -0.70889669526203, -0.00251758193079668, 2.39527162977682, -2.7559594317269,
  -0.547393502656671, -2.62144031572617, 2.81504147017922, -1.02036850201042, -1.00713927602786,
  -0.520197775122254, 1.00625480138649, 2.46756916531313, 1.64364743727799, 0.704545210648595,
  -0.425885789416992, -1.78387854908546, -0.286783886710481, 0.404183648369076, -0.369324280845769,
  -0.0391185138840443, 2.41257787857293, 2.49744281317859, -0.826964496939021, -0.792555379958975,
   1.81097685787403, -0.475014580016638, 1.23387615291805, 0.646615294802053, 1.88496377454523, 1.20390698380814,
  -0.27812153371728, 2.50149494533101, 0.406964323253817, -1.72253451309982, 1.98432494184332, 2.2223658560333,
   0.393086362404685, -0.504073151377089, -0.0484610869883821)

uniform <- c(0.7930305, 0.6424382, 0.8747699, 0.7156518, 0.1845909, 0.2022326,
   0.4877206, 0.8928752, 0.2293062, 0.4222006, 0.1610459, 0.2830535, 0.9946345, 0.7329499,
   0.26411126, 0.87958133, 0.29827437, 0.39185988, 0.38351185, 0.36359611, 0.48646472, 0.05577866,
   0.56152250, 0.52672013, 0.13171783, 0.95864085, 0.03060207, 0.33514887, 0.72508148, 0.38901437,
   0.9978665, 0.5981300, 0.1065388, 0.7036991, 0.1071584, 0.4423963, 0.1107071, 0.6437221,
   0.58523872, 0.05044634, 0.65999539, 0.37367260, 0.73270024, 0.47473755, 0.74661163, 0.50765549,
   0.05377347, 0.40998009, 0.55235182, 0.21361998, 0.63117971, 0.18109222, 0.89153510, 0.23203248,
   0.6177106, 0.6856418, 0.2158557, 0.9870501, 0.2036914, 0.2100311, 0.9065020, 0.7459159,
   0.56631790, 0.06753629, 0.39684629, 0.52504615, 0.14199103, 0.78551120, 0.90503321, 0.80452362,
   0.9960115, 0.8172592, 0.5831134, 0.8794187, 0.2021501, 0.2923505, 0.9561824, 0.8792248,
   0.85201008, 0.02945562, 0.26200374, 0.11382818, 0.17238856, 0.36449473, 0.69688273, 0.96216330,
   0.4859432,0.4503438, 0.1917656, 0.8357845, 0.9957812, 0.4633570, 0.8654599, 0.4597996,
   0.68190289, 0.58887855, 0.09359396, 0.98081979, 0.73659533, 0.89344777, 0.18903099, 0.97660425)

smallSample1 <- c(6, 7, 9, 13, 19, 21, 22, 23, 24)
smallSample2 <- c(10, 11, 12, 16, 20, 27, 28, 32, 44, 54)
smallSample3 <- c(6, 7, 9, 13, 19, 21, 22, 23, 24, 29, 30, 34, 36, 41, 45, 47, 51, 63, 33, 91)
smallSample4 <- c(10, 11, 12, 16, 20, 27, 28, 32, 44, 54, 56, 57, 64, 69, 71, 80, 81, 88, 90)
smallSample5 <- c(-10, -5, 17, 21, 22, 23, 24, 30, 44, 50, 56, 57, 59, 67, 73, 75, 77, 78, 79, 80, 81, 83, 84, 85, 88, 90,
                   92, 93, 94, 95, 98, 100, 101, 103, 105, 110)
smallSample6 <- c(-2, -1, 0, 10, 14, 15, 16, 20, 25, 26, 27, 31, 32, 33, 34, 45, 47, 48, 51, 52, 53, 54, 60, 61, 62, 63,
                  74, 82, 106, 107, 109, 11, 112, 113, 114)
bootSample1 <- c(0, 2, 4, 6, 8, 8, 10, 15, 22, 30, 33, 36, 38)
bootSample2 <- c(9, 17, 20, 33, 40, 51, 60, 60, 72, 90, 101)
roundingSample1 <- c(2,4,6,8,9,10,11,12,13)
roundingSample2 <- c(0,1,3,5,7)

shortUniform <- uniform[1:20]

verifyOneSampleGaussian(gaussian, 0.3172069207622391, 0.0932947561266756, 0, 1,
TRUE, tol, "One sample gaussian - gaussian values")

verifyOneSampleGaussian(shortGaussian, 0.683736463728347, 0.09820779969463278, 0, 1,
TRUE, tol, "One sample gaussian - gaussian values - small sample")

verifyOneSampleGaussian(uniform, 8.881784197001252E-16, 0.5117493931609258, 0, 1,
TRUE, tol, "One sample gaussian - uniform values")

verifyOneSampleUniform(uniform, 8.881784197001252E-16, 0.5400666982352942, -0.5, 0.5,
TRUE, tol, "One sample uniform - uniform values")

verifyOneSampleUniform(shortUniform, 4.117594598618268E-9, 0.6610459, -0.5, 0.5,
TRUE, tol, "One sample uniform - uniform values - small sample")

verifyOneSampleUniform(gaussian, 4.9405812774239166E-11, 0.3401058049019608, -0.5, 0.5,
TRUE, tol, "One sample uniform - unit normal values")

verifyTwoSampleLargeSamples(gaussian, gaussian2, 0.0319983962391632, 0.202352941176471, tol,
"Two sample N(0, 1) vs N(0, 1.6)")

verifyTwoSampleSmallSamplesExact(smallSample1, smallSample2, 0.105577085453247, .5, tol,
"Two sample small samples exact 1")

verifyTwoSampleSmallSamplesExact(smallSample3, smallSample4, 0.046298660942952,  0.426315789473684, tol,
"Two sample small samples exact 2")

verifyTwoSampleSmallSamplesExact(smallSample5, smallSample6, 0.00300743602233366, 0.41031746031746, tol,
"Two sample small samples exact 3")

verifyTwoSampleBootstrap(bootSample1, bootSample2, 0.0059, 1E-3, "Two sample bootstrap - isolated failures possible")
verifyTwoSampleBootstrap(gaussian, gaussian2, 0.0237, 1E-2, "Two sample bootstrap - isolated failures possible")
verifyTwoSampleBootstrap(roundingSample1, roundingSample2, 0.06303, 1E-2, "Two sample bootstrap - isolated failures possible")

displayDashes(WIDTH)


