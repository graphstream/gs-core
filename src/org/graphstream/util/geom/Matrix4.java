/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.util.geom;

import org.graphstream.util.InvalidArgumentException;
import org.graphstream.util.InvalidIndexException;

// cm(mat,y,x)	(mat[(x<<2)+y])	 * OpenGL column-major order
// rm(mat,y,x)	(mat[(y<<2)+x])	 * Standard row-major order (RenderMan for example)

/**
 * 4x4 Matrix type.
 *
 * <p>The <code>Matrix4</code> class, as its name suggests, does not define a
 * base for abritrary sized matrices. All computations are done on 4x4 square
 * matrices, but some operators consider only 3x3 or 2x2 sub-matrices in the
 * main 4x4 one.</p>
 *
 * <p>In all prototypes, <em>x</em> identifies the abscissas or columns, and
 * <em>y</em> the ordinates or rows. When defining coordinates in the matrix,
 * the rows, <em>y</em>, are always given before the columns, <em>x</em>. For
 * instance to access the element at row 3 of column 2 in a matrix <em>m</em>
 * you write <em>m(3,2)</em>. This is the usual mathematical notation. We will
 * write (<em>y</em>,<em>x</em>).</p>
 *
 * <p>Clearly this matrix class is aimed at 3D and 2D operations, not to large
 * computations or complex linear algebra.</p>
 *
 * <h3>Implementation</h3>
 *
 * <p>The matrix is stored in a linear array of 16 adjacent cells. Therefore, all
 * cells are consecutive in memory, and a reference on the this array can be
 * used to dialog with other libraries or JNI/CNI code.</p>
 *
 * <p>The ordering of elements in memory is column-major order. This has been
 * choosed to be compatible with OpenGL. You can directly use this matrix class
 * with OpenGL function that take a matrix as argument.</p>
 *
 * <p>For instance here are two representations of the matrix:<pre>
 *    | 0   4   8  12 |
 *    | 1   5   9  13 |  == [0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15]
 *    | 2   6  10  14 |
 *    | 3   7  11  15 |
 * </pre>Therefore we have a translation matrix that looks like this:<pre>
 *    |  1  0  0  0 |
 *    |  0  1  0  0 |
 *    |  0  0  1  0 |
 *    | Tx Ty Tz  1 |
 * </pre></p>
 *		
 * <p>This memory organisation should not be visible from the exterior until you
 * directly access the {@link #data} array to pass it as argument to another class. In
 * this case, if you are using OpenGL or a library that use the column-major
 * convention, you have nothing to do, else, you should transpose the matrix.</p>
 *
 * <p>For example, RanderMan use row-major order as most mathematical textbook
 * do. I choosed to adopt the OpenGL convention because OpenGL is a direct
 * rendering library wich needs speed whereas RenderMan is mostly designed for
 * batch processing and in this case, we can aford a transposition to correctly
 * organize memory.</p>
 *
 * <p>The access to elements is the same as in, usual mathematical notations,
 * you give the row first (noted <em>y</em> here) and the column second (noted
 * <em>x</em>) here.</p>
 *
 * @author	Antoine Dutot
 * @since 20000210
 * @note 20030414
 */
