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
 * @since 2009-02-19
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author habernal <habernal@users.noreply.github.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph;

import java.io.IOException;

import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkFactory;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.ui.view.Viewer;

/**
 * An Interface that advises general purpose methods for handling graphs.
 * <p>
 * <p>
 * This interface is one of the main interfaces of GraphStream. It defines the
 * services provided by a graph structure. Graphs implementations must at least
 * implement this interface (but are free to provide more services).
 * </p>
 * <p>
 * <p>
 * With {@link org.graphstream.stream.Source},
 * {@link org.graphstream.stream.Sink} and {@link org.graphstream.stream.Pipe},
 * this interface is one of the most important. A graph is a
 * {@link org.graphstream.stream.Pipe} that buffers the graph events and present
 * the graph structure as it is actually.
 * </p>
 * <p>
 * <p>
 * In other words, it allows to browse the graph structure, to explore it, to
 * modify it, and to implement algorithms on it. This class can be seen as a
 * snapshot of a stream of event at current time.
 * </p>
 * <p>
 * <p>
 * With factories ({@link org.graphstream.graph.NodeFactory},
 * {@link org.graphstream.graph.EdgeFactory}), users can define their own models
 * of nodes or edges. Problem is that when you define such model, you want to
 * access to elements with the valid type, without cast if possible. To improve
 * the access to elements in such cases, Graph offers implicit genericity to
 * access nodes or edges. The following is an example of an access without
 * genericity :
 * <p>
 * 
 * <pre>
 * 	Graph g = ... ;
 * 	g.setNodeFactory( new MyNodeFactory() );
 *  g.addNode("root");
 *
 *  MyNode n = (MyNode) g.getNode("root");
 *
 *  for( Node o : g.getEachNode() )
 *  {
 *  	MyNode node = (MyNode) o;
 *  	// Do something with node
 *  }
 * </pre>
 * <p>
 * With implicit genericity offers by Graph, this can be done easier:
 * <p>
 * 
 * <pre>
 *  Graph g = ... ;
 * 	g.setNodeFactory( new MyNodeFactory() );
 *  g.addNode("root");
 *
 *  MyNode n = g.getNode("root");
 *
 *  for( MyNode node : g.getEachNode() )
 *  {
 *  	// Do something with node
 *  }
 * </pre>
 * <p>
 * </p>
 * <p>
 * <p>
 * Graph elements (nodes and edges) can be accessed using their identifier or
 * their index. Each node / edge has a unique string identifier assigned when
 * the element is created. Each element has an automatically maintained unique
 * index between 0 and {@link #getNodeCount()} - 1 or {@link #getEdgeCount()} -
 * 1. When a new element is added, its index is <code>getNodeCount() - 1</code>
 * or <code>getEdgeCount() - 1</code>. When an element is removed, the element
 * with the biggest index takes its place. Unlike identifiers, indices can
 * change when the graph is modified, but they are always successive. A loop of
 * the form
 * <p>
 * 
 * <pre>
 * for (int i = 0; i &lt; g.getNodeCount(); i++) {
 * 	Node node = g.getNode(i);
 * 	// Do something with node
 * }
 * </pre>
 * <p>
 * will always iterate on all the nodes of <code>g</code>.
 * </p>
 */
public interface Graph extends Element, Pipe, Iterable<Node>, Structure {
	// Access

	/**
	 * Get a node by its identifier. This method is implicitly generic and returns
	 * something which extends Node. The return type is the one of the left part of
	 * the assignment. For example, in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedNode node = graph.getNode(&quot;...&quot;);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedNode node. If no left part exists, method
	 * will just return a Node.
	 *
	 * @param id
	 *            Identifier of the node to find.
	 * @return The searched node or null if not found.
	 */
	Node getNode(String id);

	/**
	 * Get an edge by its identifier. This method is implicitly generic and returns
	 * something which extends Edge. The return type is the one of the left part of
	 * the assignment. For example, in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedEdge edge = graph.getEdge(&quot;...&quot;);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedEdge edge. If no left part exists, method
	 * will just return an Edge.
	 *
	 * @param id
	 *            Identifier of the edge to find.
	 * @return The searched edge or null if not found.
	 */
	Edge getEdge(String id);

