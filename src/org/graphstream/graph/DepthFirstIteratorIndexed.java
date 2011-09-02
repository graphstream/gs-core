package org.graphstream.graph;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DepthFirstIteratorIndexed<T extends Node> implements Iterator<T> {
	boolean directed;
	Graph graph;

	Node[] parent;
	Iterator<Edge>[] iterator;
	int depth[];
	Node next;
	int maxDepth;

	@SuppressWarnings("unchecked")
	public DepthFirstIteratorIndexed(Node startNode, boolean directed) {
		this.directed = directed;
		graph = startNode.getGraph();
		int n = graph.getNodeCount();
		parent = new Node[n];
		iterator = new Iterator[n];
		depth = new int[n];

		int s = startNode.getIndex();
		for (int i = 0; i < n; i++)
			depth[i] = i == s ? 0 : -1;
		next = startNode;
	}

	protected void gotoNext() {
		while (next != null) {
			int i = next.getIndex();
			while (iterator[i].hasNext()) {
				Node neighbor = iterator[i].next().getOpposite(next);
				int j = neighbor.getIndex();
				if (iterator[j] == null) {
					parent[j] = next;
					iterator[j] = directed ? neighbor.getLeavingEdgeIterator()
							: neighbor.getEnteringEdgeIterator();
					depth[j] = depth[i] + 1;
					if (depth[j] > maxDepth)
						maxDepth = depth[j];
					next = neighbor;
					return;
				}
			}
			next = parent[i];
		}
	}

	public DepthFirstIteratorIndexed(Node startNode) {
		this(startNode, true);
	}

	public boolean hasNext() {
		return next != null;
	}

	@SuppressWarnings("unchecked")
	public T next() {
		if (next == null)
			throw new NoSuchElementException();
		iterator[next.getIndex()] = directed ? next.getLeavingEdgeIterator()
				: next.getEnteringEdgeIterator();
		Node previous = next;
		gotoNext();
		return (T) previous;
	}

	public void remove() {
		throw new UnsupportedOperationException(
				"This iterator does not support remove");
	}

	public int getDepthOf(Node node) {
		return depth[node.getIndex()];
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public boolean isTabu(Node node) {
		return depth[node.getIndex()] != -1;
	}

	public boolean isDirected() {
		return directed;
	}
}
