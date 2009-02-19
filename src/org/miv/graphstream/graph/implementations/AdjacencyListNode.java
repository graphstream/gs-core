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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.miv.graphstream.graph.BreadthFirstIterator;
import org.miv.graphstream.graph.DepthFirstIterator;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;

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
 * @author Antoine Dutot
 * @author Yoann Pigné
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

		public EnteringEdgeIterator(AdjacencyListNode n)
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

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return( index < edges.size() && nb < nbEntering );
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Edge next()
		{
			if( hasNext() )
			{

				while( edges.get( index ).isDirected() && edges.get( index ).getTargetNode() != n );
				{
					index++;
				}
				nb++;
				return edges.get( index++ );
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
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

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return( index < edges.size() && nb < nbLeaving );
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
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

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			throw new UnsupportedOperationException( "this iterator does not allow removing" );

		}

	}

	/**
	 * @author Yoann Pigné
	 * 
	 */
	public class EdgeIterator implements Iterator<Edge>
	{

		int index = 0;

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			if( index < edges.size() )
			{
				return true;
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Edge next()
		{
			if( hasNext() )
			{
				return edges.get( index++ );
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			throw new UnsupportedOperationException( "this iterator does not allow removing" );
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

	public AdjacencyListNode()
	{
		this(null,"");
	}
	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getBreadthFirstIterator()
	 */
	public Iterator<Node> getBreadthFirstIterator()
	{
		return new BreadthFirstIterator( this );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getBreadthFirstIterator(boolean)
	 */
	public Iterator<Node> getBreadthFirstIterator( boolean directed )
	{
		return new BreadthFirstIterator( this, directed );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getDegree()
	 */
	public int getDegree()
	{
		return edges.size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getDepthFirstIterator()
	 */
	public Iterator<Node> getDepthFirstIterator()
	{
		return new DepthFirstIterator( this );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getDepthFirstIterator(boolean)
	 */
	public Iterator<Node> getDepthFirstIterator( boolean directed )
	{
		return new DepthFirstIterator( this, directed );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getEdge(int)
	 */
	public Edge getEdge( int i )
	{
		return edges.get( i );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getEdgeFrom(java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getEdgeIterator()
	 */
	public Iterator<Edge> getEdgeIterator()
	{
		return new EdgeIterator();
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getEdgeSet()
	 */
	public Collection<Edge> getEdgeSet()
	{
		return edges;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getEdgeToward(java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getEnteringEdgeIterator()
	 */
	public Iterator<Edge> getEnteringEdgeIterator()
	{
		return new EnteringEdgeIterator(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getEnteringEdgeSet()
	 */
	public Collection<Edge> getEnteringEdgeSet()
	{
		throw new UnsupportedOperationException( "Method not implemented." );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getGraph()
	 */
	public Graph getGraph()
	{
		return graph;
	}
	

	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.Node#setGraph(org.miv.graphstream.graph.Graph)
	 */
	public void setGraph( Graph graph )
	{
		this.graph = graph; 
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getInDegree()
	 */
	public int getInDegree()
	{
		EnteringEdgeIterator it = new EnteringEdgeIterator(this);
		return it.nbEntering;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getLeavingEdgeIterator()
	 */
	public Iterator<Edge> getLeavingEdgeIterator()
	{
		return new LeavingEdgeIterator(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getLeavingEdgeSet()
	 */
	public Collection<Edge> getLeavingEdgeSet()
	{
		throw new UnsupportedOperationException( "Method not implemented." );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getNeighborNodeIterator()
	 */
	public Iterator<Node> getNeighborNodeIterator()
	{
		throw new UnsupportedOperationException( "Method not implemented." );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#getOutDegree()
	 */
	public int getOutDegree()
	{
		LeavingEdgeIterator it = new LeavingEdgeIterator(this);
		return it.nbLeaving;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#hasEdgeFrom(java.lang.String)
	 */
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
	
	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.NodeInterface#hasEdgeToward(java.lang.String)
	 */
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


	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.Element#attributeChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void attributeChanged( String attribute, Object oldValue, Object newValue )
	{
		if( graph != null )
			( (AdjacencyListGraph) graph ).attributeChangedEvent( this, attribute, oldValue, newValue );	
	}
	
	
	/**
	 * Distributed part
	 */
	
	public boolean isDistributed() {
		return true ;
	}
		
	public String getHost() {
		return "" ;

	}
		

	public void setHost( String newHost ) {

	}
		

	public String getGraphName() {
		return "" ;

	}
		

	public void setGraphName( String newHost ) {

	}
}