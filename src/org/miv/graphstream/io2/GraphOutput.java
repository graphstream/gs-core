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

package org.miv.graphstream.io2;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.MultiGraph;

/**
 * Listen at graph events and build a graph from them.
 */
public class GraphOutput implements Output
{
	/**
	 * The graph to modify.
	 */
	protected Graph graph;

	/**
	 * Create an instance of {@link org.miv.graphstream.graph.implementations.MultiGraph} and
	 * applies all received events to this graph.
	 */
	public GraphOutput()
	{
		graph = new MultiGraph();
	}
	
	/**
	 * Apply all the received events to the given graph.
	 * @param graph The graph to modify.
	 */
	public GraphOutput( Graph graph )
	{
		this.graph = graph;
	}

// The output part.
	
	public void edgeAttributeAdded( String graphId, String edgeId, String attribute, Object value )
    {
		Edge edge = graph.getEdge( edgeId );
		
		if( edge != null )
			edge.addAttribute( attribute, value );
    }

	public void edgeAttributeChanged( String graphId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
		Edge edge = graph.getEdge( edgeId );
		
		if( edge != null )
			edge.changeAttribute( attribute, newValue );
    }

	public void edgeAttributeRemoved( String graphId, String edgeId, String attribute )
    {
		Edge edge = graph.getEdge( edgeId );
		
		if( edge != null )
			edge.removeAttribute( attribute );
    }

	public void graphAttributeAdded( String graphId, String attribute, Object value )
    {
		graph.addAttribute( attribute, value );
    }

	public void graphAttributeChanged( String graphId, String attribute, Object oldValue,
            Object newValue )
    {
		graph.changeAttribute( attribute, newValue );
    }

	public void graphAttributeRemoved( String graphId, String attribute )
    {
		graph.removeAttribute( attribute );
    }

	public void nodeAttributeAdded( String graphId, String nodeId, String attribute, Object value )
    {
		Node node = graph.getNode( nodeId );
		
		if( node != null )
			node.addAttribute( attribute, value );
    }

	public void nodeAttributeChanged( String graphId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
		Node node = graph.getNode( nodeId );
		
		if( node != null )
			node.addAttribute( attribute, newValue );
    }

	public void nodeAttributeRemoved( String graphId, String nodeId, String attribute )
    {
		Node node = graph.getNode( nodeId );
		
		if( node != null )
			node.removeAttribute( attribute );
    }

	public void edgeAdded( String graphId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		graph.addEdge( edgeId, fromNodeId, toNodeId, directed );
    }

	public void edgeRemoved( String graphId, String edgeId )
    {
		graph.removeEdge( edgeId );
    }

	public void graphCleared( String graphId )
    {
		graph.clear();
    }

	public void nodeAdded( String graphId, String nodeId )
    {
		graph.addNode( nodeId );
    }

	public void nodeRemoved( String graphId, String nodeId )
    {
		graph.removeNode( nodeId );
    }

	public void stepBegins( String graphId, double time )
    {
		graph.stepBegins( time );
    }
}