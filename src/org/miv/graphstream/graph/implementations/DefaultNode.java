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

import org.miv.graphstream.graph.BreadthFirstIterator;
import org.miv.graphstream.graph.DepthFirstIterator;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.util.*;

import java.util.*;

/**
 * Base implementation of a {@link org.miv.graphstream.graph.Node} for the {@link DefaultGraph}.
 *
 * <p>
 * This node ensures the consistency of the graph. Such a node is able to
 * give informations about all leaving edges but also entering edges (when
 * directed), at the price however of a larger memory footprint.
 * </p>
 * 
 * <p>
 * This is a base implementation that is refined in two classes {@link SingleNode}
 * and {@link MultiNode}. The first allows only one edge between two nodes, the other
 * allows multiple edges between two nodes (or several loops).
 * </p>
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20020709
 */
public abstract class DefaultNode extends AbstractElement implements Node
{
// Constants

	/**
	 * Property used to store labels.
	 */
	public static final String ATTRIBUTE_LABEL = "label";

// Fields

	/**
	 * Parent graph.
	 */
	protected DefaultGraph G;

	/**
	 * List of all entering or leaving edges the node knows.
	 */
	protected ArrayList<Edge> edges = new ArrayList<Edge>();

// Constructors

	/**
	 * New unconnected,unnamed node. Useful for reflexive instantiation.
	 * 
	 */
	public DefaultNode()
	{
		super("");
	}
	
	/**
	 * New unconnected node.
	 * @param graph The graph containing the node.
	 * @param id Tag of the node.
	 */
	public DefaultNode( DefaultGraph graph, String id )
	{
		super( id );
		G = graph;
	}

// Modifiers

	public void setGraph( Graph graph )
	{
		this.G = (DefaultGraph) graph;
	}

	
// Access

	public Graph getGraph()
	{
		return G;
	}

	public int getDegree()
	{
		return edges.size();
	}

	public abstract int getOutDegree();

	public abstract int getInDegree();

	public abstract boolean hasEdgeToward( String id );
	
	public abstract boolean hasEdgeFrom( String id );

	public abstract Edge getEdgeToward( String id );

	public abstract Edge getEdgeFrom( String id );

	public Iterator<Edge> getEdgeIterator()
	{
		return new ElementIterator<Edge>( edges );
	}
	
	public abstract Iterator<Edge> getEnteringEdgeIterator();
	
	public abstract Iterator<Edge> getLeavingEdgeIterator();

	public Iterator<Node> getNeighborNodeIterator()
	{
		return new NeighborNodeIterator( this );
	}
	
	public Edge getEdge( int i )
	{
		return edges.get( i );
	}
	
	/**
	 * @complexity of the breath first iterator O(n+m) with n the number of
	 *             nodes and m the number of edges.
	 */
	public Iterator<Node> getBreadthFirstIterator()
	{
		return new BreadthFirstIterator( this );
	}
	
	/**
	 * @complexity of the breath first iterator O(n+m) with n the number of
	 *             nodes and m the number of edges.
	 */
	public Iterator<Node> getBreadthFirstIterator( boolean directed )
	{
		return new BreadthFirstIterator( this );
	}
	
	/**
	 * @complexity of the depth first iterator O(n+m) with n the number of nodes
	 *             and m the number of edges.
	 */
	public Iterator<Node> getDepthFirstIterator()
	{
		return new DepthFirstIterator( this );
	}
	
	/**
	 * @complexity of the depth first iterator O(n+m) with n the number of nodes
	 *             and m the number of edges.
	 */
	public Iterator<Node> getDepthFirstIterator( boolean directed )
	{
		return new DepthFirstIterator( this );
	}

// Access -- Not in Node interface

	public Collection<Edge> getEdgeSet()
	{
		return edges;
	}
	
	public abstract Collection<Edge> getLeavingEdgeSet();

	public abstract Collection<Edge> getEnteringEdgeSet();

// Commands

	/**
	 * Add an edge between this node and the given target.
	 * @param tag Tag of the edge.
	 * @param target Target node.
	 * @param directed If the edge is directed only from this node to the target.
	 */
	protected abstract Edge addEdgeToward( String tag, DefaultNode target, boolean directed )
		throws IllegalArgumentException;

	/**
	 * Called by an edge to bind it.
	 */
	protected abstract void registerEdge( Edge edge )
		throws IllegalArgumentException, SingletonException;

	protected abstract void unregisterEdge( Edge edge );
	
	/**
	 * When a node is unregistered from a graph, it must not keep edges
	 * connected to nodes still in the graph. This methods untie all edges
	 * connected to this node (this also unregister them from the graph).
	 */
	protected abstract void disconnectAllEdges()
		throws IllegalStateException;

	@Override
	protected void attributeChanged( String attribute, Object oldValue, Object newValue )
	{
		if( G != null )
			G.attributeChangedEvent( this, attribute, oldValue, newValue );
	}
	
	@Override
	public String toString()
	{
		return String.format( "[node %s (%d edges)]", id, edges.size() );
	}

// Nested classes

protected class NeighborNodeIterator
	implements Iterator<Node>
{
	protected int i;

	protected Node n;

	protected
	NeighborNodeIterator( Node node )
	{
		i = 0;
		n = node;
	}

	public boolean
	hasNext()
	{
		return( i < edges.size() );
	}

	public Node
	next()
		throws NoSuchElementException
	{
		Edge e = edges.get( i++ );
		return e.getOpposite( n );
	}

	public void
	remove()
		throws UnsupportedOperationException, IllegalStateException
	{
		throw new UnsupportedOperationException( "this iterator does not allow removing" );
	}
	
}

static class ElementIterator<T extends Element> implements Iterator<T>
{
	Iterator<? extends T> iterator;
	
	ElementIterator( ArrayList<T> elements )
	{
		iterator = elements.iterator();
	}
	
	ElementIterator( HashMap<String,? extends T> elements )
	{
		iterator = elements.values().iterator();
	}

	public boolean hasNext()
	{
		return iterator.hasNext();
	}

	public T next()
	{
		return iterator.next();
	}

	public void remove()
		throws UnsupportedOperationException, IllegalStateException
	{
		throw new UnsupportedOperationException( "this iterator does not allow removing" );
	}
}

// Distributed part

	public boolean isDistributed() {
		return false;
	}
		
	public String getHost() {
		return "";
	}
		
	public void setHost( String newHost ) {
	}
	
	public String getGraphName() {
		return G.getId();	
	}
	
	public void setGraphName( String newHost ) {
	}
}