public class Matrix4
	implements java.io.Serializable
{
// Types

	private static final long serialVersionUID = 8843888609006249117L;
	public static final int COLUMN_MAJOR = 0;
	public static final int ROW_MAJOR    = 1;

// Attributes
	
	/**
	 * Array of 16 sequential cells. The column-major ordering is used to store
	 * elements. This should not be visible out of the matrix excepted when you
	 * directly access the data array. This ordering has been choosed to be
	 * compatible with OpenGL.
	 */
	public float[] data = new float[16];
		
// Constructors
	
	/**
	 * New zero 4x4 matrix.
	 */
	public
	Matrix4()
	{
	}
	
	/**
	 * New 4x4 matrix. All cells are set to <code>init</code>.
	 * @param init The element to copy in each cell of the matrix.
	 * @see #fill(float)
	 */
	public
	Matrix4( float init )
	{
		fill( init );
	}
	
	/**
	 * New copy of <code>other</code>.
	 * @param other The matrix to copy.
	 * @see #copy(Matrix4)
	 */
	public
	Matrix4( Matrix4 other )
	{
		copy( other );
	}
	
	/**
	 * New copy of the 16 components of <code>m16</code> at
	 * <code>offset</code>. <code>m16</code> must have at least
	 * <code>offset</code>+16 cells. order can be {@link #COLUMN_MAJOR} or
	 * {@link #ROW_MAJOR} and indicates how the matrix is stored in
	 * <code>m16</code> ({@link #COLUMN_MAJOR} is the OpenGL convention, {@link
	 * #ROW_MAJOR }is the C and RenderMan convention). See also {@link
	 * #copy(float[],int,int)}.
	 * @param m16 The array of values to copy, it must at least contain 16 values.
	 * @param offset The offset inside the m16 array to start copy.
	 * @param order The matrix order (row or column major).
	 * @throws InvalidArgumentException If <code>order</code> has an invalid value.
	 * @throws InvalidIndexException If <code>m16</code> has less than
	 * <code>offset</code>+16 cells.
	 */
	public
	Matrix4( float[] m16, int offset, int order ) throws
		InvalidArgumentException, InvalidIndexException
	{
		copy( m16, offset, order );
	}

// Predicates
	
	/**
	 * Does the pair (<code>x</code>,<code>y</code>] identifies a component ?
	 * <code>x</code> is the abcissa or column and <code>y</code> the ordinate
	 * or row.
	 */
	public boolean
	validComponent( int x, int y )
	{
		return( x >= 0 && x < 4 && y >= 0 && y < 4 );
	}
	
	/**
	 * Is this equal to other ?
	 */
	public boolean
	equals( Matrix4 other )
	{
		for( int y = 0; y < 4; ++y )
		{
			if ( data[0+y]  != other.data[0+y] )  return false;
			if ( data[4+y]  != other.data[4+y] )  return false;
			if ( data[8+y]  != other.data[8+y] )  return false;
			if ( data[12+y] != other.data[12+y] ) return false;
		}

		return true;
	}

// Accessors

	/**
	 * Component at (<code>y</code>,<code>x</code>). The row <code>y</code>
	 * (ordinate), is given before he column <code>x</code> (abscissa) as in
	 * usual mathematical notations.
	 */
	public float
	get( int y, int x )
	{
		return data[(x<<2)+y];
	}

// Commands
	
	/**
	 * Change this into an identity matrix.
	 */
	public void
	setIdentity()
	{
		data[0] = 1.0f;	data[4] = 0.0f;	data[8]  = 0.0f;	data[12] = 0.0f;
		data[1] = 0.0f;	data[5] = 1.0f;	data[9]  = 0.0f;	data[13] = 0.0f;
		data[2] = 0.0f;	data[6] = 0.0f;	data[10] = 1.0f;	data[14] = 0.0f;
		data[3] = 0.0f;	data[7] = 0.0f;	data[11] = 0.0f;	data[15] = 1.0f;
	}
	
	/**
	 * Set the line to be the vector
	 * [<code>x</code>,<code>y</code>,<code>z</code>,<code>w</code>].
	 */
	public void
	setLine( int line, float x, float y, float z, float w )
	{
		data[ 0 + line] = x;
		data[ 4 + line] = y; 
		data[ 8 + line] = z;
		data[12 + line] = w;
	}
	
	/**
	 * Set the component at (<code>x</code>,<code>y</code>) to value.
	 */
	public void
	set( int y, int x, float value )
	{
		data[( x * 4 ) + y] = value;
	}
	
	/**
	 * Assign value to each component.
	 */
	public void
	fill( float value )
	{
		data[0] = data[4] = data[8]  = data[12] = value;
		data[1] = data[5] = data[9]  = data[13] = value;
		data[2] = data[6] = data[10] = data[14] = value;
		data[3] = data[7] = data[11] = data[15] = value;
	}
	
	/**
	 * Make this a copy of other.
	 */
	public void
	copy( final Matrix4 other )
	{
		if( other == this )
			return;
		
		System.arraycopy( other.data, 0, data, 0, 16 );
	}
	
	/**
	 * Make this a copy of the 16 components of <code>m16</code> at
	 * <code>offset</code>. <code>m16</code> must have at least
	 * <code>offset</code>+16 cells. order can be {@link #COLUMN_MAJOR} or
	 * {@link #ROW_MAJOR} and indicates how the matrix is stored in
	 * <code>m16</code> ({@link #COLUMN_MAJOR} is the OpenGL convention, {@link
	 * #ROW_MAJOR} is the C and RenderMan convention). See also {@link
	 * #copy(float[],int,int)}.
	 *
	 * @throws InvalidArgumentException If <code>order</code> has an invalid
	 * value.
	 * @throws InvalidIndexException If <code>m16</code> has less than
	 * <code>offset</code>+16 cells.
	 */
	public void
	copy( float[] m16, int offset, int order )
	{
		if( order == COLUMN_MAJOR )
		{
			try
			{
				System.arraycopy( m16, offset, data, 0, 16 );
			}
			catch( IndexOutOfBoundsException e )
			{
				throw new InvalidIndexException( e.getMessage () );
			}
		}
		else if( order == ROW_MAJOR )
		{
			data[0] = m16[0];   data[4] = m16[1];   data[8]  = m16[2];   data[12] = m16[3];
			data[1] = m16[4];   data[5] = m16[5];   data[9]  = m16[6];   data[13] = m16[7];
			data[2] = m16[8];   data[6] = m16[9];   data[10] = m16[10];  data[14] = m16[9];
			data[3] = m16[12];  data[7] = m16[13];  data[11] = m16[14];  data[15] = m16[15];
		}
		else
		{
			throw new InvalidArgumentException(
				"order must be `COLUMN_MAJOR' or `ROW_MAJOR'" );
		}
	}
	
	/**
	 * Add each element of <code>other</code> to the corresponding element of
	 * this. It is allowed to add this matrix to itself.
	 */
	public void
	add( final Matrix4 other )
	{
		data[0] += other.data[0];
		data[1] += other.data[1];
		data[2] += other.data[2];
		data[3] += other.data[3];
		
		data[4] += other.data[4];
		data[5] += other.data[5];
		data[6] += other.data[6];
		data[7] += other.data[7];
		
		data[8]  += other.data[8];
		data[9]  += other.data[9];
		data[10] += other.data[10];
		data[11] += other.data[11];
		
		data[12] += other.data[12];
		data[13] += other.data[13];
		data[14] += other.data[14];
		data[15] += other.data[15];
	}
	
	/**
	 * Substract each element of <code>other</code> to the corresponding
	 * element of this. It is allowed to substract this matrix from itself.
	 */
	public void
	sub( final Matrix4 other )
	{
		data[0] -= other.data[0];
		data[1] -= other.data[1];
		data[2] -= other.data[2];
		data[3] -= other.data[3];
								
		data[4] -= other.data[4];
		data[5] -= other.data[5];
		data[6] -= other.data[6];
		data[7] -= other.data[7];
								
		data[8]  -= other.data[8];
		data[9]  -= other.data[9];
		data[10] -= other.data[10];
		data[11] -= other.data[11];
									
		data[12] -= other.data[12];
		data[13] -= other.data[13];
		data[14] -= other.data[14];
		data[15] -= other.data[15];
	}
	
	/**
	 * Multiply this as left hand side by <code>rhs</code> as right hand side.
	 * The result is stored in this, it is not allowed to multiply this matrix
	 * by itself.
	 */
	public void
	mult( final Matrix4 rhs )
		throws InvalidArgumentException
	{
		if( rhs == this )
			throw new InvalidArgumentException( "this and rhs cannot be the same matrix" );

		float a, b, c, d;

		//
		// For each row of the result.
		//
		for ( int i = 0; i < 4; ++i )
		{
			//
			// Row i of this.
			//
			a = data[     i];
			b = data[ 4 + i];
			c = data[ 8 + i];
			d = data[12 + i];

			//
			// With each column of rhs.
			//
			data[i] =
					( a * rhs.data[0] )
				+	( b * rhs.data[1] )
				+	( c * rhs.data[2] )
				+	( d * rhs.data[3] );
			
			data[4+i] =
					( a * rhs.data[4] )
				+	( b * rhs.data[5] )
				+	( c * rhs.data[6] )
				+	( d * rhs.data[7] );
			
			data[8+i] =
					( a * rhs.data[8] )
				+	( b * rhs.data[9] )
				+	( c * rhs.data[10] )
				+	( d * rhs.data[11] );
			
			data[12+i] =
					( a * rhs.data[12] )
				+	( b * rhs.data[13] )
				+	( c * rhs.data[14] )
				+	( d * rhs.data[15] );
		}
	}
	
	/**
	 * Make this the result of the <code>lhs</code> times <code>rhs</code>
	 * product. Neither, <code>rhs</code> or <code>lhs</code> can be this
	 * matrix.
	 */
	public void
	matMult( final Matrix4 lhs, final Matrix4 rhs ) throws InvalidArgumentException
	{
		if( lhs == this || rhs == this )
			throw new InvalidArgumentException( "neither lhs nor rhs can be this" );
		
		//
		// For each row of the result,
		// i-th row of lhs and each column of rhs.
		//
		for( int i = 0; i < 4; ++i )
		{
			data[i] =
						( lhs.data[   i] * rhs.data[0] ) 
					+	( lhs.data[4 +i] * rhs.data[1] )
					+	( lhs.data[8 +i] * rhs.data[2] )
					+	( lhs.data[12+i] * rhs.data[3] );
			
			data[4+i] =
						( lhs.data[   i] * rhs.data[4] ) 
					+	( lhs.data[4 +i] * rhs.data[5] )
					+	( lhs.data[8 +i] * rhs.data[6] )
					+	( lhs.data[12+i] * rhs.data[7] );
			
			data[8+i] =
						( lhs.data[   i] * rhs.data[8] ) 
					+	( lhs.data[4 +i] * rhs.data[9] )
					+	( lhs.data[8 +i] * rhs.data[10] )
					+	( lhs.data[12+i] * rhs.data[11] );
			
			data[12+i] =
						( lhs.data[   i] * rhs.data[12] ) 
					+	( lhs.data[4 +i] * rhs.data[13] )
					+	( lhs.data[8 +i] * rhs.data[14] )
					+	( lhs.data[12+i] * rhs.data[15] );
		}
		
	}
	
	/**
	 * <code>result</code> gets the mulitplication of this by <code>rhs</code>.
	 * <code>result</code> must not be <code>rhs</code>. Only the upper 3x3
	 * sub-matrix is considered for the multiplication. Whatever be the
	 * coefficients in the last line and last column of the matrix, they are
	 * not used.
	 */
	public void
	vec3Mult( final Vector3 rhs, Vector3 result )
		throws InvalidArgumentException
	{
		if( rhs == result )
			throw new InvalidArgumentException( "result must not be lhs" );

		result.data[0] = ( data[0] * rhs.data[0] ) + ( data[4] * rhs.data[1] ) + ( data[8]  * rhs.data[2] );
		result.data[1] = ( data[1] * rhs.data[0] ) + ( data[5] * rhs.data[1] ) + ( data[9]  * rhs.data[2] );
		result.data[2] = ( data[2] * rhs.data[0] ) + ( data[6] * rhs.data[1] ) + ( data[10] * rhs.data[2] );
	}
	
	/**
	 * <code>result</code> gets the mulitplication of this by <code>rhs</code>
	 * in homogenous coordinates. <code>result</code> must not be
	 * <code>rhs</code>. At the contrary of {@link #vec3Mult(Vector3, Vector3)}
	 * the vector, with only three components, is considered to be homogenous
	 * with an implicit w coefficient of 1. The whole 4x4 matrix is used (thus
	 * translations occurs). At the end the vector is divided by the resulting
	 * w.
	 */
	public void
	hVec3Mult( final Vector3 rhs, Vector3 result, boolean divide_by_w )
		throws InvalidArgumentException
	{
		if( rhs == result )
			throw new InvalidArgumentException( "rhs must not be result" );
		
		result.data[0] = ( data[0] * rhs.data[0] ) + ( data[4] * rhs.data[1] ) + ( data[8]  * rhs.data[2] ) + ( data[12] );
		result.data[1] = ( data[1] * rhs.data[0] ) + ( data[5] * rhs.data[1] ) + ( data[9]  * rhs.data[2] ) + ( data[13] );
		result.data[2] = ( data[2] * rhs.data[0] ) + ( data[6] * rhs.data[1] ) + ( data[10] * rhs.data[2] ) + ( data[14] );

		if( divide_by_w )
		{
			float w = ( data[3] * rhs.data[0] ) + ( data[7] * rhs.data[1] ) + ( data[11] * rhs.data[2] ) + ( data[15] );
		
			if( w != 1.0 )
				result.scalarDiv( w );
		}
	}
	
	/**
	 * <code>result</code> gets the mulitplication of this by <code>rhs</code>
	 * in homogenous coordinates. <code>result</code> must not be
	 * <code>rhs</code>.
	 */
	public void
	vec4Mult( final Vector4 rhs, Vector4 result )
		throws InvalidArgumentException
	{
		if( rhs == result )
			throw new InvalidArgumentException( "rhs must not be result" );
		
		result.data[0] = ( data[0] * rhs.data[0] ) + ( data[4] * rhs.data[1] ) + ( data[8]  * rhs.data[2] ) + ( data[12] * rhs.data[3] );
		result.data[1] = ( data[1] * rhs.data[0] ) + ( data[5] * rhs.data[1] ) + ( data[9]  * rhs.data[2] ) + ( data[13] * rhs.data[3] );
		result.data[2] = ( data[2] * rhs.data[0] ) + ( data[6] * rhs.data[1] ) + ( data[10] * rhs.data[2] ) + ( data[14] * rhs.data[3] );
		result.data[3] = ( data[3] * rhs.data[0] ) + ( data[7] * rhs.data[1] ) + ( data[11] * rhs.data[2] ) + ( data[15] * rhs.data[3] );
	}
	
	/**
	 * <code>result</code> gets the mulitplication of this by <code>rhs</code>.
	 * <code>result</code> must not be <code>rhs</code>. Only the upper 3x3
	 * sub-matrix is considered for the multiplication. Whatever be the
	 * coefficients in the last line and last column of the matrix, they are
	 * not used.
	 */
	public void
	point3Mult( final Point3 rhs, Point3 result )
		throws InvalidArgumentException
	{
		if( rhs == result )
			throw new InvalidArgumentException( "result must not be lhs" );

		result.x = ( data[0] * rhs.x ) + ( data[4] * rhs.y ) + ( data[8]  * rhs.z );
		result.y = ( data[1] * rhs.x ) + ( data[5] * rhs.y ) + ( data[9]  * rhs.z );
		result.z = ( data[2] * rhs.x ) + ( data[6] * rhs.y ) + ( data[10] * rhs.z );
	}
	
	/**
	 * <code>result</code> gets the mulitplication of this by <code>rhs</code>
	 * in homogenous coordinates. <code>result</code> must not be
	 * <code>rhs</code>. At the contrary of {@link #vec3Mult(Vector3, Vector3)}
	 * the vector, with only three components, is considered to be homogenous
	 * with an implicit w coefficient of 1. The whole 4x4 matrix is used (thus
	 * translations occurs). At the end the vector is divided by the resulting
	 * w.
	 */
	public void
	hPoint3Mult( final Point3 rhs, Point3 result, boolean divide_by_w )
		throws InvalidArgumentException
	{
		if( rhs == result )
			throw new InvalidArgumentException( "rhs must not be result" );
		
		result.x = ( data[0] * rhs.x ) + ( data[4] * rhs.y ) + ( data[8]  * rhs.z ) + ( data[12] );
		result.y = ( data[1] * rhs.x ) + ( data[5] * rhs.y ) + ( data[9]  * rhs.z ) + ( data[13] );
		result.z = ( data[2] * rhs.x ) + ( data[6] * rhs.y ) + ( data[10] * rhs.z ) + ( data[14] );

		if( divide_by_w )
		{
			float w = ( data[3] * rhs.x ) + ( data[7] * rhs.y ) + ( data[11] * rhs.z ) + ( data[15] );

			if( w != 1.0 )
			{
				result.x /= w;
				result.y /= w;
				result.z /= w;
			}
		}
	}
	
	/**
	 * Add <code>value</code> to each element.
	 */
	public void
	scalarAdd( float value )
	{
		data[0] += value;
		data[1] += value;
		data[2] += value;
		data[3] += value;
				
		data[4] += value;
		data[5] += value;
		data[6] += value;
		data[7] += value;
				
		data[8]  += value;
		data[9]  += value;
		data[10] += value;
		data[11] += value;
				
		data[12] += value;
		data[13] += value;
		data[14] += value;
		data[15] += value;
	}
	
	/**
	 * Substract <code>value</code> to each element.
	 */
	public void
	scalarSub( float value )
	{
		data[0] -= value;
		data[1] -= value;
		data[2] -= value;
		data[3] -= value;
				
		data[4] -= value;
		data[5] -= value;
		data[6] -= value;
		data[7] -= value;
				
		data[8]  -= value;
		data[9]  -= value;
		data[10] -= value;
		data[11] -= value;
				
		data[12] -= value;
		data[13] -= value;
		data[14] -= value;
		data[15] -= value;
	}
	
	/**
	 * Multiply each element by <code>value</code>.
	 */
	public void
	scalarMult( float value )
	{
		data[0] *= value;
		data[1] *= value;
		data[2] *= value;
		data[3] *= value;
				
		data[4] *= value;
		data[5] *= value;
		data[6] *= value;
		data[7] *= value;
				
		data[8]  *= value;
		data[9]  *= value;
		data[10] *= value;
		data[11] *= value;
				
		data[12] *= value;
		data[13] *= value;
		data[14] *= value;
		data[15] *= value;
	}
	
	/**
	 * Transpose in place.
	 */
	public void
	transpose()
	{
		float t;
		
		for( int y = 1;  y < 4; ++y )
		{
			for( int x = 0; x < y; ++x )
			{
				t              = data[(x<<2)+y];
				data[(x<<2)+y] = data[(y<<2)+x];
				data[(y<<2)+x] = t;
			}
		}
	}

	/**
	 * Transpose only the upper-left 3x3 matrix. This allows to invert rotation
	 * matrices for example.
	 */
	public void
	transpose3x3()
	{
		float tmp;

		tmp     = data[1];
		data[1] = data[4];
		data[4] = tmp;

		tmp     = data[2];
		data[2] = data[8];
		data[8] = tmp;

		tmp     = data[6];
		data[6] = data[9];
		data[9] = tmp;
	}

// Commands -- Preset transformation matrices
	
	/**
	 * Change into a rotation matrix around the X axis about
	 * <code>angle</code>. <code>angle</code> is expressed in degees. The old
	 * matrix is erased, not post-multiplied.
	 */
	public void
	setXRotation( float angle )
	{
		if     ( angle < -360 ) angle = -360;
		else if( angle >  360 ) angle =  360;

		float sint = (float) Math.sin( ( Math.PI / 180.0 ) * angle );
		float cost = (float) Math.cos( ( Math.PI / 180.0 ) * angle );
	
		data[0] = 1;  data[4] = 0;     data[8]  = 0;      data[12] = 0;
		data[1] = 0;  data[5] = cost;  data[9]  = -sint;  data[13] = 0;
		data[2] = 0;  data[6] = sint;  data[10] =  cost;  data[14] = 0;
		data[3] = 0;  data[7] = 0;     data[11] = 0;      data[15] = 1;
	}
	
	/**
	 * Idem to {@link #setXRotation(float)} but arround the Y axis.
	 */
	public void
	setYRotation( float angle )
	{
		if     ( angle < -360 ) angle = -360;
		else if( angle >  360 ) angle =  360;
	
		float sint = (float) Math.sin( ( Math.PI / 180.0 ) * angle );
		float cost = (float) Math.cos( ( Math.PI / 180.0 ) * angle );
	
		data[0] = cost;   data[4] = 0;  data[8]  = sint;  data[12] = 0;
		data[1] = 0;      data[5] = 1;  data[9]  = 0;     data[13] = 0;
		data[2] = -sint;  data[6] = 0;  data[10] = cost;  data[14] = 0;
		data[3] = 0;      data[7] = 0;  data[11] = 0;     data[15] = 1;
	}
	
	/**
	 * Idem to {@link #setXRotation(float)} but arround the Z axis.
	 */
	public void
	setZRotation( float angle )
	{
		if     ( angle < -360 ) angle = -360;
		else if( angle >  360 ) angle =  360;
	
		float sint = (float) Math.sin( ( Math.PI / 180.0 ) * angle );
		float cost = (float) Math.cos( ( Math.PI / 180.0 ) * angle );
	
		data[0] = cost;  data[4] = -sint;  data[8]  = 0;  data[12] = 0;
		data[1] = sint;  data[5] =  cost;  data[9]  = 0;  data[13] = 0;
		data[2] = 0;     data[6] = 0;      data[10] = 1;  data[14] = 0;
		data[3] = 0;     data[7] = 0;      data[11] = 0;  data[15] = 1;
	}

	/**
	 * Fill only the translation part of this matrix with the vector <code>t</code>.
	 */
	public void
	setTranslation( Vector3 t )
	{
		data[12] = t.data[0];
		data[13] = t.data[1];
		data[14] = t.data[2];
	}

	/**
	 * Fill only the translation part of this matrix with the vector
	 * (<code>tx</code>,<code>ty</code>,<code>tz</code>).
	 */
	public void
	setTranslation( float tx, float ty, float tz )
	{
		data[12] = tx;
		data[13] = ty;
		data[14] = tz;
	}

	/**
	 * Fill only the upper left 3x3 matrix.
	 */
	public void
	setRotation(
		float r01, float r02, float r03,
		float r11, float r12, float r13,
		float r21, float r22, float r23 )
	{
		data[0] = r01;		data[4] = r02;		data[8]  = r03;
		data[1] = r11;		data[5] = r12;		data[9]  = r13;
		data[2] = r21;		data[6] = r22;		data[10] = r23;
	}

	/**
	 * Inverse each of the translation coefficients.
	 */
	public void
	inverseTranslation()
	{
		data[12] = -data[12];
		data[13] = -data[13];
		data[14] = -data[14];
	}

	/**
	 * Make this matrix a rotation matrix using the euler angles <code>r</code>.
	 * @see #setEulerRotation(float, float, float)
	 */
	public void
	setEulerRotation( Vector3 r )
	{
		setEulerRotation( r.data[0], r.data[1], r.data[2] );
	}

	/**
	 * Make this matrix a rotation matrix using the euler angles
	 * (<code>rx</code>,<code>ry</code>,<code>rz</code>). Euler angles define
	 * three rotation matrices around the X, Y and Z axis. The matrix are then
	 * applied following the order M=X.Y.Z (not using this technique though
	 * that would involve three full matrix multiplications).
	 * @see #setEulerRotation(Vector3)
	 */
	public void
	setEulerRotation( float rx, float ry, float rz )
	{
		// See the Matrix FAQ for an explanation of this.
		// http://skal.planet-d.net/demo/matrixfaq.htm (or type Matrix FAQ in
		// Google!).

		rx *= ( Math.PI / 180 );
		ry *= ( Math.PI / 180 );
		rz *= ( Math.PI / 180 );

		float A  = (float) Math.cos( rx );
		float B  = (float) Math.sin( rx );
		float C  = (float) Math.cos( ry );
		float D  = (float) Math.sin( ry );
		float E  = (float) Math.cos( rz );
		float F  = (float) Math.sin( rz );
		float AD =   A * D;
		float BD =   B * D;
		data[0]   =   C * E;
		data[4]   =  -C * F;
		data[8]   =   D;
		data[1]   =  BD * E + A * F;
		data[5]   = -BD * F + A * E;
		data[9]   =  -B * C;
		data[2]   = -AD * E + B * F;
		data[6]   =  AD * F + B * E;
		data[10]  =   A * C;
		data[3]   =  data[7] = data[11] = data[12] = data[13] = data[14] = 0;
		data[15]  =  1;
	}

// Miscellany

	@Override
	public String
	toString()
	{
		StringBuffer buf;

		buf = new StringBuffer( "Matrix4[" );
	
		for( int y = 0; y < 4; ++y )
		{
			//
			// One line of coefficients.
			//
			buf.append( '(' );
		
			for( int x = 0; x < 4; ++x )
			{
				buf.append( data[(x<<2)+y] );
				
				if( x != 3 )
				{
					buf.append( ' ' );
				}
			}

			buf.append( ')' );
		}

		buf.append( ']' );

		return buf.toString();
	}
}