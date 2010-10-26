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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.graph.implementations;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.DepthFirstIterator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;
import org.graphstream.stream.SourceBase.ElementType;

/**
 * <p>
 * An implementation of a Node with multi-thread capabilities.
 * </p>
 * <p>
 * It is similar to the
 * {@link org.graphstream.graph.implementations.AdjacencyListNode} class, but
 * with thread-safe data structures.
 * </p>
 * <p>
 * Time and memory complexity is comparable to the values given in
 * {@link org.graphstream.graph.implementations.AdjacencyListNode}. Consider
 * some time overhead due to the thread synchronization machinery.
 * </p>
 * 
 * @see org.graphstream.graph.implementations.AdjacencyListNode
 */

public class ConcurrentNode extends AbstractConcurrentElement implements Node {
	public class NeighborNodeIterator<T extends Node> implements Iterator<T> {
		T node;
		Iterator<Edge> ite;

		public NeighborNodeIterator(T node) {
			this.node = node;
			ite = ((ConcurrentNode) node).edges.iterator();
		}

		public boolean hasNext() {
			return ite.hasNext();
		}

		public T next() {
			if (hasNext())
				return ite.next().getOpposite(node);

			return null;
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"this iterator does not allow removing");
		}
	}

	public class EdgeIterable<T extends Edge> implements Iterable<T> {
		protected Iterator<T> iterator;

		public EdgeIterable(Iterator<T> iterator) {
			this.iterator = iterator;
		}

		public Iterator<T> iterator() {
			return iterator;
		}
	}

	ConcurrentLinkedQueue<Edge> edges;

	Graph graph;

	public ConcurrentNode(Graph graph, String id) {
		super(id);
		this.graph = graph;
		edges = new ConcurrentLinkedQueue<Edge>();
	}

	@Override
	protected String myGraphId() // XXX
	{
		return graph.getId();
	}

	@Override
	protected long newEvent() // XXX
	{
		return ((ConcurrentGraph) graph).newEvent();
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getBreadthFirstIterator() {
		return new BreadthFirstIterator<T>((T) this);
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getBreadthFirstIterator(boolean directed) {
		return new BreadthFirstIterator<T>((T) this, directed);
	}

	public int getDegree() {
		return edges.size();
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getDepthFirstIterator() {
		return new DepthFirstIterator<T>((T) this);
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getDepthFirstIterator(boolean directed) {
		return new DepthFirstIterator<T>((T) this, directed);
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEdge(int i) {
		int j = 0;
		Iterator<Edge> ite = edges.iterator();

		while (ite.hasNext()) {
			if (i == j)
				return (T) ite.next();

			j++;
			ite.next();
		}

		return null;
	}

	/**
	 * @complexity 0(n+d) with d the degree of the node and n the number nodes
	 *             in the graph.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEdgeFrom(String id) {
		Node n = ((ConcurrentGraph) graph).lookForNode(id);

		if (n != null) {
			for (Edge e : edges) {
				if (e.getSourceNode() == n) {
					return (T) e;
				}
				if (!e.isDirected() && e.getTargetNode() == n) {
					return (T) e;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return (Iterator<T>) edges.iterator();
	}

	public Iterator<Edge> iterator() {
		return (Iterator<Edge>) getEdgeIterator();
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterable<T> getEachEdge() {
		return (Iterable<T>) edges;
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> Collection<T> getEdgeSet() {
		return (Collection<T>) Collections.unmodifiableCollection(edges);
	}
	
	public <T extends Edge> T getEdgeBetween(String id) {
		if (hasEdgeToward(id)) return getEdgeToward(id);
		else return getEdgeFrom(id);
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEdgeToward(String id) {
		Node n = ((ConcurrentGraph) graph).lookForNode(id);
		if (n != null) {
			for (Edge e : edges) {
				if (e.getTargetNode() == n) {
					return (T) e;
				}
				if (!e.isDirected() && e.getSourceNode() == n) {
					return (T) e;
				}
			}
		}
		return null;
	}

	public <T extends Edge> Iterator<T> getEnteringEdgeIterator() {
		throw new UnsupportedOperationException(
				"unsupported entering edge iterator");
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterable<T> getEachEnteringEdge() {
		return new EdgeIterable<T>((Iterator<T>) getEnteringEdgeIterator());
	}

	public <T extends Edge> Collection<T> getEnteringEdgeSet() {
		// Ah ah, this set does not exists, must create it.
		HashSet<T> set = new HashSet<T>();
		Iterator<T> k = getEnteringEdgeIterator();
		while(k.hasNext()) {
			set.add(k.next());
		}
		return set;
	}

	public Graph getGraph() {
		return graph;
	}

	public int getInDegree() {
		Iterator<Edge> ite = edges.iterator();

		int d = 0;
		Edge e;
		while (ite.hasNext()) {
			e = ite.next();
			if (e.getSourceNode() == this || !e.isDirected())
				d++;
		}

		return d;
	}

	public <T extends Edge> Iterator<T> getLeavingEdgeIterator() {
		throw new UnsupportedOperationException(
				"unsupported leaving edge iterator");
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterable<T> getEachLeavingEdge() {
		return new EdgeIterable<T>((Iterator<T>) getLeavingEdgeIterator());
	}

	public <T extends Edge> Collection<T> getLeavingEdgeSet() {
		// Ah ah, this set does not exists, must create it.
		HashSet<T> set = new HashSet<T>();
		Iterator<T> k = getLeavingEdgeIterator();
		while(k.hasNext()) {
			set.add(k.next());
		}
		return set;
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getNeighborNodeIterator() {
		return new NeighborNodeIterator<T>((T) this);
	}

	public int getOutDegree() {
		Iterator<Edge> ite = edges.iterator();

		int d = 0;
		Edge e;
		while (ite.hasNext()) {
			e = ite.next();
			if (e.getTargetNode() == this || !e.isDirected())
				d++;
		}

		return d;
	}
	
	public boolean hasEdgeBetween(String id) {
		return( hasEdgeToward(id) || hasEdgeFrom(id) );
	}

	public boolean hasEdgeFrom(String id) {
		Node n = ((ConcurrentGraph) graph).lookForNode(id);
		return hasEdgeFrom(n) == null ? false : true;
	}

	/**
	 * Tries to find in the edges of this node the one that links the given node
	 * to the current one.
	 * 
	 * @return An reference to the edge coming from the given node if there is
	 *         one, null otherwise.
	 * @param n
	 *            The node we look for an edge towards.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> T hasEdgeFrom(Node n) {
		if (n != null) {
			Iterator<Edge> it = edges.iterator();

			while (it.hasNext()) {
				Edge e = it.next();

				if (e.isDirected()) {
					if (e.getSourceNode() == n)
						return (T) e;
				} else
					return (T) e;
			}
		}
		return null;
	}

	public boolean hasEdgeToward(String id) {
		Node n = ((ConcurrentGraph) graph).lookForNode(id);
		return hasEdgeToward(n) == null ? false : true;
	}

	/**
	 * Tries to find in the edges of this node the one that links the current
	 * node to the given one.
	 * 
	 * @return An reference to the edge leading to the given node if there is
	 *         one, null otherwise.
	 * @param n
	 *            The node we look for an edge towards.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> T hasEdgeToward(Node n) {
		if (n != null) {
			Iterator<Edge> it = edges.iterator();

			while (it.hasNext()) {
				Edge e = it.next();

				if (e.isDirected()) {
					if (e.getTargetNode() == n)
						return (T) e;
				} else {
					if (e.getTargetNode() == n || e.getSourceNode() == n)
						return (T) e;
				}
			}
		}
		return null;
	}

	@Override
	protected void attributeChanged(String sourceId, long timeId,
			String attribute, AttributeChangeEvent event, Object oldValue,
			Object newValue) {
		if (graph != null)
			((ConcurrentGraph) graph).listeners.sendAttributeChangedEvent(
					sourceId, timeId, getId(), ElementType.NODE, attribute,
					event, oldValue, newValue);
	}
}