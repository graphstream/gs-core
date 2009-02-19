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

import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.Sprite;

/**
 * Test the sprite API.
 * 
 * <p>
 * This small test demonstrates the addition of "sprites" on the graph display.
 * Sprites are small visual elements that can be positionned everywhere on the
 * graph display. They can also be attached to elements of the graph. Here a
 * sprite is attached to an edge. When attached to an edge the position of the
 * sprite takes only one coordinate whose value is in (0-1). This value gives
 * the position along the edge from the source node. A value of 0 means the
 * sprite is on the source node, a value of 1 means the sprite is on the
 * target node. Values in between make the sprite travel between the source
 * and target node.
 * </p>
 * 
 * @author Antoine Dutot
 */
public class TestSprites2
{
	public static void main( String args[] )
	{
		new TestSprites2();
	}
	
	public TestSprites2()
	{
		Graph graph = new DefaultGraph();
		
		GraphViewerRemote viewerRemote = graph.display();
		
		viewerRemote.setQuality( 4 );
		
		// A simple triangle graph.
		
		Node A  = graph.addNode( "A" );
		Node B  = graph.addNode( "B" );
		Node C  = graph.addNode( "C" );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );

		// A style sheet. Sprites can have a style, exactly like
		// nodes and edges.
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
		
		A.addAttribute( "label", "A" );
		B.addAttribute( "label", "B" );
		C.addAttribute( "label", "C" );
		
		// We add one sprite and attach it to the edge "AB".
		
		Sprite S1 = viewerRemote.addSprite( "S1" );
		Sprite S2 = viewerRemote.addSprite( "S2" );
		Sprite S3 = viewerRemote.addSprite( "S3" );
		
		S1.attachToEdge( "AB" );
		S2.attachToEdge( "BC" );
		S3.attachToEdge( "CA" );
		S1.position( 0.5f );
		S2.position( 0.5f );
		S3.position( 0.5f );
		
		// Here we animate the sprite so that is loops around the triangle graph.
		
		float minSize = 10;
		float maxSize = 200;
		float size = minSize;
		boolean growing = true;
		
		while( true )
		{
			S1.addAttribute( "width", size );
			S2.addAttribute( "width", size );
			S3.addAttribute( "width", size );
			
			if( growing )
			{
				size+=1;
			
				if( size > maxSize )
					growing = false;
			}
			else
			{
				size-=1;
				
				if( size < minSize )
					growing = true;
			}
			
			try{ Thread.sleep( 80 ); } catch( Exception e ) {}
		}
	}
	
// Constants
		
		protected static String styleSheet =
				  "node {"
				+		"width:16;"
				+		"color:lightgrey;"
				+		"border-width:1;"
				+		"border-color:black;"
				+		"text-color:black;"
				+ "}"
//				+ "edge { edge-shape:cubic-curve; }"
				+ "sprite {"
				+		"color:rgba(255,0,0,128);"
				+		"width:10;"
				+		"text-align:aside;"
				+		"text-color:darkgrey;"
				+		"z-index:-1;"
				+ "}"
				+ "sprite#S1 {"
				+		"color:rgba(0,255,0,128);"
				+ "}"
				+ "sprite#S2 {"
				+		"color:rgba(0,0,255,128);"
				+ "}";
}