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

package org.miv.graphstream.io2;

import org.miv.graphstream.graph.GraphAttributesListener;
import org.miv.graphstream.graph.GraphElementsListener;
import org.miv.graphstream.graph.GraphListener;

/**
 * Interface for all inputs that can support synchronisation mechanisms.
 * 
 * <p>Synchronisation happens when an input X is connected to an output Y but
 * Y is also an input connected to X as an output. In other words, Y listens at X
 * but X listens at Y.</p>
 * 
 * <pre>
 *      +------------------> Y
 *      |                    |
 * 	    X <------------------+
 * </pre>
 * 
 * <p>In such a configuration it is easy to fall in an endless loop where an event
 * appearing in X will be propagated to Y that will send it to X that will send it to
 * Y, etc.</p>
 * 
 * <p>GraphStream has two mechanisms to avoid such problems. The first one is the "events on
 * change" rule. This means that an event is generated only if a change really occurred. If the
 * modification to an input did not really resulted in a modification no event is generated.
 * For example, an attribute was set to its same old value, or an existing node was added anew.</p>
 * 
 * <p>This first mechanism is often sufficient if X and Y are in the same thread since events
 * are processed in order. However if X and Y are in distinct threads or on distinct machines,
 * events can be processed in packets leading to an "echo" or "feedback" phenomenon : in few
 * words the addition and just after removal of an element for example will generate two events
 * that will arrive later on the second thread that will generate these two events anew etc.
 * The "events on change" rule cannot detect such problems.</p>
 * 
 * <p>Furthermore, it is a waste of time to wait for an event to go from X to Y then again
 * to X to stop propagating in the "event on change" rule.</p>
 * 
 * <p>Therefore, to avoid these problems it is possible to explicitly synchronise X and Y.
 * The way each one know the other depends on them, however they rely on one mechanism : muting.</p>
 * 
 * <p>Muting allow an input to avoid sending the next event that will occur to a specific listener
 * that is the source of the event.</p>
 * 
 * <p>Imagine some change occurs on X. Normally an event is unconditionally generated to all
 * listeners (outputs), therefore to Y. But if the change originate from Y we can
 * ask Y to be ignored by X when sending back the event.</p>
 */
public interface SynchronizableInput
{
	/**
	 * Avoid the next event to be propagated to the given listener. Once this event has been
	 * processed, the listener will receive other events normally. This method is used in
	 * synchronisation mechanism to avoid recursive loops.
	 * @param listener The listener to mute only for the next event.
	 */
	void muteSource( GraphListener listener );
	
	/**
	 * Avoid the next event to be propagated to the given listener. Once this event has been
	 * processed, the listener will receive other events normally. This method is used in
	 * synchronisation mechanism to avoid recursive loops.
	 * @param listener The listener to mute only for the next event.
	 */
	void muteSource( GraphAttributesListener listener );
	
	/**
	 * Avoid the next event to be propagated to the given listener. Once this event has been
	 * processed, the listener will receive other events normally. This method is used in
	 * synchronisation mechanism to avoid recursive loops.
	 * @param listener The listener to mute only for the next event.
	 */
	void muteSource( GraphElementsListener listener );
}