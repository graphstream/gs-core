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
 * @since 2011-08-23
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.graph.implementations;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

public class Graphs {

	private static final Logger logger = Logger.getLogger(Graphs.class.getSimpleName());

	public static Graph unmutableGraph(Graph g) {
		return null;
	}

	/**
	 * Synchronizes a graph. The returned graph can be accessed and modified by
	 * several threads. You lose genericity in methods returning edge or node
	 * because each element (graph, nodes and edges) is wrapped into a synchronized
	 * wrapper which breaks original elements class.
	 *
	 * @param g
	 *            the graph to synchronize
	 * @return a synchronized wrapper for g
	 */
	public static Graph synchronizedGraph(Graph g) {
		return new SynchronizedGraph(g);
	}

	/**
	 * Merge several graphs in one. A new graph is created, that will contain the
	 * result. The method will try to create a graph of the same class that the
	 * first graph to merge (it needs to have a constructor with a String). Else, a
	 * MultiGraph is used.
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
			logger.warning(String.format("Cannot create a graph of %s.", graphs[0].getClass().getName()));
			result = new MultiGraph(id);
		}

		mergeIn(result, graphs);

		return result;
	}

	/**
	 * Merge several graphs in one. The first parameter is the graph in which the
	 * other graphs will be merged.
	 *
	 * @param result
	 *            destination graph.
	 * @param graphs
	 *            all graphs that will be merged in result.
	 */
	public static void mergeIn(Graph result, Graph... graphs) {
		boolean strict = result.isStrict();
		GraphReplay replay = new GraphReplay(String.format("replay-%x", System.nanoTime()));

		replay.addSink(result);
		result.setStrict(false);

		if (graphs != null)
			for (Graph g : graphs)
				replay.replay(g);

		replay.removeSink(result);
		result.setStrict(strict);
	}

	/**
	 * Clone a given graph with same node/edge structure and same attributes.
	 *
	 * @param g
	 *            the graph to clone
	 * @return a copy of g
	 */
	public static Graph clone(Graph g) {
		Graph copy;

		try {
			Class<? extends Graph> cls = g.getClass();
			copy = cls.getConstructor(String.class).newInstance(g.getId());
		} catch (Exception e) {
			logger.warning(String.format("Cannot create a graph of %s.", g.getClass().getName()));
			copy = new AdjacencyListGraph(g.getId());
		}

		copyAttributes(g, copy);

		for (int i = 0; i < g.getNodeCount(); i++) {
			Node source = g.getNode(i);
			Node target = copy.addNode(source.getId());

			copyAttributes(source, target);
		}

		for (int i = 0; i < g.getEdgeCount(); i++) {
			Edge source = g.getEdge(i);
			Edge target = copy.addEdge(source.getId(), source.getSourceNode().getId(), source.getTargetNode().getId(),
					source.isDirected());

			copyAttributes(source, target);
		}

		return copy;
	}

