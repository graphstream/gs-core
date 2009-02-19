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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.miv.graphstream.graph.BreadthFirstIterator;
import org.miv.graphstream.graph.DepthFirstIterator;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;

/**
 * <p>
 * A thread-safe node to use with ConcurrentGraph.
 * </p>
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @author Guilhelm Savin
 * 
 * @since 2009008
  * 
 */
public class ConcurrentNode
	extends AbstractConcurrentElement
	implements Node
{
	Graph graph;
	ConcurrentLinkedQueue<Edge> leavingEdges;
	ConcurrentLinkedQueue<Edge> enteringEdges;
	
	public ConcurrentNode()
	{
		this( null, "" );
	}
	
	public ConcurrentNode( Graph graph, String id )
	{
		super( id );
		
		setGraph( graph );
		
		leavingEdges	= new ConcurrentLinkedQueue<Edge>();
		enteringEdges	= new ConcurrentLinkedQueue<Edge>();
	}
	
	protected void registerEdge( Edge e )
	{
		if( e.getSourceNode() == this )
		{
			leavingEdges.add(e);
		}
		else if( e.getTargetNode() == this )
		{
			enteringEdges.add(e);
		}
	}
	
	protected void unregisterEdge( Edge e )
	{
		if( e.getSourceNode() == this )
		{
			leavingEdges.remove(e);
		}
		else if( e.getTargetNode() == this )
		{
			enteringEdges.remove(e);
		}
	}

// --- AbstractConcurrentElement implementation --- //
	
	/* @see org.miv.graphstream.graph.Element */
	@Override
	protected void attributeChanged(String attribute, Object oldValue,
			Object newValue)
	{
		if( graph != null && graph instanceof ConcurrentGraph )
		{
			((ConcurrentGraph) graph).attributeChangedEvent( this, attribute, oldValue, newValue );
		}
	}

// --- //
// --- Node implementation --- //
	
	/* @see org.miv.graphstream.graph.Node */
	public Iterator<? extends Node> getBreadthFirstIterator()
	{
		return new BreadthFirstIterator( this );
	}

	/* @see org.miv.graphstream.graph.Node */
	public Iterator<? extends Node> getBreadthFirstIterator(boolean directed)
	{
		return new BreadthFirstIterator( this, directed );
	}

	/* @see org.miv.graphstream.graph.Node */
	public int getDegree()
	{
		return getInDegree() + getOutDegree();
	}

	/* @see org.miv.graphstream.graph.Node */
	public Iterator<? extends Node> getDepthFirstIterator()
	{
		return new DepthFirstIterator( this );
	}

	/* @see org.miv.graphstream.graph.Node */
	public Iterator<? extends Node> getDepthFirstIterator(boolean directed)
	{
		return new DepthFirstIterator( this, directed );
	}

	/* @see org.miv.graphstream.graph.Node */
	public Edge getEdge(int i)
	{
		Iterator<Edge> ite = new FullEdgeIterator();
		
		while( ite.hasNext() && --i > 0 ) ite.next();
		if( ite.hasNext() ) return ite.next();
		
		return null;
	}

	/* @see org.miv.graphstream.graph.Node */
	public Edge getEdgeFrom(String id)
	{
		Node n = graph.getNode(id);
		Iterator<Edge> ite = leavingEdges.iterator();
		Edge e;
		
		
		while( ite.hasNext() ) 
		{
			e = ite.next();
			if( e.getTargetNode() == n ) return e;
		}
		
		return null;
	}

	/* @see org.miv.graphstream.graph.Node */
	public Iterator<? extends Edge> getEdgeIterator()
	{
		return new FullEdgeIterator();
	}

	/* @see org.miv.graphstream.graph.Node */
	public Collection<? extends Edge> getEdgeSet()
	{
		throw new UnsupportedOperationException( "deprecated operation : Node#getEdgeSet()" );
	}

	/* @see org.miv.graphstream.graph.Node */
	public Edge getEdgeToward(String id)
	{
		Node n = graph.getNode(id);
		Iterator<Edge> ite = enteringEdges.iterator();
		Edge e;
		
		while( ite.hasNext() ) 
			if( ( e = ite.next() ).getSourceNode() == n ) return e;
		
		return null;
	}

	/* @see org.miv.graphstream.graph.Node */
	public Iterator<? extends Edge> getEnteringEdgeIterator()
	{
		return enteringEdges.iterator();
	}

	/* @see org.miv.graphstream.graph.Node */
	public Collection<? extends Edge> getEnteringEdgeSet()
	{
		return Collections.unmodifiableCollection(enteringEdges);
	}

	/* @see org.miv.graphstream.graph.Node */
	public Graph getGraph()
	{
		return graph;
	}

	/* @see org.miv.graphstream.graph.Node */
	public String getGraphName()
	{
		return graph == null ? "" : graph.getId();
	}

	/* @see org.miv.graphstream.graph.Node */
	public String getHost()
	{
		return "";
	}

	/* @see org.miv.graphstream.graph.Node */
	public int getInDegree()
	{
		return enteringEdges.size();
	}

	/* @see org.miv.graphstream.graph.Node */
	public Iterator<? extends Edge> getLeavingEdgeIterator()
	{
		return leavingEdges.iterator();
	}

	/* @see org.miv.graphstream.graph.Node */
	public Collection<? extends Edge> getLeavingEdgeSet()
	{
		return Collections.unmodifiableCollection(leavingEdges);
	}

	/* @see org.miv.graphstream.graph.Node */
	public Iterator<? extends Node> getNeighborNodeIterator()
	{
		throw new UnsupportedOperationException( "Method not implemented." );
	}

	/* @see org.miv.graphstream.graph.Node */
	public int getOutDegree()
	{
		return leavingEdges.size();
	}

	/* @see org.miv.graphstream.graph.Node */
	public boolean hasEdgeFrom(String id)
	{
		return getEdgeFrom(id) != null;
	}

	/* @see org.miv.graphstream.graph.Node */
	public boolean hasEdgeToward(String id)
	{
		return getEdgeToward(id) != null;
	}

	/* @see org.miv.graphstream.graph.Node */
	public boolean isDistributed()
	{
		return false;
	}

	/* @see org.miv.graphstream.graph.Node */
	public void setGraph(Graph graph)
	{
		this.graph = graph;
		
		if( graph != null && ! ( graph instanceof ConcurrentGraph ) )
		{
			System.err.printf( "[W] use a ConcurrentGraph for full performances\n" );
		}
	}

	/* @see org.miv.graphstream.graph.Node */
	public void setGraphName(String newHost)
	{
	}

	/* @see org.miv.graphstream.graph.Node */
	public void setHost(String newHost)
	{
	}
	
// --- //

	class FullEdgeIterator implements Iterator<Edge>
	{
		Iterator<Edge> current = null;
		boolean remaining = true;
		
		public FullEdgeIterator()
		{
			current = leavingEdges.iterator();
		}
		
		public Edge next()
		{
			if( current == null && remaining )
			{
				remaining = false;
				current = enteringEdges.iterator();
			}
			
			return current != null && current.hasNext() ? current.next() : null;
		}
		
		public boolean hasNext()
		{
			if( current == null && remaining )
			{
				remaining = false;
				current = enteringEdges.iterator();
			}
			
			return current != null && current.hasNext();
		}
		
		public void remove()
		{
			throw new UnsupportedOperationException( "this iterator does not allow removing" );
		}
	}
}
