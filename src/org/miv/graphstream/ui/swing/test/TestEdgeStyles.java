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
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;

/**
 * Test various edge rendering styles.
 * 
 * @author Antoine Dutot
 */
public class TestEdgeStyles
{
	public static void main( String args[] )
	{
		new TestEdgeStyles();
	}
	
	public TestEdgeStyles()
	{
		test( styleSheet1 );
		test( styleSheet2 );
		test( styleSheet3 );
	}
	
	protected void test( String styleSheet )
	{
		Graph graph = new DefaultGraph( false, true );
		
		GraphViewerRemote viewerRemote = graph.display();
		
		viewerRemote.setQuality( 4 );
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
		graph.addEdge( "AB", "A", "B", true );
		graph.addEdge( "BC", "B", "C", true );
		graph.addEdge( "CA", "C", "A", false );
		graph.addEdge( "AD", "A", "D", true );
		graph.addEdge( "DB", "D", "B", false );
		
		graph.addEdge( "AA", "A", "A", true );
		graph.addEdge( "DD", "D", "D", false );
		
		graph.getNode("A").addAttribute( "label", "A" );
		graph.getNode("B").addAttribute( "label", "B" );
		graph.getNode("C").addAttribute( "label", "C" );
		graph.getNode("D").addAttribute( "label", "D" );
		
		graph.getEdge( "AB" ).addAttribute( "label", "Edge AB" );
		graph.getEdge( "AA" ).addAttribute( "label", "Edge AA" );
	}
	
	protected static String styleSheet1 =
		"graph {" +
		"	background-color: #A09999;" +
		"	padding: 40px;" +
		"}" +
		"node {" +
		"	width: 16px;" +
		"	border-width: 1px;" +
		"	border-color: black;" +
		"	color: #F0F0F0;" +
		"	border-color: #402020;" +
		"	text-color: #202020;" +
		"}" +
		"node#A { border-width: 3px; }" +
		"edge {" +
		"	color: #402020;" +
		"	text-color: #404040;" +
		"}";
	
	protected static String styleSheet2 =
		"graph {" +
		"	background-color: #99A099;" +
		"	padding: 40px;" +
		"}" +
		"node {" +
		"	width: 16px;" +
		"	border-width: 1px;" +
		"	border-color: black;" +
		"	color: #F0F0F0;" +
		"	border-color: #104020A0;" +
		"	text-color: #202020;" +
		"}" +
		"node#A { border-width: 3px; }" +
		"edge {" +
		"	color: #307040A0;" +
		"	width: 3;" +
		"	edge-style: dots;" +
		"	text-color: #404040;" +
		"}";
	
	protected static String styleSheet3 =
		"graph {" +
		"	background-color: #9999A0;" +
		"	padding: 40px;" +
		"}" +
		"node {" +
		"	width: 16px;" +
		"	border-width: 1px;" +
		"	border-color: black;" +
		"	color: #F0F0F0;" +
		"	border-color: #202040;" +
		"	text-color: #202020;" +
		"}" +
		"node#A { border-width: 3px; }" +
		"edge {" +
		"	color: #202040;" +
		"	edge-shape: cubic-curve;" +
		"	text-color: #404040;" +
		"}";

}