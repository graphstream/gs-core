/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.graph.test;

import java.util.HashSet;
import java.util.Iterator;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.MultiGraph;

/**
 * Test the multi-graph implementation.
 * 
 * @author Antoine Dutot
 */
public class TestMultiGraph extends TestBase
{
	public static void main( String args[] )
	{
		new TestMultiGraph();
	}
	
	public TestMultiGraph()
	{
		test1();
		printTestResults( "Test 1 finished." );
		Graph g = test2();
		printTestResults( "Test 2 finished." );
		test3( g );
		printTestResults( "Test 3 finished." );
		test4( g );
		printTestResults( "Test 4 finished." );
		test5( g );
		printTestResults( "Test 5 finished. That's all." );
	}
	
	protected void test1()
	{
		// Test adding several undirected edges between two same nodes.
		
		Graph graph = new MultiGraph( "multi" );
		
		graph.addNode( "A" );
		check( graph.getNodeCount() == 1, "test  bad node count" );
		graph.addNode( "B" );
		check( graph.getNodeCount() == 2, "test  bad node count" );
		graph.addNode( "C" );
		check( graph.getNodeCount() == 3, "test  bad node count" );
		
		graph.addEdge( "AB1", "A", "B", false );
		check( graph.getEdgeCount() == 1, "test  bad edge count" );
		graph.addEdge( "BC1", "B", "C", false );
		check( graph.getEdgeCount() == 2, "test  bad edge count" );
		graph.addEdge( "CA1", "C", "A", false );
		check( graph.getEdgeCount() == 3, "test  bad edge count" );
		
		graph.addEdge( "AB2", "A", "B", false );
		check( graph.getEdgeCount() == 4, "test  bad edge count" );
		graph.addEdge( "BC2", "B", "C", false );
		check( graph.getEdgeCount() == 5, "test  bad edge count" );
		graph.addEdge( "CA2", "C", "A", false );
		check( graph.getEdgeCount() == 6, "test  bad edge count" );
		
		check( graph.getNode("A").getDegree() == 4, "bad node degree" );
		check( graph.getNode("A").getOutDegree() == 4, "bad node degree" );
		check( graph.getNode("A").getInDegree() == 4, "bad node degree" );
	}
	
