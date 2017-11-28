package org.graphstream.ui.viewerScala.test;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class TestPolys {
	public static void main(String[] args) {
		(new TestPolys()).run();
	}

	private void run() {
		System.setProperty("org.graphstream.ui", "org.graphstream.ui.scalaViewer.util.ScalaDisplay");
		SingleGraph g = new SingleGraph("Polys");
		
		Node A = g.addNode("A");
		Node B = g.addNode("B");
		Node C = g.addNode("C");
		Node D = g.addNode("D");
		
		A.setAttribute("xyz", new double[]{  1,  1, 0});
		B.setAttribute("xyz", new double[]{  1,  -1, 0});
		C.setAttribute("xyz", new double[]{  -1,  -1, 0});
		D.setAttribute("xyz", new double[]{  -1,  1, 0});
		
		A.setAttribute("ui.label", "A");
		B.setAttribute("ui.label", "B");
		C.setAttribute("ui.label", "C");
		D.setAttribute("ui.label", "D");

		Edge AB = g.addEdge("AB", "A", "B");
		Edge BC = g.addEdge("BC", "B", "C");
		Edge CD = g.addEdge("CD", "C", "D");
		Edge DA = g.addEdge("DA", "D", "A");

	
		AB.setAttribute("ui.points", new double[]{1, 1, 0,
                1.25, 0.5, 0,
                0.75, -0.5, 0,
                1, -1, 0});
		BC.setAttribute("ui.points", new double[]{1, -1, 0,
		                0.5, -0.5, 0,
		                -0.5, -0.25, 0,
		                -1, -1, 0});
		CD.setAttribute("ui.points", new double[]{-1, -1, 0,
		                -0.40, -0.5, 0,
		                -1.70, 0.5, 0,
		                -1, 1, 0});
		//DA.setAttribute("ui.points", new double[]{-1, 1, 0,
		//                -0.5, 0.75, 0,
		//                0.5, 0.25, 0,
		//                1, 1, 0});
		
		g.setAttribute("ui.stylesheet", styleSheet);
		g.setAttribute("ui.antialias");
		g.display(false);
	}
	
	String styleSheet = "edge { shape: cubic-curve; }";
}
