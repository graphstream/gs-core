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


/**
 * 4D point.
 *
 * This adds a W coordinate to the Point3 class. This coordinate is used as a
 * weight. In all other ways, it is identical to Point3 (which is also a
 * Point2, thus a Point4 is also a Point2).
 *
 * @author Antoine Dutot
 * @since 20010512
 * @version 0.1
 */
public class Point4
	extends Point3
	implements java.io.Serializable
{
// Attributes

	private static final long serialVersionUID = -8880686984758413395L;

	/**
	 * W "weight" value.
	 */
	public float w;

// Attributes -- Shared

	/**
	 * Specific point at (0,0,0,1) (with a weight of 1).
	 */
	public static final Point4 NULL_POINT4 = new Point4( 0, 0, 0, 1 );

// Constructors

	/**
	 * New 4D Points at (0,0,0,1).
	 */
	public
	Point4()
	{
		w = 1;
	}

	/**
	 * New 4D Point at (x,y,0,1).
	 */
	public
	Point4( float x, float y )
	{
		set( x, y, 0, 1 );
	}

	/**
	 * New 4D Point at (x,y,z,1).
	 */
	public
	Point4( float x, float y, float z )
	{
		set( x, y, z, 1 );
	}

	/**
	 * New 4D Point at (x,y,z,w).
	 */
	public
	Point4( float x, float y, float z, float w )
	{
		set( x, y, z, w );
	}
	
	/**
	 * New copy of other.
	 */
	public
	Point4( Point4 other )
	{
		copy( other );
	}

// Accessors

	/**
	 * Create a new point, linear interpolation of this and <code>other</code>.
	 * The new point is located between this and <code>other</code> if
	 * <code>factor</code> is between 0 and 1 (0 yields this point, 1 yields
	 * the <code>other</code> point). 
	 */
	public Point4
	interpolate( Point4 other, float factor )
	{
		Point4 p = new Point4(
			x + ( ( other.x - x ) * factor ),
			y + ( ( other.y - y ) * factor ),
			z + ( ( other.z - z ) * factor ),
			w + ( ( other.w - w ) * factor ) );

		return p;
	}

	/**
	 * Distance between this and <code>other</code>.
	 */
	public float
	distance( Point4 other )
	{
		float xx = other.x - x;
		float yy = other.y - y;
		float zz = other.z - z;
// Do not consider W...
		return (float) Math.sqrt( ( xx * xx ) + ( yy * yy ) + ( zz *zz ) );
	}

// Commands

	/**
	 * Are all components to zero?.
	 */
	@Override
	public boolean
	isZero()
	{
		return( x == 0 && y == 0 && z == 0 && w == 0 );
	}

	/**
	 * Make this a copy of other.
	 */
	public void
	copy( Point4 other )
	{
		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
	}

	/**
	 * Like #moveTo().
	 */
	public void
	set( float x, float y, float z, float w )
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

// Commands -- moving
	
	/**
	 * Move to absolute position (x,y,z,w).
	 */
	public void
	moveTo( float x, float y, float z, float w )
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	/**
	 * Move of given vector (dx,dy,dz,dw).
	 */
	public void
	move( float dx, float dy, float dz, float dw )
	{
		this.x += dx;
		this.y += dy;
		this.z += dz;
		this.w += dw;
	}

	/**
	 * Move of given point <code>p</code>.
	 */
	public void
	move( Point4 p )
	{
		this.x += p.x;
		this.y += p.y;
		this.z += p.z;
		this.w += p.w;
	}
	
	/**
	 * Move of given vector <code>d</code>.
	 */
	public void
	move( Vector4 d )
	{
		this.x += d.data[0];
		this.y += d.data[1];
		this.z += d.data[2];
		this.w += d.data[3];
	}
	
	/**
	 * Move weight of <code>dw</code>.
	 */
	public void
	moveW( float dw )
	{
		w += dw;
	}

	/**
	 * Scale of factor (sx,sy,sz,sw).
	 */
	public void
	scale( float sx, float sy, float sz, float sw )
	{
		x *= sx;
		y *= sy;
		z *= sz;
		x *= sw;
	}

	/**
	 * Scale by factor s.
	 */
	public void
	scale( Point4 s )
	{
		x *= s.x;
		y *= s.y;
		z *= s.z;
		w *= s.w;
	}

	/**
	 * Scale by factor s.
	 */
	public void
	scale( Vector4 s )
	{
		x *= s.data[0];
		y *= s.data[1];
		z *= s.data[2];
		w *= s.data[3];
	}
	
	/**
	 * Change only weight at absolute coordinate w.
	 */
	public void
	setW( float w )
	{
		this.w = w;
	}

	/**
	 * Exchange the values of this and other.
	 */
	public void
	swap( Point4 other )
	{
		float t;

		if( other != this )
		{
			t       = this.x;
			this. x = other.x;
			other.x = t;
			
			t       = this.y;
			this. y = other.y;
			other.y = t;
			
			t       = this.z;
			this. z = other.z;
			other.z = t;
			
			t       = this.w;
			this. w = other.w;
			other.w = t;
		}
	}

// Commands -- misc.

	@Override
	public String
	toString()
	{
		StringBuffer buf;

		buf = new StringBuffer( "Point4[" );
		
		buf.append( x );
		buf.append( '|' );
		buf.append( y );
		buf.append( '|' );
		buf.append( z );
		buf.append( '|' );
		buf.append( w );
		buf.append( "]" );

		return buf.toString();
	}
}