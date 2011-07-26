package org.graphstream.graph.implementations;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;

/**
 * AbstractGraph provides event management, strict checking and auto-create
 * policies, as well as other services as displaying, reading and writing.
 * 
 * Your implementation has to maintain data structures allowing to efficiently
 * access graph elements by their id or index and iterating on them. It has also
 * to maintain coherent indices of the graph elements. When AbstractGraph
 * decides to add or remove elements, it calls one of the "callbacks" of the
 * subclass. The role of these callbacks is to update the data structure and to
 * re-index elements if necessary.
 * 
 * @author Stefan Balev
 * 
 */
public class ToyGraph extends AbstractGraph {

	/**
	 * These two are maintained for quickly access elements by their ids.
	 */
	private HashMap<String, AbstractNode> nodeMap;
	private HashMap<String, AbstractEdge> edgeMap;

	/**
	 * These two ensure quick access to elements by their indices. The index of
	 * an element will always be it's position in the list.
	 */
	private ArrayList<AbstractNode> nodeList;
	private ArrayList<AbstractEdge> edgeList;

	/**
	 * In the constructor you need to call the constructor of the superclass,
	 * create your node and edge factories and initialize your data structures
	 */
	public ToyGraph(String id, boolean strictChecking, boolean autoCreate) {
		super(id, strictChecking, autoCreate);

		setNodeFactory(new NodeFactory<ToyNode>() {
			public ToyNode newInstance(String id, Graph graph) {
				return new ToyNode((AbstractGraph) graph, id);
			}
		});
		setEdgeFactory(new EdgeFactory<ToyEdge>() {
			public ToyEdge newInstance(String id, Node src, Node dst,
					boolean directed) {
				return new ToyEdge(id, (AbstractNode) src, (AbstractNode) dst,
						directed);
			}
		});

		nodeMap = new HashMap<String, AbstractNode>();
		edgeMap = new HashMap<String, AbstractEdge>();
		nodeList = new ArrayList<AbstractNode>();
		edgeList = new ArrayList<AbstractEdge>();
	}

	public ToyGraph(String id) {
		this(id, true, false);
	}

	// The superclass will automatically call the following callbacks
	// each time when an element is to be added to or removed from the graph.

	/**
	 * This one is called when an edge is added. No need to check that the id is
	 * unique, to associate it with it's endpoints or whatever. All this is done
	 * by the superclass.
	 * 
	 * All we need to do is to add it to our data structure and to set its
	 * index.
	 * 
	 */
	@Override
	protected void addEdgeCallback(AbstractEdge edge) {
		edgeMap.put(edge.getId(), edge);
		edgeList.add(edge);
		// the element index will be always it's position in the list
		edge.setIndex(edgeList.size() - 1);
	}

	/**
	 * Quite the same as addNodeCallback
	 */
	@Override
	protected void addNodeCallback(AbstractNode node) {
		nodeMap.put(node.getId(), node);
		nodeList.add(node);
		node.setIndex(nodeList.size() - 1);
	}

	/**
	 * This one is called when an edge is removed. Here we take a special care
	 * to reindex the remaining edges. The last edge takes the place and the
	 * index of the removed edge.
	 */
	@Override
	protected void removeEdgeCallback(AbstractEdge edge) {
		edgeMap.remove(edge.getId());
		int i = edge.getIndex();
		AbstractEdge last = edgeList.get(edgeList.size() - 1);
		edgeList.set(i, last);
		last.setIndex(i);
		edgeList.remove(edgeList.size() - 1);
	}

	/**
	 * Quite the same as removeEdgeCallback
	 */
	@Override
	protected void removeNodeCallback(AbstractNode node) {
		nodeMap.remove(node.getId());
		int i = node.getIndex();
		AbstractNode last = nodeList.get(nodeList.size() - 1);
		nodeList.set(i, last);
		last.setIndex(i);
		nodeList.remove(nodeList.size() - 1);
	}

	/**
	 * This one is called when the graph is cleared. All we need to do is to
	 * reset our data structures.
	 */
	@Override
	protected void clearCallback() {
		nodeMap.clear();
		edgeMap.clear();
		nodeList.clear();
		edgeList.clear();
	}

