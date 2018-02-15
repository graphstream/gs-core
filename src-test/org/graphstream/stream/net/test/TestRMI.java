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
 * @since 2011-05-12
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.net.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.LinkedList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.rmi.RMISink;
import org.graphstream.stream.rmi.RMISource;
import org.junit.Test;

public class TestRMI {

	@Test
	public void test() {
		RMISink sink;
		RMISource source;

		Graph g1 = new DefaultGraph("g1");
		Graph g2 = new DefaultGraph("g2");

		try {
			LocateRegistry.createRegistry(1099);
		} catch (Exception e) {

		}

		try {
			String name = "__test_rmi_source";

			sink = new RMISink();
			g1.addSink(sink);

			source = new RMISource();
			source.addSink(g2);

			source.bind(name);
			sink.register("//localhost/" + name);
		} catch (RemoteException e) {
			fail();
		}

		Node A = g1.addNode("A");
		Node B = g1.addNode("B");
		Node C = g1.addNode("C");

		Edge AB = g1.addEdge("AB", "A", "B", false);
		Edge AC = g1.addEdge("AC", "A", "C", true);
		Edge BC = g1.addEdge("BC", "B", "C", false);

		A.setAttribute("int", 1);
		B.setAttribute("string", "test");
		C.setAttribute("double", 2.0);

		AB.setAttribute("points", (Object) (new double[][] { { 1, 1 }, { 2, 2 } }));
		LinkedList<Integer> list = new LinkedList<Integer>();
		list.add(1);
		list.add(2);
		AC.setAttribute("list", list);
		BC.setAttribute("boolean", true);

		// -----

		A = g2.getNode("A");
		B = g2.getNode("B");
		C = g2.getNode("C");

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);
		assertEquals(g2.getNodeCount(), 3);

		AB = g2.getEdge("AB");
		AC = g2.getEdge("AC");
		BC = g2.getEdge("BC");

		assertNotNull(AB);
		assertNotNull(AC);
		assertNotNull(BC);
		assertEquals(g2.getEdgeCount(), 3);

		assertEquals("A", AB.getNode0().getId());
		assertEquals("B", AB.getNode1().getId());
		assertEquals("A", AC.getNode0().getId());
		assertEquals("C", AC.getNode1().getId());
		assertEquals("B", BC.getNode0().getId());
		assertEquals("C", BC.getNode1().getId());

		assertTrue(!AB.isDirected());
		assertTrue(AC.isDirected());
		assertTrue(!BC.isDirected());

		assertEquals(A.getAttribute("int"), Integer.valueOf(1));
		assertEquals(B.getAttribute("string"), "test");
		assertEquals(C.getNumber("double"), 2.0, 0);

		try {
			double[][] points = (double[][]) AB.getAttribute("points");

			assertEquals(points.length, 2);
			assertEquals(points[0].length, 2);
			assertEquals(points[1].length, 2);
			assertEquals(1.0, points[0][0], 1E-12);
			assertEquals(1.0, points[0][1], 1E-12);
			assertEquals(2.0, points[1][0], 1E-12);
			assertEquals(2.0, points[1][1], 1E-12);
		} catch (ClassCastException e) {
			fail();
		} catch (NullPointerException e) {
			fail();
		}

		assertEquals(list, AC.getAttribute("list"));
		assertTrue((Boolean) BC.getAttribute("boolean"));
	}
}
