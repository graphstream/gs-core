/*
 * Copyright 2006 - 2011 
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
package org.graphstream.stream.netstream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.Sink;

/**
 * 
 * 
 * One client must send to only one identified stream (streamID, host, port)
 * 
 * 
 * @date Jul 10, 2011
 * 
 * @author Yoann Pigné
 * 
 */
public class NetStreamClient implements Sink {
	protected String stream;
	byte[] streamIdArray;
	protected String host;
	protected int port;
	protected Socket socket;
	protected BufferedOutputStream out;

	ByteBuffer buffSizeBuff;

	public NetStreamClient(String stream, String host, int port) {
		this.stream = stream;
		this.host = host;
		this.port = port;
		buffSizeBuff = ByteBuffer.allocate(4);
		streamIdArray = stream.getBytes(Charset.forName("UTF-8"));

		connect();
	}

	protected void connect() {

		try {
			socket = new Socket(host, port);
			out = new BufferedOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected int getType(Object value) {
		int valueType = 0;
		@SuppressWarnings("rawtypes")
		Class valueClass = value.getClass();
		boolean isArray = valueClass.isArray();
		if (isArray) {
			valueClass = ((Object[]) value)[0].getClass();
		}
		if (valueClass.equals(Boolean.class)) {
			if (isArray) {
				valueType = NetStreamConstants.TYPE_BOOLEAN_ARRAY;
			} else {
				valueType = NetStreamConstants.TYPE_BOOLEAN;
			}
		} else if (valueClass.equals(Byte.class)) {
			if (isArray) {
				valueType = NetStreamConstants.TYPE_BYTE_ARRAY;
			} else {
				valueType = NetStreamConstants.TYPE_BYTE;
			}
		} else if (valueClass.equals(Short.class)) {
			if (isArray) {
				valueType = NetStreamConstants.TYPE_SHORT_ARRAY;
			} else {
				valueType = NetStreamConstants.TYPE_SHORT;
			}
		} else if (valueClass.equals(Integer.class)) {
			if (isArray) {
				valueType = NetStreamConstants.TYPE_INT_ARRAY;
			} else {
				valueType = NetStreamConstants.TYPE_INT;
			}
		} else if (valueClass.equals(Long.class)) {
			if (isArray) {
				valueType = NetStreamConstants.TYPE_LONG_ARRAY;
			} else {
				valueType = NetStreamConstants.TYPE_LONG;
			}
		} else if (valueClass.equals(Float.class)) {
			if (isArray) {
				valueType = NetStreamConstants.TYPE_FLOAT_ARRAY;
			} else {
				valueType = NetStreamConstants.TYPE_FLOAT;
			}
		} else if (valueClass.equals(Double.class)) {
			if (isArray) {
				valueType = NetStreamConstants.TYPE_DOUBLE_ARRAY;
			} else {
				valueType = NetStreamConstants.TYPE_DOUBLE;
			}
		} else if (valueClass.equals(String.class)) {
			if (isArray) {
				valueType = NetStreamConstants.TYPE_ARRAY;
			} else {
				valueType = NetStreamConstants.TYPE_STRING;
			}
		}
		// System.out.println("ValueType="+valueType+" "+value.getClass());
		return valueType;
	}

	protected ByteBuffer encodeValue(Object in, int valueType) {

		if (NetStreamConstants.TYPE_BOOLEAN == valueType) {
			return encodeBoolean(in);
		} else if (NetStreamConstants.TYPE_BOOLEAN_ARRAY == valueType) {
			return encodeBooleanArray(in);
		} else if (NetStreamConstants.TYPE_BYTE == valueType) {
			return encodeByte(in);
		} else if (NetStreamConstants.TYPE_BYTE_ARRAY == valueType) {
			return encodeByteArray(in);
		} else if (NetStreamConstants.TYPE_SHORT == valueType) {
			return encodeShort(in);
		} else if (NetStreamConstants.TYPE_SHORT_ARRAY == valueType) {
			return encodeShortArray(in);
		} else if (NetStreamConstants.TYPE_INT == valueType) {
			return encodeInt(in);
		} else if (NetStreamConstants.TYPE_INT_ARRAY == valueType) {
			return encodeIntArray(in);
		} else if (NetStreamConstants.TYPE_LONG == valueType) {
			return encodeLong(in);
		} else if (NetStreamConstants.TYPE_LONG_ARRAY == valueType) {
			return encodeLongArray(in);
		} else if (NetStreamConstants.TYPE_FLOAT == valueType) {
			return encodeFloat(in);
		} else if (NetStreamConstants.TYPE_FLOAT_ARRAY == valueType) {
			return encodeFloatArray(in);
		} else if (NetStreamConstants.TYPE_DOUBLE == valueType) {
			return encodeDouble(in);
		} else if (NetStreamConstants.TYPE_DOUBLE_ARRAY == valueType) {
			return encodeDoubleArray(in);
		} else if (NetStreamConstants.TYPE_STRING == valueType) {
			return encodeString(in);
		} else if (NetStreamConstants.TYPE_ARRAY == valueType) {
			return encodeArray(in);
		}
		return null;

	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeArray(Object in) {

		return null;

	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeString(Object in) {
		String s = (String) in;
		byte[] data = s.getBytes(Charset.forName("UTF-8"));
		return ByteBuffer.allocate(2 + data.length)
				.putShort((short) data.length).put(data);
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeDoubleArray(Object in) {
		double[] data = (double[]) in;
		ByteBuffer b = ByteBuffer.allocate(2 + data.length * 8).putShort(
				(short) data.length);

		for (int i = 0; i < data.length; i++) {
			b.putDouble(data[i]);
		}
		return b;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeDouble(Object in) {
		return ByteBuffer.allocate(8).putDouble((Double) in);
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeFloatArray(Object in) {
		float[] data = (float[]) in;
		ByteBuffer b = ByteBuffer.allocate(2 + data.length * 4).putShort(
				(short) data.length);

		for (int i = 0; i < data.length; i++) {
			b.putFloat(data[i]);
		}
		return b;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeFloat(Object in) {
		return ByteBuffer.allocate(4).putFloat(((Float) in));
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeLongArray(Object in) {
		long[] data = (long[]) in;
		ByteBuffer b = ByteBuffer.allocate(2 + data.length * 8).putShort(
				(short) data.length);

		for (int i = 0; i < data.length; i++) {
			b.putLong(data[i]);
		}
		return b;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeLong(Object in) {
		return ByteBuffer.allocate(8).putLong((Long) in);
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeIntArray(Object in) {
		int[] data = (int[]) in;
		ByteBuffer b = ByteBuffer.allocate(2 + data.length * 4).putShort(
				(short) data.length);

		for (int i = 0; i < data.length; i++) {
			b.putInt(data[i]);
		}
		return b;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeInt(Object in) {
		return ByteBuffer.allocate(4).putInt((Integer) in);
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeShortArray(Object in) {
		short[] data = (short[]) in;
		ByteBuffer b = ByteBuffer.allocate(2 + data.length * 2).putShort(
				(short) data.length);

		for (int i = 0; i < data.length; i++) {
			b.putShort(data[i]);
		}
		return b;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeShort(Object in) {
		return ByteBuffer.allocate(2).putShort((Short) in);
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeByteArray(Object in) {
		byte[] data = (byte[]) in;
		ByteBuffer b = ByteBuffer.allocate(2 + data.length).putShort(
				(short) data.length);

		for (int i = 0; i < data.length; i++) {
			b.put(data[i]);
		}
		return b;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeByte(Object in) {
		return ByteBuffer.allocate(1).put((Byte) in);
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeBooleanArray(Object in) {
		boolean[] data = (boolean[]) in;
		ByteBuffer b = ByteBuffer.allocate(2 + data.length).putShort(
				(short) data.length);

		for (int i = 0; i < data.length; i++) {
			b.put((byte) (data[i] == false ? 0 : 1));
		}
		return b;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeBoolean(Object in) {
		return ByteBuffer.allocate(1).put(
				(byte) (((Boolean) in) == false ? 0 : 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang.String
	 * , long, java.lang.String, java.lang.Object)
	 */
	@Override
	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(value);
			ByteBuffer bValue = encodeValue(value, valueType);
			bValue.flip();
			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + // CMD
					2 + attrArray.length + // attribute id
					1 + // attr type
					bValue.capacity()); // attr value

			buff.putShort((short) streamIdArray.length).put(streamIdArray)
					.put((byte) NetStreamConstants.CMD_ADD_GRAPH_ATTR)

					.putShort((short) attrArray.length).put(attrArray)
					.put((byte) valueType).put(bValue);

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang.
	 * String, long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(oldValue);

			ByteBuffer bOldValue = encodeValue(oldValue, valueType);
			bOldValue.flip();
			ByteBuffer bNewValue = encodeValue(oldValue, valueType);
			bNewValue.flip();

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length
					+ // stream id
					1 + 2 + attrArray.length + 1 + bOldValue.capacity()
					+ bNewValue.capacity());
			buff.putShort((short) streamIdArray.length).put(streamIdArray)
					.put((byte) NetStreamConstants.CMD_CHG_GRAPH_ATTR)
					.putShort((short) attrArray.length).put(attrArray)
					.put((byte) valueType).put(bOldValue).put(bNewValue);
			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang.
	 * String, long, java.lang.String)
	 */
	@Override
	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + 2 + attrArray.length);

			buff.putShort((short) streamIdArray.length).put(streamIdArray)
					.put((byte) NetStreamConstants.CMD_DEL_GRAPH_ATTR)
					.putShort((short) attrArray.length).put(attrArray);
			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(value);
			ByteBuffer bValue = encodeValue(value, valueType);
			bValue.flip();
			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + // CMD
					(2 + nodeIdArray.length) + // nodeId
					(2 + attrArray.length) + // attribute
					1 + // value type
					bValue.capacity() // value
			);

			buff.putShort((short) streamIdArray.length).put(streamIdArray) // Stream
																			// ID
					.put((byte) NetStreamConstants.CMD_ADD_NODE_ATTR) // CMD
					.putShort((short) nodeIdArray.length).put(nodeIdArray) // nodeId
					.putShort((short) attrArray.length).put(attrArray) // attribute
					.put((byte) valueType) // value type
					.put(bValue); // value

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(oldValue);

			ByteBuffer bOldValue = encodeValue(oldValue, valueType);
			bOldValue.flip();
			ByteBuffer bNewValue = encodeValue(oldValue, valueType);
			bNewValue.flip();

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + // CMD
					(2 + nodeIdArray.length) + // nodeId
					(2 + attrArray.length) + // attribute
					1 + // value type
					bOldValue.capacity() + // value
					bNewValue.capacity() // new value
			);

			buff.putShort((short) streamIdArray.length).put(streamIdArray) // Stream
																			// ID
					.put((byte) NetStreamConstants.CMD_ADD_NODE_ATTR) // CMD
					.putShort((short) nodeIdArray.length).put(nodeIdArray) // nodeId
					.putShort((short) attrArray.length).put(attrArray) // attribute
					.put((byte) valueType) // value type
					.put(bOldValue) // value
					.put(bNewValue); // value
			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	@Override
	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + // CMD
					(2 + nodeIdArray.length) + // nodeId
					(2 + attrArray.length) // attribute
			);

			buff.putShort((short) streamIdArray.length).put(streamIdArray) // Stream
																			// ID
					.put((byte) NetStreamConstants.CMD_ADD_NODE_ATTR) // CMD
					.putShort((short) nodeIdArray.length).put(nodeIdArray) // nodeId
					.putShort((short) attrArray.length).put(attrArray); // attribute

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");
			// System.out.println("sending "+buff.capacity()+" bytes");out.write(buff.array(),0,buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(value);
			ByteBuffer bValue = encodeValue(value, valueType);
			bValue.flip();
			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + // CMD
					(2 + edgeIdArray.length) + // nodeId
					(2 + attrArray.length) + // attribute
					1 + // value type
					bValue.capacity() // value
			);

			buff.putShort((short) streamIdArray.length).put(streamIdArray) // Stream
																			// ID
					.put((byte) NetStreamConstants.CMD_ADD_EDGE_ATTR) // CMD
					.putShort((short) edgeIdArray.length).put(edgeIdArray) // nodeId
					.putShort((short) attrArray.length).put(attrArray) // attribute
					.put((byte) valueType) // value type
					.put(bValue); // value
			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");
			// System.out.println("sending "+buff.capacity()+" bytes");out.write(buff.array(),0,buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(oldValue);

			ByteBuffer bOldValue = encodeValue(oldValue, valueType);
			bOldValue.flip();
			ByteBuffer bNewValue = encodeValue(oldValue, valueType);
			bNewValue.flip();

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + // CMD
					(2 + edgeIdArray.length) + // nodeId
					(2 + attrArray.length) + // attribute
					1 + // value type
					bOldValue.capacity() + // value
					bNewValue.capacity() // new value
			);

			buff.putShort((short) streamIdArray.length).put(streamIdArray) // Stream
																			// ID
					.put((byte) NetStreamConstants.CMD_ADD_EDGE_ATTR) // CMD
					.putShort((short) edgeIdArray.length).put(edgeIdArray) // nodeId
					.putShort((short) attrArray.length).put(attrArray) // attribute
					.put((byte) valueType) // value type
					.put(bOldValue) // value
					.put(bNewValue); // value
			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	@Override
	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + // CMD
					(2 + edgeIdArray.length) + // nodeId
					(2 + attrArray.length) // attribute
			);

			buff.putShort((short) streamIdArray.length).put(streamIdArray) // Stream
																			// ID
					.put((byte) NetStreamConstants.CMD_ADD_NODE_ATTR) // CMD
					.putShort((short) edgeIdArray.length).put(edgeIdArray) // nodeId
					.putShort((short) attrArray.length).put(attrArray); // attribute

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + 2 + nodeIdArray.length);
			buff.putShort((short) streamIdArray.length).put(streamIdArray)
					// Stream ID
					.put((byte) NetStreamConstants.CMD_ADD_NODE)
					.putShort((short) nodeIdArray.length).put(nodeIdArray);

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + 2 + nodeIdArray.length);
			buff.putShort((short) streamIdArray.length).put(streamIdArray)
					// Stream ID
					.put((byte) NetStreamConstants.CMD_DEL_NODE)
					.putShort((short) nodeIdArray.length).put(nodeIdArray);

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
			byte[] fromNodeIdArray = fromNodeId.getBytes(Charset
					.forName("UTF-8"));
			byte[] toNodeIdArray = toNodeId.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length
					+ // stream id
					1 + 2 + edgeIdArray.length + 2 + fromNodeIdArray.length + 2
					+ toNodeIdArray.length + 1);
			buff.putShort((short) streamIdArray.length)
					.put(streamIdArray)
					// Stream ID
					.put((byte) NetStreamConstants.CMD_ADD_EDGE)
					.putShort((short) edgeIdArray.length).put(edgeIdArray)
					.putShort((short) fromNodeIdArray.length)
					.put(fromNodeIdArray)
					.putShort((short) toNodeIdArray.length).put(toNodeIdArray)
					.put((byte) (directed == false ? 0 : 1));

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {
			byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + 2 + edgeIdArray.length);
			buff.putShort((short) streamIdArray.length).put(streamIdArray)
					// Stream ID
					.put((byte) NetStreamConstants.CMD_DEL_EDGE)
					.putShort((short) edgeIdArray.length).put(edgeIdArray);

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String,
	 * long)
	 */
	@Override
	public void graphCleared(String sourceId, long timeId) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1);
			buff.putShort((short) streamIdArray.length).put(streamIdArray) // Stream
																			// ID
					.put((byte) NetStreamConstants.CMD_CLEARED);

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String,
	 * long, double)
	 */
	@Override
	public void stepBegins(String sourceId, long timeId, double step) {
		if (socket == null || socket.isClosed()) {
			connect();
		}
		try {

			ByteBuffer buff = ByteBuffer.allocate(2 + streamIdArray.length + // stream
																				// id
					1 + 8);
			buff.putShort((short) streamIdArray.length).put(streamIdArray) // Stream
																			// ID
					.put((byte) NetStreamConstants.CMD_STEP).putDouble(step);

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			// System.out.println("sending "+buff.capacity()+" bytes");

			out.write(buff.array(), 0, buff.capacity());
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws InterruptedException {

		Graph g1_1 = new MultiGraph("G1_1");
		Graph g1_2 = new MultiGraph("G1_2");
		Graph g2 = new MultiGraph("G2");

		g1_1.display();
		g1_2.display();

		NetStreamClient nsc1_1 = new NetStreamClient("G1", "localhost", 2001);
		NetStreamClient nsc1_2 = new NetStreamClient("G1", "localhost", 2001);
		NetStreamClient nsc2 = new NetStreamClient("G2", "localhost", 2001);
		
		g1_1.addSink(nsc1_1);
		g1_2.addSink(nsc1_2);
		g2.addSink(nsc2);

		String ss = "node{fill-mode:plain;fill-color:#567;size:6px;}";
		g1_1.addAttribute("layout.stabilization-limit", 0);
		g1_1.addAttribute("stylesheet", ss);
		g1_1.addAttribute("ui.antialias", true);
		g1_2.addAttribute("layout.stabilization-limit", 0);
		g1_2.addAttribute("stylesheet", ss);
		g1_2.addAttribute("ui.antialias", true);
		
		
		String ss2 = "node{fill-mode:plain;fill-color:#765;size:6px;}";
		g2.addAttribute("layout.stabilization-limit", 0);
		g2.addAttribute("stylesheet", ss2);
		g2.addAttribute("ui.antialias", true);
		
		
		
		
		
		
		
		
		
		for (int i = 0; i < 50; i++) {

			g1_1.addNode(i + "");
			if (i > 0) {
				g1_1.addEdge(i + "-" + (i - 1), i + "", (i - 1) + "");
				g1_1.addEdge(i + "--" + (i / 2), i + "", (i / 2) + "");

			}
			g1_2.addNode(i + "*");
			if (i > 0) {
				g1_2.addEdge(i + "-*" + (i - 1), i + "*", (i - 1) + "*");
				g1_2.addEdge(i + "--*" + (i / 2), i + "*", (i / 2) + "*");
			}
			g2.addNode(i + "");
			if (i > 0) {
				g2.addEdge(i + "-" + (i - 1), i + "", (i - 1) + "");
				g2.addEdge(i + "--" + (i / 2), i + "", (i / 2) + "");

			}

		}

	}

}
