/*
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

/**
 * @since 2011-11-25
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.util;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Stack;

import org.graphstream.stream.Sink;

/**
 * A sink that can be used to display event in a PrintStream like System.out.
 * Format of messages can be customized, inserting keywords quoted with '%' in
 * the format.
 * 
 * '%sourceId%' and '%timeId%' keywords are defined for each event. Following
 * defines keywords available for each event types:
 * <dl>
 * <dt>ADD_NODE</dt>
 * <dd>
 * <ul>
 * <li>%nodeId%</li>
 * </ul>
 * </dd>
 * <dt>ADD_NODE_ATTRIBUTE</dt>
 * <dd>
 * <ul>
 * <li>%nodeId%</li>
 * <li>%attributeId%</li>
 * <li>%value%</li>
 * </ul>
 * </dd>
 * <dt>SET_NODE_ATTRIBUTE</dt>
 * <dd>
 * <ul>
 * <li>%nodeId%</li>
 * <li>%attributeId%</li>
 * <li>%value%</li>
 * </ul>
 * </dd>
 * <dt>DEL_NODE_ATTRIBUTE</dt>
 * <dd>
 * <ul>
 * <li>%nodeId%</li>
 * <li>%attributeId%</li>
 * </ul>
 * </dd>
 * <dt>DEL_NODE</dt>
 * <dd>
 * <ul>
 * <li>%nodeId%</li>
 * </ul>
 * </dd>
 * <dt>ADD_EDGE</dt>
 * <dd>
 * <ul>
 * <li>%edgeId%</li>
 * <li>%source%</li>
 * <li>%target%</li>
 * <li>%directed%</li>
 * </ul>
 * </dd>
 * <dt>ADD_EDGE_ATTRIBUTE</dt>
 * <dd>
 * <ul>
 * <li>%edgeId%</li>
 * <li>%attributeId%</li>
 * <li>%value%</li>
 * </ul>
 * </dd>
 * <dt>SET_EDGE_ATTRIBUTE</dt>
 * <dd>
 * <ul>
 * <li>%edgeId%</li>
 * <li>%attributeId%</li>
 * <li>%value%</li>
 * </ul>
 * </dd>
 * <dt>DEL_EDGE_ATTRIBUTE</dt>
 * <dd>
 * <ul>
 * <li>%edgeId%</li>
 * <li>%attributeId%</li>
 * </ul>
 * </dd>
 * <dt>DEL_EDGE</dt>
 * <dd>
 * <ul>
 * <li>%edgeId%</li>
 * </ul>
 * </dd>
 * <dt>ADD_GRAPH_ATTRIBUTE</dt>
 * <dd>
 * <ul>
 * <li>%attributeId%</li>
 * <li>%value%</li>
 * </ul>
 * </dd>
 * <dt>SET_GRAPH_ATTRIBUTE</dt>
 * <dd>
 * <ul>
 * <li>%attributeId%</li>
 * <li>%value%</li>
 * </ul>
 * </dd>
 * <dt>DEL_GRAPH_ATTRIBUTE</dt>
 * <dd>
 * <ul>
 * <li>%attributeId%</li>
 * </ul>
 * </dd>
 * <dt>CLEAR</dt>
 * <dd></dd>
 * <dt>STEP_BEGINS</dt>
 * <dd>
 * <ul>
 * <li>%step%</li>
 * </ul>
 * </dd>
 * </dl>
 */
public class VerboseSink implements Sink {
	public static final String DEFAULT_AN_FORMAT = "%prefix%[%sourceId%:%timeId%] add node \"%nodeId%\"%suffix%";
	public static final String DEFAULT_CNA_FORMAT = "%prefix%[%sourceId%:%timeId%] set node \"%nodeId%\" +\"%attributeId%\"=%value%%suffix%";
	public static final String DEFAULT_CNC_FORMAT = "%prefix%[%sourceId%:%timeId%] set node \"%nodeId%\" \"%attributeId%\"=%value%%suffix%";
	public static final String DEFAULT_CNR_FORMAT = "%prefix%[%sourceId%:%timeId%] set node \"%nodeId%\" -\"%attributeId%\"%suffix%";
	public static final String DEFAULT_DN_FORMAT = "%prefix%[%sourceId%:%timeId%] remove node \"%nodeId%\"%suffix%";

