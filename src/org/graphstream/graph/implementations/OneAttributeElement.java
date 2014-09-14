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

import org.graphstream.graph.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * An implementation of an {@link org.graphstream.graph.Element}.
 * 
 * <p>
 * It allows only one attribute and has no internal map structure. <b>It is not
 * used and may be removed.</b>
 * </p>
 * 
 */
public abstract class OneAttributeElement implements Element {

	// Attributes

	/**
	 * Tag of this element.
	 */
	protected String id;

	/**
	 * The only one attribute
	 */
	Object attribute = null;

	// Constructors

	/**
	 * New element.
	 * 
	 * @param id
	 *            The unique identifier of this element.
	 */
	public OneAttributeElement(String id) {
		assert id != null : "Graph elements cannot have a null identifier";
		this.id = id;
	}

	// Accessors

    @Override
	public String getId() {
		return id;
	}

	@SuppressWarnings("all")
    @Override
	public <T> T getAttribute(String key) {
		return (T) attribute;
	}

	@SuppressWarnings("all")
    @Override
	public <T> T getFirstAttributeOf(String... keys) {
		return (T) attribute;
	}

	@SuppressWarnings("all")
    @Override
	public <T> T getAttribute(String key, Class<T> clazz) {
		return (T) attribute;
	}

	@SuppressWarnings("all")
    @Override
	public <T> T getFirstAttributeOf(Class<T> clazz, String... keys) {
		return (T) attribute;
	}

    @Override
	public String getLabel(String key) {
		if (attribute instanceof CharSequence) {
            return attribute.toString();
        } else {
            return null;
        }
	}

    @Override
	public Number getNumber(String key) {
		if (attribute instanceof Number)
			return (Number) attribute;
        else
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

	@SuppressWarnings("unchecked")
    @Override
	public Collection<? extends Number> getVector(String key) {
		if (attribute != null && attribute instanceof Collection)
			return ((Collection<? extends Number>) attribute);

		return null;
	}

    @Override
	public boolean hasAttribute(String key) {
		return true;
	}

    @Override
	public boolean hasAttribute(String key, Class<?> clazz) {
		if (attribute != null)
			return (clazz.isInstance(attribute));
		return false;
	}

    @Override
	public boolean hasLabel(String key) {
		return attribute instanceof CharSequence;
	}

    @Override
	public boolean hasNumber(String key) {
		return attribute instanceof Number;
	}

    @Override
	public boolean hasVector(String key) {
		return attribute instanceof Collection;
	}

    @Override
	public Iterator<String> getAttributeKeyIterator() {
		return Collections.emptyIterator();
	}


	@Override
	public String toString() {
		return id;
	}

	// Commands

    @Override
	public void clearAttributes() {
		attribute = null;
	}

    @Override
	public boolean addAttribute(String attribute, Object... values) {
        final Object oldValue = this.attribute;
        if (null == values || values.length < 0) {
            this.attribute = Boolean.TRUE;
        } else if (values.length == 1) {
            this.attribute = values[0];
        } else {
            this.attribute = values;
        }
        if (null == oldValue) {
            return true;
        } else {
            return !oldValue.equals(this.attribute);
        }
	}

    @Override
	public boolean changeAttribute(String attribute, Object... values) {
		return addAttribute(attribute, values);
	}

    @Override
	public boolean addAttributes(Map<String, Object> attributes) {
        if (null == attributes || attributes.isEmpty()) {
            return false;
        }
		return addAttribute("attribute", attributes.values().iterator().next());
	}

    @Override
	public boolean removeAttribute(String attribute) {
        if (null == this.attribute) {
            return false;
        } else {
            this.attribute = null;
            return true;
        }
	}

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