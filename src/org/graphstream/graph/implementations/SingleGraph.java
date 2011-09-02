package org.graphstream.graph.implementations;

import org.graphstream.graph.Graph;
import org.graphstream.graph.NodeFactory;

/**
 * An implementation of graph that supports only one edge between two nodes.
 */
public class SingleGraph extends AdjacencyListGraph {

	/**
	 * Creates an empty graph.
	 * 
	 * @param id
	 *            Unique identifier of the graph.
	 * @param strictChecking
	 *            If true any non-fatal error throws an exception.
	 * @param autoCreate
	 *            If true (and strict checking is false), nodes are
	 *            automatically created when referenced when creating a edge,
	 *            even if not yet inserted in the graph.
	 * @param initialNodeCapacity
	 *            Initial capacity of the node storage data structures. Use this
	 *            if you know the approximate maximum number of nodes of the
	 *            graph. The graph can grow beyond this limit, but storage
	 *            reallocation is expensive operation.
	 * @param initialEdgeCapacity
	 *            Initial capacity of the edge storage data structures. Use this
	 *            if you know the approximate maximum number of edges of the
	 *            graph. The graph can grow beyond this limit, but storage
	 *            reallocation is expensive operation.
	 */
	public SingleGraph(String id, boolean strictChecking, boolean autoCreate,
			int initialNodeCapacity, int initialEdgeCapacity) {
		super(id, strictChecking, autoCreate, initialNodeCapacity,
				initialEdgeCapacity);
		// All we need to do is to change the node factory
		setNodeFactory(new NodeFactory<SingleNode>() {
			public SingleNode newInstance(String id, Graph graph) {
				return new SingleNode((AbstractGraph) graph, id);
			}
		});
	}

	/**
	 * Creates an empty graph with default edge and node capacity.
	 * 
	 * @param id
	 *            Unique identifier of the graph.
	 * @param strictChecking
	 *            If true any non-fatal error throws an exception.
	 * @param autoCreate
	 *            If true (and strict checking is false), nodes are
	 *            automatically created when referenced when creating a edge,
	 *            even if not yet inserted in the graph.
	 */
	public SingleGraph(String id, boolean strictChecking, boolean autoCreate) {
		this(id, strictChecking, autoCreate, DEFAULT_NODE_CAPACITY,
				DEFAULT_EDGE_CAPACITY);
	}

	/**
	 * Creates an empty graph with strict checking and without auto-creation.
	 * 
	 * @param id
	 *            Unique identifier of the graph.
	 */
	public SingleGraph(String id) {
		this(id, true, false);
	}

}
