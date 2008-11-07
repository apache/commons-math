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

package org.apache.commons.math.fraction;

import java.io.Serializable;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.MathRuntimeException;

/**
 * Formats a Fraction number in proper format or improper format.  The number
 * format for each of the whole number, numerator and, denominator can be
 * configured.
 *
 * @since 1.1
 * @version $Revision$ $Date$
 */
public class FractionFormat extends Format implements Serializable {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = -6337346779577272306L;

    /** The format used for the denominator. */
    private NumberFormat denominatorFormat;

    /** The format used for the numerator. */
    private NumberFormat numeratorFormat;
    
    /**
     * Create an improper formatting instance with the default number format
     * for the numerator and denominator.  
     */
    public FractionFormat() {
        this(getDefaultNumberFormat());
    }

    /**
     * Create an improper formatting instance with a custom number format for
     * both the numerator and denominator.
     * @param format the custom format for both the numerator and denominator.
     */
    public FractionFormat(NumberFormat format) {
        this(format, (NumberFormat)format.clone());
    }

    /**
     * Create an improper formatting instance with a custom number format for
     * the numerator and a custom number format for the denominator.
     * @param numeratorFormat the custom format for the numerator.
     * @param denominatorFormat the custom format for the denominator.
     */
    public FractionFormat(NumberFormat numeratorFormat,
            NumberFormat denominatorFormat)
    {
        super();
        this.numeratorFormat = numeratorFormat;
        this.denominatorFormat = denominatorFormat;
    }

    /**
     * This static method calls formatFraction() on a default instance of
     * FractionFormat.
     *
     * @param f Fraction object to format
     * @return A formatted fraction in proper form.
     */
    public static String formatFraction(Fraction f) {
        return getImproperInstance().format(f);
    }
    
    /**
     * Get the set of locales for which complex formats are available.  This
     * is the same set as the {@link NumberFormat} set. 
     * @return available complex format locales.
     */
    public static Locale[] getAvailableLocales() {
        return NumberFormat.getAvailableLocales();
    }
    
    /**
     * Returns the default complex format for the current locale.
     * @return the default complex format.
     */
    public static FractionFormat getImproperInstance() {
        return getImproperInstance(Locale.getDefault());
    }
    
    /**
     * Returns the default complex format for the given locale.
     * @param locale the specific locale used by the format.
     * @return the complex format specific to the given locale.
     */
    public static FractionFormat getImproperInstance(Locale locale) {
        NumberFormat f = getDefaultNumberFormat(locale);
        return new FractionFormat(f);
    }
    
    /**
     * Returns the default complex format for the current locale.
     * @return the default complex format.
     */
    public static FractionFormat getProperInstance() {
        return getProperInstance(Locale.getDefault());
    }
    
    /**
     * Returns the default complex format for the given locale.
     * @param locale the specific locale used by the format.
     * @return the complex format specific to the given locale.
     */
    public static FractionFormat getProperInstance(Locale locale) {
        NumberFormat f = getDefaultNumberFormat(locale);
        return new ProperFractionFormat(f);
    }
    
    /**
     * Create a default number format.  The default number format is based on
     * {@link NumberFormat#getNumberInstance(java.util.Locale)} with the only
     * customizing is the maximum number of fraction digits, which is set to 0.  
     * @return the default number format.
     */
    protected static NumberFormat getDefaultNumberFormat() {
        return getDefaultNumberFormat(Locale.getDefault());
    }
    
    /**
     * Create a default number format.  The default number format is based on
     * {@link NumberFormat#getNumberInstance(java.util.Locale)} with the only
     * customizing is the maximum number of fraction digits, which is set to 0.  
     * @param locale the specific locale used by the format.
     * @return the default number format specific to the given locale.
     */
    private static NumberFormat getDefaultNumberFormat(Locale locale) {
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        nf.setMaximumFractionDigits(0);
        nf.setParseIntegerOnly(true);
        return nf;
    }
    
    /**
     * Formats a {@link Fraction} object to produce a string.  The fraction is
     * output in improper format.
     *
     * @param fraction the object to format.
     * @param toAppendTo where the text is to be appended
     * @param pos On input: an alignment field, if desired. On output: the
     *            offsets of the alignment field
     * @return the value passed in as toAppendTo.
     */
    public StringBuffer format(Fraction fraction, StringBuffer toAppendTo,
            FieldPosition pos) {
        
        pos.setBeginIndex(0);
        pos.setEndIndex(0);

        getNumeratorFormat().format(fraction.getNumerator(), toAppendTo, pos);
        toAppendTo.append(" / ");
        getDenominatorFormat().format(fraction.getDenominator(), toAppendTo,
            pos);
        
        return toAppendTo;
    }
    
