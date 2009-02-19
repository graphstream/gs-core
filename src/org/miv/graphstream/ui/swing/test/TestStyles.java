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
 * The purpose of this test is to show the various styling possibilities.
 * 
 * @author Antoine Dutot
 */
public class TestStyles
{
	public static void main( String args[] )
	{
		new TestStyles();
	}
	
	public TestStyles()
	{
		test( styleSheet1 );
		test( styleSheet2 );
		test( styleSheet3 );
		test( styleSheet4 );
		test( styleSheet5 );
	}
	
	protected void test( String styleSheet )
	{
		Graph graph = new DefaultGraph( false, true );
		
		GraphViewerRemote viewerRemote = graph.display();
		
		viewerRemote.setQuality( 4 );
		
		graph.addEdge( "AB", "A", "B", true );
		graph.addEdge( "BC", "B", "C", true );
		graph.addEdge( "CA", "C", "A", false );
		graph.addEdge( "AD", "A", "D", true );
		graph.addEdge( "DB", "D", "B", false );
		
		graph.getNode("A").addAttribute( "label", "A" );
		graph.getNode("B").addAttribute( "label", "B" );
		graph.getNode("C").addAttribute( "label", "C" );
		graph.getNode("D").addAttribute( "label", "D" );
		
		graph.getEdge( "AB" ).addAttribute( "label", "Edge AB" );
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
	}
	
	protected static final String styleSheet1 =
		  "graph { background-color: grey; }"
		+ "node {"
		+		"color:yellow;"
		+		"width:22;"
		+		"border-width:2;"
		+		"border-color:black;"
		+		"text-color:black;"
		+		"text-style:bold;"
		+ "}"
		+ "edge { text-align: along; text-color:black; text-offset: 0 7; }";
	
	protected static final String styleSheet2 =
		  "graph { background-color: rgb(50,50,50); }"
		+ "node    { width:26; color:#AAAAAA; border-width:3; border-color:black; text-color:black; }"
		+ "edge    { edge-style:dashes; text-align: along; text-offset: 0 7; }"
		+ "edge#AB { color:#FF555555; width:2; }"
		+ "edge#BC { color:#55FF5555; width:4; }"
		+ "edge#CA { color:#5555FF55; width:6; }"
		+ "edge#DB { color:#FFFF5555; width:4; }"
		+ "edge#AD { color:#FF55FF55; width:2; }";
	
	protected static final String styleSheet3 =
		  "node {"
		+		"text-color:white;"
		+		"text-style:bold-italic;"
		+		"node-shape:text-box;"
		+		"border-width:1;"
		+		"border-color:black;"
		+		"color:rgb(100,100,100);"
		+ "}"
		+ "node#A { text-color:red; }"
		+ "node#B { text-color:green; node-shape:circle; width:20; }"
		+ "node#C { text-color:blue; }"
		+ "node#D { text-color:yellow; }"
		+ "edge { color:black; arrow-shape:diamant; text-align: along; text-offset: 0 7; }"
		+ "edge#AB { color:black; arrow-shape:circle; arrow-length:10; }";
	
	protected static final String styleSheet4 =
		  "node {"
		+		"width:16;"
		+		"text-color:white;"
//		+		"border-width:2; border-color:white;"
		+ "}"
//		+ "graph { background-color: grey; }"
		+ "edge {"
		+		"width:8;"
		+ 		"edge-shape:angle;"
		+		"text-align:along;"
		+		"text-offset: 0 11;"
//		+		"border-width:2;"
//		+		"border-color:white;"
		+ "}"
		+ "edge#CA { width:2; color:#00000055; }"
		+ "edge#DB { width:2; color:#00000055; }";
	
	protected static final String styleSheet5 =
		  "node    { width:16; text-color:lightgray; }"
		+ "edge    { edge-shape:cubic-curve; color:rgb(70,70,70); arrow-length:13; arrow-width:4; text-align:center; }"
		+ "edge#AB { color:red;   edge-style:dots; }"
		+ "edge#BC { color:green; edge-style:dashes; }"
		+ "edge#AD { color:red;   edge-style:dots; }";
}