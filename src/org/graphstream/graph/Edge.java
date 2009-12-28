/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.graph;

/**
 * A general purpose class that provides methods for the management of edges in
 * a graph.
 * 
 * <h3>Important</h3>
 * Implementing classes may indicate the complexity of their implementation of
 * the methods with the <code>complexity</code> tag.
 * 
 * @since July 12 2007
 */
public interface Edge extends Element
{
	/**
	 * Is the edge directed ?.
	 * @return True if the edge is directed.
	 * @complexity O(1)
	 */
	boolean isDirected();

	/**
	 * First node of the edge. This is equivalent to the {@link #getSourceNode()}
	 * method, but may be clearer in the source code if the graph you are using
	 * is not directed.
	 * @see #getNode1()
	 * @see #getSourceNode()
	 * @return The first node of the edge.
	 * @complexity O(1)
	 */
	Node getNode0();

	/**
	 * Second node of the edge. This is equivalent to the {@link #getTargetNode()}
	 * method, but may be clearer in the source code if the graph you are using
	 * is not directed.
	 * @see #getNode0()
	 * @see #getTargetNode()
	 * @return The second node of the edge.
	 * @complexity O(1)
	 */
	Node getNode1();

	/**
	 * Start node. When the edge is directed this is the source node, in this
	 * case you can get the opposite node using {@link #getTargetNode()}. This
	 * is equivalent to the {@link #getNode0()} method but may be clearer in
	 * the source code if the graph you are using is directed.
	 * @see #getNode0()
	 * @see #getTargetNode()
	 * @return The origin node of the edge.
	 * @complexity O(1)
	 */
	Node getSourceNode();

	/**
	 * End node. When the edge is directed this is the target node, in this
	 * case you can get the opposite node using {@link #getSourceNode()}. This
	 * is equivalent to the {@link #getNode1()} method but may be clearer in
	 * the source code if the graph you are using is directed.
	 * @see #getNode1()
	 * @see #getSourceNode()
	 * @return The destination node of the edge.
	 * @complexity O(1)
	 */
	Node getTargetNode();

	/**
	 * When knowing one node and one edge of this node, this method return the
	 * node at the other end of the edge. Return null if the given node is not
	 * at any end of the edge.
	 * @param node The node we search the opposite of.
	 * @return the opposite node of the given node.
	 * @complexity O(1)
	 */
	Node getOpposite( Node node );

	/**
	 * Direct or undirect the edge. You make this edge directed, it will be
	 * oriented from its {@link #getSourceNode()} node toward its {@link #getTargetNode()}
	 * node. You can swap these nodes using {@link #switchDirection()}.
	 * @param on If true the edge becomes directed, else it becomes
	 * bidirectionnal.
	 * @complexity O(1)
	 */
	@Deprecated 
	void setDirected( boolean on );

	/**
	 * Swap the source and target nodes. This allows to redirect an edge.
	 * However, if the edge was not directed, this method does not direct it,
	 * see the {@link #setDirected(boolean)} method.
	 * @complexity O(1)
	 */
	@Deprecated
	void switchDirection();	
}