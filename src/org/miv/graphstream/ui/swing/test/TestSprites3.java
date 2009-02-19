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

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.Sprite;
import org.miv.graphstream.ui.graphicGraph.stylesheet.Style;

/**
 * Test the sprite API.
 * 
 * @author Antoine Dutot
 */
public class TestSprites3
{
	public static void main( String args[] )
	{
		new TestSprites3();
	}
	
	public TestSprites3()
	{
		Graph graph = new DefaultGraph();
		
		GraphViewerRemote viewerRemote = graph.display( false );
		
		viewerRemote.setQuality( 4 );
		
		// A simple triangle graph.
		
		Node A  = graph.addNode( "A" );
		Node B  = graph.addNode( "B" );
		Node C  = graph.addNode( "C" );
		Node D  = graph.addNode( "D" );
		
		A.addAttribute( "xy", -0.8f,    0f );
		D.addAttribute( "xy",  0.8f,    0f );
		B.addAttribute( "xy",  0.2f,  0.8f );
		C.addAttribute( "xy", -0.2f, -0.8f );
		
		Edge AB = graph.addEdge( "AB", "A", "B" );
		Edge BC = graph.addEdge( "BC", "B", "C" );
		Edge CA = graph.addEdge( "CA", "C", "A" );
	/*	Edge CD=*/graph.addEdge( "CD", "C", "D" );
	/*	Edge BD=*/graph.addEdge( "BD", "B", "D" );

		// A style sheet. Sprites can have a style, exactly like
		// nodes and edges.
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
		
		A.addAttribute( "label", "A" );
		B.addAttribute( "label", "B" );
		C.addAttribute( "label", "C" );
		D.addAttribute( "label", "D" );
		
		// We add two sprites and attach it to some edges.
	
		Sprite S0 = viewerRemote.addSprite( "S0" );
		Sprite S1 = viewerRemote.addSprite( "S1" );
		Sprite S2 = viewerRemote.addSprite( "S2" );
		
		S0.attachToEdge( "AB" );
		S1.attachToEdge( "AB" );
		S2.attachToEdge( "AB" );
		S0.addAttribute( "label", "Here" );
		S1.addAttribute( "label", "Ant 1" );
		S2.addAttribute( "label", "Ant 2" );
		S1.addAttribute( "ui.class", "edgeSprite" );
		S2.addAttribute( "ui.class", "edgeSprite" );
		
		// Add two sprites and attach them to nodes.
	
		Sprite S3 = viewerRemote.addSprite( "S3" );
		Sprite S4 = viewerRemote.addSprite( "S4" );
		
		S3.addAttribute( "ui.class", "nodeSprite" );
		S4.addAttribute( "ui.class", "nodeSprite" );
		S3.addAttribute( "label", "Orbit 1" );
		S4.addAttribute( "label", "Orbit 2" );
		S3.attachToNode( "A" );
		S4.attachToNode( "B" );
		S3.position( 10, 0, 0, Style.Units.PX );
		S4.position( 10, 0, 0, Style.Units.PX );
	
		// Add two flow sprites to a cubic-curve edge.
		
		Sprite S5 = viewerRemote.addSprite( "S5" );
		Sprite S6 = viewerRemote.addSprite( "S6" );
		
		S5.addAttribute( "ui.class" , "flowSprite" );
		S6.addAttribute( "ui.class" , "flowSprite" );
		S5.attachToEdge( "CD" );
		S6.attachToEdge( "CD" );
		
		// Add to other flow sprites to a line edge.
		
		Sprite S13 = viewerRemote.addSprite( "S13" );
		Sprite S14 = viewerRemote.addSprite( "S14" );
		
		S13.addAttribute( "ui.class", "flowSprite" );
		S14.addAttribute( "ui.class", "flowSprite" );
		S13.attachToEdge( "BD" );
		S14.attachToEdge( "BD" );
		
		// Add two pie-chart sprites, one on an edge, another on a node.
		
		Sprite S7 = viewerRemote.addSprite( "S7" );
		Sprite S8 = viewerRemote.addSprite( "S8" );

		S7.attachToEdge( "AB" );
		S8.attachToNode( "D" );
		
		S7.position( 0.5f, 0, 0, Style.Units.PX );
		S8.position(  25f, 0, 0, Style.Units.PX );
		S7.addAttribute( "pie-values", 0.4f, 0.2f, 0.3f, 0.1f );
		S8.addAttribute( "pie-values", 0.8f, 0.2f );
		
		// Add two textbox and ellipsebox sprites one on an edge, another on a node.
		
		Sprite S9  = viewerRemote.addSprite( "S9" );
		Sprite S10 = viewerRemote.addSprite( "S10" );
		
		S9.attachToEdge( "CD" );
		S10.attachToNode( "C" );
		
		S9.addAttribute( "label", "This is a label." );
		S10.addAttribute( "label", "This is another label." );
		
		S9.position(  0.5f, 0, 0, Style.Units.PX );
		S10.position(  10f, 0, 0, Style.Units.PX );
		
		// Add two non-attached sprites as text boxes.
		
		Sprite S11 = viewerRemote.addSprite( "S11" );
		Sprite S12 = viewerRemote.addSprite( "S12" );
		
		S11.addAttribute( "label", "(-1,-1)" );
		S12.addAttribute( "label", "(1,1)" );
		
		S11.position( -1, -1, 0 );		// Units are always GU for non-attached sprites.
		S12.position(  1,  1, 0 );		// Units are always GU for non-attached sprites.
		
		// Here we animate the edge sprites so that they loop around the triangle graph.
		// And we animate the node sprites so that they orbit arround the triangle graph.
		
		Edge current0  = AB;
		Edge current1  = AB;
		Edge current2  = AB;
		float pos0     = 0;
		float pos1     = 0;
		float pos2     = 0;
		float offset   = 20;
		float angle1   = (float) Math.PI;
		float angle2   = 0;
		float flow1    = 0;
		float flow2    = 1;
		float flowdir1 =  0.01f;
		float flowdir2 = -0.01f;
		
		while( true )
		{
			S0.position( pos0,   offset, 0, Style.Units.PX );
			S1.position( pos1,   offset, 0, Style.Units.PX );
			S2.position( pos2,  -offset, 0, Style.Units.PX );
			S3.position( offset, 0,  angle1, Style.Units.PX );
			S4.position( offset, 0,  angle2, Style.Units.PX );
			S5.position( flow1,  3,      0, Style.Units.PX );
			S6.position( flow2, -3,      0, Style.Units.PX );
		   S13.position( flow1,  3,      0, Style.Units.PX );
		   S14.position( flow2, -3,      0, Style.Units.PX );
			
			pos0   += 0.005f;
			pos1   += 0.01f;
			pos2   += 0.03f;
			angle1 += 0.05f;
			angle2 += 0.1f;
			flow1  += flowdir1;
			flow2  += flowdir2;
			
			if( pos0 >= 1 )
			{
				if(      current0 == AB ) current0 = BC;
				else if( current0 == BC ) current0 = CA;
				else if( current0 == CA ) current0 = AB;
				pos0 = 0;
				S0.attachToEdge( current0.getId() );
				S0.position( pos0, 0, offset, Style.Units.PX );
			}
			
			if( pos1 >= 1 )
			{
				if(      current1 == AB ) current1 = BC;
				else if( current1 == BC ) current1 = CA;
				else if( current1 == CA ) current1 = AB;
				pos1 = 0;
				S1.attachToEdge( current1.getId() );
				S1.position( pos1, 0, offset, Style.Units.PX );
			}
			
			if( pos2 >= 1 )
			{
				if(      current2 == AB ) current2 = BC;
				else if( current2 == BC ) current2 = CA;
				else if( current2 == CA ) current2 = AB;
				pos2 = 0;
				S2.attachToEdge( current2.getId() );
				S2.position( pos2, 0, -offset, Style.Units.PX );
			}
			
			if( angle1 > Math.PI*2 )
				angle1 = 0;
			
			if( angle2 > Math.PI*2 )
				angle2 = 0;
			
			if( flow1 >= 1 )
			{
				flow1    = 1;
				flowdir1 = -0.01f;
			}
			else if( flow1 <= 0 )
			{
				flow1    = 0;
				flowdir1 = 0.01f;
			}
			
			if( flow2 >= 1 )
			{
				flow2    = 1;
				flowdir2 = -0.01f;
			}
			else if( flow2 <= 0 )
			{
				flow2    = 0;
				flowdir2 = 0.01f;
			}
			
			try{ Thread.sleep( 40 ); } catch( Exception e ) {}
		}
	}
	
// Constants
		
