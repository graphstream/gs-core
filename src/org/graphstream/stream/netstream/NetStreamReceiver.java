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

import java.io.BufferedInputStream;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.sync.SourceTime;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.miv.mbox.MBoxListener;
import org.miv.mbox.net.IdAlreadyInUseException;
import org.miv.mbox.net.MBoxLocator;
import org.miv.mbox.net.PositionableByteArrayInputStream;


/**
 * Receives messages and dispatches them to message boxes.
 * 
 * <p>
 * A message receiver listen at a given address and port. Several senders ({@link org.miv.mbox.net.Sender})
 * can connect to it, the receiver will demultiplex the data flow and dispatch
 * incoming messages to registered message boxes.
 * </p>
 * 
 * <p>
 * A receiver is created by giving it the host and port on which it must listen
 * at incoming messages (using a {@link org.miv.mbox.net.MBoxLocator},
 * although, the receiver is not a real message box, is represents a set of
 * them). Then one registers several message boxes inside the receiver. The
 * receiver runs in its own thread.
 * </p>
 * 
 * <p>
 * There exist two way to receive messages with the receiver. One is to register
 * a {@link org.miv.mbox.MBoxListener} using the
 * {@link #register(String,MBoxListener)} method. This will return a message box
 * implementation of type {@link org.miv.mbox.MBoxStandalone} that can buffer
 * messages from the receiver thread an later dispatch them to the MBoxListener.
 * </p>
 * 
 * <p>
 * The other way is to directly implement the {@link org.miv.mbox.MBox}
 * interface or reify the {@link org.miv.mbox.MBoxBase} abstract class to
 * receive messages directly by registering using the
 * {@link #register(String,org.miv.mbox.MBox)} method. This is more flexible,
 * but the user must ensure proper locking since the
 * {@link org.miv.mbox.MBox#post(String,Object...)} method will be invoked
 * from the receiver thread (the MBoxBase class already does this job).
 * </p>
 * 
 * TODO: What happen to messages that are directed to inexistent message boxes?
 * 
 * @see org.miv.mbox.MBox
 * @see org.miv.mbox.MBoxBase
 * @see org.miv.mbox.MBoxListener
 * @see org.miv.mbox.net.Sender
 * @author Antoine Dutot
 * @since 20040624
 */



/**
 * 
 */
public class NetStreamReceiver extends Thread{
	// Attributes

	protected String graphId = "NetStream";
	protected SourceTime sourceTime;

	
		/**
		 * Host name for this application.
		 */
		protected MBoxLocator locator;

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
		 * @see #register(String,MBoxListener)
		 */
		//protected HashMap<String,MBox> boxes = new HashMap<String,MBox>();
		protected HashMap<String,ThreadProxyPipe> streams = new HashMap<String,ThreadProxyPipe>();
		
		/**
		 * Current active incoming connections.
		 */
		protected HashMap<SelectionKey,IncomingBuffer> incoming = new HashMap<SelectionKey,IncomingBuffer>();

	// Constructors

		/**
		 * New receiver, awaiting for messages in its own thread at the given
		 * locator. The receiver aggregates several message boxes, therefore its
		 * locator will only contain a host id, something of the form
		 * "//"+hostname+":"+port. The initialisation is done in the thread that
		 * creates this receiver. Then the receiver will run in a thread of its own.
		 * @param locator the host and port of this receiver to listen at messages.
		 */
		public NetStreamReceiver( MBoxLocator locator )
			throws IOException, UnknownHostException
		{
			this( locator.getHostname(), locator.getPort() );
		}

		/**
		 * New receiver, awaiting for messages in its own thread at the given
		 * locator. The receiver aggregates several message boxes, therefore its
		 * locator will only contain a host id, something of the form
		 * "//"+hostname+":"+port. The initialisation is done in the thread that
		 * creates this receiver. Then the receiver will run in a thread of its own.
		 * @param locator the host and port of this receiver to listen at messages.
		 * @param debug If true informations are output for each message received.
		 */
		public NetStreamReceiver( MBoxLocator locator, boolean debug )
			throws IOException, UnknownHostException
		{
			this( locator.getHostname(), locator.getPort(), debug );
		}

		/**
		 * Like {@link #Receiver(MBoxLocator)} but specify the host name and port
		 * explicitly.
		 * @param hostname The host name to listen at messages.
		 * @param port The port to listen at messages.
		 */
		public NetStreamReceiver( String hostname, int port )
			throws IOException, UnknownHostException
		{
			this( hostname, port, false );
		}

		/**
		 * Like {@link #Receiver(MBoxLocator,boolean)} but specify the host name and
		 * port explicitly.
		 * @param hostname The host name to listen at messages.
		 * @param port The port to listen at messages.
		 * @param debug If true informations are output for each message received.
		 */
		public NetStreamReceiver( String hostname, int port, boolean debug )
			throws IOException, UnknownHostException
		{
			locator = new MBoxLocator( hostname, port );
			sourceTime = new SourceTime(0);
			
			setDebugOn( debug );
			init();
			start();
		}

	// Access

		/**
		 * False as soon as the receiver terminates.
		 */
		public synchronized boolean isRunning()
		{
			return loop;
		}

