/*
 * Copyright 2004,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.complex;

import java.text.NumberFormat;

/**
 * Formats a Complex number in cartesian format "Re(c) + Im(c)i".  'i' can
 * be replaced with 'j', and the number of decimal places to display 
 * can be configured.
 *
 * @author Apache Software Foundation
 * @version $Revision: 1.3 $
 */
public class ComplexFormat {

	private static final ComplexFormat DEFAULT = new ComplexFormat();

	// @TODO This class only allows for max fraction digits, we might want to allow other parameters
    private String imaginaryCharacter = "i";

    private int fractionDigits = 2;

    /**
     * Create an instance with the default imaginary character 'i', and the default
     * number of decimal places - 2.
     */
    public ComplexFormat() {}

    /**
     * Create an instance with a custom imaginary character, and the default number
     * of decimal places - 2.
     */
    public ComplexFormat(String imaginaryCharacter) {
        this.imaginaryCharacter = imaginaryCharacter;
    }

    /**
     * Create an instance with a custom imaginary character, and a custom number of
     * decimal places.
     */
    public ComplexFormat(String imaginaryCharacter, int fractionDigits) {
        this.imaginaryCharacter = imaginaryCharacter;
        this.fractionDigits = fractionDigits;
    }

    /**
     * Formats a Complex object and returns a String representing the "cartesian
     * form" of a complex number.
     *
     * @param c Complex object to format
     * @return A formatted number in the form "Re(c) + Im(c)i"
     */
    public String format(Complex c) {

		// @TODO What happens when either a real or imaginary is NaN, INIFINITY, etc?

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits( fractionDigits );

        StringBuffer buffer = new StringBuffer();

        buffer.append( format.format( c.getReal() ) );

        if( c.getImaginary() < 0 ) {
            buffer.append( " - " );
            buffer.append( format.format( Math.abs(c.getImaginary()) ) );
            buffer.append( imaginaryCharacter );
        } else if( c.getImaginary() > 0 ) {
            buffer.append( " + " );
            buffer.append( format.format( c.getImaginary() ) );
            buffer.append( imaginaryCharacter );
        }            
        
        return( buffer.toString() );

    }
    
    /**
     * This static method calls formatComplex() on a default instance of
     * ComplexFormat.
     *
     * @param c Complex object to format
     * @return A formatted number in the form "Re(c) + Im(c)i"
     */
    public static String formatComplex( Complex c ) {
    	return DEFAULT.format( c );
    }
}


    
