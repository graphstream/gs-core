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

import org.graphstream.graph.*;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.graphstream.ui.graphicGraph.stylesheet.Selector.Type;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Graphical edge.
 * 
 * <p>
 * The graphic edge defines its source and target node as well as a direction, a string label
 * and a style from the style sheet.
 * </p>
 * 
 * @see GraphicGraph
 */
public class GraphicEdge extends GraphicElement implements Edge
{
// Attributes
	
	/**
	 * The first node.
	 */
	public GraphicNode from;

	/**
	 * The second node.
	 */
	public GraphicNode to;

	/**
	 * Is the edge directed ?.
	 */
	public boolean directed;
	
	/**
	 * In case of a multi-graph this is the index of the node between to and from.
	 */
	public int multi;
	
	/**
	 * If non null, this gives the number of edges between the two same nodes.
	 */
	public EdgeGroup group;
	
	/**
	 * Control points for curved edges. This contains the control points of an edge.
	 * If the edge is in 2D each sequence of two cells gives the x and y coordinates
	 * of a control point. Else each sequence of three cells gives the x, y and z
	 * coordinates. Therefore the number of control points can be obtained by dividing
	 * by 2 or 3 the length of this array. For example for cubic Bezier curves in 2D
	 * this array contains four cells. The control points are ordered from node0 to
	 * node1.
	 */
	public float[] ctrl;
	
// Constructors
	
	/**
	 * New graphic edge.
	 * @param id The edge unique identifier.
	 * @param from The source node.
	 * @param to The target node.
	 * @param dir True if the edge is directed in the direction from-to.
	 * @param attributes A set of initial attributes.
	 */
	public GraphicEdge(String id, GraphicNode from, GraphicNode to, boolean dir, HashMap<String,Object> attributes) 
	{
		super( id, from.mygraph );
	
		this.from     = from;
		this.to       = to;
		this.directed = dir;

		if( this.attributes == null )
			this.attributes = new HashMap<String,Object>();
		
		if( attributes != null )
		{
			addAttributes( attributes );
		}
	}
	

	@Override
    protected Type getSelectorType()
    {
	    return Selector.Type.EDGE;
    }
	
	/**
	 * Obtain the node that is not "n" attached to this edge.
	 * @param n One of the node of this edge.
	 * @return The other node of this edge.
	 */
	public GraphicNode otherNode(GraphicNode n)
	{
		return (GraphicNode) getOpposite( n );
	}

	@Override
	public float getX()
	{
		return from.x; 
	}

	@Override
	public float getY()
	{
		return from.y;
	}
	
	@Override
	public float getZ()
	{
		return from.z;
	}
	
	/**
	 * Control points for curved edges. This contains the control points of an edge.
	 * If the edge is in 2D each sequence of two cells gives the x and y coordinates
	 * of a control point. Else each sequence of three cells gives the x, y and z
	 * coordinates. Therefore the number of control points can be obtained by dividing
	 * by 2 or 3 the length of this array. For example for cubic Bezier curves in 2D
	 * this array contains four cells. The control points are ordered from node0 to
	 * node1. The units are "graph units".
	 * @return The control points coordinates or null if this edge is a straight line.
	 */
	public float[] getControlPoints()
	{
		return ctrl;
	}
	
	/**
	 * True if the the edge defines control points to draw a curve. This does not mean
	 * the edge style asks to paint the edge as a curve, only that control points are
	 * defined.
	 * @return True if control points are available.
	 */
	public boolean isCurve()
	{
		return ctrl != null;
	}
	
	/**
	 * Change the control points array for this edge.
	 * @param points The new set of points. See the {@link #getControlPoints()}
	 * method for an explanation on the organisation of this array.
	 * @see #getControlPoints()
	 */
	public void setControlPoints( float points[] )
	{
		ctrl = points;
	}
	
	/**
	 * This edge is the i-th between the two same nodes.
	 * @return The edge index between the two nodes if there are several such edges.
	 */
	public int getMultiIndex()
	{
		return multi;
	}
	
	@Override
	public boolean contains( float x, float y )
	{
		return false;
	}

	@Override
    public void move( float x, float y, float z )
    {
    	// NOP on edges !!!
    }
    
	@Override
    public void setBounds( float x, float y, float w, float h )
    {
    	// NOP on edges !!!
    }

	@Override
	protected void attributeChanged( String sourceId, long timeId, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
	{
		super.attributeChanged( sourceId, timeId, attribute, event, oldValue, newValue );
		
		if( attribute.equals( "ui.edge-style" ) || attribute.equals( "edge-style" ) )
		{
			if( newValue instanceof String )
			{
				rule.getStyle().setEdgeStyle( GraphicGraph.convertEdgeStyle( newValue ) );					
				to.mygraph.graphChanged = true;
			}
			else if( newValue == null )
			{
				rule.getStyle().unsetEdgeStyle();
				to.mygraph.graphChanged = true;
			}
		}
	}
	
	protected void countSameEdges( ArrayList<GraphicEdge> edgeList )
	{
		for( GraphicEdge other: edgeList )
		{
			if( other != this )
			{
				if( ( other.from == from && other.to == to ) || ( other.to == from && other.from == to ) )
				{
					group = other.group;
					
					if( group == null )
					     group = new EdgeGroup( other, this );
					else group.increment( this );
					
					break;
				}
			}
		}
	}
	
	@Override
	public void removed()
	{
		super.removed();
		
		if( group != null )
		{
			group.decrement( this );
		
			if( group.getCount() == 1 )
				group = null;
		}
	}

// Edge interface
	
	public Node getNode0()
    {
	    return from;
    }

	public Node getNode1()
    {
	    return to;
    }
	
	/**
	 * If there are several edges between two nodes, this edge pertains to a group. Else
	 * this method returns null.
	 * @return The group of edges between two same nodes, null if the edge is alone
	 *   between the two nodes.
	 */
	public EdgeGroup getGroup()
	{
		return group;
	}

	public Node getOpposite( Node node )
    {
	    if( node == from )
	    	return to;
	    
	    return from;
    }

	public Node getSourceNode()
    {
	    return from;
    }

	public Node getTargetNode()
    {
	    return to;
    }

	public boolean isDirected()
    {
	    return directed;
    }

	public void setDirected( boolean on )
    {
		directed = on;
    }

	public void switchDirection()
    {
		GraphicNode tmp;
		tmp  = from;
		from = to;
		to   = tmp;
    }
	
// Nested classes
	
	public class EdgeGroup
	{
		public ArrayList<GraphicEdge> edges;

		public EdgeGroup( GraphicEdge first, GraphicEdge second )
		{
			edges = new ArrayList<GraphicEdge>();
			first.group  = this;
			second.group = this;
			edges.add( first );
			edges.add( second );
			first.multi  = 0;
			second.multi = 1;
		}
		
		public GraphicEdge getEdge( int i )
		{
			return edges.get( i );
		}
		
		public int getCount()
		{
			return edges.size();
		}
		
		public void increment( GraphicEdge edge )
		{
			edge.multi = getCount();
			edges.add( edge );
		}
		
		public void decrement( GraphicEdge edge )
		{
			edges.remove( edges.indexOf( edge ) );
			
			for( int i=0; i<edges.size(); i++ )
				edges.get(i).multi = i;
		}
	}
}