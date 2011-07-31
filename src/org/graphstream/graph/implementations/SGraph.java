package org.graphstream.graph.implementations;

import org.graphstream.graph.Graph;
import org.graphstream.graph.NodeFactory;

public class SGraph extends ALGraph {

	public SGraph(String id, boolean strictChecking, boolean autoCreate,
			int initialNodeCapacity, int initialEdgeCapacity) {
		super(id, strictChecking, autoCreate, initialNodeCapacity,
				initialEdgeCapacity);
		// All we need to do is to change the node factory
		setNodeFactory(new NodeFactory<SNode>() {
			public SNode newInstance(String id, Graph graph) {
				return new SNode((AbstractGraph) graph, id);
			}
		});
	}

	public SGraph(String id, boolean strictChecking, boolean autoCreate) {
		this(id, strictChecking, autoCreate, DEFAULT_NODE_CAPACITY,
				DEFAULT_EDGE_CAPACITY);
	}

	public SGraph(String id) {
		this(id, true, false);
	}

}
