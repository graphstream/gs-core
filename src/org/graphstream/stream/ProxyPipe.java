/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
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
 * This way of doing allows for example to pass a thread frontier with a
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