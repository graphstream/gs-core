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
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph;

import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Path description.
 * 
 * <p>
 * A path is a class that stores ordered lists of nodes and links that are
 * adjacent. Such a path may be manipulated with nodes and/or edges added or
 * removed. This class is designed as a dynamic structure that is, to add edges
 * during the construction of the path. Only edges need to be added, the nodes
 * list is maintained automatically.
 * </p>
 * 
 * <p>
 * The two lists (one for nodes, one for edges) may be acceded at any moment in
 * constant time.
 * </p>
 * 
 * <p>
 * The constraint of this class is that it needs to know the first node of the
 * path (the root). This root can be set with the {@link #setRoot(Node)} method
 * or by using the {@link #add(Node, Edge)} method.
 * </p>
 * 
 * <p>
 * The normal use with this class is to first use the {@link #setRoot(Node)}
 * method to initialize the path; then to use the {@link #add(Edge)} method to
 * grow it and the {@link #popEdge()} or {@link #popNode()}.
 * 
 */
public class Path implements Structure {

	/**
	 * class level logger
	 */
	private static final Logger logger = Logger.getLogger(Path.class.getSimpleName());

	// ------------- ATTRIBUTES ------------

	/**
	 * The root of the path;
	 */
	private Node root = null;

	/**
	 * The list of edges that represents the path.
	 */
	Stack<Edge> edgePath;

	/**
	 * The list of nodes representing the path.
	 */
	Stack<Node> nodePath;

	// ------------- CONSTRUCTORS ------------

	/**
	 * New empty path.
	 */
	public Path() {
		edgePath = new Stack<Edge>();
		nodePath = new Stack<Node>();
	}

	/**
	 * Get the root (the first node) of the path.
	 * 
	 * @return the root of the path.
	 */
	public Node getRoot() {
		return this.root;
	}

	/**
	 * Set the root (first node) of the path.
	 * 
	 * @param root
	 *            The root of the path.
	 */
	public void setRoot(Node root) {
		if (this.root == null) {
			this.root = root;
			nodePath.push(root);
		} else {
			logger.warning("Root node is not null - first use the clear method.");
		}
	}

	/**
	 * Says whether the path contains this node or not.
	 * 
	 * @param node
	 *            The node tested for existence in the path.
	 * @return <code>true</code> if the path contains the node.
	 */
	public boolean contains(Node node) {
		return nodePath.contains(node);
	}

	/**
	 * Says whether the path contains this edge or not.
	 * 
	 * @param edge
	 *            The edge tested for existence in the path.
	 * @return <code>true</code> if the path contains the edge.
	 */
	public boolean contains(Edge edge) {
		return edgePath.contains(edge);
	}

	/**
	 * Returns true if the path is empty.
	 * 
	 * @return <code>true</code> if the path is empty.
	 */
	public boolean empty() {
		return nodePath.empty();
	}

	/**
	 * Returns the size of the path
	 */
	public int size() {
		return nodePath.size();
	}

	/**
	 * It returns the sum of the <code>characteristic</code> given value in the
	 * Edges of the path.
	 * 
	 * @param characteristic
	 *            The characteristic.
	 * @return Sum of the characteristics.
	 */
	public Double getPathWeight(String characteristic) {
		double d = 0;
		for (Edge l : edgePath) {
			d += (Double) l.getAttribute(characteristic, Number.class);
		}
		return d;
	}

	/**
	 * Returns the list of edges representing the path.
	 * 
	 * @return The list of edges representing the path.
	 */
	public List<Edge> getEdgePath() {
		return edgePath;
	}

	/**
	 * Construct an return a list of nodes that represents the path.
	 * 
	 * @return A list of nodes representing the path.
	 */
	public List<Node> getNodePath() {
		return nodePath;
	}


	/**
	 * Adds a node and an edge to the path. If root is not set, the node will be
	 * set as root. Otherwise from node must be the same as the head node of the
	 * path.
	 *
	 * @param from
	 * 		The start node.
	 * @param edge
	 * 		The edge used.
	 */
	public void add(Node from, Edge edge) {
		if (root == null) {
			if (from == null) {
				throw new IllegalArgumentException("From node cannot be null.");
			} else {
				setRoot(from);
			}
		}

		if (from == null) {
			from = nodePath.peek();
		}

		if (!nodePath.peek().equals(from)) {
			throw new IllegalArgumentException("From node must be at the head of the path");
		}

		if (!edge.getSourceNode().equals(from) && !edge.getTargetNode().equals(from)) {
			throw new IllegalArgumentException("From node must be part of the edge");
		}

		nodePath.push(edge.getOpposite(from));
		edgePath.push(edge);
	}

	/**
	 * Adds an edge to the path.
	 *
	 * @param edge
	 * 		The edge to add to the path.
	 */
	public void add(Edge edge) {
		if (nodePath.isEmpty()) {
			add(null, edge);
		} else {
			add(nodePath.peek(), edge);
		}
	}

	/**
	 * A synonym for {@link #add(Edge)}.
	 */
	public void push(Node from, Edge edge) {
		add(from, edge);
	}

	/**
	 * A synonym for {@link #add(Edge)}.
	 */
	public void push(Edge edge) {
		add(edge);
	}

	/**
	 * This methods pops the 2 stacks (<code>edgePath</code> and
	 * <code>nodePath</code>) and returns the removed edge.
	 * 
	 * @return The edge that have just been removed.
	 */
	public Edge popEdge() {
		nodePath.pop();
		return edgePath.pop();
	}

	/**
	 * This methods pops the 2 stacks (<code>edgePath</code> and
	 * <code>nodePath</code>) and returns the removed node.
	 * 
	 * @return The node that have just been removed.
	 */
	public Node popNode() {
		edgePath.pop();
		return nodePath.pop();
	}

	/**
	 * Looks at the node at the top of the stack without removing it from the stack.
	 * 
	 * @return The node at the top of the stack.
	 */
	public Node peekNode() {
		return nodePath.peek();
	}

	/**
	 * Looks at the edge at the top of the stack without removing it from the stack.
	 * 
	 * @return The edge at the top of the stack.
	 */

	public Edge peekEdge() {
		return edgePath.peek();
	}

	/**
	 * Clears the path;
	 */
	public void clear() {
		nodePath.clear();
		edgePath.clear();
		// Runtime.getRuntime().gc();
		root = null;
	}

	/**
	 * Get a copy of this path
	 * 
	 * @return A copy of this path.
	 */
	@SuppressWarnings("unchecked")
	public Path getACopy() {
		Path newPath = new Path();
		newPath.root = this.root;
		newPath.edgePath = (Stack<Edge>) edgePath.clone();
		newPath.nodePath = (Stack<Node>) nodePath.clone();

		return newPath;
	}

	/**
	 * Remove all parts of the path that start at a given node and pass a new at
	 * this node.
	 */
	public void removeLoops() {
		int n = nodePath.size();
		// For each node-edge pair
		for (int i = 0; i < n; i++) {
			// Lookup each other following node. We start
			// at the end to find the largest loop possible.
			for (int j = n - 1; j > i; j--) {
				// If another node match, this is a loop.
				if (nodePath.get(i) == nodePath.get(j)) {
					// We found a loop between i and j.
					// Remove ]i,j].
					for (int k = i + 1; k <= j; k++) {
						nodePath.remove(i + 1);
						edgePath.remove(i);
					}
					n -= (j - i);
					j = i; // To stop the search.
				}
			}
		}
	}

	/**
	 * Compare the content of the current path and the specified path to decide
	 * weather they are equal or not.
	 * 
	 * @param p
	 *            A path to compare to the curent one.
	 * @return True if both paths are equal.
	 */
	public boolean equals(Path p) {
		if (nodePath.size() != p.nodePath.size()) {
			return false;
		} else {
			for (int i = 0; i < nodePath.size(); i++) {
				if (nodePath.get(i) != p.nodePath.get(i)) {
					return false;
				}
			}
		}
		return true;
	}

	// ------------ UTILITY METHODS ------------

	/**
	 * Returns a String description of the path.
	 * 
	 * @return A String representation of the path.
	 */
	@Override
	public String toString() {
		return nodePath.toString();
	}

	/**
	 * Returns the size of the path. Identical to {@link #size()}.
	 * 
	 * @return The size of the path.
	 */
	@Override
	public int getNodeCount() {
		return nodePath.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Structure#getEdgeCount()
	 */
	@Override
	public int getEdgeCount() {
		return edgePath.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Structure#nodes()
	 */
	@Override
	public Stream<Node> nodes() {
		return nodePath.stream();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Structure#edges()
	 */
	@Override
	public Stream<Edge> edges() {
		return edgePath.stream();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Structure#getNodeSet()
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> Collection<T> getNodeSet() {
		return (Collection<T>) nodePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Structure#getEdgeSet()
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> Collection<T> getEdgeSet() {
		return (Collection<T>) edgePath;
	}
}