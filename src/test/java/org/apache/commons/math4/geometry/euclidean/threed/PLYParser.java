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
package org.apache.commons.math4.geometry.euclidean.threed;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.math4.util.Precision;

/** This class is a small and incomplete parser for PLY files.
 * <p>
 * This parser is only intended for test purposes, it does not
 * parse the full header, it does not handle all properties,
 * it has rudimentary error handling.
 * </p>
 * @since 3.5
 */
public class PLYParser {

    /** Parsed vertices. */
    private Vector3D[] vertices;

    /** Parsed faces. */
    private int[][] faces;

    /** Reader for PLY data. */
    private BufferedReader br;

    /** Last parsed line. */
    private String line;

    /** Simple constructor.
     * @param stream stream to parse (closing it remains caller responsibility)
     * @exception IOException if stream cannot be read
     * @exception ParseException if stream content cannot be parsed
     */
    public PLYParser(final InputStream stream)
        throws IOException, ParseException {

        try {
            br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

            // parse the header
            List<Field> fields = parseNextLine();
            if (fields.size() != 1 || fields.get(0).getToken() != Token.PLY) {
                complain();
            }

            boolean parsing       = true;
            int nbVertices        = -1;
            int nbFaces           = -1;
            int xIndex            = -1;
            int yIndex            = -1;
            int zIndex            = -1;
            int vPropertiesNumber = -1;
            boolean inVertexElt   = false;
            boolean inFaceElt     = false;
            while (parsing) {
                fields = parseNextLine();
                if (fields.size() < 1) {
                    complain();
                }
                switch (fields.get(0).getToken()) {
                    case FORMAT:
                        if (fields.size() != 3 ||
                        fields.get(1).getToken() != Token.ASCII ||
                        fields.get(2).getToken() != Token.UNKNOWN ||
                        !Precision.equals(Double.parseDouble(fields.get(2).getValue()), 1.0, 0.001)) {
                            complain();
                        }
                        inVertexElt = false;
                        inFaceElt   = false;
                        break;
                    case COMMENT:
                        // we just ignore this line
                        break;
                    case ELEMENT:
                        if (fields.size() != 3 ||
                        (fields.get(1).getToken() != Token.VERTEX && fields.get(1).getToken() != Token.FACE) ||
                        fields.get(2).getToken() != Token.UNKNOWN) {
                            complain();
                        }
                        if (fields.get(1).getToken() == Token.VERTEX) {
                            nbVertices  = Integer.parseInt(fields.get(2).getValue());
                            inVertexElt = true;
                            inFaceElt   = false;
                        } else {
                            nbFaces     = Integer.parseInt(fields.get(2).getValue());
                            inVertexElt = false;
                            inFaceElt   = true;
                        }
                        break;
                    case PROPERTY:
                        if (inVertexElt) {
                            ++vPropertiesNumber;
                            if (fields.size() != 3 ||
                                (fields.get(1).getToken() != Token.CHAR   &&
                                 fields.get(1).getToken() != Token.UCHAR  &&
                                 fields.get(1).getToken() != Token.SHORT  &&
                                 fields.get(1).getToken() != Token.USHORT &&
                                 fields.get(1).getToken() != Token.INT    &&
                                 fields.get(1).getToken() != Token.UINT   &&
                                 fields.get(1).getToken() != Token.FLOAT  &&
                                 fields.get(1).getToken() != Token.DOUBLE)) {
                                complain();
                            }
                            if (fields.get(2).getToken() == Token.X) {
                                xIndex = vPropertiesNumber;
                            }else if (fields.get(2).getToken() == Token.Y) {
                                yIndex = vPropertiesNumber;
                            }else if (fields.get(2).getToken() == Token.Z) {
                                zIndex = vPropertiesNumber;
                            }
                        } else if (inFaceElt) {
                            if (fields.size() != 5 ||
                                fields.get(1).getToken()  != Token.LIST   &&
                                (fields.get(2).getToken() != Token.CHAR   &&
                                 fields.get(2).getToken() != Token.UCHAR  &&
                                 fields.get(2).getToken() != Token.SHORT  &&
                                 fields.get(2).getToken() != Token.USHORT &&
                                 fields.get(2).getToken() != Token.INT    &&
                                 fields.get(2).getToken() != Token.UINT) ||
                                (fields.get(3).getToken() != Token.CHAR   &&
                                 fields.get(3).getToken() != Token.UCHAR  &&
                                 fields.get(3).getToken() != Token.SHORT  &&
                                 fields.get(3).getToken() != Token.USHORT &&
                                 fields.get(3).getToken() != Token.INT    &&
                                 fields.get(3).getToken() != Token.UINT) ||
                                 fields.get(4).getToken() != Token.VERTEX_INDICES) {
                                complain();
                            }
                        } else {
                            complain();
                        }
                        break;
                    case END_HEADER:
                        inVertexElt = false;
                        inFaceElt   = false;
                        parsing     = false;
                        break;
                    default:
                        throw new ParseException("unable to parse line: " + line, 0);
                }
            }
            ++vPropertiesNumber;

            // parse vertices
            vertices = new Vector3D[nbVertices];
            for (int i = 0; i < nbVertices; ++i) {
                fields = parseNextLine();
                if (fields.size() != vPropertiesNumber ||
                    fields.get(xIndex).getToken() != Token.UNKNOWN ||
                    fields.get(yIndex).getToken() != Token.UNKNOWN ||
                    fields.get(zIndex).getToken() != Token.UNKNOWN) {
                    complain();
                }
                vertices[i] = new Vector3D(Double.parseDouble(fields.get(xIndex).getValue()),
                                           Double.parseDouble(fields.get(yIndex).getValue()),
                                           Double.parseDouble(fields.get(zIndex).getValue()));
            }

            // parse faces
            faces = new int[nbFaces][];
            for (int i = 0; i < nbFaces; ++i) {
                fields = parseNextLine();
                if (fields.isEmpty() ||
                    fields.size() != (Integer.parseInt(fields.get(0).getValue()) + 1)) {
                    complain();
                }
                faces[i] = new int[fields.size() - 1];
                for (int j = 0; j < faces[i].length; ++j) {
                    faces[i][j] = Integer.parseInt(fields.get(j + 1).getValue());
                }
            }

        } catch (NumberFormatException nfe) {
            complain();
        }
    }

