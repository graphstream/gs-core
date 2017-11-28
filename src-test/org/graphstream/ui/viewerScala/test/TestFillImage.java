package org.graphstream.ui.viewerScala.test;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.scalaViewer.ScalaGraphRenderer;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.SwingViewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class TestFillImage implements ViewerListener {
	public static void main(String[] args) {
		(new TestFillImage()).run(args);
	}
	public static final String URL_IMAGE = "file:///home/hicham/Bureau/b.png" ;

	private boolean loop = true ;
	
	private void run(String[] args) {
		MultiGraph graph  = new MultiGraph( "g1" );
		Viewer viewer = new SwingViewer( graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD );
		ViewerPipe pipeIn = viewer.newViewerPipe();
		View view   = viewer.addView( "view1", new ScalaGraphRenderer() );

		pipeIn.addAttributeSink( graph );
		pipeIn.addViewerListener( this );
		pipeIn.pump();

		graph.setAttribute( "ui.stylesheet", styleSheet );
		graph.setAttribute( "ui.antialias" );
		graph.setAttribute( "ui.quality" );
/*
		val A = graph.addNode( "A" )
		val B = graph.addNode( "B" )

		graph.addEdge( "AB1", "A", "B" )
		graph.addEdge( "AB2", "A", "B" )
		graph.addEdge( "AB3", "A", "B" )

		A("xyz") = ( -1, 0, 0 )
		B("xyz") = (  1, 0, 0 )

		A("label") = "A"
		B("label") = "B"
*/
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		Node D = graph.addNode( "D" );
		Node E = graph.addNode( "E" );
		Node F = graph.addNode( "F" );
		
//				graph.addEdges( "A", "B", "C", "A" )
//				graph.addEdges( "D", "E", "A", "D" )
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");
		graph.addEdge("DE", "D", "E");
		graph.addEdge("EA", "E", "A");
		graph.addEdge("AD", "A", "D");

		graph.addEdge( "BD", "B", "D", true );

		graph.addEdge( "CC", "C", "C" );
		graph.addEdge( "CC2", "C", "C", true );
		graph.addEdge( "FF", "F", "F" );
		graph.addEdge( "DD", "D", "D", true );
		graph.addEdge( "BC2", "B", "C" );
		graph.addEdge( "AB2", "A", "B", true );
		graph.addEdge( "AB3", "A", "B", true );
		graph.addEdge( "AF0", "A", "F" );
		graph.addEdge( "AF1", "A", "F" );
		graph.addEdge( "AF2", "A", "F" );
		graph.addEdge( "AF3", "A", "F" );

		A.setAttribute("xyz", new double[] {  0,   0,   0 });
		B.setAttribute("xyz", new double[] { -0.2, 1,   0 });
		C.setAttribute("xyz", new double[] {  0.7, 0.5, 0 });
		D.setAttribute("xyz", new double[] { -1,  -1,   0 });
		E.setAttribute("xyz", new double[] {  1,  -1,   0 });
		F.setAttribute("xyz", new double[] {  1,   0,   0 });

		A.setAttribute("label", "A");
		B.setAttribute("label", "B");
		C.setAttribute("label", "C");
		D.setAttribute("label", "D");
		E.setAttribute("label", "E");

		SpriteManager sman = new SpriteManager( graph );

		Sprite s1 = sman.addSprite( "S1" );
		Sprite s2 = sman.addSprite( "S2" );
		Sprite quit = sman.addSprite( "quit" );

		s1.attachToNode( "A" );
		s2.attachToEdge( "BC" );

		s1.setPosition( StyleConstants.Units.GU, 0.2f, 45f, 45f );
		s2.setPosition( 0f );
		quit.setPosition( StyleConstants.Units.PX, 40, 15, 0 );
		quit.setAttribute( "label", "quit" );

		double p   = 0f;
		double dir = 0.005f;
		double a   = 0f;
		double ang = 0.01f;

		while( loop ) {
//					p += dir
//					a += ang
//
//					if( p > 1f ) { dir = - dir; p = 1f; s2.attachToEdge( "BC2" ) }
//					if( p < 0f ) { dir = - dir; p = 0f; s2.attachToEdge( "BC" ) }
//					if( a > 360 ) { a = 0f; }
//
//					s1.setPosition( StyleConstants.Units.GU, 0.2f, 0f, a )
//					s2.setPosition( p )

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

	public void buttonPushed( String id ) {
		System.out.println(id);
		if( id.equals("quit") )
 			loop = false;
 		else if( id.equals("A") )
 			System.out.println( "Button A pushed" );
	}

 	public void buttonReleased( String id ) {}
 	
 // Data
  	private String styleSheet = ""
  			+ "graph {"
  			+ "	canvas-color: white;"
  			+ "	fill-mode: image-scaled-ratio-max;"
  			+ " fill-image: url('"+URL_IMAGE+"'); "
  			+ "	fill-color: white, gray;"
  			+ " padding: 60px; "
  			+ "}"
  			+ ""
  			+ "node {"
  			+ "	shape: box;"
  			+ "	size: 60px, 60px;"
  			+ "	fill-mode: image-scaled-ratio-max;"
  			+ "	fill-image: url('"+URL_IMAGE+"');"
  			+ " fill-color: white, rgb(200,200,200);"
  			+ "	stroke-mode: plain;"
  			+ "	stroke-color: rgba(100,100,100,255);"
  			+ "	stroke-width: 9px;"
  			+ "	shadow-mode: plain;"
  			+ "	shadow-width: 0px;"
  			+ "	shadow-offset: 3px, -3px;"
  			+ "	shadow-color: rgba(0,0,0,100);"
  			+ "	icon-mode: at-left;"
  			+ "	icon: url('"+URL_IMAGE+"');"
  			+ "}"
  			+ ""
  			+ "node#E {"
  			+ "	size: 90px, 90px;"
  			+ "}"
  			+ ""
  			+ "node:clicked {"
  			+ "	stroke-mode: plain;"
  			+ "	stroke-color: red;"
  			+ "}"
  			+ ""
  			+ "node:selected {"
  			+ "	stroke-mode: plain;"
  			+ "	stroke-width: 6px;"
  			+ "	stroke-color: blue;"
  			+ "}"
  			+ ""
  			+ "node#A {"
  			+ "	stroke-mode: plain;"
  			+ "	stroke-width: 9px;"
  			+ "	stroke-color: yellow;"
  			+ "	size: 80px, 30px;"
  			+ "	shape: jcomponent;"
  			+ "	jcomponent: button;"
  			+ "}"
  			+ ""
  			+ "node#F {"
  			+ "	size: 20px, 20px;"
  			+ "	icon-mode: none;"
  			+ "}"
  			+ ""
  			+ "edge {"
  			+ "	shape: cubic-curve;"
  			+ "	size: 9px;"
  			//+ "fill-color: rgb(128,128,128);"
  			+ "	fill-color: rgba(100,100,100,255);"
  			+ "	fill-mode: plain;"
  			//+ "stroke-mode: plain;"
  			//+ "stroke-color: rgb(80,80,80);"
  			//+ "stroke-width: 2px;shadow-mode: plain;shadow-color: rgba(0,0,0,50);"
  			+ "	shadow-offset: 3px, -3px;"
  			+ "	shadow-width: 0px;"
  			+ "	arrow-shape: arrow;"
  			+ "	arrow-size: 20px, 6px;"
  			+ "}"
  			+ ""
  			+ "sprite {"
  			+ "	size: 8px;"
  			+ "	shape: circle;"
  			+ "	fill-mode: gradient-radial;"
  			+ "	fill-color: red, white;"
  			+ "	stroke-mode: plain;"
  			+ "	stroke-color: rgb(100,100,100);"
  			+ "	stroke-width: 1px;"
  			+ "}"
  			+ "sprite#quit {"
  			+ "	shape: jcomponent;"
  			+ "	jcomponent: button;"
  			+ "	size: 80px, 30px;"
  			+ "}";
	
	public void mouseOver(String id){}

	public void mouseLeft(String id){}
}
