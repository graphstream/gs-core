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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.miv.graphstream.graph.BreadthFirstIterator;
import org.miv.graphstream.graph.DepthFirstIterator;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.io2.InputBase.ElementType;

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
public class AdjacencyListNode extends AbstractElement implements Node
{
	private class EnteringEdgeIterator implements Iterator<Edge>
	{
		public AdjacencyListNode n;
		
		public int index = 0;

		public int nbEntering = 0;

		public int nb = 0;

		public EnteringEdgeIterator( AdjacencyListNode n )
		{
			this.n = n;
			
			for( Edge e: edges )
			{
				if( e.isDirected() )
				{
					if( e.getTargetNode() == n )
					{
						nbEntering++;
					}
				}
				else
				{
					nbEntering++;
				}
			}
		}

		public boolean hasNext()
		{
			return( index < edges.size() && nb < nbEntering );
		}

		public Edge next()
		{
			if( hasNext() )
			{
				while( edges.get( index ).isDirected() && edges.get( index ).getTargetNode() != n )
				{
					index++;
				}
				nb++;
				return edges.get( index++ );
			}
			return null;
		}

		public void remove()
		{
			throw new UnsupportedOperationException( "this iterator does not allow removing" );

		}
	}

	public class LeavingEdgeIterator implements Iterator<Edge>
	{
		public AdjacencyListNode n;
		
		int index = 0;

		int nbLeaving = 0;

		int nb = 0;

		public LeavingEdgeIterator(AdjacencyListNode n)
		{
			this.n = n; 
			for( Edge e: edges )
			{
				if( e.isDirected() )
				{
					if( e.getSourceNode() == n )
					{
						nbLeaving++;
					}
				}
				else
				{
					nbLeaving++;
				}
			}
		}

		public boolean hasNext()
		{
			return( index < edges.size() && nb < nbLeaving );
		}

		public Edge next()
		{
			if( hasNext() )
			{
				while( (edges.get( index ).isDirected()) && (edges.get( index ).getSourceNode() != n) )
				{
					index++;
				}
				nb++;
				return edges.get( index++ );
			}
			return null;
		}

		public void remove()
		{
			throw new UnsupportedOperationException( "this iterator does not allow removing" );

		}
	}

	public class EdgeIterator implements Iterator<Edge>
	{
		int index = 0;

		public boolean hasNext()
		{
			if( index < edges.size() )
			{
				return true;
			}
			return false;
		}

		public Edge next()
		{
			if( hasNext() )
			{
				return edges.get( index++ );
			}
			return null;
		}

		public void remove()
		{
			throw new UnsupportedOperationException( "this iterator does not allow removing" );
		}
	}

	public class NeighborNodeIterator implements Iterator<Node>
	{
		int index = 0;
		Node node;
		
		public NeighborNodeIterator( Node node )
		{
			this.node = node;
		}

		public boolean hasNext()
		{
			if( index < edges.size() )
			{
				return true;
			}
			return false;
		}

		public Node next()
		{
			if( hasNext() )
			{
				Edge edge = edges.get( index++ );
				
				return edge.getOpposite( node );
			}
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

	ArrayList<Edge> edges;

	Graph graph;

	public AdjacencyListNode( Graph graph, String id )
	{
		super( id );
		this.graph = graph;
		edges = new ArrayList<Edge>();
	}

	@Override
	protected String getMyGraphId()
	{
		return graph.getId();
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
		return edges.get( i );
	}

	/**
	 * @complexity 0(n+d) with d the degree of the node and n the number nodes
	 *             in the graph.
	 */
	public Edge getEdgeFrom( String id )
	{
		Node n = ( (AdjacencyListGraph) graph ).lookForNode( id );
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
		return new EdgeIterator();
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
		Node n = ( (AdjacencyListGraph) graph ).lookForNode( id );
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
		return new EnteringEdgeIterator(this);
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
		EnteringEdgeIterator it = new EnteringEdgeIterator(this);
		return it.nbEntering;
	}

	public Iterator<Edge> getLeavingEdgeIterator()
	{
		return new LeavingEdgeIterator(this);
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
		LeavingEdgeIterator it = new LeavingEdgeIterator(this);
		return it.nbLeaving;
	}

	public boolean hasEdgeFrom( String id )
	{
		Node n = ( (AdjacencyListGraph) graph ).lookForNode( id );
		return hasEdgeFrom(n)==null?false:true;
	}
	
	public Edge hasEdgeFrom( Node n )
	{
		if( n != null )
		{
			Iterator<Edge> it = new EnteringEdgeIterator(this);
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
		Node n = ( (AdjacencyListGraph) graph ).lookForNode( id );
		return hasEdgeToward(n)==null?false:true;
	}

	/**
	 * @return an edge is there is one, else null.
	 */
	public Edge hasEdgeToward( Node n )
	{
		if( n != null )
		{
			Iterator<Edge> it = new LeavingEdgeIterator(this);
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
	protected void attributeChanged( String sourceId, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
	{
		if( graph != null )
			((AdjacencyListGraph)graph).listeners.sendAttributeChangedEvent(
					sourceId, getId(), ElementType.NODE, attribute, event, oldValue, newValue );
	}
}