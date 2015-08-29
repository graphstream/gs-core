package org.graphstream.graph.implementations;

import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.NodeFactory;

public class SimpleAdjacencyListGraph extends AdjacencyListGraph<AdjacencyListNode, AbstractEdge> {

	public SimpleAdjacencyListGraph(String id, boolean strictChecking, boolean autoCreate, int initialNodeCapacity, int initialEdgeCapacity) {
		super(id, strictChecking, autoCreate, initialNodeCapacity, initialEdgeCapacity);
	}

	public SimpleAdjacencyListGraph(String id, boolean strictChecking, boolean autoCreate) {
		super(id, strictChecking, autoCreate);
	}

	public SimpleAdjacencyListGraph(String id) {
		super(id);
	}

	{
		NodeFactory<AdjacencyListNode> nf = (i, g) -> (new AdjacencyListNode(i, (AbstractGraph) g));
		setNodeFactory(nf);

		EdgeFactory<AbstractEdge> ef = (i, s, ds, di) -> (new AbstractEdge(i, (AbstractNode) s, (AbstractNode) ds, di));
		setEdgeFactory(ef);
	}
}
