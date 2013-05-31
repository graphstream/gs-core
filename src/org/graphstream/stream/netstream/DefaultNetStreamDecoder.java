/**
 *
 * Copyright (c) 2013 University of Le Havre
 *
 * @file NetStreamDecoder.java
 * @date May 31, 2013
 *
 * @author Yoann Pigné
 *
 */
package org.graphstream.stream.netstream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 * 
 */
public class DefaultNetStreamDecoder implements NetStreamDecoder {
	
	/**
	 * Show debugging messages.
	 */
	protected boolean debug = true;

	/**
	 * The current pipe commands are being written to.
	 */
	protected ThreadProxyPipe currentStream;

	/**
	 * Pairs (key,value) where the key is the listener ID and the value the MBox
	 * of the listener. This can be modified by other threads and must be
	 * properly locked.
	 * 
	 * @see #register(String,ThreadProxyPipe)
	 */
	// protected HashMap<String,MBox> boxes = new HashMap<String,MBox>();
	protected HashMap<String, ThreadProxyPipe> streams = new HashMap<String, ThreadProxyPipe>();

	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.NetStreamDecoder#getStream(java.lang.String)
	 */
	public synchronized ThreadProxyPipe getStream(String name) {
		ThreadProxyPipe s = streams.get(name);
		if (s == null) {
			s = new ThreadProxyPipe();
			streams.put(name, s);
		}
		return s;
	}
	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.NetStreamDecoder#getDefaultStream()
	 */

