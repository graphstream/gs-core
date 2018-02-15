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
 * @since 2011-07-22
 * 
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph.implementations;

import java.util.Iterator;

import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.DepthFirstIterator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.SourceBase;

/**
 * <p>
 * This class provides a basic implementation of {@code Node} interface, to
 * minimize the effort required to implement this interface.
 * </p>
 * 
 * <p>
 * This class implements all the methods of
 * {@link org.graphstream.graph.implementations.AbstractElement} and most of the
 * methods of {@link org.graphstream.graph.Node} (there are "only" ten abstract
 * methods). In addition to these, subclasses must provide implementations for
 * {@link #addEdgeCallback(AbstractEdge)} and
 * {@link #removeEdgeCallback(AbstractEdge)} which are called by the parent
 * graph when an edge incident to this node is added to or removed from the
 * graph. This class has a low memory overhead (one reference as field).
 * </p>
 */
public abstract class AbstractNode extends AbstractElement implements Node {

	// *** Fields ***

	/**
	 * The graph to which this node belongs
	 */
	protected AbstractGraph graph;

	// *** Constructors

	/**
	 * Constructs a new node. This constructor copies the parameters into the
	 * corresponding fields
	 * 
	 * @param graph
	 *            The graph to which this node belongs.
	 * @param id
	 *            Unique identifier of this node.
	 */
	protected AbstractNode(AbstractGraph graph, String id) {
		super(id);
		this.graph = graph;
	}

	// *** Inherited from abstract element ***

	@Override
	protected void attributeChanged(AttributeChangeEvent event, String attribute, Object oldValue, Object newValue) {
		graph.listeners.sendAttributeChangedEvent(id, SourceBase.ElementType.NODE, attribute, event, oldValue,
				newValue);
	}

	// *** Inherited from Node ***

	/**
	 * This implementation returns {@link #graph}.
	 * 
	 * @see org.graphstream.graph.Node#getGraph()
	 */
	public Graph getGraph() {
		return graph;
	}

	// [has|get]Edge[Toward|From|Between](Node|int|String) -> 2 * 3 * 3 = 18
	// methods

	/**
	 * This implementation uses {@link #getEdgeToward(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeToward(int)
	 */
	@Override
	public Edge getEdgeToward(int index) {
		return getEdgeToward(graph.getNode(index));
	}

	/**
	 * This implementation uses {@link #getEdgeToward(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeToward(java.lang.String)
	 */
	@Override
	public Edge getEdgeToward(String id) {
		return getEdgeToward(graph.getNode(id));
	}

	/**
	 * This implementation uses {@link #getEdgeFrom(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeFrom(int)
	 */
	@Override
	public Edge getEdgeFrom(int index) {
		return getEdgeFrom(graph.getNode(index));
	}

	/**
	 * This implementation uses {@link #getEdgeFrom(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeFrom(java.lang.String)
	 */
	@Override
	public Edge getEdgeFrom(String id) {
		return getEdgeFrom(graph.getNode(id));
	}

	/**
	 * This implementation uses {@link #getEdgeBetween(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeBetween(int)
	 */
	@Override
	public Edge getEdgeBetween(int index) {
		return getEdgeBetween(graph.getNode(index));
	}

	/**
	 * This implementation uses {@link #getEdgeBetween(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeBetween(java.lang.String)
	 */
	@Override
	public Edge getEdgeBetween(String id) {
		return getEdgeBetween(graph.getNode(id));
	}

	/**
	 * This implementation creates an instance of
	 * {@link org.graphstream.graph.BreadthFirstIterator} and returns it.
	 * 
	 * @see org.graphstream.graph.Node#getBreadthFirstIterator()
	 */
	@Override
	public Iterator<Node> getBreadthFirstIterator() {
		// XXX change it when the old iterator disappears
		// XXX change the return type to have access to the other methods
		return new BreadthFirstIterator(this);
	}

	/**
	 * This implementation creates an instance of
	 * {@link org.graphstream.graph.BreadthFirstIterator} and returns it.
	 * 
	 * @see org.graphstream.graph.Node#getBreadthFirstIterator(boolean)
	 */
	@Override
	public Iterator<Node> getBreadthFirstIterator(boolean directed) {
		// XXX change it when the old iterator disappears
		// XXX change the return type to have access to the other methods
		return new BreadthFirstIterator(this, directed);
	}

	/**
	 * This implementation creates an instance of
	 * {@link org.graphstream.graph.DepthFirstIterator} and returns it.
	 * 
	 * @see org.graphstream.graph.Node#getDepthFirstIterator()
	 */
	@Override
	public Iterator<Node> getDepthFirstIterator() {
		// XXX change it when the old iterator disappears
		// XXX change the return type to have access to the other methods
		return new DepthFirstIterator(this);
	}

	/**
	 * This implementation creates an instance of
	 * {@link org.graphstream.graph.DepthFirstIterator} and returns it.
	 * 
	 * @see org.graphstream.graph.Node#getDepthFirstIterator(boolean)
	 */
	@Override
	public Iterator<Node> getDepthFirstIterator(boolean directed) {
		// XXX change it when the old iterator disappears
		// XXX change the return type to have access to the other methods
		return new DepthFirstIterator(this, directed);
	}

	// *** Other methods ***

	/**
	 * This method is called automatically when an edge incident to this node is
	 * created. Subclasses use it to add the edge to their data structure.
	 * 
	 * @param edge
	 *            a new edge incident to this node
	 */
	protected abstract boolean addEdgeCallback(AbstractEdge edge);

	/**
	 * This method is called automatically before removing an edge incident to this
	 * node. Subclasses use it to remove the edge from their data structure.
	 * 
	 * @param edge
	 *            an edge incident to this node that will be removed
	 */
	protected abstract void removeEdgeCallback(AbstractEdge edge);

	/**
	 * This method is called for each node when the graph is cleared. Subclasses may
	 * use it to clear their data structures in order to facilitate the garbage
	 * collection.
	 */
	protected abstract void clearCallback();

	/**
	 * Checks if an edge enters this node. Utility method that can be useful in
	 * subclasses.
	 * 
	 * @param e
	 *            an edge
	 * @return {@code true} if {@code e} is entering edge for this node.
	 */
	public boolean isEnteringEdge(Edge e) {
		return e.getTargetNode() == this || (!e.isDirected() && e.getSourceNode() == this);
	}

	/**
	 * Checks if an edge leaves this node. Utility method that can be useful in
	 * subclasses.
	 * 
	 * @param e
	 *            an edge
	 * @return {@code true} if {@code e} is leaving edge for this node.
	 */
	public boolean isLeavingEdge(Edge e) {
		return e.getSourceNode() == this || (!e.isDirected() && e.getTargetNode() == this);
	}

	/**
	 * Checks if an edge is incident to this node. Utility method that can be useful
	 * in subclasses.
	 * 
	 * @param e
	 *            an edge
	 * @return {@code true} if {@code e} is incident edge for this node.
	 */
	public boolean isIncidentEdge(Edge e) {
		return e.getSourceNode() == this || e.getTargetNode() == this;
	}
}
