package org.graphstream.ui.viewerScala.test;

import org.graphstream.graph.implementations.MultiGraph;

public class TestStrokeMode {
	public static void main(String[] args) {
		System.setProperty("org.graphstream.ui", "org.graphstream.ui.scalaViewer.util.ScalaDisplay");
		(new TestStrokeMode()).run();
	}

	private void run() {
		MultiGraph graph = new MultiGraph("stroke");

		graph.setAttribute("ui.quality");
	    graph.setAttribute("ui.antialias");
	    graph.setAttribute("ui.stylesheet", styleSheet);
	    graph.display();
	    graph.addNode("A");
	    graph.addNode("B");
	    graph.addNode("C");
	    graph.addNode("D");
	    graph.addEdge("AB", "A", "B");
	    graph.addEdge("BC", "B", "C");
	    graph.addEdge("CA", "C", "A");
	    graph.addEdge("AD", "A", "D");
	    graph.forEach(node -> node.setAttribute("ui.label", node.getId()));
	}
	
	private String styleSheet =
    		"node {"+
			"	fill-color: white;"+
			"	fill-mode: plain;"+
			"	stroke-mode: dashes;"+
			"	stroke-width: 1px;"+
			"	stroke-color: red;"+
			"	size: 20px;"+
			"}"+
			"node#C {"+
			"	stroke-mode: double;"+
			"}"+
			"node#D {"+
			"	fill-color: gray; "+
			"	stroke-mode: plain; " +
			"	stroke-color: blue; "+
			"}"+
			"edge {"+
			"	fill-mode: none;"+
			"	size: 0px;"+
			"	stroke-mode: dashes;"+
			"	stroke-width: 1px;"+
			"	stroke-color: red;"+
			"}";
}
