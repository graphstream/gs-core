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
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;

/**
 * The purpose of this test is to show the various styling possibilities.
 * 
 * @author Antoine Dutot
 */
public class TestStyles3
{
	public static void main( String args[] )
	{
		new TestStyles3();
	}
	
	public TestStyles3()
	{
		test( styleSheet1 );
	}
	
	protected void test( String styleSheet )
	{
		Graph graph = new DefaultGraph( false, true );
		
		GraphViewerRemote viewerRemote = graph.display( false );
		
		viewerRemote.setQuality( 3 );
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		Node D = graph.addNode( "D" );
		
		Edge AB = graph.addEdge( "AB", "A", "B", true );
		Edge BC = graph.addEdge( "BC", "B", "C", true );
		Edge CA = graph.addEdge( "CA", "C", "A", false );
		Edge AD = graph.addEdge( "AD", "A", "D", true );
		Edge DB = graph.addEdge( "DB", "D", "B", false );
		
		A.addAttribute( "label", "A" );
		B.addAttribute( "label", "B" );
		C.addAttribute( "label", "C" );
		D.addAttribute( "label", "D" );
		
		A.addAttribute( "x", 0 );
		A.addAttribute( "y", 1 );
		B.addAttribute( "x", -1 );
		B.addAttribute( "y", 0 );
		C.addAttribute( "x", 1 );
		C.addAttribute( "y", 0 );
		D.addAttribute( "x", 0 );
		D.addAttribute( "y", -1 );
		
		AB.addAttribute( "label", "Edge AB" );
		BC.addAttribute( "label", "Edge BC" );
		CA.addAttribute( "label", "Edge CA" );
		AD.addAttribute( "label", "Edge AD" );
		DB.addAttribute( "label", "Edge DB" );
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
	}
	
	protected static final String styleSheet1 =
		  "graph {" +
		  "		image:url('http://upload.wikimedia.org/wikipedia/en/thumb/2/2d/Heroes_helix.svg/400px-Heroes_helix.svg.png');" +
		  "		image-offset: -1gu -1gu;" +
		  " 	width: 2gu;" +
		  "		height: 2gu;" +
		  "}" +
		  "node {" +
		  "		width: 10%;" +
		  "		height: 10%;" +
		  "		node-shape: image;" +
		  "		text-align: aside;" +
		  "		border-width: 1px;" +
		  "		border-color: red;" +
		  "}" +
		  "edge {" +
		  "		color:#FF000055;" +
		  "}";
	
}