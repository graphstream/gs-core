/*
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */

/**
 * @since 2009-02-19
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph.implementations;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

/**
 * Nodes used with {@link AdjacencyListGraph}
 * 
 */
public class AdjacencyListNode extends AbstractNode {
	protected static final int INITIAL_EDGE_CAPACITY;
	protected static final double GROWTH_FACTOR = 1.1;

	static {
		String p = "org.graphstream.graph.node.initialEdgeCapacity";
		int initialEdgeCapacity = 16;
		try {
			initialEdgeCapacity = Integer.valueOf(System.getProperty(p, "16"));
		} catch (AccessControlException e) {
		}
		INITIAL_EDGE_CAPACITY = initialEdgeCapacity;
	}

	protected static final char I_EDGE = 0;
	protected static final char IO_EDGE = 1;
	protected static final char O_EDGE = 2;

	protected AbstractEdge[] edges;
	protected int ioStart, oStart, degree;

	// *** Constructor ***

	protected AdjacencyListNode(AbstractGraph graph, String id) {
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

	@Override
	public Edge getEdge(int i) {
		if (i < 0 || i >= degree)
			throw new IndexOutOfBoundsException("Node \"" + this + "\"" + " has no edge " + i);
		return edges[i];
	}

	@Override
	public Edge getEnteringEdge(int i) {
		if (i < 0 || i >= getInDegree())
			throw new IndexOutOfBoundsException("Node \"" + this + "\"" + " has no entering edge " + i);
		return edges[i];
	}

	@Override
	public Edge getLeavingEdge(int i) {
		if (i < 0 || i >= getOutDegree())
			throw new IndexOutOfBoundsException("Node \"" + this + "\"" + " has no edge " + i);
		return edges[ioStart + i];
	}

	@Override
	public Edge getEdgeBetween(Node node) {
		return locateEdge(node, IO_EDGE);
	}

	@Override
	public Edge getEdgeFrom(Node node) {
		return locateEdge(node, I_EDGE);
	}

	@Override
	public Edge getEdgeToward(Node node) {
		return locateEdge(node, O_EDGE);
	}

	// *** Iterators ***

	@Override
	public Stream<Edge> edges() {
		return Arrays.stream(edges, 0, degree);
	}

	@Override
	public Stream<Edge> enteringEdges() {
		return Arrays.stream(edges, 0, oStart);
	}

	@Override
	public Stream<Edge> leavingEdges() {
		return Arrays.stream(edges, ioStart, degree);
	}

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
			graph.removeEdge(e, true, e.source != AdjacencyListNode.this, e.target != AdjacencyListNode.this);
			removeEdge(iPrev);
			iNext = iPrev;
			iPrev = -1;
			iEnd--;
		}
	}
}
