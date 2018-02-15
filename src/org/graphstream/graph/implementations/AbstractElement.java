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
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.graphstream.graph.Element;

/**
 * A base implementation of an element.
 * <p>
 * <p>
 * This class is the Base class for {@link org.graphstream.graph.Node},
 * {@link org.graphstream.graph.Edge} and {@link org.graphstream.graph.Graph}.
 * An element is made of an unique and arbitrary identifier that identifies it,
 * and a set of attributes.
 * </p>
 *
 * @since 20040910
 */
public abstract class AbstractElement implements Element {
	public enum AttributeChangeEvent {
		ADD, CHANGE, REMOVE
	}

	// Attribute

	/**
	 * Tag of this element.
	 */
	protected final String id;

	/**
	 * The index of this element.
	 */
	private int index;

	/**
	 * Attributes map. This map is created only when needed. It contains pairs
	 * (key,value) where the key is the attribute name and the value an Object.
	 */
	protected Map<String, Object> attributes = null;

	/**
	 * Vector used when removing attributes to avoid recursive removing.
	 */
	protected ArrayList<String> attributesBeingRemoved = null;

	// Construction

	/**
	 * New element.
	 *
	 * @param id
	 *            The unique identifier of this element.
	 */
	public AbstractElement(String id) {
		assert id != null : "Graph elements cannot have a null identifier";
		this.id = id;
	}

	// Access

	public String getId() {
		return id;
	}

	public int getIndex() {
		return index;
	}

	/**
	 * Used by subclasses to change the index of an element
	 *
	 * @param index
	 *            the new index
	 */
	protected void setIndex(int index) {
		this.index = index;
	}

	// XXX UGLY. how to create events in the abstract element ?
	// XXX The various methods that add and remove attributes will propagate an
	// event
	// XXX sometimes this is in response to another event and the
	// sourceId/timeId is given
	// XXX sometimes this comes from a direct call to
	// add/change/removeAttribute() methods
	// XXX in which case we need to generate a new event (sourceId/timeId) using
	// the graph
	// XXX id and a new time. These methods allow access to this.
	// protected abstract String myGraphId(); // XXX

	// protected abstract long newEvent(); // XXX

	/**
	 * Called for each change in the attribute set. This method must be implemented
	 * by sub-elements in order to send events to the graph listeners.
	 *
	 * @param attribute
	 *            The attribute name that changed.
	 * @param event
	 *            The type of event among ADD, CHANGE and REMOVE.
	 * @param oldValue
	 *            The old value of the attribute, null if the attribute was added.
	 * @param newValue
	 *            The new value of the attribute, null if the attribute is about to
	 *            be removed.
	 */
	protected abstract void attributeChanged(AttributeChangeEvent event, String attribute, Object oldValue,
			Object newValue);

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	@Override
	public Object getAttribute(String key) {
		if (attributes != null) {
			Object value = attributes.get(key);

			if (value != null)
				return value;
		}

		return null;
	}

	/**
	 * @complexity O(log(n*m)) with n being the number of attributes of this element
	 *             and m the number of keys given.
	 */
	@Override
	public Object getFirstAttributeOf(String... keys) {
		Object o = null;

		if (attributes != null) {
			for (String key : keys) {
				o = attributes.get(key);

				if (o != null)
					return o;
			}
		}

		return o;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	@Override
	public <T> T getAttribute(String key, Class<T> clazz) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null && clazz.isInstance(o))
				return clazz.cast(o);
		}

		return null;
	}

	/**
	 * @complexity O(log(n*m)) with n being the number of attributes of this element
	 *             and m the number of keys given.
	 */
	@Override
	public <T> T getFirstAttributeOf(Class<T> clazz, String... keys) {
		Object o = null;

		if (attributes == null)
			return null;

		for (String key : keys) {
			o = attributes.get(key);

			if (o != null && clazz.isInstance(o))
				return clazz.cast(o);
		}

		return null;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	@Override
	public boolean hasAttribute(String key) {
		return attributes != null && attributes.containsKey(key);
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	@Override
	public boolean hasAttribute(String key, Class<?> clazz) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null)
				return (clazz.isInstance(o));
		}

		return false;
	}

	@Override
	public Stream<String> attributeKeys() {
		if (attributes == null)
			return Stream.empty();

		return attributes.keySet().stream();
	}

	/**
	 * Override the Object method
	 */
	@Override
	public String toString() {
		return id;
	}

	@Override
	public int getAttributeCount() {
		if (attributes != null)
			return attributes.size();

		return 0;
	}

	// Command

	@Override
	public void clearAttributes() {
		if (attributes != null) {
			for (Map.Entry<String, Object> entry : attributes.entrySet())
				attributeChanged(AttributeChangeEvent.REMOVE, entry.getKey(), entry.getValue(), null);

			attributes.clear();
		}
	}

	protected void clearAttributesWithNoEvent() {
		if (attributes != null)
			attributes.clear();
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	@Override
	public void setAttribute(String attribute, Object... values) {
		if (attributes == null)
			attributes = new HashMap<>(1);

		Object oldValue;
		Object value;

		if (values == null)
			value = null;
		else if (values.length == 0)
			value = true;
		else if (values.length == 1)
			value = values[0];
		else
			value = values;

		AttributeChangeEvent event = AttributeChangeEvent.ADD;

		if (attributes.containsKey(attribute)) // In case the value is null,
			event = AttributeChangeEvent.CHANGE; // but the attribute exists.

		oldValue = attributes.put(attribute, value);
		attributeChanged(event, attribute, oldValue, value);
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this element.
	 */
	@Override
	public void removeAttribute(String attribute) {
		if (attributes != null) {
			//
			// 'attributesBeingRemoved' is created only if this is required.
			//
			if (attributesBeingRemoved == null)
				attributesBeingRemoved = new ArrayList<>();

			//
			// Avoid recursive calls when synchronizing graphs.
			//
			if (attributes.containsKey(attribute) && !attributesBeingRemoved.contains(attribute)) {
				attributesBeingRemoved.add(attribute);

				attributeChanged(AttributeChangeEvent.REMOVE, attribute, attributes.get(attribute), null);

				attributesBeingRemoved.remove(attributesBeingRemoved.size() - 1);
				attributes.remove(attribute);
			}
		}
	}
}