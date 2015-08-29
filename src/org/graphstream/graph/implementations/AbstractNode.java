/*
 * Copyright 2006 - 2015
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann PignÃ©      <yoann.pigne@graphstream-project.org>
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
package org.graphstream.graph.implementations;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

/**
 * <p>
 * This class provides a basic implementation of {@code Node} interface, to
 * minimize the effort required to implement this interface.
 * </p>
 *
 * <p>
 * This class implements all the methods of
 * {@link org.graphstream.graph.implementations#AbstractElement} and most of the
 * methods of {@link org.graphstream.graph#Node} (there are "only" ten abstract
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
	 * Constructs a new node. This constructor copies the parameters into
	 * the corresponding fields
	 *
	 * @param graph The graph to which this node belongs.
	 * @param id    Unique identifier of this node.
	 */
	protected AbstractNode(String id, AbstractGraph graph) {
		super(id);
		this.graph = graph;
	}

	// *** Inherited from abstract element ***
	@Override
	protected void attributeChanged(AttributeChangeEvent event,
		String attribute, Object oldValue, Object newValue) {
		graph.listeners.sendAttributeChangedEvent(id,
			ElementType.NODE, attribute, event, oldValue,
			newValue);
	}

	/**
	 * This implementation calls the corresponding method of the parent
	 * graph
	 *
	 * @see
	 * org.graphstream.graph.implementations.AbstractElement#nullAttributesAreErrors()
	 */
	@Override
	protected boolean nullAttributesAreErrors() {
		return graph.nullAttributesAreErrors();
	}

	// *** Inherited from Node ***
	/**
	 * This implementation returns {@link #graph}.
	 *
	 * @see org.graphstream.graph.Node#getGraph()
	 */
	@Override
	public AbstractGraph getGraph() {
		return graph;
	}

	// *** Other methods ***
	/**
	 * This method is called automatically when an edge incident to this
	 * node is created. Subclasses use it to add the edge to their data
	 * structure.
	 *
	 * @param edge a new edge incident to this node
	 */
	protected abstract boolean addEdgeCallback(AbstractEdge edge);

	/**
	 * This method is called automatically before removing an edge incident
	 * to this node. Subclasses use it to remove the edge from their data
	 * structure.
	 *
	 * @param edge an edge incident to this node that will be removed
	 */
	protected abstract void removeEdgeCallback(AbstractEdge edge);

	/**
	 * This method is called for each node when the graph is cleared.
	 * Subclasses may use it to clear their data structures in order to
	 * facilitate the garbage collection.
	 */
	protected abstract void clearCallback();

	/**
	 * Checks if an edge enters this node. Utility method that can be useful
	 * in subclasses.
	 *
	 * @param e an edge
	 * @return {@code true} if {@code e} is entering edge for this node.
	 */
	public boolean isEnteringEdge(Edge e) {
		return e.getTargetNode() == this
			|| (!e.isDirected() && e.getSourceNode() == this);
	}

	/**
	 * Checks if an edge leaves this node. Utility method that can be useful
	 * in subclasses.
	 *
	 * @param e an edge
	 * @return {@code true} if {@code e} is leaving edge for this node.
	 */
	public boolean isLeavingEdge(Edge e) {
		return e.getSourceNode() == this
			|| (!e.isDirected() && e.getTargetNode() == this);
	}

	/**
	 * Checks if an edge is incident to this node. Utility method that can
	 * be useful in subclasses.
	 *
	 * @param e an edge
	 * @return {@code true} if {@code e} is incident edge for this node.
	 */
	public boolean isIncidentEdge(Edge e) {
		return e.getSourceNode() == this || e.getTargetNode() == this;
	}
}
