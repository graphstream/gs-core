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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.IdAlreadyInUseException;

/**
 * Full implementation of {@link org.graphstream.graph.Node} that allows
 * multiple edges between two nodes.
 */
public class MultiNode extends DefaultNode {
	// Attributes

	/**
	 * Map of leaving edges toward nodes. Each element of the map is a pair
	 * (key,value) where the key is the id of a node that can be reached
	 * following a leaving edge, and the value is a set of all leaving edges
	 * toward this node.
	 */
	protected MultiEdgeMap<? extends Edge> to = new MultiEdgeMap<Edge>();

	/**
	 * Map of entering edges from nodes. Each element of the map is a pair
	 * (key,value) where the key is the id of a node that can be reached
	 * following an entering edge, and the value is a set of all entering edges
	 * from this node.
	 */
	protected MultiEdgeMap<? extends Edge> from = new MultiEdgeMap<Edge>();

	protected int inDegree = 0;

	protected int outDegree = 0;

	// Constructors

	/**
	 * New unconnected node.
	 * 
	 * @param graph
	 *            The graph containing the node.
	 * @param id
	 *            Tag of the node.
	 */
	public MultiNode(Graph graph, String id) {
		super(graph, id);
	}

	// Access

	@Override
	public int getOutDegree() {
		return outDegree;
	}

	@Override
	public int getInDegree() {
		return inDegree;
	}

	@Override
	public boolean hasEdgeToward(String id) {
		return (to.get(id) != null);
	}

