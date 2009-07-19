/*
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.miv.graphstream.ui2.graphicGraph;

import java.util.Iterator;

import org.miv.graphstream.graph.GraphAttributesListener;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Selector;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Style;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Values;

/**
 * A small gentle sprite.
 */
public class GraphicSprite extends GraphicElement
{
// Attributes
	
	/**
	 * The node this sprite is attached to.
	 */
	protected GraphicNode node;
	
	/**
	 * The edge this sprite is attached to.
	 */
	protected GraphicEdge edge;

	/**
	 * Sprite position, in graph units.
	 */
	public float x, y, z;
	
	/**
	 * Units for lengths and radii.
	 */
	public Style.Units units = Style.Units.GU;
	
	/**
	 * Bounds of the sprite (for nodeSelection). These are set by the renderer according to the node
	 * shape. THis is in graph units.
	 */
	public float boundsX, boundsY, boundsZ, boundsW, boundsH, boundsD;
	
// Constructors
	
	/**
	 * New sprite.
	 * @param id The sprite unique identifier.
	 * @param graph The graph containing this sprite.
	 */
	public GraphicSprite( String id, GraphicGraph graph )
	{
		super( id, graph );
		
		// Get the position of a random node.
		
		if( graph.getNodeCount() > 0 )
		{
			Iterator<? extends Node> nodes = graph.getNodeIterator();
		
			GraphicNode node = (GraphicNode) nodes.next();
			
			x = node.x;
			y = node.y;
			z = node.z;
		}
		
		String myPrefix = String.format( "ui.sprite.%s", id );
		
		if( mygraph.getAttribute( myPrefix ) == null )
			mygraph.addAttribute( myPrefix, x, y, z );
	}
	
// Access
	
	/**
	 * The node this sprite is attached to or null if not attached to an edge.
	 * @return A graphic node.
	 */
	public GraphicNode getNodeAttachment()
	{
		return node;
	}
	
	/**
	 * The edge this sprite is attached to or null if not attached to an edge.
	 * @return A graphic edge.
	 */
	public GraphicEdge getEdgeAttachment()
	{
		return edge;
	}
	
	/**
	 * Return the graphic object this sprite is attached to or null if not attached.
	 * @return A graphic object or null if no attachment.
	 */
	public GraphicElement getAttachment()
	{
		GraphicNode n = getNodeAttachment();
		
		if( n != null )
			return n;
		
		return getEdgeAttachment();
	}

	@Override
    protected Selector.Type getSelectorType()
    {
	    return Selector.Type.SPRITE;
    }
	
	@Override
	public float getX()
	{
		return x; 
	}

	@Override
	public float getY()
	{
		return y;
	}
	
	@Override
	public float getZ()
	{
		return z;
	}
	
	@Override
	public boolean contains( float x, float y, float z )
	{
		return( x > boundsX && y > boundsY && x < ( boundsX + boundsW ) && y < ( boundsY + boundsH ) );
	}
	
// Commands

	@Override
    public void move( float x, float y, float z )
    {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    	mygraph.graphChanged = true;
    }
	
	@Override
	public void setBounds( float x, float y, float w, float h )
	{
		boundsX = x;
		boundsY = y;
		boundsW = w;
		boundsH = h;
	}
    
	/**
	 * Attach this sprite to the given node.
	 * @param node A graphic node.
	 */
	public void attachToNode( GraphicNode node )
	{
		this.edge = null;
		this.node = node;
		
		String prefix = String.format( "ui.sprite.%s", getId() );
		
		if( this.node.getAttribute( prefix ) == null )
			this.node.addAttribute( prefix );
		
		mygraph.graphChanged = true;
	}
	
	/**
	 * Attach this sprite to the given edge.
	 * @param edge A graphic edge.
	 */
	public void attachToEdge( GraphicEdge edge )
	{
		this.node = null;
		this.edge = edge;
		
		String prefix = String.format( "ui.sprite.%s", getId() );
		
		if( this.edge.getAttribute( prefix ) == null )
			this.edge.addAttribute( prefix );
		
		mygraph.graphChanged = true;
	}
	
	/**
	 * Detach this sprite from the edge or node it was attached to.
	 */
	public void detach()
	{
		String prefix = String.format( "ui.sprite.%s", getId() );
		
		if( this.node != null )
			this.node.removeAttribute( prefix );
		else if( this.edge != null )
			this.edge.removeAttribute( prefix );
		
		this.edge = null;
		this.node = null;
		mygraph.graphChanged = true;
	}
	
	/**
	 * Reposition this sprite.
	 * @param value The coordinate.
	 */
	public void setPosition( float value )
	{
		if( edge != null )
		{
			if( value < 0 ) value = 0;
			else if( value > 1 ) value = 1;
		}
		
		x = value;
		y = 0;
		z = 0;
		mygraph.graphChanged = true;
	}
	
	/**
	 * Reposition this sprite.
	 * @param x First coordinate.
	 * @param y Second coordinate.
	 * @param z Third coordinate.
	 * @param units The units to use for lengths and radii, null means "unchanged".
	 */
	public void setPosition( float x, float y, float z, Style.Units units )
	{
		if( node != null )
		{
			y = checkAngle( y );
			z = checkAngle( z );
		}
		else if( edge != null )
		{
			if( x < 0 ) x = 0;
			else if( x > 1 ) x = 1;
		}
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		if( units != null )
			this.units = units;

		mygraph.graphChanged = true;
	}
	
	public void setPosition( Values values )
	{
		float x = 0;
		float y = 0;
		float z = 0;
		
		if( values.getValueCount() > 0 ) x = values.getValue( 0 ); 
		if( values.getValueCount() > 1 ) x = values.getValue( 1 ); 
		if( values.getValueCount() > 2 ) x = values.getValue( 2 ); 
		
		setPosition( x, y, z, values.units );
	}
	
	protected float checkAngle( double angle )
	{
		if( angle > Math.PI*2 )
			angle = angle % (Math.PI*2);
		else if( angle < 0 )
			angle = (Math.PI*2) - ( angle % (Math.PI*2) );
		
		return (float) angle;
	}
	
	@Override
    protected void attributeChanged( String attribute, Object oldValue, Object newValue )
    {
		super.attributeChanged( attribute, oldValue, newValue );

		String completeAttr = String.format( "ui.sprite.%s.%s", getId(), attribute );
		
		if( oldValue == null )		// ADD
		{
			Object o = mygraph.getAttribute( completeAttr );
			
			if( o == null || ( ! o.equals( newValue ) ) )
			{
				for( GraphAttributesListener listener: mygraph.attrListeners )
					listener.graphAttributeAdded( mygraph.getId(), completeAttr, newValue );
			}
		}
		else if( newValue == null )	// REMOVE
		{
			for( GraphAttributesListener listener: mygraph.attrListeners )
				listener.graphAttributeRemoved( mygraph.getId(), completeAttr );			
		}
		else						// CHANGE
		{
			Object o = mygraph.getAttribute( completeAttr );

			if( o == null || ( ! o.equals( newValue ) ) )
			{
				for( GraphAttributesListener listener: mygraph.attrListeners )
					listener.graphAttributeChanged( mygraph.getId(), completeAttr, oldValue, newValue );
			}
		}
    }

	@Override
    protected void removed()
    {
    }
}