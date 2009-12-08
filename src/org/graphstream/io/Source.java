/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.io;

import org.graphstream.graph.GraphAttributesListener;
import org.graphstream.graph.GraphElementsListener;
import org.graphstream.graph.GraphListener;

/**
 * Source of graph events.
 *
 * <p>An input is something that produces graph events (attributes and elements), but does
 * not contain a graph instance.</p>
 * 
 * @see Sink
 * @see Pipe
 */
public interface Source
{	
	/**
	 * Add a listener for all graph events (attributes and graph elements) coming from this input.
	 * This is similar to registering a listener for attributes an another for elements.
	 * @param listener The listener to register.
	 */
	void addGraphListener( GraphListener listener );
	
	/**
	 * Remove a graph listener.
	 * @param listener The listener to remove, if it does not exist, this is ignored silently.
	 */
	void removeGraphListener( GraphListener listener );
	
	/**
	 * Add a listener for attribute events only. Attribute events include attribute addition
	 * change and removal.
	 * @param listener The listener to register.
	 */
	void addGraphAttributesListener( GraphAttributesListener listener );
	
	/**
	 * Remove an attribute listener.
	 * @param listener The listener to remove, if it does not exist, this is ignored silently.
	 */
	void removeGraphAttributesListener( GraphAttributesListener listener );
	
	/**
	 * Add a listener for elements events only. Elements events include, addition and removal
	 * of nodes and edges, as well as step events.
	 * @param listener The listener to register.
	 */
	void addGraphElementsListener( GraphElementsListener listener );
	
	/**
	 * Remove an element listener.
	 * @param listener The listener to remove, if it does not exist, this is ignored silently.
	 */
	void removeGraphElementsListener( GraphElementsListener listener );
}