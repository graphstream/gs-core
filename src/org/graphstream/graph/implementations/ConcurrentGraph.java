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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.graph.implementations;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;
import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.Sink;
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.SourceBase;
import org.graphstream.stream.SourceBase.ElementType;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkFactory;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.stream.sync.SinkTime;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.swingViewer.GraphRenderer;
import org.graphstream.ui.swingViewer.Viewer;

/**
 * <p>
 * An implementation of a Graph with multi-thread capabilities.
 * </p>
 * <p>
 * It is similar to the
 * {@link org.graphstream.graph.implementations.AdjacencyListGraph} class, but
 * with thread-safe data structures.
 * </p>
 * <p>
 * Time and memory complexity is comparable to the values given in
 * {@link org.graphstream.graph.implementations.AdjacencyListGraph}. Consider
 * some time overhead due to the thread synchronization machinery.
 * </p>
 * 
 * @see org.graphstream.graph.implementations.AdjacencyListGraph
 */

public class ConcurrentGraph extends AbstractConcurrentElement implements Graph {
	public class EdgeIterator<T extends Edge> implements Iterator<T> {
		Iterator<T> edgeIterator;

		@SuppressWarnings("unchecked")
		public EdgeIterator() {
			edgeIterator = (Iterator<T>) edges.values().iterator();
		}

		public boolean hasNext() {
			return edgeIterator.hasNext();
		}

