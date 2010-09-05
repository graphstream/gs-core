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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.stream;


/**
 * Source of graph events.
 *
 * <p>An source is something that produces graph events (attributes and elements), but does
 * not contain a graph instance.</p>
 * 
 * @see Sink
 * @see Pipe
 */
public interface Source
{	
	/**
	 * Add a sink for all graph events (attributes and graph elements) coming from this source.
	 * This is similar to registering a sink for attributes an another for elements.
	 * @param sink The sink to register.
	 */
	void addSink( Sink sink );
	
	/**
	 * Remove a sink.
	 * @param sink The sink to remove, if it does not exist, this is ignored silently.
	 */
	void removeSink( Sink sink );
	
	/**
	 * Add a sink for attribute events only. Attribute events include attribute addition
	 * change and removal.
	 * @param sink The sink to register.
	 */
	void addAttributeSink( AttributeSink sink );
	
	/**
	 * Remove an attribute sink.
	 * @param sink The sink to remove, if it does not exist, this is ignored silently.
	 */
	void removeAttributeSink( AttributeSink sink );
	
	/**
	 * Add a sink for elements events only. Elements events include, addition and removal
	 * of nodes and edges, as well as step events.
	 * @param sink The sink to register.
	 */
	void addElementSink( ElementSink sink );
	
	/**
	 * Remove an element sink.
	 * @param sink The sink to remove, if it does not exist, this is ignored silently.
	 */
	void removeElementSink( ElementSink sink );
	
	/**
	 * Remove all listener element sinks.
	 */
	void clearElementSinks();
	
	/**
	 * Remove all listener attribute sinks.
	 */
	void clearAttributeSinks();
	
	/**
	 * Remove all listener sinks.
	 */
	void clearSinks();
}