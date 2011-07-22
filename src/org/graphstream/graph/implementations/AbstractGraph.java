package org.graphstream.graph.implementations;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.Sink;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.ui.swingViewer.Viewer;

public abstract class AbstractGraph extends AbstractElement implements Graph {

	public AbstractGraph(String id) {
		super(id);
		// TODO Auto-generated constructor stub
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
	
	public <T extends Node> T getNode(int index) {
		// TODO
		return null;
	}
	
	
	

}
