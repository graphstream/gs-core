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

package org.miv.graphstream.graph.implementations;

import java.util.Iterator;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.GraphListenerProxy;
import org.miv.graphstream.graph.Node;

/**
 * Implementation of the GraphListenerProxy that merely copy the evolution of
 * a graph into another. 
 * 
 * @author Antoine Dutot
 */
public class GraphListenerProxyCopy implements GraphListenerProxy
{
// Attributes

	/**
	 * The graph we observe.
	 */
	protected Graph inGraph;
	
	/**
	 * The graph that copies the observed graph.
	 */
	protected Graph outGraph;
	
// Constructors

	/**
	 * New proxy that copies everything that happen in the input graph into the output graph.
	 * @param input The input graph.
	 * @param output The output graph.
	 */
	public GraphListenerProxyCopy( Graph input, Graph output )
	{
		this( input, output, true );
	}
	
	public GraphListenerProxyCopy( Graph input, Graph output, boolean replayGraph )
	{
		if( input == output )
			throw new RuntimeException( "input == output ???" );
		
		inGraph  = input;
		outGraph = output;
		
		inGraph.addGraphListener( this );
		
		if( replayGraph )
			replayTheGraph();
	}
	
	protected void replayTheGraph()
	{
		// Replay all attributes of the graph.

		Iterator<String> k = inGraph.getAttributeKeyIterator();

		if( k != null )
		{
			while( k.hasNext() )
			{
				String key = k.next();
				Object val = inGraph.getAttribute( key );

				attributeChanged( inGraph, key, null, val );
			}
		}

		k = null;

		// Replay all nodes and their attributes.

		Iterator<? extends Node> nodes = inGraph.getNodeIterator();

		while( nodes.hasNext() )
		{
			Node node = nodes.next();

			afterNodeAdd( inGraph, node );

			k = node.getAttributeKeyIterator();

			if( k != null )
			{
				while( k.hasNext() )
				{
					String key = k.next();
					Object val = node.getAttribute( key );

					attributeChanged( node, key, null, val );
				}
			}
		}

		k = null;

		// Replay all edges and their attributes.

		Iterator<? extends Edge> edges = inGraph.getEdgeIterator();
		
		while( edges.hasNext() )
		//for( Edge edge : graph.getEdgeSet() )
		{
			Edge edge = edges.next();
	
			afterEdgeAdd( inGraph, edge );

			k = edge.getAttributeKeyIterator();

			if( k != null )
			{
				while( k.hasNext() )
				{
					String key = k.next();
					Object val = edge.getAttribute( key );

					attributeChanged( edge, key, null, val );
				}
			}
		}
	}
	
// Access
	
// Commands
	
	public void addGraphListener( GraphListener listener )
    {
		outGraph.addGraphListener( listener );
    }
	
	public void removeGraphListener( GraphListener listener )
    {
		outGraph.removeGraphListener( listener );
    }

	public void unregisterFromGraph()
    {
		inGraph.removeGraphListener( this );
    }

	public void checkEvents()
    {
		// NOP! This is a direct copy.
    }

// Commands -- GraphListener

	public void beforeGraphClear( Graph graph )
    {
		outGraph.clear();
    }

	public void attributeChanged( Element element, String attribute, Object oldValue,
            Object newValue )
    {
		if( element instanceof Graph )
		{
			outGraph.setAttribute( attribute, newValue );
		}
		else if( element instanceof Node )
		{
			Node node = outGraph.getNode( element.getId() );
			
			if( node != null )
				node.setAttribute( attribute, newValue );
		}
		else if( element instanceof Edge )
		{
			Edge edge = outGraph.getEdge( element.getId() );
			
			if( edge != null )
				edge.setAttribute( attribute, newValue );			
		}
    }

	public void afterNodeAdd( Graph graph, Node node )
    {
		outGraph.addNode( node.getId() );
    }

	public void afterEdgeAdd( Graph graph, Edge edge )
    {
		outGraph.addEdge( edge.getId(), edge.getNode0().getId(), edge.getNode1().getId(), edge.isDirected() );
    }

	public void beforeNodeRemove( Graph graph, Node node )
    {
		outGraph.removeNode( node.getId() );
    }

	public void beforeEdgeRemove( Graph graph, Edge edge )
    {
		outGraph.removeEdge( edge.getId() );
    }

	public void stepBegins(Graph graph, double time)
	{
		outGraph.stepBegins(time);
	}
}