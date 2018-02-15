/*
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

/**
 * @since 2009-12-22
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
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
	 * (key,value) where the key is the field name. As we cannot enforce the types
	 * of the key and value, the key are considered strings (or Object.toString()).
	 * The value is an arbitrary object.
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