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
package org.graphstream.stream.binary.test;

import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.SourceBase;
import org.graphstream.stream.binary.ByteDecoder;
import org.graphstream.stream.binary.ByteEncoder;
import org.graphstream.stream.binary.ByteFactory;
import org.graphstream.stream.binary.ByteProxy;

import java.nio.ByteBuffer;

/**
 * @since 01/02/16.
 */
public class ExampleByteProxy {
	public static void main(String... args) throws Exception {
		ByteProxy proxy = new ByteProxy(new ByteFactory() {
			@Override
			public ByteEncoder createByteEncoder() {
				return new InternalByteEncoder();
			}

			@Override
			public ByteDecoder createByteDecoder() {
				return new InternalByteDecoder();
			}
		}, 10000);

		DefaultGraph g = new DefaultGraph("g");
		g.addSink(proxy);

		proxy.setReplayable(g);
		proxy.start();

		int idx = 0;

		while (true) {
			String a = String.format("node-%03d", idx++);
			String b = String.format("node-%03d", idx++);

			g.addNode(a);
			g.addNode(b);
			g.addEdge("edge-" + a + "-" + b, a, b);

			Thread.sleep(1000);
		}
	}

	static class InternalByteDecoder extends SourceBase implements ByteDecoder {
		@Override
		public void decode(ByteBuffer buffer) {
			System.out.printf("%X%n", buffer.get());
		}

		@Override
		public boolean validate(ByteBuffer buffer) {
			return buffer.position() > 0;
		}
	}

	static class InternalByteEncoder implements ByteEncoder {
		Transport transport;

		@Override
		public void addTransport(Transport transport) {
			this.transport = transport;
		}

		@Override
		public void removeTransport(Transport transport) {

		}

		void send(ByteBuffer buffer) {
			buffer.rewind();
			transport.send(buffer);
		}

		@Override
		public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x01);

			send(buffer);
		}

		@Override
		public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
				Object newValue) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x02);

			send(buffer);
		}

		@Override
		public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x03);

			send(buffer);
		}

		@Override
		public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x11);

			send(buffer);
		}

		@Override
		public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
				Object newValue) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x12);

			send(buffer);
		}

		@Override
		public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x13);

			send(buffer);
		}

		@Override
		public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x21);

			send(buffer);
		}

		@Override
		public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
				Object newValue) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x22);

			send(buffer);
		}

		@Override
		public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x23);

			send(buffer);
		}

		@Override
		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x31);

			send(buffer);
		}

		@Override
		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x32);

			send(buffer);
		}

		@Override
		public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
				boolean directed) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x41);

			send(buffer);
		}

		@Override
		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x42);

			send(buffer);
		}

		@Override
		public void graphCleared(String sourceId, long timeId) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x50);

			send(buffer);
		}

		@Override
		public void stepBegins(String sourceId, long timeId, double step) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 0x60);

			send(buffer);
		}
	}
}
