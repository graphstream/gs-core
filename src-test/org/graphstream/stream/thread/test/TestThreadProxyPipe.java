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
 * @since 2011-05-11
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 */
package org.graphstream.stream.thread.test;

import java.io.IOException;
import java.io.StringWriter;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the thread proxy filter.
 * 
 * <p>
 * Sadly it is quite difficult to test thread things using junit.
 * </p>
 */
public class TestThreadProxyPipe {
	public static void main(String args[]) {
		new TestThreadProxyPipe();
	}

	public TestThreadProxyPipe() {

	}

	@Test
	public void Test1_GraphToWardGraph() {
		Graph source = new MultiGraph("g1");
		Graph target = new MultiGraph("g2");

		// Start to populate the graph to test the "replay" feature of the
		// proxy.

		source.addNode("A");
		source.addNode("B");
		source.addNode("C");
		source.addEdge("AB", "A", "B");
		source.addEdge("BC", "B", "C");
		source.addEdge("CA", "C", "A");

		source.getNode("A").setAttribute("A1", "foo");
		source.getNode("A").setAttribute("A2", "foo");

		ThreadProxyPipe proxy = new ThreadProxyPipe();
		proxy.addSink(target);
		proxy.init(source, true);

		Thread other = new Thread(new AnotherThread(proxy, target) {
			public void run() {
				// The second part of the test starts
				// in this target thread.

				boolean loop = true;

				do {
					proxy.pump();

					if (target.hasAttribute("STOP!"))
						loop = false;
				} while (loop);
			}

		});

		other.start();

		// The first part of the test begins in this
		// source thread.

		source.addNode("X");
		source.addNode("Y");
		source.addNode("Z");
		source.addEdge("XY", "X", "Y");
		source.addEdge("YZ", "Y", "Z");
		source.addEdge("ZX", "Z", "X");
		source.addEdge("XA", "X", "A");
		source.removeEdge("AB");
		source.removeNode("B");
		source.getNode("X").setAttribute("X1", "foo");
		source.getNode("X").setAttribute("X1", "bar");
		source.getNode("A").removeAttribute("A1");

		source.setAttribute("STOP!");

		// End of the test, wait for the other thread to terminate

		try {
			other.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Now test the results in the target thread.
	}

	/**
	 * Separate runnable that knows about the proxy.
	 */
	public abstract class AnotherThread implements Runnable {
		protected ThreadProxyPipe proxy;

		protected Graph target;

		public AnotherThread(ThreadProxyPipe proxy, Graph target) {
			this.proxy = proxy;
			this.target = target;
		}
	}

	@Test
	public void test() {
		try {
			for (int i = 0; i < 100; i++)
				testOne();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testOne() throws IOException {
		Graph g = new AdjacencyListGraph("g");
		ThreadProxyPipe tpp = new ThreadProxyPipe();
		tpp.init(g);

		FileSinkDGS dgs1 = new FileSinkDGS();
		FileSinkDGS dgs2 = new FileSinkDGS();
		StringWriter w1 = new StringWriter();
		StringWriter w2 = new StringWriter();

		Actor a = new Actor(tpp);
		Thread t = new Thread(a);

		g.addSink(dgs1);
		tpp.addSink(dgs2);

		dgs1.begin(w1);
		dgs2.begin(w2);

		t.start();
		generateRandom(g, 1000);

		try {
			Thread.yield();
			a.alive = false;
			t.join();
		} catch (InterruptedException e) {
		}

		w1.close();
		w2.close();

		String str1 = w1.toString();
		String str2 = w2.toString();

		Assert.assertTrue(str1.length() > 0);
		Assert.assertEquals(str1, str2);
	}

	static class Actor implements Runnable {
		ThreadProxyPipe pipe;
		boolean alive;

		public Actor(ThreadProxyPipe pipe) {
			this.pipe = pipe;
			this.alive = true;
		}

		public void run() {
			do
				pipe.pump();
			while (alive || pipe.hasPostRemaining());
		}
	}

	protected int ri(int size) {
		return (int) (Math.random() * size);
	}

	protected Object rv() {
		int i = ri(3);

		switch (i) {
		case 0:
			return ri(1000);
		case 1:
			return Math.random() * 1000;
		case 2: {
			StringBuilder sb = new StringBuilder();
			String chars = "abcedfghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

			for (int j = 0; j < 16; j++)
				sb.append(chars.charAt(ri(chars.length())));

			return sb.toString();
		}
		default:
			return null;
		}
	}

	protected void generateRandom(Graph g, int size) {
		String[] attributes = { "a", "b", "c", "d", "e", "f", "g" };

		for (int i = 0; i < size; i++) {
			Node n = g.addNode(String.format("%d", i));

			for (int j = 0; j < 3; j++)
				n.setAttribute(attributes[ri(attributes.length)], rv());
		}

		for (int i = 0; i < size; i++) {
			Node a, b;

			a = g.getNode((int) (Math.random() * size));

			do {
				b = g.getNode((int) (Math.random() * size));
			} while (a == b);

			Edge e = g.addEdge(String.format("edge%d", i), a, b);

			for (int j = 0; j < 3; j++)
				e.setAttribute(attributes[ri(attributes.length)], rv());
		}
	}
}