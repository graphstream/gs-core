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
 * @since 2011-08-13
 * 
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.netstream;

/**
 * <h1 class="title">NetStream</h1>
 * 
 * <p>
 * The NetStream framework allows to export the idea of &quot;streams of graph
 * events&quot; to other languages than Java, through a network interface. The
 * aim is mainly to allow the use of GraphStream with other projects written in
 * other languages. However, since it is a network interface it also allows the
 * use of several machines. The protocol is optimized to be have as low overhead
 * as possible.
 * </p>
 * <p>
 * If you are looking for a Java-to-Java network link between GraphStream and
 * some other project, you may prefer GraphStream's
 * <a class="reference external" href=
 * "http://graphstream-project.org/doc/Tutorials/Using-remote-source_1.0/"
 * >RMI</a> facilities.
 * </p>
 * <p>
 * This document is organized in 3 sections. The first one details the
 * Receiver's mechanisms. The second section describes the Sender. The last
 * section details the NetStream Protocol.
 * </p>
 * <div class="section" id="receiver">
 * <h1>Receiver</h1>
 * <p>
 * This one is responsible for receiving graph events from the network following
 * the &quot;NetStream&quot; protocol. Events are then dispatched to pipes
 * according to a given names. Here we consider that several stream of events
 * (independent one another) can be handled by the receiver. We thus introduce
 * the idea of <strong>stream ID</strong> where a stream is identified by an ID.
 * </p>
 * <p>
 * The Receiver is composed of:
 * </p>
 * <ul class="simple">
 * <li>A socket server that handles multiples connections directed to multiple
 * streams (pipes). That part is mostly a copy/past from Antoine's &quot;MBox
 * Receiver&quot; code.</li>
 * <li>An implementation of the NetStream Protocol (see below) that parses the
 * received byte arrays and creates/sends graph events to specified pipes.</li>
 * <li>a set of streams (ThreadProxyPipes) identified by an ID. From
 * GraphStream's point of view, the NetStreamReceriver provides sources
 * (actually pipes) on which sinks (or other pipes) can connect to, to receive
 * graph events.</li>
 * </ul>
 * <p>
 * The Receiver's general behavior is:
 * </p>
 * <ul class="simple">
 * <li>Wait for messages from any sender received data is stored separately for
 * each sender until a message is completely received. The receiver knows about
 * a complete message because the first 4 bytes of the messages are an integer
 * that gives the size of the message.</li>
 * <li>A complete message is decoded (according to the NetStream Protocol), an
 * event is created and sent to the specified stream (pipe)</li>
 * </ul>
 * <p>
 * The graph event receiver listens at a given address and port. It runs on its
 * own thread. Several senders can connect to it, the receiver will demultiplex
 * the data flow and dispatch incoming events to specified pipes. No extra
 * thread are created when client connect.
 * </p>
 * <p>
 * From the graph event stream point of view, the NetStream receiver can be seen
 * as a set of pipes identified by an id. When an event is received is is
 * directed to one specific stream. By default, senders not willing to handle
 * different streams may send to the stream called &quot;default&quot;.
 * </p>
 * <p>
 * The only way to receive events from the network is to ask for a stream by
 * means of a ThreadProxyPipe to the Receiver. The
 * <tt class="docutils literal">getStream()</tt> and
 * <tt class="docutils literal">getDefaultStream()</tt> give access to such
 * pipe. Asking a non-existing stream (with an unknown id) will create it, so
 * those functions always return a pipe. On the opposite, any new stream
 * introduced by a sender will be created by the receiver.
 * </p>
 * 
 * <div class="section" id="example">
 * <h2>Example</h2>
 * 
 * <pre class="code-java literal-block">
 * import java.io.IOException;
 * import java.net.UnknownHostException;
 * 
 * import org.graphstream.graph.Graph;
 * import org.graphstream.graph.implementations.MultiGraph;
 * import org.graphstream.stream.thread.ThreadProxyPipe;
 * 
 * // A simple example of use of the NetStream receiver.
 * 
 * public class ReceiverExample {
 * 
 * 	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
 * 		// ----- On the receiver side -----
 * 		//
 * 		// - a graph that will display the received events
 * 		Graph g = new MultiGraph(&quot;G&quot;);
 * 		g.display();
 * 		// - the receiver that waits for events
 * 		NetStreamReceiver net = new NetStreamReceiver(2001);
 * 		// - received events end up in the &quot;default&quot; pipe
 * 		ThreadProxyPipe pipe = net.getDefaultStream();
 * 		// - plug the pipe to the sink of the graph
 * 		pipe.addSink(g);
 * 		// -The receiver pro-actively checks for events on the ThreadProxyPipe
 * 		while (true) {
 * 			pipe.pump();
 * 			Thread.sleep(100);
 * 		}
 * 	}
 * }
 * </pre>
 * 
 * </div> </div> <div class="section" id="sender">
 * <h1>Sender</h1>
 * <p>
 * A sender, from the GraphStream API, is first of all a sink where one can plug
 * sources so that it can receive events. Receiving these events the sender will
 * pack them into messages according to the NetStream Protocol and then send
 * those messages to a defined receiver through a given <strong>port</strong>,
 * <strong>host</strong> and <strong>stream ID</strong>.
 * </p>
 * <div class="section" id="id1">
 * <h2>Example</h2>
 * 
 * <pre class="code-java literal-block">
 * import java.io.IOException;
 * import java.net.UnknownHostException;
 * 
 * import org.graphstream.graph.Graph;
 * import org.graphstream.graph.implementations.MultiGraph;
 * 
 * // A simple example of use of the NetStream sender.
 * 
 * public class SenderExample {
 * 
 * 	public static void main(String[] args) {
 * 		Graph g = new MultiGraph(&quot;G&quot;);
 * 		// - the sender
 * 		NetStreamSender nsc = null;
 * 		try {
 * 			nsc = new NetStreamSender(2001);
 * 		} catch (UnknownHostException e) {
 * 			e.printStackTrace();
 * 		} catch (IOException e) {
 * 			e.printStackTrace();
 * 		}
 * 		// - plug the graph to the sender so that graph events can be
 * 		// sent automatically
 * 		g.addSink(nsc);
 * 		// - generate some events on the client side
 * 		String style = &quot;node{fill-mode:plain;fill-color:#567;size:6px;}&quot;;
 * 		g.setAttribute(&quot;stylesheet&quot;, style);
 * 		g.setAttribute(&quot;ui.antialias&quot;, true);
 * 		g.setAttribute(&quot;layout.stabilization-limit&quot;, 0);
 * 		for (int i = 0; i &lt; 500; i++) {
 * 			g.addNode(i + &quot;&quot;);
 * 			if (i &gt; 0) {
 * 				g.addEdge(i + &quot;-&quot; + (i - 1), i + &quot;&quot;, (i - 1) + &quot;&quot;);
 * 				g.addEdge(i + &quot;--&quot; + (i / 2), i + &quot;&quot;, (i / 2) + &quot;&quot;);
 * 			}
 * 		}
 * 	}
 * 
 * }
 * </pre>
 * 
 * </div> </div> <div class="section" id="the-netstream-protocol">
 * <h1>The NetStream Protocol</h1>
 * <p>
 * Messages in the NetStream protocol are specified a the byte level. It is
 * different than an XML-based protocols like client/server REST approaches.
 * Here the content and different formats constituting a message are optimize as
 * much as possible, so as to reduce the network payload.
 * </p>
 * <p>
 * A message, as it is created by a sender, is composed of three main parts:
 * <ol class="arabic simple">
 * <li>A 4 bytes integer that indicates the size (in bytes) of the remaining of
 * this message (not including those 4 bytes).</li>
 * <li>A string, encoded using the NetStream protocol (see
 * <tt class="docutils literal">TYPE_STRING</tt> below), that identifies the
 * stream targeted by this event.</li>
 * <li>The event itself, that can be decoded, according to the NetStream
 * protocol.</li>
 * </ol>
 * <div class="section" id="data-types">
 * <h2>Data Types</h2>
 * <p>
 * Before sending a value whose type is unknown (integer, double, string,
 * array...) one have to specify its type (and if applicable, its length) to the
 * server. Value types are defined to allow the server to recognize the type of
 * a value. When applicable (strings, tables, raw data) types are followed by a
 * length. This length is always coded with a 16-bits signed short and usually
 * represents the number of elements (for arrays).
 * </p>
 * <ul>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_BOOLEAN</tt> [0x50]
 * </p>
 * <p>
 * Announces a boolean value. Followed by a byte whose value is 0 (false) or 1
 * (true).
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_BOOLEAN_ARRAY</tt> [0X51]
 * </p>
 * <p>
 * Announces an array of boolean values. Followed by first, a 32-bit integer
 * that indicates the length of this array, and then, by the actual sequence of
 * booleans.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_BYTE</tt> [0x52]
 * </p>
 * <p>
 * Announces a byte. Followed by a 8-bit signed byte.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_INT_BYTE</tt> [0x53]
 * </p>
 * <p>
 * Announces an array of bytes. Followed by first, a 32-bit integer that
 * indicates the length in number of elements of this array, and then, by the
 * actual sequence of 8-bit signed bytes.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_SHORT</tt> [0x54]
 * </p>
 * <p>
 * Announces a short. Followed by a 16-bit signed short.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_SHORT_ARRAY</tt> [0x55]
 * </p>
 * <p>
 * Announces an array of shorts. Followed by first, a 32-bit integer that
 * indicates the length in number of elements of this array, and then, by the
 * actual sequence of 16-bit signed shorts.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_INT</tt> [0x56]
 * </p>
 * <p>
 * Announces an integer. Followed by a 32-bit signed integer.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_INT_ARRAY</tt> [0x57]
 * </p>
 * <p>
 * Announces an array of integers. Followed by first, a 32-bit integer that
 * indicates the length in number of elements of this array, and then, the
 * actual sequence of 32-bit signed integers.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_LONG</tt> [0x58]
 * </p>
 * <p>
 * Announces a long. Followed by a 64-bit signed long.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_LONG_ARRAY</tt> [0x59]
 * </p>
 * <p>
 * Announces an array of longs. Followed by first, a 32-bit integer that
 * indicates the length in number of elements of this array, and then, by the
 * actual sequence of 64-bit signed longs.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_FLOAT</tt> [0x5A]
 * </p>
 * <p>
 * Announces a float. Followed by a 32-bit single precision signed floating
 * point number.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_FLOAT_ARRAY</tt> [0x5B]
 * </p>
 * <p>
 * Announces an array of floats. Followed by first, a 32-bit integer that
 * indicates the length in number of elements of this array, and then, by the
 * actual sequence of 32-bit double precision signed floating point numbers.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_DOUBLE</tt> [0x5C]
 * </p>
 * <p>
 * Announces a double. Followed by a 64-bit double precision signed floating
 * point number.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_DOUBLE_ARRAY</tt> [0x5D]
 * </p>
 * <p>
 * Announces an array of doubles. Followed by first, a 32-bit integer that
 * indicates the length in number of elements of this array, and then, by the
 * actual sequence of 64-bit double precision signed floating point numbers.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_STRING</tt> [0x5E]
 * </p>
 * <p>
 * Announces an array of characters. Followed by first, a 32-bits integer for
 * the size in bytes (not in number of characters) of the string, then by the
 * unicode string itself.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_RAW</tt> [0x5F]
 * </p>
 * <p>
 * Announces raw data, good for serialization or to exchange data the will then
 * be understood in any language (an image, for instance). Followed by first, a
 * 16-bits integer indicating the length in bytes of the dataset, and then by
 * the data itself, as unsigned bytes.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">TYPE_ARRAY</tt> [0x60]
 * </p>
 * <p>
 * Announces an undefined-type array. Followed by first, a 32-bits integer
 * indicating the number of elements, and then, the elements themselves. The
 * elements themselves have to give their types. It may contain data of
 * different types or even other arrays.
 * </p>
 * </li>
 * </ul>
 * </div> <div class="section" id="graph-events">
 * <h2>Graph Events</h2>
 * <p>
 * the graph event, as created by a sender, is the third part of the whole sent
 * message. It is made of several parts that differ according the event. The
 * common information is the first byte of the event, that identifies the event.
 * Then, other data depending on the event follow up. Those event identifiers
 * are one byte long. To avoid problems between languages (mainly because of
 * java) those bytes are unsigned and only positive values are used. So, any
 * event identifier will take a value between 0 and 127.
 * </p>
 * <p>
 * Here is a list of graph event identifiers followed by the expected
 * information to fulfill these events:
 * </p>
 * <ul>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_ADD_NODE</tt> [0x10]
 * </p>
 * <p>
 * Add a node. Followed by a node id (
 * <tt class="docutils literal">TYPE_STRING</tt> format).
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_DEL_NODE</tt> [0x11]
 * </p>
 * <p>
 * Remove a node. Followed by a node id (
 * <tt class="docutils literal">TYPE_STRING</tt> format)
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_ADD_EDGE</tt> [0x12]
 * </p>
 * <p>
 * Add an edge. Followed by:
 * </p>
 * <ul class="simple">
 * <li>the edge id (TYPE_STRING format),</li>
 * <li>the source node id (TYPE_STRING format),</li>
 * <li>the target node id (TYPE_STRING format</li>
 * <li>a boolean indicating if the edge is directed (is it an arc?)
 * (TYPE_BOOLEAN format)</li>
 * </ul>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_DEL_NODE</tt> [0x13]
 * </p>
 * <p>
 * Remove an edge. Followed by the string id of this edge.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_STEP</tt> [0x14]
 * </p>
 * <p>
 * Time step. Followed by a 64-bit double indicating the timestamp.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_CLEARED</tt> [0x15]
 * </p>
 * <p>
 * Clear the graph. This event will remove any attribute or element in the
 * graph.
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_ADD_GRAPH_ATTR</tt> [0x16]
 * </p>
 * <p>
 * Add an attribute to the graph. Followed by:
 * </p>
 * <ul class="simple">
 * <li>the attribute name (TYPE_STRING format)</li>
 * <li>the attribute value type (one of the bytes shown in the &quot;Data
 * Types&quot; section)</li>
 * <li>the attribute value, encoded according to its value type (see the
 * &quot;Data Types&quot; section)</li>
 * </ul>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_CHG_GRAPH_ATTR</tt> [0x17]
 * </p>
 * <p>
 * Change an existing attribute on the graph. Followed by:
 * </p>
 * <ul class="simple">
 * <li>the attribute name (TYPE_STRING format)</li>
 * <li>the attribute'd old value type (one of the bytes shown in the &quot;Data
 * Types&quot; section)</li>
 * <li>the old attribute value, encoded according to its value type (see the
 * &quot;Data Types&quot; section)</li>
 * <li>the attribute's new value type (one of the bytes shown in the &quot;Data
 * Types&quot; section)</li>
 * <li>the new attribute value, encoded according to its value type (see the
 * &quot;Data Types&quot; section)</li>
 * </ul>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_DEL_GRAPH_ATTR</tt> [0x18]
 * </p>
 * <p>
 * Remove an attribute from the graph. Followed by the attribute name (encoded
 * with the TYPE_STRING format).
 * </p>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_ADD_NODE_ATTR</tt> [0x19]
 * </p>
 * <p>
 * Add an attribute to a node. Followed by:
 * </p>
 * <ul class="simple">
 * <li>the ID of the considered node (TYPE_STRING format)</li>
 * <li>the attribute name (TYPE_STRING format)</li>
 * <li>the attribute value type (one of the bytes shown in the &quot;Data
 * Types&quot; section)</li>
 * <li>the attribute value, encoded according to its value type (see the
 * &quot;Data Types&quot; section)</li>
 * </ul>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_CHG_NODE_ATTR</tt> [0x1A]
 * </p>
 * <p>
 * Change an existing attribute on a given node. Followed by:
 * </p>
 * <ul class="simple">
 * <li>the ID of the considered node (TYPE_STRING format)</li>
 * <li>the attribute name (TYPE_STRING format)</li>
 * <li>the attribute's old value type (one of the bytes shown in the &quot;Data
 * Types&quot; section)</li>
 * <li>the old attribute value, encoded according to its value type (see the
 * &quot;Data Types&quot; section)</li>
 * <li>the attribute's new value type (one of the bytes shown in the &quot;Data
 * Types&quot; section)</li>
 * <li>the new attribute value, encoded according to its value type (see the
 * &quot;Data Types&quot; section)</li>
 * </ul>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_DEL_NODE_ATTR</tt> [0x1B]
 * </p>
 * <p>
 * Remove an attribute from a given node. Followed by:
 * </p>
 * <ul class="simple">
 * <li>the ID of the considered node (TYPE_STRING format)</li>
 * <li>the attribute name (encoded with the TYPE_STRING format).</li>
 * </ul>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_ADD_EDGE_ATTR</tt> [0x1C]
 * </p>
 * <p>
 * Add an attribute to an edge. Followed by:
 * </p>
 * <ul class="simple">
 * <li>the ID of the considered edge (TYPE_STRING format)</li>
 * <li>the attribute name (TYPE_STRING format)</li>
 * <li>the attribute value type (one of the bytes shown in the &quot;Data
 * Types&quot; section)</li>
 * <li>the attribute value, encoded according to its value type (see the
 * &quot;Data Types&quot; section)</li>
 * </ul>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_CHG_EDGE_ATTR</tt> [0x1D]
 * </p>
 * <p>
 * Change an existing attribute on a given edge. Followed by:
 * </p>
 * <ul class="simple">
 * <li>the ID of the considered edge (TYPE_STRING format)</li>
 * <li>the attribute name (TYPE_STRING format)</li>
 * <li>the attribute's old value type (one of the bytes shown in the &quot;Data
 * Types&quot; section)</li>
 * <li>the old attribute value, encoded according to its value type (see the
 * &quot;Data Types&quot; section)</li>
 * <li>the attribute's new value type (one of the bytes shown in the &quot;Data
 * Types&quot; section)</li>
 * <li>the new attribute value, encoded according to its value type (see the
 * &quot;Data Types&quot; section)</li>
 * </ul>
 * </li>
 * <li>
 * <p class="first">
 * <tt class="docutils literal">EVENT_DEL_EDGE_ATTR</tt> [0x1E]
 * </p>
 * <p>
 * Remove an attribute from a given edge. Followed by:
 * </p>
 * <ul class="simple">
 * <li>the ID of the considered edge (TYPE_STRING format)</li>
 * <li>the attribute name (encoded with the TYPE_STRING format).</li>
 * </ul>
 * </li>
 * </ul>
 * </div> </div>
 * 
 * 
 * 
 * Copyright (c) 2010-2012 University of Luxembourg - University of Le Havre
 * 
 * NetStreamConstants.java
 * 
 * @since Aug 3, 2011
 * 
 * @author Yoann Pigné
 * 
 */
