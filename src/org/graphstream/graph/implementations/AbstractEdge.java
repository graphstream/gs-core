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
package org.graphstream.graph.implementations;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.stream.SourceBase.ElementType;

/**
 * <p>
 * This class provides a basic implementation of {@code Edge} interface, to
 * minimize the effort required to implement this interface.
 * </p>
 * 
 * <p>
 * Although this class is abstract it implements all the methods of
 * {@link org.graphstream.graph.Edge} and
 * {@link org.graphstream.graph.implementations.AbstractElement}. It has a low
 * memory overhead (3 references and a boolean as fields). All {@code Edge}
 * methods are executed in O(1) time.
 * </p>
 */
public class AbstractEdge extends AbstractElement implements Edge {

	// *** Fields ***

	/**
	 * The source node
	 */
	protected AbstractNode source;

	/**
	 * The target node
	 */
	protected AbstractNode target;

	/**
	 * Is this edge directed ?
	 */
	protected boolean directed;

	/**
	 * The graph to which this edge belongs
	 */
	protected AbstractGraph graph;

	// *** Constructors ***

	/**
	 * Constructs a new edge. This constructor copies the parameters into the
	 * corresponding fields.
	 * 
	 * @param id
	 *            Unique identifier of this edge.
	 * @param source
	 *            Source node.
	 * @param target
	 *            Target node.
	 * @param directed
	 *            Indicates if the edge is directed.
	 */
	protected AbstractEdge(String id, AbstractNode source, AbstractNode target,
			boolean directed) {
		super(id);
		assert source != null && target != null : "An edge cannot have null endpoints";
		this.source = source;
		this.target = target;
		this.directed = directed;
		this.graph = (AbstractGraph) source.getGraph();
	}


	// *** Inherited from AbstractElement ***

	@Override
	protected void attributeChanged(String sourceId, long timeId,
			String attribute, AttributeChangeEvent event, Object oldValue,
			Object newValue) {
		graph.listeners.sendAttributeChangedEvent(sourceId, timeId, id,
				ElementType.EDGE, attribute, event, oldValue,
				newValue);
	}

	/**
	 * @return The id of the parent graph
	 * @see org.graphstream.graph.implementations.AbstractElement#myGraphId()
	 */
	@Override
	protected String myGraphId() {
		return graph.getId();
	}

	/**
	 * This implementation calls the corresponding method of the parent graph
	 * 
	 * @see org.graphstream.graph.implementations.AbstractElement#newEvent()
	 */
	@Override
	protected long newEvent() {
		return graph.newEvent();
	}

	/**
	 * This implementation calls the corresponding method of the parent graph
	 * 
	 * @see org.graphstream.graph.implementations.AbstractElement#nullAttributesAreErrors()
	 */
	@Override
	protected boolean nullAttributesAreErrors() {
		return graph.nullAttributesAreErrors();
	}
	
	@Override
	public String toString() {
		return String.format("%s[%s-%s%s]", getId(), source, directed ? ">"
				: "-", target);
	}

	// *** Inherited from Edge ***

	@SuppressWarnings("unchecked")
	public <T extends Node> T getNode0() {
		return (T) source;
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getNode1() {
		return (T) target;
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getOpposite(Node node) {
		if (node == source)
			return (T) target;
		if (node == target)
			return (T) source;
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getSourceNode() {
		return (T) source;
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getTargetNode() {
		return (T) target;
	}

	public boolean isDirected() {
		return directed;
	}

	public boolean isLoop() {
		return source == target;
	}
}
