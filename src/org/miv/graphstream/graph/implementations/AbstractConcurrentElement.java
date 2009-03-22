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
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.miv.graphstream.graph.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.miv.graphstream.graph.CompoundAttribute;
import org.miv.graphstream.graph.Element;

/**
 * A base implementation of a thread-safe element.
 * 
 * @see org.miv.graphstream.graph.Element
 * @see org.miv.graphstream.graph.Graph
 * @see org.miv.graphstream.graph.Node
 * @see org.miv.graphstream.graph.Edge
 * 
 * @since 20090108
 */
public abstract class AbstractConcurrentElement 
	implements Element
{
	protected ConcurrentHashMap<String,Object> attributes;
	protected String id;
	
	private AbstractConcurrentElement()
	{
		attributes = new ConcurrentHashMap<String,Object>();
	}
	
	protected AbstractConcurrentElement( String id )
	{
		this();
		
		this.id = id;
	}
	
// --- Element implementation --- //
	
	/* @Override */
	public void addAttribute(String attribute, Object... values)
	{
		Object old_value = attributes.get( attribute );
		Object value;

		if( values.length == 0 )
		     value = true;
		else if( values.length == 1 )
		     value = values[0];
		else value = values;
		
		if( old_value != null )
		{
			attributeChanged( attribute, old_value, value );
			attributes.put( attribute, value );
		}
		else
		{
			attributes.put( attribute, value );
			attributeChanged( attribute, old_value, value );
		}
	}

	/* @Override */
	public void addAttributes(Map<String, Object> attributes)
	{
		Iterator<String> i = attributes.keySet().iterator();
		Iterator<Object> j = attributes.values().iterator();

		while( i.hasNext() && j.hasNext() )
			addAttribute( i.next(), j.next() );
	}

	/* @Override */
	public void changeAttribute(String attribute, Object... values)
	{
		addAttribute( attribute, values );
	}

	/* @Override */
	public void clearAttributes()
	{
		Iterator<String> keys = attributes.keySet().iterator();
		Iterator<Object> vals = attributes.values().iterator();

		while( keys.hasNext() && vals.hasNext() )
		{
			String key = keys.next();
			Object val = vals.next();

			attributeChanged( key, val, null );
		}

		attributes.clear();
	}

	/* @Override */
	public Object[] getArray(String key)
	{
		Object o = attributes.get( key );
		
		if( o != null && o instanceof Object[] )
			return ((Object[])o);
		
		return null;
	}

	/* @Override */
	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	/* @Override */
	public Object getAttribute(String key, Class<?> clazz)
	{
		Object o = attributes.get( key );
		
		if( o != null && clazz.isInstance( o ) )
			return o;
		
		return null;
	}

	/* @Override */
	public int getAttributeCount()
	{
		return attributes.size();
	}

	/* @Override */
	public Iterator<String> getAttributeKeyIterator()
	{
		return attributes.keySet().iterator();
	}

	/* @Override */
	public Map<String, Object> getAttributeMap()
	{
		return Collections.unmodifiableMap( attributes );
	}

	/* @Override */
	public Object getFirstAttributeOf(String... keys)
	{
		Object o = null;
		
		for( String key: keys )
		{
			o = getAttribute( key );
			
			if( o != null )
				return o;
		}
		
		return o;
	}

	/* @Override */
	public Object getFirstAttributeOf(Class<?> clazz, String... keys)
	{
		Object o = null;
		
		for( String key: keys )
		{
			o = attributes.get( key );
			
			if( o != null && clazz.isInstance( o ) )
				return o;
		}
		
		return null;
	}

	/* @Override */
	public HashMap<?, ?> getHash(String key)
	{
		Object o = attributes.get( key );
		
		if( o != null )
		{
			if( o instanceof HashMap<?,?> )
				return ((HashMap<?,?>)o);
			if( o instanceof CompoundAttribute )
				return ((CompoundAttribute)o).toHashMap();
		}
		
		return null;
	}

	/* @Override */
	public String getId()
	{
		return id;
	}

	/* @Override */
	public CharSequence getLabel(String key)
	{
		Object o = attributes.get( key );

		if( o != null && o instanceof CharSequence )
			return (CharSequence) o;
		
		return null;
	}

	/* @Override */
	public double getNumber(String key)
	{
		Object o = attributes.get( key );

		if( o != null && o instanceof Number )
			return ((Number)o).doubleValue();
		
		return Double.NaN;
	}

	@SuppressWarnings("all")
	/* @Override */
	public ArrayList<? extends Number> getVector(String key)
	{
		Object o = attributes.get( key );
		
		if( o != null && o instanceof ArrayList )
			return ((ArrayList<? extends Number>)o);
		
		return null;
	}

	/* @Override */
	public boolean hasArray(String key)
	{
		Object o = attributes.get( key );
		
		if( o != null && o instanceof Object[] )
			return  true;
		
		return false;
	}

	/* @Override */
	public boolean hasAttribute(String key)
	{
		return attributes.containsKey(key);
	}

	/* @Override */
	public boolean hasAttribute(String key, Class<?> clazz)
	{
		Object o = attributes.get( key );

		if( o != null )
			return( clazz.isInstance( o ) );
		
		return false;
	}

	/* @Override */
	public boolean hasHash(String key)
	{
		Object o = attributes.get( key );
		
		if( o != null && ( o instanceof HashMap || o instanceof CompoundAttribute ) )
			return  true;
		
		return false;
	}

	/* @Override */
	public boolean hasLabel(String key)
	{
		Object o = attributes.get( key );

		if( o != null )
			return( o instanceof CharSequence );
		
		return false;
	}

	/* @Override */
	public boolean hasNumber(String key)
	{
		Object o = attributes.get( key );

		if( o != null )
			return( o instanceof Number );
	
		return false;
	}

	/* @Override */
	public boolean hasVector(String key)
	{
		Object o = attributes.get( key );
		
		if( o != null && o instanceof ArrayList )
			return  true;
		
		return false;
	}

	/* @Override */
	public void removeAttribute(String attribute)
	{
		attributeChanged( attribute, attributes.get( attribute ), null );
		attributes.remove( attribute );
	}

	/* @Override */
	public void setAttribute(String attribute, Object... values)
	{
		addAttribute( attribute, values );
	}

	/* @Override *//*
	public void setId(String id)
	{
		this.id = id;
	}*/
	
// --- //
	
	/**
	 * Called for each change in the attribute set. This method must be
	 * implemented by sub-elements in order to send events to the graph
	 * listeners.
	 * @param attribute The attribute name that changed.
	 * @param oldValue The old value of the attribute, null if the attribute was
	 *        added.
	 * @param newValue The new value of the attribute, null if the attribute is
	 *        about to be removed.
	 */
	protected abstract void attributeChanged( String attribute, Object oldValue, Object newValue );

}
