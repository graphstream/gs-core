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

/**
 * 3D box.
 *
 * A Box3 is a rectangular parallelepiped characterized by two 3D points.
 *
 * @author Antoine Dutot
 * @since 19990829
 * @version 0.1
 */
public class Box3
	implements java.io.Serializable
{
// Attributes

	/**
	 * 
	 */
	private static final long serialVersionUID = 1319892728772327081L;

	/**
	 * Minimum point.
	 */
	public Point3 lo;
	
	/**
	 * Maximum point.
	 */
	public Point3 hi;

// Attributes -- Shared

	/**
	 * Box with two null points.
	 */
	public static final Box3 NULL_BOX = new Box3();

// Constructors

	/**
	 * New "null" box (both points are at the origin).
	 */
	public
	Box3()
	{
		lo = new Point3( 0, 0, 0 );
		hi = new Point3( 0, 0, 0 );
	}

	/**
	 * New box from point lo to point hi.
	 */
	public
	Box3( Point3 lo, Point3 hi )
	{
		this.lo = new Point3( lo );
		this.hi = new Point3( hi );
	}
	
	/**
	 * New box from point (x1,y1,z1) to point (x2,y2,x2).
	 */
	public
	Box3( float x1, float y1, float z1, float x2, float y2, float z2 )
	{
		lo = new Point3( x1, y1, z1 );
		hi = new Point3( x2, y2, z2 );
	}
	
	/**
	 * New copy of other.
	 */
	public
	Box3( Box3 other )
	{
		copy( other );
	}
	
	/**
	 * New box from point (x1,y1,z1) to point (x2,y2,x2).
	 */
	public void
	make( float x1, float y1, float z1, float x2, float y2, float z2 )
	{
		set( x1, y1, z1, x2, y2, z2 );
	}
	
	/**
	 * New copy of other.
	 */
	public void
	makeCopy( Box3 other )
	{
		copy( other );
	}

// Predicates

//	/**
//	 * Is other equal to this ?
//	 */
//	public boolean
//	equals( const Box3 < float > other )
//	{
//		return ( lo.equals ( other.lo ) and hi.equals ( other.hi ) );
//	}

// Accessors

	/**
	 * Left-Bottom-Front point.
	 */
	public Point3
	getLowPoint()
	{
		return lo;
	}

	/**
	 * Right-Top-Back point.
	 */
	public Point3
	getHiPoint()
	{
		return hi;
	}

	/**
	 * Absolute width.
	 */
	public float
	getWidth()
	{
		return hi.x - lo.x;
	}

	/**
	 * Absolute height.
	 */
	public float
	getHeight()
	{
		return hi.y - lo.y;
	}

	/**
	 * Absolute depth.
	 */
	public float
	getDepth()
	{
		return hi.z - lo.z;
	}

// Commands

	/**
	 * Makt this a copy of other.
	 */
	public void
	copy( Box3 other )
	{
		lo.copy( other.lo );
		hi.copy( other.hi );
	}
	
	/**
	 * Change the box to (x1,y1,z1,x2,y2,z2).
	 */
	public void
	set( float x1, float y1, float z1, float x2, float y2, float z2 )
	{
		lo.moveTo( x1, y1, z1 );
		hi.moveTo( x2, y2, z2 );
	}

	/**
	 * Set geometry as the union of a and b. This always works, even if
	 * a and b have no common part. Neither a nor b are modified.
	 */
	public void
	unionOf( Box3 a, Box3 b ) throws InvalidArgumentException
	{
		if( a == this || b == this )
		{
			throw new InvalidArgumentException( "parameters cannot be this" );
		}
	
		if( a.lo.x < b.lo.x )
		{
			lo.x = a.lo.x;
		}
		else
		{	
			lo.x = b.lo.x;
		}

		if( a.lo.y < b.lo.y )
		{
			lo.y = b.lo.y;
		}
		else
		{
			lo.y = b.lo.y;
		}

		if( a.lo.z < b.lo.z )
		{
			lo.z = a.lo.z;
		}
		else
		{
			lo.z = b.lo.z;
		}

		if( a.hi.x > b.hi.x )
		{
			hi.x = a.hi.x;
		}
		else
		{
			hi.x = b.hi.x;
		}

		if( a.hi.y > b.hi.y )
		{
			hi.y = a.hi.y;
		}
		else
		{
			hi.y = b.hi.y;
		}

		if( a.hi.z > b.hi.z )
		{
			hi.z = a.hi.z;
		}
		else
		{
			hi.z = b.hi.z;
		}
	}
	
	/**
	 * Set geometry as the union of this and other. other is not modified.
	 */
	public void
	unionWith( Box3 other ) throws InvalidArgumentException
	{
		if( other == this )
		{
			throw new InvalidArgumentException( "parameter cannot be this" );
		}
		
		if( other.lo.x < lo.x )
		{
			lo.x = other.lo.x;
		}
		
		if( other.lo.y < lo.y )
		{
			lo.y = other.lo.y;
		}
		
		if( other.lo.z < lo.z )
		{
			lo.z = other.lo.z;
		}
		
		if( other.hi.x > hi.x )
		{
			hi.x = other.hi.x;
		}
		
		if( other.hi.y > hi.y )
		{
			hi.y = other.hi.y;
		}

		if( other.hi.z > hi.z )
		{
			hi.z = other.hi.z;
		}
	}
	
	/**
	 * Set geometry as the intersection of a and b. If this intersection
	 * is empty, one or all components of lo will be greater than the
	 * corresponding components of hi. Neither a nor b are modified.
	 */
	public void
	intersectionOf( Box3 a, Box3 b ) throws InvalidArgumentException
	{
		if( a == this || b == this )
		{
			throw new InvalidArgumentException( "parameter cannot be this" );
		}
	
		// We consider first the width (x part) There is 6 cases (each char
		// is a pixel, even `|'):
		//            :                      :
		//         a.lo.x                 a.hi.x
		//            |----------a-----------|
		// |--b1--|   :                      :
		//         |--b2--|                  :   |--b6--|
		//            :      |--b3--|        :
		//            :                   |--b4--|
		//            :                      :
		//         |------------b5-------------|
		//            :                      :
		
		if( a.lo.x >= b.lo.x )
		{
			lo.x = a.lo.x;
		}
		else
		{
			lo.x = b.lo.x;
		}

		if( a.hi.x <= b.hi.x )
		{
			hi.x = a.hi.x;
		}
		else
		{
			hi.x = b.hi.x;
		}
		
		if( a.lo.y >= b.lo.y )
		{
			lo.y = a.lo.y;
		}
		else
		{
			lo.y = b.lo.y;
		}

		if( a.hi.y <= b.hi.y )
		{
			hi.y = a.hi.y;
		}
		else
		{
			hi.y = b.hi.y;
		}
		
		if( a.lo.z >= b.lo.z )
		{
			lo.z = a.lo.z;
		}
		else
		{
			lo.z = b.lo.z;
		}

		if( a.hi.z <= b.hi.z )
		{
			hi.z = a.hi.z;
		}
		else
		{
			hi.z = b.hi.z;
		}
	}
	
	/**
	 * Set geometry as the intersection of this and other. If this
	 * intersection is empty, one or all components of lo will be greater
	 * than the corresponding components of hi. other is not modified.
	 */
	public void
	intersectionWith( Box3 other ) throws InvalidArgumentException
	{
		if( other == this )
		{
			throw new InvalidArgumentException();
		}
		
		if( other.lo.x >= lo.x )
		{
			lo.x = other.lo.x;
		}
		
		if( other.lo.y >= lo.y )
		{
			lo.y = other.lo.y;
		}
		
		if( other.lo.z >= lo.z )
		{
			lo.z = other.lo.z;
		}
		
		if( other.hi.x <= hi.x )
		{
			hi.x = other.hi.x;
		}
		
		if( other.hi.y <= hi.y )
		{
			hi.y = other.hi.y;
		}

		if( other.hi.z <= hi.z )
		{
			hi.z = other.hi.z;
		}
	}

// Commands -- moving
	
	/**
	 * Move lo to the absolute location (x,y,z). The point hi is
	 * moved from the same amount than lo.
	 */
	public void
	moveTo( float x, float y, float z )
	{
		hi.moveTo(
			hi.x + ( x - lo.x ),
			hi.y + ( y - lo.y ),
			hi.z + ( z - lo.z ) );
		lo.moveTo( x, y, z );
	}
	
	/**
	 * Move the two points along the (dx,dy,dz) vector.
	 */
	public void
	move( float dx, float dy, float dz )
	{
		lo.move( dx, dy, dz );
		hi.move( dx, dy, dz );
	}
	
// Misc.

	@Override
	public String
	toString()
	{
		StringBuffer buf;

		buf = new StringBuffer( "Box3[(" );
		
		buf.append( lo.x );
		buf.append( ':' );
		buf.append( lo.y );
		buf.append( ':' );
		buf.append( lo.z );
		buf.append( ")(" );
		
		buf.append( hi.x );
		buf.append( ':' );
		buf.append( hi.y );
		buf.append( ':' );
		buf.append( hi.z );
		buf.append( ")]" );

		return buf.toString();
	}
}