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
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * An element is a part of a graph (node, edge, the graph itself).
 * <p>
 * <p>
 * An interface that defines common method to manipulate identifiers, attributes
 * and indices of the elements (graph, nodes and edges) of a graph.
 * </p>
 * *
 * <p>
 * Attributes can be any object and are identified by arbitrary strings. Some
 * attributes are stored as numbers or strings and are in this case named
 * number, label or vector. There are utility methods to handle these attributes
 * ({@link #getNumber(String)}, {@link #getLabel(String)}) or
 * {@link #getVector(String)}, however they are also accessible through the more
 * general method {@link #getAttribute(String)}.
 * </p>
 * <p>
 * <h3>Important</h3>
 * <p>
 * Implementing classes should indicate the complexity of their implementation
 * for each method.
 * </p>
 *
 * @since July 12 2007
 */
public interface Element {
	/**
	 * Unique identifier of this element.
	 *
	 * @return The identifier value.
	 */
	String getId();

	/**
	 * The current index of this element
	 *
	 * @return The index value
	 */
	int getIndex();

	/**
	 * Get the attribute object bound to the given key. The returned value may be
	 * null to indicate the attribute does not exists or is not supported.
	 *
	 * @param key
	 *            Name of the attribute to search.
	 * @return The object bound to the given key or null if no object match this
	 *         attribute name.
	 */
	Object getAttribute(String key);

	/**
	 * Like {@link #getAttribute(String)}, but returns the first existing attribute
	 * in a list of keys, instead of only one key. The key list order matters.
	 *
	 * @param keys
	 *            Several strings naming attributes.
	 * @return The first attribute that exists.
	 */
	Object getFirstAttributeOf(String... keys);

	/**
	 * Get the attribute object bound to the given key if it is an instance of the
	 * given class. Some The returned value maybe null to indicate the attribute
	 * does not exists or is not an instance of the given class.
	 *
	 * @param key
	 *            The attribute name to search.
	 * @param clazz
	 *            The expected attribute class.
	 * @return The object bound to the given key or null if no object match this
	 *         attribute.
	 */
	// Object getAttribute( String key, Class<?> clazz );
	<T> T getAttribute(String key, Class<T> clazz);

	/**
	 * Like {@link #getAttribute(String, Class)}, but returns the first existing
	 * attribute in a list of keys, instead of only one key. The key list order
	 * matters.
	 *
	 * @param clazz
	 *            The class the attribute must be instance of.
	 * @param keys
	 *            Several string naming attributes.
	 * @return The first attribute that exists.
	 */
	// Object getFirstAttributeOf( Class<?> clazz, String... keys );
	<T> T getFirstAttributeOf(Class<T> clazz, String... keys);

	/**
	 * Get the label string bound to the given key key. Labels are special
	 * attributes whose value is a character sequence. If an attribute with the same
	 * name exists but is not a character sequence, null is returned.
	 *
	 * @param key
	 *            The label to search.
	 * @return The label string value or null if not found.
	 */
	default CharSequence getLabel(String key) {
		return getAttribute(key, CharSequence.class);
	}

	/**
	 * Get the number bound to key. Numbers are special attributes whose value is an
	 * instance of Number. If an attribute with the same name exists but is not a
	 * Number, NaN is returned.
	 *
	 * @param key
	 *            The name of the number to search.
	 * @return The number value or NaN if not found.
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	default double getNumber(String key) {
		Object o = getAttribute(key);

		if (o != null) {
			if (o instanceof Number)
				return ((Number) o).doubleValue();

			if (o instanceof CharSequence) {
				try {
					return Double.parseDouble(o.toString());
				} catch (NumberFormatException ignored) {
				}
			}
		}

		return Double.NaN;
	}

	/**
	 * Get the vector of number bound to key. Vectors of numbers are special
	 * attributes whose value is a sequence of numbers. If an attribute with the
	 * same name exists but is not a vector of number, null is returned. A vector of
	 * number is a non-empty {@link java.util.List} of {@link java.lang.Number}
	 * objects.
	 *
	 * @param key
	 *            The name of the number to search.
	 * @return The vector of numbers or null if not found.
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	@SuppressWarnings("unchecked")
	default List<? extends Number> getVector(String key) {
		Object o = getAttribute(key);

		if (o != null && o instanceof List) {
			List<?> l = (List<?>) o;

			if (l.size() > 0 && l.get(0) instanceof Number)
				return (List<? extends Number>) l;
		}

		return null;
	}

	/**
	 * Get the array of objects bound to key. Arrays of objects are special
	 * attributes whose value is a sequence of objects. If an attribute with the
	 * same name exists but is not an array, null is returned.
	 *
	 * @param key
	 *            The name of the array to search.
	 * @return The array of objects or null if not found.
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	default Object[] getArray(String key) {
		return getAttribute(key, Object[].class);
	}

	/**
	 * Get the map bound to key. Maps are special attributes whose value is a set of
	 * pairs (name,object). Instances of object implementing the
	 * {@link CompoundAttribute} interface are considered like maps since they can
	 * be transformed to a map. If an attribute with the same name exists but is not
	 * a map, null is returned. We cannot enforce the type of the key. It is
	 * considered a string and you should use "Object.toString()" to get it.
	 *
	 * @param key
	 *            The name of the map to search.
	 * @return The map or null if not found.
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	default Map<?, ?> getMap(String key) {
		Object o = getAttribute(key);

		if (o != null) {
			if (o instanceof Map<?, ?>)
				return ((Map<?, ?>) o);
		}

		return null;
	}

	/**
	 * Does this element store a value for the given attribute key? Note that
	 * returning true here does not mean that calling getAttribute with the same key
	 * will not return null since attribute values can be null. This method just
	 * checks if the key is present, with no test on the value.
	 *
	 * @param key
	 *            The name of the attribute to search.
	 * @return True if a value is present for this attribute.
	 */
	boolean hasAttribute(String key);

	/**
	 * Does this element store a value for the given attribute key and this value is
	 * an instance of the given class?
	 *
	 * @param key
	 *            The name of the attribute to search.
	 * @param clazz
	 *            The expected class of the attribute value.
	 * @return True if a value is present for this attribute.
	 */
	boolean hasAttribute(String key, Class<?> clazz);

	/**
	 * Does this element store a label value for the given key? A label is an
	 * attribute whose value is a char sequence.
	 *
	 * @param key
	 *            The name of the label.
	 * @return True if a value is present for this attribute and implements
	 *         CharSequence.
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	default boolean hasLabel(String key) {
		return getAttribute(key, CharSequence.class) != null;
	}

	/**
	 * Does this element store a number for the given key? A number is an attribute
	 * whose value is an instance of Number.
	 *
	 * @param key
	 *            The name of the number.
	 * @return True if a value is present for this attribute and can contain a
	 *         double (inherits from Number).
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	default boolean hasNumber(String key) {
		if (getAttribute(key, Number.class) != null)
			return true;

		CharSequence o = getAttribute(key, CharSequence.class);

		if (o != null) {
			try {
				Double.parseDouble(o.toString());
				return true;
			} catch (NumberFormatException ignored) {
			}
		}

		return false;
	}

	/**
	 * Does this element store a vector value for the given key? A vector is an
	 * attribute whose value is a sequence of numbers.
	 *
	 * @param key
	 *            The name of the vector.
	 * @return True if a value is present for this attribute and can contain a
	 *         sequence of numbers.
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	default boolean hasVector(String key) {
		List<?> o = getAttribute(key, List.class);

		if (o != null && o.size() > 0) {
			return o.get(0) instanceof Number;
		}

		return false;
	}

	/**
	 * Does this element store an array value for the given key? Only object arrays
	 * (instance of Object[]) are considered as array here.
	 *
	 * @param key
	 *            The name of the array.
	 * @return True if a value is present for this attribute and can contain an
	 *         array object.
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	default boolean hasArray(String key) {
		return getAttribute(key, Object[].class) != null;
	}

	/**
	 * Does this element store a map value for the given key? A map is a set of
	 * pairs (key,value) ({@link java.util.Map}) or objects that implement the
	 * {@link org.graphstream.graph.CompoundAttribute} class.
	 *
	 * @param key
	 *            The name of the hash.
	 * @return True if a value is present for this attribute and can contain a hash.
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	default boolean hasMap(String key) {
		Object o = getAttribute(key);
		return o != null && (o instanceof Map<?, ?>);
	}

	/**
	 * Stream over the attribute keys of the element. If no attribute exist, method
	 * will return empty stream.
	 *
	 * @return a String stream corresponding to the keys of the attributes.
	 */
	Stream<String> attributeKeys();

	/**
	 * Remove all registered attributes. This includes numbers, labels and vectors.
	 */
	void clearAttributes();

	/**
	 * Add or replace the value of an attribute. Existing attributes are overwritten
	 * silently. All classes inheriting from Number can be considered as numbers.
	 * All classes inheriting from CharSequence can be considered as labels. You can
	 * pass zero, one or more arguments for the attribute values. If no value is
	 * given, a boolean with value "true" is added. If there is more than one value,
	 * an array is stored. If there is only one value, the value is stored (but not
	 * in an array).
	 *
	 * @param attribute
	 *            The attribute name.
	 * @param values
	 *            The attribute value or set of values.
	 */
	void setAttribute(String attribute, Object... values);

	/**
	 * Add or replace each attribute found in attributes. Existing attributes are
	 * overwritten silently. All classes inheriting from Number can be considered as
	 * numbers. All classes inheriting from CharSequence can be considered as
	 * labels.
	 *
	 * @param attributes
	 *            A set of (key,value) pairs.
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	default void setAttributes(Map<String, Object> attributes) {
		attributes.forEach(this::setAttribute);
	}

	/**
	 * Remove an attribute. Non-existent attributes errors are ignored silently.
	 *
	 * @param attribute
	 *            Name of the attribute to remove.
	 */
	void removeAttribute(String attribute);

	/**
	 * Number of attributes stored in this element.
	 *
	 * @return the number of attributes.
	 */
	int getAttributeCount();
}