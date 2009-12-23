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

package org.graphstream.ui;

import org.graphstream.stream.Sink;

/**
 * Helper class that allows to listen at a graph an either copy it or send events
 * across thread boundaries, for example.
 * 
 * <p>
 * This interface and its implementations has many usages. It acts as a proxy that allows to put a
 * listener on a graph and manipulate, translate, send these events to other graph listeners.
 * </p>
 * 
 * <p>The two main implementations of this class are a version that allows to maintain an exact
 * copy of the graph (an "input graph" and an "output graph"), and another that allows to do the
 * same but in addition is able to do this
 * across threads boundaries. This means that the copy of the graph, the output graph,
 * can be in another thread.
 * </p>
 * 
 * <p>
 * This class is (or maybe) "passive", you must check that events coming from graph are available
 * regularly by calling the
 * {@link #checkEvents()} method. Indeed, events may be buffered, or need a translation phase,
 * etc. This however is not always the case.
 * </p>
 * 
 * <p>
 * This method will check if some events occurred in the input graph
 * and will modify the output graph accordingly (if any) and forward events to each registered
 * graph listener.
 * </p>
 * 
 * @see org.graphstream.stream.Sink
 * @since 20061208
 */
public interface GraphListenerProxy extends Sink
{
// Commands

	/**
	 * Ask the proxy to unregister from the graph (stop receive events) as soon as possible
	 * (when the next event will occur in the graph).
	 */
	void unregisterFromGraph();
	
	/**
	 * Add a listener to the events of the input graph.
	 * @param listener The listener to call for each event in the input graph.
	 */
	void addGraphListener( Sink listener );
	
	/**
	 * Remove a listener. 
	 * @param listener The listener to remove.
	 */
	void removeGraphListener( Sink listener );
	
	/**
	 * This method must be called regularly to check if the input graph sent
	 * events. If some event occurred, the listeners will be called and the output graph
	 * given as argument will be modified (if any).
	 */
	void checkEvents();	
}