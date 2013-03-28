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

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.Replayable;
import org.graphstream.stream.Sink;
import org.graphstream.stream.SourceBase;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkFactory;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.stream.sync.SinkTime;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.swingViewer.GraphRenderer;
import org.graphstream.ui.swingViewer.Viewer;

/**
 * <p>
 * This class provides a basic implementation of
 * {@link org.graphstream.graph.Graph} interface, to minimize the effort
 * required to implement this interface. It provides event management
 * implementing all the methods of {@link org.graphstream.stream.Pipe}. It also
 * manages strict checking and auto-creation policies, as well as other services
 * as displaying, reading and writing.
 * </p>
 * 
 * <p>
 * Subclasses have to maintain data structures allowing to efficiently access
 * graph elements by their id or index and iterating on them. They also have to
 * maintain coherent indices of the graph elements. When AbstractGraph decides
 * to add or remove elements, it calls one of the "callbacks"
 * {@link #addNodeCallback(AbstractNode)},
 * {@link #addEdgeCallback(AbstractEdge)},
 * {@link #removeNodeCallback(AbstractNode)},
 * {@link #removeEdgeCallback(AbstractEdge)}, {@link #clearCallback()}. The role
 * of these callbacks is to update the data structures and to re-index elements
 * if necessary.
 * </p>
 */
