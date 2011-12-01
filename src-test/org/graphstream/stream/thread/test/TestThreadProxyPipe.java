/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
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
package org.graphstream.stream.thread.test;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
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

		source.getNode("A").addAttribute("A1", "foo");
		source.getNode("A").addAttribute("A2", "foo");

		ThreadProxyPipe proxy = new ThreadProxyPipe(source, target, true);

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
		source.getNode("X").addAttribute("X1", "foo");
		source.getNode("X").setAttribute("X1", "bar");
		source.getNode("A").removeAttribute("A1");

		source.addAttribute("STOP!");

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
}