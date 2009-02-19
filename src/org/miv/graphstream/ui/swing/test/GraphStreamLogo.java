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
 * Draw the GraphStream logo.
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
 */
public class GraphStreamLogo
{
	public static void main( String args[] )
	{
		new GraphStreamLogo();
	}
	
	public GraphStreamLogo()
	{
		logo2();
		logo1();
	}
	
	public void logo1()
	{
		Graph graph = new DefaultGraph( "Logo 2" );
		GraphViewerRemote remote = graph.display( false );
		
		graph.addAttribute( "ui.stylesheet", styleSheet1 );
		remote.setQuality( 4 );
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		Node D = graph.addNode( "D" );
		Node E = graph.addNode( "E" );
		Node F = graph.addNode( "F" );
		Node X = graph.addNode( "X" );
		Node Y = graph.addNode( "Y" );

		A.addAttribute( "xy", 0,  3 );
		B.addAttribute( "xy", 5,  1.5 );
		C.addAttribute( "xy", 0,  2 );
		D.addAttribute( "xy", 5,  0.5 );
		E.addAttribute( "xy", 0,  1 );
		F.addAttribute( "xy", 5, -0.5 );
		X.addAttribute( "xy", 5,  2.5 );
		Y.addAttribute( "xy", 0,  0 );
		
		X.addAttribute( "ui.class", "smallFlow" );
		Y.addAttribute( "ui.class", "smallFlow" );
		
		graph.addEdge( "AB", "A", "B", true );
		graph.addEdge( "CD", "C", "D", true );
		graph.addEdge( "EF", "E", "F", true );
		
		Edge CB = graph.addEdge( "CB", "C", "B" );
		Edge ED = graph.addEdge( "ED", "E", "D" );
		Edge AX = graph.addEdge( "AX", "A", "X" );
		Edge YF = graph.addEdge( "YF", "Y", "F" );
		
		CB.addAttribute( "label", "G  r  a  p  h" );
		ED.addAttribute( "label", "S  t  r  e  a  m" );
		
		CB.addAttribute( "ui.class", "smallFlow" );
		ED.addAttribute( "ui.class", "smallFlow" );
		AX.addAttribute( "ui.class", "smallFlow" );
		YF.addAttribute( "ui.class", "smallFlow" );
	}
	
	public static String styleSheet1 =
		"node { width:5%; border-width:2px; border-color:black; color:#505050; }" +
		"edge { edge-shape: cubic-curve; }" +
		"edge.smallFlow { color: #808080; z-index:-1; text-align: along; text-size:15; }" +
		"node.smallFlow { color: #808080; width:3%; z-index:-1; border-width:0; }" +
		"node#A { color:orange; border-width: 2; border-color: black; text-style: bold; }" +
		"node#F { color:#0070A0; border-width: 2; border-color: black; text-style: bold; }";
	
	public void logo2()
	{
		Graph graph = new DefaultGraph( "Logo 1" );
		GraphViewerRemote remote = graph.display( false );
		
		graph.addAttribute( "ui.stylesheet", styleSheet2 );
		remote.setQuality( 4 );
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		Node D = graph.addNode( "D" );
		Node E = graph.addNode( "E" );
		Node F = graph.addNode( "F" );
		Node G = graph.addNode( "G" );
		Node H = graph.addNode( "H" );
		Node I = graph.addNode( "I" );
		Node J = graph.addNode( "J" );
		Node K = graph.addNode( "K" );
		Node L = graph.addNode( "L" );
		
		A.addAttribute( "xy", -1, 1.5 );
		A.addAttribute( "label", "G" );
		B.addAttribute( "xy", -1, 0.5 );
		B.addAttribute( "label", "t" );
		C.addAttribute( "xy", 1, -0.5 );
		C.addAttribute( "label", "r" );
		D.addAttribute( "xy", 1, -1.5 );
		D.addAttribute( "label", "r" );
		E.addAttribute( "xy", 3, 0.5 );
		E.addAttribute( "label", "e" );
		F.addAttribute( "xy", 3, -0.5 );
		F.addAttribute( "label", "a" );
		G.addAttribute( "xy", 1, 1.5 );
		G.addAttribute( "label", "a" );
		H.addAttribute( "xy", 1, 0.5 );
		H.addAttribute( "label", "p" );
		I.addAttribute( "xy", -1, -0.5 );
		I.addAttribute( "label", "m" );
		J.addAttribute( "xy", -1, -1.5 );
		J.addAttribute( "label", "h" );
		K.addAttribute( "xy", -3, 0.5 );
		K.addAttribute( "label", "-" );
		L.addAttribute( "xy", -3, -0.5 );
		L.addAttribute( "label", "S" );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "CD", "C", "D" );
		graph.addEdge( "EF", "E", "F" );
		graph.addEdge( "GH", "G", "H" );
		graph.addEdge( "IJ", "I", "J" );
		graph.addEdge( "KL", "K", "L" );
		
		graph.addEdge( "AD", "A", "D", true );
		graph.addEdge( "BC", "B", "C", true );
		
		graph.addEdge( "CE", "C", "E", true );
		graph.addEdge( "DF", "D", "F", true );
		graph.addEdge( "EG", "E", "G", true );
		graph.addEdge( "FH", "F", "H", true );
		graph.addEdge( "GI", "G", "I", true );
		graph.addEdge( "HJ", "H", "J", true );
		graph.addEdge( "IK", "I", "K", true );
		graph.addEdge( "JL", "J", "L", true );
		graph.addEdge( "KA", "K", "A", true );
		graph.addEdge( "LB", "L", "B", true );
	}
	
	public static String styleSheet2 =
		"node { width: 18px; text-color: white; text-style: normal; }" +
		"edge { arrow-length: 8; arrow-width: 3; color:#444444; }" + //edge-shape: cubic-curve; }" +
		"node#A { color:red; border-width: 2; border-color: yellow; text-style: bold; }" +
		"node#L { text-style: bold; }";
}
