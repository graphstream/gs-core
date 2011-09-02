/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
package org.graphstream.graph;

import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;

import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;

// TODO describe indices at the beginning
// TODO review javadoc for methods that add/remove edges/nodes 

/**
 * An Interface that advises general purpose methods for handling graphs.
 * 
 * <p>
 * This interface is one of the main interfaces of GraphStream. It defines the
 * services provided by a graph structure. Graphs implementations must at least
 * implement this interface (but are free to provide more services).
 * </p>
 * 
 * <p>
 * With {@link org.graphstream.stream.Source},
 * {@link org.graphstream.stream.Sink} and {@link org.graphstream.stream.Pipe},
 * this interface is one of the most important. A graph is a
 * {@link org.graphstream.stream.Pipe} that buffers the graph events and present
 * the graph structure as it is actually.
 * </p>
 * 
 * <p>
 * In other words, it allows to browse the graph structure, to explore it, to
 * modify it, and to implement algorithms on it. This class can be seen as a
 * snapshot of a stream of event at current time.
 * </p>
 * 
 * <p>
 * With factories ({@link org.graphstream.graph.NodeFactory},
 * {@link org.graphstream.graph.EdgeFactory}), users can define their own models
 * of nodes or edges. Problem is that when you define such model, you want to
 * access to elements with the valid type, without cast if possible. To improve
 * the access to elements in such cases, Graph offers implicit genericity to
 * access nodes or edges. The following is an example of an access without
 * genericity :
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
 * 
 * With implicit genericity offers by Graph, this can be done easier:
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
 * 
 * </p>
 */
public interface Graph extends Element, Pipe, Iterable<Node> {
	// Access

	/**
	 * Get a node by its identifier. This method is implicitly generic and
	 * return something which extends Node. The return type is the one of the
	 * left part of the assignment. For example, in the following call :
	 * 
	 * <pre>
	 * ExtendedNode node = graph.getNode(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedNode node. If no left part exists,
	 * method will just return a Node.
	 * 
	 * @param id
	 *            Identifier of the node to find.
	 * @return The searched node or null if not found.
	 */
	<T extends Node> T getNode(String id);

	/**
	 * Get an edge by its identifier. This method is implicitly generic and
	 * return something which extends Edge. The return type is the one of the
	 * left part of the assignment. For example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge edge = graph.getEdge(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge edge. If no left part exists,
	 * method will just return an Edge.
	 * 
	 * @param id
	 *            Identifier of the edge to find.
	 * @return The searched edge or null if not found.
	 */
	<T extends Edge> T getEdge(String id);

	/**
	 * Number of nodes in this graph.
	 * 
	 * @return The number of nodes.
	 */
	int getNodeCount();

	/**
	 * Number of edges in this graph.
	 * 
	 * @return The number of edges.
	 */
	int getEdgeCount();

	/**
	 * Iterator on the set of nodes, in an undefined order. This method is
	 * implicitly generic and return an Iterator over something which extends
	 * Node. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedNode&gt; ite = graph.getNodeIterator();
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Node&gt;.
	 * 
	 * @return The iterator.
	 */
	<T extends Node> Iterator<T> getNodeIterator();

	/**
	 * Iterator on the set of edges, in an undefined order. This method is
	 * implicitly generic and return an Iterator over something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * Iterator&lt;ExtendedEdge&gt; ite = graph.getEdgeIterator();
	 * </pre>
	 * 
	 * the method will return an Iterator&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterator&lt;Edge&gt;.
	 * 
	 * @return The iterator.
	 */
	<T extends Edge> Iterator<T> getEdgeIterator();

	/**
	 * Set of nodes usable in a for-each instruction. This method is implicitly
	 * generic and return an Iterable over something which extends Node. The
	 * return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * Iterable&lt;ExtendedNode&gt; ite = graph.getEachNode();
	 * </pre>
	 * 
	 * the method will return an Iterable&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return an Iterable&lt;Node&gt;. It is possible
	 * to use it in a for-each loop by giving the parameter :
	 * 
	 * <pre>
	 * for (ExtendedNode n : graph.&lt;ExtendedNode&gt; getEachNode()) {
	 * 	// ...
	 * }
	 * </pre>
	 * 
	 * @return An "iterable" view of the set of nodes.
	 * @see #getNodeIterator()
	 * @see #getEachNode()
	 */
	<T extends Node> Iterable<? extends T> getEachNode();

