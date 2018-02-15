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
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph.test;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.junit.Test;

/**
 * Test the bases of the viewer.
 */
public class TestGraphSynchronisationProxyThread {
	@Test
	public void testGraphSynchronisation() {
		// Here a Graph is created in this thread and another thread is created
		// with a GraphicGraph.
		// The two graphs being in separate threads we use thread proxies
		// filters to pass
		// informations between the two. Once again we will use synchronisation
		// (the two graphs
		// listen at each other). In the direction Graph -> GraphicGraph the
		// graphic graph listens
		// at ALL the events (elements + attributes). In the direction
		// GraphicGraph -> Graph, the
		// graph only listen at attributes since we do not intend to add
		// elements directly in the
		// graphic graph.

		Graph main = new MultiGraph("main");
		ThreadProxyPipe toGraphic = new ThreadProxyPipe();
		toGraphic.init(main);

		InTheSwingThread viewerThread = new InTheSwingThread(toGraphic);
		ThreadProxyPipe toMain = viewerThread.getProxy();

		toMain.addAttributeSink(main); // Get the graphic graph proxy.

		// Now launch the graphic graph in the Swing thread using a Swing Timer.

		viewerThread.start();

		// We modify the graph in the main thread.

		Node A = main.addNode("A");
		Node B = main.addNode("B");
		Node C = main.addNode("C");
		main.addEdge("AB", "A", "B");
		main.addEdge("BC", "B", "C");
		main.addEdge("CA", "C", "A");

		SpriteManager sman = new SpriteManager(main);
		Sprite S1 = sman.addSprite("S1");
		Sprite S2 = sman.addSprite("S2");
		Sprite S3 = sman.addSprite("S3");

		S3.setPosition(1, 2, 2);
		S3.setPosition(2, 3, 2);
		S3.setPosition(3, 2, 1);

		A.setAttribute("ui.foo", "bar");
		B.setAttribute("ui.bar", "foo");
		C.setAttribute("truc"); // Not prefixed by UI, will not pass.
		S1.setAttribute("ui.foo", "bar");
		main.stepBegins(1);

		toMain.pump();

		// We ask the Swing thread to modify the graphic graph.

		main.stepBegins(2);
		main.setAttribute("ui.EQUIP"); // Remember GraphicGraph filters
										// attributes.

		// Wait and stop.

		toMain.pump();
		sleep(1000);
		toMain.pump();

		main.setAttribute("ui.STOP");

		toMain.pump();
		sleep(1000);
		toMain.pump();

		// ****************************************************************************************
		// Now we can begin the real test. We ensure the timer in the Swing
		// graph stopped and check
		// If the two graphs (main and graphic) synchronized correctly.

		GraphicGraph graphic = viewerThread.graphic;

		assertTrue(viewerThread.isStopped());
		assertFalse(main.hasAttribute("ui.EQUIP"));
		assertFalse(graphic.hasAttribute("ui.EQUIP"));
		assertTrue(main.hasAttribute("ui.STOP"));
		assertTrue(graphic.hasAttribute("ui.STOP"));

		assertEquals(3, graphic.getStep(), 0);
		assertEquals(2, main.getStep(), 0); // We do not listen at elements events
											// the step 3
											// of the graphic graph did not
											// reached us.
		// Assert all events passed toward the graphic graph.

		assertEquals(3, graphic.getNodeCount());
		assertEquals(3, graphic.getEdgeCount());
		assertEquals(3, graphic.getSpriteCount());
		assertNotNull(graphic.getNode("A"));
		assertNotNull(graphic.getNode("B"));
		assertNotNull(graphic.getNode("C"));
		assertNotNull(graphic.getEdge("AB"));
		assertNotNull(graphic.getEdge("BC"));
		assertNotNull(graphic.getEdge("CA"));
		assertNotNull(graphic.getSprite("S1"));
		assertNotNull(graphic.getSprite("S2"));
		assertEquals("bar", graphic.getNode("A").getAttribute("ui.foo"));
		assertEquals("foo", graphic.getNode("B").getAttribute("ui.bar"));
		// assertNull( graphic.getNode("C").getAttribute( "truc" ) ); // Should
		// not pass the attribute filter.
		assertEquals("bar", graphic.getSprite("S1").getAttribute("ui.foo"));
		assertEquals("bar", sman.getSprite("S1").getAttribute("ui.foo"));

		// Assert attributes passed back to the graph from the graphic graph.

		Object xyz1[] = { 4, 3, 2 };
		Object xyz2[] = { 2, 1, 0 };
		Object xyz3[] = { 3, 2, 1 };

		assertArrayEquals(xyz1, (Object[]) main.getNode("A").getAttribute("xyz"));
		assertArrayEquals(xyz2, (Object[]) main.getNode("B").getAttribute("xyz"));
		assertArrayEquals(xyz3, (Object[]) main.getNode("C").getAttribute("xyz"));

		assertEquals("foobar", S2.getAttribute("ui.foobar"));

		GraphicSprite gs3 = graphic.getSprite("S3");

		assertEquals(0.5f, S1.getX(), 0);
		assertEquals(0, S1.getY(), 0);
		assertEquals(0, S1.getZ(), 0);
		assertEquals(1, S2.getX(), 0);
		assertEquals(2, S2.getY(), 0);
		assertEquals(3, S2.getZ(), 0);

		assertEquals(3, gs3.getX(), 0);
		assertEquals(2, gs3.getY(), 0);
		assertEquals(1, gs3.getZ(), 0);
	}

	protected void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * The graphic graph in the Swing thread.
	 */
	public static class InTheSwingThread implements ActionListener {
		protected ThreadProxyPipe fromMain;

		protected GraphicGraph graphic;

		protected Timer timer;

		public InTheSwingThread(ThreadProxyPipe input) {
			fromMain = input;
			graphic = new GraphicGraph("gg");
			timer = new Timer(40, this);

			timer.setRepeats(true);
			timer.setCoalesce(true);
			input.addSink(graphic);
		}

		public void start() {
			timer.start();
		}

		public boolean isStopped() {
			return (!timer.isRunning());
		}

		public void actionPerformed(ActionEvent e) {
			fromMain.pump();

			// We wait for some attributes to be added. Such events trigger
			// actions that modify
			// the graphic graph and should be propagated (synchronised) to the
			// main graph.
			// When we encounter the "ui.STOP" event we stop the timer.

			if (graphic.hasAttribute("ui.EQUIP")) {
				Node A = graphic.getNode("A");
				Node B = graphic.getNode("B");
				Node C = graphic.getNode("C");

				if (A != null)
					A.setAttribute("xyz", 4, 3, 2);
				if (B != null)
					B.setAttribute("xyz", 2, 1, 0);
				if (C != null)
					C.setAttribute("xyz", 3, 2, 1);

				GraphicSprite S1 = graphic.getSprite("S1");
				GraphicSprite S2 = graphic.getSprite("S2");

				if (S2 != null) {
					S2.setAttribute("ui.foobar", "foobar");
					S2.setPosition(1, 2, 3, Style.Units.GU);
				}

				if (S1 != null)
					S1.setPosition(0.5f);

				graphic.removeAttribute("ui.EQUIP");
				graphic.stepBegins(3);
			} else if (graphic.hasAttribute("ui.STOP")) {
				timer.stop();
				// System.err.printf( "STOP!%n" );
			}
		}

		public ThreadProxyPipe getProxy() {
			ThreadProxyPipe toMain = new ThreadProxyPipe();
			toMain.init(graphic);

			// fromMain.synchronizeWith( toMain, graphic );

			return toMain;
		}
	}
}