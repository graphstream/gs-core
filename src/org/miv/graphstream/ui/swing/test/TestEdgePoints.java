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

package org.miv.graphstream.ui.swing.test;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.MultiGraph;

public class TestEdgePoints
{
	public static void main( String args[] )
	{
		new TestEdgePoints();
	}
	
	public TestEdgePoints()
	{
		Graph graph = new MultiGraph( "map" );
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		Node D = graph.addNode( "D" );
		
		Edge AB = graph.addEdge( "AB", "A", "B" );
/*		Edge BC =*/ graph.addEdge( "BC", "B", "C" );
/*		Edge CD =*/ graph.addEdge( "CD", "C", "D" );
/*		Edge DA =*/ graph.addEdge( "DA", "D", "A" );
		
		A.addAttribute( "xy", 0, 0 );
		B.addAttribute( "xy", 1, 0 );
		C.addAttribute( "xy", 1, 1 );
		D.addAttribute( "xy", 0, 1 );
		
		AB.addAttribute( "style", "edge-shape: (0.25,0.1,0), (0.5,-0.05,0), (0.75,0.1,0);" );
		
		graph.display( false );
		graph.addAttribute( "stylesheet", styleSheet );
	}
	
	protected String styleSheet =
		"graph { padding: 20px; }";
}