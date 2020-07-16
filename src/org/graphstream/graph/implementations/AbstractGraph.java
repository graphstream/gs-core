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
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Richard O. Legendi <richard.legendi@gmail.com>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @since 2011-07-22
 */
package org.graphstream.graph.implementations;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

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
import org.graphstream.stream.Replayable;
import org.graphstream.stream.Sink;
import org.graphstream.stream.SourceBase;
import org.graphstream.ui.view.Viewer;
import org.graphstream.util.Display;
import org.graphstream.util.GraphListeners;
import org.graphstream.util.MissingDisplayException;

/**
 * <p>
 * This class provides a basic implementation of
 * {@link org.graphstream.graph.Graph} interface, to minimize the effort
 * required to implement this interface. It provides event management
 * implementing all the methods of {@link org.graphstream.stream.Pipe}. It also
 * manages strict checking and auto-creation policies, as well as other services
 * as displaying, reading and writing.
 * </p>
 * <p>
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
public abstract class AbstractGraph extends AbstractElement implements Graph, Replayable {
	// *** Fields ***

	GraphListeners listeners;
	private boolean strictChecking;
	private boolean autoCreate;
	private NodeFactory<? extends AbstractNode> nodeFactory;
	private EdgeFactory<? extends AbstractEdge> edgeFactory;

	private double step = 0;

	private long replayId = 0;

	// *** Constructors ***

	/**
	 * The same as {@code AbstractGraph(id, true, false)}
	 *
	 * @param id
	 * 		Identifier of the graph
	 * @see #AbstractGraph(String, boolean, boolean)
	 */
	public AbstractGraph(String id) {
		this(id, true, false);
	}

	/**
	 * Creates a new graph. Subclasses must create their node and edge factories and
	 * initialize their data structures in their constructors.
	 *
	 * @param id
	 * @param strictChecking
	 * @param autoCreate
	 */
	public AbstractGraph(String id, boolean strictChecking, boolean autoCreate) {
		super(id);

		this.strictChecking = strictChecking;
		this.autoCreate = autoCreate;
		this.listeners = new GraphListeners(this);
	}

	// *** Inherited from abstract element

	@Override
	protected void attributeChanged(AttributeChangeEvent event, String attribute, Object oldValue, Object newValue) {
		listeners.sendAttributeChangedEvent(id, SourceBase.ElementType.GRAPH, attribute, event, oldValue, newValue);
	}

	// *** Inherited from graph ***

	/**
	 * This implementation returns an iterator over nodes.
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Node> iterator() {
		return nodes().iterator();
	}

	// Factories

	@Override
	public NodeFactory<? extends Node> nodeFactory() {
		return nodeFactory;
	}

	@Override
	public EdgeFactory<? extends Edge> edgeFactory() {
		return edgeFactory;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setNodeFactory(NodeFactory<? extends Node> nf) {
		nodeFactory = (NodeFactory<? extends AbstractNode>) nf;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setEdgeFactory(EdgeFactory<? extends Edge> ef) {
		edgeFactory = (EdgeFactory<? extends AbstractEdge>) ef;
	}

	// strict checking, autocreation, etc

	@Override
	public boolean isStrict() {
		return strictChecking;
	}

	@Override
	public void setStrict(boolean on) {
		strictChecking = on;
	}

	@Override
	public boolean isAutoCreationEnabled() {
		return autoCreate;
	}

	@Override
	public double getStep() {
		return step;
	}

	@Override
	public void setAutoCreate(boolean on) {
		autoCreate = on;
	}

	@Override
	public void stepBegins(double time) {
		listeners.sendStepBegins(time);
		this.step = time;
	}

	// display, read, write

	public Viewer display() {
		return display(true);
	}

	public Viewer display(boolean autoLayout) {
		try {
			Display display = Display.getDefault();
			return display.display(this, autoLayout);
		} catch (MissingDisplayException e) {
			throw new RuntimeException("Cannot launch viewer.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#clear()
	 */
	@Override
	public void clear() {
		listeners.sendGraphCleared();

		nodes().forEach(n -> ((AbstractNode) n).clearCallback());

		clearCallback();
		clearAttributesWithNoEvent();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#addNode(java.lang.String)
	 */
	@Override
	public Node addNode(String id) {
		AbstractNode node = (AbstractNode) getNode(id);

		if (node != null) {
			if (strictChecking)
				throw new IdAlreadyInUseException("id \"" + id + "\" already in use. Cannot create a node.");
			return node;
		}

		node = nodeFactory.newInstance(id, this);
		addNodeCallback(node);

		listeners.sendNodeAdded(id);

		return node;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String,
	 * org.graphstream.graph.Node, org.graphstream.graph.Node, boolean)
	 */
	@Override
	public Edge addEdge(String id, Node from, Node to, boolean directed) {
		return addEdge(id, (AbstractNode) from, from.getId(), (AbstractNode) to, to.getId(), directed);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeNode(org.graphstream.graph.Node)
	 */
	@Override
	public Node removeNode(Node node) {
		if (node == null)
			return null;

		removeNode((AbstractNode) node, true);
		return node;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeEdge(org.graphstream.graph.Edge)
	 */
	@Override
	public Edge removeEdge(Edge edge) {
		if (edge == null)
			return null;

		removeEdge((AbstractEdge) edge, true, true, true);
		return edge;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeEdge(org.graphstream.graph.Node,
	 * org.graphstream.graph.Node)
	 */
	@Override
	public Edge removeEdge(Node node1, Node node2) {
		Edge edge = node1.getEdgeToward(node2);

		if (edge == null) {
			if (strictChecking)
				throw new ElementNotFoundException("There is no edge from \"%s\" to \"%s\". Cannot remove it.",
						node1.getId(), node2.getId());
			return null;
		}

		return removeEdge(edge);
	}

	// *** Sinks, sources etc. ***

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#attributeSinks()
	 */
	@Override
	public Iterable<AttributeSink> attributeSinks() {
		return listeners.attributeSinks();
	}

	/*
	 * *(non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#elementSinks()
	 */
	@Override
	public Iterable<ElementSink> elementSinks() {
		return listeners.elementSinks();
	}

	/*
	 * *(non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#addAttributeSink(org.graphstream.stream
	 * .AttributeSink)
	 */
	@Override
	public void addAttributeSink(AttributeSink sink) {
		listeners.addAttributeSink(sink);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#addElementSink(org.graphstream.stream.
	 * ElementSink)
	 */
	@Override
	public void addElementSink(ElementSink sink) {
		listeners.addElementSink(sink);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#addSink(org.graphstream.stream.Sink)
	 */
	@Override
	public void addSink(Sink sink) {
		listeners.addSink(sink);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#clearAttributeSinks()
	 */
	@Override
	public void clearAttributeSinks() {
		listeners.clearAttributeSinks();
	}

	/*
	 * *(non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#clearElementSinks()
	 */
	@Override
	public void clearElementSinks() {
		listeners.clearElementSinks();
	}

	/*
	 * *(non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#clearSinks()
	 */
	@Override
	public void clearSinks() {
		listeners.clearSinks();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#removeAttributeSink(org.graphstream.stream
	 * .AttributeSink)
	 */
	@Override
	public void removeAttributeSink(AttributeSink sink) {
		listeners.removeAttributeSink(sink);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#removeElementSink(org.graphstream.stream
	 * .ElementSink)
	 */
	@Override
	public void removeElementSink(ElementSink sink) {
		listeners.removeElementSink(sink);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#removeSink(org.graphstream.stream.Sink)
	 */
	@Override
	public void removeSink(Sink sink) {
		listeners.removeSink(sink);
	}

	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		listeners.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
	}

	@Override
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		listeners.edgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue, newValue);
	}

	@Override
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		listeners.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	@Override
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
		listeners.graphAttributeAdded(sourceId, timeId, attribute, value);
	}

	@Override
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
		listeners.graphAttributeChanged(sourceId, timeId, attribute, oldValue, newValue);
	}

	@Override
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
		listeners.graphAttributeRemoved(sourceId, timeId, attribute);
	}

	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		listeners.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
	}

	@Override
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		listeners.nodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue, newValue);
	}

	@Override
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		listeners.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
	}

	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		listeners.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
	}

	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		listeners.edgeRemoved(sourceId, timeId, edgeId);
	}

	@Override
	public void graphCleared(String sourceId, long timeId) {
		listeners.graphCleared(sourceId, timeId);
	}

	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		listeners.nodeAdded(sourceId, timeId, nodeId);
	}

	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		listeners.nodeRemoved(sourceId, timeId, nodeId);
	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step) {
		listeners.stepBegins(sourceId, timeId, step);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Replayable#getReplayController()
	 */
	@Override
	public Replayable.Controller getReplayController() {
		return new GraphReplayController();
	}

	// *** callbacks maintaining user's data structure

	/**
	 * This method is automatically called when a new node is created. Subclasses
	 * must add the new node to their data structure and to set its index correctly.
	 *
	 * @param node
	 * 		the node to be added
	 */
	protected abstract void addNodeCallback(AbstractNode node);

	/**
	 * This method is automatically called when a new edge is created. Subclasses
	 * must add the new edge to their data structure and to set its index correctly.
	 *
	 * @param edge
	 * 		the edge to be added
	 */
	protected abstract void addEdgeCallback(AbstractEdge edge);

	/**
	 * This method is automatically called when a node is removed. Subclasses must
	 * remove the node from their data structures and to re-index other node(s) so
	 * that node indices remain coherent.
	 *
	 * @param node
	 * 		the node to be removed
	 */
	protected abstract void removeNodeCallback(AbstractNode node);

	/**
	 * This method is automatically called when an edge is removed. Subclasses must
	 * remove the edge from their data structures and re-index other edge(s) so that
	 * edge indices remain coherent.
	 *
	 * @param edge
	 * 		the edge to be removed
	 */
	protected abstract void removeEdgeCallback(AbstractEdge edge);

	/**
	 * This method is automatically called when the graph is cleared. Subclasses
	 * must remove all the nodes and all the edges from their data structures.
	 */
	protected abstract void clearCallback();

	// *** _ methods ***

	// Why do we pass both the ids and the references of the endpoints here?
	// When the caller knows the references it's stupid to call getNode(id)
	// here. If the node does not exist the reference will be null.
	// And if autoCreate is on, we need also the id. Sad but true!
	protected Edge addEdge(String edgeId, AbstractNode src, String srcId, AbstractNode dst, String dstId,
			boolean directed) {
		AbstractEdge edge = (AbstractEdge) getEdge(edgeId);

		if (edge != null) {
			if (strictChecking)
				throw new IdAlreadyInUseException("id \"" + edgeId + "\" already in use. Cannot create an edge.");
			if ((edge.getSourceNode() == src && edge.getTargetNode() == dst) || (!directed
					&& edge.getTargetNode() == src && edge.getSourceNode() == dst))
				return edge;
			return null;
		}

		if (src == null || dst == null) {
			if (strictChecking)
				throw new ElementNotFoundException(
						String.format("Cannot create edge %s[%s-%s%s]. Node '%s' does not exist.", edgeId, srcId,
								directed ? ">" : "-", dstId, src == null ? srcId : dstId));
			if (!autoCreate)
				return null;

			if (src == null)
				src = (AbstractNode) addNode(srcId);
			if (dst == null)
				dst = (AbstractNode) addNode(dstId);
		}
		// at this point edgeId is not in use and both src and dst are not null

		if(src.getGraph() != this || dst.getGraph() != this) {
			throw new ElementNotFoundException("At least one of two nodes does not belong to the graph.");
		}
		edge = edgeFactory.newInstance(edgeId, src, dst, directed);
		// see if the endpoints accept the edge
		if (!src.addEdgeCallback(edge)) {
			if (strictChecking)
				throw new EdgeRejectedException("Edge " + edge + " was rejected by node " + src);
			return null;
		}
		// note that for loop edges the callback is called only once
		if (src != dst && !dst.addEdgeCallback(edge)) {
			// the edge is accepted by src but rejected by dst
			// so we have to remove it from src
			src.removeEdgeCallback(edge);
			if (strictChecking)
				throw new EdgeRejectedException("Edge " + edge + " was rejected by node " + dst);
			return null;
		}

		// now we can finally add it
		addEdgeCallback(edge);

		listeners.sendEdgeAdded(edgeId, srcId, dstId, directed);

		return edge;
	}

	// helper for removeNode_
	private void removeAllEdges(AbstractNode node) {
		Collection<Edge> toRemove = node.edges().collect(Collectors.toList());
		toRemove.forEach(this::removeEdge);
	}

	// *** Methods for iterators ***

	/**
	 * This method is similar to {@link #removeNode(Node)} but allows to control if
	 * {@link #removeNodeCallback(AbstractNode)} is called or not. It is useful for
	 * iterators supporting {@link java.util.Iterator#remove()} who want to update
	 * the data structures by their owns.
	 *
	 * @param node
	 * 		the node to be removed
	 * @param graphCallback
	 * 		if {@code false}, {@code removeNodeCallback(node)} is not called
	 */
	protected void removeNode(AbstractNode node, boolean graphCallback) {
		if (node == null) {
			throw new NullPointerException("node reference is null");
		}
		if (node.getGraph() != this){
			throw new ElementNotFoundException( "Node \""+node.getId()+"\" does not belong to this graph");
		}
		

		removeAllEdges(node);
		listeners.sendNodeRemoved(node.getId());

		if (graphCallback)
			removeNodeCallback(node);
	}

	/**
	 * This method is similar to {@link #removeEdge(Edge)} but allows to control if
	 * different callbacks are called or not. It is useful for iterators supporting
	 * {@link java.util.Iterator#remove()} who want to update the data structures by
	 * their owns.
	 *
	 * @param edge
	 * 		the edge to be removed
	 * @param graphCallback
	 * 		if {@code false}, {@link #removeEdgeCallback(AbstractEdge)} of the
	 * 		graph is not called
	 * @param sourceCallback
	 * 		if {@code false},
	 * 		{@link AbstractNode#removeEdgeCallback(AbstractEdge)} is not
	 * 		called for the source node of the edge
	 * @param targetCallback
	 * 		if {@code false},
	 * 		{@link AbstractNode#removeEdgeCallback(AbstractEdge)} is not
	 * 		called for the target node of the edge
	 */
	protected void removeEdge(AbstractEdge edge, boolean graphCallback, boolean sourceCallback,
			boolean targetCallback) {
		if (edge == null) {
			throw new NullPointerException("edge reference is null");
		}

		AbstractNode src = (AbstractNode) edge.getSourceNode();
		AbstractNode dst = (AbstractNode) edge.getTargetNode();
		
		if (src.getGraph() != this || dst.getGraph() != this){
			throw new ElementNotFoundException( "Edge \""+edge.getId()+"\" does not belong to this graph");
		}

		listeners.sendEdgeRemoved(edge.getId());

		if (sourceCallback)
			src.removeEdgeCallback(edge);

		if (src != dst && targetCallback)
			dst.removeEdgeCallback(edge);

		if (graphCallback)
			removeEdgeCallback(edge);
	}

	class GraphReplayController extends SourceBase implements Replayable.Controller {
		GraphReplayController() {
			super(AbstractGraph.this.id + "replay");
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.graphstream.stream.Replayable.Controller#replay()
		 */
		@Override
		public void replay() {
			String sourceId = String.format("%s-replay-%x", id, replayId++);
			replay(sourceId);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.graphstream.stream.Replayable.Controller#replay(java.lang.String)
		 */
		@Override
		public void replay(String sourceId) {
			attributeKeys().forEach(key -> sendGraphAttributeAdded(sourceId, key, getAttribute(key)));

			for (int i = 0; i < getNodeCount(); i++) {
				Node node = getNode(i);
				String nodeId = node.getId();

				sendNodeAdded(sourceId, nodeId);

				node.attributeKeys()
						.forEach(key -> sendNodeAttributeAdded(sourceId, nodeId, key, node.getAttribute(key)));
			}

			for (int i = 0; i < getEdgeCount(); i++) {
				Edge edge = getEdge(i);
				String edgeId = edge.getId();

				sendEdgeAdded(sourceId, edgeId, edge.getNode0().getId(), edge.getNode1().getId(), edge.isDirected());

				edge.attributeKeys()
						.forEach(key -> sendEdgeAttributeAdded(sourceId, edgeId, key, edge.getAttribute(key)));
			}
		}
	}
}
