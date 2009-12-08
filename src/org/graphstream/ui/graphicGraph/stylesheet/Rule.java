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

package org.graphstream.ui.graphicGraph.stylesheet;

/**
 * Style application rule.
 *
 * <p>
 * A rule is made of a selector and values. The selector identifies the element(s) this rule applies
 * to, and the values are styles to apply to the matched elements.
 * </p>
 */
public class Rule
{
// Attributes
	
	/**
	 * The match.
	 */
	public Selector selector;

	/**
	 * The style.
	 */
	public Style style;
	
// Constructors
	
	protected Rule()
	{
	}
	
	/**
	 * New rule with a matcher.
	 * @param selector The rule selector.
	 */
	public Rule( Selector selector )
	{
		this.selector = selector;
	}
	
	public Rule( Selector selector, Rule parent )
	{
		this.selector = selector;
		this.style    = new Style( parent );
	}
	
	/**
	 * This rule style.
	 * @return The rule style.
	 */
	public Style getStyle()
	{
		return style;
	}
	
	/**
	 * True if this rule selector match the given identifier.
	 * @param identifier The identifier to test for the match.
	 * @return True if matching.
	 */
	public boolean matchId( String identifier )
	{
		String ident = selector.getId();
		
		if( ident != null )
			return ident.equals( identifier );
		
		return false;
	}
	
	/**
	 * Change the style.
	 * @param style A style specification.
	 */
	public void setStyle( Style style )
	{
		this.style = style;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append( "\t" );
		builder.append( selector.type );
		
		if( selector.id != null )
			builder.append( "."+selector.id );
		if( selector.clazz != null )
			builder.append( "#"+selector.clazz );
		
		builder.append( " :\n" );
		builder.append( style.toString() );
		
		return builder.toString();
	}
}