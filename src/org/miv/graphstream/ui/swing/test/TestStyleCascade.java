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
import org.miv.graphstream.graph.implementations.MultiGraph;
import org.miv.graphstream.ui.GraphViewerListener;
import org.miv.graphstream.ui.GraphViewerRemote;

/**
 * Test the cascading in style sheets as well as the style aggregation.
 *
 * @author Antoine Dutot
 */
public class TestStyleCascade implements GraphViewerListener
{
	public static void main( String args[] )
	{
		new TestStyleCascade();
	}
	
	protected Graph graph;
	
	protected boolean state = false;
	
	public TestStyleCascade()
	{
		graph = new MultiGraph( "test ccs", false, true );
		
		graph.addAttribute( "stylesheet", styleSheet );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CD", "C", "D" );
		graph.addEdge( "DA", "D", "A" );
	
		Node A = graph.getNode( "A" );
		Node B = graph.getNode( "B" );
		Node C = graph.getNode( "C" );
		Node D = graph.getNode( "D" );
		
		A.addAttribute( "ui.class", " class2   ,  class3 " );
		C.addAttribute( "ui.class", "class2", "class1" );
		D.addAttribute( "ui.class", "class3" );
		
		A.addAttribute( "label", "A (yellow,border,20px)" );
		B.addAttribute( "label", "B (yellow,normal)" );
		C.addAttribute( "label", "C (border,40px)" );
		D.addAttribute( "label", "D (magenta,20px)" );
		
		GraphViewerRemote remote = graph.display();
		
		remote.setQuality( 4 );
		remote.addViewerListener( this );
		
		while( true )
		{
			remote.pumpEvents();
			
			try
            {
	            Thread.sleep( 100 );
            }
            catch( InterruptedException e )
            {
	            e.printStackTrace();
            }
		}
	}
	
	protected String styleSheet = 
		"graph { padding: 100px; }" +
		"node { color: red; text-align: aside; }" +
		"node.class1 { width: 40px; }" +
		"node.class2 { border-width: 2px; border-color: blue; }" +
		"node.class3 { width: 20px; color: magenta; }" +
		"node.class4 { width: 50px; }" +
		"node#A { color: yellow; }" +
		"node#B { color: yellow; }";

	public void backgroundClicked( float x, float y, int button, boolean clicked )
    {
		if( clicked )
		{
			Node A = graph.getNode( "A" );
			Node D = graph.getNode( "D" );
	
			if( state )
			{
				A.setAttribute( "ui.class", "class2" );
				D.removeAttribute( "ui.class" );
			}
			else
			{
				A.setAttribute( "ui.class", "class2,class3" );
				D.setAttribute( "ui.class", "class3" );
			}
			
			
			
			state = !state;
		}
    }

	public void nodeMoved( String id, float x, float y, float z )
    {
    }

	public void nodeSelected( String id, boolean selected )
    {
    }

	public void spriteMoved( String id, float x, float y, float z )
    {
    }

	public void spriteSelected( String id, boolean selected )
    {
    }
}