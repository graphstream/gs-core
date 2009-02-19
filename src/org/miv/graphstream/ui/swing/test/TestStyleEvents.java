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

import java.util.Iterator;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.MultiGraph;
import org.miv.graphstream.ui.GraphViewerListener;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.Sprite;

/**
 * Test the ":clicked" and ":selected" pseudo-classes.
 * 
 * @author Antoine Dutot
 */
public class TestStyleEvents implements GraphViewerListener
{
	public static void main( String args[] )
	{
		new TestStyleEvents();
	}
	
	protected Graph graph;
	
	public TestStyleEvents()
	{
		graph = new MultiGraph( "test", false, true );
		
		graph.addAttribute( "stylesheet", styleSheet );
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );

		GraphViewerRemote remote = graph.display( false );

		remote.addViewerListener( this );
		
		Node A = graph.getNode( "A" );
		Node B = graph.getNode( "B" );
		Node C = graph.getNode( "C" );
		
		A.addAttribute( "xy",  0, 1 );
		B.addAttribute( "xy", -1, 0 );
		C.addAttribute( "xy",  1, 0 );
		
		Sprite s1 = remote.addSprite( "AB" );
		Sprite s2 = remote.addSprite( "BC" );
		Sprite s3 = remote.addSprite( "CA" );
		s1.attachToEdge( "AB" );
		s2.attachToEdge( "BC" );
		s3.attachToEdge( "CA" );
		s1.position( 0.5f );
		s2.position( 0.5f );
		s3.position( 0.5f );
		
		while( true )
		{
			remote.pumpEvents();
			
			try
            {
	            Thread.sleep( 40 );
            }
            catch( InterruptedException e )
            {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
		}
	}
	
	protected String styleSheet =
		"node           { color: darkgrey; }" +
		"edge           { color: #404040; }" +
		"sprite         { width: 8px; color: lightgrey; }" +
		"node  :clicked { border-width: 2px; border-color: yellow; }" +
		"sprite:clicked { border-width: 2px; border-color: green; }" +
		"node:selected  { color: red; }" +
		"edge:selected  { color: red; }";

	public void backgroundClicked( float x, float y, int button, boolean clicked )
    {
		Iterator<? extends Node> i = graph.getNodeIterator();
		
		while( i.hasNext() )
		{
			Node n = i.next();
			n.removeAttribute( "ui.selected" );
		}
		
		Iterator<? extends Edge> j = graph.getEdgeIterator();
		
		while( j.hasNext() )
		{
			Edge e = j.next();
			e.removeAttribute( "ui.selected" );
		}
    }

	public void nodeMoved( String id, float x, float y, float z )
    {
    }

	public void nodeSelected( String id, boolean selected )
    {
		if( selected )
		{
			Node n = graph.getNode( id );
	
			if( n.hasAttribute( "ui.selected" ) )
			     n.removeAttribute( "ui.selected" );
			else n.addAttribute( "ui.selected" );
		}
    }

	public void spriteMoved( String id, float x, float y, float z )
    {
    }

	public void spriteSelected( String id, boolean selected )
    {
		if( selected )
		{
			Edge e = graph.getEdge( id );
			
			if( e != null )
			{
				if( e.hasAttribute( "ui.selected" ) )
				     e.removeAttribute( "ui.selected" );
				else e.addAttribute( "ui.selected" );
			}
		}
    }
}