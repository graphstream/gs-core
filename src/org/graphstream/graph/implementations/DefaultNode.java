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
package org.graphstream.graph.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.DepthFirstIterator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.stream.SourceBase;

/**
 * Base implementation of a {@link org.graphstream.graph.Node} for the
 * {@link DefaultGraph}.
 * 
 * <p>
 * This node ensures the consistency of the graph. Such a node is able to give
 * informations about all leaving edges but also entering edges (when directed),
 * at the price however of a larger memory footprint.
 * </p>
 * 
 * <p>
 * This is a base implementation that is refined in two classes
 * {@link SingleNode} and {@link MultiNode}. The first allows only one edge
 * between two nodes, the other allows multiple edges between two nodes (or
 * several loops).
 * </p>
 * 
 * @since 20020709
 */
public abstract class DefaultNode extends AbstractElement implements Node {
	// Constant

	/**
	 * Property used to store labels.
	 */
	public static final String ATTRIBUTE_LABEL = "label";

	// Attribute

	/**
	 * Parent graph.
	 */
	protected DefaultGraph G;

	/**
	 * List of all entering or leaving edges the node knows.
	 */
	protected ArrayList<Edge> edges = new ArrayList<Edge>();

	// Construction

	/**
	 * New unconnected node.
	 * 
	 * @param graph
	 *            The graph containing the node.
	 * @param id
	 *            Tag of the node.
	 */
	public DefaultNode(Graph graph, String id) {
		super(id);
		G = (DefaultGraph) graph;
	}

	// Access

	public Graph getGraph() {
		return G;
	}

	public int getDegree() {
		return edges.size();
	}

	public abstract int getOutDegree();

	public abstract int getInDegree();

	public abstract boolean hasEdgeToward(String id);

	public abstract boolean hasEdgeFrom(String id);
	
	public boolean hasEdgeBetween(String id) {
		return( hasEdgeToward(id) || hasEdgeFrom(id) );
	}

	public abstract <T extends Edge> T getEdgeToward(String id);

	public abstract <T extends Edge> T getEdgeFrom(String id);

	public abstract <T extends Edge> T getEdgeBetween(String id);
	
	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return (Iterator<T>) new ElementIterator<Edge>(edges);
	}

	public abstract <T extends Edge> Iterator<T> getEnteringEdgeIterator();

	public abstract <T extends Edge> Iterator<T> getLeavingEdgeIterator();

	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getNeighborNodeIterator() {
		return new NeighborNodeIterator<T>((T) this);
	}

	public Iterator<Edge> iterator() {
		return (Iterator<Edge>) getEdgeIterator();
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEdge(int i) {
		return (T) edges.get(i);
	}

	/**
	 * @complexity Same as the breath first iterator: O(n+m) with n the number
	 *             of nodes and m the number of edges.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getBreadthFirstIterator() {
		return new BreadthFirstIterator<T>((T) this);
	}

	/**
	 * @complexity Same as the breath first iterator: O(n+m) with n the number
	 *             of nodes and m the number of edges.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getBreadthFirstIterator(boolean directed) {
		return new BreadthFirstIterator<T>((T) this);
	}

	/**
	 * @complexity Same as the depth first iterator: O(n+m) with n the number of
	 *             nodes and m the number of edges.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getDepthFirstIterator() {
		return new DepthFirstIterator<T>((T) this);
	}

	/**
	 * @complexity Same as the depth first iterator: O(n+m) with n the number of
	 *             nodes and m the number of edges.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getDepthFirstIterator(boolean directed) {
		return new DepthFirstIterator<T>((T) this);
	}

	// Access -- Not in Node interface

	@Override
	protected String myGraphId() {
		if (G != null)
			return G.getId();

		throw new RuntimeException("WTF ?");
	}

	@Override
	protected long newEvent() {
		if (G != null)
			return G.newEvent();

		throw new RuntimeException("WTF ?");
	}
	
	@Override
	protected boolean nullAttributesAreErrors() {
		return G.nullAttributesAreErrors();
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterable<T> getEachEdge() {
		return (Iterable<T>) edges;
	}

	public abstract <T extends Edge> Iterable<T> getEachLeavingEdge();

	public abstract <T extends Edge> Iterable<T> getEachEnteringEdge();

	@SuppressWarnings("unchecked")
	public <T extends Edge> Collection<T> getEdgeSet() {
		return (Collection<T>) Collections.unmodifiableCollection(edges);
	}

	public abstract <T extends Edge> Collection<T> getLeavingEdgeSet();

	public abstract <T extends Edge> Collection<T> getEnteringEdgeSet();

	// Command

	/**
	 * Add an edge between this node and the given target.
	 * 
	 * @param tag
	 *            Tag of the edge.
	 * @param target
	 *            Target node.
	 * @param directed
	 *            If the edge is directed only from this node to the target.
	 * @return A reference to the created edge.
	 */
	protected abstract <T extends Edge> T addEdgeToward(String tag,
			DefaultNode target, boolean directed)
			throws IllegalArgumentException;

	/**
	 * Called by an edge to bind it.
	 */
	protected abstract void registerEdge(Edge edge)
			throws IllegalArgumentException, IdAlreadyInUseException;

	protected abstract void unregisterEdge(Edge edge);

	/**
	 * When a node is unregistered from a graph, it must not keep edges
	 * connected to nodes still in the graph. This methods untie all edges
	 * connected to this node (this also unregister them from the graph).
	 */
	protected abstract void disconnectAllEdges() throws IllegalStateException;

	@Override
	protected void attributeChanged(String sourceId, long timeId,
			String attribute, AttributeChangeEvent event, Object oldValue,
			Object newValue) {
		if (G != null)
			G.listeners.sendAttributeChangedEvent(sourceId, timeId, getId(),
					SourceBase.ElementType.NODE, attribute, event, oldValue,
					newValue);
	}

	@Override
	public String toString() {
		// return String.format( "[node %s (%d edges)]", getId(), edges.size()
		// );
		return getId();
	}

	// Nested classes

	protected class NeighborNodeIterator<T extends Node> implements Iterator<T> {
		protected int i;

		protected T n;

		protected NeighborNodeIterator(T node) {
			i = 0;
			n = node;
		}

		public boolean hasNext() {
			return (i < edges.size());
		}

		public T next() throws NoSuchElementException {
			Edge e = edges.get(i++);
			return e.getOpposite(n);
		}

		public void remove() throws UnsupportedOperationException,
				IllegalStateException {
			throw new UnsupportedOperationException(
					"this iterator does not allow removing");
		}

	}

	static class ElementIterator<T extends Element> implements Iterator<T> {
		Iterator<? extends T> iterator;

		ElementIterator(ArrayList<T> elements) {
			iterator = elements.iterator();
		}

		ElementIterator(HashMap<String, ? extends T> elements) {
			iterator = elements.values().iterator();
		}

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public T next() {
			return iterator.next();
		}

		public void remove() throws UnsupportedOperationException,
				IllegalStateException {
			throw new UnsupportedOperationException(
					"this iterator does not allow removing");
		}
	}
}