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
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.Sprite;
import org.miv.graphstream.ui.graphicGraph.stylesheet.Style;

public class TutorialUI003a
{
	public static void main( String args[] )
	{
		new TutorialUI003a();
	}
	
	public TutorialUI003a()
	{
		Graph graph = new DefaultGraph();
		
		GraphViewerRemote remote = graph.display( false );
		
		remote.setQuality( 4 );
		graph.addAttribute( "ui.stylesheet", styleSheet );
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		A.addAttribute( "label", "A" );
		B.addAttribute( "label", "B" );
		C.addAttribute( "label", "C" );
		
		A.addAttribute( "x",  0 );
		A.addAttribute( "y",  1 );
		B.addAttribute( "x", -1 );
		B.addAttribute( "y",  0 );
		C.addAttribute( "x",  1 );
		C.addAttribute( "y",  0 );
		
		// The use of sprites begins here.
		
		Sprite S1 = remote.addSprite( "S1" );
		Sprite S2 = remote.addSprite( "S2" );
		Sprite S3 = remote.addSprite( "S3" );
		
		S1.attachToEdge( "AB" );
		S2.attachToNode( "B" );
		
		S1.position( 0.4f, 15, 0, Style.Units.PX );
		S2.position( 20, 0, (float)Math.PI, Style.Units.PX );
		S3.position( 0, 0.5f, 0 );
	}
	
	public static String styleSheet =
		"node { width:16px; color:grey; border-width:1px; border-color:black; text-color:black; }" +
		"edge { color:black; }" +
		"sprite { width:10px; }" +
		"sprite#S1 { color: red; }" +
		"sprite#S2 { color: yellow; }" +
		"sprite#S3 { color: blue; }";
}