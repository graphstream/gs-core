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

package org.miv.graphstream.ui2.graphicGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.miv.graphstream.graph.Element;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Rule;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Selector;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Style;

/**
 * A group of graph elements that share the same style.
 * 
 * @author Antoine Dutot
 */
public class StyleGroup extends Style
{
// Attribute

	/**
	 * The group unique identifier.
	 */
	protected String id;
	
	/**
	 * The set of style rules.
	 */
	protected ArrayList<Rule> rules = new ArrayList<Rule>();

	/**
	 * Graph elements of this group.
	 */
	protected HashMap<String,Element> elements = new HashMap<String,Element>();
	
	/**
	 * The events actually occurring.
	 */
	protected StyleGroupSet.EventSet eventSet;

	/**
	 * True if this style contains at least one style property whose value is dynamic (set
	 * according to some other attribute of the element).
	 */
	protected boolean isDynamic = false;
	
// Construction
	
	/**
	 * New style group for a first graph element and the set of style rules that matches it. More
	 * graph elements can be added later.
	 * @param identifier The unique group identifier (see {@link org.miv.graphstream.ui2.graphicGraph.stylesheet.StyleSheet#getStyleGroupIdFor(Element, ArrayList)}).
	 * @param rules The set of style rules for the style group (see {@link org.miv.graphstream.ui2.graphicGraph.stylesheet.StyleSheet#getRulesFor(Element)}).
	 * @param firstElement The first element to construct the group.
	 */
	public StyleGroup( String identifier, Collection<Rule> rules, Element firstElement, StyleGroupSet.EventSet eventSet )
	{
		this.id = identifier;
		this.rules.addAll( rules );
		this.elements.put( firstElement.getId(), firstElement );
		this.values = null;	// To avoid consume memory since this style will not store anything.
		this.eventSet = eventSet;
		
		for( Rule rule: rules )
			rule.addGroup( identifier );
	}
	
// Access
	
	/**
	 * The group unique identifier.
	 * @return A style group identifier.
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * Type of graph element concerned by this style (node, edge, sprite, graph).
	 * @return The type of the style group elements.
	 */
	public Selector.Type getType()
	{
		return rules.get(0).selector.type;
	}
	
	/**
	 * True if at least one of the style properties is dynamic (set according to an attribute of
	 * the element to draw).
	 * @return True if one property is dynamic.
	 */
	public boolean isDynamic()
	{
		return isDynamic;
	}
	
	/**
	 * Get the value of a given property.
	 * 
	 * This is a redefinition of the method in {@link Style} to consider the
	 * fact a style group aggregates several style rules. 
	 * 
	 * @param property The style property the value is searched for.
	 */
	@Override
	public Object getValue( String property, String...events )
	{
		int n = rules.size();
	
		if( events == null || events.length == 0 )
			events = eventSet.events;
		
		for( int i=1; i<n; i++ )
		{
			Style style = rules.get(i).getStyle();
			
			if( style.hasValue( property, events ) )
				return style.getValue( property, events );
		}
		
		return rules.get(0).getStyle().getValue( property, events );
	}
	
	/**
	 * True if there are no elements in the group.
	 * @return True if the group is empty of elements.
	 */
	public boolean isEmpty()
	{
		return elements.isEmpty();
	}
	
	/**
	 * True if the group contains the element whose identifier is given.
	 * @param elementId The element to search.
	 * @return true if the element is in the group.
	 */
	public boolean contains( String elementId )
	{
		return elements.containsKey( elementId );
	}
	
	/**
	 * True if the group contains the element given.
	 * @param element The element to search.
	 * @return true if the element is in the group.
	 */
	public boolean contains( Element element )
	{
		return elements.containsKey( element.getId() );
	}
	
	/**
	 * Return an element of the group, knowing its identifier.
	 * @param id The searched element identifier.
	 * @return The element corresponding to the identifier or null if not found.
	 */
	public Element getElement( String id )
	{
		return elements.get( id );
	}
	
	/**
	 * The number of elements of the group.
	 * @return The element count.
	 */
	public int getElementCount()
	{
		return elements.size();
	}
	
	/**
	 * Iterator on the set of graph elements of this group. 
	 * @return The elements iterator.
	 */
	public Iterator<? extends Element> getElementIterator()
	{
		return elements.values().iterator();
	}

// Command

	/**
	 * Add a new graph element to the group.
	 * @param element The new graph element to add.
	 */
	public void addElement( Element element )
	{
		elements.put( element.getId(), element );
	}
	
	/**
	 * Remove a graph element from the group.
	 * @param element The element to remove.
	 * @return The removed element, or null if the element was not found.
	 */
	public Element removeElement( Element element )
	{
		return elements.remove( element.getId() );
	}
	
	/**
	 * Remove all graph elements of this group, and remove this group from the group list of each
	 * style rule.
	 */
	public void release()
	{
		for( Rule rule: rules )
			rule.removeGroup( id );
		
		elements.clear();
	}
		
	/**
	 * Redefinition of the {@link Style} to forbid changing the values.
	 */
	@Override
	public void setValue( String property, Object value )
	{
		throw new RuntimeException( "you cannot change the values of a style group." );
	}

	@Override
	public String toString()
	{
		return toString( -1 );
	}

	@Override
	public String toString( int level )
	{
		StringBuilder builder = new StringBuilder();
		String        prefix  = "";
		String        sprefix = "    ";
		
		for( int i=0; i<level; i++ )
			prefix += sprefix;
		
		builder.append( String.format( "%s%s%n", prefix, id ) );
		builder.append( String.format( "%s%sContains : ", prefix, sprefix ) );
		
		for( Element element: elements.values() )
		{
			builder.append( String.format( "%s ", element.getId() ) );
		}
		
		builder.append( String.format( "%n%s%sStyle : ", prefix, sprefix ) );
		
		for( Rule rule: rules )
		{
			builder.append( String.format( "%s ", rule.selector.toString() ) );
		}
		
		builder.append( String.format( "%n" ) );
		
		return builder.toString();
	}
}