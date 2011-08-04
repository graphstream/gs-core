package org.graphstream.graph.implementations;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class MNode extends ALNode {
	protected HashMap<AbstractNode, List<AbstractEdge>> neighborMap;

	// *** Constructor ***

	public MNode(AbstractGraph graph, String id) {
		super(graph, id);
		neighborMap = new HashMap<AbstractNode, List<AbstractEdge>>(
				4 * INITIAL_EDGE_CAPACITY / 3 + 1);
	}

	// *** Helpers ***

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Edge> T locateEdge(Node opposite, char type) {
		List<AbstractEdge> l = neighborMap.get(opposite);
		if (l == null)
			return null;

		for (AbstractEdge e : l) {
			char etype = edgeType(e);
			if ((type != I_EDGE || etype != O_EDGE)
					&& (type != O_EDGE || etype != I_EDGE))
				return (T) e;
		}
		return null;
	}

	@Override
	protected void removeEdge(int i) {
		AbstractNode opposite = edges[i].getOpposite(this);
		List<AbstractEdge> l = neighborMap.get(opposite);
		l.remove(edges[i]);
		if (l.isEmpty())
			neighborMap.remove(opposite);
		super.removeEdge(i);
	}

	// *** Callbacks ***

	@Override
	protected boolean addEdgeCallback(AbstractEdge edge) {
		AbstractNode opposite = edge.getOpposite(this);
		List<AbstractEdge> l = neighborMap.get(opposite);
		if (l == null) {
			l = new LinkedList<AbstractEdge>();
			neighborMap.put(opposite, l);
		}
		l.add(edge);
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

	@SuppressWarnings("unchecked")
	public <T extends Edge> Collection<T> getEdgeSetBetween(Node node) {
		List<AbstractEdge> l = neighborMap.get(node);
		if (l == null)
			return Collections.emptyList();
		return (Collection<T>) Collections.unmodifiableList(l);
	}

	public <T extends Edge> Collection<T> getEdgeSetBetween(String id) {
		return getEdgeSetBetween(graph.getNode(id));
	}

	public <T extends Edge> Collection<T> getEdgeSetBetween(int index) {
		return getEdgeSetBetween(graph.getNode(index));
	}
}
