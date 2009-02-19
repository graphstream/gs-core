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

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Node;
import org.miv.util.*;

/**
 * Connection between two nodes.
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20020709
 */
public abstract class DefaultEdge extends AbstractElement implements Edge
{
// Attributes

	/**
	 * Is this edge directed.
	 */
	protected boolean directed = false;

	/**
	 * Source node (when directed).
	 */
	protected DefaultNode src = null;

	/**
	 * Destination node (when directed).
	 */
	protected DefaultNode trg = null;

// Constructors

	/**
	 * New edge between a source node and target node. If the directed argument
	 * is true the edge is directed from the source to the target. The edge
	 * registers itself into the nodes and the graph.
	 * @param tag The edge unique id.
	 * @param source The origin node of the edge.
	 * @param target The destination node of the edge.
	 * @param directed Is the order source to target important?.
	 * @throws IllegalArgumentException If the source and or the target are not
	 *         part of a graph or not part of the same graph.
	 * @throws SingletonException If the source or the target already reference
	 *         this edge or if an edge with the same id already exists.
	 */
	protected DefaultEdge( String tag, DefaultNode source, DefaultNode target, boolean directed )
		throws IllegalStateException, SingletonException
	{
		super( tag );
		bind( source, target, directed );
	}

	protected DefaultEdge()
	{
		super( "" );
	}
	
// Getters

	public boolean isDirected()
	{
		return directed;
	}

	public Node getNode0()
	{
		return src;
	}

	public Node getNode1()
	{
		return trg;
	}
	
	public Node getSourceNode()
	{
		return src;
	}

	public Node getTargetNode()
	{
		return trg;
	}

	public Node getOpposite( Node node )
	{
		if( src == node )return trg;
		else if( trg == node ) return src;
		
		return null;
	}

	/**
	 * Override the Object.toString() method.
	 */
	@Override
	public String toString()
	{
		return String.format( "[edge %s (%s -> %s)]", id, src, trg );
	}

// Commands

	public void setDirected( boolean on )
	{
		if( directed != on )
		{
			src.G.beforeEdgeRemoveEvent( this );
		
			src.unregisterEdge( this );
			trg.unregisterEdge( this );
			
			directed = on;
			
			src.registerEdge( this );
			trg.registerEdge( this );
		
			src.G.afterEdgeAddEvent( this );
		}
	}
	
	public void switchDirection()
	{
		src.G.beforeEdgeRemoveEvent( this );
		
		src.unregisterEdge( this );
		trg.unregisterEdge( this );
		
		DefaultNode tmp;
		
		tmp = src;
		src = trg;
		trg = tmp;
		
		src.registerEdge( this );
		trg.registerEdge( this );
		
		src.G.afterEdgeAddEvent( this );
	}

	/**
	 * Bind this edge to the given source node and target node. If
	 * directed is true, the edge goes from source to target, else
	 * this is a bidirectional edge. The edge is also registered in the graph
	 * of the two nodes.
	 * @throws IllegalStateException if the edge is already bound, or if
	 * source is not part of the same graph than target or one is not part of a
	 * graph, or if the edge has no ID yet.
	 * @throws SingletonException if source or target already register an edge
	 * with the same name.
	 */
	protected void bind( DefaultNode source, DefaultNode target, boolean directed )
		throws IllegalStateException, SingletonException
	{
		if( src != null || trg != null )
			throw new IllegalStateException(
				"edge already bound, call rebind(), not bind()" );

		// Store information.

		this.directed = directed;
		src           = source;
		trg           = target;

		// Register in the nodes.

		src.registerEdge( this );
		trg.registerEdge( this );
	}

	/**
	 * Unregister from the attached nodes. Can be called if the edge is not
	 * bound. The edge is unregistered from the graph of the nodes it
	 * connected. This operation removes the ID of the edge.
	 * @throws IllegalStateException If the edge is partially bound (to only
	 * one node) or bound to non existing nodes.
	 */
	protected void unbind()
		throws IllegalStateException
	{
		DefaultGraph g;

		if( src != null || trg != null )
		{
			if( ( src != null && trg == null ) || ( trg != null && src == null ) )
				throw new IllegalStateException( "inconsistency, edge `" + this.id + "' is half bound" );

			src.unregisterEdge( this );
			trg.unregisterEdge( this );
		}
		else if( src == null && trg == null )
		{
			throw new IllegalStateException( "inconsistency, edge '" + this.id + "' is not bound" );
		}
		
		g = (DefaultGraph) src.getGraph();
		g.beforeEdgeRemoveEvent( this );

		id = null;
		src = null;
		trg = null;
	}

	@Override
	protected void attributeChanged( String attribute, Object oldValue, Object newValue )
	{
		if( src != null )
			( (DefaultGraph) src.getGraph() ).attributeChangedEvent( this, attribute, oldValue, newValue );
	}
}