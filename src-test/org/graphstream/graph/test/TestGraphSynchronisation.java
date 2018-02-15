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
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Test;

/**
 * Synchronisation between Sources and Sinks is not as trivial as it seems, but
 * should looks like it is ! Therefore we need to test it accurately ...
 */
public class TestGraphSynchronisation {
	@Test
	public void testGraphSyncBase() {
		// Simple test with two graphs that mutually listen at themselves.
		//
		// /-------> g2
		// | |
		// g1 <-------/

		testGraphSyncBase(new MultiGraph("g1"), new MultiGraph("g2"));
		testGraphSyncBase(new SingleGraph("g1"), new SingleGraph("g2"));
		testGraphSyncBase(new AdjacencyListGraph("g1"), new AdjacencyListGraph("g2"));
		testGraphSyncBase(new MultiGraph("g1"), new AdjacencyListGraph("g2"));

	}

	protected void testGraphSyncBase(Graph g1, Graph g2) {
		g2.addNode("Z"); // Allows to offset the internal "time" of source g2
		g2.removeNode("Z"); // (see implementation of synchronisation).

		g1.addSink(g2); // These two lines seem simple but introduce an eventual
		g2.addSink(g1); // recursive loop between the two graphs. Graph
						// synchronisation
						// is all about avoiding this loop.

		// Test with element addition.
		// We add elements in both graphs alternatively. At the end, the two
		// graphs must be
		// the same.

		g1.addNode("A");
		g2.addNode("B");
		g1.addNode("C");
		g2.addEdge("AB", "A", "B", false);
		g1.addEdge("BC", "B", "C", true);
		g2.addEdge("CA", "C", "A", true);

		assertEquals(3, g1.getNodeCount());
		assertEquals(3, g2.getNodeCount());
		assertEquals(3, g1.getEdgeCount());
		assertEquals(3, g2.getEdgeCount());

		assertNotNull(g1.getNode("A"));
		assertNotNull(g2.getNode("A"));
		assertNotNull(g1.getNode("B"));
		assertNotNull(g2.getNode("B"));
		assertNotNull(g1.getNode("C"));
		assertNotNull(g2.getNode("C"));

		assertNotNull(g1.getEdge("AB"));
		assertNotNull(g2.getEdge("AB"));
		assertNotNull(g1.getEdge("BC"));
		assertNotNull(g2.getEdge("BC"));
		assertNotNull(g1.getEdge("CA"));
		assertNotNull(g2.getEdge("CA"));

		// Test with attribute addition.

		g1.getNode("A").setAttribute("foo", "bar");
		g2.getEdge("AB").setAttribute("foo", "bar");

		assertEquals(1, g1.getNode("A").getAttributeCount());
		assertEquals(1, g2.getNode("A").getAttributeCount());
		assertEquals(1, g1.getEdge("AB").getAttributeCount());
		assertEquals(1, g2.getEdge("AB").getAttributeCount());
		assertEquals("bar", g1.getNode("A").getAttribute("foo"));
		assertEquals("bar", g2.getNode("A").getAttribute("foo"));
		assertEquals("bar", g1.getEdge("AB").getAttribute("foo"));
		assertEquals("bar", g2.getEdge("AB").getAttribute("foo"));

		// Test attribute change.

		g1.getNode("A").setAttribute("foo", "truc");
		g2.getEdge("AB").setAttribute("foo", "truc");

		assertEquals("truc", g1.getNode("A").getAttribute("foo"));
		assertEquals("truc", g2.getNode("A").getAttribute("foo"));
		assertEquals("truc", g1.getEdge("AB").getAttribute("foo"));
		assertEquals("truc", g2.getEdge("AB").getAttribute("foo"));

		// Test attribute removal.

		g2.getNode("A").removeAttribute("foo");
		g1.getEdge("AB").removeAttribute("foo");

		assertEquals(0, g1.getNode("A").getAttributeCount());
		assertEquals(0, g2.getNode("A").getAttributeCount());
		assertEquals(0, g1.getEdge("AB").getAttributeCount());
		assertEquals(0, g2.getEdge("AB").getAttributeCount());
		assertFalse(g1.getNode("A").hasAttribute("foo"));
		assertFalse(g2.getNode("A").hasAttribute("foo"));
		assertFalse(g1.getEdge("AB").hasAttribute("foo"));
		assertFalse(g2.getEdge("AB").hasAttribute("foo"));

		// Test edge removal

		g1.removeEdge("CA");

		assertEquals(2, g1.getEdgeCount());
		assertEquals(2, g2.getEdgeCount());
		assertNull(g1.getEdge("CA"));
		assertNull(g2.getEdge("CA"));

		// Test node removal and automatic edge removal (edge "AB" is
		// automatically removed).

		g2.removeNode("A");

		assertEquals(2, g2.getNodeCount());
		assertEquals(2, g1.getNodeCount());
		assertEquals(1, g1.getEdgeCount());
		assertEquals(1, g2.getEdgeCount());
		assertNull(g1.getNode("A"));
		assertNull(g2.getNode("A"));
		assertNull(g1.getEdge("AB"));
		assertNull(g2.getEdge("AB"));
	}

