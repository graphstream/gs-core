package org.graphstream.ui.viewerScala.test;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swing.SwingFullGraphRenderer;
import org.graphstream.ui.swingViewer.SwingViewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class TestArrows {
	public static void main(String[] args) {
		(new BTest()).run();
	}
}

class BTest implements ViewerListener {
	boolean loop = true;
	
	public static final String URL_IMAGE = "file:///home/hicham/Bureau/b.png" ;

	
	public void run(){
		try {
		MultiGraph graph  = new MultiGraph( "TestSize" );
		Viewer viewer = new SwingViewer( graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD );
		ViewerPipe pipeIn = viewer.newViewerPipe();

		View view   = viewer.addView("view1", new SwingFullGraphRenderer() );

		pipeIn.addAttributeSink( graph );
		pipeIn.addViewerListener( this );
		pipeIn.pump();

		graph.setAttribute( "ui.stylesheet", styleSheet );
		graph.setAttribute( "ui.antialias" );
		graph.setAttribute( "ui.quality" );

		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		Node D = graph.addNode( "D" );
		Node E = graph.addNode( "E" );
		Node F = graph.addNode( "F" );

		Edge AB = graph.addEdge( "AB", "A", "B", true );
		Edge BC = graph.addEdge( "BC", "B", "C", true );
		Edge CD = graph.addEdge( "CD", "C", "D", true );
		Edge DA = graph.addEdge( "DA", "D", "A", true );
		Edge BB = graph.addEdge( "BB", "B", "B", true );
		Edge DE = graph.addEdge( "DE", "D", "E", true );
		Edge DF = graph.addEdge( "DF", "D", "F", true );
		Edge CF = graph.addEdge( "CF", "C", "F", true );

		A.setAttribute("xyz", new double[] { 0, 1, 0 });
		B.setAttribute("xyz", new double[] { 1, 0.8, 0 });
		C.setAttribute("xyz", new double[] { 0.8, 0, 0 });
		D.setAttribute("xyz", new double[] { 0, 0, 0 });
		E.setAttribute("xyz", new double[] { 0.5, 0.5, 0 });
		F.setAttribute("xyz", new double[] { 0.5, 0.25, 0 });

		A.setAttribute("label", "A");
		B.setAttribute("label", "Long label ...");
		C.setAttribute("label", "C");
		D.setAttribute("label", "A long label ...");
		E.setAttribute("label", "Another very long label");
		F.setAttribute("label", "F");

		double size = 20f;
		double sizeInc = 1f;
			
		while( loop ) {
			pipeIn.pump();
			sleep( 40 );
			A.setAttribute( "ui.size", size );

			size += sizeInc;

			if( size > 50 ) {
				sizeInc = -1f; size = 50f;
			}
			else if( size < 20 ) {
				sizeInc = 1f; size = 20f;
			}
		}
		}
		catch (Exception e) {
			e.printStackTrace();
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
 	private String styleSheet = ""
 			+ "graph {"
 			+ "	canvas-color: white;  "
 			+ "	fill-mode: gradient-radial; "
 			+ "	fill-color: white, #EEEEEE; 	"
 			+ "	padding: 60px; "
 			+ "}"
 			+ ""	
 			+ "node {shape: circle;"
 			+ " size: 30px;"
 			+ " fill-mode: plain;"
 			+ " fill-color: #CCCC;"
 			+ " stroke-mode: plain; "
 			+ " stroke-color: black; "
 			+ " stroke-width: 1px; } "
 			+ ""
 			+ "node:clicked { "
 			+ "	stroke-mode: plain;"
 			+ "	stroke-color: red;"
 			+ "}"
 			+ ""
 			+ "node:selected { "
 			+ "	stroke-mode: plain; "
 			+ "	stroke-color: blue; "
 			+ "}"
 			+ ""
 			+ "node#A { "
 			+ "	shape: rounded-box; "
 			+ "	size-mode: dyn-size;"
 			+ " size: 10px; } "
 			+ ""
 			+ "node#B { "
 			+ "	shape: circle;"
 			+ " size-mode: fit; "
 			+ "	size: 50px; "
 			+ "	padding: 10px; "
 			+ "} "
 			+ ""
 			+ "node#C { 	"
 			+ " shape: box; 	"
 			+ " size: 50px; "
 			+ "} "
 			+ ""
 			+ "node#D { "
 			+ " shape: box; "
 			+ " size-mode: fit; "
 			+ " padding: 5px;"
 			+ "}"
 			+ ""
 			+ "node#E {"
 			+ "	shape: circle; "
 			+ "	size-mode: fit;"
 			+ "	size: 20px, 10px;"
 			+ "	padding: 6px;"
 			+ " }"
 			+ ""
 			+ "edge { 	shape: line; size: 1px; fill-color: grey; 	fill-mode: plain; 	arrow-shape: arrow; arrow-size: 20px, 5px; } "
 			+ "edge#BC { 	shape: cubic-curve; }"
 			+ "edge#AB { 	shape: cubic-curve; }"
 			+ "edge#CF { 	shape: cubic-curve; arrow-shape: image; arrow-image: url('"+URL_IMAGE+"'); } "
 			+ "edge#DF { 	arrow-shape: image; arrow-image: url('"+URL_IMAGE+"'); } " ;
 	public void mouseOver(String id){}

	public void mouseLeft(String id){}
}