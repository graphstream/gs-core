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

import org.miv.graphstream.algorithm.layout2.LayoutRunner;
import org.miv.graphstream.algorithm.layout2.elasticbox.ElasticBox;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewer;
import org.miv.graphstream.ui.GraphViewerRemote;
import org.miv.graphstream.ui.swing.SwingGraphViewer;

public class TestSwingGraphViewer
{
	public static void main( String args[] )
	{
		new TestSwingGraphViewer();
	}
	
	public TestSwingGraphViewer()
	{
		Graph graph = new DefaultGraph( "Test" );
		GraphViewer viewer = new SwingGraphViewer();

		viewer.open( graph, new ElasticBox( false ) );
		LayoutRunner.LayoutRemote layoutRemote = viewer.getLayoutRemote();
		GraphViewerRemote viewerRemote = viewer.newViewerRemote();
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		Edge AB = graph.addEdge( "AB", "A", "B" );
		Edge BC = graph.addEdge( "BC", "B", "C" );
		Edge CA = graph.addEdge( "CA", "C", "A" );
		
		A.addAttribute( "label", "A" );
		B.addAttribute( "label", "B" );
		C.addAttribute( "label", "C" );
		A.addAttribute( "ui.color", "red" );
		B.addAttribute( "ui.color", "green" );
		C.addAttribute( "ui.color", "blue" );
		
		AB.addAttribute( "ui.width", 1 );
		BC.addAttribute( "ui.width", 2 );
		CA.addAttribute( "ui.width", 3 );
		
		Node D = graph.addNode( "D" );
		graph.addEdge(  "BD", "B", "D" );
		graph.addEdge(  "CD", "C", "D" );
		
		D.addAttribute( "label", "D" );
		
		layoutRemote.setQuality( 4 );
		viewerRemote.setQuality( 4 );
	}
}