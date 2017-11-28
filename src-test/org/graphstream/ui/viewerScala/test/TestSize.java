package org.graphstream.ui.viewerScala.test;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swing.SwingFullGraphRenderer;
import org.graphstream.ui.swingViewer.SwingViewer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class TestSize implements ViewerListener {
	public static void main(String[] args) {
		(new TestSize()).run();
	}

	boolean loop = true ;
	private void run() {
		MultiGraph graph = new MultiGraph("Test Size");
		
		Viewer viewer = new SwingViewer( graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD );
		ViewerPipe pipeIn = viewer.newViewerPipe();
		viewer.addView( "view1", new SwingFullGraphRenderer() );
	
		pipeIn.addAttributeSink( graph );
		pipeIn.addViewerListener( this );
		pipeIn.pump();;

		graph.setAttribute( "ui.stylesheet", styleSheet );
		graph.setAttribute( "ui.antialias" );
		graph.setAttribute( "ui.quality" );
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		Node D = graph.addNode( "D" );
		
		Edge AB = graph.addEdge( "AB", "A", "B", true );
		Edge BC = graph.addEdge( "BC", "B", "C", true );
		Edge CD = graph.addEdge( "CD", "C", "D", true );
		Edge DA = graph.addEdge( "DA", "D", "A", true );
		Edge BB = graph.addEdge( "BB", "B", "B", true );
		
		A.setAttribute("xyz", new double[] { 0, 1, 0 });
		B.setAttribute("xyz", new double[] { 1, 1, 0 });
		C.setAttribute("xyz", new double[] { 1, 0, 0 });
		D.setAttribute("xyz", new double[] { 0, 0, 0 });
		
		AB.setAttribute("ui.label", "AB");
		BC.setAttribute("ui.label", "A Long label ...");
		CD.setAttribute("ui.label", "CD");
		BB.setAttribute("ui.label", "BB");
		
		SpriteManager sm = new SpriteManager( graph );
		Sprite S1 = sm.addSprite("S1");
		
		S1.attachToNode( "C" );
		S1.setPosition( StyleConstants.Units.PX, 40, 45, 0 );

		double size = 20f;
		double sizeInc = 1f;

		while( loop ) {
			pipeIn.pump();
			sleep( 40 );
			A.setAttribute( "ui.size", size );
//			A.setAttribute( "ui.size", "%spx".format( size ) )
			BC.setAttribute( "ui.size", size );
			S1.setAttribute( "ui.size", size );

			size += sizeInc;

			if( size > 50 ) {
				sizeInc = -1f; size = 50f;
			} else if( size < 20 ) {
				sizeInc = 1f; size = 20f;
			}
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

	public void buttonPushed( String id ) {
		if( id == "quit" )
 			loop = false;
 		else if( id == "A" )
 			System.out.println( "Button A pushed" );
	}

 	public void buttonReleased( String id ) {}
	 
	 // Data
	private String styleSheet = 
			"graph {"+
			"	canvas-color: white;"+
			"		fill-mode: gradient-radial;"+
			"		fill-color: white, #EEEEEE;"+
			"		padding: 60px;"+
			"	}"+
			"node {"+
			"	shape: circle;"+
			"	size: 20px;"+
			"	fill-mode: plain;"+
			"	fill-color: #CCC;"+
			"	stroke-mode: plain;"+
			"	stroke-color: black;"+
			"	stroke-width: 1px;"+
			"}"+
			"node:clicked {"+
			"	stroke-mode: plain;"+
			"	stroke-color: red;"+
			"}"+
			"node:selected {"+
			"	stroke-mode: plain;"+
			"	stroke-color: blue;"+
			"}"+
			"node#A {"+
			"	size-mode: dyn-size;"+
			"	size: 10px;"+
			"}"+
			"node#D {"+
			"	shape: box;"+
			"	size-mode: fit;"+
			"	padding: 5px;"+
			"}"+
			"edge {"+
			"	shape: blob;"+
			"	size: 1px;"+
			"	fill-color: grey;"+
			"	fill-mode: plain;"+
			"	arrow-shape: arrow;"+
			"	arrow-size: 10px, 3px;"+
			"}"+
			"edge#BC {"+
			"	size-mode: dyn-size;"+
			"	size: 1px;"+
			"}"+
			"sprite {"+
			"	shape: circle;"+
			"	fill-color: #FCC;"+
			"	stroke-mode: plain;"+
			"	stroke-color: black;"+
			"}"+
			"sprite:selected {"+
			"	stroke-color: red;"+
			"}"+
			"sprite#S1 {"+
			"	size-mode: dyn-size;"+
			"}";
	public void mouseOver(String id){}

	public void mouseLeft(String id){}
}
	
