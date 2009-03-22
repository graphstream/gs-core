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
* A thread-safe edge to use with ConcurrentGraph.
* </p>
* 
* @since 20090108
* 
*/
public class ConcurrentEdge
	extends AbstractConcurrentElement 
	implements Edge
{
	ConcurrentNode source;
	ConcurrentNode target;
	
	boolean directed = false;
	
	protected ConcurrentEdge( String id, Node src, Node dst )
	{
		super( id );
		
		if( ( src != null && ! ( src instanceof ConcurrentNode ) ) ||
			( dst != null && ! ( dst instanceof ConcurrentNode ) ) )
			throw new ClassCastException( "ConcurrentEdge needs an " +
				"extended class ConcurrentNode" );
		
		source = (ConcurrentNode) src;
		target = (ConcurrentNode) dst;

		source.registerEdge(this);
		target.registerEdge(this);
	}
	
// --- AbstractConcurrentElement --- //
	
	@Override
	protected void attributeChanged(String attribute, Object oldValue,
			Object newValue)
	{
		if( source != null && source.getGraph() instanceof ConcurrentGraph )
		{
			((ConcurrentGraph) source.getGraph()).edgeAttributeChangedEvent( this, attribute, oldValue, newValue );
		}
	}
	
	protected void attributeAdded( String attribute, Object value )
	{
		if( source != null && source.getGraph() instanceof ConcurrentGraph )
		{
			((ConcurrentGraph) source.getGraph()).edgeAttributeAddedEvent( this, attribute, value );
		}
	}
	
	protected void attributeRemoved( String attribute )
	{
		if( source != null && source.getGraph() instanceof ConcurrentGraph )
		{
			((ConcurrentGraph) source.getGraph()).edgeAttributeRemovedEvent( this, attribute );
		}
	}
	
// --- //
// --- Edge implementation --- //

	/* @Override */
	public Node getNode0()
	{
		return source;
	}

	/* @Override */
	public Node getNode1()
	{
		return target;
	}

	/* @Override */
	public Node getOpposite(Node node)
	{
		if( node == source ) return target;
		if( node == target ) return source;
		
		return null;
	}

	/* @Override */
	public Node getSourceNode()
	{
		return source;
	}

	/* @Override */
	public Node getTargetNode()
	{
		return target;
	}

	/* @Override */
	public boolean isDirected()
	{
		return directed;
	}

	/* @Override */
	public void setDirected(boolean on)
	{
		directed = on;
	}

	/* @Override */
	public void switchDirection()
	{
		if( source != null && source.getGraph() instanceof ConcurrentGraph )
			( (ConcurrentGraph) source.getGraph() ).edgeRemovedEvent( this );
		
		ConcurrentNode tmp = source;
		
		source = target;
		target = tmp;
		
		if( source != null && source.getGraph() instanceof ConcurrentGraph )
			( (ConcurrentGraph) source.getGraph() ).edgeAddedEvent( this );
	}

// --- //
	
}