public class NetStreamConstants {
	/**
	 * Followed by an 32-bit signed integer for this protocol version. Certainly
	 * useless.
	 */
	public static int EVENT_GETVERSION = 0x00;
	/**
	 * Not used.
	 */
	public static int EVENT_START = 0x01;

	/**
	 * Constant indicating that the client has disconnected.
	 */
	public static int EVENT_END = 0x02;

	//
	// ----------------------------------
	// GraphStream's graph events
	// ----------------------------------
	//

	/**
	 * Followed by a node id (TYPE_STRING format)
	 */
	public static int EVENT_ADD_NODE = 0x10;

	/**
	 * Followed by a node id (TYPE_STRING format)
	 */
	public static int EVENT_DEL_NODE = 0x11;

	/**
	 * Followed by - an edge id (TYPE_STRING format), - an source node id
	 * (TYPE_STRING format), - a target node id (TYPE_STRING format - a boolean
	 * indicating if directed (TYPE_BOOLEAN format)
	 */
	public static int EVENT_ADD_EDGE = 0x12;

	/**
	 * Followed by an edge id (TYPE_STRING format)
	 */
	public static int EVENT_DEL_EDGE = 0x13;

	/**
	 * Followed by double (TYPE_DOUBLE format)
	 */
	public static int EVENT_STEP = 0x14;
	/**
	 * 
	 */
	public static int EVENT_CLEARED = 0x15;

