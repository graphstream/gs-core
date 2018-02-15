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
package org.graphstream.graph;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * An Interface that advises general purpose methods for handling nodes as
 * elements of a graph.
 * 
 * <h3>Important</h3>
 * <p>
 * Implementing classes should indicate the complexity of their implementation
 * for each method.
 * </p>
 * 
 * @since July 12 2007
 */
public interface Node extends Element, Iterable<Edge> {
	/**
	 * Parent graph. Some elements are not able to give their parent graph.
	 * 
	 * @return The graph containing this node or null if unknown.
	 */
	Graph getGraph();

	/**
	 * Total number of relations with other nodes or this node.
	 * 
	 * @return The number of edges/relations/links.
	 */
	int getDegree();

	/**
	 * Number of leaving edges.
	 * 
	 * @return the count of edges that only leave this node plus all undirected
	 *         edges.
	 */
	int getOutDegree();

	/**
	 * Number of entering edges.
	 * 
	 * @return the count of edges that only enter this node plus all undirected
	 *         edges.
	 */
	int getInDegree();

	/**
	 * Retrieve an edge that leaves this node toward 'id'.
	 * <p>
	 * This method selects only edges leaving this node an pointing at node 'id'
	 * (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeToward(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param id
	 *            Identifier of the target node.
	 * @return Directed edge going from this node to 'id', or undirected edge if it
	 *         exists, else null.
	 */
	Edge getEdgeToward(String id);

	/**
	 * Retrieve an edge that leaves node 'id' toward this node.
	 * <p>
	 * This method selects only edges leaving node 'id' an pointing at this node
	 * (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeFrom(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param id
	 *            Identifier of the source node.
	 * @return Directed edge going from node 'id' to this node, or undirected edge
	 *         if it exists, else null.
	 */
	Edge getEdgeFrom(String id);

	/**
	 * Retrieve an edge between this node and the node 'id', if it exits.
	 * <p>
	 * This method selects directed or undirected edges. If the edge is directed,
	 * its direction is not important and leaving or entering edges will be
	 * selected.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeBetween(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param id
	 *            Identifier of the opposite node.
	 * @return Edge between node 'id' and this node if it exists, else null.
	 */
	Edge getEdgeBetween(String id);

	/**
	 * Stream over neighbor nodes connected to this node via one or more edges. This
	 * iterator iterates across any leaving, entering and non directed edges (nodes
	 * are neighbors even if they only have a directed edge from them toward this
	 * node). If there are multiple edges connecting the same node, it might be
	 * iterated several times.
	 * 
	 * @return The stream, neighbors are streamed in arbitrary order.
	 */
	default Stream<Node> neighborNodes() {
		return edges().map(edge -> {
			return edge.getOpposite(Node.this);
		});
	}

	/**
	 * I-th edge. Edges are stored in no given order.
	 * <p>
	 * However this method allows to iterate very quickly on all edges, or to choose
	 * a given edge with direct access.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdge(i);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param i
	 *            Index of the edge.
	 * @return The i-th edge.
	 * @throws IndexOutOfBoundsException
	 *             if <code>i</code> is negative or greater than or equal to the
	 *             degree
	 */
	Edge getEdge(int i);

	/**
	 * I-th entering edge. Edges are stored in no given order.
	 * <p>
	 * However this method allows to iterate very quickly on all entering edges, or
	 * to choose a given entering edge with direct access.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEnteringEdge(i);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param i
	 *            Index of the edge.
	 * @return The i-th entering edge.
	 * @throws IndexOutOfBoundsException
	 *             if <code>i</code> is negative or greater than or equal to the
	 *             in-degree
	 */
	Edge getEnteringEdge(int i);

	/**
	 * I-th leaving edge. Edges are stored in no given order.
	 * <p>
	 * However this method allows to iterate very quickly on all leaving edges, or
	 * to choose a given leaving edge with direct access.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getLeavingEdge(i);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param i
	 *            Index of the edge.
	 * @return The i-th leaving edge.
	 * @throws IndexOutOfBoundException
	 *             if <code>i</code> is negative or greater than or equal to the
	 *             out-degree
	 */
	Edge getLeavingEdge(int i);

