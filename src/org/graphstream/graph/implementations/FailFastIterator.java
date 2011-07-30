package org.graphstream.graph.implementations;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.stream.ElementSink;

/**
 * <p>
 * All {@code Graph} and {@code Node} iterators should be fail-fast: if the
 * graph is structurally modified at any time after the iterator is created, in
 * any way except through the iterator's own remove method, the iterator will
 * throw a {@link java.util.ConcurrentModificationException} instead of risking
 * arbitrary, non-deterministic behavior.
 * </p>
 * 
 * <p>
 * This class encapsulates an iterator and makes it fail-fast. {@code Graph} and
 * {@code Node} implementations can use it as follows :
 * 
 * <pre>
 * 	public class MyGraph implements Graph {
 * 		...
 * 		protected class MyNodeIterator&lt;T extends Node&gt;
 * 			implements Iterator&lt;T&gt; {
 * 			...
 * 		}
 * 		...
 * 		public &lt;T extends Node&gt; Iterator&lt;T&gt; getNodeIterator() {
 * 			return new FailFastIterator(new MyNodeIterator&lt;T&gt;(), this);
 * 		}
 * }
 * 
 * 
 */
public class FailFastIterator<T extends Element> implements ElementSink,
		Iterator<T> {

	protected Iterator<T> iterator;
	protected boolean modif;

	/**
	 * Creates a new fail-fast iterator that encapsulates a given iterator and
	 * listens to a given graph. The new iterator will work exactly as {@code
	 * iterator} but will throw
	 * {@link java.util.ConcurrentModificationException} if the {@code graph} is
	 * structurally modified from outside.
	 * 
	 * @param iterator
	 *            the iterator to encapsulate
	 * @param graph
	 *            the graph to listen
	 */
	public FailFastIterator(Iterator<T> iterator, Graph graph) {
		this.iterator = iterator;
		graph.addElementSink(this);
		modif = false;
	}

	// *** ElementSink methods - just raise the flag ***

	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		modif = true;
	}

	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		modif = true;
	}

	public void graphCleared(String sourceId, long timeId) {
		modif = true;
	}

	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		modif = true;
	}

	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		modif = true;
	}

	public void stepBegins(String sourceId, long timeId, double step) {
	}

	// *** Iterator methods - just check the flag ***

	public boolean hasNext() {
		if (modif)
			throw new ConcurrentModificationException();
		return iterator.hasNext();
	}

	public T next() {
		if (modif)
			throw new ConcurrentModificationException();
		return iterator.next();
	}

	public void remove() {
		if (modif)
			throw new ConcurrentModificationException();
		iterator.remove();
		// the flag will be raised, but these modifications come from the
		// iterator, so ignore them
		modif = false;
	}
}