	// some simple access methods

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Edge> T getEdge(String id) {
		return (T) edgeMap.get(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> T getNode(String id) {
		return (T) nodeMap.get(id);
	}

	/**
	 * Your implementation is supposed to throw index out of bounds exception if
	 * the index is bad. In our case we don't need to check because ArrayList
	 * does it for us.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Edge> T getEdge(int index) {
		return (T) edgeList.get(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> T getNode(int index) {
		return (T) nodeList.get(index);
	}

	@Override
	public int getEdgeCount() {
		return edgeList.size();
	}

	@Override
	public int getNodeCount() {
		return nodeList.size();
	}

	// Now let's take care of iterators

	/**
	 * Iterators are kindly invited to be fail-fast: if the graph is
	 * structurally modified at any time after the iterator is created, in any
	 * way except through the iterator's own remove method, the iterator will
	 * throw a ConcurrentModificationException instead of risking arbitrary,
	 * non-deterministic behavior. To implement this, they can use the
	 * getModifCount method of their graph.
	 * 
	 * They are free to support remove or to throw
	 * UnsupportedOperationException, but the first option is preferable. If
	 * remove is supported, special care must be taken (see below).
	 * 
	 * Here we will not use the list iterator because we have to maintain
	 * indices when removing edges
	 */
	private class edgeIterator<T extends Edge> implements Iterator<T> {
		private int iPrev = -1;
		private int iNext = 0;
		private int modifCount = getModifCount();

		private void checkModif() {
			if (modifCount != getModifCount())
				throw new ConcurrentModificationException();
		}

		public boolean hasNext() {
			checkModif();
			return iNext < edgeList.size();
		}

		@SuppressWarnings("unchecked")
		public T next() {
			checkModif();
			if (!hasNext())
				throw new NoSuchElementException();
			iPrev = iNext++;
			return (T) edgeList.get(iPrev);
		}

		/**
		 * When this method is called, the edge must be removed not only from
		 * the data structure, but also from the graph. To do this, the iterator
		 * must call one of the removeEdge methods.
		 * 
		 * You can use removeEdge(edgeList.get(iPrev), false, true, true) if you
		 * want to take care of your data structure here and maintain your
		 * iterator in coherent state. If you use just
		 * removeEdge(edgeList.get(iPrev)), removeEdgeCallback() will be called
		 * automatically. This risks to put your iterator in incoherent state.
		 * 
		 * You have the choice to block the automatic call of
		 * removeEdgeCallback(). In our case the updates needed are exactly
		 * these done by removeEdgeCallback() so we will not block its call.
		 */
		public void remove() {
			checkModif();
			if (iPrev == -1)
				throw new IllegalStateException();
			removeEdge(edgeList.get(iPrev), true, true, true);
			// or just removeEdge(edgeList.get(iPrev));

			iNext = iPrev; // !!! the last element is now at position iPrev
			iPrev = -1;
			modifCount++;
		}
	}

	private class nodeIterator<T extends Node> implements Iterator<T> {
		private int iPrev = -1;
		private int iNext = 0;
		private int modifCount = getModifCount();

		private void checkModif() {
			if (modifCount != getModifCount())
				throw new ConcurrentModificationException();
		}

		public boolean hasNext() {
			checkModif();
			return iNext < nodeList.size();
		}

		@SuppressWarnings("unchecked")
		public T next() {
			checkModif();
			if (!hasNext())
				throw new NoSuchElementException();
			iPrev = iNext++;
			return (T) nodeList.get(iPrev);
		}

		public void remove() {
			checkModif();
			if (iPrev == -1)
				throw new IllegalStateException();
			AbstractNode removed = nodeList.get(iPrev);
			modifCount += 1 + removed.getDegree(); // for the node and all it's
													// adjacent edges
			removeNode(removed, true);
			// or just removeNode(nodeList.get(iPrev));

			iNext = iPrev; // !!! the last element is now at position iPrev
			iPrev = -1;
		}
	}

	@Override
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return new edgeIterator<T>();
	}

	@Override
	public <T extends Node> Iterator<T> getNodeIterator() {
		return new nodeIterator<T>();
	}
}
