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
import java.util.Map;

import org.miv.graphstream.io.*;
import org.miv.util.NotFoundException;

/**
 * Test the GraphReaderWeb.
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 2007
 */
public class TestGraphReaderWeb implements GraphReaderListener
{
	public static void main( String args[] )
	{
		try
		{
			new TestGraphReaderWeb( args );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public TestGraphReaderWeb( String args[] )
		throws IOException, NotFoundException, GraphParseException
	{
		if( args.length > 0 )
		{
			explore( args[0] );
		}
	}
	
	protected void explore( String URL ) throws NotFoundException, GraphParseException, IOException
	{
		GraphReaderWeb reader = new GraphReaderWeb( 4, false, true );

		reader.setShowLog( true );
		//reader.setOnlySites( true );
		reader.addGraphReaderListener( this );
		
		reader.begin( URL );
		while( reader.nextEvents() ) {};
		reader.end();
	}

// Listener
	
	public void edgeAdded( String id, String from, String to, boolean directed, Map<String, Object> attributes ) throws GraphParseException
	{
		System.err.printf( "EDGE [%s] -> [%s]%n", from, to );
	}

	public void edgeChanged( String id, Map<String, Object> attributes ) throws GraphParseException
	{
		System.err.printf( "edge changed ?%n" );
	}

	public void edgeRemoved( String id ) throws GraphParseException
	{
		System.err.printf( "edge removed ?%n" );
	}

	public void graphChanged( Map<String, Object> attributes ) throws GraphParseException
	{
		System.err.printf( "graph changed ?%n" );
	}

	public void nodeAdded( String id, Map<String, Object> attributes ) throws GraphParseException
	{
		System.err.printf( "NODE [%s]%n", id );
	}

	public void nodeChanged( String id, Map<String, Object> attributes ) throws GraphParseException
	{
		System.err.printf( "node changed ?%n" );
	}

	public void nodeRemoved( String id ) throws GraphParseException
	{
		System.err.printf( "node removed ?%n" );
	}

	public void stepBegins( double time ) throws GraphParseException
	{
		System.err.printf( "step ?%n" );
	}

	public void unknownEventDetected( String unknown ) throws GraphParseException
	{
		System.err.printf( "unknown event ?%n" );
	}
}