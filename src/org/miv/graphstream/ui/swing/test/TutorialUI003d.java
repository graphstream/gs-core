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
 * How to use pie-char sprites.
 * 
 * @author Antoine Dutot
 */
public class TutorialUI003d
{
	public static void main( String args[] )
	{
		new TutorialUI003d();
	}
	
	public TutorialUI003d()
	{
		Graph graph = new DefaultGraph( "Pie-charts", false, true );
		
		GraphViewerRemote remote = graph.display();
		
		remote.setQuality( 4 );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		graph.getNode("A").addAttribute( "label", "A" );
		graph.getNode("B").addAttribute( "label", "B" );
		graph.getNode("C").addAttribute( "label", "C" );
		
		Sprite pc1 = remote.addSprite( "pc1" );
		Sprite pc2 = remote.addSprite( "pc2" );
		Sprite pc3 = remote.addSprite( "pc3" );
		
		pc1.attachToEdge( "AB" );
		pc2.attachToNode( "A" );
		pc3.attachToEdge( "BC" );
		pc1.position( 0.4f );
		pc2.position( 28, 0, 0, Style.Units.PX );
		pc3.position( 0.5f );
		pc1.addAttribute( "pie-values", 0.1f, 0.3f, 0.6f );
		pc2.addAttribute( "pie-values", 0.4f, 0.6f );
		pc3.addAttribute( "pie-values", 0.4f );
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
	}
	
	protected static String styleSheet = 
		"graph { background-color: white; }" +
		"node  { width: 18px; color: yellow; border-width: 2px; border-color: black; text-color: black; }" +
		"edge  { width: 1px; color: black; }" +
		"sprite { width: 30px; sprite-shape: pie-chart; border-width: 1px; }" +
		"sprite#pc1 { color: #F02020AA #20F020AA #2020F0AA; }" +
		"sprite#pc2 { color: #F0C020AA #20F0F0AA; }" +
		"sprite#pc3 { color: #F020F0AA; }";
}