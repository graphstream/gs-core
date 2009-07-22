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

/**
 * Proxy filter.
 * 
 * <p>
 * A proxy is a kind of event buffer that allows to pass a barrier. The barrier can be a thread,
 * or a network for example. A proxy completely decouple the input from the output. The proxy
 * buffers the input events and when possible it sends them to the listeners at the output.
 * In other words, a proxy is indirect, non synchronised and non blocking. 
 * </p>
 * 
 * <p>
 * The usual input/output mechanism is synchronised, direct and blocking : when the event occurs,
 * all listeners are called, and we have to wait they finish to process these events to continue
 * and send new events. 
 * </p>
 * 
 * <p>
 * With proxyes, there is a buffer often compared to a mail box. Each event produced as input is
 * buffered and when the output is free to receive these events it can check the mail box and
 * empty it, thus receiving the pending events. This way of doing is completely non synchronised
 * and non blocking (due to the mail box). 
 * </p>
 * 
 * <p>
 * This way of doing allows for example to passe a thread frontier with a minimum of
 * synchronisation : only the mail box has to be synchronised. And the input and output can most
 * of the time run in parallel. Without such a proxy, we would have to synchronise the whole
 * graph, and threads would consume their time waiting one another since most of the work in
 * GraphStream is centred on graphs. 
 * </p>
 * 
 * <p>
 * For network, this is the same thing : events are buffered before sending them to the network.
 * When the other end is ready it can check these events in one operation. 
 * </p>
 * 
 * <p>However proxyes have a limitation : they force the receiving end to check for events 
 * regularly. This is however most of the time not a problem since most work on graphs in
 * GraphStream is dynamic and runs iteratively.</p>
 */
public interface ProxyFilter extends Filter
{
	/**
	 * Check if some events are pending and dispatch them to the registered outputs.
	 */
	void checkEvents();
}