		/**
		 * Locator of this receiver.
		 */
		public MBoxLocator getLocator()
		{
			return locator;
		}

		/**
		 * Message box attached to the given message box name, or null if no MBox is
		 * registered at this name.
		 * @param name Identifier of the MBox listener.
		 */
		//public synchronized MBox getMBox( String name )
		//{
		//	return boxes.get( name );
		//}

		public ThreadProxyPipe getStream(String name){
			ThreadProxyPipe s = streams.get(name);
			if (s==null){
				s=new ThreadProxyPipe();
				streams.put(name, s);
			}
			return s;
		}
		
		
		public ThreadProxyPipe getDefaultStream(){
			ThreadProxyPipe s = streams.get("default");
			if(s==null){
				s= new ThreadProxyPipe();
				streams.put("default", s);
			}
			return s;
			
		}
		
	// Commands

		/**
		 * Initialise the server socket.
		 */
		protected void init()
			throws IOException, UnknownHostException
		{
			selector = Selector.open();
			server   = ServerSocketChannel.open();

			server.configureBlocking( false );

			InetAddress       ia  = InetAddress.getByName( locator.getHostname() );
			InetSocketAddress isa = new InetSocketAddress( ia, locator.getPort() );

			server.socket().bind( isa );
			
			if( debug )
				debug( "bound to socket %s:%d", server.socket().getInetAddress(),
						server.socket().getLocalPort() );
			
			// Register a first server socket inside the multiplexer.

			key = server.register( selector, SelectionKey.OP_ACCEPT );
		}

		/**
		 * Enable or disable debugging.
		 */
		public void setDebugOn( boolean on )
		{
			debug = on;
		}

		/**
		 * Register a message box listener for incoming messages. All messages with
		 * the given name will be directed to it. The listener can then call
		 * {@link org.miv.mbox.MBoxBase#processMessages()} to receive its pending
		 * messages.
		 * @param name Filter only message with this name to the given listener.
		 * @param listener The listener to register.
		 * @throws IdAlreadyInUseException If another message box is already registered
		 * 	at the given name.
		 */
		/*public synchronized MBoxStandalone register( String name, MBoxListener listener )
			throws IdAlreadyInUseException
		{
			if( boxes.containsKey( name ) )
				throw new IdAlreadyInUseException( "name "+name+" already registered" );
			
			MBoxStandalone mbox = new MBoxStandalone( listener );
			boxes.put( name, mbox );
			
			if( debug )
				debug( "registered message box %s", name );
			
			return mbox;
		}*/

		/**
		 * Register a message box. All messages with the given name will be directed
		 * to it. Unlike with the {@link MBoxListener}, the messages are directly
		 * posted to the given message box that must ensure proper locking (since
		 * the receiver runs in a distinct thread) that is implemented by the user.
		 * @param name Filter only message with this name to the given message box.
		 * @param box The message box implementation to post messages to.
		 * @throws IdAlreadyInUseException If another message box is already registered
		 * 	at the given name.
		 */
		public synchronized void register( String name, ThreadProxyPipe stream )
			throws Exception
		{
			if( streams.containsKey( name ) )
				throw new  Exception( "name "+name+" already registered" );

			streams.put( name, stream );
			
			if( debug )
				debug( "registered message box %s", name );
		}

		/**
		 * Stop the receiver.
		 */
		public synchronized void quit()
		{
			loop = false;
			
			if( debug )
				debug( "stopped" );
		}

		/**
		 * Wait for connections, accept them, demultiplexes them and dispatch
		 * messages to registered message boxes.
		 */
		@Override
		public void run()
		{
			boolean l;

			synchronized( this ) { l = loop; }

			while( l )
			{
				poll();
			
				synchronized( this ) { l = loop; }
			}

			try
			{
				server.close();
			}
			catch( IOException e )
			{
				error( "cannot close the server socket: " + e.getMessage(), e );
			}

			debug( "receiver "+locator+" finished" );
		}

		/**
		 * Wait until one or several chunks of message are acceptable. This method
		 * should be called in a loop. It can be used to block a program until some
		 * data is available.
		 */
		public void
		poll()
		{
			try
			{
				// Wait for incoming messages in a loop.

				if( key.selector().select() > 0 )
				{
					Set<?> readyKeys = selector.selectedKeys();
					Iterator<?> i = readyKeys.iterator();

					while( i.hasNext() )
					{
						SelectionKey akey = (SelectionKey) i.next();

						i.remove();

						if( akey.isAcceptable() )
						{
							// If a new connection occurs, register the new socket in the multiplexer.

							ServerSocketChannel ssocket = (ServerSocketChannel) akey.channel();
							SocketChannel       socket  = ssocket.accept();

							if( debug )
								debug( "accepting socket %s:%d", 
									socket.socket().getInetAddress(),
									socket.socket().getPort() );
							
							socket.configureBlocking( false );
							socket.finishConnect();

//							SelectionKey otherKey = socket.register( selector, SelectionKey.OP_READ );
							socket.register( selector, SelectionKey.OP_READ );
						}
						else if( akey.isReadable() )
						{
							// If a message arrives, read it.

							readDataChunk( akey );
						}
						else if( akey.isWritable() )
						{
							throw new RuntimeException( "should not happen" );
						}
					}
				}
			}
			catch( IOException e )
			{
				error( e, "I/O error in receiver %s thread: aborting: %s",
						locator, e.getMessage() );

				loop = false;
			}
			catch( Throwable e )
			{
				error( e, "Unknown error: %s", e.getMessage() );
				
				loop = false;
			}
		}