	/**
	 * The factory used to create node instances. The factory can be changed to
	 * refine the node class generated for this graph.
	 *
	 * @see #setNodeFactory(NodeFactory)
	 * @see #edgeFactory()
	 */
	NodeFactory<? extends Node> nodeFactory();

	/**
	 * The factory used to create edge instances. The factory can be changed to
	 * refine the edge class generated for this graph.
	 *
	 * @see #setEdgeFactory(EdgeFactory)
	 * @see #nodeFactory()
	 */
	EdgeFactory<? extends Edge> edgeFactory();

	/**
	 * Is strict checking enabled? If strict checking is enabled the graph checks
	 * for name space conflicts (e.g. insertion of two nodes with the same name),
	 * removal of non-existing elements, use of non existing elements (create an
	 * edge between two non existing nodes). Graph implementations are free to
	 * respect strict checking or not.
	 *
	 * @return True if enabled.
	 */
	boolean isStrict();

	/**
	 * Is the automatic creation of missing elements enabled?. If strict checking is
	 * disabled and auto-creation is enabled, when an edge is created and one or two
	 * of its nodes are not already present in the graph, the nodes are
	 * automatically created.
	 *
	 * @return True if enabled.
	 */
	boolean isAutoCreationEnabled();

	/**
	 * The current step.
	 *
	 * @return The step.
	 */
	double getStep();

	// Command

	/**
	 * Set the node factory used to create nodes.
	 *
	 * @param nf
	 *            the new NodeFactory
	 */
	void setNodeFactory(NodeFactory<? extends Node> nf);

	/**
	 * Set the edge factory used to create edges.
	 *
	 * @param ef
	 *            the new EdgeFactory
	 */
	void setEdgeFactory(EdgeFactory<? extends Edge> ef);

	/**
	 * Enable or disable strict checking.
	 *
	 * @param on
	 *            True or false.
	 * @see #isStrict()
	 */
	void setStrict(boolean on);

	/**
	 * Enable or disable the automatic creation of missing elements.
	 *
	 * @param on
	 *            True or false.
	 * @see #isAutoCreationEnabled()
	 */
	void setAutoCreate(boolean on);

	// Graph construction

	/**
	 * Empty the graph completely by removing any references to nodes or edges.
	 * Every attribute is also removed. However, listeners are kept.
	 *
	 * @see #clearSinks()
	 */
	void clear();

	/**
	 * Add a node in the graph.
	 * <p>
	 * This acts as a factory, creating the node instance automatically (and
	 * eventually using the node factory provided). An event is generated toward the
	 * listeners. If strict checking is enabled, and a node already exists with this
	 * identifier, an {@link org.graphstream.graph.IdAlreadyInUseException} is
	 * raised. Else the error is silently ignored and the already existing node is
	 * returned.
	 * </p>
	 * <p>
	 * This method is implicitly generic and returns something which extends Node.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedNode n = graph.addNode(&quot;...&quot;);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedNode. If no left part exists, method will
	 * just return a Node.
	 * </p>
	 *
	 * @param id
	 *            Arbitrary and unique string identifying the node.
	 * @return The created node (or the already existing node).
	 * @throws IdAlreadyInUseException
	 *             If strict checking is enabled the identifier is already used.
	 */
	Node addNode(String id) throws IdAlreadyInUseException;

