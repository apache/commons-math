/*
 * 
 * Copyright (c) 2004 The Apache Software Foundation. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *  
 */

package org.apache.commons.math.linear;
import java.io.Serializable;

/**
 * Implementation for RealMatrix using a double[][] array to store entries
 * and <a href="http://www.math.gatech.edu/~bourbaki/
 * math2601/Web-notes/2num.pdf">LU decompostion</a> to support linear system 
 * solution and inverse.
 * <p>
 * The <a href="http://www.math.gatech.edu/~bourbaki/math2601/Web-notes
 * /2num.pdf">LU decompostion</a> is performed as needed, to support the 
 * following operations: <ul>
 * <li>solve</li>
 * <li>isSingular</li>
 * <li>getDeterminant</li>
 * <li>inverse</li> </ul>
 * <p>
 * <strong>Usage note</strong>:<br>
 * The LU decomposition is stored and reused on subsequent calls.  If matrix
 * data are modified using any of the public setXxx methods, the saved 
 * decomposition is discarded.  If data are modified via references to the
 * underlying array obtained using <code>getDataRef()</code>, then the stored
 * LU decomposition will not be discarded.  In this case, you need to 
 * explicitly invoke <code>LUDecompose()</code> to recompute the decomposition
 * before using any of the methods above.
 *
 * @version $Revision: 1.13 $ $Date: 2004/01/29 16:48:49 $
 */
public class RealMatrixImpl implements RealMatrix, Serializable {

	/** Entries of the matrix */
	private double data[][] = null;

	/** Entries of LU decomposition.
	 * All updates to data (other than luDecompostion) *must* set this to null
	 */
	private double lu[][] = null;

	/** Pivot array associated with LU decompostion */
	private int[] pivot = null;

	/** Parity of the permutation associated with the LU decomposition */
	private int parity = 1;

	/** Bound to determine effective singularity in LU decomposition */
	private static double TOO_SMALL = 10E-12;

	/** 
	 * Creates a matrix with no data
	 */
	public RealMatrixImpl() {
	}

	/**
	 * Create a new RealMatrix with the supplied row and column dimensions.
	 *
	 * @param rowDimension      the number of rows in the new matrix
	 * @param columnDimension   the number of columns in the new matrix
	 */
	public RealMatrixImpl(int rowDimension, int columnDimension) {
		data = new double[rowDimension][columnDimension];
		lu = null;
	}

	/**
	 * Create a new RealMatrix using the <code>data</code> as the underlying
	 * data array.
	 * <p>
	 * The input array is copied, not referenced.
	 *
	 * @param d data for new matrix
	 */
	public RealMatrixImpl(double[][] d) {
		this.copyIn(d);
		lu = null;
	}

	/**
	 * Create a new (column) RealMatrix using <code>v</code> as the 
	 * data for the unique column of the <code>v.length x 1</code> matrix 
	 * created.
	 * <p>
	 * The input array is copied, not referenced.
	 *
	 * @param v column vector holding data for new matrix
	 */
	public RealMatrixImpl(double[] v) {
		int nRows = v.length;
		data = new double[nRows][1];
		for (int row = 0; row < nRows; row++) {
			data[row][0] = v[row];
		}
	}

	/**
	 * Create a new RealMatrix which is a copy of this.
	 *
	 * @return  the cloned matrix
	 */
	public RealMatrix copy() {
		return new RealMatrixImpl(this.copyOut());
	}

