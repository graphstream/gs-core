/*
 * Copyright 2006 - 2015
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
package org.graphstream.stream.thread;

import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Replayable;
import org.graphstream.stream.Replayable.Controller;
import org.graphstream.stream.Source;
import org.graphstream.stream.SourceBase;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * Filter that allows to pass graph events between two threads without explicit
 * synchronization.
 *
 * <p>
 * This filter allows to register it as an output for some source of events in a
 * source thread (hereafter called the input thread) and to register listening
 * outputs in a destination thread (hereafter called the sink thread).
 * </p>
 *
 * <pre>
 *                       |
 *   Source ---> ThreadProxyFilter ----> Sink
 *  Thread 1             |              Thread 2
 *                       |
 * </pre>
 *
 * <p>
 * In other words, this class allows to listen in a sink thread graph events
 * that are produced in another source thread without any explicit
 * synchronization on the source of events.
 * </p>
 *
 * <p>
 * The only restriction is that the sink thread must regularly call the
 * {@link #pump()} method to dispatch events coming from the source to all sinks
 * registered (see the explanation in {@link org.graphstream.stream.ProxyPipe}).
 * </p>
 *
 * <p>
 * You can register any kind of input as source of event, but if the input is a
 * graph, then you can choose to "replay" all the content of the graph so that
 * at the other end of the filter, all outputs receive the complete content of
 * the graph. This is the default behavior if this filter is constructed with a
 * graph as input.
 * </p>
 */
public class ThreadProxyPipe extends SourceBase implements ProxyPipe {

	/**
	 * class level logger
	 */
	private static final Logger logger = Logger.getLogger(ThreadProxyPipe.class.getSimpleName());

	/**
	 * Proxy id.
	 */
	protected String id;

	/**
	 * The message box used to exchange messages between the two threads.
	 */
	protected LinkedList<GraphEvents> events;
	protected LinkedList<Object[]> eventsData;

	protected ReentrantLock lock;
	protected Condition notEmpty;

	public static ThreadProxyPipe create() {
		return new ThreadProxyPipe();
	}

	public static ThreadProxyPipe create(Source source) {
		ThreadProxyPipe tpp = new ThreadProxyPipe();
		source.addSink(tpp);
		return tpp;
	}

	public static ThreadProxyPipe createAndReplay(Source source) {
		Objects.requireNonNull(source, "Source must not be null");
		ThreadProxyPipe tpp = new ThreadProxyPipe();

		source.addSink(tpp);

		if (source instanceof Replayable) {
			Controller rc = ((Replayable) source).getReplayController();
			rc.addSink(tpp);
			rc.replay();
		}

		return tpp;
	}

	public ThreadProxyPipe() {
		this.events = new LinkedList<>();
		this.eventsData = new LinkedList<>();
		this.lock = new ReentrantLock();
		this.notEmpty = this.lock.newCondition();
	}

	@Override
	public String toString() {
		String dest = "nil";

		if (attrSinks.size() > 0) {
			dest = attrSinks.get(0).toString();
		}

		return String.format("thread-proxy(to %s)", dest);
	}

	/**
	 * This method must be called regularly in the output thread to check if
	 * the input source sent events. If some event occurred, the listeners
	 * will be called.
	 */
	@Override
	public void pump() {
		GraphEvents e = null;
		Object[] data = null;

		do {
			lock.lock();

			try {
				e = events.poll();
				data = eventsData.poll();
			} finally {
				lock.unlock();
			}

			if (e != null) {
				processMessage(e, data);
			}
		} while (e != null);
	}

	@Override
	public void blockingPump() throws InterruptedException {
		blockingPump(0);
	}

	@Override
	public void blockingPump(long timeout) throws InterruptedException {
		GraphEvents e;
		Object[] data;

		lock.lock();

		try {
			if (timeout > 0) {
				while (events.size() == 0) {
					notEmpty.await(timeout, TimeUnit.MILLISECONDS);
				}
			} else {
				while (events.size() == 0) {
					notEmpty.await();
				}
			}
		} finally {
			lock.unlock();
		}

		do {
			lock.lock();

			try {
				e = events.poll();
				data = eventsData.poll();
			} finally {
				lock.unlock();
			}

			if (e != null) {
				processMessage(e, data);
			}
		} while (e != null);
	}

	public boolean hasPostRemaining() {
		boolean r = true;
		lock.lock();

		try {
			r = events.size() > 0;
		} finally {
			lock.unlock();
		}

		return r;
	}

	/**
	 * Set of events sent via the message box.
	 */
	protected static enum GraphEvents {
		ADD_NODE, DEL_NODE, ADD_EDGE, DEL_EDGE, STEP, CLEARED, ADD_GRAPH_ATTR, CHG_GRAPH_ATTR, DEL_GRAPH_ATTR, ADD_NODE_ATTR, CHG_NODE_ATTR, DEL_NODE_ATTR, ADD_EDGE_ATTR, CHG_EDGE_ATTR, DEL_EDGE_ATTR
	};