    /** Complain about a bad line.
     * @exception ParseException always thrown
     */
    private void complain() throws ParseException {
        throw new ParseException("unable to parse line: " + line, 0);
    }

    /** Parse next line.
     * @return parsed fields
     * @exception IOException if stream cannot be read
     * @exception ParseException if the line does not contain the expected number of fields
     */
    private List<Field> parseNextLine()
        throws IOException, ParseException {
        final List<Field> fields = new ArrayList<Field>();
        line = br.readLine();
        if (line == null) {
            throw new EOFException();
        }
        final StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
            fields.add(new Field(tokenizer.nextToken()));
        }
        return fields;
    }

    /** Get the parsed vertices.
     * @return parsed vertices
     */
    public List<Vector3D> getVertices() {
        return Arrays.asList(vertices);
    }

    /** Get the parsed faces.
     * @return parsed faces
     */
    public List<int[]> getFaces() {
        return Arrays.asList(faces);
    }

    /** Tokens from PLY files. */
    private static enum Token {
        PLY, FORMAT, ASCII, BINARY_BIG_ENDIAN, BINARY_LITTLE_ENDIAN,
        COMMENT, ELEMENT, VERTEX, FACE, PROPERTY, LIST, OBJ_INFO,
        CHAR, UCHAR, SHORT, USHORT, INT, UINT, FLOAT, DOUBLE,
        X, Y, Z, VERTEX_INDICES, END_HEADER, UNKNOWN;
    }

    /** Parsed line fields. */
    private static class Field {

        /** Token. */
        private final Token token;

        /** Value. */
        private final String value;

        /** Simple constructor.
         * @param value field value
         */
        public Field(final String value) {
            Token parsedToken = null;
            try {
                parsedToken = Token.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException iae) {
                parsedToken = Token.UNKNOWN;
            }
            this.token = parsedToken;
            this.value = value;
        }

        /** Get the recognized token.
         * @return recognized token
         */
        public Token getToken() {
            return token;
        }

        /** Get the field value.
         * @return field value
         */
        public String getValue() {
            return value;
        }

    }

}
