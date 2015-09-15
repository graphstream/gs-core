package org.graphstream.graph.test;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SimpleAdjacencyListGraph;
import org.graphstream.util.GraphDiff;

public class DemoGraphDiff {
	
	public void graphDiff() throws Exception {
		Graph g1 = new SimpleAdjacencyListGraph("g1");
		Graph g2 = new SimpleAdjacencyListGraph("g2");

		Node a1 = g1.addNode("A");
		a1.setAttribute("attr1", "test");
		a1.setAttribute("attr2", 10.0);
		a1.setAttribute("attr3", 12);

		Node a2 = g2.addNode("A");
		a2.setAttribute("attr1", "test1");
		a2.setAttribute("attr2", 10.0);
		g2.addNode("B");
		g2.addNode("C");

		GraphDiff diff = new GraphDiff(g2, g1);
		System.out.println(diff);
	}
}