	public synchronized ThreadProxyPipe getDefaultStream() {
		ThreadProxyPipe s = streams.get("default");
		if (s == null) {
			s = new ThreadProxyPipe();
			streams.put("default", s);
		}
		return s;

	}
	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.NetStreamDecoder#register(java.lang.String, org.graphstream.stream.thread.ThreadProxyPipe)
	 */
	public synchronized void register(String name, ThreadProxyPipe stream)
			throws Exception {
		if (streams.containsKey(name))
			throw new Exception("name " + name + " already registered");

		streams.put(name, stream);

		if (debug)
			debug("registered pipe %s", name);
	}

	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.NetStreamDecoder#decodeMessage(java.io.InputStream)
	 */
	public void decodeMessage(InputStream in) throws IOException {	

		int cmd = 0;

		// First read the name of the stream that will be addressed.
		String stream = readString(in);
		if (debug) {
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
	 * @param in
	 * @see NetStreamConstants.EVENT_DEL_EDGE
	 */
	protected void serve_EVENT_DEL_EDGE_ATTR(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received DEL_EDGE_ATTR command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String edgeId = readString(in);
		String attrId = readString(in);
		currentStream.edgeAttributeRemoved(sourceId, timeId, edgeId, attrId);
	}

	/**
	 * @see NetStreamConstants.EVENT_CHG_EDGE_ATTR
	 */
	protected void serve_EVENT_CHG_EDGE_ATTR(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received CHG_EDGE_ATTR command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String edgeId = readString(in);
		String attrId = readString(in);
		int oldValueType = readType(in);
		Object oldValue = readValue(in, oldValueType);
		int newValueType = readType(in);
		Object newValue = readValue(in, newValueType);

		currentStream.edgeAttributeChanged(sourceId, timeId, edgeId, attrId,
				oldValue, newValue);

	}

	/**
	 * @see NetStreamConstants.EVENT_ADD_EDGE_ATTR
	 */
	protected void serve_EVENT_ADD_EDGE_ATTR(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received ADD_EDGE_ATTR command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String edgeId = readString(in);
		String attrId = readString(in);
		Object value = readValue(in, readType(in));

		currentStream.edgeAttributeAdded(sourceId, timeId, edgeId, attrId,
				value);

	}

	/**
	 * @see NetStreamConstants.EVENT_DEL_NODE_ATTR
	 */
	protected void serve_EVENT_DEL_NODE_ATTR(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received DEL_NODE_ATTR command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String nodeId = readString(in);
		String attrId = readString(in);

		currentStream.nodeAttributeRemoved(sourceId, timeId, nodeId, attrId);

	}

	/**
	 * @see NetStreamConstants.EVENT_CHG_NODE_ATTR
	 */
	protected void serve_EVENT_CHG_NODE_ATTR(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received EVENT_CHG_NODE_ATTR command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String nodeId = readString(in);
		String attrId = readString(in);
		int oldValueType = readType(in);
		Object oldValue = readValue(in, oldValueType);
		int newValueType = readType(in);
		Object newValue = readValue(in, newValueType);

		currentStream.nodeAttributeChanged(sourceId, timeId, nodeId, attrId,
				oldValue, newValue);
	}

	/**
	 * @see NetStreamConstants.EVENT_ADD_NODE_ATTR
	 */
	protected void serve_EVENT_ADD_NODE_ATTR(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received EVENT_ADD_NODE_ATTR command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String nodeId = readString(in);
		String attrId = readString(in);
		Object value = readValue(in, readType(in));

		currentStream.nodeAttributeAdded(sourceId, timeId, nodeId, attrId,
				value);
	}

	/**
	 * @see NetStreamConstants.EVENT_DEL_GRAPH_ATTR
	 */
	protected void serve_EVENT_DEL_GRAPH_ATTR(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received EVENT_DEL_GRAPH_ATTR command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String attrId = readString(in);

		currentStream.graphAttributeRemoved(sourceId, timeId, attrId);
	}

	/**
	 * @see NetStreamConstants.EVENT_CHG_GRAPH_ATTR
	 */
	protected void serve_EVENT_CHG_GRAPH_ATTR(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received EVENT_CHG_GRAPH_ATTR command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String attrId = readString(in);
		int oldValueType = readType(in);
		Object oldValue = readValue(in, oldValueType);
		int newValueType = readType(in);
		Object newValue = readValue(in, newValueType);

		currentStream.graphAttributeChanged(sourceId, timeId, attrId, oldValue,
				newValue);

	}

	/**
	 * @see NetStreamConstants.EVENT_ADD_GRAPH_ATTR
	 */
	protected void serve_EVENT_ADD_GRAPH_ATTR(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received EVENT_ADD_GRAPH_ATTR command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String attrId = readString(in);
		Object value = readValue(in, readType(in));
		if (debug) {
			debug("NetStreamServer | EVENT_ADD_GRAPH_ATTR | %s=%s", attrId,
					value.toString());
		}
		currentStream.graphAttributeAdded(sourceId, timeId, attrId, value);

	}

	/**
	 * @see NetStreamConstants.EVENT_CLEARED
	 */
	protected void serve_EVENT_CLEARED(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received EVENT_CLEARED command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		currentStream.graphCleared(sourceId, timeId);

	}

	/**
	 * @see NetStreamConstants.EVENT_STEP
	 */
	protected void serve_EVENT_STEP(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received EVENT_STEP command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		double time = readDouble(in);
		currentStream.stepBegins(sourceId, timeId, time);
	}

	/**
	 * @see NetStreamConstants.EVENT_DEL_EDGE
	 */
	protected void serve_EVENT_DEL_EDGE(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received EVENT_DEL_EDGE command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String edgeId = readString(in);
		currentStream.edgeRemoved(sourceId, timeId, edgeId);
	}

	/**
	 * @see NetStreamConstants.EVENT_ADD_EDGE
	 */
	protected void serve_EVENT_ADD_EDGE(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received ADD_EDGE command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String edgeId = readString(in);
		String from = readString(in);
		String to = readString(in);
		boolean directed = readBoolean(in);
		currentStream.edgeAdded(sourceId, timeId, edgeId, from, to, directed);
	}

	/**
	 * @see NetStreamConstants.DEL_NODE
	 */
	protected void serve_DEL_NODE(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received DEL_NODE command.");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String nodeId = readString(in);
		currentStream.nodeRemoved(sourceId, timeId, nodeId);
	}

	/**
	 * @see NetStreamConstants.EVENT_ADD_NODE
	 */
	protected void serve_EVENT_ADD_NODE(InputStream in) {
		if (debug) {
			debug("NetStreamServer: Received EVENT_ADD_NODE command");
		}
		String sourceId = readString(in);
		long timeId = readUnsignedVarint(in);
		String nodeId = readString(in);
		currentStream.nodeAdded(sourceId, timeId, nodeId);

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
			if (debug) {
				debug("NetStreamServer: type "+data);
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

		int len = (int) readUnsignedVarint(in);

		Object[] array = new Object[len];
		for (int i = 0; i < len; i++) {
			array[i] = readValue(in, readType(in));
		}
		return array;

	}

	protected String readString(InputStream in) {
		try {

			int len = (int) readUnsignedVarint(in);
			byte[] data = new byte[len];
			if (in.read(data, 0, len) != len) {
				return null;
			}
			String s = new String(data, Charset.forName("UTF-8"));
			return s;
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

	protected long readUnsignedVarint(InputStream in) {
		try {

			int size = 0;
			long[] data = new long[9];
			do {
				data[size] = in.read();
				
				size++;
				
				//int bt =data[size-1]; 
				//if (bt < 0) bt = (bt & 127) + (bt & 128);
				//System.out.println("test "+bt+"  -> "+(data[size - 1]& 128) );
			} while ((data[size - 1] & 128) == 128);
			long number = 0;
			for (int i = 0; i < size; i++) {

				number ^= (data[i] & 127L) << (i * 7L);
				
			}
			
			return number;

		} catch (IOException e) {
			debug("readUnsignedVarintFromInteger: could not read");
			e.printStackTrace();
		}
		return 0L;
	}
	
	

	protected long readVarint(InputStream in) {
		long number = readUnsignedVarint(in);
		return ((number & 1) == 0) ? number >> 1 : -(number >> 1);
	}

	protected Short readShort(InputStream in) {
		return (short) readVarint(in);
	}

	protected Integer readInt(InputStream in) {
		return (int) readVarint(in);
	}

	protected Long readLong(InputStream in) {
		return readVarint(in);
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
		
		int len = (int) readUnsignedVarint(in);

		Integer[] res = new Integer[len];
		for (int i = 0; i < len; i++) {
			res[i] = (int) readVarint(in);
			//System.out.printf("array[%d]=%d%n",i,res[i]);
		}
		return res;
	}

	protected Boolean[] readBooleanArray(InputStream in) {
		byte[] data = null;

		try {
			int len = (int) readUnsignedVarint(in);

			data = new byte[len];
			if (in.read(data, 0, len) != len) {
				debug("readBooleanArray: could not read array");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(len);
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
		byte[] data = null;

		try {
			int len = (int) readUnsignedVarint(in);

			data = new byte[len];
			if (in.read(data, 0, len) != len) {
				debug("readByteArray: could not read array");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(len);
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
		byte[] data = null;

		try {
			int len = (int) readUnsignedVarint(in);

			data = new byte[len * 8];
			if (in.read(data, 0, len * 8) != len * 8) {
				debug("readDoubleArray: could not read array");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(8 * len);
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
		byte[] data = null;

		try {
			int len = (int) readUnsignedVarint(in);

			data = new byte[len * 4];
			if (in.read(data, 0, len * 4) != len * 4) {
				debug("readFloatArray: could not read array");
				return null;
			}

			ByteBuffer bb = ByteBuffer.allocate(4 * len);
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
		int len = (int) readUnsignedVarint(in);

		Long[] res = new Long[len];
		for (int i = 0; i < len; i++) {
			res[i] = readVarint(in);
		}
		return res;
	}

	protected Short[] readShortArray(InputStream in) {
		int len = (int) readUnsignedVarint(in);

		Short[] res = new Short[len];
		for (int i = 0; i < len; i++) {
			res[i] = (short) readVarint(in);
			//System.out.printf("array[%d]=%d%n",i,res[i]);
		}
		return res;
	}
	
	
	
	
	protected void debug(String message, Object... data) {
		// System.err.print( LIGHT_YELLOW );
		System.err.printf("[//NetStreamDecoder | ");
		// System.err.print( RESET );
		System.err.printf(message, data);
		// System.err.print( LIGHT_YELLOW );
		System.err.printf("]%n");
		// System.err.println( RESET );
	}

	/* (non-Javadoc)
	 * @see org.graphstream.stream.netstream.NetStreamDecoder#setDebugOn(boolean)
	 */
	public void setDebugOn(boolean on) {
		debug = on;
	}

}