		/**
		 * When data is readable on a socket, send it to the appropriate buffer
		 * (creating it if needed).
		 */
		protected void
		readDataChunk( SelectionKey key )
			throws IOException
		{
			IncomingBuffer buf = incoming.get( key );

			if( buf == null )
			{
				buf = new IncomingBuffer();
				incoming.put( key, buf );

				SocketChannel socket = (SocketChannel) key.channel();
				
				if( debug )
					debug( "creating buffer for new connection from %s:%d",
							socket.socket().getInetAddress(),
							socket.socket().getPort() );
			}

			try
			{
				buf.readDataChunk( key );
			}
			catch( IOException e )
			{
				incoming.remove( key );
				e.printStackTrace();
				error( e, "receiver %s cannot read object socket channel (I/O error): %s",
					locator.toString(), e.getMessage() );
				loop = false;
			}
		}

	// Utilities

		protected void
		error( String message, Object ... data )
		{
			error( null, message, data );
		}

		protected static final String LIGHT_YELLOW = "[33;1m";
		protected static final String RESET = "[0m";
		
		protected void
		error( Throwable e, String message, Object ... data )
		{
			//System.err.print( LIGHT_YELLOW );
			System.err.print( "[" );
			//System.err.print( RESET );
			System.err.printf( message, data );
			//System.err.print( LIGHT_YELLOW );
			System.err.printf( "]%n" );
			//System.err.println( RESET );

			if( e != null )
				e.printStackTrace();
		}

		protected void
		debug( String message, Object ... data )
		{
			//System.err.print( LIGHT_YELLOW );
			System.err.printf( "[%s | ", locator.toString() );
			//System.err.print( RESET );
			System.err.printf( message, data );
			//System.err.print( LIGHT_YELLOW );
			System.err.printf( "]%n" );
			//System.err.println( RESET );
		}

	// Nested classes

	/**
	 * The connection to a sender.
	 *
	 * The receiver maintains several incoming connections and demultiplexes them.
	 */
	protected class IncomingBuffer
	{
	// Attributes

		protected static final int BUFFER_INITIAL_SIZE = 8192; // 65535, 4096
		
		/**
		 * Buffer for reading.
		 */
		protected ByteBuffer buf = ByteBuffer.allocate( BUFFER_INITIAL_SIZE );

		/**
		 * Index in the buffer past the last byte that forms the current message.
		 * End can be out of the buffer or out of the data read actually.
		 */
		protected int end = -1;

		/**
		 * Index in the buffer of the first byte that forms the currents message.
		 * Beg does not count the 4 bytes that give the size of the message. While
		 * the header is being read, beg is the first byte of the header.
		 */
		protected int beg = 0;

		/**
		 * Position inside beg and end past the last byte read. All bytes at and
		 * after pos have unspecified contents. Pos always verifies pos&gt;=beg and
		 * pos&lt;end. While the header is being read, pos is past the last byte
		 * of the header that has been read.
		 */
		protected int pos = 0;

		/**
		 * Object input stream for reading the buffer. This input stream reads data
		 * from the "bin" positionable byte array input stream, itself mapped on
		 * the current message to decode.
		 */
		PositionableByteArrayInputStream in;
		
		/**
		 * Input stream filter on the buffer. This descendant of
		 * ByteArrayInputStream is able to change its offset and length so that we
		 * can map exactly the message to decode inside the buffer.
		 */
		PositionableByteArrayInputStream bin;

		/**
		 * When false the socket is closed and this buffer must be removed
		 * from the active connections.
		 */
		protected boolean active = false;

	// Constructors

		public
		IncomingBuffer()
		{
		}

	// Commands

