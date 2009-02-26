/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.graph;

import java.util.Collection;
import java.util.Iterator;

/**
 * <p>
 * An Interface that advises general purpose methods for handling nodes as elements of a graph.
 * </p>
 * 
 * <h3>Important</h3>
 * <p>
 * Implementing classes should indicate the complexity of their implementation for each method.
 * </p>
 * 
 * @author Antoine Dutot
 * @author Yoann Pign�
 * @since July 12 2007
 */
public interface Node 
	extends Element
{

	/**
	 * Parent graph. Some elements are not able to give their parent graph.
	 * @return The graph containing this node or null if unknown.
	 */
	public Graph 
	getGraph();

	/**
	 * Some element may need to be assigned a perent graph depending on there implementation. 
	 * @param graph that holds this node.
	 *//*
	public void
	setGraph(Graph graph);
	*/
	
	/**
	 * Total number of relations with other nodes or this node.
	 * @return The number of edges/relations/links.
	 */
	public int 
	getDegree();

	/**
	 * Number of leaving edges.
	 * @return the count of edges that only enter this node plus all undirected edges.
	 */
	public int 
	getOutDegree();

	/**
	 * Number of entering edges.
	 * @return the count of edges that only leave this node plus all undirected edges.
	 */
	public int 
	getInDegree();

	/**
	 * True if an edge leaves this node toward node 'id'.
	 * @param id Identifier of the target node. 
	 * @return True if a directed edges goes from this node to 'id' or if
	 * an undirected edge exists. 
	 */
	public boolean 
	hasEdgeToward( String id );

	/**
	 * True if an edge enters this node from node 'id'.
	 * @param id Identifier of the source node.
	 * @return True if a directed edges goes from this node to 'id' or if an
	 * undirected edge exists.
	 */
	public boolean 
	hasEdgeFrom( String id );

	/**
	 * Retrieve an edge that leaves this node toward 'id'. This method selects
	 * only directed edges leaving this node an pointing at node 'id' (this
	 * also selects undirected edges).
	 * @param id Identifier of the target node.
	 * @return Directed edge going from this node to 'id', or
	 * undirected edge if it exists, else null.
	 * @complexity O(1)
	 */
	public Edge 
	getEdgeToward( String id );

	/**
	 * Retrieve an edge that leaves node 'id' toward this node. This method 
	 * selects only directed edges leaving node 'id an pointing at this node
	 * (this also selects undirected edges).
	 * @param id Identifier of the source node.
	 * @return Directed edge going from node 'id' to this node, or undirected
	 * edge if it exists, else null.
	 */
	public Edge 
	getEdgeFrom( String id );

	/**
	 * Iterator on the set of connected edges. This iterator iterates on all
	 * edges leaving and entering (this includes any non-directed edge present,
	 * and a non-directed edge is only iterated once).
	 * @return The iterator, edges are iterated in arbitrary order.
	 */
	public Iterator<? extends Edge> 
	getEdgeIterator();

	/**
	 * Iterator only on leaving edges. This iterator iterates only on directed
	 * edges going from this node to others (non-directed edges are included
	 * in the iteration).
	 * @return The iterator, edges are iterated in arbitrary order.
	 */
	public Iterator<? extends Edge> 
	getEnteringEdgeIterator();

	/**
	 * Iterator only on entering edges. This iterator iterates only on directed
	 * edges going from other nodes toward this node (non-directed edges are
	 * included in the iteration).
	 * @return The iterator, edges are iterated in arbitrary order.
	 */
	public Iterator<? extends Edge> 
	getLeavingEdgeIterator();

	/**
	 * Iterator on the set of neighbor nodes connected to this node via one or
	 * more edges. This iterator iterate across any leaving, entering and non
	 * directed edge (nodes are neighbor even if they only have a directed edge
	 * from them toward this node).
	 * @return The iterator, neighbor are iterated in arbitrary order.
	 */
	public Iterator<? extends Node> 
	getNeighborNodeIterator();

	/**
	 * I-th edge. Edges are stored in no given order. However this method
	 * allows to iterate very quickly on all edges, or to choose a given
	 * edge with direct access.
	 * @param i Index of the edge.
	 * @return The i-th edge.
	 */
	public Edge 
	getEdge( int i );

	/**
	 * Iterator for breadth first exploration of the graph, starting at this
	 * node. If the graph is not connex, only a part of it will be explored. By
	 * default, this iterator will respect edge orientation.
	 * @return An iterator able to explore the graph in a breadth first way
	 *         starting at this node.
	 */
	public Iterator<? extends Node> 
	getBreadthFirstIterator();

	/**
	 * Iterator for breadth first exploration of the graph, starting at this
	 * node. If the graph is not connex, only a part of it will be explored.
	 * @param directed If false, the iterator will ignore edge orientation (the
	 *        default is "True").
	 * @return An iterator able to explore the graph in a breadth first way
	 *         starting at this node.
	 */
	public Iterator<? extends Node> 
	getBreadthFirstIterator( boolean directed );

	/**
	 * Iterator for depth first exploration of the graph, starting at this
	 * node. If the graph is not connex, only a part of it will be explored. By
	 * default, this iterator will respect edge orientation.
	 * @return An iterator able to explore the graph in a depth first way
	 *         starting at this node.
	 * @complexity of the depth first iterator O(n+m) with n the number of
	 *             nodes and m the number of edges.
	 */
	public Iterator<? extends Node> 
	getDepthFirstIterator();

	/**
	 * Iterator for depth first exploration of the graph, starting at this
	 * node. If the graph is not connex, only a part of it will be explored.
	 * @param directed If false, the iterator will ignore edge orientation (the
	 *        default is "True").
	 * @return An iterator able to explore the graph in a depth first way
	 *         starting at this node.
	 */
	public Iterator<? extends Node> 
	getDepthFirstIterator( boolean directed );

	/**
	 * Set of all entering and leaving edges.
	 * @return A collection containing all directed and undirected edges, leaving or entering.
	 */
	@Deprecated
	public Collection<? extends Edge>
	getEdgeSet();

	/**
	 * Set of all leaving edges.
	 * @return A collection of only edges that leave this node plus all undirected edges.
	 * @complexity O(1)
	 */
	@Deprecated
	public Collection<? extends Edge> 
	getLeavingEdgeSet();

	/**
	 * Set of all entering edges.
	 * @return A collection of only edges that enter this node plus all undirected edges.
	 * @complexity O(1)
	 */
	@Deprecated
	public Collection<? extends Edge> 
	getEnteringEdgeSet();

	/**
	 * Override the Object.toString() method.
	 */
	public String 
	toString();
}