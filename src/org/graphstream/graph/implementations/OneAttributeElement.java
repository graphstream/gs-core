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
import java.util.Iterator;
import java.util.Map;

import org.graphstream.graph.Element;


/**
 * An implementation of an {@link org.graphstream.graph.Element}.
 * 
 * <p>
 * It allows only one attribute and has no internal map structure. <b>It is not used and may be removed.</b>
 * </p>
 * 
 */
public abstract class OneAttributeElement implements Element
{
// Constants

// Attributes

	/**
	 * Tag of this element.
	 */
	protected String id;

	/**
	 * The only one attribute
	 */
	Object attribute=null;

// Constructors

	/**
	 * New element.
	 * @param id The unique identifier of this element.
	 */
	public OneAttributeElement( String id )
	{
		assert id != null : "Graph elements cannot have a null identifier";
		this.id = id;
	}

// Accessors

	public String getId()
	{
		return id;
	}

	@SuppressWarnings("all")
	public <T> T getAttribute( String key )
	{
		return (T)attribute;
	}
	
	@SuppressWarnings("all")
	public <T> T getFirstAttributeOf( String ... keys )
	{
		return (T)attribute;
	}

	@SuppressWarnings("all")
	public <T> T getAttribute( String key, Class<T> clazz )
	{
		return (T)attribute;
	}
	
	@SuppressWarnings("all")
	public <T> T getFirstAttributeOf( Class<T> clazz, String ... keys )
	{
		return (T)attribute;
	}

	public CharSequence getLabel( String key )
	{
			if( attribute != null && attribute instanceof CharSequence )
				return (CharSequence) attribute;
			return null;
	}

	public double getNumber( String key )
	{
					if( attribute != null && attribute instanceof Number )
				return ((Number)attribute).doubleValue();
		

		return Double.NaN;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<? extends Number> getVector( String key )
	{
			if( attribute != null && attribute instanceof ArrayList )
				return ((ArrayList<? extends Number>)attribute);
		
		return null;
	}

	public boolean hasAttribute( String key )
	{
		
		return true;
	}

	public boolean hasAttribute( String key, Class<?> clazz )
	{
			if( attribute != null )
				return( clazz.isInstance( attribute ) );
		return false;
	}

	public boolean hasLabel( String key )
	{
			if( attribute != null )
				return( attribute instanceof CharSequence );
		

		return false;
	}
	
	public boolean hasNumber( String key )
	{
			if( attribute != null )
				return( attribute instanceof Number );
		

		return false;
	}
	
	public boolean hasVector( String key )
	{
			if( attribute != null && attribute instanceof ArrayList<?> )
				return  true;
		
		
		return false;
	}

	public Iterator<String> getAttributeKeyIterator()
	{
				return null;
	}
	
	public Map<String,Object> getAttributeMap()
	{
				return null;
	}
	
	/**
	 * Override the Object method
	 */
	@Override
	public String toString()
	{
		return id;
	}

// Commands
	
	public void clearAttributes()
	{
		attribute = null;
	}

	public void addAttribute( String attribute, Object value )
	{
		this.attribute= value;
//		System.out.println(attribute+" = "+value.toString());
	}
	
	public void changeAttribute( String attribute, Object value )
	{
		addAttribute( attribute, value );
//		System.out.println(attribute+" = "+value.toString());

	}

	public void addAttributes( Map<String,Object> attributes )
	{
//		System.out.println(attributes.toString());
		if(attributes.size()>=1)
			addAttribute( "", attributes.get( ( attributes.keySet().toArray()[0] ) ));

	}

	public void removeAttribute( String attribute )
	{
		this.attribute=null;
	}

	public static enum AttributeChangeEvent { ADD, CHANGE, REMOVE };
	
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