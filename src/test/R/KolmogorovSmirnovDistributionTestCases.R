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
cat("/* ", version$version.string, " */\n\n\n", sep = "")

ns <- c(200, 341, 389)
ps <- c(0.005, 0.02, 0.031111, 0.04)

for (n in ns) {
  for (p in ps) {
    res <- .C("pkolmogorov2x", p = as.double(p), n = as.integer(n), PACKAGE = "stats")$p

    cat("/* formatC(.C(\"pkolmogorov2x\", p = as.double(", p, "), n = as.integer(", n, "), PACKAGE = \"stats\")$p, 40) gives\n", sep = "")
    cat(" * ", formatC(res, digits = 40), "\n", sep = "")
    cat(" */\n")

    cat("dist = new KolmogorovSmirnovDistributionImpl(", n, ");\n", sep = "")
    #cat("Assert.assertEquals(", formatC(res, digits = 40), ", dist.cdf(", p, ", true), TOLERANCE);\n", sep = "")
    cat("Assert.assertEquals(", formatC(res, digits = 40), ", dist.cdf(", p, ", false), TOLERANCE);\n", sep = "")
    cat("\n")

    #cat("System.out.println(\"", formatC(res, digits = 20), " - \" + dist.cdf(", p, ", false) + \" = \" + (", res, " - dist.cdf(", p, ", false)));\n", sep = "")
  }
}

