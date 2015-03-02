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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.graphstream.stream.netstream.packing.NetStreamUnpacker;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.miv.mbox.net.PositionableByteArrayInputStream;

/**
 * <p>
 * This class implements a receiver according to specifications the NetStream
 * protocol.
 * </p>
 * 
 * <p>
 * See {@link NetStreamConstants} for a full description of the protocol, the
 * sender and the receiver.
 * </p>
 * 
 * @see NetStreamConstants
 * @see NetStreamSender
 * 
 * 
 *      Copyright (c) 2010 University of Luxembourg
 * 
 *      NetStreamReceiver.java
 * @since Aug 13, 2011
 * 
 * @author Yoann Pigné
 * 
 */
public class NetStreamReceiver extends Thread implements NetStreamDecoder {

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
	 * Utility class that decodes messages according to the NetStream Protocol
	 */
	protected NetStreamDecoder decoder;
	

	/**
	 * Current active incoming connections.
	 */
	protected HashMap<SelectionKey, IncomingBuffer> incoming = new HashMap<SelectionKey, IncomingBuffer>();

	class DefaultUnpacker extends NetStreamUnpacker {

		@Override
		public ByteBuffer unpackMessage(ByteBuffer buffer, int startIndex,
				int endIndex) {
			return buffer;
		}

		@Override
		public int unpackMessageSize(ByteBuffer buffer) {
			return buffer.getInt();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.netstream.packing.NetStreamUnpacker#sizeOfInt
		 * ()
		 */
		@Override
		public int sizeOfInt() {
			return 4;
		}
	};
	private NetStreamUnpacker unpacker;

	// Constructors

	/**
	 * New NetStream Receiver, awaiting in its own thread at the given host name
	 * and port, for new graph events.
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
	 * New NetStream Receiver, awaiting in its own thread at "localhost" on the
	 * given port, for new graph events.
	 * 
	 * @param port
	 *            The port to listen at messages.
	 */
	public NetStreamReceiver(int port) throws IOException, UnknownHostException {
		this("localhost", port, false);
	}

	/**
	 * New NetStream Receiver, awaiting in its own thread at the given host name
	 * and port, for new graph events.
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
		this.unpacker = new DefaultUnpacker();
		this.decoder = new DefaultNetStreamDecoder();
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
		decoder.setDebugOn(on);
	}


	/**
	 * Stop the receiver.
	 */
	public synchronized void quit() {
		loop = false;
		key.selector().wakeup();

		if (debug)
			debug("stopped");
	}

	/**
	 * Ask the receiver about its active connections
	 */
	public synchronized boolean hasActiveConnections() {
		return !incoming.isEmpty();
	}

	/**
	 * Sets an optional NetStreamUnpaker whose "unpack" method will be called on
	 * each message.
	 * 
	 * It allows to do extra decoding on the all byte array message. You can
	 * also decrypt things.
	 * 
	 * @param unpaker
	 */
	public void setUnpacker(NetStreamUnpacker unpaker) {
		this.unpacker = unpaker;
	}
	public void removeUnpacker() {
		unpacker = new DefaultUnpacker();
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

		if (debug) {
			debug("receiver //" + hostname + ":" + port + " finished");
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
			error(e, "I/O error in receiver //%s:%d thread: aborting: %s",
					hostname, port, e.getMessage());

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

		if (!buf.active) {
			incoming.remove(key);
			if (debug)
				debug("removing buffer %s from incoming for geting inactive. %d left",
						key.toString(), incoming.size());

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

			int sizeOfInt = unpacker.sizeOfInt();
			// Buffers the data.

			nbytes = bufferize(pos, socket);
			limit = pos + nbytes;

			if (nbytes <= 0)
				return;

			if (debug) {
				debug("<chunk (%d bytes) from "
						+ socket.socket().getInetAddress() + ":"
						+ socket.socket().getPort() + ">", nbytes);
				int at = buf.position();
				for (int i = 0; i < nbytes; i++) {
					System.err.printf("%d ", buf.get(at + i));
				}
				System.err.println();
				buf.position(at);
			}
			// Read the first header.

			if (end < 0) {
				if ((limit - beg) >= sizeOfInt) {
					// If no data has been read yet in the buffer or if the
					// buffer
					// was emptied completely at previous call: prepare to read
					// a
					// new message by decoding its header.

					buf.position(0);
					int size = unpacker.unpackMessageSize(buf);
					end = size + sizeOfInt;
					beg = sizeOfInt;
					if (debug)
						debug("start to bufferize a %d byte long messsage",
								size);
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

					ByteBuffer unpackedBuffer = unpacker.unpackMessage(buf, beg, end);
					if (unpackedBuffer == buf) {
						in = new PositionableByteArrayInputStream(buf.array(), beg, end - beg);
					} else {
						in = new PositionableByteArrayInputStream(
								unpackedBuffer.array(), 0, unpackedBuffer.capacity());
					}
					
					decoder.decodeMessage(in);
					buf.position(end);

					if (end + sizeOfInt <= limit) {
						// There is a following message.

						beg = end + sizeOfInt;
						end = end + unpacker.unpackMessageSize(buf) + sizeOfInt;
					} else {
						// There is the beginning of a following message
						// but the header is incomplete. Compact the buffer
						// and stop here.
						assert (beg >= sizeOfInt);

						beg = end;
						int p = sizeOfInt - ((end + sizeOfInt) - limit);
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

					ByteBuffer unpackedBuffer = unpacker.unpackMessage(buf, beg, end);
					if (unpackedBuffer == buf) {
						in = new PositionableByteArrayInputStream(buf.array(), beg, end - beg);
					} else {
						in = new PositionableByteArrayInputStream(
								unpackedBuffer.array(), 0, unpackedBuffer.capacity());
					}
					
					decoder.decodeMessage(in);
					
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
		 * Compact the buffer by removing all read data before <code>beg</code>.
		 * The <code>beg</code>, <code>end</code> and <code>pos</code> markers
		 * are updated accordingly. Compact works only if beg is larger than
		 * four (the size of a header).
		 * 
		 * @return the offset.
		 */
		protected int compactBuffer() {
			if (beg > unpacker.sizeOfInt()) {
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

	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.NetStreamDecoder#getStream(java.lang.String)
	 */
	public ThreadProxyPipe getStream(String name) {
		return decoder.getStream(name);
	}

	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.NetStreamDecoder#getDefaultStream()
	 */
	public ThreadProxyPipe getDefaultStream() {
		return decoder.getDefaultStream();
	}

	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.NetStreamDecoder#register(java.lang.String, org.graphstream.stream.thread.ThreadProxyPipe)
	 */
	public void register(String name, ThreadProxyPipe stream) throws Exception {
		decoder.register(name, stream);
	}

	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.NetStreamDecoder#decodeMessage(java.io.InputStream)
	 */
	public void decodeMessage(InputStream in) throws IOException {
		decoder.decodeMessage(in);
		
	}


}
