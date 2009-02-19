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
import org.miv.graphstream.graph.implementations.MultiGraph;
import org.miv.graphstream.ui.GraphViewerRemote;

public class TestEdgeStyles2 extends TestEdgeStyles
{
	public static void main( String args[] )
	{
		new TestEdgeStyles2();
	}
	
	@Override
	protected void test( String styleSheet )
	{
		Graph graph = new MultiGraph( false, true );
		
		GraphViewerRemote viewerRemote = graph.display();
		
		viewerRemote.setQuality( 4 );
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
		graph.addEdge( "AB",  "A", "B", true );
		graph.addEdge( "AB2", "A", "B", true );
		graph.addEdge( "BC",  "B", "C", true );
		graph.addEdge( "CA",  "C", "A", false );
		graph.addEdge( "CA2", "C", "A", false );
		graph.addEdge( "CA3", "A", "C", false );
		graph.addEdge( "CA4", "C", "A", true );
		graph.addEdge( "CA5", "A", "C", true );
		graph.addEdge( "AD",  "A", "D", true );
		graph.addEdge( "DB",  "D", "B", false );
		graph.addEdge( "DB2", "D", "B", false );
		graph.addEdge( "DB3", "D", "B", false );
		graph.addEdge( "DB4", "D", "B", false );
		
		graph.addEdge( "AA",  "A", "A", false );
		graph.addEdge( "AA1", "A", "A", false );
		graph.addEdge( "DD",  "D", "D", true );
		
		graph.getNode("A").addAttribute( "label", "A" );
		graph.getNode("B").addAttribute( "label", "B" );
		graph.getNode("C").addAttribute( "label", "C" );
		graph.getNode("D").addAttribute( "label", "D" );
		
		graph.getEdge( "AB" ).addAttribute( "label", "Edge AB" );
		graph.getEdge( "AA" ).addAttribute( "label", "Edge AA" );
	}
}