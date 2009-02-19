package org.miv.graphstream.ui.swing.test;

import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.MultiGraph;

public class TestMultiGraph
{
	public static void main( String args[] )
	{
		new TestMultiGraph();
	}
	
	public TestMultiGraph()
	{
		Graph graph = new MultiGraph( "MultiGraph" );
		
		graph.display();
		graph.addAttribute( "stylesheet", styleSheet );
		
		graph.setAutoCreate( true );
		graph.setStrictChecking( false );
		graph.addEdge( "AB1", "B", "A", true );
		graph.addEdge( "AB2", "A", "B", true );
		graph.addEdge( "AB3", "A", "B", true );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA1", "C", "A", true );
		graph.addEdge( "CA2", "A", "C" );
		graph.addEdge( "AA1", "A", "A" );
		graph.addEdge( "AA2", "A", "A" );
		graph.addEdge( "BB", "B", "B" );
		graph.getNode("A").setAttribute( "label", "A" );
		graph.getNode("B").setAttribute( "label", "B" );
		graph.getNode("C").setAttribute( "label", "C" );
	}
	
	protected String styleSheet = "graph { border-width: 40px; } node { border-width: 0px; } edge { border-width: 0px; }";
}
