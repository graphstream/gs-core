package org.graphstream.graph.implementations;

import java.io.IOException;
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
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.sync.SinkTime;
import org.graphstream.ui.swingViewer.Viewer;

public abstract class AbstractGraph extends AbstractElement implements Graph {
	// *** Fields ***

	private boolean strictChecking;
	private boolean autoCreate;
	protected GraphListeners listeners;
	private NodeFactory<? extends AbstractNode> nodeFactory;
	private EdgeFactory<? extends AbstractEdge> edgeFactory;
	/**
	 * The number of adds and removes of elements. Maintained because of
	 * iterators
	 */
	private int modifCount = 0;

	// *** Constructors ***

	public AbstractGraph(String id) {
		this(id, true, false);
	}

	public AbstractGraph(String id, boolean strictChecking, boolean autoCreate) {
		super(id);
		this.strictChecking = strictChecking;
		this.autoCreate = autoCreate;
		listeners = new GraphListeners();

		// XXX Subclasses must instanciate their factories

	}

	@Override
	protected void attributeChanged(String sourceId, long timeId,
			String attribute, AttributeChangeEvent event, Object oldValue,
			Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String myGraphId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected long newEvent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean nullAttributesAreErrors() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends Edge> T addEdge(String id, String node1, String node2)
			throws IdAlreadyInUseException, ElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Edge> T addEdge(String id, String from, String to,
			boolean directed) throws IdAlreadyInUseException,
			ElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Node> T addNode(String id) throws IdAlreadyInUseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<AttributeSink> attributeSinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Viewer display() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Viewer display(boolean autoLayout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EdgeFactory<? extends Edge> edgeFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<ElementSink> elementSinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Edge> Iterable<? extends T> getEachEdge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Node> Iterable<? extends T> getEachNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Edge> T getEdge(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEdgeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Edge> Collection<T> getEdgeSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Node> T getNode(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNodeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends Node> Iterator<T> getNodeIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Node> Collection<T> getNodeSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getStep() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isAutoCreationEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStrict() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NodeFactory<? extends Node> nodeFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void read(String filename) throws IOException, GraphParseException,
			ElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void read(FileSource input, String filename) throws IOException,
			GraphParseException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Edge> T removeEdge(String from, String to)
			throws ElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Edge> T removeEdge(String id)
			throws ElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Node> T removeNode(String id)
			throws ElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAutoCreate(boolean on) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEdgeFactory(EdgeFactory<? extends Edge> ef) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNodeFactory(NodeFactory<? extends Node> nf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNullAttributesAreErrors(boolean on) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStrict(boolean on) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stepBegins(double time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String filename) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(FileSink output, String filename) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAttributeSink(AttributeSink sink) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addElementSink(ElementSink sink) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSink(Sink sink) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAttributeSinks() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearElementSinks() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearSinks() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAttributeSink(AttributeSink sink) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeElementSink(ElementSink sink) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSink(Sink sink) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		// TODO Auto-generated method stub

	}

	@Override
	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void graphCleared(String sourceId, long timeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<Node> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	// *** new methods in Graph

	public <T extends Edge> T getEdge(int index) {
		// TODO
		return null;
	}

	public <T extends Node> T getNode(int index) {
		// TODO
		return null;
	}

	public <T extends Edge> T addEdge(String id, int index1, int index2) {
		// TODO
		return null;
	}

	public <T extends Edge> T addEdge(String id, int fromIndex, int toIndex,
			boolean directed) {
		// TODO
		return null;
	}

	public <T extends Edge> T removeEdge(int index) {
		// TODO
		return null;
	}

	public <T extends Edge> T removeEdge(int fromIndex, int toIndex) {
		// TODO
		return null;
	}

	public <T extends Edge> T removeEdge(Edge edge) {
		// TODO
		return null;
	}

	public <T extends Node> T removeNode(int index) {
		// TODO
		return null;
	}

	public <T extends Node> T removeNode(Node node) {
		// TODO
		return null;
	}

	// *** callbacks maintaining user's data structure

	protected abstract void addNodeCallback(AbstractNode node);

	protected abstract void addEdgeCallback(AbstractEdge edge);

	protected abstract void removeNodeCallback(AbstractNode node);

	protected abstract void removeEdgeCallback(AbstractEdge edge);
	
	protected abstract void clearCallback();

	// *** _ methods ***

	protected AbstractNode addNode_(String sourceId, long timeId, String nodeId) {
		AbstractNode node = getNode(nodeId);
		if (node != null) {
			if (strictChecking)
				throw new IdAlreadyInUseException("id \"" + nodeId
						+ "\" already in use. Cannot create a node.");
			return node;
		}
		node = nodeFactory.newInstance(nodeId, this);
		addNodeCallback(node);
		modifCount++;
		listeners.sendNodeAdded(sourceId, timeId, nodeId);
		return node;
	}

	// Why do we pass both the ids and the references of the endpoints here?
	// When the caller knows the references it's stupid to call getNode(id)
	// here. If the node does not exist the reference will be null.
	// And if autoCreate is on, we need also the id. Sad but true!
	protected AbstractEdge addEdge_(String sourceId, long timeId,
			String edgeId, AbstractNode src, String srcId, AbstractNode dst,
			String dstId, boolean directed) {
		AbstractEdge edge = getEdge(edgeId);
		if (edge != null) {
			if (strictChecking)
				throw new IdAlreadyInUseException("id \"" + edgeId
						+ "\" already in use. Cannot create an edge.");
			if ((edge.getSourceNode() == src && edge.getTargetNode() == dst)
					|| (!directed && edge.getTargetNode() == src && edge.getSourceNode() == dst))
				return edge;
			return null;
		}

		if (src == null || dst == null) {
			if (strictChecking)
				throw new ElementNotFoundException(
						String.format("Cannot create edge %s[%s-%s%s]. Node '%s' does not exist.",
								edgeId, srcId, directed ? ">" : "-", src == null ? srcId : dstId));
			if (!autoCreate)
				return null;
			if (src == null)
				src = addNode(srcId);
			if (dst == null)
				dst = addNode(dstId);
		}
		// at this point edgeId is not in use and both src and dst are not null
		edge = edgeFactory.newInstance(edgeId, src, dst, directed);
		// see if the endpoints accept the dege
		if (!src.addEdgeCallback(edge)) {
			if (strictChecking)
				throw new EdgeRejectedException("Edge " + edge + " was rejected by node " + src);
			return null;
		}
		if (!dst.addEdgeCallback(edge)) {
			src.removeEdgeCallback(edge);
			if (strictChecking)
				throw new EdgeRejectedException("Edge " + edge + " was rejected by node " + dst);
			return null;
		}
		// now we can finally add it
		addEdgeCallback(edge);
		modifCount++;
		listeners.sendEdgeAdded(sourceId, edgeId, srcId, dstId, directed);
		return edge;
	}

	protected AbstractNode removeNode_(String sourceId, long timeId,
			AbstractNode node, String nodeId, boolean graphCallback) {
		if (node == null) {
			if (strictChecking)
				throw new ElementNotFoundException("Node \"" + nodeId + "\" not found. Cannot remove it.");
			return null;
		}
		removeAllEdges(node);
		if (graphCallback)
			removeNodeCallback(node);
		modifCount++;
		listeners.sendNodeRemoved(sourceId, timeId, nodeId);
		return node;
	}

	protected AbstractEdge removeEdge_(String sourceId, long timeId,
			AbstractEdge edge, String edgeId, boolean graphCallback, boolean nodeCallback) {
		if (edge == null) {
			if (strictChecking)
				throw new ElementNotFoundException("Edge \"" + edgeId + "\" not found. Cannot remove it.");
			return null;
		}
		if (nodeCallback) {
			edge.<AbstractNode>getSourceNode().removeEdgeCallback(edge);
			edge.<AbstractNode>getTargetNode().removeEdgeCallback(edge);			
		}
		if (graphCallback)
			removeEdgeCallback(edge);
		modifCount++;
		listeners.sendEdgeRemoved(sourceId, edgeId);
		return edge;
	}

	protected void clear_(String sourceId, long timeId) {
		clearCallback();
		modifCount++;
		clearAttributes();
		listeners.sendGraphCleared(sourceId, timeId);
	}

	protected void stepBegins_(String sourceId, long timeId, double step) {
		// TODO
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
	
	protected void removeNode(Node node, boolean graphCallback) {
		// TODO
	}
	
	protected void removeEdge(Edge edge, boolean graphCallback, boolean nodeCallback) {
		// TODO
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
}
