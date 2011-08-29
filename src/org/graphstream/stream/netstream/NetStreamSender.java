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

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
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
public class NetStreamSender implements Sink {
	protected String stream;
	byte[] streamIdArray;
	protected String host;
	protected int port;
	protected Socket socket;
	protected BufferedOutputStream out;

	ByteBuffer buffSizeBuff;

	public NetStreamSender(String host, int port) throws UnknownHostException, IOException {
	this("default",host,port);
	}
	public NetStreamSender( int port) throws UnknownHostException, IOException {
	this("default","localhost",port);
	}
		
	public NetStreamSender(String stream, String host, int port) throws UnknownHostException, IOException {
		this.stream = stream;
		this.host = host;
		this.port = port;
		buffSizeBuff = ByteBuffer.allocate(4);
		streamIdArray = stream.getBytes(Charset.forName("UTF-8"));

		connect();
	}

	protected void connect() throws UnknownHostException, IOException {

		
			socket = new Socket(host, port);
			out = new BufferedOutputStream(socket.getOutputStream());

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
		//System.out.println("ValueType="+valueType+" "+value.getClass());
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
		return ByteBuffer.allocate(4 + data.length)
				.putInt( data.length)
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
		Object[] data = (Object[]) in;
		ByteBuffer b = ByteBuffer.allocate(4 + data.length * 4).putInt(
			 data.length);
		
		for (int i = 0; i < data.length; i++) {
			b.putFloat((Float) data[i]);
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
		Object[] data = (Object[]) in;
		ByteBuffer b = ByteBuffer.allocate(4 + data.length * 8);
		b.putInt(data.length);

		for (int i = 0; i < data.length; i++) {
			b.putLong((Long) data[i]);
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
		Object[] data = (Object[]) in;
		ByteBuffer b = ByteBuffer.allocate(4 + data.length * 4);
		b.putInt(data.length);

		for (int i = 0; i < data.length; i++) {
			b.putInt((Integer)data[i]);
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
		Object[] data = (Object[]) in;
		ByteBuffer b = ByteBuffer.allocate(4 + data.length * 2);
		b.putInt(data.length);

		for (int i = 0; i < data.length; i++) {
			b.putShort((Short)data[i]);
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
			b.put((byte) ((Boolean)data[i] == false ? 0 : 1));
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
		
		try {
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(value);
			ByteBuffer bValue = encodeValue(value, valueType);
			bValue.flip();
			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream id
					1 + // CMD
					4 + attrArray.length + // attribute id
					1 + // attr type
					bValue.capacity()); // attr value

			buff
				.putInt(streamIdArray.length).put(streamIdArray)
				.put((byte) NetStreamConstants.EVENT_ADD_GRAPH_ATTR)
				.putInt(attrArray.length).put(attrArray)
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
		
		try {
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(oldValue);

			ByteBuffer bOldValue = encodeValue(oldValue, valueType);
			bOldValue.flip();
			ByteBuffer bNewValue = encodeValue(newValue, valueType);
			bNewValue.flip();

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // Stream id 
					1 + // CMD
					4 + attrArray.length + // attr name  
					1 + bOldValue.capacity() + bNewValue.capacity()); // values
			
			buff
				.putInt( streamIdArray.length).put(streamIdArray)
				.put((byte) NetStreamConstants.EVENT_CHG_GRAPH_ATTR)
				.putInt( attrArray.length).put(attrArray)
				.put((byte) valueType)
				.put(bOldValue)
				.put(bNewValue);
			
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
		
		try {
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream id
					1 + // CMD
					4 + attrArray.length // ATTR name
					);

			buff
				.putInt(streamIdArray.length)
				.put(streamIdArray)
				.put((byte) NetStreamConstants.EVENT_DEL_GRAPH_ATTR)
				.putInt(attrArray.length).put(attrArray);
			
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
		
		try {
			byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(value);
			ByteBuffer bValue = encodeValue(value, valueType);
			bValue.flip();
			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream ID
					1 + // CMD
					(4 + nodeIdArray.length) + // nodeId
					(4 + attrArray.length) + // attribute
					1 + // value type
					bValue.capacity() // value
			);

			buff
				.putInt( streamIdArray.length).put(streamIdArray) // Stream
				.put((byte) NetStreamConstants.EVENT_ADD_NODE_ATTR) // CMD
				.putInt(nodeIdArray.length).put(nodeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray) // attribute
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
		try {
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(oldValue);

			ByteBuffer bOldValue = encodeValue(oldValue, valueType);
			bOldValue.flip();
			ByteBuffer bNewValue = encodeValue(newValue, valueType);
			bNewValue.flip();

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream
					1 + // CMD
					(4 + nodeIdArray.length) + // nodeId
					(4 + attrArray.length) + // attribute
					1 + // value type
					bOldValue.capacity() + // value
					bNewValue.capacity() // new value
			);

			buff
				.putInt(streamIdArray.length).put(streamIdArray) // Stream id
				.put((byte) NetStreamConstants.EVENT_CHG_NODE_ATTR) // CMD
				.putInt(nodeIdArray.length).put(nodeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray) // attribute
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
		
		try {
			byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream id
					1 + // CMD
					(4 + nodeIdArray.length) + // nodeId
					(4 + attrArray.length) // attribute
			);

			buff
				.putInt (streamIdArray.length).put(streamIdArray) // Stream id
				.put((byte) NetStreamConstants.EVENT_DEL_NODE_ATTR) // CMD
				.putInt(nodeIdArray.length).put(nodeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray); // attribute

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);

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
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		
		try {
			byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(value);
			ByteBuffer bValue = encodeValue(value, valueType);
			bValue.flip();
			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream
					1 + // CMD
					(4 + edgeIdArray.length) + // nodeId
					(4 + attrArray.length) + // attribute
					1 + // value type
					bValue.capacity() // value
			);

			buff
				.putInt(streamIdArray.length).put(streamIdArray) // Stream id
				.put((byte) NetStreamConstants.EVENT_ADD_EDGE_ATTR) // CMD
				.putInt(edgeIdArray.length).put(edgeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray) // attribute
				.put((byte) valueType) // value type
				.put(bValue); // value
			
			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);

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
	 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		
		try {
			byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));
			int valueType = getType(oldValue);

			ByteBuffer bOldValue = encodeValue(oldValue, valueType);
			bOldValue.flip();
			ByteBuffer bNewValue = encodeValue(newValue, valueType);
			bNewValue.flip();

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream id
					1 + // CMD
					(4 + edgeIdArray.length) + // nodeId
					(4 + attrArray.length) + // attribute
					1 + // value type
					bOldValue.capacity() + // value
					bNewValue.capacity() // new value
			);

			buff
				.putInt(streamIdArray.length).put(streamIdArray) // Stream
				.put((byte) NetStreamConstants.EVENT_CHG_EDGE_ATTR) // CMD
				.putInt(edgeIdArray.length).put(edgeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray) // attribute
				.put((byte) valueType) // value type
				.put(bOldValue) // value
				.put(bNewValue); // value

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);

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
		
