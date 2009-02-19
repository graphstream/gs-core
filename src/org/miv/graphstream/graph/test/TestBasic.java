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

import java.util.*;

import org.miv.graphstream.graph.*;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.graph.implementations.MultiGraph;
import org.miv.graphstream.graph.implementations.SingleGraph;

/**
 * Test the core behaviour of the {@link org.miv.graphstream.graph.implementations.DefaultGraph}
 * class and its descendants {@link org.miv.graphstream.graph.implementations.SingleGraph}
 * and {@link org.miv.graphstream.graph.implementations.MultiGraph}.
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20061206
 */
public class TestBasic extends TestBase
{
	public static void
	main( String args[] )
	{
		new TestBasic();
	}
	
	public
	TestBasic()
	{
		test1( new DefaultGraph( "default" ) );
		printTestResults( "Test with the DefaultGraph finished." );
		test1( new SingleGraph( "single" ) );		// The same as Default.
		printTestResults( "Test with the SingleGraph finished." );
		test1( new MultiGraph( "multi" ) );			// Multi.
		printTestResults( "Test with the MultiGraph finished. End of tests." );
	}
	
// Commands
	
	@SuppressWarnings("unchecked")
	protected void test1( Graph graph )
	{
		// Test add 2 nodes an 1 edge.
		
		graph.addNode( "node1" );
		graph.addNode( "node2" );
		graph.addEdge( "1--2", "node1", "node2", false );
		
		check( graph.getNodeCount() == 2, "bad node count%n" );
		check( graph.getEdgeCount() == 1, "bad edge count%n" );
		check( graph.getNode("node1").hasEdgeToward( "node2" ), "bad node edge set" );
		check( graph.getNode("node1").hasEdgeFrom( "node2" ), "bad node edge set" );
		check( graph.getNode("node2").hasEdgeToward( "node1" ), "bad node edge set" );
		check( graph.getNode("node2").hasEdgeFrom( "node1" ), "bad node edge set" );
		check( graph.getNode("node1").getEdge(0) != null, "bad edge access" );
		check( graph.getNode("node2").getEdge(0) != null, "bad edge access" );
		
		// Test retrieve nodes and edges.
		
		Node node1   = graph.getNode( "node1" );
		Node node2   = graph.getNode( "node2" );
		Edge edge1_2 = graph.getEdge( "1--2" );
		
		check( node1   != null, "cannot retrieve node1%n" );
		check( node2   != null, "cannot retrieve node2%n" );
		check( edge1_2 != null, "cannot retrieve edge 1--2%n" );
		
		// Test node informations.
		
		check( node1.getDegree() == 1, "bad node1 degree%n" ); 
		check( node2.getDegree() == 1, "bad node2 degree%n" ); 
		
		// Test node neighbour iterators.
		
		int count = 0;
		Iterator<Node> nodes = (Iterator<Node>) node1.getNeighborNodeIterator();
		
		while( nodes.hasNext() )
		{
			Node n = nodes.next();
			count++;
			check( n.getId().equals( "node2" ), "bad node id in node1 iterator%n" );
		}
		
		check( count == 1, "bad node count %d (expected 1) in node1 iterator%n", count );
		
		count = 0;
		nodes = (Iterator<Node>) node2.getNeighborNodeIterator();
		
		while( nodes.hasNext() )
		{
			Node n = nodes.next();
			count++;
			check( n.getId().equals( "node1" ), "bad node id in node2 iterator%n" );
		}
		
		check( count == 1, "bad node count %d (expected 1) in node2 iterator%n", count );
		
		// Test directed edges.
		
		graph.addNode( "node3" );
		graph.addEdge( "2->3", "node2", "node3", true );
		
		check( graph.getNode("node2").hasEdgeToward( "node3" ), "bad node edge set" );
		check(!graph.getNode("node2").hasEdgeFrom( "node3" ), "bad node edge set" );
		check(!graph.getNode("node3").hasEdgeToward( "node2" ), "bad node edge set" );
		check( graph.getNode("node3").hasEdgeFrom( "node2" ), "bad node edge set" );
	}
/*	
	protected void test2()
	{
		// Try error detection.
		
		boolean error = false;
		
		try
		{
			// Such an edge already exists
			graph.addEdge( "2->3 bis", "node2", "node3", true );
		}
		catch( Exception e )
		{
			error = true;
		}
		
		check( error, "single-graph allows multiple edges !!" );
		
		error = true;
		
		try
		{
			// OK the edge is transformed in undirected edge.
			// Is this a correct behaviour ? Indeed the identifier
			// "2<-3" is not conserved.
			graph.addEdge( "2<-3", "node3", "node2", true );
		}
		catch( Exception e )
		{
			error = false;
		}
		
		check( error, "single-graph does not set edge direction" );
	}
*/
}