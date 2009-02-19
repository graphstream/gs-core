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
import java.util.Iterator;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.io.GraphParseException;
import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphReaderFactory;
import org.miv.graphstream.io.GraphReaderListenerHelper;
import org.miv.util.NotFoundException;

/**
 * Read a graph and then display all its nodes and edges attributes.
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
 */
public class TestAttributes
{
	public static void main( String args[] )
	{
		try
		{
			for( int i=0; i<args.length; ++i )
				new TestAttributes( args[i] );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public TestAttributes( String fileName ) throws NotFoundException, IOException, GraphParseException
	{
		Graph graph = new DefaultGraph();
		GraphReader reader = GraphReaderFactory.readerFor( fileName );
		
		reader.addGraphReaderListener( new GraphReaderListenerHelper( graph ) );
		reader.read( fileName );
		
		Iterator<? extends Node> nodes = graph.getNodeIterator();
		
		while( nodes.hasNext() )
		{
			printAttributes( nodes.next() );
		}
		
		Iterator<? extends Edge> edges = graph.getEdgeIterator();
		
		while( edges.hasNext() )
		{
			printAttributes( edges.next() );
		}
	}
	
	public void printAttributes( Element element )
	{
		Iterator<String> keys = element.getAttributeKeyIterator();
		
		if( element instanceof Node )
		     System.out.printf( "Node %s : %n", element.getId() );
		else System.out.printf( "Edge %s : %n", element.getId() );
		
		while( keys.hasNext() )
		{
			String key = keys.next();
			Object val = element.getAttribute( key );
			System.out.printf( "\t%s -> %s (%s)%n", key, val, val.getClass().getName() );

			if( val instanceof Number )
			{
				System.out.printf( "\t\t NUMBER %f%n", element.getNumber( key ) );
			}
		}
	}
}