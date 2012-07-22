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

package org.apache.commons.math3.linear;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math3.exception.MathParseException;
import org.apache.commons.math3.util.CompositeFormat;

/**
 * Formats a {@code nxm} matrix in components list format
 * "[a<sub>0</sub><sub>0</sub>, a<sub>0</sub><sub>1</sub>, ...,
 * a<sub>0</sub><sub>m-1</sub>; a<sub>1</sub><sub>0</sub>,
 * a<sub>1</sub><sub>1</sub>, ..., a<sub>1</sub><sub>m-1</sub>; ...;
 * a<sub>n-1</sub><sub>0</sub>, a<sub>n-1</sub><sub>1</sub>, ...,
 * a<sub>n-1</sub><sub>m-1</sub>}".
 * <p>The prefix and suffix "[" and "]", the row separator "; " and the column
 * separator ", " can be replaced by any user-defined strings. The number format
 * for components can be configured.</p>
 *
 * <p>White space is ignored at parse time, even if it is in the prefix, suffix
 * or separator specifications. So even if the default separator does include a space
 * character that is used at format time, both input string "[1,1,1]" and
 * " [ 1 , 1 , 1 ] " will be parsed without error and the same vector will be
 * returned. In the second case, however, the parse position after parsing will be
 * just after the closing curly brace, i.e. just before the trailing space.</p>
 *
 * <p><b>Note:</b> the grouping functionality of the used {@link NumberFormat} is
 * disabled to prevent problems when parsing (e.g. 1,345.34 would be a valid number
 * but conflicts with the default column separator).</p>
 *
 * @since 3.1
 * @version $Id$
 */
public class RealMatrixFormat {

    /** The default prefix: "{". */
    private static final String DEFAULT_PREFIX = "[";
    /** The default suffix: "}". */
    private static final String DEFAULT_SUFFIX = "]";
    /** The default row separator: ";". */
    private static final String DEFAULT_ROW_SEPARATOR = "; ";
    /** The default column separator: ", ". */
    private static final String DEFAULT_COLUMN_SEPARATOR = ", ";
    /** Prefix. */
    private final String prefix;
    /** Suffix. */
    private final String suffix;
    /** Row separator. */
    private final String rowSeparator;
    /** Column separator. */
    private final String columnSeparator;
    /** Trimmed prefix. */
    private final String trimmedPrefix;
    /** Trimmed suffix. */
    private final String trimmedSuffix;
    /** Trimmed row separator. */
    private final String trimmedRowSeparator;
    /** Trimmed column separator. */
    private final String trimmedColumnSeparator;
    /** The format used for components. */
    private final NumberFormat format;

    /**
     * Create an instance with default settings.
     * <p>The instance uses the default prefix, suffix and row/column separator:
     * "[", "]", ";" and ", " and the default number format for components.</p>
     */
    public RealMatrixFormat() {
        this(DEFAULT_PREFIX, DEFAULT_SUFFIX, DEFAULT_ROW_SEPARATOR, DEFAULT_COLUMN_SEPARATOR,
             CompositeFormat.getDefaultNumberFormat());
    }

    /**
     * Create an instance with a custom number format for components.
     * @param format the custom format for components.
     */
    public RealMatrixFormat(final NumberFormat format) {
        this(DEFAULT_PREFIX, DEFAULT_SUFFIX, DEFAULT_ROW_SEPARATOR, DEFAULT_COLUMN_SEPARATOR, format);
    }

    /**
     * Create an instance with custom prefix, suffix and separator.
     * @param prefix prefix to use instead of the default "{"
     * @param suffix suffix to use instead of the default "}"
     * @param rowSeparator tow separator to use instead of the default ";"
     * @param columnSeparator column separator to use instead of the default ", "
     */
    public RealMatrixFormat(final String prefix, final String suffix,
                            final String rowSeparator, final String columnSeparator) {
        this(prefix, suffix, rowSeparator, columnSeparator, CompositeFormat.getDefaultNumberFormat());
    }

    /**
     * Create an instance with custom prefix, suffix, separator and format
     * for components.
     * @param prefix prefix to use instead of the default "{"
     * @param suffix suffix to use instead of the default "}"
     * @param rowSeparator tow separator to use instead of the default ";"
     * @param columnSeparator column separator to use instead of the default ", "
     * @param format the custom format for components.
     */
    public RealMatrixFormat(final String prefix, final String suffix,
                            final String rowSeparator, final String columnSeparator,
                            final NumberFormat format) {
        this.prefix            = prefix;
        this.suffix            = suffix;
        this.rowSeparator      = rowSeparator;
        this.columnSeparator   = columnSeparator;
        trimmedPrefix          = prefix.trim();
        trimmedSuffix          = suffix.trim();
        trimmedRowSeparator    = rowSeparator.trim();
        trimmedColumnSeparator = columnSeparator.trim();
        this.format            = format;
        // disable grouping to prevent parsing problems
        this.format.setGroupingUsed(false);
    }

    /**
     * Get the set of locales for which real vectors formats are available.
     * <p>This is the same set as the {@link NumberFormat} set.</p>
     * @return available real vector format locales.
     */
    public static Locale[] getAvailableLocales() {
        return NumberFormat.getAvailableLocales();
    }

