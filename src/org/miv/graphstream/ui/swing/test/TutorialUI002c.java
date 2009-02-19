/*
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.miv.graphstream.ui.swing.test;

import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;

public class TutorialUI002c
{
	public static void main( String args[] ) {
		new TutorialUI002c();
	}
	
	public TutorialUI002c() {
		Graph graph = new DefaultGraph( false, true );

		GraphViewerRemote gvr = graph.display();
		
		gvr.setQuality( 4 );
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		A.addAttribute( "label", "A" );
		B.addAttribute( "label", "B" );
		C.addAttribute( "label", "C" );
		
		graph.addAttribute( "ui.stylesheet", styleSheet2 );
		
		Node current = A;
		
		while( true )
		{
			current.addAttribute( "class", "active" );
			
			try{ Thread.sleep( 700 ); } catch( Exception e ) {}
			
			current.removeAttribute( "class" );
			
			if( current == A ) current = B;
			else if( current == B ) current = C;
			else if( current == C ) current = A;
		}
	}
	
	public static final String styleSheet2 = 
		"node {" +
		"	color: yellow;" +
		"	text-color: black;" +
		"	border-width: 1;" +
		"	border-color: black;" +
		"}" +
		"node.active {" +
		"	color: red;" +
		"	text-color: yellow;" +
		"}";
}