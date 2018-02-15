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

import org.graphstream.stream.binary.ByteEncoder;
import org.graphstream.stream.netstream.NetStreamConstants;
import org.graphstream.stream.netstream.NetStreamEncoder;

import static org.graphstream.stream.netstream.NetStreamUtils.*;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @since 22/01/16.
 */
public class TestNetStreamEncoder {
	@Test
	public void testEventNodeAdded() {
		final String streamId = "stream-test";
		final String sourceId = "test";
		final String nodeId = "node-test";
		final long timeId = 123;

		NetStreamEncoder enc = new NetStreamEncoder("stream-test", new ByteEncoder.Transport() {
			@Override
			public void send(ByteBuffer buffer) {
				byte[] streamBytes = streamId.getBytes(Charset.forName("UTF-8"));
				byte[] sourceBytes = sourceId.getBytes(Charset.forName("UTF-8"));
				byte[] nodeBytes = nodeId.getBytes(Charset.forName("UTF-8"));

				int expectedSize = 4 + getVarintSize(streamBytes.length) + streamBytes.length
						+ getVarintSize(sourceBytes.length) + sourceBytes.length + 1 + getVarintSize(123)
						+ getVarintSize(nodeBytes.length) + nodeBytes.length;

				Assert.assertEquals(expectedSize, buffer.limit());
				Assert.assertEquals(expectedSize, buffer.getInt());

				Assert.assertEquals(streamId, decodeString(buffer));
				Assert.assertEquals(NetStreamConstants.EVENT_ADD_NODE, buffer.get());
				Assert.assertEquals(sourceId, decodeString(buffer));
				Assert.assertEquals(timeId, decodeUnsignedVarint(buffer));
				Assert.assertEquals(nodeId, decodeString(buffer));

				Assert.assertEquals(0, buffer.remaining());
			}
		});

		enc.nodeAdded(sourceId, timeId, nodeId);
	}

	@Test
	public void testEventNodeRemoved() {
		final String streamId = "stream-test";
		final String sourceId = "test";
		final String nodeId = "node-test";
		final long timeId = 123;

		NetStreamEncoder enc = new NetStreamEncoder("stream-test", new ByteEncoder.Transport() {
			@Override
			public void send(ByteBuffer buffer) {
				byte[] streamBytes = streamId.getBytes(Charset.forName("UTF-8"));
				byte[] sourceBytes = sourceId.getBytes(Charset.forName("UTF-8"));
				byte[] nodeBytes = nodeId.getBytes(Charset.forName("UTF-8"));

				int expectedSize = 4 + getVarintSize(streamBytes.length) + streamBytes.length
						+ getVarintSize(sourceBytes.length) + sourceBytes.length + 1 + getVarintSize(123)
						+ getVarintSize(nodeBytes.length) + nodeBytes.length;

				Assert.assertEquals(expectedSize, buffer.limit());
				Assert.assertEquals(expectedSize, buffer.getInt());

				Assert.assertEquals(streamId, decodeString(buffer));
				Assert.assertEquals(NetStreamConstants.EVENT_DEL_NODE, buffer.get());
				Assert.assertEquals(sourceId, decodeString(buffer));
				Assert.assertEquals(timeId, decodeUnsignedVarint(buffer));
				Assert.assertEquals(nodeId, decodeString(buffer));

				Assert.assertEquals(0, buffer.remaining());
			}
		});

		enc.nodeRemoved(sourceId, timeId, nodeId);
	}

