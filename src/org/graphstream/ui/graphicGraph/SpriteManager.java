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

package org.graphstream.ui.graphicGraph;

import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.ui.graphicGraph.stylesheet.Style;

/**
 * A set of sprite.
 */
public class SpriteManager
{
// Attributes

	/**
	 * Sprites.
	 */
	protected HashMap<String,GraphicSprite> sprites = new HashMap<String,GraphicSprite>();
	
	/**
	 * The graph.
	 */
	protected GraphicGraph graph;
	
// Constructors
	
	/**
	 * New empty sprite manager.
	 */
	public SpriteManager( GraphicGraph graph )
	{
		this.graph = graph;
	}
	
// Access
	
	/**
	 * Iterator on the set of sprites.
	 */
	public Iterator<GraphicSprite> getSpriteIterator()
	{
		return sprites.values().iterator();
	}
	
	/**
	 * Number of sprites in the manager.
	 * @return The sprite count.
	 */
	public int getSpriteCount()
	{
		return sprites.size();
	}
	
	/**
	 * Find the first sprite that is at the given coordinates. If there are several such sprites,
	 * only one is selected. The coordinates are given in 2D (as the screen is 2D) and if the
	 * graph is in 3D the z coordinate is ignored.
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @return The first sprite that match the coordinates, or null if no sprite match the coordinates.
	 */
	public GraphicSprite findSprite( float x, float y )
	{
		for( GraphicSprite sprite: sprites.values() )
		{
			if( sprite.contains( x, y ) )
				return sprite;
		}
		
		return null;
	}
	
// Commands
	
	/**
	 * Ask each sprite to check its style, as the style sheet changed.
	 */
	public void checkStyles()
	{
		for( GraphicSprite sprite: sprites.values() )
			sprite.checkStyle();
	}
	
// Sprite API
	
	/**
	 * Declare a new sprite.
	 * @param id The unique identifier of the sprite.
	 */
	public void addSprite( String id )
	{
		GraphicSprite old = sprites.put( id, new GraphicSprite( id, graph ) );
		
		if( old != null )
			sprites.put( id, old );
	}
	
	/**
	 * Remove a sprite.
	 * @param id The unique identifier of the sprite.
	 */
	public void removeSprite( String id )
	{
		GraphicSprite sprite = sprites.remove( id );
		
		if( sprite != null )
		{
			sprite.removed();
			graph.graphChanged = true;
		}
	}
	
	/**
	 * Attach a sprite to a node, all positions will be expressed choosing the node centre
	 * as origin. Therefore the position of the sprite can be seen as an offset from the
	 * centre of the node.
	 * @param id The sprite identifier.
	 * @param nodeId The node identifier.
	 */
	public void attachSpriteToNode( String id, String nodeId )
	{
		GraphicNode node   = graph.getNode( nodeId );
		GraphicSprite      sprite = sprites.get( id );
		
		if( node != null && sprite != null )
		{
			sprite.attachToNode( node );
		}
	}
	
	/**
	 * Attach a sprite to an edge, all positions will be expressed as a position on the edge.
	 * The position of the sprite will be seen as an offset from the source node along the
	 * edge. The sprite will remain on the edge. If {@link #positionSprite(String, float)}
	 * is used the value is a number between 0 and 1 that allows to express an offset from
	 * the source node along the edge. 0 means on the source node, 1 on the target node
	 * and values in between a percent of the distance between the two nodes. If
	 * {@link #positionSprite(String, float, float, float)} is used, the first value is
	 * the same as for {@link #positionSprite(String, float)}, the two other values allows
	 * to offset the sprite on a plane whose normal vector is the edge.
	 * @param id The sprite id.
	 * @param edgeId The edge id.
	 */
	public void attachSpriteToEdge( String id, String edgeId )
	{
		GraphicEdge   edge   = graph.getEdge( edgeId );
		GraphicSprite sprite = sprites.get( id );
		
		if( edge != null && sprite != null )
		{
			sprite.attachToEdge( edge );
		}		
	}
	
	/**
	 * Detach a sprite from the node or edge it was attached to.
	 * @param id The sprite identifier.
	 */
	public void detachSprite( String id )
	{
		GraphicSprite sprite = sprites.get( id );
		
		if( sprite != null )
			sprite.detach();
	}
	
	/**
	 * Position a sprite along an edge. This method is dedicated to the positioning of
	 * a sprite along an edge. The value is a percent of the distance between the source
	 * and target node. 0 means the sprite is on the source node and 1 means the sprite
	 * is on the target node. In between values indicate a percent of the distance.
	 * @param id The sprite identifier.
	 * @param percent The percent of the distance between the two nodes of the edge.
	 */
	public void positionSprite( String id, float percent )
	{
		GraphicSprite sprite = sprites.get( id );
		
		if( sprite != null )
			sprite.setPosition( percent );
	}
	
	/**
	 * Position a sprite in the graph, node or edge space. If the sprite is not attached
	 * the coordinates are in graph space. In this case, the sprite is positioned somewhere
	 * in the space of the graph, like nodes and edges. If the sprite is attached to a node,
	 * the coordinates express and offset from the node centre. If the sprite is attached
	 * to an edge, the first coordinate must be in [0..1] and indicates a percent of the
	 * distance between the source and target node of the edge. If the two other coordinates
	 * are zero, the sprite will remain along the edge. Else they express an offset on a
	 * plane that is perpendicular to the edge. That is the edge is the normal vector of
	 * this plane.
	 * @param id The sprite identifier.
	 * @param x First coordinate.
	 * @param y Second coordinate.
	 * @param z Third coordinate.
	 * @param units The units used to measure lengths and radii.
	 */
	public void positionSprite( String id, float x, float y, float z, Style.Units units )
	{
		GraphicSprite sprite = sprites.get( id );
		
		if( sprite != null )
			sprite.setPosition( x, y, z, units );
	}
	
	public void positionSprite( String id, float x, float y, float z )
	{
		GraphicSprite sprite = sprites.get( id );
		
		if( sprite != null )
			sprite.setPosition( x, y, z, null );
	}

	/**
	 * Add or change an attribute on a sprite.
	 * @param id The sprite identifier.
	 * @param attribute The attribute name.
	 * @param value The attribute value.
	 */
	public void addSpriteAttribute( String id, String attribute, Object value )
	{
		GraphicSprite sprite = sprites.get( id );
		
		if( sprite != null )
			sprite.addAttribute( attribute, value );
	}
	
	/**
	 * Remove an attribute from a sprite.
	 * @param id The sprite identifier.
	 * @param attribute The attribute name.
	 */
	public void removeSpriteAttribute( String id, String attribute )
	{
		GraphicSprite sprite = sprites.get( id );
		
		if( sprite != null )
			sprite.removeAttribute( attribute );
	}
}