	/**
	 * Compute the sum of this and <code>m</code>.
	 *
	 * @param m    matrix to be added
	 * @return     this + m
	 * @exception  IllegalArgumentException if m is not the same size as this
	 */
	public RealMatrix add(RealMatrix m) throws IllegalArgumentException {
		if (this.getColumnDimension() != m.getColumnDimension()
			|| this.getRowDimension() != m.getRowDimension()) {
			throw new IllegalArgumentException("matrix dimension mismatch");
		}
		int rowCount = this.getRowDimension();
		int columnCount = this.getColumnDimension();
		double[][] outData = new double[rowCount][columnCount];
		double[][] mData = m.getData();
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < columnCount; col++) {
				outData[row][col] = data[row][col] + mData[row][col];
			}
		}
		return new RealMatrixImpl(outData);
	}

	/**
	 * Compute  this minus <code>m</code>.
	 *
	 * @param m    matrix to be subtracted
	 * @return     this + m
	 * @exception  IllegalArgumentException if m is not the same size as *this
	 */
	public RealMatrix subtract(RealMatrix m) throws IllegalArgumentException {
		if (this.getColumnDimension() != m.getColumnDimension()
			|| this.getRowDimension() != m.getRowDimension()) {
			throw new IllegalArgumentException("matrix dimension mismatch");
		}
		int rowCount = this.getRowDimension();
		int columnCount = this.getColumnDimension();
		double[][] outData = new double[rowCount][columnCount];
		double[][] mData = m.getData();
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < columnCount; col++) {
				outData[row][col] = data[row][col] - mData[row][col];
			}
		}
		return new RealMatrixImpl(outData);
	}

	/**
	 * Returns the rank of the matrix.
	 *
	 * @return the rank of this matrix
	 */
	public int getRank() {
		// @TODO need to add singular value decomposition or drop this
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * Returns the result of adding d to each entry of this.
	 *
	 * @param d    value to be added to each entry
	 * @return     d + this
	 */
	public RealMatrix scalarAdd(double d) {
		int rowCount = this.getRowDimension();
		int columnCount = this.getColumnDimension();
		double[][] outData = new double[rowCount][columnCount];
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < columnCount; col++) {
				outData[row][col] = data[row][col] + d;
			}
		}
		return new RealMatrixImpl(outData);
	}

	/**
	 * Returns the result multiplying each entry of this by <code>d</code>
	 * @param d  value to multiply all entries by
	 * @return d * this
	 */
	public RealMatrix scalarMultiply(double d) {
		int rowCount = this.getRowDimension();
		int columnCount = this.getColumnDimension();
		double[][] outData = new double[rowCount][columnCount];
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < columnCount; col++) {
				outData[row][col] = data[row][col] * d;
			}
		}
		return new RealMatrixImpl(outData);
	}

	/**
	 * Returns the result postmultiplying this by <code>m</code>.
	 * @param m    matrix to postmultiply by
	 * @return     this*m
	 * @throws     IllegalArgumentException
	 *             if columnDimension(this) != rowDimension(m)
	 */
	public RealMatrix multiply(RealMatrix m) throws IllegalArgumentException {
		if (this.getColumnDimension() != m.getRowDimension()) {
			throw new IllegalArgumentException("Matrices are not multiplication compatible.");
		}
		int nRows = this.getRowDimension();
		int nCols = m.getColumnDimension();
		int nSum = this.getColumnDimension();
		double[][] mData = m.getData();
		double[][] outData = new double[nRows][nCols];
		double sum = 0;
		for (int row = 0; row < nRows; row++) {
			for (int col = 0; col < nCols; col++) {
				sum = 0;
				for (int i = 0; i < nSum; i++) {
					sum += data[row][i] * mData[i][col];
				}
				outData[row][col] = sum;
			}
		}
		return new RealMatrixImpl(outData);
	}

	/**
	 * Returns matrix entries as a two-dimensional array.
	 * <p>
	 * Makes a fresh copy of the underlying data.
	 *
	 * @return    2-dimensional array of entries
	 */
	public double[][] getData() {
		return copyOut();
	}

	/**
	 * Overwrites the underlying data for the matrix
	 * with a fresh copy of <code>inData</code>.
	 *
	 * @param  inData 2-dimensional array of entries
	 */
	public void setData(double[][] inData) {
		copyIn(inData);
		lu = null;
	}

	/**
	 * Returns a reference to the underlying data array.
	 * <p>
	 * Does not make a fresh copy of the underlying data.
	 *
	 * @return 2-dimensional array of entries
	 */
	public double[][] getDataRef() {
		return data;
	}

	/**
	 * Overwrites the underlying data for the matrix
	 * with a reference to <code>inData</code>.
	 * <p>
	 * Does not make a fresh copy of <code>data</code>.
	 *
	 * @param  inData 2-dimensional array of entries
	 */
	public void setDataRef(double[][] inData) {
		this.data = inData;
		lu = null;
	}

	/**
	 *
	 * @return norm
	 */
	public double getNorm() {
		double maxColSum = 0;
		for (int col = 0; col < this.getColumnDimension(); col++) {
			double sum = 0;
			for (int row = 0; row < this.getRowDimension(); row++) {
				sum += Math.abs(data[row][col]);
			}
			maxColSum = Math.max(maxColSum, sum);
		}
		return maxColSum;
	}

	/**
	 * Returns the entries in row number <code>row</code> as an array.
	 *
	 * @param row the row to be fetched
	 * @return array of entries in the row
	 * @throws MatrixIndexException if the specified row is greater 
	 *                              than the number of rows in this matrix
	 */
	public double[] getRow(int row) throws MatrixIndexException {
		if ( !isValidCoordinate( row, 1 ) ) {
			throw new MatrixIndexException("illegal row argument");
		}
		int ncols = this.getColumnDimension();
		double[] out = new double[ncols];
		System.arraycopy(data[row - 1], 0, out, 0, ncols);
		return out;
	}

	/**
	 * Returns the entries in column number <code>col</code> as an array.
	 *
	 * @param col  column to fetch
	 * @return array of entries in the column
	 * @throws MatrixIndexException if the specified column is greater
	 *                              than the number of columns in this matrix
	 */
	public double[] getColumn(int col) throws MatrixIndexException {
		if ( !isValidCoordinate(1, col) ) {
			throw new MatrixIndexException("illegal column argument");
		}
		int nRows = this.getRowDimension();
		double[] out = new double[nRows];
		for (int row = 0; row < nRows; row++) {
			out[row] = data[row][col - 1];
		}
		return out;
	}

	/**
	 * Returns the entry in the specified row and column.
	 *
	 * @param row  row location of entry to be fetched  
	 * @param column  column location of entry to be fetched
	 * @return matrix entry in row,column
	 * @throws MatrixIndexException if the specified coordinate is outside 
	 *                              the dimensions of this matrix
	 */
	public double getEntry(int row, int column)
		throws MatrixIndexException {
		if (!isValidCoordinate(row,column)) {
			throw new MatrixIndexException("matrix entry does not exist");
		}
		return data[row - 1][column - 1];
	}

	/**
	 * Sets the entry in the specified row and column to the specified value.
	 *
	 * @param row    row location of entry to be set 
	 * @param column    column location of entry to be set
	 * @param value  value to set 
	 * @throws MatrixIndexException if the specified coordinate is outside
	 *                              he dimensions of this matrix
	 */
	public void setEntry(int row, int column, double value)
		throws MatrixIndexException {
		if (!isValidCoordinate(row,column)) {
			throw new MatrixIndexException("matrix entry does not exist");
		}
		data[row - 1][column - 1] = value;
		lu = null;
	}

	/**
	 *
	 * @return transpose matrix
	 */
	public RealMatrix transpose() {
		int nRows = this.getRowDimension();
		int nCols = this.getColumnDimension();
		RealMatrixImpl out = new RealMatrixImpl(nCols, nRows);
		double[][] outData = out.getDataRef();
		for (int row = 0; row < nRows; row++) {
			for (int col = 0; col < nCols; col++) {
				outData[col][row] = data[row][col];
			}
		}
		return out;
	}

	/**
	 * @return inverse matrix
	 * @throws IllegalArgumentException if this is not invertible
	 */
	public RealMatrix inverse() throws IllegalArgumentException {
		return solve(getIdentity(this.getRowDimension()));
	}

	/**
	 * @return determinant
	 * @throws IllegalArgumentException if matrix is not square
	 */
	public double getDeterminant() throws InvalidMatrixException {
		if (!isSquare()) {
			throw new InvalidMatrixException("matrix is not square");
		}
		if (isSingular()) { // note: this has side effect of attempting LU
			return 0d; //       decomp if lu == null
		} else {
			double det = (double) parity;
			for (int i = 0; i < this.getRowDimension(); i++) {
				det *= lu[i][i];
			}
			return det;
		}
	}

	/**
	 * @return true if the matrix is square (rowDimension = columnDimension)
	 */
	public boolean isSquare() {
		return (this.getColumnDimension() == this.getRowDimension());
	}

	/**
	 * @return true if the matrix is singular
	 */
	public boolean isSingular() {
		// @TODO A bad way to check for a singular matrix, is this the only way - kick off an LU decompose?
		if (lu == null) {
			try {
				LUDecompose();
				return false;
			} catch (InvalidMatrixException ex) {
				return true;
			}
		} else { // LU decomp must have been successfully performed
			return false; // so the matrix is not singular
		}
	}

	/**
	 * @return rowDimension
	 */
	public int getRowDimension() {
		return data.length;
	}

	/**
	 * @return columnDimension
	 */
	public int getColumnDimension() {
		return data[0].length;
	}

	/**
	 * @return trace
	 * @throws IllegalArgumentException if the matrix is not square
	 */
	public double getTrace() throws IllegalArgumentException {
		if (!isSquare()) {
			throw new IllegalArgumentException("matrix is not square");
		}
		double trace = data[0][0];
		for (int i = 1; i < this.getRowDimension(); i++) {
			trace += data[i][i];
		}
		return trace;
	}

	/**
	 * @param v vector to operate on
	 * @throws IllegalArgumentException if columnDimension != v.length
	 * @return resulting vector
	 */
	public double[] operate(double[] v) throws IllegalArgumentException {
		if (v.length != this.getColumnDimension()) {
			throw new IllegalArgumentException("vector has wrong length");
		}
		int nRows = this.getRowDimension();
		int nCols = this.getColumnDimension();
		double[] out = new double[v.length];
		for (int row = 0; row < nRows; row++) {
			double sum = 0;
			for (int i = 0; i < nCols; i++) {
				sum += data[row][i] * v[i];
			}
			out[row] = sum;
		}
		return out;
	}

	/**
	 * @param v vector to premultiply by
	 * @throws IllegalArgumentException if rowDimension != v.length
	 * @return resulting matrix
	 */
	public RealMatrix preMultiply(double[] v) throws IllegalArgumentException {
		int nCols = this.getColumnDimension();
		if (v.length != nCols) {
			throw new IllegalArgumentException("vector has wrong length");
		}
		// being a bit lazy here -- probably should implement directly, like
		// operate
		RealMatrix pm = new RealMatrixImpl(v).transpose();
		return pm.multiply(this);
	}

	/**
	 * Returns a matrix of (column) solution vectors for linear systems with
	 * coefficient matrix = this and constant vectors = columns of
	 * <code>b</code>. 
	 *
	 * @param b  array of constant forming RHS of linear systems to
	 * to solve
	 * @return solution array
	 * @throws IllegalArgumentException if this.rowDimension != row dimension
	 * @throws InvalidMatrixException if this matrix is not square or is singular
	 */
	public double[] solve(double[] b) throws IllegalArgumentException, InvalidMatrixException {
		int nRows = this.getRowDimension();
		if (b.length != nRows) {
			throw new IllegalArgumentException("constant vector has wrong length");
		}
		RealMatrix bMatrix = new RealMatrixImpl(b);
		double[][] solution = ((RealMatrixImpl) (solve(bMatrix))).getDataRef();
		double[] out = new double[nRows];
		for (int row = 0; row < nRows; row++) {
			out[row] = solution[row][0];
		}
		return out;
	}

	/**
	 * Returns a matrix of (column) solution vectors for linear systems with
	 * coefficient matrix = this and constant vectors = columns of
	 * <code>b</code>. 
	 *
	 * @param b  matrix of constant vectors forming RHS of linear systems to
	 * to solve
	 * @return matrix of solution vectors
	 * @throws IllegalArgumentException if this.rowDimension != row dimension
	 * @throws InvalidMatrixException if this matrix is not square or is singular
	 */
	public RealMatrix solve(RealMatrix b) throws IllegalArgumentException, InvalidMatrixException  {
		if (b.getRowDimension() != this.getRowDimension()) {
			throw new IllegalArgumentException("Incorrect row dimension");
		}
		if (!this.isSquare()) {
			throw new InvalidMatrixException("coefficient matrix is not square");
		}
		if (this.isSingular()) { // side effect: compute LU decomp
			throw new InvalidMatrixException("Matrix is singular.");
		}

		int nCol = this.getColumnDimension();
		int nColB = b.getColumnDimension();
		int nRowB = b.getRowDimension();

		// Apply permutations to b
		double[][] bv = b.getData();
		double[][] bp = new double[nRowB][nColB];
		for (int row = 0; row < nRowB; row++) {
			for (int col = 0; col < nColB; col++) {
				bp[row][col] = bv[pivot[row]][col];
			}
		}
		bv = null;

		// Solve LY = b
		for (int col = 0; col < nCol; col++) {
			for (int i = col + 1; i < nCol; i++) {
				for (int j = 0; j < nColB; j++) {
					bp[i][j] -= bp[col][j] * lu[i][col];
				}
			}
		}

		// Solve UX = Y
		for (int col = nCol - 1; col >= 0; col--) {
			for (int j = 0; j < nColB; j++) {
				bp[col][j] /= lu[col][col];
			}
			for (int i = 0; i < col; i++) {
				for (int j = 0; j < nColB; j++) {
					bp[i][j] -= bp[col][j] * lu[i][col];
				}
			}
		}

		RealMatrixImpl outMat = new RealMatrixImpl(bp);
		return outMat;
	}

	/**
	 * Computes a new <a href="http://www.math.gatech.edu/~bourbaki/
	 * math2601/Web-notes/2num.pdf">LU decompostion</a> for this matrix,
	 * storing the result for use by other methods.
	 * <p>
	 * <strong>Implementation Note</strong>:<br>
	 * Uses <a href="http://www.damtp.cam.ac.uk/user/fdl/
	 * people/sd/lectures/nummeth98/linear.htm">Crout's algortithm</a>,
	 * with partial pivoting.
	 * <p>
	 * <strong>Usage Note</strong>:<br>
	 * This method should rarely be invoked directly. Its only use is
	 * to force recomputation of the LU decomposition when changes have been
	 * made to the underlying data using direct array references. Changes
	 * made using setXxx methods will trigger recomputation when needed
	 * automatically.
	 *
	 * @throws InvalidMatrixException if the matrix is singular or if the matrix has more rows than columns
	 */
	public void LUDecompose() throws InvalidMatrixException {
		// @TODO Bad method name - get rid of leading capitals
		
		int nRows = this.getRowDimension();
		int nCols = this.getColumnDimension();
		if (nRows < nCols) {
			throw new InvalidMatrixException("LU decomposition requires row dimension >= column dimension");
		}
		lu = this.getData();

		// Initialize pivot array and parity
		pivot = new int[nRows];
		for (int row = 0; row < nRows; row++) {
			pivot[row] = row;
		}
		parity = 1;

		// Loop over columns
		for (int col = 0; col < nCols; col++) {

			double sum = 0;

			// upper
			for (int row = 0; row < col; row++) {
				sum = lu[row][col];
				for (int i = 0; i < row; i++) {
					sum -= lu[row][i] * lu[i][col];
				}
				lu[row][col] = sum;
			}

			// lower
			int max = col; // pivot row
			double largest = 0d;
			for (int row = col; row < nRows; row++) {
				sum = lu[row][col];
				for (int i = 0; i < col; i++) {
					sum -= lu[row][i] * lu[i][col];
				}
				lu[row][col] = sum;

				// maintain best pivot choice
				if (Math.abs(sum) > largest) {
					largest = Math.abs(sum);
					max = row;
				}
			}

			// Singularity check
			if (Math.abs(lu[max][col]) < TOO_SMALL) {
				lu = null;
				throw new InvalidMatrixException("matrix is singular");
			}

			// Pivot if necessary
			if (max != col) {
				double tmp = 0;
				for (int i = 0; i < nCols; i++) {
					tmp = lu[max][i];
					lu[max][i] = lu[col][i];
					lu[col][i] = tmp;
				}
				int temp = pivot[max];
				pivot[max] = pivot[col];
				pivot[col] = temp;
				parity = -parity;
			}

			//Divide the lower elements by the "winning" diagonal elt.
			for (int row = col + 1; row < nRows; row++) {
				lu[row][col] /= lu[col][col];
			}
		}
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("RealMatrixImpl{");
		for (int i = 0; i < data.length; i++) {
			if (i > 0)
				res.append(",");
			res.append("{");
			for (int j = 0; j < data[0].length; j++) {
				if (j > 0)
					res.append(",");
				res.append(data[i][j]);
			} //for
			res.append("}");
		} //for
		res.append("}");
		return res.toString();
	} //toString

	//------------------------ Protected methods

	/**
	 * Returns <code>dimension x dimension</code> identity matrix.
	 *
	 * @param dimension dimension of identity matrix to generate
	 * @return identity matrix
	 */
	protected RealMatrix getIdentity(int dimension) {
		RealMatrixImpl out = new RealMatrixImpl(dimension, dimension);
		double[][] d = out.getDataRef();
		for (int row = 0; row < dimension; row++) {
			for (int col = 0; col < dimension; col++) {
				d[row][col] = row == col ? 1d : 0d;
			}
		}
		return out;
	}

	//------------------------ Private methods

	/**
	 * Returns a fresh copy of the underlying data array.
	 *
	 * @return a copy of the underlying data array.
	 */
	private double[][] copyOut() {
		int nRows = this.getRowDimension();
		double[][] out = new double[nRows][this.getColumnDimension()];
		// can't copy 2-d array in one shot, otherwise get row references
		for (int i = 0; i < nRows; i++) {
			System.arraycopy(data[i], 0, out[i], 0, data[i].length);
		}
		return out;
	}

	/**
	 * Replaces data with a fresh copy of the input array.
	 *
	 * @param in data to copy in
	 */
	private void copyIn(double[][] in) {
		int nRows = in.length;
		int nCols = in[0].length;
		data = new double[nRows][nCols];
		System.arraycopy(in, 0, data, 0, in.length);
		for (int i = 0; i < nRows; i++) {
			System.arraycopy(in[i], 0, data[i], 0, nCols);
		}
		lu = null;
	}

	/**
	 * Tests a given coordinate as being valid or invalid
	 *
	 * @return true if the coordinate is with the current dimensions
	 */
	private boolean isValidCoordinate(int row, int col) {
		int nRows = this.getRowDimension();
		int nCols = this.getColumnDimension();

		return !(row < 1 || row > nRows || col < 1 || col > nCols);
	}

}