	/**
	 * Adds an undirected edge between nodes.
	 * <p>
	 * <p>
	 * The behavior of this method depends on many conditions. It can be summarized
	 * as follows.
	 * </p>
	 * <p>
	 * <p>
	 * First of all, the method checks if the graph already contains an edge with
	 * the same id. If this is the case and strict checking is enabled,
	 * {@code IdAlreadyInUseException} is thrown. If the strict checking is disabled
	 * the method returns a reference to the existing edge if it has endpoints
	 * {@code node1} and {@code node2} (in the same order if the edge is directed)
	 * or {@code null} otherwise.
	 * </p>
	 * <p>
	 * <p>
	 * In the case when the graph does not contain an edge with the same id, the
	 * method checks if {@code node1} and {@code node2} exist. If one or both of
	 * them do not exist, and strict checking is enabled, {@code
	 * ElementNotFoundException} is thrown. Otherwise if auto-creation is disabled,
	 * the method returns {@code null}. If auto-creation is enabled, the method
	 * creates the missing endpoints.
	 * <p>
	 * <p>
	 * When the edge id is not already in use and the both endpoints exist (or
	 * created), the edge can still be rejected. It may happen for example when it
	 * connects two already connected nodes in a single graph. If the edge is
	 * rejected, the method throws {@code EdgeRejectedException} if strict checking
	 * is enabled or returns {@code null} otherwise. Finally, if the edge is
	 * accepted, it is created using the corresponding edge factory and a reference
	 * to it is returned.
	 * <p>
	 * <p>
	 * An edge creation event is sent toward the listeners. If new nodes are
	 * created, the corresponding events are also sent to the listeners.
	 * </p>
	 * <p>
	 * <p>
	 * This method is implicitly generic and return something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedEdge e = graph.addEdge(&quot;...&quot;, &quot;...&quot;, &quot;...&quot;);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 *
	 * @param id
	 *            Unique and arbitrary string identifying the edge.
	 * @param node1
	 *            The first node identifier.
	 * @param node2
	 *            The second node identifier.
	 * @return The newly created edge, an existing edge or {@code null} (see the
	 *         detailed description above)
	 * @throws IdAlreadyInUseException
	 *             If an edge with the same id already exists and strict checking is
	 *             enabled.
	 * @throws ElementNotFoundException
	 *             If strict checking is enabled, and 'node1' or 'node2' are not
	 *             registered in the graph.
	 * @throws EdgeRejectedException
	 *             If strict checking is enabled and the edge is not accepted.
	 */
	default Edge addEdge(String id, String node1, String node2)
			throws IdAlreadyInUseException, ElementNotFoundException, EdgeRejectedException {
		return addEdge(id, node1, node2, false);
	}

	/**
	 * Like {@link #addEdge(String, String, String)}, but this edge can be directed
	 * between the two given nodes. If directed, the edge goes in the 'from' -&gt;
	 * 'to' direction. An event is sent toward the listeners.
	 *
	 * @param id
	 *            Unique and arbitrary string identifying the edge.
	 * @param from
	 *            The first node identifier.
	 * @param to
	 *            The second node identifier.
	 * @param directed
	 *            Is the edge directed?
	 * @return The newly created edge, an existing edge or {@code null} (see the
	 *         detailed description in {@link #addEdge(String, String, String)})
	 * @throws IdAlreadyInUseException
	 *             If an edge with the same id already exists and strict checking is
	 *             enabled.
	 * @throws ElementNotFoundException
	 *             If strict checking is enabled, and 'node1' or 'node2' are not
	 *             registered in the graph.
	 * @throws EdgeRejectedException
	 *             If strict checking is enabled and the edge is not accepted.
	 * @see #addEdge(String, String, String)
	 */
	default Edge addEdge(String id, String from, String to, boolean directed)
			throws IdAlreadyInUseException, ElementNotFoundException, EdgeRejectedException {
		Node src = getNode(from);
		Node dst = getNode(to);

		if (src == null || dst == null) {
			if (isStrict())
				throw new ElementNotFoundException("Node '%s'", src == null ? from : to);

			if (!isAutoCreationEnabled())
				return null;

			if (src == null)
				src = addNode(from);

			if (dst == null)
				dst = addNode(to);
		}

		return addEdge(id, src, dst, directed);
	}

	/**
	 * <p>
	 * Since dynamic graphs are based on discrete event modifications, the notion of
	 * step is defined to simulate elapsed time between events. So a step is a event
	 * that occurs in the graph, it does not modify it but it gives a kind of
	 * timestamp that allows the tracking of the progress of the graph over the
	 * time.
	 * </p>
	 * <p>
	 * This kind of event is useful for dynamic algorithms that listen to the
	 * dynamic graph and need to measure the time in the graph's evolution.
	 * </p>
	 *
	 * @param time
	 *            A numerical value that may give a timestamp to track the evolution
	 *            of the graph over the time.
	 */
	void stepBegins(double time);

