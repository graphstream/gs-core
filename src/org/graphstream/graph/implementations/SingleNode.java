package org.graphstream.graph.implementations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

/**
 * Nodes used with {@link SingleGraph}
 *
 */
public class SingleNode extends AdjacencyListNode {
	protected HashMap<AbstractNode, AbstractEdge> neighborMap;

	// *** Constructor ***

	protected SingleNode(AbstractGraph graph, String id) {
		super(graph, id);
		neighborMap = new HashMap<AbstractNode, AbstractEdge>(
				4 * INITIAL_EDGE_CAPACITY / 3 + 1);
	}

	// *** Helpers ***

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Edge> T locateEdge(Node opposite, char type) {
		AbstractEdge e = neighborMap.get(opposite);
		if (e == null)
			return null;

		char etype = edgeType(e);

		if ((type != I_EDGE || etype != O_EDGE)
				&& (type != O_EDGE || etype != I_EDGE))
			return (T) e;
		return null;
	}

	@Override
	protected void removeEdge(int i) {
		neighborMap.remove(edges[i].getOpposite(this));
		super.removeEdge(i);
	}

	// *** Callbacks ***

	@Override
	protected boolean addEdgeCallback(AbstractEdge edge) {
		AbstractNode opposite = edge.getOpposite(this);
		if (neighborMap.containsKey(opposite))
			return false;
		neighborMap.put(opposite, edge);
		return super.addEdgeCallback(edge);
	}

	@Override
	protected void clearCallback() {
		neighborMap.clear();
		super.clearCallback();
	}

	// *** Others ***

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> Iterator<T> getNeighborNodeIterator() {
		return (Iterator<T>) Collections.unmodifiableSet(neighborMap.keySet())
				.iterator();
	}
}
