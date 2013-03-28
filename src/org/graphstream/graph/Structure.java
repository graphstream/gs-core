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
 * Structures are generic objects which may contain nodes and edges.
 * 
 */
public interface Structure {
	/**
	 * Number of nodes in this graph.
	 * 
	 * @return The number of nodes.
	 */
	int getNodeCount();

	/**
	 * Number of edges in this graph.
	 * 
	 * @return The number of edges.
	 */
	int getEdgeCount();

	/**
	 * Iterator on the set of nodes, in an undefined order. This method is
	 * implicitly generic and returns an Iterator over something which extends
	 * Node. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedNode&gt; ite = graph.getNodeIterator();
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Node&gt;.
	 * 
	 * @return The iterator.
	 */
	<T extends Node> Iterator<T> getNodeIterator();

	/**
	 * Iterator on the set of edges, in an undefined order. This method is
	 * implicitly generic and returns an Iterator over something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedEdge&gt; ite = graph.getEdgeIterator();
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Edge&gt;.
	 * 
	 * @return The iterator.
	 */
	<T extends Edge> Iterator<T> getEdgeIterator();

	/**
	 * Set of nodes usable in a for-each instruction. This method is implicitly
	 * generic and returns an Iterable over something which extends Node. The
	 * return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * Iterable&lt;ExtendedNode&gt; ite = struct.getEachNode();
	 * </pre>
	 * 
	 * the method will return an Iterable&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return an Iterable&lt;Node&gt;. It is possible
	 * to use it in a for-each loop by giving the parameter :
	 * 
	 * <pre>
	 * for (ExtendedNode n : struct.&lt;ExtendedNode&gt; getEachNode()) {
	 * 	// ...
	 * }
	 * </pre>
	 * 
	 * @return An "iterable" view of the set of nodes.
	 * @see #getNodeIterator()
	 * @see #getEachNode()
	 */
	<T extends Node> Iterable<? extends T> getEachNode();

	/**
	 * Set of edges usable in a for-each instruction. This method is implicitly
	 * generic and returns an Iterable over something which extends Edge. The
	 * return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * Iterable&lt;ExtendedNEdge&gt; ite = struct.getEachEdge();
	 * </pre>
	 * 
	 * the method will return an Iterable&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterable&lt;Edge&gt;. It is possible
	 * to use it in a for-each loop by giving the parameter :
	 * 
	 * <pre>
	 * for (ExtendedEdge e : struct.&lt;ExtendedEdge&gt; getEachEdge()) {
	 * 	// ...
	 * }
	 * </pre>
	 * 
	 * @return An "iterable" view of the set of edges.
	 * @see #getEdgeIterator()
	 * @see #getEdgeSet()
	 */
	<T extends Edge> Iterable<? extends T> getEachEdge();

	/**
	 * Unmodifiable view of the set of nodes. This method is implicitly generic
	 * and returns a Collection of something which extends Node. The return type
	 * is the one of the left part of the assignment. For example, in the
	 * following call :
	 * 
	 * <pre>
	 * Collection&lt;ExtendedNode&gt; c = struct.getNodeSet();
	 * </pre>
	 * 
	 * the method will return a Collection&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return a Collection&lt;Node&gt;.
	 * 
	 * @return A set of nodes that can only be read, not changed.
	 * @see #getNodeIterator()
	 * @see #getEachNode()
	 */
	<T extends Node> Collection<T> getNodeSet();

	/**
	 * Unmodifiable view of the set of edges. This method is implicitly generic
	 * and returns a Collection of something which extends Edge. The return type
	 * is the one of the left part of the assignment. For example, in the
	 * following call :
	 * 
	 * <pre>
	 * Collection&lt;ExtendedEdge&gt; c = struct.getEdgeSet();
	 * </pre>
	 * 
	 * the method will return a Collection&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return a Collection&lt;Edge&gt;.
	 * 
	 * @return A set of edges that can only be read, not changed.
	 * @see #getEdgeIterator()
	 * @see #getEachEdge()
	 */
	<T extends Edge> Collection<T> getEdgeSet();
}
