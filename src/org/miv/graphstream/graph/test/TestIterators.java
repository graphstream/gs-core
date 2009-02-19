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
import org.miv.graphstream.graph.implementations.SingleGraph;

/**
 * Test Iterators in Graph.
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
 * 
 */
public class TestIterators extends TestBase
{
	public static void main( String... strs )
	{
		new TestIterators();
	}

	public TestIterators()
	{
		System.out.printf( "%nTesting the default (single) graph:%n" );
		
		test1( new SingleGraph( "single" ) );
		printTestResults( "End of test 1." );
		test2( new SingleGraph( "single" ) );
		printTestResults( "End of test 2." );
		test3( new SingleGraph( "single", false, true ) );
		printTestResults( "End of test 3." );
		test4( new SingleGraph( "single", false, true ) );
		printTestResults( "End of test 4. That's all." );

		System.out.printf( "%nTesting the multi graph:%n" );
		
		test1( new MultiGraph( "multi" ) );
		printTestResults( "End of test 1." );
		test2( new MultiGraph( "multi" ) );
		printTestResults( "End of test 2." );
		test3( new MultiGraph( "multi", false, true ) );
		printTestResults( "End of test 3." );
		test4( new MultiGraph( "multi", false, true ) );
		printTestResults( "End of test 4. That's all." );
	}
	
	protected void test1( Graph g )
	{
		g.addNode( "A" );
		g.addNode( "B" );
		g.addNode( "C" );
		g.addNode( "D" );
		g.addEdge( "AB", "A", "B", false );
		g.addEdge( "AC", "A", "C", false );
		g.addEdge( "AD", "A", "D", false );

		// Test node iterator.
		
		Iterator<? extends Node> itN = g.getNodeIterator();
		HashSet<String> elements = new HashSet<String>();
		int count = 0;
		while( itN.hasNext() )
		{
			elements.add( itN.next().getId() );
			count++;
		}
		check( elements.size() == 4, "bad node iterator count" );
		check( count == 4, "bad node iterator count" );
		check( elements.contains( "A" ), "bad node iterator elements" );
		check( elements.contains( "B" ), "bad node iterator elements" );
		check( elements.contains( "C" ), "bad node iterator elements" );
		check( elements.contains( "D" ), "bad node iterator elements" );

		// Test edge iterator.
		
		Iterator<? extends Edge> itE = g.getEdgeIterator();
		elements.clear();
		count = 0;
		while( itE.hasNext() )
		{
			elements.add( itE.next().getId() );
			count++;
		}
		check( elements.size() == 3, "bad edge iterator count" );
		check( count == 3, "bad edge iterator count" );
		check( elements.contains( "AB" ), "bad edge iterator elements" );
		check( elements.contains( "AC" ), "bad edge iterator elements" );
		check( elements.contains( "AD" ), "bad edge iterator elements" );

		// Removing of nodes.
		
		itN = g.getNodeIterator();
		itN.next();
		itN.remove();
		check( g.getNodeCount() == 3, "bad node iterator remove" );
		check( g.getEdgeCount() <= 2, "bad node iterator remove" );
		itN.next();
		itN.remove();
		check( g.getNodeCount() == 2, "bad node iterator remove" );
		check( g.getEdgeCount() <= 1, "bad node iterator remove" );
		itN.next();
		itN.remove();
		check( g.getNodeCount() == 1, "bad node iterator remove" );
		check( g.getEdgeCount() == 0, "bad node iterator remove" );
		itN.next();
		itN.remove();
		check( g.getNodeCount() == 0, "bad iterator remove node count" );
		check( g.getEdgeCount() == 0, "bad iterator remove edge count" );
		check( ! itN.hasNext(), "bad iterator remove node" );

		g.addNode( "A" );
		g.addNode( "B" );
		g.addNode( "C" );
		g.addNode( "D" );
		g.addEdge( "AB", "A", "B", false );
		g.addEdge( "AC", "A", "C", false );
		g.addEdge( "AD", "A", "D", false );

		itE = g.getEdgeIterator();
		itE.next();
		itE.remove();
		check( g.getNodeCount() == 4, "bad edge iterator remove" );
		check( g.getEdgeCount() == 2, "bad edge iterator remove" );
		itE.next();
		itE.remove();
		check( g.getNodeCount() == 4, "bad edge iterator remove" );
		check( g.getEdgeCount() == 1, "bad edge iterator remove" );
		itE.next();
		itE.remove();
		check( g.getNodeCount() == 4, "bad edge iterator remove" );
		check( g.getEdgeCount() == 0, "bad edge iterator remove" );
		check( ! itE.hasNext(), "bad edge iterator remove" );
	}
	
