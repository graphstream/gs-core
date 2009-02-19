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

/**
 * Test the "shadow" CSS properties.
 *
 * @author Antoine Dutot
 */
public class TestShadows
{
	public static void main( String args[] )
	{
		new TestShadows( styleSheet1 );
		new TestShadows( styleSheet2 );
		new TestShadows( styleSheet3 );
	}
	
	public TestShadows( String styleSheet )
	{
		Graph graph = new MultiGraph( "Shadows !", false, true );
		
		graph.setAttribute( "stylesheet", styleSheet );
		graph.display( true );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CD", "C", "D" );
		graph.addEdge( "DA", "D", "A" );

		graph.addEdge( "AC", "A", "C" );
		graph.addEdge( "BD", "B", "D" );
		
		graph.getNode("A").setAttribute( "label", "A" );
		graph.getNode("B").setAttribute( "label", "B" );
		graph.getNode("C").setAttribute( "label", "C" );
		graph.getNode("D").setAttribute( "label", "D" );
	}
	
	protected static String styleSheet1 =
		"node" +
		"{" +
		"	width: 14px;" +
		"	text-color: white;" +
		"	text-size: 9;" +
		"	color: #D05030;" +
		"	border-width: 2px;" +
		"	border-color: #303030;" +
		"	shadow-style: simple;" +
		"	shadow-width: 0px;" +
		"	shadow-color: #00000044;" +
		"	shadow-offset: 3 3;" +
		"}";
	
	protected static String styleSheet2 =
		"node" +
		"{" +
		"	width: 14px;" +
		"	color: white;" +
		"	border-width: 0px;" +
		"	text-color: black;" +
		"	text-size: 9;" +
		"	shadow-style: simple;" +
		"	shadow-width: 3px;" +
		"	shadow-offset: 0px 0px;" +
		"	shadow-color: #707080;" +
		"}" +
		"edge" +
		"{" +
		"	width: 2px;" +
		"	color: white;" +
		"	shadow-style: simple;" +
		"	shadow-width: 3px;" +
		"	shadow-offset: 0px 0px;" +
		"	shadow-color: #707080;" +
		"}";

	protected static String styleSheet3 =
		"node" +
		"{" +
		"	color: white;" +
		"	text-color: #303030;" +
		"	text-font: arial;" +
		"	text-size: 8;" +
		"	border-width: 1px;" +
		"	border-color: #505050;" +
		"	shadow-style: simple;" +
		"	shadow-offset: 0px 0px;" +
		"	shadow-color: #BACB58;" +
		"	shadow-width: 2px;" +
		"}" +
		"edge" +
		"{" +
		"	color: #505050;" +
		"}";
	
}