	protected Graph test2()
	{
		// Test adding several directed edges between two same nodes.
		
		Graph graph = new MultiGraph( "multi" );
		
		graph.addNode( "A" );
		graph.addNode( "B" );
		graph.addNode( "C" );
		
		// A triangle graph.
		// Each node has two edge leaving and two edges entering.
		
		graph.addEdge( "AB1", "A", "B", true );
		check( graph.getEdgeCount() == 1, "bad graph edge count" );
		check( graph.getEdge("AB1").isDirected(), "bad edge direction" );
		check( graph.getNode("A").getDegree() == 1, "bad node degree" );
		check( graph.getNode("A").getInDegree() == 0, "bad in node degree" );
		check( graph.getNode("A").getOutDegree() == 1, "bad out node degree" );
		check( graph.getNode("B").getDegree() == 1, "bad node degree" );
		check( graph.getNode("B").getInDegree() == 1, "bad in node degree" );
		check( graph.getNode("B").getOutDegree() == 0, "bad out node degree" );

		graph.addEdge( "BA1", "B", "A", true );
		check( graph.getEdgeCount() == 2, "bad graph edge count" );
		check( graph.getEdge("BA1").isDirected(), "bad edge direction" );
		check( graph.getNode("A").getDegree() == 2, "bad node degree" );
		check( graph.getNode("A").getInDegree() == 1, "bad in node degree" );
		check( graph.getNode("A").getOutDegree() == 1, "bad out node degree" );
		check( graph.getNode("B").getDegree() == 2, "bad node degree" );
		check( graph.getNode("B").getInDegree() == 1, "bad in node degree" );
		check( graph.getNode("B").getOutDegree() == 1, "bad out node degree" );
		
		graph.addEdge( "BC1", "B", "C", true );
		check( graph.getEdgeCount() == 3, "bad graph edge count" );
		check( graph.getEdge("BC1").isDirected(), "bad edge direction" );
		check( graph.getNode("B").getDegree() == 3, "bad node degree" );
		check( graph.getNode("B").getInDegree() == 1, "bad in node degree" );
		check( graph.getNode("B").getOutDegree() == 2, "bad out node degree" );
		check( graph.getNode("C").getDegree() == 1, "bad node degree" );
		check( graph.getNode("C").getInDegree() == 1, "bad in node degree" );
		check( graph.getNode("C").getOutDegree() == 0, "bad out node degree" );
		
		graph.addEdge( "CB1", "C", "B", true );
		check( graph.getEdgeCount() == 4, "bad graph edge count" );
		check( graph.getEdge("CB1").isDirected(), "bad edge direction" );
		check( graph.getNode("B").getDegree() == 4, "bad node degree" );
		check( graph.getNode("B").getInDegree() == 2, "bad in node degree" );
		check( graph.getNode("B").getOutDegree() == 2, "bad out node degree" );
		check( graph.getNode("C").getDegree() == 2, "bad node degree" );
		check( graph.getNode("C").getInDegree() == 1, "bad in node degree" );
		check( graph.getNode("C").getOutDegree() == 1, "bad out node degree" );
		
		graph.addEdge( "CA1", "C", "A", true );
		check( graph.getEdgeCount() == 5, "bad graph edge count" );
		check( graph.getEdge("CA1").isDirected(), "bad edge direction" );
		check( graph.getNode("C").getDegree() == 3, "bad node degree" );
		check( graph.getNode("C").getInDegree() == 1, "bad in node degree" );
		check( graph.getNode("C").getOutDegree() == 2, "bad out node degree" );
		check( graph.getNode("A").getDegree() == 3, "bad node degree" );
		check( graph.getNode("A").getInDegree() == 2, "bad in node degree" );
		check( graph.getNode("A").getOutDegree() == 1, "bad out node degree" );
		
		graph.addEdge( "AC1", "A", "C", true );
		check( graph.getEdgeCount() == 6, "bad graph edge count" );
		check( graph.getEdge("AC1").isDirected(), "bad edge direction" );
		check( graph.getNode("A").getDegree() == 4, "bad node degree" );
		check( graph.getNode("A").getInDegree() == 2, "bad in node degree" );
		check( graph.getNode("A").getOutDegree() == 2, "bad out node degree" );
		check( graph.getNode("B").getDegree() == 4, "bad node degree" );
		check( graph.getNode("B").getInDegree() == 2, "bad in node degree" );
		check( graph.getNode("B").getOutDegree() == 2, "bad out node degree" );
		check( graph.getNode("C").getDegree() == 4, "bad node degree" );
		check( graph.getNode("C").getInDegree() == 2, "bad in node degree" );
		check( graph.getNode("C").getOutDegree() == 2, "bad out node degree" );
		
		// Now add even more edges. Each node adds a second edge toward its
		// successor.
		
		graph.addEdge( "AB2", "A", "B", true );
		check( graph.getEdgeCount() == 7, "bad graph edge count" );
		check( graph.getEdge("AB2").isDirected(), "bad edge direction" );
		check( graph.getNode("A").getDegree() == 5, "bad node degree" );
		check( graph.getNode("A").getInDegree() == 2, "bad in node degree" );
		check( graph.getNode("A").getOutDegree() == 3, "bad out node degree" );
		check( graph.getNode("B").getDegree() == 5, "bad node degree" );
		check( graph.getNode("B").getInDegree() == 3, "bad in node degree" );
		check( graph.getNode("B").getOutDegree() == 2, "bad out node degree" );
		
		graph.addEdge( "BC2", "B", "C", true );
		check( graph.getEdgeCount() == 8, "bad graph edge count" );
		check( graph.getEdge("BC2").isDirected(), "bad edge direction" );
		check( graph.getNode("B").getDegree() == 6, "bad node degree" );
		check( graph.getNode("B").getInDegree() == 3, "bad in node degree" );
		check( graph.getNode("B").getOutDegree() == 3, "bad out node degree" );
		check( graph.getNode("C").getDegree() == 5, "bad node degree" );
		check( graph.getNode("C").getInDegree() == 3, "bad in node degree" );
		check( graph.getNode("C").getOutDegree() == 2, "bad out node degree" );
		
		graph.addEdge( "CA2", "C", "A", true );
		check( graph.getEdgeCount() == 9, "bad graph edge count" );
		check( graph.getEdge("CA2").isDirected(), "bad edge direction" );
		check( graph.getNode("C").getDegree() == 6, "bad node degree" );
		check( graph.getNode("C").getInDegree() == 3, "bad in node degree" );
		check( graph.getNode("C").getOutDegree() == 3, "bad out node degree" );
		check( graph.getNode("A").getDegree() == 6, "bad node degree" );
		check( graph.getNode("A").getInDegree() == 3, "bad in node degree" );
		check( graph.getNode("A").getOutDegree() == 3, "bad out node degree" );
		
		return graph;
	}
	
