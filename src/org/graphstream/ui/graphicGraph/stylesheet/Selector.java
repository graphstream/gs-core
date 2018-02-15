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
 * @since 2009-02-19
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph.stylesheet;

/**
 * A selector is the part of a CSS rule that defines to which element a style
 * applies in the graph.
 * 
 * @author Antoine Dutot
 * @author Yoann Pign�
 */
public class Selector {
	/**
	 * Types of elements.
	 */
	public static enum Type {
		ANY, GRAPH, NODE, EDGE, SPRITE
	};

	/**
	 * The kind of element this matcher applies to.
	 */
	public Type type;

	/**
	 * If the selector specify an identifier.
	 */
	public String id;

	/**
	 * If the selector specify a class.
	 */
	public String clazz;

	/**
	 * If the selector also specify a pseudo class.
	 */
	public String pseudoClass;

	/**
	 * New selector for a given type of element.
	 * 
	 * @param type
	 *            The element type of this selector.
	 */
	public Selector(Type type) {
		this.type = type;
	}

	/**
	 * New selector for a given type of element. This constructor allows to specify
	 * either an identifier or a class to restrict this selector. If the identifier
	 * is given, the class will never be used (as identifiers are finer than
	 * classes). If the identifier is null the class will be used. The identifier
	 * allow to select only one element by its name. The class allows to select
	 * several elements.
	 * 
	 * @param type
	 *            The element type of this selector.
	 * @param identifier
	 *            The element name.
	 * @param clazz
	 *            The element class.
	 */
	public Selector(Type type, String identifier, String clazz) {
		this.type = type;
		setId(identifier);
		setClass(clazz);
	}

	/**
	 * Utility constructor that assign the correct type to the selector from a
	 * string. The type must be "node", "edge", "graph", or "sprite".
	 * 
	 * @param type
	 *            Either "node", "edge", "graph" or "sprite".
	 */
	public Selector(String type) {
		if (type.equals("node"))
			this.type = Type.NODE;
		else if (type.equals("edge"))
			this.type = Type.EDGE;
		else if (type.equals("graph"))
			this.type = Type.GRAPH;
		else if (type.equals("sprite"))
			this.type = Type.SPRITE;
		else
			throw new RuntimeException("invalid matcher type '" + type + "'");
	}

	/**
	 * New selector, copy of another.
	 * 
	 * @param other
	 *            The other selector.
	 */
	public Selector(Selector other) {
		this.type = other.type;
		setId(other.id);
		setClass(other.clazz);
	}

	/**
	 * Specify the identifier of the unique element this selector applies to.
	 * 
	 * @param id
	 *            A string that identifies an element of the graph.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Specify the class of the elements this selector applies to.
	 * 
	 * @param clazz
	 *            A string that matches all elements of a given class.
	 */
	public void setClass(String clazz) {
		this.clazz = clazz;
	}

	/**
	 * Specify the pseudo-class of the elements this selector applies to.
	 * 
	 * @param pseudoClass
	 *            A string that matches all elements of a given pseudo-class.
	 */
	public void setPseudoClass(String pseudoClass) {
		this.pseudoClass = pseudoClass;
	}

	/**
	 * The kind of elements this selector applies to.
	 * 
	 * @return An element type.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * The identifier of the element this selector uniquely applies to. This can be
	 * null if this selector is general.
	 * 
	 * @return The identifier or null if the selector is general.
	 */
	public String getId() {
		return id;
	}

	/**
	 * The class of elements this selector applies to. This can be null if this
	 * selector is general.
	 * 
	 * @return A class name or null if the selector is general.
	 */
	public String getClazz() {
		return clazz;
	}

	/**
	 * The pseudo-class of elements this selector applies to. This can be null.
	 * 
	 * @return A pseudo-class name or null.
	 */
	public String getPseudoClass() {
		return pseudoClass;
	}

	@Override
	public String toString() {
		return String.format("%s%s%s%s", type.toString(), id != null ? String.format("#%s", id) : "",
				clazz != null ? String.format(".%s", clazz) : "",
				pseudoClass != null ? String.format(":%s", pseudoClass) : "");
	}
}