package org.miv.graphstream.io.test;

import org.miv.graphstream.io.*;
import org.miv.graphstream.graph.implementations.DefaultGraph;

/**
 * A small class to show how to read a graph very quickly.
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 2007
 */
public class TestGraphReaderQuick
{
	public static void main( String args[] )
	{
		if( args.length != 1 )
		{
			System.err.printf( "usage: %s <graph-file-name>%n", TestGraphReaderQuick.class.getName() );
			System.exit( 1 );
		}
		
		try
		{
			new TestGraphReaderQuick( args[0] );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	}
	
	public TestGraphReaderQuick( String fileName )
		throws Exception
	{
		// Create a graph:
		
		DefaultGraph g = new DefaultGraph();
		
		// Create a reader according to the graph format:
		
		GraphReader r = GraphReaderFactory.readerFor( fileName );
		
		// Use a helper to build the graph according to the reader:
		
		GraphReaderListenerHelper l = new GraphReaderListenerHelper( g );
		r.addGraphReaderListener( l );
		
		// Prepare to display what is read:
		
		g.display();
		
		// And read the graph in a loop:
		
		r.begin( fileName );

		while( r.nextEvents() )
		{
			try { Thread.sleep( 100 ); } catch( InterruptedException e ) {} 
		}

		r.end();
	}
}