    /**
     * Get the format prefix.
     * @return format prefix.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get the format suffix.
     * @return format suffix.
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Get the format separator between rows of the matrix.
     * @return format separator for rows.
     */
    public String getRowSeparator() {
        return rowSeparator;
    }

    /**
     * Get the format separator between components.
     * @return format separator between components.
     */
    public String getColumnSeparator() {
        return columnSeparator;
    }

    /**
     * Get the components format.
     * @return components format.
     */
    public NumberFormat getFormat() {
        return format;
    }

    /**
     * Returns the default real vector format for the current locale.
     * @return the default real vector format.
     */
    public static RealMatrixFormat getInstance() {
        return getInstance(Locale.getDefault());
    }

    /**
     * Returns the default real vector format for the given locale.
     * @param locale the specific locale used by the format.
     * @return the real vector format specific to the given locale.
     */
    public static RealMatrixFormat getInstance(final Locale locale) {
        return new RealMatrixFormat(CompositeFormat.getDefaultNumberFormat(locale));
    }

    /**
     * This method calls {@link #format(RealMatrix,StringBuffer,FieldPosition)}.
     *
     * @param m RealMatrix object to format.
     * @return a formatted matrix.
     */
    public String format(RealMatrix m) {
        return format(m, new StringBuffer(), new FieldPosition(0)).toString();
    }

    /**
     * Formats a {@link RealMatrix} object to produce a string.
     * @param matrix the object to format.
     * @param toAppendTo where the text is to be appended
     * @param pos On input: an alignment field, if desired. On output: the
     *            offsets of the alignment field
     * @return the value passed in as toAppendTo.
     */
    public StringBuffer format(RealMatrix matrix, StringBuffer toAppendTo,
                               FieldPosition pos) {

        pos.setBeginIndex(0);
        pos.setEndIndex(0);

        // format prefix
        toAppendTo.append(prefix);

        // format rows
        final int rows = matrix.getRowDimension();
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < matrix.getColumnDimension(); ++j) {
                if (j > 0) {
                    toAppendTo.append(columnSeparator);
                }
                CompositeFormat.formatDouble(matrix.getEntry(i, j), format, toAppendTo, pos);
            }
            if (i < rows - 1) {
                toAppendTo.append(rowSeparator);
            }
        }

        // format suffix
        toAppendTo.append(suffix);

        return toAppendTo;
    }

    /**
     * Parse a string to produce a {@link RealMatrix} object.
     *
     * @param source String to parse.
     * @return the parsed {@link RealMatrix} object.
     * @throws MathParseException if the beginning of the specified string
     * cannot be parsed.
     */
    public RealMatrix parse(String source) {
        final ParsePosition parsePosition = new ParsePosition(0);
        final RealMatrix result = parse(source, parsePosition);
        if (parsePosition.getIndex() == 0) {
            throw new MathParseException(source,
                                         parsePosition.getErrorIndex(),
                                         Array2DRowRealMatrix.class);
        }
        return result;
    }

    /**
     * Parse a string to produce a {@link RealMatrix} object.
     *
     * @param source String to parse.
     * @param pos input/ouput parsing parameter.
     * @return the parsed {@link RealMatrix} object.
     */
    public RealMatrix parse(String source, ParsePosition pos) {
        int initialIndex = pos.getIndex();

        // parse prefix
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        if (!CompositeFormat.parseFixedstring(source, trimmedPrefix, pos)) {
            return null;
        }

        // parse components
        List<List<Number>> matrix = new ArrayList<List<Number>>();
        List<Number> rowComponents = new ArrayList<Number>();
        for (boolean loop = true; loop;){

            if (!rowComponents.isEmpty()) {
                CompositeFormat.parseAndIgnoreWhitespace(source, pos);
                if (!CompositeFormat.parseFixedstring(source, trimmedColumnSeparator, pos)) {
                    if (CompositeFormat.parseFixedstring(source, trimmedRowSeparator, pos)) {
                        matrix.add(rowComponents);
                        rowComponents = new ArrayList<Number>();
                    } else {
                        loop = false;
                    }
                }
            }

            if (loop) {
                CompositeFormat.parseAndIgnoreWhitespace(source, pos);
                Number component = CompositeFormat.parseNumber(source, format, pos);
                if (component != null) {
                    rowComponents.add(component);
                } else {
                    if (rowComponents.isEmpty()) {
                        loop = false;
                    } else {
                        // invalid component
                        // set index back to initial, error index should already be set
                        pos.setIndex(initialIndex);
                        return null;
                    }
                }
            }

        }

        if (!rowComponents.isEmpty()) {
            matrix.add(rowComponents);
        }

        // parse suffix
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        if (!CompositeFormat.parseFixedstring(source, trimmedSuffix, pos)) {
            return null;
        }

        // do not allow an empty matrix
        if (matrix.isEmpty()) {
            pos.setIndex(initialIndex);
            return null;
        }

        // build vector
        double[][] data = new double[matrix.size()][];
        int row = 0;
        for (List<Number> rowList : matrix) {
            data[row] = new double[rowList.size()];
            for (int i = 0; i < rowList.size(); i++) {
                data[row][i] = rowList.get(i).doubleValue();
            }
            row++;
        }
        return MatrixUtils.createRealMatrix(data);
    }
}