	/**
	 * Followed by - an attribute id (TYPE_STRING format) - the attribute TYPE - the
	 * attribute value
	 */
	public static int EVENT_ADD_GRAPH_ATTR = 0x16;
	/**
	 * Followed by - an attribute id (TYPE_STRING format) - the attribute TYPE - the
	 * attribute old value - the attribute new value
	 */
	public static int EVENT_CHG_GRAPH_ATTR = 0x17;
	/**
	 * Followed by - the attribute id (TYPE_STRING format)
	 */
	public static int EVENT_DEL_GRAPH_ATTR = 0x18;

	/**
	 * Followed by - an attribute id (TYPE_STRING format) - the attribute TYPE - the
	 * attribute value
	 */
	public static int EVENT_ADD_NODE_ATTR = 0x19;
	/**
	 * Followed by - an attribute id (TYPE_STRING format) - the attribute TYPE - the
	 * attribute old value - the attribute new value
	 */
	public static int EVENT_CHG_NODE_ATTR = 0x1a;
	/**
	 * Followed by - the node id (TYPE_STRING format) - the attribute id
	 * (TYPE_STRING format)
	 */
	public static int EVENT_DEL_NODE_ATTR = 0x1b;

	/**
	 * Followed by - an attribute id (TYPE_STRING format) - the attribute TYPE - the
	 * attribute value
	 */
	public static int EVENT_ADD_EDGE_ATTR = 0x1c;
	/**
	 * Followed by - an attribute id (TYPE_STRING format) - the attribute TYPE - the
	 * attribute old value - the attribute new value
	 */
	public static int EVENT_CHG_EDGE_ATTR = 0x1d;
	/**
	 * Followed by - the edge id (TYPE_STRING format) - the attribute id
	 * (TYPE_STRING format)
	 */
	public static int EVENT_DEL_EDGE_ATTR = 0x1e;

