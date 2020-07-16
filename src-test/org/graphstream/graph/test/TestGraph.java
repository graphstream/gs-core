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
 * @author Richard O. Legendi <richard.legendi@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.MultiNode;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.Replayable;
import org.junit.Test;

public class TestGraph {
	@Test
	public void testBasic() {
		testBasic(new SingleGraph("sg"));
		testBasic(new MultiGraph("mg"));
		testBasic(new AdjacencyListGraph("alg"));
		testBasic(new AdjacencyListGraph("AL")); // XXX
		testBasic(new SingleGraph("S")); // XXX
		testBasic(new MultiGraph("M")); // XXX
	}

	@Test
	public void testReplay() {
		AbstractGraph g1 = new AdjacencyListGraph("g1");
		Graph g2 = new AdjacencyListGraph("g2");

		Node A1 = g1.addNode("A");
		Node B1 = g1.addNode("B");
		Node C1 = g1.addNode("C");

		Edge AB1 = g1.addEdge("AB", "A", "B");
		Edge BC1 = g1.addEdge("BC", "B", "C");
		Edge CA1 = g1.addEdge("CA", "C", "A");

		A1.setAttribute("string", "an example");
		B1.setAttribute("double", 42.0);
		C1.setAttribute("array", new int[] { 1, 2, 3 });

		AB1.setAttribute("string", "an example");
		BC1.setAttribute("double", 42.0);
		CA1.setAttribute("array", new int[] { 1, 2, 3 });

		Replayable.Controller controller = g1.getReplayController();
		controller.addSink(g2);
		controller.replay();

		Node A2 = g2.getNode("A");
		Node B2 = g2.getNode("B");
		Node C2 = g2.getNode("C");

		assertNotNull(A2);
		assertNotNull(B2);
		assertNotNull(C2);

		checkAttribute(A1, A2);
		checkAttribute(B1, B2);
		checkAttribute(C1, C2);

		Edge AB2 = g2.getEdge("AB");
		Edge BC2 = g2.getEdge("BC");
		Edge CA2 = g2.getEdge("CA");

		assertNotNull(AB2);
		assertNotNull(BC2);
		assertNotNull(CA2);

		checkAttribute(AB1, AB2);
		checkAttribute(BC1, BC2);
		checkAttribute(CA1, CA2);
	}

	protected void checkAttribute(Element e1, Element e2) {
		e1.attributeKeys().forEach(key -> {
			assertTrue(e2.hasAttribute(key));
			assertEquals(e1.getAttribute(key), e2.getAttribute(key));
		});

		e2.attributeKeys().forEach(key -> assertTrue(e1.hasAttribute(key)));
	}

	protected void testBasic(Graph graph) {
		Node A = graph.addNode("A");
		Node B = graph.addNode("B");
		Node C = graph.addNode("C");

		Edge AB = graph.addEdge("AB", "A", "B");
		Edge BC = graph.addEdge("BC", "B", "C");
		Edge CA = graph.addEdge("CA", "C", "A");

		assertEquals(3, graph.getNodeCount());
		assertEquals(3, graph.getEdgeCount());

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);
		assertNotNull(AB);
		assertNotNull(BC);
		assertNotNull(CA);

		assertEquals("A", A.getId());
		assertEquals("B", B.getId());
		assertEquals("C", C.getId());
		assertEquals("AB", AB.getId());
		assertEquals("BC", BC.getId());
		assertEquals("CA", CA.getId());

		assertEquals(A, graph.getNode("A"));
		assertEquals(B, graph.getNode("B"));
		assertEquals(C, graph.getNode("C"));
		assertEquals(AB, graph.getEdge("AB"));
		assertEquals(BC, graph.getEdge("BC"));
		assertEquals(CA, graph.getEdge("CA"));

		assertFalse(AB.isDirected());
		assertFalse(BC.isDirected());
		assertFalse(CA.isDirected());

		assertEquals(A, AB.getNode0());
		assertEquals(B, AB.getNode1());
		assertEquals(A, AB.getSourceNode());
		assertEquals(B, AB.getTargetNode());
		assertEquals(B, BC.getNode0());
		assertEquals(C, BC.getNode1());
		assertEquals(B, BC.getSourceNode());
		assertEquals(C, BC.getTargetNode());
		assertEquals(C, CA.getNode0());
		assertEquals(A, CA.getNode1());
		assertEquals(C, CA.getSourceNode());
		assertEquals(A, CA.getTargetNode());

