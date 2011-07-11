/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

/**
 * Iterator allowing to explore a graph in a breadth-first way.
 * 
 * This iterator also allows to compute the depth or each node (number of edges
 * crossed to reach a node from the starting node) as well as the maximum
 * depth. Be careful that this depth does not take eventual weights of edges,
 * it only counts edges. The depth of each node and the depth max are known
 * only when all the nodes have been processed by this iterator (only when
 * {@link #hasNext()} returns false). 
 * 
 * @complexity O(n+m) with n the number of nodes and m the number of edges.
 * @since 20040730
 */
public class BreadthFirstIterator<T extends Node> implements Iterator<T> {
	// Attributes

	/**
	 * Respect the edge orientation?.
	 */
	protected boolean directed = true;

	/**
	 * Already processed nodes.
	 */
	protected HashMap<T,Integer> closed = new HashMap<T,Integer>();

	/**
	 * Nodes remaining to process. The iteration continues as long as this array
	 * is not empty.
	 */
	protected LinkedList<T> open = new LinkedList<T>();

	/**
	 * Maximum depth.
	 */
	protected int depthMax = 0;
	
	// Constructors

	/**
	 * New breadth-first iterator starting at the given start node.
	 * 
	 * @param startNode
	 *            The node where the graph exploration begins.
	 */
	public BreadthFirstIterator(T startNode) {
		this(startNode, true);
	}

	/**
	 * New breadth-first iterator starting at the given start node.
	 * 
	 * @param startNode
	 *            The node where the graph exploration begins.
	 * @param directed
	 *            If true the iterator respects the edge direction (the
	 *            default).
	 */
	public BreadthFirstIterator(T startNode, boolean directed) {
		open.add(startNode);
		closed.put(startNode,0);
		this.directed = directed;
	}

	// Accessors

	/**
	 * Is this iterator respecting edge orientation ?.
	 * 
	 * @return True if edge orientation is respected (the default).
	 */
	public boolean isDirected() {
		return directed;
	}
	
	/**
	 * Depth of the more distant node from the starting point of the BFS, this is also known as
	 * the eccentricity if the graph non-weighted. This method can only be used reliably after all
	 * the nodes have been processed.
	 * 
	 * @return The maximum depth of a node from the starting node (in edge jumps).
	 */
	public int getDepthMax() {
		return depthMax;
	}
	
	/**
	 * Depth of a given node. The depth here is minimal number of edges to cross to reach
	 * the given node from the starting node of the BFS. Weights are not used. 
	 * This method can only be used reliably after all the nodes
	 * have been processed, if the depth has not been computed -1 is returned.
	 * 
	 * @param node
	 * 			The node for which the depth is required.
	 * @return The depth of the given node.
	 */
	public int getDepthOf(Node node) {
		Integer i = closed.get(node);
		
		if(i != null)
			return (int)i;
		
		return -1;
	}
	
	/**
	 * Is the given node tabu?.
	 * 
	 * Tabu nodes are nodes that have already been discovered (and are therefore either
	 * in closed or open or both).
	 * 
	 * @param node
	 *            The node to test.
	 * @return True if tabu.
	 */
	protected boolean tabu(T node) {
		return closed.containsKey(node);
	}

	/**
	 * Is there a next node to process?.
	 * 
	 * @return True if it remains nodes.
	 */
	public boolean hasNext() {
		return open.size() > 0;
	}

	/**
	 * Next node to process, in BF order.
	 * 
	 * @return The next node.
	 */
	public T next() throws NoSuchElementException {
		if (open.size() > 0) {
			T next = open.removeFirst();

			addNeighborsOf(next);

			return next;
		} else {
			throw new NoSuchElementException("no more elements in iterator");
		}
	}

	/**
	 * Append the neighbors of the given node (excepted nodes already processed/marked)
	 * in the list of nodes to process next.
	 * 
	 * @param node
	 *            The nodes the neighbors are to be processed.
	 */
	protected void addNeighborsOf(T node) {
		Iterator<? extends Edge> k;

		if (directed)
			k = node.getLeavingEdgeIterator();
		else
			k = node.getEdgeIterator();

		boolean found = false;
		int curDepth = ((int)closed.get(node)) + 1;
		
		while (k.hasNext()) {
			Edge edge = k.next();
			T adj = edge.getOpposite(node);

			if (!tabu(adj)) {
				open.add(adj);
				closed.put(adj, curDepth);
				found = true;
			}
		}
		
		if(found && curDepth > depthMax)
			depthMax = curDepth;
	}

	// Commands

	/**
	 * Unsupported with this iterator.
	 */
	public void remove() throws UnsupportedOperationException,
			IllegalStateException {
		throw new UnsupportedOperationException(
				"cannot remove a node using this iterator (yet)");
	}
}