public abstract class AbstractGraph extends AbstractElement implements Graph,
		Replayable {
	// *** Fields ***

	private boolean strictChecking;
	private boolean autoCreate;
	GraphListeners listeners;
	private NodeFactory<? extends AbstractNode> nodeFactory;
	private EdgeFactory<? extends AbstractEdge> edgeFactory;

	private double step = 0;

	private boolean nullAttributesAreErrors;

	private long replayId = 0;

	// *** Constructors ***

	/**
	 * The same as {@code AbstractGraph(id, true, false)}
	 * 
	 * @param id
	 *            Identifier of the graph
	 * @see #AbstractGraph(String, boolean, boolean)
	 */
	public AbstractGraph(String id) {
		this(id, true, false);
	}

	/**
	 * Creates a new graph. Subclasses must create their node and edge factories
	 * and initialize their data structures in their constructors.
	 * 
	 * @param id
	 * @param strictChecking
	 * @param autoCreate
	 */
	public AbstractGraph(String id, boolean strictChecking, boolean autoCreate) {
		super(id);
		this.strictChecking = strictChecking;
		this.autoCreate = autoCreate;
		listeners = new GraphListeners();
	}

	// *** Inherited from abstract element

	@Override
	protected void attributeChanged(String sourceId, long timeId,
			String attribute, AttributeChangeEvent event, Object oldValue,
			Object newValue) {
		listeners.sendAttributeChangedEvent(sourceId, timeId, id,
				SourceBase.ElementType.GRAPH, attribute, event, oldValue,
				newValue);
	}

	@Override
	protected String myGraphId() {
		return getId();
	}

	@Override
	protected long newEvent() {
		return listeners.newEvent();
	}

	@Override
	public boolean nullAttributesAreErrors() {
		return nullAttributesAreErrors;
	}

	// *** Inherited from graph ***

	// some helpers

	// get node / edge by its id/index

	public abstract <T extends Node> T getNode(String id);

	public abstract <T extends Node> T getNode(int index);

	public abstract <T extends Edge> T getEdge(String id);

	public abstract <T extends Edge> T getEdge(int index);

	// node and edge count, iterators and views

	public abstract int getNodeCount();

	public abstract int getEdgeCount();

	public abstract <T extends Node> Iterator<T> getNodeIterator();

	public abstract <T extends Edge> Iterator<T> getEdgeIterator();

	/**
	 * This implementation uses {@link #getNodeIterator()}
	 * 
	 * @see org.graphstream.graph.Graph#getEachNode()
	 */
	public <T extends Node> Iterable<? extends T> getEachNode() {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return getNodeIterator();
			}
		};
	}

	/**
	 * This implementation uses {@link #getEdgeIterator()}
	 * 
	 * @see org.graphstream.graph.Graph#getEachEdge()
	 */
	public <T extends Edge> Iterable<? extends T> getEachEdge() {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return getEdgeIterator();
			}
		};
	}

	/**
	 * This implementation uses {@link #getNodeIterator()} and
	 * {@link #getNodeCount()}
	 * 
	 * @see org.graphstream.graph.Graph#getNodeSet()
	 */
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

	/**
	 * This implementation uses {@link #getEdgeIterator()} and
	 * {@link #getEdgeCount()}
	 * 
	 * @see org.graphstream.graph.Graph#getNodeSet()
	 */
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

	/**
	 * This implementation returns {@link #getNodeIterator()}
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Node> iterator() {
		return getNodeIterator();
	}

	// Factories

	public NodeFactory<? extends Node> nodeFactory() {
		return nodeFactory;
	}

	public EdgeFactory<? extends Edge> edgeFactory() {
		return edgeFactory;
	}

	@SuppressWarnings("unchecked")
	public void setNodeFactory(NodeFactory<? extends Node> nf) {
		nodeFactory = (NodeFactory<? extends AbstractNode>) nf;
	}

	@SuppressWarnings("unchecked")
	public void setEdgeFactory(EdgeFactory<? extends Edge> ef) {
		edgeFactory = (EdgeFactory<? extends AbstractEdge>) ef;
	}

	// strict checking, autocreation, etc

	public boolean isStrict() {
		return strictChecking;
	}

	public boolean isAutoCreationEnabled() {
		return autoCreate;
	}

	public double getStep() {
		return step;
	}

	public void setNullAttributesAreErrors(boolean on) {
		nullAttributesAreErrors = on;
	}

	public void setStrict(boolean on) {
		strictChecking = on;
	}

	public void setAutoCreate(boolean on) {
		autoCreate = on;
	}

	public void stepBegins(double time) {
		stepBegins_(getId(), -1, time);
	}

	// adding and removing elements

	public void clear() {
		clear_(getId(), -1);
	}

	public <T extends Node> T addNode(String id) {
		return addNode_(getId(), -1, id);
	}

	public <T extends Edge> T addEdge(String id, String node1, String node2) {
		return addEdge(id, node1, node2, false);
	}

	public <T extends Edge> T addEdge(String id, String from, String to,
			boolean directed) {
		return addEdge_(getId(), -1, id, (AbstractNode) getNode(from), from,
				(AbstractNode) getNode(to), to, directed);
	}

	public <T extends Edge> T addEdge(String id, int index1, int index2) {
		return addEdge(id, index1, index2, false);
	}

	public <T extends Edge> T addEdge(String id, int fromIndex, int toIndex,
			boolean directed) {
		return addEdge(id, getNode(fromIndex), getNode(toIndex), directed);
	}

	public <T extends Edge> T addEdge(String id, Node node1, Node node2) {
		return addEdge(id, node1, node2, false);
	}

	public <T extends Edge> T addEdge(String id, Node from, Node to,
			boolean directed) {
		return addEdge_(getId(), -1, id, (AbstractNode) from, from.getId(),
				(AbstractNode) to, to.getId(), directed);
	}

	public <T extends Node> T removeNode(String id) {
		return removeNode_(getId(), -1, (AbstractNode) getNode(id), id, true);
	}

	public <T extends Node> T removeNode(int index) {
		Node node = getNode(index);
		return removeNode(node);
	}

	public <T extends Node> T removeNode(Node node) {
		return removeNode_(getId(), -1, (AbstractNode) node,
				node == null ? null : node.getId(), true);
	}

	public <T extends Edge> T removeEdge(String id) {
		return removeEdge_(getId(), -1, (AbstractEdge) getEdge(id), id, true,
				true, true);
	}

	public <T extends Edge> T removeEdge(int index) {
		Edge edge = getEdge(index);
		return removeEdge(edge);
	}

	public <T extends Edge> T removeEdge(Edge edge) {
		return removeEdge_(getId(), -1, (AbstractEdge) edge,
				edge == null ? null : edge.getId(), true, true, true);
	}

	public <T extends Edge> T removeEdge(String from, String to) {
		Node fromNode = getNode(from);
		Node toNode = getNode(to);

		if (fromNode == null || toNode == null) {
			if (strictChecking)
				throw new ElementNotFoundException(
						"Cannot remove the edge. The node \"%s\" does not exist",
						fromNode == null ? from : to);
			return null;
		}

		return removeEdge(fromNode, toNode);
	}

	public <T extends Edge> T removeEdge(int fromIndex, int toIndex) {
		Node fromNode = getNode(fromIndex);
		Node toNode = getNode(toIndex);

		return removeEdge(fromNode, toNode);
	}

	public <T extends Edge> T removeEdge(Node node1, Node node2) {
		AbstractEdge edge = node1.getEdgeToward(node2);

		if (edge == null) {
			if (strictChecking)
				throw new ElementNotFoundException(
						"There is no edge from \"%s\" to \"%s\". Cannot remove it.",
						node1.getId(), node2.getId());
			return null;
		}

		return removeEdge_(id, -1, edge, edge.getId(), true, true, true);
	}

	// *** Sinks, sources etc. ***

	public Iterable<AttributeSink> attributeSinks() {
		return listeners.attributeSinks();
	}

	public Iterable<ElementSink> elementSinks() {
		return listeners.elementSinks();
	}

	public void addAttributeSink(AttributeSink sink) {
		listeners.addAttributeSink(sink);
	}

	public void addElementSink(ElementSink sink) {
		listeners.addElementSink(sink);
	}

	public void addSink(Sink sink) {
		listeners.addSink(sink);
	}

	public void clearAttributeSinks() {
		listeners.clearAttributeSinks();
	}

	public void clearElementSinks() {
		listeners.clearElementSinks();
	}

	public void clearSinks() {
		listeners.clearSinks();
	}

	public void removeAttributeSink(AttributeSink sink) {
		listeners.removeAttributeSink(sink);
	}

	public void removeElementSink(ElementSink sink) {
		listeners.removeElementSink(sink);
	}

	public void removeSink(Sink sink) {
		listeners.removeSink(sink);
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

	// display, read, write

	public Viewer display() {
		return display(true);
	}

	public Viewer display(boolean autoLayout) {
		Viewer viewer = new Viewer(this,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		GraphRenderer renderer = Viewer.newGraphRenderer();
		viewer.addView(Viewer.DEFAULT_VIEW_ID, renderer);
		if (autoLayout) {
			Layout layout = Layouts.newLayoutAlgorithm();
			viewer.enableAutoLayout(layout);
		}
		return viewer;
	}

	public void read(FileSource input, String filename) throws IOException,
			GraphParseException {
		input.readAll(filename);
	}

	public void read(String filename) throws IOException, GraphParseException,
			ElementNotFoundException {
		FileSource input = FileSourceFactory.sourceFor(filename);
		if (input != null) {
			input.addSink(this);
			read(input, filename);
			input.removeSink(this);
		} else {
			throw new IOException("No source reader for " + filename);
		}
	}

	public void write(FileSink output, String filename) throws IOException {
		output.writeAll(this, filename);
	}

	public void write(String filename) throws IOException {
		FileSink output = FileSinkFactory.sinkFor(filename);
		if (output != null) {
			write(output, filename);
		} else {
			throw new IOException("No sink writer for " + filename);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Replayable#getReplayController()
	 */
	public Replayable.Controller getReplayController() {
		return new GraphReplayController();
	}

	// *** callbacks maintaining user's data structure

	/**
	 * This method is automatically called when a new node is created.
	 * Subclasses must add the new node to their data structure and to set its
	 * index correctly.
	 * 
	 * @param node
	 *            the node to be added
	 */
	protected abstract void addNodeCallback(AbstractNode node);

	/**
	 * This method is automatically called when a new edge is created.
	 * Subclasses must add the new edge to their data structure and to set its
	 * index correctly.
	 * 
	 * @param edge
	 *            the edge to be added
	 */
	protected abstract void addEdgeCallback(AbstractEdge edge);

	/**
	 * This method is automatically called when a node is removed. Subclasses
	 * must remove the node from their data structures and to re-index other
	 * node(s) so that node indices remain coherent.
	 * 
	 * @param node
	 *            the node to be removed
	 */
	protected abstract void removeNodeCallback(AbstractNode node);

	/**
	 * This method is automatically called when an edge is removed. Subclasses
	 * must remove the edge from their data structures and re-index other
	 * edge(s) so that edge indices remain coherent.
	 * 
	 * @param edge
	 *            the edge to be removed
	 */
	protected abstract void removeEdgeCallback(AbstractEdge edge);

	/**
	 * This method is automatically called when the graph is cleared. Subclasses
	 * must remove all the nodes and all the edges from their data structures.
	 */
	protected abstract void clearCallback();

	// *** _ methods ***

	@SuppressWarnings("unchecked")
	protected <T extends Node> T addNode_(String sourceId, long timeId,
			String nodeId) {
		AbstractNode node = getNode(nodeId);
		if (node != null) {
			if (strictChecking)
				throw new IdAlreadyInUseException("id \"" + nodeId
						+ "\" already in use. Cannot create a node.");
			return (T) node;
		}
		node = nodeFactory.newInstance(nodeId, this);
		addNodeCallback(node);
		// If the event comes from the graph itself, create timeId
		if (timeId == -1)
			timeId = newEvent();
		listeners.sendNodeAdded(sourceId, timeId, nodeId);
		return (T) node;
	}

	// Why do we pass both the ids and the references of the endpoints here?
	// When the caller knows the references it's stupid to call getNode(id)
	// here. If the node does not exist the reference will be null.
	// And if autoCreate is on, we need also the id. Sad but true!
	@SuppressWarnings("unchecked")
	protected <T extends Edge> T addEdge_(String sourceId, long timeId,
			String edgeId, AbstractNode src, String srcId, AbstractNode dst,
			String dstId, boolean directed) {
		AbstractEdge edge = getEdge(edgeId);
		if (edge != null) {
			if (strictChecking)
				throw new IdAlreadyInUseException("id \"" + edgeId
						+ "\" already in use. Cannot create an edge.");
			if ((edge.getSourceNode() == src && edge.getTargetNode() == dst)
					|| (!directed && edge.getTargetNode() == src && edge
							.getSourceNode() == dst))
				return (T) edge;
			return null;
		}

		if (src == null || dst == null) {
			if (strictChecking)
				throw new ElementNotFoundException(
						String.format(
								"Cannot create edge %s[%s-%s%s]. Node '%s' does not exist.",
								edgeId, srcId, directed ? ">" : "-", dstId,
								src == null ? srcId : dstId));
			if (!autoCreate)
				return null;
			if (src == null)
				src = addNode(srcId);
			if (dst == null)
				dst = addNode(dstId);
		}
		// at this point edgeId is not in use and both src and dst are not null
		edge = edgeFactory.newInstance(edgeId, src, dst, directed);
		// see if the endpoints accept the edge
		if (!src.addEdgeCallback(edge)) {
			if (strictChecking)
				throw new EdgeRejectedException("Edge " + edge
						+ " was rejected by node " + src);
			return null;
		}
		// note that for loop edges the callback is called only once
		if (src != dst && !dst.addEdgeCallback(edge)) {
			// the edge is accepted by src but rejected by dst
			// so we have to remove it from src
			src.removeEdgeCallback(edge);
			if (strictChecking)
				throw new EdgeRejectedException("Edge " + edge
						+ " was rejected by node " + dst);
			return null;
		}
		// now we can finally add it
		addEdgeCallback(edge);
		// If the event comes from the graph itself, create timeId
		if (timeId == -1)
			timeId = newEvent();
		listeners.sendEdgeAdded(sourceId, timeId, edgeId, srcId, dstId,
				directed);
		return (T) edge;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Node> T removeNode_(String sourceId, long timeId,
			AbstractNode node, String nodeId, boolean graphCallback) {

		if (node == null) {
			if (strictChecking)
				throw new ElementNotFoundException("Node \"" + nodeId
						+ "\" not found. Cannot remove it.");
			return null;
		}

		removeAllEdges(node);

		// If the event comes from the graph itself, create timeId
		if (timeId == -1)
			timeId = newEvent();
		listeners.sendNodeRemoved(sourceId, timeId, nodeId);

		if (graphCallback)
			removeNodeCallback(node);

		return (T) node;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Edge> T removeEdge_(String sourceId, long timeId,
			AbstractEdge edge, String edgeId, boolean graphCallback,
			boolean srcCallback, boolean dstCallback) {
		if (edge == null) {
			if (strictChecking)
				throw new ElementNotFoundException("Edge \"" + edgeId
						+ "\" not found. Cannot remove it.");
			return null;
		}

		AbstractNode src = edge.getSourceNode();
		AbstractNode dst = edge.getTargetNode();

		// If the event comes from the graph itself, create timeId
		if (timeId == -1)
			timeId = newEvent();
		listeners.sendEdgeRemoved(sourceId, timeId, edgeId);

		if (srcCallback)
			src.removeEdgeCallback(edge);
		// note that the callback is called only once for loop edges
		if (src != dst && dstCallback)
			dst.removeEdgeCallback(edge);
		if (graphCallback)
			removeEdgeCallback(edge);

		return (T) edge;
	}

	protected void clear_(String sourceId, long timeId) {
		Iterator<AbstractNode> it = getNodeIterator();
		while (it.hasNext())
			it.next().clearCallback();
		clearCallback();
		clearAttributes();
		// If the event comes from the graph itself, create timeId
		if (timeId == -1)
			timeId = newEvent();
		listeners.sendGraphCleared(sourceId, timeId);
	}

	protected void stepBegins_(String sourceId, long timeId, double step) {
		this.step = step;
		// If the event comes from the graph itself, create timeId
		if (timeId == -1)
			timeId = newEvent();
		listeners.sendStepBegins(sourceId, timeId, step);
	}

	// helper for removeNode_
	private void removeAllEdges(AbstractNode node) {
		// first check if the EdgeIterator of node supports remove
		// if this is the case, we will use it, generally it will be much more
		// efficient
		Iterator<AbstractEdge> edgeIt = node.getEdgeIterator();
		boolean supportsRemove = true;
		if (!edgeIt.hasNext())
			return;
		try {
			edgeIt.next();
			edgeIt.remove();
		} catch (UnsupportedOperationException e) {
			supportsRemove = false;
		}
		if (supportsRemove)
			while (edgeIt.hasNext()) {
				edgeIt.next();
				edgeIt.remove();
			}
		else
			while (node.getDegree() > 0)
				removeEdge(node.getEdge(0));
	}

	// *** Methods for iterators ***

	/**
	 * This method is similar to {@link #removeNode(Node)} but allows to control
	 * if {@link #removeNodeCallback(AbstractNode)} is called or not. It is
	 * useful for iterators supporting {@link java.util.Iterator#remove()} who
	 * want to update the data structures by their owns.
	 * 
	 * @param node
	 *            the node to be removed
	 * @param graphCallback
	 *            if {@code false}, {@code removeNodeCallback(node)} is not
	 *            called
	 */
	protected void removeNode(AbstractNode node, boolean graphCallback) {
		removeNode_(id, -1, node, node.getId(), graphCallback);
	}

	/**
	 * This method is similar to {@link #removeEdge(Edge)} but allows to control
	 * if different callbacks are called or not. It is useful for iterators
	 * supporting {@link java.util.Iterator#remove()} who want to update the
	 * data structures by their owns.
	 * 
	 * @param edge
	 *            the edge to be removed
	 * @param graphCallback
	 *            if {@code false}, {@link #removeEdgeCallback(AbstractEdge)} of
	 *            the graph is not called
	 * @param sourceCallback
	 *            if {@code false},
	 *            {@link AbstractNode#removeEdgeCallback(AbstractEdge)} is not
	 *            called for the source node of the edge
	 * @param targetCallback
	 *            if {@code false},
	 *            {@link AbstractNode#removeEdgeCallback(AbstractEdge)} is not
	 *            called for the target node of the edge
	 */
	protected void removeEdge(AbstractEdge edge, boolean graphCallback,
			boolean sourceCallback, boolean targetCallback) {
		removeEdge_(id, -1, edge, edge.getId(), graphCallback, sourceCallback,
				targetCallback);
	}

	class GraphReplayController extends SourceBase implements
			Replayable.Controller {
		GraphReplayController() {
			super(AbstractGraph.this.id + "replay");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.Replayable.Controller#replay()
		 */
		public void replay() {
			String sourceId = String.format("%s-replay-%x", id, replayId++);
			replay(sourceId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.Replayable.Controller#replay(java.lang.String)
		 */
		public void replay(String sourceId) {
			for (String key : getAttributeKeySet())
				sendGraphAttributeAdded(sourceId, key, getAttribute(key));

			for (int i = 0; i < getNodeCount(); i++) {
				Node node = getNode(i);
				String nodeId = node.getId();

				sendNodeAdded(sourceId, nodeId);

				if (node.getAttributeCount() > 0)
					for (String key : node.getAttributeKeySet())
						sendNodeAttributeAdded(sourceId, nodeId, key,
								node.getAttribute(key));
			}

			for (int i = 0; i < getEdgeCount(); i++) {
				Edge edge = getEdge(i);
				String edgeId = edge.getId();

				sendEdgeAdded(sourceId, edgeId, edge.getNode0().getId(), edge
						.getNode1().getId(), edge.isDirected());

				if (edge.getAttributeCount() > 0)
					for (String key : edge.getAttributeKeySet())
						sendEdgeAttributeAdded(sourceId, edgeId, key,
								edge.getAttribute(key));
			}
		}
	}

	// *** Listeners ***

	// Handling the listeners -- We use the IO2 InputBase for this.

	class GraphListeners extends SourceBase implements Pipe {
		SinkTime sinkTime;

		public GraphListeners() {
			super(getId());

			sinkTime = new SinkTime();
			sourceTime.setSinkTime(sinkTime);
		}

		public long newEvent() {
			return sourceTime.newEvent();
		}

		public void edgeAttributeAdded(String sourceId, long timeId,
				String edgeId, String attribute, Object value) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				AbstractEdge edge = getEdge(edgeId);
				if (edge != null)
					edge.addAttribute_(sourceId, timeId, attribute, value);
			}
		}

		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				AbstractEdge edge = getEdge(edgeId);
				if (edge != null)
					edge.changeAttribute_(sourceId, timeId, attribute, newValue);
			}
		}

		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				AbstractEdge edge = getEdge(edgeId);
				if (edge != null)
					edge.removeAttribute_(sourceId, timeId, attribute);
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
				AbstractNode node = getNode(nodeId);
				if (node != null)
					node.addAttribute_(sourceId, timeId, attribute, value);
			}
		}

		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				AbstractNode node = getNode(nodeId);
				if (node != null)
					node.changeAttribute_(sourceId, timeId, attribute, newValue);
			}
		}

		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				AbstractNode node = getNode(nodeId);
				if (node != null)
					node.removeAttribute_(sourceId, timeId, attribute);
			}
		}

		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				AbstractNode from = getNode(fromNodeId);
				AbstractNode to = getNode(toNodeId);
				addEdge_(sourceId, timeId, edgeId, from, fromNodeId, to,
						toNodeId, directed);
			}
		}

		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				AbstractEdge edge = getEdge(edgeId);
				removeEdge_(sourceId, timeId, edge, edgeId, true, true, true);
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
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				AbstractNode node = getNode(nodeId);
				removeNode_(sourceId, timeId, node, nodeId, true);
			}
		}

		public void stepBegins(String sourceId, long timeId, double step) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				stepBegins_(sourceId, timeId, step);
			}
		}
	}
}
