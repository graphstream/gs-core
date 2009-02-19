package org.miv.graphstream.ui.swing;

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
		Graph graph = new MultiGraph( "multi-graph!", false, true );
		
		graph.display();
		
		graph.addNode( "A" );
		graph.addNode( "B" );
		graph.addNode( "C" );
		
		graph.addEdge( "A->B", "A", "B", true );
		graph.addEdge( "B->A", "B", "A", true );
		graph.addEdge( "B->C", "B", "C", true );
		graph.addEdge( "C->B", "C", "B", true );
		graph.addEdge( "C->A", "C", "A", true );
		graph.addEdge( "A->C", "A", "C", true );
		
		graph.addEdge( "A--B", "A", "B" );
		graph.addEdge( "B--C", "B", "C" );
		graph.addEdge( "C--A", "C", "A" );
	}
}
