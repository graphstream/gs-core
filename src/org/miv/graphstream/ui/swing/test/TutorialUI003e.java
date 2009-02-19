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
 * How to use arrow sprites.
 * 
 * @author Antoine Dutot
 */
public class TutorialUI003e
{
	public static void main( String args[] )
	{
		new TutorialUI003e();
	}
	
	public TutorialUI003e()
	{
		Graph graph = new DefaultGraph( "Arrows", false, true );
		
		GraphViewerRemote remote = graph.display();
		
		remote.setQuality( 4 );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		graph.getNode("A").addAttribute( "label", "A" );
		graph.getNode("B").addAttribute( "label", "B" );
		graph.getNode("C").addAttribute( "label", "C" );
		
		Sprite arrow1 = remote.addSprite( "arrow1" );
		Sprite arrow2 = remote.addSprite( "arrow2" );
		Sprite arrow3 = remote.addSprite( "arrow3" );
		Sprite arrow4 = remote.addSprite( "arrow4" );
		
		arrow1.attachToEdge( "AB" );
		arrow2.attachToEdge( "AB" );
		arrow3.attachToNode( "A" );
		arrow4.attachToEdge( "BC" );
		arrow1.position( 0.4f );
		arrow2.position( 0.6f );
		arrow3.position( 28, 0, (float)(Math.PI/4), Style.Units.PX );
		arrow4.position( 0.5f, 16, 0, Style.Units.PX );
		
		arrow3.addAttribute( "label", "Here" );
		arrow4.addAttribute( "label", "There" );
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
	}
	
	protected static String styleSheet = 
		"graph { background-color: white; }" +
		"node { width: 18px; color: #3050F0; border-width: 2px; border-color: black; text-color: white; }" +
		"edge { width: 1px; color: black; }" +
		"sprite { sprite-shape: arrow; border-width: 1px; text-align: aside; }" +
		"sprite#arrow1 { color: #F02020; sprite-orientation: from; }" +
		"sprite#arrow2 { color: #20F020; sprite-orientation: to; }" +
		"sprite#arrow3 { color: #F0C020; sprite-orientation: origin; }" +
		"sprite#arrow4 { color: #F020F0; sprite-orientation: origin; }";
}