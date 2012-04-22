/*
 * Copyright 2006 - 2012
 *      Stefan Balev       <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	<yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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

import org.graphstream.stream.Sink;
import org.graphstream.stream.netstream.packing.NetStreamPacker;

/**
 * <p>
 * This class implements a sender according to specifications the NetStream
 * protocol.
 * </p>
 * 
 * <p>
 * See {@link NetStreamConstants} for a full description of the protocol, the
 * sender and the receiver.
 * </p>
 * 
 * @see NetStreamConstants
 * @see NetStreamReceiver
 * 
 * 
 *      Copyright (c) 2010 University of Luxembourg
 * 
 *      NetStreamSender.java
 * @since Aug 10, 2011
 * 
 * @author Yoann Pigné
 * 
 */
public class NetStreamSender implements Sink {
	protected String stream;
	byte[] streamIdArray;
	protected String host;
	protected int port;
	protected Socket socket;
	protected BufferedOutputStream out;

	protected String sourceId = "";
	protected byte[] sourceIdBuff;

	class DefaultPacker extends NetStreamPacker {
		ByteBuffer sizeBuffer = ByteBuffer.allocate(4);

		@Override
		public ByteBuffer packMessage(ByteBuffer buffer, int startIndex,
				int endIndex) {
			return buffer;
		}

		@Override
		public ByteBuffer packMessageSize(int capacity) {
			sizeBuffer.rewind();
			sizeBuffer.putInt(capacity);
			return sizeBuffer;
		}

	};

	protected NetStreamPacker packer = new DefaultPacker();

	public NetStreamSender(String host, int port) throws UnknownHostException,
			IOException {
		this("default", host, port);
	}
	public NetStreamSender(int port) throws UnknownHostException, IOException {
		this("default", "localhost", port);
	}

	public NetStreamSender(String stream, String host, int port)
			throws UnknownHostException, IOException {
		this.stream = stream;
		this.host = host;
		this.port = port;
		streamIdArray = stream.getBytes(Charset.forName("UTF-8"));

		connect();
		
	}

	/**
	 * Sets an optional NetStreamPaker whose "pack" method will be called on
	 * each message.
	 * 
	 * a Packer can do extra encoding on the all byte array message, it may also
	 * crypt things.
	 * 
	 * @param paker
	 *            The packer object
	 */
	public void setPacker(NetStreamPacker paker) {
		this.packer = paker;
	}
	public void removePacker() {
		packer = new DefaultPacker();
	}

	protected void connect() throws UnknownHostException, IOException {

		socket = new Socket(host, port);
		out = new BufferedOutputStream(socket.getOutputStream());

	}