	// Source
	// XXX do we put the iterable attributeSinks and elementSinks in Source ?

	/**
	 * Returns an "iterable" of {@link AttributeSink} objects registered to this
	 * graph.
	 *
	 * @return the set of {@link AttributeSink} under the form of an iterable
	 *         object.
	 */
	Iterable<AttributeSink> attributeSinks();

	/**
	 * Returns an "iterable" of {@link ElementSink} objects registered to this
	 * graph.
	 *
	 * @return the list of {@link ElementSink} under the form of an iterable object.
	 */
	Iterable<ElementSink> elementSinks();

	// Utility shortcuts (should be mixins or traits, what are you doing Mr Java
	// ?)
	// XXX use a Readable/Writable/Displayable interface for this ?

	/**
	 * Utility method to read a graph. This method tries to identify the graph
	 * format by itself and instantiates the corresponding reader automatically. If
	 * this process fails, a NotFoundException is raised.
	 *
	 * @param filename
	 *            The graph filename (or URL).
	 * @throws ElementNotFoundException
	 *             If the file cannot be found or if the format is not recognized.
	 * @throws GraphParseException
	 *             If there is a parsing error while reading the file.
	 * @throws IOException
	 *             If an input output error occurs during the graph reading.
	 */
	default void read(String filename) throws IOException, GraphParseException, ElementNotFoundException {
		FileSource input = FileSourceFactory.sourceFor(filename);

		if (input != null) {
			input.addSink(this);
			read(input, filename);
			input.removeSink(this);
		} else {
			throw new IOException("No source reader for " + filename);
		}
	}

	/**
	 * Utility method to read a graph using the given reader.
	 *
	 * @param input
	 *            An appropriate reader for the filename.
	 * @param filename
	 *            The graph filename (or URL).
	 * @throws ElementNotFoundException
	 *             If the file cannot be found or if the format is not recognised.
	 * @throws GraphParseException
	 *             If there is a parsing error while reading the file.
	 * @throws IOException
	 *             If an input/output error occurs during the graph reading.
	 */
	default void read(FileSource input, String filename) throws IOException, GraphParseException {
		input.readAll(filename);
	}

	/**
	 * Utility method to write a graph in DGS format to a file.
	 *
	 * @param filename
	 *            The file that will contain the saved graph (or URL).
	 * @throws IOException
	 *             If an input/output error occurs during the graph writing.
	 */
	default void write(String filename) throws IOException {
		FileSink output = FileSinkFactory.sinkFor(filename);

		if (output != null) {
			write(output, filename);
		} else {
			throw new IOException("No sink writer for " + filename);
		}
	}

	/**
	 * Utility method to write a graph in the chosen format to a file.
	 *
	 * @param filename
	 *            The file that will contain the saved graph (or URL).
	 * @param output
	 *            The output format to use.
	 * @throws IOException
	 *             If an input/output error occurs during the graph writing.
	 */
	default void write(FileSink output, String filename) throws IOException {
		output.writeAll(this, filename);
	}

	/**
	 * Utility method that creates a new graph viewer, and register the graph in it.
	 * Notice that this method is a quick way to see a graph, and only this. It can
	 * be used to prototype a program, but may be limited. This method automatically
	 * launch a graph layout algorithm in its own thread to compute best node
	 * positions.
	 * 
	 * @see org.graphstream.ui.view.Viewer
	 * @see #display(boolean )
	 * @return a graph viewer that allows to command the viewer (it often run in
	 *         another thread).
	 */
	Viewer display();

	/**
	 * Utility method that creates a new graph viewer, and register the graph in it.
	 * Notice that this method is a quick way to see a graph, and only this. It can
	 * be used to prototype a program, but is very limited.
	 * 
	 * @param autoLayout
	 *            If true a layout algorithm is launched in its own thread to
	 *            compute best node positions.
	 * @see org.graphstream.ui.view.Viewer
	 * @see #display()
	 * @return a graph viewer that allows to command the viewer (it often run in
	 *         another thread).
	 */
	Viewer display(boolean autoLayout);

