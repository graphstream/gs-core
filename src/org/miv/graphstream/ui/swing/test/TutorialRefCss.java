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

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;

import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.MultiGraph;
import org.miv.graphstream.ui.GraphViewer;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.Sprite;
import org.miv.graphstream.ui.swing.SwingGraphViewer;

public class TutorialRefCss extends JFrame
{
    private static final long serialVersionUID = 1L;

	protected Graph graph;
	
	protected GraphViewer viewer;
	
	protected GraphViewerRemote remote;
	
	public static void main( String args[] )
	{
		new TutorialRefCss();
	}
	
	public TutorialRefCss()
	{
		graph  = new MultiGraph( "RefCss", false, true );
		viewer = new SwingGraphViewer( graph, false, true );
		remote = viewer.newViewerRemote();
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CD", "C", "D" );
		graph.addEdge( "DA", "D", "A" );
		graph.addEdge( "AC", "A", "C" );
		
		graph.getNode("A").setAttribute( "xy", -1, -1 );
		graph.getNode("B").setAttribute( "xy",  1, -1 );
		graph.getNode("C").setAttribute( "xy",  1,  1 );
		graph.getNode("D").setAttribute( "xy", -1,  1 );
		
		remote.setQuality( 4 );
		add( (Component)viewer.getComponent(), BorderLayout.CENTER );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 200, 200 );
		setVisible( true );
	
//		test( styleSheet0, false, false, false );
//		test( styleSheet1, false, false, false );
//		test( styleSheet2, false, false, false );
//		test( styleSheet3, false, false, false );
//		test( styleSheet4, true, false, false );
//		test( styleSheet5, false, true, false );
//		test( styleSheet6, false, true, false );
//		test( styleSheet7, false, false, false );
//		test( styleSheet8, false, false, false );
//		test( styleSheet9, false, false, false );
//		test( styleSheet10, false, false, false );
//		test( styleSheet11, false, false, false );
//		test( styleSheet12, false, false, false );
//		test( styleSheet13, false, false, false );
//		test( styleSheet14, false, false, false );
//		test( styleSheet15, false, true, false );
//		test( styleSheet16, false, true, true );
//		test( styleSheet17, false, false, false );
//		test( styleSheet18, false, false, false );
//		test( styleSheet19, false, false, true );
//		test( styleSheet20, true, false, false );
//		test( styleSheet21, true, false, false );
		test( styleSheet22, true, false, false );
	}
	
	public void test( String styleSheet, boolean addSprites, boolean addLabels, boolean oriented )
	{
		Sprite s1 = null;
		Sprite s2 = null;
		
	//	graph.removeAttribute( "stylesheet" );
		graph.addAttribute( "stylesheet", styleSheet );
		
		if( addSprites )
		{
			s1 = remote.addSprite( "1" );
			s2 = remote.addSprite( "2" );
			
			s1.attachToEdge( "AB" );
			s2.attachToEdge( "CD" );
			s1.position( 0.5f );
			s2.position( 0.5f );
			
			s1.addAttribute( "pie-values", 0.3f, 0.4f, 0.3f );
			s2.addAttribute( "pie-values", 0.2f, 0.5f, 0.3f );
		}
		
		if( addLabels )
		{
			graph.getNode("A").addAttribute( "label", "A" );
			graph.getNode("B").addAttribute( "label", "B" );
			graph.getNode("C").addAttribute( "label", "C" );
			graph.getNode("D").addAttribute( "label", "D" );
			
			if( s1 != null && s2 != null )
			{
				s1.addAttribute( "label", "1" );
				s2.addAttribute( "label", "2" );
			}
			
			setSize( 270,270 );
		}
		
		if( oriented )
		{
			graph.getEdge("AB").setDirected( true );
			graph.getEdge("BC").setDirected( true );
			graph.getEdge("CD").setDirected( true );
			graph.getEdge("DA").setDirected( true );
			graph.getEdge("AC").setDirected( true );
		}
	}
	
	public static String styleSheet0  = "node#A { color: purple; } node#B { color: green; } node#C { color: magenta; } node#D { color: LimeGreen; }";
	public static String styleSheet1  = "node { width: 5px; }";
	public static String styleSheet2  = "node { width: 20px; }";
	public static String styleSheet3  = "sprite { sprite-shape: arrow; width: 30px; height: 10px; }";
	public static String styleSheet4  = "sprite { sprite-shape: arrow; width: 10px; height: 30px; }";
	public static String styleSheet5  = "graph { padding: 70px; } edge { color: grey; } node { width: 8px; color: grey; text-font: 'Purisa'; text-color: black; text-size: 10; text-style: bold; text-align: aside; }";
	public static String styleSheet6  = "graph { padding: 70px; } node { width: 18px; text-font: 'Purisa'; text-color: white; text-size: 12; text-style: bold-italic; text-align: center; }";
	public static String styleSheet7  = "graph { padding: 30px; } node { node-shape: image; width: 48px; height: 48px; image: url('http://graphstream.sf.net/pix/node.png'); }";
	public static String styleSheet8  = "graph { image: url('http://graphstream.sf.net/pix/node.png'); image-offset: -1gu -1gu; width: 2gu; height: 2gu; } node { width: 10px; } edge { width: 1px; }";
	public static String styleSheet9  = "node { color: red; shadow-style: simple; shadow-offset: 3px 3px; shadow-width: 0px; shadow-color: grey; }";
	public static String styleSheet10 = "node { color: yellow; border-color: black; border-width: 1px; shadow-style: simple; shadow-width: 3px; shadow-offset: 0px 0px; shadow-color: blue; }";
	public static String styleSheet11 = "node { color: white; shadow-style: simple; shadow-width: 2px; shadow-offset: 0px 0px; shadow-color: grey; } edge { width: 2px; color: white; shadow-style: simple; shadow-width: 2px; shadow-offset: 0px 0px; shadow-color: grey; }";
	public static String styleSheet12 = "node { color: yellow; border-color: black; border-width: 1px; }";
	public static String styleSheet13 = "node { color: white; border-color: grey; border-width: 2px; } edge { width: 2px; color: white; border-width: 2px; border-color: grey; }";
	public static String styleSheet14 = "node { color: grey; z-index: 1; } edge { color: black; z-index: 2; }";
	public static String styleSheet15 = "graph { padding: 70px; } node { node-shape: text-box; color: #E0E0E0; border-width: 1px; border-color: grey; image: url('http://graphstream.sf.net/pix/node.png'); }";
	public static String styleSheet16 = "edge { edge-shape: angle; width: 6px; }";
	public static String styleSheet17 = "edge { edge-shape: cubic-curve; }";
	public static String styleSheet18 = "edge#AB { edge-style: dots; } edge#BC { edge-style: dashes; } edge#AC { edge-style: dots; } edge#DA { edge-style: dashes; }";
	public static String styleSheet19 = "node { color: red; border-width: 1px; } edge { color: grey; } edge#AB { arrow-shape: diamant; } edge#BC { arrow-shape: circle; } edge#CD { arrow-shape: image; arrow-image: url('http://graphstream.sf.net/pix/node.png'); arrow-width: 16px; arrow-length: 16px; }";
	public static String styleSheet20 = "graph { padding: 30px; } sprite#'1' { sprite-shape: arrow; sprite-orientation: to; width: 6px; height: 20px; } sprite#'2' { sprite-shape: arrow; sprite-orientation: origin; width: 6px; height: 12px; }";
	public static String styleSheet21 = "sprite { sprite-shape: flow; z-index: -1; width: 7px; } sprite#'1' { color: #D0503077; } sprite#'2' { color: #50D03077; width: 7px; }";
	public static String styleSheet22 = "sprite { sprite-shape: pie-chart; width: 20px; color: red green blue; border-width: 1px; }";
}