    /**
     * Formats a object to produce a string.  <code>obj</code> must be either a 
     * {@link Fraction} object or a {@link Number} object.  Any other type of
     * object will result in an {@link IllegalArgumentException} being thrown.
     *
     * @param obj the object to format.
     * @param toAppendTo where the text is to be appended
     * @param pos On input: an alignment field, if desired. On output: the
     *            offsets of the alignment field
     * @return the value passed in as toAppendTo.
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     * @throws IllegalArgumentException is <code>obj</code> is not a valid type.
     */
    public StringBuffer format(Object obj, StringBuffer toAppendTo,
            FieldPosition pos)
    {
        StringBuffer ret = null;
        
        if (obj instanceof Fraction) {
            ret = format( (Fraction)obj, toAppendTo, pos);
        } else if (obj instanceof Number) {
            try {
                ret = format( new Fraction(((Number)obj).doubleValue()),
                    toAppendTo, pos);
            } catch (ConvergenceException ex) {
                throw new IllegalArgumentException(
                    "Cannot convert given object to a fraction.");
            }
        } else { 
            throw new IllegalArgumentException(
                "Cannot format given object as a fraction");
        }
        
        return ret;
    }

    /**
     * Access the denominator format.
     * @return the denominator format.
     */
    public NumberFormat getDenominatorFormat() {
        return denominatorFormat;
    }
    
    /**
     * Access the numerator format.
     * @return the numerator format.
     */
    public NumberFormat getNumeratorFormat() {
        return numeratorFormat;
    }

    /**
     * Parses a string to produce a {@link Fraction} object.
     * @param source the string to parse
     * @return the parsed {@link Fraction} object.
     * @exception ParseException if the beginning of the specified string
     *            cannot be parsed.
     */
    public Fraction parse(String source) throws ParseException {
        ParsePosition parsePosition = new ParsePosition(0);
        Fraction result = parse(source, parsePosition);
        if (parsePosition.getIndex() == 0) {
            throw MathRuntimeException.createParseException("unparseable fraction number: \"{0}\"",
                                                            new Object[] { source },
                                                            parsePosition.getErrorIndex());
        }
        return result;
    }
    
    /**
     * Parses a string to produce a {@link Fraction} object.  This method
     * expects the string to be formatted as an improper fraction.  
     * @param source the string to parse
     * @param pos input/ouput parsing parameter.
     * @return the parsed {@link Fraction} object.
     */
    public Fraction parse(String source, ParsePosition pos) {
        int initialIndex = pos.getIndex();

        // parse whitespace
        parseAndIgnoreWhitespace(source, pos);

        // parse numerator
        Number num = getNumeratorFormat().parse(source, pos);
        if (num == null) {
            // invalid integer number
            // set index back to initial, error index should already be set
            // character examined.
            pos.setIndex(initialIndex);
            return null;
        }

        // parse '/'
        int startIndex = pos.getIndex();
        char c = parseNextCharacter(source, pos);
        switch (c) {
        case 0 :
            // no '/'
            // return num as a fraction
            return new Fraction(num.intValue(), 1);
        case '/' :
            // found '/', continue parsing denominator
            break;
        default :
            // invalid '/'
            // set index back to initial, error index should be the last
            // character examined.
            pos.setIndex(initialIndex);
            pos.setErrorIndex(startIndex);
            return null;
        }

        // parse whitespace
        parseAndIgnoreWhitespace(source, pos);

        // parse denominator
        Number den = getDenominatorFormat().parse(source, pos);
        if (den == null) {
            // invalid integer number
            // set index back to initial, error index should already be set
            // character examined.
            pos.setIndex(initialIndex);
            return null;
        }

        return new Fraction(num.intValue(), den.intValue());
    }

    /**
     * Parses a string to produce a object.
     * @param source the string to parse
     * @param pos input/ouput parsing parameter.
     * @return the parsed object.
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     */
    public Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }
    
    /**
     * Modify the denominator format.
     * @param format the new denominator format value.
     * @throws IllegalArgumentException if <code>format</code> is
     *         <code>null</code>.
     */
    public void setDenominatorFormat(NumberFormat format) {
        if (format == null) {
            throw new IllegalArgumentException(
                "denominator format can not be null.");
        }
        this.denominatorFormat = format;
    }
    
    /**
     * Modify the numerator format.
     * @param format the new numerator format value.
     * @throws IllegalArgumentException if <code>format</code> is
     *         <code>null</code>.
     */
    public void setNumeratorFormat(NumberFormat format) {
        if (format == null) {
            throw new IllegalArgumentException(
                "numerator format can not be null.");
        }
        this.numeratorFormat = format;
    }
     
    /**
     * Parses <code>source</code> until a non-whitespace character is found.
     * @param source the string to parse
     * @param pos input/ouput parsing parameter.  On output, <code>pos</code>
     *        holds the index of the next non-whitespace character.
     */
    protected static void parseAndIgnoreWhitespace(
        String source, ParsePosition pos)
    {
        parseNextCharacter(source, pos);
        pos.setIndex(pos.getIndex() - 1);
    }

    /**
     * Parses <code>source</code> until a non-whitespace character is found.
     * @param source the string to parse
     * @param pos input/ouput parsing parameter.
     * @return the first non-whitespace character.
     */
    protected static char parseNextCharacter(String source, ParsePosition pos) {
         int index = pos.getIndex();
         int n = source.length();
         char ret = 0;

         if (index < n) {
             char c;
             do {
                 c = source.charAt(index++);
             } while (Character.isWhitespace(c) && index < n);
             pos.setIndex(index);
         
             if (index < n) {
                 ret = c;
             }
         }
         
         return ret;
    }
}
