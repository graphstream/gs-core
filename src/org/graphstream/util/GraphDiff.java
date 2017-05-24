/*
 * Copyright 2006 - 2016
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
package org.graphstream.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.Sink;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSourceDGS;

public class GraphDiff {
	protected static enum ElementType {
		NODE, EDGE, GRAPH
	}

	private Bridge bridge;
	private final LinkedList<Event> events;

	/**
	 * Create a new empty diff.
	 */
	public GraphDiff() {
		this.events = new LinkedList<Event>();
		this.bridge = null;
	}

	/**
	 * Create a diff between two graphs.
	 * 
	 * @param g1
	 * @param g2
	 */
	public GraphDiff(Graph g1, Graph g2) {
		this();

		if (g2.getNodeCount() == 0 && g2.getEdgeCount() == 0
				&& g2.getAttributeCount() == 0
				&& (g1.getNodeCount() > 0 || g1.getEdgeCount() > 0)) {
			events.add(new GraphCleared(g1));
		} else {
			attributeDiff(ElementType.GRAPH, g1, g2);
 
			for (int idx = 0; idx < g1.getEdgeCount(); idx++) {
				Edge e1 = g1.getEdge(idx);
				Edge e2 = g2.getEdge(e1.getId());

				if (e2 == null) {
					attributeDiff(ElementType.EDGE, e1, e2);
					events.add(new EdgeRemoved(e1.getId(),
							e1.getSourceNode().getId(),
							e1.getTargetNode().getId(),
							e1.isDirected()));
				}
			}

			for (int idx = 0; idx < g1.getNodeCount(); idx++) {
				Node n1 = g1.getNode(idx);
				Node n2 = g2.getNode(n1.getId());

				if (n2 == null) {
					attributeDiff(ElementType.NODE, n1, n2);
					events.add(new NodeRemoved(n1.getId()));
				}
			}

			for (int idx = 0; idx < g2.getNodeCount(); idx++) {
				Node n2 = g2.getNode(idx);
				Node n1 = g1.getNode(n2.getId());

				if (n1 == null)
					events.add(new NodeAdded(n2.getId()));

				attributeDiff(ElementType.NODE, n1, n2);
			}

			for (int idx = 0; idx < g2.getEdgeCount(); idx++) {
				Edge e2 = g2.getEdge(idx);
				Edge e1 = g1.getEdge(e2.getId());

				if (e1 == null)
					events.add(new EdgeAdded(e2.getId(), e2.getSourceNode()
							.getId(), e2.getTargetNode().getId(), e2
							.isDirected()));

				attributeDiff(ElementType.EDGE, e1, e2);
			}
		}
	}

	/**
	 * Start to record changes. If a record is already started, then it will be
	 * ended.
	 * 
	 * @param g
	 *            the graph to start listening for changes.
	 */
	public void start(Graph g) {
		if (bridge != null)
			end();

		bridge = new Bridge(g);
	}

	/**
	 * Stop to record changes. If there is no record, calling this method has no
	 * effect.
	 */
	public void end() {
		if (bridge != null) {
			bridge.end();
			bridge = null;
		}
	}

	/**
	 * Clear all recorded changes.
	 */
	public void reset() {
		events.clear();
	}

	/**
	 * Considering this object is a diff between g1 and g2, calling this method
	 * will applied changes on g1 such that g1 will look like g2.
	 * 
	 * @param g1
	 */
	public void apply(Sink g1) {
		String sourceId = String.format("GraphDiff@%x", System.nanoTime());
		apply(sourceId, g1);
	}

	public void apply(String sourceId, Sink g1) {
		for (int i = 0; i < events.size(); i++)
			events.get(i).apply(sourceId, i, g1);
	}

	/**
	 * Considering this object is a diff between g1 and g2, calling this method
	 * will applied changes on g2 such that g2 will look like g1.
	 * 
	 * @param g2
	 */
	public void reverse(Sink g2) {
		String sourceId = String.format("GraphDiff@%x", System.nanoTime());
		reverse(sourceId, g2);
	}

	public void reverse(String sourceId, Sink g2) {
		for (int i = events.size() - 1; i >= 0; i--)
			events.get(i).reverse(sourceId, events.size() + 1 - i, g2);
	}

	private void attributeDiff(ElementType type, Element e1, Element e2) {
		if (e1 == null && e2 == null)
			return;
		else if (e1 == null) {
			e2.attributeKeys().forEach(key ->
				events.add(new AttributeAdded(type, e2.getId(), key, e2.getAttribute(key))));
		} else if (e2 == null) {
			e1.attributeKeys().forEach(key ->
				events.add(new AttributeRemoved(type, e1.getId(), key, e1.getAttribute(key))));
		} else {
			e2.attributeKeys().forEach(key -> {
				if (e1.hasAttribute(key)) {
					Object o1 = e1.getAttribute(key);
					Object o2 = e2.getAttribute(key);

					if (!(o1 == null ? o2 == null : o1.equals(o2)))
						events.add(new AttributeChanged(type, e1.getId(), key,
								o2, o1));
				} else
					events.add(new AttributeAdded(type, e1.getId(), key, e2
							.getAttribute(key)));
			});

			e1.attributeKeys().forEach(key -> {
				if (!e2.hasAttribute(key))
					events.add(new AttributeRemoved(type, e1.getId(), key, e1
							.getAttribute(key)));
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < events.size(); i++)
			buffer.append(events.get(i).toString()).append("\n");

		return buffer.toString();
	}

	protected abstract class Event {
		/**
		 * Apply this event on a given graph.
		 * 
		 * @param g
		 *            the graph on which the action should be applied
		 */
		abstract void apply(String sourceId, long timeId, Sink g);

		/**
		 * Apply the dual event on a given graph.
		 * 
		 * @param g
		 *            the graph on which the dual action should be applied.
		 */
		abstract void reverse(String sourceId, long timeId, Sink g);
	}

	protected class NodeAdded extends Event {
		String nodeId;

		public NodeAdded(String nodeId) {
			this.nodeId = nodeId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#apply(org.graphstream.graph.
		 * Graph)
		 */
		public void apply(String sourceId, long timeId, Sink g) {
			g.nodeAdded(sourceId, timeId, nodeId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#reverse(org.graphstream.graph
		 * .Graph)
		 */
		public void reverse(String sourceId, long timeId, Sink g) {
			g.nodeRemoved(sourceId, timeId, nodeId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format("an \"%s\"", nodeId);
		}
	}

	protected class NodeRemoved extends NodeAdded {
		public NodeRemoved(String nodeId) {
			super(nodeId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.NodeAdded#apply(org.graphstream.graph
		 * .Graph)
		 */
		public void apply(String sourceId, long timeId, Sink g) {
			super.reverse(sourceId, timeId, g);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.NodeAdded#reverse(org.graphstream.
		 * graph.Graph)
		 */
		public void reverse(String sourceId, long timeId, Sink g) {
			super.apply(sourceId, timeId, g);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.util.GraphDiff.NodeAdded#toString()
		 */
		@Override
		public String toString() {
			return String.format("dn \"%s\"", nodeId);
		}
	}

	protected abstract class ElementEvent extends Event {
		ElementType type;
		String elementId;

		protected ElementEvent(ElementType type, String elementId) {
			this.type = type;
			this.elementId = elementId;
		}

		protected Element getElement(Graph g) {
			Element e;

			switch (type) {
			case NODE:
				e = g.getNode(elementId);
				break;
			case EDGE:
				e = g.getEdge(elementId);
				break;
			case GRAPH:
				e = g;
				break;
			default:
				e = null;
			}

			if (e == null)
				throw new ElementNotFoundException();

			return e;
		}

		protected String toStringHeader() {
			String header;

			switch (type) {
			case NODE:
				header = "cn";
				break;
			case EDGE:
				header = "ce";
				break;
			case GRAPH:
				header = "cg";
				break;
			default:
				header = "??";
				break;
			}

			return String.format("%s \"%s\"", header, elementId);
		}

		protected String toStringValue(Object o) {
			if (o == null)
				return "null";
			else if (o instanceof String)
				return "\"" + o.toString() + "\"";
			else if (o instanceof Number)
				return o.toString();
			else
				return o.toString();
		}
	}

	protected class AttributeAdded extends ElementEvent {
		String attrId;
		Object value;

		public AttributeAdded(ElementType type, String elementId,
				String attrId, Object value) {
			super(type, elementId);

			this.attrId = attrId;
			this.value = value;

			if (value != null && value.getClass().isArray()
					&& Array.getLength(value) > 0) {
				Object o = Array.newInstance(Array.get(value, 0).getClass(),
						Array.getLength(value));

				for (int i = 0; i < Array.getLength(value); i++)
					Array.set(o, i, Array.get(value, i));

				this.value = o;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#apply(org.graphstream.graph.
		 * Graph)
		 */
		public void apply(String sourceId, long timeId, Sink g) {
			switch (type) {
			case NODE:
				g.nodeAttributeAdded(sourceId, timeId, elementId, attrId, value);
				break;
			case EDGE:
				g.edgeAttributeAdded(sourceId, timeId, elementId, attrId, value);
				break;
			case GRAPH:
				g.graphAttributeAdded(sourceId, timeId, attrId, value);
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#reverse(org.graphstream.graph
		 * .Graph)
		 */
		public void reverse(String sourceId, long timeId, Sink g) {
			switch (type) {
			case NODE:
				g.nodeAttributeRemoved(sourceId, timeId, elementId, attrId);
				break;
			case EDGE:
				g.edgeAttributeRemoved(sourceId, timeId, elementId, attrId);
				break;
			case GRAPH:
				g.graphAttributeRemoved(sourceId, timeId, attrId);
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format("%s +\"%s\":%s", toStringHeader(), attrId,
					toStringValue(value));
		}
	}

	protected class AttributeChanged extends ElementEvent {
		String attrId;
		Object newValue;
		Object oldValue;

		public AttributeChanged(ElementType type, String elementId,
				String attrId, Object newValue, Object oldValue) {
			super(type, elementId);

			this.attrId = attrId;
			this.newValue = newValue;
			this.oldValue = oldValue;

			if (newValue != null && newValue.getClass().isArray()
					&& Array.getLength(newValue) > 0) {
				Object o = Array.newInstance(Array.get(newValue, 0).getClass(),
						Array.getLength(newValue));

				for (int i = 0; i < Array.getLength(newValue); i++)
					Array.set(o, i, Array.get(newValue, i));

				this.newValue = o;
			}

			if (oldValue != null && oldValue.getClass().isArray()
					&& Array.getLength(oldValue) > 0) {
				Object o = Array.newInstance(Array.get(oldValue, 0).getClass(),
						Array.getLength(oldValue));

				for (int i = 0; i < Array.getLength(oldValue); i++)
					Array.set(o, i, Array.get(oldValue, i));

				this.oldValue = o;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#apply(org.graphstream.graph.
		 * Graph)
		 */
		public void apply(String sourceId, long timeId, Sink g) {
			switch (type) {
			case NODE:
				g.nodeAttributeChanged(sourceId, timeId, elementId, attrId,
						oldValue, newValue);
				break;
			case EDGE:
				g.edgeAttributeChanged(sourceId, timeId, elementId, attrId,
						oldValue, newValue);
				break;
			case GRAPH:
				g.graphAttributeChanged(sourceId, timeId, attrId, oldValue,
						newValue);
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#reverse(org.graphstream.graph
		 * .Graph)
		 */
		public void reverse(String sourceId, long timeId, Sink g) {
			switch (type) {
			case NODE:
				g.nodeAttributeChanged(sourceId, timeId, elementId, attrId,
						newValue, oldValue);
				break;
			case EDGE:
				g.edgeAttributeChanged(sourceId, timeId, elementId, attrId,
						newValue, oldValue);
				break;
			case GRAPH:
				g.graphAttributeChanged(sourceId, timeId, attrId, newValue,
						oldValue);
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format("%s \"%s\":%s", toStringHeader(), attrId,
					toStringValue(newValue));
		}
	}

	protected class AttributeRemoved extends ElementEvent {
		String attrId;
		Object oldValue;

		public AttributeRemoved(ElementType type, String elementId,
				String attrId, Object oldValue) {
			super(type, elementId);

			this.attrId = attrId;
			this.oldValue = oldValue;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#apply(org.graphstream.graph.
		 * Graph)
		 */
		public void apply(String sourceId, long timeId, Sink g) {
			switch (type) {
			case NODE:
				g.nodeAttributeRemoved(sourceId, timeId, elementId, attrId);
				break;
			case EDGE:
				g.edgeAttributeRemoved(sourceId, timeId, elementId, attrId);
				break;
			case GRAPH:
				g.graphAttributeRemoved(sourceId, timeId, attrId);
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#reverse(org.graphstream.graph
		 * .Graph)
		 */
		public void reverse(String sourceId, long timeId, Sink g) {
			switch (type) {
			case NODE:
				g.nodeAttributeAdded(sourceId, timeId, elementId, attrId,
						oldValue);
				break;
			case EDGE:
				g.edgeAttributeAdded(sourceId, timeId, elementId, attrId,
						oldValue);
				break;
			case GRAPH:
				g.graphAttributeAdded(sourceId, timeId, attrId, oldValue);
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format("%s -\"%s\":%s", toStringHeader(), attrId,
					toStringValue(oldValue));
		}
	}

	protected class EdgeAdded extends Event {
		String edgeId;
		String source, target;
		boolean directed;

		public EdgeAdded(String edgeId, String source, String target,
				boolean directed) {
			this.edgeId = edgeId;
			this.source = source;
			this.target = target;
			this.directed = directed;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#apply(org.graphstream.graph.
		 * Graph)
		 */
		public void apply(String sourceId, long timeId, Sink g) {
			g.edgeAdded(sourceId, timeId, edgeId, source, target, directed);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#reverse(org.graphstream.graph
		 * .Graph)
		 */
		public void reverse(String sourceId, long timeId, Sink g) {
			g.edgeRemoved(sourceId, timeId, edgeId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format("ae \"%s\" \"%s\" %s \"%s\"", edgeId, source,
					directed ? ">" : "--", target);
		}
	}

	protected class EdgeRemoved extends EdgeAdded {
		public EdgeRemoved(String edgeId, String source, String target,
				boolean directed) {
			super(edgeId, source, target, directed);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.EdgeAdded#apply(org.graphstream.graph
		 * .Graph)
		 */
		public void apply(String sourceId, long timeId, Sink g) {
			super.reverse(sourceId, timeId, g);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.EdgeAdded#reverse(org.graphstream.
		 * graph.Graph)
		 */
		public void reverse(String sourceId, long timeId, Sink g) {
			super.apply(sourceId, timeId, g);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.util.GraphDiff.EdgeAdded#toString()
		 */
		@Override
		public String toString() {
			return String.format("de \"%s\"", edgeId);
		}
	}

	protected class StepBegins extends Event {
		double newStep, oldStep;

		public StepBegins(double oldStep, double newStep) {
			this.newStep = newStep;
			this.oldStep = oldStep;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#apply(org.graphstream.graph.
		 * Graph)
		 */
		public void apply(String sourceId, long timeId, Sink g) {
			g.stepBegins(sourceId, timeId, newStep);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#reverse(org.graphstream.graph
		 * .Graph)
		 */
		public void reverse(String sourceId, long timeId, Sink g) {
			g.stepBegins(sourceId, timeId, oldStep);
		}

		@Override
		public String toString() {
			return String.format("st %f", newStep);
		}
	}

	protected class GraphCleared extends Event {
		byte[] data;

		public GraphCleared(Graph g) {
			this.data = null;

			try {
				FileSinkDGS sink = new FileSinkDGS();
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				GZIPOutputStream out = new GZIPOutputStream(bytes);

				sink.writeAll(g, out);
				out.flush();
				out.close();

				this.data = bytes.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#apply(org.graphstream.graph.
		 * Graph)
		 */
		public void apply(String sourceId, long timeId, Sink g) {
			g.graphCleared(sourceId, timeId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.util.GraphDiff.Event#reverse(org.graphstream.graph
		 * .Graph)
		 */
		public void reverse(String sourceId, long timeId, Sink g) {
			try {
				ByteArrayInputStream bytes = new ByteArrayInputStream(this.data);
				GZIPInputStream in = new GZIPInputStream(bytes);
				FileSourceDGS dgs = new FileSourceDGS();

				dgs.addSink(g);
				dgs.readAll(in);
				dgs.removeSink(g);

				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "cl";
		}
	}

	private class Bridge implements Sink {
		Graph g;

		Bridge(Graph g) {

			this.g = g;
			g.addSink(this);
		}

		void end() {
			g.removeSink(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang
		 * .String, long, java.lang.String, java.lang.Object)
		 */
		public void graphAttributeAdded(String sourceId, long timeId,
				String attribute, Object value) {
			Event e;
			e = new AttributeAdded(ElementType.GRAPH, null, attribute, value);
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.Object, java.lang.Object)
		 */
		public void graphAttributeChanged(String sourceId, long timeId,
				String attribute, Object oldValue, Object newValue) {
			Event e;
			e = new AttributeChanged(ElementType.GRAPH, null, attribute,
					newValue, g.getAttribute(attribute));
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang
		 * .String, long, java.lang.String)
		 */
		public void graphAttributeRemoved(String sourceId, long timeId,
				String attribute) {
			Event e;
			e = new AttributeRemoved(ElementType.GRAPH, null, attribute,
					g.getAttribute(attribute));
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object)
		 */
		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attribute, Object value) {
			Event e;
			e = new AttributeAdded(ElementType.NODE, nodeId, attribute, value);
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object,
		 * java.lang.Object)
		 */
		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			Event e;
			e = new AttributeChanged(ElementType.NODE, nodeId, attribute,
					newValue, g.getNode(nodeId).getAttribute(attribute));
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang
		 * .String, long, java.lang.String, java.lang.String)
		 */
		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			Event e;
			e = new AttributeRemoved(ElementType.NODE, nodeId, attribute, g
					.getNode(nodeId).getAttribute(attribute));
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object)
		 */
		public void edgeAttributeAdded(String sourceId, long timeId,
				String edgeId, String attribute, Object value) {
			Event e;
			e = new AttributeAdded(ElementType.EDGE, edgeId, attribute, value);
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object,
		 * java.lang.Object)
		 */
		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			Event e;
			e = new AttributeChanged(ElementType.EDGE, edgeId, attribute,
					newValue, g.getEdge(edgeId).getAttribute(attribute));
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang
		 * .String, long, java.lang.String, java.lang.String)
		 */
		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			Event e;
			e = new AttributeRemoved(ElementType.EDGE, edgeId, attribute, g
					.getEdge(edgeId).getAttribute(attribute));
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String,
		 * long, java.lang.String)
		 */
		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			Event e;
			e = new NodeAdded(nodeId);
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String,
		 * long, java.lang.String)
		 */
		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			Node n = g.getNode(nodeId);

			n.attributeKeys().forEach(key -> nodeAttributeRemoved(sourceId, timeId, nodeId, key));

			Event e;
			e = new NodeRemoved(nodeId);
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String,
		 * long, java.lang.String, java.lang.String, java.lang.String, boolean)
		 */
		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			Event e;
			e = new EdgeAdded(edgeId, fromNodeId, toNodeId, directed);
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String,
		 * long, java.lang.String)
		 */
		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			Edge edge = g.getEdge(edgeId);

			edge.attributeKeys().forEach(key ->
				edgeAttributeRemoved(sourceId, timeId, edgeId, key));

			Event e;
			e = new EdgeRemoved(edgeId, edge.getSourceNode().getId(), edge
					.getTargetNode().getId(), edge.isDirected());
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.ElementSink#graphCleared(java.lang.String,
		 * long)
		 */
		public void graphCleared(String sourceId, long timeId) {
			Event e = new GraphCleared(g);
			events.add(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String,
		 * long, double)
		 */
		public void stepBegins(String sourceId, long timeId, double step) {
			Event e = new StepBegins(g.getStep(), step);
			events.add(e);
		}
	}

	public static void main(String... args) throws Exception {
		Graph g1 = new AdjacencyListGraph("g1");
		Graph g2 = new AdjacencyListGraph("g2");

		Node a1 = g1.addNode("A");
		a1.setAttribute("attr1", "test");
		a1.setAttribute("attr2", 10.0);
		a1.setAttribute("attr3", 12);

		Node a2 = g2.addNode("A");
		a2.setAttribute("attr1", "test1");
		a2.setAttribute("attr2", 10.0);
		g2.addNode("B");
		g2.addNode("C");

		GraphDiff diff = new GraphDiff(g2, g1);
		System.out.println(diff);
	}
}
