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

import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.MultiGraph;

public class TestStyleEventsState
{
	public static void main( String args[] )
	{
		new TestStyleEventsState();
	}
	
	public TestStyleEventsState()
	{
		Graph graph = new MultiGraph( "states ?", false, true );
		
		graph.addAttribute( "stylesheet", styleSheet );
		graph.display();
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		Node A = graph.getNode("A");
		Node B = graph.getNode("B");
		
		A.setAttribute( "ui.state", "foo" );
		B.setAttribute( "ui.state", "bar" );
	}
	
	protected static String styleSheet = 
		"node:foo { color: red; }" +
		"node:bar { color: green; }";
}