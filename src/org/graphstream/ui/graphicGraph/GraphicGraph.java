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
package org.graphstream.ui.graphicGraph;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.AbstractElement;
import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.Sink;
import org.graphstream.stream.SourceBase.ElementType;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.util.GraphListeners;

/**
 * Graph representation used in display classes.
 * 
 * <p>
 * Warning: This class is NOT a general graph class, and it should NOT be used
 * as it. This class is particularly dedicated to fast drawing of the graph and
 * is internally arranged to be fast for this task only. It implements graph
 * solely to be easily susceptible to be used as a sink and source for graph
 * events. Some of the common methods of the Graph interface are not functional
 * and will throw an exception if used (as documented in their respective
 * JavaDoc).
 * </p>
 * 
 * <p>
 * The purpose of the graphic graph is to represent a graph with some often used
 * graphic attributes (like position, label, etc.) stored as fields in the nodes
 * and edges and most of the style stored in styles pertaining to a style sheet
 * that tries to imitate the way CSS works. For example, the GraphicNode class
 * defines a label, a position (x,y,z) and a style that is taken from the style
 * sheet.
 * </p>
 * 
 * <p>
 * The style sheet is uploaded on the graph using an attribute correspondingly
 * named "ui.stylesheet" or "ui.stylesheet" (the second one is better). It can
 * be a string that contains the whole style sheet, or an URL of the form :
 * </p>
 * 
 * <pre>
 * url(name)
 * </pre>
 * 
 * <p>
 * The graphic graph does not completely duplicate a graph, it only store things
 * that are useful for drawing it. Although it implements "Graph", some methods
 * are not implemented and will throw a runtime exception. These methods are
 * mostly utility methods like write(), read(), and naturally display().
 * </p>
 * 
 * <p>
 * The graphic graph has the ability to store attributes like any other graph
 * element, however the attributes stored by the graphic graph are restricted.
 * There is a filter on the attribute adding methods that let pass only:
 * <ul>
 * <li>All attributes starting with "ui.".</li>
 * <li>The "x", "y", "z", "xy" and "xyz" attributes.</li>
 * <li>The "stylesheet" attribute (although "ui.stylesheet" is preferred).</li>
 * <li>The "label" attribute.</li>
 * </ul>
 * All other attributes are filtered and not stored. The result is that if the
 * graphic graph is used as an input (a source of graph events) some attributes
 * will not pass through the filter.
 * </p>
 * 
 * <p>
 * The implementation of this graph relies on the StyleGroupSet class and this
 * is indeed its way to store its elements (grouped by style and Z level).
 * </p>
 * 
 * <p>
 * In addition to this, it provides, as all graphs do, the relational
 * information for edges.
 * </p>
 * 
 * TODO : this graph cannot handle modification inside event listener methods !!
 */
