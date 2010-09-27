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
package org.graphstream.graph.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.graphstream.graph.CompoundAttribute;
import org.graphstream.graph.Element;
import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;

/**
 * A base implementation of an element with multi-thread capabilities. 
 * 
 * It is similar to the  {@link org.graphstream.graph.implementations.AbstractElement} class, but with thread-safe data structures. 
 * 
 * @see org.graphstream.graph.implementations.AbstractElement
 */
public abstract class AbstractConcurrentElement implements Element
{
// Attribute

	/**
	 * Tag of this element.
	 */
	private String id;

	/**
	 * Attributes map. This map is created only when needed. It contains pairs
	 * (key,value) where the key is the attribute name and the value an Object.
	 */
	protected ConcurrentHashMap<String,Object> attributes = null;
	

// Construction

	/**
	 * New element.
	 * @param id The unique identifier of this element.
	 */
	public AbstractConcurrentElement( String id )
	{
		assert id != null : "Graph elements cannot have a null identifier";
		this.id = id;
	}

// Access

	public String getId()
	{
		return id;
	}
	
	// XXX UGLY. how to create events in the abstract element ?
	// XXX The various methods that add and remove attributes will propagate an event
	// XXX sometimes this is in response to another event and the sourceId/timeId is given
	// XXX sometimes this comes from a direct call to add/change/removeAttribute() methods
	// XXX in which case we need to generate a new event (sourceId/timeId) using the graph
	// XXX id and a new time. These methods allow access to this.
	protected abstract String myGraphId();		// XXX
	protected abstract long newEvent();			// XXX

	@SuppressWarnings("all")
	public <T> T getAttribute( String key )
	{
		if( attributes != null )
			return (T)attributes.get( key );

		return null;
	}
	
	@SuppressWarnings("all")
	public <T> T getFirstAttributeOf( String ... keys )
	{
		Object o = null;
		
		if( attributes == null )
			return null;
		
		for( String key: keys )
		{
			o = getAttribute( key );
			
			if( o != null )
				return (T)o;
		}
		
		return (T)o;
	}

	@SuppressWarnings("all")
	public <T> T getAttribute( String key, Class<T> clazz )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );
			
			if( o != null && clazz.isInstance( o ) )
				return (T)o;
		}
		
		return null;
	}
	
	@SuppressWarnings("all")
	public <T> T getFirstAttributeOf( Class<T> clazz, String ... keys )
	{
		Object o = null;
		
		if( attributes == null )
			return null;
		
		for( String key: keys )
		{
			o = attributes.get( key );
			
			if( o != null && clazz.isInstance( o ) )
				return (T)o;
		}
		
		return null;
	}

	public CharSequence getLabel( String key )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );

			if( o != null && o instanceof CharSequence )
				return (CharSequence) o;
		}

		return null;
	}

	public double getNumber( String key )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );

			if( o != null && o instanceof Number )
				return ((Number)o).doubleValue();
		}

		return Double.NaN;
	}
	
	@SuppressWarnings("unchecked")
    public ArrayList<? extends Number> getVector( String key )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );
			
			if( o != null && o instanceof ArrayList )
				return ((ArrayList<? extends Number>)o);
		}
		
		return null;
	}
	
	public Object[] getArray( String key )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );
			
			if( o != null && o instanceof Object[] )
				return ((Object[])o);
		}

		return null;
	}
	
	public HashMap<?,?> getHash( String key )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );
			
			if( o != null )
			{
				if( o instanceof HashMap<?,?> )
					return ((HashMap<?,?>)o);
				if( o instanceof CompoundAttribute )
					return ((CompoundAttribute)o).toHashMap();
			}
		}
		
		return null;
	}

	public boolean hasAttribute( String key )
	{
		if( attributes != null )
			return attributes.containsKey( key );

		return false;
	}

	public boolean hasAttribute( String key, Class<?> clazz )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );

			if( o != null )
				return( clazz.isInstance( o ) );
		}

		return false;
	}

	public boolean hasLabel( String key )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );

			if( o != null )
				return( o instanceof CharSequence );
		}

		return false;
	}
	
	public boolean hasNumber( String key )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );

			if( o != null )
				return( o instanceof Number );
		}

		return false;
	}
	
	public boolean hasVector( String key )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );
			
			if( o != null && o instanceof ArrayList<?> )
				return  true;
		}
		
		return false;
	}
	
	public boolean hasArray( String key )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );
			
			if( o != null && o instanceof Object[] )
				return  true;
		}
		
		return false;
	}
	
	public boolean hasHash( String key )
	{
		if( attributes != null )
		{
			Object o = attributes.get( key );
			
			if( o != null && ( o instanceof HashMap<?,?> || o instanceof CompoundAttribute ) )
				return  true;
		}
		
		return false;
	}

	public Iterator<String> getAttributeKeyIterator()
	{
		if( attributes != null )
			return attributes.keySet().iterator();

		return null;
	}
	
	public Iterable<String> getAttributeKeySet()
	{
		if( attributes != null )
			return attributes.keySet();
		
		return null;
	}
	
