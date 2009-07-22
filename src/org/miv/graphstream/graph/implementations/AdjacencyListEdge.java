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

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Node;

/**
  * <p>
 * A light edge class intended to allow the construction of big graphs
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
public class AdjacencyListEdge
	extends AbstractElement implements Edge
{

	AdjacencyListNode n0;

	AdjacencyListNode n1;

	boolean directed = false;
	

	/**
	 * @param id
	 */
	protected AdjacencyListEdge( String id, Node src, Node dst, boolean directed )
	{
		super( id );
		
		if( ( src != null && ! ( src instanceof AdjacencyListNode ) ) ||
			( dst != null && ! ( dst instanceof AdjacencyListNode ) ) )
			throw new ClassCastException( "AdjacencyListEdge needs an " +
					"extended class AdjacencyListNode" );
		
		this.n0 = (AdjacencyListNode) src;
		this.n1 = (AdjacencyListNode) dst;
		this.directed = directed;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.EdgeInterface#getNode0()
	 */
	public Node getNode0()
	{
		return n0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.EdgeInterface#getNode1()
	 */
	public Node getNode1()
	{
		return n1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.EdgeInterface#getOpposite(org.miv.graphstream.graph.NodeInterface)
	 */
	public Node getOpposite( Node node )
	{
		if( node == n0 )
			return n1;
		else if( node == n1 )
			return n0;
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.EdgeInterface#getSourceNode()
	 */
	public Node getSourceNode()
	{
		return n0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.EdgeInterface#getTargetNode()
	 */
	public Node getTargetNode()
	{
		return n1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.EdgeInterface#isDirected()
	 */
	public boolean isDirected()
	{
		return directed;
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.EdgeInterface#setDirected(boolean)
	 */
	public void setDirected( boolean on )
	{
		( (AdjacencyListGraph) n0.getGraph() ).beforeEdgeRemoveEvent( this );
		this.directed = on;
		( (AdjacencyListGraph) n0.getGraph() ).afterEdgeAddEvent( this );
	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.EdgeInterface#switchDirection()
	 */
	public void switchDirection()
	{
		( (AdjacencyListGraph) n0.getGraph() ).beforeEdgeRemoveEvent( this );
		
		AdjacencyListNode n = n0;
		n0 = n1;
		n1 = n;
		( (AdjacencyListGraph) n0.getGraph() ).afterEdgeAddEvent( this );
	}



	/* (non-Javadoc)
	 * @see org.miv.graphstream.graph.Element#attributeChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void attributeChanged( String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
	{
		if( n0 != null )
			( (AdjacencyListGraph) n0.getGraph() ).attributeChangedEvent( this, attribute, event, oldValue, newValue );
	}
}