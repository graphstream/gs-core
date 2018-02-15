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
 * @since 2011-12-21
 * 
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph.test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Ignore;

@Ignore
public class BenchPerformance {
	Graph g;
	Runtime r;
	List<String> nodeIds;
	List<String> edgeIds;
	long start, end;

	static enum Measures {
		MEMORY, NODE_BY_ID, EDGE_BY_ID, GRAPH_NODE_IT, GRAPH_EDGE_IT, NODE_EDGE_IT, NODE_ENTERING_EDGE_IT, NODE_LEAVING_EDGE_IT, NODE_NEIGHBOR_IT, NODE_GET_EDGE, BFS_IT, DFS_IT, EDGE_BETWEEN, EDGE_FROM, EDGE_TOWARD, TRIANGLE, ADD_NODE, ADD_EDGE, REMOVE_NODE, REMOVE_EDGE
	}

	EnumMap<Measures, Long> measureValues;

	static void forceGC() {
		for (int i = 0; i < 10; i++) {
			System.gc();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	public BenchPerformance(String fileName, Graph graph) {
		r = Runtime.getRuntime();
		forceGC();
		long used1 = r.totalMemory() - r.freeMemory();
		g = graph;
		try {
			g.read(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Graph read: " + g.getNodeCount() + " nodes and " + g.getEdgeCount() + " edges");

		g.nodes().forEach(Node::clearAttributes);
		g.edges().forEach(Edge::clearAttributes);

		forceGC();
		long used2 = r.totalMemory() - r.freeMemory();
		measureValues = new EnumMap<Measures, Long>(Measures.class);
		measureValues.put(Measures.MEMORY, used2 - used1);

		nodeIds = new ArrayList<String>(g.getNodeCount());
		for (Node n : g)
			nodeIds.add(n.getId());
		// sort them to be sure that we always work with the same nodes
		Collections.sort(nodeIds);

		edgeIds = new ArrayList<String>(g.getEdgeCount());
		g.edges().forEach(e -> edgeIds.add(e.getId()));
		Collections.sort(edgeIds);
	}

	public int testAccessById() {
		int foo = 0;

		// access each node by id
		start = System.currentTimeMillis();
		for (String id : nodeIds) {
			Node n = g.getNode(id);
			if (n.hasAttribute("foo"))
				foo++;
		}
		end = System.currentTimeMillis();
		measureValues.put(Measures.NODE_BY_ID, end - start);

		// access each edge by id
		start = System.currentTimeMillis();
		for (String id : edgeIds) {
			Edge e = g.getEdge(id);
			if (e.hasAttribute("foo"))
				foo++;
		}
		end = System.currentTimeMillis();
		measureValues.put(Measures.EDGE_BY_ID, end - start);
		return foo;
	}

	public int testGraphIterators() {
		int foo = 0;

		// Iterating on all nodes
		start = System.currentTimeMillis();

		foo = (int) g.nodes().filter(n -> n.hasAttribute("foo")).count();

		//
		// Iterator<Node> nodeIt = g.getNodeIterator();
		// while (nodeIt.hasNext()) {
		// Node n = nodeIt.next();
		// if (n.hasAttribute("foo"))
		// foo++;
		// }
		//

		end = System.currentTimeMillis();
		measureValues.put(Measures.GRAPH_NODE_IT, end - start);

		// iterating on all edges
		start = System.currentTimeMillis();

		foo += (int) g.edges().filter(e -> e.hasAttribute("foo")).count();

		//
		// Iterator<Edge> edgeIt = g.getEdgeIterator();
		// while (edgeIt.hasNext()) {
		// Edge e = edgeIt.next();
		// if (e.hasAttribute("foo"))
		// foo++;
		// }
		//

		end = System.currentTimeMillis();
		measureValues.put(Measures.GRAPH_EDGE_IT, end - start);

		return foo;
	}

	public int testNodeIterators() {
		int foo = 0;

		// For each node n, iterating on all edges of n
		start = System.currentTimeMillis();

		foo += (int) g.nodes().mapToLong(n -> n.edges().filter(e -> e.hasAttribute("foo")).count()).sum();

		// Iterator<Node> nodeIt = g.getNodeIterator();
		// while (nodeIt.hasNext()) {
		// Node n = nodeIt.next();
		// Iterator<Edge> edgeIt = n.getEdgeIterator();
		// while (edgeIt.hasNext()) {
		// Edge e = edgeIt.next();
		// if (e.hasAttribute("foo"))
		// foo++;
		// }
		// }
		end = System.currentTimeMillis();
		measureValues.put(Measures.NODE_EDGE_IT, end - start);

		// For each node n, iterating on all entering edges of n
		start = System.currentTimeMillis();

		foo += (int) g.nodes().mapToLong(n -> n.enteringEdges().filter(e -> e.hasAttribute("foo")).count()).sum();

		// nodeIt = g.getNodeIterator();
		// while (nodeIt.hasNext()) {
		// Node n = nodeIt.next();
		// Iterator<Edge> edgeIt = n.getEnteringEdgeIterator();
		// while (edgeIt.hasNext()) {
		// Edge e = edgeIt.next();
		// if (e.hasAttribute("foo"))
		// foo++;
		// }
		// }
		end = System.currentTimeMillis();
		measureValues.put(Measures.NODE_ENTERING_EDGE_IT, end - start);

		// For each node n, iterating on all leaving edges of n
		start = System.currentTimeMillis();

		foo += (int) g.nodes().mapToLong(n -> n.leavingEdges().filter(e -> e.hasAttribute("foo")).count()).sum();

		// nodeIt = g.getNodeIterator();
		// while (nodeIt.hasNext()) {
		// Node n = nodeIt.next();
		// Iterator<Edge> edgeIt = n.getLeavingEdgeIterator();
		// while (edgeIt.hasNext()) {
		// Edge e = edgeIt.next();
		// if (e.hasAttribute("foo"))
		// foo++;
		// }
		// }
		end = System.currentTimeMillis();
		measureValues.put(Measures.NODE_LEAVING_EDGE_IT, end - start);

		// For each node n, iterating on all neighbors of n
		start = System.currentTimeMillis();

		foo += (int) g.nodes().mapToLong(n -> n.neighborNodes().filter(ne -> ne.hasAttribute("foo")).count()).sum();

		// nodeIt = g.getNodeIterator();
		// while (nodeIt.hasNext()) {
		// Node n = nodeIt.next();
		// Iterator<Node> neighborIt = n.getNeighborNodeIterator();
		// while (neighborIt.hasNext()) {
		// Node neighbor = neighborIt.next();
		// if (neighbor.hasAttribute("foo"))
		// foo++;
		// }
		// }
		end = System.currentTimeMillis();
		measureValues.put(Measures.NODE_NEIGHBOR_IT, end - start);

		// For each node n, iterating on all edges of n using n.getEdge(i)
		start = System.currentTimeMillis();

		foo += (int) g.nodes().mapToLong(n -> {
			int localFoo = 0;

			for (int i = 0; i < n.getDegree(); i++) {
				Edge e = n.getEdge(i);
				if (e.hasAttribute("foo"))
					localFoo++;
			}

			return localFoo;
		}).sum();

		// nodeIt = g.getNodeIterator();
		// while (nodeIt.hasNext()) {
		// Node n = nodeIt.next();
		// for (int i = 0; i < n.getDegree(); i++) {
		// Edge e = n.getEdge(i);
		// if (e.hasAttribute("foo"))
		// foo++;
		// }
		// }
		end = System.currentTimeMillis();
		measureValues.put(Measures.NODE_GET_EDGE, end - start);

		return foo;
	}

	public int testBfsDfs() {
		int foo = 0;

		// BFS from 1000 nodes
		start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			Iterator<Node> bfsIt = g.getNode(nodeIds.get(i)).getBreadthFirstIterator();
			while (bfsIt.hasNext()) {
				Node node = bfsIt.next();
				if (node.hasAttribute("foo"))
					foo++;
			}
		}
		end = System.currentTimeMillis();
		measureValues.put(Measures.BFS_IT, end - start);

		// DFS from 1000 nodes - tested only for new implementations
		// because of a bug in the old
		start = System.currentTimeMillis();
		if (g instanceof org.graphstream.graph.implementations.AbstractGraph) {
			for (int i = 0; i < 1000; i++) {
				Iterator<Node> dfsIt = g.getNode(nodeIds.get(i)).getDepthFirstIterator();
				while (dfsIt.hasNext()) {
					Node node = dfsIt.next();
					if (node.hasAttribute("foo"))
						foo++;
				}
			}
		}
		end = System.currentTimeMillis();
		measureValues.put(Measures.DFS_IT, end - start);

		return foo;
	}

	public int testTriangleCount() {
		start = System.currentTimeMillis();
		int count = 0;
		for (Node n0 : g) {
			int d = n0.getDegree();
			for (int i = 0; i < d; i++) {
				Node n1 = n0.getEdge(i).getOpposite(n0);
				String n1id = n1.getId();
				for (int j = i + 1; j < d; j++) {
					Node n2 = n0.getEdge(j).getOpposite(n0);
					if (n2.hasEdgeBetween(n1id))
						count++;
				}
			}
		}
		end = System.currentTimeMillis();
		measureValues.put(Measures.TRIANGLE, end - start);
		return count / 3;
	}

	public int testTriangleCountIndex() {
		start = System.currentTimeMillis();
		int count = 0;
		for (Node n0 : g) {
			int d = n0.getDegree();
			for (int i = 0; i < d; i++) {
				Node n1 = n0.getEdge(i).getOpposite(n0);
				if (n0.getIndex() < n1.getIndex()) {
					for (int j = i + 1; j < d; j++) {
						Node n2 = n0.getEdge(j).getOpposite(n0);
						if (n1.getIndex() < n2.getIndex() && n2.hasEdgeBetween(n1))
							count++;
					}
				}
			}
		}
		end = System.currentTimeMillis();
		measureValues.put(Measures.TRIANGLE, end - start);
		return count;
	}

	public int testFindEdge() {
		int foo = 0;

		// for each pair of nodes (n1, n2) find the edge between n1 and n2
		long start = System.currentTimeMillis();
		for (String id1 : nodeIds) {
			Node n1 = g.getNode(id1);
			for (String id2 : nodeIds) {
				Edge e = n1.getEdgeBetween(id2);
				if (e != null && e.hasAttribute("foo"))
					foo++;
			}
		}
		end = System.currentTimeMillis();
		measureValues.put(Measures.EDGE_BETWEEN, end - start);

		// for each pair of nodes (n1, n2) find the edge from n1 to n2
		start = System.currentTimeMillis();
		for (String id1 : nodeIds) {
			Node n1 = g.getNode(id1);
			for (String id2 : nodeIds) {
				Edge e = n1.getEdgeToward(id2);
				if (e != null && e.hasAttribute("foo"))
					foo++;
			}
		}
		end = System.currentTimeMillis();
		measureValues.put(Measures.EDGE_TOWARD, end - start);

		// for each pair of nodes (n1, n2) find the edge from n2 to n1
		start = System.currentTimeMillis();
		for (String id1 : nodeIds) {
			Node n1 = g.getNode(id1);
			for (String id2 : nodeIds) {
				Edge e = n1.getEdgeFrom(id2);
				if (e != null && e.hasAttribute("foo"))
					foo++;
			}
		}
		end = System.currentTimeMillis();
		measureValues.put(Measures.EDGE_FROM, end - start);

		return foo;
	}

	public void testAddRemove() {
		// add 10000 new nodes
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++)
			g.addNode("__newnode__" + i);
		end = System.currentTimeMillis();
		measureValues.put(Measures.ADD_NODE, end - start);

		// for each new node n, add 100 edges between n and old nodes
		start = System.currentTimeMillis();
		int current = 0;
		int edgeId = 0;
		for (int i = 0; i < 10000; i++) {
			String id = "__newnode__" + i;
			for (int j = 0; j < 100; j++) {
				g.addEdge("__newedge__" + edgeId, id, nodeIds.get(current));
				edgeId++;
				current++;
				if (current == nodeIds.size())
					current = 0;
			}
		}
		end = System.currentTimeMillis();
		measureValues.put(Measures.ADD_EDGE, end - start);

		// remove all the new nodes (and new edges)
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++)
			g.removeNode("__newnode__" + i);
		end = System.currentTimeMillis();
		measureValues.put(Measures.REMOVE_NODE, end - start);

		// remove 10000 edges
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++)
			g.removeEdge(edgeIds.get(i));
		end = System.currentTimeMillis();
		measureValues.put(Measures.REMOVE_EDGE, end - start);
	}

