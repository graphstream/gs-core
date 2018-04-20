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
package org.graphstream.stream.netstream;

import org.graphstream.stream.binary.ByteDecoder;
import org.graphstream.stream.binary.ByteEncoder;
import org.graphstream.stream.binary.ByteFactory;

import java.lang.reflect.Array;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.logging.Logger;

/**
 * @since 22/01/16.
 */
public class NetStreamUtils {
	private static ByteBuffer NULL_BUFFER = ByteBuffer.allocate(0);
	private static final Logger LOGGER = Logger.getLogger(NetStreamUtils.class.getName());

	public static ByteFactory getDefaultNetStreamFactory() {
		return new ByteFactory() {
			@Override
			public ByteEncoder createByteEncoder() {
				return new NetStreamEncoder();
			}

			@Override
			public ByteDecoder createByteDecoder() {
				return new NetStreamDecoder();
			}
		};
	}

	public static int getType(Object value) {
		int valueType = NetStreamConstants.TYPE_UNKNOWN;

		if (value == null)
			return NetStreamConstants.TYPE_NULL;

		Class<?> valueClass = value.getClass();
		boolean isArray = valueClass.isArray();
		if (isArray) {
			if (Array.getLength(value) > 0) {
				valueClass = Array.get(value, 0).getClass();
			} else {
				return NetStreamConstants.TYPE_ARRAY;
			}
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
				valueType = NetStreamConstants.TYPE_STRING_ARRAY;
			} else {
				valueType = NetStreamConstants.TYPE_STRING;
			}
		} else
			LOGGER.warning(String.format("can not find type of %s.", valueClass));

