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

import org.graphstream.graph.CompoundAttribute;
import org.graphstream.graph.Element;
import org.graphstream.graph.NullAttributeException;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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

	// Construction

	/**
	 * New element.
	 *
	 * @param id
	 *            The unique identifier of this element.
	 */
	public AbstractElement(final String id) {
        if (null == id || id.isEmpty()) {
          throw new IllegalArgumentException("Id cannot be null/empty.");
        }
		this.id = id;
	}

	// Access

    @Override
	public String getId() {
		return id;
	}

    @Override
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

	protected abstract boolean nullAttributesAreErrors(); // XXX

	/**
	 * Called for each change in the attribute set. This method must be
	 * implemented by sub-elements in order to send events to the graph
	 * listeners.
	 *
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
	protected abstract void attributeChanged(AttributeChangeEvent event,
			String attribute, Object oldValue, Object newValue);

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
	public String getLabel(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null && o instanceof CharSequence)
				return o.toString();
		}

		if (nullAttributesAreErrors())
			throw new NullAttributeException(key);

		return null;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
    @Override
	public Number getNumber(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);
            if (null == o) {
                return null;
            }
            if (o instanceof Number) {
                return (Number) o;
            }
            if (o instanceof CharSequence) {
                try {
                    return Double.parseDouble((String) o);
                } catch (NumberFormatException e) {

                }
            }
		}

		if (nullAttributesAreErrors())
			throw new NullAttributeException(key);

        return null;
	}

    @Override
    public double getDouble(String key) {
        final Number num = this.getNumber(key);
        if (null == num) {
            return Double.NaN;
        }
        return num.doubleValue();
    }

    @Override
    public float getFloat(String key) {
        final Number num = this.getNumber(key);
        if (null == num) {
            return Float.NaN;
        }
        return num.floatValue();
    }

    @Override
    public int getInteger(String key) {
        final Number num = this.getNumber(key);
        if (null == num) {
            return 0;
        }
        return num.intValue();
    }

    @Override
    public long getLong(String key) {
        final Number num = this.getNumber(key);
        if (null == num) {
            return 0L;
        }
        return num.longValue();
    }

    @Override
    public short getShort(String key) {
        final Number num = this.getNumber(key);
        if (null == num) {
            return 0;
        }
        return num.shortValue();
    }

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
    @Override
	@SuppressWarnings("unchecked")
	public Collection<? extends Number> getVector(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null && o instanceof Collection)
				return ((Collection<? extends Number>) o);
		}

		if (nullAttributesAreErrors())
			throw new NullAttributeException(key);

		return null;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
    @Override
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
    @Override
	public Map<?, ?> getHash(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null) {
				if (o instanceof Map<?, ?>)
					return ((Map<?, ?>) o);
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
    @Override
	public boolean hasAttribute(String key) {
		if (attributes != null)
			return attributes.containsKey(key);

		return false;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
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

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
    @Override
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
    @Override
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
    @Override
	public boolean hasVector(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null && o instanceof Collection<?>)
				return true;
		}

		return false;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
    @Override
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
    @Override
	public boolean hasHash(String key) {
		if (attributes != null) {
			Object o = attributes.get(key);

			if (o != null
					&& (o instanceof Map<?, ?> || o instanceof CompoundAttribute))
				return true;
		}

		return false;
	}

    @Override
	public Iterator<String> getAttributeKeyIterator() {
		if (attributes != null)
			return attributes.keySet().iterator();

		return Collections.emptyIterator();
	}

    @Override
	public Iterable<String> getEachAttributeKey() {
		return getAttributeKeySet();
	}

    @Override
	public Collection<String> getAttributeKeySet() {
		if (attributes != null)
			return Collections.unmodifiableCollection(attributes.keySet());

		return Collections.emptySet();
	}

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

    @Override
	public void clearAttributes() {
        if (null == attributes) {
            return;
        }
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            attributeChanged(AttributeChangeEvent.REMOVE, entry.getKey(), entry.getValue(), null);
        }
        attributes.clear();
	}

	protected void clearAttributesWithNoEvent() {
		if (attributes != null)
			attributes.clear();
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
    @Override
	public boolean addAttribute(String attribute, Object... values) {
		if (attributes == null)
			attributes = new TreeMap<>();

		final Object value;
        if (null == values || values.length == 0)
			value = Boolean.TRUE;
		else if (values.length == 1)
			value = values[0];
		else
			value = values;

		final Object oldValue = this.attributes.put(attribute, value);
        if (null == oldValue || !oldValue.equals(value)) {
            // send updates
            final AttributeChangeEvent event = oldValue != null ? AttributeChangeEvent.CHANGE : AttributeChangeEvent.ADD;
            this.attributeChanged(event, attribute, oldValue, value);
            return true;
        } else {
            // attribute value did not change
            return false;
        }
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
    @Override
	public boolean changeAttribute(String attribute, Object... values) {
		return this.addAttribute(attribute, values);
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
    @Override
	public boolean setAttribute(String attribute, Object... values) {
		return this.addAttribute(attribute, values);
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
    @Override
	public boolean addAttributes(final Map<String, Object> attributes) {
        if (null == attributes || attributes.isEmpty()) {
            return false;
        }
		if (this.attributes == null)
			this.attributes = new TreeMap<>();
        boolean modified = false;
        for(final Map.Entry<String, Object> entry : attributes.entrySet()) {
            modified |= this.addAttribute(entry.getKey(), entry.getValue());
        }
        return modified;
	}

	/**
	 * @complexity O(log(n)) with n being the number of attributes of this
	 *             element.
	 */
    @Override
	public boolean removeAttribute(String attribute) {
        if (null == attributes) {
            return false;
        }

        // make sure to avoid recursive calls when synchronizing graphs
        final Object oldValue = this.attributes.remove(attribute);
        if (null == oldValue) {
            return false;
        }
        attributeChanged(AttributeChangeEvent.REMOVE, attribute, oldValue, null);
        return true;
	}
}