		assertEquals(B, AB.getOpposite(A));
		assertEquals(A, AB.getOpposite(B));
		assertEquals(C, BC.getOpposite(B));
		assertEquals(B, BC.getOpposite(C));
		assertEquals(A, CA.getOpposite(C));
		assertEquals(C, CA.getOpposite(A));

		assertEquals(2, A.getDegree());
		assertEquals(2, B.getDegree());
		assertEquals(2, C.getDegree());

		assertEquals(2, A.getInDegree());
		assertEquals(2, A.getOutDegree());
		assertEquals(2, B.getInDegree());
		assertEquals(2, B.getOutDegree());
		assertEquals(2, C.getInDegree());
		assertEquals(2, C.getOutDegree());

		assertTrue(A.hasEdgeFrom("B"));
		assertTrue(A.hasEdgeFrom("C"));
		assertTrue(B.hasEdgeFrom("A"));
		assertTrue(B.hasEdgeFrom("C"));
		assertTrue(C.hasEdgeFrom("A"));
		assertTrue(C.hasEdgeFrom("B"));

		assertEquals(AB, A.getEdgeFrom("B"));
		assertEquals(CA, A.getEdgeFrom("C"));
		assertEquals(AB, B.getEdgeFrom("A"));
		assertEquals(BC, B.getEdgeFrom("C"));
		assertEquals(CA, C.getEdgeFrom("A"));
		assertEquals(BC, C.getEdgeFrom("B"));

		assertTrue(A.hasEdgeToward("B"));
		assertTrue(A.hasEdgeToward("C"));
		assertTrue(B.hasEdgeToward("A"));
		assertTrue(B.hasEdgeToward("C"));
		assertTrue(C.hasEdgeToward("A"));
		assertTrue(C.hasEdgeToward("B"));

		assertEquals(AB, A.getEdgeToward("B"));
		assertEquals(CA, A.getEdgeToward("C"));
		assertEquals(AB, B.getEdgeToward("A"));
		assertEquals(BC, B.getEdgeToward("C"));
		assertEquals(CA, C.getEdgeToward("A"));
		assertEquals(BC, C.getEdgeToward("B"));

		assertNull(A.getEdgeFrom("Z"));
		assertNull(B.getEdgeFrom("Z"));
		assertNull(C.getEdgeFrom("Z"));
		assertNull(A.getEdgeToward("Z"));
		assertNull(B.getEdgeToward("Z"));
		assertNull(C.getEdgeToward("Z"));

