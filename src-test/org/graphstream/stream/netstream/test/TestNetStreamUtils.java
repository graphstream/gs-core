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
package org.graphstream.stream.netstream.test;

import static org.graphstream.stream.netstream.NetStreamUtils.*;

import org.graphstream.stream.netstream.NetStreamConstants;
import org.graphstream.stream.netstream.NetStreamUtils;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @since 23/01/16.
 */
public class TestNetStreamUtils {
	String randomChars = "abcdefghijklmnopqrstuvwxzyABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-*+_";

	protected String getRandomString(int size) {
		String s = "";

		for (int i = 0; i < size; i++) {
			int ind = (int) ((randomChars.length() - 1) * Math.random());
			s += randomChars.substring(ind, ind + 1);
		}

		return s;
	}

	@Test
	public void testVarintSize() {
		int p = 7;

		for (int i = 1; i < 9; i++) {
			long l = (1L << p) - 1;

			Assert.assertEquals(i, NetStreamUtils.getVarintSize(l));
			Assert.assertEquals(i + 1, NetStreamUtils.getVarintSize(l + 1));

			p += 7;
		}
	}

	@Test
	public void testEncodeDecodeString() {
		for (int i = 0; i < 100; i++) {
			String s = getRandomString(64);
			ByteBuffer bb = NetStreamUtils.encodeString(s);
			String r = NetStreamUtils.decodeString(bb);

			Assert.assertEquals(s, r);
		}
	}

	@Test
	public void testEncodeStringArray() {
		String[] strings = {"OK", "notOK"};
		ByteBuffer buffer = encodeStringArray(strings);
		String[] decodedStrings = decodeStringArray(buffer);
		for (int i = 0 ; i < strings.length ; i++) {
			Assert.assertEquals(strings[i], decodedStrings[i]);
		}
	}

	@Test
	public void testGetValueType() {
		Assert.assertEquals(NetStreamConstants.TYPE_ARRAY, getType(new Object[] {}));
		Assert.assertEquals(NetStreamConstants.TYPE_BOOLEAN, getType(true));
		Assert.assertEquals(NetStreamConstants.TYPE_BOOLEAN_ARRAY, getType(new boolean[] { true, false }));
		Assert.assertEquals(NetStreamConstants.TYPE_BOOLEAN_ARRAY, getType(new Boolean[] { true, false }));
		Assert.assertEquals(NetStreamConstants.TYPE_BYTE, getType((byte) 0x0A));
		Assert.assertEquals(NetStreamConstants.TYPE_BYTE_ARRAY, getType(new byte[] { 0x0B }));
		Assert.assertEquals(NetStreamConstants.TYPE_BYTE_ARRAY, getType(new Byte[] { 0x0B }));
		Assert.assertEquals(NetStreamConstants.TYPE_DOUBLE, getType(3.14));
		Assert.assertEquals(NetStreamConstants.TYPE_DOUBLE_ARRAY, getType(new double[] { 3.14 }));
		Assert.assertEquals(NetStreamConstants.TYPE_DOUBLE_ARRAY, getType(new Double[] { 3.14 }));
		Assert.assertEquals(NetStreamConstants.TYPE_FLOAT, getType(3.14f));
		Assert.assertEquals(NetStreamConstants.TYPE_FLOAT_ARRAY, getType(new float[] { 3.14f }));
		Assert.assertEquals(NetStreamConstants.TYPE_FLOAT_ARRAY, getType(new Float[] { 3.14f }));
		Assert.assertEquals(NetStreamConstants.TYPE_INT, getType(314));
		Assert.assertEquals(NetStreamConstants.TYPE_INT_ARRAY, getType(new int[] { 314 }));
		Assert.assertEquals(NetStreamConstants.TYPE_INT_ARRAY, getType(new Integer[] { 314 }));
		Assert.assertEquals(NetStreamConstants.TYPE_LONG, getType(314L));
		Assert.assertEquals(NetStreamConstants.TYPE_LONG_ARRAY, getType(new long[] { 314L }));
		Assert.assertEquals(NetStreamConstants.TYPE_LONG_ARRAY, getType(new Long[] { 314L }));
		Assert.assertEquals(NetStreamConstants.TYPE_NULL, getType(null));
		Assert.assertEquals(NetStreamConstants.TYPE_SHORT, getType((short) 314));
		Assert.assertEquals(NetStreamConstants.TYPE_SHORT_ARRAY, getType(new short[] { 314 }));
		Assert.assertEquals(NetStreamConstants.TYPE_SHORT_ARRAY, getType(new Short[] { 314 }));
		Assert.assertEquals(NetStreamConstants.TYPE_STRING, getType(getRandomString(16)));
		Assert.assertEquals(NetStreamConstants.TYPE_STRING_ARRAY, getType(new String[] {getRandomString(16),getRandomString(11)}));
		Assert.assertEquals(NetStreamConstants.TYPE_UNKNOWN, getType(this));
	}

	@Test
	public void testEncodeFloat() {
		float f = 3.14f;

		ByteBuffer buffer = encodeFloat(f);
		buffer.rewind();

		float r = decodeFloat(buffer);

		Assert.assertEquals(f, r, 0);
	}

	@Test
	public void testEncodeDouble() {
		double d = 3.14;

		ByteBuffer buffer = encodeDouble(d);
		buffer.rewind();

		double r = decodeDouble(buffer);

		Assert.assertEquals(d, r, 0);
	}

	@Test
	public void testEncodeInt() {
		int i = 314;

		ByteBuffer buffer = encodeInt(i);
		buffer.rewind();

		int r = decodeInt(buffer);

		Assert.assertEquals(i, r);
	}

	@Test
	public void testEncodeLong() {
		long i = 314L;

		ByteBuffer buffer = encodeLong(i);
		buffer.rewind();

		long r = decodeLong(buffer);

		Assert.assertEquals(i, r);
	}

	@Test
	public void testEncodeShort() {
		short i = 314;

		ByteBuffer buffer = encodeShort(i);
		buffer.rewind();

		short r = decodeShort(buffer);

		Assert.assertEquals(i, r);
	}

	@Test
	public void testEncodeByte() {
		byte i = (byte) 0x0A;

		ByteBuffer buffer = encodeByte(i);
		buffer.rewind();

		byte r = decodeByte(buffer);

		Assert.assertEquals(i, r);
	}

	@Test
	public void testEncodeBoolean() {
		boolean i = true;

		ByteBuffer buffer = encodeBoolean(i);
		buffer.rewind();

		boolean r = decodeBoolean(buffer);

		Assert.assertEquals(i, r);
	}
}
