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
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph;

/**
 * A general purpose class that provides methods for the management of edges in
 * a graph.
 * <p>
 * <h3>Important</h3> Implementing classes may indicate the complexity of their
 * implementation of the methods with the <code>complexity</code> tag.
 *
 * @since July 12 2007
 */
public interface Edge extends Element {
	/**
	 * Is the edge directed ?.
	 *
	 * @return True if the edge is directed.
	 */
	boolean isDirected();

	/**
	 * Does the source and target of this edge identify the same node ?.
	 *
	 * @return True if this edge is a loop.
	 */
	boolean isLoop();

	/**
	 * First node of the edge.
	 * <p>
	 * This is equivalent to the {@link #getSourceNode()} method, but may be clearer
	 * in the source code if the graph you are using is not directed.
	 * </p>
	 *
	 * @return The first node of the edge.
	 * @see #getNode1()
	 * @see #getSourceNode()
	 */
	Node getNode0();

	/**
	 * Second node of the edge.
	 * <p>
	 * This is equivalent to the {@link #getTargetNode()} method, but may be clearer
	 * in the source code if the graph you are using is not directed.
	 * </p>
	 *
	 * @return The second node of the edge.
	 * @see #getNode0()
	 * @see #getTargetNode()
	 */
	Node getNode1();

	/**
	 * Start node.
	 * <p>
	 * When the edge is directed this is the source node, in this case you can get
	 * the opposite node using {@link #getTargetNode()}. This is equivalent to the
	 * {@link #getNode0()} method but may be clearer in the source code if the graph
	 * you are using is directed.
	 * </p>
	 *
	 * @return The origin node of the edge.
	 * @see #getNode0()
	 * @see #getTargetNode()
	 */
	Node getSourceNode();

	/**
	 * End node.
	 * <p>
	 * When the edge is directed this is the target node, in this case you can get
	 * the opposite node using {@link #getSourceNode()}. This is equivalent to the
	 * {@link #getNode1()} method but may be clearer in the source code if the graph
	 * you are using is directed.
	 * </p>
	 *
	 * @return The destination node of the edge.
	 * @see #getNode1()
	 * @see #getSourceNode()
	 */
	Node getTargetNode();

	/**
	 * When knowing one node and one edge of this node, this method return the node
	 * at the other end of the edge.
	 * <p>
	 * Return null if the given node is not at any end of the edge.
	 * </p>
	 *
	 * @param node
	 *            The node we search the opposite of.
	 * @return the opposite node of the given node.
	 */
	Node getOpposite(Node node);
}