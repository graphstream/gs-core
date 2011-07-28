package org.graphstream.graph;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BreadthFirstIteratorIndexed<T extends Node> implements Iterator<T> {
	protected boolean directed;
	protected Graph graph;
	protected Node[] queue;
	protected int[] depth;
	protected int qHead, qTail;

	public BreadthFirstIteratorIndexed(Node startNode, boolean directed) {
		this.directed = directed;
		graph = startNode.getGraph();
		int n = graph.getNodeCount();
		queue = new Node[n];
		depth = new int[n];

		int s = startNode.getIndex();
		for (int i = 0; i < n; i++)
			depth[i] = i == s ? 0 : -1;
		queue[0] = startNode;
		qHead = 0;
		qTail = 1;
	}

	public BreadthFirstIteratorIndexed(Node startNode) {
		this(startNode, true);
	}

	public boolean hasNext() {
		return qHead < qTail;
	}

	@SuppressWarnings("unchecked")
	public T next() {
		if (qHead >= qTail)
			throw new NoSuchElementException();
		Node current = queue[qHead++];
		int level = depth[current.getIndex()] + 1;
		Iterable<Edge> edges = directed ? current.getEachLeavingEdge()
				: current.getEachEdge();
		for (Edge e : edges) {
			Node node = e.getOpposite(current);
			int j = node.getIndex();
			if (depth[j] == -1) {
				queue[qTail++] = node;
				depth[j] = level;
			}
		}
		return (T)current;
	}

	public void remove() {
		throw new UnsupportedOperationException(
				"This iterator does not support remove");
	}

	public int getDepthOf(Node node) {
		return depth[node.getIndex()];
	}

	public int getMaxDepth() {
		return depth[queue[qTail - 1].getIndex()];
	}

	public boolean isTabu(Node node) {
		return depth[node.getIndex()] != -1;
	}

	public boolean isDirected() {
		return directed;
	}
}