	@Override
	public boolean hasEdgeFrom(String id) {
		return (from.get(id) != null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEdgeToward(String id) {
		ArrayList<Edge> edges = (ArrayList<Edge>) to.get(id);

		if (edges != null)
			return (T) edges.get(0);

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEdgeFrom(String id) {
		ArrayList<Edge> edges = (ArrayList<Edge>) from.get(id);

		if (edges != null)
			return (T) edges.get(0);

		return null;
	}
	
	@Override
	public <T extends Edge> T getEdgeBetween(String id) {
		if (hasEdgeToward(id)) return getEdgeToward(id);
		else return getEdgeFrom(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterator<T> getEnteringEdgeIterator() {
		return new MultiElementIterator<T>((MultiEdgeMap<T>) from);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterator<T> getLeavingEdgeIterator() {
		return new MultiElementIterator<T>((MultiEdgeMap<T>) to);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterable<T> getEachEnteringEdge() {
		return (Iterable<T>) from;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterable<T> getEachLeavingEdge() {
		return (Iterable<T>) to;
	}
	
	@Override
	public <T extends Edge> Collection<T> getEnteringEdgeSet() {
		// Ah ah, this set does not exists, must create it.
		HashSet<T> set = new HashSet<T>();
		Iterator<T> k = getEnteringEdgeIterator();
		while(k.hasNext()) {
			set.add(k.next());
		}
		return set;
	}

	@Override
	public <T extends Edge> Collection<T> getLeavingEdgeSet() {
		// Ah ah, this set does not exists, must create it.
		HashSet<T> set = new HashSet<T>();
		Iterator<T> k = getLeavingEdgeIterator();
		while(k.hasNext()) {
			set.add(k.next());
		}
		return set;
	}

	// Commands

	/**
	 * Add an edge between this node and the given target.
	 * 
	 * @param tag
	 *            Tag of the edge.
	 * @param target
	 *            Target node.
	 * @param directed
	 *            If the edge is directed only from this node to the target.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected <T extends Edge> T addEdgeToward(String tag, DefaultNode target,
			boolean directed) throws IllegalArgumentException {
		// Some checks.

		if (target.G == null)
			throw new IllegalArgumentException("cannot add edge to node `"
					+ target.getId()
					+ "' since this node is not yet part of a graph");

		if (G == null)
			throw new IllegalArgumentException("cannot add edge to node `"
					+ getId() + "' since this node is not yet part of a graph");

		if (G != target.G)
			throw new IllegalArgumentException("cannot add edge between node `"
					+ getId() + "' and node `" + target.getId()
					+ "' since they pertain to distinct graphs");

		// Register the edge.

		ArrayList<Edge> toward = (ArrayList<Edge>) to.get(getId());

		if (toward != null) {
			// There exist yet an edge from the target to this node.

			T e = (T) G.edgeFactory.newInstance(tag, this, target, directed);
			// e.bind( this, target, directed );
			// e.setDirected(directed);
			return e;
		} else {
			T e = (T) G.edgeFactory.newInstance(tag, this, target, directed);
			// e.bind( this, target, directed );
			// e.setDirected(directed);
			return e;
		}
	}

	/**
	 * Called by an edge to bind it.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void registerEdge(Edge edge) throws IllegalArgumentException,
			IdAlreadyInUseException {
		// If the edge or an edge with the same id is already registered.

		Node other = edge.getOpposite(this);

		// Add the edge.

		edges.add(edge);

		if (edge.isDirected()) {
			MultiEdgeMap<Edge> map;

			if (edge.getSourceNode() == this) {
				map = (MultiEdgeMap<Edge>) to;
				outDegree++;
			} else {
				map = (MultiEdgeMap<Edge>) from;
				inDegree++;
			}

			ArrayList<Edge> list = map.get(other.getId());

			if (list == null) {
				list = new ArrayList<Edge>();
				map.put(other.getId(), list);
			}

			list.add(edge);
		} else {
			ArrayList<Edge> listTo = (ArrayList<Edge>) to.get(other.getId());
			ArrayList<Edge> listFrom = (ArrayList<Edge>) from
					.get(other.getId());

			if (listTo == null) {
				listTo = new ArrayList<Edge>();
				((MultiEdgeMap<Edge>) to).put(other.getId(), listTo);
			}
			if (listFrom == null) {
				listFrom = new ArrayList<Edge>();
				((MultiEdgeMap<Edge>) from).put(other.getId(), listFrom);
			}

			inDegree++;
			outDegree++;
			listTo.add(edge);
			listFrom.add(edge);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void unregisterEdge(Edge edge) {
		Node other = edge.getOpposite(this);
		ArrayList<Edge> toList;
		ArrayList<Edge> fromList;
		int pos;

		toList = (ArrayList<Edge>) to.get(other.getId());
		fromList = (ArrayList<Edge>) from.get(other.getId());

		if (toList != null) {
			pos = toList.indexOf(edge);

			if (pos >= 0) {
				toList.remove(pos);
				outDegree--;

				if (toList.isEmpty())
					to.remove(other.getId());
			}
		}
		if (fromList != null) {
			pos = fromList.indexOf(edge);

			if (pos >= 0) {
				fromList.remove(pos);
				inDegree--;

				if (fromList.isEmpty())
					from.remove(other.getId());
			}
		}

		pos = edges.indexOf(edge);

		if (pos >= 0)
			edges.remove(pos);
	}

	/**
	 * When a node is unregistered from a graph, it must not keep edges
	 * connected to nodes still in the graph. This methods untie all edges
	 * connected to this node (this also unregister them from the graph).
	 */
	@Override
	protected void disconnectAllEdges() throws IllegalStateException {
		int n = edges.size();

		// We cannot use a "for" since untying an edge removes this edge from
		// the node. The number of edges will change continuously.

		while (n > 0) {
			Edge e = edges.get(0);
			G.removeEdge(((AbstractElement) e).getId());
			// e.unbind();
			n = edges.size();
		}
	}

	// Nested classes

	static class MultiElementIterator<T extends Element> implements Iterator<T> {
		Iterator<ArrayList<T>> iterator;

		Iterator<T> current;

		MultiElementIterator(HashMap<String, ArrayList<T>> elements) {
			iterator = elements.values().iterator();

			if (iterator.hasNext())
				current = iterator.next().iterator();
		}

		public boolean hasNext() {
			if (current == null)
				return false; // Case if iterator is empty.

			return current.hasNext();
		}

		public T next() {
			T next = current.next();

			if (!current.hasNext()) {
				if (iterator.hasNext()) {
					current = iterator.next().iterator();
				}
			}

			return next;
		}

		public void remove() throws UnsupportedOperationException,
				IllegalStateException {
			throw new UnsupportedOperationException(
					"this iterator does not allow removing");
		}
	}

	protected class MultiEdgeMap<T extends Edge> extends
			HashMap<String, ArrayList<T>> implements Iterable<T> {
		private static final long serialVersionUID = 1L;

		public Iterator<T> iterator() {
			return new MultiElementIterator<T>(this);
		}
	}
}