	/**
	 * Iterator for breadth first exploration of the graph, starting at this node.
	 * <p>
	 * If the graph is not connected, only a part of it will be explored. By
	 * default, this iterator will respect edge orientation.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return an Iterator over something which
	 * extends Node. The return type is the one of the left part of the assignment.
	 * For example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedNode&gt; ite = node.getBreadthFirstIterator();
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Node&gt;.
	 * </p>
	 * 
	 * @return An iterator able to explore the graph in a breadth first way starting
	 *         at this node.
	 */
	Iterator<Node> getBreadthFirstIterator();

	/**
	 * Iterator for breadth first exploration of the graph, starting at this node.
	 * <p>
	 * If the graph is not connected, only a part of it will be explored.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return an Iterator over something which
	 * extends Node. The return type is the one of the left part of the assignment.
	 * For example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedNode&gt; ite = node.getBreadthFirstIterator(true);
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Node&gt;.
	 * </p>
	 * 
	 * @param directed
	 *            If false, the iterator will ignore edge orientation (the default
	 *            is "True").
	 * @return An iterator able to explore the graph in a breadth first way starting
	 *         at this node.
	 */
	Iterator<Node> getBreadthFirstIterator(boolean directed);

	/**
	 * Iterator for depth first exploration of the graph, starting at this node.
	 * <p>
	 * If the graph is not connected, only a part of it will be explored. By
	 * default, this iterator will respect edge orientation.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return an Iterator over something which
	 * extends Node. The return type is the one of the left part of the assignment.
	 * For example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedNode&gt; ite = node.getDepthFirstIterator();
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Node&gt;.
	 * </p>
	 * 
	 * @return An iterator able to explore the graph in a depth first way starting
	 *         at this node.
	 * @complexity of the depth first iterator O(n+m) with n the number of nodes and
	 *             m the number of edges.
	 */
	Iterator<Node> getDepthFirstIterator();

	/**
	 * Iterator for depth first exploration of the graph, starting at this node.
	 * <p>
	 * If the graph is not connected, only a part of it will be explored.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return an Iterator over something which
	 * extends Node. The return type is the one of the left part of the assignment.
	 * For example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedNode&gt; ite = node.getDepthFirstIterator(true);
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Node&gt;.
	 * </p>
	 * 
	 * @param directed
	 *            If false, the iterator will ignore edge orientation (the default
	 *            is "True").
	 * @return An iterator able to explore the graph in a depth first way starting
	 *         at this node.
	 */
	Iterator<Node> getDepthFirstIterator(boolean directed);

	/**
	 * Stream over all entering and leaving edges.
	 * 
	 * @return A stream over all directed and undirected edges, leaving or entering.
	 */
	Stream<Edge> edges();

	/**
	 * Stream over all leaving edges.
	 * 
	 * @return A stream over only edges that leave this node plus all undirected
	 *         edges.
	 */
	default Stream<Edge> leavingEdges() {
		return edges().filter(e -> (e.getSourceNode() == this));
	}

	/**
	 * Stream over all entering edges.
	 *
	 * @return A stream over only edges that enter this node plus all undirected
	 *         edges.
	 */
	default Stream<Edge> enteringEdges() {
		return edges().filter(e -> (e.getTargetNode() == this));
	}

	@Override
	default Iterator<Edge> iterator() {
		return edges().iterator();
	}

	/**
	 * Override the Object.toString() method.
	 */
	String toString();

	// New methods

	/**
	 * True if an edge leaves this node toward node 'id'.
	 *
	 * @param id
	 *            Identifier of the target node.
	 * @return True if a directed edge goes from this node to 'id' or if an
	 *         undirected edge exists.
	 */
	default boolean hasEdgeToward(String id) {
		return getEdgeToward(id) != null;
	}

	/**
	 * True if an edge leaves this node toward a given node.
	 * 
	 * @param node
	 *            The target node.
	 * @return True if a directed edge goes from this node to the other node or if
	 *         an undirected edge exists.
	 */
	default boolean hasEdgeToward(Node node) {
		return getEdgeToward(node) != null;
	}

	/**
	 * True if an edge leaves this node toward a node with given index.
	 * 
	 * @param index
	 *            Index of the target node.
	 * @return True if a directed edge goes from this node to the other node or if
	 *         an undirected edge exists.
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than {@code
	 *             getNodeCount() - 1}.
	 */
	default boolean hasEdgeToward(int index) throws IndexOutOfBoundsException {
		return getEdgeToward(index) != null;
	}

	/**
	 * True if an edge enters this node from node 'id'.
	 *
	 * @param id
	 *            Identifier of the source node.
	 * @return True if a directed edge goes from this node to 'id' or if an
	 *         undirected edge exists.
	 */
	default boolean hasEdgeFrom(String id) {
		return getEdgeFrom(id) != null;
	}

