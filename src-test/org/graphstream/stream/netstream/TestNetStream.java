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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Vector;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.Sink;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.netstream.packing.Base64Packer;
import org.graphstream.stream.netstream.packing.Base64Unpacker;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.junit.Test;

/**
 * Test of the NetStream protocol, sender and receiver.
 * 
 * TestNetStream.java
 * 
 * @since Aug 16, 2011
 * 
 * @author Yoann Pigné
 * 
 */
public class TestNetStream {

	final Vector<String> errors = new Vector<String>();
	boolean debug = false;

	@Test
	public void testNetStreamAttributesChanges() {
		errors.clear();
		NetStreamReceiver net = null;

		try {
			net = new NetStreamReceiver("localhost", 2000, debug);
		} catch (UnknownHostException e1) {
			fail(e1.toString());
		} catch (IOException e1) {
			fail(e1.toString());
		}

		try {
			ThreadProxyPipe pipe = net.getDefaultStream();

			pipe.addSink(new SinkAdapter() {

				public void graphAttributeAdded(String sourceId, long timeId,
						String attribute, Object value) {
				}
			});

			Thread t = new Thread() {

				@Override
				public void run() {

					Graph g = new MultiGraph("G", false, true);
					NetStreamSender nsc = null;
					try {
						nsc = new NetStreamSender("localhost", 2000);
					} catch (UnknownHostException e1) {
						error(e1.toString());
						return;
					} catch (IOException e1) {
						error(e1.toString());
						return;
					}

					g.addSink(nsc);

					g.addAttribute("attribute", "foo");
					g.changeAttribute("attribute", false);
					Edge e = g.addEdge("AB", "A", "B");
					e.addAttribute("attribute", "foo");
					e.changeAttribute("attribute", false);
					Node n = e.getNode0();
					n.addAttribute("attribute", "foo");
					n.changeAttribute("attribute", false);

					try {
						nsc.close();
					} catch (IOException e1) {
					}
				}
			};

			t.start();

			try {
				t.join();
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}

			while (pipe.hasPostRemaining() || net.hasActiveConnections()) {
				pipe.pump();

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}

		} catch (ClassCastException cce) {
			fail("Bad cast in attribute change.");
		} finally {
			try {
				net.quit();
				net.join();
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Tests (almost) all the possible data types encoding and decoding.
	 */
	@Test
	public void testNetStreamTypes() {
		errors.clear();

		NetStreamReceiver net = null;
		try {
			net = new NetStreamReceiver("localhost", 2001, debug);
		} catch (UnknownHostException e1) {
			fail(e1.toString());
		} catch (IOException e1) {
			fail(e1.toString());
		}

		net.setUnpacker(new Base64Unpacker());

		ThreadProxyPipe pipe = net.getDefaultStream();

		pipe.addSink(new SinkAdapter() {

			public void graphAttributeAdded(String sourceId, long timeId,
					String attribute, Object value) {
				validate(attribute, value);
			}

			public void graphAttributeChanged(String sourceId, long timeId,
					String attribute, Object oldValue, Object newValue) {
				validate(attribute, newValue);
			}

			private void validate(String attribute, Object value) {

				String valueType = null;
				Class<?> valueClass = value.getClass();
				boolean isArray = valueClass.isArray();
				if (isArray) {
					valueClass = ((Object[]) value)[0].getClass();
				}
				if (valueClass.equals(Boolean.class)) {
					if (isArray) {
						valueType = "booleanArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Boolean[]) value));

					} else {
						valueType = "boolean";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Byte.class)) {
					if (isArray) {
						valueType = "byteArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Byte[]) value));
					} else {
						valueType = "byte";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Short.class)) {
					if (isArray) {
						valueType = "shortArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Short[]) value));
					} else {
						valueType = "short";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Integer.class)) {
					if (isArray) {
						valueType = "intArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Integer[]) value));
					} else {
						valueType = "int";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Long.class)) {
					if (isArray) {
						valueType = "longArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Long[]) value));
					} else {
						valueType = "long";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Float.class)) {
					if (isArray) {
						valueType = "floatArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Float[]) value));
					} else {
						valueType = "float";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(Double.class)) {
					if (isArray) {
						valueType = "doubleArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Double[]) value));
					} else {
						valueType = "double";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				} else if (valueClass.equals(String.class)) {
					if (isArray) {
						valueType = "typeArray";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute,
								Arrays.toString((Boolean[]) value));
					} else {
						valueType = "string";
						System.out.printf("found a %s for attribute %s=%s%n",
								valueType, attribute, value.toString());
					}
				}

				assertTrue(valueType.equals(attribute));

			}

		});

		Thread t = new Thread() {

			@Override
			public void run() {

				Graph g = new MultiGraph("G");
				NetStreamSender nsc = null;
				try {
					nsc = new NetStreamSender("localhost", 2001);
				} catch (UnknownHostException e1) {
					error(e1.toString());
					return;
				} catch (IOException e1) {
					error(e1.toString());
					return;
				}

				nsc.setPacker(new Base64Packer());

				g.addSink(nsc);

				g.addAttribute("intArray", 0, Integer.MAX_VALUE,
						Integer.MIN_VALUE);
				g.addAttribute("floatArray", 0f, Float.MAX_VALUE,
						Float.MIN_VALUE);
				g.addAttribute("doubleArray", 0.0, Double.MAX_VALUE,
						Double.MIN_VALUE);
				g.addAttribute("shortArray", (short) 0, Short.MAX_VALUE,
						Short.MIN_VALUE);
				g.addAttribute("longArray", 0L, Long.MAX_VALUE, Long.MIN_VALUE);
				g.addAttribute("byteArray", (byte) 0, Byte.MAX_VALUE,
						Byte.MIN_VALUE);
				g.addAttribute("booleanArray", true, false);
				// Object[] three = {new Short((short) 3),new Long(3L),"3"};
				// g.addAttribute("typeArray","one", 2 , three);
				g.addAttribute("int", 1);
				g.addAttribute("float", 1f);
				g.addAttribute("double", 1.0);
				g.addAttribute("short", (short) 0);
				g.addAttribute("long", 1L);
				g.addAttribute("byte", (byte) 0);
				g.addAttribute("boolean", true);
				g.addAttribute("string", "true");

				try {
					nsc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		t.start();

		try {
			t.join();
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}

		while (pipe.hasPostRemaining() || net.hasActiveConnections()) {
			pipe.pump();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		if (errors.size() > 0) {
			for (String s : errors) {
				System.err.println(s);
				fail(s);
			}
		}

		try {
			net.quit();
			net.join();
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Test multiple senders running on separated threads. They target different
	 * and/or same streams of the same receiver.
	 * 
	 * <ul>
	 * <li>Sender 1 -> Stream 1</li>
	 * <li>Sender 2 -> Stream 1</li>
	 * <li>Sender 3 -> Stream 2</li>
	 * </ul>
	 */
	@Test
	public void testNetStreamMultiThreadSenders() {
		errors.clear();

		Graph g1 = new MultiGraph("G1");
		Graph g2 = new MultiGraph("G2");
		NetStreamReceiver net = null;
		try {
			net = new NetStreamReceiver("localhost", 2002, debug);
		} catch (UnknownHostException e1) {
			fail(e1.toString());
		} catch (IOException e1) {
			fail(e1.toString());
		}

		ThreadProxyPipe pipe1 = net.getStream("G1");
		ThreadProxyPipe pipe2 = net.getStream("G2");

		pipe1.addSink(g1);
		pipe2.addSink(g2);

		Thread t1 = launchClient(2002, "G1", "0");
		Thread t2 = launchClient(2002, "G1", "1");
		Thread t3 = launchClient(2002, "G2", "0");

		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}

		while (pipe1.hasPostRemaining() || pipe2.hasPostRemaining()
				|| net.hasActiveConnections()) {
			pipe1.pump();
			pipe2.pump();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		if (errors.size() > 0) {
			for (String s : errors) {
				System.err.println(s);
				fail(s);
			}
		}

		assertEquals("G1", g1.getAttribute("id"));
		assertEquals("G2", g2.getAttribute("id"));
		assertEquals(180, g1.getNodeCount());
		assertEquals(90, g2.getNodeCount());

		try {
			net.quit();
			net.join();
		} catch (InterruptedException e) {
		}
	}

	private Thread launchClient(final int port, final String id,
			final String prefix) {
		Thread t = new Thread() {

			@Override
			public void run() {
				Graph g = new MultiGraph(id + prefix);

				NetStreamSender nsc = null;
				try {
					nsc = new NetStreamSender(id, "localhost", port);
				} catch (UnknownHostException e1) {
					error(e1.toString());
					return;
				} catch (IOException e1) {
					error(e1.toString());
					return;
				}
				g.addSink(nsc);

				g.addAttribute("id", id);

				for (int i = 0; i < 30; i++) {
					g.addNode(prefix + i + "_1");
					g.addNode(prefix + i + "_0");
					g.addNode(prefix + i + "_2");
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					nsc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		return t;
	}

	/**
	 * Hopefully tests all possible graph events through the NetStream
	 * framework.
	 */
	@Test
	public void testNetStreamEvents() {
		errors.clear();

		final Graph g1 = new DefaultGraph("G");
		NetStreamReceiver net = null;
		try {
			net = new NetStreamReceiver("localhost", 2002, debug);
		} catch (UnknownHostException e1) {
			fail(e1.toString());
		} catch (IOException e1) {
			fail(e1.toString());
		}

		ThreadProxyPipe pipe = net.getDefaultStream();

		pipe.addSink(g1);

		g1.addSink(new Sink() {

			public void graphAttributeAdded(String sourceId, long timeId,
					String attribute, Object value) {
				assertEquals(0, value);
				assertEquals("graphAttribute", attribute);
			}

			public void graphAttributeChanged(String sourceId, long timeId,
					String attribute, Object oldValue, Object newValue) {
				assertTrue((Integer) newValue == 0 || (Integer) newValue == 1);
				assertEquals("graphAttribute", attribute);
			}

			public void graphAttributeRemoved(String sourceId, long timeId,
					String attribute) {
				assertEquals("graphAttribute", attribute);
			}

			public void nodeAttributeAdded(String sourceId, long timeId,
					String nodeId, String attribute, Object value) {
				assertEquals(0, value);
				assertEquals("nodeAttribute", attribute);
			}

			public void nodeAttributeChanged(String sourceId, long timeId,
					String nodeId, String attribute, Object oldValue,
					Object newValue) {
				assertTrue((Integer) newValue == 0 || (Integer) newValue == 1);
				assertEquals("nodeAttribute", attribute);
			}

			public void nodeAttributeRemoved(String sourceId, long timeId,
					String nodeId, String attribute) {
				assertEquals("nodeAttribute", attribute);
			}

			public void edgeAttributeAdded(String sourceId, long timeId,
					String edgeId, String attribute, Object value) {
				assertEquals(0, value);
				assertEquals("edgeAttribute", attribute);
			}

			public void edgeAttributeChanged(String sourceId, long timeId,
					String edgeId, String attribute, Object oldValue,
					Object newValue) {
				assertTrue((Integer) newValue == 0 || (Integer) newValue == 1);
				assertEquals("edgeAttribute", attribute);
			}

			public void edgeAttributeRemoved(String sourceId, long timeId,
					String edgeId, String attribute) {
				assertEquals("edgeAttribute", attribute);
			}

			public void nodeAdded(String sourceId, long timeId, String nodeId) {
				assertTrue("node0".equals(nodeId) || "node1".equals(nodeId));
			}

			public void nodeRemoved(String sourceId, long timeId, String nodeId) {
				assertTrue("node0".equals(nodeId) || "node1".equals(nodeId));
			}

			public void edgeAdded(String sourceId, long timeId, String edgeId,
					String fromNodeId, String toNodeId, boolean directed) {
				assertEquals("edge", edgeId);
				assertEquals("node0", fromNodeId);
				assertEquals("node1", toNodeId);
				assertEquals(true, directed);
			}

			public void edgeRemoved(String sourceId, long timeId, String edgeId) {
				assertEquals("edge", edgeId);
			}

			public void graphCleared(String sourceId, long timeId) {

			}

			public void stepBegins(String sourceId, long timeId, double step) {
				assertEquals(1.1, step, 1E-12);
			}
		});

		Thread t = new Thread() {

			@Override
			public void run() {

				Graph g = new MultiGraph("G", false, true);

				NetStreamSender nsc = null;

				try {
					nsc = new NetStreamSender("localhost", 2002);
				} catch (UnknownHostException e1) {
					error(e1.toString());
					return;
				} catch (IOException e1) {
					error(e1.toString());
					return;
				}

				g.addSink(nsc);
				Node node0 = g.addNode("node0");
				Edge edge = g.addEdge("edge", "node0", "node1", true);
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
				g.removeEdge("edge");
				g.removeNode("node0");
				g.clear();

				try {
					nsc.close();
				} catch (IOException e1) {
				}
			}
		};

		t.start();

		try {
			t.join();
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}

		while (pipe.hasPostRemaining() || net.hasActiveConnections()) {
			pipe.pump();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		if (errors.size() > 0) {
			for (String s : errors) {
				System.err.println(s);
				fail(s);
			}
		}

		try {
			net.quit();
			net.join();
		} catch (InterruptedException e) {
		}
	}

	@Test
	public void testNetStreamVarint() {
		@SuppressWarnings("unused")
		NetStreamReceiver net = null;
		try {
			net = new NetStreamReceiver("localhost", 2004, false);
		} catch (UnknownHostException e1) {
			fail(e1.toString());
		} catch (IOException e1) {
			fail(e1.toString());
		}
		NetStreamSender nss = null;
		try {
			nss = new NetStreamSender(2004);
		} catch (UnknownHostException e) {
			fail(e.toString());
		} catch (IOException e) {
			fail(e.toString());
		}

		// -----------------------------
		// test encodeVarint
		// -----------------------------
		ByteBuffer buff = nss.encodeVarint(300);
		buff.rewind();
		// outBuffer(buff);
		assertEquals(buff.capacity(), 2);
		int bt = buff.get(0);
		assertEquals(216, (bt & 127) + (bt & 128));
		bt = buff.get(1);
		assertEquals(4, (bt & 127) + (bt & 128));

		buff = nss.encodeVarint(-16384);
		buff.rewind();
		// outBuffer(buff);
		assertEquals(buff.capacity(), 3);
		bt = buff.get(0);
		assertEquals(129, (bt & 127) + (bt & 128));
		bt = buff.get(1);
		assertEquals(128, (bt & 127) + (bt & 128));
		bt = buff.get(2);
		assertEquals(2, (bt & 127) + (bt & 128));

		// Short.MAX_VALUE
		buff = nss.encodeVarint(Short.MAX_VALUE);
		buff.rewind();
		//outBuffer(buff);
		assertEquals(buff.capacity(), 3);
		bt = buff.get(0);
		assertEquals(254, (bt & 127) + (bt & 128));
		bt = buff.get(1);
		assertEquals(255, (bt & 127) + (bt & 128));
		bt = buff.get(2);
		assertEquals(3, (bt & 127) + (bt & 128));

		// Short.MIN_VALUE
		buff = nss.encodeVarint(Short.MIN_VALUE);
		buff.rewind();
		//outBuffer(buff);
		assertEquals(buff.capacity(), 3);
		bt = buff.get(0);
		assertEquals(129, (bt & 127) + (bt & 128));
		bt = buff.get(1);
		assertEquals(128, (bt & 127) + (bt & 128));
		bt = buff.get(2);
		assertEquals(4, (bt & 127) + (bt & 128));

		// -----------------------------
		// test encodeVarintArray
		// -----------------------------
		Integer[] array = {300, -16384};
		buff = nss.encodeVarintArray(array);
		buff.rewind();
		// outBuffer(buff);
		assertEquals(buff.capacity(), 6);
		bt = buff.get(0);
		assertEquals(2, (bt & 127) + (bt & 128));
		bt = buff.get(1);
		assertEquals(216, (bt & 127) + (bt & 128));
		bt = buff.get(2);
		assertEquals(4, (bt & 127) + (bt & 128));
		bt = buff.get(3);
		assertEquals(129, (bt & 127) + (bt & 128));
		bt = buff.get(4);
		assertEquals(128, (bt & 127) + (bt & 128));
		bt = buff.get(5);
		assertEquals(2, (bt & 127) + (bt & 128));

		// -----------------------------
		// test encodeUnsignedVarint
		// -----------------------------
		buff = nss.encodeUnsignedVarint(300);
		buff.rewind();
		// outBuffer(buff);
		assertEquals(buff.capacity(), 2);
		bt = buff.get(0);
		assertEquals(172, (bt & 127) + (bt & 128));
		bt = buff.get(1);
		assertEquals(2, (bt & 127) + (bt & 128));

		// Short MAX_VALUE
		buff = nss.encodeUnsignedVarint(Short.MAX_VALUE);
		buff.rewind();
		//outBuffer(buff);
		assertEquals(buff.capacity(), 3);
		bt = buff.get(0);
		assertEquals(255, (bt & 127) + (bt & 128));
		bt = buff.get(1);
		assertEquals(255, (bt & 127) + (bt & 128));
		bt = buff.get(2);
		assertEquals(1, (bt & 127) + (bt & 128));

	}
	
	@SuppressWarnings("unused")
	private void outBuffer(ByteBuffer buf) {
		System.out.println(buf.toString());
		int nbytes = buf.capacity();
		int at = buf.position();
		for (int i = 0; i < nbytes; i++) {
			int bt = buf.get(at + i);
			if (bt < 0)
				bt = (bt & 127) + (bt & 128);
			System.out.printf("%d ", bt);
		}
		System.out.println();
	}
	synchronized void error(String s) {
		errors.add(s);
	}

	public static void main(String[] args) {
		new TestNetStream().testNetStreamAttributesChanges();
	}
}
