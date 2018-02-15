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

import org.graphstream.stream.Sink;
import org.graphstream.stream.binary.ByteEncoder;
import org.graphstream.stream.netstream.NetStreamDecoder;
import org.graphstream.stream.netstream.NetStreamEncoder;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @since 01/02/16.
 */
public class TestNetStreamDecoder {

	@Test
	public void testEventNodeAdded() {
		final String streamId = "stream-test";
		final String sourceId = "test";
		final String nodeId = "node-test";
		final long timeId = 123;

		final NetStreamDecoder dec = new NetStreamDecoder();

		NetStreamEncoder enc = new NetStreamEncoder("stream-test", new ByteEncoder.Transport() {
			@Override
			public void send(ByteBuffer buffer) {
				dec.decode(buffer);
			}
		});

		FailSink sink = new FailSink() {
			public void nodeAdded(String sourceIdDec, long timeIdDec, String nodeIdDec) {
				triggered = true;

				Assert.assertEquals(sourceId, sourceIdDec);
				Assert.assertEquals(timeId, timeIdDec);
				Assert.assertEquals(nodeId, nodeIdDec);
			}
		};

		dec.addSink(sink);

		enc.nodeAdded(sourceId, timeId, nodeId);

		Assert.assertTrue(sink.triggered);
	}

	@Test
	public void testEventNodeRemoved() {
		final String streamId = "stream-test";
		final String sourceId = "test";
		final String nodeId = "node-test";
		final long timeId = 123;

		final NetStreamDecoder dec = new NetStreamDecoder();

		NetStreamEncoder enc = new NetStreamEncoder("stream-test", new ByteEncoder.Transport() {
			@Override
			public void send(ByteBuffer buffer) {
				dec.decode(buffer);
			}
		});

		FailSink sink = new FailSink() {
			public void nodeRemoved(String sourceIdDec, long timeIdDec, String nodeIdDec) {
				triggered = true;

				Assert.assertEquals(sourceId, sourceIdDec);
				Assert.assertEquals(timeId, timeIdDec);
				Assert.assertEquals(nodeId, nodeIdDec);
			}
		};

		dec.addSink(sink);

		enc.nodeRemoved(sourceId, timeId, nodeId);

		Assert.assertTrue(sink.triggered);
	}

	@Test
	public void testEventEdgeAdded() {
		final String streamId = "stream-test";
		final String sourceId = "test";
		final String edgeId = "edge-test";
		final String nodeA = "node-a";
		final String nodeB = "node-b";
		final long timeId = 123;

		final NetStreamDecoder dec = new NetStreamDecoder();

		NetStreamEncoder enc = new NetStreamEncoder("stream-test", new ByteEncoder.Transport() {
			@Override
			public void send(ByteBuffer buffer) {
				dec.decode(buffer);
			}
		});

		FailSink sink = new FailSink() {
			public void edgeAdded(String sourceIdDec, long timeIdDec, String edgeIdDec, String fromNodeId,
					String toNodeId, boolean directed) {
				triggered = true;

				Assert.assertEquals(sourceId, sourceIdDec);
				Assert.assertEquals(timeId, timeIdDec);
				Assert.assertEquals(edgeId, edgeIdDec);
				Assert.assertEquals(nodeA, fromNodeId);
				Assert.assertEquals(nodeB, toNodeId);
				Assert.assertEquals(true, directed);
			}
		};

		dec.addSink(sink);

		enc.edgeAdded(sourceId, timeId, edgeId, nodeA, nodeB, true);

		Assert.assertTrue(sink.triggered);
	}

	@Test
	public void testEventEdgeRemoved() {
		final String streamId = "stream-test";
		final String sourceId = "test";
		final String edgeId = "edge-test";
		final long timeId = 123;

		final NetStreamDecoder dec = new NetStreamDecoder();

		NetStreamEncoder enc = new NetStreamEncoder("stream-test", new ByteEncoder.Transport() {
			@Override
			public void send(ByteBuffer buffer) {
				dec.decode(buffer);
			}
		});

		FailSink sink = new FailSink() {
			public void edgeRemoved(String sourceIdDec, long timeIdDec, String edgeIdDec) {
				triggered = true;

				Assert.assertEquals(sourceId, sourceIdDec);
				Assert.assertEquals(timeId, timeIdDec);
				Assert.assertEquals(edgeId, edgeIdDec);
			}
		};

		dec.addSink(sink);

		enc.edgeRemoved(sourceId, timeId, edgeId);

		Assert.assertTrue(sink.triggered);
	}

	@Test
	public void testEventGraphCleared() {
		final String streamId = "stream-test";
		final String sourceId = "test";
		final long timeId = 123;

		final NetStreamDecoder dec = new NetStreamDecoder();

		NetStreamEncoder enc = new NetStreamEncoder("stream-test", new ByteEncoder.Transport() {
			@Override
			public void send(ByteBuffer buffer) {
				dec.decode(buffer);
			}
		});

		FailSink sink = new FailSink() {
			public void graphCleared(String sourceIdDec, long timeIdDec) {
				triggered = true;

				Assert.assertEquals(sourceId, sourceIdDec);
				Assert.assertEquals(timeId, timeIdDec);
			}
		};

		dec.addSink(sink);

		enc.graphCleared(sourceId, timeId);

		Assert.assertTrue(sink.triggered);
	}

	class FailSink implements Sink {
		boolean triggered = false;

		@Override
		public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
			Assert.fail();
		}

		@Override
		public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
				Object newValue) {
			Assert.fail();
		}

		@Override
		public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
			Assert.fail();
		}

		@Override
		public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
			Assert.fail();
		}

		@Override
		public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
				Object newValue) {
			Assert.fail();
		}

		@Override
		public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
			Assert.fail();
		}

		@Override
		public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
			Assert.fail();
		}

		@Override
		public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
				Object newValue) {
			Assert.fail();
		}

		@Override
		public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
			Assert.fail();
		}

		@Override
		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			Assert.fail();
		}

		@Override
		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			Assert.fail();
		}

		@Override
		public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
				boolean directed) {
			Assert.fail();
		}

		@Override
		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			Assert.fail();
		}

		@Override
		public void graphCleared(String sourceId, long timeId) {
			Assert.fail();
		}

		@Override
		public void stepBegins(String sourceId, long timeId, double step) {
			Assert.fail();
		}
	}
}
