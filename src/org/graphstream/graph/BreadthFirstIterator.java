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
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class BreadthFirstIterator implements Iterator<Node> {
	protected boolean directed;
	protected Graph graph;
	protected Node[] queue;
	protected int[] depth;
	protected int qHead, qTail;

	public BreadthFirstIterator(Node startNode, boolean directed) {
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

	public BreadthFirstIterator(Node startNode) {
		this(startNode, true);
	}

	public boolean hasNext() {
		return qHead < qTail;
	}

	public Node next() {
		if (qHead >= qTail)
			throw new NoSuchElementException();
		Node current = queue[qHead++];
		int level = depth[current.getIndex()] + 1;
		Stream<Edge> edges = directed ? current.leavingEdges() : current.edges();

		edges.forEach(e -> {
			Node node = e.getOpposite(current);
			int j = node.getIndex();

			if (depth[j] == -1) {
				queue[qTail++] = node;
				depth[j] = level;
			}
		});

		return current;
	}

	public void remove() {
		throw new UnsupportedOperationException("This iterator does not support remove");
	}

	public int getDepthOf(Node node) {
		return depth[node.getIndex()];
	}

	public int getDepthMax() {
		return depth[queue[qTail - 1].getIndex()];
	}

	public boolean tabu(Node node) {
		return depth[node.getIndex()] != -1;
	}

	public boolean isDirected() {
		return directed;
	}
}
