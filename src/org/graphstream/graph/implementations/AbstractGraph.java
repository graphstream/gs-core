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

public abstract class AbstractGraph extends AbstractElement implements Graph {
	// *** Fields ***

	private boolean strictChecking;
	private boolean autoCreate;
	GraphListeners listeners;
	private NodeFactory<? extends AbstractNode> nodeFactory;
	private EdgeFactory<? extends AbstractEdge> edgeFactory;

	/**
	 * The number of adds and removes of elements. Maintained because of
	 * iterators
	 */
	private int modifCount = 0;

	private double step = 0;

	private boolean nullAttributesAreErrors;

	// *** Constructors ***

	public AbstractGraph(String id) {
		this(id, true, false);
	}

	public AbstractGraph(String id, boolean strictChecking, boolean autoCreate) {
		super(id);
		this.strictChecking = strictChecking;
		this.autoCreate = autoCreate;
		listeners = new GraphListeners();

		// Subclasses must instanciate their factories and initialize their data
		// structures

	}

	// *** Inherited from abstract element

	@Override
	protected void attributeChanged(String sourceId, long timeId,
			String attribute, AttributeChangeEvent event, Object oldValue,
			Object newValue) {
		listeners.sendAttributeChangedEvent(sourceId, timeId, getId(),
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

	public <T extends Node> T getNode(int index)
			throws IndexOutOfBoundsException {
		if (index < 0 || index >= getNodeCount()) {
			if (strictChecking)
				throw new IndexOutOfBoundsException("Node with index " + index
						+ " does not exist");
			return null;
		}
		return getNodeByIndex(index);
	}

	protected abstract <T extends Node> T getNodeByIndex(int index);

	public abstract <T extends Edge> T getEdge(String id);

	public <T extends Edge> T getEdge(int index)
			throws IndexOutOfBoundsException {
		if (index < 0 || index >= getEdgeCount()) {
			if (strictChecking)
				throw new IndexOutOfBoundsException("Edge with index " + index
						+ "does not exist");
			return null;
		}
		return getEdgeByIndex(index);
	}

	protected abstract <T extends Edge> T getEdgeByIndex(int index);

	// node and edge count, iterators and views

	public abstract int getNodeCount();

	public abstract int getEdgeCount();

	public abstract <T extends Node> Iterator<T> getNodeIterator();

	public abstract <T extends Edge> Iterator<T> getEdgeIterator();

	public <T extends Node> Iterable<? extends T> getEachNode() {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				Iterator<T> it = getNodeIterator();
				return new ImmutableIterator<T>(it);
			}
		};
	}

