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
	protected static class TwoEdges {
		AbstractEdge in, out;
	}
	
	protected HashMap<AbstractNode, TwoEdges> neighborMap;

	// *** Constructor ***

	protected SingleNode(AbstractGraph graph, String id) {
		super(graph, id);
		neighborMap = new HashMap<AbstractNode, TwoEdges>(
				4 * INITIAL_EDGE_CAPACITY / 3 + 1);
	}

	// *** Helpers ***

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Edge> T locateEdge(Node opposite, char type) {
		TwoEdges ee = neighborMap.get(opposite);
		if (ee == null)
			return null;
		return (T)(type == I_EDGE ? ee.in : ee.out);
	}

	@Override
	protected void removeEdge(int i) {
		AbstractNode opposite = edges[i].getOpposite(this);
		TwoEdges ee = neighborMap.get(opposite);
		char type = edgeType(edges[i]);
		if (type != O_EDGE)
			ee.in = null;
		if (type != I_EDGE)
			ee.out = null;
		if (ee.in == null && ee.out == null)
			neighborMap.remove(opposite);
		super.removeEdge(i);
	}

	// *** Callbacks ***

	@Override
	protected boolean addEdgeCallback(AbstractEdge edge) {
		AbstractNode opposite = edge.getOpposite(this);
		TwoEdges ee = neighborMap.get(opposite);
		if (ee == null)
			ee = new TwoEdges();
		char type = edgeType(edge);
		if (type != O_EDGE) {
			if (ee.in != null)
				return false;
			ee.in = edge;
		}
		if (type != I_EDGE) {
			if (ee.out != null)
				return false;
			ee.out = edge;
		}
		neighborMap.put(opposite, ee);
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
