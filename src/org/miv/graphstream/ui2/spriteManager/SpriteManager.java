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
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.miv.graphstream.ui2.spriteManager;

import java.util.HashMap;

import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Style;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Values;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.StyleConstants.Units;

public class SpriteManager
{
// Attribute
	
	/**
	 * The graph to add sprites to.
	 */
	protected Graph graph;
	
	/**
	 * The set of sprites.
	 */
	protected HashMap<String,Sprite> sprites;
	
// Construction
	
	/**
	 * Create a new manager for sprite and bind it to the given graph. If the graph already contains
	 * attributes describing sprites, the manager is automatically filled with the existing
	 * sprites. Only one manager can be bound to a graph at a time.
	 * @param graph The graph to associate with this manager;
	 */
	public SpriteManager( Graph graph )
	{
		this.graph    = graph;
		
		lookForAnotherManager();
		graph.addAttribute( "ui.SpriteMagager", this );
		lookForExistingSprites();
	}
	
	protected void lookForAnotherManager()
	{
		Object o = graph.getAttribute( "ui.SpriteManager" );
		
		if( o != this )
			throw new RuntimeException( "Only one sprite manager is allowed at a time one a graph." );
	}
	
	protected void lookForExistingSprites()
	{
		for( String attr: graph.getAttributeKeySet() )
		{
			if( attr.startsWith( "ui.sprite." ) )
			{
				String id = attr.substring( 10 );
				
				if( id.indexOf( '.' ) < 0 )
				{
					addSprite( id );
				}
			}
		}
	}

// Access
	
	/**
	 * Sprite corresponding to the given identifier or null if no sprite is associated with the
	 * given identifier.
	 * @param identifier The sprite identifier.
	 */
	public Sprite getSprite( String identifier )
	{
		return sprites.get( identifier );
	}
	
// Command

	/**
	 * Add a sprite with the given identifier. If the sprite already exists, nothing is done.
	 * @param identifier The identifier of the new sprite to add.
	 * @return The created sprite.
	 */
	public Sprite addSprite( String identifier )
	{
		Sprite sprite = sprites.get( identifier ); 
		
		if( sprite == null )
		{
			sprite = new Sprite( identifier, this );
			sprites.put( identifier, sprite );
		}
		
		return sprite;
	}
	
	/**
	 * Remove a sprite knowing its identifier. If no such sprite exists, this fails silently.
	 * @param identifier The identifier of the sprite to remove.
	 */
	public void removeSprite( String identifier )
	{
		Sprite sprite = sprites.get( identifier ); 
		
		if( sprite != null )
		{
			sprite.removed();
			sprites.remove( identifier );
		}		
	}
	
// Utility
	
	protected static Values getPositionValue( Object value )
	{
		if( value instanceof Object[] )
		{
			Object[] values = (Object[]) value;
			
			if( values.length == 4 )
			{
				if( values[0] instanceof Number && values[1] instanceof Number
				 && values[2] instanceof Number && values[3] instanceof Style.Units )
				{
					return new Values(
							(Style.Units)values[3],
							((Number)values[0]).floatValue(),
							((Number)values[1]).floatValue(),
							((Number)values[2]).floatValue() );					
				}
				else
				{
					System.err.printf( "GraphicGraph : cannot parse values[4] for sprite position.%n" );
				}
			}
			else if( values.length == 3 )
			{
				if( values[0] instanceof Number && values[1] instanceof Number
				 && values[2] instanceof Number )
				{
					return new Values(
						Units.GU,
						((Number)values[0]).floatValue(),
						((Number)values[1]).floatValue(),
						((Number)values[2]).floatValue() );
				}
				else
				{
					System.err.printf( "GraphicGraph : cannot parse values[3] for sprite position.%n" );
				}
			}
			else if( values.length == 1 )
			{
				if( values[0] instanceof Number )
				{
					return new Values( Units.GU, ((Number)value).floatValue() );
				}
				else
				{
					System.err.printf( "GraphicGraph : sprite position percent is not a number.%n" );
				}
			}
			else
			{
				System.err.printf( "GraphicGraph : cannot transform value '%s' (length=%d) into a position%n", values, values.length );
			}
		}
		else if( value instanceof Number )
		{
			return new Values( Units.GU, ((Number)value).floatValue() );
		}
		else
		{
			System.err.printf( "GraphicGraph : cannot place sprite with posiiton '%s'%n", value );
		}
		
		return null;
	}
}