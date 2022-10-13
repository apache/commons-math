/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math4.legacy.optim.linear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.legacy.linear.RealVector;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.numbers.core.Precision;

/**
 * A tableau for use in the Simplex method.
 *
 * <p>
 * Example:
 * <pre>
 *   W |  Z |  x1 |  x2 |  x- | s1 |  s2 |  a1 |  RHS
 * ---------------------------------------------------
 *  -1    0    0     0     0     0     0     1     0   &lt;= phase 1 objective
 *   0    1   -15   -10    0     0     0     0     0   &lt;= phase 2 objective
 *   0    0    1     0     0     1     0     0     2   &lt;= constraint 1
 *   0    0    0     1     0     0     1     0     3   &lt;= constraint 2
 *   0    0    1     1     0     0     0     1     4   &lt;= constraint 3
 * </pre>
 * W: Phase 1 objective function<br>
 * Z: Phase 2 objective function<br>
 * x1 &amp; x2: Decision variables<br>
 * x-: Extra decision variable to allow for negative values<br>
 * s1 &amp; s2: Slack/Surplus variables<br>
 * a1: Artificial variable<br>
 * RHS: Right hand side<br>
 *
 * Note on usage and safety:
 * The class is package private. It is not meant for public usage.
 * The core data structure, the tableau field, is mutated internally and
 * even reallocated when necessary.
 * Proper usage of this class is demonstrated in SimplexSolver,
 * where the class is only ever constructed in a method (never a field
 * of an object), and its lifetime, is therefore bound to a single thread (the
 * thread that's invoking the method).
 *
 * @since 2.0
 */
class SimplexTableau {

    /** Column label for negative vars. */
    private static final String NEGATIVE_VAR_COLUMN_LABEL = "x-";
    /** bit mask for IEEE double exponent. */
    private static final long EXPN = 0x7ff0000000000000L;
    /** bit mask for IEEE double mantissa and sign. */
    private static final long FRAC = 0x800fffffffffffffL;
    /** max IEEE exponent is 2047. */
    private static final int MAX_IEEE_EXP = 2047;
    /** min IEEE exponent is 0. */
    private static final int MIN_IEEE_EXP = 0;
    /** IEEE exponent is kept in an offset form, 1023 is zero. */
    private static final int OFFSET_IEEE_EXP = 1023;
    /** double exponent shift per IEEE standard. */
    private static final int IEEE_EXPONENT_SHIFT = 52;

    /** Linear objective function. */
    private final LinearObjectiveFunction f;

    /** Linear constraints. */
    private final List<LinearConstraint> constraints;

    /** Whether to restrict the variables to non-negative values. */
    private final boolean restrictToNonNegative;

    /** The variables each column represents. */
    private final List<String> columnLabels = new ArrayList<>();

    /** Simple tableau. */
    private Array2DRowRealMatrix tableau;

    /** Number of decision variables. */
    private final int numDecisionVariables;

    /** Number of slack variables. */
    private final int numSlackVariables;

    /** Number of artificial variables. */
    private int numArtificialVariables;

    /** Amount of error to accept when checking for optimality. */
    private final double epsilon;

    /** Amount of error to accept in floating point comparisons. */
    private final int maxUlps;

    /** Maps basic variables to row they are basic in. */
    private int[] basicVariables;

    /** Maps rows to their corresponding basic variables. */
    private int[] basicRows;

    /** changes in floating point exponent to scale the input. */
    private int[] variableExpChange;

    /**
     * Builds a tableau for a linear problem.
     *
     * @param f Linear objective function.
     * @param constraints Linear constraints.
     * @param goalType Optimization goal: either {@link GoalType#MAXIMIZE}
     * or {@link GoalType#MINIMIZE}.
     * @param restrictToNonNegative Whether to restrict the variables to non-negative values.
     * @param epsilon Amount of error to accept when checking for optimality.
     * @throws DimensionMismatchException if the dimension of the constraints does not match the
     *   dimension of the objective function
     */
    SimplexTableau(final LinearObjectiveFunction f,
                   final Collection<LinearConstraint> constraints,
                   final GoalType goalType,
                   final boolean restrictToNonNegative,
                   final double epsilon) {
        this(f, constraints, goalType, restrictToNonNegative, epsilon, SimplexSolver.DEFAULT_ULPS);
    }

