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

package org.miv.graphstream.algorithm.layout2.elasticbox.test;

import org.miv.graphstream.algorithm.generator.GridGenerator;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.MultiGraph;

public class TestWeights
{
	public static void main( String args[] )
	{
		new TestWeights();
	}
	
	public TestWeights()
	{
		Graph graph = new MultiGraph();
		graph.addAttribute( "stylesheet", styleSheet );
		graph.display();
		GridGenerator gg = new GridGenerator();
		
		gg.begin( graph );
		for( int i=0; i<10; ++i )
			gg.nextElement();
		gg.end();

		graph.getNode("4_4").addAttribute( "class", "selected" );
		graph.getNode("4_5").addAttribute( "class", "selected" );
		graph.getNode("4_6").addAttribute( "class", "selected" );
		graph.getNode("5_4").addAttribute( "class", "selected" );
		graph.getNode("5_5").addAttribute( "class", "selected" );
		graph.getNode("5_6").addAttribute( "class", "selected" );
		graph.getNode("6_4").addAttribute( "class", "selected" );
		graph.getNode("6_5").addAttribute( "class", "selected" );
		graph.getNode("6_6").addAttribute( "class", "selected" );

		graph.getNode("4_4").addAttribute( "layout.weight", 0.03f );
		graph.getNode("4_5").addAttribute( "layout.weight", 0.03f );
		graph.getNode("4_6").addAttribute( "layout.weight", 0.03f );
		graph.getNode("5_4").addAttribute( "layout.weight", 0.03f );
		graph.getNode("5_5").addAttribute( "layout.weight", 0.03f );
		graph.getNode("5_6").addAttribute( "layout.weight", 0.03f );
		graph.getNode("6_4").addAttribute( "layout.weight", 0.03f );
		graph.getNode("6_5").addAttribute( "layout.weight", 0.03f );
		graph.getNode("6_6").addAttribute( "layout.weight", 0.03f );
/*
		graph.getNode("4_4").removeAttribute( "layout.weight" );
		graph.getNode("4_5").removeAttribute( "layout.weight" );
		graph.getNode("4_6").removeAttribute( "layout.weight" );
		graph.getNode("5_4").removeAttribute( "layout.weight" );
		graph.getNode("5_5").removeAttribute( "layout.weight" );
		graph.getNode("5_6").removeAttribute( "layout.weight" );
		graph.getNode("6_4").removeAttribute( "layout.weight" );
		graph.getNode("6_5").removeAttribute( "layout.weight" );
		graph.getNode("6_6").removeAttribute( "layout.weight" );
*/		
	}
	
	protected static String styleSheet =
		"node { width: 6px; }" +
		"node.selected { color:red; }";
}