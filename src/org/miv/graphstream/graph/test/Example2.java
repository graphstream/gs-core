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


/**
 * Example 2.
 *
 * @author Antoine Dutot
 * @since 20061227
 */
public class Example2
{
	public static void main( String args[] )
	{
		new Example2();
	}
	
	public Example2()
	{
		Graph g = new DefaultGraph( false, true );
		
		g.addEdge( "AB", "A", "B" );
		g.addEdge( "BC", "B", "C" );
		g.addEdge( "CA", "C", "A" );
		Edge loop = g.addEdge( "AA", "A", "A" );
		Node a    = g.getNode( "A" );
	
		System.out.printf( "loop is %s == %s%n", g.getEdge( "AA" ).getId(), loop.getId() );
		System.out.printf( "A -> A = %b%n", a.hasEdgeToward( "A" ) );
		System.out.printf( "A <- A = %b%n", a.hasEdgeFrom( "A" ) );
		System.out.printf( "loop is directed ? = %b%n", loop.isDirected() );
		
		g.display();
	}
}