	protected void post(GraphEvents e, Object... data) {
		lock.lock();

		try {
			events.add(e);
			eventsData.add(data);

			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

	public void edgeAttributeAdded(String graphId, long timeId, String edgeId,
		String attribute, Object value) {
		post(GraphEvents.ADD_EDGE_ATTR, graphId, timeId, edgeId, attribute,
			value);
	}

	public void edgeAttributeChanged(String graphId, long timeId,
		String edgeId, String attribute, Object oldValue, Object newValue) {
		post(GraphEvents.CHG_EDGE_ATTR, graphId, timeId, edgeId, attribute,
			oldValue, newValue);
	}

	public void edgeAttributeRemoved(String graphId, long timeId,
		String edgeId, String attribute) {
		post(GraphEvents.DEL_EDGE_ATTR, graphId, timeId, edgeId, attribute);
	}

	public void graphAttributeAdded(String graphId, long timeId,
		String attribute, Object value) {
		post(GraphEvents.ADD_GRAPH_ATTR, graphId, timeId, attribute, value);
	}

	public void graphAttributeChanged(String graphId, long timeId,
		String attribute, Object oldValue, Object newValue) {
		post(GraphEvents.CHG_GRAPH_ATTR, graphId, timeId, attribute, oldValue,
			newValue);
	}

	public void graphAttributeRemoved(String graphId, long timeId,
		String attribute) {
		post(GraphEvents.DEL_GRAPH_ATTR, graphId, timeId, attribute);
	}

	public void nodeAttributeAdded(String graphId, long timeId, String nodeId,
		String attribute, Object value) {
		post(GraphEvents.ADD_NODE_ATTR, graphId, timeId, nodeId, attribute,
			value);
	}

	public void nodeAttributeChanged(String graphId, long timeId,
		String nodeId, String attribute, Object oldValue, Object newValue) {
		post(GraphEvents.CHG_NODE_ATTR, graphId, timeId, nodeId, attribute,
			oldValue, newValue);
	}

	public void nodeAttributeRemoved(String graphId, long timeId,
		String nodeId, String attribute) {
		post(GraphEvents.DEL_NODE_ATTR, graphId, timeId, nodeId, attribute);
	}

	public void edgeAdded(String graphId, long timeId, String edgeId,
		String fromNodeId, String toNodeId, boolean directed) {
		post(GraphEvents.ADD_EDGE, graphId, timeId, edgeId, fromNodeId,
			toNodeId, directed);
	}

	public void edgeRemoved(String graphId, long timeId, String edgeId) {
		post(GraphEvents.DEL_EDGE, graphId, timeId, edgeId);
	}

	public void graphCleared(String graphId, long timeId) {
		post(GraphEvents.CLEARED, graphId, timeId);
	}

	public void nodeAdded(String graphId, long timeId, String nodeId) {
		post(GraphEvents.ADD_NODE, graphId, timeId, nodeId);
	}

	public void nodeRemoved(String graphId, long timeId, String nodeId) {
		post(GraphEvents.DEL_NODE, graphId, timeId, nodeId);
	}

	public void stepBegins(String graphId, long timeId, double step) {
		post(GraphEvents.STEP, graphId, timeId, step);
	}

	protected void processMessage(GraphEvents e, Object[] data) {
		String graphId, elementId, attribute;
		Long timeId;
		Object newValue, oldValue;

		switch (e) {
			case ADD_NODE:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				elementId = (String) data[2];

				sendNodeAdded(graphId, timeId, elementId);
				break;
			case DEL_NODE:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				elementId = (String) data[2];

				sendNodeRemoved(graphId, timeId, elementId);
				break;
			case ADD_EDGE:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				elementId = (String) data[2];

				String fromId = (String) data[3];
				String toId = (String) data[4];
				boolean directed = (Boolean) data[5];

				sendEdgeAdded(graphId, timeId, elementId, fromId, toId, directed);
				break;
			case DEL_EDGE:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				elementId = (String) data[2];

				sendEdgeRemoved(graphId, timeId, elementId);
				break;
			case STEP:
				graphId = (String) data[0];
				timeId = (Long) data[1];

				double step = (Double) data[2];

				sendStepBegins(graphId, timeId, step);
				break;
			case ADD_GRAPH_ATTR:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				attribute = (String) data[2];
				newValue = data[3];

				sendGraphAttributeAdded(graphId, timeId, attribute, newValue);
				break;
			case CHG_GRAPH_ATTR:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				attribute = (String) data[2];
				oldValue = data[3];
				newValue = data[4];

				sendGraphAttributeChanged(graphId, timeId, attribute, oldValue,
					newValue);
				break;
			case DEL_GRAPH_ATTR:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				attribute = (String) data[2];

				sendGraphAttributeRemoved(graphId, timeId, attribute);
				break;
			case ADD_EDGE_ATTR:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				elementId = (String) data[2];
				attribute = (String) data[3];
				newValue = data[4];

				sendEdgeAttributeAdded(graphId, timeId, elementId, attribute,
					newValue);
				break;
			case CHG_EDGE_ATTR:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				elementId = (String) data[2];
				attribute = (String) data[3];
				oldValue = data[4];
				newValue = data[5];

				sendEdgeAttributeChanged(graphId, timeId, elementId, attribute,
					oldValue, newValue);
				break;
			case DEL_EDGE_ATTR:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				elementId = (String) data[2];
				attribute = (String) data[3];

				sendEdgeAttributeRemoved(graphId, timeId, elementId, attribute);
				break;
			case ADD_NODE_ATTR:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				elementId = (String) data[2];
				attribute = (String) data[3];
				newValue = data[4];

				sendNodeAttributeAdded(graphId, timeId, elementId, attribute,
					newValue);
				break;
			case CHG_NODE_ATTR:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				elementId = (String) data[2];
				attribute = (String) data[3];
				oldValue = data[4];
				newValue = data[5];

				sendNodeAttributeChanged(graphId, timeId, elementId, attribute,
					oldValue, newValue);
				break;
			case DEL_NODE_ATTR:
				graphId = (String) data[0];
				timeId = (Long) data[1];
				elementId = (String) data[2];
				attribute = (String) data[3];

				sendNodeAttributeRemoved(graphId, timeId, elementId, attribute);
				break;
			case CLEARED:
				graphId = (String) data[0];
				timeId = (Long) data[1];

				sendGraphCleared(graphId, timeId);
				break;
			default:
				logger.warning(String.format("Unknown message %s.", e));
				break;
		}
	}
}
