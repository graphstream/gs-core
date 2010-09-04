/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

/**
 * Graph classes (graphs, nodes, edges).
 * 
 * <p>
 * This is the main package of GraphStream. It defines interfaces that allow to manipulate graphs.
 * The most important interfaces are {@link org.graphstream.graph.Graph},
 * {@link org.graphstream.graph.Node} and {@link org.graphstream.graph.Edge}. All these
 * interfaces are child of {@link org.graphstream.graph.Element}. Elements are parts of graphs.
 * Each element can contain a list of attributes. Attributes can be arbitrary objects. They are
 * identified by arbitrary strings.
 * </p>
 * 
 * <p>
 * The attribute mechanism allows to extend the graph elements in many ways and is an easy method to
 * store data in graphs. However it is also possible to extend nodes and edges the usual way by
 * subclassing them. To do this the {@link org.graphstream.graph.NodeFactory} and
 * {@link org.graphstream.graph.EdgeFactory} can be used.
 * </p>
 * 
 * <p>
 * Indeed, one never create edges or nodes directly. The
 * {@link org.graphstream.graph.Graph#addNode(String)} and
 * {@link org.graphstream.graph.Graph#addEdge(String, String, String)} (or
 * {@link org.graphstream.graph.Graph#addEdge(String, String, String, boolean)}) methods are
 * used to insert new nodes and edges in the graph. The graph is able then able to create the node
 * and edges through the factories).
 * </p>
 * 
 * <p>
 * You can listen at each event occurring to a graph by implementing a Sink and
 * registering it in the graph. You can also subclass the SinkAdapter to implement
 * only the required methods. The GraphListenerProxy is a special kind of listeners that can
 * also register a set of Sinks. This class has several implementations that allow
 * to copy a graph in another, or to send graph events between threads for example.  
 * </p>
 * 
 * <p>
 * This package provides also some basic services on graphs, like iterators in breadth first of
 * depth first order, and a path class that allows to express a sequence of nodes and edges in the
 * graph.
 * </p>
 */
package org.graphstream.graph;