		/**
		 * Read the available bytes and buffers them. If one or more complete
		 * serialised objects are available, send them to their respective MBoxes.
		 *
		 * Here is the junk...
		 */
		public void
		readDataChunk( SelectionKey key )
			throws IOException
		{
			int           limit  = 0;		// Index past the last byte read during the current invocation.
			int           nbytes = 0;		// Number of bytes read.
			SocketChannel socket = (SocketChannel) key.channel();

			// Buffers the data.

			nbytes = bufferize( pos, socket );
			limit  = pos + nbytes;

			if( nbytes <= 0 )
				return;

			debug( "<chunk (%d bytes) from "+socket.socket().getInetAddress()+":"+socket.socket().getPort()+">", nbytes );

			// Read the first header.

			if( end < 0 )
			{
				if( ( limit-beg ) >= 4 )
				{
					// If no data has been read yet in the buffer or if the buffer
					// was emptied completely at previous call: prepare to read a
					// new message by decoding its header.

					buf.position( 0 );
					end = buf.getInt() + 4;
					beg = 4;
				}
				else
				{
					// The header is incomplete, wait next call to complete it.

					pos = limit;
				}
			}

			// Read one or more messages or wait next call to buffers more.

			if( end > 0 )
			{
				while( end < limit )
				{
					// While the end of the message is in the limit of what was
					// read, there are one or more complete messages. Decode them
					// and read the header of the next message, until a message is
					// incomplete or there are no more messages or a header is
					// incomplete.

					decodeMessage( limit );
					buf.position( end );

					if( end + 4 <= limit )
					{
						// There is a following message.
						
						beg = end + 4;
						end = end + buf.getInt() + 4;
					}
					else
					{
						// There is the beginning of a following message
						// but the header is incomplete. Compact the buffer
						// and stop here.
						assert( beg >= 4 );

						beg   = end;
						int p = 4 - ( (end+4) - limit );
						compactBuffer();
						pos = p;
						beg = 0;
						end = -1;
						break;
					}
				}
			
				if( end == limit )
				{
					// If the end of the message coincides with the limit of what
					// was read we have one last complete message. We decode it and
					// clear the buffer for the next call.

					decodeMessage( limit );
					buf.clear();
					pos =  0;
					beg =  0;
					end = -1;
				}
				else if( end > limit )
				{
					// If the end of the message if after what was read, prepare to
					// read more at next call when we will have buffered more
					// data. If we are at the end of the buffer compact it (else no
					// more space will be available for buffering).

					pos = limit;

					if( end > buf.capacity() )
						compactBuffer();
				}
			}
		}

		/**
		 * Read more data from the <code>socket</code> and put it in the buffer at
		 * <code>at</code>. If the read returns -1 bytes (meaning the
		 * connection ended), the socket is closed and this buffer will be made
		 * inactive (and therefore removed from the active connections by the
		 * Receiver that called it).
		 * @return the number of bytes read.
		 * @throws IOException if an I/O error occurs, in between the socket is
		 * closed and the connection is made inactive, then the exception is
		 * thrown.
		 */
		protected int
		bufferize( int at, SocketChannel socket )
			throws IOException
		{
			int nbytes = 0;
//			int limit  = 0;

			try
			{
				buf.position( at );
		
				nbytes = socket.read( buf );

				if( nbytes < 0 )
				{
					active = false;
					if( in != null )
						in.close();
					socket.close();
					if( debug )
						debug( "socket from %s:%d closed",
							socket.socket().getInetAddress(),
							socket.socket().getPort() );
					return nbytes;
				}
				else if( nbytes == 0 )
				{
					throw new RuntimeException( "should not happen: buffer to small, 0 bytes read: compact does not function? messages is larger than "+buf.capacity()+"?" );
					// This means that there are no bytes remaining in the buffer... it is full.	
					//compactBuffer();
					//return nbytes;
				}
				
				buf.position( at );

				return nbytes;
			}
			catch( IOException e )
			{
				if( debug )
				debug( "socket from %s:%d I/O error: %s",
						socket.socket().getInetAddress(),socket.socket().getPort(),
						e.getMessage() );
				active = false;
				if( in != null )
					in.close();
				socket.close();
				throw e;
			}
		}

		/**
		 * Decode one message.
		 */
		protected void
		decodeMessage( int limit )
			throws IOException
		{

		
			if( in == null )
			{
				in = new PositionableByteArrayInputStream( buf.array(), beg, end );	// Only a wrapping.
				//bin = new PositionableByteArrayInputStream( buf.array(), beg, end );	// Only a wrapping.
				//in  = new BufferedInputStream( bin );
			}
			else
			{
				in.setPos( beg, end );
				//in = new BufferedInputStream(bin);
			}

			
			
			
			
			
			
			
			
				int cmd = 0;

				// First read the name of the stream that will be addressed.
				String stream = readString(in);
				debug("Stream \"%s\" is addressed in this message.",stream);
				currentStream = streams.get(stream);
				if(stream == null){
					currentStream = new ThreadProxyPipe();
					streams.put(stream, currentStream);
				}
				
				
				cmd = in.read();

				if (cmd != -1) {

					if (cmd == NetStreamConstants.CMD_ADD_NODE) {
						serve_CMD_ADD_NODE(in);
					} else if ((cmd & 0xFF) == (NetStreamConstants.CMD_DEL_NODE & 0xFF)) {
						serve_DEL_NODE(in);
					} else if (cmd == NetStreamConstants.CMD_ADD_EDGE) {
						serve_CMD_ADD_EDGE(in);
					} else if (NetStreamConstants.CMD_DEL_EDGE == cmd) {
						serve_CMD_DEL_EDGE(in);
					} else if (cmd == NetStreamConstants.CMD_STEP) {
						serve_CMD_STEP(in);
					} else if (cmd == NetStreamConstants.CMD_CLEARED) {
						serve_CMD_CLEARED(in);
					} else if (cmd == NetStreamConstants.CMD_ADD_GRAPH_ATTR) {
						serve_CMD_ADD_GRAPH_ATTR(in);
					} else if (cmd == NetStreamConstants.CMD_CHG_GRAPH_ATTR) {
						serve_CMD_CHG_GRAPH_ATTR(in);
					} else if (cmd == NetStreamConstants.CMD_DEL_GRAPH_ATTR) {
						serve_CMD_DEL_GRAPH_ATTR(in);
					} else if (cmd == NetStreamConstants.CMD_ADD_NODE_ATTR) {
						serve_CMD_ADD_NODE_ATTR(in);
					} else if (cmd == NetStreamConstants.CMD_CHG_NODE_ATTR) {
						serve_CMD_CHG_NODE_ATTR(in);
					} else if (cmd == NetStreamConstants.CMD_DEL_NODE_ATTR) {
						serve_CMD_DEL_NODE_ATTR(in);
					} else if (cmd == NetStreamConstants.CMD_ADD_EDGE_ATTR) {
						serve_CMD_ADD_EDGE_ATTR(in);
					} else if (cmd == NetStreamConstants.CMD_CHG_EDGE_ATTR) {
						serve_CMD_CHG_EDGE_ATTR(in);
					} else if (cmd == NetStreamConstants.CMD_DEL_EDGE_ATTR) {
						serve_CMD_DEL_EDGE_ATTR(in);
					} else if (cmd == NetStreamConstants.CMD_END) {
						debug("NetStreamReceiver : Client properly ended the connection.");
						return;
					} else {
						debug("NetStreamReceiver: Don't know this command: "
										+ cmd);
						return;
					}

					cmd = in.read();

				}
			
			
			
		}

