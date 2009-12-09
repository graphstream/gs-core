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

import java.util.Map;

import org.graphstream.io.GraphParseException;

/**
 * Dynamic graph reader listener.
 *
 * <p>A graph can be either static or dynamic. A static graph does not change
 * over time, whereas a dynamic one can vary. Each method of this interface
 * represents a variation in a graph. Such events are the apparition,
 * removal or change of a node or edge.</p>
 * 
 * <p>As dynamic graphs vary over time, a notion of time is introduced in this
 * interface by the {@link #stepBegins(double)} method. This allows to split the
 * graph description in discrete time steps. The argument of this method is the
 * current time. Each event received after a step event can be considered
 * occurring at this time.</p>
 * 
 * <p>Notice that a static and a dynamic graph can be described using this same
 * interface without changes. Indeed, one can listen at each event and directly
 * reflect changes in the graph (dynamic way), or listen at all events, and only
 * when no more events are available, reflect the changes (static way).</p> 
 * 
 * <p>Each method of the listener is declared to throw GraphParseException.
 * You can throw this exception when the data received, for example in
 * attributes, is not the one expected. This will interrupt the parser
 * (GraphReader) that will in turn throw a GraphParseException.</p>
 * 
 * <p>This interface is the evolution of the original GraphReaderListener.
 * It adds the ability to add or remove individual attributes to node, edges and
 * graphs. The GraphReaderListener is depreciated by this interface.</p>
 * 
 * @see org.graphstream.io.old.GraphReader
 */
public interface GraphReaderListenerExtended
{
	/**
	 * A graph attribute addition or removal has been read.
	 * @param attribute The name of the changed attribute
	 * @param value The new value for the attribute.
	 * @param removed True if the attribute is removed (in which case the value is null, however
	 *        note that a null value does not necessarily indicates a removed attribute).
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	void graphChanged( String attribute, Object value, boolean removed )
		throws GraphParseException;
	
	/**
	 * A node has been read. The node is identified by a string.
	 * @param id The node unique identifier.
	 * @param attributes The set of attributes to directly add to the node during its creation,
	 *        this is a set of pairs (name,attribute) where the name identifies the attributes,
	 *        this set can be null.
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	void nodeAdded( String id, Map<String,Object> attributes )
		throws GraphParseException;

	/**
	 * A node attribute addition or removal has been read.
	 * @param id The node identifier.
	 * @param attribute The name of the changed attribute
	 * @param value The new value for the attribute.
	 * @param removed True if the attribute is removed (in which case the value is null, however
	 *        note that a null value does not necessarily indicates a removed attribute).
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	void nodeChanged( String id, String attribute, Object value, boolean removed )
		throws GraphParseException;
	
	/**
	 * A node disappeared.
	 * @param id The node unique identifier.
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	void nodeRemoved( String id )
		throws GraphParseException;

	/**
	 * An edge has been read.
	 * @param id Edge identifier.
	 * @param from The source node identifier.
	 * @param to The target node identifier.
	 * @param directed If true the edge is directed from the "from" node to the "to" node.
	 * @param attributes The set of attributes to directly add to the edge during its creation,
	 *        this is a set of pairs (name,attribute) where the name identifies the attributes,
	 *        this set can be null.
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	void edgeAdded( String id, String from, String to, boolean directed, Map<String,Object> attributes )
		throws GraphParseException;
	
	/**
	 * A node attribute addition or removal has been read.
	 * @param id The edge identifier.
	 * @param attribute The name of the changed attribute
	 * @param value The new value for the attribute.
	 * @param removed True if the attribute is removed (in which case the value is null, however
	 *        note that a null value does not necessarily indicates a removed attribute).
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	void edgeChanged( String id, String attribute, Object value, boolean removed )
		throws GraphParseException;
	
	/**
	 * An edge disappeared.
	 * @param id The edge unique identifier.
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	void edgeRemoved( String id )
		throws GraphParseException;

	/**
	 * A new step (group of events) begins.
	 * @param time
	 * @throws GraphParseException
	 */
	void stepBegins( double time )
		throws GraphParseException;
	
	/**
	 * Something unknown has been read by the parser and is passed as is.
	 * @param unknown The data read.
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	void unknownEventDetected( String unknown )
		throws GraphParseException;
}