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

import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;

/**
 * This tutorial show how to copy back in the graph node coordinates computed
 * by an automatic layout algorithm
 * 
 * @author Antoine Dutot
 */
public class TutorialUI004a
{
	public static void main( String args[] )
	{
		new TutorialUI004a();
	}
	
	public TutorialUI004a()
	{
		Graph graph = new DefaultGraph( "Coos", false, true );
		
		GraphViewerRemote remote = graph.display( true );
		graph.addAttribute( "ui.stylesheet", "graph { text-align:aside; }" );
		
		remote.copyBackLayoutCoordinates( graph );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CD", "C", "D" );
		graph.addEdge( "DA", "D", "A" );
		graph.addEdge( "CA", "C", "A" );
		
		while( true )
		{
			remote.pumpEvents();
			
			Iterator<? extends Node> i = graph.getNodeIterator();
			
			while( i.hasNext() )
			{
				Node node = i.next();
				float x = (float) node.getNumber( "x" );
				float y = (float) node.getNumber( "y" );
				node.addAttribute( "label", String.format( "(%.4f, %.4f)", x, y ) );
			}
			
			try{ Thread.sleep( 100 ); } catch( Exception e ) { }
		}
	}
}