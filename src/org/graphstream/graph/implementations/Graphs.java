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
package org.graphstream.graph.implementations;

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
import org.graphstream.ui.view.Viewer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.DepthFirstIterator;

public class Graphs {

	private static final Logger logger = Logger.getLogger(Graphs.class.getSimpleName());

	/**
	 * Synchronizes a graph. The returned graph can be accessed and modified
	 * by several threads. You lose genericity in methods returning edge or
	 * node because each element (graph, nodes and edges) is wrapped into a
	 * synchronized wrapper which breaks original elements class.
	 *
	 * @param g the graph to synchronize
	 * @return a synchronized wrapper for g
	 */
	public static Graph<Node, Edge> synchronizedGraph(Graph<? extends Node, ? extends Edge> g) {
		return new SynchronizedGraph((Graph<Node, Edge>) g);
	}

	/**
	 * Merge several graphs in one. A new graph is created, that will
	 * contain the result. The method will try to create a graph of the same
	 * class that the first graph to merge (it needs to have a constructor
	 * with a String). Else, a MultiGraph is used.
	 *
	 * @param graphs graphs to merge
	 * @return merge result
	 */
	public static Graph merge(Graph... graphs) {
		if (graphs == null) {
			return new SingleGraph("void-merge");
		}

		String id = "merge";

		for (Graph g : graphs) {
			id += "-" + g.getId();
		}

		Graph result;

		try {
			Class<? extends Graph> cls = graphs[0].getClass();
			result = cls.getConstructor(String.class).newInstance(id);
		} catch (Exception e) {
			logger.warning(String.format("Cannot create a graph of %s.", graphs[0].getClass().getName()));
			result = new MultiGraph(id);
		}

		mergeIn(result, graphs);

		return result;
	}

	/**
	 * Merge several graphs in one. The first parameter is the graph in
	 * which the other graphs will be merged.
	 *
	 * @param result destination graph.
	 * @param graphs all graphs that will be merged in result.
	 */
	public static void mergeIn(Graph result, Graph... graphs) {
		boolean strict = result.isStrict();
		GraphReplay replay = new GraphReplay(String.format("replay-%x",
			System.nanoTime()));

		replay.addSink(result);
		result.setStrict(false);

		if (graphs != null) {
			for (Graph g : graphs) {
				replay.replay(g);
			}
		}

		replay.removeSink(result);
		result.setStrict(strict);
	}

	/**
	 * Clone a given graph with same node/edge structure and same
	 * attributes.
	 *
	 * @param g the graph to clone
	 * @return a copy of g
	 */
	public static Graph clone(Graph g) {
		Graph copy;

		try {
			Class<? extends Graph> cls = g.getClass();
			copy = cls.getConstructor(String.class).newInstance(g.getId());
		} catch (Exception e) {
			logger.warning(String.format("Cannot create a graph of %s.", g.getClass().getName()));
			copy = new SimpleAdjacencyListGraph(g.getId());
		}

		copyAttributes(g, copy);

		for (int i = 0; i < g.getNodeCount(); i++) {
			Node source = g.getNode(i);
			Node target = copy.addNode(source.getId());

			copyAttributes(source, target);
		}

		for (int i = 0; i < g.getEdgeCount(); i++) {
			Edge source = g.getEdge(i);
			Edge target = copy.addEdge(source.getId(), source.getSourceNode()
				.getId(), source.getTargetNode().getId(), source
				.isDirected());

			copyAttributes(source, target);
		}

		return copy;
	}

