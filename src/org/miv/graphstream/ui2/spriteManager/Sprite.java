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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.miv.graphstream.graph.Element;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Style;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Values;

/**
 * A gentle little sprite.
 * 
 * <p>Sprite objects allow to add data representations in a graphic display of a graph. A sprite
 * is a graphical representation that can float anywhere in the graph drawing surface, or be
 * "attached" to nodes or edges. When attached to an edge, a sprite can be positioned easily at
 * any point along the edge (or perpendicular to it). When attached to a node, a sprite "orbits"
 * around the node at any given radius and angle around it.</p>
 * 
 * <p>Sprites can have many shapes. Most of the CSS nodes shapes are available for sprites, but
 * more are possible. Some shapes follow the form of the element (node or edge) they are attched
 * to.</p>
 * 
 * <p>Sprites can be moved and animated easily along edges, around nodes, or any where on the graph
 * surface. Their shape can change. Some sprites allows to draw pie charts or statistics,
 * or images.</p>
 * 
 * <p>Sprites are not part of a graph so to speak. Furthermore they make sense only when a graph
 * is displayed with a viewer that supports sprites. Therefore they are handled by a
 * {@link SpriteManager} which is always associated to a graph and is in charge of handling the
 * whole set of sprites, creating them, enumerating them, and destroying them.</p>
 *
 * <p>
 * Implementation note : sprites do not exist ! In fact the sprite class only handles a set of
 * attributes that are stored in the graph (the one associated with the sprite manager that
 * created the sprite). These special attributes are handled for you by the sprite class. This
 * technique allows to pass sprites informations through the I/O system of GraphStream. Indeed
 * sprites appearing in a graph can therefore be stored in files and retrieved if the graph file
 * format supports attributes. If this is a dynamic graph format, like DGS, the whole sprite
 * history is remembered : when it moved, when it changed, etc.
 * </p>
 * @see SpriteManager
 */
public class Sprite implements Element
{
// Attribute
	
	/**
	 * The sprite unique identifier.
	 */
	protected String id;

	/**
	 * The identifier prefixed by "ui.sprite.".
	 */
	protected String completeId;
	
	/**
	 * The boss.
	 */
	protected SpriteManager manager;

	/**
	 * Current sprite position.
	 */
	protected Values position;

	/**
	 * The element this sprite is attached to (or null).
	 */
	protected Element attachment;
	
// Construction

	/**
	 * New sprite with a given identifier.
	 * 
	 * You cannot build sprites yourself, they are created by the sprite manager.
	 */
	protected Sprite( String id, SpriteManager manager )
	{
		this.id         = id;
		this.completeId = String.format( "ui.sprite.%s", id );
		this.manager    = manager;

		if( ! manager.graph.hasAttribute( completeId ) )
		{
			manager.graph.addAttribute( completeId, 0, 0, 0, Style.Units.GU );
			position = new Values( Style.Units.GU, 0f, 0f, 0f );
		}
		else
		{
			position = SpriteManager.getPositionValue( manager.graph.getAttribute( completeId ) );
		}
	}
	
	/**
	 * Called by the manager when the sprite is removed.
	 */
	protected void removed()
	{
		manager.graph.removeAttribute( completeId );
		
		String start = String.format( "%s.", completeId );

		if( attached() )
			detach();
	
		ArrayList<String> keys = new ArrayList<String>();
		
		for( String key: manager.graph.getAttributeKeySet() )
		{
			if( key.startsWith( start ) )
				keys.add( key );
		}
			
		for( String key: keys )
			manager.graph.removeAttribute( key );
	}
	
// Access
	
	/**
	 * The element the sprite is attached to or null if the sprite is not attached.
	 * @return An element the sprite is attached to or null.
	 */
	public Element getAttachment()
	{
		return attachment;
	}
	
	/**
	 * True if attached to an edge or node.
	 * @return False if not attached.
	 */
	public boolean attached()
	{
		return( attachment != null );
	}
	
// Command
	
	/**
	 * Attach the sprite to a node with the given identifier. If needed the sprite is first
	 * detached. If the given node identifier does not exist, the sprite stays in detached
	 * state.
	 * @param id Identifier of the node to attach to.
	 */
	public void attachToNode( String id )
	{
		if( attachment != null )
			detach();
		
		attachment = manager.graph.getNode( id );
		
		if( attachment != null )
			attachment.addAttribute( completeId );
	}
	
	/**
	 * Attach the sprite to an edge with the given identifier. If needed the sprite is first
	 * detached. If the given edge identifier does not exist, the sprite stays in detached
	 * state.
	 * @param id Identifier of the edge to attach to.
	 */
	public void attachToEdge( String id )
	{
		if( attachment != null )
			detach();
		
		attachment = manager.graph.getEdge( id );
		
		if( attachment != null )
			attachment.addAttribute( completeId );		
	}
	
	/**
	 * Detach the sprite from the element it is attached to (if any).
	 */
	public void detach()
	{
		if( attachment != null )
		{
			attachment.removeAttribute( completeId );
			attachment = null;
		}
	}
	
	public void setPosition( float percent )
	{
		position.setValue( 0, percent );
		manager.graph.setAttribute( completeId, position );
	}
	
