/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
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
package org.graphstream.stream.thread;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Sink;
import org.graphstream.stream.Source;
import org.graphstream.stream.SourceBase;
import org.miv.mbox.CannotPostException;
import org.miv.mbox.MBox;
import org.miv.mbox.MBoxListener;
import org.miv.mbox.MBoxStandalone;

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
 * {@link #pump()} method to dispatch events coming from the source to all
 * sinks registered (see the explanation in {@link org.graphstream.stream.ProxyPipe}).
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
public class ThreadProxyPipe extends SourceBase implements ProxyPipe,
		MBoxListener {

	/**
	 * Proxy id.
	 */
	protected String id;

	/**
	 * The event sender name, usually the graph name.
	 */
	protected String from;

	/**
	 * The message box used to exchange messages between the two threads.
	 */
	protected MBox events;

	/**
	 * Used only to remove the listener. We ensure this is done in the source
	 * thread.
	 */
	protected Source input;

	/**
	 * Signals that this proxy must be removed from the source input.
	 */
	protected boolean unregisterWhenPossible = false;

	/**
	 * New thread proxy pipe with no input.
	 */
	public ThreadProxyPipe() {
		this((Source) null);
	}

	/**
	 * Listen at an input in a given thread and redirect all events to
	 * GraphListeners that may be in another thread.
	 * 
	 * @param input
	 *            The source of graph events we listen at.
	 */
	public ThreadProxyPipe(Source input) {
		this(input, new MBoxStandalone());
	}

	/**
	 * Like {@link #ThreadProxyPipe(Source)}, but allow to share the message box
	 * with another message processor. This can be needed to share the same
	 * message stack, when message order is important.
	 * 
	 * @param input
	 *            The source of events we listen at.
	 * @param sharedMBox
	 *            The message box used to send and receive graph messages across
	 *            the thread boundary.
	 */
	public ThreadProxyPipe(Source input, MBox sharedMBox) {
		this.events = sharedMBox;
		this.from = "<in>";
		this.input = input;

		if (input != null)
			input.addSink(this);

		((MBoxStandalone) this.events).addListener(this);
	}

	/**
	 * Listen at an input graph in a given thread and redirect all events to
	 * GraphListeners that may be in another thread. By default, if the graph
	 * already contains some elements, they are "replayed". This means that
	 * events are sent to mimic the fact they just appeared.
	 * 
	 * @param inputGraph
	 *            The graph we listen at.
	 */
	public ThreadProxyPipe(Graph inputGraph) {
		this(inputGraph, true);
	}

	/**
	 * Like {@link #ThreadProxyPipe(Graph)} but allow to avoid replaying the
	 * graph.
	 * 
	 * @param inputGraph
	 *            The graph we listen at.
	 * @param replayGraph
	 *            If false, and if the input graph already contains element they
	 *            are not replayed.
	 */
	public ThreadProxyPipe(Graph inputGraph, boolean replayGraph) {
		this(inputGraph, null, replayGraph);
	}

	/**
	 * Like {@link #ThreadProxyPipe(Graph,boolean)} but allows to pass an
	 * initial listener, therefore specifying the input and output at once.
	 * 
	 * @param inputGraph
	 *            The graph we listen at.
	 * @param firstListener
	 *            The initial listener to register.
	 * @param replayGraph
	 *            If false, and if the input graph already contains element they
	 *            are not replayed.
	 */
	public ThreadProxyPipe(Graph inputGraph, Sink firstListener,
			boolean replayGraph) {
		this(inputGraph, firstListener, replayGraph, new MBoxStandalone());
	}

	/**
	 * Like {@link #ThreadProxyPipe(Graph,Sink,boolean)}, but allows to share
	 * the message box with another message processor. This can be needed to
	 * share the same message stack, when message order is important.
	 * 
	 * @param inputGraph
	 *            The graph we listen at.
	 * @param replayGraph
	 *            If false, and if the input graph already contains element they
	 *            are not replayed.
	 * @param sharedMBox
	 *            The message box used to send and receive graph messages across
	 *            the thread boundary.
	 */
	public ThreadProxyPipe(Graph inputGraph, Sink firstListener,
			boolean replayGraph, MBox sharedMBox) {
		this.events = sharedMBox;
		this.from = inputGraph.getId();
		this.input = inputGraph;

		if (firstListener != null)
			addSink(firstListener);

		if (replayGraph)
			replayGraph(inputGraph);

		input.addSink(this);
		((MBoxStandalone) this.events).addListener(this);
	}

	@Override
	public String toString() {
		String dest = "nil";

		if (attrSinks.size() > 0)
			dest = attrSinks.get(0).toString();

		return String.format("thread-proxy(from %s to %s)", from, dest);
	}

	/**
	 * Ask the proxy to unregister from the event input source (stop receive
	 * events) as soon as possible (when the next event will occur in the
	 * graph).
	 */
	public void unregisterFromSource() {
		unregisterWhenPossible = true;
	}

	/**
	 * This method must be called regularly in the output thread to check if the
	 * input source sent events. If some event occurred, the listeners will be
	 * called.
	 */
	public void pump() {
		((MBoxStandalone) events).processMessages();
	}

	/**
	 * Set of events sent via the message box.
	 */
	protected static enum GraphEvents {
		ADD_NODE, DEL_NODE, ADD_EDGE, DEL_EDGE,
		STEP, CLEARED,
		ADD_GRAPH_ATTR, CHG_GRAPH_ATTR, DEL_GRAPH_ATTR,
		ADD_NODE_ATTR, CHG_NODE_ATTR, DEL_NODE_ATTR,
		ADD_EDGE_ATTR, CHG_EDGE_ATTR, DEL_EDGE_ATTR
	};

	protected void replayGraph(Graph graph) {
		try {
			String graphId = "@replay";

			// Replay all graph attributes.

			if (graph.getAttributeKeySet() != null)
				for (String key : graph.getAttributeKeySet())
					events.post(from, GraphEvents.ADD_GRAPH_ATTR, graphId,
							sourceTime.newEvent(), key, graph.getAttribute(key));

			Thread.yield();
			
			// Replay all nodes and their attributes.

			for (Node node : graph) {
				events.post(from, GraphEvents.ADD_NODE, graphId,
						sourceTime.newEvent(), node.getId());

				if (node.getAttributeKeySet() != null)
					for (String key : node.getAttributeKeySet())
						events.post(from, GraphEvents.ADD_NODE_ATTR, graphId,
								sourceTime.newEvent(), node.getId(), key,
								node.getAttribute(key));
				Thread.yield();
			}

			// Replay all edges and their attributes.

			for (Edge edge : graph.getEachEdge()) {
				events.post(from, GraphEvents.ADD_EDGE, graphId, sourceTime
						.newEvent(), edge.getId(),
						edge.getSourceNode().getId(), edge.getTargetNode()
								.getId(), edge.isDirected());

				if (edge.getAttributeKeySet() != null)
					for (String key : edge.getAttributeKeySet())
						events.post(from, GraphEvents.ADD_EDGE_ATTR, graphId,
								sourceTime.newEvent(), edge.getId(), key,
								edge.getAttribute(key));
				Thread.yield();
			}
		} catch (CannotPostException e) {
			System.err
					.printf("GraphRendererRunner: cannot post message to listeners: %s%n",
							e.getMessage());
		}
	}

	protected boolean maybeUnregister() {
		if (unregisterWhenPossible) {
			if (input != null)
				input.removeSink(this);
			return true;
		}

		return false;
	}

	public void edgeAttributeAdded(String graphId, long timeId, String edgeId,
			String attribute, Object value) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.ADD_EDGE_ATTR, graphId, timeId,
					edgeId, attribute, value);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void edgeAttributeChanged(String graphId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.CHG_EDGE_ATTR, graphId, timeId,
					edgeId, attribute, oldValue, newValue);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void edgeAttributeRemoved(String graphId, long timeId,
			String edgeId, String attribute) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.DEL_EDGE_ATTR, graphId, timeId,
					edgeId, attribute);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void graphAttributeAdded(String graphId, long timeId,
			String attribute, Object value) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.ADD_GRAPH_ATTR, graphId, timeId,
					attribute, value);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void graphAttributeChanged(String graphId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.CHG_GRAPH_ATTR, graphId, timeId,
					attribute, oldValue, newValue);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void graphAttributeRemoved(String graphId, long timeId,
			String attribute) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.DEL_GRAPH_ATTR, graphId, timeId,
					attribute);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void nodeAttributeAdded(String graphId, long timeId, String nodeId,
			String attribute, Object value) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.ADD_NODE_ATTR, graphId, timeId,
					nodeId, attribute, value);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void nodeAttributeChanged(String graphId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.CHG_NODE_ATTR, graphId, timeId,
					nodeId, attribute, oldValue, newValue);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void nodeAttributeRemoved(String graphId, long timeId,
			String nodeId, String attribute) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.DEL_NODE_ATTR, graphId, timeId,
					nodeId, attribute);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void edgeAdded(String graphId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.ADD_EDGE, graphId, timeId, edgeId,
					fromNodeId, toNodeId, directed);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void edgeRemoved(String graphId, long timeId, String edgeId) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.DEL_EDGE, graphId, timeId, edgeId);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void graphCleared(String graphId, long timeId) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.CLEARED, graphId, timeId);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void nodeAdded(String graphId, long timeId, String nodeId) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.ADD_NODE, graphId, timeId, nodeId);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void nodeRemoved(String graphId, long timeId, String nodeId) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.DEL_NODE, graphId, timeId, nodeId);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	public void stepBegins(String graphId, long timeId, double step) {
		if (maybeUnregister())
			return;

		try {
			events.post(from, GraphEvents.STEP, graphId, timeId, step);
		} catch (CannotPostException e) {
			e.printStackTrace();
		}
	}

	// MBoxListener

	public void processMessage(String from, Object[] data) {
		// System.err.printf( "    %s.msg(%s, %s, %s, %s)%n", from, data[1],
		// data[2], data[0], data[3] );
		if (data[0].equals(GraphEvents.ADD_NODE)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String nodeId = (String) data[3];

			sendNodeAdded(graphId, timeId, nodeId);
		} else if (data[0].equals(GraphEvents.DEL_NODE)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String nodeId = (String) data[3];

			sendNodeRemoved(graphId, timeId, nodeId);
		} else if (data[0].equals(GraphEvents.ADD_EDGE)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String edgeId = (String) data[3];
			String fromId = (String) data[4];
			String toId = (String) data[5];
			boolean directed = (Boolean) data[6];

			sendEdgeAdded(graphId, timeId, edgeId, fromId, toId, directed);
		} else if (data[0].equals(GraphEvents.DEL_EDGE)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String edgeId = (String) data[3];

			sendEdgeRemoved(graphId, timeId, edgeId);
		} else if (data[0].equals(GraphEvents.STEP)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			double step = (Double) data[3];

			sendStepBegins(graphId, timeId, step);
		} else if (data[0].equals(GraphEvents.ADD_GRAPH_ATTR)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String attribute = (String) data[3];
			Object value = data[4];

			sendGraphAttributeAdded(graphId, timeId, attribute, value);
		} else if (data[0].equals(GraphEvents.CHG_GRAPH_ATTR)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String attribute = (String) data[3];
			Object oldValue = data[4];
			Object newValue = data[5];

			sendGraphAttributeChanged(graphId, timeId, attribute, oldValue,
					newValue);
		} else if (data[0].equals(GraphEvents.DEL_GRAPH_ATTR)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String attribute = (String) data[3];

			sendGraphAttributeRemoved(graphId, timeId, attribute);
		} else if (data[0].equals(GraphEvents.ADD_EDGE_ATTR)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String edgeId = (String) data[3];
			String attribute = (String) data[4];
			Object value = data[5];

			sendEdgeAttributeAdded(graphId, timeId, edgeId, attribute, value);
		} else if (data[0].equals(GraphEvents.CHG_EDGE_ATTR)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String edgeId = (String) data[3];
			String attribute = (String) data[4];
			Object oldValue = data[5];
			Object newValue = data[6];

			sendEdgeAttributeChanged(graphId, timeId, edgeId, attribute,
					oldValue, newValue);
		} else if (data[0].equals(GraphEvents.DEL_EDGE_ATTR)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String edgeId = (String) data[3];
			String attribute = (String) data[4];

			sendEdgeAttributeRemoved(graphId, timeId, edgeId, attribute);
		} else if (data[0].equals(GraphEvents.ADD_NODE_ATTR)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String nodeId = (String) data[3];
			String attribute = (String) data[4];
			Object value = data[5];

			sendNodeAttributeAdded(graphId, timeId, nodeId, attribute, value);
		} else if (data[0].equals(GraphEvents.CHG_NODE_ATTR)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String nodeId = (String) data[3];
			String attribute = (String) data[4];
			Object oldValue = data[5];
			Object newValue = data[6];

			sendNodeAttributeChanged(graphId, timeId, nodeId, attribute,
					oldValue, newValue);
		} else if (data[0].equals(GraphEvents.DEL_NODE_ATTR)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];
			String nodeId = (String) data[3];
			String attribute = (String) data[4];

			sendNodeAttributeRemoved(graphId, timeId, nodeId, attribute);
		} else if (data[0].equals(GraphEvents.CLEARED)) {
			String graphId = (String) data[1];
			Long timeId = (Long) data[2];

			sendGraphCleared(graphId, timeId);
		} else {
			System.err.printf("ThreadProxyFilter : Unknown message %s !!%n",
					data[0]);
		}
	}
}