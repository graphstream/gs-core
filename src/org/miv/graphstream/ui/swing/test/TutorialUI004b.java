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
import org.miv.graphstream.ui.Sprite;

/**
 * Demonstrate the use of the GraphViewerListener. 
 * 
 * @author Antoine Dutot
 */
public class TutorialUI004b implements GraphViewerListener
{
	public static void main( String args[] )
	{
		new TutorialUI004b();
	}
	
	protected Graph graph;
	
	protected GraphViewerRemote remote;
	
	protected int sprites = 0;
	
	public TutorialUI004b()
	{
		graph = new DefaultGraph( "Click!", false, true );
		
		remote = graph.display( false );

		graph.addAttribute( "ui.stylesheet", styleSheet );
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		A.addAttribute( "xy",  0, 1 );
		B.addAttribute( "xy", -1, 0 );
		C.addAttribute( "xy",  1, 0 );
		
		remote.addViewerListener( this );
		
		while( true )
		{
			remote.pumpEvents();
			try { Thread.sleep( 100 ); } catch( Exception e ) {}
		}
	}
	
	protected static String styleSheet =
		"node.active { color: yellow; border-width: 1px; border-color: black; }" +
		"node { width: 16px; color: black; text-size:9; text-align: aside; }" +
		"sprite { width: 8px; color: red; border-width:2; border-color: black; }";

	public void backgroundClicked( float x, float y, int button, boolean clicked )
    {
		if( ! clicked )
		{
			Sprite s = remote.addSprite( String.format( "%d", sprites++ ) );
			s.position( x, y, 0 );
		}
    }

	public void nodeMoved( String id, float x, float y, float z )
    {
		graph.getNode( id ).addAttribute( "label", String.format( "(%.2f, %.2f)", x, y ) );
    }

	public void nodeSelected( String id, boolean selected )
    {
		if( selected )
		{
			graph.getNode(id).addAttribute( "ui.class", "active" );
		}
		else
		{
			graph.getNode(id).removeAttribute( "ui.class" );
			graph.getNode(id).removeAttribute( "label" );
		}
    }

	public void spriteMoved( String id, float x, float y, float z )
    {
    }

	public void spriteSelected( String id, boolean selected )
    {
		if( ! selected )
			remote.removeSprite( id );
    }
}