	/**
	 * True if an edge enters this node from a given node.
	 * 
	 * @param node
	 *            The source node.
	 * @return True if a directed edge goes from the other node to this node or if
	 *         an undirected edge exists.
	 */
	default boolean hasEdgeFrom(Node node) {
		return getEdgeFrom(node) != null;
	}

	/**
	 * True if an edge enters this node from a node with given index.
	 * 
	 * @param index
	 *            Index of the source node.
	 * @return True if a directed edge goes from the other node to this node or if
	 *         an undirected edge exists.
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than {@code
	 *             getNodeCount() - 1}.
	 */
	default boolean hasEdgeFrom(int index) throws IndexOutOfBoundsException {
		return getEdgeFrom(index) != null;
	}

	/**
	 * True if an edge exists between this node and node 'id'.
	 *
	 * @param id
	 *            Identifier of another node.
	 * @return True if a edge exists between this node and node 'id'.
	 */
	default boolean hasEdgeBetween(String id) {
		return getEdgeBetween(id) != null;
	}

	/**
	 * True if an edge exists between this node and another node.
	 * 
	 * @param node
	 *            Another node.
	 * @return True if an edge exists between this node and the other node.
	 */
	default boolean hasEdgeBetween(Node node) {
		return getEdgeBetween(node) != null;
	}

	/**
	 * True if an edge exists between this node and a node with given index.
	 * 
	 * @param index
	 *            Index of another node.
	 * @return True if an edge exists between this node and the other node.
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than {@code
	 *             getNodeCount() - 1}.
	 */
	default boolean hasEdgeBetween(int index) throws IndexOutOfBoundsException {
		return getEdgeBetween(index) != null;
	}

	/**
	 * Retrieves an edge that leaves this node toward another node.
	 * <p>
	 * This method selects only edges leaving this node an pointing at the parameter
	 * node (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and returns something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeToward(...);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param node
	 *            The target node.
	 * @return Directed edge going from this node to the parameter node, or
	 *         undirected edge if it exists, else null.
	 */
	Edge getEdgeToward(Node node);

	/**
	 * Retrieves an edge that leaves this node toward the node with given index.
	 * <p>
	 * This method selects only edges leaving this node an pointing at the parameter
	 * node (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and returns something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeToward(...);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param index
	 *            Index of the target node.
	 * @return Directed edge going from this node to the parameter node, or
	 *         undirected edge if it exists, else null.
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than {@code
	 *             getNodeCount() - 1}.
	 */
	Edge getEdgeToward(int index) throws IndexOutOfBoundsException;

	/**
	 * Retrieves an edge that leaves given node toward this node.
	 * <p>
	 * This method selects only edges leaving the other node an pointing at this
	 * node (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and returns something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeFrom(...);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param node
	 *            The source node.
	 * @return Directed edge going from the parameter node to this node, or
	 *         undirected edge if it exists, else null.
	 */
	Edge getEdgeFrom(Node node);

	/**
	 * Retrieves an edge that leaves node with given index toward this node.
	 * <p>
	 * This method selects only edges leaving the other node an pointing at this
	 * node (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and returns something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeFrom(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param index
	 *            Index of the source node.
	 * @return Directed edge going from the parameter node to this node, or
	 *         undirected edge if it exists, else null.
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than {@code
	 *             getNodeCount() - 1}.
	 */
	Edge getEdgeFrom(int index) throws IndexOutOfBoundsException;

	/**
	 * Retrieves an edge between this node and and another node if one exists.
	 * <p>
	 * This method selects directed or undirected edges. If the edge is directed,
	 * its direction is not important and leaving or entering edges will be
	 * selected.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeBetween(...);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param node
	 *            The opposite node.
	 * @return Edge between this node and the parameter node if it exists, else
	 *         null.
	 */
	Edge getEdgeBetween(Node node);

	/**
	 * Retrieves an edge between this node and the node with index i if one exists.
	 * <p>
	 * This method selects directed or undirected edges. If the edge is directed,
	 * its direction is not important and leaving or entering edges will be
	 * selected.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeBetween(...);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 * 
	 * @param index
	 *            The index of the opposite node.
	 * @return Edge between node with index i and this node if it exists, else null.
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than {@code
	 *             getNodeCount() - 1}.
	 */
	Edge getEdgeBetween(int index) throws IndexOutOfBoundsException;

}