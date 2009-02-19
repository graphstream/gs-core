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
import org.miv.graphstream.ui.Sprite;

/**
 * The purpose of this test is to show the various styling possibilities.
 * 
 * @author Antoine Dutot
 */
public class TestStyles4
{
	public static void main( String args[] )
	{
		new TestStyles4();
	}
	
	public TestStyles4()
	{
		test( styleSheet );
	}
	
	protected void test( String styleSheet )
	{
		Graph graph = new DefaultGraph( false, true );
		
		GraphViewerRemote viewerRemote = graph.display( false );
		
		viewerRemote.setQuality( 4 );
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
		graph.addEdge( "AB", "A", "B", true );
		graph.addEdge( "BC", "B", "C", true );
		graph.addEdge( "CA", "C", "A", false );
		graph.addEdge( "AD", "A", "D", true );
		graph.addEdge( "BD", "B", "D", true );
		
		graph.getNode("A").addAttribute( "label", "Grimm" );
		graph.getNode("B").addAttribute( "label", "Attila" );
		graph.getNode("C").addAttribute( "label", "Mother Goose" );
		graph.getNode("D").addAttribute( "label", "Sumo" );
		graph.getEdge("BD").addAttribute( "ui.class", "curved" );
		
		graph.getNode("A").setAttribute( "xy", 1, 1 );
		graph.getNode("B").setAttribute( "xy", 0, 1 );
		graph.getNode("C").setAttribute( "xy", 0, 0 );
		graph.getNode("D").setAttribute( "xy", 1, 0 );
		
		graph.getEdge( "AB" ).addAttribute( "label", "Edge AB" );

		Sprite X = viewerRemote.addSprite( "X" );
		Sprite Y = viewerRemote.addSprite( "Y" );
		
		X.attachToEdge( "CA" );
		Y.attachToEdge( "BD" );
		X.position( 0.5f );
		Y.position( 0.5f );
		X.addAttribute( "label", "Whiz" );
		Y.addAttribute( "label", "Lassie" );
	}
	
	protected static String styleSheet =
		"graph {" +
		"	color: grey;" +
		"	padding: 70px;" +
		"	border-width: 2px;" +
		"	border-color: black;" +
		"}" +
		"node {" +
		"	node-shape: text-box;" +
		"	border-width: 1px;" +
		"	border-color: black;" +
		"	color: #E0E0E088;" +
		"}" +
		"edge { border-width: 0px; }" +
		"sprite { border-width: 0px; }" +
		"node#A {" +
		"	image:url('http://www.iconarchive.com/icons/pino/grimm/Grimm-Head-icon.gif');" +
		"}" +
		"node#B {" +
		"	image:url('http://www.iconarchive.com/icons/pino/grimm/Attila-Side-icon.gif');" +
		"}" +
		"node#C {" +
		"	node-shape:text-ellipse;" +
		"	image:url('http://www.iconarchive.com/icons/pino/grimm/Mother-Goose-1-icon.gif');" +
		"}" +
		"node#D {" +
		"	node-shape:text-ellipse;" +
		"	image:url('http://www.iconarchive.com/icons/pino/grimm/Sumo-icon.gif');" +
		"}" +
		"sprite {" +
		"	sprite-shape:text-box;" +
		"}" +
		"sprite#X {" +
		"	image:url('http://www.iconarchive.com/icons/pino/grimm/Whiz-2-icon.gif');" +
		"	text-align:left;" +
		"}" +
		"sprite#Y {" +
		"	image:url('http://www.iconarchive.com/icons/pino/grimm/Lassie-icon.gif');" +
		"	text-align:right;" +
		"}" +
		"edge.curved { edge-shape:cubic-curve; }";
}
