/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.ui.graphicGraph.stylesheet;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Ordered set of colours.
 */
public class Colors extends ArrayList<Color> {
	/**
	 * New empty colour set.
	 */
	public Colors() {
	}

	/**
	 * New copy of the other colour set.
	 * 
	 * @param others
	 *            The other colour set to copy.
	 */
	public Colors(Colors others) {
		for (Color color : others)
			add(color);
	}
}
