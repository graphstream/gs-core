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

package org.graphstream.io.old;

import java.util.Map;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.io.GraphParseException;

/**
 * Default implementation for a graph reader listener that build a graph
 * according to the events received from a graph reader.
 * 
 * <p>
 * The reader helper takes as argument a
 * graph, and modifies this graph according to the events received. This is a
 * simple and quick way to build a graph from a graph reader.
 * </p>
 * 
 * @since 20060101
 * @see GraphReader
 * @see GraphReaderListenerExtended
 */
public class GraphReaderListenerHelper implements GraphReaderListenerExtended
{
// Attribute
	
	/**
	 * A reference to the graph it modifies.
	 */
	protected Graph graph;
	
// Construction
	
	/**
	 * New default graph reader listener that modifies the given graph according
	 * to the received events.
	 * @param graph The graph to modify according to events. 
	 */
	public GraphReaderListenerHelper( Graph graph )
	{
		this.graph = graph;
	}
	
// Command
	
	public void edgeAdded( String id, String from, String to, boolean directed, Map<String, Object> attributes ) throws GraphParseException
	{
		Edge edge = graph.addEdge(id, from, to, directed);
		
		if( attributes != null )
			edge.addAttributes( attributes );
	}
/*
	public void edgeChanged(String id, Map<String, Object> attributes) throws GraphParseException
	{
		Edge edge = graph.getEdge(id);
		
		if( attributes != null && edge != null )
			edge.addAttributes( attributes );
	}
*/
	public void edgeChanged( String id, String attribute, Object value, boolean removed )
		throws GraphParseException
	{
		Edge edge = graph.getEdge( id );
		
		if( edge == null )
		{
			if( graph.isStrict() )
				throw new GraphParseException( "cannot change edge '"+id+"' that does not yet exist" );
		}
		
		if( id != null )
		{
			if( removed )
			     edge.removeAttribute( attribute );
			else edge.setAttribute( attribute, value );
		}
	}

	public void edgeRemoved( String id ) throws GraphParseException
	{
		graph.removeEdge( id );
	}
/*
	public void graphChanged(Map<String, Object> attributes) throws GraphParseException
	{
		if( attributes != null )
			graph.addAttributes( attributes );
	}
*/
	public void graphChanged( String attribute, Object value, boolean removed )
		throws GraphParseException
	{
		if( removed )
		     graph.removeAttribute( attribute );
		else graph.setAttribute( attribute, value );
	}

	public void nodeAdded( String id, Map<String, Object> attributes ) throws GraphParseException
	{
		Node node = graph.addNode( id );
		
		if( attributes != null && node != null )
			node.addAttributes( attributes );
	}
/*
	public void nodeChanged( String id, Map<String, Object> attributes ) throws GraphParseException
	{
		Node n = graph.getNode( id );
		
		if( n == null )
		{
			if (graph.isStrictCheckingEnabled() )
			{
				throw new GraphParseException();
			}
			if( graph.isAutoCreationEnabled() ) {
				n = graph.addNode(id);
			}
		}
		
		if( n != null )
		{
			if( attributes != null )
				n.addAttributes( attributes );
		}
	}
*/
	public void nodeChanged( String id, String attribute, Object value, boolean removed )
		throws GraphParseException
	{
		Node node = graph.getNode( id );
		
		if( node == null )
		{
			if( graph.isStrict() )
				throw new GraphParseException( "cannot change node '"+id+"' that does not yet exist" );
			if( graph.isAutoCreationEnabled() )
				node = graph.addNode( id );
		}
		
		if( node != null )
		{
			if( removed )
			     node.removeAttribute( attribute );
			else node.setAttribute( attribute, value );
		}
	}

	public void nodeRemoved( String id ) throws GraphParseException
	{
		graph.removeNode( id );
	}

	public void stepBegins( double time ) throws GraphParseException
	{
		graph.stepBegins( time );
	}

	public void unknownEventDetected( String unknown ) throws GraphParseException
	{
//		System.out.println("GraphReaderListener.unknownEventDetected");
	}
}