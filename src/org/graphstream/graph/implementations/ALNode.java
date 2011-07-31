package org.graphstream.graph.implementations;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class ALNode extends AbstractNode {
	protected static final int INITIAL_EDGE_CAPACITY = 16;
	protected static final double GROWTH_FACTOR = 1.1;

	protected static final char I_EDGE = 0;
	protected static final char IO_EDGE = 1;
	protected static final char O_EDGE = 2;

	protected AbstractEdge[] edges;
	protected int ioStart, oStart, degree;

	// *** Constructor ***

	protected ALNode(AbstractGraph graph, String id) {
		super(graph, id);
		edges = new AbstractEdge[INITIAL_EDGE_CAPACITY];
		ioStart = oStart = degree = 0;
	}

	// *** Helpers ***

	protected char edgeType(AbstractEdge e) {
		if (!e.directed || e.source == e.target)
			return IO_EDGE;
		return e.source == this ? O_EDGE : I_EDGE;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Edge> T locateEdge(Node opposite, char type) {
		// where to search ?
		int start = 0;
		int end = degree;
		if (type == I_EDGE)
			end = oStart;
		else if (type == O_EDGE)
			start = ioStart;

		for (int i = start; i < end; i++)
			if (edges[i].getOpposite(this) == opposite)
				return (T) edges[i];
		return null;
	}

	protected void removeEdge(int i) {
		if (i >= oStart) {
			edges[i] = edges[--degree];
			edges[degree] = null;
			return;
		}

		if (i >= ioStart) {
			edges[i] = edges[--oStart];
			edges[oStart] = edges[--degree];
			edges[degree] = null;
			return;
		}

		edges[i] = edges[--ioStart];
		edges[ioStart] = edges[--oStart];
		edges[oStart] = edges[--degree];
		edges[degree] = null;

	}

	// *** Callbacks ***

	@Override
	protected boolean addEdgeCallback(AbstractEdge edge) {
		// resize edges if necessary
		if (edges.length == degree) {
			AbstractEdge[] tmp = new AbstractEdge[(int) (GROWTH_FACTOR * edges.length) + 1];
			System.arraycopy(edges, 0, tmp, 0, edges.length);
			Arrays.fill(edges, null);
			edges = tmp;
		}

		char type = edgeType(edge);

		if (type == O_EDGE) {
			edges[degree++] = edge;
			return true;
		}

		if (type == IO_EDGE) {
			edges[degree++] = edges[oStart];
			edges[oStart++] = edge;
			return true;
		}

		edges[degree++] = edges[oStart];
		edges[oStart++] = edges[ioStart];
		edges[ioStart++] = edge;
		return true;
	}

	@Override
	protected void removeEdgeCallback(AbstractEdge edge) {
		// locate the edge first
		char type = edgeType(edge);
		int i = 0;
		if (type == IO_EDGE)
			i = ioStart;
		else if (type == O_EDGE)
			i = oStart;
		while (edges[i] != edge)
			i++;

		removeEdge(i);
	}

	@Override
	protected void clearCallback() {
		Arrays.fill(edges, 0, degree, null);
		ioStart = oStart = degree = 0;
	}

	// *** Access methods ***

	@Override
	public int getDegree() {
		return degree;
	}

	@Override
	public int getInDegree() {
		return oStart;
	}

	@Override
	public int getOutDegree() {
		return degree - ioStart;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Edge> T getEdge(int i) {
		if (i < 0 || i >= degree)
			throw new IndexOutOfBoundsException("Node \"" + this + "\""
					+ " has no edge " + i);
		return (T) edges[i];
	}

	@Override
	public <T extends Edge> T getEdgeBetween(Node node) {
		return locateEdge(node, IO_EDGE);
	}

	@Override
	public <T extends Edge> T getEdgeFrom(Node node) {
		return locateEdge(node, I_EDGE);
	}

	@Override
	public <T extends Edge> T getEdgeToward(Node node) {
		return locateEdge(node, O_EDGE);
	}

	// *** Iterators ***

	protected class EdgeIterator<T extends Edge> implements Iterator<T> {
		protected int iPrev, iNext, iEnd;

		protected EdgeIterator(char type) {
			iPrev = -1;
			iNext = 0;
			iEnd = degree;
			if (type == I_EDGE)
				iEnd = oStart;
			else if (type == O_EDGE)
				iNext = ioStart;
		}

		public boolean hasNext() {
			return iNext < iEnd;
		}

		@SuppressWarnings("unchecked")
		public T next() {
			if (iNext >= iEnd)
				throw new NoSuchElementException();
			iPrev = iNext++;
			return (T) edges[iPrev];
		}

		public void remove() {
			if (iPrev == -1)
				throw new IllegalStateException();
			AbstractEdge e = edges[iPrev];
			// do not call the callback because we already know the index
			graph.removeEdge(e, true, e.source != ALNode.this,
					e.target != ALNode.this);
			removeEdge(iPrev);
			iNext = iPrev;
			iPrev = -1;
			iEnd--;
		}
	}

	@Override
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return new EdgeIterator<T>(IO_EDGE);
	}

	@Override
	public <T extends Edge> Iterator<T> getEnteringEdgeIterator() {
		return new EdgeIterator<T>(I_EDGE);
	}

	@Override
	public <T extends Edge> Iterator<T> getLeavingEdgeIterator() {
		return new EdgeIterator<T>(O_EDGE);
	}
}