	protected void test2( Graph g )
	{
		g.setStrictChecking( false );
		g.setAutoCreate( true );
		g.addEdge( "AB", "A", "B" );
		g.addEdge( "BC", "B", "C" );
		g.addEdge( "CA", "C", "A" );
		
		Iterator<? extends Node> i = g.getNodeIterator();
		int j = 0;
		boolean error = false;
		
		try
		{
			while( i.hasNext() )
			{
				i.next();
				j++;
				if( j == 2 ) g.addNode( "D" ); 
			}		
		}
		catch( java.util.ConcurrentModificationException e )
		{
			error = true;
		}
		
		check( error, "bad iteration add verification" );
	}
	
	protected void test3( Graph g )
	{
		// Test the BreadthFirstIterator.
		
		g.addEdge( "AB", "A", "B", true );
		g.addEdge( "BC", "B", "C", true );
		g.addEdge( "BG", "B", "G", true );
		g.addEdge( "CD", "C", "D", true );
		g.addEdge( "CG", "C", "G", true );
		g.addEdge( "CB", "C", "B", true );
		g.addEdge( "DE", "D", "E", true );
		g.addEdge( "EC", "E", "C", true );
		g.addEdge( "EG", "E", "G", true );
		g.addEdge( "GF", "G", "F", true );
		
		Node start = g.getNode( "A" );
		Iterator<? extends Node> i = start.getBreadthFirstIterator();
		String res = "";

		while( i.hasNext() )
		{
			Node n = i.next();
			res += n.getId();
		}
		
		check( res.equals( "ABGCFDE" ), "invalid breadth first iterator" );
		
		start = g.getNode( "E" );	// Not all the graph is accessible here if we use directed edges.
		i     = start.getBreadthFirstIterator();
		res   = "";
		
		while( i.hasNext() )
		{
			Node n = i.next();
			res += n.getId();
		}
		
		check( res.equals( "EGCFDB" ), "invalid breadth first iterator" );
	}
	
	protected void test4( Graph g )
	{
		// Test the DepthFirstIterator.
		
		g.addEdge( "AB", "A", "B", true );
		g.addEdge( "BC", "B", "C", true );
		g.addEdge( "BG", "B", "G", true );
		g.addEdge( "CD", "C", "D", true );
		g.addEdge( "CG", "C", "G", true );
		g.addEdge( "CB", "C", "B", true );
		g.addEdge( "DE", "D", "E", true );
		g.addEdge( "EC", "E", "C", true );
		g.addEdge( "EG", "E", "G", true );
		g.addEdge( "GF", "G", "F", true );
		
		Node start = g.getNode( "A" );
		Iterator<? extends Node> i = start.getDepthFirstIterator();

		String res = "";
		
		while( i.hasNext() )
		{
			Node n = i.next();
			res = res + n.getId();
		}
		
		check( res.equals( "ABCGFDE" ), "invalid depth first iterator" );
		
		start = g.getNode( "E" );	// Not all the graph is accessible here if we use directed edges.
		i     = start.getDepthFirstIterator();
		res   = "";
		
		while( i.hasNext() )
		{
			Node n = i.next();
			res += n.getId();
		}
		
		check( res.equals( "ECBGFD" ), "invalid depth first iterator" );
	}
}