	/**
	 *
	 * @param source
	 * @param target
	 */
	public static void copyAttributes(Element source, Element target) {
		for (String key : source.getAttributeKeySet()) {
			Object value = source.getAttribute(key);
			value = checkedArrayOrCollectionCopy(value);

			target.setAttribute(key, value);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static Object checkedArrayOrCollectionCopy(Object o) {
		if (o == null) {
			return null;
		}

		if (o.getClass().isArray()) {

			Object c = Array.newInstance(o.getClass().getComponentType(),
				Array.getLength(o));

			for (int i = 0; i < Array.getLength(o); i++) {
				Object t = checkedArrayOrCollectionCopy(Array.get(o, i));
				Array.set(c, i, t);
			}

			return c;
		}

		if (Collection.class.isAssignableFrom(o.getClass())) {
			Collection<?> t;

			try {
				t = (Collection<?>) o.getClass().newInstance();
				t.addAll((Collection) o);

				return t;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return o;
	}

	static class SynchronizedElement<U extends Element> implements Element {

		private final ReentrantLock elementLock;
		protected final U wrappedElement;

		SynchronizedElement(U e) {
			this.wrappedElement = e;
			this.elementLock = new ReentrantLock();
		}

		@Override
		public void setAttributes(Map<String, Object> attributes) {
			elementLock.lock();
			try {
				wrappedElement.setAttributes(attributes);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void clearAttributes() {
			elementLock.lock();
			try {
				wrappedElement.clearAttributes();
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public Object[] getArray(String key) {
			Object[] o;

			elementLock.lock();
			try {
				o = wrappedElement.getArray(key);
			} finally {
				elementLock.unlock();
			}

			return o;
		}

		@Override
		public <T> T getAttribute(String key) {
			T o;

			elementLock.lock();
			try {
				o = wrappedElement.getAttribute(key);
			} finally {
				elementLock.unlock();
			}

			return o;
		}

		@Override
		public <T> T getAttribute(Class<T> clazz, String key) {
			T o;

			elementLock.lock();
			try {
				o = wrappedElement.getAttribute(clazz, key);
			} finally {
				elementLock.unlock();
			}

			return o;
		}

		@Override
		public int getAttributeCount() {
			int c;

			elementLock.lock();
			try {
				c = wrappedElement.getAttributeCount();
			} finally {
				elementLock.unlock();
			}

			return c;
		}

		@Override
		public Iterator<String> getAttributeKeyIterator() {
			return getAttributeKeySet().iterator();
		}

		@Override
		public Collection<String> getAttributeKeySet() {
			ArrayList<String> o;
			Iterator<String> it;

			elementLock.lock();
			try {
				o = new ArrayList<>(wrappedElement.getAttributeKeySet());
				it = wrappedElement.getAttributeKeyIterator();
				
				while (it.hasNext()) {
					o.add(it.next());
				}
			} finally {
				elementLock.unlock();
			}

			return o;
		}

		@Override
		public Iterable<String> getEachAttributeKey() {
			return getAttributeKeySet();
		}

		@Override
		public <T> T getFirstAttributeOf(String... keys) {
			T o;

			elementLock.lock();
			try {
				o = wrappedElement.getFirstAttributeOf(keys);
			} finally {
				elementLock.unlock();
			}

			return o;
		}

		@Override
		public <T> T getFirstAttributeOf(Class<T> clazz, String... keys) {
			T o;

			elementLock.lock();
			try {
				o = wrappedElement.getFirstAttributeOf(clazz, keys);
			} finally {
				elementLock.unlock();
			}

			return o;
		}

		@Override
		public Map<?, ?> getMap(String key) {
			Map<?, ?> o;

			elementLock.lock();
			try {
				o = wrappedElement.getMap(key);
			} finally {
				elementLock.unlock();
			}

			return o;
		}

		@Override
		public String getId() {
			return wrappedElement.getId();
		}

		@Override
		public int getIndex() {
			return wrappedElement.getIndex();
		}

		@Override
		public String getLabel(String key) {
			String o;

			elementLock.lock();
			try {
				o = wrappedElement.getLabel(key);
			} finally {
				elementLock.unlock();
			}

			return o;
		}

		@Override
		public Number getNumber(String key) {
			Number n;

			elementLock.lock();
			try {
				n = wrappedElement.getNumber(key);
			} finally {
				elementLock.unlock();
			}

			return n;
		}

		@Override
		public boolean hasArray(String key) {
			boolean b;

			elementLock.lock();
			try {
				b = wrappedElement.hasArray(key);
			} finally {
				elementLock.unlock();
			}

			return b;
		}

		@Override
		public boolean hasAttribute(String key) {
			boolean b;

			elementLock.lock();
			try {
				b = wrappedElement.hasAttribute(key);
			} finally {
				elementLock.unlock();
			}

			return b;
		}

		@Override
		public boolean hasAttribute(String key, Class<?> clazz) {
			boolean b;

			elementLock.lock();
			try {
				b = wrappedElement.hasAttribute(key, clazz);
			} finally {
				elementLock.unlock();
			}

			return b;
		}

		@Override
		public boolean hasMap(String key) {
			boolean b;

			elementLock.lock();
			try {
				b = wrappedElement.hasMap(key);
			} finally {
				elementLock.unlock();
			}

			return b;
		}

		@Override
		public boolean hasLabel(String key) {
			boolean b;

			elementLock.lock();
			try {
				b = wrappedElement.hasLabel(key);
			} finally {
				elementLock.unlock();
			}

			return b;
		}

		@Override
		public boolean hasNumber(String key) {
			boolean b;

			elementLock.lock();
			try {
				b = wrappedElement.hasNumber(key);
			} finally {
				elementLock.unlock();
			}

			return b;
		}

		@Override
		public boolean removeAttribute(String attribute) {
			elementLock.lock();
			try {
				return wrappedElement.removeAttribute(attribute);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void setAttribute(String attribute, Object... values) {
			elementLock.lock();
			try {
				wrappedElement.setAttribute(attribute, values);
			} finally {
				elementLock.unlock();
			}
		}
	}

	static class SynchronizedGraph extends SynchronizedElement<Graph<Node, Edge>> implements
		Graph<Node, Edge> {

		final ReentrantLock elementLock;
		final HashMap<String, Node> synchronizedNodes;
		final HashMap<String, Edge> synchronizedEdges;

		SynchronizedGraph(Graph<Node, Edge> g) {
			super(g);

			elementLock = new ReentrantLock();
			synchronizedNodes = new HashMap<>();
			synchronizedEdges = new HashMap<>();

			for (Node n : g.getEachNode()) {
				synchronizedNodes.put(n.getId(), new SynchronizedNode(this, n));
			}

			for (Edge e : g.getEachEdge()) {
				synchronizedEdges.put(e.getId(), new SynchronizedEdge(this, e));
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public Edge addEdge(String id, String node1, String node2)
			throws IdAlreadyInUseException, ElementNotFoundException,
			EdgeRejectedException {
			Edge e;
			Edge se;

			elementLock.lock();
			try {
				e = wrappedElement.addEdge(id, node1, node2);
				se = new SynchronizedEdge(this, e);
				synchronizedEdges.put(id, se);
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Edge addEdge(String id, String from, String to,
			boolean directed) throws IdAlreadyInUseException,
			ElementNotFoundException {
			Edge e;
			Edge se;

			elementLock.lock();
			try {
				e = wrappedElement.addEdge(id, from, to, directed);
				se = new SynchronizedEdge(this, e);
				synchronizedEdges.put(id, se);
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Edge addEdge(String id, int index1, int index2) {
			Edge e;
			Edge se;

			elementLock.lock();
			try {
				e = wrappedElement.addEdge(id, index1, index2);
				se = new SynchronizedEdge(this, e);
				synchronizedEdges.put(id, se);
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Edge addEdge(String id, int fromIndex,
			int toIndex, boolean directed) {
			Edge e;
			Edge se;

			elementLock.lock();
			try {
				e = wrappedElement.addEdge(id, fromIndex, toIndex, directed);
				se = new SynchronizedEdge(this, e);
				synchronizedEdges.put(id, se);
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Edge addEdge(String id, Node node1, Node node2) {
			Edge e;
			Edge se;
			final Node unsyncNode1, unsyncNode2;

			unsyncNode1 = ((SynchronizedElement<Node>) node1).wrappedElement;
			unsyncNode2 = ((SynchronizedElement<Node>) node2).wrappedElement;

			elementLock.lock();
			try {
				e = wrappedElement.addEdge(id, unsyncNode1, unsyncNode2);
				se = new SynchronizedEdge(this, e);
				synchronizedEdges.put(id, se);
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Edge addEdge(String id, Node from, Node to,
			boolean directed) {
			Edge e;
			Edge se;
			final Node unsyncFrom, unsyncTo;

			unsyncFrom = ((SynchronizedElement<Node>) from).wrappedElement;
			unsyncTo = ((SynchronizedElement<Node>) to).wrappedElement;

			elementLock.lock();
			try {
				e = wrappedElement.addEdge(id, unsyncFrom, unsyncTo, directed);
				se = new SynchronizedEdge(this, e);
				synchronizedEdges.put(id, se);
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Node addNode(String id)
			throws IdAlreadyInUseException {
			Node n;
			Node sn;

			elementLock.lock();
			try {
				n = wrappedElement.addNode(id);
				sn = new SynchronizedNode(this, n);
				synchronizedNodes.put(id, sn);
			} finally {
				elementLock.unlock();
			}

			return sn;
		}

		@Override
		public Iterable<AttributeSink> attributeSinks() {
			LinkedList<AttributeSink> sinks = new LinkedList<>();

			elementLock.lock();
			try {
				for (AttributeSink as : wrappedElement.attributeSinks()) {
					sinks.add(as);
				}
			} finally {
				elementLock.unlock();
			}

			return sinks;
		}

		@Override
		public void clear() {
			elementLock.lock();
			try {
				wrappedElement.clear();
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public Viewer display() {
			return wrappedElement.display();
		}

		@Override
		public Viewer display(boolean autoLayout) {
			return wrappedElement.display(autoLayout);
		}

		@Override
		public EdgeFactory<? extends Edge> edgeFactory() {
			return wrappedElement.edgeFactory();
		}

		@Override
		public Iterable<ElementSink> elementSinks() {
			LinkedList<ElementSink> sinks = new LinkedList<>();

			elementLock.lock();
			try {
				for (ElementSink es : wrappedElement.elementSinks()) {
					sinks.add(es);
				}
			} finally {
				elementLock.unlock();
			}

			return sinks;
		}

		@Override
		public Iterable<Edge> getEachEdge() {
			LinkedList<Edge> edges;

			elementLock.lock();
			try {
				edges = new LinkedList<>(synchronizedEdges.values());
			} finally {
				elementLock.unlock();
			}

			return edges;
		}

		@Override
		public Iterable<Node> getEachNode() {
			LinkedList<Node> nodes;

			elementLock.lock();
			try {
				nodes = new LinkedList<>(synchronizedNodes.values());
			} finally {
				elementLock.unlock();
			}

			return nodes;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends Edge> T getEdge(String id) {
			T e;

			elementLock.lock();
			try {
				e = (T) synchronizedEdges.get(id);
			} finally {
				elementLock.unlock();
			}

			return e;
		}

		@Override
		public <T extends Edge> T getEdge(int index)
			throws IndexOutOfBoundsException {
			Edge e;

			elementLock.lock();
			try {
				e = wrappedElement.getEdge(index);
			} finally {
				elementLock.unlock();
			}

			return e == null ? null : this.<T>getEdge(e.getId());
		}

		@Override
		public int getEdgeCount() {
			int c;

			elementLock.lock();
			try {
				c = synchronizedEdges.size();
			} finally {
				elementLock.unlock();
			}

			return c;
		}

		@Override
		public Iterator<Edge> getEdgeIterator() {
			return getEdgeSet().iterator();
		}

		@Override
		public Collection<Edge> getEdgeSet() {
			LinkedList<Edge> l;

			elementLock.lock();
			try {
				l = new LinkedList<Edge>(synchronizedEdges.values());
			} finally {
				elementLock.unlock();
			}

			return l;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends Node> T getNode(String id) {
			T n;

			elementLock.lock();
			try {
				n = (T) synchronizedNodes.get(id);
			} finally {
				elementLock.unlock();
			}

			return n;
		}

		@Override
		public <T extends Node> T getNode(int index)
			throws IndexOutOfBoundsException {
			Node n;

			elementLock.lock();
			try {
				n = wrappedElement.getNode(index);
			} finally {
				elementLock.unlock();
			}

			return n == null ? null : this.<T>getNode(n.getId());
		}

		@Override
		public int getNodeCount() {
			int c;

			elementLock.lock();
			try {
				c = synchronizedNodes.size();
			} finally {
				elementLock.unlock();
			}

			return c;
		}

		@Override
		public Iterator<Node> getNodeIterator() {
			return getNodeSet().iterator();
		}

		@Override
		public Collection<Node> getNodeSet() {
			LinkedList<Node> l;

			elementLock.lock();
			try {
				l = new LinkedList<Node>(synchronizedNodes.values());
			} finally {
				elementLock.unlock();
			}

			return l;
		}

		@Override
		public double getStep() {
			double s;

			elementLock.lock();
			try {
				s = wrappedElement.getStep();
			} finally {
				elementLock.unlock();
			}

			return s;
		}

		@Override
		public boolean isAutoCreationEnabled() {
			return wrappedElement.isAutoCreationEnabled();
		}

		@Override
		public boolean isStrict() {
			return wrappedElement.isStrict();
		}

		@Override
		public NodeFactory<? extends Node> nodeFactory() {
			return wrappedElement.nodeFactory();
		}

		@Override
		public boolean nullAttributesAreErrors() {
			return wrappedElement.nullAttributesAreErrors();
		}

		@Override
		public void read(String filename) throws IOException,
			GraphParseException, ElementNotFoundException {
			elementLock.lock();
			try {
				wrappedElement.read(filename);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void read(FileSource input, String filename) throws IOException,
			GraphParseException {
			elementLock.lock();
			try {
				wrappedElement.read(input, filename);
			} finally {
				elementLock.unlock();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends Edge> T removeEdge(String from, String to)
			throws ElementNotFoundException {
			T e;
			Edge se;

			elementLock.lock();
			try {
				e = wrappedElement.removeEdge(from, to);
				se = synchronizedEdges.remove(e.getId());
			} finally {
				elementLock.unlock();
			}

			return (T) se;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Edge removeEdge(String id)
			throws ElementNotFoundException {
			Edge e;
			Edge se;

			elementLock.lock();
			try {
				e = wrappedElement.removeEdge(id);
				se = synchronizedEdges.remove(e.getId());
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Edge removeEdge(int index) {
			Edge e;
			Edge se;

			elementLock.lock();
			try {
				e = wrappedElement.removeEdge(index);
				se = synchronizedEdges.remove(e.getId());
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Edge removeEdge(int fromIndex, int toIndex) {
			Edge e;
			Edge se;

			elementLock.lock();
			try {
				e = wrappedElement.removeEdge(fromIndex, toIndex);
				se = synchronizedEdges.remove(e.getId());
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Edge removeEdge(Node node1, Node node2) {
			Edge e;
			Edge se;

			if (node1 instanceof SynchronizedNode) {
				node1 = ((SynchronizedNode) node1).wrappedElement;
			}

			if (node2 instanceof SynchronizedNode) {
				node2 = ((SynchronizedNode) node1).wrappedElement;
			}

			elementLock.lock();
			try {
				e = wrappedElement.removeEdge(node1, node2);
				se = synchronizedEdges.remove(e.getId());
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Edge removeEdge(Edge edge) {
			Edge e;
			Edge se;

			if (edge instanceof SynchronizedEdge) {
				edge = ((SynchronizedEdge) edge).wrappedElement;
			}

			elementLock.lock();
			try {
				e = wrappedElement.removeEdge(edge);
				se = synchronizedEdges.remove(e.getId());
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Node removeNode(String id)
			throws ElementNotFoundException {
			Node n;
			Node sn;

			elementLock.lock();
			try {
				n = wrappedElement.removeNode(id);
				sn = synchronizedNodes.remove(n.getId());
			} finally {
				elementLock.unlock();
			}

			return sn;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Node removeNode(int index) {
			Node n;
			Node sn;

			elementLock.lock();
			try {
				n = wrappedElement.removeNode(index);
				sn = synchronizedNodes.remove(n.getId());
			} finally {
				elementLock.unlock();
			}

			return sn;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Node removeNode(Node node) {
			Node n;
			Node sn;

			if (node instanceof SynchronizedNode) {
				node = ((SynchronizedNode) node).wrappedElement;
			}

			elementLock.lock();
			try {
				n = wrappedElement.removeNode(node);
				sn = synchronizedNodes.remove(n.getId());
			} finally {
				elementLock.unlock();
			}

			return sn;
		}

		@Override
		public void setAutoCreate(boolean on) {
			elementLock.lock();
			try {
				wrappedElement.setAutoCreate(on);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void setEdgeFactory(EdgeFactory<? extends Edge> ef) {
			elementLock.lock();
			try {
				wrappedElement.setEdgeFactory(ef);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void setNodeFactory(NodeFactory<? extends Node> nf) {
			elementLock.lock();
			try {
				wrappedElement.setNodeFactory(nf);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void setNullAttributesAreErrors(boolean on) {
			elementLock.lock();
			try {
				wrappedElement.setNullAttributesAreErrors(on);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void setStrict(boolean on) {
			elementLock.lock();
			try {
				wrappedElement.setStrict(on);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void stepBegins(double time) {
			elementLock.lock();
			try {
				wrappedElement.stepBegins(time);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void write(String filename) throws IOException {
			elementLock.lock();
			try {
				wrappedElement.write(filename);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void write(FileSink output, String filename) throws IOException {
			elementLock.lock();
			try {
				wrappedElement.write(output, filename);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void addAttributeSink(AttributeSink sink) {
			elementLock.lock();
			try {
				wrappedElement.addAttributeSink(sink);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void addElementSink(ElementSink sink) {
			elementLock.lock();
			try {
				wrappedElement.addElementSink(sink);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void addSink(Sink sink) {
			elementLock.lock();
			try {
				wrappedElement.addSink(sink);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void clearAttributeSinks() {
			elementLock.lock();
			try {
				wrappedElement.clearAttributeSinks();
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void clearElementSinks() {
			elementLock.lock();
			try {
				wrappedElement.clearElementSinks();
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void clearSinks() {
			elementLock.lock();
			try {
				wrappedElement.clearSinks();
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void removeAttributeSink(AttributeSink sink) {
			elementLock.lock();
			try {
				wrappedElement.removeAttributeSink(sink);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void removeElementSink(ElementSink sink) {
			elementLock.lock();
			try {
				wrappedElement.removeElementSink(sink);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void removeSink(Sink sink) {
			elementLock.lock();
			try {
				wrappedElement.removeSink(sink);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void edgeAttributeAdded(String sourceId, long timeId,
			String edgeId, String attribute, Object value) {
			wrappedElement.edgeAttributeAdded(sourceId, timeId, edgeId,
				attribute, value);
		}

		@Override
		public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue,
			Object newValue) {
			wrappedElement.edgeAttributeChanged(sourceId, timeId, edgeId,
				attribute, oldValue, newValue);
		}

		@Override
		public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
			wrappedElement.edgeAttributeRemoved(sourceId, timeId, edgeId,
				attribute);
		}

		@Override
		public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
			wrappedElement.graphAttributeAdded(sourceId, timeId, attribute,
				value);
		}

		@Override
		public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
			wrappedElement.graphAttributeChanged(sourceId, timeId, attribute,
				oldValue, newValue);
		}

		@Override
		public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
			wrappedElement.graphAttributeRemoved(sourceId, timeId, attribute);
		}

		@Override
		public void nodeAttributeAdded(String sourceId, long timeId,
			String nodeId, String attribute, Object value) {
			wrappedElement.nodeAttributeAdded(sourceId, timeId, nodeId,
				attribute, value);
		}

		@Override
		public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue,
			Object newValue) {
			wrappedElement.nodeAttributeChanged(sourceId, timeId, nodeId,
				attribute, oldValue, newValue);
		}

		@Override
		public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
			wrappedElement.nodeAttributeRemoved(sourceId, timeId, nodeId,
				attribute);
		}

		@Override
		public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
			wrappedElement.edgeAdded(sourceId, timeId, edgeId, fromNodeId,
				toNodeId, directed);
		}

		@Override
		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			wrappedElement.edgeRemoved(sourceId, timeId, edgeId);
		}

		@Override
		public void graphCleared(String sourceId, long timeId) {
			wrappedElement.graphCleared(sourceId, timeId);
		}

		@Override
		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			wrappedElement.nodeAdded(sourceId, timeId, nodeId);
		}

		@Override
		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			wrappedElement.nodeRemoved(sourceId, timeId, nodeId);
		}

		@Override
		public void stepBegins(String sourceId, long timeId, double step) {
			wrappedElement.stepBegins(sourceId, timeId, step);
		}

		@Override
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

		@Override
		public <T extends Node> BreadthFirstIterator<T> getBreadthFirstIterator(boolean directed) {
			// Need a Synchronized version
			throw new UnsupportedOperationException("Not supported yet.");
			/*
			LinkedList<Node> l = new LinkedList<>();
			Iterator<Node> it;

			elementLock.lock();
			sg.elementLock.lock();
			try {
				it = wrappedElement.getBreadthFirstIterator(directed);
				
				while (it.hasNext()) {
					l.add(sg.getNode(it.next().getIndex()));
				}
				
			} finally {
				elementLock.unlock();
				sg.elementLock.unlock();
			}

			return l.iterator();
			*/
		}

		@Override
		public int getDegree() {
			int d;

			elementLock.lock();
			try {
				d = wrappedElement.getDegree();
			} finally {
				elementLock.unlock();
			}

			return d;
		}

		@Override
		public <T extends Node> DepthFirstIterator<T> getDepthFirstIterator(boolean directed) {
			// Need a Synchronized version
			throw new UnsupportedOperationException("Not supported yet.");
			/*
			LinkedList<Node> l = new LinkedList<>();
			Iterator<Node> it;

			elementLock.lock();
			try {
				sg.elementLock.lock();
				
				it = wrappedElement.getDepthFirstIterator();
				
				while (it.hasNext()) {
					l.add(sg.getNode(it.next().getIndex()));
				}
				
				sg.elementLock.unlock();
			} finally {
				elementLock.unlock();
			}

			return l.iterator();
			*/
		}

		@Override
		public Iterable<Edge> getEachEdge() {
			return getEdgeSet();
		}

		@Override
		public Iterable<Edge> getEachEnteringEdge() {
			return getEnteringEdgeSet();
		}

		@Override
		public Iterable<Edge> getEachLeavingEdge() {
			return getLeavingEdgeSet();
		}

		@Override
		public Edge getEdge(int i) {
			Edge e;

			elementLock.lock();
			try {
				e = sg.getEdge(wrappedElement.getEdge(i).getIndex());
			} finally {
				elementLock.unlock();
			}

			return e;
		}

		@Override
		public Edge getEnteringEdge(int i) {
			Edge e;

			elementLock.lock();
			try {
				e = sg.getEdge(wrappedElement.getEnteringEdge(i).getIndex());
			} finally {
				elementLock.unlock();
			}

			return e;
		}

		@Override
		public Edge getLeavingEdge(int i) {
			Edge e;

			elementLock.lock();
			try {
				e = sg.getEdge(wrappedElement.getLeavingEdge(i).getIndex());
			} finally {
				elementLock.unlock();
			}

			return e;
		}

		@Override
		public Edge getEdgeBetween(Node n) {
			Edge e;

			elementLock.lock();
			try {
				e = sg.getEdge(wrappedElement.getEdgeBetween(n).getIndex());
			} finally {
				elementLock.unlock();
			}

			return e;
		}

		@Override
		public Edge getEdgeFrom(Node n) {
			Edge e;

			elementLock.lock();
			try {
				e = sg.getEdge(wrappedElement.getEdgeFrom(n).getIndex());
			} finally {
				elementLock.unlock();
			}

			return e;
		}

		@Override
		public Iterator<Edge> getEdgeIterator() {
			return getEdgeSet().iterator();
		}

		@Override
		public Collection<Edge> getEdgeSet() {
			ArrayList<Edge> l;
			Iterator<Edge> it;

			elementLock.lock();
			try {
				l = new ArrayList<>(wrappedElement.getDegree());
				it = wrappedElement.getEachEdge().iterator();
				
				while (it.hasNext()) {
					l.add(sg.getEdge(it.next().getIndex()));
				}
			} finally {
				elementLock.unlock();
			}

			return l;
		}

		@Override
		public Edge getEdgeToward(Node n) {
			Edge e;

			elementLock.lock();
			try {
				e = sg.getEdge(wrappedElement.getEdgeToward(n).getIndex());
			} finally {
				elementLock.unlock();
			}

			return e;
		}

		@Override
		public Iterator<Edge> getEnteringEdgeIterator() {
			return getEnteringEdgeSet().iterator();
		}

		@Override
		public Collection<Edge> getEnteringEdgeSet() {
			ArrayList<Edge> l;
			Iterator<Edge> it;

			elementLock.lock();
			try {
				sg.elementLock.lock();
				
				l = new ArrayList<Edge>(wrappedElement.getInDegree());
				it = wrappedElement.getEachEnteringEdge().iterator();
				
				while (it.hasNext()) {
					l.add(sg.getEdge(it.next().getIndex()));
				}
				
				sg.elementLock.unlock();
			} finally {
				elementLock.unlock();
			}

			return l;
		}

		@Override
		public Graph<Node, Edge> getGraph() {
			return sg;
		}

		@Override
		public int getInDegree() {
			int d;

			elementLock.lock();
			try {
				d = wrappedElement.getInDegree();
			} finally {
				elementLock.unlock();
			}

			return d;
		}

		@Override
		public Iterator<Edge> getLeavingEdgeIterator() {
			return getLeavingEdgeSet().iterator();
		}

		@Override
		public Collection<Edge> getLeavingEdgeSet() {
			ArrayList<Edge> l;
			Iterator<Edge> it;

			elementLock.lock();
			try {
				sg.elementLock.lock();
				
				l = new ArrayList<Edge>(wrappedElement.getOutDegree());
				it = wrappedElement.<Edge>getEachLeavingEdge().iterator();
				
				while (it.hasNext()) {
					l.add(sg.getEdge(it.next().getIndex()));
				}
				
				sg.elementLock.unlock();
			} finally {
				elementLock.unlock();
			}

			return l;
		}

		@Override
		public Iterator<Node> getNeighborNodeIterator() {
			ArrayList<Node> l;
			Iterator<Node> it;

			elementLock.lock();
			try {
				sg.elementLock.lock();
				
				l = new ArrayList<Node>(wrappedElement.getDegree());
				it = wrappedElement.getNeighborNodeIterator();
				
				while (it.hasNext()) {
					l.add(sg.getNode(it.next().getIndex()));
				}
				
				sg.elementLock.unlock();
			} finally {
				elementLock.unlock();
			}

			return l.iterator();
		}

		@Override
		public int getOutDegree() {
			int d;

			elementLock.lock();
			try {
				d = wrappedElement.getOutDegree();
			} finally {
				elementLock.unlock();
			}

			return d;
		}

		@Override
		public boolean hasEdgeBetween(Node node) {
			boolean b;

			elementLock.lock();
			try {
				b = wrappedElement.hasEdgeBetween(node);
			} finally {
				elementLock.unlock();
			}

			return b;
		}

		@Override
		public boolean hasEdgeFrom(Node node) {
			boolean b;

			elementLock.lock();
			try {
				b = wrappedElement.hasEdgeFrom(node);
			} finally {
				elementLock.unlock();
			}

			return b;
		}

		@Override
		public boolean hasEdgeToward(Node node) {
			boolean b;

			elementLock.lock();
			try {
				b = wrappedElement.hasEdgeToward(node);
			} finally {
				elementLock.unlock();
			}

			return b;
		}

		@Override
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

		@Override
		public <T extends Node> T getNode0() {
			T n;

			sg.elementLock.lock();
			try {
				n = sg.getNode(wrappedElement.getNode0().getIndex());
			} finally {
				sg.elementLock.unlock();
			}

			return n;
		}

		@Override
		public <T extends Node> T getNode1() {
			T n;

			sg.elementLock.lock();
			try {
				n = sg.getNode(wrappedElement.getNode1().getIndex());
			} finally {
				sg.elementLock.unlock();
			}

			return n;
		}

		@Override
		public <T extends Node> T getOpposite(Node node) {
			T n;

			if (node instanceof SynchronizedNode) {
				node = ((SynchronizedNode) node).wrappedElement;
			}

			sg.elementLock.lock();
			try {
				n = sg.getNode(wrappedElement.getOpposite(node).getIndex());
			} finally {
				sg.elementLock.unlock();
			}

			return n;
		}

		@Override
		public <T extends Node> T getSourceNode() {
			T n;

			sg.elementLock.lock();
			try {
				n = sg.getNode(wrappedElement.getSourceNode().getIndex());
			} finally {
				sg.elementLock.unlock();
			}

			return n;
		}

		@Override
		public <T extends Node> T getTargetNode() {
			T n;

			sg.elementLock.lock();
			try {
				n = sg.getNode(wrappedElement.getTargetNode().getIndex());
			} finally {
				sg.elementLock.unlock();
			}

			return n;
		}

		@Override
		public boolean isDirected() {
			return wrappedElement.isDirected();
		}

		@Override
		public boolean isLoop() {
			return wrappedElement.isLoop();
		}
	}
}