		public T next() {
			return edgeIterator.next();
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"this iterator does not allow removing");
		}
	}

	public class NodeIterator<T extends Node> implements Iterator<T> {
		Iterator<T> nodeIterator;

		@SuppressWarnings("unchecked")
		public NodeIterator() {
			nodeIterator = (Iterator<T>) nodes.values().iterator();
		}

		public boolean hasNext() {
			return (nodeIterator.hasNext());
		}

		public T next() {
			return nodeIterator.next();
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"this iterator does not allow removing");
		}
	}

	/**
	 * All the nodes.
	 */
	protected ConcurrentHashMap<String, ? extends Node> nodes = new ConcurrentHashMap<String, Node>();

	/**
	 * All the edges.
	 */
	protected ConcurrentHashMap<String, ? extends Edge> edges = new ConcurrentHashMap<String, Edge>();

	/**
	 * Verify name space conflicts, removal of non-existing elements, use of
	 * non-existing elements.
	 */
	protected boolean strictChecking = true;

	/**
	 * Automatically create missing elements. For example, if an edge is created
	 * between two non-existing nodes, create the nodes.
	 */
	protected boolean autoCreate = false;

	/**
	 * Help full class that dynamically instantiate nodes according to a given
	 * class name.
	 */
	protected NodeFactory<? extends ConcurrentNode> nodeFactory;

	/**
	 * Help full class that dynamically instantiate edges according to a given
	 * class name.
	 */
	protected EdgeFactory<? extends ConcurrentEdge> edgeFactory;

	/**
	 * The current step.
	 */
	protected double step;

	/**
	 * The set of listeners.
	 */
	protected GraphListeners listeners;

	// Constructors

	/**
	 * New empty graph, with a default string as an identifier.
	 * 
	 * @see #ConcurrentGraph(String)
	 * @see #ConcurrentGraph(boolean, boolean)
	 * @see #ConcurrentGraph(String, boolean, boolean)
	 */
	@Deprecated
	public ConcurrentGraph() {
		this("ConcurentGraph");
	}

	/**
	 * New empty graph.
	 * 
	 * @param id
	 *            Unique identifier of the graph.
	 * @see #ConcurrentGraph(boolean, boolean)
	 * @see #ConcurrentGraph(String, boolean, boolean)
	 */
	public ConcurrentGraph(String id) {
		this(id, true, false);
	}

	/**
	 * New empty graph, with a default string as an identifier.
	 * 
	 * @param strictChecking
	 *            If true any non-fatal error throws an exception.
	 * @param autoCreate
	 *            If true (and strict checking is false), nodes are
	 *            automatically created when referenced when creating a edge,
	 *            even if not yet inserted in the graph.
	 * @see #ConcurrentGraph(String, boolean, boolean)
	 * @see #setStrict(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	@Deprecated
	public ConcurrentGraph(boolean strictChecking, boolean autoCreate) {
		this("ConcurentGraph", strictChecking, autoCreate);
	}

	/**
	 * New empty graph.
	 * 
	 * @param id
	 *            Unique identifier of this graph.
	 * @param strictChecking
	 *            If true any non-fatal error throws an exception.
	 * @param autoCreate
	 *            If true (and strict checking is false), nodes are
	 *            automatically created when referenced when creating a edge,
	 *            even if not yet inserted in the graph.
	 * @see #setStrict(boolean)
	 * @see #setAutoCreate(boolean)
	 */
	public ConcurrentGraph(String id, boolean strictChecking, boolean autoCreate) {
		super(id);
		setStrict(strictChecking);
		setAutoCreate(autoCreate);

		listeners = new GraphListeners();

		nodeFactory = new NodeFactory<ConcurrentNode>() {
			public ConcurrentNode newInstance(String id, Graph graph) {
				return new ConcurrentNode(graph, id);
			}
		};
		edgeFactory = new EdgeFactory<ConcurrentEdge>() {
			public ConcurrentEdge newInstance(String id, Node src, Node trg,
					boolean directed) {
				return new ConcurrentEdge(id, src, trg, directed);
			}
		};
	}

	@Override
	protected String myGraphId() // XXX
	{
		return getId();
	}

	@Override
	protected long newEvent() // XXX
	{
		return listeners.newEvent();
	}

	public EdgeFactory<? extends Edge> edgeFactory() {
		return edgeFactory;
	}

	@SuppressWarnings("unchecked")
	public void setEdgeFactory(EdgeFactory<? extends Edge> ef) {
		try {
			this.edgeFactory = (EdgeFactory<? extends ConcurrentEdge>) ef;
		} catch (ClassCastException e) {
			System.err
					.printf("need an EdgeFactory<? extends ConcurrentEdge>%n");
		}
	}

	public NodeFactory<? extends Node> nodeFactory() {
		return nodeFactory;
	}

	@SuppressWarnings("unchecked")
	public void setNodeFactory(NodeFactory<? extends Node> nf) {
		try {
			this.nodeFactory = (NodeFactory<? extends ConcurrentNode>) nf;
		} catch (ClassCastException e) {
			System.err
					.printf("need an NodeFactory<? extends ConcurrentNode>%n");
		}
	}

	public <T extends Edge> T addEdge(String id, String node1, String node2)
			throws IdAlreadyInUseException, ElementNotFoundException {
		return addEdge(id, node1, node2, false);
	}

	@SuppressWarnings("unchecked")
	protected <T extends Edge> T addEdge_(String sourceId, long timeId,
			String edgeId, String from, String to, boolean directed)
			throws IdAlreadyInUseException, ElementNotFoundException {
		ConcurrentNode src;
		ConcurrentNode trg;

		src = lookForNode(from);
		trg = lookForNode(to);

		if (src == null) {
			if (strictChecking) {
				throw new ElementNotFoundException("cannot make edge from '"
						+ from + "' to '" + to + "' since node '" + from
						+ "' is not part of this graph");
			} else if (autoCreate) {
				src = addNode(from);
			}
		}

		if (trg == null) {
			if (strictChecking) {
				throw new ElementNotFoundException("cannot make edge from '"
						+ from + "' to '" + to + "' since node '" + to
						+ "' is not part of this graph");
			} else if (autoCreate) {
				trg = addNode(to);
			}
		}

		if (src != null && trg != null) {
			T edge = null;
			ConcurrentEdge old = lookForEdge(edgeId);
			if (old != null) {
				if (strictChecking) {
					throw new IdAlreadyInUseException("id '" + edgeId
							+ "' already used, cannot add edge");
				} else {
					//
					// If fails, old edge is not of type T.
					//
					edge = (T) old;
				}
			} else {
				if (src.hasEdgeToward(trg) != null) {
					throw new IdAlreadyInUseException(
							"Cannot add edge between " + from + " and " + to
									+ ". A link already exists.");
				} else {
					//
					// If fails, edgeFactory does not provide edge of type T.
					//
					edge = (T) edgeFactory.newInstance(edgeId, src, trg,
							directed);

					((ConcurrentHashMap<String, T>) edges).put(edgeId, edge);

					src.edges.add(edge);
					trg.edges.add(edge);

					listeners.sendEdgeAdded(sourceId, timeId, edgeId, from, to,
							directed);
				}
			}

			return edge;
		}

		return null;
	}

	/**
	 * @complexity O(log(n)) with n being the number of edges in the graph, plus
	 *             overhead due to thread synchronization.
	 */
	public <T extends Edge> T addEdge(String id, String from, String to,
			boolean directed) throws IdAlreadyInUseException,
			ElementNotFoundException {
		T e = addEdge_(getId(), newEvent(), id, from, to, directed);
		return e;
	}

	/**
	 * @complexity O(log(n)) with n being the number of nodes in the graph, plus
	 *             overhead due to thread synchronization.
	 */
	public <T extends Node> T addNode(String id) throws IdAlreadyInUseException {
		T n = addNode_(getId(), newEvent(), id);
		return n;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Node> T addNode_(String sourceId, long timeId,
			String nodeId) throws IdAlreadyInUseException {
		T node;
		ConcurrentNode old = lookForNode(nodeId);

		if (old != null) {
			if (strictChecking) {
				throw new IdAlreadyInUseException("id '" + nodeId
						+ "' already used, cannot add node");
			} else {
				//
				// If fails, old node is not of type T.
				//
				node = (T) old;
			}
		} else {
			//
			// If fails, nodeFactory does not provide node of type T.
			//
			node = (T) nodeFactory.newInstance(nodeId, this);

			((ConcurrentHashMap<String, T>) nodes).put(nodeId, node);
			listeners.sendNodeAdded(sourceId, timeId, nodeId);
		}

		return node;
	}

	/**
	 * @complexity Constant.
	 */
	public void clear() {
		clear_(getId(), newEvent());
	}

	protected void clear_(String sourceId, long timeId) {
		listeners.sendGraphCleared(sourceId, timeId);
		nodes.clear();
		edges.clear();
	}

	/**
	 * @complexity Constant.
	 */
	public void clearSinks() {
		listeners.clearSinks();
	}

	public void clearAttributeSinks() {
		listeners.clearAttributeSinks();
	}

	public void clearElementSinks() {
		listeners.clearElementSinks();
	}

	/**
	 * @complexity O(log(n)) with n being the number of edges in the graph, plus
	 *             overhead due to thread synchronization.
	 */
	public <T extends Edge> T getEdge(String id) {
		return lookForEdge(id);
	}

	/**
	 * @complexity Constant.
	 */
	public int getEdgeCount() {
		return edges.size();
	}

	/**
	 * @complexity Constant.
	 */
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return new EdgeIterator<T>();
	}

	/**
	 * @complexity Constant.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> Iterable<? extends T> getEachEdge() {
		return (Iterable<T>) edges.values();
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> Collection<T> getEdgeSet() {
		return (Collection<T>) Collections.unmodifiableCollection(edges
				.values());
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> Collection<T> getNodeSet() {
		return (Collection<T>) Collections.unmodifiableCollection(nodes
				.values());
	}

	/**
	 * @complexity O(log(n)) with n being the number of nodes in the graph, plus
	 *             overhead due to thread synchronization.
	 */
	public <T extends Node> T getNode(String id) {
		return lookForNode(id);
	}

	/**
	 * @complexity Constant.
	 */
	public int getNodeCount() {
		return nodes.size();
	}

	/**
	 * @complexity Constant.
	 */
	public <T extends Node> Iterator<T> getNodeIterator() {
		return new NodeIterator<T>();
	}

	public Iterator<Node> iterator() {
		return new NodeIterator<Node>();
	}

	/**
	 * @complexity Constant.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> Iterable<? extends T> getEachNode() {
		return (Iterable<? extends T>) nodes.values();
	}

	public boolean isAutoCreationEnabled() {
		return autoCreate;
	}

	public boolean isStrict() {
		return strictChecking;
	}

	public Iterable<AttributeSink> attributeSinks() {
		return listeners.attributeSinks();
	}

	public Iterable<ElementSink> elementSinks() {
		return listeners.elementSinks();
	}

	public double getStep() {
		return step;
	}

	/**
	 * @complexity O( 2*log(n)+log(m) ) with n being the number of nodes and m
	 *             the number of edges in the graph, plus overhead due to thread
	 *             synchronization.
	 */
	public <T extends Edge> T removeEdge(String from, String to)
			throws ElementNotFoundException {
		return removeEdge_(getId(), newEvent(), from, to);
	}

	@SuppressWarnings("unchecked")
	protected <T extends Edge> T removeEdge_(String sourceId, long timeId,
			String from, String to) {
		ConcurrentNode n0 = lookForNode(from);
		ConcurrentNode n1 = lookForNode(to);

		if (n0 != null && n1 != null) {
			//
			// If fails, n0 does not register an edge of type T.
			//
			T e = (T) n0.hasEdgeToward(n1);

			if (e != null) {
				return removeEdge_(sourceId, timeId, e);
			} else {
				//
				// If fails, n0 does not register an edge of type T.
				//
				e = (T) n0.hasEdgeToward(n1);

				if (e != null) {
					return removeEdge_(sourceId, timeId, e);
				}
			}
		}
		return null;
	}

	/**
	 * @complexity O( 2*log(m) ) with m being the number of edges in the graph,
	 *             plus overhead due to thread synchronization.
	 */
	public <T extends Edge> T removeEdge(String id)
			throws ElementNotFoundException {
		T edge = lookForEdge(id);

		if (edge != null)
			removeEdge_(getId(), newEvent(), edge);

		return edge;
	}

	/**
	 * Removes an edge from a given reference to it.
	 * 
	 * @param edge
	 *            The reference of the edge to remove.
	 * @complexity O( log(m) ) with m the number of edges in the graph, plus
	 *             overhead due to thread synchronization.
	 */
	public <T extends Edge> T removeEdge(T edge)
			throws ElementNotFoundException {
		return removeEdge_(getId(), newEvent(), edge);
	}

	protected <T extends Edge> T removeEdge_(String sourceId, long timeId,
			T edge) {
		listeners.sendEdgeRemoved(sourceId, timeId, edge.getId());

		Node n0 = edge.getSourceNode();
		Node n1 = edge.getTargetNode();

		((ConcurrentNode) n0).edges.remove(edge);
		((ConcurrentNode) n1).edges.remove(edge);
		edges.remove(edge.getId());

		return edge;
	}

	/**
	 * @complexity 0( 2*log(n) ) with n the number of nodes in the graph, plus
	 *             overhead due to thread synchronization.
	 */
	public <T extends Node> T removeNode(String id)
			throws ElementNotFoundException {
		T node = lookForNode(id);

		if (node != null)
			return removeNode_(getId(), newEvent(), node);

		return null;
	}

	/**
	 * Remove a node form a given reference of it.
	 * 
	 * @param node
	 *            The reference of the node to be removed.
	 * @complexity 0( log(n) ) with n the number of nodes in the graph, plus
	 *             overhead due to thread synchronization.
	 */
	public <T extends Node> T removeNode(T node)
			throws ElementNotFoundException {
		return removeNode_(getId(), newEvent(), node);
	}

	protected <T extends Node> T removeNode_(String sourceId, long timeId,
			T node) {
		if (node != null) {
			listeners.sendNodeRemoved(sourceId, timeId, node.getId());
			disconnectEdges(node);
			nodes.remove(node.getId());

			return node;
		}

		if (strictChecking)
			throw new ElementNotFoundException("node not found, cannot remove");

		return null;
	}

	public void stepBegins(double step) {
		stepBegins_(getId(), newEvent(), step);
	}

	protected void stepBegins_(String sourceId, long timeId, double step) {
		this.step = step;

		listeners.sendStepBegins(sourceId, timeId, step);
	}

	/**
	 * When a node is unregistered from a graph, it must not keep edges
	 * connected to nodes still in the graph. This method unbinds all edges
	 * connected to this node and also unregister them from the graph.
	 * 
	 * @param node
	 *            A reference to the nod which edges are to be removed.
	 */
	protected void disconnectEdges(Node node) throws IllegalStateException {
		int n = node.getDegree();

		// We cannot use a "for" since unbinding an edge removes this edge from
		// the node. The number of edges will change continuously.

		while (n > 0) {
			Edge e = ((AdjacencyListNode) node).edges.get(0);
			removeEdge(e);
			n = node.getDegree(); // edges.size(); ???
		}
	}

	public void setAutoCreate(boolean on) {
		autoCreate = on;
	}

	public void setStrict(boolean on) {
		strictChecking = on;
	}

	/**
	 * Tries to retrieve a node in the internal structure identified by the
	 * given string.
	 * 
	 * @param id
	 *            The string identifier of the seek node.
	 * @complexity 0( log(n) ), with n being the number of nodes, plus overhead
	 *             due to thread synchronization.
	 * @return A reference to the node if found, or null if not.
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Node> T lookForNode(String id) {
		T node = null;

		try {
			node = (T) nodes.get(id);
		} catch (ClassCastException e) {
			StackTraceElement elt = e.getStackTrace()[0];

			System.err
					.printf("%s:%s:%d invalid node type%n", elt.getFileName(),
							elt.getMethodName(), elt.getLineNumber());
		}

		return node;
	}

	/**
	 * Tries to retrieve an edge in the internal structure identified by the
	 * given string.
	 * 
	 * @param id
	 *            The string identifier of the seek edges.
	 * @complexity 0( log(m) ), with m being the number of edges, plus overhead
	 *             due to thread synchronization.
	 * @return A reference to the edge if found, or null if not.
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Edge> T lookForEdge(String id) {
		T edge = null;

		try {
			edge = (T) edges.get(id);
		} catch (ClassCastException e) {
			StackTraceElement elt = e.getStackTrace()[0];

			System.err
					.printf("%s:%s:%d invalid edge type%n", elt.getFileName(),
							elt.getMethodName(), elt.getLineNumber());
		}

		return edge;
	}

	// Events

	public void addSink(Sink listener) {
		listeners.addSink(listener);
	}

	public void addAttributeSink(AttributeSink listener) {
		listeners.addAttributeSink(listener);
	}

	public void addElementSink(ElementSink listener) {
		listeners.addElementSink(listener);
	}

	public void removeSink(Sink listener) {
		listeners.removeSink(listener);
	}

	public void removeAttributeSink(AttributeSink listener) {
		listeners.removeAttributeSink(listener);
	}

	public void removeElementSink(ElementSink listener) {
		listeners.removeElementSink(listener);
	}

	protected void attributeChanged(String sourceId, long timeId,
			String attribute, AttributeChangeEvent event, Object oldValue,
			Object newValue) {
		listeners.sendAttributeChangedEvent(sourceId, timeId, getId(),
				ElementType.GRAPH, attribute, event, oldValue, newValue);
	}

	/*
	 * public void graphCleared() { clear_( getId(), newEvent() ); }
	 */
	// Commands -- Utility

	public void read(FileSource input, String filename) throws IOException,
			GraphParseException {
		input.readAll(filename);
	}

	public void read(String filename) throws IOException, GraphParseException,
			ElementNotFoundException {
		FileSource input = FileSourceFactory.sourceFor(filename);
		input.addSink(this);
		read(input, filename);
	}

	public void write(FileSink output, String filename) throws IOException {
		output.writeAll(this, filename);
	}

	public void write(String filename) throws IOException {
		FileSink output = FileSinkFactory.sinkFor(filename);
		write(output, filename);
	}

	public Viewer display() {
		return display(true);
	}

	public Viewer display(boolean autoLayout) {
		Viewer viewer = new Viewer(this,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		GraphRenderer renderer = Viewer.newGraphRenderer();

		viewer.addView(
				String.format("defaultView_%d", (long) (Math.random() * 10000)),
				renderer);

		if (autoLayout) {
			Layout layout = newLayoutAlgorithm();
			viewer.enableAutoLayout(layout);
		}

		return viewer;
	}

	protected static Layout newLayoutAlgorithm() {
		String layoutClassName = System.getProperty("gs.ui.layout");

		if (layoutClassName == null)
			return new org.graphstream.ui.layout.springbox.SpringBox(false);

		try {
			Class<?> c = Class.forName(layoutClassName);
			Object object = c.newInstance();

			if (object instanceof Layout) {
				return (Layout) object;
			} else {
				System.err.printf("class '%s' is not a 'GraphRenderer'%n",
						object);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err
					.printf("Cannot create layout, 'GraphRenderer' class not found : "
							+ e.getMessage());
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.err.printf("Cannot create layout, class '" + layoutClassName
					+ "' error : " + e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.err.printf("Cannot create layout, class '" + layoutClassName
					+ "' illegal access : " + e.getMessage());
		}

		return new org.graphstream.ui.layout.springbox.SpringBox(false);
	}

	// Sink

	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		listeners.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId,
				directed);
	}

	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		listeners.edgeRemoved(sourceId, timeId, edgeId);
	}

	public void graphCleared(String sourceId, long timeId) {
		listeners.graphCleared(sourceId, timeId);
	}

	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		listeners.nodeAdded(sourceId, timeId, nodeId);
	}

	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		listeners.nodeRemoved(sourceId, timeId, nodeId);
	}

	public void stepBegins(String sourceId, long timeId, double step) {
		listeners.stepBegins(sourceId, timeId, step);
	}

	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		listeners
				.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
	}

	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		listeners.edgeAttributeChanged(sourceId, timeId, edgeId, attribute,
				oldValue, newValue);
	}

	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		listeners.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		listeners.graphAttributeAdded(sourceId, timeId, attribute, value);
	}

	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		listeners.graphAttributeChanged(sourceId, timeId, attribute, oldValue,
				newValue);
	}

	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		listeners.graphAttributeRemoved(sourceId, timeId, attribute);
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		listeners
				.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
	}

	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		listeners.nodeAttributeChanged(sourceId, timeId, nodeId, attribute,
				oldValue, newValue);
	}

	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		listeners.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
	}

	// Handling the listeners -- We use the IO2 InputBase for this.

	class GraphListeners extends SourceBase implements Pipe {
		SinkTime sinkTime;

		public GraphListeners() {
			super(getId());

			sinkTime = new SinkTime();
			sourceTime.setSinkTime(sinkTime);
		}

		protected long newEvent() {
			return sourceTime.newEvent();
		}

		public void edgeAttributeAdded(String sourceId, long timeId,
				String edgeId, String attribute, Object value) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				Edge edge = getEdge(edgeId);

				if (edge != null)
					((AdjacencyListEdge) edge).addAttribute_(sourceId, timeId,
							attribute, value);
			}
		}

		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				Edge edge = getEdge(edgeId);

				if (edge != null)
					((AdjacencyListEdge) edge).changeAttribute_(sourceId,
							timeId, attribute, newValue);
			}
		}

		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				Edge edge = getEdge(edgeId);

				if (edge != null)
					((AdjacencyListEdge) edge).removeAttribute_(sourceId,
							timeId, attribute);
			}
		}

		public void graphAttributeAdded(String sourceId, long timeId,
				String attribute, Object value) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				addAttribute_(sourceId, timeId, attribute, value);
			}
		}

		public void graphAttributeChanged(String sourceId, long timeId,
				String attribute, Object oldValue, Object newValue) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				changeAttribute_(sourceId, timeId, attribute, newValue);
			}
		}

		public void graphAttributeRemoved(String sourceId, long timeId,
				String attribute) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				removeAttribute_(sourceId, timeId, attribute);
			}
		}

		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attribute, Object value) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				Node node = getNode(nodeId);

				if (node != null)
					((AdjacencyListNode) node).addAttribute_(sourceId, timeId,
							attribute, value);
			}
		}

		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				Node node = getNode(nodeId);

				if (node != null)
					((AdjacencyListNode) node).changeAttribute_(sourceId,
							timeId, attribute, newValue);
			}
		}

		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				Node node = getNode(nodeId);

				if (node != null)
					((AdjacencyListNode) node).removeAttribute_(sourceId,
							timeId, attribute);
			}
		}

		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				addEdge_(sourceId, timeId, edgeId, fromNodeId, toNodeId,
						directed);
			}
		}

		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				Edge e = getEdge(edgeId);

				if (e != null)
					removeEdge_(sourceId, timeId, getEdge(edgeId));
			}
		}

		public void graphCleared(String sourceId, long timeId) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				clear_(sourceId, timeId);
			}
		}

		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				addNode_(sourceId, timeId, nodeId);
			}
		}

		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			// System.err.printf( "%s.nodeRemoved( %s, %d, %s ) => ", getId(),
			// sourceId, timeId, nodeId );
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				Node n = getNode(nodeId);

				if (n != null) {
					// System.err.printf( "=> removed%n" );
					removeNode_(sourceId, timeId, n);
				}
			}
			// else System.err.printf( "=> ignored%n" );
		}

		public void stepBegins(String sourceId, long timeId, double step) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				stepBegins_(sourceId, timeId, step);
			}
		}
	}
}