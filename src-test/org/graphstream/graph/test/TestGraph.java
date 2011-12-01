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
package org.graphstream.graph.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.MultiNode;
import org.graphstream.graph.implementations.SingleGraph;
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
	}

	@Test
	public void testMulti() {
		MultiGraph graph = new MultiGraph("g");
		MultiNode A = graph.addNode("A");
		MultiNode B = graph.addNode("B");
		
		graph.addEdge("AB1", "A", "B");
		graph.addEdge("AB2", "A", "B");
		
		assertEquals(2, A.getDegree());
		assertEquals(2, B.getDegree());
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
		} catch(Exception e) {
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

		for (Node node : graph.getEachNode())
			nodes.add(node);

		assertEquals(3, nodes.size());
		assertTrue(nodes.contains(A));
		assertTrue(nodes.contains(B));
		assertTrue(nodes.contains(C));
		nodes.clear();

		for (Edge edge : graph.getEachEdge())
			edges.add(edge);

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

		for (Edge edge : A.getEdgeSet())
			edges.add(edge);

		assertEquals(2, edges.size());
		assertTrue(edges.contains(AB));
		assertTrue(edges.contains(CA));
		edges.clear();

		for (Edge edge : B.getEdgeSet())
			edges.add(edge);

		assertEquals(2, edges.size());
		assertTrue(edges.contains(AB));
		assertTrue(edges.contains(BC));
		edges.clear();

		for (Edge edge : C.getEdgeSet())
			edges.add(edge);

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

		for (Edge edge : A.getEnteringEdgeSet())
			edges.add(edge);

		assertEquals(1, edges.size());
		assertTrue(edges.contains(CA));
		edges.clear();

		for (Edge edge : B.getEnteringEdgeSet())
			edges.add(edge);

		assertEquals(1, edges.size());
		assertTrue(edges.contains(AB));
		edges.clear();

		for (Edge edge : C.getEnteringEdgeSet())
			edges.add(edge);

		assertEquals(2, edges.size());
		assertTrue(edges.contains(BC));
		assertTrue(edges.contains(CA));
		edges.clear();

		for (Edge edge : A.getLeavingEdgeSet())
			edges.add(edge);

		assertEquals(2, edges.size());
		assertTrue(edges.contains(AB));
		assertTrue(edges.contains(CA));
		edges.clear();

		for (Edge edge : B.getLeavingEdgeSet())
			edges.add(edge);

		assertEquals(1, edges.size());
		assertTrue(edges.contains(BC));
		edges.clear();

		for (Edge edge : C.getLeavingEdgeSet())
			edges.add(edge);

		assertEquals(1, edges.size());
		assertTrue(edges.contains(CA));
		edges.clear();
	}

	@Test
	public void testRemoval() {
		testRemoval(new SingleGraph("sg"));
		testRemoval(new MultiGraph("mg"));
		// testRemoval( new AdjacencyListGraph( "alg" ) );
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

		// Test the whole graph erasing.

		graph.clear();

		assertEquals(0, graph.getNodeCount());
		assertEquals(0, graph.getEdgeCount());
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

		A.addAttribute("foo", "bar");
		BC.addAttribute("foo", "bar");

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

		BC.changeAttribute("foo", "truc");

		assertEquals("truc", BC.getAttribute("foo"));
		assertEquals("truc", output.getEdge("BC").getAttribute("foo"));
	}
}