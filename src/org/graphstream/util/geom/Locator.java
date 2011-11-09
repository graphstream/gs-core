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
 * Pivot and bounding box.
 *
 * <p>A locator define the size (as a bounding box) and position (as a pivot
 * point) of a sprite inside the projection space of its parent sprite. The
 * values are always expressed in the units of the parent. The bounding box is
 * expressed with respect to the pivot, whereas the pivot is expressed with
 * respect to the parent's pivot.</p>
 *
 * @author Antoine Dutot
 * @since 20040110
 */
public class Locator
	extends Box3
{
// Attributes

	/**
	 * 
	 */
	private static final long serialVersionUID = 5629752956109593217L;

	/**
	 * Pivot point.
	 */
	protected Point3 pivot = new Point3();

// Constructors

	/**
	 * New locator with its pivot at (0,0,0), and size (1,1,1) around the
	 * pivot.
	 */
	public
	Locator()
	{
		this( 0, 0, 0, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f );
	}

	/**
	 * New locator with its pivot at <code>(x,y,z)</code>, and size (1,1,1)
	 * around the pivot.
	 */
	public
	Locator( float x, float y, float z )
	{
		this( x, y, z, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f );
	}

	/**
	 * New locator with its pivot at <code>(x,y,z)</code>, and size
	 * <code>(width,height,depht)</code> around the pivot.
	 */
	public
	Locator( float x, float y, float z, float width, float height, float depth )
	{
		this( x, y, z, -width/2, -height/2, -depth/2, width/2, height/2, depth/2 );
	}

	/**
	 * New locator with its pivot at <code>(x,y,z)</code> and lowest point at
	 * <code>(low_x,low_y,low_z)</code> and highest point at
	 * <code>(hi_x,hi_y,hi_z)</code>.
	 */
	public
	Locator( float x, float y, float z, float low_x, float low_y, float low_z,
		float hi_x, float hi_y, float hi_z )
	{
		super( low_x, low_y, low_z, hi_x, hi_y, hi_z );
		pivot.set( x, y, z );
	}

// Accessors

	/**
	 * Pivot point.
	 */
	public Point3
	getPivot()
	{
		return pivot;
	}

	/**
	 * Abscissa of the pivot.
	 */
	public float
	getX()
	{
		return pivot.x;
	}

	/**
	 * Ordinate of the pivot.
	 */
	public float
	getY()
	{
		return pivot.y;
	}

	/**
	 * Depth of the pivot.
	 */
	public float
	getZ()
	{
		return pivot.z;
	}

	@Override
	public String
	toString()
	{
		return "[pivot("+pivot.x+","+pivot.y+","+pivot.z+") lo("+lo.x+","+lo.y+","+lo.z+") hi("+hi.x+","+hi.y+","+hi.z+")]";
	}
}