    /**
     * Build a tableau for a linear problem.
     * @param f linear objective function
     * @param constraints linear constraints
     * @param goalType type of optimization goal: either {@link GoalType#MAXIMIZE} or {@link GoalType#MINIMIZE}
     * @param restrictToNonNegative whether to restrict the variables to non-negative values
     * @param epsilon amount of error to accept when checking for optimality
     * @param maxUlps amount of error to accept in floating point comparisons
     * @throws DimensionMismatchException if the dimension of the constraints does not match the
     *   dimension of the objective function
     */
    SimplexTableau(final LinearObjectiveFunction f,
                   final Collection<LinearConstraint> constraints,
                   final GoalType goalType,
                   final boolean restrictToNonNegative,
                   final double epsilon,
                   final int maxUlps) throws DimensionMismatchException {
        checkDimensions(f, constraints);
        this.f                      = f;
        this.constraints            = normalizeConstraints(constraints);
        this.restrictToNonNegative  = restrictToNonNegative;
        this.epsilon                = epsilon;
        this.maxUlps                = maxUlps;
        this.numDecisionVariables   = f.getCoefficients().getDimension() + (restrictToNonNegative ? 0 : 1);
        this.numSlackVariables      = getConstraintTypeCounts(Relationship.LEQ) +
                                      getConstraintTypeCounts(Relationship.GEQ);
        this.numArtificialVariables = getConstraintTypeCounts(Relationship.EQ) +
                                      getConstraintTypeCounts(Relationship.GEQ);
        this.tableau = createTableau(goalType == GoalType.MAXIMIZE);
        // initialize the basic variables for phase 1:
        //   we know that only slack or artificial variables can be basic
        initializeBasicVariables(getSlackVariableOffset());
        initializeColumnLabels();
    }

    /**
     * Checks that the dimensions of the objective function and the constraints match.
     * @param objectiveFunction the objective function
     * @param c the set of constraints
     * @throws DimensionMismatchException if the constraint dimensions do not match with the
     *   dimension of the objective function
     */
    private void checkDimensions(final LinearObjectiveFunction objectiveFunction,
                                 final Collection<LinearConstraint> c) {
        final int dimension = objectiveFunction.getCoefficients().getDimension();
        for (final LinearConstraint constraint : c) {
            final int constraintDimension = constraint.getCoefficients().getDimension();
            if (constraintDimension != dimension) {
                throw new DimensionMismatchException(constraintDimension, dimension);
            }
        }
    }
    /**
     * Initialize the labels for the columns.
     */
    protected void initializeColumnLabels() {
      if (getNumObjectiveFunctions() == 2) {
        columnLabels.add("W");
      }
      columnLabels.add("Z");
      for (int i = 0; i < getOriginalNumDecisionVariables(); i++) {
        columnLabels.add("x" + i);
      }
      if (!restrictToNonNegative) {
        columnLabels.add(NEGATIVE_VAR_COLUMN_LABEL);
      }
      for (int i = 0; i < getNumSlackVariables(); i++) {
        columnLabels.add("s" + i);
      }
      for (int i = 0; i < getNumArtificialVariables(); i++) {
        columnLabels.add("a" + i);
      }
      columnLabels.add("RHS");
    }