		return valueType;
	}

	public static int getVarintSize(long data) {
		// 7 bits -> 127
		if (data < (1L << 7)) {
			return 1;
		}

		// 14 bits -> 16383
		if (data < (1L << 14)) {
			return 2;
		}

		// 21 bits -> 2097151
		if (data < (1L << 21)) {
			return 3;
		}

		// 28 bits -> 268435455
		if (data < (1L << 28)) {
			return 4;
		}

		// 35 bits -> 34359738367
		if (data < (1L << 35)) {
			return 5;
		}

		// 42 bits -> 4398046511103
		if (data < (1L << 42)) {
			return 6;
		}

		// 49 bits -> 562949953421311
		if (data < (1L << 49)) {
			return 7;
		}

		// 56 bits -> 72057594037927935
		if (data < (1L << 56)) {
			return 8;
		}

		return 9;
	}

	public static void putVarint(ByteBuffer buffer, long number, int byteSize) {
		for (int i = 0; i < byteSize; i++) {
			int head = 128;
			if (i == byteSize - 1)
				head = 0;
			long b = ((number >> (7 * i)) & 127) ^ head;
			buffer.put((byte) (b & 255));
		}
	}

	//
	// ENCODING METHODS
	//

	public static ByteBuffer encodeValue(Object in, int valueType) {
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
		} else if (NetStreamConstants.TYPE_STRING_ARRAY == valueType) {
			return encodeStringArray(in);
		} else if (NetStreamConstants.TYPE_ARRAY == valueType) {
			return encodeArray(in);
		} else if (NetStreamConstants.TYPE_NULL == valueType) {
			return NULL_BUFFER;
		}

		return null;
	}

	public static ByteBuffer encodeUnsignedVarint(Object in) {
		long data = ((Number) in).longValue();
		int size = getVarintSize(data);

		ByteBuffer buff = ByteBuffer.allocate(size);
		for (int i = 0; i < size; i++) {
			int head = 128;
			if (i == size - 1)
				head = 0;
			long b = ((data >> (7 * i)) & 127) ^ head;
			buff.put((byte) (b & 255));
		}
		buff.rewind();

		return buff;
	}

	public static ByteBuffer encodeVarint(Object in) {
		long data = ((Number) in).longValue();

		// signed integers encoding
		// (n << 1) ^ (n >> 31)
		// OK but java's negative values are two's complements...

		return encodeUnsignedVarint(data >= 0 ? (data << 1) : ((Math.abs(data) << 1) ^ 1));
	}

	public static ByteBuffer encodeString(Object in) {
		String s = (String) in;
		byte[] data = s.getBytes(Charset.forName("UTF-8"));

		ByteBuffer lenBuff = encodeUnsignedVarint(data.length);
		// outBuffer(lenBuff);
		ByteBuffer bb = ByteBuffer.allocate(lenBuff.capacity() + data.length);
		bb.put(lenBuff).put(data);
		bb.rewind();
		// outBuffer(bb);

		return bb;
	}

	public static ByteBuffer encodeArray(Object in) {
		// TODO...
		return null;
	}

	public static ByteBuffer encodeDoubleArray(Object in) {
		Object[] data = (Object[]) in;

		int ssize = getVarintSize(data.length);

		ByteBuffer b = ByteBuffer.allocate(ssize + data.length * 8);

		putVarint(b, data.length, ssize);

		for (int i = 0; i < data.length; i++) {
			b.putDouble((Double) data[i]);
		}
		b.rewind();
		return b;
	}

	public static ByteBuffer encodeStringArray(Object in) {
		Object[] data = (Object[]) in;

		int ssize = getVarintSize(data.length);

		byte[][] dataArray = new byte[data.length][];
		ByteBuffer[] lenBuffArray = new ByteBuffer[data.length];
		int bufferSize = 0;
		for(int i = 0; i < data.length; i++){
			byte[] bs = ((String)data[i]).getBytes(Charset.forName("UTF-8"));
			dataArray[i] = bs;

			ByteBuffer lenBuff = encodeUnsignedVarint(bs.length);
			lenBuffArray[i] = lenBuff;

			bufferSize += lenBuff.capacity() +bs.length;
		}


		ByteBuffer bb = ByteBuffer.allocate(ssize + bufferSize);

		putVarint(bb, data.length, ssize);

		for(int i = 0; i < data.length; i++) {
			bb.put(lenBuffArray[i]).put(dataArray[i]);
		}
		bb.rewind();

		return bb;
	}

	/**
	 * @param in
	 *            The double to encode
	 * @return ByteBuffer with encoded double in it
	 */
	public static ByteBuffer encodeDouble(Object in) {
		ByteBuffer bb = ByteBuffer.allocate(8).putDouble((Double) in);
		bb.rewind();
		return bb;
	}

	/**
	 * @param in
	 *            The float array to encode
	 * @return ByteBuffer with encoded float array in it
	 */
	public static ByteBuffer encodeFloatArray(Object in) {
		Object[] data = (Object[]) in;

		int ssize = getVarintSize(data.length);

		ByteBuffer b = ByteBuffer.allocate(ssize + data.length * 4);

		putVarint(b, data.length, ssize);

		for (int i = 0; i < data.length; i++) {
			b.putFloat((Float) data[i]);
		}
		b.rewind();
		return b;
	}

	/**
	 * @param in
	 *            The float to encode
	 * @return ByteBuffer with encoded float in it
	 */
	public static ByteBuffer encodeFloat(Object in) {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putFloat(((Float) in));
		b.rewind();
		return b;
	}

	/**
	 * @param in
	 *            The long array to encode
	 * @return ByteBuffer with encoded long array in it
	 */
	public static ByteBuffer encodeLongArray(Object in) {
		return encodeVarintArray(in);
	}

	/**
	 * @param in
	 *            The long to encode
	 * @return ByteBuffer with encoded long in it
	 */
	public static ByteBuffer encodeLong(Object in) {
		return encodeVarint(in);
	}

	/**
	 * @param in
	 *            The integer array to encode
	 * @return ByteBuffer with encoded integer array in it
	 */
	public static ByteBuffer encodeIntArray(Object in) {
		return encodeVarintArray(in);
	}

	/**
	 * @param in
	 *            The integer to encode
	 * @return ByteBuffer with encoded integer in it
	 */
	public static ByteBuffer encodeInt(Object in) {
		return encodeVarint(in);
	}

	/**
	 * @param in
	 * @return
	 */
	public static ByteBuffer encodeShortArray(Object in) {
		return encodeVarintArray(in);
	}

	/**
	 * @param in
	 * @return
	 */
	public static ByteBuffer encodeShort(Object in) {
		return encodeVarint(in);
	}

	/**
	 * @param in
	 * @return
	 */
	public static ByteBuffer encodeByteArray(Object in) {
		Object[] data = (Object[]) in;

		int ssize = getVarintSize(data.length);

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
	public static ByteBuffer encodeByte(Object in) {
		ByteBuffer b = ByteBuffer.allocate(1);
		b.put(((Byte) in));
		b.rewind();
		return b;
	}

	/**
	 * @param in
	 * @return
	 */
	public static ByteBuffer encodeBooleanArray(Object in) {
		Object[] data = (Object[]) in;

		int ssize = getVarintSize(data.length);

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
	public static ByteBuffer encodeBoolean(Object in) {
		ByteBuffer b = ByteBuffer.allocate(1);
		b.put((byte) (((Boolean) in) == false ? 0 : 1));
		b.rewind();
		return b;
	}

	public static ByteBuffer encodeVarintArray(Object in) {
		Object[] data = (Object[]) in;
		int[] sizes = new int[data.length];
		long[] zigzags = new long[data.length];
		int sumsizes = 0;
		for (int i = 0; i < data.length; i++) {
			long datum = ((Number) data[i]).longValue();
			// signed integers encoding
			// (n << 1) ^ (n >> 31)
			// OK but java's negative values are two's complements...
			zigzags[i] = datum > 0 ? (datum << 1) : ((Math.abs(datum) << 1) ^ 1);

			sizes[i] = getVarintSize(zigzags[i]);
			sumsizes += sizes[i];
			// System.out.printf("i=%d, zigzag=%d, size=%d\n",i, zigzags[i], sizes[i]);
		}

		// the size of the size!
		int ssize = getVarintSize(data.length);

		ByteBuffer b = ByteBuffer.allocate(ssize + sumsizes);

		putVarint(b, data.length, ssize);

		for (int i = 0; i < data.length; i++) {
			putVarint(b, zigzags[i], sizes[i]);
		}
		b.rewind();
		// outBuffer(b);
		return b;
	}

	//
	// DECODING METHODS
	//

	/**
	 * @param bb
	 * @return
	 */
	public static int decodeType(ByteBuffer bb) {
		try {
			return bb.get();
		} catch (BufferUnderflowException e) {
			LOGGER.info("decodeType: could not decode type");
			e.printStackTrace();
		}

		return 0;
	}

	public static Object decodeValue(ByteBuffer bb, int valueType) {
		if (NetStreamConstants.TYPE_BOOLEAN == valueType) {
			return decodeBoolean(bb);
		} else if (NetStreamConstants.TYPE_BOOLEAN_ARRAY == valueType) {
			return decodeBooleanArray(bb);
		} else if (NetStreamConstants.TYPE_BYTE == valueType) {
			return decodeByte(bb);
		} else if (NetStreamConstants.TYPE_BYTE_ARRAY == valueType) {
			return decodeByteArray(bb);
		} else if (NetStreamConstants.TYPE_SHORT == valueType) {
			return decodeShort(bb);
		} else if (NetStreamConstants.TYPE_SHORT_ARRAY == valueType) {
			return decodeShortArray(bb);
		} else if (NetStreamConstants.TYPE_INT == valueType) {
			return decodeInt(bb);
		} else if (NetStreamConstants.TYPE_INT_ARRAY == valueType) {
			return decodeIntArray(bb);
		} else if (NetStreamConstants.TYPE_LONG == valueType) {
			return decodeLong(bb);
		} else if (NetStreamConstants.TYPE_LONG_ARRAY == valueType) {
			return decodeLongArray(bb);
		} else if (NetStreamConstants.TYPE_FLOAT == valueType) {
			return decodeFloat(bb);
		} else if (NetStreamConstants.TYPE_FLOAT_ARRAY == valueType) {
			return decodeFloatArray(bb);
		} else if (NetStreamConstants.TYPE_DOUBLE == valueType) {
			return decodeDouble(bb);
		} else if (NetStreamConstants.TYPE_DOUBLE_ARRAY == valueType) {
			return decodeDoubleArray(bb);
		} else if (NetStreamConstants.TYPE_STRING == valueType) {
			return decodeString(bb);
		} else if (NetStreamConstants.TYPE_STRING_ARRAY == valueType) {
			return decodeStringArray(bb);
		} else if (NetStreamConstants.TYPE_ARRAY == valueType) {
			return decodeArray(bb);
		}
		return null;
	}

	/**
	 * @param bb
	 * @return
	 */
	public static Object[] decodeArray(ByteBuffer bb) {

		int len = (int) decodeUnsignedVarint(bb);

		Object[] array = new Object[len];
		for (int i = 0; i < len; i++) {
			array[i] = decodeValue(bb, decodeType(bb));
		}
		return array;

	}

	public static String decodeString(ByteBuffer bb) {
		try {
			int len = (int) decodeUnsignedVarint(bb);
			byte[] data = new byte[len];

			bb.get(data);

			return new String(data, Charset.forName("UTF-8"));
		} catch (BufferUnderflowException e) {
			LOGGER.info("decodeString: could not decode string");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] decodeStringArray(ByteBuffer bb) {
		int len = (int) decodeUnsignedVarint(bb);
		String[] array = new String[len];
		for (int i = 0; i < len; i++) {
			array[i] = decodeString(bb);
		}
		return array;
	}

	public static Boolean decodeBoolean(ByteBuffer bb) {
		int data = 0;

		try {
			data = bb.get();
		} catch (BufferUnderflowException e) {
			LOGGER.info("decodeByte: could not decode");
			e.printStackTrace();
		}

		return data != 0;
	}

	public static Byte decodeByte(ByteBuffer bb) {
		byte data = 0;

		try {
			data = bb.get();
		} catch (BufferUnderflowException e) {
			LOGGER.info("decodeByte: could not decode");
			e.printStackTrace();
		}

		return data;
	}

	public static long decodeUnsignedVarint(ByteBuffer bb) {
		try {
			int size = 0;
			long[] data = new long[9];

			do {
				data[size] = bb.get();

				size++;

				// int bt =data[size-1];
				// if (bt < 0) bt = (bt & 127) + (bt & 128);
				// System.out.println("test "+bt+" -> "+(data[size - 1]& 128) );
			} while ((data[size - 1] & 128) == 128);
			long number = 0;

			for (int i = 0; i < size; i++) {
				number ^= (data[i] & 127L) << (i * 7L);
			}

			return number;

		} catch (BufferUnderflowException e) {
			LOGGER.info("decodeUnsignedVarintFromInteger: could not decode");
			e.printStackTrace();
		}

		return 0L;
	}

	public static long decodeVarint(ByteBuffer bb) {
		long number = decodeUnsignedVarint(bb);
		return ((number & 1) == 0) ? number >> 1 : -(number >> 1);
	}

	public static Short decodeShort(ByteBuffer bb) {
		return (short) decodeVarint(bb);
	}

	public static Integer decodeInt(ByteBuffer bb) {
		return (int) decodeVarint(bb);
	}

	public static Long decodeLong(ByteBuffer bb) {
		return decodeVarint(bb);
	}

	public static Float decodeFloat(ByteBuffer bb) {
		return bb.getFloat();
	}

	public static Double decodeDouble(ByteBuffer bb) {
		return bb.getDouble();
	}

	public static Integer[] decodeIntArray(ByteBuffer bb) {
		int len = (int) decodeUnsignedVarint(bb);

		Integer[] res = new Integer[len];
		for (int i = 0; i < len; i++) {
			res[i] = (int) decodeVarint(bb);
			// System.out.printf("array[%d]=%d%n",i,res[i]);
		}

		return res;
	}

	public static Boolean[] decodeBooleanArray(ByteBuffer bb) {
		try {
			int len = (int) decodeUnsignedVarint(bb);
			Boolean[] res = new Boolean[len];

			for (int i = 0; i < len; i++) {
				byte b = bb.get();
				res[i] = b != 0;
			}

			return res;
		} catch (BufferUnderflowException e) {
			LOGGER.info("decodeBooleanArray: could not decode array");
			e.printStackTrace();
		}

		return null;
	}

	public static Byte[] decodeByteArray(ByteBuffer bb) {
		try {
			int len = (int) decodeUnsignedVarint(bb);
			Byte[] res = new Byte[len];

			for (int i = 0; i < len; i++) {
				res[i] = bb.get();
			}

			return res;
		} catch (BufferUnderflowException e) {
			LOGGER.info("decodeBooleanArray: could not decode array");
			e.printStackTrace();
		}

		return null;
	}

	public static Double[] decodeDoubleArray(ByteBuffer bb) {
		try {
			int len = (int) decodeUnsignedVarint(bb);
			Double[] res = new Double[len];

			for (int i = 0; i < len; i++) {
				res[i] = bb.getDouble();
			}

			return res;
		} catch (BufferUnderflowException e) {
			LOGGER.info("decodeDoubleArray: could not decode array");
			e.printStackTrace();
		}

		return null;
	}

	public static Float[] decodeFloatArray(ByteBuffer bb) {
		try {
			int len = (int) decodeUnsignedVarint(bb);
			Float[] res = new Float[len];

			for (int i = 0; i < len; i++) {
				res[i] = bb.getFloat();
			}

			return res;
		} catch (BufferUnderflowException e) {
			LOGGER.info("decodeFloatArray: could not decode array");
			e.printStackTrace();
		}

		return null;
	}

	public static Long[] decodeLongArray(ByteBuffer bb) {
		int len = (int) decodeUnsignedVarint(bb);
		Long[] res = new Long[len];

		for (int i = 0; i < len; i++) {
			res[i] = decodeVarint(bb);
		}

		return res;
	}

	public static Short[] decodeShortArray(ByteBuffer bb) {
		int len = (int) decodeUnsignedVarint(bb);
		Short[] res = new Short[len];

		for (int i = 0; i < len; i++) {
			res[i] = (short) decodeVarint(bb);
		}

		return res;
	}
}