	protected void test3( Graph graph )
	{
		// Test iterators on the multi-graph.
		
		// On the whole graph.
		
		Iterator<? extends Edge> edges = graph.getEdgeIterator();
		HashSet<String> elements = new HashSet<String>();
		int count = 0;
		
		while( edges.hasNext() )
		{
			elements.add( edges.next().getId() );
			count++;
		}
		
		check( count == 9, "bad edge iterator count" );
		check( elements.size() == 9, "bad edge iterator count" );
		check( elements.contains( "AB1" ), "bad edge iterator" );
		check( elements.contains( "AB2" ), "bad edge iterator" );
		check( elements.contains( "BA1" ), "bad edge iterator" );
		check( elements.contains( "BC1" ), "bad edge iterator" );
		check( elements.contains( "BC2" ), "bad edge iterator" );
		check( elements.contains( "CB1" ), "bad edge iterator" );
		check( elements.contains( "CA1" ), "bad edge iterator" );
		check( elements.contains( "CA2" ), "bad edge iterator" );
		check( elements.contains( "AC1" ), "bad edge iterator" );
		
		// On nodes.
		
		Node node = graph.getNode("A");
		edges = node.getEdgeIterator();
		count = 0;
		elements.clear();
		
		while( edges.hasNext() )
		{
			elements.add( edges.next().getId() );
			count++;
		}
		
		check( count == 6, "bad node edge-iterator count" );
		check( elements.size() == 6, "bad node edge-iterator count" );
		check( elements.contains( "AB1" ), "bad node edge-iterator" );
		check( elements.contains( "AB2" ), "bad node edge-iterator" );
		check( elements.contains( "CA1" ), "bad node edge-iterator" );
		check( elements.contains( "CA2" ), "bad node edge-iterator" );
		check( elements.contains( "AC1" ), "bad node edge-iterator" );
		check( elements.contains( "BA1" ), "bad node edge-iterator" );
		
		edges = node.getEnteringEdgeIterator();
		count = 0;
		elements.clear();

		while( edges.hasNext() )
		{
			elements.add( edges.next().getId() );
			count++;
		}
		
		check( count == 3, "bad node entering-edge-iterator count" );
		check( elements.size() == 3, "bad node entering-edge-iterator count" );
		check( elements.contains( "BA1" ), "bad node entering-edge-iterator" );
		check( elements.contains( "CA1" ), "bad node entering-edge-iterator" );
		check( elements.contains( "CA2" ), "bad node entering-edge-iterator" );
		
		edges = node.getLeavingEdgeIterator();
		count = 0;
		elements.clear();

		while( edges.hasNext() )
		{
			elements.add( edges.next().getId() );
			count++;
		}
		
		check( count == 3, "bad node leaving-edge-iterator count" );
		check( elements.size() == 3, "bad node leaving-edge-iterator count" );
		check( elements.contains( "AB1" ), "bad node leaving-edge-iterator" );
		check( elements.contains( "AB2" ), "bad node leaveing-edge-iterator" );
		check( elements.contains( "AC1" ), "bad node leaveing-edge-iterator" );
	}
	
