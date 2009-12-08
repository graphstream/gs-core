/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */
package org.graphstream.ui.swing;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

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
