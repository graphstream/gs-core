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

import org.miv.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.miv.graphstream.algorithm.generator.Generator;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewer;
import org.miv.graphstream.ui.GraphViewerRemote;

/**
 * Test the screen shot and "wait for stabilisation" feature of the graph viewer remote.
 * 
 * @author Antoine Dutot
 */
public class TutorialUI001b
{
	public static void main( String args[] )
	{
		new TutorialUI001b();
	}
	
	public TutorialUI001b()
	{
		Graph graph = new DefaultGraph();
		
		Generator generator = new DorogovtsevMendesGenerator();
		
		generator.begin( graph );
		for( int i=0; i<200; i++ )
			generator.nextElement();
		generator.end();
		
		GraphViewerRemote remote = graph.display( true );
		
		remote.setQuality( 3 );
		remote.waitForLayoutStabilisation( 60000 );
		remote.screenShot( "screenshot", GraphViewer.ScreenshotType.PNG );
		System.out.printf( "Ok !!" );
		System.exit( 0 );
	}
}