	// Values types

	public static int TYPE_UNKNOWN = 0x00;

	/**
	 * Followed by a byte who's value is 0 or 1
	 */
	public static int TYPE_BOOLEAN = 0x50;
	/**
	 * An array of booleans. Followed by first, a 16-bits integer for the number of
	 * booleans and then, a list of bytes who's value is 0 or 1
	 */
	public static int TYPE_BOOLEAN_ARRAY = 0x51;
	/**
	 * Followed by a signed byte [-127,127]
	 */
	public static int TYPE_BYTE = 0x52;
	/**
	 * An array of bytes. Followed by first, a 16-bits integer for the number of
	 * integers and then, a list of signed bytes.
	 */
	public static int TYPE_BYTE_ARRAY = 0x53;
	/**
	 * Followed by an 16-bit signed integer (a short)
	 */
	public static int TYPE_SHORT = 0x54;
	/**
	 * An array of shorts. Followed by first, a 16-bits integer for the number of
	 * integers and then, a list of 16-bit signed shorts
	 */
	public static int TYPE_SHORT_ARRAY = 0x55;
	/**
	 * Followed by an 32-bit signed integer
	 */
	public static int TYPE_INT = 0x56;
	/**
	 * An array of integers. Followed by first, a 16-bits integer for the number of
	 * integers and then, a list of 32-bit signed integers
	 */
	public static int TYPE_INT_ARRAY = 0x57;
	/**
	 * Followed by an 64-bit signed integer
	 */
	public static int TYPE_LONG = 0x58;
	/**
	 * An array of longs. Followed by first, a 16-bits integer for the number of
	 * longs and then, a list of 62-bit signed integers
	 */
	public static int TYPE_LONG_ARRAY = 0x59;
	/**
	 * Followed by a single precision 32-bits floating point number
	 */
	public static int TYPE_FLOAT = 0x5a;
	/**
	 * Array of double. Followed by first, a 16-bits integer for the number of
	 * floats and then, a list of 32-bit floats
	 */
	public static int TYPE_FLOAT_ARRAY = 0x5b;
	/**
	 * Followed by a double precision 64-bits floating point number
	 */
	public static int TYPE_DOUBLE = 0x5c;
	/**
	 * Array of double. Followed by first, a 16-bits integer for the number of
	 * doubles and then, a list of 64-bit doubles
	 */
	public static int TYPE_DOUBLE_ARRAY = 0x5d;
	/**
	 * Array of characters. Followed by first, a 16-bits integer for the size in
	 * bytes (not in number of characters) of the string, then by the unicode string
	 */
	public static int TYPE_STRING = 0x5e;
    /**
     * Array of Array of characters.
     */
    public static int TYPE_STRING_ARRAY = 0x62;
	/**
	 * Raw data, good for serialization. Followed by first, a 16-bits integer
	 * indicating the length in bytes of the dataset, and then the data itself.
	 */
	public static int TYPE_RAW = 0x5f;

	/**
	 * An type-unspecified array. Followed by first, a 16-bits integer indicating
	 * the number of elements, and then, the elements themselves. The elements
	 * themselves have to give their type.
	 */
	public static byte TYPE_ARRAY = 0x60;

	public static int TYPE_NULL = 0x61;

	/**
	 * Constant that indicates that this message is a COMMAND, not and EVENT.
	 * 
	 * For now it is followed by a string that has to be parssed at the application
	 * level.
	 * 
	 * THIS IS EXPERIMENTAL AND MAY (WILL) CHANGE !
	 */
	public static int COMMAND = 0x70;

}