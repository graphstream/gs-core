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

import org.miv.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.miv.graphstream.algorithm.generator.Generator;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.ui.GraphViewerRemote;

/**
 * The purpose of this test is to show the various styling possibilities.
 * 
 * @author Antoine Dutot
 */
public class TestStyles2
{
	public static void main( String args[] )
	{
		new TestStyles2();
	}
	
	public TestStyles2()
	{
		test( TestStyles.styleSheet1 );
		test( TestStyles.styleSheet2 );
		test( TestStyles.styleSheet3 );
		test( TestStyles.styleSheet4 );
		test( TestStyles.styleSheet5 );
	}
	
	protected void test( String styleSheet )
	{
		Graph graph = new DefaultGraph( false, true );
		
		GraphViewerRemote viewerRemote = graph.display();
		
		viewerRemote.setQuality( 3 );

		Generator dmg = new DorogovtsevMendesGenerator();
		
		dmg.begin( graph );
		
		for( int i=0; i<100; ++i )
			dmg.nextElement();
		
		dmg.end();

		Iterator<?extends Node> nodes = graph.getNodeIterator();

		while( nodes.hasNext() )
		{
			Node node = nodes.next();
			
			node.addAttribute( "label", node.getId() );
		}
		
		Iterator<?extends Edge> edges = graph.getEdgeIterator();
		
		while( edges.hasNext() )
		{
			Edge edge = edges.next();
			
			edge.setDirected( true );
			edge.addAttribute( "label", edge.getId() );
		}
		
		graph.addAttribute( "ui.stylesheet", styleSheet );
	}
}