package org.graphstream.graph.implementations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;

public class ALGraph extends AbstractGraph {

	public static final double GROW_FACTOR = 1.1;
	public static final int DEFAULT_NODE_CAPACITY = 128;
	public static final int DEFAULT_EDGE_CAPACITY = 1024;

	protected HashMap<String, AbstractNode> nodeMap;
	protected HashMap<String, AbstractEdge> edgeMap;

	protected AbstractNode[] nodeArray;
	protected AbstractEdge[] edgeArray;

	protected int nodeCount;
	protected int edgeCount;

	// *** Constructors ***

	public ALGraph(String id, boolean strictChecking, boolean autoCreate,
			int initialNodeCapacity, int initialEdgeCapacity) {
		super(id, strictChecking, autoCreate);

		setNodeFactory(new NodeFactory<ALNode>() {
			public ALNode newInstance(String id, Graph graph) {
				return new ALNode((AbstractGraph) graph, id);
			}
		});

		setEdgeFactory(new EdgeFactory<AbstractEdge>() {
			public AbstractEdge newInstance(String id, Node src, Node dst,
					boolean directed) {
				return new AbstractEdge(id, (AbstractNode) src,
						(AbstractNode) dst, directed);
			}
		});

		if (initialNodeCapacity < DEFAULT_NODE_CAPACITY)
			initialNodeCapacity = DEFAULT_NODE_CAPACITY;
		if (initialEdgeCapacity < DEFAULT_EDGE_CAPACITY)
			initialEdgeCapacity = DEFAULT_EDGE_CAPACITY;

		nodeMap = new HashMap<String, AbstractNode>(
				4 * initialNodeCapacity / 3 + 1);
		edgeMap = new HashMap<String, AbstractEdge>(
				4 * initialEdgeCapacity / 3 + 1);
		nodeArray = new AbstractNode[initialNodeCapacity];
		edgeArray = new AbstractEdge[initialEdgeCapacity];
		nodeCount = edgeCount = 0;
	}

	public ALGraph(String id, boolean strictChecking, boolean autoCreate) {
		this(id, strictChecking, autoCreate, DEFAULT_NODE_CAPACITY,
				DEFAULT_EDGE_CAPACITY);
	}

	public ALGraph(String id) {
		this(id, true, false);
	}

	// *** Callbacks ***

	@Override
	protected void addEdgeCallback(AbstractEdge edge) {
		edgeMap.put(edge.getId(), edge);
		if (edgeCount == edgeArray.length) {
			AbstractEdge[] tmp = new AbstractEdge[(int) (edgeArray.length * GROW_FACTOR) + 1];
			System.arraycopy(edgeArray, 0, tmp, 0, edgeArray.length);
			Arrays.fill(edgeArray, null);
			edgeArray = tmp;
		}
		edgeArray[edgeCount] = edge;
		edge.setIndex(edgeCount++);
	}

	@Override
	protected void addNodeCallback(AbstractNode node) {
		nodeMap.put(node.getId(), node);
		if (nodeCount == nodeArray.length) {
			AbstractNode[] tmp = new AbstractNode[(int) (nodeArray.length * GROW_FACTOR) + 1];
			System.arraycopy(nodeArray, 0, tmp, 0, nodeArray.length);
			Arrays.fill(nodeArray, null);
			nodeArray = tmp;
		}
		nodeArray[nodeCount] = node;
		node.setIndex(nodeCount++);
	}

	@Override
	protected void removeEdgeCallback(AbstractEdge edge) {
		edgeMap.remove(edge.getId());
		int i = edge.getIndex();
		edgeArray[i] = edgeArray[--edgeCount];
		edgeArray[i].setIndex(i);
		edgeArray[edgeCount] = null;
	}

	@Override
	protected void removeNodeCallback(AbstractNode node) {
		nodeMap.remove(node.getId());
		int i = node.getIndex();
		nodeArray[i] = nodeArray[--nodeCount];
		nodeArray[i].setIndex(i);
		nodeArray[nodeCount] = null;
	}

	@Override
	protected void clearCallback() {
		nodeMap.clear();
		edgeMap.clear();
		Arrays.fill(nodeArray, 0, nodeCount, null);
		Arrays.fill(edgeArray, 0, edgeCount, null);
		nodeCount = edgeCount = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Edge> T getEdge(String id) {
		return (T) edgeMap.get(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Edge> T getEdge(int index) {
		if (index < 0 || index >= edgeCount)
			throw new IndexOutOfBoundsException("Edge " + index
					+ " does not exist");
		return (T) edgeArray[index];
	}

	@Override
	public int getEdgeCount() {
		return edgeCount;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> T getNode(String id) {
		return (T) nodeMap.get(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> T getNode(int index) {
		if (index < 0 || index > nodeCount)
			throw new IndexOutOfBoundsException("Node " + index
					+ " does not exist");
		return (T) nodeArray[index];
	}

	@Override
	public int getNodeCount() {
		return nodeCount;
	}

	// *** Iterators ***

	protected class EdgeIterator<T extends Edge> implements Iterator<T> {
		int iNext = 0;
		int iPrev = -1;

		public boolean hasNext() {
			return iNext < edgeCount;
		}

		@SuppressWarnings("unchecked")
		public T next() {
			if (iNext >= edgeCount)
				throw new NoSuchElementException();
			iPrev = iNext++;
			return (T) edgeArray[iPrev];
		}

		public void remove() {
			if (iPrev == -1)
				throw new IllegalStateException();
			removeEdge(edgeArray[iPrev], true, true, true);
			iNext = iPrev;
			iPrev = -1;
		}
	}

	protected class NodeIterator<T extends Node> implements Iterator<T> {
		int iNext = 0;
		int iPrev = -1;

		public boolean hasNext() {
			return iNext < nodeCount;
		}

		@SuppressWarnings("unchecked")
		public T next() {
			if (iNext >= nodeCount)
				throw new NoSuchElementException();
			iPrev = iNext++;
			return (T) nodeArray[iPrev];
		}

		public void remove() {
			if (iPrev == -1)
				throw new IllegalStateException();
			removeNode(nodeArray[iPrev], true);
			iNext = iPrev;
			iPrev = -1;
		}
	}

	@Override
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return new EdgeIterator<T>();
	}

	@Override
	public <T extends Node> Iterator<T> getNodeIterator() {
		return new NodeIterator<T>();
	}

	/**
	 * For performance tuning
	 * 
	 * @return the number of allocated but unused array elements
	 */
	public int getUnusedArrayElements() {
		int count = 0;
		count += edgeArray.length - edgeCount;
		count += nodeArray.length - nodeCount;
		for (ALNode n : this.<ALNode> getEachNode())
			count += n.edges.length - n.degree;
		return count;
	}
}