		try {
			byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
			byte[] attrArray = attribute.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream id
					1 + // CMD
					(4 + edgeIdArray.length) + // nodeId
					(4 + attrArray.length) // attribute
			);

			buff
				.putInt(streamIdArray.length).put(streamIdArray) // Stream id
				.put((byte) NetStreamConstants.EVENT_DEL_EDGE_ATTR) // CMD
				.putInt(edgeIdArray.length).put(edgeIdArray) // nodeId
				.putInt(attrArray.length).put(attrArray); // attribute

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			

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
		
		try {
			byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream
					1 + // CMD 
					4 + nodeIdArray.length // node id
					);
			
			buff
				.putInt(streamIdArray.length).put(streamIdArray)// Stream ID
				.put((byte) NetStreamConstants.EVENT_ADD_NODE)
				.putInt(nodeIdArray.length).put(nodeIdArray);

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			
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
		try {
			byte[] nodeIdArray = nodeId.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream id
					1 +  // CMD
					4 + nodeIdArray.length // node id
					);
			buff
				.putInt(streamIdArray.length).put(streamIdArray)// Stream ID
				.put((byte) NetStreamConstants.EVENT_DEL_NODE)
				.putInt(nodeIdArray.length).put(nodeIdArray);

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			
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
		
		try {
			byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));
			byte[] fromNodeIdArray = fromNodeId.getBytes(Charset
					.forName("UTF-8"));
			byte[] toNodeIdArray = toNodeId.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream id
					1 + // CMD 
					4 + edgeIdArray.length + // edge 
					4 + fromNodeIdArray.length + // node from
					4 + toNodeIdArray.length +  // node to 
					1 // direction
					);
			buff
				.putInt(streamIdArray.length).put(streamIdArray) // Stream ID
				.put((byte) NetStreamConstants.EVENT_ADD_EDGE)
				.putInt(edgeIdArray.length).put(edgeIdArray)
				.putInt(fromNodeIdArray.length).put(fromNodeIdArray)
				.putInt(toNodeIdArray.length).put(toNodeIdArray)
				.put((byte) (directed == false ? 0 : 1));

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			
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
		
		try {
			byte[] edgeIdArray = edgeId.getBytes(Charset.forName("UTF-8"));

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream
					1 + // CMD 
					4 + edgeIdArray.length // edge
					);
			buff
				.putInt(streamIdArray.length).put(streamIdArray)// Stream ID
				.put((byte) NetStreamConstants.EVENT_DEL_EDGE)
				.putInt(edgeIdArray.length).put(edgeIdArray);

			buffSizeBuff.rewind();
			buffSizeBuff.putInt(buff.capacity());
			out.write(buffSizeBuff.array(), 0, 4);
			
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
		
		try {

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream id
					1 // CMD
					);
			buff
				.putInt(streamIdArray.length).put(streamIdArray) // Stream id
				.put((byte) NetStreamConstants.EVENT_CLEARED);

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
		
		try {

			ByteBuffer buff = ByteBuffer.allocate(
					4 + streamIdArray.length + // stream id
					1 + //CMD 
					8 // time
					);
			buff
				.putInt(streamIdArray.length).put(streamIdArray) // Stream
				.put((byte) NetStreamConstants.EVENT_STEP)
				.putDouble(step);

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

	public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {

		// ------------------------------
		//           EXAMPLE 0 
		// ------------------------------
/*
		Graph g = new MultiGraph("G");
		g.display();
		
		NetStreamSender nsc = new NetStreamSender(2001);
		g.addSink(nsc);
		
	
		String ss = "node{fill-mode:plain;fill-color:#567;size:6px;}";
		
		g.addAttribute("stylesheet", ss);
		g.addAttribute("ui.antialias", true);
		g.addAttribute("layout.stabilization-limit", 0);
		
		for (int i = 0; i < 500; i++) {
			//g.addAttribute("ui.sprite."+i, i,i,0);
			g.addNode(i + "");
			if (i > 0) {
				g.addEdge(i + "-" + (i - 1), i + "", (i - 1) + "");
				g.addEdge(i + "--" + (i / 2), i + "", (i / 2) + "");

			}
		}
	*/	
		// ------------------------------
		//           EXAMPLE 1 
		// ------------------------------
		/*
		Graph g1_1 = new MultiGraph("G1_1");
		Graph g1_2 = new MultiGraph("G1_2");
		Graph g2 = new MultiGraph("G2");

		g1_1.display();
		g1_2.display();

		NetStreamSender nsc1_1 = new NetStreamSender("G1", "kvatch", 2001);
		NetStreamSender nsc1_2 = new NetStreamSender("G1", "kvatch", 2001);
		NetStreamSender nsc2 = new NetStreamSender("G2", "kvatch", 2001);
		
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
		
		for (int i = 0; i < 5; i++) {
			g1_1.addAttribute("ui.sprite."+i, i,i,0);
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
		
		*/

		
		
		
		// ------------------------------
		//           EXAMPLE 2
		// ------------------------------
		/*
		Graph g = new MultiGraph("G");
		NetStreamSender nsc = new NetStreamSender("localhost", 2001);
		g.addSink(nsc);
		
		g.addAttribute("intArray", 0,Integer.MAX_VALUE,Integer.MIN_VALUE);
		g.addAttribute("floatArray", 0f,Float.MAX_VALUE,Float.MIN_VALUE);
		g.addAttribute("doubleArray", 0.0,Double.MAX_VALUE,Double.MIN_VALUE);
		g.addAttribute("shortArray", (short)0, Short.MAX_VALUE, Short.MIN_VALUE);
		g.addAttribute("longArray", 0L,Long.MAX_VALUE,Long.MIN_VALUE);
		g.addAttribute("byteArray",(byte)0, Byte.MAX_VALUE, Byte.MIN_VALUE);
		g.addAttribute("booleanArray",true,false);
		//Object[] three = {new Short((short) 3),new Long(3L),"3"};
		//g.addAttribute("typeArray","one", 2 , three);
		g.addAttribute("int", 1);
		g.addAttribute("float", 1f);
		g.addAttribute("double", 1.0);
		g.addAttribute("short", (short)0);
		g.addAttribute("long", 1L);
		g.addAttribute("byte",(byte)0 );
		g.addAttribute("boolean",true);
		g.addAttribute("string","true");
		*/

		//---------------------------------------------
		//      exemple 3
		//----------------------------------
		Graph g = new MultiGraph("G", false, true);
		
		NetStreamSender nsc=null;
		try {
			nsc = new NetStreamSender("localhost", 2001);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		g.addSink(nsc);
		Node node0 = g.addNode("node0");
		//Node node1 = g.addNode("node1");
		Edge edge = g.addEdge("edge", "node0", "node1",true);
		node0.addAttribute("nodeAttribute", 0);
		node0.changeAttribute("nodeAttribute", 1);
		node0.removeAttribute("nodeAttribute");
		edge.addAttribute("edgeAttribute", 0);
		edge.changeAttribute("edgeAttribute", 1);
		edge.removeAttribute("edgeAttribute");
		g.addAttribute("graphAttribute", 0);
		g.changeAttribute("graphAttribute", 1);
		g.removeAttribute("graphAttribute");
		g.stepBegins(1.1);
		
		
	}
	/**
	 * Force the connection to close (properly) with the server
	 * @throws IOException 
	 */
	public void close() throws IOException {
		socket.close();
	}

}
