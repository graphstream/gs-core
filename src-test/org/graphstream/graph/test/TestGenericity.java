/*
 * Copyright 2006 - 2012
 *      Stefan Balev       <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	<yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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

import java.util.Collection;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AbstractNode;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.AdjacencyListNode;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.MultiNode;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.implementations.SingleNode;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestGenericity {
	protected static class BadTypedNode extends SingleNode {
		public BadTypedNode(Graph graph, String id) {
			super((AbstractGraph) graph, id);
		}
	}

	protected static class BadTypedEdge extends AbstractEdge {
		protected BadTypedEdge(String id, Node src, Node dst, boolean directed) {
			super(id, (AbstractNode)src, (AbstractNode)dst, directed);
		}
	}

	protected static class MyALGNode extends AdjacencyListNode {
		public MyALGNode(Graph graph, String id) {
			super((AbstractGraph)graph, id);
		}
	}

	protected static class MyALGNodeFactory implements NodeFactory<MyALGNode> {
		public MyALGNode newInstance(String id, Graph graph) {
			return new MyALGNode(graph, id);
		}
	}

	protected static class MyALGEdge extends AbstractEdge {
		protected MyALGEdge(String id, Node src, Node dst, boolean directed) {
			super(id, (AbstractNode)src, (AbstractNode)dst, directed);
		}
	}

	protected static class MyALGEdgeFactory implements EdgeFactory<MyALGEdge> {
		public MyALGEdge newInstance(String id, Node src, Node dst,
				boolean directed) {
			return new MyALGEdge(id, src, dst, directed);
		}
	}

	protected static class MySingleNode extends SingleNode {
		public MySingleNode(Graph graph, String id) {
			super((AbstractGraph)graph, id);
		}
	}

	protected static class MySingleNodeFactory implements
			NodeFactory<MySingleNode> {
		public MySingleNode newInstance(String id, Graph graph) {
			return new MySingleNode(graph, id);
		}
	}

	protected static class MySingleEdge extends AbstractEdge {
		protected MySingleEdge(String id, Node src, Node dst, boolean directed) {
			super(id, (AbstractNode)src, (AbstractNode)dst, directed);
		}
	}

	protected static class MySingleEdgeFactory implements
			EdgeFactory<MySingleEdge> {
		public MySingleEdge newInstance(String id, Node src, Node dst,
				boolean directed) {
			return new MySingleEdge(id, src, dst, directed);
		}
	}

	protected static class MyMultiNode extends MultiNode {
		public MyMultiNode(Graph graph, String id) {
			super((AbstractGraph)graph, id);
		}
	}

	protected static class MyMultiNodeFactory implements
			NodeFactory<MyMultiNode> {
		public MyMultiNode newInstance(String id, Graph graph) {
			return new MyMultiNode(graph, id);
		}
	}

	protected static class MyMultiEdge extends AbstractEdge {
		protected MyMultiEdge(String id, Node src, Node dst, boolean directed) {
			super(id, (AbstractNode)src, (AbstractNode)dst, directed);
		}
	}

	protected static class MyMultiEdgeFactory implements
			EdgeFactory<MyMultiEdge> {
		public MyMultiEdge newInstance(String id, Node src, Node dst,
				boolean directed) {
			return new MyMultiEdge(id, src, dst, directed);
		}
	}

	@Test
	public void checkAdjacencyListGraph() {
		Graph g = new AdjacencyListGraph("g");

		g.setNodeFactory(new MyALGNodeFactory());
		g.setEdgeFactory(new MyALGEdgeFactory());

		new TestAddRemoveNode<MyALGNode>(g);
		new TestForEachNode<MyALGNode>(g);
		new TestNodeCollection<MyALGNode>(g);
		new TestAddRemoveEdge<MyALGEdge>(g);
		new TestForEachEdge<MyALGEdge>(g);
		new TestEdgeCollection<MyALGEdge>(g);

		new TestNodeEdgeSet<MyALGEdge>(g);
		new TestNodeNeighborhood<MyALGNode>(g);

		new TestEdgeExtremities<MyALGNode>(g);
	}

	@Test
	public void checkSingleGraph() {
		Graph g = new SingleGraph("g");

		g.setNodeFactory(new MySingleNodeFactory());
		g.setEdgeFactory(new MySingleEdgeFactory());

		new TestAddRemoveNode<MySingleNode>(g);
		new TestForEachNode<MySingleNode>(g);
		new TestNodeCollection<MySingleNode>(g);
		new TestAddRemoveEdge<MySingleEdge>(g);
		new TestForEachEdge<MySingleEdge>(g);
		new TestEdgeCollection<MySingleEdge>(g);

		new TestNodeEdgeSet<MySingleEdge>(g);
		new TestNodeNeighborhood<MySingleNode>(g);

		new TestEdgeExtremities<MySingleNode>(g);
	}

	@Test
	public void checkMultiGraph() {
		Graph g = new MultiGraph("g");

		g.setNodeFactory(new MyMultiNodeFactory());
		g.setEdgeFactory(new MyMultiEdgeFactory());

		new TestAddRemoveNode<MyMultiNode>(g);
		new TestForEachNode<MyMultiNode>(g);
		new TestNodeCollection<MyMultiNode>(g);
		new TestAddRemoveEdge<MyMultiEdge>(g);
		new TestForEachEdge<MyMultiEdge>(g);
		new TestEdgeCollection<MyMultiEdge>(g);

		new TestNodeEdgeSet<MyMultiEdge>(g);
		new TestNodeNeighborhood<MyMultiNode>(g);

		new TestEdgeExtremities<MyMultiNode>(g);
	}

	static class TestAddRemoveNode<A extends Node> {
		@SuppressWarnings("unused")
		TestAddRemoveNode(Graph g) {
			A goodTypedNode;
			BadTypedNode badTypedNode;
			Node simpleNode;

			try {
				goodTypedNode = g.addNode("test-add-remove-node-A");
				simpleNode = g.addNode("test-add-remove-node-N");
			} catch (ClassCastException e) {
				fail();
			}

			try {
				badTypedNode = g.addNode("test-add-remove-node-B");
				System.err.println(badTypedNode.getClass());
				fail();
			} catch (ClassCastException e) {
			}

			assertNotNull(g.getNode("test-add-remove-node-A"));
			assertNotNull(g.getNode("test-add-remove-node-B"));
			assertNotNull(g.getNode("test-add-remove-node-N"));

			// Get

			try {
				badTypedNode = g.getNode("test-add-remove-node-A");
				fail();
				badTypedNode = g.getNode("test-add-remove-node-B");
				fail();
				badTypedNode = g.getNode("test-add-remove-node-N");
				fail();
			} catch (ClassCastException e) {
			}

			try {
				goodTypedNode = g.getNode("test-add-remove-node-A");
				goodTypedNode = g.getNode("test-add-remove-node-B");
				goodTypedNode = g.getNode("test-add-remove-node-N");

				simpleNode = g.getNode("test-add-remove-node-A");
				simpleNode = g.getNode("test-add-remove-node-B");
				simpleNode = g.getNode("test-add-remove-node-N");
			} catch (ClassCastException e) {
				fail();
			}

			// Remove

			try {
				simpleNode = g.removeNode("test-add-remove-node-A");
				goodTypedNode = g.removeNode("test-add-remove-node-B");
			} catch (ClassCastException e) {
				fail();
			}

			try {
				badTypedNode = g.removeNode("test-add-remove-node-N");
				fail();
			} catch (ClassCastException e) {
			}

			assertNull(g.getNode("test-add-remove-node-A"));
			assertNull(g.getNode("test-add-remove-node-B"));
			assertNull(g.getNode("test-add-remove-node-N"));
		}
	}

	static class TestForEachNode<A extends Node> {
		@SuppressWarnings("unused")
		TestForEachNode(Graph g) {
			g.clear();

			for (int i = 0; i < 10; i++)
				g.addNode(String.format("test-for-each-%02d", i));

			try {
				for (A goodTypedNode : g.<A> getEachNode()) {
				}

				for (Node simpleNode : g.getEachNode()) {
				}
			} catch (ClassCastException e) {
				fail();
			}

			try {
				for (BadTypedNode badTypedNode : g.<BadTypedNode> getEachNode()) {
					fail();
				}
			} catch (ClassCastException e) {
			}

			g.clear();
		}
	}

	static class TestAddRemoveEdge<A extends Edge> {
		@SuppressWarnings("unused")
		TestAddRemoveEdge(Graph g) {
			g.clear();

			g.addNode("0");
			g.addNode("1");
			g.addNode("2");
			g.addNode("3");
			g.addNode("4");
			g.addNode("5");

			try {
				A goodTypedEdge = g.addEdge("e0", "0", "1");
				Edge e = g.addEdge("e1", "1", "2");
			} catch (ClassCastException e) {
				fail();
			}

			try {
				BadTypedEdge bte = g.addEdge("e2", "2", "3");
				fail();
			} catch (ClassCastException e) {
			}

			assertNotNull(g.getEdge("e0"));
			assertNotNull(g.getEdge("e1"));
			assertNotNull(g.getEdge("e2"));

			// Get

			try {
				A goodTypedEdge;

				goodTypedEdge = g.getEdge("e0");
				goodTypedEdge = g.getEdge("e1");
				goodTypedEdge = g.getEdge("e2");

				Edge e;

				e = g.getEdge("e0");
				e = g.getEdge("e1");
				e = g.getEdge("e2");
			} catch (ClassCastException e) {
				fail();
			}

			try {
				BadTypedEdge bte;

				bte = g.getEdge("e0");
				fail();
				bte = g.getEdge("e1");
				fail();
				bte = g.getEdge("e2");
				fail();
			} catch (ClassCastException e) {
			}

			// Remove

			try {
				A goodTypedEdge = g.removeEdge("e0");
				Edge e = g.removeEdge("e1");
			} catch (ClassCastException e) {
				fail();
			}

			try {
				BadTypedEdge bte = g.removeEdge("e2");
				fail();
			} catch (ClassCastException e) {
			}

			assertNull(g.getEdge("e0"));
			assertNull(g.getEdge("e1"));
			assertNull(g.getEdge("e2"));
		}
	}

	static class TestForEachEdge<A extends Edge> {
		@SuppressWarnings("unused")
		TestForEachEdge(Graph g) {
			g.clear();

			for (int i = 0; i < 10; i++)
				g.addNode(String.format("node-%02d", i));

			for (int i = 0; i < 9; i++)
				g.addEdge(String.format("edge-%02d", i),
						String.format("node-%02d", i),
						String.format("node-%02d", i + 1));

			try {
				for (A goodTypedEdge : g.<A> getEachEdge()) {
				}

				for (Edge simpleEdge : g.getEachEdge()) {
				}
			} catch (ClassCastException e) {
				fail();
			}

			try {
				for (BadTypedEdge bte : g.<BadTypedEdge> getEachEdge()) {
					fail();
				}
			} catch (ClassCastException e) {
			}

			g.clear();
		}
	}

	static class TestNodeCollection<A extends Node> {
		@SuppressWarnings("unused")
		TestNodeCollection(Graph g) {
			g.clear();

			for (int i = 0; i < 10; i++)
				g.addNode(String.format("node-%02d", i));

			try {
				Collection<A> cA = g.getNodeSet();
				for (A goodTypedNode : cA)
					;

				Collection<Node> cNode = g.getNodeSet();
				for (Node n : cNode)
					;
			} catch (ClassCastException e) {
				fail();
			}

			try {
				Collection<BadTypedNode> cB = g.getNodeSet();

				for (BadTypedNode n : cB) {
					fail();
				}
			} catch (ClassCastException e) {
			}

			try {
				Iterator<A> iA = g.getNodeIterator();
				while (iA.hasNext()) {
					A goodTypedNode = iA.next();
				}

				Iterator<Node> iN = g.getNodeIterator();
				while (iN.hasNext()) {
					Node n = iN.next();
				}
			} catch (ClassCastException e) {
				fail();
			}

			try {
				Iterator<BadTypedNode> iB = g.getNodeIterator();
				while (iB.hasNext()) {
					BadTypedNode btn = iB.next();
					fail();
				}
			} catch (ClassCastException e) {
			}

			g.clear();
		}
	}

	static class TestEdgeCollection<A extends Edge> {
		@SuppressWarnings("unused")
		TestEdgeCollection(Graph g) {
			g.clear();

			for (int i = 0; i < 10; i++)
				g.addNode(String.format("node-%02d", i));

			for (int i = 0; i < 9; i++)
				g.addEdge(String.format("edge-%02d", i),
						String.format("node-%02d", i),
						String.format("node-%02d", i + 1));

			try {
				Collection<A> cA = g.getEdgeSet();
				for (A goodTypedEdge : cA)
					;

				Collection<Edge> cEdge = g.getEdgeSet();
				for (Edge e : cEdge)
					;
			} catch (ClassCastException e) {
				fail();
			}

			try {
				Collection<BadTypedEdge> cB = g.getEdgeSet();
				for (BadTypedEdge bte : cB) {
					fail();
				}
			} catch (ClassCastException e) {
			}

			try {
				Iterator<A> iA = g.getEdgeIterator();
				while (iA.hasNext()) {
					A goodTypedEdge = iA.next();
				}

				Iterator<Edge> iE = g.getEdgeIterator();
				while (iE.hasNext()) {
					Edge e = iE.next();
				}
			} catch (ClassCastException e) {
				fail();
			}

			try {
				Iterator<BadTypedEdge> iB = g.getEdgeIterator();
				while (iB.hasNext()) {
					BadTypedEdge bte = iB.next();
				}

				fail();
			} catch (ClassCastException e) {
			}

			g.clear();
		}
	}

	static class TestNodeEdgeSet<A extends Edge> {
		@SuppressWarnings("unused")
		TestNodeEdgeSet(Graph g) {
			g.clear();

			g.addNode("root");

			for (int i = 0; i < 10; i++) {
				g.addNode(String.format("ext-%02d", i));
				g.addEdge(String.format("edge-%02d", i), "root",
						String.format("ext-%02d", i));
			}

			Node root = g.getNode("root");

			try {
				for (A edge : root.<A> getEdgeSet())
					;
				for (Edge e : root.getEdgeSet())
					;

				for (int i = 0; i < 10; i++) {
					A edge = root.getEdge(i);
					Edge e = root.getEdge(i);
				}

				Iterator<A> iA;

				iA = root.getLeavingEdgeIterator();
				while (iA.hasNext()) {
					A edge = iA.next();
				}

				iA = root.getEnteringEdgeIterator();
				while (iA.hasNext()) {
					A edge = iA.next();
				}

				Iterator<Edge> iE;

				iE = root.getLeavingEdgeIterator();
				while (iE.hasNext()) {
					Edge e = iE.next();
				}

				iE = root.getEnteringEdgeIterator();
				while (iE.hasNext()) {
					Edge e = iE.next();
				}
			} catch (ClassCastException e) {
				fail();
			} catch (UnsupportedOperationException e) {
			}

			try {
				for (BadTypedEdge bte : root.<BadTypedEdge> getEdgeSet()) {
					fail();
				}
			} catch (ClassCastException e) {
			}

			try {
				for (int i = 0; i < 10; i++) {
					BadTypedEdge bte = g.getEdge(String.format("edge-%02d", i));
					fail();
				}
			} catch (ClassCastException e) {
			}

			try {
				Iterator<BadTypedEdge> iB;

				iB = root.getLeavingEdgeIterator();
				while (iB.hasNext()) {
					BadTypedEdge e = iB.next();
					fail();
				}
			} catch (ClassCastException e) {
			} catch (UnsupportedOperationException e) {
			}

			try {
				Iterator<BadTypedEdge> iB;

				iB = root.getEnteringEdgeIterator();
				while (iB.hasNext()) {
					BadTypedEdge e = iB.next();
					fail();
				}
			} catch (ClassCastException e) {
			} catch (UnsupportedOperationException e) {
			}

			g.clear();
		}
	}

	static class TestNodeNeighborhood<A extends Node> {
		@SuppressWarnings("unused")
		TestNodeNeighborhood(Graph g) {
			g.clear();

			g.addNode("root");

			for (int i = 0; i < 10; i++) {
				g.addNode(String.format("ext-%02d", i));
				g.addEdge(String.format("edge-%02d", i), "root",
						String.format("ext-%02d", i));
			}

			Node root = g.getNode("root");

			try {
				Iterator<A> iA;

				iA = root.getBreadthFirstIterator();
				while (iA.hasNext()) {
					A node = iA.next();
				}

				iA = root.getDepthFirstIterator();
				while (iA.hasNext()) {
					A node = iA.next();
				}

				Iterator<Node> iN;

				iN = root.getBreadthFirstIterator();
				while (iN.hasNext()) {
					Node node = iN.next();
				}

				iN = root.getDepthFirstIterator();
				while (iN.hasNext()) {
					Node node = iN.next();
				}
			} catch (ClassCastException e) {
				fail();
			} catch (UnsupportedOperationException e) {
			}

			try {
				Iterator<BadTypedNode> iB;

				iB = root.getBreadthFirstIterator();
				while (iB.hasNext()) {
					BadTypedNode node = iB.next();
					fail();
				}

				iB = root.getDepthFirstIterator();
				while (iB.hasNext()) {
					BadTypedNode node = iB.next();
					fail();
				}
			} catch (ClassCastException e) {
			} catch (UnsupportedOperationException e) {
			}

			g.clear();
		}
	}

	static class TestEdgeExtremities<A extends Node> {
		@SuppressWarnings("unused")
		TestEdgeExtremities(Graph g) {
			g.clear();

			g.addNode("0");
			g.addNode("1");

			g.addEdge("A", "0", "1");

			Edge edge = g.getEdge("A");

			try {
				A nodeA;

				nodeA = edge.getNode0();
				nodeA = edge.getSourceNode();
				nodeA = edge.getNode1();
				nodeA = edge.getTargetNode();
			} catch (ClassCastException e) {
				fail();
			}

			try {
				Node nodeA;

				nodeA = edge.getNode0();
				nodeA = edge.getSourceNode();
				nodeA = edge.getNode1();
				nodeA = edge.getTargetNode();
			} catch (ClassCastException e) {
				fail();
			}

			try {
				BadTypedNode nodeA;

				nodeA = edge.getNode0();
				fail();
				nodeA = edge.getSourceNode();
				fail();
				nodeA = edge.getNode1();
				fail();
				nodeA = edge.getTargetNode();
				fail();
			} catch (ClassCastException e) {
			}

			g.clear();
		}
	}
}