	public static void latexOutput(BenchPerformance[] tests, PrintStream ps) {
		String header = "\\begin{tabular}{|l|";
		for (int i = 0; i < tests.length; i++)
			header += "r";
		header += "|}";
		ps.println(header);
		ps.println("\\hline");

		ps.printf("%35s ", "measure");
		for (BenchPerformance t : tests)
			ps.printf("& %10s ", t.g.getId());
		ps.println("\\\\");
		ps.println("\\hline");

		for (Measures m : Measures.values()) {
			// skip if not measured
			if (!tests[0].measureValues.containsKey(m))
				continue;
			ps.printf("%35s ", "\\lstinline~" + m.name() + "~");
			for (BenchPerformance t : tests) {
				double val = t.measureValues.get(m);
				if (m == Measures.MEMORY)
					val /= 1 << 20;
				else
					val /= 1000;
				ps.printf("& %10.3f ", val);
			}
			ps.println("\\\\");
		}
		ps.println("\\hline");
		ps.println("\\end{tabular}");
	}

	public static void main(String[] args) {
		String fileName = args[0];
		// String fileName = "/home/stefan/tmp/imdb/imdb-full.dgs";
		// String fileName = "/home/stefan/tmp/yoann/test_cleaned.dgs";
		int gCount = 2;
		Graph[] graphs = new Graph[gCount];
		graphs[0] = new SingleGraph("Single");
		graphs[2] = new AdjacencyListGraph("Adj");

		BenchPerformance[] tests = new BenchPerformance[gCount];
		for (int i = 0; i < gCount; i++) {
			System.out.println("Loading graph " + graphs[i].getId());
			tests[i] = new BenchPerformance(fileName, graphs[i]);
			System.out.println("  Testing access by id");
			tests[i].testAccessById();
			System.out.println("  Testing graph iterators");
			tests[i].testGraphIterators();
			System.out.println("  Testing node iterators");
			tests[i].testNodeIterators();
			System.out.println("  Testing BFS and DFS iterators");
			tests[i].testBfsDfs();
			System.out.println("  Testing finding edges");
			tests[i].testFindEdge();
			System.out.println("  Testing triangles");
			tests[i].testTriangleCount();
			System.out.println("  Testing add / remove");
			tests[i].testAddRemove();
			tests[i].g.clear();
			tests[i].nodeIds.clear();
			tests[i].nodeIds = null;
			tests[i].edgeIds.clear();
			tests[i].edgeIds = null;

		}
		latexOutput(tests, System.out);
	}
}