	protected int getType(Object value) {
		int valueType = 0;
		Class<?> valueClass = value.getClass();
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
		// TODO...
		return null;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeString(Object in) {
		String s = (String) in;
		byte[] data = s.getBytes(Charset.forName("UTF-8"));
		return ByteBuffer.allocate(4 + data.length).putInt(data.length)
				.put(data);
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeDoubleArray(Object in) {
		Object[] data = (Object[]) in;

		ByteBuffer b = ByteBuffer.allocate(4 + data.length * 8);

		b.putInt(data.length);

		for (int i = 0; i < data.length; i++) {
			b.putDouble((Double) data[i]);
		}
		return b;
	}

	/**
	 * @param in The double to encode
	 * @return ByteBuffer with encoded double in it
	 */
	protected ByteBuffer encodeDouble(Object in) {
		return ByteBuffer.allocate(8).putDouble((Double) in);
	}

	/**
	 * @param in The float array to encode
	 * @return ByteBuffer with encoded float array in it
	 */
	protected ByteBuffer encodeFloatArray(Object in) {
		Object[] data = (Object[]) in;
		ByteBuffer b = ByteBuffer.allocate(4 + data.length * 4).putInt(
				data.length);

		for (int i = 0; i < data.length; i++) {
			b.putFloat((Float) data[i]);
		}
		return b;
	}

	/**
	 * @param in The float to encode
	 * @return ByteBuffer with encoded float in it
	 */
	protected ByteBuffer encodeFloat(Object in) {
		return ByteBuffer.allocate(4).putFloat(((Float) in));
	}

	/**
	 * @param in The long array to encode
	 * @return ByteBuffer with encoded long array in it
	 */
	protected ByteBuffer encodeLongArray(Object in) {
		Object[] data = (Object[]) in;
		ByteBuffer b = ByteBuffer.allocate(4 + data.length * 8);
		b.putInt(data.length);

		for (int i = 0; i < data.length; i++) {
			b.putLong((Long) data[i]);
		}
		return b;
	}

	/**
	 * @param in The long to encode
	 * @return ByteBuffer with encoded long in it
	 */
	protected ByteBuffer encodeLong(Object in) {
		return ByteBuffer.allocate(8).putLong((Long) in);
	}

	/**
	 * @param in The integer array to encode
	 * @return ByteBuffer with encoded integer array in it
	 */
	protected ByteBuffer encodeIntArray(Object in) {
		Object[] data = (Object[]) in;
		ByteBuffer b = ByteBuffer.allocate(4 + data.length * 4);
		b.putInt(data.length);

        for (Object aData : data) {
            b.putInt((Integer) aData);
        }
		return b;
	}

	/**
	 * @param in The integer to encode
	 * @return ByteBuffer with encoded integer in it
	 */
	protected ByteBuffer encodeInt(Object in) {
		return ByteBuffer.allocate(4).putInt((Integer) in);
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeShortArray(Object in) {
		Object[] data = (Object[]) in;
		ByteBuffer b = ByteBuffer.allocate(4 + data.length * 2);
		b.putInt(data.length);

		for (int i = 0; i < data.length; i++) {
			b.putShort((Short) data[i]);
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
		Object[] data = (Object[]) in;
		ByteBuffer b = ByteBuffer.allocate(4 + data.length);
		b.putInt(data.length);

		for (int i = 0; i < data.length; i++) {
			b.put((Byte) data[i]);
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
		Object[] data = (Object[]) in;
		ByteBuffer b = ByteBuffer.allocate(4 + data.length);
		b.putInt(data.length);

		for (int i = 0; i < data.length; i++) {
			b.put((byte) ((Boolean) data[i] == false ? 0 : 1));
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

	/**
	 * @param buff
	 */
	private void doSend(ByteBuffer buff) {

		if (socket.isClosed()) {
			System.err
					.println("NetStreamSender : can't send. The socket is closed.");
		} else {
			ByteBuffer buffer = packer.packMessage(buff);
			ByteBuffer sizeBuffer = packer.packMessageSize(buffer.capacity());
			buff.rewind();
			// real sending
			try {
				out.write(sizeBuffer.array(), 0, sizeBuffer.capacity());
				out.write(buffer.array(), 0, buffer.capacity());
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang.String
	 * , long, java.lang.String, java.lang.Object)
	 */
	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
		int valueType = getType(value);
		ByteBuffer bValue = encodeValue(value, valueType);
		bValue.flip();
		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
																			// id
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				4 + attrArray.length + // attribute id
				1 + // attr type
				bValue.capacity()); // attr value

		buff.putInt(streamIdArray.length).put(streamIdArray)
				.put((byte) NetStreamConstants.EVENT_ADD_GRAPH_ATTR)
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(attrArray.length).put(attrArray).put((byte) valueType)
				.put(bValue);

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang.
	 * String, long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
		int oldValueType = getType(oldValue);
		int newValueType = getType(newValue);

		ByteBuffer bOldValue = encodeValue(oldValue, oldValueType);
		bOldValue.flip();
		ByteBuffer bNewValue = encodeValue(newValue, newValueType);
		bNewValue.flip();

		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // Stream
																			// id
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				4 + attrArray.length + // attr name
				1 + // value type
				bOldValue.capacity() + 
				1 + // value type
				bNewValue.capacity()); // values

		buff.putInt(streamIdArray.length).put(streamIdArray)
				.put((byte) NetStreamConstants.EVENT_CHG_GRAPH_ATTR)
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(attrArray.length).put(attrArray)
				.put((byte) oldValueType)
				.put(bOldValue)
				.put((byte) newValueType)
				.put(bNewValue);

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang.
	 * String, long, java.lang.String)
	 */
	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));

		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
																			// id
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				4 + attrArray.length // ATTR name
		);

		buff.putInt(streamIdArray.length).put(streamIdArray)
				.put((byte) NetStreamConstants.EVENT_DEL_GRAPH_ATTR)
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(attrArray.length).put(attrArray);

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));
		byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
		int valueType = getType(value);
		ByteBuffer bValue = encodeValue(value, valueType);
		bValue.flip();
		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
																			// ID
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				(4 + nodeIdArray.length) + // nodeId
				(4 + attrArray.length) + // attribute
				1 + // value type
				bValue.capacity() // value
		);

