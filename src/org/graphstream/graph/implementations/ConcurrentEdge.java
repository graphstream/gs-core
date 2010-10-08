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

package org.graphstream.graph.implementations;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.stream.SourceBase.ElementType;

/**
 * <p>
 * An implementation of an Edge with multi-thread capabilities.
 * </p>
 * <p>
 * It is similar to the
 * {@link org.graphstream.graph.implementations.AdjacencyListEdge} class, but
 * with thread-safe data structures.
 * </p>
 * <p>
 * Time and memory complexity is comparable to the values given in
 * {@link org.graphstream.graph.implementations.AdjacencyListEdge}. Consider
 * some time overhead due to the thread synchronization machinery.
 * </p>
 * 
 * @see org.graphstream.graph.implementations.AdjacencyListEdge
 */
public class ConcurrentEdge extends AbstractElement implements Edge {

	ConcurrentNode n0;

	ConcurrentNode n1;

	boolean directed = false;

	/**
	 * Constructor for a ConcurrentEdge with specified id nodes and information
	 * about the direction.
	 * 
	 * @param id
	 *            Unique identifier.
	 * @param src
	 *            Source node.
	 * @param dst
	 *            Target Node.
	 * @param directed
	 *            Say whether the edge is directed or not.
	 */
	protected ConcurrentEdge(String id, Node src, Node dst, boolean directed) {
		super(id);

		if ((src != null && !(src instanceof ConcurrentNode))
				|| (dst != null && !(dst instanceof ConcurrentNode)))
			throw new ClassCastException("ConcurrentEdge needs an "
					+ "extended class ConcurrentNode");

		this.n0 = (ConcurrentNode) src;
		this.n1 = (ConcurrentNode) dst;
		this.directed = directed;
	}

	@Override
	protected String myGraphId() {
		return n0.graph.getId();
	}

	@Override
	protected long newEvent() {
		return ((ConcurrentGraph) n0.graph).newEvent();
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getNode0() {
		return (T) n0;
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getNode1() {
		return (T) n1;
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getOpposite(T node) {
		if (node == n0)
			return (T) n1;
		else if (node == n1)
			return (T) n0;
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getSourceNode() {
		return (T) n0;
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getTargetNode() {
		return (T) n1;
	}

	public boolean isDirected() {
		return directed;
	}

	public void setDirected(boolean on) {
		// XXX Bug, the new edge created in the event stream will loose all its
		// attributes.
		((ConcurrentGraph) n0.graph).listeners.sendEdgeRemoved(myGraphId(),
				newEvent(), getId());
		this.directed = on;
		((ConcurrentGraph) n0.graph).listeners.sendEdgeAdded(myGraphId(),
				newEvent(), getId(), n0.getId(), n1.getId(), directed);
	}

	public void switchDirection() {
		// XXX Bug, the new edge create in the event stream will loose all its
		// attributes.
		((ConcurrentGraph) n0.graph).listeners.sendEdgeRemoved(myGraphId(),
				newEvent(), getId());

		ConcurrentNode n = n0;
		n0 = n1;
		n1 = n;
		((ConcurrentGraph) n0.graph).listeners.sendEdgeAdded(myGraphId(),
				newEvent(), getId(), n0.getId(), n1.getId(), directed);
	}

	@Override
	protected void attributeChanged(String sourceId, long timeId,
			String attribute, AttributeChangeEvent event, Object oldValue,
			Object newValue) {
		if (n0 != null)
			((ConcurrentGraph) n0.graph).listeners.sendAttributeChangedEvent(
					sourceId, timeId, getId(), ElementType.EDGE, attribute,
					event, oldValue, newValue);
	}
}