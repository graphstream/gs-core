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

import org.miv.graphstream.graph.*;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import static org.miv.graphstream.ui2.graphicGraph.GraphPosLengthUtils.*;

/**
 * Graphical node.
 * 
 * <p>
 * A graphic node defines a position (x,y,z), a string label, and a style from the style sheet.
 * </p>
 * 
 * @see GraphicGraph
 */
public class GraphicNode extends GraphicElement implements Node 
{
	/**
	 * The position of the node. In graph units.
	 */
	public float x, y, z;

	/**
	 * Node bounds in graph units and on the 2D screen (set by the rendering engine). This
	 * represents globally the space used on screen. In graph units.
	 */
	public float boundsX, boundsY, boundsW, boundsH;
	
	/**
	 * New graphic node.
	 * @param id The node identifier.
	 * @param x The node X position.
	 * @param y The node Y position.
	 * @param z The node Z position.
	 * @param attributes The node attribute set (can be null).
	 */
	public GraphicNode( GraphicGraph graph, String id, Float x, Float y, Float z, HashMap<String, Object> attributes )
	{
		super( id, graph );
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		if( attributes != null )
			addAttributes( attributes );
	}

	@Override
    protected Selector.Type getSelectorType()
    {
	    return Selector.Type.NODE;
    }
	
	@Override
	public boolean contains( float x, float y, float z )
	{
		return( x > boundsX && y > boundsY && x < ( boundsX + boundsW ) && y < ( boundsY + boundsH ) );
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
    public void move( float x, float y, float z )
    {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    	mygraph.graphChanged = true;
    }

	@Override
	protected void attributeChanged( String attribute, Object oldValue, Object newValue )
	{
		super.attributeChanged( attribute, oldValue, newValue );
		
		if( attribute.equals( "x" ) )
		{
			x = numberAttribute( newValue );
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "y" ) )
		{
			y = numberAttribute( newValue );
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "z" ) )
		{
			z = numberAttribute( newValue );
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "xy" ) || attribute.equals( "xyz" ) )
		{
			float pos[] = nodePosition( this );
			
			x = pos[0];
			y = pos[1];
			z = pos[2];
		}
	}

	/**
	 * Try to convert the object to a float.
	 * @param value The object to convert.
	 * @return The value.
	 */
	protected float numberAttribute( Object value )
	{
		if( value instanceof Number )
		{
			return ((Number)value).floatValue();
		}
		else if( value instanceof CharSequence )
		{
			String xs = ((CharSequence)value).toString();
			
			try
			{
				return Float.parseFloat( xs );
			}
			catch( NumberFormatException e )
			{
			}
		}
		
		return 0;
	}
	
	@Override
	public void setBounds( float x, float y, float w, float h )
	{
		boundsX = x;
		boundsY = y;
		boundsW = w;
		boundsH = h;
	}

	@Override
    protected void removed()
    {
		// NOP
    }

// Node interface.
	
	/**
	 * Not implemented.
	 */
	public Iterator<Node> getBreadthFirstIterator()
    {
		throw new RuntimeException( "not implemented !" );
    }

	/**
	 * Not implemented.
	 */
	public Iterator<Node> getBreadthFirstIterator( boolean directed )
    {
		throw new RuntimeException( "not implemented !" );
    }

	/**
	 * Not implemented.
	 */
	public Iterator<Node> getDepthFirstIterator()
    {
		throw new RuntimeException( "not implemented !" );
    }

	/**
	 * Not implemented.
	 */
	public Iterator<Node> getDepthFirstIterator( boolean directed )
    {
		throw new RuntimeException( "not implemented !" );
    }

	public int getDegree()
    {
	    ArrayList<GraphicEdge> edges = mygraph.connectivity.get( this );
	    
	    if( edges != null )
	    	return edges.size();
	    
	    return 0;
    }

	public Edge getEdge( int i )
    {
		ArrayList<GraphicEdge> edges = mygraph.connectivity.get( this );
		
		if( edges != null && i >= 0 && i < edges.size() )
			return edges.get( i );
		
		return null;
    }

	public Edge getEdgeFrom( String id )
    {
	    return null;
    }

	public Iterator<? extends Edge> getEdgeIterator()
    {
	    ArrayList<GraphicEdge> edges = mygraph.connectivity.get( this );
	    
	    if( edges != null )
	    	return edges.iterator();
	    
	    return null;
    }

	@SuppressWarnings( "unchecked" )
    public Iterator<Edge> iterator()
    {
		return (Iterator<Edge>) getEdgeIterator();
    }

	public Collection<? extends Edge> getEdgeSet()
    {
		return mygraph.connectivity.get( this );
    }

	public Edge getEdgeToward( String id )
    {
		ArrayList<? extends Edge> edges = mygraph.connectivity.get( this );
		
		for( Edge edge: edges )
		{
			if( edge.getOpposite( this ).getId().equals( id ) )
				return edge;
		}
		
		return null;
    }

	public Iterator<? extends Edge> getEnteringEdgeIterator()
    {
		return getEdgeIterator();
    }

	public Collection<? extends Edge> getEnteringEdgeSet()
    {
	    return getEdgeSet();
    }

	public Graph getGraph()
    {
	    return mygraph;
    }

	public String getGraphName()
    {
		throw new RuntimeException( "impossible with GraphicGraph" );
    }

	public String getHost()
    {
		throw new RuntimeException( "impossible with GraphicGraph" );
    }

	public int getInDegree()
    {
	    return getDegree();
    }

	public Iterator<? extends Edge> getLeavingEdgeIterator()
    {
		return getEdgeIterator();
    }

	public Collection<? extends Edge> getLeavingEdgeSet()
    {
	    return getEdgeSet();
    }

	public Iterator<Node> getNeighborNodeIterator()
    {
	    return null;
    }

	public int getOutDegree()
    {
	    return getDegree();
    }

	public boolean hasEdgeFrom( String id )
    {
	    return false;
    }

	public boolean hasEdgeToward( String id )
    {
	    return false;
    }

	public boolean isDistributed()
    {
	    return false;
    }

	public void setGraph( Graph graph )
    {
		throw new RuntimeException( "impossible with GraphicGraph" );
    }

	public void setGraphName( String newHost )
    {
		throw new RuntimeException( "impossible with GraphicGraph" );
    }

	public void setHost( String newHost )
    {
		throw new RuntimeException( "impossible with GraphicGraph" );
    }
}