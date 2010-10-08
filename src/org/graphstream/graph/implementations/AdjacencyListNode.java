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

import java.util.ArrayList;
import java.util.Iterator;

import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.DepthFirstIterator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.SourceBase.ElementType;

/**
 * <p>
 * A lightweight node class intended to allow the construction of big graphs
 * (millions of elements).
 * </p>
 * <p>
 * The main purpose here is to minimize memory consumption even if the
 * management of such a graph implies more CPU consuming. See the
 * <code>complexity</code> tags on each method so as to figure out the impact on
 * the CPU.
 * </p>
 * 
 * @since July 12 2007
 * 
 */
public class AdjacencyListNode extends AbstractElement implements Node {
	private class EnteringEdgeIterator<T extends Edge> implements Iterator<T> {
		public AdjacencyListNode n;

		public int index = 0;

		public int nbEntering = 0;

		public int nb = 0;

		public EnteringEdgeIterator(AdjacencyListNode n) {
			this.n = n;

			for (Edge e : edges) {
				if (e.isDirected()) {
					if (e.getTargetNode() == n) {
						nbEntering++;
					}
				} else {
					nbEntering++;
				}
			}
		}

		public boolean hasNext() {
			return (index < edges.size() && nb < nbEntering);
		}

		@SuppressWarnings("unchecked")
		public T next() {
			if (hasNext()) {
				while (edges.get(index).isDirected()
						&& edges.get(index).getTargetNode() != n) {
					index++;
				}
				nb++;
				return (T) edges.get(index++);
			}
			return null;
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"this iterator does not allow removing");

		}
	}

	public class LeavingEdgeIterator<T extends Edge> implements Iterator<T> {
		public AdjacencyListNode n;

		int index = 0;

		int nbLeaving = 0;

		int nb = 0;

		public LeavingEdgeIterator(AdjacencyListNode n) {
			this.n = n;
			for (Edge e : edges) {
				if (e.isDirected()) {
					if (e.getSourceNode() == n) {
						nbLeaving++;
					}
				} else {
					nbLeaving++;
				}
			}
		}

		public boolean hasNext() {
			return (index < edges.size() && nb < nbLeaving);
		}

		@SuppressWarnings("unchecked")
		public T next() {
			if (hasNext()) {
				while ((edges.get(index).isDirected())
						&& (edges.get(index).getSourceNode() != n)) {
					index++;
				}
				nb++;
				return (T) edges.get(index++);
			}
			return null;
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"this iterator does not allow removing");

		}
	}

	public class EdgeIterator<T extends Edge> implements Iterator<T> {
		int index = 0;

		public boolean hasNext() {
			if (index < edges.size()) {
				return true;
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		public T next() {
			if (hasNext()) {
				return (T) edges.get(index++);
			}
			return null;
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"this iterator does not allow removing");
		}
	}

	public class NeighborNodeIterator<T extends Node> implements Iterator<T> {
		int index = 0;
		Node node;

		public NeighborNodeIterator(Node node) {
			this.node = node;
		}

		public boolean hasNext() {
			if (index < edges.size()) {
				return true;
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		public T next() {
			if (hasNext()) {
				Edge edge = edges.get(index++);

				return (T) edge.getOpposite(node);
			}

			return null;
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"this iterator does not allow removing");
		}
	}

	public class EdgeIterable<T extends Edge> implements Iterable<T> {
		protected Iterator<? extends T> iterator;

		public EdgeIterable(Iterator<? extends T> iterator) {
			this.iterator = iterator;
		}

		@SuppressWarnings("unchecked")
		public Iterator<T> iterator() {
			return (Iterator<T>) iterator;
		}
	}

	ArrayList<Edge> edges;

	Graph graph;

	/**
	 * Constructs a node for the given graph with the given identifier.
	 * 
	 * @param graph
	 *            The graph this node will be added to.
	 * @param id
	 *            This node's unique identifier.
	 */
	public AdjacencyListNode(Graph graph, String id) {
		super(id);
		this.graph = graph;
		edges = new ArrayList<Edge>();
	}

	@Override
	protected String myGraphId() // XXX
	{
		return graph.getId();
	}

	@Override
	protected long newEvent() // XXX
	{
		return ((AdjacencyListGraph) graph).newEvent();
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
		return (T) edges.get(i);
	}

	/**
	 * @complexity 0(n+d) with d being the degree of the node and n the number
	 *             nodes in the graph.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEdgeFrom(String id) {
		Node n = ((AdjacencyListGraph) graph).lookForNode(id);

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

	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return new EdgeIterator<T>();
	}

	public Iterator<Edge> iterator() {
		return (Iterator<Edge>) getEdgeIterator();
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterable<T> getEdgeSet() {
		return (Iterable<T>) edges;
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEdgeToward(String id) {
		Node n = ((AdjacencyListGraph) graph).lookForNode(id);
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
		return new EnteringEdgeIterator<T>(this);
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterable<T> getEnteringEdgeSet() {
		return new EdgeIterable<T>((Iterator<T>) getEnteringEdgeIterator());
	}

	public Graph getGraph() {
		return graph;
	}

	public int getInDegree() {
		EnteringEdgeIterator<?> it = new EnteringEdgeIterator<Edge>(this);
		return it.nbEntering;
	}

	public <T extends Edge> Iterator<T> getLeavingEdgeIterator() {
		return new LeavingEdgeIterator<T>(this);
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterable<T> getLeavingEdgeSet() {
		return new EdgeIterable<T>((Iterator<T>) getLeavingEdgeIterator());
	}

	public <T extends Node> Iterator<T> getNeighborNodeIterator() {
		return new NeighborNodeIterator<T>(this);
	}

	public int getOutDegree() {
		LeavingEdgeIterator<?> it = new LeavingEdgeIterator<Edge>(this);
		return it.nbLeaving;
	}

	public boolean hasEdgeFrom(String id) {
		Node n = ((AdjacencyListGraph) graph).lookForNode(id);
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
	public <T extends Edge> T hasEdgeFrom(Node n) {
		if (n != null) {
			Iterator<T> it = new EnteringEdgeIterator<T>(this);

			while (it.hasNext()) {
				T e = it.next();
				if (e.isDirected()) {
					if (e.getSourceNode() == n)
						return e;
				} else
					return e;
			}
		}

		return null;
	}

	public boolean hasEdgeToward(String id) {
		Node n = ((AdjacencyListGraph) graph).lookForNode(id);
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
	public <T extends Edge> T hasEdgeToward(Node n) {
		if (n != null) {
			Iterator<T> it = new LeavingEdgeIterator<T>(this);

			while (it.hasNext()) {
				T e = it.next();
				if (e.isDirected()) {
					if (e.getTargetNode() == n)
						return e;
				} else {
					if (e.getTargetNode() == n || e.getSourceNode() == n)
						return e;
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
			((AdjacencyListGraph) graph).listeners.sendAttributeChangedEvent(
					sourceId, timeId, getId(), ElementType.NODE, attribute,
					event, oldValue, newValue);
	}
}