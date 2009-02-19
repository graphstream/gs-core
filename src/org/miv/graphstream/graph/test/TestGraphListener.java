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

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.AdjacencyListGraph;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.graph.implementations.MultiGraph;

import java.util.LinkedList;

public class TestGraphListener extends TestBase implements GraphListener
{
	protected LinkedList<String> stack = new LinkedList<String>();
	
	public static void main( String args[] )
	{
		new TestGraphListener();
	}
	
	public TestGraphListener()
	{
		test( new DefaultGraph( false, true ) );
		printTestResults( "* Test on DefaultGraph:" );
		test( new MultiGraph( false, true ) );
		printTestResults( "* Test on MultiGraph:" );
		test( new AdjacencyListGraph( false, true ) );
		printTestResults( "* Test on AdjacencyListGraph:" );
	}
	
	protected void test( Graph graph )
	{
		// Basic test, we create a triangle graph.
		
		graph.addGraphListener( this );
		graph.addNode( "A" );
		check( stack.removeLast().equals( "AN|A" ), "invalid add node" );
		check( stack.isEmpty(), "invalid add node" );
		graph.addNode( "B" );
		check( stack.removeLast().equals( "AN|B" ), "invalid add node" );
		check( stack.isEmpty(), "invalid add node" );
		graph.addEdge( "AB", "A", "B" );
		check( stack.removeLast().equals( "AE|AB" ), "invalid add edge" );
		check( stack.isEmpty(), "invalid add edge" );
		graph.addEdge( "BC", "B", "C" );
		check( stack.size() == 2, String.format( "invalid add edge and nodes 2 != %d", stack.size() ) );
		check( stack.removeLast().equals( "AE|BC" ), "invalid add edge and nodes" );
		check( stack.removeLast().equals( "AN|C" ), "invalid add edge and nodes" );
		check( stack.isEmpty(), "invalid add edge and nodes" );
		graph.addEdge( "CA", "C", "A" );
		check( stack.size() == 1, String.format( "invalid add edge and nodes 1 != %d", stack.size() ) );
		check( stack.removeLast().equals( "AE|CA" ), "invalid add edge and nodes" );
		check( stack.isEmpty(), "invalid add edge and nodes" );
		
		// More difficult, we add nodes in the listener.
		// When the node named "D" is added,t he listener part adds a node "E" and an edge between
		// the two.
		
		graph.addNode( "D" );
		check( stack.size() == 3, String.format( "invalid add edge and nodes 3 != %d", stack.size() ) );
		check( stack.removeLast().equals( "AE|DE" ), "invalid add edge and nodes" );
		check( stack.removeLast().equals( "AN|E" ), "invalid add edge and nodes" );
		check( stack.removeLast().equals( "AN|D" ), "invalid add edge and nodes" );
		check( stack.isEmpty(), "invalid add edge and nodes" );
	}
	
// Graph Listener

	public void afterEdgeAdd( Graph graph, Edge edge )
    {
		stack.add( "AE|"+edge.getId() );
    }

	public void afterNodeAdd( Graph graph, Node node )
    {
		stack.add( "AN|"+node.getId() );
		
		if( node.getId().equals( "D" ) )
		{
			graph.addNode( "E" );
			graph.addEdge( "DE", "D", "E" );
		}
    }

	public void attributeChanged( Element element, String attribute, Object oldValue,
            Object newValue )
    {
		stack.add( "AC|"+element.getId() );
    }

	public void beforeEdgeRemove( Graph graph, Edge edge )
    {
		stack.add( "DE|"+edge.getId() );
    }

	public void beforeGraphClear( Graph graph )
    {
		stack.add( "CG" );
    }

	public void beforeNodeRemove( Graph graph, Node node )
    {
		stack.add( "DN|"+node.getId() );
    }

	public void stepBegins(Graph graph, double time)
	{
		System.out.println("Step test - "+time);
		
	}
}