	/**
	 * Set of edges usable in a for-each instruction. This method is implicitly
	 * generic and return an Iterable over something which extends Edge. The
	 * return type is the one of the left part of the assignment. For example,
	 * in the following call :
	 * 
	 * <pre>
	 * Iterable&lt;ExtendedNEdge&gt; ite = graph.getEachEdge();
	 * </pre>
	 * 
	 * the method will return an Iterable&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return an Iterable&lt;Edge&gt;. It is possible
	 * to use it in a for-each loop by giving the parameter :
	 * 
	 * <pre>
	 * for (ExtendedEdge e : graph.&lt;ExtendedEdge&gt; getEachEdge()) {
	 * 	// ...
	 * }
	 * </pre>
	 * 
	 * @return An "iterable" view of the set of edges.
	 * @see #getEdgeIterator()
	 * @see #getEdgeSet()
	 */
	<T extends Edge> Iterable<? extends T> getEachEdge();

	/**
	 * Unmodifiable view of the set of nodes. This method is implicitly generic
	 * and return a Collection of something which extends Node. The return type
	 * is the one of the left part of the assignment. For example, in the
	 * following call :
	 * 
	 * <pre>
	 * Collection&lt;ExtendedNode&gt; c = graph.getNodeSet();
	 * </pre>
	 * 
	 * the method will return a Collection&lt;ExtendedNode&gt;. If no left part
	 * exists, method will just return a Collection&lt;Node&gt;.
	 * 
	 * @return A set of nodes that can only be read, not changed.
	 * @see #getNodeIterator()
	 * @see #getEachNode()
	 */
	<T extends Node> Collection<T> getNodeSet();

	/**
	 * Unmodifiable view of the set of edges. This method is implicitly generic
	 * and return a Collection of something which extends Edge. The return type
	 * is the one of the left part of the assignment. For example, in the
	 * following call :
	 * 
	 * <pre>
	 * Collection&lt;ExtendedEdge&gt; c = graph.getEdgeSet();
	 * </pre>
	 * 
	 * the method will return a Collection&lt;ExtendedEdge&gt;. If no left part
	 * exists, method will just return a Collection&lt;Edge&gt;.
	 * 
	 * @return A set of edges that can only be read, not changed.
	 * @see #getEdgeIterator()
	 * @see #getEachEdge()
	 */
	<T extends Edge> Collection<T> getEdgeSet();

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
	 * Is strict checking enabled? If strict checking is enabled the graph
	 * checks for name space conflicts (e.g. insertion of two nodes with the
	 * same name), removal of non-existing elements, use of non existing
	 * elements (create an edge between two non existing nodes). Graph
	 * implementations are free to respect strict checking or not.
	 * 
	 * @return True if enabled.
	 */
	boolean isStrict();

	/**
	 * Is the automatic creation of missing elements enabled?. If enabled, when
	 * an edge is created and one or two of its nodes are not already present in
	 * the graph, the nodes are automatically created.
	 * 
	 * @return True if enabled.
	 */
	boolean isAutoCreationEnabled();

	/**
	 * If true, when accessing an attribute that does not exist (or is not of
	 * the expected type), a {@link NullAttributeException} is thrown. Else null
	 * is returned.
	 * 
	 * @return True if exceptions must be thrown when accessing a null
	 *         attribute.
	 */
	boolean nullAttributesAreErrors();

	/**
	 * The current step.
	 * 
	 * @return The step.
	 */
	double getStep();

	// Command

