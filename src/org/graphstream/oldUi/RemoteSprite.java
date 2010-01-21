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

package org.graphstream.oldUi;

import org.graphstream.oldUi.graphicGraph.stylesheet.Style;

/**
 * Sprite implementation that can be used with the {@link org.graphstream.oldUi.GraphViewerRemote}.
 */
public class RemoteSprite implements Sprite
{
// Attributes	

	/**
	 * The sprite (unique) identifier.
	 */
	protected String id;
	
	/**
	 * The remote.
	 */
	protected GraphViewerRemote remote;
	
// Constructors
	
	/**
	 * New sprite.
	 * @param id The sprite unique identifier.
	 */
	public RemoteSprite( String id, GraphViewerRemote remote )
	{
		this.id = id;
		this.remote = remote;
		remote.addSpriteNoInstance( id );
	}
	
// Access
	
	public String getId()
	{
		return id;
	}
	
	public GraphViewerRemote getViewerRemote()
	{
		return remote;
	}
	
// Commands

	public void attachToNode( String nodeId )
	{
		remote.attachSpriteToNode( id, nodeId );
	}

	public void attachToEdge( String edgeId )
	{
		remote.attachSpriteToEdge( id, edgeId );
	}

	public void detach()
	{
		remote.detachSprite( id );
	}

	public void position( float percent )
	{
		remote.positionSprite( id, percent );
	}

	public void position( float x, float y, float z, Style.Units units )
	{
		remote.positionSprite( id, x, y, z, units );
	}

	public void position( float x, float y, float z )
	{
		remote.positionSprite( id, x, y, z );
	}
	
	public void addAttribute( String attribute, Object ... values )
	{
		Object value;
		
		if( values.length == 0 )
		     value = true;
		else if( values.length == 1 )
		     value = values[0];
		else value = values;
		
		remote.addSpriteAttribute( id, attribute, value );
	}
	
	public void setAttribute( String attribute, Object ... values )
	{
		addAttribute( attribute, values );
	}
	
	public void changeAttribute( String attribute, Object ... values )
	{
		addAttribute( attribute, values );
	}
	
	public void removeAttribute( String attribute )
	{
		remote.removeSpriteAttribute( id, attribute );
	}
}