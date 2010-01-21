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

package org.graphstream.ui2.old;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.Sink;

/**
 * Implementation of the GraphListenerProxy that merely copy the evolution of
 * a graph into another. 
 */
public class GraphListenerProxyCopy implements GraphListenerProxy
{
// Attribute

	/**
	 * The graph we observe.
	 */
	protected Graph inGraph;
	
	/**
	 * The graph that copies the observed graph.
	 */
	protected Graph outGraph;
	
// Construction

	/**
	 * New proxy that copies everything that happen in the input graph into the output graph.
	 * @param input The input graph.
	 * @param output The output graph.
	 */
	public GraphListenerProxyCopy( Graph input, Graph output )
	{
		this( input, output, true );
	}
	
	/**
	 * New proxy that copies everything that happen in the input graph into the output graph.
	 * @param input The input graph.
	 * @param output The output graph.
	 * @param replayGraph If true, and if the input graph already contains elements and attributes
	 *  they are first copied to the output graph.
	 */
	public GraphListenerProxyCopy( Graph input, Graph output, boolean replayGraph )
	{
		if( input == output )
			throw new RuntimeException( "input == output ???" );
		
		inGraph  = input;
		outGraph = output;
		
		inGraph.addSink( this );
		
		if( replayGraph )
			replayTheGraph();
	}
	
	/**
	 * Copy everything from the input graph to the output graph.
	 */
	protected void replayTheGraph()
	{
		// Replay all attributes of the graph.

		Iterable<String> k = inGraph.getAttributeKeySet();

		long timeId = 0;
		
		if( k != null )
		{
			for( String key: k )
			{
				Object val = inGraph.getAttribute( key );
				graphAttributeAdded( inGraph.getId(), timeId++, key, val );
			}
		}

		k = null;

		// Replay all nodes and their attributes.


		for( Node node: inGraph )
		{
			nodeAdded( inGraph.getId(), timeId++, node.getId() );

			k = node.getAttributeKeySet();

			if( k != null )
			{
				for( String key: k )
				{
					Object val = node.getAttribute( key );
					nodeAttributeAdded( inGraph.getId(), timeId++, node.getId(), key, val );
				}
			}
		}

		k = null;

		// Replay all edges and their attributes.

		for( Edge edge : inGraph.edgeSet() )
		{
			edgeAdded( inGraph.getId(), timeId++, edge.getId(), edge.getNode0().getId(), edge.getNode1().getId(), edge.isDirected() );

			k = edge.getAttributeKeySet();

			if( k != null )
			{
				for( String key: k )
				{
					Object val = edge.getAttribute( key );
					edgeAttributeAdded( inGraph.getId(), timeId++, edge.getId(), key, val );
				}
			}
		}
	}
	
// Command
	
	public void addGraphListener( Sink listener )
    {
		outGraph.addSink( listener );
    }
	
	public void removeGraphListener( Sink listener )
    {
		outGraph.removeSink( listener );
    }

	public void unregisterFromGraph()
    {
		inGraph.removeSink( this );
    }

	public void checkEvents()
    {
		// NOP! This is a direct copy.
    }

// Commands -- GraphListener

	public void nodeAdded( String graphId, long timeId, String nodeId )
    {
		outGraph.addNode( nodeId );
    }

	public void edgeAdded( String graphId, long timeId, String edgeId, String fromId, String toId, boolean directed )
    {
		outGraph.addEdge( edgeId, fromId, toId, directed );
    }

	public void nodeRemoved( String graphId, long timeId, String nodeId )
    {
		outGraph.removeNode( nodeId );
    }

	public void edgeRemoved( String graphId, long timeId, String edgeId )
    {
		outGraph.removeEdge( edgeId );
    }
	
	public void graphCleared( String graphId, long timeId )
	{
		outGraph.clear();
	}

	public void stepBegins( String graphId, long timeId, double time )
	{
		outGraph.stepBegins( time );
	}

	public void edgeAttributeAdded( String graphId, long timeId, String edgeId, String attribute, Object value )
    {
		Edge edge = outGraph.getEdge( edgeId );
		
		if( edge != null )
			edge.setAttribute( attribute, value );			
    }

	public void edgeAttributeChanged( String graphId, long timeId, String edgeId, String attribute, Object oldValue, Object newValue )
    {
		Edge edge = outGraph.getEdge( edgeId );
		
		if( edge != null )
			edge.changeAttribute( attribute, newValue );			
    }

	public void edgeAttributeRemoved( String graphId, long timeId, String edgeId, String attribute )
    {
		Edge edge = outGraph.getEdge( edgeId );
		
		if( edge != null )
			edge.removeAttribute( attribute );			
    }

	public void graphAttributeAdded( String graphId, long timeId, String attribute, Object value )
    {
		outGraph.setAttribute( attribute, value );
    }

	public void graphAttributeChanged( String graphId, long timeId, String attribute, Object oldValue, Object newValue )
    {
		outGraph.changeAttribute( attribute, newValue );
    }

	public void graphAttributeRemoved( String graphId, long timeId, String attribute )
    {
		outGraph.removeAttribute( attribute );
    }

	public void nodeAttributeAdded( String graphId, long timeId, String nodeId, String attribute, Object value )
    {
		Node node = outGraph.getNode( nodeId );
			
		if( node != null )
			node.setAttribute( attribute, value );
    }

	public void nodeAttributeChanged( String graphId, long timeId, String nodeId, String attribute, Object oldValue, Object newValue )
    {
		Node node = outGraph.getNode( nodeId );
		
		if( node != null )
			node.changeAttribute( attribute, newValue );
    }

	public void nodeAttributeRemoved( String graphId, long timeId, String nodeId, String attribute )
    {
		Node node = outGraph.getNode( nodeId );
		
		if( node != null )
			node.removeAttribute( attribute );
    }
}