	/**
	 * Should a {@link NullAttributeException} be thrown when one tries to
	 * access a non existing attribute, or an attribute whose type is not the
	 * expected one?.
	 * 
	 * @param on
	 *            if true, exceptions will be thrown when accessing a non
	 *            existing attribute.
	 */
	void setNullAttributesAreErrors(boolean on);

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
	 * @see #isStrict()
	 * @param on
	 *            True or false.
	 */
	void setStrict(boolean on);

	/**
	 * Enable or disable the automatic creation of missing elements.
	 * 
	 * @see #isAutoCreationEnabled()
	 * @param on
	 *            True or false.
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
	 * eventually using the node factory provided). An event is generated toward
	 * the listeners. If strict checking is enabled, and a node already exists
	 * with this identifier, a singleton exception is raised. Else the error is
	 * silently ignored and the already existing node is returned.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Node. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedNode n = graph.addNode(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedNode. If no left part exists, method
	 * will just return a Node.
	 * </p>
	 * 
	 * @param id
	 *            Arbitrary and unique string identifying the node.
	 * @return The created node (or the already existing node).
	 * @throws IdAlreadyInUseException
	 *             If the identifier is already used.
	 */
	<T extends Node> T addNode(String id) throws IdAlreadyInUseException;

	/**
	 * Remove the node using its identifier.
	 * <p>
	 * An event is generated toward the listeners. Note that removing a node may
	 * remove all edges it is connected to. In this case corresponding events
	 * will also be generated toward the listeners.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Node. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedNode n = graph.removeNode(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedNode. If no left part exists, method
	 * will just return a Node.
	 * </p>
	 * 
	 * @param id
	 *            The unique identifier of the node to remove.
	 * @return The removed node, if strict checking is disabled, it can return
	 *         null if the node to remove does not exist.
	 * @complexity O(1)
	 * @throws ElementNotFoundException
	 *             If no node matches the given identifier.
	 */
	<T extends Node> T removeNode(String id) throws ElementNotFoundException;

	/**
	 * Add an undirected edge between nodes.
	 * <p>
	 * An event is sent toward the listeners. If strict checking is enabled and
	 * at least one of the two given nodes do not exist, a "not found" exception
	 * is raised. Else if the auto-creation feature is disabled, the error is
	 * silently ignored, and null is returned. If the auto-creation feature is
	 * enabled (see {@link #setAutoCreate(boolean)}) and one or two of the given
	 * nodes do not exist, they are automatically created.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = graph.addEdge(&quot;...&quot;, &quot;...&quot;, &quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param id
	 *            Unique an arbitrary string identifying the edge.
	 * @param node1
	 *            The first node identifier.
	 * @param node2
	 *            The second node identifier.
	 * 
	 * @return The newly created edge (this can return null, if strict checking
	 *         is disabled, auto-creation disabled, and one or two of the given
	 *         nodes do not exist).
	 * @throws IdAlreadyInUseException
	 *             If an edge already exist between 'from' and 'to', strict
	 *             checking is enabled and the graph is not a multi-graph.
	 * @throws ElementNotFoundException
	 *             If strict checking is enabled, and the 'from' or 'to' node is
	 *             not registered in the graph.
	 * @throws EdgeRejectedException
	 *             If strict checking is enabled and the endpoints of the edge
	 *             do not accept it. Typically this happens when trying to add
	 *             an edge between two already connected nodes in a single
	 *             graph.
	 */
	<T extends Edge> T addEdge(String id, String node1, String node2)
			throws IdAlreadyInUseException, ElementNotFoundException,
			EdgeRejectedException;

	/**
	 * Like {@link #addEdge(String, String, String)}, but this edge can be
	 * directed between the two given nodes. If directed, the edge goes in the
	 * 'from' -&gt; 'to' direction. An event is sent toward the listeners.
	 * 
	 * @param id
	 *            Unique an arbitrary string identifying the node.
	 * @param from
	 *            The source node identifier.
	 * @param to
	 *            The target node identifier.
	 * @param directed
	 *            Is the edge directed?.
	 * 
	 * @return The newly created edge (this can return null, if strict checking
	 *         is disabled, auto-creation disabled, and one or two of the given
	 *         nodes do not exist).
	 * @throws IdAlreadyInUseException
	 *             If an edge already exist between 'from' and 'to', strict
	 *             checking is enabled, and the graph is not a multi-graph.
	 * @throws ElementNotFoundException
	 *             If strict checking is enabled, and the 'from' or 'to' node is
	 *             not registered in the graph.
	 */
	<T extends Edge> T addEdge(String id, String from, String to,
			boolean directed) throws IdAlreadyInUseException,
			ElementNotFoundException;