	public static final String DEFAULT_AE_FORMAT = "%prefix%[%sourceId%:%timeId%] add edge \"%edgeId%\" : \"%source%\" -- \"%target%\"%suffix%";
	public static final String DEFAULT_CEA_FORMAT = "%prefix%[%sourceId%:%timeId%] set edge \"%edgeId%\" +\"%attributeId%\"=%value%%suffix%";
	public static final String DEFAULT_CEC_FORMAT = "%prefix%[%sourceId%:%timeId%] set edge \"%edgeId%\" \"%attributeId%\"=%value%%suffix%";
	public static final String DEFAULT_CER_FORMAT = "%prefix%[%sourceId%:%timeId%] set edge \"%edgeId%\" -\"%attributeId%\"%suffix%";
	public static final String DEFAULT_DE_FORMAT = "%prefix%[%sourceId%:%timeId%] remove edge \"%edgeId%\"%suffix%";

	public static final String DEFAULT_CGA_FORMAT = "%prefix%[%sourceId%:%timeId%] set +\"%attributeId%\"=%value%%suffix%";
	public static final String DEFAULT_CGC_FORMAT = "%prefix%[%sourceId%:%timeId%] set \"%attributeId%\"=%value%%suffix%";
	public static final String DEFAULT_CGR_FORMAT = "%prefix%[%sourceId%:%timeId%] set -\"%attributeId%\"%suffix%";

	public static final String DEFAULT_CL_FORMAT = "%prefix%[%sourceId%:%timeId%] clear%suffix%";
	public static final String DEFAULT_ST_FORMAT = "%prefix%[%sourceId%:%timeId%] step %step% begins%suffix%";

	/*
	 * Shortcut to use HashMap<String, Object>.
	 */
	private static class Args extends HashMap<String, Object> {
		private static final long serialVersionUID = 3064164898156692557L;
	}

	/**
	 * Enumeration defining type of events.
	 * 
	 */
	public static enum EventType {
		ADD_NODE, ADD_NODE_ATTRIBUTE, SET_NODE_ATTRIBUTE, DEL_NODE_ATTRIBUTE, DEL_NODE, ADD_EDGE, ADD_EDGE_ATTRIBUTE, SET_EDGE_ATTRIBUTE, DEL_EDGE_ATTRIBUTE, DEL_EDGE, ADD_GRAPH_ATTRIBUTE, SET_GRAPH_ATTRIBUTE, DEL_GRAPH_ATTRIBUTE, CLEAR, STEP_BEGINS
	}

	/**
	 * Flag used to indicate if the sink has to flush the output when writting a
	 * message.
	 */
	protected boolean autoflush;
	/**
	 * Stream used to write message.
	 */
	protected final PrintStream out;
	/**
	 * Format of messages associated with each event.
	 */
	protected final EnumMap<EventType, String> formats;
	/**
	 * Flag used to indicate if an event has to be written or note.
	 */
	protected final EnumMap<EventType, Boolean> enable;
	/*
	 * Used to avoid to create a lot of hashmap when passing event arguments.
	 */
	private final Stack<Args> argsStack;

	protected String prefix;

	protected String suffix;

	/**
	 * Create a new verbose sink using System.out.
	 */
	public VerboseSink() {
		this(System.out);
	}