	public <T extends Edge> Iterable<? extends T> getEachEdge() {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				Iterator<T> it = getEdgeIterator();
				return new ImmutableIterator<T>(it);
			}
		};
	}

	public <T extends Node> Collection<T> getNodeSet() {
		return new AbstractCollection<T>() {
			public Iterator<T> iterator() {
				Iterator<T> it = getNodeIterator();
				return new ImmutableIterator<T>(it);
			}

			public int size() {
				return getNodeCount();
			}
		};
	}

	public <T extends Edge> Collection<T> getEdgeSet() {
		return new AbstractCollection<T>() {
			public Iterator<T> iterator() {
				Iterator<T> it = getEdgeIterator();
				return new ImmutableIterator<T>(it);
			}

			public int size() {
				return getNodeCount();
			}
		};
	}

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
		stepBegins_(getId(), listeners.newEvent(), time);
	}

	// adding and removing elements

	public void clear() {
		clear_(getId(), listeners.newEvent());
	}

	public <T extends Node> T addNode(String id) {
		return addNode_(getId(), listeners.newEvent(), id);
	}

	public <T extends Edge> T addEdge(String id, String node1, String node2) {
		return addEdge(id, node1, node2, false);
	}

	public <T extends Edge> T addEdge(String id, String from, String to,
			boolean directed) {
		return addEdge_(getId(), listeners.newEvent(), id,
				(AbstractNode) getNode(from), from, (AbstractNode) getNode(to),
				to, directed);
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
		if (from == null || to == null) {
			if (strictChecking)
				throw new ElementNotFoundException(
						"Cannot create edge with null endpoints");
			return null;
		}
		return addEdge_(getId(), listeners.newEvent(), id, (AbstractNode) from,
				from.getId(), (AbstractNode) to, to.getId(), directed);
	}

	public <T extends Node> T removeNode(String id) {
		return removeNode_(getId(), listeners.newEvent(),
				(AbstractNode) getNode(id), id, true);
	}

	public <T extends Node> T removeNode(int index) {
		return removeNode(getNode(index));
	}

	public <T extends Node> T removeNode(Node node) {
		if (node == null) {
			if (strictChecking)
				throw new ElementNotFoundException("Cannot remove null node");
			return null;
		}
		return removeNode_(getId(), listeners.newEvent(), (AbstractNode) node,
				node.getId(), true);
	}

	public <T extends Edge> T removeEdge(String id) {
		return removeEdge_(getId(), listeners.newEvent(),
				(AbstractEdge) getEdge(id), id, true, true);
	}

	public <T extends Edge> T removeEdge(int index) {
		return removeEdge(getEdge(index));
	}

	public <T extends Edge> T removeEdge(Edge edge) {
		if (edge == null) {
			if (strictChecking)
				throw new ElementNotFoundException("Cannot remove null edge");
			return null;
		}
		return removeEdge_(getId(), listeners.newEvent(), (AbstractEdge) edge,
				edge.getId(), true, true);
	}

	public <T extends Edge> T removeEdge(String from, String to) {
		return removeEdge(getNode(from), getNode(to));
	}

	public <T extends Edge> T removeEdge(int fromIndex, int toIndex) {
		return removeEdge(getNode(fromIndex), getNode(toIndex));
	}

	public <T extends Edge> T removeEdge(Node node1, Node node2) {
		if (node1 == null || node2 == null) {
			if (strictChecking)
				throw new ElementNotFoundException(
						"Cannot remove the edge. One of its endpoints does not exist");
			return null;
		}
		AbstractEdge edge = node1.getEdgeToward(node2);
		if (edge == null) {
			if (strictChecking)
				throw new ElementNotFoundException("There is no edge from \""
						+ node1.getId() + "\" to \"" + node2.getId()
						+ "\". Cannot remove it.");
			return null;
		}
		return removeEdge_(getId(), listeners.newEvent(), edge, edge.getId(),
				true, true);
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


	// *** callbacks maintaining user's data structure

	protected abstract void addNodeCallback(AbstractNode node);

	protected abstract void addEdgeCallback(AbstractEdge edge);

	protected abstract void removeNodeCallback(AbstractNode node);

	protected abstract void removeEdgeCallback(AbstractEdge edge);

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
		modifCount++;
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
						String
								.format(
										"Cannot create edge %s[%s-%s%s]. Node '%s' does not exist.",
										edgeId, srcId, directed ? ">" : "-",
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
		modifCount++;
		listeners.sendEdgeAdded(sourceId, edgeId, srcId, dstId, directed);
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
		if (graphCallback)
			removeNodeCallback(node);
		modifCount++;
		listeners.sendNodeRemoved(sourceId, timeId, nodeId);
		return (T) node;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Edge> T removeEdge_(String sourceId, long timeId,
			AbstractEdge edge, String edgeId, boolean graphCallback,
			boolean nodeCallback) {
		if (edge == null) {
			if (strictChecking)
				throw new ElementNotFoundException("Edge \"" + edgeId
						+ "\" not found. Cannot remove it.");
			return null;
		}
		if (nodeCallback) {
			AbstractNode src = edge.getSourceNode();
			AbstractNode dst = edge.getSourceNode();
			src.removeEdgeCallback(edge);
			// note that the callback is called only once for loop edges
			if (src != dst)
				dst.removeEdgeCallback(edge);
		}
		if (graphCallback)
			removeEdgeCallback(edge);
		modifCount++;
		listeners.sendEdgeRemoved(sourceId, edgeId);
		return (T) edge;
	}

	protected void clear_(String sourceId, long timeId) {
		Iterator<AbstractNode> it = getNodeIterator();
		while (it.hasNext())
			it.next().clearCallback();
		clearCallback();
		modifCount++;
		clearAttributes();
		listeners.sendGraphCleared(sourceId, timeId);
	}

	protected void stepBegins_(String sourceId, long timeId, double step) {
		this.step = step;
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

	protected void removeNode(AbstractNode node, boolean graphCallback) {
		removeNode_(getId(), listeners.newEvent(), node, node.getId(), graphCallback);
	}

	protected void removeEdge(AbstractEdge edge, boolean graphCallback,
			boolean nodeCallback) {
		removeEdge_(getId(), listeners.newEvent(), edge, edge.getId(), graphCallback, nodeCallback);
	}

	protected int getModifCount() {
		return modifCount;
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
					edge
							.changeAttribute_(sourceId, timeId, attribute,
									newValue);
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
					node
							.changeAttribute_(sourceId, timeId, attribute,
									newValue);
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
				removeEdge_(sourceId, timeId, edge, edgeId, true, true);
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

	// *** immutable iterators used for the views

	protected static class ImmutableIterator<T> implements Iterator<T> {
		private Iterator<T> it;

		protected ImmutableIterator(Iterator<T> it) {
			this.it = it;
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		public T next() {
			return it.next();
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"This iterator does not support remove.");
		}
	}

}
