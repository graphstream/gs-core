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
 * 3D Bounds.
 *
 * @author Antoine Dutot
 * @since 20021215
 */
public class Bounds3
	implements java.io.Serializable
{
// Attributes

	private static final long serialVersionUID = 4922239937149305829L;

	/**
	 * First axis.
	 */
	public float width;

	/**
	 * Second axis.
	 */
	public float height;

	/**
	 * Third axis.
	 */
	public float depth;

// Attributes -- Shared

	/**
	 * Empty bounds.
	 */
	public static final Bounds3 NULL_BOUNDS = new Bounds3( 0, 0, 0 );

	/**
	 * Unit bounds.
	 */
	public static final Bounds3 UNIT_BOUNDS = new Bounds3();

// Constructors

	/**
	 * New "unit" bounds (1,1,1).
	 */
	public
	Bounds3()
	{
		width = height = depth = 1;
	}

	/**
	 * New bounds (width,height,depth).
	 */
	public
	Bounds3( float width, float height, float depth )
	{
		this.width  = width;
		this.height = height;
		this.depth  = depth;
	}

// Accessors

// Commands

	/**
	 * Make this a copy of other.
	 */
	public void
	copy( Bounds3 other )
	{
		width  = other.width;
		height = other.height;
		depth  = other.depth;
	}
	
	/**
	 * Change the box to (x1,y1,z1,x2,y2,z2).
	 */
	public void
	set( float width, float height, float depth )
	{
		this.width  = width;
		this.height = height;
		this.depth  = depth;
	}

// Commands -- misc.

	@Override
	public String toString()
	{
		StringBuffer buf;

		buf = new StringBuffer( "Bounds3[" );
		
		buf.append( width );
		buf.append( ':' );
		buf.append( height );
		buf.append( ':' );
		buf.append( depth );
		buf.append( "]" );

		return buf.toString();
	}
}