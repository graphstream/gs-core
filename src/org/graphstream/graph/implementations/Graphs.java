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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Element;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.GraphReplay;
import org.graphstream.stream.Sink;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.ui.swingViewer.Viewer;

public class Graphs {

	public static Graph unmutableGraph(Graph g) {
		return null;
	}

	/**
	 * Synchronizes a graph. The returned graph can be accessed and modified by
	 * several threads. You lose genericity in methods returning edge or node
	 * because each element (graph, nodes and edges) is wrapped into a
	 * synchronized wrapper which breaks original elements class.
	 * 
	 * @param g
	 *            the graph to synchronize
	 * @return a synchronized wrapper for g
	 */
	public static Graph synchronizedGraph(Graph g) {
		return new SynchronizedGraph(g);
	}

	/**
	 * Merge several graphs in one. A new graph is created, that will contain
	 * the result. The method will try to create a graph of the same class that
	 * the first graph to merge (it needs to have a constructor with a String).
	 * Else, a MultiGraph is used.
	 * 
	 * @param graphs
	 *            graphs to merge
	 * @return merge result
	 */
	public static Graph merge(Graph... graphs) {
		if (graphs == null)
			return new DefaultGraph("void-merge");

		String id = "merge";

		for (Graph g : graphs)
			id += "-" + g.getId();

		Graph result;

		try {
			Class<? extends Graph> cls = graphs[0].getClass();
			result = cls.getConstructor(String.class).newInstance(id);
		} catch (Exception e) {
			System.err.printf("*** WARNING *** can not create a graph of %s\n",
					graphs[0].getClass().getName());

			result = new MultiGraph(id);
		}

		mergeIn(result, graphs);

		return result;
	}

	/**
	 * Merge several graphs in one. The first parameter is the graph in which
	 * the other graphs will be merged.
	 * 
	 * @param result
	 *            destination graph.
	 * @param graphs
	 *            all graphs that will be merged in result.
	 */
	public static void mergeIn(Graph result, Graph... graphs) {
		boolean strict = result.isStrict();
		GraphReplay replay = new GraphReplay(String.format("replay-%x",
				System.nanoTime()));

		replay.addSink(result);
		result.setStrict(false);

		if (graphs != null)
			for (Graph g : graphs)
				replay.replay(g);

		replay.removeSink(result);
		result.setStrict(strict);
	}

	static class SynchronizedElement<U extends Element> implements Element {

		private final ReentrantLock attributeLock;
		protected final U wrappedElement;

		SynchronizedElement(U e) {
			this.wrappedElement = e;
			this.attributeLock = new ReentrantLock();
		}

		public void addAttribute(String attribute, Object... values) {
			attributeLock.lock();
			wrappedElement.addAttribute(attribute, values);
			attributeLock.unlock();
		}

		public void addAttributes(Map<String, Object> attributes) {
			attributeLock.lock();
			wrappedElement.addAttributes(attributes);
			attributeLock.unlock();
		}

		public void changeAttribute(String attribute, Object... values) {
			attributeLock.lock();
			wrappedElement.changeAttribute(attribute, values);
			attributeLock.unlock();
		}

		public void clearAttributes() {
			attributeLock.lock();
			wrappedElement.clearAttributes();
			attributeLock.unlock();
		}

		public Object[] getArray(String key) {
			Object[] o;

			attributeLock.lock();
			o = wrappedElement.getArray(key);
			attributeLock.unlock();

			return o;
		}

		public <T> T getAttribute(String key) {
			T o;

			attributeLock.lock();
			o = wrappedElement.getAttribute(key);
			attributeLock.unlock();

			return o;
		}

		public <T> T getAttribute(String key, Class<T> clazz) {
			T o;

			attributeLock.lock();
			o = wrappedElement.getAttribute(key, clazz);
			attributeLock.unlock();

			return o;
		}

		public int getAttributeCount() {
			int c;

			attributeLock.lock();
			c = wrappedElement.getAttributeCount();
			attributeLock.unlock();

			return c;
		}

		public Iterator<String> getAttributeKeyIterator() {
			return getAttributeKeySet().iterator();
		}

