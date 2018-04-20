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
 * @since 2016-02-01
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.binary;

import org.graphstream.stream.Pipe;
import org.graphstream.stream.Sink;
import org.graphstream.stream.SourceBase;
import org.graphstream.stream.Replayable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * This class is a proxy that can exchange event binary-encoded (opposed to
 * text-encoder) with another proxy.
 * <p/>
 * It can be either a server that will listen to connections, or a client that
 * will connect to a server. The
 * {@link org.graphstream.stream.binary.ByteFactory} passed to the constructor
 * will define the encoder and decoder of binary data.
 * <p/>
 * Proxy can run on its own thread, just by calling the
 * {@link ByteProxy#start()} method. It can be manually used with the
 * {@link ByteProxy#poll()} method that process available
 * {@link java.nio.channels.SelectionKey}.
 *
 * @since 31/01/16.
 */
public class ByteProxy extends SourceBase implements Pipe, Runnable {
	private static final Logger LOGGER = Logger.getLogger(ByteProxy.class.getName());

	/**
	 * Defines the mode of this proy, server or client.
	 */
	public enum Mode {
		/**
		 * The proxy is a server. It has its own
		 * {@link java.nio.channels.ServerSocketChannel} and can listen to entering
		 * connections.
		 */
		SERVER,
		/**
		 * The proxy is just a client that connects to another proxy server.
		 */
		CLIENT
	}

	protected static final int BUFFER_INITIAL_SIZE = 8192;

	protected final ByteFactory byteFactory;
	protected final ByteEncoder encoder;
	protected final ByteDecoder decoder;

	/**
	 * Flag to tell is the proxy is running or not.
	 */
	protected final AtomicBoolean running;

	/**
	 * Proxy mode.
	 */
	public final Mode mode;

	/**
	 * The address the proxy is bound to. If in server mode, this is the address
	 * where the server is listening to connections. If in client mode, this is the
	 * address where the proxy is connected to.
	 */
	public final InetAddress address;

	/**
	 * The port listened or connected to.
	 */
	public final int port;

	/**
	 * The main channel of the proxy. If in server mode, it will be the
	 * {@link java.nio.channels.ServerSocketChannel}. Else, just the
	 * {@link java.nio.channels.SocketChannel} connected to the server.
	 */
	protected SelectableChannel mainChannel;

	/**
	 * Multiplexor.
	 */
	protected Selector selector;

	/**
	 * The thread processing selection key when the proxy has been started. If the
	 * proxy is not started, the field will be null.
	 */
	protected Thread thread;

	/**
	 * List of opened channels that can be written when new events are received by
	 * the proxy.
	 */
	protected Collection<SocketChannel> writableChannels;

	/**
	 * If not null, this will be replayed when a new connection occured.
	 */
	protected Replayable replayable;

	/**
	 * Create a new ByteProxy, in server mode, which will be bound to a local
	 * address and the given port.
	 *
	 * @param factory
	 *            the factory to create encoder and decoder
	 * @param port
	 *            port to bind the server to
	 * @throws IOException
	 *             if troubles occurred while connecting the socket
	 */
	public ByteProxy(ByteFactory factory, int port) throws IOException {
		this(factory, Mode.SERVER, InetAddress.getLocalHost(), port);
	}

	/**
	 * Complete constructor of the proxy.
	 *
	 * @param factory
	 *            the factory to create encoder and decoder
	 * @param mode
	 *            mode of the proxy
	 * @param address
	 *            address to listen or to connect to
	 * @param port
	 *            port to listen or to connect to
	 * @throws IOException
	 *             if troubles occurred while connecting the socket
	 */
	public ByteProxy(ByteFactory factory, Mode mode, InetAddress address, int port) throws IOException {
		running = new AtomicBoolean(false);
		writableChannels = new LinkedList<>();
		replayable = null;
		thread = null;

		this.mode = mode;
		this.address = address;
		this.port = port;

		byteFactory = factory;
		encoder = factory.createByteEncoder();
		decoder = factory.createByteDecoder();

		encoder.addTransport(new ByteEncoder.Transport() {
			@Override
			public void send(ByteBuffer buffer) {
				doSend(buffer);
			}
		});

		decoder.addSink(new Sink() {
			@Override
			public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
				sendGraphAttributeAdded(sourceId, timeId, attribute, value);
			}

			@Override
			public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
					Object newValue) {
				sendGraphAttributeChanged(sourceId, timeId, attribute, oldValue, newValue);
			}

			@Override
			public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
				sendGraphAttributeRemoved(sourceId, timeId, attribute);
			}

			@Override
			public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute,
					Object value) {
				sendNodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
			}

			@Override
			public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute,
					Object oldValue, Object newValue) {
				sendNodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue, newValue);
			}

			@Override
			public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
				sendNodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
			}

			@Override
			public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute,
					Object value) {
				sendEdgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
			}

			@Override
			public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute,
					Object oldValue, Object newValue) {
				sendEdgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue, newValue);
			}

			@Override
			public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
				sendEdgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
			}

			@Override
			public void nodeAdded(String sourceId, long timeId, String nodeId) {
				sendNodeAdded(sourceId, timeId, nodeId);
			}

			@Override
			public void nodeRemoved(String sourceId, long timeId, String nodeId) {
				sendNodeRemoved(sourceId, timeId, nodeId);
			}

			@Override
			public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
					boolean directed) {
				sendEdgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
			}

			@Override
			public void edgeRemoved(String sourceId, long timeId, String edgeId) {
				sendEdgeRemoved(sourceId, timeId, edgeId);
			}

			@Override
			public void graphCleared(String sourceId, long timeId) {
				sendGraphCleared(sourceId, timeId);
			}

			@Override
			public void stepBegins(String sourceId, long timeId, double step) {
				sendStepBegins(sourceId, timeId, step);
			}
		});

		init();
	}

	protected void init() throws IOException {
		InetSocketAddress isa = new InetSocketAddress(address, port);

		selector = Selector.open();

		switch (mode) {
		case SERVER:
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.bind(isa);

			mainChannel = serverChannel;
			mainChannel.register(selector, SelectionKey.OP_ACCEPT);

			break;
		case CLIENT:
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.connect(isa);
			socketChannel.finishConnect();
			socketChannel.configureBlocking(false);

			mainChannel = socketChannel;
			mainChannel.register(selector, SelectionKey.OP_READ + SelectionKey.OP_WRITE);
			writableChannels.add(socketChannel);
			break;
		}
	}

	/**
	 * Set the stream that can be replayed on a new connection.
	 *
	 * @param replayable
	 *            the stream to replay, or null if nothing has to be replayed.
	 */
	public void setReplayable(Replayable replayable) {
		this.replayable = replayable;
	}

	/**
	 * Starts the proxy worker.
	 */
	public synchronized void start() {
		if (thread != null) {
			LOGGER.warning("Already started.");
		} else {
			Thread t = new Thread(this);
			t.start();
		}
	}

	/**
	 * Stops the proxy worker, if running, and wait the end of the worker thread.
	 *
	 * @throws InterruptedException
	 *             if an interruption occurred while waiting for the end of the
	 *             worker thread.
	 */
	public void stop() throws InterruptedException {
		if (thread != null) {
			Thread t = thread;
			running.set(false);

			t.join();
		}
	}

	@Override
	public void run() {
		thread = Thread.currentThread();
		running.set(true);

		LOGGER.info(String.format("[%s] started on %s:%d...", mode, address.getHostName(), port));

		while (running.get()) {
			poll();
		}

		thread = null;
	}

	protected void processSelectedKeys() throws IOException {
		Set<?> readyKeys = selector.selectedKeys();
		Iterator<?> i = readyKeys.iterator();

		while (i.hasNext()) {
			SelectionKey key = (SelectionKey) i.next();

			i.remove();

			if (key.isAcceptable()) {
				//
				// If a new connection occurs, register the new socket
				// in the multiplexer.
				//

				assert mode == Mode.SERVER;

				ServerSocketChannel ssocket = (ServerSocketChannel) key.channel();
				SocketChannel socketChannel = ssocket.accept();

				LOGGER.info(String.format("accepting socket %s:%d", socketChannel.socket().getInetAddress(),
						socketChannel.socket().getPort()));

				socketChannel.finishConnect();
				socketChannel.configureBlocking(false);

				if (decoder != null)
					socketChannel.register(selector, SelectionKey.OP_READ);

				replay(socketChannel);
				writableChannels.add(socketChannel);
			} else if (key.isReadable()) {
				//
				// If a message arrives, read it.
				//

				readDataChunk(key);
			} else if (key.isWritable() && key.attachment() != null) {
				ByteBuffer buffer = (ByteBuffer) key.attachment();
				WritableByteChannel out = (WritableByteChannel) key.channel();

				try {
					out.write(buffer);
				} catch (IOException e) {
					LOGGER.severe("I/O error while writing to channel.");
					close(out);
				} finally {
					key.cancel();
				}
			}
		}
	}

	/**
	 * Same as calling {@link #poll(boolean)} with blocking flag set to true.
	 */
	public void poll() {
		poll(true);
	}

	/**
	 * Wait until one or several chunks of message are acceptable. This method
	 * should be called in a loop. It can be used to block a program until some data
	 * is available.
	 *
	 * @param blocking
	 *            flag true if method has to wait for some keys to be ready. If
	 *            false, just process the available keys.
	 */
	public void poll(boolean blocking) {
		try {
			if (blocking) {
				if (selector.select() > 0) {
					processSelectedKeys();
				}
			} else {
				if (selector.selectNow() > 0) {
					processSelectedKeys();
				}
			}
		} catch (IOException e) {
			LOGGER.severe(String.format("I/O error in receiver //:%d thread: aborting: %s", port, e.getMessage()));
			running.set(false);
		} catch (Throwable e) {
			LOGGER.severe(String.format("Unknown error: %s", e.getMessage()));
			e.printStackTrace();
			running.set(false);
		}
	}

	/**
	 * When data is readable on a socket, send it to the appropriate buffer
	 * (creating it if needed).
	 */
	protected void readDataChunk(SelectionKey key) throws IOException {
		ByteBuffer buffer = (ByteBuffer) key.attachment();
		SocketChannel socket = (SocketChannel) key.channel();

		if (buffer == null) {
			buffer = ByteBuffer.allocate(BUFFER_INITIAL_SIZE);
			key.attach(buffer);

			LOGGER.info(String.format("creating buffer for new connection from %s:%d", socket.socket().getInetAddress(),
					socket.socket().getPort()));
		}

		try {
			int r = socket.read(buffer);

			if (r < 0) {
				//
				// End-of-stream
				//

				LOGGER.info("end-of-stream reached. Closing the mainChannel.");
				close(socket);
			} else if (r == 0) {
				LOGGER.warning("Strange, no binary read.");
			} else {
				while (decoder.validate(buffer)) {
					buffer.flip();
					decoder.decode(buffer);
					buffer.compact();
				}

				if (!buffer.hasRemaining()) {
					ByteBuffer bigger = ByteBuffer.allocate(buffer.capacity() + BUFFER_INITIAL_SIZE);
					bigger.put(buffer);
					key.attach(bigger);
				}
			}
		} catch (IOException e) {
			LOGGER.severe(String.format("receiver //%s:%d cannot read object socket mainChannel (I/O error): %s",
					address.getHostName(), port, e.getMessage()));

			close(key.channel());
		}
	}

	protected void doSend(ByteBuffer buffer) {
		ByteBuffer sendBuffer = ByteBuffer.allocate(buffer.remaining());
		sendBuffer.put(buffer);
		sendBuffer.rewind();

		Iterator<SocketChannel> channels = writableChannels.iterator();

		while (channels.hasNext()) {
			SocketChannel writableChannel = channels.next();

			try {
				try {
					writableChannel.write(sendBuffer.duplicate());
				} catch (NotYetConnectedException e) {
					writableChannel.register(selector, SelectionKey.OP_WRITE, sendBuffer.duplicate());
				}
			} catch (IOException e) {
				LOGGER.severe("I/O error while writing to channel : " + e.getMessage());

				channels.remove();
				close(writableChannel);
			}
		}
	}

	protected void replay(final SocketChannel channel) {
		if (replayable != null) {
			final Replayable.Controller controller = replayable.getReplayController();
			final ByteEncoder encoder = byteFactory.createByteEncoder();

			encoder.addTransport(new ByteEncoder.Transport() {
				@Override
				public void send(ByteBuffer buffer) {
					try {
						channel.write(buffer);
					} catch (IOException e) {
						LOGGER.severe("Failled to replay : " + e.getMessage());
						controller.removeSink(encoder);
					}
				}
			});

			controller.addSink(encoder);
			controller.replay();
		}
	}

	protected void close(Channel channel) {
		writableChannels.remove(channel);

		if (channel == mainChannel) {
			LOGGER.warning("Closing main channel.");

			if (running.get()) {
				try {
					stop();
				} catch (InterruptedException e) {
					LOGGER.warning("Failed to properly terminate the worker.");
				}
			}
		}

		try {
			channel.close();
		} catch (IOException e) {
			LOGGER.warning("closing channel: " + e.getMessage());
		}
	}

	@Override
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
		encoder.graphAttributeAdded(sourceId, timeId, attribute, value);
	}

	@Override
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
		encoder.graphAttributeChanged(sourceId, timeId, attribute, oldValue, newValue);
	}

	@Override
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
		encoder.graphAttributeRemoved(sourceId, timeId, attribute);
	}

	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		encoder.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
	}

	@Override
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		encoder.nodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue, newValue);
	}

	@Override
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		encoder.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
	}

	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		encoder.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
	}

	@Override
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		encoder.edgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue, newValue);
	}

	@Override
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		encoder.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		encoder.nodeAdded(sourceId, timeId, nodeId);
	}

	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		encoder.nodeRemoved(sourceId, timeId, nodeId);
	}

	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		encoder.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
	}

	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		encoder.edgeRemoved(sourceId, timeId, edgeId);
	}

	@Override
	public void graphCleared(String sourceId, long timeId) {
		encoder.graphCleared(sourceId, timeId);
	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step) {
		encoder.stepBegins(sourceId, timeId, step);
	}
}