	/**
	 * @param source
	 * @param target
	 */
	public static void copyAttributes(Element source, Element target) {
		source.attributeKeys().forEach(key -> {
			Object value = source.getAttribute(key);
			value = checkedArrayOrCollectionCopy(value);

			target.setAttribute(key, value);
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Object checkedArrayOrCollectionCopy(Object o) {
		if (o == null)
			return null;

		if (o.getClass().isArray()) {

			Object c = Array.newInstance(o.getClass().getComponentType(), Array.getLength(o));

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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return o;
	}

	static class SynchronizedElement<U extends Element> implements Element {

		private static final ReentrantLock attributeLock = new ReentrantLock();  // Static to lock the attributes from different sources (graph/node/edge). Fix issue #293
		protected final U wrappedElement;

		SynchronizedElement(U e) {
			this.wrappedElement = e;
		}

		public void setAttribute(String attribute, Object... values) {
			attributeLock.lock();

			try {
				wrappedElement.setAttribute(attribute, values);
			} finally {
				attributeLock.unlock();
			}
		}

		public void setAttributes(Map<String, Object> attributes) {
			attributeLock.lock();

			try {
				wrappedElement.setAttributes(attributes);
			} finally {
				attributeLock.unlock();
			}
		}

		public void clearAttributes() {
			attributeLock.lock();

			try {
				wrappedElement.clearAttributes();
			} finally {
				attributeLock.unlock();
			}
		}

		public Object[] getArray(String key) {
			Object[] o;

			attributeLock.lock();

			try {
				o = wrappedElement.getArray(key);
			} finally {
				attributeLock.unlock();
			}

			return o;
		}

		public Object getAttribute(String key) {
			Object o;

			attributeLock.lock();

			try {
				o = wrappedElement.getAttribute(key);
			} finally {
				attributeLock.unlock();
			}

			return o;
		}

		public <T> T getAttribute(String key, Class<T> clazz) {
			T o;

			attributeLock.lock();

			try {
				o = wrappedElement.getAttribute(key, clazz);
			} finally {
				attributeLock.unlock();
			}

			return o;
		}

		public int getAttributeCount() {
			int c;

			attributeLock.lock();

			try {
				c = wrappedElement.getAttributeCount();
			} finally {
				attributeLock.unlock();
			}

			return c;
		}

		@Override
		public Stream<String> attributeKeys() {
			Stream<String> s = null;

			attributeLock.lock();

			try {
				s = wrappedElement.attributeKeys();

				if (!s.spliterator().hasCharacteristics(Spliterator.CONCURRENT))
					s = s.collect(Collectors.toList()).stream();
			} finally {
				attributeLock.unlock();
			}

			return s;
		}

		public Object getFirstAttributeOf(String... keys) {
			Object o;

			attributeLock.lock();

			try {
				o = wrappedElement.getFirstAttributeOf(keys);
			} finally {
				attributeLock.unlock();
			}

			return o;
		}

		public <T> T getFirstAttributeOf(Class<T> clazz, String... keys) {
			T o;

			attributeLock.lock();

			try {
				o = wrappedElement.getFirstAttributeOf(clazz, keys);
			} finally {
				attributeLock.unlock();
			}

			return o;
		}

		public Map<?, ?> getMap(String key) {
			Map<?, ?> o;

			attributeLock.lock();

			try {
				o = wrappedElement.getMap(key);
			} finally {
				attributeLock.unlock();
			}

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

			try {
				o = wrappedElement.getLabel(key);
			} finally {
				attributeLock.unlock();
			}

			return o;
		}

		public double getNumber(String key) {
			double o;

			attributeLock.lock();

			try {
				o = wrappedElement.getNumber(key);
			} finally {
				attributeLock.unlock();
			}

			return o;
		}

		public List<? extends Number> getVector(String key) {
			List<? extends Number> o;

			attributeLock.lock();

			try {
				o = wrappedElement.getVector(key);
			} finally {
				attributeLock.unlock();
			}

			return o;
		}

		public boolean hasArray(String key) {
			boolean b;

			attributeLock.lock();

			try {
				b = wrappedElement.hasArray(key);
			} finally {
				attributeLock.unlock();
			}

			return b;
		}

		public boolean hasAttribute(String key) {
			boolean b;

			attributeLock.lock();

			try {
				b = wrappedElement.hasAttribute(key);
			} finally {
				attributeLock.unlock();
			}

			return b;
		}

		public boolean hasAttribute(String key, Class<?> clazz) {
			boolean b;

			attributeLock.lock();

			try {
				b = wrappedElement.hasAttribute(key, clazz);
			} finally {
				attributeLock.unlock();
			}

			return b;
		}

		public boolean hasMap(String key) {
			boolean b;

			attributeLock.lock();

			try {
				b = wrappedElement.hasMap(key);
			} finally {
				attributeLock.unlock();
			}

			return b;
		}

		public boolean hasLabel(String key) {
			boolean b;

			attributeLock.lock();

			try {
				b = wrappedElement.hasLabel(key);
			} finally {
				attributeLock.unlock();
			}

			return b;
		}

		public boolean hasNumber(String key) {
			boolean b;

			attributeLock.lock();

			try {
				b = wrappedElement.hasNumber(key);
			} finally {
				attributeLock.unlock();
			}

			return b;
		}

		public boolean hasVector(String key) {
			boolean b;

			attributeLock.lock();

			try {
				b = wrappedElement.hasVector(key);
			} finally {
				attributeLock.unlock();
			}

			return b;
		}

		public void removeAttribute(String attribute) {
			attributeLock.lock();

			try {
				wrappedElement.removeAttribute(attribute);
			} finally {
				attributeLock.unlock();
			}
		}
	}

	static class SynchronizedGraph extends SynchronizedElement<Graph> implements Graph {

		final ReentrantLock elementLock;
		final Map<String, Node> synchronizedNodes;
		final Map<String, Edge> synchronizedEdges;

		SynchronizedGraph(Graph g) {
			super(g);

			elementLock = new ReentrantLock();

			synchronizedNodes = g.nodes().collect(Collectors.toMap(Node::getId, n -> new SynchronizedNode(this, n)));
			synchronizedEdges = g.edges().collect(Collectors.toMap(Edge::getId, e -> new SynchronizedEdge(this, e)));
		}

		@Override
		public Stream<Node> nodes() {
			Collection<Node> nodes;

			elementLock.lock();

			try {
				nodes = new Vector<>(synchronizedNodes.values());
			} finally {
				elementLock.unlock();
			}

			return nodes.stream();
		}

		@Override
		public Stream<Edge> edges() {
			Collection<Edge> edges;

			elementLock.lock();

			try {
				edges = new Vector<>(synchronizedEdges.values());
			} finally {
				elementLock.unlock();
			}

			return edges.stream();
		}

		@Override
		public Edge addEdge(String id, String node1, String node2)
				throws IdAlreadyInUseException, ElementNotFoundException, EdgeRejectedException {
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
		public Edge addEdge(String id, String from, String to, boolean directed)
				throws IdAlreadyInUseException, ElementNotFoundException {
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
		public Edge addEdge(String id, int fromIndex, int toIndex, boolean directed) {
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

		@SuppressWarnings("unchecked")
		@Override
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

		@SuppressWarnings("unchecked")
		@Override
		public Edge addEdge(String id, Node from, Node to, boolean directed) {
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
		public Node addNode(String id) throws IdAlreadyInUseException {
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
			LinkedList<AttributeSink> sinks = new LinkedList<AttributeSink>();

			elementLock.lock();

			try {
				for (AttributeSink as : wrappedElement.attributeSinks())
					sinks.add(as);
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
		public EdgeFactory<? extends Edge> edgeFactory() {
			return wrappedElement.edgeFactory();
		}

		@Override
		public Iterable<ElementSink> elementSinks() {
			LinkedList<ElementSink> sinks = new LinkedList<ElementSink>();

			elementLock.lock();

			try {
				for (ElementSink es : wrappedElement.elementSinks())
					sinks.add(es);
			} finally {
				elementLock.unlock();
			}

			return sinks;
		}

		@Override
		public Edge getEdge(String id) {
			Edge e;

			elementLock.lock();

			try {
				e = synchronizedEdges.get(id);
			} finally {
				elementLock.unlock();
			}

			return e;
		}

		@Override
		public Edge getEdge(int index) throws IndexOutOfBoundsException {
			Edge e;

			elementLock.lock();

			try {
				e = wrappedElement.getEdge(index);
			} finally {
				elementLock.unlock();
			}

			return e == null ? null : getEdge(e.getId());
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
		public Node getNode(String id) {
			Node n;

			elementLock.lock();

			try {
				n = synchronizedNodes.get(id);
			} finally {
				elementLock.unlock();
			}

			return n;
		}

		@Override
		public Node getNode(int index) throws IndexOutOfBoundsException {
			Node n;

			elementLock.lock();

			try {
				n = wrappedElement.getNode(index);
			} finally {
				elementLock.unlock();
			}

			return n == null ? null : getNode(n.getId());
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

		public Viewer display() {
			return wrappedElement.display();
		}

		public Viewer display(boolean autoLayout) {
			return wrappedElement.display(autoLayout);
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
		public void read(String filename) throws IOException, GraphParseException, ElementNotFoundException {
			elementLock.lock();

			try {
				wrappedElement.read(filename);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public void read(FileSource input, String filename) throws IOException, GraphParseException {
			elementLock.lock();

			try {
				wrappedElement.read(input, filename);
			} finally {
				elementLock.unlock();
			}
		}

		@Override
		public Edge removeEdge(String from, String to) throws ElementNotFoundException {
			Edge e;
			Edge se;

			elementLock.lock();

			try {
				e = wrappedElement.removeEdge(from, to);
				se = synchronizedEdges.remove(e.getId());
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@Override
		public Edge removeEdge(String id) throws ElementNotFoundException {
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

		@Override
		public Edge removeEdge(Node node1, Node node2) {
			Edge e;
			Edge se;

			if (node1 instanceof SynchronizedNode)
				node1 = ((SynchronizedNode) node1).wrappedElement;

			if (node2 instanceof SynchronizedNode)
				node2 = ((SynchronizedNode) node1).wrappedElement;

			elementLock.lock();

			try {
				e = wrappedElement.removeEdge(node1, node2);
				se = synchronizedEdges.remove(e.getId());
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@Override
		public Edge removeEdge(Edge edge) {
			Edge e;
			Edge se;

			if (edge instanceof SynchronizedEdge)
				edge = ((SynchronizedEdge) edge).wrappedElement;

			elementLock.lock();

			try {
				e = wrappedElement.removeEdge(edge);
				se = synchronizedEdges.remove(e.getId());
			} finally {
				elementLock.unlock();
			}

			return se;
		}

		@Override
		public Node removeNode(String id) throws ElementNotFoundException {
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

		@Override
		public Node removeNode(Node node) {
			Node n;
			Node sn;

			if (node instanceof SynchronizedNode)
				node = ((SynchronizedNode) node).wrappedElement;

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
		public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
			wrappedElement.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
		}

		@Override
		public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
				Object newValue) {
			wrappedElement.edgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue, newValue);
		}

		@Override
		public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
			wrappedElement.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
		}

		@Override
		public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
			wrappedElement.graphAttributeAdded(sourceId, timeId, attribute, value);
		}

		@Override
		public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
				Object newValue) {
			wrappedElement.graphAttributeChanged(sourceId, timeId, attribute, oldValue, newValue);
		}

		@Override
		public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
			wrappedElement.graphAttributeRemoved(sourceId, timeId, attribute);
		}

		@Override
		public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
			wrappedElement.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
		}

		@Override
		public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
				Object newValue) {
			wrappedElement.nodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue, newValue);
		}

		@Override
		public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
			wrappedElement.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
		}

		@Override
		public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
				boolean directed) {
			wrappedElement.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
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
			return nodes().iterator();
		}

	}

	static class SynchronizedNode extends SynchronizedElement<Node> implements Node {

		private final SynchronizedGraph sg;
		private final ReentrantLock elementLock;

		SynchronizedNode(SynchronizedGraph sg, Node n) {
			super(n);

			this.sg = sg;
			this.elementLock = new ReentrantLock();
		}

		@Override
		public Stream<Node> neighborNodes() {
			List<Node> nodes;

			elementLock.lock();
			sg.elementLock.lock();

			try {
				nodes = wrappedElement.neighborNodes().map(n -> sg.getNode(n.getIndex())).collect(Collectors.toList());
			} finally {
				sg.elementLock.unlock();
				elementLock.unlock();
			}

			return nodes.stream();
		}

		@Override
		public Stream<Edge> edges() {
			List<Edge> edges;

			elementLock.lock();
			sg.elementLock.lock();

			try {

				edges = wrappedElement.edges().map(e -> sg.getEdge(e.getIndex())).collect(Collectors.toList());
			} finally {
				sg.elementLock.unlock();
				elementLock.unlock();
			}

			return edges.stream();
		}

		@Override
		public Stream<Edge> leavingEdges() {
			List<Edge> edges;

			elementLock.lock();
			sg.elementLock.lock();

			try {

				edges = wrappedElement.leavingEdges().map(e -> sg.getEdge(e.getIndex())).collect(Collectors.toList());
			} finally {
				sg.elementLock.unlock();
				elementLock.unlock();
			}

			return edges.stream();
		}

		@Override
		public Stream<Edge> enteringEdges() {
			List<Edge> edges;

			elementLock.lock();
			sg.elementLock.lock();

			try {

				edges = wrappedElement.enteringEdges().map(e -> sg.getEdge(e.getIndex())).collect(Collectors.toList());
			} finally {
				sg.elementLock.unlock();
				elementLock.unlock();
			}

			return edges.stream();
		}

		@Override
		public Iterator<Node> getBreadthFirstIterator() {
			return getBreadthFirstIterator(false);
		}

		@Override
		public Iterator<Node> getBreadthFirstIterator(boolean directed) {
			LinkedList<Node> l = new LinkedList<Node>();
			Iterator<Node> it;

			elementLock.lock();
			sg.elementLock.lock();

			try {

				it = wrappedElement.getBreadthFirstIterator(directed);

				while (it.hasNext())
					l.add(sg.getNode(it.next().getIndex()));
			} finally {
				sg.elementLock.unlock();
				elementLock.unlock();
			}

			return l.iterator();
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
		public Iterator<Node> getDepthFirstIterator() {
			return getDepthFirstIterator(false);
		}

		@Override
		public Iterator<Node> getDepthFirstIterator(boolean directed) {
			LinkedList<Node> l = new LinkedList<Node>();
			Iterator<Node> it;

			elementLock.lock();
			sg.elementLock.lock();

			try {
				it = wrappedElement.getDepthFirstIterator();

				while (it.hasNext())
					l.add(sg.getNode(it.next().getIndex()));
			} finally {
				sg.elementLock.unlock();
				elementLock.unlock();
			}

			return l.iterator();
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
		public Edge getEdgeBetween(String id) {
			Edge e;

			elementLock.lock();

			try {
				e = sg.getEdge(wrappedElement.getEdgeBetween(id).getIndex());
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
		public Edge getEdgeBetween(int index) {
			Edge e;

			elementLock.lock();

			try {
				e = sg.getEdge(wrappedElement.getEdgeBetween(index).getIndex());
			} finally {
				elementLock.unlock();
			}

			return e;
		}

		@Override
		public Edge getEdgeFrom(String id) {
			Edge e;

			elementLock.lock();

			try {
				e = sg.getEdge(wrappedElement.getEdgeFrom(id).getIndex());
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
		public Edge getEdgeFrom(int index) {
			Edge e;

			elementLock.lock();

			try {
				e = sg.getEdge(wrappedElement.getEdgeFrom(index).getIndex());
			} finally {
				elementLock.unlock();
			}

			return e;
		}

		@Override
		public Edge getEdgeToward(String id) {
			Edge e;

			elementLock.lock();

			try {
				e = sg.getEdge(wrappedElement.getEdgeToward(id).getIndex());
			} finally {
				elementLock.unlock();
			}

			return e;
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
		public Edge getEdgeToward(int index) {
			Edge e;

			elementLock.lock();

			try {
				e = sg.getEdge(wrappedElement.getEdgeToward(index).getIndex());
			} finally {
				elementLock.unlock();
			}

			return e;
		}

		@Override
		public Graph getGraph() {
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
		public Iterator<Edge> iterator() {
			return edges().iterator();
		}
	}

	static class SynchronizedEdge extends SynchronizedElement<Edge> implements Edge {

		final SynchronizedGraph sg;

		SynchronizedEdge(SynchronizedGraph sg, Edge e) {
			super(e);
			this.sg = sg;
		}

		@Override
		public Node getNode0() {
			Node n;

			sg.elementLock.lock();

			try {
				n = sg.getNode(wrappedElement.getNode0().getIndex());
			} finally {
				sg.elementLock.unlock();
			}

			return n;
		}

		@Override
		public Node getNode1() {
			Node n;

			sg.elementLock.lock();

			try {
				n = sg.getNode(wrappedElement.getNode1().getIndex());
			} finally {
				sg.elementLock.unlock();
			}

			return n;
		}

		@Override
		public Node getOpposite(Node node) {
			Node n;

			if (node instanceof SynchronizedNode)
				node = ((SynchronizedNode) node).wrappedElement;

			sg.elementLock.lock();

			try {
				n = sg.getNode(wrappedElement.getOpposite(node).getIndex());
			} finally {
				sg.elementLock.unlock();
			}

			return n;
		}

		@Override
		public Node getSourceNode() {
			Node n;

			sg.elementLock.lock();

			try {
				n = sg.getNode(wrappedElement.getSourceNode().getIndex());
			} finally {
				sg.elementLock.unlock();
			}

			return n;
		}

		@Override
		public Node getTargetNode() {
			Node n;

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