//	public Map<String,Object> getAttributeMap()
//	{
//		if( attributes != null )
//		{
//			if( constMap == null )
//				constMap = new ConstMap<String,Object>( attributes );
//
//			return constMap;
//		}
//	
//		return null;
//	}
	

	@Override
	public String toString()
	{
		return id;
	}
	
	public int getAttributeCount()
	{
		if( attributes != null )
			return attributes.size();
		
		return 0;
	}

// Command
	
	public void clearAttributes()
	{
		clearAttributes_( myGraphId(), newEvent() );
	}
	
	protected void clearAttributes_( String sourceId, long timeId )
	{
		if( attributes != null )
		{
			Iterator<String> keys = attributes.keySet().iterator();
			Iterator<Object> vals = attributes.values().iterator();

			while( keys.hasNext() && vals.hasNext() )
			{
				String key = keys.next();
				Object val = vals.next();

				attributeChanged( sourceId, timeId, key, AttributeChangeEvent.REMOVE, val, null );
			}

			attributes.clear();
		}
	}

	public void addAttribute( String attribute, Object ... values )
	{
		addAttribute_( myGraphId(), newEvent(), attribute, values );
	}
	
	protected void addAttribute_( String sourceId, long timeId, String attribute, Object ... values )
	{
		if( attributes == null )
			attributes = new ConcurrentHashMap<String,Object>(1);

		Object old_value = attributes.get( attribute );
		Object value;

		if( values.length == 0 )
		     value = true;
		else if( values.length == 1 )
		     value = values[0];
		else value = values;
		
		AttributeChangeEvent event = AttributeChangeEvent.ADD;
			
		if( attributes.containsKey( attribute ) )	// In case the value is null,
			event = AttributeChangeEvent.CHANGE;	// but the attribute exists.
		
		attributes.put( attribute, value );
		attributeChanged( sourceId, timeId, attribute, event, old_value, value );
	}
	
	public void changeAttribute( String attribute, Object ... values )
	{
		changeAttribute_( myGraphId(), newEvent(), attribute, values );
	}
	
	protected void changeAttribute_( String sourceId, long timeId, String attribute, Object ... values )
	{
		addAttribute_( sourceId, timeId, attribute, values );
	}
	
	public void setAttribute( String attribute, Object ... values )
	{
		setAttribute_( myGraphId(), newEvent(), attribute, values );
	}
	
	protected void setAttribute_( String sourceId, long timeId, String attribute, Object ...values )
	{
		addAttribute_( sourceId, timeId, attribute, values );
	}
	
	public void addAttributes( Map<String,Object> attributes )
	{
		addAttributes_( myGraphId(), newEvent(), attributes );
	}
	
	protected void addAttributes_( String sourceId, long timeId, Map<String,Object> attributes )
	{
		if( this.attributes == null )
			this.attributes = new ConcurrentHashMap<String,Object>(1);

		Iterator<String> i = attributes.keySet().iterator();
		Iterator<Object> j = attributes.values().iterator();

		while( i.hasNext() && j.hasNext() )
			addAttribute_( sourceId, timeId, i.next(), j.next() );
	}
	
	public void removeAttribute( String attribute )
	{
		removeAttribute_( myGraphId(), newEvent(), attribute );
	}
	
	protected void removeAttribute_( String sourceId, long timeId, String attribute )
	{
		if( attributes != null )
		{
			if( attributes.containsKey( attribute ) )	// Avoid recursive calls when synchronising graphs.
			{
				attributes.remove( attribute );
				attributeChanged( sourceId, timeId, attribute, AttributeChangeEvent.REMOVE, attributes.get( attribute ), null );
			}
		}
	}
	
	/**
	 * Called for each change in the attribute set. This method must be
	 * implemented by sub-elements in order to send events to the graph
	 * listeners.
	 * @param sourceId The source of the change.
	 * @param timeId The source time of the change, for synchronization.
	 * @param attribute The attribute name that changed.
	 * @param event The type of event among ADD, CHANGE and REMOVE.
	 * @param oldValue The old value of the attribute, null if the attribute was
	 *        added.
	 * @param newValue The new value of the attribute, null if the attribute is
	 *        about to be removed.
	 */
	protected abstract void attributeChanged( String sourceId, long timeId, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue );
}