    /**
     * Create the tableau by itself.
     * @param maximize if true, goal is to maximize the objective function
     * @return created tableau
     */
    protected Array2DRowRealMatrix createTableau(final boolean maximize) {

        // create a matrix of the correct size
        int width = numDecisionVariables + numSlackVariables +
        numArtificialVariables + getNumObjectiveFunctions() + 1; // + 1 is for RHS
        int height = constraints.size() + getNumObjectiveFunctions();
        Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(height, width);

        // initialize the objective function rows
        if (getNumObjectiveFunctions() == 2) {
            matrix.setEntry(0, 0, -1);
        }

        int zIndex = (getNumObjectiveFunctions() == 1) ? 0 : 1;
        matrix.setEntry(zIndex, zIndex, maximize ? 1 : -1);

        double[][] scaled = new double[constraints.size() + 1][];

        RealVector objectiveCoefficients = maximize ? f.getCoefficients().mapMultiply(-1) : f.getCoefficients();
        scaled[0] = objectiveCoefficients.toArray();
        double[] scaledRhs = new double[constraints.size() + 1];
        double value = maximize ? f.getConstantTerm() : -1 * f.getConstantTerm();
        scaledRhs[0] = value;

        for (int i = 0; i < constraints.size(); i++) {
            LinearConstraint constraint = constraints.get(i);
            scaled[i + 1] = constraint.getCoefficients().toArray();
            scaledRhs[i + 1] = constraint.getValue();
        }
        variableExpChange = new int[scaled[0].length];

        scale(scaled, scaledRhs);

        copyArray(scaled[0], matrix.getDataRef()[zIndex]);
        matrix.setEntry(zIndex, width - 1, scaledRhs[0]);

        if (!restrictToNonNegative) {
            matrix.setEntry(zIndex, getSlackVariableOffset() - 1,
                            getInvertedCoefficientSum(scaled[0]));
        }

        // initialize the constraint rows
        int slackVar = 0;
        int artificialVar = 0;
        for (int i = 0; i < constraints.size(); i++) {
            final LinearConstraint constraint = constraints.get(i);
            final int row = getNumObjectiveFunctions() + i;

            // decision variable coefficients
            copyArray(scaled[i + 1], matrix.getDataRef()[row]);

            // x-
            if (!restrictToNonNegative) {
                matrix.setEntry(row, getSlackVariableOffset() - 1,
                                getInvertedCoefficientSum(scaled[i + 1]));
            }

            // RHS
            matrix.setEntry(row, width - 1, scaledRhs[i + 1]);

            // slack variables
            if (constraint.getRelationship() == Relationship.LEQ) {
                matrix.setEntry(row, getSlackVariableOffset() + slackVar++, 1);  // slack
            } else if (constraint.getRelationship() == Relationship.GEQ) {
                matrix.setEntry(row, getSlackVariableOffset() + slackVar++, -1); // excess
            }

            // artificial variables
            if (constraint.getRelationship() == Relationship.EQ ||
                constraint.getRelationship() == Relationship.GEQ) {
                matrix.setEntry(0, getArtificialVariableOffset() + artificialVar, 1);
                matrix.setEntry(row, getArtificialVariableOffset() + artificialVar++, 1);
                matrix.setRowVector(0, matrix.getRowVector(0).subtract(matrix.getRowVector(row)));
            }
        }

        return matrix;
    }

    /** We scale the constants in the equations and objective, which means we try
     * to get the IEEE double exponent as close to zero (1023) as possible, which makes the
     * constants closer to 1.
     * We use exponent shifts instead of division because that introduces no bit errors.
     *
     * @param scaled coefficients before scaling
     * @param scaledRhs right hand side before scaling
     */
    private void scale(double[][] scaled, double[] scaledRhs) {
        /*
            first transform across:
            c0 x0 + c1 x1 + ... + cn xn = vn ==> (2^expChange) * (c0 x0 + c1 x1 + ... + cn xn = vn)

            expChange will be negative if the constants are larger than 1,
            it'll be positive if the constants are less than 1.
        */
        for (int i = 0; i < scaled.length; i++) {
            int minExp = MAX_IEEE_EXP + 1;
            int maxExp = MIN_IEEE_EXP - 1;
            for (double d: scaled[i]) {
                if (d != 0) {
                    int e = exponent(d);
                    if (e < minExp) {
                        minExp = e;
                    }
                    if (e > maxExp) {
                        maxExp = e;
                    }
                }
            }
            if (scaledRhs[i] != 0) {
                final int e = exponent(scaledRhs[i]);
                if (e < minExp) {
                    minExp = e;
                }
                if (e > maxExp) {
                    maxExp = e;
                }
            }
            final int expChange = computeExpChange(minExp, maxExp);
            if (expChange != 0) {
                scaledRhs[i] = updateExponent(scaledRhs[i], expChange);
                updateExponent(scaled[i], expChange);
            }
        }

        /*
            second, transform down the columns. this is like defining a new variable for that column
            that is yi = xi * (2^expChange)
            After solving for yi, we compute xi by shifting again. See getSolution()
         */
        for (int i = 0; i < variableExpChange.length; i++) {
            int minExp = MAX_IEEE_EXP + 1;
            int maxExp = MIN_IEEE_EXP - 1;

            for (double[] coefficients : scaled) {
                final double d = coefficients[i];
                if (d != 0) {
                    int e = exponent(d);
                    if (e < minExp) {
                        minExp = e;
                    }
                    if (e > maxExp) {
                        maxExp = e;
                    }
                }
            }
            final int expChange = computeExpChange(minExp, maxExp);
            variableExpChange[i] = expChange;
            if (expChange != 0) {
                for (double[] coefficients : scaled) {
                     coefficients[i] = updateExponent(coefficients[i], expChange);
                }
            }
        }
    }

