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
import org.miv.graphstream.graph.implementations.DefaultGraph;

public class TutorialBase003 {
	public static void main( String args[] ) {
		new TutorialBase003();
	}
	public TutorialBase003() {
		Graph graph = new DefaultGraph();
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		Node X = graph.addNode( "X" );
		Node Y = graph.addNode( "Y" );
		Node Z = graph.addNode( "Z" );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		graph.addEdge( "XY", "X", "Y" );
		graph.addEdge( "YZ", "Y", "Z" );
		graph.addEdge( "ZX", "Z", "X" );
		graph.addEdge( "AX", "A", "X" );
		
		A.addAttribute( "label", "A" );
		B.addAttribute( "label", "B" );
		C.addAttribute( "label", "C" );
		X.addAttribute( "label", "X" );
		Y.addAttribute( "label", "Y" );
		Z.addAttribute( "label", "Z" );
		
		graph.display();
	}
}