		/**
		 * Compact the buffer by removing all read data before <code>beg</code>.
		 * The <code>beg</code>, <code>end</code> and <code>pos</code> markers are
		 * updated accordingly. Compact works only if beg is larger than four (the
		 * size of a header).
		 * @return the offset.
		 */
		protected int
		compactBuffer()
		{
			if( beg > 4 )
			{
				int off = beg;

				buf.position( beg );
				buf.limit( buf.capacity() );
				buf.compact();
		
				pos -= beg;
				end -= beg;
				beg  = 0;

				return off;
			}

			return 0;
		}

		/**
		 * Not used in the current implementation, we assumes that no message will
		 * be larger than the size of the buffer.
		 */
		protected void
		enlargeBuffer()
		{
			ByteBuffer tmp = ByteBuffer.allocate( buf.capacity() * 2 );

			buf.position( 0 );
			buf.limit( buf.capacity() );
			tmp.put( buf );
			tmp.position( pos );

			buf = tmp;

			if( bin != null )
				bin.changeBuffer( buf.array() );
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @see NetStreamConstants.CMD_DEL_EDGE
	 */
	protected void serve_CMD_DEL_EDGE_ATTR(InputStream in) {
		debug("NetStreamServer: Received DEL_EDGE_ATTR command.");
		String edgeId = readString(in);
		String attrId = readString(in);
		currentStream.edgeAttributeRemoved(graphId, sourceTime.newEvent(),
				edgeId, attrId);
	}

	/**
	 * @see NetStreamConstants.CMD_CHG_EDGE_ATTR
	 */
	protected void serve_CMD_CHG_EDGE_ATTR(InputStream in) {
		debug("NetStreamServer: Received CHG_EDGE_ATTR command.");
		String edgeId = readString(in);
		String attrId = readString(in);
		int valueType = readType(in);
		Object oldValue = readValue(in, valueType);
		Object newValue = readValue(in, valueType);

		currentStream.edgeAttributeChanged(graphId, sourceTime.newEvent(),
				edgeId, attrId, oldValue, newValue);

	}

	/**
	 * @see NetStreamConstants.CMD_ADD_EDGE_ATTR
	 */
	protected void serve_CMD_ADD_EDGE_ATTR(InputStream in) {
		debug("NetStreamServer: Received ADD_EDGE_ATTR command.");
		String edgeId = readString(in);
		String attrId = readString(in);
		Object value = readValue(in, readType(in));

		currentStream.edgeAttributeAdded(graphId, sourceTime.newEvent(),
				edgeId, attrId, value);

	}

	/**
	 * @see NetStreamConstants.CMD_DEL_NODE_ATTR
	 */
	protected void serve_CMD_DEL_NODE_ATTR(InputStream in) {
		debug("NetStreamServer: Received DEL_NODE_ATTR command.");
		String nodeId = readString(in);
		String attrId = readString(in);
		
		currentStream.nodeAttributeRemoved(graphId, sourceTime.newEvent(),
				nodeId, attrId);

	}

	/**
	 * @see NetStreamConstants.CMD_CHG_NODE_ATTR
	 */
	protected void serve_CMD_CHG_NODE_ATTR(InputStream in) {
		debug("NetStreamServer: Received CMD_CHG_NODE_ATTR command.");
		String nodeId = readString(in);
		String attrId = readString(in);
		int valueType = readType(in);
		Object oldValue = readValue(in, valueType);
		Object newValue = readValue(in, valueType);

		currentStream.nodeAttributeChanged(graphId, sourceTime.newEvent(),
				nodeId, attrId, oldValue, newValue);
	}

	/**
	 * @see NetStreamConstants.CMD_ADD_NODE_ATTR
	 */
	protected void serve_CMD_ADD_NODE_ATTR(InputStream in) {
		debug("NetStreamServer: Received CMD_ADD_NODE_ATTR command.");
		String nodeId = readString(in);
		String attrId = readString(in);
		Object value = readValue(in, readType(in));

		currentStream.nodeAttributeAdded(graphId, sourceTime.newEvent(),
				nodeId, attrId, value);
	}

	/**
	 * @see NetStreamConstants.CMD_DEL_GRAPH_ATTR
	 */
	protected void serve_CMD_DEL_GRAPH_ATTR(InputStream in) {
		debug("NetStreamServer: Received CMD_DEL_GRAPH_ATTR command.");
		String attrId = readString(in);
		
		currentStream.graphAttributeRemoved(graphId, sourceTime.newEvent(),
				attrId);
	}

	/**
	 * @see NetStreamConstants.CMD_CHG_GRAPH_ATTR
	 */
	protected void serve_CMD_CHG_GRAPH_ATTR(InputStream in) {
		debug("NetStreamServer: Received CMD_CHG_GRAPH_ATTR command.");
		String attrId = readString(in);
		int valueType = readType(in);
		Object oldValue = readValue(in, valueType);
		Object newValue = readValue(in, valueType);

		currentStream.graphAttributeChanged(graphId, sourceTime.newEvent(),
				attrId, oldValue, newValue);

	}

	/**
	 * @see NetStreamConstants.CMD_ADD_GRAPH_ATTR
	 */
	protected void serve_CMD_ADD_GRAPH_ATTR(InputStream in) {
		debug("NetStreamServer: Received CMD_ADD_GRAPH_ATTR command.");
		String attrId = readString(in);
		Object value = readValue(in, readType(in));
		debug("NetStreamServer | CMD_ADD_GRAPH_ATTR | %s=%s",attrId,value.toString());
		currentStream.graphAttributeAdded(graphId, sourceTime.newEvent(),
			 attrId, value);

	}

	/**
	 * @see NetStreamConstants.CMD_CLEARED
	 */
	protected void serve_CMD_CLEARED(InputStream in) {
		debug("NetStreamServer: Received CMD_CLEARED command.");
		currentStream.graphCleared(graphId, sourceTime.newEvent());

	}

	/**
	 * @see NetStreamConstants.CMD_STEP
	 */
	protected void serve_CMD_STEP(InputStream in) {
		debug("NetStreamServer: Received CMD_STEP command.");
		double time = readDouble(in);
		currentStream.stepBegins(graphId, sourceTime.newEvent(), time);
	}

	/**
	 * @see NetStreamConstants.CMD_DEL_EDGE
	 */
	protected void serve_CMD_DEL_EDGE(InputStream in) {
		debug("NetStreamServer: Received CMD_DEL_EDGE command.");
		String edgeId = readString(in);
		currentStream.edgeRemoved(graphId, sourceTime.newEvent(), edgeId);
	}

	/**
	 * @see NetStreamConstants.CMD_ADD_EDGE
	 */
	protected void serve_CMD_ADD_EDGE(InputStream in) {
		debug("NetStreamServer: Received ADD_EDGE command.");
		String edgeId = readString(in);
		String from = readString(in);
		String to = readString(in);
		boolean directed = readBoolean(in);
		currentStream.edgeAdded(graphId, sourceTime.newEvent(), edgeId, from, to, directed);
	}

	/**
	 * @see NetStreamConstants.DEL_NODE
	 */
	protected void serve_DEL_NODE(InputStream in) {
		debug("NetStreamServer: Received DEL_NODE command.");
		String nodeId = readString(in);
		currentStream.nodeRemoved(graphId, sourceTime.newEvent(), nodeId);
	}

	/**
	 * @see NetStreamConstants.CMD_ADD_NODE
	 */
	protected void serve_CMD_ADD_NODE(InputStream in) {
		debug("NetStreamServer: Received CMD_ADD_NODE command.");
		String nodeId = readString(in);
		currentStream.nodeAdded(graphId, sourceTime.newEvent(), nodeId);

	}

	/**
	 * @param in
	 * @return
	 */
	protected int readType(InputStream in) {
		try {
			int data = 0;
			if ((data = in.read()) == -1) {
				System.out.println("readType : could not read type%n");
				return 0;
			}
			return data;
		} catch (IOException e) {
			debug("readType : could not read type%n");
			e.printStackTrace();
		}

		return 0;
	}

	protected Object readValue(InputStream in, int valueType) {
		if (NetStreamConstants.TYPE_BOOLEAN == valueType) {
			return readBoolean(in);
		}
		else if (NetStreamConstants.TYPE_BOOLEAN_ARRAY == valueType) {
			return readBooleanArray(in);
		}
		else if (NetStreamConstants.TYPE_BYTE == valueType) {
			return readByte(in);
		}
		else if (NetStreamConstants.TYPE_BYTE_ARRAY == valueType) {
			return readByteArray(in);
		}
		else if (NetStreamConstants.TYPE_SHORT == valueType) {
			return readShort(in);
		}
		else if (NetStreamConstants.TYPE_SHORT_ARRAY == valueType) {
			return readShortArray(in);
		}
		else if (NetStreamConstants.TYPE_INT == valueType) {
			return readInt(in);
		}
		else if (NetStreamConstants.TYPE_INT_ARRAY == valueType) {
			return readIntArray(in);
		}
		else if (NetStreamConstants.TYPE_LONG == valueType) {
			return readLong(in);
		}
		else if (NetStreamConstants.TYPE_LONG_ARRAY == valueType) {
			return readLongArray(in);
		}
		else if (NetStreamConstants.TYPE_FLOAT == valueType) {
			return readFloat(in);
		}
		else if (NetStreamConstants.TYPE_FLOAT_ARRAY == valueType) {
			return readFloatArray(in);
		}
		else if (NetStreamConstants.TYPE_DOUBLE == valueType) {
			return readDouble(in);
		}
		else if (NetStreamConstants.TYPE_DOUBLE_ARRAY == valueType) {
			return readDoubleArray(in);
		}
		else if (NetStreamConstants.TYPE_STRING == valueType) {
			return readString(in);
		}
		else if (NetStreamConstants.TYPE_ARRAY == valueType) {
			return readArray(in);
		}
		return null;
	}

	/**
	 * @param in
	 * @return
	 */
	protected Object readArray(InputStream in) {
		byte[] data = new byte[2];

		try {
			if (in.read(data, 0, 2) != 2) {
				debug("readByteArray : could not read length of array (short)%n");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.put(data);
			bb.flip();
			short len = bb.getShort();
			
			Object[] array = new Object[len];
			for(int i =0;i<len;i++){
				array[i]=readValue(in,readType(in));
			}
			return array;
	
		}  catch (IOException e) {
			debug("readArray : could not read\n");
			e.printStackTrace();
		}
		return null;
	}

	protected String readString(InputStream in) {

		try {
			byte[] data = new byte[2];
			if (in.read(data, 0, 2) != 2) {
				System.out
						.println("readString : could not read length of array (short)%n");
				return null;
			}

			int len = 0;
			len |= data[0] & 0xFF;
			len <<= 8;
			len |= data[1] & 0xFF;

			data = new byte[len];
			if (in.read(data, 0, len) != len) {
				return null;
			}
			return new String(data, Charset.forName("UTF-8"));
		} catch (IOException e) {
			debug("readString : could not read string%n");
			e.printStackTrace();
		}
		return null;
	}

	protected boolean readBoolean(InputStream in) {
		int data = 0;
		try {
			data = in.read();
		} catch (IOException e) {
			debug("readByte : could not read\n");
			e.printStackTrace();
		}
		return data == 0 ? false : true;
	}

	protected int readByte(InputStream in) {
		int data = 0;
		try {
			data = in.read();
		} catch (IOException e) {
			debug("readByte : could not read\n");
			e.printStackTrace();
		}
		return data;
	}

	protected short readShort(InputStream in) {
		byte[] data = new byte[2];
		try {
			if (in.read(data, 0, 2) != 2) {
				debug("readShort : could not read%n");
				return 0;
			}
		} catch (IOException e) {
			debug("readShort : could not read\n");
			e.printStackTrace();
		}
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.put(data);
		bb.flip();
		return bb.getShort();
	}

	protected int readInt(InputStream in) {
		byte[] data = new byte[4];
		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readInt : could not read%n");
				return 0;
			}
		} catch (IOException e) {
			debug("readInt : could not read\n");
			e.printStackTrace();
		}
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(data);
		bb.flip();
		return bb.getInt();
	}

