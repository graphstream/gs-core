/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
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
package org.graphstream.graph.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graphstream.graph.CompoundAttribute;
import org.graphstream.graph.Element;
import org.graphstream.graph.NullAttributeException;

/**
 * A base implementation of an element.
 * 
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
	// Attribute

	// protected static Set<String> emptySet = new HashSet<String>();

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
	protected HashMap<String, Object> attributes = null;

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
	protected abstract String myGraphId(); // XXX

	protected abstract long newEvent(); // XXX

	protected abstract boolean nullAttributesAreErrors(); // XXX

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	// public Object getAttribute( String key )
	@SuppressWarnings("all")
	public <T> T getAttribute(String key) {
		if (attributes != null) {
			T value = (T) attributes.get(key);

			if (value != null)
				return value;
		}

		if (nullAttributesAreErrors())
			throw new NullAttributeException(key);

		return null;
	}

	/**
	 * @complexity O(log(n*m)) with n being the number of attributes of this
	 *             element and m the number of keys given.
	 */
	// public Object getFirstAttributeOf( String ... keys )
	@SuppressWarnings("all")
	public <T> T getFirstAttributeOf(String... keys) {
		Object o = null;

		if (attributes != null) {
			for (String key : keys) {
				o = attributes.get(key);

				if (o != null)
					return (T) o;
			}
		}

		if (o == null && nullAttributesAreErrors())
			throw new NullAttributeException();

		return (T) o;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	// public Object getAttribute( String key, Class<?> clazz )
	@SuppressWarnings("all")
	public <T> T getAttribute(String key, Class<T> clazz) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null && clazz.isInstance(o))
				return (T) o;
		}

		if (nullAttributesAreErrors())
			throw new NullAttributeException(key);

		return null;
	}

	/**
	 * @complexity O(log(n*m)) with n being the number of attributes of this
	 *             element and m the number of keys given.
	 */
	// public Object getFirstAttributeOf( Class<?> clazz, String ... keys )
	@SuppressWarnings("all")
	public <T> T getFirstAttributeOf(Class<T> clazz, String... keys) {
		Object o = null;

		if (attributes == null)
			return null;

		for (String key : keys) {
			o = attributes.get(key);

			if (o != null && clazz.isInstance(o))
				return (T) o;
		}

		if (nullAttributesAreErrors())
			throw new NullAttributeException();

		return null;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public CharSequence getLabel(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null && o instanceof CharSequence)
				return (CharSequence) o;
		}

		if (nullAttributesAreErrors())
			throw new NullAttributeException(key);

		return null;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public double getNumber(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null) {
				if (o instanceof Number)
					return ((Number) o).doubleValue();

				if (o instanceof String) {
					try {
						return Double.parseDouble((String) o);
					} catch (NumberFormatException e) {}
				} else if(o instanceof CharSequence) {
					try {
						return Double.parseDouble(((CharSequence)o).toString());
					} catch (NumberFormatException e) {}
				}
			}
		}

		if (nullAttributesAreErrors())
			throw new NullAttributeException(key);

		return Double.NaN;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<? extends Number> getVector(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null && o instanceof ArrayList)
				return ((ArrayList<? extends Number>) o);
		}

		if (nullAttributesAreErrors())
			throw new NullAttributeException(key);

		return null;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public Object[] getArray(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null && o instanceof Object[])
				return ((Object[]) o);
		}

		if (nullAttributesAreErrors())
			throw new NullAttributeException(key);

		return null;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public HashMap<?, ?> getHash(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null) {
				if (o instanceof HashMap<?, ?>)
					return ((HashMap<?, ?>) o);
				if (o instanceof CompoundAttribute)
					return ((CompoundAttribute) o).toHashMap();
			}
		}

		if (nullAttributesAreErrors())
			throw new NullAttributeException(key);

		return null;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public boolean hasAttribute(String key) {
		if (attributes != null)
			return attributes.containsKey(key);

		return false;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public boolean hasAttribute(String key, Class<?> clazz) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null)
				return (clazz.isInstance(o));
		}

		return false;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public boolean hasLabel(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null)
				return (o instanceof CharSequence);
		}

		return false;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public boolean hasNumber(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null)
				return (o instanceof Number);
		}

		return false;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public boolean hasVector(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null && o instanceof ArrayList<?>)
				return true;
		}

		return false;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public boolean hasArray(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null && o instanceof Object[])
				return true;
		}

		return false;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public boolean hasHash(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null
					&& (o instanceof HashMap<?, ?> || o instanceof CompoundAttribute))
				return true;
		}

		return false;
	}

	public Iterator<String> getAttributeKeyIterator() {
		if (attributes != null)
			return attributes.keySet().iterator();

		return null;
	}

	public Iterable<String> getEachAttributeKey() {
		if (attributes != null)
			return attributes.keySet();

		return Collections.emptySet();
	}

	public Collection<String> getAttributeKeySet() {
		if (attributes != null)
			return (Collection<String>) Collections
					.unmodifiableCollection(attributes.keySet());

		return Collections.emptySet();
	}

	// public Map<String,Object> getAttributeMap()
	// {
	// if( attributes != null )
	// {
	// if( constMap == null )
	// constMap = new ConstMap<String,Object>( attributes );
	//
	// return constMap;
	// }
	//
	// return null;
	// }

	/**
	 * Override the Object method
	 */
	@Override
	public String toString() {
		return id;
	}

	public int getAttributeCount() {
		if (attributes != null)
			return attributes.size();

		return 0;
	}

	// Command

	public void clearAttributes() {
		clearAttributes_(myGraphId(), newEvent());
	}

	protected void clearAttributes_(String sourceId, long timeId) {
		if (attributes != null) {
			Iterator<String> keys = attributes.keySet().iterator();
			Iterator<Object> vals = attributes.values().iterator();

			while (keys.hasNext() && vals.hasNext()) {
				String key = keys.next();
				Object val = vals.next();

				attributeChanged(sourceId, timeId, key,
						AttributeChangeEvent.REMOVE, val, null);
			}

			attributes.clear();
		}
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public void addAttribute(String attribute, Object... values) {
		addAttribute_(myGraphId(), newEvent(), attribute, values);
	}

	protected void addAttribute_(String sourceId, long timeId,
			String attribute, Object... values) {
		if (attributes == null)
			attributes = new HashMap<String, Object>(1);

		Object oldValue;
		Object value;

		if (values.length == 0)
			value = true;
		else if (values.length == 1)
			value = values[0];
		else
			value = values;

		AttributeChangeEvent event = AttributeChangeEvent.ADD;

		if (attributes.containsKey(attribute)) // In case the value is null,
			event = AttributeChangeEvent.CHANGE; // but the attribute exists.

		oldValue = attributes.put(attribute, value);
		attributeChanged(sourceId, timeId, attribute, event, oldValue, value);
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public void changeAttribute(String attribute, Object... values) {
		changeAttribute_(myGraphId(), newEvent(), attribute, values);
	}

	protected void changeAttribute_(String sourceId, long timeId,
			String attribute, Object... values) {
		addAttribute_(sourceId, timeId, attribute, values);
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public void setAttribute(String attribute, Object... values) {
		setAttribute_(myGraphId(), newEvent(), attribute, values);
	}

	protected void setAttribute_(String sourceId, long timeId,
			String attribute, Object... values) {
		addAttribute_(sourceId, timeId, attribute, values);
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public void addAttributes(Map<String, Object> attributes) {
		addAttributes_(myGraphId(), newEvent(), attributes);
	}

	protected void addAttributes_(String sourceId, long timeId,
			Map<String, Object> attributes) {
		if (this.attributes == null)
			this.attributes = new HashMap<String, Object>(1);

		Iterator<String> i = attributes.keySet().iterator();
		Iterator<Object> j = attributes.values().iterator();

		while (i.hasNext() && j.hasNext())
			addAttribute_(sourceId, timeId, i.next(), j.next());
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
	public void removeAttribute(String attribute) {
		removeAttribute_(myGraphId(), newEvent(), attribute);
	}

	protected void removeAttribute_(String sourceId, long timeId,
			String attribute) {
		if (attributes != null) {
			//
			// 'attributesBeingRemoved' is created only if this is required.
			//
			if (attributesBeingRemoved == null)
				attributesBeingRemoved = new ArrayList<String>();

			//
			// Avoid recursive calls when synchronising graphs.
			//
			if (attributes.containsKey(attribute)
					&& !attributesBeingRemoved.contains(attribute)) {
				attributesBeingRemoved.add(attribute);

				attributeChanged(sourceId, timeId, attribute,
						AttributeChangeEvent.REMOVE, attributes.get(attribute),
						null);

				attributesBeingRemoved
						.remove(attributesBeingRemoved.size() - 1);
				attributes.remove(attribute);
			}
		}
	}

	public static enum AttributeChangeEvent {
		ADD, CHANGE, REMOVE
	};

	/**
	 * Called for each change in the attribute set. This method must be
	 * implemented by sub-elements in order to send events to the graph
	 * listeners.
	 * 
	 * @param sourceId
	 *            The source of the change.
	 * @param timeId
	 *            The source time of the change, for synchronization.
	 * @param attribute
	 *            The attribute name that changed.
	 * @param event
	 *            The type of event among ADD, CHANGE and REMOVE.
	 * @param oldValue
	 *            The old value of the attribute, null if the attribute was
	 *            added.
	 * @param newValue
	 *            The new value of the attribute, null if the attribute is about
	 *            to be removed.
	 */
	protected abstract void attributeChanged(String sourceId, long timeId,
			String attribute, AttributeChangeEvent event, Object oldValue,
			Object newValue);
}