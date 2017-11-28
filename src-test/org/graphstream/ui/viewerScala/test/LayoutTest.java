package org.graphstream.ui.viewerScala.test;

import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.ViewerListener;

public class LayoutTest {
	public static void main(String[] args) {
		System.setProperty("org.graphstream.ui", "org.graphstream.ui.scalaViewer.util.ScalaDisplay");
		ATest test = new ATest();
		test.run( args );
	}
}

class ATest implements ViewerListener {
	private boolean loop = true;

	public void run( String[] args ) {
		Graph graph  = new MultiGraph( "g1" );
		Viewer viewer = graph.display( true );
		ViewerPipe pipeIn = viewer.newViewerPipe();
		DorogovtsevMendesGenerator gen    = new DorogovtsevMendesGenerator();

		pipeIn.addAttributeSink( graph );
		pipeIn.addViewerListener( this );
		pipeIn.pump();

		graph.setAttribute( "ui.default.title", "Layout Test" );
		graph.setAttribute( "ui.antialias" );
		graph.setAttribute( "ui.stylesheet", styleSheet );

		gen.addSink( graph );
		gen.setDirectedEdges( true, true );
		gen.begin();
		int i = 0;
		while ( i < 100 ) {
			gen.nextEvents(); 
			i += 1;
		}
		gen.end();

		graph.forEach( n -> n.setAttribute( "ui.label", "truc" ));

		while( loop ) {
			pipeIn.pump();
			sleep( 10 );
		}

		System.out.println( "bye bye" );
		System.exit(0);
	}

	protected void sleep( long ms ) {
		try {
			Thread.sleep( ms );
		} catch (InterruptedException e) { e.printStackTrace(); }
	}

// Viewer Listener Interface

	public void viewClosed( String id ) { loop = false ;}

	public void buttonPushed( String id ) {}

 	public void buttonReleased( String id ) {}

// Data
	private String styleSheet =
			"graph {"+
 				"fill-mode: gradient-radial;"+
 				"fill-color: white, gray;"+
 				"padding: 60px;"+
 			"}"+
			"node {"+
				"shape: circle;"+
				"size: 10px;"+
				"fill-mode: gradient-vertical;"+
				"fill-color: white, rgb(200,200,200);"+
				"stroke-mode: plain;"+
				"stroke-color: rgba(255,255,0,255);"+
				"stroke-width: 2px;"+
				"shadow-mode: plain;"+
				"shadow-width: 0px;"+
				"shadow-offset: 3px, -3px;"+
				"shadow-color: rgba(0,0,0,100);"+
				"text-visibility-mode: zoom-range;"+
				"text-visibility: 0, 0.9;"+
				//icon-mode: at-left;
				//icon: url('file:///home/antoine/GSLogo11d24.png');
			"}"+
			"node:clicked {"+
				"stroke-mode: plain;"+
				"stroke-color: red;"+
			"}"+
			"node:selected {"+
				"stroke-mode: plain;"+
				"stroke-width: 4px;"+
				"stroke-color: blue;"+
			"}"+
			"edge {"+
				"size: 1px;"+
				"shape: cubic-curve;"+
				"fill-color: rgb(128,128,128);"+
				"fill-mode: plain;"+
				"stroke-mode: plain;"+
				"stroke-color: rgb(80,80,80);"+
				"stroke-width: 1px;"+
				"shadow-mode: none;"+
				"shadow-color: rgba(0,0,0,50);"+
				"shadow-offset: 3px, -3px;"+
				"shadow-width: 0px;"+
				"arrow-shape: diamond;"+
				"arrow-size: 14px, 7px;"+
			"}";

	private String oldStyleSheet =
			"graph {"+
 				"fill-mode: gradient-radial;"+
 				"fill-color: white, gray;"+
 				"padding: 60px;"+
 			"}"+
			"node {"+
				"shape: box;"+
				"size: 10px, 10px;"+
				"fill-mode: gradient-vertical;"+
				"fill-color: white, rgb(200,200,200);"+
				"stroke-mode: plain;"+
				"stroke-color: rgba(255,255,0,255);"+
				"stroke-width: 2px;"+
				"shadow-mode: plain;"+
				"shadow-width: 0px;"+
				"shadow-offset: 3px, -3px;"+
				"shadow-color: rgba(0,0,0,100);"+
				"text-visibility-mode: zoom-range;"+
				"text-visibility: 0, 0.9;"+
				//icon-mode: at-left;
				//icon: url('file:///home/antoine/GSLogo11d24.png');
			"}"+
			"node:clicked {"+
				"stroke-mode: plain;"+
				"stroke-color: red;"+
			"}"+
			"node:selected {"+
				"stroke-mode: plain;"+
				"stroke-width: 4px;"+
				"stroke-color: blue;"+
			"}"+
			"edge {"+
				"size: 2px;"+
				"shape: blob;"+
				"fill-color: rgb(128,128,128);"+
				"fill-mode: plain;"+
				"stroke-mode: plain;"+
				"stroke-color: rgb(80,80,80);"+
				"stroke-width: 2px;"+
				"shadow-mode: plain;"+
				"shadow-color: rgba(0,0,0,50);"+
				"shadow-offset: 3px, -3px;"+
				"shadow-width: 0px;"+
				"arrow-shape: arrow;"+
				"arrow-size: 20px, 6px;"+
			"}";

	public void mouseOver(String id){}

	public void mouseLeft(String id){}
}
