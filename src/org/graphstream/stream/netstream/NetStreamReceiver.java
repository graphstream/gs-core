/**
 * 
 * Copyright (c) 2010 University of Luxembourg
 * 
 * @file NetStreamReceiver.java
 * @date Aug 13, 2011
 * 
 * @author Yoann Pigné
 * 
 */
package org.graphstream.stream.netstream;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.Sink;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.sync.SourceTime;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.miv.mbox.MBoxListener;
import org.miv.mbox.net.IdAlreadyInUseException;
import org.miv.mbox.net.MBoxLocator;
import org.miv.mbox.net.PositionableByteArrayInputStream;

/**
 * This one is responsible for receiving graph events from the network following
 * the "NetStream" protocol. Events are then dispatched to pipes according to a
 * given names.
 * 
 * <p>
 * The class is composed of both:
 * </p>
 * <ul>
 * <li>A server that handles multiples connections directed to multiple streams
 * (pipes). That part is mostly a copy/past from Antoine's "MBox Receiver" code.
 * </li>
 * <li>An implementation of the NetStream Protocol (see below) that parses the
 * received byte arrays and creates/sends graph events to specified pipes.</li>
 * </ul>
 * 
 * <h2>Receiver</h2>
 * The Receiver's general behavior is:
 * <ul>
 * <li>Wait for messages from any sender</li>
 * <li>received data is stored separately for each sender until a message is completely received. The reiceiver knows about a complete message because the first 4 bytes of the messages are an integer that gives the size of the message.</li>
 * <li>a complete message is decoded (according to the NetStream Protocol), an event is created and sent ton the specified stream (pipe)</li>
 * </ul>

 * <p>
 * The graph event receiver listens at a given address and port. It runs on its
 * own thread. Several senders can connect to it, the receiver will demultiplex
 * the data flow and dispatch incoming events to specified pipes. No extra
 * thread are created when client connect.
 * 
 * </p>
 * 
 * <p>
 * From the graph event stream point of view, the NetStream receiver can be seen
 * as a set of pipes identified by an id. When an event is received is is
 * directed to one specific stream. By default, senders not willing to handle
 * different streams may send to the stream called "default".
 * </p>
 * 
 * <p>
 * The only way to receive events from the network is to ask for a stream by
 * means of a ThreadProxyPipe to the Receiver. The {@link #getStream} and
 * {@link #getDefaultStream} give access to such pipe. Asking a non-existing
 * stream (with an unknown id) will create it, so those functions always return
 * a pipe. On the opposite, any new stream introduced by a sender will be created by the
 * receiver.
 * </p>
 * 
 * 
 * 
 * 
 * <h2>The NetStream Protocol</h2>
 * 
 * A messages sent by a sender is composed of two general parts : 
 * <ul>
 * <li>A 4 bytes integer that indicates the length (in bytes) of the remaining of this message.</li>
 * <li>The message itself that can be decode into a graph event, according to the NetStream Protocol.</li>
 * </ul>
 * 
 * 
 * 
 * 
 * <h3>Data types</h3>
 * 
 * <p>
 * Before sending a value who's type is unknown (integer, double, string,
 * table...) one have to specify its type (and if applicable, its length) to the
 * server. Value types are defined to allow the server to recognize the type of
 * a value. When applicable (strings, tables, raw data) types are followed by a
 * length. This length is always coded with a 16-bits signed short and usually
 * represents the number of elements (for arrays).
 * </p>
 * 
 * <ul>
 * <li><code>TYPE_BOOLEAN</code>: Announces a boolean value. Followed by a byte
 * who's value is 0 (false) or 1 (true).</li>
 * 
 * <li><code>TYPE_BOOLEAN_ARRAY</code>: Announces an array of boolean values.
 * Followed by first, a 16-bit short that indicates the length of this array,
 * and then, the actual sequence of booleans.</li>
 * 
 * <li><code>TYPE_INT</code>: Announces an integer. Followed by an 32-bit signed
 * integer.</li>
 * 
 * <li><code>TYPE_DOUBLE</code>: Announces a double. Followed by an 64-bit
 * double precision floating point number.</li>
 * 
 * <li><code>TYPE_INT_ARRAY</code>: Announces an array of integers. Followed by
 * first, a 16-bit short that indicates the length <b>in number of elements</b>
 * of this array, and then, the actual sequence of integers.</li>
 * 
 * <li><code>TYPE_DOUBLE_ARRAY</code>: Announces an array of doubles. Followed
 * by first, a 16-bit short that indicates the length <b>in number of
 * elements</b> of this array, and then, the actual sequence of doubles.</li>
 * 
 * <li><code>TYPE_STRING</code>: Announces an array of characters. Followed by
 * first, a 16-bits short for the size <b>in bytes</b> (not in number of
 * characters) of the string, then by the <b>unicode</b> string itself.</li>
 * 
 * <li><code>TYPE_RAW</code>: Announces raw data, good for serialization.
 * Followed by first, a 16-bits integer indicating the length in bytes of the
 * dataset, and then the data itself.</li>
 * 
 * <li><code>TYPE_COMPOUND</code>: Announces a compound data set, where arrays
 * contain other arrays mixed with native types. Each data piece in this case,
 * has to announce it's type (and length if applicable). May be useless because
 * hard to decode...</li>
 * 
 * <li><code>TYPE_ARRAY</code>: Announces an undefined-type array. Followed by
 * first, a 16-bits integer indicating the number of elements, and then, the
 * elements themselves. The elements themselves have to give their types.
 * <b>Should only be used in conjunction with <code>TYPE_COMPOUND</code></b>.</li>
 * </ul>
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class NetStreamReceiver extends Thread {
	
	/**
	 * the source Id used to identify events in the following streams. Synchronization is not handled yet.
	 */
	protected String sourceId = "NetStream";
	/**
	 *  This timestamp object is used to label received events. Synchronization is not handled yet.
	 */
	protected SourceTime sourceTime;

	/**
	 * the hostname this receiver is listening at.
	 */
	private String hostname;
	
	/**
	 * the port listened to.
	 */
	private int port;
	
	/**
	 * Receiver socket.
	 */
	protected ServerSocketChannel server;

	/**
	 * Multiplexor.
	 */
	protected Selector selector;

	/**
	 * Key for the selector.
	 */
	protected SelectionKey key;

	/**
	 * While true, the received is running.
	 */
	protected boolean loop = true;

	/**
	 * Show debugging messages.
	 */
	protected boolean debug = true;

	/**
	 * Last encountered error.
	 */
	protected String lastError = null;

	/**
	 * The current pipe commands are being written to.
	 */
	protected ThreadProxyPipe currentStream;

	/**
	 * Pairs (key,value) where the key is the listener ID and the value the MBox
	 * of the listener. This can be modified by other threads and must be
	 * properly locked.
	 * 
	 * @see #register(String,MBoxListener)
	 */
	// protected HashMap<String,MBox> boxes = new HashMap<String,MBox>();
	protected HashMap<String, ThreadProxyPipe> streams = new HashMap<String, ThreadProxyPipe>();

	/**
	 * Current active incoming connections.
	 */
	protected HashMap<SelectionKey, IncomingBuffer> incoming = new HashMap<SelectionKey, IncomingBuffer>();
	
	// Constructors

	
	/**
	 * New NetStream Receiver, awaiting in its own thread at the given host name and port, for new graph events.
	 * 
	 * @param hostname
	 *            The host name to listen at messages.
	 * @param port
	 *            The port to listen at messages.
	 */
	public NetStreamReceiver(String hostname, int port) throws IOException,
			UnknownHostException {
		this(hostname, port, false);
	}

	/**
	 * New NetStream Receiver, awaiting in its own thread at "localhost" on the given port, for new graph events.
	 * 
	 * @param port
	 *            The port to listen at messages.
	 */
	public NetStreamReceiver(int port) throws IOException,
			UnknownHostException {
		this("localhost", port, false);
	}

	
	/**
	 * New NetStream Receiver, awaiting in its own thread at the given host name and port, for new graph events.
	 * 
	 * @param hostname
	 *            The host name to listen at messages.
	 * @param port
	 *            The port to listen at messages.
	 * @param debug
	 *            If true informations are output for each message received.
	 */
	public NetStreamReceiver(String hostname, int port, boolean debug)
			throws IOException, UnknownHostException {
		this.hostname = hostname;
		this.port = port;
		sourceTime = new SourceTime(0);

		setDebugOn(debug);
		init();
		start();
	}

	// Access

	/**
	 * False as soon as the receiver terminates.
	 */
	public synchronized boolean isRunning() {
		return loop;
	}

	/**
	 * Gives the stream (a ThreadProxyPipe) identified with this name. If no
	 * pipe exists under this name, a new one is created and returned
	 * 
	 * @param name
	 *            Identifier of the stream.
	 * @return the identified pipe
	 */
	public ThreadProxyPipe getStream(String name) {
		ThreadProxyPipe s = streams.get(name);
		if (s == null) {
			s = new ThreadProxyPipe();
			streams.put(name, s);
		}
		return s;
	}
	/**
	 * Gives the default stream (a ThreadProxyPipe) identified with the name
	 * "default". It is created if it does not exist.
	 * 
	 * @return the default pipe
	 */

	public ThreadProxyPipe getDefaultStream() {
		ThreadProxyPipe s = streams.get("default");
		if (s == null) {
			s = new ThreadProxyPipe();
			streams.put("default", s);
		}
		return s;

	}

	// Commands

	/**
	 * Initialize the server socket.
	 */
	protected void init() throws IOException, UnknownHostException {
		selector = Selector.open();
		server = ServerSocketChannel.open();

		server.configureBlocking(false);

		InetAddress ia = InetAddress.getByName(hostname);
		InetSocketAddress isa = new InetSocketAddress(ia, port);

		server.socket().bind(isa);

		if (debug)
			debug("bound to socket %s:%d", server.socket().getInetAddress(),
					server.socket().getLocalPort());

		// Register a first server socket inside the multiplexer.

		key = server.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * Enable or disable debugging.
	 */
	public void setDebugOn(boolean on) {
		debug = on;
	}

	/**
	 * Register a message box listener for incoming messages. All messages with
	 * the given name will be directed to it. The listener can then call
	 * {@link org.miv.mbox.MBoxBase#processMessages()} to receive its pending
	 * messages.
	 * 
	 * @param name
	 *            Filter only message with this name to the given listener.
	 * @param listener
	 *            The listener to register.
	 * @throws IdAlreadyInUseException
	 *             If another message box is already registered at the given
	 *             name.
	 */
	/*
	 * public synchronized MBoxStandalone register( String name, MBoxListener
	 * listener ) throws IdAlreadyInUseException { if( boxes.containsKey( name )
	 * ) throw new IdAlreadyInUseException( "name "+name+" already registered"
	 * );
	 * 
	 * MBoxStandalone mbox = new MBoxStandalone( listener ); boxes.put( name,
	 * mbox );
	 * 
	 * if( debug ) debug( "registered message box %s", name );
	 * 
	 * return mbox; }
	 */

	/**
	 * Register a stream. All events  with the given stream name will be directed
	 * to it. The user has to ensure the ThreadProxyPipe can be safely written to by the Receiver's thread.
	 *  
	 * @param name
	 *            Filter only message with this name to the given message box.
	 * @param pipe
	 *            The ThreadProxyPipe to push the events to.
	 * @throws IdAlreadyInUseException
	 *             If another Pipe is already registered at the given
	 *             name.
	 */
	public synchronized void register(String name, ThreadProxyPipe stream)
			throws Exception {
		if (streams.containsKey(name))
			throw new IdAlreadyInUseException("name " + name + " already registered");

		streams.put(name, stream);

		if (debug)
			debug("registered pipe %s", name);
	}

	/**
	 * Stop the receiver.
	 */
	public synchronized void quit() {
		loop = false;

		if (debug)
			debug("stopped");
	}

	/**
	 * Ask the receiver about its active connections
	 */
	public synchronized boolean hasActiveConnections(){
		return ! incoming.isEmpty();
	}
	
	
	/**
	 * Wait for connections, accept them, demultiplexes them and dispatch
	 * messages to registered message boxes.
	 */
	@Override
	public void run() {
		boolean l;

		synchronized (this) {
			l = loop;
		}

		while (l) {
			poll();

			synchronized (this) {
				l = loop;
			}
		}

		try {
			server.close();
		} catch (IOException e) {
			error("cannot close the server socket: " + e.getMessage(), e);
		}

		if(debug){
			debug("receiver //" + hostname +":"+port+ " finished");
		}
	}

	/**
	 * Wait until one or several chunks of message are acceptable. This method
	 * should be called in a loop. It can be used to block a program until some
	 * data is available.
	 */
	public void poll() {
		try {
			// Wait for incoming messages in a loop.

			if (key.selector().select() > 0) {
				Set<?> readyKeys = selector.selectedKeys();
				Iterator<?> i = readyKeys.iterator();

				while (i.hasNext()) {
					SelectionKey akey = (SelectionKey) i.next();

					i.remove();

					if (akey.isAcceptable()) {
						// If a new connection occurs, register the new socket
						// in the multiplexer.

						ServerSocketChannel ssocket = (ServerSocketChannel) akey
								.channel();
						SocketChannel socket = ssocket.accept();

						if (debug)
							debug("accepting socket %s:%d", socket.socket()
									.getInetAddress(), socket.socket()
									.getPort());

						socket.configureBlocking(false);
						socket.finishConnect();

						// SelectionKey otherKey = socket.register( selector,
						// SelectionKey.OP_READ );
						socket.register(selector, SelectionKey.OP_READ);
					} else if (akey.isReadable()) {
						// If a message arrives, read it.

						readDataChunk(akey);
					} else if (akey.isWritable()) {
						throw new RuntimeException("should not happen");
					}
				}
			}
		} catch (IOException e) {
			error(e, "I/O error in receiver //%s:%d thread: aborting: %s", hostname, port,
					e.getMessage());

			loop = false;
		} catch (Throwable e) {
			error(e, "Unknown error: %s", e.getMessage());

			loop = false;
		}
	}

	/**
	 * When data is readable on a socket, send it to the appropriate buffer
	 * (creating it if needed).
	 */
	protected void readDataChunk(SelectionKey key) throws IOException {
		IncomingBuffer buf = incoming.get(key);

		if (buf == null) {
			buf = new IncomingBuffer();
			incoming.put(key, buf);
			SocketChannel socket = (SocketChannel) key.channel();

			if (debug)
				debug("creating buffer for new connection from %s:%d", socket
						.socket().getInetAddress(), socket.socket().getPort());
		}

		try {
			buf.readDataChunk(key);
			
		} catch (IOException e) {
			incoming.remove(key);
			e.printStackTrace();
			error(e,
					"receiver //%s:%d cannot read object socket channel (I/O error): %s",
					hostname, port, e.getMessage());
			loop = false;
		}
		
		if( ! buf.active){
			incoming.remove(key);
			if(debug)
				debug("removing buffer %s from incoming for geting inactive. %d left",key.toString(), incoming.size());
			
		}
		
		
	}

	// Utilities

	protected void error(String message, Object... data) {
		error(null, message, data);
	}

	protected static final String LIGHT_YELLOW = "[33;1m";
	protected static final String RESET = "[0m";

	protected void error(Throwable e, String message, Object... data) {
		// System.err.print( LIGHT_YELLOW );
		System.err.print("[");
		// System.err.print( RESET );
		System.err.printf(message, data);
		// System.err.print( LIGHT_YELLOW );
		System.err.printf("]%n");
		// System.err.println( RESET );

		if (e != null)
			e.printStackTrace();
	}

	protected void debug(String message, Object... data) {
		// System.err.print( LIGHT_YELLOW );
		System.err.printf("[//%s:%d | ", hostname, port);
		// System.err.print( RESET );
		System.err.printf(message, data);
		// System.err.print( LIGHT_YELLOW );
		System.err.printf("]%n");
		// System.err.println( RESET );
	}

	// Nested classes

	/**
	 * The connection to a sender.
	 * 
	 * The receiver maintains several incoming connections and demultiplexes
	 * them.
	 */
	protected class IncomingBuffer {
		// Attributes

		protected static final int BUFFER_INITIAL_SIZE = 8192; // 65535, 4096

		/**
		 * Buffer for reading.
		 */
		protected ByteBuffer buf = ByteBuffer.allocate(BUFFER_INITIAL_SIZE);

		/**
		 * Index in the buffer past the last byte that forms the current
		 * message. End can be out of the buffer or out of the data read
		 * actually.
		 */
		protected int end = -1;

		/**
		 * Index in the buffer of the first byte that forms the currents
		 * message. Beg does not count the 4 bytes that give the size of the
		 * message. While the header is being read, beg is the first byte of the
		 * header.
		 */
		protected int beg = 0;

		/**
		 * Position inside beg and end past the last byte read. All bytes at and
		 * after pos have unspecified contents. Pos always verifies pos&gt;=beg
		 * and pos&lt;end. While the header is being read, pos is past the last
		 * byte of the header that has been read.
		 */
		protected int pos = 0;

		/**
		 * Object input stream for reading the buffer. This input stream reads
		 * data from the "bin" positionable byte array input stream, itself
		 * mapped on the current message to decode.
		 */
		PositionableByteArrayInputStream in;

		/**
		 * Input stream filter on the buffer. This descendant of
		 * ByteArrayInputStream is able to change its offset and length so that
		 * we can map exactly the message to decode inside the buffer.
		 */
		PositionableByteArrayInputStream bin;

		/**
		 * When false the socket is closed and this buffer must be removed from
		 * the active connections.
		 */
		protected boolean active = true;

		// Constructors

		public IncomingBuffer() {
		}

		// Commands

		/**
		 * Read the available bytes and buffers them. If one or more complete
		 * serialised objects are available, send them to their respective
		 * MBoxes.
		 * 
		 * Here is the junk...
		 */
		public void readDataChunk(SelectionKey key) throws IOException {
			int limit = 0; // Index past the last byte read during the current
							// invocation.
			int nbytes = 0; // Number of bytes read.
			SocketChannel socket = (SocketChannel) key.channel();

			// Buffers the data.

			nbytes = bufferize(pos, socket);
			limit = pos + nbytes;

			if (nbytes <= 0)
				return;

			//debug("<chunk (%d bytes) from " + socket.socket().getInetAddress()
			//		+ ":" + socket.socket().getPort() + ">", nbytes);

			// Read the first header.

			if (end < 0) {
				if ((limit - beg) >= 4) {
					// If no data has been read yet in the buffer or if the
					// buffer
					// was emptied completely at previous call: prepare to read
					// a
					// new message by decoding its header.

					buf.position(0);
					end = buf.getInt() + 4;
					beg = 4;
				} else {
					// The header is incomplete, wait next call to complete it.

					pos = limit;
				}
			}

			// Read one or more messages or wait next call to buffers more.

			if (end > 0) {
				while (end < limit) {
					// While the end of the message is in the limit of what was
					// read, there are one or more complete messages. Decode
					// them
					// and read the header of the next message, until a message
					// is
					// incomplete or there are no more messages or a header is
					// incomplete.

					decodeMessage(limit);
					buf.position(end);

					if (end + 4 <= limit) {
						// There is a following message.

						beg = end + 4;
						end = end + buf.getInt() + 4;
					} else {
						// There is the beginning of a following message
						// but the header is incomplete. Compact the buffer
						// and stop here.
						assert (beg >= 4);

						beg = end;
						int p = 4 - ((end + 4) - limit);
						compactBuffer();
						pos = p;
						beg = 0;
						end = -1;
						break;
					}
				}

				if (end == limit) {
					// If the end of the message coincides with the limit of
					// what
					// was read we have one last complete message. We decode it
					// and
					// clear the buffer for the next call.

					decodeMessage(limit);
					buf.clear();
					pos = 0;
					beg = 0;
					end = -1;
				} else if (end > limit) {
					// If the end of the message if after what was read, prepare
					// to
					// read more at next call when we will have buffered more
					// data. If we are at the end of the buffer compact it (else
					// no
					// more space will be available for buffering).

					pos = limit;

					if (end > buf.capacity())
						compactBuffer();
				}
			}
		}

		/**
		 * Read more data from the <code>socket</code> and put it in the buffer
		 * at <code>at</code>. If the read returns -1 bytes (meaning the
		 * connection ended), the socket is closed and this buffer will be made
		 * inactive (and therefore removed from the active connections by the
		 * Receiver that called it).
		 * 
		 * @return the number of bytes read.
		 * @throws IOException
		 *             if an I/O error occurs, in between the socket is closed
		 *             and the connection is made inactive, then the exception
		 *             is thrown.
		 */
		protected int bufferize(int at, SocketChannel socket)
				throws IOException {
			int nbytes = 0;
			// int limit = 0;

			try {
				buf.position(at);

				nbytes = socket.read(buf);

				if (nbytes < 0) {
					active = false;
					if (in != null)
						in.close();
					socket.close();
					if (debug)
						debug("socket from %s:%d closed", socket.socket()
								.getInetAddress(), socket.socket().getPort());
					return nbytes;
				} else if (nbytes == 0) {
					throw new RuntimeException(
							"should not happen: buffer to small, 0 bytes read: compact does not function? messages is larger than "
									+ buf.capacity() + "?");
					// This means that there are no bytes remaining in the
					// buffer... it is full.
					// compactBuffer();
					// return nbytes;
				}

				buf.position(at);

				return nbytes;
			} catch (IOException e) {
				if (debug)
					debug("socket from %s:%d I/O error: %s", socket.socket()
							.getInetAddress(), socket.socket().getPort(),
							e.getMessage());
				active = false;
				if (in != null)
					in.close();
				socket.close();
				throw e;
			}
		}

		/**
		 * Decode one message.
		 */
		protected void decodeMessage(int limit) throws IOException {

			if (in == null) {
				in = new PositionableByteArrayInputStream(buf.array(), beg, end); // Only
																					// a
																					// wrapping.
				// bin = new PositionableByteArrayInputStream( buf.array(), beg,
				// end ); // Only a wrapping.
				// in = new BufferedInputStream( bin );
			} else {
				in.setPos(beg, end);
				// in = new BufferedInputStream(bin);
			}

			int cmd = 0;

			// First read the name of the stream that will be addressed.
			String stream = readString(in);
			if(debug){
				debug("Stream \"%s\" is addressed in this message.", stream);
			}
			currentStream = getStream(stream);
			
			cmd = in.read();
			if (cmd != -1) {
				if (cmd == NetStreamConstants.EVENT_ADD_NODE) {
					serve_EVENT_ADD_NODE(in);
				} else if ((cmd & 0xFF) == (NetStreamConstants.EVENT_DEL_NODE & 0xFF)) {
					serve_DEL_NODE(in);
				} else if (cmd == NetStreamConstants.EVENT_ADD_EDGE) {
					serve_EVENT_ADD_EDGE(in);
				} else if (NetStreamConstants.EVENT_DEL_EDGE == cmd) {
					serve_EVENT_DEL_EDGE(in);
				} else if (cmd == NetStreamConstants.EVENT_STEP) {
					serve_EVENT_STEP(in);
				} else if (cmd == NetStreamConstants.EVENT_CLEARED) {
					serve_EVENT_CLEARED(in);
				} else if (cmd == NetStreamConstants.EVENT_ADD_GRAPH_ATTR) {
					serve_EVENT_ADD_GRAPH_ATTR(in);
				} else if (cmd == NetStreamConstants.EVENT_CHG_GRAPH_ATTR) {
					serve_EVENT_CHG_GRAPH_ATTR(in);
				} else if (cmd == NetStreamConstants.EVENT_DEL_GRAPH_ATTR) {
					serve_EVENT_DEL_GRAPH_ATTR(in);
				} else if (cmd == NetStreamConstants.EVENT_ADD_NODE_ATTR) {
					serve_EVENT_ADD_NODE_ATTR(in);
				} else if (cmd == NetStreamConstants.EVENT_CHG_NODE_ATTR) {
					serve_EVENT_CHG_NODE_ATTR(in);
				} else if (cmd == NetStreamConstants.EVENT_DEL_NODE_ATTR) {
					serve_EVENT_DEL_NODE_ATTR(in);
				} else if (cmd == NetStreamConstants.EVENT_ADD_EDGE_ATTR) {
					serve_EVENT_ADD_EDGE_ATTR(in);
				} else if (cmd == NetStreamConstants.EVENT_CHG_EDGE_ATTR) {
					serve_EVENT_CHG_EDGE_ATTR(in);
				} else if (cmd == NetStreamConstants.EVENT_DEL_EDGE_ATTR) {
					serve_EVENT_DEL_EDGE_ATTR(in);
				} else if (cmd == NetStreamConstants.EVENT_END) {
					debug("NetStreamReceiver : Client properly ended the connection.");
					return;
				} else {
					debug("NetStreamReceiver: Don't know this command: " + cmd);
					return;
				}
				cmd = in.read();
			}
		}

		/**
		 * Compact the buffer by removing all read data before <code>beg</code>.
		 * The <code>beg</code>, <code>end</code> and <code>pos</code> markers
		 * are updated accordingly. Compact works only if beg is larger than
		 * four (the size of a header).
		 * 
		 * @return the offset.
		 */
		protected int compactBuffer() {
			if (beg > 4) {
				int off = beg;

				buf.position(beg);
				buf.limit(buf.capacity());
				buf.compact();

				pos -= beg;
				end -= beg;
				beg = 0;

				return off;
			}

			return 0;
		}

		/**
		 * Not used in the current implementation, we assumes that no message
		 * will be larger than the size of the buffer.
		 */
		protected void enlargeBuffer() {
			ByteBuffer tmp = ByteBuffer.allocate(buf.capacity() * 2);

			buf.position(0);
			buf.limit(buf.capacity());
			tmp.put(buf);
			tmp.position(pos);

			buf = tmp;

			if (bin != null)
				bin.changeBuffer(buf.array());
		}
	}

	/**
	 * @see NetStreamConstants.EVENT_DEL_EDGE
	 */
	protected void serve_EVENT_DEL_EDGE_ATTR(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received DEL_EDGE_ATTR command.");
		}
		String edgeId = readString(in);
		String attrId = readString(in);
		currentStream.edgeAttributeRemoved(sourceId, sourceTime.newEvent(),
				edgeId, attrId);
	}

	/**
	 * @see NetStreamConstants.EVENT_CHG_EDGE_ATTR
	 */
	protected void serve_EVENT_CHG_EDGE_ATTR(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received CHG_EDGE_ATTR command.");
		}
		String edgeId = readString(in);
		String attrId = readString(in);
		int valueType = readType(in);
		Object oldValue = readValue(in, valueType);
		Object newValue = readValue(in, valueType);

		currentStream.edgeAttributeChanged(sourceId, sourceTime.newEvent(),
				edgeId, attrId, oldValue, newValue);

	}

	/**
	 * @see NetStreamConstants.EVENT_ADD_EDGE_ATTR
	 */
	protected void serve_EVENT_ADD_EDGE_ATTR(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received ADD_EDGE_ATTR command.");
		}
		String edgeId = readString(in);
		String attrId = readString(in);
		Object value = readValue(in, readType(in));

		currentStream.edgeAttributeAdded(sourceId, sourceTime.newEvent(),
				edgeId, attrId, value);

	}

	/**
	 * @see NetStreamConstants.EVENT_DEL_NODE_ATTR
	 */
	protected void serve_EVENT_DEL_NODE_ATTR(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received DEL_NODE_ATTR command.");
		}
		String nodeId = readString(in);
		String attrId = readString(in);

		currentStream.nodeAttributeRemoved(sourceId, sourceTime.newEvent(),
				nodeId, attrId);

	}

	/**
	 * @see NetStreamConstants.EVENT_CHG_NODE_ATTR
	 */
	protected void serve_EVENT_CHG_NODE_ATTR(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received EVENT_CHG_NODE_ATTR command.");
		}
		String nodeId = readString(in);
		String attrId = readString(in);
		int valueType = readType(in);
		Object oldValue = readValue(in, valueType);
		Object newValue = readValue(in, valueType);

		currentStream.nodeAttributeChanged(sourceId, sourceTime.newEvent(),
				nodeId, attrId, oldValue, newValue);
	}

	/**
	 * @see NetStreamConstants.EVENT_ADD_NODE_ATTR
	 */
	protected void serve_EVENT_ADD_NODE_ATTR(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received EVENT_ADD_NODE_ATTR command.");
		}
		String nodeId = readString(in);
		String attrId = readString(in);
		Object value = readValue(in, readType(in));

		currentStream.nodeAttributeAdded(sourceId, sourceTime.newEvent(),
				nodeId, attrId, value);
	}

	/**
	 * @see NetStreamConstants.EVENT_DEL_GRAPH_ATTR
	 */
	protected void serve_EVENT_DEL_GRAPH_ATTR(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received EVENT_DEL_GRAPH_ATTR command.");
		}
		String attrId = readString(in);

		currentStream.graphAttributeRemoved(sourceId, sourceTime.newEvent(),
				attrId);
	}

	/**
	 * @see NetStreamConstants.EVENT_CHG_GRAPH_ATTR
	 */
	protected void serve_EVENT_CHG_GRAPH_ATTR(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received EVENT_CHG_GRAPH_ATTR command.");
		}
		String attrId = readString(in);
		int valueType = readType(in);
		Object oldValue = readValue(in, valueType);
		Object newValue = readValue(in, valueType);

		currentStream.graphAttributeChanged(sourceId, sourceTime.newEvent(),
				attrId, oldValue, newValue);

	}

	/**
	 * @see NetStreamConstants.EVENT_ADD_GRAPH_ATTR
	 */
	protected void serve_EVENT_ADD_GRAPH_ATTR(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received EVENT_ADD_GRAPH_ATTR command.");
		}
		String attrId = readString(in);
		Object value = readValue(in, readType(in));
		if(debug){
			debug("NetStreamServer | EVENT_ADD_GRAPH_ATTR | %s=%s", attrId, value
				.toString());
		}
		currentStream.graphAttributeAdded(sourceId, sourceTime.newEvent(),
				attrId, value);

	}

	/**
	 * @see NetStreamConstants.EVENT_CLEARED
	 */
	protected void serve_EVENT_CLEARED(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received EVENT_CLEARED command.");
		}
		currentStream.graphCleared(sourceId, sourceTime.newEvent());

	}

	/**
	 * @see NetStreamConstants.EVENT_STEP
	 */
	protected void serve_EVENT_STEP(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received EVENT_STEP command.");
		}
		double time = readDouble(in);
		currentStream.stepBegins(sourceId, sourceTime.newEvent(), time);
	}

	/**
	 * @see NetStreamConstants.EVENT_DEL_EDGE
	 */
	protected void serve_EVENT_DEL_EDGE(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received EVENT_DEL_EDGE command.");
		}
		String edgeId = readString(in);
		currentStream.edgeRemoved(sourceId, sourceTime.newEvent(), edgeId);
	}

	/**
	 * @see NetStreamConstants.EVENT_ADD_EDGE
	 */
	protected void serve_EVENT_ADD_EDGE(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received ADD_EDGE command.");
		}
		String edgeId = readString(in);
		String from = readString(in);
		String to = readString(in);
		boolean directed = readBoolean(in);
		currentStream.edgeAdded(sourceId, sourceTime.newEvent(), edgeId, from,
				to, directed);
	}

	/**
	 * @see NetStreamConstants.DEL_NODE
	 */
	protected void serve_DEL_NODE(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received DEL_NODE command.");
		}
		String nodeId = readString(in);
		currentStream.nodeRemoved(sourceId, sourceTime.newEvent(), nodeId);
	}

	/**
	 * @see NetStreamConstants.EVENT_ADD_NODE
	 */
	protected void serve_EVENT_ADD_NODE(InputStream in) {
		if(debug){
			debug("NetStreamServer: Received EVENT_ADD_NODE command");
		}
		String nodeId = readString(in);
		currentStream.nodeAdded(sourceId, sourceTime.newEvent(), nodeId);

	}

	/**
	 * @param in
	 * @return
	 */
	protected int readType(InputStream in) {
		try {
			int data = 0;
			if ((data = in.read()) == -1) {
				debug("readType : could not read type");
				return 0;
			}
			return data;
		} catch (IOException e) {
			debug("readType: could not read type");
			e.printStackTrace();
		}

		return 0;
	}

	protected Object readValue(InputStream in, int valueType) {
		if (NetStreamConstants.TYPE_BOOLEAN == valueType) {
			return readBoolean(in);
		} else if (NetStreamConstants.TYPE_BOOLEAN_ARRAY == valueType) {
			return readBooleanArray(in);
		} else if (NetStreamConstants.TYPE_BYTE == valueType) {
			return readByte(in);
		} else if (NetStreamConstants.TYPE_BYTE_ARRAY == valueType) {
			return readByteArray(in);
		} else if (NetStreamConstants.TYPE_SHORT == valueType) {
			return readShort(in);
		} else if (NetStreamConstants.TYPE_SHORT_ARRAY == valueType) {
			return readShortArray(in);
		} else if (NetStreamConstants.TYPE_INT == valueType) {
			return readInt(in);
		} else if (NetStreamConstants.TYPE_INT_ARRAY == valueType) {
			return readIntArray(in);
		} else if (NetStreamConstants.TYPE_LONG == valueType) {
			return readLong(in);
		} else if (NetStreamConstants.TYPE_LONG_ARRAY == valueType) {
			return readLongArray(in);
		} else if (NetStreamConstants.TYPE_FLOAT == valueType) {
			return readFloat(in);
		} else if (NetStreamConstants.TYPE_FLOAT_ARRAY == valueType) {
			return readFloatArray(in);
		} else if (NetStreamConstants.TYPE_DOUBLE == valueType) {
			return readDouble(in);
		} else if (NetStreamConstants.TYPE_DOUBLE_ARRAY == valueType) {
			return readDoubleArray(in);
		} else if (NetStreamConstants.TYPE_STRING == valueType) {
			return readString(in);
		} else if (NetStreamConstants.TYPE_ARRAY == valueType) {
			return readArray(in);
		}
		return null;
	}

	/**
	 * @param in
	 * @return
	 */
	protected Object[] readArray(InputStream in) {
		byte[] data = new byte[4];

		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readArray: could not read length of array (int)");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(data);
			bb.flip();
			int len = bb.getInt();

			Object[] array = new Object[len];
			for (int i = 0; i < len; i++) {
				array[i] = readValue(in, readType(in));
			}
			return array;

		} catch (IOException e) {
			debug("readArray: could not read");
			e.printStackTrace();
		}
		return null;
	}

	protected String readString(InputStream in) {
		byte[] data = new byte[4];

		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readString: could not read length of array (int)");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(data);
			bb.flip();
			int len = bb.getInt();

			data = new byte[len];
			if (in.read(data, 0, len) != len) {
				return null;
			}
			return new String(data, Charset.forName("UTF-8"));
		} catch (IOException e) {
			debug("readString: could not read string");
			e.printStackTrace();
		}
		return null;
	}

	protected Boolean readBoolean(InputStream in) {
		int data = 0;
		try {
			data = in.read();
		} catch (IOException e) {
			debug("readByte: could not read");
			e.printStackTrace();
		}
		return data == 0 ? false : true;
	}

	protected Byte readByte(InputStream in) {
		byte data = 0;
		try {
			data = (byte) in.read();
		} catch (IOException e) {
			debug("readByte: could not read");
			e.printStackTrace();
		}
		return data;
	}

	protected Short readShort(InputStream in) {
		byte[] data = new byte[2];
		try {
			if (in.read(data, 0, 2) != 2) {
				debug("readShort: could not read");
				return 0;
			}
		} catch (IOException e) {
			debug("readShort: could not read");
			e.printStackTrace();
		}
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.put(data);
		bb.flip();
		return bb.getShort();
	}

	protected Integer readInt(InputStream in) {
		byte[] data = new byte[4];
		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readInt: could not read");
				return 0;
			}
		} catch (IOException e) {
			debug("readInt: could not read");
			e.printStackTrace();
		}
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(data);
		bb.flip();
		return bb.getInt();
	}

	protected Long readLong(InputStream in) {
		byte[] data = new byte[8];
		try {
			if (in.read(data, 0, 8) != 8) {
				debug("readLong: could not read");
				return 0L;
			}
		} catch (IOException e) {
			debug("readLong: could not read");
			e.printStackTrace();
		}
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.put(data);
		bb.flip();
		return bb.getLong();
	}

	protected Float readFloat(InputStream in) {
		byte[] data = new byte[4];
		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readFloat: could not read");
				return 0f;
			}
		} catch (IOException e) {
			debug("readFloat: could not read");
			e.printStackTrace();
		}
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(data);
		bb.flip();
		return bb.getFloat();
	}

	protected Double readDouble(InputStream in) {
		byte[] data = new byte[8];
		try {
			if (in.read(data, 0, 8) != 8) {
				debug("readDouble: could not read");
				return 0.0;
			}
		} catch (IOException e) {
			debug("readDouble: could not read");
			e.printStackTrace();
		}
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.put(data);
		bb.flip();
		return bb.getDouble();
	}

	/**
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected Integer[] readIntArray(InputStream in) {
		byte[] data = new byte[4];

		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readIntArray: could not read length of array (int)");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(data);
			bb.flip();
			int len = bb.getInt();

			data = new byte[len * 4];
			if (in.read(data, 0, len * 4) != len * 4) {
				debug("readIntArray: could not read array");
				return null;
			}

			bb = ByteBuffer.allocate(4 * len);
			bb.put(data);
			bb.flip();
			Integer[] res = new Integer[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.getInt();
			}
			return res;
		} catch (IOException e) {
			debug("readIntArray: could not read array");
			e.printStackTrace();
		}
		return null;
	}

	protected Boolean[] readBooleanArray(InputStream in) {
		byte[] data = new byte[4];

		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readBooleanArray: could not read length of array (int)");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(data);
			bb.flip();
			int len = bb.getInt();

			data = new byte[len];
			if (in.read(data, 0, len) != len) {
				debug("readBooleanArray: could not read array");
				return null;
			}

			bb = ByteBuffer.allocate(len);
			bb.put(data);
			bb.flip();
			Boolean[] res = new Boolean[len];
			for (int i = 0; i < len; i++) {

				byte b = bb.get();

				res[i] = b == 0 ? false : true;
			}
			return res;
		} catch (IOException e) {
			debug("readBooleanArray: could not read array");
			e.printStackTrace();
		}
		return null;
	}

	protected Byte[] readByteArray(InputStream in) {
		byte[] data = new byte[4];

		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readByteArray: could not read length of array (int)");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(data);
			bb.flip();
			int len = bb.getInt();

			data = new byte[len];
			if (in.read(data, 0, len) != len) {
				debug("readByteArray: could not read array");
				return null;
			}

			bb = ByteBuffer.allocate(len);
			bb.put(data);
			bb.flip();
			Byte[] res = new Byte[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.get();

			}
			return res;
		} catch (IOException e) {
			debug("readBooleanArray: could not read array");
			e.printStackTrace();
		}
		return null;
	}

	protected Double[] readDoubleArray(InputStream in) {
		byte[] data = new byte[4];

		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readDoubleArray: could not read length of array (int)");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(data);
			bb.flip();
			int len = bb.getInt();

			data = new byte[len * 8];
			if (in.read(data, 0, len * 8) != len * 8) {
				debug("readDoubleArray: could not read array");
				return null;
			}

			bb = ByteBuffer.allocate(8 * len);
			bb.put(data);
			bb.flip();
			Double[] res = new Double[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.getDouble();
			}
			return res;
		} catch (IOException e) {
			debug("readDoubleArray: could not read array");
			e.printStackTrace();
		}
		return null;
	}

	protected Float[] readFloatArray(InputStream in) {
		byte[] data = new byte[4];

		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readFloatArray: could not read length of array (int)");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(data);
			bb.flip();
			int len = bb.getInt();


			data = new byte[len * 4];
			if (in.read(data, 0, len * 4) != len * 4) {
				debug("readFloatArray: could not read array");
				return null;
			}

			bb = ByteBuffer.allocate(4 * len);
			bb.put(data);
			bb.flip();
			Float[] res = new Float[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.getFloat();
			}
			return res;
		} catch (IOException e) {
			debug("readFloatArray: could not read array");
			e.printStackTrace();
		}
		return null;
	}

	protected Long[] readLongArray(InputStream in) {
		byte[] data = new byte[4];

		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readLongArray: could not read length of array (int)");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(data);
			bb.flip();
			int len = bb.getInt();

			data = new byte[len * 8];
			if (in.read(data, 0, len * 8) != len * 8) {
				debug("readLongArray: could not read array");
				return null;
			}

			bb = ByteBuffer.allocate(8 * len);
			bb.put(data);
			bb.flip();
			Long[] res = new Long[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.getLong();
				debug(res[i] + ",");
			}
			debug("%n");
			return res;
		} catch (IOException e) {
			debug("readLongArray: could not read array");
			e.printStackTrace();
		}
		return null;
	}

	protected Short[] readShortArray(InputStream in) {
		byte[] data = new byte[4];

		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readShortArray: could not read length of array (int)");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(data);
			bb.flip();
			int len = bb.getInt();

			data = new byte[len * 2];
			if (in.read(data, 0, len * 2) != len * 2) {
				debug("readShortArray: could not read array");
				return null;
			}

			bb = ByteBuffer.allocate(2 * len);
			bb.put(data);
			bb.flip();
			Short[] res = new Short[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.getShort();
				debug(res[i] + ",");
			}
			debug("%n");
			return res;
		} catch (IOException e) {
			debug("readShortArray: could not read array");
			e.printStackTrace();
		}
		return null;
	}
}