	protected void test4( Graph graph )
	{
		Node node = graph.getNode("A");
		
		check( node.hasEdgeToward( "B" ), "bad edge iterator remove" );
		check( node.hasEdgeToward( "C" ), "bad edge iterator remove" );
		
		// Test element removing.
		
		Iterator<? extends Edge> edges = graph.getEdgeIterator();
		int count = 0;
		int removeCount = 0;
		
		while( edges.hasNext() )
		{
			Edge edge = edges.next();
			
			if( edge.getId().endsWith( "2" ) )
			{
				edges.remove();
				removeCount++;
			}
			
			count++;
		}
		
		check( count == 9, "bad edge iterator" );
		check( removeCount == 3, "bad edge iterator remove" );
		check( graph.getEdgeCount() == 6, "bad edge iterator remove" );
		check( graph.getNodeCount() == 3, "bad edge iterator remove" );
		check( graph.getNode("A").getDegree() == 4, "bad edge iterator remove" );
		check( graph.getNode("A").getInDegree() == 2, "bad edge iterator remove" );
		check( graph.getNode("A").getOutDegree() == 2, "bad edge iterator remove" );
		check( graph.getNode("B").getDegree() == 4, "bad edge iterator remove" );
		check( graph.getNode("B").getInDegree() == 2, "bad edge iterator remove" );
		check( graph.getNode("B").getOutDegree() == 2, "bad edge iterator remove" );
		check( graph.getNode("C").getDegree() == 4, "bad edge iterator remove" );
		check( graph.getNode("C").getInDegree() == 2, "bad edge iterator remove" );
		check( graph.getNode("C").getOutDegree() == 2, "bad edge iterator remove" );
		
		// Now check graph integrity.
		
		edges = graph.getEdgeIterator();
		count = 0;
		HashSet<String> elements = new HashSet<String>();
		
		while( edges.hasNext() )
		{
			elements.add( edges.next().getId() );
			count++;
		}
		
		check( count == 6, "bad edge iterator remove" );
		check( elements.size() == 6, "bad edge iterator remove" );
		check( elements.contains( "AB1" ), "bad edge iterator remove element" );
		check( elements.contains( "BA1" ), "bad edge iterator remove element" );
		check( elements.contains( "BC1" ), "bad edge iterator remove element" );
		check( elements.contains( "CB1" ), "bad edge iterator remove element" );
		check( elements.contains( "CA1" ), "bad edge iterator remove element" );
		check( elements.contains( "AC1" ), "bad edge iterator remove element" );
		check( graph.getEdge( "AB1" ) != null, "bad edge iterator remove element" );
		check( graph.getEdge( "BA1" ) != null, "bad edge iterator remove element" );
		check( graph.getEdge( "BC1" ) != null, "bad edge iterator remove element" );
		check( graph.getEdge( "CB1" ) != null, "bad edge iterator remove element" );
		check( graph.getEdge( "CA1" ) != null, "bad edge iterator remove element" );
		check( graph.getEdge( "AC1" ) != null, "bad edge iterator remove element" );
		check( graph.getEdge( "AB2" ) == null, "bad edge iterator remove element" );
		check( graph.getEdge( "CA2" ) == null, "bad edge iterator remove element" );
		check( graph.getEdge( "BC2" ) == null, "bad edge iterator remove element" );
		
		// Check node integrity.
		
		/*Node*/ node = graph.getNode("A");
		
		check( node.getDegree() == 4, "bad node degree after remove" );
		check( node.getInDegree() == 2, "bad node in-degree after remove" );
		check( node.getOutDegree() == 2, "bad node out-degree after remove" );
		check( node.hasEdgeToward( "B" ), "bad edge iterator remove" );
		check( node.hasEdgeToward( "C" ), "bad edge iterator remove" );
		check( node.hasEdgeFrom( "B" ), "bad edge iterator remove" );
		check( node.hasEdgeFrom( "C" ), "bad edge iterator remove" );
	}
	
	protected void test5( Graph graph )
	{
		graph.removeNode( "A" );
		check( graph.getNodeCount() == 2, "bad node remove" );
		check( graph.getEdgeCount() == 2, "bad node remove" );
		
		Node B = graph.getNode( "B" );
		check( B.hasEdgeToward( "C" ), "bad node remove" );
		check( B.hasEdgeFrom( "C" ), "bad node remove" );
		check( B.getDegree() == 2, "bad node remove" );
		check( B.getInDegree() == 1, "bad node remove" );
		check( B.getOutDegree() == 1, "bad node remove" );
		
		graph.removeNode( "B" );
		check( graph.getNodeCount() == 1, "bad node remove" );
		check( graph.getEdgeCount() == 0, "bad node remove" );
		
		Node C = graph.getNode( "C" );
		check( C.getDegree() == 0, "bad node remove" );
		check( C.getInDegree() == 0, "bad node remove" );
		check( C.getOutDegree() == 0, "bad node remove" );
	}
}