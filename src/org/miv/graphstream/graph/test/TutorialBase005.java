package org.miv.graphstream.graph.test;
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

import org.miv.graphstream.graph.*;
import org.miv.graphstream.graph.implementations.*;
import java.awt.*;

/**
 * A simple triangle graph and example of attribute using to change the graph viewer.
 */
public class TutorialBase005
{
	public static void main( String args[] ) {
		new TutorialBase005();
	}
	
	public TutorialBase005() {
		Graph graph = new DefaultGraph( "Triangle" );
			
		graph.addNode( "node1" );
		graph.addNode( "node2" );
		graph.addNode( "node3" );

		graph.addEdge( "edge1", "node1", "node2" );    
		graph.addEdge( "edge2", "node2", "node3" );    
		graph.addEdge( "edge3", "node3", "node1" );    

		graph.getNode( "node1" ).addAttribute( "color", Color.RED );
		graph.getNode( "node2" ).addAttribute( "color", "green" );
		graph.getNode( "node3" ).addAttribute( "color", "#0000FF" );

		graph.getEdge( "edge1" ).addAttribute( "color", "cyan" ); 
		graph.getEdge( "edge2" ).addAttribute( "color", Color.MAGENTA ); 
		graph.getEdge( "edge3" ).addAttribute( "color", "rgb(255,255,0)" ); 

		graph.getNode( "node1" ).addAttribute( "label", "Node 1" ); 
		graph.getNode( "node2" ).addAttribute( "label", "Node 2" ); 
		graph.getNode( "node3" ).addAttribute( "label", "Node 3" ); 

		graph.getEdge( "edge1" ).addAttribute( "label", 1.5f );
		graph.getEdge( "edge2" ).addAttribute( "label", 2f );
		graph.getEdge( "edge3" ).addAttribute( "label", 3.4f );
		
		graph.getNode( "node1" ).addAttribute( "width", 3 );
		graph.getNode( "node2" ).addAttribute( "width", 6 );
		graph.getNode( "node3" ).addAttribute( "width", 12 );

		graph.getEdge( "edge1" ).addAttribute( "edge-style", "dots" );
		graph.getEdge( "edge2" ).addAttribute( "edge-style", "dashes" );
		graph.getEdge( "edge3" ).addAttribute( "width", 4 );
		
		graph.getNode( "node1" ).addAttribute( "x", -1 );
		graph.getNode( "node1" ).addAttribute( "y",  0 );
		graph.getNode( "node2" ).addAttribute( "x",  1 );
		graph.getNode( "node2" ).addAttribute( "y",  0 );
		graph.getNode( "node3" ).addAttribute( "x",  0 );
		graph.getNode( "node3" ).addAttribute( "y",  1 );

		graph.display( false );
	}
}