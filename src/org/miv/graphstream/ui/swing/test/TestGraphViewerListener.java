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
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerListener;
import org.miv.graphstream.ui.GraphViewerRemote;

public class TestGraphViewerListener implements GraphViewerListener
{
	protected Graph graph;
	
	protected GraphViewerRemote remote;
	
	public static void main( String args[] )
	{
		new TestGraphViewerListener();
	}
	
	public TestGraphViewerListener()
	{
		graph = new DefaultGraph();
		
		remote = graph.display( false );
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
		
		remote.setQuality( 3 );
		remote.addViewerListener( this );
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		A.addAttribute( "label", "A" );
		B.addAttribute( "label", "B" );
		C.addAttribute( "label", "C" );
		A.addAttribute( "x", 0 );
		A.addAttribute( "y", 1 );
		B.addAttribute( "x", -1 );
		B.addAttribute( "y", 0 );
		C.addAttribute( "x", 1 );
		C.addAttribute( "y", 0 );
		
		remote.addSprite( "S1" );
		remote.addSprite( "S2" );
		remote.addSprite( "S3" );
		remote.attachSpriteToEdge( "S1", "AB" );
		remote.attachSpriteToNode( "S2", "C" );
		remote.positionSprite( "S1", 0.5f );
		remote.positionSprite( "S2", 0.1f, 0.1f, 0.1f );
		remote.positionSprite( "S3", 0, 0, 0 );
		remote.addSpriteAttribute( "S1", "label", "S1" );
		remote.addSpriteAttribute( "S2", "label", "S2" );
		remote.addSpriteAttribute( "S3", "label", "S3" );
		
		while( true )
		{
			remote.pumpEvents();
			try{ Thread.sleep( 80 ); } catch( Exception e ) {}
		}
	}

	public void backgroundClicked( float x, float y, int button, boolean clicked )
    {
	    System.err.printf( "Background clicked (%f,%f | %d | %b)%n", x, y, button, clicked );
    }
	
	public void nodeMoved( String id, float x, float y, float z )
    {
	    System.err.printf( "Node moved (%s (%f,%f,%f))%n", id, x, y, z );
    }

	public void nodeSelected( String id, boolean selected )
    {
	    System.err.printf( "Node selected (%s | %b)%n", id, selected );
	    Node node = graph.getNode( id );
	    
	    if( selected )
	         node.addAttribute( "class", "selected" );
	    else node.removeAttribute( "class" );
	    
    }

	public void spriteMoved( String id, float x, float y, float z )
    {
	    System.err.printf( "Sprite moved (%s (%f,%f,%f))%n", id, x, y, z );	    
    }

	public void spriteSelected( String id, boolean selected )
    {
	    System.err.printf( "Sprite selected (%s | %b)%n", id, selected );	    
	    
	    if( selected )
	         remote.addSpriteAttribute( id, "class", "selected" );
	    else remote.removeSpriteAttribute( id, "class" );
    }

	public static final String styleSheet = 
		"node   { color:blue; text-color:yellow; border-width:2; border-color:#444444; width:10; }" +
		"edge   { color:grey; }" +
		"sprite { color:red; text-color:black; border-width:1; border-color:black; text-align:aside; }" +
		"node.selected { color:red; border-width:2; border-color:#AAAAAA; }" +
		"sprite.selected { color:blue; text-color:red; }";
}
