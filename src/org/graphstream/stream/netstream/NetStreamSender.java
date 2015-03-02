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
	private static ByteBuffer NULL_BUFFER = ByteBuffer.allocate(0);
	
	protected String stream;
	protected ByteBuffer streamBuffer;
	byte[] streamIdArray;
	protected String host;
	protected int port;
	protected Socket socket;
	protected BufferedOutputStream out;

	protected String sourceId = "";
	protected ByteBuffer sourceIdBuff;

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
		setStream(stream);
		
		connect();
		
	}
	
	/**
	 * @param stream2
	 */
	public void setStream(String stream) {
		streamIdArray = stream.getBytes(Charset.forName("UTF-8"));
		streamBuffer = encodeString(stream);
		
		
	}
	public NetStreamSender(Socket socket) throws IOException {
		this("default", socket);
	}
	
	public NetStreamSender(String stream, Socket socket) throws IOException {
		this.host = socket.getInetAddress().getHostName();
		this.port = socket.getPort();
		this.socket = socket;
		this.out = new BufferedOutputStream(socket.getOutputStream());
		this.streamIdArray = stream.getBytes(Charset.forName("UTF-8"));
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
		int valueType = NetStreamConstants.TYPE_UNKNOWN;
		
		if (value == null)
			return NetStreamConstants.TYPE_NULL;
		
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
		} else 
			System.err.printf("[warning] can not find type of %s\n", valueClass);
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
		} else if (NetStreamConstants.TYPE_NULL == valueType) {
			return NULL_BUFFER;
		}
		
		System.err.printf("[warning] unknown value type %d\n", valueType);
		
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

	/*
	private void outBuffer(ByteBuffer buf){
		System.out.println(buf.toString());
		int nbytes = buf.capacity();
		int at = buf.position();
		for(int i=0; i< nbytes; i++){
			int bt = buf.get(at+i);
			if (bt < 0) bt = (bt & 127) + (bt & 128); 
			System.out.printf("%d ", bt);
		}
		System.out.println();
	}*/
	
	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeString(Object in) {
		//System.out.println("They want me to encode this string: "+in);
		String s = (String) in;
		byte[] data = s.getBytes(Charset.forName("UTF-8"));
		
		ByteBuffer lenBuff = encodeUnsignedVarint(data.length);
		//outBuffer(lenBuff);
		ByteBuffer bb = ByteBuffer.allocate(lenBuff.capacity() + data.length);
		bb.put(lenBuff).put(data);
		bb.rewind();
		//outBuffer(bb);
		
		return bb;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeDoubleArray(Object in) {
		Object[] data = (Object[]) in;

		int ssize = varintSize(data.length);
		
		ByteBuffer b = ByteBuffer.allocate(ssize + data.length * 8);

		putVarint(b, data.length, ssize);

		for (int i = 0; i < data.length; i++) {
			b.putDouble((Double) data[i]);
		}
		b.rewind();
		return b;
	}

	/**
	 * @param in The double to encode
	 * @return ByteBuffer with encoded double in it
	 */
	protected ByteBuffer encodeDouble(Object in) {
		ByteBuffer bb = ByteBuffer.allocate(8).putDouble((Double) in);
		bb.rewind();
		return bb;
	}

	/**
	 * @param in The float array to encode
	 * @return ByteBuffer with encoded float array in it
	 */
	protected ByteBuffer encodeFloatArray(Object in) {
		Object[] data = (Object[]) in;
		
		int ssize = varintSize(data.length);
		
		ByteBuffer b = ByteBuffer.allocate(ssize + data.length * 4);
		
		putVarint(b, data.length, ssize);

		for (int i = 0; i < data.length; i++) {
			b.putFloat((Float) data[i]);
		}
		b.rewind();
		return b;
	}

	/**
	 * @param in The float to encode
	 * @return ByteBuffer with encoded float in it
	 */
	protected ByteBuffer encodeFloat(Object in) {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putFloat(((Float) in));
		b.rewind();
		return b;
	}

	/**
	 * @param in The long array to encode
	 * @return ByteBuffer with encoded long array in it
	 */
	protected ByteBuffer encodeLongArray(Object in) {
		return encodeVarintArray(in);
	}

	/**
	 * @param in The long to encode
	 * @return ByteBuffer with encoded long in it
	 */
	protected ByteBuffer encodeLong(Object in) {
		return encodeVarint(in);
	}

	/**
	 * @param in The integer array to encode
	 * @return ByteBuffer with encoded integer array in it
	 */
	protected ByteBuffer encodeIntArray(Object in) {
		return encodeVarintArray(in);
	}

	/**
	 * @param in The integer to encode
	 * @return ByteBuffer with encoded integer in it
	 */
	protected ByteBuffer encodeInt(Object in) {
		return encodeVarint(in);
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeShortArray(Object in) {
		return encodeVarintArray(in);
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeShort(Object in) {
		return encodeVarint(in);
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeByteArray(Object in) {
		Object[] data = (Object[]) in;

		int ssize = varintSize(data.length);
		
		ByteBuffer b = ByteBuffer.allocate(ssize + data.length);
		
		putVarint(b, data.length, ssize);

		for (int i = 0; i < data.length; i++) {
			b.put((Byte) data[i]);
		}
		b.rewind();
		return b;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeByte(Object in) {
		ByteBuffer b = ByteBuffer.allocate(1);
		b.put(((Byte) in));
		b.rewind();
		return b;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeBooleanArray(Object in) {
		Object[] data = (Object[]) in;

		int ssize = varintSize(data.length);
		
		ByteBuffer b = ByteBuffer.allocate(ssize + data.length);
		
		putVarint(b, data.length, ssize);

		for (int i = 0; i < data.length; i++) {
			b.put((byte) ((Boolean) data[i] == false ? 0 : 1));
		}
		b.rewind();
		return b;
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeBoolean(Object in) {
		ByteBuffer b = ByteBuffer.allocate(1);
		b.put((byte) (((Boolean) in) == false ? 0 : 1));
		b.rewind();
		return b;
	}

	private int varintSize(long data){
		
		// 7 bits -> 127
		if(data < (1L << 7)){
			return 1;
		}
		
		// 14 bits -> 16383
		if(data < (1L << 14)){
			return 2;
		}
		
		// 21 bits -> 2097151
		if(data < (1L << 21)){
			return 3;
		}
		
		// 28 bits -> 268435455
		if(data < (1L << 28)){
			return 4;
		}

		// 35 bits -> 34359738367
		if(data < (1L << 35)){
			return 5;
		}

		// 42 bits -> 4398046511103
		if(data < (1L << 42)){
			return 6;
		}
		
		// 49 bits -> 562949953421311
		if(data < (1L << 49)){
			return 7;
		}
		
		// 56 bits -> 72057594037927935
		if(data < (1L << 56)){
			return 8;
		}	
		
		return 9;
	}
	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeVarint(Object in) {
		long data = ((Number)in).longValue();
		
		// signed integers encoding
		// (n << 1) ^ (n >> 31)
		// OK but java's negative values are two's complements...
		
		return encodeUnsignedVarint(data>=0?(data<<1):((Math.abs(data) << 1) ^ 1));
	}

	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeVarintArray(Object in) {
		Object[] data = (Object[]) in;
		int[] sizes = new int[data.length];
		long[] zigzags = new long[data.length];
		int sumsizes=0;
		for (int i = 0; i < data.length; i++) {
			long datum = ((Number)data[i]).longValue();
			// signed integers encoding
			// (n << 1) ^ (n >> 31)
			// OK but java's negative values are two's complements...
			zigzags[i] = datum>0?(datum<<1):((Math.abs(datum) << 1) ^ 1);
			
			sizes[i] = varintSize(zigzags[i]);
			sumsizes+=sizes[i];
			//System.out.printf("i=%d, zigzag=%d, size=%d\n",i, zigzags[i], sizes[i]);
		}		
		
		// the size of the size!
		int ssize = varintSize(data.length);
		
		ByteBuffer b = ByteBuffer.allocate(ssize + sumsizes);
		
		putVarint(b, data.length, ssize);
		
		for (int i = 0; i < data.length; i++) {
			putVarint(b, zigzags[i], sizes[i]);
		}
		b.rewind();
		//outBuffer(b);
		return b;
	}
	
	/**
	 * @param in
	 * @return
	 */
	protected ByteBuffer encodeUnsignedVarint(Object in) {
		long data = ((Number)in).longValue();
		
		int size = varintSize(data);
		
		ByteBuffer buff = ByteBuffer.allocate(size);
		for(int i = 0; i < size; i++){
			int head=128;
			if(i==size-1) head = 0;
			long b = ((data >> (7*i)) & 127) ^ head;
			buff.put((byte)(b & 255 ));
		}
		buff.rewind();
		return  buff;
	}

	
	/**
	 * @param b
	 * @param sumsizes
	 * @param ssize
	 */
	private void putVarint(ByteBuffer buffer, long number, int byteSize) {
		for(int i = 0; i < byteSize; i++){
			int head=128;
			if(i==byteSize-1) head = 0;
			long b = ((number >> (7*i)) & 127) ^ head;
			buffer.put((byte)(b & 255 ));
		}
	}
	
	/**
	 * @param buff
	 */
	private void doSend(ByteBuffer buff) {

		if (socket.isClosed()) {
			System.err
					.println("NetStreamSender : can't send. The socket is closed.");
		} else {
			buff.rewind();
			//outBuffer(buff);
			ByteBuffer buffer = packer.packMessage(buff);
			ByteBuffer sizeBuffer = packer.packMessageSize(buffer.capacity());
	
			// real sending
			try {
				out.write(sizeBuffer.array(), 0, sizeBuffer.capacity());
				out.write(buffer.array(), 0, buffer.capacity());
				out.flush();
			} catch (IOException e) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				System.err.printf("socket error : %s\n", e.getMessage());
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
			sourceIdBuff = encodeString(sourceId);
			
		}
		ByteBuffer attrBuff = encodeString(attribute);
		int valueType = getType(value);
		ByteBuffer valueBuff = encodeValue(value, valueType);
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				attrBuff.capacity() + // attribute id
				1 + // attr type
				valueBuff.capacity()); // attr value
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		buff
			.put(streamBuffer)
			.put((byte) NetStreamConstants.EVENT_ADD_GRAPH_ATTR)
			.put(sourceIdBuff)
			.put(encodeUnsignedVarint(timeId))
			.put(attrBuff)
			.put((byte) valueType)
			.put(valueBuff);
		
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
			sourceIdBuff = encodeString(sourceId);
		}
		ByteBuffer attrBuff = encodeString(attribute);
		int oldValueType = getType(oldValue);
		int newValueType = getType(newValue);
		
		ByteBuffer oldValueBuff = encodeValue(oldValue, oldValueType);
		ByteBuffer newValueBuff = encodeValue(newValue, newValueType);
		
		
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				attrBuff.capacity() + // attribute id
				1 + // attr type
				oldValueBuff.capacity() + // attr value
				1 + // attr type
				newValueBuff.capacity()); // attr value
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		
		buff
			.put(streamBuffer)
			.put((byte) NetStreamConstants.EVENT_CHG_GRAPH_ATTR)
			.put(sourceIdBuff)
			.put(encodeUnsignedVarint(timeId))
			.put(attrBuff)
			.put((byte) oldValueType)
			.put(oldValueBuff)
			.put((byte) newValueType)
			.put(newValueBuff);

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
			sourceIdBuff = encodeString(sourceId);
		}
		ByteBuffer attrBuff = encodeString(attribute);

		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				attrBuff.capacity()
				); // attribute id
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
								
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_DEL_GRAPH_ATTR)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
		.put(attrBuff);

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
			sourceIdBuff = encodeString(sourceId);
		}
		ByteBuffer nodeBuff = encodeString(nodeId);
		ByteBuffer attrBuff = encodeString(attribute);
		int valueType = getType(value);
		ByteBuffer valueBuff = encodeValue(value, valueType);
		
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				nodeBuff.capacity() + // nodeId 
				attrBuff.capacity() + // attribute
				1 + // value type
				valueBuff.capacity() // value
		);
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		
		
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_ADD_NODE_ATTR)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
		.put(nodeBuff)
		.put(attrBuff)
		.put((byte) valueType)
		.put(valueBuff);

		
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
			sourceIdBuff = encodeString(sourceId);
		}
		
		ByteBuffer nodeBuff = encodeString(nodeId);
		ByteBuffer attrBuff = encodeString(attribute);
		
		int oldValueType = getType(oldValue);
		int newValueType = getType(newValue);
		
		ByteBuffer oldValueBuff = encodeValue(oldValue, oldValueType);
		ByteBuffer newValueBuff = encodeValue(newValue, newValueType);
		
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				nodeBuff.capacity() + // nodeId 
				attrBuff.capacity() + // attribute
				1 + // value type
				oldValueBuff.capacity() + // value
				1 + // value type
				newValueBuff.capacity() // value
		);
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		
		
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_CHG_NODE_ATTR)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
		.put(nodeBuff)
		.put(attrBuff)
		.put((byte) oldValueType)
		.put(oldValueBuff)
		.put((byte) newValueType)
		.put(newValueBuff);

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
			sourceIdBuff = encodeString(sourceId);
		}
		ByteBuffer nodeBuff = encodeString(nodeId);
		ByteBuffer attrBuff = encodeString(attribute);

		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				nodeBuff.capacity() + // nodeId 
				attrBuff.capacity() // attribute
		);
		
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		
		
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_DEL_NODE_ATTR)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
		.put(nodeBuff)
		.put(attrBuff);
		
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
			sourceIdBuff = encodeString(sourceId);
		}
		ByteBuffer edgeBuff = encodeString(edgeId);
		ByteBuffer attrBuff = encodeString(attribute);

		int valueType = getType(value);
		
		ByteBuffer valueBuff = encodeValue(value, valueType);
		
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				edgeBuff.capacity() + // nodeId 
				attrBuff.capacity() + // attribute
				1 + // value type
				valueBuff.capacity() // value
		);
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		
		
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_ADD_EDGE_ATTR)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
		.put(edgeBuff)
		.put(attrBuff)
		.put((byte) valueType) // value type
		.put(valueBuff);

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
			sourceIdBuff = encodeString(sourceId);
		}
		ByteBuffer edgeBuff = encodeString(edgeId);
		ByteBuffer attrBuff = encodeString(attribute);
		int oldValueType = getType(oldValue);
		int newValueType = getType(newValue);

		ByteBuffer oldValueBuff = encodeValue(oldValue, oldValueType);
		ByteBuffer newValueBuff = encodeValue(newValue, newValueType);
		
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				edgeBuff.capacity() + // nodeId 
				attrBuff.capacity() + // attribute
				1 + // value type
				oldValueBuff.capacity() + // value
				1 + // value type
				newValueBuff.capacity()  // value
		);

		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		
		
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_CHG_EDGE_ATTR)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
		.put(edgeBuff)
		.put(attrBuff)
		.put((byte) oldValueType)
		.put(oldValueBuff)
		.put((byte) newValueType)
		.put(newValueBuff);

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
			sourceIdBuff = encodeString(sourceId);
			}
		ByteBuffer edgeBuff = encodeString(edgeId);
		ByteBuffer attrBuff = encodeString(attribute);
		
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				edgeBuff.capacity() + // nodeId 
				attrBuff.capacity() // attribute
		);
		
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		

		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_DEL_EDGE_ATTR)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
		.put(edgeBuff)
		.put(attrBuff);
		

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
			sourceIdBuff = encodeString(sourceId);
		}
		ByteBuffer nodeBuff = encodeString(nodeId);
		
		
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				nodeBuff.capacity() // nodeId 
		);
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		
		
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_ADD_NODE)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
		.put(nodeBuff);
		
		
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
			sourceIdBuff = encodeString(sourceId);
		}
		ByteBuffer nodeBuff = encodeString(nodeId);
		
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				nodeBuff.capacity() // nodeId 
		);
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		
		
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_DEL_NODE)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
		.put(nodeBuff);
		
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
			sourceIdBuff = encodeString(sourceId);
		}
		ByteBuffer edgeBuff = encodeString(edgeId);
		ByteBuffer fromNodeBuff = encodeString(fromNodeId);
		ByteBuffer toNodeBuff = encodeString(toNodeId);
		
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				edgeBuff.capacity() + // edge
				fromNodeBuff.capacity() + // from nodeId
				toNodeBuff.capacity() + // to nodeId 
				1 // direction
		);
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		
		
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_ADD_EDGE)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
		.put(edgeBuff)
		.put(fromNodeBuff)
		.put(toNodeBuff)
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
			sourceIdBuff = encodeString(sourceId);
		}
		ByteBuffer edgeBuff = encodeString(edgeId);
		
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) + // timeId
				edgeBuff.capacity()  // edge
		);
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		
		
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_DEL_EDGE)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
		.put(edgeBuff);

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
			sourceIdBuff = encodeString(sourceId);
		}
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId)
		);
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
		
		
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_CLEARED)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId));

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
			sourceIdBuff = encodeString(sourceId);
		}
		
		ByteBuffer buff = ByteBuffer.allocate(
				streamBuffer.capacity() + // stream																			
				1 + // CMD
				sourceIdBuff.capacity() + // source id
				varintSize(timeId) +
				8 // time
		);
		
		streamBuffer.rewind();
		sourceIdBuff.rewind();
				
		buff
		.put(streamBuffer)
		.put((byte) NetStreamConstants.EVENT_STEP)
		.put(sourceIdBuff)
		.put(encodeUnsignedVarint(timeId))
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
