package org.miv.graphstream.ui.swing.test;

import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;

public class TutorialUI002b
{
	public static void main( String args[] ) {
		new TutorialUI002b();
	}
	
	public TutorialUI002b() {
		Graph graph = new DefaultGraph( false, true );

		GraphViewerRemote gvr = graph.display();
		
		gvr.setQuality( 4 );

		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		A.addAttribute( "label", "A" );
		B.addAttribute( "label", "B" );
		C.addAttribute( "label", "C" );

		A.addAttribute( "ui.style", "color:grey;text-color:black;border-width:1;border-color:black;" );
	}
}