		// Loop edges
		assertFalse(A.hasEdgeBetween(A));
		assertFalse(A.hasEdgeToward(A));
		assertFalse(A.hasEdgeFrom(A));
		assertNull(A.getEdgeBetween(A));
		assertNull(A.getEdgeToward(A));
		assertNull(A.getEdgeFrom(A));
		Edge AA = graph.addEdge("AA", "A", "A");
		assertEquals(4, graph.getEdgeCount());
		assertEquals(3, A.getDegree());
		assertEquals(3, A.getInDegree());
		assertEquals(3, A.getOutDegree());
		assertTrue(A.hasEdgeBetween(A));
		assertTrue(A.hasEdgeToward(A));
		assertTrue(A.hasEdgeFrom(A));
		assertEquals(AA, A.getEdgeBetween(A));
		assertEquals(AA, A.getEdgeToward(A));
		assertEquals(AA, A.getEdgeFrom(A));
		assertEquals(A, AA.getSourceNode());
		assertEquals(A, AA.getTargetNode());
	}

	@Test
	public void testDirected() {
		testDirected(new SingleGraph("sg"));
		testDirected(new MultiGraph("mg"));
		// testDirected( new AdjacencyListGraph( "alg" ) );
		testDirected(new AdjacencyListGraph("AL")); // XXX
		testDirected(new SingleGraph("S")); // XXX
		testDirected(new MultiGraph("M")); // XXX
	}

	protected void testDirected(Graph graph) {
		Node A = graph.addNode("A");
		Node B = graph.addNode("B");
		Node C = graph.addNode("C");

		Edge AB = graph.addEdge("AB", "A", "B");
		Edge BC = graph.addEdge("BC", "B", "C", true);
		Edge CA = graph.addEdge("CA", "C", "A", false);

		// A
		// |\
		// | \
		// | \
		// | \
		// B--->C

		assertFalse(AB.isDirected());
		assertTrue(BC.isDirected());
		assertFalse(CA.isDirected());

		assertEquals(2, A.getDegree());
		assertEquals(2, B.getDegree());
		assertEquals(2, C.getDegree());

		assertEquals(2, A.getInDegree());
		assertEquals(2, A.getOutDegree());
		assertEquals(1, B.getInDegree());
		assertEquals(2, B.getOutDegree());
		assertEquals(2, C.getInDegree());
		assertEquals(1, C.getOutDegree());

		assertEquals(AB, A.getEdgeFrom("B"));
		assertEquals(CA, A.getEdgeFrom("C"));
		assertEquals(AB, B.getEdgeFrom("A"));
		assertNull(B.getEdgeFrom("C"));
		assertEquals(CA, C.getEdgeFrom("A"));
		assertEquals(BC, C.getEdgeFrom("B"));

		assertEquals(AB, A.getEdgeToward("B"));
		assertEquals(CA, A.getEdgeToward("C"));
		assertEquals(AB, B.getEdgeToward("A"));
		assertEquals(BC, B.getEdgeToward("C"));
		assertEquals(CA, C.getEdgeToward("A"));
		assertNull(C.getEdgeToward("B"));

		// Now change things a little :
		//
		// A
		// |\
		// | \
		// | \
		// v \
		// B<---C
		//
		// BC changes its direction, and AB becomes directed.

		graph.removeEdge("BC");
		BC = graph.addEdge("BC", "C", "B", true);
		graph.removeEdge("AB");
		AB = graph.addEdge("AB", "A", "B", true);

		assertTrue(AB.isDirected());
		assertTrue(BC.isDirected());
		assertFalse(CA.isDirected());

		assertEquals(2, A.getDegree());
		assertEquals(2, B.getDegree());
		assertEquals(2, C.getDegree());

		assertEquals(1, A.getInDegree());
		assertEquals(2, A.getOutDegree());
		assertEquals(2, B.getInDegree());
		assertEquals(0, B.getOutDegree());
		assertEquals(1, C.getInDegree());
		assertEquals(2, C.getOutDegree());

		assertNull(A.getEdgeFrom("B"));
		assertEquals(CA, A.getEdgeFrom("C"));
		assertEquals(AB, B.getEdgeFrom("A"));
		assertEquals(BC, B.getEdgeFrom("C"));
		assertEquals(CA, C.getEdgeFrom("A"));
		assertNull(C.getEdgeFrom("B"));

		assertEquals(AB, A.getEdgeToward("B"));
		assertEquals(CA, A.getEdgeToward("C"));
		assertNull(B.getEdgeToward("A"));
		assertNull(B.getEdgeToward("C"));
		assertEquals(CA, C.getEdgeToward("A"));
		assertEquals(BC, C.getEdgeToward("B"));

		// Directed loop edges
		assertFalse(A.hasEdgeBetween(A));
		assertFalse(A.hasEdgeToward(A));
		assertFalse(A.hasEdgeFrom(A));
		assertNull(A.getEdgeBetween(A));
		assertNull(A.getEdgeToward(A));
		assertNull(A.getEdgeFrom(A));
		Edge AA = graph.addEdge("AA", "A", "A", true);
		assertEquals(4, graph.getEdgeCount());
		assertEquals(3, A.getDegree());
		assertEquals(2, A.getInDegree());
		assertEquals(3, A.getOutDegree());
		assertTrue(A.hasEdgeBetween(A));
		assertTrue(A.hasEdgeToward(A));
		assertTrue(A.hasEdgeFrom(A));
		assertEquals(AA, A.getEdgeBetween(A));
		assertEquals(AA, A.getEdgeToward(A));
		assertEquals(AA, A.getEdgeFrom(A));
		assertEquals(A, AA.getSourceNode());
		assertEquals(A, AA.getTargetNode());
	}

	@Test
	public void testMulti() {
		MultiGraph graph = new MultiGraph("g");
		MultiNode A = (MultiNode) graph.addNode("A");
		MultiNode B = (MultiNode) graph.addNode("B");

		graph.addEdge("AB1", "A", "B");
		graph.addEdge("AB2", "A", "B");

		assertEquals(2, A.getDegree());
		assertEquals(2, B.getDegree());

		// loop edges
		graph.addEdge("AA1", "A", "B");
		graph.addEdge("AA2", "A", "B", true);

		assertEquals(4, A.getDegree());
	}

	@Test
	public void testSingle() {
		SingleGraph graph = new SingleGraph("g");
		Node A = graph.addNode("A");
		Node B = graph.addNode("B");

		graph.addEdge("AB1", "A", "B");

		try {
			graph.addEdge("AB2", "A", "B");
			fail();
		} catch (Exception e) {
			// Ok !
		}

		assertEquals(1, A.getDegree());
		assertEquals(1, B.getDegree());
	}

	@Test
	public void testIterables() {
		testIterables(new SingleGraph("sg"));
		testIterables(new MultiGraph("mg"));
		// testIterables( new AdjacencyListGraph( "alg" ) );
		testIterables(new AdjacencyListGraph("AL")); // XXX
		testIterables(new SingleGraph("S")); // XXX
		testIterables(new MultiGraph("M")); // XXX
	}

	protected void testIterables(Graph graph) {
		Node A = graph.addNode("A");
		Node B = graph.addNode("B");
		Node C = graph.addNode("C");

		Edge AB = graph.addEdge("AB", "A", "B");
		Edge BC = graph.addEdge("BC", "B", "C");
		Edge CA = graph.addEdge("CA", "C", "A");

		// Test graph iterables.

		HashSet<Node> nodes = new HashSet<Node>();
		HashSet<Edge> edges = new HashSet<Edge>();

		for (Node node : graph)
			nodes.add(node);

		assertEquals(3, nodes.size());
		assertTrue(nodes.contains(A));
		assertTrue(nodes.contains(B));
		assertTrue(nodes.contains(C));
		nodes.clear();

		graph.nodes().forEach(node -> nodes.add(node));

		assertEquals(3, nodes.size());
		assertTrue(nodes.contains(A));
		assertTrue(nodes.contains(B));
		assertTrue(nodes.contains(C));
		nodes.clear();

		graph.edges().forEach(edge -> edges.add(edge));

		assertEquals(3, edges.size());
		assertTrue(edges.contains(AB));
		assertTrue(edges.contains(BC));
		assertTrue(edges.contains(CA));
		edges.clear();

		// Test node iterables.

		for (Edge edge : A)
			edges.add(edge);

		assertEquals(2, edges.size());
		assertTrue(edges.contains(AB));
		assertTrue(edges.contains(CA));
		edges.clear();

		A.edges().forEach(edge -> edges.add(edge));

		assertEquals(2, edges.size());
		assertTrue(edges.contains(AB));
		assertTrue(edges.contains(CA));
		edges.clear();

		B.edges().forEach(edge -> edges.add(edge));

		assertEquals(2, edges.size());
		assertTrue(edges.contains(AB));
		assertTrue(edges.contains(BC));
		edges.clear();

		C.edges().forEach(edge -> edges.add(edge));

		assertEquals(2, edges.size());
		assertTrue(edges.contains(BC));
		assertTrue(edges.contains(CA));
		edges.clear();

		graph.removeEdge("AB");
		AB = graph.addEdge("AB", "A", "B", true);

		graph.removeEdge("BC");
		BC = graph.addEdge("BC", "B", "C", true);

		// A
		// |\
		// | \
		// | \
		// v \
		// B--->C

		A.enteringEdges().forEach(edge -> edges.add(edge));

		assertEquals(1, edges.size());
		assertTrue(edges.contains(CA));
		edges.clear();

		B.enteringEdges().forEach(edge -> edges.add(edge));

		assertEquals(1, edges.size());
		assertTrue(edges.contains(AB));
		edges.clear();

		C.enteringEdges().forEach(edge -> edges.add(edge));

		assertEquals(2, edges.size());
		assertTrue(edges.contains(BC));
		assertTrue(edges.contains(CA));
		edges.clear();

		A.leavingEdges().forEach(edge -> edges.add(edge));

		assertEquals(2, edges.size());
		assertTrue(edges.contains(AB));
		assertTrue(edges.contains(CA));
		edges.clear();

		B.leavingEdges().forEach(edge -> edges.add(edge));

		assertEquals(1, edges.size());
		assertTrue(edges.contains(BC));
		edges.clear();

		C.leavingEdges().forEach(edge -> edges.add(edge));

		assertEquals(1, edges.size());
		assertTrue(edges.contains(CA));
		edges.clear();
	}

	@Test
	public void testRemoval() {
		testRemoval(new AdjacencyListGraph("AL")); // XXX
		testRemoval(new SingleGraph("S")); // XXX
		testRemoval(new MultiGraph("M")); // XXX
	}

	public void testRemoval(Graph graph) {
		Node A = graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");

		Edge AB = graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		Edge CA = graph.addEdge("CA", "C", "A");

		assertEquals(3, graph.getNodeCount());
		assertEquals(3, graph.getEdgeCount());

		// Remove a node. This should also remove two edges.

		Node n = graph.removeNode("A");

		assertEquals(2, graph.getNodeCount());
		assertEquals(1, graph.getEdgeCount());

		assertEquals(n, A);
		assertNull(graph.getEdge("AB"));
		assertNull(graph.getEdge("CA"));
		assertNull(graph.getNode("A"));

		assertNotNull(graph.getEdge("BC"));
		assertNotNull(graph.getNode("B"));
		assertNotNull(graph.getNode("C"));

		// Rebuild the graph and remove an edge.

		A = graph.addNode("A");
		AB = graph.addEdge("AB", "A", "B");
		CA = graph.addEdge("CA", "C", "A");

		assertEquals(3, graph.getNodeCount());
		assertEquals(3, graph.getEdgeCount());

		Edge e = graph.removeEdge("A", "B");

		assertEquals(3, graph.getNodeCount());
		assertEquals(2, graph.getEdgeCount());

		assertEquals(e, AB);
		assertNull(graph.getEdge("AB"));

		assertNotNull(graph.getNode("A"));
		assertNotNull(graph.getNode("B"));
		assertNotNull(graph.getNode("C"));
		assertNotNull(graph.getEdge("BC"));
		assertNotNull(graph.getEdge("CA"));

		// Now remove another edge in another way.

		e = graph.removeEdge("CA");

		assertEquals(3, graph.getNodeCount());
		assertEquals(1, graph.getEdgeCount());

		assertEquals(e, CA);
		assertNull(graph.getEdge("AB"));
		assertNull(graph.getEdge("CA"));

		assertNotNull(graph.getNode("A"));
		assertNotNull(graph.getNode("B"));
		assertNotNull(graph.getNode("C"));
		assertNotNull(graph.getEdge("BC"));

		// loop edges
		Edge AA = graph.addEdge("AA", "A", "A");
		assertEquals(2, graph.getEdgeCount());
		e = graph.removeEdge("AA");
		assertEquals(1, graph.getEdgeCount());
		assertEquals(AA, e);
		assertEquals(0, A.getDegree());
		assertNull(graph.getEdge("AA"));

		Edge BB = graph.addEdge("BB", "B", "B", true);
		assertEquals(2, graph.getEdgeCount());
		e = graph.removeEdge("BB");
		assertEquals(BB, e);
		assertEquals(1, graph.getNode("B").getDegree());
		assertNull(graph.getEdge("BB"));
		BB = graph.addEdge("BB", "B", "B");
		graph.removeNode("B");
		assertNull(graph.getEdge("BB"));
		assertEquals(0, graph.getEdgeCount());

		graph.addEdge("AC", "A", "C");
		graph.addEdge("AA", "A", "A");

		// Test the whole graph erasing.

		graph.clear();

		assertEquals(0, graph.getNodeCount());
		assertEquals(0, graph.getEdgeCount());
	}
	 
	@Test
	public void testForeignEdgeRemoval(){
		try{
			foreignEdgeRemoval(new SingleGraph("sg"), new SingleGraph("sg"));
			fail();
		} catch (ElementNotFoundException e){ /* ALL GOOD */}
		try {
			foreignEdgeRemoval(new MultiGraph("mg"), new MultiGraph("mg"));
			fail();
		} catch (ElementNotFoundException e){ /* ALL GOOD */}
		try {
			foreignEdgeRemoval(new AdjacencyListGraph("AL"), new AdjacencyListGraph("AL")); // XXX
			fail();
		} catch (ElementNotFoundException e){ /* ALL GOOD */}
		try {
			foreignEdgeRemoval(new SingleGraph("S"), new MultiGraph("M")); // XXX
			fail();
		} catch (ElementNotFoundException e){ /* ALL GOOD */}
		try {
			foreignEdgeRemoval(new AdjacencyListGraph("AL"), new MultiGraph("M")); // XXX
			fail();
		} catch (ElementNotFoundException e){ /* ALL GOOD */}

	}
	public void foreignEdgeRemoval(Graph g1, Graph g2){

			g1.addNode("A");
			g1.addNode("B");
			g1.addNode("C");
			g1.addEdge("AB", "A", "B");
			Edge BC1 = g1.addEdge("BC", "B", "C");
			g1.addEdge("CA", "C", "A");
	
			g2.addNode("A");
			g2.addNode("B");
			g2.addNode("C");
			g2.addEdge("AB", "A", "B");
			g2.addEdge("BC", "B", "C");
			g2.addEdge("CA", "C", "A");
	
			g2.removeEdge(BC1);
	
	}
	

	@Test(expected = ElementNotFoundException.class)
	public void testForeignNodeRemoval(){
		Graph g1 = new AdjacencyListGraph("1");
		Graph g2 = new AdjacencyListGraph("2");

		g1.addNode("A");
		g1.addNode("B");
		Node ng1 = g1.addNode("C");
		g1.addEdge("AB", "A", "B");
		g1.addEdge("BC", "B", "C");
		g1.addEdge("CA", "C", "A");

		g2.addNode("A");
		g2.addNode("B");
		g2.addNode("C");
		g2.addEdge("AB", "A", "B");
		g2.addEdge("BC", "B", "C");
		g2.addEdge("CA", "C", "A");

		g2.removeNode(ng1);

	}

	@Test
	public void TestAddEdgeWithForeignNode(){
		Graph g1 = new AdjacencyListGraph("1");
		Graph g2 = new AdjacencyListGraph("2");

		g1.addNode("A");
		Node b1 = g1.addNode("B");
		Node c1 = g1.addNode("C");
		g1.addEdge("AB", "A", "B");
		g1.addEdge("BC", "B", "C");
		g1.addEdge("CA", "C", "A");

		
		g2.addNode("A");
		Node b2 = g2.addNode("B");
		Node c2 = g2.addNode("C");
		
		try{
			g2.addEdge("BC", b1, c1);
			fail();
		} catch (ElementNotFoundException e){ /* ALL GOOD */}
		try{
			g2.addEdge("BC", b1, c2);
			fail();
		} catch (ElementNotFoundException e){ /* ALL GOOD */}
		try{
			g2.addEdge("BC", b2, c1);
			fail();
		} catch (ElementNotFoundException e){ /* ALL GOOD */}

		
		
	}

	@Test
	public void testGraphListener() {
		testGraphListener(new SingleGraph("sg"));
		testGraphListener(new MultiGraph("mg"));
		// testGraphListener( new AdjacencyListGraph( "alg" ) );
		testGraphListener(new AdjacencyListGraph("AL")); // XXX
		testGraphListener(new SingleGraph("S")); // XXX
		testGraphListener(new MultiGraph("M")); // XXX
	}

	protected void testGraphListener(Graph input) {
		// We create a second graph (output) to receive the events of the first
		// (input).
		// We populate (or remove elements from) the input and check the output
		// to see
		// if it is a copy of the input.

		Graph output = new MultiGraph("outout");

		input.addSink(output);

		Node A = input.addNode("A");
		input.addNode("B");
		input.addNode("C");

		input.addEdge("AB", "A", "B");
		Edge BC = input.addEdge("BC", "B", "C");
		input.addEdge("CA", "C", "A");

		A.setAttribute("foo", "bar");
		BC.setAttribute("foo", "bar");

		assertEquals(3, input.getNodeCount());
		assertEquals(3, output.getNodeCount());
		assertEquals(3, input.getEdgeCount());
		assertEquals(3, output.getEdgeCount());

		assertNotNull(output.getNode("A"));
		assertNotNull(output.getNode("B"));
		assertNotNull(output.getNode("C"));
		assertNotNull(output.getEdge("AB"));
		assertNotNull(output.getEdge("BC"));
		assertNotNull(output.getEdge("CA"));

		assertEquals("bar", output.getNode("A").getAttribute("foo"));
		assertEquals("bar", output.getEdge("BC").getAttribute("foo"));

		// Now remove an attribute.

		A.removeAttribute("foo");

		assertFalse(output.hasAttribute("foo"));

		// Now remove a node.

		input.removeNode("A");

		assertEquals(2, input.getNodeCount());
		assertEquals(1, input.getEdgeCount());
		assertEquals(2, output.getNodeCount());
		assertEquals(1, output.getEdgeCount());

		// Now check that attribute change works.

		BC.setAttribute("foo", "truc");

		assertEquals("truc", BC.getAttribute("foo"));
		assertEquals("truc", output.getEdge("BC").getAttribute("foo"));
	}
}