		buff.putInt(streamIdArray.length).put(streamIdArray)
				// Stream
				.put((byte) NetStreamConstants.EVENT_ADD_NODE_ATTR)
				// CMD
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(nodeIdArray.length).put(nodeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray) // attribute
				.put((byte) valueType) // value type
				.put(bValue); // value

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
		byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));
		int oldValueType = getType(oldValue);
		int newValueType = getType(newValue);

		ByteBuffer bOldValue = encodeValue(oldValue, oldValueType);
		bOldValue.flip();
		ByteBuffer bNewValue = encodeValue(newValue, newValueType);
		bNewValue.flip();

		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				(4 + nodeIdArray.length) + // nodeId
				(4 + attrArray.length) + // attribute
				1 + // value type
				bOldValue.capacity() + // value
				1 + // value type
				bNewValue.capacity() // new value
		);

		buff.putInt(streamIdArray.length).put(streamIdArray)
				// Stream id
				.put((byte) NetStreamConstants.EVENT_CHG_NODE_ATTR)
				// CMD
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(nodeIdArray.length).put(nodeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray) // attribute
				.put((byte) oldValueType) // value type
				.put(bOldValue) // value
				.put((byte) newValueType) // value type
				.put(bNewValue); // value
		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));
		byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));

		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
																			// id
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				(4 + nodeIdArray.length) + // nodeId
				(4 + attrArray.length) // attribute
		);

		buff.putInt(streamIdArray.length).put(streamIdArray)
				// Stream id
				.put((byte) NetStreamConstants.EVENT_DEL_NODE_ATTR)
				// CMD
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(nodeIdArray.length).put(nodeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray); // attribute

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
		byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
		int valueType = getType(value);
		ByteBuffer bValue = encodeValue(value, valueType);
		bValue.flip();
		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				(4 + edgeIdArray.length) + // nodeId
				(4 + attrArray.length) + // attribute
				1 + // value type
				bValue.capacity() // value
		);

		buff.putInt(streamIdArray.length).put(streamIdArray)
				// Stream id
				.put((byte) NetStreamConstants.EVENT_ADD_EDGE_ATTR)
				// CMD
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(edgeIdArray.length).put(edgeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray) // attribute
				.put((byte) valueType) // value type
				.put(bValue); // value

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
		byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
		int oldValueType = getType(oldValue);
		int newValueType = getType(newValue);

		ByteBuffer bOldValue = encodeValue(oldValue, oldValueType);
		bOldValue.flip();
		ByteBuffer bNewValue = encodeValue(newValue, newValueType);
		bNewValue.flip();

		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
																			// id
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				(4 + edgeIdArray.length) + // nodeId
				(4 + attrArray.length) + // attribute
				1 + // value type
				bOldValue.capacity() + // value
				1 + // value type
				bNewValue.capacity() // new value
		);

		buff.putInt(streamIdArray.length).put(streamIdArray)
				// Stream
				.put((byte) NetStreamConstants.EVENT_CHG_EDGE_ATTR)
				// CMD
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(edgeIdArray.length).put(edgeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray) // attribute
				.put((byte) oldValueType) // value type
				.put(bOldValue) // value
				.put((byte) newValueType) // value type
				.put(bNewValue); // value

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
		byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));

		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
																			// id
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				(4 + edgeIdArray.length) + // nodeId
				(4 + attrArray.length) // attribute
		);

		buff.putInt(streamIdArray.length).put(streamIdArray)
				// Stream id
				.put((byte) NetStreamConstants.EVENT_DEL_EDGE_ATTR)
				// CMD
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(edgeIdArray.length).put(edgeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray); // attribute

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));

		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				4 + nodeIdArray.length // node id
		);

		buff.putInt(streamIdArray.length)
				.put(streamIdArray)
				// Stream ID
				.put((byte) NetStreamConstants.EVENT_ADD_NODE)
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(nodeIdArray.length).put(nodeIdArray);

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));

		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
																			// id
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				4 + nodeIdArray.length // node id
		);
		buff.putInt(streamIdArray.length)
				.put(streamIdArray)
				// Stream ID
				.put((byte) NetStreamConstants.EVENT_DEL_NODE)
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(nodeIdArray.length).put(nodeIdArray);

		doSend(buff);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
		byte[] fromNodeIdArray = fromNodeId.getBytes(Charset.forName("UTF-8"));
		byte[] toNodeIdArray = toNodeId.getBytes(Charset.forName("UTF-8"));

		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
																			// id
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				4 + edgeIdArray.length + // edge
				4 + fromNodeIdArray.length + // node from
				4 + toNodeIdArray.length + // node to
				1 // direction
		);
		buff.putInt(streamIdArray.length)
				.put(streamIdArray)
				// Stream ID
				.put((byte) NetStreamConstants.EVENT_ADD_EDGE)
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(edgeIdArray.length).put(edgeIdArray)
				.putInt(fromNodeIdArray.length).put(fromNodeIdArray)
				.putInt(toNodeIdArray.length).put(toNodeIdArray)
				.put((byte) (!directed ? 0 : 1));

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));

		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				4 + edgeIdArray.length // edge
		);
		buff.putInt(streamIdArray.length)
				.put(streamIdArray)
				// Stream ID
				.put((byte) NetStreamConstants.EVENT_DEL_EDGE)
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putInt(edgeIdArray.length).put(edgeIdArray);

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String,
	 * long)
	 */
	public void graphCleared(String sourceId, long timeId) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
																			// id
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 // timeId
		);
		buff.putInt(streamIdArray.length).put(streamIdArray)
				// Stream id
				.put((byte) NetStreamConstants.EVENT_CLEARED)
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId);

		doSend(buff);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String,
	 * long, double)
	 */
	public void stepBegins(String sourceId, long timeId, double step) {

		if (!sourceId.equals(this.sourceId)) {
			this.sourceId = sourceId;
			sourceIdBuff = sourceId.getBytes(Charset.forName("UTF-8"));
		}
		ByteBuffer buff = ByteBuffer.allocate(4 + streamIdArray.length + // stream
																			// id
				1 + // CMD
				4 + sourceIdBuff.length + // source id
				8 + // timeId
				8 // time
		);
		buff.putInt(streamIdArray.length)
				.put(streamIdArray)
				// Stream
				.put((byte) NetStreamConstants.EVENT_STEP)
				.putInt(sourceIdBuff.length).put(sourceIdBuff).putLong(timeId)
				.putDouble(step);

		doSend(buff);
	}

	/**
	 * Force the connection to close (properly) with the server
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		socket.close();
	}

}
