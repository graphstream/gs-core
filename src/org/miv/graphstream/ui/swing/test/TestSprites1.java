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

import java.util.Iterator;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.RemoteSprite;

/**
 * Test the sprite API.
 * 
 * @author Antoine Dutot
 */
public class TestSprites1
{
	public static void main( String args[] )
	{
		new TestSprites1();
	}
	
	public TestSprites1()
	{
		Graph graph = new DefaultGraph();
		
		GraphViewerRemote viewerRemote = graph.display();
		
		viewerRemote.setQuality( 4 );
		
		// A simple triangle graph.
		
		Node A  = graph.addNode( "A" );
		Node B  = graph.addNode( "B" );
		Node C  = graph.addNode( "C" );
		
		graph.addEdge( "AB", "A", "B", true );
		graph.addEdge( "BC", "B", "C", true );
		graph.addEdge( "CA", "C", "A", true );

		// A style sheet. Sprites can have a style, exactly like
		// nodes and edges.
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
		
		A.addAttribute( "label", "A" );
		B.addAttribute( "label", "B" );
		C.addAttribute( "label", "C" );
		
		// We add one sprite and attach it to the edge "AB".
		
		MovingSprite S1 = new MovingSprite( graph.getEdge( "AB" ), "S1", viewerRemote );
		
		while( true )
		{
			S1.move();
			try{ Thread.sleep( 40 ); } catch( Exception e ) {}
		}
	}
	
	/**
	 * Our own sprite class.
	 */
	protected static class MovingSprite extends RemoteSprite
	{
		Edge current;
		float pos = 0;
	
		/**
		 * The constructor needs and edge to start from, the sprite identifier
		 * and the graph viewer remote to communicate with the viewer.
		 */
		public MovingSprite( Edge start, String id, GraphViewerRemote remote )
		{
			super( id, remote );
			current = start;
			attachToEdge( current.getId() );
		}

		/**
		 * Move the sprite along the current edge. If the end of the edge is
		 * reached, another edge is chosen.
		 */
		public void move()
		{
			position( pos );
			
			pos += 0.01f;
			
			if( pos >= 1 )
			{
				pos = 0;
				
				Node node = current.getNode1();
				Iterator<? extends Edge> i = node.getEdgeIterator();
				
				while( i.hasNext() )
				{
					Edge edge = i.next();

					if( edge != current )
					{
						current = edge;
						attachToEdge( current.getId() );
						position( pos );
						break;
					}
				}
			}
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
				+ "edge { edge-shape:cubic-curve; }"
				+ "sprite {"
				+		"color:red;"
				+		"border-width:1;"
				+		"border-color:black;"
				+		"width:10;"
				+		"text-align:aside;"
				+		"text-color:darkgrey;"
				+ "}";
}