	/**
	 * Remove an edge given the identifier of its two linked nodes.
	 * <p>
	 * If the edge is directed it is removed only if its source and destination
	 * nodes are identified by 'from' and 'to' respectively. If the graph is a
	 * multi-graph and there are several edges between the two nodes, one of the
	 * edge at random is removed. An event is sent toward the listeners. If
	 * strict checking is enabled and at least one of the two given nodes does
	 * not exist, a not found exception is raised. Else the error is silently
	 * ignored, and null is returned.
	 * </p>
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = graph.removeEdge(&quot;...&quot;, &quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param from
	 *            The origin node identifier to select the edge.
	 * @param to
	 *            The destination node identifier to select the edge.
	 * @return The removed edge, or null if strict checking is disabled and at
	 *         least one of the two given nodes does not exist.
	 * @throws ElementNotFoundException
	 *             If the 'from' or 'to' node is not registered in the graph and
	 *             strict checking is enabled.
	 */
	<T extends Edge> T removeEdge(String from, String to)
			throws ElementNotFoundException;

	/**
	 * Remove the edge knowing its identifier. An event is sent toward the
	 * listeners. If strict checking is enabled and the edge does not exist, a
	 * not found exception is raised. Else the error is silently ignored and
	 * null is returned.
	 * <p>
	 * This method is implicitly generic and return something which extends
	 * Edge. The return type is the one of the left part of the assignment. For
	 * example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge e = graph.removeEdge(&quot;...&quot;);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge. If no left part exists, method
	 * will just return an Edge.
	 * </p>
	 * 
	 * @param id
	 *            Identifier of the edge to remove.
	 * @return The removed edge, or null if strict checking is disabled and the
	 *         edge does not exist.
	 * @throws ElementNotFoundException
	 *             If no edge matches the identifier and strict checking is
	 *             enabled.
	 */
	<T extends Edge> T removeEdge(String id) throws ElementNotFoundException;

	/**
	 * <p>
	 * Since dynamic graphs are based on discrete event modifications, the
	 * notion of step is defined to simulate elapsed time between events. So a
	 * step is a event that occurs in the graph, it does not modify it but it
	 * gives a kind of timestamp that allows the tracking of the progress of the
	 * graph over the time.
	 * </p>
	 * <p>
	 * This kind of event is useful for dynamic algorithms that listen to the
	 * dynamic graph and need to measure the time in the graph's evolution.
	 * </p>
	 * 
	 * @param time
	 *            A numerical value that may give a timestamp to track the
	 *            evolution of the graph over the time.
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
	 * @return the list of {@link ElementSink} under the form of an iterable
	 *         object.
	 */
	Iterable<ElementSink> elementSinks();

	// Utility shortcuts (should be mixins or traits, what are you doing Mr Java
	// ?)
	// XXX use a Readable/Writable/Displayable interface for this ?

	/**
	 * Utility method to read a graph. This method tries to identify the graph
	 * format by itself and instantiates the corresponding reader automatically.
	 * If this process fails, a NotFoundException is raised.
	 * 
	 * @param filename
	 *            The graph filename (or URL).
	 * @throws ElementNotFoundException
	 *             If the file cannot be found or if the format is not
	 *             recognized.
	 * @throws GraphParseException
	 *             If there is a parsing error while reading the file.
	 * @throws IOException
	 *             If an input output error occurs during the graph reading.
	 */
	void read(String filename) throws IOException, GraphParseException,
			ElementNotFoundException;

