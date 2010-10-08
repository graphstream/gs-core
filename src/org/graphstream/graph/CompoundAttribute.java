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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */
package org.graphstream.graph;

import java.util.HashMap;

/**
 * Definition of some compound value that can be used as attribute and can be
 * transformed to a hash map.
 * 
 * <p>
 * The purpose of this class is to allow to specify how some values are stored
 * in the graph and can be exported to files (or others) when the graph is
 * output. Most graph writers can only handle basic types for attributes (when
 * they are able to store attributes in files). This interface may allow to
 * store more complex attributes, made of several elements. The DGS writer is
 * able to understand these kinds of attributes and store them in files.
 * </p>
 * 
 * <p>
 * The compound attribute is made of fields. Each fields has a name and a value.
 * For these fields to be exported successfully, they must be transformable to a
 * hash map where each element is indexed by its name (a String).
 * </p>
 * 
 * <p>
 * For the values to be exported successfully, they must either be basic types,
 * or be themselves instances of CompountAttribute.
 * </p>
 */
public interface CompoundAttribute {
	/**
	 * Transforms this object to a hash map where each field is stored as a pair
	 * (key,value) where the key is the field name. As we cannot enforce the
	 * types of the key and value, the key are considered strings (or
	 * Object.toString()). The value is an arbitrary object.
	 * 
	 * @return The conversion of this attribute to a hash.
	 */
	HashMap<?, ?> toHashMap();

	/**
	 * The usual key used to store this attribute inside a graph element.
	 * 
	 * @return The attribute usual name.
	 */
	String getKey();
}