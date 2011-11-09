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
 * 2D rectangle.
 *
 * @author Antoine Dutot
 * @since 20001121
 * @version 0.1
 */
public class Rectangle
	implements java.io.Serializable
{
// Attributes

	private static final long serialVersionUID = -7996459628848481249L;

	public float x;

	public float y;

	public float w;

	public float h;

// Constructors

	public
	Rectangle()
	{
	}

	public
	Rectangle( float x, float y, float w, float h )
	{
		set( x, y, w, h );
	}
	
	public
	Rectangle( Rectangle other )
	{
		set( other.x, other.y, other.w, other.h );
	}

// Commands

	public void
	copy( Rectangle other )
	{
		x = other.x;
		y = other.y;
		w = other.w;
		h = other.h;
	}

	/**
	 * Set all the rectangle coordinates.
	 */
	public void
	set( float x, float y, float w, float h )
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	/**
	 * Move the rectangle at absolute position (x,y).
	 */
	public void
	move_to( float x, float y )
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Move the rectangle of vector (dx,dy).
	 */
	public void
	move( float dx, float dy )
	{
		x += dx;
		y += dy;
	}

	/**
	 * Set the size of the rectangle to (w,h).
	 */
	public void
	size( float w, float h )
	{
		this.w = w;
		this.h = h;
	}
}