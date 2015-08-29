/*
 * Copyright 2006 - 2015
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
package org.graphstream.ui.graphicGraph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.graphstream.ui.graphicGraph.GraphPosLengthUtils.nodePosition;

/**
 * Graphical node.
 *
 * <p>
 * A graphic node defines a position (x,y,z), a string label, and a style from
 * the style sheet.
 * </p>
 *
 * @see GraphicGraph
 */
public class GraphicNode extends GraphicElement implements Node {
	/**
	 * The position of the node. In graph units.
	 */
	public double x, y, z;

	public boolean positionned = false;

	/**
	 * New graphic node.
	 *
	 * @param id The node identifier.
	 * @param attributes The node attribute set (can be null).
	 */
	public GraphicNode(GraphicGraph graph, String id,
		HashMap<String, Object> attributes) {
		super(id, graph);

		if (attributes != null) {
			setAttributes(attributes);
		}
	}

	@Override
	public Selector.Type getSelectorType() {
		return Selector.Type.NODE;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getZ() {
		return z;
	}

	protected Point3 getPosition() {
		return new Point3(x, y, z);
	}

	protected void moveFromEvent(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;

		if (!positionned) {
			positionned = true;
		}

		graph.graphChanged = true;
		graph.boundsChanged = true;
	}

	@Override
	public void move(double x, double y, double z) {
		moveFromEvent(x, y, z);

		if (graph.feedbackXYZ) {
			setAttribute("xyz", x, y, z);
		}
	}

	@Override
	protected void attributeChanged(AttributeChangeEvent event,
		String attribute, Object oldValue, Object newValue) {
		super.attributeChanged(event, attribute, oldValue, newValue);
		char c = attribute.charAt(0);

		if (attribute.length() > 2 && c == 'u' && attribute.charAt(1) == 'i'
			&& attribute.startsWith("ui.sprite.")) {
			graph.spriteAttribute(event, this, attribute, newValue);
		} else if ((event == AttributeChangeEvent.ADD || event == AttributeChangeEvent.CHANGE)) {
			if (attribute.length() == 1) {
				switch (c) {
					case 'x':
						moveFromEvent(numberAttribute(newValue), y, z);
						break;
					case 'y':
						moveFromEvent(x, numberAttribute(newValue), z);
						break;
					case 'z':
						moveFromEvent(x, y, numberAttribute(newValue));
						break;
					default:
						break;
				}
			} else if (c == 'x'
				&& attribute.length() > 1
				&& attribute.charAt(1) == 'y'
				&& (attribute.length() == 2 || (attribute.length() == 3 && attribute
				.charAt(2) == 'z'))) {

				double pos[] = nodePosition(this);
				moveFromEvent(pos[0], pos[1], pos[2]);
			}
		}

		graph.listeners.sendAttributeChangedEvent(getId(), ElementType.NODE,
			attribute, event, oldValue, newValue);
	}

	/**
	 * Try to convert the object to a double.
	 *
	 * @param value The object to convert.
	 * @return The value.
	 */
	protected double numberAttribute(Object value) {
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		} else if (value instanceof String) {
			try {
				return Double.parseDouble((String) value);
			} catch (NumberFormatException e) {
			}
		} else if (value instanceof CharSequence) {
			try {
				return Double.parseDouble(((CharSequence) value).toString());
			} catch (NumberFormatException e) {
			}
		}

		return 0;
	}

	@Override
	protected void removed() {
		// NOP
	}

	// Node interface.
	@Override
	public int getDegree() {
		List<GraphicEdge> edges = graph.connectivity.get(this);

		if (edges != null) {
			return edges.size();
		}

		return 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEdge(int i) {
		List<GraphicEdge> edges = graph.connectivity.get(this);

		if (edges != null && i >= 0 && i < edges.size()) {
			return (T) edges.get(i);
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		List<GraphicEdge> edges = graph.connectivity.get(this);

		if (edges != null) {
			return (Iterator<T>) Collections.unmodifiableList(edges).iterator();
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> Collection<T> getEdgeSet() {
		return (Collection<T>) Collections
			.unmodifiableCollection(graph.connectivity.get(this));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterator<T> getEnteringEdgeIterator() {
		return getEdgeIterator();
	}

	@Override
	public <T extends Edge> Collection<T> getEnteringEdgeSet() {
		return getEdgeSet();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterator<T> getLeavingEdgeIterator() {
		return getEdgeIterator();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> Collection<T> getLeavingEdgeSet() {
		return getEdgeSet();
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	public String getGraphName() {
		throw new RuntimeException("impossible with GraphicGraph");
	}

	public String getHost() {
		throw new RuntimeException("impossible with GraphicGraph");
	}

	@Override
	public int getInDegree() {
		return getDegree();
	}

	@Override
	public int getOutDegree() {
		return getDegree();
	}

	// XXX stubs for the new methods
	@Override
	public <T extends Edge> T getEdgeBetween(Node node) {
		List<? extends Edge> edges = graph.connectivity.get(this);

		for (Edge edge : edges) {
			if (edge.getOpposite(this).getId().equals(node.getId())) {
				return (T) edge;
			}
		}

		return null;
	}

	@Override
	public <T extends Edge> T getEdgeFrom(Node node) {
		return getEdgeBetween(node);
	}

	@Override
	public <T extends Edge> T getEdgeToward(Node node) {
		return getEdgeBetween(node);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEnteringEdge(int i) {
		List<GraphicEdge> edges = graph.connectivity.get(this);
		if (i >= edges.size()) {
			return null;
		}
		return (T) edges.get(i);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Edge> T getLeavingEdge(int i) {
		List<GraphicEdge> edges = graph.connectivity.get(this);
		if (i >= edges.size()) {
			return null;
		}
		return (T) edges.get(i);
	}
}
