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
public class DepthFirstIterator<T extends Node> implements Iterator<T> {
	// Attributes

	/**
	 * Respect the edge orientation?.
	 */
	protected boolean directed = true;

	/**
	 * Set of already explored nodes.
	 */
	protected HashSet<T> closed = new HashSet<T>();

	/**
	 * Nodes remaining to process. The iteration continues as long as this array
	 * is not empty.
	 */
	protected LinkedList<T> lifo = new LinkedList<T>();

	// Constructors

	/**
	 * New breadth-first iterator starting at the given start node.
	 * 
	 * @param startNode
	 *            The node where the graph exploration begins.
	 */
	public DepthFirstIterator(T startNode) {
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
	public DepthFirstIterator(T startNode, boolean directed) {
		lifo.add(startNode);
		closed.add(startNode);
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
	 * Is the given node tabu?.
	 * 
	 * @param node
	 *            The node to test.
	 * @return True if tabu.
	 */
	protected boolean tabu(T node) {
		return (closed.contains(node));
	}

	/**
	 * Is there a next node to process?.
	 * 
	 * @return True if it remains nodes.
	 */
	public boolean hasNext() {
		return lifo.size() > 0;
	}

	/**
	 * Next node to process.
	 * 
	 * @return The next node.
	 */
	public T next() throws NoSuchElementException {
		if (lifo.size() > 0) {
			T next = lifo.removeLast();

			closed.add(next);
			while (lifo.remove(next))
				;

			addNeighborsOf(next);

			return next;
		} else {
			throw new NoSuchElementException("no more elements in iterator");
		}
	}

	/**
	 * Append the neighbors of the given node (excepted nodes already processed)
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

		while (k.hasNext()) {
			Edge edge = k.next();
			T adj = (T) edge.getOpposite(node);

			if (!tabu(adj)) {
				lifo.add(adj);
			}
		}
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