	@Test
	public void testGraphSyncCycleSimple() {
		// More advanced test where three graphs mutually listen at themselves.
		//
		// /--------> g3
		// | |
		// /--------> g2 |
		// | |
		// g1 <--------------------/

		testGraphSyncCycleSimple(new MultiGraph("g1"), new MultiGraph("g2"), new MultiGraph("g3"));
		testGraphSyncCycleSimple(new SingleGraph("g1"), new SingleGraph("g2"), new SingleGraph("g3"));
		testGraphSyncCycleSimple(new AdjacencyListGraph("g1"), new AdjacencyListGraph("g2"),
				new AdjacencyListGraph("g3"));
		testGraphSyncCycleSimple(new MultiGraph("g1"), new SingleGraph("g2"), new AdjacencyListGraph("g3"));
	}

	protected void testGraphSyncCycleSimple(Graph g1, Graph g2, Graph g3) {
		g1.addSink(g2);
		g2.addSink(g3);
		g3.addSink(g1);
		testGraphSyncCycle(g1, g2, g3);
	}

	@Test
	public void testGraphSyncCycleProblem() {
		// More advanced test where three graphs mutually listen at themselves.
		//
		// /--------> g3
		// | |
		// /--------> g2 <---------+
		// | |
		// g1 <--------------------/

		testGraphSyncCycleProblem(new MultiGraph("g1"), new MultiGraph("g2"), new MultiGraph("g3"));
		testGraphSyncCycleProblem(new SingleGraph("g1"), new SingleGraph("g2"), new SingleGraph("g3"));
		testGraphSyncCycleProblem(new AdjacencyListGraph("g1"), new AdjacencyListGraph("g2"),
				new AdjacencyListGraph("g3"));
		testGraphSyncCycleProblem(new MultiGraph("g1"), new SingleGraph("g2"), new AdjacencyListGraph("g3"));
	}

	protected void testGraphSyncCycleProblem(Graph g1, Graph g2, Graph g3) {
		g1.addSink(g2);
		g2.addSink(g3);
		g3.addSink(g1);
		g3.addSink(g2); // Exactly the same test as above with this line
						// added... :-)
		testGraphSyncCycle(g1, g2, g3);
	}

