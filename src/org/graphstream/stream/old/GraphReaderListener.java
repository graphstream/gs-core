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
 * @see org.graphstream.io.old.GraphReader
 * @since 20040911
 */
public interface GraphReaderListener
{
// Commands
	
	/**
	 * A node has been added.
	 * @param id The node unique identifier.
	 * @param attributes A set of pairs (name,attribute) where the name identifies the attributes or
	 *  null if none.
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	void nodeAdded( String id, Map<String,Object> attributes )
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
	 * @param id BufferedEdge identifier.
	 * @param from The source node identifier.
	 * @param to The target node identifier.
	 * @param directed If true the edge is directed from the "from" node to the "to" node.
	 * @param attributes A set of pairs (name,attribute) where the name identifies the attributes or null if none.
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	void edgeAdded( String id, String from, String to, boolean directed, Map<String,Object> attributes )
		throws GraphParseException;
	
	/**
	 * An edge disappeared.
	 * @param id The edge unique identifier.
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	void edgeRemoved( String id )
		throws GraphParseException;
/*	
	void graphAttributeAdded( String attribute, Object value ) throws GraphParseException;
	
	void graphAttributeRemoved( String attribute ) throws GraphParseException;
	
	void nodeAttributeAdded( String nodeId, String attribute, Object value ) throws GraphParseException;
	
	void nodeAttributeRemoved( String nodeId, String attribute ) throws GraphParseException;
	
	void edgeAttributeAdded( String edgeId, String attribute, Object value ) throws GraphParseException;
	
	void edgeAttributeRemoved( String edgeId, String attribute ) throws GraphParseException;
*/
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

// Depreciated
	
	/**
	 * A graph attribute has been read.
	 * @param attributes A set of pairs (name,attribute) where the name identifies the attributes or
	 *  null if none.
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	@Deprecated
	void graphChanged( Map<String,Object> attributes )
		throws GraphParseException;

	/**
	 * Any of the attributes of the node changed. 
	 * @param id The node unique identifier.
	 * @param attributes A set of pairs (name,attribute) where the name identifies the attributes or
	 *  null if none.
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	@Deprecated
	void nodeChanged( String id, Map<String,Object> attributes )
		throws GraphParseException;
	
	/**
	 * Any of the attributes changed.
	 * @param id The edge unique identifier.
	 * @param attributes A set of pairs (name,attribute) where the name identifies the attributes or null if none.
	 * @throws GraphParseException If something you expect from the read event did not occurred.
	 */
	@Deprecated
	void edgeChanged( String id, Map<String,Object> attributes )
		throws GraphParseException;
}