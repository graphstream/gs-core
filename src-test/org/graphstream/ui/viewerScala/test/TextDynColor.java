package org.graphstream.ui.viewerScala.test;

import java.awt.Color;

import org.graphstream.graph.implementations.MultiGraph;

public class TextDynColor {
	public static void main(String[] args) {	
		System.setProperty("org.graphstream.ui", "org.graphstream.ui.scalaViewer.util.ScalaDisplay");
		
		MultiGraph g = new MultiGraph("foo");		
		
		g.setAttribute("ui.stylesheet", "node { fill-mode: dyn-plain; stroke-mode: plain; stroke-width: 1px; } edge { fill-mode: dyn-plain; }");
		g.addNode("A"); g.addNode("B"); g.addNode("C");
		g.addEdge("AB", "A", "B"); g.addEdge("BC", "B", "C"); g.addEdge("CA", "C", "A");
		g.display();
		g.getNode("A").setAttribute("ui.color", Color.RED);
		g.getNode("B").setAttribute("ui.color", Color.GREEN);
		g.getNode("C").setAttribute("ui.color", Color.BLUE);
		g.getEdge("AB").setAttribute("ui.color", Color.YELLOW);
		g.getEdge("BC").setAttribute("ui.color", Color.MAGENTA);
		g.getEdge("CA").setAttribute("ui.color", Color.CYAN);
	}
}
