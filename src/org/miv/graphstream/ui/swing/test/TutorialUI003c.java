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
import org.miv.graphstream.ui.Sprite;
import org.miv.graphstream.ui.graphicGraph.stylesheet.Style;

/**
 * How to use flow sprites.
 * 
 * @author Antoine Dutot
 */
public class TutorialUI003c
{
	public static void main( String args[] )
	{
		new TutorialUI003c();
	}
	
	public TutorialUI003c()
	{
		Graph graph = new DefaultGraph( "Flows", false, true );
		
		GraphViewerRemote remote = graph.display();
		
		remote.setQuality( 4 );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		Sprite flow1 = remote.addSprite( "flow1" );
		Sprite flow2 = remote.addSprite( "flow2" );
		
		flow1.attachToEdge( "AB" );
		flow2.attachToEdge( "AB" );
		flow1.position( 0.4f,  3, 0, Style.Units.PX );
		flow2.position( 0.8f, -3, 0, Style.Units.PX );
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
	}
	
	protected static String styleSheet = 
		"graph { background-color: #707070; }" +
		"node  { width: 12px; color: #C0C0C0; border-width: 3px; border-color: black; }" +
		"edge  { width: 3px; color: black; }" +
		"sprite { width: 5px; sprite-shape: flow; sprite-orientation: from; z-index:-1; }" +
		"sprite#flow1 { color: #C03030; }" +
		"sprite#flow2 { color: #C0C030; }";
}