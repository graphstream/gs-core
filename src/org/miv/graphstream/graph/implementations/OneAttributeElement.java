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
package org.miv.graphstream.graph.implementations;

import java.util.*;

import org.miv.graphstream.graph.Element;

/**
 * Any element of the graph (node, edge or graph).
 * 
 * <p>
 * Base class for {@link org.miv.graphstream.graph.Node},
 * {@link org.miv.graphstream.graph.Edge} and
 * {@link org.miv.graphstream.graph.Graph}. An element is made of an unique and
 * arbitrary identifier that identifies it, and a set of attributes.
 * </p>
 * 
 * <p>
 * Attributes can be any object and are identified by arbitrary strings. Some
 * attributes are stored as numbers or strings and are in this case named
 * number, label or vector. There are utility methods to handle these attributes
 * ({@link #getNumber(String)}, {@link #getLabel(String)}) or
 * {@link #getVector(String)}, however they are  also accessible through the
 * more general method {@link #getAttribute(String)}.
 * </p>
 * 
 * @see org.miv.graphstream.graph.Graph
 * @see org.miv.graphstream.graph.Node
 * @see org.miv.graphstream.graph.Edge
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20040910
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

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#getId()
	 */
	public String getId()
	{
		return id;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#getAttribute(java.lang.String)
	 */
	public Object getAttribute( String key )
	{
		return attribute;
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#getFirstAttributeOf(java.lang.String)
	 */
	public Object getFirstAttributeOf( String ... keys )
	{
		return attribute;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#getAttribute(java.lang.String, java.lang.Class)
	 */
	public Object getAttribute( String key, Class<?> clazz )
	{
		return attribute;
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#getFirstAttributeOf(java.lang.Class, java.lang.String)
	 */
	public Object getFirstAttributeOf( Class<?> clazz, String ... keys )
	{
		return attribute;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#getLabel(java.lang.String)
	 */
	public CharSequence getLabel( String key )
	{
			if( attribute != null && attribute instanceof CharSequence )
				return (CharSequence) attribute;
			return null;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#getNumber(java.lang.String)
	 */
	public double getNumber( String key )
	{
					if( attribute != null && attribute instanceof Number )
				return ((Number)attribute).doubleValue();
		

		return Double.NaN;
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#getVector(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
    public ArrayList<? extends Number> getVector( String key )
	{
			if( attribute != null && attribute instanceof ArrayList )
				return ((ArrayList<? extends Number>)attribute);
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#hasAttribute(java.lang.String)
	 */
	public boolean hasAttribute( String key )
	{
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#hasAttribute(java.lang.String, java.lang.Class)
	 */
	public boolean hasAttribute( String key, Class<?> clazz )
	{
			if( attribute != null )
				return( clazz.isInstance( attribute ) );
		return false;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#hasLabel(java.lang.String)
	 */
	public boolean hasLabel( String key )
	{
			if( attribute != null )
				return( attribute instanceof CharSequence );
		

		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#hasNumber(java.lang.String)
	 */
	public boolean hasNumber( String key )
	{
			if( attribute != null )
				return( attribute instanceof Number );
		

		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#hasVector(java.lang.String)
	 */
	public boolean hasVector( String key )
	{
			if( attribute != null && attribute instanceof ArrayList )
				return  true;
		
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#getAttributeKeyIterator()
	 */
	public Iterator<String> getAttributeKeyIterator()
	{
				return null;
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#getAttributeMap()
	 */
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
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#clearAttributes()
	 */
	public void clearAttributes()
	{
		attribute = null;
	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#addAttribute(java.lang.String, java.lang.Object)
	 */
	public void addAttribute( String attribute, Object value )
	{
		this.attribute= value;
//		System.out.println(attribute+" = "+value.toString());
	}
	
	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#changeAttribute(java.lang.String, java.lang.Object)
	 */
	public void changeAttribute( String attribute, Object value )
	{
		addAttribute( attribute, value );
//		System.out.println(attribute+" = "+value.toString());

	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#addAttributes(java.util.Map)
	 */
	public void addAttributes( Map<String,Object> attributes )
	{
//		System.out.println(attributes.toString());
		if(attributes.size()>=1)
			addAttribute( "", attributes.get( ( attributes.keySet().toArray()[0] ) ));

	}

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.lementInterface#removeAttribute(java.lang.String)
	 */
	public void removeAttribute( String attribute )
	{
		this.attribute=null;
	}

	/**
	 * Called for each change in the attribute set. This method must be
	 * implemented by subelements in order to send events to the graph
	 * listeners.
	 * @param attribute The attribute name that changed.
	 * @param oldValue The old value of the attribute, null if the attribute was
	 *        added.
	 * @param newValue The new value of the attribute, null if the attribute is
	 *        about to be removed.
	 */
	protected abstract void attributeChanged( String attribute, Object oldValue, Object newValue );
}