	public void setPosition( float x, float y, float z )
	{
		position.setValue( 0, x );
		position.setValue( 1, y );
		position.setValue( 2, z );
		manager.graph.setAttribute( completeId, position );
	}
	
	public void setPosition( Style.Units units, float x, float y, float z )
	{
		position.setValue( 0, x );
		position.setValue( 1, y );
		position.setValue( 2, z );
		position.setUnits( units );
		manager.graph.setAttribute( completeId, position );
	}
	
// Access (Element)

	public String getId()
    {
	    return id;
    }

	public CharSequence getLabel( String key )
    {
		return manager.graph.getLabel( String.format( "%s.%s", completeId, key ) );
    }
	
	public Object getAttribute( String key )
    {
		return manager.graph.getAttribute( String.format( "%s.%s", completeId, key ) );
    }

	public Object getAttribute( String key, Class<?> clazz )
    {
		return manager.graph.getAttribute( String.format( "%s.%s", completeId, key ), clazz );
    }

	/**
	 * Quite expensive operation !.
	 */
	public int getAttributeCount()
    {
		String start = String.format( "%s.", completeId );
		int    count = 0;
		
		for( String key: manager.graph.getAttributeKeySet() )
		{
			if( key.startsWith( start ) )
				count++;
		}
		
		return count;
    }

	public Iterator<String> getAttributeKeyIterator()
    {
		throw new RuntimeException( "not implemented" );
    }

	public Iterable<String> getAttributeKeySet()
    {
		throw new RuntimeException( "not implemented" );
    }

	public Map<String,Object> getAttributeMap()
    {
		throw new RuntimeException( "not implemented" );
    }

	public Object getFirstAttributeOf( String ... keys )
    {
		String completeKeys[] = new String[keys.length];
		int    i = 0;

		for( String key: keys )
		{
			completeKeys[i] = String.format( "%s.%s", completeId, key );
			i++;
		}
		
		return manager.graph.getFirstAttributeOf( completeKeys );
    }

	public Object getFirstAttributeOf( Class<?> clazz, String ... keys )
    {
		String completeKeys[] = new String[keys.length];
		int    i = 0;

		for( String key: keys )
		{
			completeKeys[i] = String.format( "%s.%s", completeId, key );
			i++;
		}
		
		return manager.graph.getFirstAttributeOf( clazz, completeKeys );
    }

	public Object[] getArray( String key )
    {
		return manager.graph.getArray( String.format( "%s.%s", completeId, key ) );
    }

	public HashMap<?,?> getHash( String key )
    {
		return manager.graph.getHash( String.format( "%s.%s", completeId, key ) );
    }

	public double getNumber( String key )
    {
		return manager.graph.getNumber( String.format( "%s.%s", completeId, key ) );
    }

	public ArrayList<? extends Number> getVector( String key )
    {
		return manager.graph.getVector( String.format( "%s.%s", completeId, key ) );
    }

	public boolean hasAttribute( String key )
    {
		return manager.graph.hasAttribute( String.format( "%s.%s", completeId, key ) );
    }

	public boolean hasArray( String key )
    {
		return manager.graph.hasArray( String.format( "%s.%s", completeId, key ) );
    }

	public boolean hasAttribute( String key, Class<?> clazz )
    {
		return manager.graph.hasAttribute( String.format( "%s.%s", completeId, key ), clazz );
    }

	public boolean hasHash( String key )
    {
		return manager.graph.hasHash( String.format( "%s.%s", completeId, key ) );
    }

	public boolean hasLabel( String key )
    {
		return manager.graph.hasLabel( String.format( "%s.%s", completeId, key ) );
    }

	public boolean hasNumber( String key )
    {
		return manager.graph.hasNumber( String.format( "%s.%s", completeId, key ) );
    }

	public boolean hasVector( String key )
    {
		return manager.graph.hasVector( String.format( "%s.%s", completeId, key ) );
    }
	
// Commands (Element)

	public void addAttribute( String attribute, Object ... values )
    {
		manager.graph.addAttribute( String.format( "%s.%s", completeId, attribute ), values ); 
    }

	public void addAttributes( Map<String,Object> attributes )
    {
		for( String key: attributes.keySet() )
			manager.graph.addAttribute( String.format( "%s.%s", completeId, key ), attributes.get( key ) ); 
    }

	public void setAttribute( String attribute, Object ... values )
    {
		manager.graph.setAttribute( String.format( "%s.%s", completeId, attribute ), values ); 
    }

	public void changeAttribute( String attribute, Object ... values )
    {
		manager.graph.changeAttribute( String.format( "%s.%s", completeId, attribute ), values ); 	    
    }

	public void clearAttributes()
    {
		String start = String.format( "%s.", completeId );
		ArrayList<String> keys = new ArrayList<String>();
		
		for( String key: manager.graph.getAttributeKeySet() )
		{
			if( key.startsWith( start ) )
				keys.add( key );
		}

		for( String key: keys )
			manager.graph.removeAttribute( key );
    }

	public void removeAttribute( String attribute )
    {
		manager.graph.removeAttribute( String.format( "%s.%s", completeId, attribute ) ); 	    
    }
}