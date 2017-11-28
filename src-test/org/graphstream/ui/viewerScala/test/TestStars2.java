package org.graphstream.ui.viewerScala.test;

import java.util.Random;

import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

public class TestStars2 {
	public static void main(String[] args) {
		System.setProperty("org.graphstream.ui", "org.graphstream.ui.scalaViewer.util.ScalaDisplay");
		(new TestStars2()).run();
	}

	private void run() {
		SingleGraph graph  = new SingleGraph("Stars !");
		double x0     = 0.0;
		double x1     = 0.0;
		double width  = 100.0;
		double height = 20.0;
		int n      = 500;
		Random random = new Random();
		double minDis = 4.0;
		double sizeMx = 10.0;
		        
		graph.setAttribute("ui.stylesheet", styleSheet);
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
		
		Viewer viewer = graph.display(false);
		ViewPanel view   = (ViewPanel)viewer.getDefaultView();
		
		view.resizeFrame(1000, (int)(1200*(height/width)));
		
		for (int i = 0 ; i < n ; i++) {
			Node node = graph.addNode(i+"");
			node.setAttribute("xyz", (random.nextDouble()*width), (random.nextDouble()*height), 0);
			node.setAttribute("ui.size", (random.nextDouble()*sizeMx));
		}
		
		graph.nodes().forEach( node -> {
			Point3 pos = new Point3(GraphPosLengthUtils.nodePosition(node));
	        
	        graph.nodes().forEach( otherNode -> {
	            if(otherNode != node) {
	            	Point3 otherPos = new Point3(GraphPosLengthUtils.nodePosition(otherNode));
	                double dist     = otherPos.distance(pos);
	                
	                if(dist < minDis) {
	                    if(! node.hasEdgeBetween(otherNode.getId())) {
	                    	try {
	                    		graph.addEdge(node.getId()+"--"+otherNode.getId(), node.getId(), otherNode.getId());
	                    	}
	                    	catch(IdAlreadyInUseException e) {
	                    		graph.addEdge(node.getId()+"--"+otherNode.getId()+"-2", node.getId(), otherNode.getId());
	                    	}
	                    }
	                }
	            }
		        
			});
		});
	}
	
	private String styleSheet = 
			"graph {"+
			"	canvas-color: black;"+
			"	fill-mode: gradient-vertical;"+
			"	fill-color: black, #004;"+
			"	padding: 20px;"+
			"}"+ 
			"node {"+
			"	shape: circle;"+
			"	size-mode: dyn-size;"+
			"	size: 10px;"+
			"	fill-mode: gradient-radial;"+
			"	fill-color: #FFFC, #FFF0;"+
			"	stroke-mode: none;"+ 
			"	shadow-mode: gradient-radial;"+
			"	shadow-color: #FFF5, #FFF0;"+
			"	shadow-width: 5px;"+
			"	shadow-offset: 0px, 0px;"+
			"}"+
			"node:clicked {"+
			"	fill-color: #F00A, #F000;"+
			"}"+
			"node:selected {"+
			"	fill-color: #00FA, #00F0;"+
			"}"+
			"edge {"+
			"	shape: L-square-line;"+
			"	size: 1px;"+
			"	fill-color: #FFF3;"+
			"	fill-mode: plain;"+
			"	arrow-shape: none;"+
			"}"+
			"sprite {"+
			"	shape: circle;"+
			"	fill-mode: gradient-radial;"+
			"	fill-color: #FFF8, #FFF0;"+
			"}";
}
