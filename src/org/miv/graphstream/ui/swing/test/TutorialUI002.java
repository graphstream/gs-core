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
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;

public class TutorialUI002
{
	public static void main( String args[] ) {
		new TutorialUI002();
	}
	
	public TutorialUI002() {
		Graph graph = new DefaultGraph( false, true );

		GraphViewerRemote gvr = graph.display();
		
		gvr.setQuality( 4 );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		graph.getNode("A").addAttribute( "label", "A" );
		graph.getNode("B").addAttribute( "label", "B" );
		graph.getNode("C").addAttribute( "label", "C" );
		
		graph.addAttribute( "ui.stylesheet", styleSheet1 );
	}
	
	public static final String styleSheet1 = 
		"graph {" +
		"    color:red;"+
 		"}" +
		"node {" +
		"    text-color:blue;" +
		"}" +
		"edge {" +
		"    width:2;" +
		"    color:green;" +
		"}" +
		"node#A {" +
		"    color:cyan;" +
		"}";
}