    /**
     * Given the minimum and maximum value of the exponent of two {@code double}
     * values, pick a change in exponent to bring those values closer to 1.
     *
     * @param minExp Smallest exponent.
     * @param maxExp Largest exponent.
     * @return the new exponent.
     */
    private int computeExpChange(int minExp, int maxExp) {
        int expChange = 0;
        if (minExp <= MAX_IEEE_EXP &&
            minExp > OFFSET_IEEE_EXP) {
            expChange = OFFSET_IEEE_EXP - minExp;
        } else if (maxExp >= MIN_IEEE_EXP &&
                   maxExp < OFFSET_IEEE_EXP) {
            expChange = OFFSET_IEEE_EXP - maxExp;
        }
        return expChange;
    }

    /**
     * Changes the exponent of every member of the array by the given amount.
     *
     * @param dar array of doubles to change
     * @param exp exponent value to change
     */
    private static void updateExponent(double[] dar, int exp) {
        for (int i = 0; i < dar.length; i++) {
            dar[i] = updateExponent(dar[i], exp);
        }
    }

    /**
     * Extract the exponent of a {@code double}.
     *
     * @param d value to extract the exponent from
     * @return the IEEE exponent in the EXPN bits, as an integer
     */
    private static int exponent(double d) {
        final long bits = Double.doubleToLongBits(d);
        return (int) ((bits & EXPN) >>> IEEE_EXPONENT_SHIFT);
    }

    /**
     * Changes the exponent of a number by the given amount.
     *
     * @param d value to change
     * @param exp exponent to add to the existing exponent (may be negative)
     * @return a double with the same sign/mantissa bits as d, but exponent changed by exp
     */
    private static double updateExponent(double d, int exp) {
        if (d == 0 ||
            exp == 0) {
            return d;
        }
        final long bits = Double.doubleToLongBits(d);
        return Double.longBitsToDouble((bits & FRAC) | ((((bits & EXPN) >>> IEEE_EXPONENT_SHIFT) + exp) << IEEE_EXPONENT_SHIFT));
    }

    /**
     * Get new versions of the constraints which have positive right hand sides.
     * @param originalConstraints original (not normalized) constraints
     * @return new versions of the constraints
     */
    public List<LinearConstraint> normalizeConstraints(Collection<LinearConstraint> originalConstraints) {
        final List<LinearConstraint> normalized = new ArrayList<>(originalConstraints.size());
        for (LinearConstraint constraint : originalConstraints) {
            normalized.add(normalize(constraint));
        }
        return normalized;
    }

    /**
     * Get a new equation equivalent to this one with a positive right hand side.
     * @param constraint reference constraint
     * @return new equation
     */
    private LinearConstraint normalize(final LinearConstraint constraint) {
        if (constraint.getValue() < 0) {
            return new LinearConstraint(constraint.getCoefficients().mapMultiply(-1),
                                        constraint.getRelationship().oppositeRelationship(),
                                        -1 * constraint.getValue());
        }
        return new LinearConstraint(constraint.getCoefficients(),
                                    constraint.getRelationship(), constraint.getValue());
    }