	/**
	 * Create a new verbose sink.
	 * 
	 * @param out
	 *            stream used to output message
	 */
	public VerboseSink(PrintStream out) {
		this.out = out;
		argsStack = new Stack<Args>();
		enable = new EnumMap<EventType, Boolean>(EventType.class);
		formats = new EnumMap<EventType, String>(EventType.class);

		formats.put(EventType.ADD_NODE, DEFAULT_AN_FORMAT);
		formats.put(EventType.ADD_NODE_ATTRIBUTE, DEFAULT_CNA_FORMAT);
		formats.put(EventType.SET_NODE_ATTRIBUTE, DEFAULT_CNC_FORMAT);
		formats.put(EventType.DEL_NODE_ATTRIBUTE, DEFAULT_CNR_FORMAT);
		formats.put(EventType.DEL_NODE, DEFAULT_DN_FORMAT);

		formats.put(EventType.ADD_EDGE, DEFAULT_AE_FORMAT);
		formats.put(EventType.ADD_EDGE_ATTRIBUTE, DEFAULT_CEA_FORMAT);
		formats.put(EventType.SET_EDGE_ATTRIBUTE, DEFAULT_CEC_FORMAT);
		formats.put(EventType.DEL_EDGE_ATTRIBUTE, DEFAULT_CER_FORMAT);
		formats.put(EventType.DEL_EDGE, DEFAULT_DE_FORMAT);

		formats.put(EventType.ADD_GRAPH_ATTRIBUTE, DEFAULT_CGA_FORMAT);
		formats.put(EventType.SET_GRAPH_ATTRIBUTE, DEFAULT_CGC_FORMAT);
		formats.put(EventType.DEL_GRAPH_ATTRIBUTE, DEFAULT_CGR_FORMAT);

		formats.put(EventType.CLEAR, DEFAULT_CL_FORMAT);
		formats.put(EventType.STEP_BEGINS, DEFAULT_ST_FORMAT);

		for (EventType t : EventType.values())
			enable.put(t, Boolean.TRUE);

		suffix = "";
		prefix = "";
	}

	/**
	 * Enable or disable autoflush.
	 * 
	 * @param on
	 *            true to enable autoflush
	 */
	public void setAutoFlush(boolean on) {
		this.autoflush = on;
	}

	/**
	 * Redefines message format of an event.
	 * 
	 * @param type
	 *            type of the event
	 * @param format
	 *            new format of the message attached with the event
	 */
	public void setEventFormat(EventType type, String format) {
		formats.put(type, format);
	}

	/**
	 * Enable or disable an event.
	 * 
	 * @param type
	 *            type of the event
	 * @param on
	 *            true to enable message for this event
	 */
	public void setEventEnabled(EventType type, boolean on) {
		enable.put(type, on);
	}

	/**
	 * Enable or disable all messages associated with attribute events.
	 * 
	 * @param on
	 *            true to enable events
	 */
	public void setElementEventEnabled(boolean on) {
		enable.put(EventType.ADD_EDGE_ATTRIBUTE, on);
		enable.put(EventType.SET_EDGE_ATTRIBUTE, on);
		enable.put(EventType.DEL_EDGE_ATTRIBUTE, on);
		enable.put(EventType.ADD_NODE_ATTRIBUTE, on);
		enable.put(EventType.SET_NODE_ATTRIBUTE, on);
		enable.put(EventType.DEL_NODE_ATTRIBUTE, on);
		enable.put(EventType.ADD_GRAPH_ATTRIBUTE, on);
		enable.put(EventType.SET_GRAPH_ATTRIBUTE, on);
		enable.put(EventType.DEL_GRAPH_ATTRIBUTE, on);
	}

	/**
	 * Enable or disable all messages associated with element events.
	 * 
	 * @param on
	 *            true to enable events
	 */
	public void setAttributeEventEnabled(boolean on) {
		enable.put(EventType.ADD_EDGE, on);
		enable.put(EventType.DEL_EDGE, on);
		enable.put(EventType.ADD_NODE, on);
		enable.put(EventType.DEL_NODE, on);
		enable.put(EventType.CLEAR, on);
	}