		public Iterable<String> getAttributeKeySet() {
			ArrayList<String> o;
			Iterator<String> it;

			attributeLock.lock();

			o = new ArrayList<String>(wrappedElement.getAttributeCount());
			it = wrappedElement.getAttributeKeyIterator();

			while (it.hasNext())
				o.add(it.next());

			attributeLock.unlock();

			return o;
		}

		public <T> T getFirstAttributeOf(String... keys) {
			T o;

			attributeLock.lock();
			o = wrappedElement.getFirstAttributeOf(keys);
			attributeLock.unlock();

			return o;
		}

		public <T> T getFirstAttributeOf(Class<T> clazz, String... keys) {
			T o;

			attributeLock.lock();
			o = wrappedElement.getFirstAttributeOf(clazz, keys);
			attributeLock.unlock();

			return o;
		}

		public HashMap<?, ?> getHash(String key) {
			HashMap<?, ?> o;

			attributeLock.lock();
			o = wrappedElement.getHash(key);
			attributeLock.unlock();

			return o;
		}

		public String getId() {
			return wrappedElement.getId();
		}

		public int getIndex() {
			return wrappedElement.getIndex();
		}

		public CharSequence getLabel(String key) {
			CharSequence o;

			attributeLock.lock();
			o = wrappedElement.getLabel(key);
			attributeLock.unlock();

			return o;
		}

		public double getNumber(String key) {
			double o;

			attributeLock.lock();
			o = wrappedElement.getNumber(key);
			attributeLock.unlock();

			return o;
		}

		public ArrayList<? extends Number> getVector(String key) {
			ArrayList<? extends Number> o;

			attributeLock.lock();
			o = wrappedElement.getVector(key);
			attributeLock.unlock();

			return o;
		}

		public boolean hasArray(String key) {
			boolean b;

			attributeLock.lock();
			b = wrappedElement.hasArray(key);
			attributeLock.unlock();

			return b;
		}

		public boolean hasAttribute(String key) {
			boolean b;

			attributeLock.lock();
			b = wrappedElement.hasAttribute(key);
			attributeLock.unlock();

			return b;
		}

		public boolean hasAttribute(String key, Class<?> clazz) {
			boolean b;

			attributeLock.lock();
			b = wrappedElement.hasAttribute(key, clazz);
			attributeLock.unlock();

			return b;
		}

		public boolean hasHash(String key) {
			boolean b;

			attributeLock.lock();
			b = wrappedElement.hasHash(key);
			attributeLock.unlock();

			return b;
		}

		public boolean hasLabel(String key) {
			boolean b;

			attributeLock.lock();
			b = wrappedElement.hasLabel(key);
			attributeLock.unlock();

			return b;
		}

		public boolean hasNumber(String key) {
			boolean b;

			attributeLock.lock();
			b = wrappedElement.hasNumber(key);
			attributeLock.unlock();

			return b;
		}

		public boolean hasVector(String key) {
			boolean b;

			attributeLock.lock();
			b = wrappedElement.hasVector(key);
			attributeLock.unlock();

			return b;
		}

		public void removeAttribute(String attribute) {
			attributeLock.lock();
			wrappedElement.removeAttribute(attribute);
			attributeLock.unlock();
		}

		public void setAttribute(String attribute, Object... values) {
			attributeLock.lock();
			wrappedElement.setAttribute(attribute, values);
			attributeLock.unlock();
		}
	}

