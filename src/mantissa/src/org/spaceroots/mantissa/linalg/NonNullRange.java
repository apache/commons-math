// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.linalg;

import java.io.Serializable;

/** This class represents range of non null elements for rows or
 * columns of matrices.

 * <p>This class is used to reduce the computation loops by avoiding
 * using elements that are known to be zeros. For full matrices, the
 * range simply spans from 0 to the order of the matrix. For lower and
 * upper triangular matrices, its width will depend on the index of
 * the row or column considered. For diagonal matrices, the range is
 * reduced to one index.</p>

 * <p>The indices provided by the class correspond to the elements
 * that are non-null only according to the <emph>structure</emph> of
 * the matrix. The real value of the element is not
 * considered. Consider for example the following lower triangular
 * matrix :</p>

 * <pre>
 *   1 0 0 0
 *   2 8 0 0
 *   0 5 3 0
 *   3 2 4 4
 * </pre>

 * <p>The third rows begins with zero, but this is not a consequence
 * of the lower triangular structure, it is only a
 * coincidence. Therefore, the range (in row/columns count)
 * corresponding to third row will span from 0 to 2, not from 1 to 2.</p>

 * @version $Id$
 * @author L. Maisonobe

 */
class NonNullRange
  implements Serializable {

  /** Index in row/column count of the first non-null element. */
  public final int begin;

  /** Index in row/column count after the last non-null element. */
  public final int end;

  /** Simple constructor.
   * @param begin index in row/column count of the first non-null element
   * @param end index in row/column count after the last non-null element
   */
  public NonNullRange(int begin, int end)
  {
    this.begin = begin;
    this.end   = end;
  }

  /** Copy constructor.
   * @param range range to copy.
   */
  public NonNullRange(NonNullRange range) {
    begin = range.begin;
    end   = range.end;
  }

  /** Build the intersection of two ranges.
   * @param first first range to consider
   * @param second second range to consider
   */
  public static NonNullRange intersection(NonNullRange first,
                                          NonNullRange second) {
    return new NonNullRange(Math.max(first.begin, second.begin),
                            Math.min(first.end, second.end));
  }

  /** Build the reunion of two ranges.
   * @param first first range to consider
   * @param second second range to consider
   */
  public static NonNullRange reunion(NonNullRange first,
                                     NonNullRange second) {
    return new NonNullRange(Math.min(first.begin, second.begin),
                            Math.max(first.end, second.end));
  }

  private static final long serialVersionUID = 8175301560126132666L;

}
