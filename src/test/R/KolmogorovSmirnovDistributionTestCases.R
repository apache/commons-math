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

