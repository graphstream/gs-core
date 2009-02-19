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

import java.util.*;

import org.miv.graphstream.io.*;

/**
 * Test the graph reading process using listeners and demonstrates how to use it.
 *
 * This small class instantiate a graph reader for all the graphs that are
 * given on its command line and process theses graphs.
 *
 * @author Yoann Pigné
 * @author Antoine Dutot
 * @since  20060909
 */
public class TutorialBase015 implements GraphReaderListener
{
	public static void
	main( String args[] )
	{
		new TutorialBase015( args );
	}
	
	public
	TutorialBase015( String args[] )
	{
		for( String argument: args )
			parseGraph( argument );
	}
	
	protected void
	parseGraph( String fileName )
	{
		try
		{
			GraphReader reader = GraphReaderFactory.readerFor( fileName );
			
			reader.addGraphReaderListener( this );
			reader.begin( fileName );
			while( reader.nextEvents() ) {};
			reader.end();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	}

	public void
	edgeAdded( String id, String from, String to, boolean directed, Map<String, Object> attributes )
		throws GraphParseException
	{
		System.out.printf( "edgeAdded( %s, %s, %s %s%n", id, from, to, directed ? "directed " : "" );
	}

	public void
	edgeChanged( String id, Map<String, Object> attributes )
		throws GraphParseException
	{
		System.out.printf( "edgeChanged %s%n", id );
	}

	public void
	edgeRemoved( String id )
		throws GraphParseException
	{
		System.out.printf( "edgeRemoved %s%n", id );
	}

	public void
	graphChanged( Map<String, Object> attributes )
		throws GraphParseException
	{
		System.out.printf( "graphChanged%n" );
	}

	public void
	nodeAdded( String id, Map<String, Object> attributes )
		throws GraphParseException
	{
		System.out.printf( "nodeAdded %s%n", id );
	}

	public void
	nodeChanged( String id, Map<String, Object> attributes )
		throws GraphParseException
	{
		System.out.printf( "nodeChanged %s%n", id );
	}

	public void
	nodeRemoved( String id )
		throws GraphParseException
	{
		System.out.printf( "nodeRemoved %s%n", id );
	}

	public void
	stepBegins( double time )
		throws GraphParseException
	{
		System.out.printf( "stepBegins %f%n", time );
	}

	public void
	unknownEventDetected( String unknown )
		throws GraphParseException
	{
		System.out.printf( "unknownEventDetected %s%n", unknown );
	}
}