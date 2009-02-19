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

import java.io.IOException;

import org.miv.graphstream.io.*;
import org.miv.graphstream.graph.*;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.util.Mem;
import org.miv.util.NotFoundException;

/**
 * Simple test to observe memory consumption.
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 2007
 */
public class TestLoadGraphInMemory
{
	public static void main( String args[] )
	{
		if( args.length == 1 )
		{
			new TestLoadGraphInMemory( args[0] );
		}
		else
		{
			System.err.printf( "usage: %s <graph-file-name>%n", TestLoadGraphInMemory.class.getName() );
			System.exit( 1 );
		}
	}
	
	public TestLoadGraphInMemory( String fileName )
	{
		try
		{
			int i = 0;
			
			GraphReader reader = GraphReaderFactory.readerFor( fileName );
			Graph       graph  =new DefaultGraph(); /*new AdjacencyListGraph( "");*/
			GraphReaderListenerHelper helper = new GraphReaderListenerHelper( graph );
			
			graph.setAutoCreate( true );
			graph.setStrictChecking( false );
			
			reader.addGraphReaderListener( helper );
			reader.begin( fileName );
			
			while( reader.nextEvents() )
			{
				if( ( i % 1000 ) == 0 )
				{
					int n = graph.getNodeCount();
					int e = graph.getEdgeCount();
					String N = null;
					String E = null;

					if( n > 1000000000 )
					     N = String.format( "%.1fG", n/1000000000f );
					else if( n > 1000000 )
					     N = String.format( "%.1fM", n/1000000f );
					else if(  n > 1000 )
					     N = String.format( "%.1fK", n/1000f );
					else N = Integer.toString( n );
					
					if( e > 1000000000 )
					     E = String.format( "%.1fG", e/1000000000f );
					else if( e > 1000000 )
					     E = String.format( "%.1fM", e/1000000f );
					else if(  e > 1000 )
					     E = String.format( "%.1fK", e/1000f );
					else E = Integer.toString( e );
					
					System.out.printf( "[%s nodes, %s edges] %s%n", N, E, Mem.getMemoryUsedString() );
					
					sleep( 20 );
				}
				
				i++;
			}
			
			String mem = Mem.getMemoryUsedString();
			
			reader.end();
			
			System.out.printf( "OK read %s nodes and %s edges, and used %s of memory.%n", graph.getNodeCount(), graph.getEdgeCount(), mem );
			System.out.flush();
		}
		catch( NotFoundException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		catch( GraphParseException e )
		{
			e.printStackTrace();
		}
	}
	
	protected void sleep( int ms )
	{
		try
		{
			Thread.sleep( ms );
		}
		catch( InterruptedException e )
		{
		}
	}
}