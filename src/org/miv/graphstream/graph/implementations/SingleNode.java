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

package org.miv.graphstream.graph.implementations;

import java.util.HashMap;
import java.util.Iterator;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.util.SingletonException;

/**
 * Full implementation of {@link org.miv.graphstream.graph.Node} that allows
 * only one edge between two nodes.
 */
public class SingleNode extends DefaultNode
{
// Attribute

	/**
	 * Map of leaving edges toward nodes. Each element of the map is a pair
	 * (key,value) where the key is the id of a node that can be reached
	 * following a leaving edge, and the value is the leaving edge.
	 */
	protected HashMap<String,Edge> to = new HashMap<String,Edge>();

	/**
	 * Map of entering edges from nodes. Each element of the map is a pair
	 * (key,value) where the key is the id of a node that can be reached
	 * following an entering edge, and the value is the entering edge.
	 */
	protected HashMap<String,Edge> from = new HashMap<String,Edge>();

// Constructor
	
	/**
	 * New unconnected node.
	 * @param graph The graph containing the node.
	 * @param id Tag of the node.
	 */
	public SingleNode( Graph graph, String id )
	{
		super( graph, id );
	}

// Access

	@Override
	public int getOutDegree()
	{
		return to.size();
	}

	@Override
	public int getInDegree()
	{
		return from.size();
	}

	@Override
	public boolean hasEdgeToward( String id )
	{
		return( to.get( id ) != null );
	}
	
	@Override
	public boolean hasEdgeFrom( String id )
	{
		return( from.get( id ) != null );
	}

	@Override
	public Edge getEdgeToward( String id )
	{
		return to.get( id );
	}

	@Override
	public Edge getEdgeFrom( String id )
	{
		return from.get( id );
	}

	@Override
	public Iterator<Edge> getEnteringEdgeIterator()
	{
		return new ElementIterator<Edge>( from );
	}
	
	@Override
	public Iterator<Edge> getLeavingEdgeIterator()
	{
		return new ElementIterator<Edge>( to );
	}

// Access -- Not in Node interface

	@Override
	public Iterable<Edge> getLeavingEdgeSet()
	{
		return to.values();
	}

	@Override
	public Iterable<Edge> getEnteringEdgeSet()
	{
		return from.values();
	}

// Command

	/**
	 * Add an edge between this node and the given target.
	 * @param tag Tag of the edge.
	 * @param target Target node.
	 * @param directed If the edge is directed only from this node to the target.
	 */
	@Override
	protected Edge addEdgeToward( String tag, DefaultNode target, boolean directed )
		throws IllegalArgumentException
	{
		// Some checks.

		if( target.G == null )
			throw new IllegalArgumentException(
				"cannot add edge to node `" + target.getId()
				+ "' since this node is not yet part of a graph" );

		if( G == null )
			throw new IllegalArgumentException(
				"cannot add edge to node `" + getId()
				+ "' since this node is not yet part of a graph" );

		if( G != target.G )
			throw new IllegalArgumentException(
				"cannot add edge between node `" + getId() + "' and node `"
				+ target.getId() + "' since they pertain to distinct graphs" );

		// Register the edge.

		Edge edge = target.getEdgeToward( getId() );
		
		if( edge != null )
		{
			// There exist yet an edge from the target to this node.
			// Change the edge so that it is no more directed since it
			// became bidirectional.
				
			edge.setDirected( false );
			
			return edge;
		}
		else
		{
			DefaultEdge e = (DefaultEdge) G.edgeFactory.newInstance(tag,this,target);
			e.setDirected(directed);
//			return new CheckedEdge( tag, this, target, directed );
			return e;
		}
	}

	/**
	 * Called by an edge to bind it.
	 */
	@Override
	protected void registerEdge( Edge edge )
		throws IllegalArgumentException, SingletonException
	{
		// If the edge or an edge with the same id is already registered.

		Node other = edge.getOpposite( this );

		if( other != this )	// case of loop edges
		{
			if( getEdgeToward( ( other ).getId() ) != null || getEdgeFrom( (  other ).getId() ) != null )
				throw new SingletonException( "multi edges are not supported: edge between node '"+getId()+"' and '"+(  other ).getId()+"' already exists" );
		}

		// Add the edge.

		edges.add( edge );
		
		String otherId = other.getId();

		if( edge.isDirected() )
		{
			if( edge.getSourceNode() == this )
				 to.put( otherId, edge );
			else from.put( otherId, edge );
		}
		else
		{
			
			to.put( otherId, edge );
			from.put( otherId, edge );
		}
	}

	@Override
	protected void unregisterEdge( Edge edge )
	{
		Node other = edge.getOpposite( this );

		to.remove( other.getId() );
		from.remove( other.getId() );

		int pos = edges.indexOf( edge );

		if( pos >= 0 )
			edges.remove( pos );
	}
	
	/**
	 * When a node is unregistered from a graph, it must not keep edges
	 * connected to nodes still in the graph. This methods untie all edges
	 * connected to this node (this also unregister them from the graph).
	 */
	@Override
	protected void disconnectAllEdges()
		throws IllegalStateException
	{
		int n = edges.size();

		// We cannot use a "for" since untying an edge removes this edge from
		// the node. The number of edges will change continuously.

		while( n > 0 )
		{
			Edge e = edges.get( 0 );
			G.removeEdge( ( (AbstractElement) e ).getId() );
			//e.unbind();
			n = edges.size();
		}
	}
}