	static class SynchronizedGraph extends SynchronizedElement<Graph> implements
			Graph {

		final ReentrantLock elementLock;
		final HashMap<String, Node> synchronizedNodes;
		final HashMap<String, Edge> synchronizedEdges;

		SynchronizedGraph(Graph g) {
			super(g);

			elementLock = new ReentrantLock();
			synchronizedNodes = new HashMap<String, Node>();
			synchronizedEdges = new HashMap<String, Edge>();

			for (Node n : g.getEachNode())
				synchronizedNodes.put(n.getId(), new SynchronizedNode(this, n));

			for (Edge e : g.getEachEdge())
				synchronizedEdges.put(e.getId(), new SynchronizedEdge(this, e));
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T addEdge(String id, String node1, String node2)
				throws IdAlreadyInUseException, ElementNotFoundException,
				EdgeRejectedException {
			T e;
			Edge se;

			elementLock.lock();

			e = wrappedElement.addEdge(id, node1, node2);
			se = new SynchronizedEdge(this, e);
			synchronizedEdges.put(id, se);

			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T addEdge(String id, String from, String to,
				boolean directed) throws IdAlreadyInUseException,
				ElementNotFoundException {
			T e;
			Edge se;

			elementLock.lock();

			e = wrappedElement.addEdge(id, from, to, directed);
			se = new SynchronizedEdge(this, e);
			synchronizedEdges.put(id, se);

			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T addEdge(String id, int index1, int index2) {
			T e;
			Edge se;

			elementLock.lock();

			e = wrappedElement.addEdge(id, index1, index2);
			se = new SynchronizedEdge(this, e);
			synchronizedEdges.put(id, se);

			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T addEdge(String id, int fromIndex,
				int toIndex, boolean directed) {
			T e;
			Edge se;

			elementLock.lock();

			e = wrappedElement.addEdge(id, fromIndex, toIndex, directed);
			se = new SynchronizedEdge(this, e);
			synchronizedEdges.put(id, se);

			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T addEdge(String id, Node node1, Node node2) {
			T e;
			Edge se;
			final Node unsyncNode1, unsyncNode2;
			
			unsyncNode1 = ((SynchronizedElement<Node>) node1).wrappedElement;
			unsyncNode2 = ((SynchronizedElement<Node>) node2).wrappedElement;
			
			elementLock.lock();

			e = wrappedElement.addEdge(id, unsyncNode1, unsyncNode2);
			se = new SynchronizedEdge(this, e);
			synchronizedEdges.put(id, se);

			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T addEdge(String id, Node from, Node to,
				boolean directed) {
			T e;
			Edge se;
			final Node unsyncFrom, unsyncTo;
			
			unsyncFrom = ((SynchronizedElement<Node>) from).wrappedElement;
			unsyncTo = ((SynchronizedElement<Node>) to).wrappedElement;

			elementLock.lock();

			e = wrappedElement.addEdge(id, unsyncFrom, unsyncTo, directed);
			se = new SynchronizedEdge(this, e);
			synchronizedEdges.put(id, se);

			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Node> T addNode(String id)
				throws IdAlreadyInUseException {
			T n;
			Node sn;

			elementLock.lock();

			n = wrappedElement.addNode(id);
			sn = new SynchronizedNode(this, n);
			synchronizedNodes.put(id, sn);

			elementLock.unlock();

			return (T) sn;
		}

		public Iterable<AttributeSink> attributeSinks() {
			LinkedList<AttributeSink> sinks = new LinkedList<AttributeSink>();

			elementLock.lock();

			for (AttributeSink as : wrappedElement.attributeSinks())
				sinks.add(as);

			elementLock.unlock();

			return sinks;
		}

		public void clear() {
			elementLock.lock();
			wrappedElement.clear();
			elementLock.unlock();
		}

		public Viewer display() {
			return wrappedElement.display();
		}

		public Viewer display(boolean autoLayout) {
			return wrappedElement.display(autoLayout);
		}

		public EdgeFactory<? extends Edge> edgeFactory() {
			return wrappedElement.edgeFactory();
		}

		public Iterable<ElementSink> elementSinks() {
			LinkedList<ElementSink> sinks = new LinkedList<ElementSink>();

			elementLock.lock();

			for (ElementSink es : wrappedElement.elementSinks())
				sinks.add(es);

			elementLock.unlock();

			return sinks;
		}

		public Iterable<Edge> getEachEdge() {
			LinkedList<Edge> edges;

			elementLock.lock();
			edges = new LinkedList<Edge>(synchronizedEdges.values());
			elementLock.unlock();

			return edges;
		}

		public Iterable<Node> getEachNode() {
			LinkedList<Node> nodes;

			elementLock.lock();
			nodes = new LinkedList<Node>(synchronizedNodes.values());
			elementLock.unlock();

			return nodes;
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T getEdge(String id) {
			T e;

			elementLock.lock();
			e = (T) synchronizedEdges.get(id);
			elementLock.unlock();

			return e;
		}

		public <T extends Edge> T getEdge(int index)
				throws IndexOutOfBoundsException {
			Edge e;

			elementLock.lock();
			e = wrappedElement.getEdge(index);
			elementLock.unlock();

			return e == null ? null : this.<T> getEdge(e.getId());
		}

		public int getEdgeCount() {
			int c;

			elementLock.lock();
			c = synchronizedEdges.size();
			elementLock.unlock();

			return c;
		}

		public Iterator<Edge> getEdgeIterator() {
			return getEdgeSet().iterator();
		}

		public Collection<Edge> getEdgeSet() {
			LinkedList<Edge> l;

			elementLock.lock();
			l = new LinkedList<Edge>(synchronizedEdges.values());
			elementLock.unlock();

			return l;
		}

		@SuppressWarnings("unchecked")
		public <T extends Node> T getNode(String id) {
			T n;

			elementLock.lock();
			n = (T) synchronizedNodes.get(id);
			elementLock.unlock();

			return n;
		}

		public <T extends Node> T getNode(int index)
				throws IndexOutOfBoundsException {
			Node n;

			elementLock.lock();
			n = wrappedElement.getNode(index);
			elementLock.unlock();

			return n == null ? null : this.<T> getNode(n.getId());
		}

		public int getNodeCount() {
			int c;

			elementLock.lock();
			c = synchronizedNodes.size();
			elementLock.unlock();

			return c;
		}

		public Iterator<Node> getNodeIterator() {
			return getNodeSet().iterator();
		}

		public Collection<Node> getNodeSet() {
			LinkedList<Node> l;

			elementLock.lock();
			l = new LinkedList<Node>(synchronizedNodes.values());
			elementLock.unlock();

			return l;
		}

		public double getStep() {
			double s;

			elementLock.lock();
			s = wrappedElement.getStep();
			elementLock.unlock();

			return s;
		}

		public boolean isAutoCreationEnabled() {
			return wrappedElement.isAutoCreationEnabled();
		}

		public boolean isStrict() {
			return wrappedElement.isStrict();
		}

		public NodeFactory<? extends Node> nodeFactory() {
			return wrappedElement.nodeFactory();
		}

		public boolean nullAttributesAreErrors() {
			return wrappedElement.nullAttributesAreErrors();
		}

		public void read(String filename) throws IOException,
				GraphParseException, ElementNotFoundException {
			elementLock.lock();
			wrappedElement.read(filename);
			elementLock.unlock();
		}

		public void read(FileSource input, String filename) throws IOException,
				GraphParseException {
			elementLock.lock();
			wrappedElement.read(input, filename);
			elementLock.unlock();
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T removeEdge(String from, String to)
				throws ElementNotFoundException {
			T e;
			Edge se;

			elementLock.lock();
			e = wrappedElement.removeEdge(from, to);
			se = synchronizedEdges.remove(e.getId());
			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T removeEdge(String id)
				throws ElementNotFoundException {
			T e;
			Edge se;

			elementLock.lock();
			e = wrappedElement.removeEdge(id);
			se = synchronizedEdges.remove(e.getId());
			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T removeEdge(int index) {
			T e;
			Edge se;

			elementLock.lock();
			e = wrappedElement.removeEdge(index);
			se = synchronizedEdges.remove(e.getId());
			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T removeEdge(int fromIndex, int toIndex) {
			T e;
			Edge se;

			elementLock.lock();
			e = wrappedElement.removeEdge(fromIndex, toIndex);
			se = synchronizedEdges.remove(e.getId());
			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T removeEdge(Node node1, Node node2) {
			T e;
			Edge se;

			if (node1 instanceof SynchronizedNode)
				node1 = ((SynchronizedNode) node1).wrappedElement;

			if (node2 instanceof SynchronizedNode)
				node2 = ((SynchronizedNode) node1).wrappedElement;

			elementLock.lock();
			e = wrappedElement.removeEdge(node1, node2);
			se = synchronizedEdges.remove(e.getId());
			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Edge> T removeEdge(Edge edge) {
			T e;
			Edge se;

			if (edge instanceof SynchronizedEdge)
				edge = ((SynchronizedEdge) edge).wrappedElement;

			elementLock.lock();
			e = wrappedElement.removeEdge(edge);
			se = synchronizedEdges.remove(e.getId());
			elementLock.unlock();

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		public <T extends Node> T removeNode(String id)
				throws ElementNotFoundException {
			T n;
			Node sn;

			elementLock.lock();
			n = wrappedElement.removeNode(id);
			sn = synchronizedNodes.remove(n.getId());
			elementLock.unlock();

			return (T) sn;
		}

		@SuppressWarnings("unchecked")
		public <T extends Node> T removeNode(int index) {
			T n;
			Node sn;

			elementLock.lock();
			n = wrappedElement.removeNode(index);
			sn = synchronizedNodes.remove(n.getId());
			elementLock.unlock();

			return (T) sn;
		}

		@SuppressWarnings("unchecked")
		public <T extends Node> T removeNode(Node node) {
			T n;
			Node sn;

			if (node instanceof SynchronizedNode)
				node = ((SynchronizedNode) node).wrappedElement;

			elementLock.lock();
			n = wrappedElement.removeNode(node);
			sn = synchronizedNodes.remove(n.getId());
			elementLock.unlock();

			return (T) sn;
		}

		public void setAutoCreate(boolean on) {
			elementLock.lock();
			wrappedElement.setAutoCreate(on);
			elementLock.unlock();
		}

		public void setEdgeFactory(EdgeFactory<? extends Edge> ef) {
			elementLock.lock();
			wrappedElement.setEdgeFactory(ef);
			elementLock.unlock();
		}

		public void setNodeFactory(NodeFactory<? extends Node> nf) {
			elementLock.lock();
			wrappedElement.setNodeFactory(nf);
			elementLock.unlock();
		}

		public void setNullAttributesAreErrors(boolean on) {
			elementLock.lock();
			wrappedElement.setNullAttributesAreErrors(on);
			elementLock.unlock();
		}

		public void setStrict(boolean on) {
			elementLock.lock();
			wrappedElement.setStrict(on);
			elementLock.unlock();
		}

		public void stepBegins(double time) {
			elementLock.lock();
			wrappedElement.stepBegins(time);
			elementLock.unlock();
		}

		public void write(String filename) throws IOException {
			elementLock.lock();
			wrappedElement.write(filename);
			elementLock.unlock();
		}

		public void write(FileSink output, String filename) throws IOException {
			elementLock.lock();
			wrappedElement.write(output, filename);
			elementLock.unlock();
		}

		public void addAttributeSink(AttributeSink sink) {
			elementLock.lock();
			wrappedElement.addAttributeSink(sink);
			elementLock.unlock();
		}

		public void addElementSink(ElementSink sink) {
			elementLock.lock();
			wrappedElement.addElementSink(sink);
			elementLock.unlock();
		}

		public void addSink(Sink sink) {
			elementLock.lock();
			wrappedElement.addSink(sink);
			elementLock.unlock();
		}

		public void clearAttributeSinks() {
			elementLock.lock();
			wrappedElement.clearAttributeSinks();
			elementLock.unlock();
		}

		public void clearElementSinks() {
			elementLock.lock();
			wrappedElement.clearElementSinks();
			elementLock.unlock();
		}

		public void clearSinks() {
			elementLock.lock();
			wrappedElement.clearSinks();
			elementLock.unlock();
		}

		public void removeAttributeSink(AttributeSink sink) {
			elementLock.lock();
			wrappedElement.removeAttributeSink(sink);
			elementLock.unlock();
		}

		public void removeElementSink(ElementSink sink) {
			elementLock.lock();
			wrappedElement.removeElementSink(sink);
			elementLock.unlock();
		}

		public void removeSink(Sink sink) {
			elementLock.lock();
			wrappedElement.removeSink(sink);
			elementLock.unlock();
		}

		public void edgeAttributeAdded(String sourceId, long timeId,
				String edgeId, String attribute, Object value) {
			wrappedElement.edgeAttributeAdded(sourceId, timeId, edgeId,
					attribute, value);
		}

		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			wrappedElement.edgeAttributeChanged(sourceId, timeId, edgeId,
					attribute, oldValue, newValue);
		}

		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			wrappedElement.edgeAttributeRemoved(sourceId, timeId, edgeId,
					attribute);
		}

		public void graphAttributeAdded(String sourceId, long timeId,
				String attribute, Object value) {
			wrappedElement.graphAttributeAdded(sourceId, timeId, attribute,
					value);
		}

		public void graphAttributeChanged(String sourceId, long timeId,
				String attribute, Object oldValue, Object newValue) {
			wrappedElement.graphAttributeChanged(sourceId, timeId, attribute,
					oldValue, newValue);
		}

		public void graphAttributeRemoved(String sourceId, long timeId,
				String attribute) {
			wrappedElement.graphAttributeRemoved(sourceId, timeId, attribute);
		}

		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attribute, Object value) {
			wrappedElement.nodeAttributeAdded(sourceId, timeId, nodeId,
					attribute, value);
		}

		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			wrappedElement.nodeAttributeChanged(sourceId, timeId, nodeId,
					attribute, oldValue, newValue);
		}

		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			wrappedElement.nodeAttributeRemoved(sourceId, timeId, nodeId,
					attribute);
		}

		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			wrappedElement.edgeAdded(sourceId, timeId, edgeId, fromNodeId,
					toNodeId, directed);
		}

		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			wrappedElement.edgeRemoved(sourceId, timeId, edgeId);
		}

		public void graphCleared(String sourceId, long timeId) {
			wrappedElement.graphCleared(sourceId, timeId);
		}

		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			wrappedElement.nodeAdded(sourceId, timeId, nodeId);
		}

		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			wrappedElement.nodeRemoved(sourceId, timeId, nodeId);
		}

		public void stepBegins(String sourceId, long timeId, double step) {
			wrappedElement.stepBegins(sourceId, timeId, step);
		}

		public Iterator<Node> iterator() {
			return getEachNode().iterator();
		}
	}

	static class SynchronizedNode extends SynchronizedElement<Node> implements
			Node {

		private final SynchronizedGraph sg;
		private final ReentrantLock elementLock;

		SynchronizedNode(SynchronizedGraph sg, Node n) {
			super(n);

			this.sg = sg;
			this.elementLock = new ReentrantLock();
		}

		public Iterator<Node> getBreadthFirstIterator() {
			return getBreadthFirstIterator(false);
		}

		public Iterator<Node> getBreadthFirstIterator(boolean directed) {
			LinkedList<Node> l = new LinkedList<Node>();
			Iterator<Node> it;

			elementLock.lock();
			sg.elementLock.lock();

			it = wrappedElement.getBreadthFirstIterator(directed);

			while (it.hasNext())
				l.add(sg.getNode(it.next().getIndex()));

			sg.elementLock.unlock();
			elementLock.unlock();

			return l.iterator();
		}

		public int getDegree() {
			int d;

			elementLock.lock();
			d = wrappedElement.getDegree();
			elementLock.unlock();

			return d;
		}

		public Iterator<Node> getDepthFirstIterator() {
			return getDepthFirstIterator(false);
		}

		public Iterator<Node> getDepthFirstIterator(boolean directed) {
			LinkedList<Node> l = new LinkedList<Node>();
			Iterator<Node> it;

			elementLock.lock();
			sg.elementLock.lock();

			it = wrappedElement.getDepthFirstIterator();

			while (it.hasNext())
				l.add(sg.getNode(it.next().getIndex()));

			sg.elementLock.unlock();
			elementLock.unlock();

			return l.iterator();
		}

		public Iterable<Edge> getEachEdge() {
			return getEdgeSet();
		}

		public Iterable<Edge> getEachEnteringEdge() {
			return getEnteringEdgeSet();
		}

		public Iterable<Edge> getEachLeavingEdge() {
			return getLeavingEdgeSet();
		}

		public <T extends Edge> T getEdge(int i) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getEdge(i).getIndex());
			elementLock.unlock();

			return e;
		}

		public <T extends Edge> T getEnteringEdge(int i) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getEnteringEdge(i).getIndex());
			elementLock.unlock();

			return e;
		}

		public <T extends Edge> T getLeavingEdge(int i) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getLeavingEdge(i).getIndex());
			elementLock.unlock();

			return e;
		}

		public <T extends Edge> T getEdgeBetween(String id) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getEdgeBetween(id).getIndex());
			elementLock.unlock();