	protected long readLong(InputStream in) {
		byte[] data = new byte[8];
		try {
			if (in.read(data, 0, 8) != 8) {
				debug("readLong : could not read%n");
				return 0;
			}
		} catch (IOException e) {
			debug("readLong : could not read\n");
			e.printStackTrace();
		}
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.put(data);
		bb.flip();
		return bb.getLong();
	}

	protected float readFloat(InputStream in) {
		byte[] data = new byte[4];
		try {
			if (in.read(data, 0, 4) != 4) {
				debug("readFloat : could not read%n");
				return 0;
			}
		} catch (IOException e) {
			debug("readFloat : could not read\n");
			e.printStackTrace();
		}
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(data);
		bb.flip();
		return bb.getFloat();
	}

	protected double readDouble(InputStream in) {
		byte[] data = new byte[8];
		try {
			if (in.read(data, 0, 8) != 8) {
				debug("readDouble : could not read%n");
				return 0;
			}
		} catch (IOException e) {
			debug("readDouble : could not read\n");
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
	protected int[] readIntArray(InputStream in) {
		byte[] data = new byte[2];

		try {
			if (in.read(data, 0, 2) != 2) {
				System.out
						.println("readIntArray : could not read length of array (short)%n");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.put(data);
			bb.flip();
			short len = bb.getShort();

			data = new byte[len * 4];
			if (in.read(data, 0, len * 4) != len * 4) {
				debug("readIntArray : could not read array\n");
				return null;
			}

			bb = ByteBuffer.allocate(4 * len);
			bb.put(data);
			bb.flip();
			int[] res = new int[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.getInt();
				debug(res[i] + ",");
			}
			debug("%n");
			return res;
		} catch (IOException e) {
			debug("readIntArray : could not read array\n");
			e.printStackTrace();
		}
		return null;
	}

	protected boolean[] readBooleanArray(InputStream in) {
		byte[] data = new byte[2];

		try {
			if (in.read(data, 0, 2) != 2) {
				debug("readBooleanArray : could not read length of array (short)%n");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.put(data);
			bb.flip();
			short len = bb.getShort();

			data = new byte[len];
			if (in.read(data, 0, len) != len) {
				debug("readBooleanArray : could not read array\n");
				return null;
			}

			bb = ByteBuffer.allocate(len);
			bb.put(data);
			bb.flip();
			boolean[] res = new boolean[len];
			for (int i = 0; i < len; i++) {

				byte b = bb.get();

				res[i] = b == 0 ? false : true;
				debug(res[i] + ",");
			}
			debug("%n");
			return res;
		} catch (IOException e) {
			debug("readBooleanArray : could not read array\n");
			e.printStackTrace();
		}
		return null;
	}

	
	protected byte[] readByteArray(InputStream in) {
		byte[] data = new byte[2];

		try {
			if (in.read(data, 0, 2) != 2) {
				debug("readByteArray : could not read length of array (short)%n");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.put(data);
			bb.flip();
			short len = bb.getShort();

			data = new byte[len];
			if (in.read(data, 0, len) != len) {
				debug("readByteArray : could not read array\n");
				return null;
			}

			bb = ByteBuffer.allocate(len);
			bb.put(data);
			bb.flip();
			byte[] res = new byte[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.get();

				debug(res[i] + ",");
			}
			debug("%n");
			return res;
		} catch (IOException e) {
			debug("readBooleanArray : could not read array\n");
			e.printStackTrace();
		}
		return null;
	}


	
	
	
	
	protected double[] readDoubleArray(InputStream in) {
		byte[] data = new byte[2];

		try {
			if (in.read(data, 0, 2) != 2) {
				System.out
						.println("readDoubleArray : could not read length of array (short)%n");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.put(data);
			bb.flip();
			short len = bb.getShort();

			data = new byte[len * 8];
			if (in.read(data, 0, len * 8) != len * 8) {
				debug("readDoubleArray : could not read array%n");
				return null;
			}

			bb = ByteBuffer.allocate(8 * len);
			bb.put(data);
			bb.flip();
			double[] res = new double[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.getDouble();
				debug(res[i] + ",");
			}
			debug("%n");
			return res;
		} catch (IOException e) {
			debug("readDoubleArray : could not read array%n");
			e.printStackTrace();
		}
		return null;
	}

	protected float[] readFloatArray(InputStream in) {
		byte[] data = new byte[2];

		try {
			if (in.read(data, 0, 2) != 2) {
				System.out
						.println("readFloatArray : could not read length of array (short)%n");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.put(data);
			bb.flip();
			short len = bb.getShort();

			data = new byte[len * 4];
			if (in.read(data, 0, len * 4) != len * 4) {
				debug("readFloatArray : could not read array%n");
				return null;
			}

			bb = ByteBuffer.allocate(4 * len);
			bb.put(data);
			bb.flip();
			float[] res = new float[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.getFloat();
				debug(res[i] + ",");
			}
			debug("%n");
			return res;
		} catch (IOException e) {
			debug("readFloatArray : could not read array%n");
			e.printStackTrace();
		}
		return null;
	}

	protected long[] readLongArray(InputStream in) {
		byte[] data = new byte[2];

		try {
			if (in.read(data, 0, 2) != 2) {
				System.out
						.println("readLongArray : could not read length of array (short)%n");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.put(data);
			bb.flip();
			short len = bb.getShort();

			data = new byte[len * 8];
			if (in.read(data, 0, len * 8) != len * 8) {
				debug("readLongArray : could not read array%n");
				return null;
			}

			bb = ByteBuffer.allocate(8 * len);
			bb.put(data);
			bb.flip();
			long[] res = new long[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.getLong();
				debug(res[i] + ",");
			}
			debug("%n");
			return res;
		} catch (IOException e) {
			debug("readLongArray : could not read array%n");
			e.printStackTrace();
		}
		return null;
	}

	protected short[] readShortArray(InputStream in) {
		byte[] data = new byte[2];

		try {
			if (in.read(data, 0, 2) != 2) {
				System.out
						.println("readShortArray : could not read length of array (short)%n");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.put(data);
			bb.flip();
			short len = bb.getShort();

			data = new byte[len * 2];
			if (in.read(data, 0, len * 2) != len * 2) {
				debug("readShortArray : could not read array%n");
				return null;
			}

			bb = ByteBuffer.allocate(2 * len);
			bb.put(data);
			bb.flip();
			short[] res = new short[len];
			for (int i = 0; i < len; i++) {

				res[i] = bb.getShort();
				debug(res[i] + ",");
			}
			debug("%n");
			return res;
		} catch (IOException e) {
			debug("readShortArray : could not read array%n");
			e.printStackTrace();
		}
		return null;
	}

	
	
	
	
	
	public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {

		Graph g1 = new MultiGraph("G1");
		g1.display();

		Graph g2 = new MultiGraph("G2");
		g2.display();

		NetStreamReceiver net = new NetStreamReceiver("localhost", 2001,false);
		
		ThreadProxyPipe pipe1 = net.getStream("G1");
		ThreadProxyPipe pipe2 = net.getStream("G2");
		
		pipe1.addSink(g1);
		pipe2.addSink(g2);

		//net.start();

		while (true) {
			pipe1.pump();
			pipe2.pump();
			Thread.sleep(100);
		}

	}
	
	
}
