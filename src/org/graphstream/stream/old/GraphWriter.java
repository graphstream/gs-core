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
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.io.old;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Graph writer interface.
 * 
 * <p>
 * A graph writer allows to save a graph to a file or output stream. The writer
 * interface is format independent, however it is made to support dynamic
 * graphs. Writers are available for common graph formats, as well as graphical
 * outputs, etc.
 * </p>
 * 
 * <p>
 * This interface works by events. The output to a new file or stream must be
 * initiated using the {@link #begin(String, String)} or
 * {@link #begin(OutputStream, String)} methods. Then you declare nodes and
 * edges, change them, or even delete them. Finally, every graph file or stream
 * must imperatively be closed using the {@link #end()} method.
 * </p>
 * 
 * <p>
 * Although this interface is generic, it proposes some methods that may not
 * result in any output. The {@link #step(double)} method is probably the method
 * that will not have any counterpart in any known graph format, excepted the
 * DGS format.
 * </p>
 *
 * <p>
 * This interface allow to output not only the graph structure (nodes and
 * edges), but also the attributes stored on each node and edge. As when
 * outputting a graph, it is easy to pass the attribute set of nodes and edges
 * directly to {@link #addNode(String, Map)} and {@link #addEdge(String, String, String, boolean, Map)}
 * for example, methods have been added to allow attribute filtering. Indeed,
 * one does not necessarily need to output all the attributes stored in a graph.
 * Some attribute may be impossible to output, depending on the graph
 * format. 
 * </p>
 * 
 * <p>
 * The graph writers will do their best to output each attribute as precisely as
 * possible, but only few graph writers will be able to output arbitrary objects.
 * Some mays even not be able to output attributes at all. The writers specify
 * themselves what they do with attributes. Most of them will probably ignore
 * silently the attributes they cannot output, or transform the value in something
 * they can output (a string). Read the writers individual documentation for
 * information.
 * </p>
 * 
 * @since 20061208
 */
public interface GraphWriter
{	
	/**
	 * Start a new graph file. Each call to this method must imperatively be
	 * paired with a call to the {@link #end()} method.
	 * @param fileName Name of the file to create. If this name does not have an
	 *        extension, one is added automatically.
	 * @param graphName Name of the graph.
	 * @see #end()
	 * @throws IOException For any I/O error.
	 */
	void begin( String fileName, String graphName ) throws IOException;
	
	/**
	 * Start a new graph output. Each call to this method must imperatively be
	 * paired with a call to the {@link #end()} method.
	 * @param out Stream toward which the graph description is sent.
	 * @param graphName Name of the graph.
	 * @see #end()
	 * @throws IOException For any I/O error.
	 */
	void begin( OutputStream out, String graphName ) throws IOException;
	
	/**
	 * Close properly the graph file or stream. A call to this method is
	 * necessary when one of the begin() methods has been called.
	 * @see #begin(String, String)
	 * @see #begin(OutputStream, String)
	 * @throws IOException For any I/O error.
	 */
	void end() throws IOException;
	
	/**
	 * Declare a node and its attributes.
	 * @param id The node name.
	 * @param attributes A map of attributes, keys are string naming the
	 *        attributes, and values are arbitrary objects.
	 * @throws IOException For any I/O error.
	 */
	void addNode( String id, Map<String,Object> attributes ) throws IOException;
	
	/**
	 * Change the attributes of a node.
	 * @param id The node name.
	 * @param attributes The new set of attributes.
	 * @throws IOException For any I/O error.
	 */
	@Deprecated
	void changeNode( String id, Map<String,Object> attributes ) throws IOException;
	
	/**
	 * Change an attribute of a node.
	 * @param id The node identifier.
	 * @param attribute The attribute to change.
	 * @param value The value of the attribute.
	 * @param remove If true, the attribute must be removed (in which case the value is ignored,
	 *        use null instead).
	 * @throws IOException For any I/O error.
	 */
	void changeNode( String id, String attribute, Object value, boolean remove ) throws IOException;
	
	/**
	 * Delete a node.
	 * @param id The node name.
	 * @throws IOException For any I/O error.
	 */
	void delNode( String id ) throws IOException;
	
	/**
	 * Declare an edge and its attributes.
	 * @param id The edge name.
	 * @param node0Id Source node identifier.
	 * @param node1Id Destination node identifier.
	 * @param directed Is this edge directed ?.
	 * @throws IOException For any I/O error.
	 */
	void addEdge( String id, String node0Id, String node1Id, boolean directed, Map<String,Object> attributes ) throws IOException;
	
	/**
	 * Change the attributes of an edge.
	 * @param id The edge name.
	 * @param attributes The new set of attributes.
	 * @throws IOException For any I/O error.
	 */
	@Deprecated
	void changeEdge( String id, Map<String,Object> attributes ) throws IOException;
	
	/**
	 * Change an attribute of a edge.
	 * @param id The edge identifier.
	 * @param attribute The attribute to change.
	 * @param value The value of the attribute.
	 * @param remove If true, the attribute must be removed (in which case the value is ignored,
	 *        use null instead).
	 * @throws IOException For any I/O error.
	 */
	void changeEdge( String id, String attribute, Object value, boolean remove ) throws IOException;
	
	/**
	 * Delete an edge.
	 * @param id The edge name.
	 * @throws IOException For any I/O error.
	 */
	void delEdge( String id ) throws IOException;
	
	/**
	 * Declare a new step in the graph stream. Steps are a way to group events
	 * together. Not all graph formats support them.
	 * @param time The time associated to the step.
	 * @throws IOException For any I/O error.
	 */
	void step( double time ) throws IOException;
	
	/**
	 * Add attributes to the graph.
	 * @param attributes The attributes to add.
	 */
	@Deprecated
	void changeGraph( Map<String,Object> attributes ) throws IOException;
	
	/**
	 * Change an attribute of the graph.
	 * @param attribute The attribute to change.
	 * @param value The value of the attribute.
	 * @param remove If true, the attribute must be removed (in which case the value is ignored,
	 *        use null instead).
	 * @throws IOException For any I/O error.
	 */
	void changeGraph( String attribute, Object value, boolean remove ) throws IOException;

	/**
	 * Flush the output.
	 */
	void flush();
	
// Attribute filtering
	
	void unfilterNodeAttribute( String name );
	
	void unfilterEdgeAttribute( String name );

	void unfilterAllNodeAttributes();
	
	void unfilterAllEdgeAttributes();
	
	void unfilterAllAttributes();
	
	void filterNodeAttribute( String name );
	
	void filterEdgeAttribute( String name );
}