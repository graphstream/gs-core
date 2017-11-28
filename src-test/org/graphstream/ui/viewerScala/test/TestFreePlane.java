package org.graphstream.ui.viewerScala.test;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.scalaViewer.ScalaGraphRenderer;
import org.graphstream.ui.swingViewer.SwingViewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class TestFreePlane implements ViewerListener {
	public static void main(String[] args) {
		(new TestFreePlane()).run(args);
	}
	
	public static final String URL_IMAGE = "file:///home/hicham/Bureau/b.png" ;
	
	private boolean loop = true;
	
	private void run(String[] args) {
		MultiGraph graph  = new MultiGraph( "g1" );
		SwingViewer viewer = new SwingViewer( graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD );
		ViewerPipe pipeIn = viewer.newViewerPipe();
		View view   = viewer.addView( "view1", new ScalaGraphRenderer() );

		pipeIn.addAttributeSink( graph );
		pipeIn.addViewerListener( this );
		pipeIn.pump();

		graph.setAttribute( "ui.stylesheet", styleSheet );
		graph.setAttribute( "ui.antialias" );
		graph.setAttribute( "ui.quality" );

		Node root = graph.addNode( "root" );
		Node A    = graph.addNode( "A" );
		Node B    = graph.addNode( "B" );
		Node C    = graph.addNode( "C" );
		Node D    = graph.addNode( "D" );
		Node E    = graph.addNode( "E" );
		Node F    = graph.addNode( "F" );
		Node G    = graph.addNode( "G" );
		Node H    = graph.addNode( "H" );

		graph.addEdge( "rA", "root", "A" );
		graph.addEdge( "rB", "root", "B" );
		graph.addEdge( "rC", "root", "C" );
		graph.addEdge( "rD", "root", "D" );
		graph.addEdge( "rE", "root", "E" );
		graph.addEdge( "AF", "A", "F" );
		graph.addEdge( "CG", "C", "G" );
		graph.addEdge( "DH", "D", "H" );

		root.setAttribute("xyz", new double[] { 0, 0, 0});
		A.setAttribute("xyz"   , new double[] {1, 1, 0 });
		B.setAttribute("xyz"   , new double[] { 1, 0, 0 });
		C.setAttribute("xyz"   , new double[] {-1, 1, 0 });
		D.setAttribute("xyz"   , new double[] {-1, 0, 0 });
		E.setAttribute("xyz"   , new double[] {-1,-1, 0 });
		F.setAttribute("xyz"   , new double[] { 2, 1.2, 0 });
		G.setAttribute("xyz"   , new double[] {-2, 1.2, 0 });
		H.setAttribute("xyz"   , new double[] {-2,-.5, 0 });

		root.setAttribute("label", "Idea");
		A.setAttribute("label", "Topic1");
		B.setAttribute("label", "Topic2");
		C.setAttribute("label", "Topic3");
		D.setAttribute("label", "Topic4");
		E.setAttribute("label", "Topic5");
		F.setAttribute("label", "SubTopic1");
		G.setAttribute("label", "SubTopic2");
		H.setAttribute("label", "Very Long Sub Topic ...");

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
  			+ "	canvas-color: white; "
  			+ "	fill-mode: gradient-radial; "
  			+ "	fill-color: white, #EEEEEE; "
  			+ "	padding: 60px; "
  			+ "}"
  			+ ""
  			+ "node {"
  			+ "	shape: freeplane;"
  			+ "	size: 10px;"
  			+ "	size-mode: fit;"
  			+ "	fill-mode: none;"
  			+ "	stroke-mode: plain;"
  			+ "	stroke-color: grey;"
  			+ "	stroke-width: 3px;"
  			+ "	padding: 5px, 1px;"
  			+ "	shadow-mode: none;"
  			+ "	icon-mode: at-left;"
  			+ "	text-style: normal;"
  			+ "	text-font: 'Droid Sans';"
  			+ "	icon: url('"+URL_IMAGE+"');"
  			+ "}"
  			+ ""
  			+ "node:clicked {"
  			+ "	stroke-mode: plain;"
  			+ "	stroke-color: red;"
  			+ "}"
  			+ ""
  			+ "node:selected {"
  			+ "	stroke-mode: plain;"
  			+ "	stroke-color: blue;"
  			+ "}"
  			+ ""
  			+ "edge {"
  			+ "	shape: freeplane;"
  			+ "	size: 3px;"
  			+ "	fill-color: grey;"
  			+ "	fill-mode: plain;"
  			+ "	shadow-mode: none;"
  			+ "	shadow-color: rgba(0,0,0,100);"
  			+ "	shadow-offset: 3px, -3px;"
  			+ "	shadow-width: 0px;"
  			+ "	arrow-shape: arrow;"
  			+ "	arrow-size: 20px, 6px;"
  			+ "}";
  	
  	public void mouseOver(String id){}

	public void mouseLeft(String id){}
}