	// New methods

	/**
	 * Get a node by its index. This method is implicitly generic and returns
	 * something which extends Node. The return type is the one of the left part of
	 * the assignment. For example, in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedNode node = graph.getNode(index);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedNode node. If no left part exists, method
	 * will just return a Node.
	 *
	 * @param index
	 *            Index of the node to find.
	 * @return The node with the given index
	 * @throws IndexOutOfBoundsException
	 *             If the index is negative or greater than {@code
	 *                                   getNodeCount() - 1}.
	 */
	Node getNode(int index) throws IndexOutOfBoundsException;

	/**
	 * Get an edge by its index. This method is implicitly generic and returns
	 * something which extends Edge. The return type is the one of the left part of
	 * the assignment. For example, in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedEdge edge = graph.getEdge(index);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedEdge edge. If no left part exists, method
	 * will just return an Edge.
	 *
	 * @param index
	 *            The index of the edge to find.
	 * @return The edge with the given index
	 * @throws IndexOutOfBoundsException
	 *             if the index is less than 0 or greater than {@code
	 *                                   getNodeCount() - 1}.
	 */
	Edge getEdge(int index) throws IndexOutOfBoundsException;

	/**
	 * Like {@link #addEdge(String, String, String)} but the nodes are identified by
	 * their indices.
	 *
	 * @param id
	 *            Unique and arbitrary string identifying the edge.
	 * @param index1
	 *            The first node index
	 * @param index2
	 *            The second node index
	 * @return The newly created edge, an existing edge or {@code null}
	 * @throws IndexOutOfBoundsException
	 *             If node indices are negative or greater than {@code
	 *                                   getNodeCount() - 1}
	 * @throws IdAlreadyInUseException
	 *             If an edge with the same id already exists and strict checking is
	 *             enabled.
	 * @throws EdgeRejectedException
	 *             If strict checking is enabled and the edge is not accepted.
	 * @see #addEdge(String, String, String)
	 */
	default Edge addEdge(String id, int index1, int index2)
			throws IndexOutOfBoundsException, IdAlreadyInUseException, EdgeRejectedException {
		return addEdge(id, getNode(index1), getNode(index2), false);
	}

	/**
	 * Like {@link #addEdge(String, String, String, boolean)} but the nodes are
	 * identified by their indices.
	 *
	 * @param id
	 *            Unique and arbitrary string identifying the edge.
	 * @param toIndex
	 *            The first node index
	 * @param fromIndex
	 *            The second node index
	 * @param directed
	 *            Is the edge directed?
	 * @return The newly created edge, an existing edge or {@code null}
	 * @throws IndexOutOfBoundsException
	 *             If node indices are negative or greater than {@code
	 *                                   getNodeCount() - 1}
	 * @throws IdAlreadyInUseException
	 *             If an edge with the same id already exists and strict checking is
	 *             enabled.
	 * @throws EdgeRejectedException
	 *             If strict checking is enabled and the edge is not accepted.
	 * @see #addEdge(String, String, String)
	 */
	default Edge addEdge(String id, int fromIndex, int toIndex, boolean directed)
			throws IndexOutOfBoundsException, IdAlreadyInUseException, EdgeRejectedException {
		return addEdge(id, getNode(fromIndex), getNode(toIndex), directed);
	}

	/**
	 * Like {@link #addEdge(String, String, String)} but the node references are
	 * given instead of node identifiers.
	 *
	 * @param id
	 *            Unique and arbitrary string identifying the edge.
	 * @param node1
	 *            The first node
	 * @param node2
	 *            The second node
	 * @return The newly created edge, an existing edge or {@code null}
	 * @throws IdAlreadyInUseException
	 *             If an edge with the same id already exists and strict checking is
	 *             enabled.
	 * @throws EdgeRejectedException
	 *             If strict checking is enabled and the edge is not accepted.
	 * @see #addEdge(String, String, String)
	 */
	default Edge addEdge(String id, Node node1, Node node2) throws IdAlreadyInUseException, EdgeRejectedException {
		return addEdge(id, node1, node2, false);
	}