	protected void testGraphSyncCycle(Graph g1, Graph g2, Graph g3) {
		g1.addNode("A");
		g2.addNode("B");
		g3.addNode("C");

		assertEquals(3, g1.getNodeCount());
		assertEquals(3, g2.getNodeCount());
		assertEquals(3, g3.getNodeCount());

		assertNotNull(g1.getNode("A"));
		assertNotNull(g2.getNode("A"));
		assertNotNull(g3.getNode("A"));
		assertNotNull(g1.getNode("B"));
		assertNotNull(g2.getNode("B"));
		assertNotNull(g3.getNode("B"));
		assertNotNull(g1.getNode("C"));
		assertNotNull(g2.getNode("C"));
		assertNotNull(g3.getNode("C"));

		g1.addEdge("AB", "A", "B");
		g2.addEdge("BC", "B", "C", true);
		g3.addEdge("CA", "C", "A", false);

		assertEquals(3, g1.getEdgeCount());
		assertEquals(3, g2.getEdgeCount());
		assertEquals(3, g3.getEdgeCount());

		assertNotNull(g1.getEdge("AB"));
		assertNotNull(g2.getEdge("AB"));
		assertNotNull(g3.getEdge("AB"));
		assertNotNull(g1.getEdge("BC"));
		assertNotNull(g2.getEdge("BC"));
		assertNotNull(g3.getEdge("BC"));
		assertNotNull(g1.getEdge("CA"));
		assertNotNull(g2.getEdge("CA"));
		assertNotNull(g3.getEdge("CA"));

		// Now attributes.

		g1.setAttribute("foo", "bar");
		g2.getNode("A").setAttribute("foo", "bar");
		g3.getEdge("AB").setAttribute("foo", "bar");

		assertEquals("bar", g1.getAttribute("foo"));
		assertEquals("bar", g2.getAttribute("foo"));
		assertEquals("bar", g3.getAttribute("foo"));
		assertEquals("bar", g1.getNode("A").getAttribute("foo"));
		assertEquals("bar", g2.getNode("A").getAttribute("foo"));
		assertEquals("bar", g3.getNode("A").getAttribute("foo"));
		assertEquals("bar", g1.getEdge("AB").getAttribute("foo"));
		assertEquals("bar", g2.getEdge("AB").getAttribute("foo"));
		assertEquals("bar", g3.getEdge("AB").getAttribute("foo"));

		// Attributes change.

		g1.setAttribute("foo", "truc");
		g2.getNode("A").setAttribute("foo", "truc");
		g3.getEdge("AB").setAttribute("foo", "truc");

		assertEquals("truc", g1.getAttribute("foo"));
		assertEquals("truc", g2.getAttribute("foo"));
		assertEquals("truc", g3.getAttribute("foo"));
		assertEquals("truc", g1.getNode("A").getAttribute("foo"));
		assertEquals("truc", g2.getNode("A").getAttribute("foo"));
		assertEquals("truc", g3.getNode("A").getAttribute("foo"));
		assertEquals("truc", g1.getEdge("AB").getAttribute("foo"));
		assertEquals("truc", g2.getEdge("AB").getAttribute("foo"));
		assertEquals("truc", g3.getEdge("AB").getAttribute("foo"));

		// Attribute removal.

		g1.removeAttribute("foo");
		g2.getNode("A").removeAttribute("foo");
		g3.getEdge("AB").removeAttribute("foo");

		assertFalse(g1.hasAttribute("foo"));
		assertFalse(g2.hasAttribute("foo"));
		assertFalse(g3.hasAttribute("foo"));
		assertFalse(g1.getNode("A").hasAttribute("foo"));
		assertFalse(g2.getNode("A").hasAttribute("foo"));
		assertFalse(g3.getNode("A").hasAttribute("foo"));
		assertFalse(g1.getEdge("AB").hasAttribute("foo"));
		assertFalse(g2.getEdge("AB").hasAttribute("foo"));
		assertFalse(g3.getEdge("AB").hasAttribute("foo"));

		// Edge removal.

		g1.removeEdge("AB");

		assertEquals(2, g1.getEdgeCount());
		assertEquals(2, g2.getEdgeCount());
		assertEquals(2, g3.getEdgeCount());
		assertNull(g1.getEdge("AB"));
		assertNull(g2.getEdge("AB"));
		assertNull(g3.getEdge("AB"));

		// Node removal and automatic edge removal.

		g2.removeNode("A");

		assertEquals(2, g1.getNodeCount());
		assertEquals(2, g2.getNodeCount());
		assertEquals(2, g3.getNodeCount());
		assertEquals(1, g1.getEdgeCount());
		assertEquals(1, g2.getEdgeCount());
		assertEquals(1, g3.getEdgeCount());
		assertNull(g1.getNode("A"));
		assertNull(g2.getNode("A"));
		assertNull(g3.getNode("A"));
		assertNull(g1.getEdge("CA"));
		assertNull(g2.getEdge("CA"));
		assertNull(g3.getEdge("CA"));
	}
}