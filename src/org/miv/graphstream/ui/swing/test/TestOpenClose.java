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
import org.miv.graphstream.graph.implementations.MultiGraph;
import org.miv.graphstream.ui.GraphViewer;
import org.miv.graphstream.ui.swing.SwingGraphViewer;

/**
 * Test if the open/close mechanism of the GraphViewer works.
 *
 * @author Antoine Dutot
 */
public class TestOpenClose
{
	public static void main( String args[] )
	{
		new TestOpenClose();
	}
	
	public TestOpenClose()
	{
		Graph graph = new MultiGraph( false, true );
		GraphViewer viewer = new SwingGraphViewer();
		
		graph.addEdge( "AB", "A", "B" );
		viewer.open( graph );
		printThreadList();
		sleep( 10 );
		viewer.close();
		sleep( 10 );
		graph.removeNode( "A" );
		graph.removeNode( "B" );
		printThreadList();
		sleep( 10 );
		System.exit( 0 );	// Kill the AWT thread !! Is there a way to do this cleanly ?
	}
	
	protected void printThreadList()
	{
		Thread threads[] = new Thread[Thread.activeCount()];
		
		Thread.enumerate( threads );
		
		System.err.printf( "Active threads :%n" );
		
		for( Thread t: threads )
		{
			System.err.printf( "    %s -> %s%n", t.getName(), t.getState() );
		}
	}
	
	protected void sleep( int seconds )
	{
		try
		{
			Thread.sleep( seconds*1000 );
		}
		catch( Exception e)
		{
			
		}
	}
}