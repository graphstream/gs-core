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

package org.miv.graphstream.io.test;

import java.io.IOException;

import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.io.GraphWriterHelper;

public class TestGraphWriterSVG
{
	public static void main( String args[] )
	{
		new TestGraphWriterSVG();
	}
	
	public TestGraphWriterSVG()
	{
		Graph g = new DefaultGraph( "Foo", false, true );
		
		g.addEdge( "AB", "A", "B" );
		g.addEdge( "BC", "B", "C" );
		g.addEdge( "CA", "C", "A" );
		
		g.getNode("A").setAttribute( "xy",  0, 1 );
		g.getNode("B").setAttribute( "xy", -1, 0 );
		g.getNode("C").setAttribute( "xy",  1, 0 );
		
		GraphWriterHelper out = new GraphWriterHelper( g );

		try
        {
	        out.write( "foo.svg" );
        }
        catch( IOException e )
        {
	        e.printStackTrace();
        }
	}
}