		protected static String styleSheet =
				"node {" +
				"		width:16;" +
				"		color:lightgrey;" +
				"		border-width:1;" +
				"		border-color:black;" +
				"		text-color:black;" +
				"}" +
				"edge#BC { edge-shape:cubic-curve; }" +
				"edge#CD { edge-shape:cubic-curve; }" +
//				"edge { edge-shape:cubic-curve; }" +
				"sprite {" +
				"		border-width:1;" +
				"		border-color:black;" +
				"		text-align:aside;" +
				"		text-color:darkgrey;" +
				"}" +
				"sprite#S0 {" +
				"		color: #A02030;" +
				"		border-width: 1px;" +
				"		border-color: black;" +
				"		sprite-shape: arrow;" +
				"		sprite-orientation:origin;" +
				"}"+
				"sprite.edgeSprite {" +
				"		color:red;" +
				"		width:32px;" +
				"		height:32px;" +
				"		sprite-shape: image;" +
				"		image:url('http://www.iconarchive.com/icons/topicons/ants/Ant-icon.gif');" +
				"		sprite-orientation:origin;" +
				"}" +
				"sprite.nodeSprite {" +
				"		color: blue;" +
				"		width: 10;" +
				"		sprite-orientation: origin;" +
				"}" +
				"sprite#S3 {" +
				"		color: yellow;" +
				"		sprite-shape: arrow;" +
				"		sprite-orientation: origin;" +
				"}" +
				"sprite#S5 {" +
				"		width: 6;" +
				"		sprite-shape: flow;" +
				"		color: #A02020AA;" +
				"		z-index: -1;" +
				"		sprite-orientation:from;" +
				"}" +
				"sprite#S6 {" +
				"		width: 6;" +
				"		sprite-shape: flow;" +
				"		color: #003070AA;" +
				"		z-index: -1;" +
				"		sprite-orientation:from;" +
				"}" +
				"sprite#S7 {" +
				"		width: 20;" +
				"		color: yellow blue red green;" +
				"		sprite-shape: pie-chart;" +
				"}" +
				"sprite#S8 {" +
				"		width: 20;" +
				"		color: red magenta;" +
				"		sprite-shape: pie-chart;" +
				"}" +
				"sprite#S9 {" +
				"		color: #FFFFFF88;" +
				"		sprite-shape: text-box;" +
				"		text-color: black;" +
				"		text-align:center;" +
				"}" +
				"sprite#S10 {" +
				"		color: #FFFFFF88;" +
				"		sprite-shape: text-ellipse;" +
				"		text-color: black;" +
				"		text-align:right;" +
				"}" +
				"sprite#S11 {" +
				"		sprite-shape:text-box;" +
				"		color: #00000055;" +
				"		text-color: white;" +
				"}" +
				"sprite#S12 {" +
				"		sprite-shape:text-box;" +
				"		color: #00000055;" +
				"		text-color: white;" +
				"}" +
				"sprite#S13 {" +
				"		width: 6;" +
				"		sprite-shape: flow;" +
				"		color: #20A020AA;" +
				"		z-index: -1;" +
				"		sprite-orientation:from;" +
				"}" +
				"sprite#S14 {" +
				"		width: 6;" +
				"		sprite-shape: flow;" +
				"		color: #007030AA;" +
				"		z-index: -1;" +
				"		sprite-orientation:from;" +
				"}";
}