	@Test
	public void testEventEdgeAdded() {
		final String streamId = "stream-test";
		final String sourceId = "test";
		final String edgeId = "edge-test";
		final String nodeA = "node-a";
		final String nodeB = "node-b";
		final long timeId = 123;

		NetStreamEncoder enc = new NetStreamEncoder("stream-test", new ByteEncoder.Transport() {
			@Override
			public void send(ByteBuffer buffer) {
				byte[] streamBytes = streamId.getBytes(Charset.forName("UTF-8"));
				byte[] sourceBytes = sourceId.getBytes(Charset.forName("UTF-8"));
				byte[] edgeBytes = edgeId.getBytes(Charset.forName("UTF-8"));
				byte[] nodeABytes = nodeA.getBytes(Charset.forName("UTF-8"));
				byte[] nodeBBytes = nodeB.getBytes(Charset.forName("UTF-8"));

				int expectedSize = 4 + getVarintSize(streamBytes.length) + streamBytes.length
						+ getVarintSize(sourceBytes.length) + sourceBytes.length + 1 + getVarintSize(123)
						+ getVarintSize(edgeBytes.length) + edgeBytes.length + getVarintSize(nodeABytes.length)
						+ nodeABytes.length + getVarintSize(nodeBBytes.length) + nodeBBytes.length + 1;

				Assert.assertEquals(expectedSize, buffer.limit());
				Assert.assertEquals(expectedSize, buffer.getInt());

				Assert.assertEquals(streamId, decodeString(buffer));
				Assert.assertEquals(NetStreamConstants.EVENT_ADD_EDGE, buffer.get());
				Assert.assertEquals(sourceId, decodeString(buffer));
				Assert.assertEquals(timeId, decodeUnsignedVarint(buffer));
				Assert.assertEquals(edgeId, decodeString(buffer));
				Assert.assertEquals(nodeA, decodeString(buffer));
				Assert.assertEquals(nodeB, decodeString(buffer));
				Assert.assertEquals(1, buffer.get());

				Assert.assertEquals(0, buffer.remaining());
			}
		});

		enc.edgeAdded(sourceId, timeId, edgeId, nodeA, nodeB, true);
	}

	@Test
	public void testEventEdgeRemoved() {
		final String streamId = "stream-test";
		final String sourceId = "test";
		final String edgeId = "edge-test";
		final long timeId = 123;

		NetStreamEncoder enc = new NetStreamEncoder("stream-test", new ByteEncoder.Transport() {
			@Override
			public void send(ByteBuffer buffer) {
				byte[] streamBytes = streamId.getBytes(Charset.forName("UTF-8"));
				byte[] sourceBytes = sourceId.getBytes(Charset.forName("UTF-8"));
				byte[] edgeBytes = edgeId.getBytes(Charset.forName("UTF-8"));

				int expectedSize = 4 + getVarintSize(streamBytes.length) + streamBytes.length
						+ getVarintSize(sourceBytes.length) + sourceBytes.length + 1 + getVarintSize(123)
						+ getVarintSize(edgeBytes.length) + edgeBytes.length;

				Assert.assertEquals(expectedSize, buffer.limit());
				Assert.assertEquals(expectedSize, buffer.getInt());

				Assert.assertEquals(streamId, decodeString(buffer));
				Assert.assertEquals(NetStreamConstants.EVENT_DEL_EDGE, buffer.get());
				Assert.assertEquals(sourceId, decodeString(buffer));
				Assert.assertEquals(timeId, decodeUnsignedVarint(buffer));
				Assert.assertEquals(edgeId, decodeString(buffer));

				Assert.assertEquals(0, buffer.remaining());
			}
		});

		enc.edgeRemoved(sourceId, timeId, edgeId);
	}

	@Test
	public void testEventGraphCleared() {
		final String streamId = "stream-test";
		final String sourceId = "test";
		final long timeId = 123;

		NetStreamEncoder enc = new NetStreamEncoder("stream-test", new ByteEncoder.Transport() {
			@Override
			public void send(ByteBuffer buffer) {
				byte[] streamBytes = streamId.getBytes(Charset.forName("UTF-8"));
				byte[] sourceBytes = sourceId.getBytes(Charset.forName("UTF-8"));

				int expectedSize = 4 + getVarintSize(streamBytes.length) + streamBytes.length
						+ getVarintSize(sourceBytes.length) + sourceBytes.length + 1 + getVarintSize(123);

				Assert.assertEquals(expectedSize, buffer.limit());
				Assert.assertEquals(expectedSize, buffer.getInt());

				Assert.assertEquals(streamId, decodeString(buffer));
				Assert.assertEquals(NetStreamConstants.EVENT_CLEARED, buffer.get());
				Assert.assertEquals(sourceId, decodeString(buffer));
				Assert.assertEquals(timeId, decodeUnsignedVarint(buffer));

				Assert.assertEquals(0, buffer.remaining());
			}
		});

		enc.graphCleared(sourceId, timeId);
	}
}