    /**
     * Get the number of objective functions in this tableau.
     * @return 2 for Phase 1.  1 for Phase 2.
     */
    protected final int getNumObjectiveFunctions() {
        return this.numArtificialVariables > 0 ? 2 : 1;
    }

    /**
     * Get a count of constraints corresponding to a specified relationship.
     * @param relationship relationship to count
     * @return number of constraint with the specified relationship
     */
    private int getConstraintTypeCounts(final Relationship relationship) {
        int count = 0;
        for (final LinearConstraint constraint : constraints) {
            if (constraint.getRelationship() == relationship) {
                ++count;
            }
        }
        return count;
    }

    /**
     * Get the -1 times the sum of all coefficients in the given array.
     * @param coefficients coefficients to sum
     * @return the -1 times the sum of all coefficients in the given array.
     */
    private static double getInvertedCoefficientSum(final double[] coefficients) {
        double sum = 0;
        for (double coefficient : coefficients) {
            sum -= coefficient;
        }
        return sum;
    }

    /**
     * Checks whether the given column is basic.
     * @param col index of the column to check
     * @return the row that the variable is basic in.  null if the column is not basic
     */
    protected Integer getBasicRow(final int col) {
        final int row = basicVariables[col];
        return row == -1 ? null : row;
    }

    /**
     * Returns the variable that is basic in this row.
     * @param row the index of the row to check
     * @return the variable that is basic for this row.
     */
    protected int getBasicVariable(final int row) {
        return basicRows[row];
    }

    /**
     * Initializes the basic variable / row mapping.
     * @param startColumn the column to start
     */
    private void initializeBasicVariables(final int startColumn) {
        basicVariables = new int[getWidth() - 1];
        basicRows = new int[getHeight()];

        Arrays.fill(basicVariables, -1);

        for (int i = startColumn; i < getWidth() - 1; i++) {
            Integer row = findBasicRow(i);
            if (row != null) {
                basicVariables[i] = row;
                basicRows[row] = i;
            }
        }
    }

    /**
     * Returns the row in which the given column is basic.
     * @param col index of the column
     * @return the row that the variable is basic in, or {@code null} if the variable is not basic.
     */
    private Integer findBasicRow(final int col) {
        Integer row = null;
        for (int i = 0; i < getHeight(); i++) {
            final double entry = getEntry(i, col);
            if (Precision.equals(entry, 1d, maxUlps) && row == null) {
                row = i;
            } else if (!Precision.equals(entry, 0d, maxUlps)) {
                return null;
            }
        }
        return row;
    }

    /**
     * Removes the phase 1 objective function, positive cost non-artificial variables,
     * and the non-basic artificial variables from this tableau.
     */
    protected void dropPhase1Objective() {
        if (getNumObjectiveFunctions() == 1) {
            return;
        }

        final Set<Integer> columnsToDrop = new TreeSet<>();
        columnsToDrop.add(0);

        // positive cost non-artificial variables
        for (int i = getNumObjectiveFunctions(); i < getArtificialVariableOffset(); i++) {
            final double entry = getEntry(0, i);
            if (Precision.compareTo(entry, 0d, epsilon) > 0) {
                columnsToDrop.add(i);
            }
        }

        // non-basic artificial variables
        for (int i = 0; i < getNumArtificialVariables(); i++) {
            int col = i + getArtificialVariableOffset();
            if (getBasicRow(col) == null) {
                columnsToDrop.add(col);
            }
        }

        final double[][] matrix = new double[getHeight() - 1][getWidth() - columnsToDrop.size()];
        for (int i = 1; i < getHeight(); i++) {
            int col = 0;
            for (int j = 0; j < getWidth(); j++) {
                if (!columnsToDrop.contains(j)) {
                    matrix[i - 1][col++] = getEntry(i, j);
                }
            }
        }

        // remove the columns in reverse order so the indices are correct
        Integer[] drop = columnsToDrop.toArray(new Integer[0]);
        for (int i = drop.length - 1; i >= 0; i--) {
            columnLabels.remove((int) drop[i]);
        }

        this.tableau = new Array2DRowRealMatrix(matrix);
        this.numArtificialVariables = 0;
        // need to update the basic variable mappings as row/columns have been dropped
        initializeBasicVariables(getNumObjectiveFunctions());
    }

