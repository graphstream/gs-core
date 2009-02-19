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
package org.miv.graphstream.graph.test;

import org.miv.graphstream.graph.*;
import org.miv.graphstream.graph.implementations.*;

/**
 * A simple triangle graph.
 */
public class TutorialBase001
{
	public static void main( String args[] ) {
		new TutorialBase001();
	}

	public TutorialBase001() {
		Graph graph = new DefaultGraph( "Triangle" );
		
		graph.addNode( "node1" );
		graph.addNode( "node2" );
		graph.addNode( "node3" );

		graph.addEdge( "edge1", "node1", "node2" );    
		graph.addEdge( "edge2", "node2", "node3" );    
		graph.addEdge( "edge3", "node3", "node1" );    

		graph.display();
	}
}