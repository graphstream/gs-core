package org.graphstream.graph.implementations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

/**
 * This class takes care only for maintaining and accessing the data structure
 * that stores its edges. Everything else is managed by the abstract class.
 * 
 * @author Stefan Balev
 * 
 */
public class ToyNode extends AbstractNode {

	/**
	 * My data structure is very simple (and inefficient). Just a list to store
	 * the edges incident to this node and two ints for the in-degree and the
	 * out-degree.
	 */
	private ArrayList<AbstractEdge> edges;
	private int inDegree, outDegree;

	/**
	 * In the constructor you need to call the constructor of the superclass and
	 * initialize your data structure.
	 */
	protected ToyNode(AbstractGraph graph, String id) {
		super(graph, id);

		edges = new ArrayList<AbstractEdge>();
		inDegree = outDegree = 0;
	}

	// Abstract graph will call automatically the following "callbacks" each
	// time when this node must update its data structure.

	/**
	 * This one is called when a new edge incident to this node is added to the
	 * graph. AbstractGraph takes care to check if this is a valid edge, you
	 * have just to add it to your data structure. You can "accept" or "reject"
	 * the edge by returning true or false. Typically this feature is used to
	 * implement single and multi-graphs.
	 */
	@Override
	protected boolean addEdgeCallback(AbstractEdge edge) {
		// Uncomment the following two lines if you are a node of single graph
		// if (hasEdgeBetween(edge.getOpposite(this))
		// return false;
		edges.add(edge);
		if (isEnteringEdge(edge))
			inDegree++;
		if (isLeavingEdge(edge))
			outDegree++;
		// I accept the edge
		return true;
	}

	/**
	 * This one is called when an edge is to be removed. You are guaranteed that
	 * the edge was previously added by addEdgeCallback. In our case we have
	 * just to remove it from the list
	 */
	protected void removeEdgeCallback(AbstractEdge edge) {
		edges.remove(edge);
		if (isEnteringEdge(edge))
			inDegree--;
		if (isLeavingEdge(edge))
			outDegree--;
	}

	/**
	 * This one is called for each node when the graph is cleared. You can leave
	 * it empty but it is recommended to add some code facilitating the garbage
	 * collection.
	 */
	protected void clearCallback() {
		edges.clear();
		inDegree = outDegree = 0;
	}

	// some simple access methods

	@Override
	public int getDegree() {
		return edges.size();
	}

	@Override
	public int getInDegree() {
		return inDegree;
	}

	@Override
	public int getOutDegree() {
		return outDegree;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Edge> T getEdge(int i) {
		return (T) edges.get(i);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Edge> T getEdgeBetween(Node node) {
		for (Edge e : edges)
			if (e.getOpposite(this) == node)
				return (T) e;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Edge> T getEdgeFrom(Node node) {
		for (Edge e : edges)
			if (e.getOpposite(this) == node && isEnteringEdge(e))
				return (T) e;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Edge> T getEdgeToward(Node node) {
		for (Edge e : edges)
			if (e.getOpposite(this) == node && isLeavingEdge(e))
				return (T) e;
		return null;
	}

	// Now let's take care of iterators

	/**
	 * Iterators are kindly invited to be fail-fast: if the graph is
	 * structurally modified at any time after the iterator is created, in any
	 * way except through the iterator's own remove method, the iterator will
	 * throw a ConcurrentModificationException instead of risking arbitrary,
	 * non-deterministic behavior. To implement this, they can use the
	 * getModifCount method of their graph. </p>
	 * 
	 * <p>
	 * They are free to support remove or to throw
	 * UnsupportedOperationException, but the first option is preferable. If
	 * remove is supported, special care must be taken (see below).
	 */
	private class EdgeIterator<T extends Edge> implements Iterator<T> {
		private Iterator<AbstractEdge> it = edges.iterator();
		AbstractEdge previous = null;

		public boolean hasNext() {
			return it.hasNext();
		}

		@SuppressWarnings("unchecked")
		public T next() {
			previous = it.next();
			return (T) previous;
		}

		/**
		 * When this method is called, the edge must be removed not only from
		 * the node's structure, but also from the graph. To do this, the
		 * iterator must call g.removeEdge(previous). But the problem is that
		 * this method will call in its turn removeCallback(previous) for this
		 * node. This risks to put the iterator in inconsistent state. To avoid
		 * this, the class AbstractGraph provides a special method to remove
		 * edges without calling their nodes' callback.
		 */
		public void remove() {
			it.remove();
			graph.removeEdge(previous, true,
					previous.getSourceNode() != ToyNode.this, previous
							.getTargetNode() != ToyNode.this);
		}
	}

	/**
	 * Here we will use indices to show a different implementation
	 */
	private class DirectedEdgeIterator<T extends Edge> implements Iterator<T> {
		private int iPrev = -1;
		private int iNext = -1;
		private boolean entering;

		protected DirectedEdgeIterator(boolean entering) {
			this.entering = entering;
			gotoNext();
		}

		private void gotoNext() {
			iNext++;
			while (iNext < edges.size()) {
				Edge e = edges.get(iNext);
				if (entering && ToyNode.this.isEnteringEdge(e)
						|| (!entering && ToyNode.this.isLeavingEdge(e)))
					break;
				iNext++;
			}
		}

		public boolean hasNext() {
			return iNext < edges.size();
		}

		@SuppressWarnings("unchecked")
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();
			iPrev = iNext;
			gotoNext();
			return (T) edges.get(iPrev);
		}

		public void remove() {
			if (iPrev == -1)
				throw new IllegalStateException();
			AbstractEdge e = edges.remove(iPrev);
			iNext--;
			graph.removeEdge(e, true, e.getSourceNode() != ToyNode.this, e
					.getTargetNode() != ToyNode.this);
		}

	}

	@Override
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return new EdgeIterator<T>();
	}

	@Override
	public <T extends Edge> Iterator<T> getEnteringEdgeIterator() {
		return new DirectedEdgeIterator<T>(true);
	}

	@Override
	public <T extends Edge> Iterator<T> getLeavingEdgeIterator() {
		return new DirectedEdgeIterator<T>(false);
	}
}