	/**
	 * Utility method to read a graph using the given reader.
	 * 
	 * @param input
	 *            An appropriate reader for the filename.
	 * @param filename
	 *            The graph filename (or URL).
	 * @throws ElementNotFoundException
	 *             If the file cannot be found or if the format is not
	 *             recognised.
	 * @throws GraphParseException
	 *             If there is a parsing error while reading the file.
	 * @throws IOException
	 *             If an input/output error occurs during the graph reading.
	 */
	void read(FileSource input, String filename) throws IOException,
			GraphParseException;

	/**
	 * Utility method to write a graph in DGS format to a file.
	 * 
	 * @param filename
	 *            The file that will contain the saved graph (or URL).
	 * @throws IOException
	 *             If an input/output error occurs during the graph writing.
	 */
	void write(String filename) throws IOException;

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
	void write(FileSink output, String filename) throws IOException;

	/**
	 * Utility method that creates a new graph viewer, and register the graph in
	 * it. Notice that this method is a quick way to see a graph, and only this.
	 * It can be used to prototype a program, but may be limited. This method
	 * automatically launch a graph layout algorithm in its own thread to
	 * compute best node positions.
	 * 
	 * @see org.graphstream.ui.swingViewer.Viewer
	 * @see #display(boolean )
	 * @return a graph viewer that allows to command the viewer (it often run in
	 *         another thread).
	 */
	org.graphstream.ui.swingViewer.Viewer display();

	/**
	 * Utility method that creates a new graph viewer, and register the graph in
	 * it. Notice that this method is a quick way to see a graph, and only this.
	 * It can be used to prototype a program, but is very limited.
	 * 
	 * @param autoLayout
	 *            If true a layout algorithm is launched in its own thread to
	 *            compute best node positions.
	 * @see org.graphstream.ui.swingViewer.Viewer
	 * @see #display()
	 * @return a graph viewer that allows to command the viewer (it often run in
	 *         another thread).
	 */
	org.graphstream.ui.swingViewer.Viewer display(boolean autoLayout);

	// XXX Propositions of new methods

	/**
	 * Get a node by its index. This method is implicitly generic and
	 * return something which extends Node. The return type is the one of the
	 * left part of the assignment. For example, in the following call :
	 * 
	 * <pre>
	 * ExtendedNode node = graph.getNode(index);
	 * </pre>
	 * 
	 * the method will return an ExtendedNode node. If no left part exists,
	 * method will just return a Node.
	 * 
	 * @param index
	 *            Index of the node to find.
	 * @return The searched node or null if the index is out of bounds.
	 * @throws IndexOutOfBoundsException if strict checking is enabled and the index is less than 0 or greater
	 * than {@code getNodeCount() - 1}.
	 */
	<T extends Node> T getNode(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Get an edge by its index. This method is implicitly generic and
	 * return something which extends Edge. The return type is the one of the
	 * left part of the assignment. For example, in the following call :
	 * 
	 * <pre>
	 * ExtendedEdge edge = graph.getEdge(index);
	 * </pre>
	 * 
	 * the method will return an ExtendedEdge edge. If no left part exists,
	 * method will just return an Edge.
	 * 
	 * @param index
	 *            Index of the edge to find.
	 * @return The searched edge or null if not found.
	 * @throws IndexOutOfBoundsException if strict checking is enabled and the index is less than 0 or greater
	 * than {@code getNodeCount() - 1}.
	 */
	<T extends Edge> T getEdge(int index) throws IndexOutOfBoundsException;

	<T extends Edge> T addEdge(String id, int index1, int index2);

	<T extends Edge> T addEdge(String id, int fromIndex, int toIndex,
			boolean directed);

	<T extends Edge> T addEdge(String id, Node node1, Node node2);
	<T extends Edge> T addEdge(String id, Node from, Node to, boolean directed);

	<T extends Edge> T removeEdge(int index);

	<T extends Edge> T removeEdge(int fromIndex, int toIndex);
	<T extends Edge> T removeEdge(Node node1, Node node2);

	<T extends Edge> T removeEdge(Edge edge);

	<T extends Node> T removeNode(int index);

	<T extends Node> T removeNode(Node node);
}