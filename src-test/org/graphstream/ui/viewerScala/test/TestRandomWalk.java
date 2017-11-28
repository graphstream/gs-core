package org.graphstream.ui.viewerScala.test;

import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.randomWalk.RandomWalk;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

public class TestRandomWalk {
	public static void main(String[] args) {
		System.setProperty("org.graphstream.ui", "org.graphstream.ui.scalaViewer.util.ScalaDisplay");
		(new TestRandomWalk()).run();
	}

	private void run() {
		MultiGraph graph = new MultiGraph("random walk");
		DorogovtsevMendesGenerator gen   = new DorogovtsevMendesGenerator();
		RandomWalk rwalk = new RandomWalk();
    	
    	gen.addSink(graph);
    	gen.begin();
    	for(int i = 0 ; i < 400 ; i++) {
    		gen.nextEvents();
    	}
    	gen.end();

    	graph.setAttribute("ui.stylesheet", styleSheet);
    	graph.setAttribute("ui.quality");
    	graph.setAttribute("ui.antialias");
    	graph.display();

    	rwalk.setEntityCount(graph.getNodeCount()*2);
    	rwalk.setEvaporation(0.97);
    	rwalk.setEntityMemory(40);
    	rwalk.init(graph);
    	for(int i = 0 ; i < 3000 ; i++) {
    	    rwalk.compute();
    	    if(i%100==0){
    	        System.err.println("step "+i);
    	    	updateGraph(graph, rwalk);
    	    }
    	//    Thread.sleep(100)
    	}
    	rwalk.terminate();
    	updateGraph(graph, rwalk);
    	graph.setAttribute("ui.screenshot", "randomWalk.png");
    }
    
	public void updateGraph(Graph graph, RandomWalk rwalk) {
		float mine[] = {Float.MAX_VALUE};
        float maxe[] = {Float.MIN_VALUE};
    	
    	graph.edges().forEach ( edge -> {
    		float passes = (float) rwalk.getPasses(edge);
    	    if(passes>maxe[0]) 
    	    	maxe[0] = passes;
    	    if(passes<mine[0]) 
    	    	mine[0] = passes;
    	});
    	
    	graph.edges().forEach ( edge -> {
    		float passes = (float) rwalk.getPasses(edge);
    	    float color  = ((passes-mine[0])/(maxe[0]-mine[0]));
    		edge.setAttribute("ui.color", color);
    	});
	}
	
	public String styleSheet = 
			"edge {"+
			"size: 2px;"+
			"fill-color: red, yellow, green, #444;"+
			"fill-mode: dyn-plain;"+
		"}"+
		"node {"+
			"size: 6px;"+
			"fill-color: #444;"+
		"}";
}
