/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.miv.graphstream.ui.layout.springbox;

import java.util.*;

/**
 * Part of the 2D or 3D space.
 *
 * <p>
 * When using the fast algorithm in the spring box, the space is cut in
 * several cells, each containing some of the nodes. This allows to improve
 * local search of neighbour nodes, and avoid the spring layout algorithm to
 * have O(n^2) complexity.
 * </p>
 *
 * @since 20050706
 */
class MapCell
	extends HashMap<String,SpringBox.Node>
{
// Attribute
	
	private static final long serialVersionUID = -7020595856454049108L;

	/**
	 * Cell average. This is the average position of all the nodes in the cell.
	 */
	public float x, y, z;

// Construction
	
	/**
	 * New empty cell.
	 */
	public MapCell()
	{
	}

// Command
	
	/**
	 * Reset the average node position to zero.
	 */
	public void reset()
	{
		x = y = z = 0;
	}

	/**
	 * Compute the average node position.
	 */
	public void average()
	{
		float n = size();

		if( n > 0 )
		{
			x /= n;
			y /= n;
			z /= n;
		}
	}
}