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
import org.miv.graphstream.io.DynamicGraphWriterHelper;

/**
 * Test the dynamic graph writer.
 * 
 * @author Antoine Dutot
 */
public class TestDynamicGraphWriter
{
	public static void main( String args[] )
	{
		try
        {
	        new TestDynamicGraphWriter();
        }
        catch( IOException e )
        {
	        e.printStackTrace();
        }
	}
	
	public TestDynamicGraphWriter() throws IOException
	{
		Graph graph = new DefaultGraph( "Test" );
		DynamicGraphWriterHelper writer = new DynamicGraphWriterHelper();
		
		writer.begin( graph, "test.dgs" );

		writer.step( 0 );
		
		graph.addNode( "A" );
		graph.addNode( "B" );
		graph.addNode( "C" );
		
		writer.step( 1 );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		writer.step( 2 );
		
		graph.getNode( "A" ).addAttribute( "label", "A" );
		graph.getNode( "B" ).addAttribute( "label", "B" );
		graph.getNode( "C" ).addAttribute( "label", "C" );
		
		graph.getEdge( "AB" ).addAttribute( "truc", new Foo() );
		
		writer.end();
	}
	
	protected class Foo {}
}