	/**
	 * Like {@link #addEdge(String, String, String, boolean)} but the node
	 * references are given instead of node identifiers.
	 *
	 * @param id
	 *            Unique and arbitrary string identifying the edge.
	 * @param from
	 *            The first node
	 * @param to
	 *            The second node
	 * @param directed
	 *            Is the edge directed?
	 * @return The newly created edge, an existing edge or {@code null}
	 * @throws IdAlreadyInUseException
	 *             If an edge with the same id already exists and strict checking is
	 *             enabled.
	 * @throws EdgeRejectedException
	 *             If strict checking is enabled and the edge is not accepted.
	 * @see #addEdge(String, String, String)
	 */
	Edge addEdge(String id, Node from, Node to, boolean directed) throws IdAlreadyInUseException, EdgeRejectedException;

	/**
	 * Removes an edge with a given index. An event is sent toward the listeners.
	 * <p>
	 * <p>
	 * This method is implicitly generic and returns something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedEdge edge = graph.removeEdge(i);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedEdge edge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 *
	 * @param index
	 *            The index of the edge to be removed.
	 * @return The removed edge
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than {@code
	 *                                   getEdgeCount() - 1}
	 */
	default Edge removeEdge(int index) throws IndexOutOfBoundsException {
		Edge edge = getEdge(index);

		if (edge == null) {
			if (isStrict())
				throw new ElementNotFoundException("Edge #" + index);

			return null;
		}

		return removeEdge(edge);
	}

	/**
	 * Removes an edge between two nodes. Like {@link #removeEdge(String, String)}
	 * but the nodes are identified by their indices.
	 *
	 * @param fromIndex
	 *            the index of the source node
	 * @param toIndex
	 *            the index of the target node
	 * @return the removed edge or {@code null} if no edge is removed
	 * @throws IndexOutOfBoundsException
	 *             If one of the node indices is negative or greater than
	 *             {@code getNodeCount() - 1}.
	 * @throws ElementNotFoundException
	 *             if strict checking is enabled and there is no edge between the
	 *             two nodes.
	 * @see #removeEdge(String, String)
	 */
	default Edge removeEdge(int fromIndex, int toIndex) throws IndexOutOfBoundsException, ElementNotFoundException {
		Node fromNode = getNode(fromIndex);
		Node toNode = getNode(toIndex);

		if (fromNode == null || toNode == null) {
			if (isStrict())
				throw new ElementNotFoundException("Node #%d", fromNode == null ? fromIndex : toIndex);

			return null;
		}

		return removeEdge(fromNode, toNode);
	}

	/**
	 * Removes an edge between two nodes. Like {@link #removeEdge(String, String)}
	 * but node references are given instead of node identifiers.
	 *
	 * @param node1
	 *            the first node
	 * @param node2
	 *            the second node
	 * @return the removed edge or {@code null} if no edge is removed
	 * @throws ElementNotFoundException
	 *             if strict checking is enabled and there is no edge between the
	 *             two nodes.
	 * @see #removeEdge(String, String)
	 */
	Edge removeEdge(Node node1, Node node2) throws ElementNotFoundException;

	/**
	 * Remove an edge given the identifiers of its two endpoints.
	 * <p>
	 * If the edge is directed it is removed only if its source and destination
	 * nodes are identified by 'from' and 'to' respectively. If the graph is a
	 * multi-graph and there are several edges between the two nodes, one of the
	 * edges at random is removed. An event is sent toward the listeners. If strict
	 * checking is enabled and at least one of the two given nodes does not exist or
	 * if they are not connected, a not found exception is raised. Else the error is
	 * silently ignored, and null is returned.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedEdge e = graph.removeEdge(&quot;...&quot;, &quot;...&quot;);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 *
	 * @param from
	 *            The origin node identifier to select the edge.
	 * @param to
	 *            The destination node identifier to select the edge.
	 * @return The removed edge, or null if strict checking is disabled and at least
	 *         one of the two given nodes does not exist or there is no edge between
	 *         them
	 * @throws ElementNotFoundException
	 *             If the 'from' or 'to' node is not registered in the graph or not
	 *             connected and strict checking is enabled.
	 */
	default Edge removeEdge(String from, String to) throws ElementNotFoundException {
		Node fromNode = getNode(from);
		Node toNode = getNode(to);

		if (fromNode == null || toNode == null) {
			if (isStrict())
				throw new ElementNotFoundException("Node \"%s\"", fromNode == null ? from : to);

			return null;
		}

		return removeEdge(fromNode, toNode);
	}

