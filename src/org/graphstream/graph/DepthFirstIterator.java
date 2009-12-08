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

package org.graphstream.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Iterator allowing to explore a graph in a depth-first way.
 *
 * @complexity O(n+m) with n the number of nodes and m the number of edges.
 * @since 20040730
 */
public class DepthFirstIterator implements Iterator<Node>
{
//	 Attributes

	/**
	 * Respect the edge orientation?.
	 */
	protected boolean directed = true;
	
	/**
	 * Set of already explored nodes.
	 */
	protected HashSet<Node> closed = new HashSet<Node>();

	/**
	 * Nodes remaining to process. The iteration continues as long
	 * as this array is not empty.
	 */
	protected LinkedList<Node> lifo = new LinkedList<Node>();

// Constructors

	/**
	 * New breadth-first iterator starting at the given start node.
	 * @param startNode The node where the graph exploration begins.
	 */
	public DepthFirstIterator( Node startNode )
	{
		this( startNode, true );
	}
	
	/**
	 * New breadth-first iterator starting at the given start node.
	 * @param startNode The node where the graph exploration begins.
	 * @param directed If true the iterator respects the edge direction (the
	 *        default).
	 */
	public DepthFirstIterator( Node startNode, boolean directed )
	{
		lifo.add( startNode );
		closed.add( startNode );
		this.directed = directed;
	}

// Accessors

	/**
	 * Is this iterator respecting edge orientation ?.
	 * @return True if edge orientation is respected (the default).
	 */
	public boolean isDirected()
	{
		return directed;
	}
	
	/**
	 * Is the given node tabu?.
	 * @param node The node to test.
	 * @return True if tabu.
	 */
	protected boolean tabu( Node node )
	{
		return( closed.contains( node )  );
	}

	/**
	 * Is there a next node to process?.
	 * @return True if it remains nodes.
	 */
	public boolean hasNext()
	{
		return lifo.size() > 0;
	}

	/**
	 * Next node to process.
	 * @return The next node.
	 */
	public Node next()
		throws NoSuchElementException
	{
		if( lifo.size() > 0 )
		{
			Node next = lifo.removeLast();

			closed.add(next);
			while(lifo.remove(next));
			
			addNeighborsOf( next );
			
			return next;
		}
		else
		{
			throw new NoSuchElementException( "no more elements in iterator" );
		}
	}

	/**
	 * Append the neighbors of the given node (excepted nodes already
	 * processed) in the list of nodes to process next.
	 * @param node The nodes the neighbors are to be processed.
	 */
	protected void addNeighborsOf( Node node )
	{
		Iterator<? extends Edge> k;
		
		if( directed )
		     k = node.getLeavingEdgeIterator();
		else k = node.getEdgeIterator();

		while( k.hasNext() )
		{
			Edge edge = k.next();
			Node adj  = edge.getOpposite( node );

			if( ! tabu( adj ) )
			{
				lifo.add( adj );
			}
		}
	}

// Commands

	/**
	 * Unsupported with this iterator.
	 */
	public void remove()
		throws UnsupportedOperationException, IllegalStateException
	{
		throw new UnsupportedOperationException( "cannot remove a node using this iterator (yet)" );
	}
}