public class GraphicGraph extends AbstractElement implements Graph,
		StyleGroupListener {
	/**
	 * Set of styles.
	 */
	protected StyleSheet styleSheet;

	/**
	 * Associate graphic elements with styles.
	 */
	protected StyleGroupSet styleGroups;

	/**
	 * Connectivity. The way nodes are connected one with another via edges. The
	 * map is sorted by node. For each node an array of edges lists the
	 * connectivity.
	 */
	protected HashMap<GraphicNode, ArrayList<GraphicEdge>> connectivity;

	/**
	 * The style of this graph. This is a shortcut to avoid searching it in the
	 * style sheet.
	 */
	public StyleGroup style;

	/**
	 * Memorize the step events.
	 */
	public double step = 0;

	/**
	 * Set to true each time the graph was modified internally and a redraw is
	 * needed.
	 */
	public boolean graphChanged;

	/**
	 * Set to true each time a sprite or node moved.
	 */
	protected boolean boundsChanged = true;

	/**
	 * Maximum position of a node or sprite in the graphic graph. Computed by
	 * {@link #computeBounds()}.
	 */
	protected Point3 hi = new Point3();

	/**
	 * Minimum position of a node or sprite in the graphic graph. Computed by
	 * {@link #computeBounds()}.
	 */
	protected Point3 lo = new Point3();

	/**
	 * Set of listeners of this graph.
	 */
	protected GraphListeners listeners;

	/**
	 * Time of other known sources.
	 */
	// protected SinkTime sinkTime = new SinkTime();

	/**
	 * Are null attributes access an error ?
	 */
	protected boolean nullAttrError = false;

	/**
	 * Report back the XYZ events on nodes and sprites? If enabled, each change
	 * in the position of nodes and sprites will be sent to potential listeners
	 * of the graph. By default this is disabled as long there are no listeners.
	 */
	protected boolean feedbackXYZ = true;

	/**
	 * New empty graphic graph.
	 * 
	 * A default style sheet is created, it then can be "cascaded" with other
	 * style sheets.
	 */
	public GraphicGraph(String id) {
		super(id);

		listeners = new GraphListeners(this);
		styleSheet = new StyleSheet();
		styleGroups = new StyleGroupSet(styleSheet);
		connectivity = new HashMap<GraphicNode, ArrayList<GraphicEdge>>();

		styleGroups.addListener(this);
		styleGroups.addElement(this); // Add style to this graph.

		style = styleGroups.getStyleFor(this);
	}

	// Access

	/**
	 * True if the graph was edited or changed in any way since the last reset
	 * of the "changed" flag.
	 * 
	 * @return true if the graph was changed.
	 */
	public boolean graphChangedFlag() {
		return graphChanged;
	}

	/**
	 * Reset the "changed" flag.
	 * 
	 * @see #graphChangedFlag()
	 */
	public void resetGraphChangedFlag() {
		graphChanged = false;
	}

	/**
	 * The style sheet. This style sheet is the result of the "cascade" or
	 * accumulation of styles added via attributes of the graph.
	 * 
	 * @return A style sheet.
	 */
	public StyleSheet getStyleSheet() {
		return styleSheet;
	}

	/**
	 * The graph style group.
	 * 
	 * @return A style group.
	 */
	public StyleGroup getStyle() {
		return style;
	}

	/**
	 * The complete set of style groups.
	 * 
	 * @return The style groups.
	 */
	public StyleGroupSet getStyleGroups() {
		return styleGroups;
	}

	@Override
	public String toString() {
		return String.format("[%s %d nodes %d edges]", getId(), getNodeCount(),
				getEdgeCount());
	}

	public double getStep() {
		return step;
	}

	/**
	 * The maximum position of a node or sprite. Notice that this is updated
	 * only each time the {@link #computeBounds()} method is called.
	 * 
	 * @return The maximum node or sprite position.
	 */
	public Point3 getMaxPos() {
		return hi;
	}

	/**
	 * The minimum position of a node or sprite. Notice that this is updated
	 * only each time the {@link #computeBounds()} method is called.
	 * 
	 * @return The minimum node or sprite position.
	 */
	public Point3 getMinPos() {
		return lo;
	}

	/**
	 * Does the graphic graph publish via attribute changes the XYZ changes on
	 * nodes and sprites when changed ?. This is disabled by default, and
	 * enabled as soon as there is at least one listener.
	 */
	public boolean feedbackXYZ() {
		return feedbackXYZ;
	}

	// Command

	/**
	 * Should the graphic graph publish via attribute changes the XYZ changes on
	 * nodes and sprites when changed ?.
	 */
	public void feedbackXYZ(boolean on) {
		feedbackXYZ = on;
	}

	/**
	 * Compute the overall bounds of the graphic graph according to the nodes
	 * and sprites positions. We can only compute the graph bounds from the
	 * nodes and sprites centres since the node and graph bounds may in certain
	 * circumstances be computed according to the graph bounds. The bounds are
	 * stored in the graph metrics.
	 * 
	 * This operation will process each node and sprite and is therefore costly.
	 * However it does this computation again only when a node or sprite moved.
	 * Therefore it can be called several times, if nothing moved in the graph,
	 * the computation will not be redone.
	 * 
	 * @see #getMaxPos()
	 * @see #getMinPos()
	 */
	public void computeBounds() {
		if (boundsChanged) {
			lo.x = lo.y = lo.z = Double.POSITIVE_INFINITY;// 1000000000; // A
															// bug with
															// Double.MAX_VALUE
															// during
			// comparisons ?
			hi.x = hi.y = hi.z = Double.NEGATIVE_INFINITY;// -1000000000; // A
															// bug with
															// Double.MIN_VALUE
															// during
			// comparisons ?

			for (Node n : getEachNode()) {
				GraphicNode node = (GraphicNode) n;

				if (!node.hidden && node.positionned) {
					if (node.x < lo.x)
						lo.x = node.x;
					if (node.x > hi.x)
						hi.x = node.x;
					if (node.y < lo.y)
						lo.y = node.y;
					if (node.y > hi.y)
						hi.y = node.y;
					if (node.z < lo.z)
						lo.z = node.z;
					if (node.z > hi.z)
						hi.z = node.z;
				}
			}

			for (GraphicSprite sprite : spriteSet()) {
				if (!sprite.isAttached()
						&& sprite.getUnits() == StyleConstants.Units.GU) {
					double x = sprite.getX();
					double y = sprite.getY();
					double z = sprite.getZ();

					if (!sprite.hidden) {
						if (x < lo.x)
							lo.x = x;
						if (x > hi.x)
							hi.x = x;
						if (y < lo.y)
							lo.y = y;
						if (y > hi.y)
							hi.y = y;
						if (z < lo.z)
							lo.z = z;
						if (z > hi.z)
							hi.z = z;
					}
				}
			}

			if ((hi.x - lo.x < 0.000001)) {
				hi.x = 1;
				lo.x = -1;
			}
			if ((hi.y - lo.y < 0.000001)) {
				hi.y = 1;
				lo.y = -1;
			}
			if ((hi.z - lo.z < 0.000001)) {
				hi.z = 1;
				lo.z = -1;
			}

			boundsChanged = false;
		}
	}

	protected void moveNode(String id, double x, double y, double z) {
		GraphicNode node = (GraphicNode) styleGroups.getNode(id);

		if (node != null) {
			node.x = x;
			node.y = y;
			node.z = z;
			node.addAttribute("x", x);
			node.addAttribute("y", y);
			node.addAttribute("z", z);

			graphChanged = true;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getNode(String id) {
		return (T) styleGroups.getNode(id);
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> T getEdge(String id) {
		return (T) styleGroups.getEdge(id);
	}

	public GraphicSprite getSprite(String id) {
		return styleGroups.getSprite(id);
	}

	@Override
	protected void attributeChanged(AttributeChangeEvent event,
			String attribute, Object oldValue, Object newValue) {

		// One of the most important method. Most of the communication comes
		// from attributes.

		if (attribute.equals("ui.repaint")) {
			graphChanged = true;
		} else if (attribute.equals("ui.stylesheet")
				|| attribute.equals("stylesheet")) {
			if (event == AttributeChangeEvent.ADD
					|| event == AttributeChangeEvent.CHANGE) {
				if (newValue instanceof String) {
					try {
						styleSheet.load((String) newValue);
						graphChanged = true;
					} catch (IOException e) {
						System.err
								.printf("Error while parsing style sheet for graph '%s' : %n",
										getId());
						if (((String) newValue).startsWith("url"))
							System.err.printf("    %s%n", ((String) newValue));
						System.err.printf("    %s%n", e.getMessage());
					}
				} else {
					System.err
							.printf("Error with stylesheet specification what to do with '%s' ?%n",
									newValue);
				}
			} else // Remove the style.
			{
				styleSheet.clear();
				graphChanged = true;
			}
		} else if (attribute.startsWith("ui.sprite.")) {
			// Defers the sprite handling to the sprite API.
			spriteAttribute(event, null, attribute, newValue);
			graphChanged = true;
		}

		listeners.sendAttributeChangedEvent(getId(), ElementType.GRAPH,
				attribute, event, oldValue, newValue);
	}

	/**
	 * Display the node/edge relations.
	 */
	public void printConnectivity() {
		Iterator<GraphicNode> keys = connectivity.keySet().iterator();

		System.err.printf("Graphic graph connectivity:%n");

		while (keys.hasNext()) {
			GraphicNode node = keys.next();
			System.err.printf("    [%s] -> ", node.getId());
			ArrayList<GraphicEdge> edges = connectivity.get(node);
			for (GraphicEdge edge : edges)
				System.err.printf(" (%s %d)", edge.getId(),
						edge.getMultiIndex());
			System.err.printf("%n");
		}
	}

	// Style group listener interface

	public void elementStyleChanged(Element element, StyleGroup oldStyle,
			StyleGroup style) {
		if (element instanceof GraphicElement) {
			GraphicElement ge = (GraphicElement) element;
			ge.style = style;
			graphChanged = true;
		} else if (element instanceof GraphicGraph) {
			GraphicGraph gg = (GraphicGraph) element;
			gg.style = style;
			graphChanged = true;
		} else {
			throw new RuntimeException("WTF ?");
		}
	}

	public void styleChanged(StyleGroup style) {

	}

	// Graph interface

	public Iterable<? extends Edge> getEachEdge() {
		return styleGroups.edges();
	}

	public Iterable<? extends Node> getEachNode() {
		return styleGroups.nodes();
	}

	@SuppressWarnings("all")
	public <T extends Node> Collection<T> getNodeSet() {
		return new AbstractCollection<T>() {
			public Iterator<T> iterator() {
				return getNodeIterator();
			}

			public int size() {
				return getNodeCount();
			}
		};
	}

	@SuppressWarnings("all")
	public <T extends Edge> Collection<T> getEdgeSet() {
		return new AbstractCollection<T>() {
			public Iterator<T> iterator() {
				return getEdgeIterator();
			}

			public int size() {
				return getEdgeCount();
			}
		};
	}

	@SuppressWarnings("unchecked")
	public Iterator<Node> iterator() {
		return (Iterator<Node>) styleGroups.getNodeIterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#addSink(org.graphstream.stream.Sink)
	 */
	public void addSink(Sink listener) {
		listeners.addSink(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.Source#removeSink(org.graphstream.stream.Sink)
	 */
	public void removeSink(Sink listener) {
		listeners.removeSink(listener);
	}

	/*
	 * *(non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.Source#addAttributeSink(org.graphstream.stream
	 * .AttributeSink)
	 */
	public void addAttributeSink(AttributeSink listener) {
		listeners.addAttributeSink(listener);
	}

	/*
	 * *(non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.Source#removeAttributeSink(org.graphstream.stream
	 * .AttributeSink)
	 */
	public void removeAttributeSink(AttributeSink listener) {
		listeners.removeAttributeSink(listener);
	}

	/*
	 * *(non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#addElementSink(org.graphstream.stream.
	 * ElementSink)
	 */
	public void addElementSink(ElementSink listener) {
		listeners.addElementSink(listener);
	}

	/*
	 * *(non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.Source#removeElementSink(org.graphstream.stream
	 * .ElementSink)
	 */
	public void removeElementSink(ElementSink listener) {
		listeners.removeElementSink(listener);
	}

	/*
	 * *(non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#attributeSinks()
	 */
	public Iterable<AttributeSink> attributeSinks() {
		return listeners.attributeSinks();
	}

	/*
	 * *(non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#elementSinks()
	 */
	public Iterable<ElementSink> elementSinks() {
		return listeners.elementSinks();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> T addEdge(String id, String from, String to)
			throws IdAlreadyInUseException, ElementNotFoundException {
		return (T) addEdge(id, from, to, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String,
	 * java.lang.String, java.lang.String, boolean)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> T addEdge(String id, String from, String to,
			boolean directed) throws IdAlreadyInUseException,
			ElementNotFoundException {
		GraphicEdge edge = (GraphicEdge) styleGroups.getEdge(id);

		if (edge == null) {
			GraphicNode n1 = (GraphicNode) styleGroups.getNode(from);
			GraphicNode n2 = (GraphicNode) styleGroups.getNode(to);

			if (n1 == null)
				throw new ElementNotFoundException("node \"%s\"", from);

			if (n2 == null)
				throw new ElementNotFoundException("node \"%s\"", to);

			edge = new GraphicEdge(id, n1, n2, directed, attributes);

			styleGroups.addElement(edge);

			ArrayList<GraphicEdge> l1 = connectivity.get(n1);
			ArrayList<GraphicEdge> l2 = connectivity.get(n2);

			if (l1 == null) {
				l1 = new ArrayList<GraphicEdge>();
				connectivity.put(n1, l1);
			}

			if (l2 == null) {
				l2 = new ArrayList<GraphicEdge>();
				connectivity.put(n2, l2);
			}

			l1.add(edge);
			l2.add(edge);
			edge.countSameEdges(l1);

			graphChanged = true;

			listeners.sendEdgeAdded(id, from, to, directed);
		}

		return (T) edge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#addNode(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> T addNode(String id) throws IdAlreadyInUseException {
		GraphicNode node = (GraphicNode) styleGroups.getNode(id);

		if (node == null) {
			node = new GraphicNode(this, id, attributes);

			styleGroups.addElement(node);

			graphChanged = true;

			listeners.sendNodeAdded(id);
		}

		return (T) node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#clear()
	 */
	public void clear() {
		listeners.sendGraphCleared();

		clearAttributesWithNoEvent();

		connectivity.clear();
		styleGroups.clear();
		styleSheet.clear();

		step = 0;
		graphChanged = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeEdge(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> T removeEdge(String id)
			throws ElementNotFoundException {
		GraphicEdge edge = (GraphicEdge) styleGroups.getEdge(id);

		if (edge != null) {
			listeners.sendEdgeRemoved(id);

			if (connectivity.get(edge.from) != null)
				connectivity.get(edge.from).remove(edge);
			if (connectivity.get(edge.to) != null)
				connectivity.get(edge.to).remove(edge);

			styleGroups.removeElement(edge);
			edge.removed();

			graphChanged = true;
		}

		return (T) edge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeEdge(java.lang.String,
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> T removeEdge(String from, String to)
			throws ElementNotFoundException {
		GraphicNode node0 = (GraphicNode) styleGroups.getNode(from);
		GraphicNode node1 = (GraphicNode) styleGroups.getNode(to);

		if (node0 != null && node1 != null) {
			ArrayList<GraphicEdge> edges0 = connectivity.get(node0);
			ArrayList<GraphicEdge> edges1 = connectivity.get(node1);

			for (GraphicEdge edge0 : edges0) {
				for (GraphicEdge edge1 : edges1) {
					if (edge0 == edge1) {
						removeEdge(edge0.getId());
						return (T) edge0;
					}
				}
			}
		}

		return null;
	}

	/*
	 * *(non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeNode(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> T removeNode(String id)
			throws ElementNotFoundException {
		GraphicNode node = (GraphicNode) styleGroups.getNode(id);

		if (node != null) {
			listeners.sendNodeRemoved(id);

			if (connectivity.get(node) != null) {
				// We must do a copy of the connectivity set for the node
				// since we will be modifying the connectivity as we process
				// edges.
				ArrayList<GraphicEdge> l = new ArrayList<GraphicEdge>(
						connectivity.get(node));

				for (GraphicEdge edge : l)
					removeEdge(edge.getId());

				connectivity.remove(node);
			}

			styleGroups.removeElement(node);
			node.removed();

			graphChanged = true;
		}

		return (T) node;
	}

	public org.graphstream.ui.swingViewer.Viewer display() {
		throw new RuntimeException(
				"GraphicGraph is used by display() and cannot recursively define display()");
	}

	public org.graphstream.ui.swingViewer.Viewer display(boolean autoLayout) {
		throw new RuntimeException(
				"GraphicGraph is used by display() and cannot recursively define display()");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#stepBegins(double)
	 */
	public void stepBegins(double step) {
		listeners.sendStepBegins(step);
		this.step = step;
	}

	public EdgeFactory<? extends Edge> edgeFactory() {
		throw new RuntimeException("GraphicGraph does not support EdgeFactory");
	}

	public int getEdgeCount() {
		return styleGroups.getEdgeCount();
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return (Iterator<T>) styleGroups.getEdgeIterator();
	}

	public int getNodeCount() {
		return styleGroups.getNodeCount();
	}

	public int getSpriteCount() {
		return styleGroups.getSpriteCount();
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> Iterator<T> getNodeIterator() {
		return (Iterator<T>) styleGroups.getNodeIterator();
	}

	public Iterator<? extends GraphicSprite> getSpriteIterator() {
		return styleGroups.getSpriteIterator();
	}

	public Iterable<? extends GraphicSprite> spriteSet() {
		return styleGroups.sprites();
	}

	public boolean isAutoCreationEnabled() {
		return false;
	}

	public NodeFactory<? extends Node> nodeFactory() {
		throw new RuntimeException("GraphicGraph does not support NodeFactory");
	}

	public void setAutoCreate(boolean on) {
		throw new RuntimeException(
				"GraphicGraph does not support auto-creation");
	}

	public boolean isStrict() {
		return false;
	}

	public void setStrict(boolean on) {
		throw new RuntimeException(
				"GraphicGraph does not support strict checking");
	}

	@Override
	public boolean nullAttributesAreErrors() {
		return nullAttrError;
	}

	public void setNullAttributesAreErrors(boolean on) {
		nullAttrError = on;
	}

	public void setEdgeFactory(EdgeFactory<? extends Edge> ef) {
		throw new RuntimeException(
				"you cannot change the edge factory for graphic graphs !");
	}

	public void setNodeFactory(NodeFactory<? extends Node> nf) {
		throw new RuntimeException(
				"you cannot change the node factory for graphic graphs !");
	}

	public void read(String filename) throws IOException {
		throw new RuntimeException("GraphicGraph does not support I/O");
	}

	public void read(FileSource input, String filename) throws IOException {
		throw new RuntimeException("GraphicGraph does not support I/O");
	}

	public void write(FileSink output, String filename) throws IOException {
		throw new RuntimeException("GraphicGraph does not support I/O");
	}

	public void write(String filename) throws IOException {
		throw new RuntimeException("GraphicGraph does not support I/O");
	}

	// Output interface

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		listeners
				.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		listeners.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		listeners.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang.String
	 * , long, java.lang.String, java.lang.Object)
	 */
	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		listeners.graphAttributeAdded(sourceId, timeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang.
	 * String, long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		listeners.graphAttributeChanged(sourceId, timeId, attribute, oldValue,
				newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang.
	 * String, long, java.lang.String)
	 */
	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		listeners.graphAttributeRemoved(sourceId, timeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		listeners
				.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		listeners.nodeAttributeChanged(sourceId, timeId, nodeId, attribute,
				oldValue, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		listeners.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		listeners.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId,
				directed);
	}

	/*
	 * *(non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		listeners.edgeRemoved(sourceId, timeId, edgeId);
	}

	/*
	 * *(non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String,
	 * long)
	 */
	public void graphCleared(String sourceId, long timeId) {
		listeners.graphCleared(sourceId, timeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		listeners.nodeAdded(sourceId, timeId, nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		listeners.nodeRemoved(sourceId, timeId, nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String,
	 * long, double)
	 */
	public void stepBegins(String sourceId, long timeId, double time) {
		listeners.sendStepBegins(sourceId, timeId, time);
		stepBegins(time);
	}

	// Sprite interface

	protected void spriteAttribute(AttributeChangeEvent event, Element element,
			String attribute, Object value) {

		String spriteId = attribute.substring(10); // Remove the "ui.sprite."
													// prefix.
		int pos = spriteId.indexOf('.'); // Look if there is something after the
											// sprite id.
		String attr = null;

		if (pos > 0) {
			attr = spriteId.substring(pos + 1); // Cut the sprite id.
			spriteId = spriteId.substring(0, pos); // Cut the sprite attribute
													// name.
		}

		if (attr == null) {
			addOrChangeSprite(event, element, spriteId, value);
		} else {
			if (event == AttributeChangeEvent.ADD) {
				GraphicSprite sprite = styleGroups.getSprite(spriteId);

				// We add the sprite, in case of a replay, some attributes of
				// the sprite can be
				// changed before the sprite is declared.
				if (sprite == null) {
					addOrChangeSprite(AttributeChangeEvent.ADD, element,
							spriteId, null);
					sprite = styleGroups.getSprite(spriteId);
				}

				sprite.addAttribute(attr, value);
			} else if (event == AttributeChangeEvent.CHANGE) {
				GraphicSprite sprite = styleGroups.getSprite(spriteId);

				if (sprite == null) {
					addOrChangeSprite(AttributeChangeEvent.ADD, element,
							spriteId, null);
					sprite = styleGroups.getSprite(spriteId);
				}

				sprite.changeAttribute(attr, value);
			} else if (event == AttributeChangeEvent.REMOVE) {
				GraphicSprite sprite = styleGroups.getSprite(spriteId);

				if (sprite != null)
					sprite.removeAttribute(attr);
			}
		}
	}

	protected void addOrChangeSprite(AttributeChangeEvent event,
			Element element, String spriteId, Object value) {

		if (event == AttributeChangeEvent.ADD
				|| event == AttributeChangeEvent.CHANGE) {
			GraphicSprite sprite = styleGroups.getSprite(spriteId);

			if (sprite == null)
				sprite = addSprite_(spriteId);

			if (element != null) {
				if (element instanceof GraphicNode)
					sprite.attachToNode((GraphicNode) element);
				else if (element instanceof GraphicEdge)
					sprite.attachToEdge((GraphicEdge) element);
			}

			if (value != null && (!(value instanceof Boolean)))
				positionSprite(sprite, value);
		} else if (event == AttributeChangeEvent.REMOVE) {
			if (element == null) {
				if (styleGroups.getSprite(spriteId) != null) {
					removeSprite_(spriteId);
				}
			} else {
				GraphicSprite sprite = styleGroups.getSprite(spriteId);

				if (sprite != null)
					sprite.detach();
			}
		}
	}

	public GraphicSprite addSprite(String id) {
		String prefix = String.format("ui.sprite.%s", id);
		System.out.printf("add sprite %s\n", id);
		addAttribute(prefix, 0, 0, 0);
		GraphicSprite s = styleGroups.getSprite(id);
		assert (s != null);
		return s;
	}

	protected GraphicSprite addSprite_(String id) {
		GraphicSprite s = new GraphicSprite(id, this);
		styleGroups.addElement(s);
		graphChanged = true;

		return s;
	}

	public void removeSprite(String id) {
		String prefix = String.format("ui.sprite.%s", id);
		removeAttribute(prefix);
	}

	protected GraphicSprite removeSprite_(String id) {
		GraphicSprite sprite = (GraphicSprite) styleGroups.getSprite(id);

		if (sprite != null) {
			sprite.detach();
			styleGroups.removeElement(sprite);
			sprite.removed();

			graphChanged = true;
		}

		return sprite;
	}

	protected void positionSprite(GraphicSprite sprite, Object value) {
		if (value instanceof Object[]) {
			Object[] values = (Object[]) value;

			if (values.length == 4) {
				if (values[0] instanceof Number && values[1] instanceof Number
						&& values[2] instanceof Number
						&& values[3] instanceof Style.Units) {
					sprite.setPosition(((Number) values[0]).doubleValue(),
							((Number) values[1]).doubleValue(),
							((Number) values[2]).doubleValue(),
							(Style.Units) values[3]);
				} else {
					System.err
							.printf("GraphicGraph : cannot parse values[4] for sprite position.%n");
				}
			} else if (values.length == 3) {
				if (values[0] instanceof Number && values[1] instanceof Number
						&& values[2] instanceof Number) {
					sprite.setPosition(((Number) values[0]).doubleValue(),
							((Number) values[1]).doubleValue(),
							((Number) values[2]).doubleValue(), Units.GU);
				} else {
					System.err
							.printf("GraphicGraph : cannot parse values[3] for sprite position.%n");
				}
			} else if (values.length == 1) {
				if (values[0] instanceof Number) {
					sprite.setPosition(((Number) values[0]).doubleValue());
				} else {
					System.err
							.printf("GraphicGraph : sprite position percent is not a number.%n");
				}
			} else {
				System.err
						.printf("GraphicGraph : cannot transform value '%s' (length=%d) into a position%n",
								Arrays.toString(values), values.length);
			}
		} else if (value instanceof Number) {
			sprite.setPosition(((Number) value).doubleValue());
		} else if (value instanceof Value) {
			sprite.setPosition(((Value) value).value);
		} else if (value instanceof Values) {
			sprite.setPosition((Values) value);
		} else if (value == null) {
			throw new RuntimeException("What do you expect with a null value ?");
		} else {
			System.err
					.printf("GraphicGraph : cannot place sprite with posiiton '%s' (instance of %s)%n",
							value, value.getClass().getName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#clearAttributeSinks()
	 */
	public void clearAttributeSinks() {
		listeners.clearAttributeSinks();
	}

	/*
	 * *(non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#clearElementSinks()
	 */
	public void clearElementSinks() {
		listeners.clearElementSinks();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#clearSinks()
	 */
	public void clearSinks() {
		listeners.clearSinks();
	}

	// stubs for the new methods

	public <T extends Edge> T addEdge(String id, int index1, int index2) {
		throw new RuntimeException("not implemented !");
	}

	public <T extends Edge> T addEdge(String id, int fromIndex, int toIndex,
			boolean directed) {
		throw new RuntimeException("not implemented !");
	}

	public <T extends Edge> T addEdge(String id, Node node1, Node node2) {
		throw new RuntimeException("not implemented !");
	}

	public <T extends Edge> T addEdge(String id, Node from, Node to,
			boolean directed) {
		throw new RuntimeException("not implemented !");
	}

	public <T extends Edge> T getEdge(int index)
			throws IndexOutOfBoundsException {
		throw new RuntimeException("not implemented !");
	}

	public <T extends Node> T getNode(int index)
			throws IndexOutOfBoundsException {
		throw new RuntimeException("not implemented !");
	}

	public <T extends Edge> T removeEdge(int index) {
		throw new RuntimeException("not implemented !");
	}

	public <T extends Edge> T removeEdge(int fromIndex, int toIndex) {
		throw new RuntimeException("not implemented !");
	}

	public <T extends Edge> T removeEdge(Node node1, Node node2) {
		throw new RuntimeException("not implemented !");
	}

	public <T extends Edge> T removeEdge(Edge edge) {
		throw new RuntimeException("not implemented !");
	}

	public <T extends Node> T removeNode(int index) {
		throw new RuntimeException("not implemented !");
	}

	public <T extends Node> T removeNode(Node node) {
		throw new RuntimeException("not implemented !");
	}

	/**
	 * Replay all the elements of the graph and all attributes as new events to
	 * all connected sinks.
	 * 
	 * Be very careful with this method, it introduces new events in the event
	 * stream and some sinks may therefore receive them twice !! Graph replay is
	 * always dangerous !
	 */
	public void replay() {
		// Replay all graph attributes.

		if (getAttributeKeySet() != null)
			for (String key : getAttributeKeySet()) {
				listeners.sendGraphAttributeAdded(id, key, getAttribute(key));
			}

		// Replay all nodes and their attributes.

		for (Node node : this) {
			listeners.sendNodeAdded(id, node.getId());

			if (node.getAttributeKeySet() != null) {
				for (String key : node.getAttributeKeySet()) {
					listeners.sendNodeAttributeAdded(id, node.getId(), key,
							node.getAttribute(key));
				}
			}
		}

		// Replay all edges and their attributes.

		for (Edge edge : getEachEdge()) {
			listeners.sendEdgeAdded(id, edge.getId(), edge.getSourceNode()
					.getId(), edge.getTargetNode().getId(), edge.isDirected());

			if (edge.getAttributeKeySet() != null) {
				for (String key : edge.getAttributeKeySet()) {
					listeners.sendEdgeAttributeAdded(id, edge.getId(), key,
							edge.getAttribute(key));
				}
			}
		}
	}
}