	/**
	 * Removes an edge knowing its identifier. An event is sent toward the
	 * listeners. If strict checking is enabled and the edge does not exist,
	 * {@code ElementNotFoundException} is raised. Otherwise the error is silently
	 * ignored and null is returned.
	 * <p>
	 * This method is implicitly generic and returns something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedEdge e = graph.removeEdge(&quot;...&quot;);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 *
	 * @param id
	 *            Identifier of the edge to remove.
	 * @return The removed edge, or null if strict checking is disabled and the edge
	 *         does not exist.
	 * @throws ElementNotFoundException
	 *             If no edge matches the identifier and strict checking is enabled.
	 */
	default Edge removeEdge(String id) throws ElementNotFoundException {
		Edge edge = getEdge(id);

		if (edge == null) {
			if (isStrict())
				throw new ElementNotFoundException("Edge \"" + id + "\"");

			return null;
		}

		return removeEdge(edge);
	}

	/**
	 * Removes an edge. An event is sent toward the listeners.
	 * <p>
	 * This method is implicitly generic and returns something which extends Edge.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedEdge e = graph.removeEdge(...);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedEdge. If no left part exists, method will
	 * just return an Edge.
	 * </p>
	 *
	 * @param edge
	 *            The edge to be removed
	 * @return The removed edge
	 */
	Edge removeEdge(Edge edge);

	/**
	 * Removes a node with a given index.
	 * <p>
	 * An event is generated toward the listeners. Note that removing a node may
	 * remove all edges it is connected to. In this case corresponding events will
	 * also be generated toward the listeners.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Node.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedNode n = graph.removeNode(index);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedNode. If no left part exists, method will
	 * just return a Node.
	 * </p>
	 *
	 * @param index
	 *            The index of the node to be removed
	 * @return The removed node
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than {@code
	 *                                   getNodeCount() - 1}.
	 */
	default Node removeNode(int index) throws IndexOutOfBoundsException {
		Node node = getNode(index);

		if (node == null) {
			if (isStrict())
				throw new ElementNotFoundException("Node #" + index);

			return null;
		}

		return removeNode(node);
	}

	/**
	 * Remove a node using its identifier.
	 * <p>
	 * An event is generated toward the listeners. Note that removing a node may
	 * remove all edges it is connected to. In this case corresponding events will
	 * also be generated toward the listeners.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Node.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedNode n = graph.removeNode(&quot;...&quot;);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedNode. If no left part exists, method will
	 * just return a Node.
	 * </p>
	 *
	 * @param id
	 *            The unique identifier of the node to remove.
	 * @return The removed node. If strict checking is disabled, it can return null
	 *         if the node to remove does not exist.
	 * @throws ElementNotFoundException
	 *             If no node matches the given identifier and strict checking is
	 *             enabled.
	 */
	default Node removeNode(String id) throws ElementNotFoundException {
		Node node = getNode(id);

		if (node == null) {
			if (isStrict())
				throw new ElementNotFoundException("Node \"" + id + "\"");

			return null;
		}

		return removeNode(node);
	}

	/**
	 * Removes a node.
	 * <p>
	 * An event is generated toward the listeners. Note that removing a node may
	 * remove all edges it is connected to. In this case corresponding events will
	 * also be generated toward the listeners.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends Node.
	 * The return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * <p>
	 * 
	 * <pre>
	 * ExtendedNode n = graph.removeNode(...);
	 * </pre>
	 * <p>
	 * the method will return an ExtendedNode. If no left part exists, method will
	 * just return a Node.
	 * </p>
	 *
	 * @param node
	 *            The node to be removed
	 * @return The removed node
	 */
	Node removeNode(Node node);

}