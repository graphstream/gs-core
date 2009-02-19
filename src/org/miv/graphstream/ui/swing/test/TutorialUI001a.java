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

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.swing.SwingGraphViewer;

public class TutorialUI001a extends JFrame
{
    private static final long serialVersionUID = 1;

	public static void main( String args[] )
	{
		new TutorialUI001a();
	}
	
	public TutorialUI001a()
	{
		Graph graph = new DefaultGraph( false, true );
		SwingGraphViewer viewer = new SwingGraphViewer( graph, true /*isPanel*/ );
		GraphViewerRemote viewerRemote = viewer.newViewerRemote();
	
		viewerRemote.setQuality( 4 );	// 4 is the max quality (range is 0-4).
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		add( (JComponent)viewer.getComponent(), BorderLayout.CENTER );
		add( new JButton( "Click Me !" ), BorderLayout.SOUTH );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		pack();
		setVisible( true );
	} 
}