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
 */

package org.miv.graphstream.graph;

import java.util.HashMap;

/**
 * Definition of some class that can be used as attribute and can be transformed to a hash map.
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
 */
public interface Attribute
{
	/**
	 * Transforms this object to a hash map where each field is stored as a pair (key,value)
	 * where the key is the field name. As we cannot enforce the types of the key and value,
	 * the key are considered strings (or Object.toString()). The value is an arbitrary object.
	 * @return The conversion of this attribute to a hash.
	 */
	HashMap<?,?> toHashMap();
	
	/**
	 * The usual key used to store this attribute inside a graph element.
	 * @return The attribute usual name.
	 */
	String getKey();
}