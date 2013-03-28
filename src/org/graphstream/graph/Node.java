/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
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
package org.graphstream.graph;

import java.util.Collection;
import java.util.Iterator;

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
	 * @return the count of edges that only enter this node plus all undirected
	 *         edges.
	 */
	int getOutDegree();

	/**
	 * Number of entering edges.
	 * 
	 * @return the count of edges that only leave this node plus all undirected
	 *         edges.
	 */
	int getInDegree();

	/**
	 * True if an edge leaves this node toward node 'id'.
	 * 
	 * @param id
	 *            Identifier of the target node.
	 * @return True if a directed edge goes from this node to 'id' or if an
	 *         undirected edge exists.
	 */
	boolean hasEdgeToward(String id);

	/**
	 * True if an edge enters this node from node 'id'.
	 * 
	 * @param id
	 *            Identifier of the source node.
	 * @return True if a directed edge goes from this node to 'id' or if an
	 *         undirected edge exists.
	 */
	boolean hasEdgeFrom(String id);

	/**
	 * True if an edge exists between this node and node 'id'.
	 * 
	 * @param id
	 *            Identifier of another node.
	 * @return True if a edge exists between this node and node 'id'.
	 */
	boolean hasEdgeBetween(String id);

	/**
	 * Retrieve an edge that leaves this node toward 'id'.
	 * <p>
	 * This method selects only edges leaving this node an pointing at node 'id'
	 * (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeToward(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param id
	 *            Identifier of the target node.
	 * @return Directed edge going from this node to 'id', or undirected edge if
	 *         it exists, else null.
	 */
	<T extends Edge> T getEdgeToward(String id);

	/**
	 * Retrieve an edge that leaves node 'id' toward this node.
	 * <p>
	 * This method selects only edges leaving node 'id' an pointing at this node
	 * (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeFrom(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param id
	 *            Identifier of the source node.
	 * @return Directed edge going from node 'id' to this node, or undirected
	 *         edge if it exists, else null.
	 */
	<T extends Edge> T getEdgeFrom(String id);

	/**
	 * Retrieve an edge between this node and the node 'id', if it exits.
	 * <p>
	 * This method selects directed or undirected edges. If the edge is
	 * directed, its direction is not important and leaving or entering edges
	 * will be selected.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeBetween(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param id
	 *            Identifier of the opposite node.
	 * @return Edge between node 'id' and this node if it exists, else null.
	 */
	<T extends Edge> T getEdgeBetween(String id);

	/**
	 * Iterator on the set of connected edges.
	 * <p>
	 * This iterator iterates on all edges leaving and entering (this includes
	 * any non-directed edge present, and a non-directed edge is only iterated
	 * once).
	 * </p>
	 * <p>
	 * This method is implicitly generic and return an Iterator over something
	 * which extends Edge. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedEdge&gt; ite = node.getEdgeIterator();
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Edge&gt;.
	 * </p>
	 * 
	 * @return The iterator, edges are iterated in arbitrary order.
	 */
	<T extends Edge> Iterator<T> getEdgeIterator();

	/**
	 * Iterator only on leaving edges.
	 * <p>
	 * This iterator iterates only on directed edges going from this node to
	 * others (non-directed edges are included in the iteration).
	 * </p>
	 * <p>
	 * This method is implicitly generic and return an Iterator over something
	 * which extends Edge. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedEdge&gt; ite = node.getEnteringEdgeIterator();
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Edge&gt;.
	 * </p>
	 * 
	 * @return The iterator, edges are iterated in arbitrary order.
	 */
	<T extends Edge> Iterator<T> getEnteringEdgeIterator();

	/**
	 * Iterator only on entering edges.
	 * <p>
	 * This iterator iterates only on directed edges going from other nodes
	 * toward this node (non-directed edges are included in the iteration).
	 * </p>
	 * <p>
	 * This method is implicitly generic and return an Iterator over something
	 * which extends Edge. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedEdge&gt; ite = node.getLeavingEdgeIterator();
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Edge&gt;.
	 * </p>
	 * 
	 * @return The iterator, edges are iterated in arbitrary order.
	 */
	<T extends Edge> Iterator<T> getLeavingEdgeIterator();

	/**
	 * Iterator on the set of neighbor nodes connected to this node via one or
	 * more edges. This iterator iterates across any leaving, entering and non
	 * directed edges (nodes are neighbors even if they only have a directed
	 * edge from them toward this node). If there are multiple edges connecting
	 * the same node, it might be iterated several times.
	 * 
	 * @return The iterator, neighbors are iterated in arbitrary order.
	 */
	<T extends Node> Iterator<T> getNeighborNodeIterator();

	/**
	 * I-th edge. Edges are stored in no given order.
	 * <p>
	 * However this method allows to iterate very quickly on all edges, or to
	 * choose a given edge with direct access.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdge(i);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param i
	 *            Index of the edge.
	 * @return The i-th edge.
	 * @throws IndexOutOfBoundException
	 *             if <code>i</code> is negative or greater than or equal to the
	 *             degree
	 */
	<T extends Edge> T getEdge(int i);

	/**
	 * I-th entering edge. Edges are stored in no given order.
	 * <p>
	 * However this method allows to iterate very quickly on all entering edges,
	 * or to choose a given entering edge with direct access.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEnteringEdge(i);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param i
	 *            Index of the edge.
	 * @return The i-th entering edge.
	 * @throws IndexOutOfBoundException
	 *             if <code>i</code> is negative or greater than or equal to the
	 *             in-degree
	 */
	<T extends Edge> T getEnteringEdge(int i);

	/**
	 * I-th leaving edge. Edges are stored in no given order.
	 * <p>
	 * However this method allows to iterate very quickly on all leaving edges,
	 * or to choose a given leaving edge with direct access.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getLeavingEdge(i);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param i
	 *            Index of the edge.
	 * @return The i-th leaving edge.
	 * @throws IndexOutOfBoundException
	 *             if <code>i</code> is negative or greater than or equal to the
	 *             out-degree
	 */
	<T extends Edge> T getLeavingEdge(int i);

	/**
	 * Iterator for breadth first exploration of the graph, starting at this
	 * node.
	 * <p>
	 * If the graph is not connected, only a part of it will be explored. By
	 * default, this iterator will respect edge orientation.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return an Iterator over something
	 * which extends Node. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedNode&gt; ite = node.getBreadthFirstIterator();
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Node&gt;.
	 * </p>
	 * 
	 * @return An iterator able to explore the graph in a breadth first way
	 *         starting at this node.
	 */
	<T extends Node> Iterator<T> getBreadthFirstIterator();

	/**
	 * Iterator for breadth first exploration of the graph, starting at this
	 * node.
	 * <p>
	 * If the graph is not connected, only a part of it will be explored.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return an Iterator over something
	 * which extends Node. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
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
	 *            If false, the iterator will ignore edge orientation (the
	 *            default is "True").
	 * @return An iterator able to explore the graph in a breadth first way
	 *         starting at this node.
	 */
	<T extends Node> Iterator<T> getBreadthFirstIterator(boolean directed);

	/**
	 * Iterator for depth first exploration of the graph, starting at this node.
	 * <p>
	 * If the graph is not connected, only a part of it will be explored. By
	 * default, this iterator will respect edge orientation.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return an Iterator over something
	 * which extends Node. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedNode&gt; ite = node.getDepthFirstIterator();
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Node&gt;.
	 * </p>
	 * 
	 * @return An iterator able to explore the graph in a depth first way
	 *         starting at this node.
	 * @complexity of the depth first iterator O(n+m) with n the number of nodes
	 *             and m the number of edges.
	 */
	<T extends Node> Iterator<T> getDepthFirstIterator();

	/**
	 * Iterator for depth first exploration of the graph, starting at this node.
	 * <p>
	 * If the graph is not connected, only a part of it will be explored.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return an Iterator over something
	 * which extends Node. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
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
	 *            If false, the iterator will ignore edge orientation (the
	 *            default is "True").
	 * @return An iterator able to explore the graph in a depth first way
	 *         starting at this node.
	 */
	<T extends Node> Iterator<T> getDepthFirstIterator(boolean directed);

	/**
	 * Set of all entering and leaving edges.
	 * 
	 * <p>
	 * This method is implicitly generic and return an Iterable over something
	 * which extends Edge. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
	 * 
	 * <pre>
	 * Iterable&lt;ExtendedEdge&gt; ite = node.getEdgeSet();
	 * </pre>
	 * 
	 * the method will return an Iterable&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterable&lt;Edge&gt;.
	 * </p>
	 * 
	 * @return A collection containing all directed and undirected edges,
	 *         leaving or entering.
	 */
	<T extends Edge> Iterable<T> getEachEdge();

	/**
	 * Set of all leaving edges.
	 * <p>
	 * This method is implicitly generic and return an Iterable over something
	 * which extends Edge. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
	 * 
	 * <pre>
	 * Iterable&lt;ExtendedEdge&gt; ite = node.getLeavingEdgeSet();
	 * </pre>
	 * 
	 * the method will return an Iterable&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterable&lt;Edge&gt;.
	 * </p>
	 * 
	 * @return A collection of only edges that leave this node plus all
	 *         undirected edges.
	 */
	<T extends Edge> Iterable<T> getEachLeavingEdge();

	/**
	 * Set of all entering edges.
	 * <p>
	 * This method is implicitly generic and return an Iterable over something
	 * which extends Edge. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
	 * 
	 * <pre>
	 * Iterable&lt;ExtendedEdge&gt; ite = node.getEnteringEdgeSet();
	 * </pre>
	 * 
	 * the method will return an Iterable&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterable&lt;Edge&gt;.
	 * </p>
	 * 
	 * @return A collection of only edges that enter this node plus all
	 *         undirected edges.
	 */
	<T extends Edge> Iterable<T> getEachEnteringEdge();

	/**
	 * Set of all entering and leaving edges.
	 * 
	 * <p>
	 * This method is implicitly generic and return an Iterable over something
	 * which extends Edge. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
	 * 
	 * <pre>
	 * Iterable&lt;ExtendedEdge&gt; ite = node.getEdgeSet();
	 * </pre>
	 * 
	 * the method will return an Iterable&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterable&lt;Edge&gt;.
	 * </p>
	 * 
	 * @return A collection containing all directed and undirected edges,
	 *         leaving or entering.
	 */
	<T extends Edge> Collection<T> getEdgeSet();

	/**
	 * Set of all leaving edges.
	 * <p>
	 * This method is implicitly generic and return an Iterable over something
	 * which extends Edge. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
	 * 
	 * <pre>
	 * Iterable&lt;ExtendedEdge&gt; ite = node.getLeavingEdgeSet();
	 * </pre>
	 * 
	 * the method will return an Iterable&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterable&lt;Edge&gt;.
	 * </p>
	 * 
	 * @return A collection of only edges that leave this node plus all
	 *         undirected edges.
	 */
	<T extends Edge> Collection<T> getLeavingEdgeSet();

	/**
	 * Set of all entering edges.
	 * <p>
	 * This method is implicitly generic and return an Iterable over something
	 * which extends Edge. The return type is the one of the left part of the
	 * assignment. For example, in the following call :
	 * 
	 * <pre>
	 * Iterable&lt;ExtendedEdge&gt; ite = node.getEnteringEdgeSet();
	 * </pre>
	 * 
	 * the method will return an Iterable&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterable&lt;Edge&gt;.
	 * </p>
	 * 
	 * @return A collection of only edges that enter this node plus all
	 *         undirected edges.
	 */
	<T extends Edge> Collection<T> getEnteringEdgeSet();

	/**
	 * Override the Object.toString() method.
	 */
	String toString();

	// New methods

	/**
	 * True if an edge leaves this node toward a given node.
	 * 
	 * @param node
	 *            The target node.
	 * @return True if a directed edge goes from this node to the other node or
	 *         if an undirected edge exists.
	 */
	boolean hasEdgeToward(Node node);

	/**
	 * True if an edge leaves this node toward a node with given index.
	 * 
	 * @param index
	 *            Index of the target node.
	 * @return True if a directed edge goes from this node to the other node or
	 *         if an undirected edge exists.
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than {@code
	 *             getNodeCount() - 1}.
	 */
	boolean hasEdgeToward(int index) throws IndexOutOfBoundsException;

	/**
	 * True if an edge enters this node from a given node.
	 * 
	 * @param node
	 *            The source node.
	 * @return True if a directed edge goes from the other node to this node or
	 *         if an undirected edge exists.
	 */
	boolean hasEdgeFrom(Node node);

	/**
	 * True if an edge enters this node from a node with given index.
	 * 
	 * @param index
	 *            Index of the source node.
	 * @return True if a directed edge goes from the other node to this node or
	 *         if an undirected edge exists.
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than {@code
	 *             getNodeCount() - 1}.
	 */
	boolean hasEdgeFrom(int index) throws IndexOutOfBoundsException;

	/**
	 * True if an edge exists between this node and another node.
	 * 
	 * @param node
	 *            Another node.
	 * @return True if an edge exists between this node and the other node.
	 */
	boolean hasEdgeBetween(Node node);

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
	boolean hasEdgeBetween(int index) throws IndexOutOfBoundsException;

	/**
	 * Retrieves an edge that leaves this node toward another node.
	 * <p>
	 * This method selects only edges leaving this node an pointing at the
	 * parameter node (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and returns something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeToward(...);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param node
	 *            The target node.
	 * @return Directed edge going from this node to the parameter node, or
	 *         undirected edge if it exists, else null.
	 */
	<T extends Edge> T getEdgeToward(Node node);

	/**
	 * Retrieves an edge that leaves this node toward the node with given index.
	 * <p>
	 * This method selects only edges leaving this node an pointing at the
	 * parameter node (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and returns something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeToward(...);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
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
	<T extends Edge> T getEdgeToward(int index)
			throws IndexOutOfBoundsException;

	/**
	 * Retrieves an edge that leaves given node toward this node.
	 * <p>
	 * This method selects only edges leaving the other node an pointing at this
	 * node (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and returns something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeFrom(...);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param node
	 *            The source node.
	 * @return Directed edge going from the parameter node to this node, or
	 *         undirected edge if it exists, else null.
	 */
	<T extends Edge> T getEdgeFrom(Node node);

	/**
	 * Retrieves an edge that leaves node with given index toward this node.
	 * <p>
	 * This method selects only edges leaving the other node an pointing at this
	 * node (this also selects undirected edges).
	 * </p>
	 * <p>
	 * This method is implicitly generic and returns something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeFrom(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
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
	<T extends Edge> T getEdgeFrom(int index) throws IndexOutOfBoundsException;

	/**
	 * Retrieves an edge between this node and and another node if one exists.
	 * <p>
	 * This method selects directed or undirected edges. If the edge is
	 * directed, its direction is not important and leaving or entering edges
	 * will be selected.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeBetween(...);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param node
	 *            The opposite node.
	 * @return Edge between this node and the parameter node if it exists, else
	 *         null.
	 */
	<T extends Edge> T getEdgeBetween(Node node);

	/**
	 * Retrieves an edge between this node and the node with index i if one
	 * exists.
	 * <p>
	 * This method selects directed or undirected edges. If the edge is
	 * directed, its direction is not important and leaving or entering edges
	 * will be selected.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = node.getEdgeBetween(...);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param index
	 *            The index of the opposite node.
	 * @return Edge between node with index i and this node if it exists, else
	 *         null.
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than {@code
	 *             getNodeCount() - 1}.
	 */
	<T extends Edge> T getEdgeBetween(int index)
			throws IndexOutOfBoundsException;

}