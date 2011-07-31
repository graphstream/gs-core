package org.graphstream.graph.implementations;

import org.graphstream.graph.Graph;
import org.graphstream.graph.NodeFactory;

public class MGraph extends ALGraph {

	public MGraph(String id, boolean strictChecking, boolean autoCreate,
			int initialNodeCapacity, int initialEdgeCapacity) {
		super(id, strictChecking, autoCreate, initialNodeCapacity,
				initialEdgeCapacity);
		// All we need to do is to change the node factory
		setNodeFactory(new NodeFactory<MNode>() {
			public MNode newInstance(String id, Graph graph) {
				return new MNode((AbstractGraph) graph, id);
			}
		});
	}

	public MGraph(String id, boolean strictChecking, boolean autoCreate) {
		this(id, strictChecking, autoCreate, DEFAULT_NODE_CAPACITY,
				DEFAULT_EDGE_CAPACITY);
	}

	public MGraph(String id) {
		this(id, true, false);
	}
}
