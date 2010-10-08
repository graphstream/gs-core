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
 * Proxy pipe.
 * 
 * <p>
 * A proxy is a kind of event buffer that allows to pass some kind of barrier.
 * The barrier can be a thread or a network for example. A proxy completely
 * decouple the source from the sink. The proxy buffers the source events and
 * when possible it sends them to the listeners at the sink. In other words, a
 * proxy is indirect, non synchronized and non blocking.
 * </p>
 * 
 * <p>
 * The usual source/sink mechanism is synchronized, direct and blocking : when
 * the event occurs, all listeners are called, and we have to wait they finish
 * to process these events to continue and send new events.
 * </p>
 * 
 * <p>
 * With proxies, there is a buffer often compared to a mail box. Each event
 * produced as source is buffered and when the sink is free to receive these
 * events it can check the mail box and empty it, thus receiving the pending
 * events. This way of doing is completely non synchronized and non blocking
 * (due to the mail box).
 * </p>
 * 
 * <p>
 * This way of doing allows for example to passe a thread frontier with a
 * minimum of synchronization : only the mail box has to be synchronized. And
 * the source and sink can most of the time run in parallel. Without such a
 * proxy, we would have to synchronize the whole graph, and threads would
 * consume their time waiting one another since most of the work in GraphStream
 * is centered on graphs.
 * </p>
 * 
 * <p>
 * For networks, this is the same thing : events are buffered before sending
 * them to the network. When the other end is ready it can check these events in
 * one operation.
 * </p>
 * 
 * <p>
 * However proxies have a limitation : they force the receiving end to check for
 * events regularly. This can be compared to "pumping" since the whole
 * GraphStream metaphor is a set of sources, pipes and sinks. Here instead of
 * flowing freely, the event stream must be pumped manually to receive it. This
 * is however most of the time not a problem since most work on graphs in
 * GraphStream is dynamic and runs iteratively.
 * </p>
 */
public interface ProxyPipe extends Pipe {
	/**
	 * Check if some events are pending and dispatch them to the registered
	 * outputs.
	 */
	void pump();
}