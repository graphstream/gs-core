package org.graphstream.stream.netstream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.sync.SourceTime;
import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 *
 * Copyright (c) 2010 University of Luxembourg
 *
 * @file NetStreamServer.java
 * @date Jul 9, 2011
 *
 * @author Yoann Pigné
 *
 */

/**
 * 
 * 
 * <h2>Protocol</h2>
 * 
 * <h3>global behavior</h3>
 * <ul>
 * <li>Once launched, the server waits for incoming connections through port
 * number <code>port</code>.</li>
 * 
 * <li>Once connected to a client the server waits for commands. If an unknown
 * command is received, the connection with the client is dropped and the
 * servers starts waiting for a new client to connect.</li>
 * <li>Once a valid command is received the server waits the parameters
 * specifics to this command before responding. The response type depends on the
 * command.</li>
 * 
 * <li>Once the server is done answering a received command with its parameters,
 * it waits for a new command from the client. The server will answer the
 * clients command until a <code>CMD_END</code> is received or until an
 * erroneous message is received.</li>
 * </ul>
 * 
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
public class NetStreamServer extends Thread {

	protected ThreadProxyPipe netStreamProxyPipe;
	protected String graphId = "NetStream";
	protected SourceTime sourceTime;
	public int port = 4444;

	/**
	 * 
	 */
	public NetStreamServer(int port) {
		this.port = port;
		sourceTime = new SourceTime(0);
		// initialize the pipe with no input since we are the input.
		setNetStreamProxyPipe(new ThreadProxyPipe());

	}

	/**
	 * @param netStreamProxyPipe
	 *            the netStreamProxyPipe to set
	 */
	public void setNetStreamProxyPipe(ThreadProxyPipe netStreamProxyPipe) {
		this.netStreamProxyPipe = netStreamProxyPipe;
	}

	/**
	 * @return the netStreamProxyPipe
	 */
	public ThreadProxyPipe getNetStreamProxyPipe() {
		return netStreamProxyPipe;
	}

	/**
	 * 
	 */
	public static int VERSION = 1;

	public static boolean debug = false;

	public void run() {
		debug("NetStreamServer: started.%n");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			debug("NetStreamServer: Could not listen on port: " + port);
			System.exit(-1);
		}

		Socket clientSocket = null;
		while (true) {
			try {
				debug("NetStreamServer: Listenning %d %n", port);
				clientSocket = serverSocket.accept();

			} catch (IOException e) {
				debug("NetStreamServer: Accept failed: %d%n", port);
				System.exit(-1);
			}

			debug("NetStreamServer: Connected%n");

			serveClient(clientSocket);

		}

	}

	/**
	 * @param clientSocket
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected void serveClient(Socket clientSocket) {
		try {
			OutputStream out = clientSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			int cmd = 0;
			cmd = in.read();

			while (cmd != -1) {

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
					System.out
							.println("NetStreamServer: Client properly ended the connection%n");
					return;
				} else {
					System.out
							.println("NetStreamServer: Don't know this command: "
									+ cmd);
					return;
				}

				cmd = in.read();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see NetStreamConstants.CMD_DEL_EDGE
	 */
	protected void serve_CMD_DEL_EDGE_ATTR(BufferedInputStream in) {
		debug("NetStreamServer: Received DEL_EDGE_ATTR comamd.");
		String edgeId = readString(in);
		String attrId = readString(in);
		netStreamProxyPipe.edgeAttributeRemoved(graphId, sourceTime.newEvent(),
				edgeId, attrId);
	}

	/**
	 * @see NetStreamConstants.CMD_CHG_EDGE_ATTR
	 */
	protected void serve_CMD_CHG_EDGE_ATTR(InputStream in) {
		debug("NetStreamServer: Received CHG_EDGE_ATTR comamd.");
		String edgeId = readString(in);
		String attrId = readString(in);
		int valueType = readType(in);
		Object oldValue = readValue(in, valueType);
		Object newValue = readValue(in, valueType);

		netStreamProxyPipe.edgeAttributeChanged(graphId, sourceTime.newEvent(),
				edgeId, attrId, oldValue, newValue);

	}

	/**
	 * @see NetStreamConstants.CMD_ADD_EDGE_ATTR
	 */
	protected void serve_CMD_ADD_EDGE_ATTR(InputStream in) {
		debug("NetStreamServer: Received ADD_EDGE_ATTR comamd.");
		String edgeId = readString(in);
		String attrId = readString(in);
		Object value = readValue(in, readType(in));

		netStreamProxyPipe.edgeAttributeAdded(graphId, sourceTime.newEvent(),
				edgeId, attrId, value);

	}

	/**
	 * @see NetStreamConstants.CMD_DEL_NODE_ATTR
	 */
	protected void serve_CMD_DEL_NODE_ATTR(InputStream in) {
		debug("NetStreamServer: Received DEL_NODE_ATTR comamd.");
		String nodeId = readString(in);
		String attrId = readString(in);
		
		netStreamProxyPipe.nodeAttributeRemoved(graphId, sourceTime.newEvent(),
				nodeId, attrId);

	}

	/**
	 * @see NetStreamConstants.CMD_CHG_NODE_ATTR
	 */
	protected void serve_CMD_CHG_NODE_ATTR(InputStream in) {
		debug("NetStreamServer: Received CMD_CHG_NODE_ATTR comamd.");
		String nodeId = readString(in);
		String attrId = readString(in);
		int valueType = readType(in);
		Object oldValue = readValue(in, valueType);
		Object newValue = readValue(in, valueType);

		netStreamProxyPipe.nodeAttributeChanged(graphId, sourceTime.newEvent(),
				nodeId, attrId, oldValue, newValue);
	}

	/**
	 * @see NetStreamConstants.CMD_ADD_NODE_ATTR
	 */
	protected void serve_CMD_ADD_NODE_ATTR(InputStream in) {
		debug("NetStreamServer: Received CMD_ADD_NODE_ATTR comamd.");
		String nodeId = readString(in);
		String attrId = readString(in);
		Object value = readValue(in, readType(in));

		netStreamProxyPipe.nodeAttributeAdded(graphId, sourceTime.newEvent(),
				nodeId, attrId, value);
	}

	/**
	 * @see NetStreamConstants.CMD_DEL_GRAPH_ATTR
	 */
	protected void serve_CMD_DEL_GRAPH_ATTR(InputStream in) {
		debug("NetStreamServer: Received CMD_DEL_GRAPH_ATTR comamd.");
		String attrId = readString(in);
		
		netStreamProxyPipe.graphAttributeRemoved(graphId, sourceTime.newEvent(),
				attrId);
	}

	/**
	 * @see NetStreamConstants.CMD_CHG_GRAPH_ATTR
	 */
	protected void serve_CMD_CHG_GRAPH_ATTR(InputStream in) {
		debug("NetStreamServer: Received CMD_CHG_GRAPH_ATTR comamd.");
		String attrId = readString(in);
		int valueType = readType(in);
		Object oldValue = readValue(in, valueType);
		Object newValue = readValue(in, valueType);

		netStreamProxyPipe.graphAttributeChanged(graphId, sourceTime.newEvent(),
				attrId, oldValue, newValue);

	}

	/**
	 * @see NetStreamConstants.CMD_ADD_GRAPH_ATTR
	 */
	protected void serve_CMD_ADD_GRAPH_ATTR(InputStream in) {
		debug("NetStreamServer: Received CMD_ADD_GRAPH_ATTR comamd.");
		String attrId = readString(in);
		Object value = readValue(in, readType(in));

		netStreamProxyPipe.graphAttributeAdded(graphId, sourceTime.newEvent(),
			 attrId, value);

	}

	/**
	 * @see NetStreamConstants.CMD_CLEARED
	 */
	protected void serve_CMD_CLEARED(InputStream in) {
		debug("NetStreamServer: Received CMD_CLEARED comamd.");
		netStreamProxyPipe.graphCleared(graphId, sourceTime.newEvent());

	}

	/**
	 * @see NetStreamConstants.CMD_STEP
	 */
	protected void serve_CMD_STEP(InputStream in) {
		debug("NetStreamServer: Received CMD_STEP comamd.");
		double time = readDouble(in);
		netStreamProxyPipe.stepBegins(graphId, sourceTime.newEvent(), time);
	}

	/**
	 * @see NetStreamConstants.CMD_DEL_EDGE
	 */
	protected void serve_CMD_DEL_EDGE(InputStream in) {
		debug("NetStreamServer: Received CMD_DEL_EDGE comamd.");
		String edgeId = readString(in);
		netStreamProxyPipe.edgeRemoved(graphId, sourceTime.newEvent(), edgeId);
	}

	/**
	 * @see NetStreamConstants.CMD_ADD_EDGE
	 */
	protected void serve_CMD_ADD_EDGE(InputStream in) {
		debug("NetStreamServer: Received ADD_EDGE comamd.");
		String edgeId = readString(in);
		String from = readString(in);
		String to = readString(in);
		boolean directed = readBoolean(in);
		netStreamProxyPipe.edgeAdded(graphId, sourceTime.newEvent(), edgeId, from, to, directed);
	}

	/**
	 * @see NetStreamConstants.DEL_NODE
	 */
	protected void serve_DEL_NODE(InputStream in) {
		debug("NetStreamServer: Received DEL_NODE comamd.");
		String nodeId = readString(in);
		netStreamProxyPipe.nodeRemoved(graphId, sourceTime.newEvent(), nodeId);
	}

	/**
	 * @see NetStreamConstants.CMD_ADD_NODE
	 */
	protected void serve_CMD_ADD_NODE(InputStream in) {
		debug("NetStreamServer: Received CMD_ADD_NODE comamd.");
		String nodeId = readString(in);
		netStreamProxyPipe.nodeAdded(graphId, sourceTime.newEvent(), nodeId);

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

	/**
	 * 
	 */
	protected void debug(String format, Object... args) {
		if (debug)
			System.err.printf(format, args);
	}

	public static void main(String[] args) throws InterruptedException {

		Graph g = new MultiGraph("ok");

		g.display();

		NetStreamServer net = new NetStreamServer(2001);
		ThreadProxyPipe pipe = net.getNetStreamProxyPipe();
		pipe.addSink(g);

		net.start();

		while (true) {
			pipe.pump();
			Thread.sleep(100);
		}

	}

}