    /**
     * @param src the source array
     * @param dest the destination array
     */
    private void copyArray(final double[] src, final double[] dest) {
        System.arraycopy(src, 0, dest, getNumObjectiveFunctions(), src.length);
    }

    /**
     * Returns whether the problem is at an optimal state.
     * @return whether the model has been solved
     */
    boolean isOptimal() {
        final double[] objectiveFunctionRow = getRow(0);
        final int end = getRhsOffset();
        for (int i = getNumObjectiveFunctions(); i < end; i++) {
            final double entry = objectiveFunctionRow[i];
            if (Precision.compareTo(entry, 0d, epsilon) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the current solution.
     * @return current solution
     */
    protected PointValuePair getSolution() {
        int negativeVarColumn = columnLabels.indexOf(NEGATIVE_VAR_COLUMN_LABEL);
        Integer negativeVarBasicRow = negativeVarColumn > 0 ? getBasicRow(negativeVarColumn) : null;
        double mostNegative = negativeVarBasicRow == null ? 0 : getEntry(negativeVarBasicRow, getRhsOffset());

        final Set<Integer> usedBasicRows = new HashSet<>();
        final double[] coefficients = new double[getOriginalNumDecisionVariables()];
        for (int i = 0; i < coefficients.length; i++) {
            int colIndex = columnLabels.indexOf("x" + i);
            if (colIndex < 0) {
                coefficients[i] = 0;
                continue;
            }
            Integer basicRow = getBasicRow(colIndex);
            if (basicRow != null && basicRow == 0) {
                // if the basic row is found to be the objective function row
                // set the coefficient to 0 -> this case handles unconstrained
                // variables that are still part of the objective function
                coefficients[i] = 0;
            } else if (usedBasicRows.contains(basicRow)) {
                // if multiple variables can take a given value
                // then we choose the first and set the rest equal to 0
                coefficients[i] = 0 - (restrictToNonNegative ? 0 : mostNegative);
            } else {
                usedBasicRows.add(basicRow);
                coefficients[i] =
                    (basicRow == null ? 0 : getEntry(basicRow, getRhsOffset())) -
                    (restrictToNonNegative ? 0 : mostNegative);
            }
            coefficients[i] = updateExponent(coefficients[i], variableExpChange[i]);
        }
        return new PointValuePair(coefficients, f.value(coefficients));
    }

    /**
     * Perform the row operations of the simplex algorithm with the selected
     * pivot column and row.
     * @param pivotCol the pivot column
     * @param pivotRow the pivot row
     */
    protected void performRowOperations(int pivotCol, int pivotRow) {
        // set the pivot element to 1
        final double pivotVal = getEntry(pivotRow, pivotCol);
        divideRow(pivotRow, pivotVal);

        // set the rest of the pivot column to 0
        for (int i = 0; i < getHeight(); i++) {
            if (i != pivotRow) {
                final double multiplier = getEntry(i, pivotCol);
                if (multiplier != 0.0) {
                    subtractRow(i, pivotRow, multiplier);
                }
            }
        }

        // update the basic variable mappings
        final int previousBasicVariable = getBasicVariable(pivotRow);
        basicVariables[previousBasicVariable] = -1;
        basicVariables[pivotCol] = pivotRow;
        basicRows[pivotRow] = pivotCol;
    }

    /**
     * Divides one row by a given divisor.
     * <p>
     * After application of this operation, the following will hold:
     * <pre>dividendRow = dividendRow / divisor</pre>
     *
     * @param dividendRowIndex index of the row
     * @param divisor value of the divisor
     */
    protected void divideRow(final int dividendRowIndex, final double divisor) {
        final double[] dividendRow = getRow(dividendRowIndex);
        for (int j = 0; j < getWidth(); j++) {
            dividendRow[j] /= divisor;
        }
    }

    /**
     * Subtracts a multiple of one row from another.
     * <p>
     * After application of this operation, the following will hold:
     * <pre>minuendRow = minuendRow - multiple * subtrahendRow</pre>
     *
     * @param minuendRowIndex row index
     * @param subtrahendRowIndex row index
     * @param multiplier multiplication factor
     */
    protected void subtractRow(final int minuendRowIndex, final int subtrahendRowIndex, final double multiplier) {
        final double[] minuendRow = getRow(minuendRowIndex);
        final double[] subtrahendRow = getRow(subtrahendRowIndex);
        for (int i = 0; i < getWidth(); i++) {
            minuendRow[i] -= subtrahendRow[i] * multiplier;
        }
    }

    /**
     * Get the width of the tableau.
     * @return width of the tableau
     */
    protected final int getWidth() {
        return tableau.getColumnDimension();
    }

    /**
     * Get the height of the tableau.
     * @return height of the tableau
     */
    protected final int getHeight() {
        return tableau.getRowDimension();
    }

    /**
     * Get an entry of the tableau.
     * @param row row index
     * @param column column index
     * @return entry at (row, column)
     */
    protected final double getEntry(final int row, final int column) {
        return tableau.getEntry(row, column);
    }

    /**
     * Set an entry of the tableau.
     * @param row row index
     * @param column column index
     * @param value for the entry
     */
    protected final void setEntry(final int row, final int column, final double value) {
        tableau.setEntry(row, column, value);
    }

    /**
     * Get the offset of the first slack variable.
     * @return offset of the first slack variable
     */
    protected final int getSlackVariableOffset() {
        return getNumObjectiveFunctions() + numDecisionVariables;
    }

    /**
     * Get the offset of the first artificial variable.
     * @return offset of the first artificial variable
     */
    protected final int getArtificialVariableOffset() {
        return getNumObjectiveFunctions() + numDecisionVariables + numSlackVariables;
    }

    /**
     * Get the offset of the right hand side.
     * @return offset of the right hand side
     */
    protected final int getRhsOffset() {
        return getWidth() - 1;
    }

    /**
     * Get the number of decision variables.
     * <p>
     * If variables are not restricted to positive values, this will include 1 extra decision variable to represent
     * the absolute value of the most negative variable.
     *
     * @return number of decision variables
     * @see #getOriginalNumDecisionVariables()
     */
    protected final int getNumDecisionVariables() {
        return numDecisionVariables;
    }

    /**
     * Get the original number of decision variables.
     * @return original number of decision variables
     * @see #getNumDecisionVariables()
     */
    protected final int getOriginalNumDecisionVariables() {
        return f.getCoefficients().getDimension();
    }

    /**
     * Get the number of slack variables.
     * @return number of slack variables
     */
    protected final int getNumSlackVariables() {
        return numSlackVariables;
    }

    /**
     * Get the number of artificial variables.
     * @return number of artificial variables
     */
    protected final int getNumArtificialVariables() {
        return numArtificialVariables;
    }

    /**
     * Get the row from the tableau.
     * @param row the row index
     * @return the reference to the underlying row data
     */
    protected final double[] getRow(int row) {
        return tableau.getDataRef()[row];
    }

    /**
     * Get the tableau data.
     * @return tableau data
     */
    protected final double[][] getData() {
        return tableau.getData();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {

      if (this == other) {
        return true;
      }

      if (other instanceof SimplexTableau) {
          SimplexTableau rhs = (SimplexTableau) other;
          return restrictToNonNegative  == rhs.restrictToNonNegative &&
                 numDecisionVariables   == rhs.numDecisionVariables &&
                 numSlackVariables      == rhs.numSlackVariables &&
                 numArtificialVariables == rhs.numArtificialVariables &&
                 epsilon                == rhs.epsilon &&
                 maxUlps                == rhs.maxUlps &&
                 f.equals(rhs.f) &&
                 constraints.equals(rhs.constraints) &&
                 tableau.equals(rhs.tableau);
      }
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Boolean.valueOf(restrictToNonNegative).hashCode() ^
               numDecisionVariables ^
               numSlackVariables ^
               numArtificialVariables ^
               Double.valueOf(epsilon).hashCode() ^
               maxUlps ^
               f.hashCode() ^
               constraints.hashCode() ^
               tableau.hashCode();
    }
}