			return e;
		}

		public <T extends Edge> T getEdgeBetween(Node n) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getEdgeBetween(n).getIndex());
			elementLock.unlock();

			return e;
		}

		public <T extends Edge> T getEdgeBetween(int index) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getEdgeBetween(index).getIndex());
			elementLock.unlock();

			return e;
		}

		public <T extends Edge> T getEdgeFrom(String id) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getEdgeFrom(id).getIndex());
			elementLock.unlock();

			return e;
		}

		public <T extends Edge> T getEdgeFrom(Node n) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getEdgeFrom(n).getIndex());
			elementLock.unlock();

			return e;
		}

		public <T extends Edge> T getEdgeFrom(int index) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getEdgeFrom(index).getIndex());
			elementLock.unlock();

			return e;
		}

		public Iterator<Edge> getEdgeIterator() {
			return getEdgeSet().iterator();
		}

		public Collection<Edge> getEdgeSet() {
			ArrayList<Edge> l;
			Iterator<Edge> it;

			elementLock.lock();

			l = new ArrayList<Edge>(wrappedElement.getDegree());
			it = wrappedElement.getEachEdge().iterator();

			while (it.hasNext())
				l.add(sg.getEdge(it.next().getIndex()));

			elementLock.unlock();

			return l;
		}

		public <T extends Edge> T getEdgeToward(String id) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getEdgeToward(id).getIndex());
			elementLock.unlock();

			return e;
		}

		public <T extends Edge> T getEdgeToward(Node n) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getEdgeToward(n).getIndex());
			elementLock.unlock();

			return e;
		}

		public <T extends Edge> T getEdgeToward(int index) {
			T e;

			elementLock.lock();
			e = sg.getEdge(wrappedElement.getEdgeToward(index).getIndex());
			elementLock.unlock();

			return e;
		}

		public Iterator<Edge> getEnteringEdgeIterator() {
			return getEnteringEdgeSet().iterator();
		}

		public Collection<Edge> getEnteringEdgeSet() {
			ArrayList<Edge> l;
			Iterator<Edge> it;

			elementLock.lock();
			sg.elementLock.lock();

			l = new ArrayList<Edge>(wrappedElement.getInDegree());
			it = wrappedElement.getEachEnteringEdge().iterator();

			while (it.hasNext())
				l.add(sg.getEdge(it.next().getIndex()));

			sg.elementLock.unlock();
			elementLock.unlock();

			return l;
		}

		public Graph getGraph() {
			return sg;
		}

		public int getInDegree() {
			int d;

			elementLock.lock();
			d = wrappedElement.getInDegree();
			elementLock.unlock();

			return d;
		}

		public Iterator<Edge> getLeavingEdgeIterator() {
			return getLeavingEdgeSet().iterator();
		}

		public Collection<Edge> getLeavingEdgeSet() {
			ArrayList<Edge> l;
			Iterator<Edge> it;

			elementLock.lock();
			sg.elementLock.lock();

			l = new ArrayList<Edge>(wrappedElement.getOutDegree());
			it = wrappedElement.<Edge> getEachLeavingEdge().iterator();

			while (it.hasNext())
				l.add(sg.getEdge(it.next().getIndex()));

			sg.elementLock.unlock();
			elementLock.unlock();

			return l;
		}

		public Iterator<Node> getNeighborNodeIterator() {
			ArrayList<Node> l;
			Iterator<Node> it;

			elementLock.lock();
			sg.elementLock.lock();

			l = new ArrayList<Node>(wrappedElement.getDegree());
			it = wrappedElement.getNeighborNodeIterator();

			while (it.hasNext())
				l.add(sg.getNode(it.next().getIndex()));

			sg.elementLock.unlock();
			elementLock.unlock();

			return l.iterator();
		}

		public int getOutDegree() {
			int d;

			elementLock.lock();
			d = wrappedElement.getOutDegree();
			elementLock.unlock();

			return d;
		}

		public boolean hasEdgeBetween(String id) {
			boolean b;

			elementLock.lock();
			b = wrappedElement.hasEdgeBetween(id);
			elementLock.unlock();

			return b;
		}

		public boolean hasEdgeBetween(Node node) {
			boolean b;

			elementLock.lock();
			b = wrappedElement.hasEdgeBetween(node);
			elementLock.unlock();

			return b;
		}

		public boolean hasEdgeBetween(int index) {
			boolean b;

			elementLock.lock();
			b = wrappedElement.hasEdgeBetween(index);
			elementLock.unlock();

			return b;
		}

		public boolean hasEdgeFrom(String id) {
			boolean b;

			elementLock.lock();
			b = wrappedElement.hasEdgeFrom(id);
			elementLock.unlock();

			return b;
		}

		public boolean hasEdgeFrom(Node node) {
			boolean b;

			elementLock.lock();
			b = wrappedElement.hasEdgeFrom(node);
			elementLock.unlock();

			return b;
		}

		public boolean hasEdgeFrom(int index) {
			boolean b;

			elementLock.lock();
			b = wrappedElement.hasEdgeFrom(index);
			elementLock.unlock();

			return b;
		}

		public boolean hasEdgeToward(String id) {
			boolean b;

			elementLock.lock();
			b = wrappedElement.hasEdgeToward(id);
			elementLock.unlock();

			return b;
		}

		public boolean hasEdgeToward(Node node) {
			boolean b;

			elementLock.lock();
			b = wrappedElement.hasEdgeToward(node);
			elementLock.unlock();

			return b;
		}

		public boolean hasEdgeToward(int index) {
			boolean b;

			elementLock.lock();
			b = wrappedElement.hasEdgeToward(index);
			elementLock.unlock();

			return b;
		}

		public Iterator<Edge> iterator() {
			return getEdgeSet().iterator();
		}
	}

	static class SynchronizedEdge extends SynchronizedElement<Edge> implements
			Edge {

		final SynchronizedGraph sg;

		SynchronizedEdge(SynchronizedGraph sg, Edge e) {
			super(e);
			this.sg = sg;
		}

		public <T extends Node> T getNode0() {
			T n;

			sg.elementLock.lock();
			n = sg.getNode(wrappedElement.getNode0().getIndex());
			sg.elementLock.unlock();

			return n;
		}

		public <T extends Node> T getNode1() {
			T n;

			sg.elementLock.lock();
			n = sg.getNode(wrappedElement.getNode1().getIndex());
			sg.elementLock.unlock();

			return n;
		}

		public <T extends Node> T getOpposite(Node node) {
			T n;

			if (node instanceof SynchronizedNode)
				node = ((SynchronizedNode) node).wrappedElement;

			sg.elementLock.lock();
			n = sg.getNode(wrappedElement.getOpposite(node).getIndex());
			sg.elementLock.unlock();

			return n;
		}

		public <T extends Node> T getSourceNode() {
			T n;

			sg.elementLock.lock();
			n = sg.getNode(wrappedElement.getSourceNode().getIndex());
			sg.elementLock.unlock();

			return n;
		}

		public <T extends Node> T getTargetNode() {
			T n;

			sg.elementLock.lock();
			n = sg.getNode(wrappedElement.getTargetNode().getIndex());
			sg.elementLock.unlock();

			return n;
		}

		public boolean isDirected() {
			return wrappedElement.isDirected();
		}

		public boolean isLoop() {
			return wrappedElement.isLoop();
		}
	}
}