	/**
	 * Set prefix used in messages.
	 * 
	 * @param prefix
	 *            new prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Set suffix used in messages.
	 * 
	 * @param suffix
	 *            new suffix
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	private void print(EventType type, Args args) {
		if (!enable.get(type))
			return;

		String out = formats.get(type);

		for (String k : args.keySet()) {
			Object o = args.get(k);
			out = out.replace(String.format("%%%s%%", k), o == null ? "null" : o.toString());
		}

		this.out.print(out);
		this.out.printf("\n");

		if (autoflush)
			this.out.flush();

		argsPnP(args);
	}

	private Args argsPnP(Args args) {
		if (args == null) {
			if (argsStack.size() > 0)
				args = argsStack.pop();
			else
				args = new Args();

			args.put("prefix", prefix);
			args.put("suffix", suffix);

			return args;
		} else {
			args.clear();
			argsStack.push(args);

			return null;
		}
	}

	private String toStringValue(Object o) {
		if (o == null)
			return "<null>";

		if (o instanceof String)
			return "\"" + ((String) o).replace("\"", "\\\"") + "\"";
		else if (o.getClass().isArray()) {
			StringBuilder buffer = new StringBuilder();
			buffer.append("{");

			for (int i = 0; i < Array.getLength(o); i++) {
				if (i > 0)
					buffer.append(", ");
				buffer.append(toStringValue(Array.get(o, i)));
			}

			buffer.append("}");
			return buffer.toString();
		}

		return o.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("edgeId", edgeId);
		args.put("attributeId", attribute);
		args.put("value", toStringValue(value));

		print(EventType.ADD_EDGE_ATTRIBUTE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang.String ,
	 * long, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("edgeId", edgeId);
		args.put("attributeId", attribute);
		args.put("value", toStringValue(newValue));

		print(EventType.SET_EDGE_ATTRIBUTE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang.String ,
	 * long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("edgeId", edgeId);
		args.put("attributeId", attribute);

		print(EventType.DEL_EDGE_ATTRIBUTE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang.String ,
	 * long, java.lang.String, java.lang.Object)
	 */
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("attributeId", attribute);
		args.put("value", toStringValue(value));

		print(EventType.ADD_GRAPH_ATTRIBUTE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang.
	 * String, long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("attributeId", attribute);
		args.put("value", toStringValue(newValue));

		print(EventType.SET_GRAPH_ATTRIBUTE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang.
	 * String, long, java.lang.String)
	 */
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("attributeId", attribute);

		print(EventType.DEL_GRAPH_ATTRIBUTE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("nodeId", nodeId);
		args.put("attributeId", attribute);
		args.put("value", toStringValue(value));

		print(EventType.ADD_NODE_ATTRIBUTE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang.String ,
	 * long, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("nodeId", nodeId);
		args.put("attributeId", attribute);
		args.put("value", toStringValue(newValue));

		print(EventType.SET_NODE_ATTRIBUTE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang.String ,
	 * long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("nodeId", nodeId);
		args.put("attributeId", attribute);

		print(EventType.DEL_NODE_ATTRIBUTE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("edgeId", edgeId);
		args.put("source", fromNodeId);
		args.put("target", toNodeId);

		print(EventType.ADD_EDGE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("edgeId", edgeId);

		print(EventType.DEL_EDGE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String, long)
	 */
	public void graphCleared(String sourceId, long timeId) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);

		print(EventType.CLEAR, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("nodeId", nodeId);

		print(EventType.ADD_NODE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("nodeId", nodeId);

		print(EventType.DEL_NODE, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String, long,
	 * double)
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
		Args args = argsPnP(null);

		args.put("sourceId", sourceId);
		args.put("timeId", timeId);
		args.put("step", step);

		print(EventType.STEP_BEGINS, args);
	}
}