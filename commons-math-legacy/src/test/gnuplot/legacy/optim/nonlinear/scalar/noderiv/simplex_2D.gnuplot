#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# "Gnuplot" script to display the behaviour of simplex-based optimizers.
#
# Required argument:
#   file -> input file (cf. "SimplexOptimizerTest").
# Optional argument:
#   showSpx -> Number of simplexes to show.
#

set term x11

showSpx = exists("showSpx") ? showSpx : 5

stats file nooutput
numOptim = STATS_blocks
evalColIndex = 1
objColIndex = 2
absObjDiffColIndex = 3
xColIndex = 4
yColIndex = 5

set size 1, 1
set origin 0, 0

lastOptim = numOptim - 1
do for [iOptim = 1:lastOptim] {

  # Evaluations range.
  stats file u evalColIndex nooutput
  numEval = STATS_max

  # Objective function range.
  # Using the absolute value of the difference with the objective function
  # at the optimum in order to be able to display the logarithm even if the
  # objective function can be negative.
  stats file index iOptim u absObjDiffColIndex nooutput
  numSpx = STATS_blank
  minObj = STATS_min
  maxObj = STATS_max

  # x-coordinate range.
  stats file index iOptim u xColIndex nooutput
  xMin = STATS_min
  xMax = STATS_max

  # y-coordinate range.
  stats file index iOptim u yColIndex nooutput
  yMin = STATS_min
  yMax = STATS_max

  lastSpx = numSpx - 1
  do for [iSpx = 0:lastSpx] {
    set multiplot

    # Number of evaluations.
    set size 1, 0.2
    set origin 0, 0.8
    unset xtics

    set title file . " - optimization " . iOptim . " - iteration " . iSpx noenhanced
    plot \
       file index iOptim \
         every ::0::0 \
         u 0:1 \
         w p ps 0.5 lc "black" title "N_{eval}", \
       '' index iOptim \
         every ::0::0:iSpx \
         u 0:1 \
         w lp pt 1 lc "black" lw 2 notitle
    unset title

    # Objective function.
    set size 1, 0.2
    set origin 0, 0.6

    plot \
       file index iOptim \
         every ::0::2 \
         u 0:(log($3)) \
         w l lc "black" title "log_{10}|f(x) - f(optimum)|", \
       '' index iOptim \
         every ::0::2:iSpx \
         u 0:(log($3)) \
         w lp pt 1 lc "black" lw 2 notitle

    # Simplex.
    set size 1, 0.6
    set origin 0, 0
    set xtics

    unset log y
    plot [xMin:xMax][yMin:yMax] \
      file index iOptim \
        every :::(iSpx - showSpx < 0 ? 0 : iSpx - showSpx)::iSpx \
        u xColIndex:yColIndex \
        w l notitle, \
      '' index "Optimum" \
        u 1:2 ps 2.5 pt 4 title "Expected", \
      '' index iOptim \
        every :::lastSpx::lastSpx \
        u xColIndex:yColIndex ps 1.5 pt 6 title "Found"

    unset multiplot
    pause 0.15
  }

  pause 1
}

pause -1
