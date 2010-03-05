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

package org.graphstream.graph.implementations;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.DepthFirstIterator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;
import org.graphstream.stream.SourceBase.ElementType;

/**
 * <p>
 * A light node class intended to allow the construction of big graphs
 * (millions of elements).
 * </p>
 * <p>
 * The main purpose here is to minimize memory consumption even if the
 * management of such a graph implies more CPU consuming. See the
 * <code>complexity</code> tags on each method so as to figure out the impact
 * on the CPU.
 * </p>
 * 
 * @since July 12 2007
  * 
 */
public class ConcurrentNode extends AbstractConcurrentElement implements Node
{
	public class NeighborNodeIterator implements Iterator<Node>
	{
		Node node;
		Iterator<Edge>	ite;
		
		public NeighborNodeIterator( Node node )
		{
			this.node = node;
			ite = ((ConcurrentNode)node).edges.iterator();
		}

		public boolean hasNext()
		{
			return ite.hasNext();
		}

		public Node next()
		{
			if( hasNext() )
				return ite.next().getOpposite( node );
			
			return null;
		}

		public void remove()
		{
			throw new UnsupportedOperationException( "this iterator does not allow removing" );
		}
	}
	
	public class EdgeIterable implements Iterable<Edge>
	{
		protected Iterator<Edge> iterator;
		
		public EdgeIterable( Iterator<Edge> iterator )
		{
			this.iterator = iterator;
		}
		
		public Iterator<Edge> iterator()
		{
			return iterator;
		}
	}

	ConcurrentLinkedQueue<Edge> edges;

	Graph graph;

	public ConcurrentNode( Graph graph, String id )
	{
		super( id );
		this.graph = graph;
		edges = new ConcurrentLinkedQueue<Edge>();
	}

	@Override
	protected String myGraphId()	// XXX
	{
		return graph.getId();
	}
	
	@Override
	protected long newEvent()		// XXX
	{
		return ((ConcurrentGraph)graph).newEvent();
	}
	
	public Iterator<Node> getBreadthFirstIterator()
	{
		return new BreadthFirstIterator( this );
	}

	public Iterator<Node> getBreadthFirstIterator( boolean directed )
	{
		return new BreadthFirstIterator( this, directed );
	}

	public int getDegree()
	{
		return edges.size();
	}

	public Iterator<Node> getDepthFirstIterator()
	{
		return new DepthFirstIterator( this );
	}

	public Iterator<Node> getDepthFirstIterator( boolean directed )
	{
		return new DepthFirstIterator( this, directed );
	}

	public Edge getEdge( int i )
	{
		int j = 0;
		Iterator<Edge> ite = edges.iterator();
		
		while( ite.hasNext() )
		{
			if( i == j )
				return ite.next();
			
			j++;
			ite.next();
		}
		
		return null;
	}

	/**
	 * @complexity 0(n+d) with d the degree of the node and n the number nodes
	 *             in the graph.
	 */
	public Edge getEdgeFrom( String id )
	{
		Node n = ( (ConcurrentGraph) graph ).lookForNode( id );
		if( n != null )
		{
			for( Edge e: edges )
			{
				if( e.getSourceNode() == n )
				{
					return e;
				}
				if( !e.isDirected() && e.getTargetNode() == n )
				{
					return e;
				}
			}
		}
		return null;
	}

	public Iterator<Edge> getEdgeIterator()
	{
		return edges.iterator();//new EdgeIterator();
	}
	
	public Iterator<Edge> iterator()
	{
		return getEdgeIterator();
	}

	public Collection<Edge> getEdgeSet()
	{
		return edges;
	}

	public Edge getEdgeToward( String id )
	{
		Node n = ( (ConcurrentGraph) graph ).lookForNode( id );
		if( n != null )
		{
			for( Edge e: edges )
			{
				if( e.getTargetNode() == n )
				{
					return e;
				}
				if( !e.isDirected() && e.getSourceNode() == n )
				{
					return e;
				}
			}
		}
		return null;
	}

	public Iterator<Edge> getEnteringEdgeIterator()
	{
		throw new UnsupportedOperationException( "unsupported entering edge iterator" );
	}

	public Iterable<Edge> getEnteringEdgeSet()
	{
		return new EdgeIterable( getEnteringEdgeIterator() );
	}

	public Graph getGraph()
	{
		return graph;
	}

	public int getInDegree()
	{
		Iterator<Edge> ite = edges.iterator();
		
		int d = 0;
		Edge e;
		while(ite.hasNext()) {
			e = ite.next();
			if( e.getSourceNode()==this || ! e.isDirected() )
				d++;
		}
		
		return d;
	}

	public Iterator<Edge> getLeavingEdgeIterator()
	{
		throw new UnsupportedOperationException( "unsupported leaving edge iterator" );
	}

	public Iterable<Edge> getLeavingEdgeSet()
	{
		return new EdgeIterable( getLeavingEdgeIterator() );
	}

	public Iterator<Node> getNeighborNodeIterator()
	{
		return new NeighborNodeIterator( this );
	}

	public int getOutDegree()
	{
		Iterator<Edge> ite = edges.iterator();
		
		int d = 0;
		Edge e;
		while(ite.hasNext()) {
			e = ite.next();
			if( e.getTargetNode()==this || ! e.isDirected() )
				d++;
		}
		
		return d;
	}

	public boolean hasEdgeFrom( String id )
	{
		Node n = ( (ConcurrentGraph) graph ).lookForNode( id );
		return hasEdgeFrom(n)==null?false:true;
	}
	
	public Edge hasEdgeFrom( Node n )
	{
		if( n != null )
		{
			Iterator<Edge> it = edges.iterator();
			
			while( it.hasNext() )
			{
				Edge e = it.next();
				
				if( e.isDirected() )
				{
					if( e.getSourceNode() == n )
						return e;
				}
				else
					return e;
			}
		}
		return null;
	}
	
	public boolean hasEdgeToward( String id )
	{
		Node n = ( (ConcurrentGraph) graph ).lookForNode( id );
		return hasEdgeToward(n)==null?false:true;
	}

	/**
	 * @return an edge is there is one, else null.
	 */
	public Edge hasEdgeToward( Node n )
	{
		if( n != null )
		{
			Iterator<Edge> it = edges.iterator();
			
			while( it.hasNext() )
			{
				Edge e = it.next();
				
				if( e.isDirected() )
				{
					if( e.getTargetNode() == n )
						return e;
				}
				else
				{
					if( e.getTargetNode() == n || e.getSourceNode() == n )
						return e;
				}
			}
		}
		return null;
	}

	@Override
	protected void attributeChanged( String sourceId, long timeId, String attribute,
			AttributeChangeEvent event, Object oldValue, Object newValue )
	{
		if( graph != null )
			((ConcurrentGraph)graph).listeners.sendAttributeChangedEvent(
					sourceId, timeId, getId(), ElementType.NODE, attribute, event, oldValue, newValue );
	}
}