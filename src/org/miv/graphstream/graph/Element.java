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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An element is a part of a graph (node, edge, the graph itself).
 * 
 * <p>
 * An interface that defines common method to manipulate identifiers and
 * attributes of the elements (graph, nodes and edges) of a graph.
 * </p>
 *  * 
 * <p>
 * Attributes can be any object and are identified by arbitrary strings. Some
 * attributes are stored as numbers or strings and are in this case named
 * number, label or vector. There are utility methods to handle these attributes
 * ({@link #getNumber(String)}, {@link #getLabel(String)}) or
 * {@link #getVector(String)}, however they are  also accessible through the
 * more general method {@link #getAttribute(String)}.
 * </p>
 * 
 * <h3>Important</h3>
 * <p>
 * Implementing classes should indicate the complexity of their implementation for each method.
 * </p>
 * 
 * @author Antoine Dutot
 * @author Yoann Pign�
 * 
 * @since July 12 2007
  *
 */
public interface Element
{
	/**
	 * Unique identifier of this element.
	 * @return The identifier value. 
	 */
	public String getId();

	/**
	 * Set the unique identifier of this element. This can be done only once.
	 * @param id The identifier value. 
	 */
	//public void setId(String id);
	
	/**
	 * Get the attribute object bound to the given key. The returned value maybe
	 * null to indicate the attribute does not exists or is not supported.
	 * @param key Name of the attribute to search.
	 * @return The object bound to the given key or null if no object match this
	 *         attribute name.
	 */
	public Object getAttribute( String key );

	/**
	 * Like {@link #getAttribute(String)}, but returns the first existing
	 * attribute in a list of keys, instead of only one key. The key list order
	 * matters.
	 * @param keys Several strings naming attributes.
	 * @return The first attribute that exists.
	 */
	public Object getFirstAttributeOf( String... keys );

	/**
	 * Get the attribute object bound to the given key if it is an instance of
	 * the given class. Some The returned value maybe null to indicate the
	 * attribute does not exists or is not an instance of the given class.
	 * @param key The attribute name to search.
	 * @param clazz The expected attribute class.
	 * @return The object bound to the given key or null if no object match this
	 *         attribute.
	 */
	public Object getAttribute( String key, Class<?> clazz );

	/**
	 * Like {@link #getAttribute(String, Class)}, but returns the first existing
	 * attribute in a list of keys, instead of only one key. The key list order
	 * matters.
	 * @param clazz The class the attribute must be instance of.
	 * @param keys Several string naming attributes.
	 * @return The first attribute that exists.
	 */
	public Object getFirstAttributeOf( Class<?> clazz, String... keys );

	/**
	 * Get the label string bound to the given key key. Labels are special
	 * attributes whose value is a character sequence. If an attribute with the
	 * same name exists but is not a character sequence, null is returned.
	 * @param key The label to search.
	 * @return The label string value or null if not found.
	 */
	public CharSequence getLabel( String key );

	/**
	 * Get the number bound to key. Numbers are special attributes whose value
	 * is an instance of Number. If an attribute with the same name exists but is not a
	 * Number, NaN is returned.
	 * @param key The name of the number to search.
	 * @return The number value or NaN if not found.
	 */
	public double getNumber( String key );

	/**
	 * Get the vector of number bound to key. Vectors of numbers are special
	 * attributes whose value is a sequence of numbers. If an attribute with the
	 * same name exists but is not a vector of number, null is returned.
	 * @param key The name of the number to search.
	 * @return The vector of numbers or null if not found.
	 * @complexity O(1)
	 */
	public ArrayList<? extends Number> getVector( String key );
	
	/**
	 * Get the array of objects bound to key. Arrays of objects are special attributes whose value
	 * is a sequence of objects. If an attribute with the same name exists but is not an array, null
	 * is returned.
	 * @param key The name of the array to search.
	 * @return The array of objects or null if not found.
	 * @complexity O(1)
	 */
	public Object[] getArray( String key );
	
	/**
	 * Get the hash bound to key. Hashes are special attributes whose value is a set of pairs
	 * (name,object). Instances of object implementing the {@link Attribute} interface are
	 * considered like hashes since they can be transformed to a hash. If an attribute with the
	 * same name exists but is not a hash, null is returned. We cannot enforce the type of
	 * the key. It is considered a string and you should use "Object.toString()" to get it.
	 * @param key The name of the hash to search.
	 * @return The hash or null if not found.
	 * @complexity O(1)
	 */
	public HashMap<?,?> getHash( String key );

	/**
	 * Does this element stores a value for the given attribute key.
	 * @param key The name of the attribute to search.
	 * @return True if a value is present for this attribute.
	 */
	public boolean hasAttribute( String key );

	/**
	 * Does this element stores a value for the given attribute key and this value is an
	 * instance of the given clazz.
	 * @param key The name of the attribute to search.
	 * @param clazz The expected class of the attribute value.
	 * @return True if a value is present for this attribute.
	 */
	public boolean hasAttribute( String key, Class<?> clazz );

	/**
	 * Does this element stores a label value for the given key. A label is an attribute
	 * whose value is a string.
	 * @param key The name of the label.
	 * @return True if a value is present for this attribute and implements
	 *         CharSequence.
	 */
	public boolean hasLabel( String key );

	/**
	 * Does this element stores a number for the given key. A number is an attribute
	 * whose value is an instance of Number.
	 * @param key The name of the number.
	 * @return True if a value is present for this attribute and can contain a
	 *         double (inherits from Number).
	 */
	public boolean hasNumber( String key );

	/**
	 * Does this element stores a vector value for the given key. A vector is an attribute
	 * whose value is a sequence of numbers.
	 * @param key The name of the vector.
	 * @return True if a value is present for this attribute and can contain a
	 *         sequence of numbers.
	 */
	public boolean hasVector( String key );

	/**
	 * Does this element stores a array value for the given key. A vector is an attribute
	 * whose value is an array of objects.
	 * @param key The name of the array.
	 * @return True if a value is present for this attribute and can contain an array object.
	 */
	public boolean hasArray( String key );

	/**
	 * Does this element stores a hash value for the given key. A hash is a set of pairs
	 * (key,value) or objects that implement the {@link Attribute} class.
	 * @param key The name of the hash.
	 * @return True if a value is present for this attribute and can contain a hash.
	 */
	public boolean hasHash( String key );
	
	/**
	 * Iterator on all attributes keys.
	 * @return An iterator on the key set of attributes.
	 */
	public Iterator<String> getAttributeKeyIterator();

	/**
	 * Return a constant view of the attribute set under the form of a map.
	 * The map can be iterated on, browsed, but not modified.
	 * @return The set of attributes.
	 */
	public Map<String, Object> getAttributeMap();

	/**
	 * Remove all registered attributes. This includes numbers, labels and
	 * vectors.
	 */
	public void clearAttributes();

	/**
	 * Add or replace the value of an attribute.
	 * Existing attributes are overwritten silently. All classes inheriting from
	 * Number can be considered as numbers. All classes inheriting from
	 * CharSequence can be considered as labels. You can pass zero, one or more
	 * arguments for the attribute values. If no value is given, a boolean with
	 * value "true" is added. If there is more than one value, an array is
	 * stored. If there is only one value, the value is stored (but not in an
	 * array).
	 * @param attribute The attribute name.
	 * @param values The attribute value or set of values.
	 */
	public void addAttribute( String attribute, Object ... values );
	
	/**
	 * Like {@link #addAttribute(String, Object...)} but for consistency.
	 * @param attribute The attribute name.
	 * @param values The attribute value or array of values.
	 * @see #addAttribute(String, Object...)
	 */
	public void changeAttribute( String attribute, Object ... values );
	
	/**
	 * Like {@link #addAttribute(String, Object...)} but for consistency.
	 * @param attribute The attribute name.
	 * @param values The attribute value or array of values.
	 * @see #addAttribute(String, Object...)
	 */
	public void setAttribute( String attribute, Object ... values );

	/**
	 * Add or replace each attribute found in attributes. Existing attributes
	 * are overwritten silently. All classes inheriting from Number can be
	 * considered as numbers. All classes inheriting from CharSequence can be
	 * considered as labels.
	 * @param attributes A set of (key,value) pairs.
	 */
	public void addAttributes( Map<String, Object> attributes );

	/**
	 * Remove an attribute. Non-existent attributes errors are ignored silently.
	 * @param attribute Name of the attribute to remove.
	 */
	public void removeAttribute( String attribute );

	/**
	 * Number of attributes stored in this element.
	 * @return the number of attributes. 
	 */
	public int getAttributeCount();	
}